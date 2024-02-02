package io.undertow.predicate;

import io.undertow.UndertowLogger;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PathTemplatePredicate implements Predicate {
   private final ExchangeAttribute attribute;
   private final String template;
   private final PathTemplate value;
   private static final boolean traceEnabled;

   public PathTemplatePredicate(String template, ExchangeAttribute attribute) {
      this.attribute = attribute;
      this.template = template;
      this.value = PathTemplate.create(template);
   }

   public boolean resolve(HttpServerExchange exchange) {
      Map<String, String> params = new HashMap();
      String path = this.attribute.readAttribute(exchange);
      if (path == null) {
         return false;
      } else {
         boolean result = this.value.matches(path, params);
         if (traceEnabled) {
            UndertowLogger.PREDICATE_LOGGER.tracef("Path template [%s] %s input [%s] for %s.", new Object[]{this.template, result ? "MATCHES" : "DOES NOT MATCH", path, exchange});
         }

         if (result) {
            Map<String, Object> context = (Map)exchange.getAttachment(PREDICATE_CONTEXT);
            if (context == null) {
               exchange.putAttachment(PREDICATE_CONTEXT, context = new TreeMap());
            }

            if (traceEnabled) {
               params.entrySet().forEach((param) -> {
                  UndertowLogger.PREDICATE_LOGGER.tracef("Storing template match [%s=%s] for %s.", param.getKey(), param.getValue(), exchange);
               });
            }

            ((Map)context).putAll(params);
         }

         return result;
      }
   }

   public String toString() {
      return this.attribute == ExchangeAttributes.relativePath() ? "path-template( '" + this.template + "' )" : "path-template( value='" + this.template + "', match='" + this.attribute.toString() + "' )";
   }

   static {
      traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "path-template";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", String.class);
         params.put("match", ExchangeAttribute.class);
         return params;
      }

      public Set<String> requiredParameters() {
         Set<String> params = new HashSet();
         params.add("value");
         return params;
      }

      public String defaultParameter() {
         return "value";
      }

      public Predicate build(Map<String, Object> config) {
         ExchangeAttribute match = (ExchangeAttribute)config.get("match");
         if (match == null) {
            match = ExchangeAttributes.relativePath();
         }

         String value = (String)config.get("value");
         return new PathTemplatePredicate(value, match);
      }
   }
}
