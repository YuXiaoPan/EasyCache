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

import li.allan.domain.ThriftBean;
import li.allan.domain.User;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ThriftJsonSerializerTest extends TestSerializerBase {
	ThriftJsonSerializer serializer = new ThriftJsonSerializer();

	@Test(expectedExceptions = ClassCastException.class)
	public void serializerJavaBean() {
		User user = getJavaBean();
		byte[] bytes = serializer.serialize(user);
		User tmp = (User) serializer.deserialize(bytes, User.class);
		Assert.assertEquals(tmp, user);
	}

	@Test
	public void serializerThriftBean() {
		ThriftBean thriftBean = getThriftBean();
		byte[] bytes = serializer.serialize(thriftBean);
		ThriftBean tmp = (ThriftBean) serializer.deserialize(bytes, ThriftBean.class);
		Assert.assertEquals(tmp, thriftBean);
	}
}
