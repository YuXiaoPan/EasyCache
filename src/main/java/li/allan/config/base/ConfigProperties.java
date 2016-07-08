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

package li.allan.config.base;

import com.google.common.base.Objects;
import li.allan.serializer.Jackson2Serializer;
import li.allan.serializer.Serializer;
import li.allan.serializer.StringSerializer;

/**
 * @author LiALuN
 */
public class ConfigProperties {
	private CacheConfig mainCacheConfig;
	private CacheConfig backupCacheConfig;
	private Class<? extends Serializer> keySerializer = StringSerializer.class;
	private Class<? extends Serializer> valueSerializer = Jackson2Serializer.class;
	private int defaultCacheExpire = -1;

	public CacheConfig getMainCacheConfig() {
		return mainCacheConfig;
	}

	public void setMainCacheConfig(CacheConfig mainCacheConfig) {
		this.mainCacheConfig = mainCacheConfig;
	}

	public CacheConfig getBackupCacheConfig() {
		return backupCacheConfig;
	}

	public void setBackupCacheConfig(CacheConfig backupCacheConfig) {
		this.backupCacheConfig = backupCacheConfig;
	}

	public Class<? extends Serializer> getKeySerializer() {
		return keySerializer;
	}

	public void setKeySerializer(Class<? extends Serializer> keySerializer) {
		this.keySerializer = keySerializer;
	}

	public Class<? extends Serializer> getValueSerializer() {
		return valueSerializer;
	}

	public void setValueSerializer(Class<? extends Serializer> valueSerializer) {
		this.valueSerializer = valueSerializer;
	}

	public int getDefaultCacheExpire() {
		return defaultCacheExpire;
	}

	public void setDefaultCacheExpire(int defaultCacheExpire) {
		this.defaultCacheExpire = defaultCacheExpire;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		ConfigProperties that = (ConfigProperties) object;
		return defaultCacheExpire == that.defaultCacheExpire &&
				Objects.equal(mainCacheConfig, that.mainCacheConfig) &&
				Objects.equal(backupCacheConfig, that.backupCacheConfig) &&
				Objects.equal(keySerializer, that.keySerializer) &&
				Objects.equal(valueSerializer, that.valueSerializer);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(mainCacheConfig, backupCacheConfig, keySerializer, valueSerializer, defaultCacheExpire);
	}

	@Override
	public String toString() {
		return "ConfigProperties{" +
				"mainCacheConfig=" + mainCacheConfig +
				", backupCacheConfig=" + backupCacheConfig +
				", keySerializer=" + keySerializer +
				", valueSerializer=" + valueSerializer +
				", defaultCacheExpire=" + defaultCacheExpire +
				'}';
	}
}
