package com.warrenstrange.googleauth;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

class ReseedingSecureRandom {
   private static final int MAX_OPERATIONS = 1000000;
   private final String provider;
   private final String algorithm;
   private final AtomicInteger count = new AtomicInteger(0);
   private volatile SecureRandom secureRandom;

   ReseedingSecureRandom() {
      this.algorithm = null;
      this.provider = null;
      this.buildSecureRandom();
   }

   ReseedingSecureRandom(String algorithm) {
      if (algorithm == null) {
         throw new IllegalArgumentException("Algorithm cannot be null.");
      } else {
         this.algorithm = algorithm;
         this.provider = null;
         this.buildSecureRandom();
      }
   }

   ReseedingSecureRandom(String algorithm, String provider) {
      if (algorithm == null) {
         throw new IllegalArgumentException("Algorithm cannot be null.");
      } else if (provider == null) {
         throw new IllegalArgumentException("Provider cannot be null.");
      } else {
         this.algorithm = algorithm;
         this.provider = provider;
         this.buildSecureRandom();
      }
   }

   private void buildSecureRandom() {
      try {
         if (this.algorithm == null && this.provider == null) {
            this.secureRandom = new SecureRandom();
         } else if (this.provider == null) {
            this.secureRandom = SecureRandom.getInstance(this.algorithm);
         } else {
            this.secureRandom = SecureRandom.getInstance(this.algorithm, this.provider);
         }

      } catch (NoSuchAlgorithmException var2) {
         throw new GoogleAuthenticatorException(String.format("Could not initialise SecureRandom with the specified algorithm: %s. Another provider can be chosen setting the %s system property.", this.algorithm, "com.warrenstrange.googleauth.rng.algorithm"), var2);
      } catch (NoSuchProviderException var3) {
         throw new GoogleAuthenticatorException(String.format("Could not initialise SecureRandom with the specified provider: %s. Another provider can be chosen setting the %s system property.", this.provider, "com.warrenstrange.googleauth.rng.algorithmProvider"), var3);
      }
   }

   void nextBytes(byte[] bytes) {
      if (this.count.incrementAndGet() > 1000000) {
         synchronized(this) {
            if (this.count.get() > 1000000) {
               this.buildSecureRandom();
               this.count.set(0);
            }
         }
      }

      this.secureRandom.nextBytes(bytes);
   }
}
