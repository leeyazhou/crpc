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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.leeyazhou.crpc.console.model.ServiceModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.leeyazhou.crpc.console.inits.ZookeeperInit;

/**
 * @author leeyazhou
 */
@RequestMapping("/producer/")
@Controller
public class ProducerController  {

  @RequestMapping("list")
  public String list(Model model) {
    int size = ZookeeperInit.getServices().size();
    List<ServiceModel> list = new ArrayList<ServiceModel>(size);
    Iterator<ServiceModel> it = ZookeeperInit.getServices().values().iterator();
    while (it.hasNext()) {
      ServiceModel item = it.next();
      if (item.getProvider().size() > 0) {
        list.add(item);
      }
    }

    model.addAttribute("serviceProducers", list);
    return "/producer/list";
  }
}
