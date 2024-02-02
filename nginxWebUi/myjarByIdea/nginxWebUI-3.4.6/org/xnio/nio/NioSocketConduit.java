package org.xnio.nio;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;
import org.xnio.Xnio;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.channels.WriteTimeoutException;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.ReadReadyHandler;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.conduits.WriteReadyHandler;

final class NioSocketConduit extends NioHandle implements StreamSourceConduit, StreamSinkConduit {
   private final SocketChannel socketChannel;
   private final NioSocketStreamConnection connection;
   private ReadReadyHandler readReadyHandler;
   private WriteReadyHandler writeReadyHandler;
   private volatile int readTimeout;
   private long lastRead;
   private static final AtomicIntegerFieldUpdater<NioSocketConduit> readTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioSocketConduit.class, "readTimeout");
   private volatile int writeTimeout;
   private long lastWrite;
   private static final AtomicIntegerFieldUpdater<NioSocketConduit> writeTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioSocketConduit.class, "writeTimeout");

   NioSocketConduit(WorkerThread workerThread, SelectionKey selectionKey, NioSocketStreamConnection connection) {
      super(workerThread, selectionKey);
      this.connection = connection;
      this.socketChannel = (SocketChannel)selectionKey.channel();
   }

   void handleReady(int ops) {
      try {
         if (ops == 0) {
            SelectionKey key = this.getSelectionKey();
            int interestOps = key.interestOps();
            if (interestOps == 0) {
               this.forceTermination();
               return;
            }

            ops = interestOps;
         }

         if (Bits.allAreSet(ops, 1)) {
            try {
               if (this.isReadShutdown()) {
                  this.suspendReads();
               }

               this.readReadyHandler.readReady();
            } catch (Throwable var5) {
            }
         }

         if (Bits.allAreSet(ops, 4)) {
            try {
               if (this.isWriteShutdown()) {
                  this.suspendWrites();
               }

               this.writeReadyHandler.writeReady();
            } catch (Throwable var4) {
            }
         }
      } catch (CancelledKeyException var6) {
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

      WriteReadyHandler write = this.writeReadyHandler;
      if (write != null) {
         write.forceTermination();
      }

   }

   void terminated() {
      ReadReadyHandler read = this.readReadyHandler;
      if (read != null) {
         read.terminated();
      }

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
            long lastRead = this.lastWrite;
            if (lastRead > 0L && (System.nanoTime() - lastRead) / 1000000L > (long)timeout) {
               throw Log.log.writeTimeout();
            }
         }
      }

   }

   public final long transferFrom(FileChannel src, long position, long count) throws IOException {
      long res = src.transferTo(position, count, this.socketChannel);
      this.checkWriteTimeout(res > 0L);
      return res;
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, (StreamSinkConduit)this);
   }

   public int write(ByteBuffer src) throws IOException {
      int res = this.socketChannel.write(src);
      this.checkWriteTimeout(res > 0);
      return res;
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (length == 1) {
         return (long)this.write(srcs[offset]);
      } else {
         long res = this.socketChannel.write(srcs, offset, length);
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
            if (this.getSelectionKey().isValid()) {
               this.suspend(4);
            }

            if (this.socketChannel.isOpen()) {
               try {
                  this.socketChannel.socket().shutdownOutput();
               } catch (SocketException var6) {
               }
            }
         } catch (ClosedChannelException var7) {
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
      if (!this.isWriteShutdown()) {
         SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.socketChannel, 4);
      }
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      Xnio.checkBlockingAllowed();
      if (!this.isWriteShutdown()) {
         SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.socketChannel, 4, time, timeUnit);
      }
   }

   public XnioIoThread getWriteThread() {
      return this.getWorkerThread();
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      this.writeReadyHandler = handler;
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
      long res = target.transferFrom(this.socketChannel, position, count);
      this.checkReadTimeout(res > 0L);
      return res;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return Conduits.transfer((StreamSourceConduit)this, count, throughBuffer, (WritableByteChannel)target);
   }

   public int read(ByteBuffer dst) throws IOException {
      int res;
      try {
         res = this.socketChannel.read(dst);
      } catch (ClosedChannelException var4) {
         return -1;
      }

      if (res != -1) {
         this.checkReadTimeout(res > 0);
      } else {
         this.terminateReads();
      }

      return res;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      if (length == 1) {
         return (long)this.read(dsts[offset]);
      } else {
         long res;
         try {
            res = this.socketChannel.read(dsts, offset, length);
         } catch (ClosedChannelException var7) {
            return -1L;
         }

         if (res != -1L) {
            this.checkReadTimeout(res > 0L);
         } else {
            this.terminateReads();
         }

         return res;
      }
   }

   public void terminateReads() throws IOException {
      if (this.connection.readClosed()) {
         try {
            if (this.getSelectionKey().isValid()) {
               this.suspend(1);
            }

            if (this.socketChannel.isOpen()) {
               try {
                  this.socketChannel.socket().shutdownInput();
               } catch (SocketException var6) {
               }
            }
         } catch (ClosedChannelException var7) {
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
      SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.socketChannel, 1);
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      Xnio.checkBlockingAllowed();
      SelectorUtils.await((NioXnio)this.getWorker().getXnio(), this.socketChannel, 1, time, timeUnit);
   }

   public XnioIoThread getReadThread() {
      return this.getWorkerThread();
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      this.readReadyHandler = handler;
   }

   SocketChannel getSocketChannel() {
      return this.socketChannel;
   }
}
