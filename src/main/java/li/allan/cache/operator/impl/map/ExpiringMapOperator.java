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

package li.allan.cache.operator.impl.map;

import li.allan.cache.operator.BaseOperator;
import li.allan.config.base.CacheConfig;
import li.allan.exception.CacheOperationException;
import li.allan.monitor.RedisStatus;

import java.util.concurrent.TimeUnit;

/**
 * @author LiALuN
 */
public class ExpiringMapOperator implements BaseOperator {
	ExpiringMapContainer expiringMapContainer = new ExpiringMapContainer();

	@Override
	public void set(String key, Object value) throws CacheOperationException {
		expiringMapContainer.getMap().put(key, value, Integer.MAX_VALUE, TimeUnit.SECONDS);
	}

	@Override
	public void setWithExpire(String key, Object value, int expire) throws CacheOperationException {
		if (expire < 0) {
			set(key, value);
		}
		if (expire == 0) {
			return;
		}
		expiringMapContainer.getMap().put(key, value, expire, TimeUnit.SECONDS);
	}

	@Override
	public <T> T getByKey(String key, Class<T> type) throws CacheOperationException {
		return (T) expiringMapContainer.getMap().get(key);
	}

	@Override
	public void removeByKey(String key) throws CacheOperationException {
		expiringMapContainer.getMap().remove(key);
	}

	@Override
	public void onConfigUpdate(CacheConfig cacheConfig) {
		expiringMapContainer.onConfigUpdate(cacheConfig);
	}


	@Override
	public void onRedisStatusUpdate(RedisStatus redisStatus) {
		//do nothing
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}

