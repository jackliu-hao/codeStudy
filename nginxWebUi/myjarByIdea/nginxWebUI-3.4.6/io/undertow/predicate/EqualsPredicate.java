package io.undertow.predicate;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.server.HttpServerExchange;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EqualsPredicate implements Predicate {
   private final ExchangeAttribute[] attributes;

   EqualsPredicate(ExchangeAttribute[] attributes) {
      this.attributes = attributes;
   }

   public boolean resolve(HttpServerExchange value) {
      if (this.attributes.length < 2) {
         return true;
      } else {
         String first = this.attributes[0].readAttribute(value);

         for(int i = 1; i < this.attributes.length; ++i) {
            String current = this.attributes[i].readAttribute(value);
            if (first == null) {
               if (current != null) {
                  return false;
               }
            } else {
               if (current == null) {
                  return false;
               }

               if (!first.equals(current)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public String toString() {
      return "equals( {" + (String)Arrays.asList(this.attributes).stream().map((a) -> {
         return a.toString();
      }).collect(Collectors.joining(", ")) + "} )";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "equals";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", ExchangeAttribute[].class);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("value");
      }

      public String defaultParameter() {
         return "value";
      }

      public Predicate build(Map<String, Object> config) {
         ExchangeAttribute[] value = (ExchangeAttribute[])((ExchangeAttribute[])config.get("value"));
         return new EqualsPredicate(value);
      }
   }
}
