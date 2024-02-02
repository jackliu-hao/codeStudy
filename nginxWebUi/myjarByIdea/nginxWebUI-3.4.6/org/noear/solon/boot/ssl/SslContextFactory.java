package org.noear.solon.boot.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.noear.solon.Utils;

public class SslContextFactory {
   public static SSLContext create() throws IOException {
      String keyStoreName = System.getProperty("javax.net.ssl.keyStore");
      String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
      String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
      if (Utils.isEmpty(keyStoreType)) {
         keyStoreType = "jks";
      }

      KeyStore keyStore = loadKeyStore(keyStoreName, keyStoreType, keyStorePassword);
      KeyManager[] keyManagers = buildKeyManagers(keyStore, keyStorePassword.toCharArray());

      try {
         SSLContext sslContext = SSLContext.getInstance("TLS");
         sslContext.init(keyManagers, (TrustManager[])null, (SecureRandom)null);
         return sslContext;
      } catch (KeyManagementException | NoSuchAlgorithmException var7) {
         throw new IOException("Unable to create and initialise the SSLContext", var7);
      }
   }

   private static KeyStore loadKeyStore(final String location, String type, String storePassword) throws IOException {
      URL KeyStoreUrl = Utils.getResource(location);
      InputStream KeyStoreStream = null;
      if (KeyStoreUrl == null) {
         KeyStoreStream = new FileInputStream(location);
      } else {
         KeyStoreStream = KeyStoreUrl.openStream();
      }

      try {
         InputStream stream = KeyStoreStream;
         Throwable var6 = null;

         KeyStore var8;
         try {
            KeyStore loadedKeystore = KeyStore.getInstance(type);
            loadedKeystore.load((InputStream)stream, storePassword.toCharArray());
            var8 = loadedKeystore;
         } catch (Throwable var18) {
            var6 = var18;
            throw var18;
         } finally {
            if (KeyStoreStream != null) {
               if (var6 != null) {
                  try {
                     ((InputStream)stream).close();
                  } catch (Throwable var17) {
                     var6.addSuppressed(var17);
                  }
               } else {
                  ((InputStream)KeyStoreStream).close();
               }
            }

         }

         return var8;
      } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException var20) {
         throw new IOException(String.format("Unable to load KeyStore %s", location), var20);
      }
   }

   private static KeyManager[] buildKeyManagers(final KeyStore keyStore, char[] storePassword) throws IOException {
      try {
         KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
         keyManagerFactory.init(keyStore, storePassword);
         KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();
         return keyManagers;
      } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException var4) {
         throw new IOException("Unable to initialise KeyManager[]", var4);
      }
   }
}
