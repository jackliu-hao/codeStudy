package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.SecureCookieCommitListener;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SecureCookieHandler implements HttpHandler {
   public static final HandlerWrapper WRAPPER = new HandlerWrapper() {
      public HttpHandler wrap(HttpHandler handler) {
         return new SecureCookieHandler(handler);
      }
   };
   private final HttpHandler next;

   public SecureCookieHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (exchange.isSecure()) {
         exchange.addResponseCommitListener(SecureCookieCommitListener.INSTANCE);
      }

      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "secure-cookie()";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "secure-cookie";
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
         return SecureCookieHandler.WRAPPER;
      }
   }
}
