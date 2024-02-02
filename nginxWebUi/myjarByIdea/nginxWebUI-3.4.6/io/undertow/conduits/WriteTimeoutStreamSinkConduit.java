package io.undertow.conduits;

import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.server.OpenListener;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.StreamSinkConduit;

public final class WriteTimeoutStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private XnioExecutor.Key handle;
   private final StreamConnection connection;
   private volatile long expireTime = -1L;
   private final OpenListener openListener;
   private static final int FUZZ_FACTOR = 50;
   private final Runnable timeoutCommand = new Runnable() {
      public void run() {
         WriteTimeoutStreamSinkConduit.this.handle = null;
         if (WriteTimeoutStreamSinkConduit.this.expireTime != -1L) {
            long current = System.currentTimeMillis();
            if (current < WriteTimeoutStreamSinkConduit.this.expireTime) {
               WriteTimeoutStreamSinkConduit.this.handle = WorkerUtils.executeAfter(WriteTimeoutStreamSinkConduit.this.getWriteThread(), WriteTimeoutStreamSinkConduit.this.timeoutCommand, WriteTimeoutStreamSinkConduit.this.expireTime - current + 50L, TimeUnit.MILLISECONDS);
            } else {
               UndertowLogger.REQUEST_LOGGER.tracef("Timing out channel %s due to inactivity", WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel());
               IoUtils.safeClose((Closeable)WriteTimeoutStreamSinkConduit.this.connection);
               if (WriteTimeoutStreamSinkConduit.this.connection.getSourceChannel().isReadResumed()) {
                  ChannelListeners.invokeChannelListener(WriteTimeoutStreamSinkConduit.this.connection.getSourceChannel(), WriteTimeoutStreamSinkConduit.this.connection.getSourceChannel().getReadListener());
               }

               if (WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel().isWriteResumed()) {
                  ChannelListeners.invokeChannelListener(WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel(), WriteTimeoutStreamSinkConduit.this.connection.getSinkChannel().getWriteListener());
               }

            }
         }
      }
   };

   public WriteTimeoutStreamSinkConduit(StreamSinkConduit delegate, StreamConnection connection, OpenListener openListener) {
      super(delegate);
      this.connection = connection;
      this.openListener = openListener;
   }

   private void handleWriteTimeout(long ret) throws IOException {
      if (this.connection.isOpen()) {
         if (ret != 0L || this.handle == null) {
            Integer timeout = this.getTimeout();
            if (timeout != null && timeout > 0) {
               long currentTime = System.currentTimeMillis();
               long expireTimeVar = this.expireTime;
               if (expireTimeVar != -1L && currentTime > expireTimeVar) {
                  IoUtils.safeClose((Closeable)this.connection);
                  throw new ClosedChannelException();
               } else {
                  this.expireTime = currentTime + (long)timeout;
               }
            }
         }
      }
   }

   public int write(ByteBuffer src) throws IOException {
      int ret = super.write(src);
      this.handleWriteTimeout((long)ret);
      return ret;
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      long ret = super.write(srcs, offset, length);
      this.handleWriteTimeout(ret);
      return ret;
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      int ret = super.writeFinal(src);
      this.handleWriteTimeout((long)ret);
      if (!src.hasRemaining() && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return ret;
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      long ret = super.writeFinal(srcs, offset, length);
      this.handleWriteTimeout(ret);
      if (!Buffers.hasRemaining(srcs) && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return ret;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      long ret = super.transferFrom(src, position, count);
      this.handleWriteTimeout(ret);
      return ret;
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      long ret = super.transferFrom(source, count, throughBuffer);
      this.handleWriteTimeout(ret);
      return ret;
   }

   public void awaitWritable() throws IOException {
      Integer timeout = this.getTimeout();
      if (timeout != null && timeout > 0) {
         super.awaitWritable((long)(timeout + 50), TimeUnit.MILLISECONDS);
      } else {
         super.awaitWritable();
      }

   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      Integer timeout = this.getTimeout();
      if (timeout != null && timeout > 0) {
         long millis = timeUnit.toMillis(time);
         super.awaitWritable(Math.min(millis, (long)(timeout + 50)), TimeUnit.MILLISECONDS);
      } else {
         super.awaitWritable(time, timeUnit);
      }

   }

   private Integer getTimeout() {
      Integer timeout = 0;

      try {
         timeout = (Integer)this.connection.getSourceChannel().getOption(Options.WRITE_TIMEOUT);
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

   public void terminateWrites() throws IOException {
      super.terminateWrites();
      if (this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

   }

   public void truncateWrites() throws IOException {
      super.truncateWrites();
      if (this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

   }

   public void resumeWrites() {
      super.resumeWrites();
      this.handleResumeTimeout();
   }

   public void suspendWrites() {
      super.suspendWrites();
      XnioExecutor.Key handle = this.handle;
      if (handle != null) {
         handle.remove();
         this.handle = null;
      }

   }

   public void wakeupWrites() {
      super.wakeupWrites();
      this.handleResumeTimeout();
   }

   private void handleResumeTimeout() {
      Integer timeout = this.getTimeout();
      if (timeout != null && timeout > 0) {
         long currentTime = System.currentTimeMillis();
         this.expireTime = currentTime + (long)timeout;
         XnioExecutor.Key key = this.handle;
         if (key == null) {
            this.handle = this.connection.getIoThread().executeAfter(this.timeoutCommand, (long)timeout, TimeUnit.MILLISECONDS);
         }

      }
   }
}
