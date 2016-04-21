/*
 *  Copyright  2015-2016. the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package li.allan.cache.operator.impl.redis;

import li.allan.cache.operator.listener.CacheInfoEventListener;
import li.allan.cache.operator.listener.ConfigUpdateEventListener;
import li.allan.cache.shard.ConsistentHashShard;
import li.allan.cache.shard.ShardMethod;
import li.allan.config.base.ConfigBase;
import li.allan.config.base.RedisConfig;
import li.allan.config.base.RedisConnectionConfig;
import li.allan.exception.ConfigException;
import li.allan.exception.NoAvailableRedisException;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import li.allan.monitor.RedisInfo;
import li.allan.monitor.RedisStatus;
import li.allan.observer.ObserverContainer;
import li.allan.observer.event.RedisStatusChangeEvent;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author LiALuN
 */
public class RedisPoolContainer implements ConfigUpdateEventListener<RedisConfig>, CacheInfoEventListener {
	private Log log = LogFactory.getLog(RedisPoolContainer.class);
	private Map<RedisConnectionConfig, JedisPool> availableRedisPoolMap = new ConcurrentHashMap<RedisConnectionConfig, JedisPool>();
	private ShardMethod<JedisPool> shardMethod;
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	/**
	 * 通过分片方法，得到JedisPool实例
	 *
	 * @param key
	 * @return
	 */
	public JedisPool getJedisPool(String key) {
		readWriteLock.readLock().lock();
		try {
			if (availableRedisPoolMap.size() <= 0) {
				throw new NoAvailableRedisException();
			}
			return shardMethod.get(key);
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	/**
	 * 初始化RedisPool
	 */
	private void initRedisPoolMap(Set<RedisConnectionConfig> redisConnectionConfigs, JedisPoolConfig jedisPoolConfig) {
		if (redisConnectionConfigs == null || redisConnectionConfigs.size() == 0) {
			throw new ConfigException("Redis Connection can't be empty");
		}
		if (availableRedisPoolMap == null) {
			availableRedisPoolMap = new HashMap<RedisConnectionConfig, JedisPool>();
		}
		closeRedisPoolMap();
		for (RedisConnectionConfig redisConnectionConfig : redisConnectionConfigs) {
			addRedisPool(redisConnectionConfig);
		}
	}

	/**
	 * 关闭所有redis连接池，并清空PoolMap
	 */
	private void closeRedisPoolMap() {
		if (availableRedisPoolMap == null) {
			return;
		}
		Iterator<Map.Entry<RedisConnectionConfig, JedisPool>> entries = availableRedisPoolMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<RedisConnectionConfig, JedisPool> entry = entries.next();
			entry.getValue().close();
			entries.remove();
		}
	}

	private void addRedisPool(RedisConnectionConfig redisConnectionConfig) {
		JedisPoolConfig jedisPoolConfig = ((RedisConfig) ConfigBase.getConfigProperties().getMainCacheConfig())
				.getJedisPoolConfig();
		JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConnectionConfig.getHost(), redisConnectionConfig.getPort(),
				redisConnectionConfig.getTimeout(), redisConnectionConfig.getPassword(), redisConnectionConfig.getDatabase());
		availableRedisPoolMap.put(redisConnectionConfig, jedisPool);
	}

	private void closeRedisPool(RedisConnectionConfig redisConnectionConfig) {
		if (availableRedisPoolMap.containsKey(redisConnectionConfig)) {
			JedisPool jedisPool = availableRedisPoolMap.get(redisConnectionConfig);
			jedisPool.close();
			availableRedisPoolMap.remove(redisConnectionConfig);
		}
	}

	private void rebuildShardMethod() {
		if (availableRedisPoolMap == null || availableRedisPoolMap.size() <= 0) {
			shardMethod = null;
		} else {
			shardMethod = new ConsistentHashShard<JedisPool>(availableRedisPoolMap.values());
		}
	}

	public int availableRedisNumber() {
		return availableRedisPoolMap.size();
	}

	@Override
	public void onConfigUpdate(RedisConfig cacheConfig) {
		readWriteLock.writeLock().lock();
		try {
			initRedisPoolMap(cacheConfig.getRedisConnectionConfigs(), cacheConfig.getJedisPoolConfig());
			rebuildShardMethod();
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void onRedisStatusUpdate(RedisInfo redisInfo) {
		if (redisInfo.isAvailable()) {
			if (!availableRedisPoolMap.containsKey(redisInfo.getRedisConnectionConfig())) {
				log.debug("EasyCache will add Redis instance:" + redisInfo);
				readWriteLock.writeLock().lock();
				try {
					addRedisPool(redisInfo.getRedisConnectionConfig());
					rebuildShardMethod();
					ObserverContainer.sendEvent(new RedisStatusChangeEvent(new RedisStatus(availableRedisNumber(), redisInfo)));
				} finally {
					readWriteLock.writeLock().unlock();
				}
			}
		} else {
			if (availableRedisPoolMap.containsKey(redisInfo.getRedisConnectionConfig())) {
				log.debug("EasyCache will remove Redis instance:" + redisInfo);
				readWriteLock.writeLock().lock();
				try {
					closeRedisPool(redisInfo.getRedisConnectionConfig());
					rebuildShardMethod();
					ObserverContainer.sendEvent(new RedisStatusChangeEvent(new RedisStatus(availableRedisNumber(), redisInfo)));
				} finally {
					readWriteLock.writeLock().unlock();
				}
			}
		}
	}
}
