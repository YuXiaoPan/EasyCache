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

package li.allan;

import li.allan.cache.operator.CacheOperator;
import li.allan.cache.operator.impl.redis.RedisOperator;
import li.allan.domain.User;
import li.allan.serializer.Serializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Random;

import static li.allan.Test_Constants.KEY_SEPARATOR;
import static li.allan.Test_Constants.TEST_REDIS_PORT;

/**
 * @author LiALuN
 */
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class TestBase extends AbstractTestNGSpringContextTests {
	public RedisOperator getRedisOperator() {
		return CacheOperator.getInstance().getMainCacheOperator(RedisOperator.class);
	}

	RedisServer redisServer;

	@BeforeSuite
	public void startRedis() throws IOException {
		RedisExecProvider customProvider = RedisExecProvider.defaultProvider();
//				.override(OS.WINDOWS, Architecture.x86_64, "D:\\Redis-x64-3.0.501\\redis-server.exe");
		redisServer = new RedisServer(customProvider, TEST_REDIS_PORT);
		redisServer.start();
	}

	@AfterSuite
	public void stopRedis() {
		redisServer.stop();
	}

	public static String randomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(str.length());
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}

	/**
	 * use stack to get invoke method class and method name,and generate cache class name.
	 *
	 * @param invokeLevel 0 this method itself. 1 first method to invoke this method. 2...
	 */
	public String getCacheClassName(int invokeLevel, boolean removeTest) {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		//get simple class name
		String clazz = stack[invokeLevel].getClassName();
		if (clazz.indexOf(".") > 0) {
			clazz = clazz.substring(clazz.lastIndexOf(".") + 1);
		}
		//get method name
		String method = stack[invokeLevel].getMethodName();
		if (removeTest) {
			method = method.replaceAll("Test(?!.*Test)", "");
		}
		return clazz + KEY_SEPARATOR + method;
	}

	public String generateCacheKey(String... methodParamNameAndValue) {
		StringBuilder str = new StringBuilder(getCacheClassName(2, true));
		str.append(KEY_SEPARATOR);
		for (int i = 0; i < methodParamNameAndValue.length; i += 2) {
			str.append(methodParamNameAndValue[i]).append(KEY_SEPARATOR).append(methodParamNameAndValue[i + 1]).append(KEY_SEPARATOR);
		}
		return str.substring(0, str.length() - 1);
	}

	public String generateCacheKeyWithSpecifyCacheClassName(String specifyCacheClassName, String... methodParamNameAndValue) {
		StringBuilder str = new StringBuilder(specifyCacheClassName);
		str.append(KEY_SEPARATOR);
		for (int i = 0; i < methodParamNameAndValue.length; i += 2) {
			str.append(methodParamNameAndValue[i]).append(KEY_SEPARATOR).append(methodParamNameAndValue[i + 1]);
		}
		return str.substring(0, str.length() - 1);
	}

	public User getUser() {
		return new User("LiALuN", 18);
	}

	public boolean isHaveRecord(String key) {
		Object resp = getRedisOperator().getByKey(key, Object.class);
		return resp == null || !resp.equals(Serializer.NO_DATA);
	}
}