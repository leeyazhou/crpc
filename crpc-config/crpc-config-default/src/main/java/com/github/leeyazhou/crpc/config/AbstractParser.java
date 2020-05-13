/**
 * 
 */
package com.github.leeyazhou.crpc.config;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.util.FieldUtil;

/**
 * @author leeyazhou
 *
 */
public abstract class AbstractParser<T> implements IParser<T> {
  static final Logger logger = LoggerFactory.getLogger(AbstractParser.class);
  protected Configuration configuration;

  public AbstractParser(Configuration configuration) {
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  protected void parseProperties(Element element, Object target) {
    NamedNodeMap nodeMap = element.getAttributes();
    for (int i = 0; i < nodeMap.getLength(); i++) {
      Node node = nodeMap.item(i);
      String name = node.getNodeName();
      String value = node.getNodeValue();
      FieldUtil.convertValue(name, value, target);
    }
  }

}
