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

package li.allan.config.base;

import com.google.common.base.Objects;
import redis.clients.jedis.Protocol;

/**
 * Redis连接信息
 *
 * @author LiALuN
 */
public class RedisConnectionConfig {
	private String host;
	private int port;
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	private int database = Protocol.DEFAULT_DATABASE;
	private String password;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "RedisConnectionConfig{" +
				"host='" + host + '\'' +
				", port=" + port +
				", timeout=" + timeout +
				", database=" + database +
				", password='" + password + '\'' +
				'}';
	}

	/**
	 * 只要host和post相同，则当成相同对象
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RedisConnectionConfig that = (RedisConnectionConfig) o;
		return port == that.port &&
				Objects.equal(host, that.host);
	}

	/**
	 * 只要host和post相同，则当成相同对象
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(host, port);
	}
}

