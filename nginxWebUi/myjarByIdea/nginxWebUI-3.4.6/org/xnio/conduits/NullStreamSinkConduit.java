package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSourceChannel;

public final class NullStreamSinkConduit implements StreamSinkConduit {
   private final XnioWorker worker;
   private final XnioIoThread writeThread;
   private WriteReadyHandler writeReadyHandler;
   private boolean shutdown;
   private boolean resumed;

   public NullStreamSinkConduit(XnioIoThread writeThread) {
      this.worker = writeThread.getWorker();
      this.writeThread = writeThread;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return Channels.drain(src, position, count);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      throughBuffer.limit(0);
      return Channels.drain(source, count);
   }

   public int write(ByteBuffer src) throws IOException {
      int var2;
      try {
         var2 = src.remaining();
      } finally {
         src.position(src.limit());
      }

      return var2;
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      long t = 0L;

      for(int i = 0; i < len; ++i) {
         t += (long)this.write(srcs[i + offs]);
      }

      return t;
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

   public boolean isWriteShutdown() {
      return this.shutdown;
   }

   public void suspendWrites() {
      this.resumed = false;
   }

   public void resumeWrites() {
      this.resumed = true;
      WriteReadyHandler handler = this.writeReadyHandler;
      this.writeThread.execute(new WriteReadyHandler.ReadyTask(handler));
   }

   public void wakeupWrites() {
      this.resumeWrites();
   }

   public boolean isWriteResumed() {
      return this.resumed;
   }

   public void awaitWritable() throws IOException {
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
   }

   public XnioIoThread getWriteThread() {
      return this.writeThread;
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      this.writeReadyHandler = handler;
   }

   public void truncateWrites() throws IOException {
      this.terminateWrites();
   }

   public void terminateWrites() throws IOException {
      if (!this.shutdown) {
         this.shutdown = true;
         this.writeReadyHandler.terminated();
      }

   }

   public XnioWorker getWorker() {
      return this.worker;
   }
}
