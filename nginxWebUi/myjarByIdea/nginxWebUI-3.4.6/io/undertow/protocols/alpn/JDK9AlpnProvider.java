package io.undertow.protocols.alpn;

import io.undertow.UndertowLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

public class JDK9AlpnProvider implements ALPNProvider {
   public static final JDK9ALPNMethods JDK_9_ALPN_METHODS = (JDK9ALPNMethods)AccessController.doPrivileged(new PrivilegedAction<JDK9ALPNMethods>() {
      public JDK9ALPNMethods run() {
         try {
            String javaVersion = System.getProperty("java.specification.version");
            int vmVersion = 8;

            try {
               Matcher matcher = Pattern.compile("^(?:1\\.)?(\\d+)$").matcher(javaVersion);
               if (matcher.find()) {
                  vmVersion = Integer.parseInt(matcher.group(1));
               }
            } catch (Exception var7) {
            }

            String value = System.getProperty("io.undertow.protocols.alpn.jdk8");
            boolean addSupportIfExists = value == null || value.trim().isEmpty() || Boolean.parseBoolean(value);
            if (vmVersion <= 8 && !addSupportIfExists) {
               UndertowLogger.ROOT_LOGGER.debugf("It's not certain ALPN support was found. Provider %s will be disabled.", JDK9AlpnProvider.class.getName());
               return null;
            } else {
               Method setApplicationProtocols = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
               Method getApplicationProtocol = SSLEngine.class.getMethod("getApplicationProtocol");
               UndertowLogger.ROOT_LOGGER.debug("Using JDK9 ALPN");
               return new JDK9ALPNMethods(setApplicationProtocols, getApplicationProtocol);
            }
         } catch (Exception var8) {
            UndertowLogger.ROOT_LOGGER.debug("JDK9 ALPN not supported");
            return null;
         }
      }
   });
   private static final String JDK8_SUPPORT_PROPERTY = "io.undertow.protocols.alpn.jdk8";

   public boolean isEnabled(SSLEngine sslEngine) {
      return JDK_9_ALPN_METHODS != null;
   }

   public SSLEngine setProtocols(SSLEngine engine, String[] protocols) {
      SSLParameters sslParameters = engine.getSSLParameters();

      try {
         JDK_9_ALPN_METHODS.setApplicationProtocols().invoke(sslParameters, protocols);
      } catch (InvocationTargetException | IllegalAccessException var5) {
         throw new RuntimeException(var5);
      }

      engine.setSSLParameters(sslParameters);
      return engine;
   }

   public String getSelectedProtocol(SSLEngine engine) {
      try {
         return (String)JDK_9_ALPN_METHODS.getApplicationProtocol().invoke(engine);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public int getPriority() {
      return 200;
   }

   public String toString() {
      return "JDK9AlpnProvider";
   }

   public static class JDK9ALPNMethods {
      private final Method setApplicationProtocols;
      private final Method getApplicationProtocol;

      JDK9ALPNMethods(Method setApplicationProtocols, Method getApplicationProtocol) {
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
