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

package li.allan.observer;

import li.allan.TestBase;
import li.allan.monitor.MonitorDaemon;
import li.allan.observer.event.RedisInfoEvent;
import org.testng.Assert;
import org.testng.annotations.Test;

import static li.allan.Test_Constants.DEFAULT_TEST_EXPIRE_IN_SECOND;

/**
 * @author LiALuN
 */
public class RedisInfoObserverTest extends TestBase {
	private boolean isS = false;
	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000 * 2)
	public void testName() throws Exception {
		new RedisInfoObserver();
		MonitorDaemon.start();
		Thread.sleep(DEFAULT_TEST_EXPIRE_IN_SECOND * 1000);
		if (!isS) {
			Assert.fail();
		}
	}

	class RedisInfoObserver implements EasyCacheObserver<RedisInfoEvent> {
		public RedisInfoObserver() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(RedisInfoEvent event) {
			isS = true;
		}
	}
}
