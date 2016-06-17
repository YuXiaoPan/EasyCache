/*
 *
 *  *  Copyright  2015-2016. the original author or authors.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package li.allan.serializer;

import li.allan.TestBase;
import li.allan.domain.ThriftBean;
import li.allan.domain.ThriftSubBean;
import li.allan.domain.User;

import java.util.*;

import static li.allan.Test_Constants.TEST_STRING;

public abstract class TestSerializerBase extends TestBase {


	public abstract void serializerJavaBean();

	public abstract void serializerThriftBean();

	public static ThriftBean getThriftBean() {
		ThriftBean thriftBean = new ThriftBean();
		thriftBean.setBoolType(true);
		thriftBean.setSignedByteType(Byte.MAX_VALUE);
		thriftBean.setSignedShortType(Short.MAX_VALUE);
		thriftBean.setSignedIntType(Integer.MAX_VALUE);
		thriftBean.setSignedLongType(Long.MAX_VALUE);
		thriftBean.setSignedDoubleType(Double.MAX_VALUE);
		thriftBean.setStringType(TEST_STRING);

		Map<String, String> map = new HashMap<String, String>();
		map.put(TEST_STRING, TEST_STRING);
		thriftBean.setMapType(map);

		Map<String, ThriftSubBean> mapTypeWithBean = new HashMap<String, ThriftSubBean>();
		mapTypeWithBean.put(TEST_STRING, getThriftSubBean());
		thriftBean.setMapTypeWithBean(mapTypeWithBean);

		List<String> list = new ArrayList<String>();
		list.add(TEST_STRING);
		thriftBean.setListType(list);

		List<ThriftSubBean> listTypeWithBean = new ArrayList<ThriftSubBean>();
		listTypeWithBean.add(getThriftSubBean());
		thriftBean.setListTypeWithBean(listTypeWithBean);

		Set<String> set = new HashSet<String>();
		set.add(TEST_STRING);
		thriftBean.setSetType(set);

		Set<ThriftSubBean> setTypeWithBean = new HashSet<ThriftSubBean>();
		listTypeWithBean.add(getThriftSubBean());
		thriftBean.setSetTypeWithBean(setTypeWithBean);

		return thriftBean;
	}

	private static ThriftSubBean getThriftSubBean() {
		ThriftSubBean thriftSubBean = new ThriftSubBean();
		thriftSubBean.setBoolType(true);
		thriftSubBean.setSignedByteType(Byte.MAX_VALUE);
		thriftSubBean.setSignedShortType(Short.MAX_VALUE);
		thriftSubBean.setSignedIntType(Integer.MAX_VALUE);
		thriftSubBean.setSignedLongType(Long.MAX_VALUE);
		thriftSubBean.setSignedDoubleType(Double.MAX_VALUE);
		thriftSubBean.setStringType(TEST_STRING);
		return thriftSubBean;
	}

	public User getJavaBean() {
		return getUser();
	}
}
