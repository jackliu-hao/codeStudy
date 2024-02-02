package ch.qos.logback.core.net;

import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

public abstract class AbstractSSLSocketAppender<E> extends AbstractSocketAppender<E> implements SSLComponent {
   private SSLConfiguration ssl;
   private SocketFactory socketFactory;

   protected AbstractSSLSocketAppender() {
   }

   protected SocketFactory getSocketFactory() {
      return this.socketFactory;
   }

   public void start() {
      try {
         SSLContext sslContext = this.getSsl().createContext(this);
         SSLParametersConfiguration parameters = this.getSsl().getParameters();
         parameters.setContext(this.getContext());
         this.socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
         super.start();
      } catch (Exception var3) {
         this.addError(var3.getMessage(), var3);
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
