package io.undertow.predicate;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.server.HttpServerExchange;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContainsPredicate implements Predicate {
   private final ExchangeAttribute attribute;
   private final String[] values;

   ContainsPredicate(ExchangeAttribute attribute, String[] values) {
      this.attribute = attribute;
      this.values = new String[values.length];
      System.arraycopy(values, 0, this.values, 0, values.length);
   }

   public boolean resolve(HttpServerExchange value) {
      String attr = this.attribute.readAttribute(value);
      if (attr == null) {
         return false;
      } else {
         for(int i = 0; i < this.values.length; ++i) {
            if (attr.contains(this.values[i])) {
               return true;
            }
         }

         return false;
      }
   }

   public ExchangeAttribute getAttribute() {
      return this.attribute;
   }

   public String[] getValues() {
      String[] ret = new String[this.values.length];
      System.arraycopy(this.values, 0, ret, 0, this.values.length);
      return ret;
   }

   public String toString() {
      return "contains( search={" + String.join(", ", Arrays.asList(this.values)) + "}, value='" + this.attribute.toString() + "' )";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "contains";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", ExchangeAttribute.class);
         params.put("search", String[].class);
         return params;
      }

      public Set<String> requiredParameters() {
         Set<String> params = new HashSet();
         params.add("value");
         params.add("search");
         return params;
      }

      public String defaultParameter() {
         return null;
      }

      public Predicate build(Map<String, Object> config) {
         String[] search = (String[])((String[])config.get("search"));
         ExchangeAttribute values = (ExchangeAttribute)config.get("value");
         return new ContainsPredicate(values, search);
      }
   }
}
