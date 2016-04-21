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

import li.allan.exception.SerializationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;

import static li.allan.utils.Constants.EMPTY_ARRAY;
import static li.allan.utils.Constants.NO_DATA;

public class JdkSerializer implements Serializer {

	private Converter<Object, byte[]> serializer = new SerializingConverter();
	private Converter<byte[], Object> deserializer = new DeserializingConverter();

	public static void main(String[] args) {
		JdkSerializer s = new JdkSerializer();
		byte[] b = s.serialize(null);
		System.out.println(b);
		Object o = s.deserialize(b, Object.class);
		System.out.println(o);
	}

	@Override
	public Object deserialize(byte[] source, Class type) {
		if (source == null) {
			return NO_DATA;
		}
		if (source.length == 0) {
			return null;
		}
		try {
			return deserializer.convert(source);
		} catch (Exception ex) {
			throw new SerializationException("Cannot deserialize", ex);
		}
	}

	public byte[] serialize(Object object) {
		if (object == null) {
			return EMPTY_ARRAY;
		}
		try {
			return serializer.convert(object);
		} catch (Exception ex) {
			throw new SerializationException("Cannot serialize", ex);
		}
	}
}
