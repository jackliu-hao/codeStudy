package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.QueryParameterUtils;

public class RelativePathAttribute implements ExchangeAttribute {
   public static final String RELATIVE_PATH_SHORT = "%R";
   public static final String RELATIVE_PATH = "%{RELATIVE_PATH}";
   public static final ExchangeAttribute INSTANCE = new RelativePathAttribute();

   private RelativePathAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getRelativePath();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      int pos = newValue.indexOf(63);
      String requestURI;
      if (pos == -1) {
         exchange.setRelativePath(newValue);
         requestURI = exchange.getResolvedPath() + newValue;
         if (requestURI.contains("%")) {
            exchange.setRequestURI(requestURI.replaceAll("%", "%25"));
         } else {
            exchange.setRequestURI(requestURI);
         }

         exchange.setRequestPath(requestURI);
      } else {
         requestURI = newValue.substring(0, pos);
         exchange.setRelativePath(requestURI);
         String requestURI = exchange.getResolvedPath() + requestURI;
         if (requestURI.contains("%")) {
            exchange.setRequestURI(requestURI.replaceAll("%", "%25"));
         } else {
            exchange.setRequestURI(requestURI);
         }

         exchange.setRequestPath(requestURI);
         String newQueryString = newValue.substring(pos);
         exchange.setQueryString(newQueryString);
         exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(newQueryString.substring(1), QueryParameterUtils.getQueryParamEncoding(exchange)));
      }

   }

   public String toString() {
      return "%{RELATIVE_PATH}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Relative Path";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{RELATIVE_PATH}") && !token.equals("%R") ? null : RelativePathAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
