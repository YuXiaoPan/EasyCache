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

import li.allan.cache.operator.listener.ConfigUpdateListener;
import li.allan.cache.operator.listener.RedisStatusUpdateListener;
import li.allan.cache.shard.ConsistentHashShard;
import li.allan.cache.shard.ShardMethod;
import li.allan.config.base.CacheConfig;
import li.allan.config.base.RedisConfig;
import li.allan.config.base.RedisConnectionConfig;
import li.allan.exception.ConfigException;
import li.allan.exception.NoAvailableRedisException;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import li.allan.monitor.RedisStatus;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author LiALuN
 */
public class RedisPoolContainer implements ConfigUpdateListener, RedisStatusUpdateListener {
	private Log log = LogFactory.getLog(RedisPoolContainer.class);
	private Map<RedisConnectionConfig, JedisPool> availableRedisPoolMap = new HashMap<RedisConnectionConfig, JedisPool>();
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
	 *
	 * @param redisConnectionConfigs
	 * @param jedisPoolConfig
	 */
	private Map<RedisConnectionConfig, JedisPool> initRedisPoolMap(Set<RedisConnectionConfig> redisConnectionConfigs, JedisPoolConfig jedisPoolConfig) {
		if (redisConnectionConfigs == null || redisConnectionConfigs.size() == 0) {
			throw new ConfigException("Redis Connection can't be empty");
		}
		Map<RedisConnectionConfig, JedisPool> redisPoolMap = new HashMap<RedisConnectionConfig, JedisPool>();
		for (RedisConnectionConfig rcc : redisConnectionConfigs) {
			JedisPool jedisPool = new JedisPool(jedisPoolConfig, rcc.getHost(), rcc.getPort(),
					rcc.getTimeout(), rcc.getPassword(), rcc.getDatabase());
			redisPoolMap.put(rcc, jedisPool);
		}
		return redisPoolMap;
	}

	public int avaliableRedisNumber() {
		return availableRedisPoolMap.size();
	}

	@Override
	public void onConfigUpdate(CacheConfig cacheConfig) {
		readWriteLock.writeLock().lock();
		try {
			availableRedisPoolMap = initRedisPoolMap(((RedisConfig) cacheConfig).getRedisConnectionConfigs(),
					((RedisConfig) cacheConfig).getJedisPoolConfig());
			shardMethod = new ConsistentHashShard<JedisPool>(availableRedisPoolMap.values());
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void onRedisStatusUpdate(RedisStatus redisStatus) {
		//TODO 等待写监控
		shardMethod = new ConsistentHashShard<JedisPool>(availableRedisPoolMap.values());
	}
}
