package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public final class StreamSourceChannelWrappingConduit implements StreamSourceConduit {
   private final StreamSourceChannel channel;

   public StreamSourceChannelWrappingConduit(StreamSourceChannel channel) {
      this.channel = channel;
   }

   public void terminateReads() throws IOException {
      this.channel.shutdownReads();
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return this.channel.transferTo(position, count, target);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return this.channel.transferTo(count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.channel.read(dst);
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return this.channel.read(dsts, offs, len);
   }

   public boolean isReadShutdown() {
      return !this.channel.isOpen();
   }

   public void resumeReads() {
      this.channel.resumeReads();
   }

   public void suspendReads() {
      this.channel.suspendReads();
   }

   public void wakeupReads() {
      this.channel.wakeupReads();
   }

   public boolean isReadResumed() {
      return this.channel.isReadResumed();
   }

   public void awaitReadable() throws IOException {
      this.channel.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.channel.awaitReadable(time, timeUnit);
   }

   public XnioIoThread getReadThread() {
      return this.channel.getIoThread();
   }

   public void setReadReadyHandler(final ReadReadyHandler handler) {
      this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
         public void handleEvent(StreamSourceChannel channel) {
            handler.readReady();
         }
      });
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }
}
