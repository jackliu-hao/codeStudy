package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

public class ResponseReasonPhraseAttribute implements ExchangeAttribute {
   public static final String RESPONSE_REASON_PHRASE = "%{RESPONSE_REASON_PHRASE}";
   public static final ExchangeAttribute INSTANCE = new ResponseReasonPhraseAttribute();

   private ResponseReasonPhraseAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return StatusCodes.getReason(exchange.getStatusCode());
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.setReasonPhrase(newValue);
   }

   public String toString() {
      return "%{RESPONSE_REASON_PHRASE}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Response reason phrase";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{RESPONSE_REASON_PHRASE}") ? ResponseReasonPhraseAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
