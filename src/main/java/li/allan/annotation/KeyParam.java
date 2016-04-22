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
 * 缓存方法参数注解
 *
 * @author LiALuN
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KeyParam {
	/**
	 * 参数在缓存Key中的名称。
	 * 如果本参数为空，参数在缓存中的名称为参数名称。
	 */
	String value() default "";

	/**
	 * 缓存key中，各个参数的排序权重。权重越高，参数名越靠前。
	 * 相同的order按照字符顺序排序。
	 */
	int order() default 0;

	/**
	 * 如果参数为一个对象，则可以将对象中的一个基本类型参数作为缓存Key参数。
	 * 具体参考SpEL表达式。
	 */
	String param() default "";

	/**
	 * 是否忽略本参数作为缓存KEY。
	 */
	boolean ignore() default false;
}
