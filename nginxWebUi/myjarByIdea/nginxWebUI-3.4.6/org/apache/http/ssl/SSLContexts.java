package org.apache.http.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class SSLContexts {
   public static SSLContext createDefault() throws SSLInitializationException {
      try {
         SSLContext sslContext = SSLContext.getInstance("TLS");
         sslContext.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
         return sslContext;
      } catch (NoSuchAlgorithmException var1) {
         throw new SSLInitializationException(var1.getMessage(), var1);
      } catch (KeyManagementException var2) {
         throw new SSLInitializationException(var2.getMessage(), var2);
      }
   }

   public static SSLContext createSystemDefault() throws SSLInitializationException {
      try {
         return SSLContext.getDefault();
      } catch (NoSuchAlgorithmException var1) {
         return createDefault();
      }
   }

   public static SSLContextBuilder custom() {
      return SSLContextBuilder.create();
   }
}
