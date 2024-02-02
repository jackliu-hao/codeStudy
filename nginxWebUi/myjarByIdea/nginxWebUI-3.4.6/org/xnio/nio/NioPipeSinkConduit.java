package org.xnio.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Xnio;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.channels.WriteTimeoutException;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.WriteReadyHandler;

final class NioPipeSinkConduit extends NioHandle implements StreamSinkConduit {
   private final Pipe.SinkChannel sinkChannel;
   private final NioPipeStreamConnection connection;
   private WriteReadyHandler writeReadyHandler;
   private volatile int writeTimeout;
   private long lastWrite;
   private static final AtomicIntegerFieldUpdater<NioPipeSinkConduit> writeTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioPipeSinkConduit.class, "writeTimeout");

   NioPipeSinkConduit(WorkerThread workerThread, SelectionKey selectionKey, NioPipeStreamConnection connection) {
      super(workerThread, selectionKey);
      this.connection = connection;
      this.sinkChannel = (Pipe.SinkChannel)selectionKey.channel();
   }

   void handleReady(int ops) {
      try {
         this.writeReadyHandler.writeReady();
      } catch (Throwable var3) {
      }

   }

   public XnioWorker getWorker() {
      return this.getWorkerThread().getWorker();
   }

   void forceTermination() {
      WriteReadyHandler write = this.writeReadyHandler;
      if (write != null) {
         write.forceTermination();
      }

   }

   void terminated() {
      WriteReadyHandler write = this.writeReadyHandler;
      if (write != null) {
         write.terminated();
      }

   }

   int getAndSetWriteTimeout(int newVal) {
      return writeTimeoutUpdater.getAndSet(this, newVal);
   }

   int getWriteTimeout() {
      return this.writeTimeout;
   }

   private void checkWriteTimeout(boolean xfer) throws WriteTimeoutException {
      int timeout = this.writeTimeout;
      if (timeout > 0) {
         if (xfer) {
            this.lastWrite = System.nanoTime();
         } else {
            long lastWrite = this.lastWrite;
            if (lastWrite > 0L && (System.nanoTime() - lastWrite) / 1000000L > (long)timeout) {
               throw Log.log.writeTimeout();
            }
         }
      }

   }

   public final long transferFrom(FileChannel src, long position, long count) throws IOException {
      long res = src.transferTo(position, count, this.sinkChannel);
      this.checkWriteTimeout(res > 0L);
      return res;
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, (StreamSinkConduit)this);
   }

   public int write(ByteBuffer src) throws IOException {
      int res = this.sinkChannel.write(src);
      this.checkWriteTimeout(res > 0);
      return res;
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (length == 1) {
         return (long)this.write(srcs[offset]);
      } else {
         long res = this.sinkChannel.write(srcs, offset, length);
         this.checkWriteTimeout(res > 0L);
         return res;
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public boolean flush() throws IOException {
      return true;
   }

   public void terminateWrites() throws IOException {
      if (this.connection.writeClosed()) {
         try {
            this.sinkChannel.close();
         } catch (ClosedChannelException var5) {
         } finally {
            this.writeTerminated();
         }
      }

   }

   public void truncateWrites() throws IOException {
      this.terminateWrites();
   }

   void writeTerminated() {
      WriteReadyHandler writeReadyHandler = this.writeReadyHandler;
      if (writeReadyHandler != null) {
         try {
            writeReadyHandler.terminated();
         } catch (Throwable var3) {
         }
      }

   }

   public boolean isWriteShutdown() {
      return this.connection.isWriteShutdown();
   }

   public void resumeWrites() {
      this.resume(4);
   }

   public void suspendWrites() {
      this.suspend(4);
   }

   public void wakeupWrites() {
      this.wakeup(4);
   }

   public boolean isWriteResumed() {
      return this.isResumed(4);
   }

   public void awaitWritable() throws IOException {
      Xnio.checkBlockingAllowed();
      SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.sinkChannel, 4);
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      Xnio.checkBlockingAllowed();
      SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.sinkChannel, 4, time, timeUnit);
   }

   public XnioIoThread getWriteThread() {
      return this.getWorkerThread();
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      this.writeReadyHandler = handler;
   }
}
