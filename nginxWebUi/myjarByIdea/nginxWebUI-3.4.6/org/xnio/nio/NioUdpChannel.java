package org.xnio.nio;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectionKey;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.Options;
import org.xnio.XnioExecutor;
import org.xnio.channels.MulticastMessageChannel;
import org.xnio.channels.ReadListenerSettable;
import org.xnio.channels.SocketAddressBuffer;
import org.xnio.channels.UnsupportedOptionException;
import org.xnio.channels.WriteListenerSettable;

class NioUdpChannel extends AbstractNioChannel<NioUdpChannel> implements MulticastMessageChannel, ReadListenerSettable<NioUdpChannel>, WriteListenerSettable<NioUdpChannel> {
   private final NioUdpChannelHandle handle;
   private ChannelListener<? super NioUdpChannel> readListener;
   private ChannelListener<? super NioUdpChannel> writeListener;
   private final DatagramChannel datagramChannel;
   private final AtomicBoolean callFlag = new AtomicBoolean(false);
   private static final Set<Option<?>> OPTIONS;

   NioUdpChannel(NioXnioWorker worker, DatagramChannel datagramChannel) throws ClosedChannelException {
      super(worker);
      this.datagramChannel = datagramChannel;
      WorkerThread workerThread = worker.chooseThread();
      SelectionKey key = workerThread.registerChannel(datagramChannel);
      this.handle = new NioUdpChannelHandle(workerThread, key, this);
      key.attach(this.handle);
   }

   public SocketAddress getLocalAddress() {
      return this.datagramChannel.socket().getLocalSocketAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return type.isInstance(this.getLocalAddress()) ? (SocketAddress)type.cast(this.getLocalAddress()) : null;
   }

   public int receiveFrom(SocketAddressBuffer addressBuffer, ByteBuffer buffer) throws IOException {
      int o = buffer.remaining();

      SocketAddress sourceAddress;
      try {
         sourceAddress = this.datagramChannel.receive(buffer);
      } catch (ClosedChannelException var6) {
         return -1;
      }

      if (sourceAddress == null) {
         return 0;
      } else {
         int t = o - buffer.remaining();
         if (addressBuffer != null) {
            addressBuffer.setSourceAddress(sourceAddress);
            addressBuffer.setDestinationAddress((SocketAddress)null);
         }

         return t;
      }
   }

   public long receiveFrom(SocketAddressBuffer addressBuffer, ByteBuffer[] buffers) throws IOException {
      return this.receiveFrom(addressBuffer, buffers, 0, buffers.length);
   }

   public long receiveFrom(SocketAddressBuffer addressBuffer, ByteBuffer[] buffers, int offs, int len) throws IOException {
      if (len == 0) {
         return 0L;
      } else if (len == 1) {
         return (long)this.receiveFrom(addressBuffer, buffers[offs]);
      } else {
         int o = (int)Math.min(Buffers.remaining(buffers, offs, len), 65536L);
         ByteBuffer buffer = ByteBuffer.allocate(o);

         SocketAddress sourceAddress;
         try {
            sourceAddress = this.datagramChannel.receive(buffer);
         } catch (ClosedChannelException var9) {
            return -1L;
         }

         if (sourceAddress == null) {
            return 0L;
         } else {
            int t = o - buffer.remaining();
            buffer.flip();
            Buffers.copy(buffers, offs, len, buffer);
            if (addressBuffer != null) {
               addressBuffer.setSourceAddress(sourceAddress);
               addressBuffer.setDestinationAddress((SocketAddress)null);
            }

            return (long)t;
         }
      }
   }

   public boolean sendTo(SocketAddress target, ByteBuffer buffer) throws IOException {
      return this.datagramChannel.send(buffer, target) != 0;
   }

   public boolean sendTo(SocketAddress target, ByteBuffer[] buffers) throws IOException {
      return this.sendTo(target, buffers, 0, buffers.length);
   }

   public boolean sendTo(SocketAddress target, ByteBuffer[] buffers, int offset, int length) throws IOException {
      if (length == 0) {
         return false;
      } else if (length == 1) {
         return this.sendTo(target, buffers[offset]);
      } else {
         long o = Buffers.remaining(buffers, offset, length);
         if (o > 65535L) {
            throw Log.log.bufferTooLarge();
         } else {
            ByteBuffer buffer = ByteBuffer.allocate((int)o);
            Buffers.copy(buffer, buffers, offset, length);
            buffer.flip();
            return this.datagramChannel.send(buffer, target) != 0;
         }
      }
   }

   public ChannelListener<? super NioUdpChannel> getReadListener() {
      return this.readListener;
   }

   public void setReadListener(ChannelListener<? super NioUdpChannel> readListener) {
      this.readListener = readListener;
   }

   public ChannelListener<? super NioUdpChannel> getWriteListener() {
      return this.writeListener;
   }

   public void setWriteListener(ChannelListener<? super NioUdpChannel> writeListener) {
      this.writeListener = writeListener;
   }

