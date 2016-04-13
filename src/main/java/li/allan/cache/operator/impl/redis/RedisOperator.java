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

import li.allan.cache.operator.BaseOperator;
import li.allan.config.base.CacheConfig;
import li.allan.config.base.ConfigBase;
import li.allan.exception.CacheOperationException;
import li.allan.monitor.RedisStatus;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

/**
 * @author LiALuN
 */
public class RedisOperator implements BaseOperator {
	RedisPoolContainer redisPoolContainer = new RedisPoolContainer();

	@Override
	public void set(final String key, final Object value) throws CacheOperationException {
		new RedisOperatorTemplate<Void>(key) {
			@Override
			Void readFromRedis() {
				jedis.set(ConfigBase.getConfigProperties().getKeySerializer().serialize(key),
						ConfigBase.getConfigProperties().getValueSerializer().serialize(value));
				return null;
			}
		}.getResult();
	}

	@Override
	public void setWithExpire(final String key, final Object value, final int expire) throws CacheOperationException {
		new RedisOperatorTemplate<Void>(key) {
			@Override
			Void readFromRedis() {
				byte[] k = ConfigBase.getConfigProperties().getKeySerializer().serialize(key);
				byte[] v = ConfigBase.getConfigProperties().getValueSerializer().serialize(value);
				jedis.setex(k, expire, v);
				return null;
			}
		}.getResult();
	}

	@Override
	public <T> T getByKey(final String key, final Class<T> type) throws CacheOperationException {
		return (T) new RedisOperatorTemplate<Object>(key) {
			@Override
			Object readFromRedis() {
				return ConfigBase.getConfigProperties().getValueSerializer().deserialize(jedis.get(SafeEncoder.encode(key)), type);
			}
		}.getResult();
	}

	@Override
	public void removeByKey(final String key) throws CacheOperationException {
		new RedisOperatorTemplate<Void>(key) {
			@Override
			Void readFromRedis() {
				jedis.del(key);
				return null;
			}
		}.getResult();
	}

	public long TTL(final String key) throws CacheOperationException {
		return new RedisOperatorTemplate<Long>(key) {
			@Override
			Long readFromRedis() {
				return jedis.ttl(key);
			}
		}.getResult();
	}

	@Override
	public boolean isAvailable() {
		return redisPoolContainer.avaliableRedisNumber() > 0;
	}

	/**
	 * Redis操作模板方法
	 */
	private abstract class RedisOperatorTemplate<T> {
		JedisPool jedisPool;
		Jedis jedis;

		public RedisOperatorTemplate(String key) {
			jedisPool = redisPoolContainer.getJedisPool(key);
			jedis = jedisPool.getResource();
		}

		abstract T readFromRedis();

		public T getResult() {
			try {
				return readFromRedis();
			} finally {
				if (jedis != null) {
					try {
						jedisPool.returnResource(jedis);
					} catch (Exception e) {
						//ignore
					}
				}
			}
		}
	}

	@Override
	public void onConfigUpdate(CacheConfig cacheConfig) {
		redisPoolContainer.onConfigUpdate(cacheConfig);
	}

	@Override
	public void onRedisStatusUpdate(RedisStatus redisStatus) {
		redisPoolContainer.onRedisStatusUpdate(redisStatus);
	}
}
