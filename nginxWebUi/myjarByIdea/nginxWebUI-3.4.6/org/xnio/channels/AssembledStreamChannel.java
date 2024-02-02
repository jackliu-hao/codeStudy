package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;

public class AssembledStreamChannel implements StreamChannel {
   private final CloseableChannel closeable;
   private final StreamSourceChannel source;
   private final StreamSinkChannel sink;
   private final ChannelListener.Setter<AssembledStreamChannel> readSetter;
   private final ChannelListener.Setter<AssembledStreamChannel> writeSetter;
   private final ChannelListener.Setter<AssembledStreamChannel> closeSetter;

   public AssembledStreamChannel(CloseableChannel closeable, StreamSourceChannel source, StreamSinkChannel sink) {
      if (source.getWorker() == sink.getWorker() && source.getWorker() == closeable.getWorker()) {
         this.closeable = closeable;
         this.source = source;
         this.sink = sink;
         this.readSetter = ChannelListeners.getDelegatingSetter(source.getReadSetter(), this);
         this.writeSetter = ChannelListeners.getDelegatingSetter(sink.getWriteSetter(), this);
         this.closeSetter = ChannelListeners.getDelegatingSetter(closeable.getCloseSetter(), this);
      } else {
         throw Messages.msg.differentWorkers();
      }
   }

   public AssembledStreamChannel(StreamSourceChannel source, StreamSinkChannel sink) {
      this(new AssembledChannel(source, sink), source, sink);
   }

   public ChannelListener.Setter<? extends AssembledStreamChannel> getReadSetter() {
      return this.readSetter;
   }

   public void suspendReads() {
      this.source.suspendReads();
   }

   public void resumeReads() {
      this.source.resumeReads();
   }

   public boolean isReadResumed() {
      return this.source.isReadResumed();
   }

   public void wakeupReads() {
      this.source.wakeupReads();
   }

   public void shutdownReads() throws IOException {
      this.source.shutdownReads();
   }

   public void awaitReadable() throws IOException {
      this.source.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.source.awaitReadable(time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.source.getReadThread();
   }

   public XnioIoThread getIoThread() {
      return this.source.getIoThread();
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.source.read(dst);
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return this.source.read(dsts, offset, length);
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.source.read(dsts);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return this.source.transferTo(position, count, target);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return this.source.transferTo(count, throughBuffer, target);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return this.sink.transferFrom(src, position, count);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return this.sink.transferFrom(source, count, throughBuffer);
   }

   public ChannelListener.Setter<? extends AssembledStreamChannel> getWriteSetter() {
      return this.writeSetter;
   }

   public int write(ByteBuffer src) throws IOException {
      return this.sink.write(src);
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.sink.write(srcs, offset, length);
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.sink.write(srcs);
   }

   public void suspendWrites() {
      this.sink.suspendWrites();
   }

   public void resumeWrites() {
      this.sink.resumeWrites();
   }

   public boolean isWriteResumed() {
      return this.sink.isWriteResumed();
   }

   public void wakeupWrites() {
      this.sink.wakeupWrites();
   }

   public void shutdownWrites() throws IOException {
      this.sink.shutdownWrites();
   }

   public void awaitWritable() throws IOException {
      this.sink.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      this.sink.awaitWritable(time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getWriteThread() {
      return this.sink.getWriteThread();
   }

   public boolean flush() throws IOException {
      return this.sink.flush();
   }

   public ChannelListener.Setter<? extends AssembledStreamChannel> getCloseSetter() {
      return this.closeSetter;
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.sink.writeFinal(src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.sink.writeFinal(srcs, offset, length);
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      return this.sink.writeFinal(srcs);
   }

   public XnioWorker getWorker() {
      return this.closeable.getWorker();
   }

   public void close() throws IOException {
      this.closeable.close();
   }

   public boolean isOpen() {
      return this.closeable.isOpen();
   }

   public boolean supportsOption(Option<?> option) {
      return this.closeable.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.closeable.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.closeable.setOption(option, value);
   }
}
