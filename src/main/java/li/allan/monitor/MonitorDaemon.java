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
import com.google.common.base.Strings;
import li.allan.config.base.CacheConfig;
import li.allan.config.base.ConfigBase;
import li.allan.config.base.RedisConfig;
import li.allan.config.base.RedisConnectionConfig;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import li.allan.observer.EasyCacheObservable;
import li.allan.observer.ObserverContainer;
import li.allan.observer.event.RedisInfoEvent;
import li.allan.utils.Constants;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author LiALuN
 */
public class MonitorDaemon extends EasyCacheObservable {
	private static Log log = LogFactory.getLog(MonitorDaemon.class);
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	private static Timer timer = new Timer();

	public static void start() {
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					monitor();
				} catch (InterruptedException e) {
					log.error("EasyCache Monitor Daemon ERROR", e);
				}
			}
		}, Constants.DEFAULT_MONITOR_INTERVAL, Constants.DEFAULT_MONITOR_INTERVAL);
	}

	public static void monitor() throws InterruptedException {
		CacheConfig mainCache = ConfigBase.getConfigProperties().getMainCacheConfig();
		if (mainCache instanceof RedisConfig) {
			redisMonitor((RedisConfig) mainCache);
		}
	}

	private static void redisMonitor(RedisConfig redisConfig) throws InterruptedException {
		List<Callable<Void>> solvers = new ArrayList<Callable<Void>>();
		for (final RedisConnectionConfig connConfig : redisConfig.getRedisConnectionConfigs()) {
			solvers.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					try {
						Map<String, String> redisInfo = getRedisInfo(connConfig);
						RedisStatus redisStatus = RedisStatus.fromRedisInfo(connConfig, connConfig.getDatabase(), redisInfo);
						ObserverContainer.sendEvent(new RedisInfoEvent(redisStatus));
					} catch (JedisException e) {
						ObserverContainer.sendEvent(new RedisInfoEvent(RedisStatus.unAvailable(connConfig)));
						log.info("Get redis info From Jedis FAIL,conn:" + connConfig, e);
					}
					return null;
				}
			});
		}
		List<Future<Void>> futures = threadPool.invokeAll(solvers, Constants.DEFAULT_MONITOR_EXPIRE * 2, TimeUnit.MILLISECONDS);
		for (Future future : futures) {
			try {
				future.get();
			} catch (Exception e) {
				log.error("EasyCache Redis Monitor ERROR", e);
			}
		}
	}

	private static Map<String, String> getRedisInfo(RedisConnectionConfig connConfig) {
		Jedis jedis = null;
		try {
			jedis = new Jedis(connConfig.getHost(), connConfig.getPort(), Constants.DEFAULT_MONITOR_EXPIRE);
			if (!Strings.isNullOrEmpty(connConfig.getPassword())) {
				jedis.auth(connConfig.getPassword());
			}
			String info = jedis.info().replaceAll("#.*\r?\n", "")//remove useless line，like line "# Server" or "# Stats"
					.replaceAll("(\r?\n){2,}", "$1").trim();//remove empty line
			return Splitter.onPattern("\r?\n").trimResults()
					.withKeyValueSeparator(":").split(info);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
