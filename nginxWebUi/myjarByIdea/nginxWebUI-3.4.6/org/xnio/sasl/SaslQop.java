package org.xnio.sasl;

import org.xnio._private.Messages;

public enum SaslQop {
   AUTH("auth"),
   AUTH_INT("auth-int"),
   AUTH_CONF("auth-conf");

   private final String s;

   private SaslQop(String s) {
      this.s = s;
   }

   public static SaslQop fromString(String name) {
      if ("auth".equals(name)) {
         return AUTH;
      } else if ("auth-int".equals(name)) {
         return AUTH_INT;
      } else if ("auth-conf".equals(name)) {
         return AUTH_CONF;
      } else {
         throw Messages.msg.invalidQop(name);
      }
   }

   public String getString() {
      return this.s;
   }

   public String toString() {
      return this.s;
   }
}
