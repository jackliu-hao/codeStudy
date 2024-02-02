package ch.qos.logback.core.net;

import ch.qos.logback.core.util.DelayStrategy;
import ch.qos.logback.core.util.FixedDelay;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;

public class DefaultSocketConnector implements SocketConnector {
   private final InetAddress address;
   private final int port;
   private final DelayStrategy delayStrategy;
   private SocketConnector.ExceptionHandler exceptionHandler;
   private SocketFactory socketFactory;

   public DefaultSocketConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
      this(address, port, new FixedDelay(initialDelay, retryDelay));
   }

   public DefaultSocketConnector(InetAddress address, int port, DelayStrategy delayStrategy) {
      this.address = address;
      this.port = port;
      this.delayStrategy = delayStrategy;
   }

   public Socket call() throws InterruptedException {
      this.useDefaultsForMissingFields();

      Socket socket;
      for(socket = this.createSocket(); socket == null && !Thread.currentThread().isInterrupted(); socket = this.createSocket()) {
         Thread.sleep(this.delayStrategy.nextDelay());
      }

      return socket;
   }

   private Socket createSocket() {
      Socket newSocket = null;

      try {
         newSocket = this.socketFactory.createSocket(this.address, this.port);
      } catch (IOException var3) {
         this.exceptionHandler.connectionFailed(this, var3);
      }

      return newSocket;
   }

   private void useDefaultsForMissingFields() {
      if (this.exceptionHandler == null) {
         this.exceptionHandler = new ConsoleExceptionHandler();
      }

      if (this.socketFactory == null) {
         this.socketFactory = SocketFactory.getDefault();
      }

   }

   public void setExceptionHandler(SocketConnector.ExceptionHandler exceptionHandler) {
      this.exceptionHandler = exceptionHandler;
   }

   public void setSocketFactory(SocketFactory socketFactory) {
      this.socketFactory = socketFactory;
   }

   private static class ConsoleExceptionHandler implements SocketConnector.ExceptionHandler {
      private ConsoleExceptionHandler() {
      }

      public void connectionFailed(SocketConnector connector, Exception ex) {
         System.out.println(ex);
      }

      // $FF: synthetic method
      ConsoleExceptionHandler(Object x0) {
         this();
      }
   }
}
