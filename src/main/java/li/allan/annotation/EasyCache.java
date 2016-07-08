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

import li.allan.serializer.Serializer;

import java.lang.annotation.*;

/**
 * @author LiALuN
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyCache {
	/**
	 * 方法在缓存Key中的名称。
	 * 如果本参数为空，方法在缓存中的名称为类名.方法名。
	 */
	String value() default "";

	/**
	 * Cache expire time, unit is second.-1 means not expire。
	 * If this param is blank mean use user config expire time.
	 * If user not config, default is not expire.
	 */
	String expired() default "";

	/**
	 * 不进行缓存的条件。
	 * 通过#result获取返回值。具体参考SpEL表达式。
	 */
	String unless() default "";

	Class<? extends Serializer> serializer() default Serializer.class;
}
