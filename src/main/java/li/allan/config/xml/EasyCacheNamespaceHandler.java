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

package li.allan.config.xml;

import com.google.common.base.Strings;
import li.allan.aspect.EasyCacheAspect;
import li.allan.serializer.Serializer;
import li.allan.config.EasyCacheConfig;
import li.allan.config.base.ExpireMapConfig;
import li.allan.config.base.RedisConfig;
import li.allan.config.base.RedisConnectionConfig;
import li.allan.logging.Log;
import li.allan.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiALuN
 */
public class EasyCacheNamespaceHandler extends NamespaceHandlerSupport {
	private static Log log = LogFactory.getLog(EasyCacheNamespaceHandler.class);

	@Override
	public void init() {
		this.registerBeanDefinitionParser("annotation_cache", new AnnotationCacheParser());
		this.registerBeanDefinitionParser("config", new EasyCacheBeanDefinitionParser());
	}

	private static class AnnotationCacheParser extends AbstractBeanDefinitionParser {

		@Override
		protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
			return "easyCacheAspect";
		}

		@Override
		protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(EasyCacheAspect.class);
			return factory.getBeanDefinition();
		}
	}

	private static class EasyCacheBeanDefinitionParser extends AbstractBeanDefinitionParser {

		@Override
		protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
			return "easyCacheConfig";
		}

		@Override
		protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
			return parseParentElement(element);
		}

		private static AbstractBeanDefinition parseParentElement(Element element) {
			loadClass();
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(EasyCacheConfig.class);
			//redis
			parseRedis(element, factory);
			//backup
			parseBackup(element, factory);
			//serializer
			parseSerializer(element, factory);
			//others
			parseOthers(element, factory);
			return factory.getBeanDefinition();
		}

		private static void loadClass() {
			try {
				Class.forName("li.allan.cache.operator.CacheOperator");
			} catch (Exception e) {
				log.error("loadClass ERROR", e);
			}
		}

		private static void parseRedis(Element element, BeanDefinitionBuilder factory) {
			List<Element> redisConfigElements = DomUtils.getChildElementsByTagName(element, "redis");
			if (redisConfigElements.size() == 0) {
				return;
			}
			Element redisConfig = redisConfigElements.get(0);
			BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(RedisConfig.class);
			//connections
			List<RedisConnectionConfig> connectionConfigs = new ArrayList<RedisConnectionConfig>();
			Element connections = DomUtils.getChildElementsByTagName(redisConfig, "connections").get(0);
			for (Element connection : DomUtils.getChildElementsByTagName(connections, "connection")) {
				RedisConnectionConfig connectionConfig = new RedisConnectionConfig();
				connectionConfig.setHost(connection.getAttribute("host"));
				connectionConfig.setPort(Integer.valueOf(connection.getAttribute("port")));
				if (connection.hasAttribute("timeout")) {
					connectionConfig.setTimeout(Integer.valueOf(connection.getAttribute("timeout")));
				}
				if (connection.hasAttribute("database")) {
					connectionConfig.setDatabase(Integer.valueOf(connection.getAttribute("database")));
				}
				if (connection.hasAttribute("password")) {
					connectionConfig.setPassword(connection.getAttribute("password"));
				}
				connectionConfigs.add(connectionConfig);
			}
			component.addPropertyValue("RedisConnectionConfigs", connectionConfigs);
			//pool
			String poolConfigRef = DomUtils.getChildElementsByTagName(redisConfig, "pool").get(0).getAttribute("ref");
			component.addPropertyReference("jedisPoolConfig", poolConfigRef);
			factory.addPropertyValue("mainCacheConfig", component.getBeanDefinition());
		}

		private static void parseBackup(Element element, BeanDefinitionBuilder factory) {
			List<Element> backupElements = DomUtils.getChildElementsByTagName(element, "backup");
			if (backupElements.size() == 0) {
				return;
			}
			BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(ExpireMapConfig.class);
			if (!Strings.isNullOrEmpty(backupElements.get(0).getAttribute("size"))) {
				component.addPropertyValue("maxSize", backupElements.get(0).getAttribute("size"));
			}
			factory.addPropertyValue("backupCacheConfig", component.getBeanDefinition());
		}

		private static void parseSerializer(Element element, BeanDefinitionBuilder factory) {
			//keySerializer
			List<Element> keySerializerElements = DomUtils.getChildElementsByTagName(element, "keySerializer");
			if (keySerializerElements.size() == 0) {
				return;
			}
			if (!Strings.isNullOrEmpty(keySerializerElements.get(0).getAttribute("class"))) {
				try {
					Class cls = Class.forName(keySerializerElements.get(0).getAttribute("class"));
					Serializer serializer = (Serializer) cls.newInstance();
					factory.addPropertyValue("keySerializer", serializer);
				} catch (Exception e) {
					log.error("EasyCache config keySerializer Illegal", e);
				}
			}
			//valueSerializer
			List<Element> valueSerializerElements = DomUtils.getChildElementsByTagName(element, "valueSerializer");
			if (valueSerializerElements.size() == 0) {
				return;
			}
			if (!Strings.isNullOrEmpty(valueSerializerElements.get(0).getAttribute("class"))) {
				try {
					Class cls = Class.forName(valueSerializerElements.get(0).getAttribute("class"));
					Serializer serializer = (Serializer) cls.newInstance();
					factory.addPropertyValue("valueSerializer", serializer);
				} catch (Exception e) {
					log.error("EasyCache config keySerializer Illegal", e);
				}
			}
		}

		private static void parseOthers(Element element, BeanDefinitionBuilder factory) {
			//cacheExpire
			List<Element> cacheExpireElements = DomUtils.getChildElementsByTagName(element, "defaultCacheExpire");
			if (cacheExpireElements.size() == 0) {
				return;
			}
			if (!Strings.isNullOrEmpty(cacheExpireElements.get(0).getAttribute("seconds"))) {
				factory.addPropertyValue("defaultCacheExpire", cacheExpireElements.get(0).getAttribute("seconds"));
			}
		}
	}
}
