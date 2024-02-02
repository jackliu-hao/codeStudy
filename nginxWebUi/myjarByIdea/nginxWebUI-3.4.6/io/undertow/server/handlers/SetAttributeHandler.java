package io.undertow.server.handlers;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeParser;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.attribute.NullAttribute;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ResponseCommitListener;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetAttributeHandler implements HttpHandler {
   private final HttpHandler next;
   private final ExchangeAttribute attribute;
   private final ExchangeAttribute value;
   private final boolean preCommit;

   public SetAttributeHandler(HttpHandler next, ExchangeAttribute attribute, ExchangeAttribute value) {
      this(next, attribute, value, false);
   }

   public SetAttributeHandler(HttpHandler next, String attribute, String value) {
      this.next = next;
      ExchangeAttributeParser parser = ExchangeAttributes.parser(this.getClass().getClassLoader());
      this.attribute = parser.parseSingleToken(attribute);
      this.value = parser.parse(value);
      this.preCommit = false;
   }

   public SetAttributeHandler(HttpHandler next, String attribute, String value, ClassLoader classLoader) {
      this.next = next;
      ExchangeAttributeParser parser = ExchangeAttributes.parser(classLoader);
      this.attribute = parser.parseSingleToken(attribute);
      this.value = parser.parse(value);
      this.preCommit = false;
   }

   public SetAttributeHandler(HttpHandler next, ExchangeAttribute attribute, ExchangeAttribute value, boolean preCommit) {
      this.next = next;
      this.attribute = attribute;
      this.value = value;
      this.preCommit = preCommit;
   }

   public SetAttributeHandler(HttpHandler next, String attribute, String value, boolean preCommit) {
      this.next = next;
      this.preCommit = preCommit;
      ExchangeAttributeParser parser = ExchangeAttributes.parser(this.getClass().getClassLoader());
      this.attribute = parser.parseSingleToken(attribute);
      this.value = parser.parse(value);
   }

   public SetAttributeHandler(HttpHandler next, String attribute, String value, ClassLoader classLoader, boolean preCommit) {
      this.next = next;
      this.preCommit = preCommit;
      ExchangeAttributeParser parser = ExchangeAttributes.parser(classLoader);
      this.attribute = parser.parseSingleToken(attribute);
      this.value = parser.parse(value);
   }

   public ExchangeAttribute getValue() {
      return this.value;
   }

   public String toString() {
      return "set( attribute='" + this.attribute.toString() + "', value='" + this.value.toString() + "' )";
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (this.preCommit) {
         exchange.addResponseCommitListener(new ResponseCommitListener() {
            public void beforeCommit(HttpServerExchange exchange) {
               try {
                  SetAttributeHandler.this.attribute.writeAttribute(exchange, SetAttributeHandler.this.value.readAttribute(exchange));
               } catch (ReadOnlyAttributeException var3) {
                  throw new RuntimeException(var3);
               }
            }
         });
      } else {
         this.attribute.writeAttribute(exchange, this.value.readAttribute(exchange));
      }

      this.next.handleRequest(exchange);
   }

   public static class ClearBuilder implements HandlerBuilder {
      public String name() {
         return "clear";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> parameters = new HashMap();
         parameters.put("attribute", ExchangeAttribute.class);
         parameters.put("pre-commit", Boolean.class);
         return parameters;
      }

      public Set<String> requiredParameters() {
         Set<String> req = new HashSet();
         req.add("attribute");
         return req;
      }

      public String defaultParameter() {
         return "attribute";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         final ExchangeAttribute attribute = (ExchangeAttribute)config.get("attribute");
         final Boolean preCommit = (Boolean)config.get("pre-commit");
         return new HandlerWrapper() {
            public HttpHandler wrap(HttpHandler handler) {
               return new SetAttributeHandler(handler, attribute, NullAttribute.INSTANCE, preCommit == null ? false : preCommit);
            }
         };
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "set";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> parameters = new HashMap();
         parameters.put("value", ExchangeAttribute.class);
         parameters.put("attribute", ExchangeAttribute.class);
         parameters.put("pre-commit", Boolean.class);
         return parameters;
      }

      public Set<String> requiredParameters() {
         Set<String> req = new HashSet();
         req.add("value");
         req.add("attribute");
         return req;
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         final ExchangeAttribute value = (ExchangeAttribute)config.get("value");
         final ExchangeAttribute attribute = (ExchangeAttribute)config.get("attribute");
         final Boolean preCommit = (Boolean)config.get("pre-commit");
         return new HandlerWrapper() {
            public HttpHandler wrap(HttpHandler handler) {
               return new SetAttributeHandler(handler, attribute, value, preCommit == null ? false : preCommit);
            }
         };
      }
   }
}
