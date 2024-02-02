package io.undertow.server.handlers;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeParser;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.Headers;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedirectHandler implements HttpHandler {
   private final ExchangeAttribute attribute;

   public RedirectHandler(String location) {
      ExchangeAttributeParser parser = ExchangeAttributes.parser(this.getClass().getClassLoader());
      this.attribute = parser.parse(location);
   }

   public RedirectHandler(String location, ClassLoader classLoader) {
      ExchangeAttributeParser parser = ExchangeAttributes.parser(classLoader);
      this.attribute = parser.parse(location);
   }

   public RedirectHandler(ExchangeAttribute attribute) {
      this.attribute = attribute;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.setStatusCode(302);
      exchange.getResponseHeaders().put(Headers.LOCATION, this.attribute.readAttribute(exchange));
      exchange.endExchange();
   }

   public String toString() {
      return "redirect( '" + this.attribute.toString() + "' )";
   }

   private static class Wrapper implements HandlerWrapper {
      private final ExchangeAttribute value;

      private Wrapper(ExchangeAttribute value) {
         this.value = value;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new RedirectHandler(this.value);
      }

      // $FF: synthetic method
      Wrapper(ExchangeAttribute x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "redirect";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", ExchangeAttribute.class);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("value");
      }

      public String defaultParameter() {
         return "value";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((ExchangeAttribute)config.get("value"));
      }
   }
}
