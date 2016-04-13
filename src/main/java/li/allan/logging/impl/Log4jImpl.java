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

package li.allan.logging.impl;

import li.allan.logging.Log;
import org.apache.log4j.Logger;

public class Log4jImpl implements Log {

	private Logger log;

	public Log4jImpl(String clazz) {
		log = Logger.getLogger(clazz);
	}

	public void debug(String s) {
		log.debug(s);
	}

	@Override
	public void debug(String s, Throwable e) {
		log.debug(s, e);
	}

	@Override
	public void info(String s) {
		log.info(s);
	}

	@Override
	public void info(String s, Throwable e) {
		log.info(s, e);
	}

	@Override
	public void warn(String s) {
		log.warn(s);
	}

	@Override
	public void warn(String s, Throwable e) {
		log.warn(s, e);
	}

	@Override
	public void error(String s) {
		log.error(s);
	}

	@Override
	public void error(String s, Throwable e) {
		log.error(s, e);
	}
}
