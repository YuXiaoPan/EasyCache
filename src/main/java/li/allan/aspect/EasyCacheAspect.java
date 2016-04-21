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
import li.allan.exception.SerializationException;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
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
		List<MethodParam> methodParams = getParamsFromMethod(method, point.getArgs());
		EasyCache cache = getMethodFromProceedingJoinPoint(point).getAnnotation(EasyCache.class);
		log.debug("Annotation Cache Method Start,Proceeding Join Point at " + className + "." + method.getName());
		/**
		 * generate cache key
		 */
		String cacheKeyName = getCacheKeyName(cache.value(), className, method.getName(), methodParams);
		log.debug("generate Cache Key=" + cacheKeyName);
		/**
		 * try to get cache
		 */
		try {
			log.debug("try to read data from cache");
			Object resp = getCacheOperator().getByKey(cacheKeyName, returnType);
			if (!(resp instanceof NoData)) {
				return resp;
			}
		} catch (SerializationException e) {
			log.warn("EasyCache Deserialization FAIL", e);
		} catch (Exception e) {
			log.error("EasyCache Get Data ERROR", e);
		}
		/**
		 * proceed method
		 */
		log.debug("invoke origin method");
		Object resp = point.proceed();
		/**
		 * save cache data
		 */
		try {
			if (!onCondition(cache.unless(), resp, methodParams)) {
				log.debug("try to save origin method value");
				int expireTime = expireTime(cache.expired());
				if (expireTime < 0) {
					getCacheOperator().set(cacheKeyName, resp);
				} else {
					getCacheOperator().setWithExpire(cacheKeyName, resp, expireTime);
				}
			}
		} catch (Exception e) {
			log.error("EasyCache Save Data ERROR", e);
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
		log.debug("Annotation CacheDel Method Start,Proceeding Join Point at " + className + "." + method.getName());
		/**
		 * generate cache key
		 */
		String cacheKeyName = getCacheKeyName(cacheDel.value(), className, method.getName(), methodParams);
		log.debug("generate Cache Key=" + cacheKeyName);
		/**
		 * proceed method
		 */
		Object resp = point.proceed();
		/**
		 * try to delete cache data
		 */
		try {
			if (!onCondition(cacheDel.unless(), resp, methodParams)) {
				log.debug("try to delete cache data");
				getCacheOperator().removeByKey(cacheKeyName);
			}
		} catch (Exception e) {
			log.error("EasyCache Delete Data ERROR", e);
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
		CachePut cachePut = getMethodFromProceedingJoinPoint(point).getAnnotation(CachePut.class);
		log.debug("Annotation CachePut Method Start,Proceeding Join Point at " + className + "." + method.getName());
		/**
		 * generate cache key
		 */
		String cacheKeyName = getCacheKeyName(cachePut.value(), className, method.getName(), methodParams);
		log.debug("generate Cache Key=" + cacheKeyName);
		/**
		 * proceed method
		 */
		Object resp = point.proceed();
		/**
		 * try to update cache data
		 */
		try {
			if (!onCondition(cachePut.unless(), resp, methodParams)) {
				log.debug("try to update cache data");
				Object cacheObject = getValueFromInvoke(cachePut.cache(), resp, methodParams);
				int expireTime = expireTime(cachePut.expired());
				if (expireTime < 0) {
					getCacheOperator().set(cacheKeyName, cacheObject);
				} else {
					getCacheOperator().setWithExpire(cacheKeyName, cacheObject, expireTime);
				}
			}
		} catch (Exception e) {
			log.error("EasyCache Update Data ERROR", e);
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

	private static Method getMethodFromProceedingJoinPoint(ProceedingJoinPoint point) {
		MethodSignature signature = (MethodSignature) point.getSignature();
		return signature.getMethod();
	}
}
