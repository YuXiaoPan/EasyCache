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

import java.lang.annotation.*;

/**
 * @author LiALuN
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CachePut {
	/**
	 * cache key replace for method part.
	 * cache key has two parts: method part and param part,it's connect by #{@link li.allan.utils.Constants#KEY_SEPARATOR}
	 * like {method part}_{param part}.if value() leave blank,method part will be {class name}_{method name}
	 */
	String value();

	/**
	 * 缓存过期时间，0为不过期。
	 * 如果本参数为空，默认过期时间为用户配置的时间。如果没有配置，则不过期。
	 */
	String expired() default "";

	/**
	 * 缓存的数据
	 */
	String cache();

	/**
	 * 不进行缓存的条件。
	 * 通过#result获取返回值。具体参考SpEL表达式。
	 */
	String unless() default "";
}
