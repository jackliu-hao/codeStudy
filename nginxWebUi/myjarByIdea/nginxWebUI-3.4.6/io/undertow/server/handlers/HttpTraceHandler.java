package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpTraceHandler implements HttpHandler {
   private final HttpHandler handler;

   public HttpTraceHandler(HttpHandler handler) {
      this.handler = handler;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (exchange.getRequestMethod().equals(Methods.TRACE)) {
         exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "message/http");
         StringBuilder body = new StringBuilder("TRACE ");
         body.append(exchange.getRequestURI());
         if (!exchange.getQueryString().isEmpty()) {
            body.append('?');
            body.append(exchange.getQueryString());
         }

         body.append(' ');
         body.append(exchange.getProtocol().toString());
         body.append("\r\n");
         Iterator var3 = exchange.getRequestHeaders().iterator();

         while(var3.hasNext()) {
            HeaderValues header = (HeaderValues)var3.next();
            Iterator var5 = header.iterator();

            while(var5.hasNext()) {
               String value = (String)var5.next();
               body.append(header.getHeaderName());
               body.append(": ");
               body.append(value);
               body.append("\r\n");
            }
         }

         body.append("\r\n");
         exchange.getResponseSender().send(body.toString());
      } else {
         this.handler.handleRequest(exchange);
      }

   }

   public String toString() {
      return "trace()";
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new HttpTraceHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "trace";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper();
      }
   }
}
