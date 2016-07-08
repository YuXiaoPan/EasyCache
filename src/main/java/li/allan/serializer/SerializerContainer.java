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

package li.allan.serializer;

import li.allan.config.base.ConfigBase;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LiALuN
 */
public class SerializerContainer {
	private static final Map<Class, Serializer> container = new HashMap<Class, Serializer>();
	private static Log log = LogFactory.getLog(SerializerContainer.class);

	public static <T extends Serializer> T getSerializer(Class<T> serializerCls) {
		Class cls = serializerCls.equals(Serializer.class) ?
				ConfigBase.getConfigProperties().getValueSerializer() : serializerCls;
		if (!container.containsKey(cls)) {
			try {
				container.put(cls, (Serializer)cls.newInstance());
			} catch (InstantiationException e) {
				log.error("EasyCache can't create serializer: " + cls.getName(), e);
			} catch (IllegalAccessException e) {
				log.error("EasyCache can't create serializer: " + cls.getName(), e);
			}
		}
		return (T) container.get(cls);
	}

	public static void main(String[] args) {
//		System.out.println(getSerializer(StringSerializer.class));
//		System.out.println(getSerializer(StringSerializer.class));
//		System.out.println(getSerializer(StringSerializer.class));
//		System.out.println(getSerializer(StringSerializer.class));
//		System.out.println(getSerializer(StringSerializer.class));
		System.out.println(getSerializer(Serializer.class));
	}
}
