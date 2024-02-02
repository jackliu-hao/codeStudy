package ch.qos.logback.core.net;

import java.net.Socket;
import java.util.concurrent.Callable;
import javax.net.SocketFactory;

public interface SocketConnector extends Callable<Socket> {
   Socket call() throws InterruptedException;

   void setExceptionHandler(ExceptionHandler var1);

   void setSocketFactory(SocketFactory var1);

   public interface ExceptionHandler {
      void connectionFailed(SocketConnector var1, Exception var2);
   }
}
