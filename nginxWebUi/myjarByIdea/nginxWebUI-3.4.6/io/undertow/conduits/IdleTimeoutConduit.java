package io.undertow.conduits;

import io.undertow.UndertowLogger;
import io.undertow.util.WorkerUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ReadReadyHandler;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.conduits.WriteReadyHandler;

public class IdleTimeoutConduit implements StreamSinkConduit, StreamSourceConduit {
   private static final int DELTA = 100;
   private volatile XnioExecutor.Key handle;
   private volatile long idleTimeout;
   private volatile long expireTime = -1L;
   private volatile boolean timedOut = false;
   private final StreamSinkConduit sink;
   private final StreamSourceConduit source;
   private volatile WriteReadyHandler writeReadyHandler;
   private volatile ReadReadyHandler readReadyHandler;
   private final Runnable timeoutCommand = new Runnable() {
      public void run() {
         IdleTimeoutConduit.this.handle = null;
         if (IdleTimeoutConduit.this.expireTime != -1L) {
            long current = System.currentTimeMillis();
            if (current < IdleTimeoutConduit.this.expireTime) {
               IdleTimeoutConduit.this.handle = WorkerUtils.executeAfter(IdleTimeoutConduit.this.getWriteThread(), IdleTimeoutConduit.this.timeoutCommand, IdleTimeoutConduit.this.expireTime - current + 100L, TimeUnit.MILLISECONDS);
            } else {
               UndertowLogger.REQUEST_LOGGER.trace("Timing out channel due to inactivity");
               IdleTimeoutConduit.this.timedOut = true;
               IdleTimeoutConduit.this.doClose();
               if (IdleTimeoutConduit.this.sink.isWriteResumed() && IdleTimeoutConduit.this.writeReadyHandler != null) {
                  IdleTimeoutConduit.this.writeReadyHandler.writeReady();
               }

               if (IdleTimeoutConduit.this.source.isReadResumed() && IdleTimeoutConduit.this.readReadyHandler != null) {
                  IdleTimeoutConduit.this.readReadyHandler.readReady();
               }

            }
         }
      }
   };

   protected void doClose() {
      safeClose(this.sink);
      safeClose(this.source);
   }

