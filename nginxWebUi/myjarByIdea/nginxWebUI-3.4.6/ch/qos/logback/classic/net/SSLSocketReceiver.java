package ch.qos.logback.classic.net;

import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

public class SSLSocketReceiver extends SocketReceiver implements SSLComponent {
   private SSLConfiguration ssl;
   private SocketFactory socketFactory;

   protected SocketFactory getSocketFactory() {
      return this.socketFactory;
   }

   protected boolean shouldStart() {
      try {
         SSLContext sslContext = this.getSsl().createContext(this);
         SSLParametersConfiguration parameters = this.getSsl().getParameters();
         parameters.setContext(this.getContext());
         this.socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
         return super.shouldStart();
      } catch (Exception var3) {
         this.addError(var3.getMessage(), var3);
         return false;
      }
   }

   public SSLConfiguration getSsl() {
      if (this.ssl == null) {
         this.ssl = new SSLConfiguration();
      }

      return this.ssl;
   }

   public void setSsl(SSLConfiguration ssl) {
      this.ssl = ssl;
   }
}
