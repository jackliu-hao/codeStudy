package io.undertow.server.handlers.builder;

import io.undertow.UndertowLogger;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.SetAttributeHandler;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RewriteHandlerBuilder implements HandlerBuilder {
   public String name() {
      return "rewrite";
   }

   public Map<String, Class<?>> parameters() {
      return Collections.singletonMap("value", ExchangeAttribute.class);
   }

   public Set<String> requiredParameters() {
      return Collections.singleton("value");
   }

   public String defaultParameter() {
      return "value";
   }

   public HandlerWrapper build(Map<String, Object> config) {
      final ExchangeAttribute value = (ExchangeAttribute)config.get("value");
      return new HandlerWrapper() {
         public HttpHandler wrap(HttpHandler handler) {
            return new SetAttributeHandler(handler, ExchangeAttributes.relativePath(), value) {
               public void handleRequest(HttpServerExchange exchange) throws Exception {
                  UndertowLogger.PREDICATE_LOGGER.debugf("Request rewritten to [%s] for %s.", this.getValue().readAttribute(exchange), exchange);
                  super.handleRequest(exchange);
               }

               public String toString() {
                  return "rewrite( '" + this.getValue().toString() + "' )";
               }
            };
         }
      };
   }
}
