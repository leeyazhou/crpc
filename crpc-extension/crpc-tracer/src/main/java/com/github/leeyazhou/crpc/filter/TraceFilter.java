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
package com.github.leeyazhou.crpc.filter;

import java.util.HashMap;
import java.util.Map;

import com.github.leeyazhou.crpc.protocol.Response;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.filter.AbstractFilter;
import com.github.leeyazhou.crpc.core.annotation.CRPCFilterType;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.logger.Logger;
import com.github.leeyazhou.crpc.core.logger.LoggerFactory;
import com.github.leeyazhou.crpc.core.object.SideType;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author lee
 *
 */
@CRPCFilterType(active = { SideType.SIDE_PROVIDER, SideType.SIDE_CONSUMER })
public class TraceFilter extends AbstractFilter {
  private static final String endpoint = "http://zipkin.iscoder.cn/api/v2/spans";
  private static final Tracer tracer = BraveTracer
      .create(Tracing.newBuilder().spanReporter(AsyncReporter.create(OkHttpSender.create(endpoint)))
          .localServiceName("crpc-demo-provider").build());
  private static final Logger logger = LoggerFactory.getLogger(TraceFilter.class);

  @Override
  public Response handle(RpcContext context) {
    logger.info("start .分布式调用链追踪");
    Span span = tracer.buildSpan("crpc-demo-provider").start();
    span.log("开始");
    span.setTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);
    try (Scope scope = tracer.scopeManager().activate(span, false)) {
      span.setOperationName("处理数据");
      return nextFilter(context);
    } catch (Exception ex) {
      Tags.ERROR.set(span, true);
      Map<String, Object> map = new HashMap<>();
      map.put(Fields.EVENT, "error");
      map.put(Fields.ERROR_OBJECT, ex);
      map.put(Fields.MESSAGE, ex.getMessage());
      span.log(map);
      throw new CrpcException(ex);
    } finally {
      span.log("结束");
      span.finish();
      logger.info("end .分布式调用链追踪");
    }

  }

}