   public IdleTimeoutConduit(StreamConnection connection) {
      this.sink = connection.getSinkChannel().getConduit();
      this.source = connection.getSourceChannel().getConduit();
      this.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler(connection.getSinkChannel()));
      this.setReadReadyHandler(new ReadReadyHandler.ChannelListenerHandler(connection.getSourceChannel()));
   }

   private void handleIdleTimeout() throws ClosedChannelException {
      if (!this.timedOut) {
         long idleTimeout = this.idleTimeout;
         if (idleTimeout > 0L) {
            long currentTime = System.currentTimeMillis();
            long expireTimeVar = this.expireTime;
            if (expireTimeVar != -1L && currentTime > expireTimeVar) {
               this.timedOut = true;
               this.doClose();
               throw new ClosedChannelException();
            } else {
               this.expireTime = currentTime + idleTimeout;
            }
         }
      }
   }

   public int write(ByteBuffer src) throws IOException {
      this.handleIdleTimeout();
      int w = this.sink.write(src);
      return w;
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      this.handleIdleTimeout();
      long w = this.sink.write(srcs, offset, length);
      return w;
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      this.handleIdleTimeout();
      int w = this.sink.writeFinal(src);
      if (this.source.isReadShutdown() && !src.hasRemaining() && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return w;
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      this.handleIdleTimeout();
      long w = this.sink.writeFinal(srcs, offset, length);
      if (this.source.isReadShutdown() && !Buffers.hasRemaining(srcs, offset, length) && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return w;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      this.handleIdleTimeout();
      long w = this.source.transferTo(position, count, target);
      if (this.sink.isWriteShutdown() && w == -1L && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return w;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      this.handleIdleTimeout();
      long w = this.source.transferTo(count, throughBuffer, target);
      if (this.sink.isWriteShutdown() && w == -1L && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return w;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      this.handleIdleTimeout();
      long r = this.source.read(dsts, offset, length);
      if (this.sink.isWriteShutdown() && r == -1L && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return r;
   }

   public int read(ByteBuffer dst) throws IOException {
      this.handleIdleTimeout();
      int r = this.source.read(dst);
      if (this.sink.isWriteShutdown() && r == -1 && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

      return r;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      this.handleIdleTimeout();
      return this.sink.transferFrom(src, position, count);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      this.handleIdleTimeout();
      return this.sink.transferFrom(source, count, throughBuffer);
   }

   public void suspendReads() {
      this.source.suspendReads();
      XnioExecutor.Key handle = this.handle;
      if (handle != null && !this.isWriteResumed()) {
         handle.remove();
         this.handle = null;
      }

   }

   public void terminateReads() throws IOException {
      this.source.terminateReads();
      if (this.sink.isWriteShutdown() && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

   }

   public boolean isReadShutdown() {
      return this.source.isReadShutdown();
   }

   public void resumeReads() {
      this.source.resumeReads();
      this.handleResumeTimeout();
   }

   public boolean isReadResumed() {
      return this.source.isReadResumed();
   }

   public void wakeupReads() {
      this.source.wakeupReads();
      this.handleResumeTimeout();
   }

   public void awaitReadable() throws IOException {
      this.source.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.source.awaitReadable(time, timeUnit);
   }

   public XnioIoThread getReadThread() {
      return this.source.getReadThread();
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      this.readReadyHandler = handler;
      this.source.setReadReadyHandler(handler);
   }

   private static void safeClose(StreamSourceConduit sink) {
      try {
         sink.terminateReads();
      } catch (IOException var2) {
      }

   }

   private static void safeClose(StreamSinkConduit sink) {
      try {
         sink.truncateWrites();
      } catch (IOException var2) {
      }

   }

   public void terminateWrites() throws IOException {
      this.sink.terminateWrites();
      if (this.source.isReadShutdown() && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

   }

   public boolean isWriteShutdown() {
      return this.sink.isWriteShutdown();
   }

   public void resumeWrites() {
      this.sink.resumeWrites();
      this.handleResumeTimeout();
   }

   public void suspendWrites() {
      this.sink.suspendWrites();
      XnioExecutor.Key handle = this.handle;
      if (handle != null && !this.isReadResumed()) {
         handle.remove();
         this.handle = null;
      }

   }

   public void wakeupWrites() {
      this.sink.wakeupWrites();
      this.handleResumeTimeout();
   }

   private void handleResumeTimeout() {
      long timeout = this.getIdleTimeout();
      if (timeout > 0L) {
         long currentTime = System.currentTimeMillis();
         long newExpireTime = currentTime + timeout;
         boolean shorter = newExpireTime < this.expireTime;
         if (shorter && this.handle != null) {
            this.handle.remove();
            this.handle = null;
         }

         this.expireTime = newExpireTime;
         XnioExecutor.Key key = this.handle;
         if (key == null) {
            this.handle = WorkerUtils.executeAfter(this.getWriteThread(), this.timeoutCommand, timeout, TimeUnit.MILLISECONDS);
         }

      }
   }

   public boolean isWriteResumed() {
      return this.sink.isWriteResumed();
   }

   public void awaitWritable() throws IOException {
      this.sink.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      this.sink.awaitWritable();
   }

   public XnioIoThread getWriteThread() {
      return this.sink.getWriteThread();
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      this.writeReadyHandler = handler;
      this.sink.setWriteReadyHandler(handler);
   }

   public void truncateWrites() throws IOException {
      this.sink.truncateWrites();
      if (this.source.isReadShutdown() && this.handle != null) {
         this.handle.remove();
         this.handle = null;
      }

   }

   public boolean flush() throws IOException {
      return this.sink.flush();
   }

   public XnioWorker getWorker() {
      return this.sink.getWorker();
   }

   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(long idleTimeout) {
      this.idleTimeout = idleTimeout;
      if (idleTimeout > 0L) {
         this.expireTime = System.currentTimeMillis() + idleTimeout;
         if (this.isReadResumed() || this.isWriteResumed()) {
            this.handleResumeTimeout();
         }
      } else {
         this.expireTime = -1L;
      }

   }
}
