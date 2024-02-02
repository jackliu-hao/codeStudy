package io.undertow.attribute;

import io.undertow.UndertowMessages;

public class ReadOnlyAttributeException extends Exception {
   public ReadOnlyAttributeException() {
   }

   public ReadOnlyAttributeException(String attributeName, String newValue) {
      super(UndertowMessages.MESSAGES.couldNotSetAttribute(attributeName, newValue));
   }
}
