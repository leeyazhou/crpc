/**
 * Copyright © 2019 leeyazhou (coderhook@gmail.com)
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
package com.github.leeyazhou.crpc.filter;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.github.leeyazhou.crpc.protocol.Request;
import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.filter.AbstractFilter;

/**
 * @author lee
 *
 */
public class SentinelFilter extends AbstractFilter {
  private static final String DOT = ".";
  private static final String sepator = "_";

  public SentinelFilter() {
    SentinelConfig.setConfig(TransportConfig.CONSOLE_SERVER, "127.0.0.1:8080");
    SentinelConfig.setConfig(TransportConfig.SERVER_PORT, "8719");
    initFlowRules();
  }

  @Override
  public Response handle(RpcContext context) {
    Entry entry = null;
    try {
      final Request request = context.getRequest();

      StringBuilder resource = new StringBuilder();
      resource.append(request.getTargetClassName());
      resource.append(DOT).append(request.getMethodName());
      if (request.getArgTypes() != null) {
        for (String arg : request.getArgTypes()) {
          resource.append(sepator).append(arg.substring(arg.lastIndexOf(DOT) + 1, arg.length()));
        }
      }
      logger.info("资源名称: " + resource.toString());

      entry = SphU.entry(resource.toString());
      // entry = SphU.entry("HelloWorld");
      return nextFilter(context);
    } catch (BlockException e1) {
      logger.error("", e1);
    } finally {
      if (entry != null) {
        entry.exit();
      }
    }
    return null;
  }

  private void initFlowRules() {
    List<FlowRule> rules = new ArrayList<FlowRule>();
    FlowRule rule = new FlowRule();
    rule.setResource("HelloWorld");
    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
    rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
    // Set limit QPS to 20.
    rule.setCount(2);
    rules.add(rule);
    FlowRuleManager.loadRules(rules);
  }
}
