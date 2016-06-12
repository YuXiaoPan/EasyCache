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
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;

import static com.google.common.base.Preconditions.checkNotNull;
import static li.allan.utils.Constants.EMPTY_ARRAY;
import static li.allan.utils.Constants.NO_DATA;

public class ThriftSerializer implements Serializer {

	private final StringSerializer stringSerializer;

	public ThriftSerializer() {
		stringSerializer = new StringSerializer();
	}

	@Override
	public byte[] serialize(Object source) throws SerializationException {
		if (source == null) {
			return EMPTY_ARRAY;
		}
		try {
			String tmp = new TSerializer(new TJSONProtocol.Factory()).toString((TBase) source, "UTF-8");
			return stringSerializer.serialize(tmp);
		} catch (TException e) {
			throw new SerializationException("Thrift Serialize to Json FAIL: " + e.getMessage(), e);
		}
	}

	@Override
	public Object deserialize(byte[] source, Class type) throws SerializationException {
		checkNotNull(type, "Deserialization type must not be null! Please provide Object.class to make use of Jackson2 default typing.");
		if (source == null) {
			return NO_DATA;
		}
		if (source.length == 0) {
			return null;
		}
		try {
			TBase obj = (TBase) type.newInstance();
			new TDeserializer(new TJSONProtocol.Factory()).deserialize(obj, stringSerializer.deserialize(source, String.class), "UTF-8");
			return obj;
		} catch (Exception e) {
			throw new SerializationException("Thrift Deserialize to Json FAIL: " + e.getMessage(), e);
		}
	}
}
