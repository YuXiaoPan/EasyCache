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

package li.allan.config;

import li.allan.serializer.Serializer;
import li.allan.config.base.CacheConfig;
import li.allan.config.base.ConfigBase;
import li.allan.config.base.ConfigProperties;

/**
 * @author LiALuN
 */
public class SpringConfig extends ConfigBase {
	private ConfigProperties tmp = new ConfigProperties();

	@Override
	public ConfigProperties initConfigProperties() {
		return tmp;
	}

	public void setMainCacheConfig(CacheConfig mainCacheConfig) {
		tmp.setMainCacheConfig(mainCacheConfig);
	}

	public void setBackupCacheConfig(CacheConfig backupCacheConfig) {
		tmp.setBackupCacheConfig(backupCacheConfig);
	}

	public void setKeySerializer(Serializer keySerializer) {
		tmp.setKeySerializer(keySerializer);
	}

	public void setValueSerializer(Serializer valueSerializer) {
		tmp.setValueSerializer(valueSerializer);
	}

	public void setDefaultCacheExpire(int defaultCacheExpire) {
		tmp.setDefaultCacheExpire(defaultCacheExpire);
	}
}
