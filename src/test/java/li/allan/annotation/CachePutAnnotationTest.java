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

import static li.allan.Test_Constants.DEFAULT_TEST_EXPIRE_IN_SECOND;
import static org.testng.Assert.assertEquals;

/**
 * @author LiALuN
 */
@Component
public class CachePutAnnotationTest extends TestBase {
	@Resource
	private CachePutAnnotationTest cachePutAnnotationTest;

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 1000)
	public void cacheResultTest() {
		String cacheKey = generateCacheKey();
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache put
		cachePutAnnotationTest.cacheResult();
		assertEquals(getCacheOperator().getByKey(cacheKey, User.class, getKeySerializer(), getValueSerializer()), getUser());
		//delete data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@CachePut(value = "CachePutAnnotationTest_cacheResult", cache = "#result")
	public User cacheResult() {
		return getUser();
	}

	@Test(timeOut = DEFAULT_TEST_EXPIRE_IN_SECOND * 100000)
	public void cacheParamTest() {
		String cacheKey = generateCacheKey("user", getUser().getName());
		//clean dirty data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
		Assert.assertTrue(!isHaveRecord(cacheKey));
		//test cache put
		cachePutAnnotationTest.cacheParam(getUser());
		assertEquals(getCacheOperator().getByKey(cacheKey, User.class, getKeySerializer(), getValueSerializer()), getUser());
		//delete data
		getCacheOperator().removeByKey(cacheKey, getKeySerializer());
	}

	@CachePut(value = "CachePutAnnotationTest_cacheParam", cache = "#user")
	public void cacheParam(@KeyParam(param = "getName()") User user) {

	}
}
