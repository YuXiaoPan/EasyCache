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

import li.allan.monitor.RedisStatus;
import li.allan.observer.event.ConfigUpdateEvent;
import li.allan.observer.event.RedisStatusChangeEvent;
import li.allan.observer.event.base.ObserverEvent;
import org.testng.annotations.Test;

public class TestMain {

	@Test
	public void test() throws ClassNotFoundException {
		new GetConfigUpdate();
		new GetRedisStatusUpdate();
		new AllUpdate();
		new AllUpdate2();
		new AllUpdate3();
		new AllUpdate4();
		EasyCacheObservable observable = new EasyCacheObservable();
		observable.sendEvent(new ConfigUpdateEvent());
		System.out.println();
		observable.sendEvent(new RedisStatusChangeEvent(new RedisStatus(0, null)));
	}

	class GetConfigUpdate implements EasyCacheObserver<ConfigUpdateEvent> {
		public GetConfigUpdate() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(ConfigUpdateEvent event) {
			System.out.println(1 + event.toString());
		}
	}

	class GetRedisStatusUpdate implements EasyCacheObserver<RedisStatusChangeEvent> {
		public GetRedisStatusUpdate() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(RedisStatusChangeEvent event) {
			System.out.println(2 + event.toString());
		}
	}

	class AllUpdate implements EasyCacheObserver<ObserverEvent> {
		public AllUpdate() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(ObserverEvent event) {
			System.out.println(3 + event.toString());
		}
	}

	class AllUpdate2 implements EasyCacheObserver {
		public AllUpdate2() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(ObserverEvent event) {
			System.out.println(4 + event.toString());
		}
	}

	interface C2 extends EasyCacheObserver<ConfigUpdateEvent> {

	}

	class AllUpdate3 implements C2 {
		public AllUpdate3() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(ConfigUpdateEvent event) {
			System.out.println(5 + event.toString());
		}
	}

	abstract class C3 implements C2 {

	}

	class AllUpdate4 extends C3 {
		public AllUpdate4() {
			ObserverContainer.addObserver(this);
		}

		@Override
		public void eventUpdate(ConfigUpdateEvent event) {
			System.out.println(6 + event.toString());
		}
	}
}
