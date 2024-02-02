package ch.qos.logback.core.net.ssl;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;

public class SSLConfigurableSocket implements SSLConfigurable {
   private final SSLSocket delegate;

   public SSLConfigurableSocket(SSLSocket delegate) {
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

   public void setHostnameVerification(boolean hostnameVerification) {
      if (hostnameVerification) {
         SSLParameters sslParameters = this.delegate.getSSLParameters();
         sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
         this.delegate.setSSLParameters(sslParameters);
      }
   }
}
