package io.undertow.server.protocol;

import io.undertow.UndertowLogger;
import io.undertow.server.ServerConnection;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.xnio.IoUtils;
import org.xnio.XnioExecutor;
import org.xnio.channels.ConnectedChannel;

public final class ParseTimeoutUpdater implements Runnable, ServerConnection.CloseListener, Closeable {
   private final ConnectedChannel connection;
   private final long requestParseTimeout;
   private final long requestIdleTimeout;
   private volatile XnioExecutor.Key handle;
   private volatile long expireTime;
   private volatile boolean parsing;
   private static final int FUZZ_FACTOR = 50;
   private final Runnable closeTask;

   public ParseTimeoutUpdater(final ConnectedChannel channel, long requestParseTimeout, long requestIdleTimeout) {
      this(channel, requestParseTimeout, requestIdleTimeout, new Runnable() {
         public void run() {
            IoUtils.safeClose((Closeable)channel);
         }
      });
   }

   public ParseTimeoutUpdater(ConnectedChannel channel, long requestParseTimeout, long requestIdleTimeout, Runnable closeTask) {
      this.expireTime = -1L;
      this.parsing = false;
      this.connection = channel;
      this.requestParseTimeout = requestParseTimeout;
      this.requestIdleTimeout = requestIdleTimeout;
      this.closeTask = closeTask;
   }

   public void connectionIdle() {
      this.parsing = false;
      this.handleSchedule(this.requestIdleTimeout);
   }

   private void handleSchedule(long timeout) {
      if (timeout == -1L) {
         this.expireTime = -1L;
      } else {
         long newExpireTime = System.currentTimeMillis() + timeout;
         long oldExpireTime = this.expireTime;
         this.expireTime = newExpireTime;
         if (newExpireTime < oldExpireTime && this.handle != null) {
            this.handle.remove();
            this.handle = null;
         }

         if (this.handle == null) {
            try {
               this.handle = WorkerUtils.executeAfter(this.connection.getIoThread(), this, timeout + 50L, TimeUnit.MILLISECONDS);
            } catch (RejectedExecutionException var8) {
               UndertowLogger.REQUEST_LOGGER.debug("Failed to schedule parse timeout, server is probably shutting down", var8);
            }
         }

      }
   }

   public void failedParse() {
      if (!this.parsing) {
         this.parsing = true;
         this.handleSchedule(this.requestParseTimeout);
      }

   }

   public void requestStarted() {
      this.expireTime = -1L;
      this.parsing = false;
   }

   public void run() {
      if (this.connection.isOpen()) {
         this.handle = null;
         if (this.expireTime > 0L) {
            long now = System.currentTimeMillis();
            if (this.expireTime > now) {
               this.handle = WorkerUtils.executeAfter(this.connection.getIoThread(), this, this.expireTime - now + 50L, TimeUnit.MILLISECONDS);
            } else {
               if (this.parsing) {
                  UndertowLogger.REQUEST_LOGGER.parseRequestTimedOut(this.connection.getPeerAddress());
               } else {
                  UndertowLogger.REQUEST_LOGGER.debugf("Timing out idle connection from %s", this.connection.getPeerAddress());
               }

               this.closeTask.run();
            }
         }

      }
   }

   public void closed(ServerConnection connection) {
      this.close();
   }

   public void close() {
      if (this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

   }
}
