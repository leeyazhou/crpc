/**
 * Copyright © 2016~2020 CRPC (coderhook@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.github.leeyazhou.crpc.config.parser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
