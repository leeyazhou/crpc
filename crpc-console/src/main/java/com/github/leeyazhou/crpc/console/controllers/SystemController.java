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

package com.github.leeyazhou.crpc.console.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.leeyazhou.crpc.console.config.ConsoleConfig;
import com.github.leeyazhou.crpc.console.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.leeyazhou.crpc.core.Constants;

/**
 * @author leeyazhou
 */
@RequestMapping("/sys/")
@Controller
public class SystemController {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SystemController.class);

  @RequestMapping("log")
  public String log(Model model) {
    logger.info("log");
    @SuppressWarnings("unchecked")
    Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
    List<Logger> loggersResult = new ArrayList<Logger>();
    while (loggers.hasMoreElements()) {
      loggersResult.add(loggers.nextElement());
    }
    model.addAttribute("loggers", loggersResult);
    model.addAttribute("rootLogger", LogManager.getRootLogger());
    return "/sys/log";
  }

  @RequestMapping(value = "log/{name}/{level}", method = RequestMethod.POST)
  @ResponseBody
  public String editLog(@PathVariable String level, @PathVariable String name) {
    String result = "失败!";

    if ("changeall".equals(name) && StringUtils.isNotBlank(level)) {
      LogManager.getRootLogger().setLevel(Level.toLevel(level));
      result = "所有日志更新为：" + level;
    } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(level)) {
      Logger logger = LogManager.exists(name);
      logger.setLevel(Level.toLevel(level));
      result = "成功!";
    }

    return "修改日志级别" + result + " level : " + level + ", name : " + name;
  }

  @RequestMapping("env")
  public String env(Model model) {
    logger.info("env");
    model.addAttribute("envs", System.getenv());
    return "/sys/env";
  }

  @RequestMapping("version")
  public String version(Model model) {
    logger.info("version");
    Map<String, Object> version = new HashMap<String, Object>();
    version.put("CRPC版本", Constants.CRPC_VERSION);
    version.put("CRPC编码", Constants.ENCODING);
    version.put("内存", getHeapMsg());
    version.put("操作系统", System.getenv("OS"));
    version.put("CPU", System.getenv("PROCESSOR_ARCHITECTURE") + " ; " + Runtime.getRuntime().availableProcessors() + " cores");
    version.put("运行时间", getSystemRunningTime());
    model.addAttribute("version", version);
    return "/sys/version";
  }

  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  private String getSystemRunningTime() {
    StringBuilder sb = new StringBuilder();
    long end = System.currentTimeMillis();
    long costTime = end - ConsoleConfig.getSystemStartTime();
    sb.append(DateUtil.parseTime(costTime));
    sb.append(" from ").append(dateFormat.format(new Date(ConsoleConfig.getSystemStartTime())));
    sb.append(" to ").append(dateFormat.format(new Date(end)));
    return sb.toString();
  }

  private String getHeapMsg() {
    StringBuilder sb = new StringBuilder();
    sb.append("max : ").append(getReadableMemory(Runtime.getRuntime().maxMemory()));
    sb.append("; total : ").append(getReadableMemory(Runtime.getRuntime().totalMemory()));
    sb.append("; free : ").append(getReadableMemory(Runtime.getRuntime().freeMemory()));
    return sb.toString();
  }

  private String getReadableMemory(long memory) {
    String result = null;
    if (memory > 1024) {
      memory = memory / 1024;// kb
      result = memory + "kb";
    }
    if (memory > 1024) {
      memory = memory / 1024;// mb
      result = memory + "mb";
    }
    if (memory > 1024) {
      memory = memory / 1024;// gb
      result = memory + "gb";
    }
    return result;
  }
}
