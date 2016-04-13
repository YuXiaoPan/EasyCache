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

package li.allan.annotation;

import li.allan.TestBase;
import li.allan.domain.User;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.Random;

import static li.allan.Test_Constants.DEFAULT_TEST_EXPIRE_IN_SECOND;

@Component
public class EasyCacheAnnotationTest extends TestBase {
	@Resource
	private EasyCacheAnnotationTest easyCacheAnnotationTest;

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void baseMethodTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.baseMethod(), easyCacheAnnotationTest.baseMethod());
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
	}

	@EasyCache
	public String baseMethod() {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void withValueTest() {
		String cacheKey = generateCacheKeyWithSpecifyCacheClassName("cacheName");
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.withValue(), easyCacheAnnotationTest.withValue());
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
	}

	@EasyCache("cacheName")
	public String withValue() {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void withExpireTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.withExpire(), easyCacheAnnotationTest.withExpire());
		Assert.assertEquals(getRedisOperator().TTL(cacheKey), DEFAULT_TEST_EXPIRE_IN_SECOND * 2);
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
	}

	@EasyCache(expired = "T(li.allan.Test_Constants).DEFAULT_TEST_EXPIRE_IN_SECOND * 2")
	public String withExpire() {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void withExpireMethodTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.withExpireMethod(), easyCacheAnnotationTest.withExpireMethod());
		Assert.assertEquals(getRedisOperator().TTL(cacheKey), 16888);
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
	}

	@EasyCache(expired = "T(li.allan.annotation.EasyCacheAnnotationTest).expireTime()")
	public String withExpireMethod() {
		return randomString(32);
	}

	public static int expireTime() {
		return 16888;
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 100000000)
	public void paramWithSpEL() {
		/*
			test unless with result param
		 */
		String cacheKey = generateCacheKeyWithSpecifyCacheClassName("EasyCacheAnnotationTest_unlessIsFalse", "user", "LiALuN");
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertNotEquals(easyCacheAnnotationTest.unlessIsTrue(getUser()),
				easyCacheAnnotationTest.unlessIsTrue(getUser()));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);

		cacheKey = generateCacheKeyWithSpecifyCacheClassName("EasyCacheAnnotationTest_unlessIsTrue", "user", "LiALuN");
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.unlessIsFalse(getUser()),
				easyCacheAnnotationTest.unlessIsFalse(getUser()));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		/*
			test unless with method param
		 */
		cacheKey = generateCacheKeyWithSpecifyCacheClassName("EasyCacheAnnotationTest_unlessWithMethodParam", "user", "LiALuN");
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.unlessWithMethodParam(getUser()),
				easyCacheAnnotationTest.unlessWithMethodParam(getUser()));
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
	}

	@EasyCache(unless = "#result.getName() == 'LiALuN'")
	public User unlessIsTrue(@KeyParam(param = "getName()") User user) {
		user.setAge(new Random().nextInt(99));
		return user;
	}

	@EasyCache(unless = "#result.getName() == 'someone else'")
	public User unlessIsFalse(@KeyParam(param = "getName()") User user) {
		user.setAge(new Random().nextInt(99));
		return user;
	}

	@EasyCache(unless = "#user.getAge() == 100")
	public User unlessWithMethodParam(@KeyParam(param = "getName()") User user) {
		user.setAge(new Random().nextInt(99));
		return user;
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void nullResultTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.nullResult(), easyCacheAnnotationTest.nullResult());
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getRedisOperator().removeByKey(cacheKey);
	}

	@EasyCache
	public String nullResult() {
		return null;
	}
}
