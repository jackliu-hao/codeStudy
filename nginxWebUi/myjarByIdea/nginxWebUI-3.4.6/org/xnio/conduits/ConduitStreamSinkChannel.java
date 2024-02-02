package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.channels.Configurable;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.channels.WriteListenerSettable;

public final class ConduitStreamSinkChannel implements StreamSinkChannel, WriteListenerSettable<ConduitStreamSinkChannel>, CloseListenerSettable<ConduitStreamSinkChannel>, Cloneable {
   private final Configurable configurable;
   private StreamSinkConduit conduit;
   private ChannelListener<? super ConduitStreamSinkChannel> writeListener;
   private ChannelListener<? super ConduitStreamSinkChannel> closeListener;

   public ConduitStreamSinkChannel(Configurable configurable, StreamSinkConduit conduit) {
      this.configurable = configurable;
      this.conduit = conduit;
      conduit.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler(this));
   }

   public StreamSinkConduit getConduit() {
      return this.conduit;
   }

   public void setConduit(StreamSinkConduit conduit) {
      this.conduit = conduit;
   }

   public ChannelListener<? super ConduitStreamSinkChannel> getWriteListener() {
      return this.writeListener;
   }

   public void setWriteListener(ChannelListener<? super ConduitStreamSinkChannel> writeListener) {
      this.writeListener = writeListener;
   }

   public ChannelListener<? super ConduitStreamSinkChannel> getCloseListener() {
      return this.closeListener;
   }

   public void setCloseListener(ChannelListener<? super ConduitStreamSinkChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener.Setter<ConduitStreamSinkChannel> getWriteSetter() {
      return new WriteListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<ConduitStreamSinkChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.conduit.writeFinal(src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.conduit.writeFinal(srcs, offset, length);
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      return this.conduit.writeFinal(srcs, 0, srcs.length);
   }

   public void suspendWrites() {
      this.conduit.suspendWrites();
   }

   public void resumeWrites() {
      this.conduit.resumeWrites();
   }

   public void wakeupWrites() {
      this.conduit.wakeupWrites();
   }

   public boolean isWriteResumed() {
      return this.conduit.isWriteResumed();
   }

   public void awaitWritable() throws IOException {
      this.conduit.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      this.conduit.awaitWritable(time, timeUnit);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return this.conduit.transferFrom(src, position, count);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return this.conduit.transferFrom(source, count, throughBuffer);
   }

   public int write(ByteBuffer dst) throws IOException {
      return this.conduit.write(dst);
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.conduit.write(srcs, 0, srcs.length);
   }

   public long write(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return this.conduit.write(dsts, offs, len);
   }

   public boolean flush() throws IOException {
      return this.conduit.flush();
   }

   public boolean supportsOption(Option<?> option) {
      return this.configurable.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.configurable.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.configurable.setOption(option, value);
   }

   public void shutdownWrites() throws IOException {
      this.conduit.terminateWrites();
   }

   public boolean isOpen() {
      return !this.conduit.isWriteShutdown();
   }

   public void close() throws IOException {
      this.conduit.truncateWrites();
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getWriteThread() {
      return this.conduit.getWriteThread();
   }

   public XnioIoThread getIoThread() {
      return this.conduit.getWriteThread();
   }

   public XnioWorker getWorker() {
      return this.conduit.getWorker();
   }

   public ConduitStreamSinkChannel clone() {
      try {
         return (ConduitStreamSinkChannel)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException(var2);
      }
   }
}
