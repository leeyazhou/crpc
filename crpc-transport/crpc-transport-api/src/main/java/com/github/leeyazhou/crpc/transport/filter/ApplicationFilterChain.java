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
///**
// * 
// */
//
//package com.github.crpc.transport.filter;
//
//import com.github.crpc.protocol.Response;
//import com.github.leeyazhou.crpc.transport.Filter;
//import com.github.crpc.transport.FilterChain;
//import com.github.crpc.transport.RpcContext;
//
///**
// * @author lee
// */
//public class ApplicationFilterChain implements FilterChain {
//
//  private Filter next;
//
//  public ApplicationFilterChain(Filter next) {
//    this.next = next;
//  }
//
//  public ApplicationFilterChain() {}
//
//  @Override
//  public Response filter(RpcContext context) {
//    if (next != null) {
//      return next.handle(context);
//    }
//    return null;
//  }
//
//  /**
//   * @return the next
//   */
//  public Filter getNext() {
//    return next;
//  }
//
//  /**
//   * @param next the next to set
//   */
//  public void setNext(Filter next) {
//    this.next = next;
//  }
//
//}
