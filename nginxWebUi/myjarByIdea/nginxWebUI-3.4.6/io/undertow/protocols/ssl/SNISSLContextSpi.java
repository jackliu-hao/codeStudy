package io.undertow.protocols.ssl;

import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

class SNISSLContextSpi extends SSLContextSpi {
   private final SNIContextMatcher matcher;

   SNISSLContextSpi(SNIContextMatcher matcher) {
      this.matcher = matcher;
   }

   protected void engineInit(KeyManager[] keyManagers, TrustManager[] trustManagers, SecureRandom secureRandom) throws KeyManagementException {
   }

   protected SSLSocketFactory engineGetSocketFactory() {
      return this.matcher.getDefaultContext().getSocketFactory();
   }

   protected SSLServerSocketFactory engineGetServerSocketFactory() {
      return this.matcher.getDefaultContext().getServerSocketFactory();
   }

   protected SSLEngine engineCreateSSLEngine() {
      return new SNISSLEngine(this.matcher);
   }

   protected SSLEngine engineCreateSSLEngine(String s, int i) {
      return new SNISSLEngine(this.matcher, s, i);
   }

   protected SSLSessionContext engineGetServerSessionContext() {
      return this.matcher.getDefaultContext().getServerSessionContext();
   }

   protected SSLSessionContext engineGetClientSessionContext() {
      return this.matcher.getDefaultContext().getClientSessionContext();
   }
}
