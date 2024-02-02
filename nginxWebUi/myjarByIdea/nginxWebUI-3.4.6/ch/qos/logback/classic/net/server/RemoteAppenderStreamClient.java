package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.HardenedObjectInputStream;
import ch.qos.logback.core.util.CloseUtil;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

class RemoteAppenderStreamClient implements RemoteAppenderClient {
   private final String id;
   private final Socket socket;
   private final InputStream inputStream;
   private LoggerContext lc;
   private Logger logger;

   public RemoteAppenderStreamClient(String id, Socket socket) {
      this.id = id;
      this.socket = socket;
      this.inputStream = null;
   }

   public RemoteAppenderStreamClient(String id, InputStream inputStream) {
      this.id = id;
      this.socket = null;
      this.inputStream = inputStream;
   }

   public void setLoggerContext(LoggerContext lc) {
      this.lc = lc;
      this.logger = lc.getLogger(this.getClass().getPackage().getName());
   }

   public void close() {
      if (this.socket != null) {
         CloseUtil.closeQuietly(this.socket);
      }
   }

   public void run() {
      this.logger.info(this + ": connected");
      HardenedObjectInputStream ois = null;

      try {
         ois = this.createObjectInputStream();

         while(true) {
            ILoggingEvent event;
            Logger remoteLogger;
            do {
               event = (ILoggingEvent)ois.readObject();
               remoteLogger = this.lc.getLogger(event.getLoggerName());
            } while(!remoteLogger.isEnabledFor(event.getLevel()));

            remoteLogger.callAppenders(event);
         }
      } catch (EOFException var10) {
      } catch (IOException var11) {
         this.logger.info(this + ": " + var11);
      } catch (ClassNotFoundException var12) {
         this.logger.error(this + ": unknown event class");
      } catch (RuntimeException var13) {
         this.logger.error(this + ": " + var13);
      } finally {
         if (ois != null) {
            CloseUtil.closeQuietly((Closeable)ois);
         }

         this.close();
         this.logger.info(this + ": connection closed");
      }

   }

   private HardenedObjectInputStream createObjectInputStream() throws IOException {
      return this.inputStream != null ? new HardenedLoggingEventInputStream(this.inputStream) : new HardenedLoggingEventInputStream(this.socket.getInputStream());
   }

   public String toString() {
      return "client " + this.id;
   }
}
