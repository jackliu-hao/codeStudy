package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;

public class AssembledMessageChannel implements MessageChannel {
   private final CloseableChannel closeable;
   private final ReadableMessageChannel readable;
   private final WritableMessageChannel writable;
   private final ChannelListener.Setter<AssembledMessageChannel> readSetter;
   private final ChannelListener.Setter<AssembledMessageChannel> writeSetter;
   private final ChannelListener.Setter<AssembledMessageChannel> closeSetter;

   public AssembledMessageChannel(CloseableChannel closeable, ReadableMessageChannel readable, WritableMessageChannel writable) {
      if (readable.getWorker() == writable.getWorker() && readable.getWorker() == closeable.getWorker()) {
         this.closeable = closeable;
         this.readable = readable;
         this.writable = writable;
         this.readSetter = ChannelListeners.getDelegatingSetter(readable.getReadSetter(), this);
         this.writeSetter = ChannelListeners.getDelegatingSetter(writable.getWriteSetter(), this);
         this.closeSetter = ChannelListeners.getDelegatingSetter(closeable.getCloseSetter(), this);
      } else {
         throw Messages.msg.differentWorkers();
      }
   }

   public AssembledMessageChannel(ReadableMessageChannel readable, WritableMessageChannel writable) {
      this(new AssembledChannel(readable, writable), readable, writable);
   }

   public XnioIoThread getIoThread() {
      return this.readable.getIoThread();
   }

   public ChannelListener.Setter<? extends AssembledMessageChannel> getReadSetter() {
      return this.readSetter;
   }

   public void suspendReads() {
      this.readable.suspendReads();
   }

   public void resumeReads() {
      this.readable.resumeReads();
   }

   public boolean isReadResumed() {
      return this.readable.isReadResumed();
   }

   public void wakeupReads() {
      this.readable.wakeupReads();
   }

   public void shutdownReads() throws IOException {
      this.readable.shutdownReads();
   }

   public void awaitReadable() throws IOException {
      this.readable.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.readable.awaitReadable(time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.readable.getReadThread();
   }

   public int receive(ByteBuffer buffer) throws IOException {
      return this.readable.receive(buffer);
   }

   public long receive(ByteBuffer[] buffers) throws IOException {
      return this.readable.receive(buffers);
   }

   public long receive(ByteBuffer[] buffers, int offs, int len) throws IOException {
      return this.readable.receive(buffers, offs, len);
   }

   public ChannelListener.Setter<? extends AssembledMessageChannel> getWriteSetter() {
      return this.writeSetter;
   }

   public void suspendWrites() {
      this.writable.suspendWrites();
   }

   public void resumeWrites() {
      this.writable.resumeWrites();
   }

   public boolean isWriteResumed() {
      return this.writable.isWriteResumed();
   }

   public void wakeupWrites() {
      this.writable.wakeupWrites();
   }

   public void shutdownWrites() throws IOException {
      this.writable.shutdownWrites();
   }

   public void awaitWritable() throws IOException {
      this.writable.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      this.writable.awaitWritable(time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getWriteThread() {
      return this.writable.getWriteThread();
   }

   public boolean send(ByteBuffer buffer) throws IOException {
      return this.writable.send(buffer);
   }

   public boolean send(ByteBuffer[] buffers) throws IOException {
      return this.writable.send(buffers);
   }

   public boolean send(ByteBuffer[] buffers, int offs, int len) throws IOException {
      return this.writable.send(buffers, offs, len);
   }

   public boolean sendFinal(ByteBuffer buffer) throws IOException {
      return this.writable.sendFinal(buffer);
   }

   public boolean sendFinal(ByteBuffer[] buffers) throws IOException {
      return this.writable.sendFinal(buffers);
   }

   public boolean sendFinal(ByteBuffer[] buffers, int offs, int len) throws IOException {
      return this.writable.sendFinal(buffers, offs, len);
   }

   public boolean flush() throws IOException {
      return this.writable.flush();
   }

   public ChannelListener.Setter<? extends AssembledMessageChannel> getCloseSetter() {
      return this.closeSetter;
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
