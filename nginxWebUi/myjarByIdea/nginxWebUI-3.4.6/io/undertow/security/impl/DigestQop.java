package io.undertow.security.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum DigestQop {
   AUTH("auth", false),
   AUTH_INT("auth-int", true);

   private static final Map<String, DigestQop> BY_TOKEN;
   private final String token;
   private final boolean integrity;

   private DigestQop(String token, boolean integrity) {
      this.token = token;
      this.integrity = integrity;
   }

   public String getToken() {
      return this.token;
   }

   public boolean isMessageIntegrity() {
      return this.integrity;
   }

   public static DigestQop forName(String name) {
      return (DigestQop)BY_TOKEN.get(name);
   }

   static {
      DigestQop[] qops = values();
      Map<String, DigestQop> byToken = new HashMap(qops.length);
      DigestQop[] var2 = qops;
      int var3 = qops.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DigestQop current = var2[var4];
         byToken.put(current.token, current);
      }

      BY_TOKEN = Collections.unmodifiableMap(byToken);
   }
}
