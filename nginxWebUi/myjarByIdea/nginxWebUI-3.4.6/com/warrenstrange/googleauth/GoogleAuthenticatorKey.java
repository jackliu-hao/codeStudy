package com.warrenstrange.googleauth;

import java.util.ArrayList;
import java.util.List;

public final class GoogleAuthenticatorKey {
   private final GoogleAuthenticatorConfig config;
   private final String key;
   private final int verificationCode;
   private final List<Integer> scratchCodes;

   private GoogleAuthenticatorKey(GoogleAuthenticatorConfig config, String key, int verificationCode, List<Integer> scratchCodes) {
      if (key == null) {
         throw new IllegalArgumentException("Key cannot be null");
      } else if (config == null) {
         throw new IllegalArgumentException("Configuration cannot be null");
      } else if (scratchCodes == null) {
         throw new IllegalArgumentException("Scratch codes cannot be null");
      } else {
         this.config = config;
         this.key = key;
         this.verificationCode = verificationCode;
         this.scratchCodes = new ArrayList(scratchCodes);
      }
   }

   public List<Integer> getScratchCodes() {
      return this.scratchCodes;
   }

   public GoogleAuthenticatorConfig getConfig() {
      return this.config;
   }

   public String getKey() {
      return this.key;
   }

   public int getVerificationCode() {
      return this.verificationCode;
   }

   // $FF: synthetic method
   GoogleAuthenticatorKey(GoogleAuthenticatorConfig x0, String x1, int x2, List x3, Object x4) {
      this(x0, x1, x2, x3);
   }

   public static class Builder {
      private GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig();
      private String key;
      private int verificationCode;
      private List<Integer> scratchCodes = new ArrayList();

      public Builder(String key) {
         this.key = key;
      }

      public GoogleAuthenticatorKey build() {
         return new GoogleAuthenticatorKey(this.config, this.key, this.verificationCode, this.scratchCodes);
      }

      public Builder setConfig(GoogleAuthenticatorConfig config) {
         this.config = config;
         return this;
      }

      public Builder setKey(String key) {
         this.key = key;
         return this;
      }

      public Builder setVerificationCode(int verificationCode) {
         this.verificationCode = verificationCode;
         return this;
      }

      public Builder setScratchCodes(List<Integer> scratchCodes) {
         this.scratchCodes = scratchCodes;
         return this;
      }
   }
}
