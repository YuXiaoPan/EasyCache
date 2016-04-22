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

package li.allan.cache.impl;

import com.google.common.base.Strings;
import li.allan.annotation.KeyParam;
import li.allan.cache.operator.CacheOperator;
import li.allan.config.base.ConfigBase;
import li.allan.exception.KeyParamNotSupportException;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static li.allan.utils.Constants.*;
import static li.allan.utils.SpELUtils.*;

/**
 * Cache on Method
 *
 * @author LiALuN
 */
public class MethodCache {
	private Log log = LogFactory.getLog(MethodCache.class);
	CacheOperator cacheOperator = CacheOperator.getInstance();

	/**
	 * 生成缓存Key字符串
	 */
	protected String getCacheKeyName(String userSpecifyKeyName, String className, String methodName, List<MethodParam> methodParams) {
		String cacheKeyInMethodPart = getCacheKeyInMethodPart(userSpecifyKeyName, className, methodName);
		String cacheKeyInParamPart = getCacheKeyInParamPart(methodParams);
		return cacheKeyInMethodPart + (Strings.isNullOrEmpty(cacheKeyInParamPart) ? "" : KEY_SEPARATOR + cacheKeyInParamPart);
	}

	/**
	 * 判断是否符合条件
	 *
	 * @param unlessSpEL 判断是否需要缓存的SpEL表达式
	 */
	protected boolean onCondition(String unlessSpEL, Object result, List<MethodParam> params) {
		if (Strings.isNullOrEmpty(unlessSpEL)) {
			return false;
		}
		try {
			StandardEvaluationContext context = setSpELVariable(result, params);
			return condition(context, unlessSpEL);
		} catch (SpelEvaluationException e) {
			throw new KeyParamNotSupportException("EasyCache unless ERROR: " + unlessSpEL + "," + e.getMessage(), e);
		}
	}

	protected Object getValueFromInvoke(String unlessSpEL, Object result, List<MethodParam> params) {
		try {
			StandardEvaluationContext context = setSpELVariable(result, params);
			return getValue(context, unlessSpEL);
		} catch (SpelEvaluationException e) {
			throw new KeyParamNotSupportException("EasyCache unless ERROR: " + unlessSpEL + "," + e.getMessage(), e);
		}
	}

	private StandardEvaluationContext setSpELVariable(Object result, List<MethodParam> params) {
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable(RESULT, result);
		if (params != null && params.size() > 0) {
			for (MethodParam param : params) {
				context.setVariable(param.getParamName(), param.getValue());
			}
		}
		return context;
	}

	/**
	 * 计算缓存过期时间
	 */
	protected int expireTime(String expireSpEL) {
		if (!Strings.isNullOrEmpty(expireSpEL)) {
			try {
				return getTime(expireSpEL);
			} catch (SpelEvaluationException e) {
				log.error("EasyCache annotation expire param ERROR, return type must be Integer.", e);
			}
		}
		if (ConfigBase.getConfigProperties().getDefaultCacheExpire() >= 0) {
			return ConfigBase.getConfigProperties().getDefaultCacheExpire();
		}
		return DEFAULT_CACHE_EXPIRE;
	}

	/**
	 * 得到缓存Key中与方法有关的部分。
	 */
	private String getCacheKeyInMethodPart(String userSpecifyKeyName, String className, String methodName) {
		if (!Strings.isNullOrEmpty(userSpecifyKeyName)) {
			return userSpecifyKeyName;
		} else {
			return className + KEY_SEPARATOR + methodName;
		}
	}

