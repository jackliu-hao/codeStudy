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
import org.xnio.channels.WritableMessageChannel;
import org.xnio.channels.WriteListenerSettable;

public final class ConduitWritableMessageChannel implements WritableMessageChannel, WriteListenerSettable<ConduitWritableMessageChannel>, CloseListenerSettable<ConduitWritableMessageChannel>, Cloneable {
   private final Configurable configurable;
   private MessageSinkConduit conduit;
   private ChannelListener<? super ConduitWritableMessageChannel> writeListener;
   private ChannelListener<? super ConduitWritableMessageChannel> closeListener;

   public ConduitWritableMessageChannel(Configurable configurable, MessageSinkConduit conduit) {
      this.configurable = configurable;
      this.conduit = conduit;
      conduit.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler(this));
   }

   public MessageSinkConduit getConduit() {
      return this.conduit;
   }

   public void setConduit(MessageSinkConduit conduit) {
      this.conduit = conduit;
   }

   public ChannelListener<? super ConduitWritableMessageChannel> getWriteListener() {
      return this.writeListener;
   }

   public void setWriteListener(ChannelListener<? super ConduitWritableMessageChannel> writeListener) {
      this.writeListener = writeListener;
   }

   public ChannelListener<? super ConduitWritableMessageChannel> getCloseListener() {
      return this.closeListener;
   }

   public void setCloseListener(ChannelListener<? super ConduitWritableMessageChannel> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener.Setter<ConduitWritableMessageChannel> getWriteSetter() {
      return new WriteListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<ConduitWritableMessageChannel> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
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

   public boolean send(ByteBuffer dst) throws IOException {
      return this.conduit.send(dst);
   }

   public boolean send(ByteBuffer[] srcs) throws IOException {
      return this.conduit.send(srcs, 0, srcs.length);
   }

   public boolean send(ByteBuffer[] dsts, int offs, int len) throws IOException {
      return this.conduit.send(dsts, offs, len);
   }

   public boolean sendFinal(ByteBuffer buffer) throws IOException {
      return this.conduit.sendFinal(buffer);
   }

   public boolean sendFinal(ByteBuffer[] buffers) throws IOException {
      return this.conduit.sendFinal(buffers, 0, buffers.length);
   }

   public boolean sendFinal(ByteBuffer[] buffers, int offs, int len) throws IOException {
      return this.conduit.sendFinal(buffers, offs, len);
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

   public ConduitWritableMessageChannel clone() {
      try {
         return (ConduitWritableMessageChannel)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException(var2);
      }
   }
}
