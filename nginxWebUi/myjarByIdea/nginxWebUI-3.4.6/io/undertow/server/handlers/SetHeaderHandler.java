package io.undertow.server.handlers;

import io.undertow.UndertowMessages;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.HttpString;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetHeaderHandler implements HttpHandler {
   private final HttpString header;
   private final ExchangeAttribute value;
   private final HttpHandler next;

   public SetHeaderHandler(String header, String value) {
      if (value == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("value");
      } else if (header == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("header");
      } else {
         this.next = ResponseCodeHandler.HANDLE_404;
         this.value = ExchangeAttributes.constant(value);
         this.header = new HttpString(header);
      }
   }

   public SetHeaderHandler(HttpHandler next, String header, ExchangeAttribute value) {
      if (value == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("value");
      } else if (header == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("header");
      } else if (next == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("next");
      } else {
         this.next = next;
         this.value = value;
         this.header = new HttpString(header);
      }
   }

   public SetHeaderHandler(HttpHandler next, String header, String value) {
      if (value == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("value");
      } else if (header == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("header");
      } else if (next == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("next");
      } else {
         this.next = next;
         this.value = ExchangeAttributes.constant(value);
         this.header = new HttpString(header);
      }
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.getResponseHeaders().put(this.header, this.value.readAttribute(exchange));
      this.next.handleRequest(exchange);
   }

   public ExchangeAttribute getValue() {
      return this.value;
   }

   public HttpString getHeader() {
      return this.header;
   }

   public String toString() {
      return "set( header='" + this.header.toString() + "', value='" + this.value.toString() + "' )";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "header";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> parameters = new HashMap();
         parameters.put("header", String.class);
         parameters.put("value", ExchangeAttribute.class);
         return parameters;
      }

      public Set<String> requiredParameters() {
         Set<String> req = new HashSet();
         req.add("value");
         req.add("header");
         return req;
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         final ExchangeAttribute value = (ExchangeAttribute)config.get("value");
         final String header = (String)config.get("header");
         return new HandlerWrapper() {
            public HttpHandler wrap(HttpHandler handler) {
               return new SetHeaderHandler(handler, header, value);
            }
         };
      }
   }
}
