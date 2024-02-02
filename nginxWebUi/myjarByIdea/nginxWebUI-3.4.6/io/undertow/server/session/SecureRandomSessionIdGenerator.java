package io.undertow.server.session;

import java.security.SecureRandom;

public class SecureRandomSessionIdGenerator implements SessionIdGenerator {
   private final SecureRandom random = new SecureRandom();
   private volatile int length = 30;
   private static final char[] SESSION_ID_ALPHABET;
   private static final String ALPHABET_PROPERTY = "io.undertow.server.session.SecureRandomSessionIdGenerator.ALPHABET";

   public String createSessionId() {
      byte[] bytes = new byte[this.length];
      this.random.nextBytes(bytes);
      return new String(this.encode(bytes));
   }

   public int getLength() {
      return this.length;
   }

   public void setLength(int length) {
      this.length = length;
   }

   private char[] encode(byte[] data) {
      char[] out = new char[(data.length + 2) / 3 * 4];
      char[] alphabet = SESSION_ID_ALPHABET;
      int i = 0;

      for(int index = 0; i < data.length; index += 4) {
         boolean quad = false;
         boolean trip = false;
         int val = 255 & data[i];
         val <<= 8;
         if (i + 1 < data.length) {
            val |= 255 & data[i + 1];
            trip = true;
         }

         val <<= 8;
         if (i + 2 < data.length) {
            val |= 255 & data[i + 2];
            quad = true;
         }

         out[index + 3] = alphabet[quad ? val & 63 : 63];
         val >>= 6;
         out[index + 2] = alphabet[trip ? val & 63 : 63];
         val >>= 6;
         out[index + 1] = alphabet[val & 63];
         val >>= 6;
         out[index] = alphabet[val & 63];
         i += 3;
      }

      return out;
   }

   static {
      String alphabet = System.getProperty("io.undertow.server.session.SecureRandomSessionIdGenerator.ALPHABET", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_");
      if (alphabet.length() != 64) {
         throw new RuntimeException("io.undertow.server.session.SecureRandomSessionIdGenerator must be exactly 64 characters long");
      } else {
         SESSION_ID_ALPHABET = alphabet.toCharArray();
      }
   }
}
