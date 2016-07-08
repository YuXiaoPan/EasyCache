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
import li.allan.exception.KeyParamNotSupportException;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resource;

import static li.allan.Test_Constants.DEFAULT_TEST_EXPIRE_IN_SECOND;

@Component
public class KeyParamAnnotationTest extends TestBase {
	@Resource
	private KeyParamAnnotationTest keyParamAnnotationTest;

	@Test
	public void noParamTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.noParam(), keyParamAnnotationTest.noParam());
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String noParam() {
		return randomString(32);
	}

	@Test
	public void withParamTest() {
		String cacheKey = generateCacheKey("p1", "v1");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.withParam("v1"), keyParamAnnotationTest.withParam("v1"));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String withParam(String p1) {
		return randomString(32);
	}

	@Test
	public void someParamWithAnnotationTest() {
		String cacheKey = generateCacheKey("p1", "v1", "p2", "v2");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.someParamWithAnnotation("v1", "v2"), keyParamAnnotationTest.someParamWithAnnotation("v1", "v2"));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String someParamWithAnnotation(@KeyParam String p1, String p2) {
		return randomString(32);
	}

	@Test
	public void someParamNotCacheTest() {
		String cacheKey = generateCacheKey("p1", "v1", "p3", "v3");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.someParamNotCache("v1", "v2", "v3"), keyParamAnnotationTest.someParamNotCache("v1", "v2", "v3"));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String someParamNotCache(String p1, @KeyParam(ignore = true) String p2, @KeyParam String p3) {
		return randomString(32);
	}

	@Test
	public void paramWithValueTest() {
		String cacheKey = generateCacheKey("pName", "v1");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.paramWithValue("v1"), keyParamAnnotationTest.paramWithValue("v1"));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String paramWithValue(@KeyParam("pName") String p1) {
		return randomString(32);
	}

	@Test
	public void paramWithOrderTest() {
		String cacheKey = generateCacheKey("p2", "v2", "p1", "v1", "p3", "v3");
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.paramWithOrder("v1", "v2", "v3"), keyParamAnnotationTest.paramWithOrder("v1", "v2", "v3"));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public String paramWithOrder(@KeyParam(order = 1) String p1, @KeyParam(order = 2) String p2, @KeyParam String p3) {
		return randomString(32);
	}

	@Test
	public void paramWithSpELTest() throws InterruptedException {
		String cacheKey = generateCacheKey("user", getUser().getName());
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.paramWithSpEL(getUser()), keyParamAnnotationTest.paramWithSpEL(getUser()));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache
	public User paramWithSpEL(@KeyParam(param = "name") User user) {
		return user;
	}

	@Test(expectedExceptions = KeyParamNotSupportException.class)
	public void paramWithIllegalSpELTest() throws InterruptedException {
		keyParamAnnotationTest.paramWithIllegalSpEL(getUser());
	}

	@EasyCache
	public User paramWithIllegalSpEL(@KeyParam(param = "abc") User user) {
		return user;
	}

	@Test(expectedExceptions = KeyParamNotSupportException.class)
	public void paramIllegalTest() throws InterruptedException {
		keyParamAnnotationTest.paramIllegal(getUser());

	}

	@EasyCache
	public User paramIllegal(User user) {
		return user;
	}

	@Test(expectedExceptions = KeyParamNotSupportException.class)
	public void paramIllegalWithAnnotationTest() throws InterruptedException {
		keyParamAnnotationTest.paramIllegal(this);
	}


	@EasyCache
	public String paramIllegal(@KeyParam(param = "getUser()") TestBase testBase) {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void nullValueTest() {
		String cacheKey = generateCacheKey("str", null);
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.nullValue(null), keyParamAnnotationTest.nullValue(null));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache()
	public String nullValue(String str) {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void nullValueWithNotSupportTest() {
		String cacheKey = generateCacheKey("user", null);
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.nullValueWithNotSupport(null), keyParamAnnotationTest.nullValueWithNotSupport(null));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache()
	public String nullValueWithNotSupport(User user) {
		return randomString(32);
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void nullValueWithSpELTest() {
		/*
			null param
		 */
		String cacheKey = generateCacheKey("user", null);
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.nullValueWithSpEL(null), keyParamAnnotationTest.nullValueWithSpEL(null));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		/*
			null param
		 */
		User user = new User(null, 18);
		cacheKey = generateCacheKey("user", null);
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache
		Assert.assertEquals(keyParamAnnotationTest.nullValueWithSpEL(user), keyParamAnnotationTest.nullValueWithSpEL(user));
		Assert.assertTrue(isHaveRecord(cacheKey));
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@EasyCache()
	public String nullValueWithSpEL(@KeyParam(param = "getName()") User user) {
		return randomString(32);
	}
}
