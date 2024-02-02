package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.QueryParameterUtils;

public class RequestPathAttribute implements ExchangeAttribute {
   public static final String REQUEST_PATH = "%{REQUEST_PATH}";
   public static final ExchangeAttribute INSTANCE = new RequestPathAttribute();

   private RequestPathAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getRelativePath();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      int pos = newValue.indexOf(63);
      exchange.setResolvedPath("");
      if (pos == -1) {
         exchange.setRelativePath(newValue);
         exchange.setRequestURI(newValue);
         exchange.setRequestPath(newValue);
      } else {
         String path = newValue.substring(0, pos);
         exchange.setRequestPath(path);
         exchange.setRelativePath(path);
         exchange.setRequestURI(newValue);
         String newQueryString = newValue.substring(pos);
         exchange.setQueryString(newQueryString);
         exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(newQueryString.substring(1), QueryParameterUtils.getQueryParamEncoding(exchange)));
      }

   }

   public String toString() {
      return "%{REQUEST_PATH}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request Path";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{REQUEST_PATH}") ? RequestPathAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
