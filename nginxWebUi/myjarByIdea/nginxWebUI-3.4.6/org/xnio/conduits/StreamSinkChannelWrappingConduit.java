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

public final class StreamSinkChannelWrappingConduit implements StreamSinkConduit {
   private final StreamSinkChannel channel;

   public StreamSinkChannelWrappingConduit(StreamSinkChannel channel) {
      this.channel = channel;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return this.channel.transferFrom(src, position, count);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return this.channel.transferFrom(source, count, throughBuffer);
   }

   public int write(ByteBuffer src) throws IOException {
      return this.channel.write(src);
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return this.channel.write(srcs, offs, len);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.channel.writeFinal(src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.channel.writeFinal(srcs, offset, length);
   }

   public void terminateWrites() throws IOException {
      this.channel.shutdownWrites();
   }

   public boolean isWriteShutdown() {
      return !this.channel.isOpen();
   }

   public void resumeWrites() {
      this.channel.resumeWrites();
   }

   public void suspendWrites() {
      this.channel.suspendWrites();
   }

   public void wakeupWrites() {
      this.channel.wakeupWrites();
   }

   public boolean isWriteResumed() {
      return this.channel.isWriteResumed();
   }

   public void awaitWritable() throws IOException {
      this.channel.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      this.channel.awaitWritable(time, timeUnit);
   }

   public XnioIoThread getWriteThread() {
      return this.channel.getIoThread();
   }

   public void setWriteReadyHandler(final WriteReadyHandler handler) {
      this.channel.getWriteSetter().set(new ChannelListener<StreamSinkChannel>() {
         public void handleEvent(StreamSinkChannel channel) {
            handler.writeReady();
         }
      });
   }

   public void truncateWrites() throws IOException {
      this.channel.close();
   }

   public boolean flush() throws IOException {
      return this.channel.flush();
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }
}
