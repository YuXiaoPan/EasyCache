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

package li.allan.observer;

import li.allan.observer.event.base.ObserverEvent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author LiALuN
 */
public class ObserverContainer {

	private static Map<Class, Set<EasyCacheObserver>> obContainer = new HashMap<Class, Set<EasyCacheObserver>>();
	private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private ObserverContainer() {
	}

	public static void addObserver(EasyCacheObserver observer) {
		readWriteLock.writeLock().lock();
		try {
			Class eventClass = getObserverEventClass(observer);
			if (!obContainer.containsKey(eventClass)) {
				obContainer.put(eventClass, new HashSet<EasyCacheObserver>());
			}
			obContainer.get(eventClass).add(observer);
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public static void removeObserver(EasyCacheObserver observer) {
		readWriteLock.writeLock().lock();
		try {
			Class eventClass = getObserverEventClass(observer);
			if (obContainer.containsKey(eventClass)) {
				obContainer.get(eventClass).remove(observer);
			}
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public static void sendEvent(ObserverEvent event) {
		readWriteLock.readLock().lock();
		try {
			for (EasyCacheObserver observer : getRelatedObserver(event)) {
				observer.eventUpdate(event);
			}
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public static Collection<EasyCacheObserver> getRelatedObserver(ObserverEvent event) {
		readWriteLock.readLock().lock();
		try {
			Set<EasyCacheObserver> collection = new HashSet<EasyCacheObserver>();
			for (Class clazz : obContainer.keySet()) {
				if (clazz.isAssignableFrom(event.getClass())) {
					collection.addAll(obContainer.get(clazz));
				}
			}
			return collection;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}


	private static Class getObserverEventClass(EasyCacheObserver observer) {
		return getObserverEventClass(observer.getClass());
	}

	private static Class getObserverEventClass(Class clazz) {
		if (clazz == Object.class) {
			return null;
		}
		Type[] types = clazz.getGenericInterfaces();
		for (Type type : types) {
			if (type == EasyCacheObserver.class) {//未使用泛型
				return ObserverEvent.class;
			} else if (type instanceof ParameterizedType) {//使用泛型
				if (type.toString().matches(EasyCacheObserver.class.getName() + "<.*>")) {
					return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
				}
			}
		}
		for (Class clz : clazz.getInterfaces()) {
			Class cls = getObserverEventClass(clz);
			if (cls != null) {
				return cls;
			}
		}
		return getObserverEventClass(clazz.getSuperclass());
	}

//	private static Class getClass(Type type, int i) {
//		if (type instanceof ParameterizedType) { // 处理泛型类型
//			return getGenericClass((ParameterizedType) type, i);
//		} else if (type instanceof TypeVariable) {
//			return getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象
//		} else {// class本身也是type，强制转型
//			return (Class) type;
//		}
//	}
//
//	private static Class getGenericClass(ParameterizedType parameterizedType, int i) {
//		Object genericClass = parameterizedType.getActualTypeArguments()[i];
//		if (genericClass instanceof ParameterizedType) { // 处理多级泛型
//			return (Class) ((ParameterizedType) genericClass).getRawType();
//		} else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
//			return (Class) ((GenericArrayType) genericClass).getGenericComponentType();
//		} else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象
//			return getClass(((TypeVariable) genericClass).getBounds()[0], 0);
//		} else {
//			return (Class) genericClass;
//		}
//	}
}
