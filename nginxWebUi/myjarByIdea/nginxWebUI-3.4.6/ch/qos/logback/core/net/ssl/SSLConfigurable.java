package ch.qos.logback.core.net.ssl;

public interface SSLConfigurable {
   String[] getDefaultProtocols();

   String[] getSupportedProtocols();

   void setEnabledProtocols(String[] var1);

   String[] getDefaultCipherSuites();

   String[] getSupportedCipherSuites();

   void setEnabledCipherSuites(String[] var1);

   void setNeedClientAuth(boolean var1);

   void setWantClientAuth(boolean var1);

   void setHostnameVerification(boolean var1);
}
