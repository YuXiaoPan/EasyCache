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
import org.testng.annotations.Test;

import javax.annotation.Resource;

import static li.allan.Test_Constants.DEFAULT_TEST_EXPIRE_IN_SECOND;
import static li.allan.Test_Constants.TEST_STRING;
import static org.testng.Assert.*;

/**
 * @author LiALuN
 */
@Component
public class CacheDelAnnotationTest extends TestBase {
	@Resource
	private CacheDelAnnotationTest cacheDelAnnotationTest;


	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void baseMethodTest() throws InterruptedException {
		String cacheKey = generateCacheKey();
		//insert test data
		getCacheOperator().setWithExpire(cacheKey, TEST_STRING, DEFAULT_TEST_EXPIRE_IN_SECOND, getKeySerializer(), getValueSerializer());
		assertNotNull(getCacheOperator().getByKey(generateCacheKey(), String.class, getKeySerializer(), getValueSerializer()));
		//delete data
		cacheDelAnnotationTest.baseMethod();
		assertFalse(isHaveRecord(generateCacheKey()));
	}

	@CacheDel("CacheDelAnnotationTest_baseMethod")
	public void baseMethod() {
		//do nothing
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void withParamTest() {
		String cacheKey = generateCacheKey("str", TEST_STRING);
		//insert test data
		getCacheOperator().setWithExpire(cacheKey, TEST_STRING, DEFAULT_TEST_EXPIRE_IN_SECOND, getKeySerializer(), getValueSerializer());
		assertNotNull(getCacheOperator().getByKey(cacheKey, String.class, getKeySerializer(), getValueSerializer()));
		//delete data
		cacheDelAnnotationTest.withParam(TEST_STRING);
		assertFalse(isHaveRecord(cacheKey));
	}

	@CacheDel("CacheDelAnnotationTest_withParam")
	public void withParam(String str) {
		//do nothing
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void withParamAnnotationTest() {
		String cacheKey = generateCacheKey("s", TEST_STRING, "user", getUser().getName());
		//insert test data
		getCacheOperator().setWithExpire(cacheKey, TEST_STRING, DEFAULT_TEST_EXPIRE_IN_SECOND, getKeySerializer(), getValueSerializer());
		assertNotNull(getCacheOperator().getByKey(cacheKey, String.class, getKeySerializer(), getValueSerializer()));
		//delete data
		cacheDelAnnotationTest.withParamAnnotation(TEST_STRING, getUser());
		assertFalse(isHaveRecord(cacheKey));
	}

	@CacheDel("CacheDelAnnotationTest_withParamAnnotation")
	public void withParamAnnotation(@KeyParam("s") String str, @KeyParam(param = "name") User user) {
		//do nothing
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void withUnlessTest() {
		String cacheKey = generateCacheKey();
		//insert test data
		getCacheOperator().setWithExpire(cacheKey, TEST_STRING, DEFAULT_TEST_EXPIRE_IN_SECOND, getKeySerializer(), getValueSerializer());
		assertNotNull(getCacheOperator().getByKey(cacheKey, String.class, getKeySerializer(), getValueSerializer()));
		//delete data
		cacheDelAnnotationTest.withUnlessIsTrue();
		assertTrue(isHaveRecord(cacheKey));
		//delete data
		cacheDelAnnotationTest.withUnlessIsFalse();
		assertFalse(isHaveRecord(cacheKey));
	}

	@CacheDel(value = "CacheDelAnnotationTest_withUnless", unless = "#result")
	public boolean withUnlessIsTrue() {
		return true;
	}

	@CacheDel(value = "CacheDelAnnotationTest_withUnless", unless = "#result")
	public boolean withUnlessIsFalse() {
		return false;
	}
}
