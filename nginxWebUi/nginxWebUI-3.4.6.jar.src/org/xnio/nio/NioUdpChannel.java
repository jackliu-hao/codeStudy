/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.StandardSocketOptions;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import java.nio.channels.MembershipKey;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.channels.MulticastMessageChannel;
/*     */ import org.xnio.channels.ReadListenerSettable;
/*     */ import org.xnio.channels.SocketAddressBuffer;
/*     */ import org.xnio.channels.UnsupportedOptionException;
/*     */ import org.xnio.channels.WriteListenerSettable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class NioUdpChannel
/*     */   extends AbstractNioChannel<NioUdpChannel>
/*     */   implements MulticastMessageChannel, ReadListenerSettable<NioUdpChannel>, WriteListenerSettable<NioUdpChannel>
/*     */ {
/*     */   private final NioUdpChannelHandle handle;
/*     */   private ChannelListener<? super NioUdpChannel> readListener;
/*     */   private ChannelListener<? super NioUdpChannel> writeListener;
/*     */   private final DatagramChannel datagramChannel;
/*  63 */   private final AtomicBoolean callFlag = new AtomicBoolean(false);
/*     */   
/*     */   NioUdpChannel(NioXnioWorker worker, DatagramChannel datagramChannel) throws ClosedChannelException {
/*  66 */     super(worker);
/*  67 */     this.datagramChannel = datagramChannel;
/*  68 */     WorkerThread workerThread = worker.chooseThread();
/*  69 */     SelectionKey key = workerThread.registerChannel(datagramChannel);
/*  70 */     this.handle = new NioUdpChannelHandle(workerThread, key, this);
/*  71 */     key.attach(this.handle);
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/*  75 */     return this.datagramChannel.socket().getLocalSocketAddress();
/*     */   }
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/*  79 */     return type.isInstance(getLocalAddress()) ? type.cast(getLocalAddress()) : null;
/*     */   }
/*     */   public int receiveFrom(SocketAddressBuffer addressBuffer, ByteBuffer buffer) throws IOException {
/*     */     SocketAddress sourceAddress;
/*  83 */     int o = buffer.remaining();
/*     */     
/*     */     try {
/*  86 */       sourceAddress = this.datagramChannel.receive(buffer);
/*  87 */     } catch (ClosedChannelException e) {
/*  88 */       return -1;
/*     */     } 
/*  90 */     if (sourceAddress == null) {
/*  91 */       return 0;
/*     */     }
/*  93 */     int t = o - buffer.remaining();
/*  94 */     if (addressBuffer != null) {
/*  95 */       addressBuffer.setSourceAddress(sourceAddress);
/*  96 */       addressBuffer.setDestinationAddress(null);
/*     */     } 
/*  98 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   public long receiveFrom(SocketAddressBuffer addressBuffer, ByteBuffer[] buffers) throws IOException {
/* 103 */     return receiveFrom(addressBuffer, buffers, 0, buffers.length);
/*     */   }
/*     */   public long receiveFrom(SocketAddressBuffer addressBuffer, ByteBuffer[] buffers, int offs, int len) throws IOException {
/*     */     SocketAddress sourceAddress;
/* 107 */     if (len == 0) {
/* 108 */       return 0L;
/*     */     }
/* 110 */     if (len == 1) {
/* 111 */       return receiveFrom(addressBuffer, buffers[offs]);
/*     */     }
/* 113 */     int o = (int)Math.min(Buffers.remaining((Buffer[])buffers, offs, len), 65536L);
/* 114 */     ByteBuffer buffer = ByteBuffer.allocate(o);
/*     */     
/*     */     try {
/* 117 */       sourceAddress = this.datagramChannel.receive(buffer);
/* 118 */     } catch (ClosedChannelException e) {
/* 119 */       return -1L;
/*     */     } 
/* 121 */     if (sourceAddress == null) {
/* 122 */       return 0L;
/*     */     }
/* 124 */     int t = o - buffer.remaining();
/* 125 */     buffer.flip();
/* 126 */     Buffers.copy(buffers, offs, len, buffer);
/* 127 */     if (addressBuffer != null) {
/* 128 */       addressBuffer.setSourceAddress(sourceAddress);
/* 129 */       addressBuffer.setDestinationAddress(null);
/*     */     } 
/* 131 */     return t;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendTo(SocketAddress target, ByteBuffer buffer) throws IOException {
/* 136 */     return (this.datagramChannel.send(buffer, target) != 0);
/*     */   }
/*     */   
/*     */   public boolean sendTo(SocketAddress target, ByteBuffer[] buffers) throws IOException {
/* 140 */     return sendTo(target, buffers, 0, buffers.length);
/*     */   }
/*     */   
/*     */   public boolean sendTo(SocketAddress target, ByteBuffer[] buffers, int offset, int length) throws IOException {
/* 144 */     if (length == 0) {
/* 145 */       return false;
/*     */     }
/* 147 */     if (length == 1) {
/* 148 */       return sendTo(target, buffers[offset]);
/*     */     }
/* 150 */     long o = Buffers.remaining((Buffer[])buffers, offset, length);
/* 151 */     if (o > 65535L)
/*     */     {
/* 153 */       throw Log.log.bufferTooLarge();
/*     */     }
/* 155 */     ByteBuffer buffer = ByteBuffer.allocate((int)o);
/* 156 */     Buffers.copy(buffer, buffers, offset, length);
/* 157 */     buffer.flip();
/* 158 */     return (this.datagramChannel.send(buffer, target) != 0);
/*     */   }
/*     */   
/*     */   public ChannelListener<? super NioUdpChannel> getReadListener() {
/* 162 */     return this.readListener;
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super NioUdpChannel> readListener) {
/* 166 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super NioUdpChannel> getWriteListener() {
/* 170 */     return this.writeListener;
/*     */   }
/*     */   
/*     */   public void setWriteListener(ChannelListener<? super NioUdpChannel> writeListener) {
/* 174 */     this.writeListener = writeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<NioUdpChannel> getReadSetter() {
/* 178 */     return (ChannelListener.Setter<NioUdpChannel>)new ReadListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<NioUdpChannel> getWriteSetter() {
/* 182 */     return (ChannelListener.Setter<NioUdpChannel>)new WriteListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 186 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 190 */     return this.datagramChannel.isOpen();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 194 */     if (!this.callFlag.getAndSet(true)) {
/* 195 */       Log.udpServerChannelLog.tracef("Closing %s", this); 
/* 196 */       try { cancelKeys(); } catch (Throwable throwable) {}
/*     */       try {
/* 198 */         this.datagramChannel.close();
/*     */       } finally {
/* 200 */         invokeCloseHandler();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private void cancelKeys() {
/*     */     
/* 206 */     try { this.handle.cancelKey(false); } catch (Throwable throwable) {}
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/* 210 */     this.handle.suspend(1);
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 214 */     this.handle.suspend(4);
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 218 */     this.handle.resume(1);
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 222 */     this.handle.resume(4);
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 226 */     return this.handle.isResumed(1);
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 230 */     return this.handle.isResumed(4);
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 234 */     this.handle.wakeup(1);
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 238 */     this.handle.wakeup(4);
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 242 */     throw Log.log.unsupported("shutdownReads");
/*     */   }
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 246 */     throw Log.log.unsupported("shutdownWrites");
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 250 */     SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 1);
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 254 */     SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 1, time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 259 */     return (XnioExecutor)getIoThread();
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 263 */     SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 4);
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 267 */     SelectorUtils.await(this.worker.getXnio(), this.datagramChannel, 4, time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 272 */     return (XnioExecutor)getIoThread();
/*     */   }
/*     */   
/*     */   public MulticastMessageChannel.Key join(InetAddress group, NetworkInterface iface) throws IOException {
/* 276 */     return new NioKey(this.datagramChannel.join(group, iface));
/*     */   }
/*     */   
/*     */   public MulticastMessageChannel.Key join(InetAddress group, NetworkInterface iface, InetAddress source) throws IOException {
/* 280 */     return new NioKey(this.datagramChannel.join(group, iface, source));
/*     */   }
/*     */   
/* 283 */   private static final Set<Option<?>> OPTIONS = Option.setBuilder()
/* 284 */     .add(Options.BROADCAST)
/* 285 */     .add(Options.RECEIVE_BUFFER)
/* 286 */     .add(Options.SEND_BUFFER)
/* 287 */     .add(Options.IP_TRAFFIC_CLASS)
/* 288 */     .add(Options.MULTICAST_TTL)
/* 289 */     .create();
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 292 */     return OPTIONS.contains(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws UnsupportedOptionException, IOException {
/* 296 */     DatagramChannel channel = this.datagramChannel;
/* 297 */     DatagramSocket socket = channel.socket();
/* 298 */     if (option == Options.RECEIVE_BUFFER)
/* 299 */       return (T)option.cast(Integer.valueOf(socket.getReceiveBufferSize())); 
/* 300 */     if (option == Options.SEND_BUFFER)
/* 301 */       return (T)option.cast(Integer.valueOf(socket.getSendBufferSize())); 
/* 302 */     if (option == Options.BROADCAST)
/* 303 */       return (T)option.cast(Boolean.valueOf(socket.getBroadcast())); 
/* 304 */     if (option == Options.IP_TRAFFIC_CLASS)
/* 305 */       return (T)option.cast(Integer.valueOf(socket.getTrafficClass())); 
/* 306 */     if (option == Options.MULTICAST_TTL) {
/* 307 */       return (T)option.cast(channel.getOption(StandardSocketOptions.IP_MULTICAST_TTL));
/*     */     }
/* 309 */     return null;
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/*     */     Object old;
/* 314 */     DatagramChannel channel = this.datagramChannel;
/* 315 */     DatagramSocket socket = channel.socket();
/*     */     
/* 317 */     if (option == Options.RECEIVE_BUFFER) {
/* 318 */       old = Integer.valueOf(socket.getReceiveBufferSize());
/* 319 */       int newValue = ((Integer)Options.RECEIVE_BUFFER.cast(value, Integer.valueOf(65536))).intValue();
/* 320 */       if (newValue < 1) {
/* 321 */         throw Log.log.optionOutOfRange("RECEIVE_BUFFER");
/*     */       }
/* 323 */       socket.setReceiveBufferSize(newValue);
/* 324 */     } else if (option == Options.SEND_BUFFER) {
/* 325 */       old = Integer.valueOf(socket.getSendBufferSize());
/* 326 */       int newValue = ((Integer)Options.SEND_BUFFER.cast(value, Integer.valueOf(65536))).intValue();
/* 327 */       if (newValue < 1) {
/* 328 */         throw Log.log.optionOutOfRange("SEND_BUFFER");
/*     */       }
/* 330 */       socket.setSendBufferSize(newValue);
/* 331 */     } else if (option == Options.IP_TRAFFIC_CLASS) {
/* 332 */       old = Integer.valueOf(socket.getTrafficClass());
/* 333 */       socket.setTrafficClass(((Integer)Options.IP_TRAFFIC_CLASS.cast(value, Integer.valueOf(0))).intValue());
/* 334 */     } else if (option == Options.BROADCAST) {
/* 335 */       old = Boolean.valueOf(socket.getBroadcast());
/* 336 */       socket.setBroadcast(((Boolean)Options.BROADCAST.cast(value, Boolean.FALSE)).booleanValue());
/* 337 */     } else if (option == Options.MULTICAST_TTL) {
/* 338 */       old = option.cast(channel.getOption(StandardSocketOptions.IP_MULTICAST_TTL));
/* 339 */       channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, (Integer)value);
/*     */     } else {
/* 341 */       return null;
/*     */     } 
/* 343 */     return (T)option.cast(old);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 348 */     return String.format("UDP socket channel (NIO) <%h>", new Object[] { this });
/*     */   }
/*     */   
/*     */   class NioKey
/*     */     implements MulticastMessageChannel.Key {
/*     */     private final MembershipKey key;
/*     */     
/*     */     NioKey(MembershipKey key) {
/* 356 */       this.key = key;
/*     */     }
/*     */     
/*     */     public MulticastMessageChannel.Key block(InetAddress source) throws IOException, UnsupportedOperationException, IllegalStateException, IllegalArgumentException {
/* 360 */       this.key.block(source);
/* 361 */       return this;
/*     */     }
/*     */     
/*     */     public MulticastMessageChannel.Key unblock(InetAddress source) throws IOException, IllegalStateException, UnsupportedOperationException {
/* 365 */       this.key.unblock(source);
/* 366 */       return this;
/*     */     }
/*     */     
/*     */     public MulticastMessageChannel getChannel() {
/* 370 */       return NioUdpChannel.this;
/*     */     }
/*     */     
/*     */     public InetAddress getGroup() {
/* 374 */       return this.key.group();
/*     */     }
/*     */     
/*     */     public NetworkInterface getNetworkInterface() {
/* 378 */       return this.key.networkInterface();
/*     */     }
/*     */     
/*     */     public InetAddress getSourceAddress() {
/* 382 */       return this.key.sourceAddress();
/*     */     }
/*     */     
/*     */     public boolean isOpen() {
/* 386 */       return this.key.isValid();
/*     */     }
/*     */     
/*     */     public void close() throws IOException {
/* 390 */       this.key.drop();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioUdpChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */