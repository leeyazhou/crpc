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
package com.github.leeyazhou.crpc.showcase.filter;

import java.util.HashMap;
import java.util.Map;
import com.github.leeyazhou.crpc.transport.RpcContext;
import com.github.leeyazhou.crpc.transport.filter.AbstractFilter;
import com.github.leeyazhou.crpc.core.annotation.CRPCFilterType;
import com.github.leeyazhou.crpc.core.exception.CrpcException;
import com.github.leeyazhou.crpc.core.object.SideType;
import com.github.leeyazhou.crpc.protocol.message.Header;
import com.github.leeyazhou.crpc.protocol.message.RequestMessage;
import com.github.leeyazhou.crpc.protocol.message.ResponseMessage;
import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.log.Fields;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.tag.Tags;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author lee
 *
 */
@CRPCFilterType(active = {SideType.SIDE_PROVIDER, SideType.SIDE_CONSUMER})
public class TraceFilter extends AbstractFilter {
  private static final String endpoint = "http://zipkin.iscoder.cn/api/v2/spans";
  private static final Tracer tracer =
      BraveTracer.create(Tracing.newBuilder().spanReporter(AsyncReporter.create(OkHttpSender.create(endpoint)))
          // .spanReporter(Reporter.CONSOLE)
          .localServiceName("crpc-demo-provider").build());

  @Override
  public ResponseMessage handle(RpcContext context) {
    logger.info("分布式调用链追踪start");
    final RequestMessage request = context.getRequest();
    String spanName = request.getTargetClassName() + "." + request.getMethodName();
    logger.info("spanName : " + spanName);
    SpanBuilder spanBuilder = tracer.buildSpan(spanName);
    Map<String, String> headerMap = getMap(request.getHeaders());
    String log = "客户端";
    SpanContext spanContext = null;
    if (headerMap != null) {
      spanContext = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(headerMap));
      if (spanContext != null) {
        logger.info("" + spanContext);
        spanBuilder.asChildOf(spanContext);
        log = "服务端";
      }
    }


    Span span = spanBuilder.start();

    span.log("CRPC Trace start ...");
    span.setTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);
    Scope scope = tracer.scopeManager().activate(span, false);
    try {
      span.setOperationName(log);
      if (spanContext == null) {
        if (headerMap == null) {
          headerMap = new HashMap<String, String>();
        }
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMapInjectAdapter(headerMap));
        addHeader(request, headerMap);
      }
      return nextFilter(context);
    } catch (Exception ex) {
      Tags.ERROR.set(span, true);
      Map<String, Object> map = new HashMap<String, Object>();
      map.put(Fields.EVENT, "error");
      map.put(Fields.ERROR_OBJECT, ex);
      map.put(Fields.MESSAGE, ex.getMessage());
      span.log(map);
      throw new CrpcException(ex);
    } finally {
      span.log("CRPC Trace end.");
      scope.close();
      span.finish();
      logger.info("分布式调用链追踪end");
    }
  }

  private Map<String, String> getMap(Header[] headers) {
    Map<String, String> map = null;
    if (headers != null) {
      map = new HashMap<String, String>();
      for (Header header : headers) {
        map.put(header.getKey(), (String) header.getValue());
        logger.info("解压请求头：" + header);
      }
    }
    return map;
  }

  private void addHeader(RequestMessage request, Map<String, String> map) {
    Header[] requestHeaders = new Header[map.size()];
    int i = 0;
    for (Map.Entry<String, String> entry : map.entrySet()) {
      requestHeaders[i++] = new Header(entry.getKey(), entry.getValue());
      logger.info("注入请求头:" + entry.toString());
    }
    request.setHeaders(requestHeaders);
  }
}
