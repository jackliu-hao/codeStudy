package com.warrenstrange.googleauth;

import java.util.concurrent.TimeUnit;

public class GoogleAuthenticatorConfig {
   private long timeStepSizeInMillis;
   private int windowSize;
   private int codeDigits;
   private int numberOfScratchCodes;
   private int keyModulus;
   private int secretBits;
   private KeyRepresentation keyRepresentation;
   private HmacHashFunction hmacHashFunction;

   public GoogleAuthenticatorConfig() {
      this.timeStepSizeInMillis = TimeUnit.SECONDS.toMillis(30L);
      this.windowSize = 3;
      this.codeDigits = 6;
      this.numberOfScratchCodes = 5;
      this.keyModulus = (int)Math.pow(10.0, (double)this.codeDigits);
      this.secretBits = 160;
      this.keyRepresentation = KeyRepresentation.BASE32;
      this.hmacHashFunction = HmacHashFunction.HmacSHA1;
   }

   public int getKeyModulus() {
      return this.keyModulus;
   }

   public KeyRepresentation getKeyRepresentation() {
      return this.keyRepresentation;
   }

   public int getCodeDigits() {
      return this.codeDigits;
   }

   public int getNumberOfScratchCodes() {
      return this.numberOfScratchCodes;
   }

   public long getTimeStepSizeInMillis() {
      return this.timeStepSizeInMillis;
   }

   public int getWindowSize() {
      return this.windowSize;
   }

   public int getSecretBits() {
      return this.secretBits;
   }

   public HmacHashFunction getHmacHashFunction() {
      return this.hmacHashFunction;
   }

   public static class GoogleAuthenticatorConfigBuilder {
      private GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig();

      public GoogleAuthenticatorConfig build() {
         return this.config;
      }

      public GoogleAuthenticatorConfigBuilder setCodeDigits(int codeDigits) {
         if (codeDigits <= 0) {
            throw new IllegalArgumentException("Code digits must be positive.");
         } else if (codeDigits < 6) {
            throw new IllegalArgumentException("The minimum number of digits is 6.");
         } else if (codeDigits > 8) {
            throw new IllegalArgumentException("The maximum number of digits is 8.");
         } else {
            this.config.codeDigits = codeDigits;
            this.config.keyModulus = (int)Math.pow(10.0, (double)codeDigits);
            return this;
         }
      }

      public GoogleAuthenticatorConfigBuilder setNumberOfScratchCodes(int numberOfScratchCodes) {
         if (numberOfScratchCodes < 0) {
            throw new IllegalArgumentException("The number of scratch codes must not be negative");
         } else if (numberOfScratchCodes > 1000) {
            throw new IllegalArgumentException("The maximum number of scratch codes is 1000");
         } else {
            this.config.numberOfScratchCodes = numberOfScratchCodes;
            return this;
         }
      }

      public GoogleAuthenticatorConfigBuilder setTimeStepSizeInMillis(long timeStepSizeInMillis) {
         if (timeStepSizeInMillis <= 0L) {
            throw new IllegalArgumentException("Time step size must be positive.");
         } else {
            this.config.timeStepSizeInMillis = timeStepSizeInMillis;
            return this;
         }
      }

      public GoogleAuthenticatorConfigBuilder setWindowSize(int windowSize) {
         if (windowSize <= 0) {
            throw new IllegalArgumentException("Window number must be positive.");
         } else {
            this.config.windowSize = windowSize;
            return this;
         }
      }

      public GoogleAuthenticatorConfigBuilder setSecretBits(int secretBits) {
         if (secretBits < 128) {
            throw new IllegalArgumentException("Secret bits must be greater than or equal to 128.");
         } else if (secretBits % 8 != 0) {
            throw new IllegalArgumentException("Secret bits must be a multiple of 8.");
         } else {
            this.config.secretBits = secretBits;
            return this;
         }
      }

      public GoogleAuthenticatorConfigBuilder setKeyRepresentation(KeyRepresentation keyRepresentation) {
         if (keyRepresentation == null) {
            throw new IllegalArgumentException("Key representation cannot be null.");
         } else {
            this.config.keyRepresentation = keyRepresentation;
            return this;
         }
      }

      public GoogleAuthenticatorConfigBuilder setHmacHashFunction(HmacHashFunction hmacHashFunction) {
         if (hmacHashFunction == null) {
            throw new IllegalArgumentException("HMAC Hash Function cannot be null.");
         } else {
            this.config.hmacHashFunction = hmacHashFunction;
            return this;
         }
      }
   }
}
