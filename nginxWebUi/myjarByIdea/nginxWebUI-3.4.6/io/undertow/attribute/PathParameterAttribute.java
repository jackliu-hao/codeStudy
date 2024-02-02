package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class PathParameterAttribute implements ExchangeAttribute {
   private final String parameter;

   public PathParameterAttribute(String parameter) {
      this.parameter = parameter;
   }

   public String readAttribute(HttpServerExchange exchange) {
      Deque<String> res = (Deque)exchange.getPathParameters().get(this.parameter);
      if (res == null) {
         return null;
      } else if (res.isEmpty()) {
         return "";
      } else if (res.size() == 1) {
         return (String)res.getFirst();
      } else {
         StringBuilder sb = new StringBuilder("[");
         int i = 0;
         Iterator var5 = res.iterator();

         while(var5.hasNext()) {
            String s = (String)var5.next();
            sb.append(s);
            ++i;
            if (i != res.size()) {
               sb.append(", ");
            }
         }

         sb.append("]");
         return sb.toString();
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      ArrayDeque<String> value = new ArrayDeque();
      value.add(newValue);
      exchange.getPathParameters().put(this.parameter, value);
   }

   public String toString() {
      return "%{p," + this.parameter + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Path Parameter";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{p,") && token.endsWith("}")) {
            String qp = token.substring(4, token.length() - 1);
            return new PathParameterAttribute(qp);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
