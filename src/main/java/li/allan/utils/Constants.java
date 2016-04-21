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

package li.allan.utils;

/**
 * @author LiALuN
 */
public class Constants {
	public static final String KEY_SEPARATOR = "_";
	public static final int MIN_ORDER = 0;
	public static final int DEFAULT_CACHE_EXPIRE = -1;
	public static final int DEFAULT_MAP_CACHE_SIZE = 20000;
	public static final String RESULT = "result";
	public static final int DEFAULT_MONITOR_INTERVAL = 1000;
	public static final int DEFAULT_MONITOR_CONNECTION_TIMEOUT = 1000;
	public static final int DEFAULT_MONITOR_EXPIRE = 4000;

	public static final byte[] EMPTY_ARRAY = new byte[0];
	public static final Object NO_DATA = new NoData();

	public static class NoData {
	}
}
