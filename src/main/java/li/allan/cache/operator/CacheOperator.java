/*
 *
 *  *  Copyright  2015-2016. the original author or authors.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package li.allan.cache.operator;

import li.allan.cache.operator.impl.map.ExpiringMapOperator;
import li.allan.cache.operator.impl.redis.RedisOperator;
import li.allan.config.base.CacheConfig;
import li.allan.config.base.ConfigBase;
import li.allan.config.base.ExpireMapConfig;
import li.allan.config.base.RedisConfig;
import li.allan.exception.CacheOperationException;
import li.allan.exception.NoAvailableCacheException;
import li.allan.observer.EasyCacheObserver;
import li.allan.observer.ObserverContainer;
import li.allan.observer.event.ConfigUpdateEvent;
import li.allan.serializer.Serializer;

/**
 * @author LiALuN
 */
public class CacheOperator implements CacheInterface, EasyCacheObserver<ConfigUpdateEvent> {
	private static CacheOperator single = new CacheOperator();

	public static CacheOperator getInstance() {
		return single;
	}

	BaseOperator mainCacheOperator;
	BaseOperator backupCacheOperator;

	private CacheOperator() {
		ObserverContainer.addObserver(this);
	}

	@Override
	public void set(String key, Object value, Serializer keySerializer, Serializer valueSerializer) throws CacheOperationException {
		getOperator().set(key, value, keySerializer, valueSerializer);
	}

	@Override
	public void setWithExpire(String key, Object value, int expire, Serializer keySerializer, Serializer valueSerializer) throws CacheOperationException {
		getOperator().setWithExpire(key, value, expire, keySerializer, valueSerializer);
	}

	@Override
	public <T> Object getByKey(String key, Class<T> type, Serializer keySerializer, Serializer valueSerializer) throws CacheOperationException {
		return getOperator().getByKey(key, type, keySerializer, valueSerializer);
	}

	@Override
	public void removeByKey(String key, Serializer keySerializer) throws CacheOperationException {
		getOperator().removeByKey(key, keySerializer);
	}

	private BaseOperator getOperator() {
		if (mainCacheOperator != null && mainCacheOperator.isAvailable()) {
			return mainCacheOperator;
		}
		if (backupCacheOperator != null && backupCacheOperator.isAvailable()) {
			return backupCacheOperator;
		}
		throw new NoAvailableCacheException();
	}

	@Override
	public void eventUpdate(ConfigUpdateEvent event) {
		CacheConfig mainCacheConfig = ConfigBase.getConfigProperties().getMainCacheConfig();
		if (mainCacheConfig instanceof RedisConfig) {
			if (mainCacheOperator == null) {
				mainCacheOperator = new RedisOperator();
			}
			mainCacheOperator.onConfigUpdate(ConfigBase.getConfigProperties().getMainCacheConfig());
		}
		CacheConfig backupCacheConfig = ConfigBase.getConfigProperties().getBackupCacheConfig();
		if (backupCacheConfig instanceof ExpireMapConfig) {
			if (backupCacheOperator == null) {
				backupCacheOperator = new ExpiringMapOperator();
			}
			backupCacheOperator.onConfigUpdate(ConfigBase.getConfigProperties().getBackupCacheConfig());
		}
	}

	public BaseOperator getMainCacheOperator() {
		return mainCacheOperator;
	}

	public <T> T getMainCacheOperator(Class<T> cacheOperatorType) {
		return (T) getMainCacheOperator();
	}

	public BaseOperator getBackupCacheOperator() {
		return backupCacheOperator;
	}
}
