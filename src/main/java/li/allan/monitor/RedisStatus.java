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

import li.allan.config.base.RedisConnectionConfig;

import java.util.Date;

/**
 * @author LiALuN
 */
public class RedisStatus {
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
	private Date statusLastUpdate;
	/**
	 * 可用性信息
	 */
	private boolean isAvaliable;//是否可用
	private boolean avaliableLastUpdate;//上次状态更新时间

	public RedisConnectionConfig getRedisConnectionConfig() {
		return redisConnectionConfig;
	}

	public void setRedisConnectionConfig(RedisConnectionConfig redisConnectionConfig) {
		this.redisConnectionConfig = redisConnectionConfig;
	}

	public long getUptimeInSeconds() {
		return uptimeInSeconds;
	}

	public void setUptimeInSeconds(long uptimeInSeconds) {
		this.uptimeInSeconds = uptimeInSeconds;
	}

	public int getConnectedClients() {
		return connectedClients;
	}

	public void setConnectedClients(int connectedClients) {
		this.connectedClients = connectedClients;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public Date getStatusLastUpdate() {
		return statusLastUpdate;
	}

	public void setStatusLastUpdate(Date statusLastUpdate) {
		this.statusLastUpdate = statusLastUpdate;
	}

	public boolean isAvaliable() {
		return isAvaliable;
	}

	public void setAvaliable(boolean avaliable) {
		isAvaliable = avaliable;
	}

	public boolean isAvaliableLastUpdate() {
		return avaliableLastUpdate;
	}

	public void setAvaliableLastUpdate(boolean avaliableLastUpdate) {
		this.avaliableLastUpdate = avaliableLastUpdate;
	}
}
