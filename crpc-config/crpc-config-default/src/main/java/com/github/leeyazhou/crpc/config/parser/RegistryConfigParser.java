/**
 * 
 */
package com.github.leeyazhou.crpc.config.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.github.leeyazhou.crpc.config.AbstractParser;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.RegistryConfig;

/**
 * @author leeyazhou
 *
 */
public class RegistryConfigParser extends AbstractParser<RegistryConfig> {

  /**
   * @param configuration
   */
  public RegistryConfigParser(Configuration configuration) {
    super(configuration);
  }

  @Override
  public RegistryConfig parse(Node node) {
    RegistryConfig registryConfig = new RegistryConfig();
    parseProperties((Element) node, registryConfig);
    return registryConfig;
  }

}
