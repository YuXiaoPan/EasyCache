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

import li.allan.TestBase;
import li.allan.annotation.CachePutAnnotationTest;
import li.allan.monitor.RedisInfo;
import li.allan.monitor.RedisStatus;
import li.allan.observer.EasyCacheObserver;
import li.allan.observer.ObserverContainer;
import li.allan.observer.event.RedisInfoEvent;
import li.allan.observer.event.RedisStatusChangeEvent;
import org.testng.annotations.Test;

import javax.annotation.Resource;

import static li.allan.Test_Constants.DEFAULT_TEST_EXPIRE_IN_SECOND;

/**
 * @author LiALuN
 */
public class SpringConfigTest extends TestBase {
	@Resource
	private CachePutAnnotationTest cachePutAnnotationTest;

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000000 * 2)
	public void testName() throws Exception {
		new RedisInfoObserver();
		new RedisStatusObserver();
//		while (true) {
//			cachePutAnnotationTest.cacheResult();
//			Thread.sleep(1000);
//		}
//		Thread.sleep(1000000);
	}

	class RedisInfoObserver implements EasyCacheObserver<RedisInfoEvent> {
		public RedisInfoObserver() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(RedisInfoEvent event) {
			RedisInfo connConfig = (RedisInfo) event.getSource();
			System.out.println("RedisInfoObserver: " + connConfig);
		}
	}

	class RedisStatusObserver implements EasyCacheObserver<RedisStatusChangeEvent> {
		public RedisStatusObserver() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(RedisStatusChangeEvent event) {
			RedisStatus connConfig = (RedisStatus) event.getSource();
			System.out.println("RedisStatusObserver: " + connConfig);
		}
	}
}
