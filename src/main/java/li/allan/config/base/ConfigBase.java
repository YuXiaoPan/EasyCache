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

import li.allan.monitor.MonitorDaemon;
import li.allan.observer.EasyCacheObservable;
import li.allan.observer.event.ConfigUpdateEvent;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author LiALuN
 */
public abstract class ConfigBase extends EasyCacheObservable<ConfigUpdateEvent> implements InitializingBean {
	private static ConfigProperties configProperties;

	public void initConfig() {
		configProperties = initConfigProperties();
		sendEvent(new ConfigUpdateEvent());
		MonitorDaemon.start();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initConfig();
	}

	abstract public ConfigProperties initConfigProperties();

	public static ConfigProperties getConfigProperties() {
		return configProperties;
	}
}
