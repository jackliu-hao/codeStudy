package io.undertow.conduits;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.server.OpenListener;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.ReadReadyHandler;
import org.xnio.conduits.StreamSourceConduit;

public final class ReadTimeoutStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private XnioExecutor.Key handle;
   private final StreamConnection connection;
   private volatile long expireTime = -1L;
   private final OpenListener openListener;
   private static final int FUZZ_FACTOR = 50;
   private volatile boolean expired;
   private final Runnable timeoutCommand = new Runnable() {
      public void run() {
         ReadTimeoutStreamSourceConduit.this.handle = null;
         if (ReadTimeoutStreamSourceConduit.this.expireTime != -1L) {
            long current = System.currentTimeMillis();
            if (current < ReadTimeoutStreamSourceConduit.this.expireTime) {
               ReadTimeoutStreamSourceConduit.this.handle = WorkerUtils.executeAfter(ReadTimeoutStreamSourceConduit.this.connection.getIoThread(), ReadTimeoutStreamSourceConduit.this.timeoutCommand, ReadTimeoutStreamSourceConduit.this.expireTime - current + 50L, TimeUnit.MILLISECONDS);
            } else {
               UndertowLogger.REQUEST_LOGGER.tracef("Timing out channel %s due to inactivity", ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel());
               synchronized(ReadTimeoutStreamSourceConduit.this) {
                  ReadTimeoutStreamSourceConduit.this.expired = true;
               }

               boolean readResumed = ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel().isReadResumed();
               ChannelListener<? super ConduitStreamSourceChannel> readListener = ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel().getReadListener();
               if (readResumed) {
                  ChannelListeners.invokeChannelListener(ReadTimeoutStreamSourceConduit.this.connection.getSourceChannel(), readListener);
               }

               if (ReadTimeoutStreamSourceConduit.this.connection.getSinkChannel().isWriteResumed()) {
                  ChannelListeners.invokeChannelListener(ReadTimeoutStreamSourceConduit.this.connection.getSinkChannel(), ReadTimeoutStreamSourceConduit.this.connection.getSinkChannel().getWriteListener());
               }

               IoUtils.safeClose((Closeable)ReadTimeoutStreamSourceConduit.this.connection);
            }
         }
      }
   };

   public ReadTimeoutStreamSourceConduit(StreamSourceConduit delegate, StreamConnection connection, OpenListener openListener) {
      super(delegate);
      this.connection = connection;
      this.openListener = openListener;
      final ReadReadyHandler handler = new ReadReadyHandler.ChannelListenerHandler(connection.getSourceChannel());
      delegate.setReadReadyHandler(new ReadReadyHandler() {
         public void readReady() {
            handler.readReady();
         }

         public void forceTermination() {
            ReadTimeoutStreamSourceConduit.this.cleanup();
            handler.forceTermination();
         }

         public void terminated() {
            ReadTimeoutStreamSourceConduit.this.cleanup();
            handler.terminated();
         }
      });
   }

   private void handleReadTimeout(long ret) throws IOException {
      if (!this.connection.isOpen()) {
         this.cleanup();
      } else if (ret == -1L) {
         this.cleanup();
      } else {
         Integer timeout = this.getTimeout();
         if (timeout != null && timeout > 0) {
            long currentTime = System.currentTimeMillis();
            if (ret == 0L) {
               long expireTimeVar = this.expireTime;
               if (expireTimeVar != -1L && currentTime > expireTimeVar) {
                  IoUtils.safeClose((Closeable)this.connection);
                  throw UndertowMessages.MESSAGES.readTimedOut((long)this.getTimeout());
               }
            }

            this.expireTime = currentTime + (long)timeout;
            if (this.handle == null) {
               this.handle = this.connection.getIoThread().executeAfter(this.timeoutCommand, (long)timeout, TimeUnit.MILLISECONDS);
            }

         }
      }
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      this.checkExpired();
      long ret = super.transferTo(position, count, target);
      this.handleReadTimeout(ret);
      return ret;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      this.checkExpired();
      long ret = super.transferTo(count, throughBuffer, target);
      this.handleReadTimeout(ret);
      return ret;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      this.checkExpired();
      long ret = super.read(dsts, offset, length);
      this.handleReadTimeout(ret);
      return ret;
   }

   public int read(ByteBuffer dst) throws IOException {
      this.checkExpired();
      int ret = super.read(dst);
      this.handleReadTimeout((long)ret);
      return ret;
   }

   public void awaitReadable() throws IOException {
      this.checkExpired();
      Integer timeout = this.getTimeout();
      if (timeout != null && timeout > 0) {
         super.awaitReadable((long)(timeout + 50), TimeUnit.MILLISECONDS);
      } else {
         super.awaitReadable();
      }

   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.checkExpired();
      Integer timeout = this.getTimeout();
      if (timeout != null && timeout > 0) {
         long millis = timeUnit.toMillis(time);
         super.awaitReadable(Math.min(millis, (long)(timeout + 50)), TimeUnit.MILLISECONDS);
      } else {
         super.awaitReadable(time, timeUnit);
      }

   }

   private Integer getTimeout() {
      Integer timeout = 0;

      try {
         timeout = (Integer)this.connection.getSourceChannel().getOption(Options.READ_TIMEOUT);
      } catch (IOException var3) {
      }

      Integer idleTimeout = (Integer)this.openListener.getUndertowOptions().get(UndertowOptions.IDLE_TIMEOUT);
      if ((timeout == null || timeout <= 0) && idleTimeout != null) {
         timeout = idleTimeout;
      } else if (timeout != null && idleTimeout != null && idleTimeout > 0) {
         timeout = Math.min(timeout, idleTimeout);
      }

      return timeout;
   }

   public void resumeReads() {
      super.resumeReads();
      if (this.handle == null) {
         try {
            this.handleReadTimeout(1L);
         } catch (IOException var2) {
         }
      }

   }

   public void terminateReads() throws IOException {
      this.checkExpired();
      super.terminateReads();
      this.cleanup();
   }

   private void cleanup() {
      if (this.handle != null) {
         this.handle.remove();
         this.handle = null;
         this.expireTime = -1L;
      }

   }

   public void suspendReads() {
      super.suspendReads();
      this.cleanup();
   }

   private void checkExpired() throws ReadTimeoutException {
      synchronized(this) {
         if (this.expired) {
            throw UndertowMessages.MESSAGES.readTimedOut(System.currentTimeMillis());
         }
      }
   }

   public String toString() {
      return super.toString() + " (next: " + this.next + ")";
   }
}
