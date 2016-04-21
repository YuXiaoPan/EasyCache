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

package li.allan.utils;

import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author LiALuN
 */
public class SpELUtils {
	private static Log log = LogFactory.getLog(SpELUtils.class);

	public static Object getValue(Object object, String SpEL) {
		ExpressionParser parser = new SpelExpressionParser();
		return parser.parseExpression(SpEL).getValue(object, Object.class);
	}

	public static Object getValue(StandardEvaluationContext context, String spEL) {
		ExpressionParser parser = new SpelExpressionParser();
		return parser.parseExpression(spEL).getValue(context, Object.class);
	}

	public static boolean condition(Object object, String SpEL) {
		return (Boolean) getValue(object, SpEL);
	}

	public static boolean condition(StandardEvaluationContext context, String SpEL) {
		return (Boolean) getValue(context, SpEL);
	}

	private static final StandardEvaluationContext timeContext = new StandardEvaluationContext();

	static {
		try {
			timeContext.registerFunction("now", TimeUtil.class.getDeclaredMethod("now"));
			timeContext.registerFunction("today", TimeUtil.class.getDeclaredMethod("today"));
			timeContext.registerFunction("tomorrow", TimeUtil.class.getDeclaredMethod("tomorrow"));
			timeContext.registerFunction("diffTime", TimeUtil.class.getDeclaredMethod("diffTime", TimeUtil.CalendarWrapper.class, TimeUtil.CalendarWrapper.class));
		} catch (NoSuchMethodException e) {
			log.error("expire time SpEL Method INIT ERROR", e);
		}
	}

	public static int getTime(String SpEL) {
		return (Integer) getValue(timeContext, SpEL);
	}

	public static void main(String[] args) {
		String spEL = "#asdf";
		Object resp = getValue(new Object(),spEL);
		System.out.println(resp);
	}
}
