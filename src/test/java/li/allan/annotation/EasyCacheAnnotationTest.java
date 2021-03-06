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
import li.allan.serializer.JdkSerializer;
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
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.baseMethod(), easyCacheAnnotationTest.baseMethod());
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String baseMethod() {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void withValueTest() {
		String cacheKey = generateCacheKeyWithSpecifyCacheClassName("cacheName");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.withValue(), easyCacheAnnotationTest.withValue());
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache("cacheName")
	public String withValue() {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000 + 1000)
	public void withExpireTest() throws InterruptedException {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.withExpire(), easyCacheAnnotationTest.withExpire());
		Thread.sleep(DEFAULT_TEST_EXPIRE_IN_SECOND * 1000 - 500);
		Assert.assertTrue(isHaveRecord(cacheKey));
		Thread.sleep(1000);
		Assert.assertFalse(isHaveRecord(cacheKey));
	}

	@EasyCache(expired = "T(li.allan.Test_Constants).DEFAULT_TEST_EXPIRE_IN_SECOND")
	public String withExpire() {
		return randomString(32);
	}

	@Test(timeOut = 5000)
	public void withExpireMethodTest() throws InterruptedException {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.withExpireMethod(), easyCacheAnnotationTest.withExpireMethod());
		Thread.sleep(expireTime() * 1000 - 500);
		Assert.assertTrue(isHaveRecord(cacheKey));
		Thread.sleep(1000);
		Assert.assertFalse(isHaveRecord(cacheKey));
	}

	@EasyCache(expired = "T(li.allan.annotation.EasyCacheAnnotationTest).expireTime()")
	public String withExpireMethod() {
		return randomString(32);
	}

	public static int expireTime() {
		return 3;
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 100000000)
	public void paramWithSpEL() {
		/*
			test unless with result param
		 */
		String cacheKey = generateCacheKeyWithSpecifyCacheClassName("EasyCacheAnnotationTest_unlessIsFalse", "user", "LiALuN");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertNotEquals(easyCacheAnnotationTest.unlessIsTrue(getUser()),
				easyCacheAnnotationTest.unlessIsTrue(getUser()));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());

		cacheKey = generateCacheKeyWithSpecifyCacheClassName("EasyCacheAnnotationTest_unlessIsTrue", "user", "LiALuN");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.unlessIsFalse(getUser()),
				easyCacheAnnotationTest.unlessIsFalse(getUser()));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		/*
			test unless with method param
		 */
		cacheKey = generateCacheKeyWithSpecifyCacheClassName("EasyCacheAnnotationTest_unlessWithMethodParam", "user", "LiALuN");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.unlessWithMethodParam(getUser()),
				easyCacheAnnotationTest.unlessWithMethodParam(getUser()));
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
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

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 10000000)
	public void nullResultTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		easyCacheAnnotationTest.nullResult();
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String nullResult() {
		return null;
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000000)
	public void withSerializerTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(easyCacheAnnotationTest.withSerializer(), easyCacheAnnotationTest.withSerializer());
		Assert.assertTrue(isHaveRecord(cacheKey,JdkSerializer.class));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache(serializer = JdkSerializer.class)
	public String withSerializer() {
		return randomString(32);
	}

}
