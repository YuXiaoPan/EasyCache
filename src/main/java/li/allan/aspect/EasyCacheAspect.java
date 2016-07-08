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

package li.allan.aspect;

import li.allan.annotation.CacheDel;
import li.allan.annotation.CachePut;
import li.allan.annotation.EasyCache;
import li.allan.annotation.KeyParam;
import li.allan.cache.impl.MethodCache;
import li.allan.config.base.ConfigBase;
import li.allan.exception.SerializationException;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import li.allan.serializer.Serializer;
import li.allan.serializer.SerializerContainer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static li.allan.utils.Constants.NoData;

/**
 * @author LiALuN
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
public class EasyCacheAspect extends MethodCache {
	Log log = LogFactory.getLog(EasyCacheAspect.class);

	@Pointcut("@annotation(li.allan.annotation.EasyCache)")
	public void cacheMethod() {

	}

	@Around("cacheMethod()")
	public Object cacheMethod(ProceedingJoinPoint point) throws Throwable {
		/**
		 * get all params for cache method
		 */
		String className = point.getTarget().getClass().getSimpleName();
		Method method = getMethodFromProceedingJoinPoint(point);
		Class returnType = method.getReturnType();
		//method return void don't need cache
		if (returnType.getName().equals("void")) {
			return point.proceed();
		}
		List<MethodParam> methodParams = getParamsFromMethod(method, point.getArgs());
		EasyCache cacheAnnotation = getMethodFromProceedingJoinPoint(point).getAnnotation(EasyCache.class);
		log.debug("Annotation cache method start, proceeding join point at " + className + "." + method.getName());
		/**
		 * generate cache key
		 */
		String cacheKeyName = getCacheKeyName(cacheAnnotation.value(), className, method.getName(), methodParams);
		log.debug("Generate cache key = " + cacheKeyName);
		/**
		 * serializer
		 */
		Serializer keySerializer = SerializerContainer.getSerializer(ConfigBase.getConfigProperties().getKeySerializer());
		Serializer valueSerializer = SerializerContainer.getSerializer(cacheAnnotation.serializer());
		/**
		 * try to get cache
		 */
		try {
			log.debug("Try to get cache");
			Object resp = getCacheOperator().getByKey(cacheKeyName, returnType, keySerializer, valueSerializer);
			if (!(resp instanceof NoData)) {
				return resp;
			}
		} catch (SerializationException e) {
			log.warn("EasyCache deserialization FAIL", e);
		} catch (Exception e) {
			log.error("EasyCache get cache ERROR", e);
		}
		/**
		 * invoke method
		 */
		log.debug("Invoke origin method");
		Object resp = point.proceed();
		/**
		 * save cache data
		 */
		try {
			if (!onCondition(cacheAnnotation.unless(), resp, methodParams)) {
				log.debug("Try to set cache");
				int expireTime = expireTime(cacheAnnotation.expired());
				if (expireTime < 0) {
					getCacheOperator().set(cacheKeyName, resp, keySerializer, valueSerializer);
				} else {
					getCacheOperator().setWithExpire(cacheKeyName, resp, expireTime, keySerializer, valueSerializer);
				}
			}
		} catch (Exception e) {
			log.error("EasyCache set cache ERROR", e);
		}
		return resp;
	}

	@Pointcut("@annotation(li.allan.annotation.CacheDel)")
	public void cacheDel() {

	}

	@Around("cacheDel()")
	public Object cacheDel(ProceedingJoinPoint point) throws Throwable {
		/**
		 * get all params for cache method
		 */
		String className = point.getTarget().getClass().getSimpleName();
		Method method = getMethodFromProceedingJoinPoint(point);
		List<MethodParam> methodParams = getParamsFromMethod(method, point.getArgs());
		CacheDel cacheDel = getMethodFromProceedingJoinPoint(point).getAnnotation(CacheDel.class);
		log.debug("Annotation cacheDel method start, proceeding join point at " + className + "." + method.getName());
		/**
		 * generate cache key
		 */
		String cacheKeyName = getCacheKeyName(cacheDel.value(), className, method.getName(), methodParams);
		log.debug("Generate cache key = " + cacheKeyName);
		/**
		 * serializer
		 */
		Serializer keySerializer = SerializerContainer.getSerializer(ConfigBase.getConfigProperties().getKeySerializer());
		/**
		 * proceed method
		 */
		Object resp = point.proceed();
		/**
		 * try to delete cache data
		 */
		try {
			if (!onCondition(cacheDel.unless(), resp, methodParams)) {
				log.debug("Try to delete cache");
				getCacheOperator().removeByKey(cacheKeyName, keySerializer);
			}
		} catch (Exception e) {
			log.error("EasyCache delete cache ERROR", e);
		}
		return resp;
	}

	@Pointcut("@annotation(li.allan.annotation.CachePut)")
	public void cachePut() {

	}

	@Around("cachePut()")
	public Object cachePut(ProceedingJoinPoint point) throws Throwable {
		/**
		 * get all params for cache method
		 */
		String className = point.getTarget().getClass().getSimpleName();
		Method method = getMethodFromProceedingJoinPoint(point);
		List<MethodParam> methodParams = getParamsFromMethod(method, point.getArgs());
		CachePut cacheAnnotation = getMethodFromProceedingJoinPoint(point).getAnnotation(CachePut.class);
		log.debug("Annotation cachePut method start, proceeding join point at " + className + "." + method.getName());
		/**
		 * generate cache key
		 */
		String cacheKeyName = getCacheKeyName(cacheAnnotation.value(), className, method.getName(), methodParams);
		log.debug("Generate cache key = " + cacheKeyName);
		/**
		 * serializer
		 */
		Serializer keySerializer = SerializerContainer.getSerializer(ConfigBase.getConfigProperties().getKeySerializer());
		Serializer valueSerializer = SerializerContainer.getSerializer(cacheAnnotation.serializer());

		/**
		 * proceed method
		 */
		Object resp = point.proceed();
		/**
		 * try to update cache data
		 */
		try {
			if (!onCondition(cacheAnnotation.unless(), resp, methodParams)) {
				log.debug("Try to update cache data");
				Object cacheObject = getValueFromInvoke(cacheAnnotation.cache(), resp, methodParams);
				int expireTime = expireTime(cacheAnnotation.expired());
				if (expireTime < 0) {
					getCacheOperator().set(cacheKeyName, cacheObject, keySerializer, valueSerializer);
				} else {
					getCacheOperator().setWithExpire(cacheKeyName, cacheObject, expireTime, keySerializer, valueSerializer);
				}
			}
		} catch (Exception e) {
			log.error("EasyCache update data ERROR", e);
		}
		return resp;
	}

	private List<MethodParam> getParamsFromMethod(Method method, Object[] args) {
		List<MethodParam> list = new ArrayList<MethodParam>();
		LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNames = discoverer.getParameterNames(method);
		for (int i = 0; i < args.length; i++) {
			MethodParam methodParam = new MethodParam();
			list.add(methodParam);
			methodParam.setValue(args[i]);
			methodParam.setParamName(paraNames[i]);
			for (Annotation annotation : method.getParameterAnnotations()[i]) {
				if (annotation instanceof KeyParam) {
					methodParam.setKeyParam((KeyParam) annotation);
					break;
				}
			}
		}
		return list;
	}

	private static Method getMethodFromProceedingJoinPoint(ProceedingJoinPoint point) throws NoSuchMethodException {
		//get method from MethodSignature
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		// signature.getMethod() will return the method of the interface,
		// be sure to get the method of the implementation class
		if (method.getDeclaringClass().isInterface()) {
			method = point.getTarget().getClass().getDeclaredMethod(point.getSignature().getName(),
					method.getParameterTypes());
		}
		return method;
	}
}
