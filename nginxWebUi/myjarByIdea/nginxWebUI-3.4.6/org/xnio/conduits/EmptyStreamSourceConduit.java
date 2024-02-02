package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;

public final class EmptyStreamSourceConduit implements StreamSourceConduit {
   private final XnioWorker worker;
   private final XnioIoThread readThread;
   private ReadReadyHandler readReadyHandler;
   private boolean shutdown;
   private boolean resumed;

   public EmptyStreamSourceConduit(XnioIoThread readThread) {
      this.worker = readThread.getWorker();
      this.readThread = readThread;
   }

   public void setReadReadyHandler(ReadReadyHandler handler) {
      this.readReadyHandler = handler;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return 0L;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      this.resumed = false;
      return -1L;
   }

   public int read(ByteBuffer dst) throws IOException {
      this.resumed = false;
      return -1;
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      this.resumed = false;
      return -1L;
   }

   public boolean isReadShutdown() {
      return this.shutdown;
   }

   public void resumeReads() {
      this.resumed = true;
      this.readThread.execute(new Runnable() {
         public void run() {
            ReadReadyHandler handler = EmptyStreamSourceConduit.this.readReadyHandler;
            if (handler != null) {
               handler.readReady();
            }

         }
      });
   }

   public void suspendReads() {
      this.resumed = false;
   }

   public void wakeupReads() {
      this.resumeReads();
   }

   public boolean isReadResumed() {
      return this.resumed;
   }

   public void awaitReadable() throws IOException {
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
   }

   public void terminateReads() throws IOException {
      if (!this.shutdown) {
         this.shutdown = true;
         this.readReadyHandler.terminated();
      }

   }

   public XnioIoThread getReadThread() {
      return this.readThread;
   }

   public XnioWorker getWorker() {
      return this.worker;
   }
}
