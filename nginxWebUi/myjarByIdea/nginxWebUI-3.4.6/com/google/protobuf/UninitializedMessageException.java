package com.google.protobuf;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class UninitializedMessageException extends RuntimeException {
   private static final long serialVersionUID = -7466929953374883507L;
   private final List<String> missingFields;

   public UninitializedMessageException(MessageLite message) {
      super("Message was missing required fields.  (Lite runtime could not determine which fields were missing).");
      this.missingFields = null;
   }

   public UninitializedMessageException(List<String> missingFields) {
      super(buildDescription(missingFields));
      this.missingFields = missingFields;
   }

   public List<String> getMissingFields() {
      return Collections.unmodifiableList(this.missingFields);
   }

   public InvalidProtocolBufferException asInvalidProtocolBufferException() {
      return new InvalidProtocolBufferException(this.getMessage());
   }

   private static String buildDescription(List<String> missingFields) {
      StringBuilder description = new StringBuilder("Message missing required fields: ");
      boolean first = true;

      String field;
      for(Iterator var3 = missingFields.iterator(); var3.hasNext(); description.append(field)) {
         field = (String)var3.next();
         if (first) {
            first = false;
         } else {
            description.append(", ");
         }
      }

      return description.toString();
   }
}
