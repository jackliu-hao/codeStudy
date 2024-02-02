package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.QueryParameterUtils;

public class RequestURLAttribute implements ExchangeAttribute {
   public static final String REQUEST_URL_SHORT = "%U";
   public static final String REQUEST_URL = "%{REQUEST_URL}";
   public static final ExchangeAttribute INSTANCE = new RequestURLAttribute();

   private RequestURLAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getRequestURI();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      int pos = newValue.indexOf(63);
      if (pos == -1) {
         exchange.setRelativePath(newValue);
         exchange.setRequestURI(newValue);
         exchange.setRequestPath(newValue);
         exchange.setResolvedPath("");
      } else {
         String path = newValue.substring(0, pos);
         exchange.setRelativePath(path);
         exchange.setRequestURI(path);
         exchange.setRequestPath(path);
         exchange.setResolvedPath("");
         String newQueryString = newValue.substring(pos);
         exchange.setQueryString(newQueryString);
         exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(newQueryString.substring(1), QueryParameterUtils.getQueryParamEncoding(exchange)));
      }

   }

   public String toString() {
      return "%{REQUEST_URL}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request URL";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REQUEST_URL}") && !token.equals("%U") ? null : RequestURLAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