	/**
	 * 得到缓存Key中与参数有关的部分。
	 */
	private String getCacheKeyInParamPart(final List<MethodParam> methodParams) {
		if (methodParams.size() == 0) {
			return "";
		}
		/**
		 * 遍历每个参数，获取用于缓存的name和value，与order一起封装为一个对象，并排序。
		 */
		List<ParamWithOrder> paramWithOrders = new ArrayList<ParamWithOrder>();
		for (MethodParam methodParam : methodParams) {
			if (methodParam.getKeyParam() != null && methodParam.getKeyParam().ignore()) {
				continue;
			}
			String name = getMethodParamCacheName(methodParam);
			Object value = getMethodParamCacheValue(methodParam);
			int order = methodParam.getKeyParam() == null ? MIN_ORDER : methodParam.getKeyParam().order();
			paramWithOrders.add(new ParamWithOrder(order, name, value));
		}
		Collections.sort(paramWithOrders);
		/**
		 * 按顺序拼装缓存Key
		 */
		StringBuilder key = new StringBuilder();
		for (ParamWithOrder paramWithOrder : paramWithOrders) {
			key.append(paramWithOrder.getName()).append(KEY_SEPARATOR);
			key.append(paramWithOrder.getValue()).append(KEY_SEPARATOR);
		}
		return key.substring(0, key.length() - 1);
	}

	/**
	 * 得到方法用于缓存的名称
	 */
	private String getMethodParamCacheName(MethodParam methodParam) {
		if (methodParam.getKeyParam() != null && !Strings.isNullOrEmpty(methodParam.getKeyParam().value())) {
			return methodParam.getKeyParam().value();
		} else {
			return methodParam.getParamName();
		}
	}

	/**
	 * 得到方法用于缓存的数据
	 */
	private Object getMethodParamCacheValue(MethodParam methodParam) {
		if (methodParam.getValue() == null) {
			return null;
		}
		Object value;
		if (methodParam.getKeyParam() == null || Strings.isNullOrEmpty(methodParam.getKeyParam().param())) {
			value = methodParam.getValue();
		} else {
			try {
				value = getValue(methodParam.getValue(), methodParam.getKeyParam().param());
				if (value == null) {
					return null;
				}
			} catch (SpelEvaluationException e) {
				throw new KeyParamNotSupportException("KeyParam param ERROR: " + methodParam.getKeyParam() + "," + e.getMessage(), e);
			}
		}
		if (!isKeyParamTypeSupport(value.getClass())) {
			throw new KeyParamNotSupportException("Cache KeyParam only support Primitive Type OR String");
		}
		return value;
	}

	/**
	 * 判断缓存方法参数类型是否被支持。
	 * 由于要将KeyParam序列化为字符串，生成缓存Key，目前缓存方法参数类型仅支持基本类型Date和String
	 */
	private boolean isKeyParamTypeSupport(Class cls) {
		return cls.isPrimitive() || cls == Boolean.class || cls == Byte.class ||
				cls == Character.class || cls == Short.class || cls == Integer.class ||
				cls == Long.class || cls == Float.class || cls == Double.class ||
				cls == String.class || cls == Date.class;
	}

	/**
	 * 切面方法中的参数信息
	 */
	public class MethodParam {
		//参数注解信息，如果没有注解则为null
		private KeyParam keyParam;
		//参数名称
		private String paramName;
		//参数类型
		private Class type;
		//参数值
		private Object value;

		public KeyParam getKeyParam() {
			return keyParam;
		}

		public void setKeyParam(KeyParam keyParam) {
			this.keyParam = keyParam;
		}

		public String getParamName() {
			return paramName;
		}

		public void setParamName(String paramName) {
			this.paramName = paramName;
		}

		public Class getType() {
			return type;
		}

		public void setType(Class type) {
			this.type = type;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}

	private class ParamWithOrder implements Comparable {
		private int order;
		private String name;
		private Object value;

		public ParamWithOrder(int order, String name, Object value) {
			this.order = order;
			this.name = name;
			this.value = value;
		}

		public int getOrder() {
			return order;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}

		/**
		 * first order by 'order' from big to small.
		 * then order by 'name' string
		 */
		public int compareTo(Object o) {
			ParamWithOrder tmp = (ParamWithOrder) o;
			if (order != tmp.getOrder()) {
				return order > tmp.getOrder() ? -1 : 1;
			}
			return name.compareTo(tmp.getName());
		}
	}

	public CacheOperator getCacheOperator() {
		return cacheOperator;
	}
}
