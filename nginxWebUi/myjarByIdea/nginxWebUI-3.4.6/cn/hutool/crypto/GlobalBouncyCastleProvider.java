package cn.hutool.crypto;

import java.security.Provider;

public enum GlobalBouncyCastleProvider {
   INSTANCE;

   private Provider provider;
   private static boolean useBouncyCastle = true;

   private GlobalBouncyCastleProvider() {
      try {
         this.provider = ProviderFactory.createBouncyCastleProvider();
      } catch (NoClassDefFoundError var4) {
      }

   }

   public Provider getProvider() {
      return useBouncyCastle ? this.provider : null;
   }

   public static void setUseBouncyCastle(boolean isUseBouncyCastle) {
      useBouncyCastle = isUseBouncyCastle;
   }
}