   public ChannelListener.Setter<NioUdpChannel> getReadSetter() {
      return new ReadListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<NioUdpChannel> getWriteSetter() {
      return new WriteListenerSettable.Setter(this);
   }

   public boolean flush() throws IOException {
      return true;
   }

   public boolean isOpen() {
      return this.datagramChannel.isOpen();
   }

   public void close() throws IOException {
      if (!this.callFlag.getAndSet(true)) {
         Log.udpServerChannelLog.tracef("Closing %s", this);

         try {
            this.cancelKeys();
         } catch (Throwable var6) {
         }

         try {
            this.datagramChannel.close();
         } finally {
            this.invokeCloseHandler();
         }
      }

   }

   private void cancelKeys() {
      try {
         this.handle.cancelKey(false);
      } catch (Throwable var2) {
      }

   }

   public void suspendReads() {
      this.handle.suspend(1);
   }

   public void suspendWrites() {
      this.handle.suspend(4);
   }

   public void resumeReads() {
      this.handle.resume(1);
   }

   public void resumeWrites() {
      this.handle.resume(4);
   }

   public boolean isReadResumed() {
      return this.handle.isResumed(1);
   }

   public boolean isWriteResumed() {
      return this.handle.isResumed(4);
   }

   public void wakeupReads() {
      this.handle.wakeup(1);
   }

   public void wakeupWrites() {
      this.handle.wakeup(4);
   }

   public void shutdownReads() throws IOException {
      throw Log.log.unsupported("shutdownReads");
   }

   public void shutdownWrites() throws IOException {
      throw Log.log.unsupported("shutdownWrites");
   }

   public void awaitReadable() throws IOException {
      SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 1);
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 1, time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.getIoThread();
   }

   public void awaitWritable() throws IOException {
      SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 4);
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 4, time, timeUnit);
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getWriteThread() {
      return this.getIoThread();
   }

   public MulticastMessageChannel.Key join(InetAddress group, NetworkInterface iface) throws IOException {
      return new NioKey(this.datagramChannel.join(group, iface));
   }

   public MulticastMessageChannel.Key join(InetAddress group, NetworkInterface iface, InetAddress source) throws IOException {
      return new NioKey(this.datagramChannel.join(group, iface, source));
   }

   public boolean supportsOption(Option<?> option) {
      return OPTIONS.contains(option);
   }

   public <T> T getOption(Option<T> option) throws UnsupportedOptionException, IOException {
      DatagramChannel channel = this.datagramChannel;
      DatagramSocket socket = channel.socket();
      if (option == Options.RECEIVE_BUFFER) {
         return option.cast(socket.getReceiveBufferSize());
      } else if (option == Options.SEND_BUFFER) {
         return option.cast(socket.getSendBufferSize());
      } else if (option == Options.BROADCAST) {
         return option.cast(socket.getBroadcast());
      } else if (option == Options.IP_TRAFFIC_CLASS) {
         return option.cast(socket.getTrafficClass());
      } else {
         return option == Options.MULTICAST_TTL ? option.cast(channel.getOption(StandardSocketOptions.IP_MULTICAST_TTL)) : null;
      }
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      DatagramChannel channel = this.datagramChannel;
      DatagramSocket socket = channel.socket();
      Object old;
      int newValue;
      if (option == Options.RECEIVE_BUFFER) {
         old = socket.getReceiveBufferSize();
         newValue = (Integer)Options.RECEIVE_BUFFER.cast(value, 65536);
         if (newValue < 1) {
            throw Log.log.optionOutOfRange("RECEIVE_BUFFER");
         }

         socket.setReceiveBufferSize(newValue);
      } else if (option == Options.SEND_BUFFER) {
         old = socket.getSendBufferSize();
         newValue = (Integer)Options.SEND_BUFFER.cast(value, 65536);
         if (newValue < 1) {
            throw Log.log.optionOutOfRange("SEND_BUFFER");
         }

         socket.setSendBufferSize(newValue);
      } else if (option == Options.IP_TRAFFIC_CLASS) {
         old = socket.getTrafficClass();
         socket.setTrafficClass((Integer)Options.IP_TRAFFIC_CLASS.cast(value, 0));
      } else if (option == Options.BROADCAST) {
         old = socket.getBroadcast();
         socket.setBroadcast((Boolean)Options.BROADCAST.cast(value, Boolean.FALSE));
      } else {
         if (option != Options.MULTICAST_TTL) {
            return null;
         }

         old = option.cast(channel.getOption(StandardSocketOptions.IP_MULTICAST_TTL));
         channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, (Integer)value);
      }

      return option.cast(old);
   }

   public String toString() {
      return String.format("UDP socket channel (NIO) <%h>", this);
   }

   static {
      OPTIONS = Option.setBuilder().add(Options.BROADCAST).add(Options.RECEIVE_BUFFER).add(Options.SEND_BUFFER).add(Options.IP_TRAFFIC_CLASS).add(Options.MULTICAST_TTL).create();
   }

   class NioKey implements MulticastMessageChannel.Key {
      private final MembershipKey key;

      NioKey(MembershipKey key) {
         this.key = key;
      }

      public MulticastMessageChannel.Key block(InetAddress source) throws IOException, UnsupportedOperationException, IllegalStateException, IllegalArgumentException {
         this.key.block(source);
         return this;
      }

      public MulticastMessageChannel.Key unblock(InetAddress source) throws IOException, IllegalStateException, UnsupportedOperationException {
         this.key.unblock(source);
         return this;
      }

      public MulticastMessageChannel getChannel() {
         return NioUdpChannel.this;
      }

      public InetAddress getGroup() {
         return this.key.group();
      }

      public NetworkInterface getNetworkInterface() {
         return this.key.networkInterface();
      }

      public InetAddress getSourceAddress() {
         return this.key.sourceAddress();
      }

      public boolean isOpen() {
         return this.key.isValid();
      }

      public void close() throws IOException {
         this.key.drop();
      }
   }
}
