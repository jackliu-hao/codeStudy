package io.undertow.attribute;

import io.undertow.UndertowLogger;
import io.undertow.conduits.StoredResponseStreamSinkConduit;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class StoredResponse implements ExchangeAttribute {
   public static final ExchangeAttribute INSTANCE = new StoredResponse();

   private StoredResponse() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      byte[] data = (byte[])exchange.getAttachment(StoredResponseStreamSinkConduit.RESPONSE);
      if (data == null) {
         return null;
      } else {
         String charset = this.extractCharset(exchange.getResponseHeaders());
         if (charset == null) {
            return null;
         } else {
            try {
               return new String(data, charset);
            } catch (UnsupportedEncodingException var5) {
               UndertowLogger.ROOT_LOGGER.debugf(var5, "Could not decode response body using charset %s", charset);
               return null;
            }
         }
      }
   }

   private String extractCharset(HeaderMap headers) {
      String contentType = headers.getFirst(Headers.CONTENT_TYPE);
      if (contentType != null) {
         String value = Headers.extractQuotedValueFromHeader(contentType, "charset");
         if (value != null) {
            return value;
         } else {
            return contentType.startsWith("text/") ? StandardCharsets.ISO_8859_1.displayName() : null;
         }
      } else {
         return null;
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Stored Response", newValue);
   }

   public String toString() {
      return "%{STORED_RESPONSE}";
   }

   public static class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Stored Response";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{STORED_RESPONSE}") ? StoredResponse.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
