package ch.qos.logback.core.net.ssl;

import javax.net.ssl.SSLServerSocket;

public class SSLConfigurableServerSocket implements SSLConfigurable {
   private final SSLServerSocket delegate;

   public SSLConfigurableServerSocket(SSLServerSocket delegate) {
      this.delegate = delegate;
   }

   public String[] getDefaultProtocols() {
      return this.delegate.getEnabledProtocols();
   }

   public String[] getSupportedProtocols() {
      return this.delegate.getSupportedProtocols();
   }

   public void setEnabledProtocols(String[] protocols) {
      this.delegate.setEnabledProtocols(protocols);
   }

   public String[] getDefaultCipherSuites() {
      return this.delegate.getEnabledCipherSuites();
   }

   public String[] getSupportedCipherSuites() {
      return this.delegate.getSupportedCipherSuites();
   }

   public void setEnabledCipherSuites(String[] suites) {
      this.delegate.setEnabledCipherSuites(suites);
   }

   public void setNeedClientAuth(boolean state) {
      this.delegate.setNeedClientAuth(state);
   }

   public void setWantClientAuth(boolean state) {
      this.delegate.setWantClientAuth(state);
   }

   public void setHostnameVerification(boolean verifyHostname) {
   }
}
