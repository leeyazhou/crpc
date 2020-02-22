/**
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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
package com.github.leeyazhou.crpc.console.controllers;

import javax.servlet.http.HttpServletRequest;

import com.github.leeyazhou.crpc.console.config.ConsoleConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author lee
 *
 */
@RequestMapping("/login/")
@Controller
public class LoginController {

  public static final String logginKey = "crpc-console-login";

  @RequestMapping(value = {"login", "login.html"}, method = RequestMethod.GET)
  public String login(String username, String passwd, Model model, HttpServletRequest request) {
    if (("root-" + ConsoleConfig.getPasswd()).equals(username + "-" + passwd)) {
      request.getSession().setAttribute(logginKey, username + "-" + passwd);
      return "/index.html";
    } else {
      request.getSession().invalidate();
      model.addAttribute("flag", false);
    }
    return "login";
  }

}
