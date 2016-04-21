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

import com.google.common.base.Objects;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import li.allan.utils.Constants;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author LiALuN
 */
public class SoftReferenceExpiringMap extends ExpiringMap {
	private static Log log = LogFactory.getLog(SoftReferenceExpiringMap.class);
	private final ConcurrentHashMap<String, SoftValue<Object, String>> internalMap;
	private final DelayQueue<ExpiringKey> delayQueue;
	private final ReferenceQueue<? super Object> softReferenceQueue;
	private int maxSize;

	public SoftReferenceExpiringMap(int maxSize) {
		this.maxSize = Math.max(16, maxSize);
		internalMap = new ConcurrentHashMap<String, SoftValue<Object, String>>(this.maxSize / 4, 0.9F);
		delayQueue = new DelayQueue<ExpiringKey>();
		softReferenceQueue = new ReferenceQueue<Object>();
		Thread t = new Thread() {
			@Override
			public void run() {
				daemon();
			}
		};
		t.setDaemon(true);
		t.start();
	}

	@Override
	public int size() {
		processSoftReferenceQueue();
		return internalMap.size();
	}

	@Override
	public boolean isEmpty() {
		processSoftReferenceQueue();
		return internalMap.isEmpty();
	}

	@Override
	public boolean containsKey(String key) {
		processSoftReferenceQueue();
		return internalMap.containsKey(key);
	}

	@Override
	public Object get(String key) {
		processSoftReferenceQueue();
		SoftValue<Object, String> value = internalMap.get(key);
		if (value == null) {
			return Constants.NO_DATA;
		}
		return value.get();
	}

	@Override
	public void put(String key, Object value, long expire, TimeUnit timeUnit) {
		processSoftReferenceQueue();
		if (internalMap.size() >= maxSize) {
			return;
		}
		internalMap.put(key, new SoftValue(value, key, softReferenceQueue));
		delayQueue.offer(new ExpiringKey(key, expire, timeUnit));
	}

	@Override
	public void remove(String key) {
		if (internalMap.containsKey(key)) {
			delayQueue.remove(ExpiringKey.useForRemove(key));
			internalMap.remove(key);
		}
	}

	@Override
	public void clear() {
		internalMap.clear();
		delayQueue.clear();
		processSoftReferenceQueue();
	}

	private void processSoftReferenceQueue() {
		SoftValue sv;
		while ((sv = (SoftValue) softReferenceQueue.poll()) != null) {
			internalMap.remove(sv.getKey());
			delayQueue.remove(ExpiringKey.useForRemove(sv.getKey()));
		}
	}

	public void daemon() {
		while (true) {
			try {
				ExpiringKey delayedItem = delayQueue.take();
				if (delayedItem != null) {
					internalMap.remove(delayedItem.getKey());
				}
			} catch (InterruptedException e) {
				log.error("SoftReferenceExpiringMap daemon ERROR", e);
			}
		}
	}

	private static class ExpiringKey<K> implements Delayed {
		private final long startTime = System.currentTimeMillis();
		private final long expire;
		private final K key;

		public ExpiringKey(K key, long expire, TimeUnit timeUnit) {
			this.expire = TimeUnit.MILLISECONDS.convert(expire, timeUnit);
			this.key = key;
		}

		public static ExpiringKey useForRemove(Object key) {
			return new ExpiringKey(key, 0L, TimeUnit.SECONDS);
		}

		public K getKey() {
			return key;
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object object) {
			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;
			ExpiringKey<K> that = (ExpiringKey<K>) object;
			return Objects.equal(key, that.key);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(key);
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(getDelayMillis(), TimeUnit.MILLISECONDS);
		}

		private long getDelayMillis() {
			return (startTime + expire) - System.currentTimeMillis();
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compareTo(Delayed that) {
			if (this.getDelayMillis() == ((ExpiringKey) that).getDelayMillis()) {
				return 0;
			} else {
				return this.getDelayMillis() - ((ExpiringKey) that).getDelayMillis() > 0 ? 1 : -1;
			}
		}

		@Override
		public String toString() {
			return "ExpiringKey{" +
					"startTime=" + startTime +
					", expire=" + expire +
					", key=" + key +
					'}';
		}
	}

	private static class SoftValue<Object, String> extends SoftReference<Object> {
		private final String key;

		private SoftValue(Object value, String key, ReferenceQueue<? super Object> queue) {
			super(value, queue);
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
