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

package li.allan.monitor;

import com.google.common.base.Splitter;
import li.allan.config.base.RedisConnectionConfig;

import java.util.Date;
import java.util.Map;

/**
 * @author LiALuN
 */
public class RedisInfo {
	/**
	 * 连接信息
	 */
	private RedisConnectionConfig redisConnectionConfig;
	/**
	 * 状态信息
	 */
	private long uptimeInSeconds;
	private int connectedClients;
	private long usedMemory;
	private long keys;
	private long expires;
	private long avgTTL;
	/**
	 * 可用性信息
	 */
	private Date statusLastUpdate;
	private boolean isAvailable;//是否可用

	private RedisInfo() {
	}

	public static RedisInfo fromRedisInfo(RedisConnectionConfig connConfig, int database, Map<String, String> redisInfo) {
		RedisInfo redisStatus = new RedisInfo();
		redisStatus.redisConnectionConfig = connConfig;
		redisStatus.uptimeInSeconds = redisInfo.containsKey("uptime_in_seconds") ?
				Long.valueOf(redisInfo.get("uptime_in_seconds")) : -1;
		redisStatus.connectedClients = redisInfo.containsKey("connected_clients") ?
				Integer.valueOf(redisInfo.get("connected_clients")) : -1;
		redisStatus.usedMemory = redisInfo.containsKey("used_memory") ?
				Long.valueOf(redisInfo.get("used_memory")) : -1;
		if (redisInfo.containsKey("db" + database)) {
			Map<String, String> dbInfo = Splitter.on(",").trimResults()
					.withKeyValueSeparator("=").split(redisInfo.get("db" + database));
			redisStatus.keys = dbInfo.containsKey("keys") ? Long.valueOf(dbInfo.get("keys")) : -1;
			redisStatus.expires = dbInfo.containsKey("expires") ? Long.valueOf(dbInfo.get("expires")) : -1;
			redisStatus.avgTTL = dbInfo.containsKey("avg_ttl") ? Long.valueOf(dbInfo.get("avg_ttl")) : -1;
		} else {
			redisStatus.keys = 0;
			redisStatus.expires = 0;
			redisStatus.avgTTL = -1;
		}
		redisStatus.statusLastUpdate = new Date();
		redisStatus.isAvailable = true;
		return redisStatus;
	}

	public static RedisInfo unAvailable(RedisConnectionConfig connConfig) {
		RedisInfo redisInfo = new RedisInfo();
		redisInfo.uptimeInSeconds = -1;
		redisInfo.connectedClients = -1;
		redisInfo.usedMemory = -1;
		redisInfo.keys = -1;
		redisInfo.expires = -1;
		redisInfo.avgTTL = -1;
		redisInfo.redisConnectionConfig = connConfig;
		redisInfo.statusLastUpdate = new Date();
		redisInfo.isAvailable = false;
		return redisInfo;
	}

	@Override
	public String toString() {
		return "RedisInfo{" +
				"redisConnectionConfig=" + redisConnectionConfig +
				", uptimeInSeconds=" + uptimeInSeconds +
				", connectedClients=" + connectedClients +
				", usedMemory=" + usedMemory +
				", keys=" + keys +
				", expires=" + expires +
				", avgTTL=" + avgTTL +
				", statusLastUpdate=" + statusLastUpdate +
				", isAvailable=" + isAvailable +
				'}';
	}

	public RedisConnectionConfig getRedisConnectionConfig() {
		return redisConnectionConfig;
	}

	public long getUptimeInSeconds() {
		return uptimeInSeconds;
	}

	public int getConnectedClients() {
		return connectedClients;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public long getKeys() {
		return keys;
	}

	public long getExpires() {
		return expires;
	}

	public long getAvgTTL() {
		return avgTTL;
	}

	public Date getStatusLastUpdate() {
		return statusLastUpdate;
	}

	public boolean isAvailable() {
		return isAvailable;
	}
}
