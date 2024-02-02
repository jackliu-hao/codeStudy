package org.xnio.sasl;

import org.xnio._private.Messages;

public enum SaslStrength {
   LOW("low"),
   MEDIUM("medium"),
   HIGH("high");

   private final String toString;

   private SaslStrength(String toString) {
      this.toString = toString;
   }

   public static SaslStrength fromString(String name) {
      if ("low".equals(name)) {
         return LOW;
      } else if ("medium".equals(name)) {
         return MEDIUM;
      } else if ("high".equals(name)) {
         return HIGH;
      } else {
         throw Messages.msg.invalidStrength(name);
      }
   }

   public String toString() {
      return this.toString;
   }
}
