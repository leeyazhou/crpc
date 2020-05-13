/**
 * 
 */
package com.github.leeyazhou.crpc.config.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.github.leeyazhou.crpc.config.AbstractParser;
import com.github.leeyazhou.crpc.config.Configuration;
import com.github.leeyazhou.crpc.config.ProtocolConfig;

/**
 * @author leeyazhou
 *
 */
public class ProtocolConfigParser extends AbstractParser<ProtocolConfig> {

  /**
   * @param configuration
   */
  public ProtocolConfigParser(Configuration configuration) {
    super(configuration);
  }

  @Override
  public ProtocolConfig parse(Node node) {
    ProtocolConfig protocolConfig = new ProtocolConfig();
    parseProperties((Element) node, protocolConfig);
    return protocolConfig;
  }

}
