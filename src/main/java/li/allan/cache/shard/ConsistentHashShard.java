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

package li.allan.cache.shard;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.hash.Hashing;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * 通过一致性hash和虚拟bucket，进行数据分片
 *
 * @author LiALuN
 */
public class ConsistentHashShard<T> extends ShardMethod<T> {
	/**
	 * Hash Bucket Size
	 */
	private static final int BUCKET_SIZE = 4096;
	/**
	 * Virtual Bucket Number Per Object
	 */
	private static final int VIRTUAL_BUCKET_NUMBER = 8;

	RangeMap<Integer, T> bucketRangeMap;

	public ConsistentHashShard(Collection<T> list) {
		Preconditions.checkArgument(list != null, "ConsistentHashShard list can't be NULL");
		Preconditions.checkArgument(list.size() > 0, "ConsistentHashShard list size must bigger than 0");
		/**
		 * 将每个对象生成{@link virtualBucketNumber}个一致性hash捅位置。
		 */
		Map<Integer, T> consistentHashBuckets = new TreeMap<Integer, T>();
		for (T tmp : list) {
			int hashCode = tmp.hashCode();
			for (int i = 0; i < VIRTUAL_BUCKET_NUMBER; i++) {
				hashCode = Hashing.sha512().hashInt(hashCode).hashCode();
				consistentHashBuckets.put(Hashing.consistentHash(hashCode, BUCKET_SIZE), tmp);
			}
		}
		/**
		 * 捅分布
		 */
		RangeMap<Integer, T> bucketRangeMapTmp = TreeRangeMap.create();
		int bucketPrev;
		int bucketNext = 0;
		for (Map.Entry<Integer, T> entry : consistentHashBuckets.entrySet()) {
			bucketPrev = bucketNext;
			bucketNext = entry.getKey();
			bucketRangeMapTmp.put(Range.closedOpen(bucketPrev, bucketNext), entry.getValue());
		}
		bucketRangeMapTmp.put(Range.atLeast(bucketNext), consistentHashBuckets.entrySet().iterator().next().getValue());
		bucketRangeMap = bucketRangeMapTmp;
	}

	@Override
	public T get(Object object) {
		return bucketRangeMap.get(Hashing.consistentHash(object.hashCode(), BUCKET_SIZE));
	}
}
