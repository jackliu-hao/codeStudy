package ch.qos.logback.classic.net;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.server.HardenedLoggingEventInputStream;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class SocketNode implements Runnable {
   Socket socket;
   LoggerContext context;
   HardenedLoggingEventInputStream hardenedLoggingEventInputStream;
   SocketAddress remoteSocketAddress;
   Logger logger;
   boolean closed = false;
   SimpleSocketServer socketServer;

   public SocketNode(SimpleSocketServer socketServer, Socket socket, LoggerContext context) {
      this.socketServer = socketServer;
      this.socket = socket;
      this.remoteSocketAddress = socket.getRemoteSocketAddress();
      this.context = context;
      this.logger = context.getLogger(SocketNode.class);
   }

   public void run() {
      try {
         this.hardenedLoggingEventInputStream = new HardenedLoggingEventInputStream(new BufferedInputStream(this.socket.getInputStream()));
      } catch (Exception var4) {
         this.logger.error((String)("Could not open ObjectInputStream to " + this.socket), (Throwable)var4);
         this.closed = true;
      }

      try {
         while(!this.closed) {
            ILoggingEvent event = (ILoggingEvent)this.hardenedLoggingEventInputStream.readObject();
            Logger remoteLogger = this.context.getLogger(event.getLoggerName());
            if (remoteLogger.isEnabledFor(event.getLevel())) {
               remoteLogger.callAppenders(event);
            }
         }
      } catch (EOFException var5) {
         this.logger.info("Caught java.io.EOFException closing connection.");
      } catch (SocketException var6) {
         this.logger.info("Caught java.net.SocketException closing connection.");
      } catch (IOException var7) {
         this.logger.info("Caught java.io.IOException: " + var7);
         this.logger.info("Closing connection.");
      } catch (Exception var8) {
         this.logger.error((String)"Unexpected exception. Closing connection.", (Throwable)var8);
      }

      this.socketServer.socketNodeClosing(this);
      this.close();
   }

   void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.hardenedLoggingEventInputStream != null) {
            try {
               this.hardenedLoggingEventInputStream.close();
            } catch (IOException var5) {
               this.logger.warn((String)"Could not close connection.", (Throwable)var5);
            } finally {
               this.hardenedLoggingEventInputStream = null;
            }
         }

      }
   }

   public String toString() {
      return this.getClass().getName() + this.remoteSocketAddress.toString();
   }
}
