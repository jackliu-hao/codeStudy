package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.channels.Configurable;
import org.xnio.channels.ReadListenerSettable;
import org.xnio.channels.ReadableMessageChannel;

public final class ConduitReadableMessageChannel implements ReadableMessageChannel, ReadListenerSettable<ConduitReadableMessageChannel>, CloseListenerSettable<ConduitReadableMessageChannel>, Cloneable {
   private final Configurable configurable;
   private MessageSourceConduit conduit;
   private ChannelListener<? super ConduitReadableMessageChannel> readListener;
   private ChannelListener<? super ConduitReadableMessageChannel> closeListener;

   public ConduitReadableMessageChannel(Configurable configurable, MessageSourceConduit conduit) {
      this.configurable = configurable;
      this.conduit = conduit;
      conduit.setReadReadyHandler(new ReadReadyHandler.ChannelListenerHandler(this));
   }

   public MessageSourceConduit getConduit() {
      return this.conduit;
   }

   public void setConduit(MessageSourceConduit conduit) {
      this.conduit = conduit;
   }

   public boolean isOpen() {
      return !this.conduit.isReadShutdown();
   }

   public void setReadListener(ChannelListener<? super ConduitReadableMessageChannel> readListener) {
      this.readListener = readListener;
   }

   public ChannelListener<? super ConduitReadableMessageChannel> getReadListener() {
      return this.readListener;
   }

   public void setCloseListener(ChannelListener<? super ConduitReadableMessageChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super ConduitReadableMessageChannel> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<ConduitReadableMessageChannel> getReadSetter() {
      return new ReadListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<ConduitReadableMessageChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public XnioWorker getWorker() {
      return this.conduit.getWorker();
   }

   public long receive(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return this.conduit.receive(dsts, offset, length);
   }

   public long receive(ByteBuffer[] dsts) throws IOException {
      return this.conduit.receive(dsts, 0, dsts.length);
   }

   public int receive(ByteBuffer dst) throws IOException {
      return this.conduit.receive(dst);
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

   public ConduitReadableMessageChannel clone() {
      try {
         return (ConduitReadableMessageChannel)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException(var2);
      }
   }
}
