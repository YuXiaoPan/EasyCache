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
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * @author LiALuN
 */
public class RedisConfig implements CacheConfig {
	private Set<RedisConnectionConfig> redisConnectionConfigs;
	private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

	public Set<RedisConnectionConfig> getRedisConnectionConfigs() {
		return redisConnectionConfigs;
	}

	public void setRedisConnectionConfigs(Set<RedisConnectionConfig> redisConnectionConfigs) {
		this.redisConnectionConfigs = redisConnectionConfigs;
	}

	public JedisPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		RedisConfig that = (RedisConfig) object;
		return Objects.equal(redisConnectionConfigs, that.redisConnectionConfigs) &&
				Objects.equal(jedisPoolConfig, that.jedisPoolConfig);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(redisConnectionConfigs, jedisPoolConfig);
	}

	@Override
	public String toString() {
		return "RedisConfig{" +
				"redisConnectionConfigs=" + redisConnectionConfigs +
				", jedisPoolConfig=" + jedisPoolConfig +
				'}';
	}
}
