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
import org.xnio.channels.ReadListenerSettable;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public final class ConduitStreamSourceChannel implements StreamSourceChannel, ReadListenerSettable<ConduitStreamSourceChannel>, CloseListenerSettable<ConduitStreamSourceChannel>, Cloneable {
   private final Configurable configurable;
   private StreamSourceConduit conduit;
   private ChannelListener<? super ConduitStreamSourceChannel> readListener;
   private ChannelListener<? super ConduitStreamSourceChannel> closeListener;

   public ConduitStreamSourceChannel(Configurable configurable, StreamSourceConduit conduit) {
      this.configurable = configurable;
      this.conduit = conduit;
      conduit.setReadReadyHandler(new ReadReadyHandler.ChannelListenerHandler(this));
   }

   public StreamSourceConduit getConduit() {
      return this.conduit;
   }

   public void setConduit(StreamSourceConduit conduit) {
      this.conduit = conduit;
   }

   public boolean isOpen() {
      return !this.conduit.isReadShutdown();
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return this.conduit.transferTo(position, count, target);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return this.conduit.transferTo(count, throughBuffer, target);
   }

   public void setReadListener(ChannelListener<? super ConduitStreamSourceChannel> readListener) {
      this.readListener = readListener;
   }

   public ChannelListener<? super ConduitStreamSourceChannel> getReadListener() {
      return this.readListener;
   }

   public void setCloseListener(ChannelListener<? super ConduitStreamSourceChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super ConduitStreamSourceChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<ConduitStreamSourceChannel> getReadSetter() {
      return new ReadListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<ConduitStreamSourceChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public XnioWorker getWorker() {
      return this.conduit.getWorker();
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return this.conduit.read(dsts, offset, length);
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.conduit.read(dsts, 0, dsts.length);
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.conduit.read(dst);
   }

   public void suspendReads() {
      this.conduit.suspendReads();
   }

   public void resumeReads() {
      this.conduit.resumeReads();
   }

   public boolean isReadResumed() {
      return this.conduit.isReadResumed();
   }

   public void wakeupReads() {
      this.conduit.wakeupReads();
   }

   public void shutdownReads() throws IOException {
      this.conduit.terminateReads();
   }

   public void awaitReadable() throws IOException {
      this.conduit.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.conduit.awaitReadable(time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.conduit.getReadThread();
   }

   public XnioIoThread getIoThread() {
      return this.conduit.getReadThread();
   }

   public void close() throws IOException {
      this.conduit.terminateReads();
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

   public ConduitStreamSourceChannel clone() {
      try {
         return (ConduitStreamSourceChannel)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException(var2);
      }
   }
}
