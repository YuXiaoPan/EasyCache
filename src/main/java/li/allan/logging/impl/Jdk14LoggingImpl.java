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

import java.util.logging.Level;
import java.util.logging.Logger;

public class Jdk14LoggingImpl implements Log {

	private Logger log;

	public Jdk14LoggingImpl(String clazz) {
		log = Logger.getLogger(clazz);
	}

	@Override
	public void debug(String s) {
		log.log(Level.FINE, s);
	}

	@Override
	public void debug(String s, Throwable e) {
		log.log(Level.FINE, s, e);
	}

	@Override
	public void info(String s) {
		log.log(Level.INFO, s);
	}

	@Override
	public void info(String s, Throwable e) {
		log.log(Level.INFO, s, e);
	}

	@Override
	public void warn(String s) {
		log.log(Level.WARNING, s);
	}

	@Override
	public void warn(String s, Throwable e) {
		log.log(Level.WARNING, s, e);
	}

	@Override
	public void error(String s) {
		log.log(Level.SEVERE, s);
	}

	@Override
	public void error(String s, Throwable e) {
		log.log(Level.SEVERE, s, e);
	}
}
