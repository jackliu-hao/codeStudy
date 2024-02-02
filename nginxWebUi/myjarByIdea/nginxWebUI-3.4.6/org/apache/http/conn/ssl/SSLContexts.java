package org.apache.http.conn.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

/** @deprecated */
@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class SSLContexts {
   public static SSLContext createDefault() throws SSLInitializationException {
      try {
         SSLContext sslcontext = SSLContext.getInstance("TLS");
         sslcontext.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
         return sslcontext;
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
      return new SSLContextBuilder();
   }
}
