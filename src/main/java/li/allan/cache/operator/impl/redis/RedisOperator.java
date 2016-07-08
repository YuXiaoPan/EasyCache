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
import li.allan.config.base.RedisConfig;
import li.allan.exception.CacheOperationException;
import li.allan.monitor.RedisInfo;
import li.allan.observer.EasyCacheObserver;
import li.allan.observer.ObserverContainer;
import li.allan.observer.event.RedisInfoEvent;
import li.allan.serializer.Serializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author LiALuN
 */
public class RedisOperator extends BaseOperator implements EasyCacheObserver<RedisInfoEvent> {
	RedisPoolContainer redisPoolContainer = new RedisPoolContainer();

	public RedisOperator() {
		ObserverContainer.addObserver(this);
	}

	@Override
	public void set(final String key, final Object value, final Serializer keySerializer, final Serializer valueSerializer) throws CacheOperationException {
		new RedisOperatorTemplate<Void>(key) {
			@Override
			Void readFromRedis() {
				jedis.set(keySerializer.serialize(key), keySerializer.serialize(value));
				return null;
			}
		}.getResult();
	}

	@Override
	public void setWithExpire(final String key, final Object value, final int expire, final Serializer keySerializer, final Serializer valueSerializer) throws CacheOperationException {
		new RedisOperatorTemplate<Void>(key) {
			@Override
			Void readFromRedis() {
				jedis.setex(keySerializer.serialize(key), expire, valueSerializer.serialize(value));
				return null;
			}
		}.getResult();
	}

	@Override
	public <T> Object getByKey(final String key, final Class<T> type, final Serializer keySerializer, final Serializer valueSerializer) throws CacheOperationException {
		return new RedisOperatorTemplate<Object>(key) {
			@Override
			Object readFromRedis() {
				return valueSerializer.deserialize(jedis.get(keySerializer.serialize(key)), type);
			}
		}.getResult();
	}

	@Override
	public void removeByKey(final String key, final Serializer keySerializer) throws CacheOperationException {
		new RedisOperatorTemplate<Void>(key) {
			@Override
			Void readFromRedis() {
				jedis.del(keySerializer.serialize(key));
				return null;
			}
		}.getResult();
	}

	public long TTL(final String key, final Serializer keySerializer) throws CacheOperationException {
		return new RedisOperatorTemplate<Long>(key) {
			@Override
			Long readFromRedis() {
				return jedis.ttl(keySerializer.serialize(key));
			}
		}.getResult();
	}

	@Override
	public boolean isAvailable() {
		return redisPoolContainer.availableRedisNumber() > 0;
	}

	@Override
	public void eventUpdate(RedisInfoEvent event) {
		redisPoolContainer.onRedisStatusUpdate((RedisInfo) event.getSource());
	}

	@Override
	public void onConfigUpdate(CacheConfig cacheConfig) {
		redisPoolContainer.onConfigUpdate((RedisConfig) cacheConfig);
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
}
