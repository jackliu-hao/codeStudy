package io.undertow.protocols.alpn;

import io.undertow.UndertowLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.net.ssl.SSLEngine;

public class OpenSSLAlpnProvider implements ALPNProvider {
   private static volatile OpenSSLALPNMethods openSSLALPNMethods;
   private static volatile boolean initialized;
   public static final String OPENSSL_ENGINE = "org.wildfly.openssl.OpenSSLEngine";

   public boolean isEnabled(SSLEngine sslEngine) {
      return sslEngine.getClass().getName().equals("org.wildfly.openssl.OpenSSLEngine") && getOpenSSLAlpnMethods() != null;
   }

   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
      try {
         getOpenSSLAlpnMethods().setApplicationProtocols().invoke(engine, protocols);
         return engine;
      } catch (InvocationTargetException | IllegalAccessException var4) {
         throw new RuntimeException(var4);
      }
   }

   public String getSelectedProtocol(SSLEngine engine) {
      try {
         return (String)getOpenSSLAlpnMethods().getApplicationProtocol().invoke(engine);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   private static OpenSSLALPNMethods getOpenSSLAlpnMethods() {
      if (!initialized) {
         Class var0 = OpenSSLAlpnProvider.class;
         synchronized(OpenSSLAlpnProvider.class) {
            if (!initialized) {
               openSSLALPNMethods = (OpenSSLALPNMethods)AccessController.doPrivileged(new PrivilegedAction<OpenSSLALPNMethods>() {
                  public OpenSSLALPNMethods run() {
                     try {
                        Class<?> openSSLEngine = Class.forName("org.wildfly.openssl.OpenSSLEngine", true, OpenSSLAlpnProvider.class.getClassLoader());
                        Method setApplicationProtocols = openSSLEngine.getMethod("setApplicationProtocols", String[].class);
                        Method getApplicationProtocol = openSSLEngine.getMethod("getSelectedApplicationProtocol");
                        UndertowLogger.ROOT_LOGGER.debug("OpenSSL ALPN Enabled");
                        return new OpenSSLALPNMethods(setApplicationProtocols, getApplicationProtocol);
                     } catch (Throwable var4) {
                        UndertowLogger.ROOT_LOGGER.debug("OpenSSL ALPN disabled", var4);
                        return null;
                     }
                  }
               });
               initialized = true;
            }
         }
      }

      return openSSLALPNMethods;
   }

   public int getPriority() {
      return 400;
   }

   public String toString() {
      return "OpenSSLAlpnProvider";
   }

   public static class OpenSSLALPNMethods {
      private final Method setApplicationProtocols;
      private final Method getApplicationProtocol;

      OpenSSLALPNMethods(Method setApplicationProtocols, Method getApplicationProtocol) {
         this.setApplicationProtocols = setApplicationProtocols;
         this.getApplicationProtocol = getApplicationProtocol;
      }

      public Method getApplicationProtocol() {
         return this.getApplicationProtocol;
      }

      public Method setApplicationProtocols() {
         return this.setApplicationProtocols;
      }
   }
}
