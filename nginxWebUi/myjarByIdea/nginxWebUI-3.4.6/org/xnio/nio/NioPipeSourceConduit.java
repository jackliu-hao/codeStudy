package org.xnio.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Xnio;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.ReadReadyHandler;
import org.xnio.conduits.StreamSourceConduit;

final class NioPipeSourceConduit extends NioHandle implements StreamSourceConduit {
   private final Pipe.SourceChannel sourceChannel;
   private final NioPipeStreamConnection connection;
   private ReadReadyHandler readReadyHandler;
   private volatile int readTimeout;
   private long lastRead;
   private static final AtomicIntegerFieldUpdater<NioPipeSourceConduit> readTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioPipeSourceConduit.class, "readTimeout");

   NioPipeSourceConduit(WorkerThread workerThread, SelectionKey selectionKey, NioPipeStreamConnection connection) {
      super(workerThread, selectionKey);
      this.connection = connection;
      this.sourceChannel = (Pipe.SourceChannel)selectionKey.channel();
   }

   void handleReady(int ops) {
      try {
         this.readReadyHandler.readReady();
      } catch (CancelledKeyException var3) {
      }

   }

   public XnioWorker getWorker() {
      return this.getWorkerThread().getWorker();
   }

   void forceTermination() {
      ReadReadyHandler read = this.readReadyHandler;
      if (read != null) {
         read.forceTermination();
      }

   }

   void terminated() {
      ReadReadyHandler read = this.readReadyHandler;
      if (read != null) {
         read.terminated();
      }

   }

   int getAndSetReadTimeout(int newVal) {
      return readTimeoutUpdater.getAndSet(this, newVal);
   }

   int getReadTimeout() {
      return this.readTimeout;
   }

   private void checkReadTimeout(boolean xfer) throws ReadTimeoutException {
      int timeout = this.readTimeout;
      if (timeout > 0) {
         if (xfer) {
            this.lastRead = System.nanoTime();
         } else {
            long lastRead = this.lastRead;
            if (lastRead > 0L && (System.nanoTime() - lastRead) / 1000000L > (long)timeout) {
               throw Log.log.readTimeout();
            }
         }
      }

   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      long res = target.transferFrom(this.sourceChannel, position, count);
      this.checkReadTimeout(res > 0L);
      return res;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return Conduits.transfer((StreamSourceConduit)this, count, throughBuffer, (WritableByteChannel)target);
   }

   public int read(ByteBuffer dst) throws IOException {
      int res;
      try {
         res = this.sourceChannel.read(dst);
      } catch (ClosedChannelException var4) {
         return -1;
      }

      if (res != -1) {
         this.checkReadTimeout(res > 0);
      }

      return res;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      if (length == 1) {
         return (long)this.read(dsts[offset]);
      } else {
         long res;
         try {
            res = this.sourceChannel.read(dsts, offset, length);
         } catch (ClosedChannelException var7) {
            return -1L;
         }

         if (res != -1L) {
            this.checkReadTimeout(res > 0L);
         }

         return res;
      }
   }

   public void terminateReads() throws IOException {
      if (this.connection.readClosed()) {
         try {
            this.sourceChannel.close();
         } catch (ClosedChannelException var5) {
         } finally {
            this.readTerminated();
         }
      }

   }

   void readTerminated() {
      ReadReadyHandler readReadyHandler = this.readReadyHandler;
      if (readReadyHandler != null) {
         try {
            readReadyHandler.terminated();
         } catch (Throwable var3) {
         }
      }

   }

   public boolean isReadShutdown() {
      return this.connection.isReadShutdown();
   }

   public void resumeReads() {
      this.resume(1);
   }

   public void suspendReads() {
      this.suspend(1);
   }

   public void wakeupReads() {
      this.wakeup(1);
   }

   public boolean isReadResumed() {
      return this.isResumed(1);
   }

   public void awaitReadable() throws IOException {
      Xnio.checkBlockingAllowed();
      SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.sourceChannel, 1);
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      Xnio.checkBlockingAllowed();
      SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.sourceChannel, 1, time, timeUnit);
   }

   public XnioIoThread getReadThread() {
      return this.getWorkerThread();
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      this.readReadyHandler = handler;
   }
}
