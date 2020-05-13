/**
 * 
 */
package com.github.leeyazhou.crpc.config.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.github.leeyazhou.crpc.config.AbstractParser;
import com.github.leeyazhou.crpc.config.ApplicationConfig;
import com.github.leeyazhou.crpc.config.Configuration;

/**
 * @author leeyazhou
 *
 */
public class ApplicationConfigParser extends AbstractParser<ApplicationConfig> {

  public ApplicationConfigParser(Configuration configuration) {
    super(configuration);
  }

  @Override
  public ApplicationConfig parse(Node node) {
    ApplicationConfig applicationConfig = new ApplicationConfig();

    parseProperties((Element) node, applicationConfig);
    return applicationConfig;
  }

}
