package io.undertow.security.idm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum DigestAlgorithm {
   MD5("MD5", "MD5", false),
   MD5_SESS("MD5-sess", "MD5", true);

   private static final Map<String, DigestAlgorithm> BY_TOKEN;
   private final String token;
   private final String digestAlgorithm;
   private final boolean session;

   private DigestAlgorithm(String token, String digestAlgorithm, boolean session) {
      this.token = token;
      this.digestAlgorithm = digestAlgorithm;
      this.session = session;
   }

   public String getToken() {
      return this.token;
   }

   public String getAlgorithm() {
      return this.digestAlgorithm;
   }

   public boolean isSession() {
      return this.session;
   }

   public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
      return MessageDigest.getInstance(this.digestAlgorithm);
   }

   public static DigestAlgorithm forName(String name) {
      return (DigestAlgorithm)BY_TOKEN.get(name);
   }

   static {
      DigestAlgorithm[] algorithms = values();
      Map<String, DigestAlgorithm> byToken = new HashMap(algorithms.length);
      DigestAlgorithm[] var2 = algorithms;
      int var3 = algorithms.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DigestAlgorithm current = var2[var4];
         byToken.put(current.token, current);
      }

      BY_TOKEN = Collections.unmodifiableMap(byToken);
   }
}
