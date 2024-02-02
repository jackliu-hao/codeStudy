/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.ManagementRegistration;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.channels.AcceptListenerSettable;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.channels.UnsupportedOptionException;
/*     */ import org.xnio.management.XnioServerMXBean;
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
/*     */ final class NioTcpServer
/*     */   extends AbstractNioChannel<NioTcpServer>
/*     */   implements AcceptingChannel<StreamConnection>, AcceptListenerSettable<NioTcpServer>
/*     */ {
/*  58 */   private static final String FQCN = NioTcpServer.class.getName();
/*     */   
/*     */   private volatile ChannelListener<? super NioTcpServer> acceptListener;
/*     */   
/*     */   private final NioTcpServerHandle[] handles;
/*     */   
/*     */   private final ServerSocketChannel channel;
/*     */   
/*     */   private final ServerSocket socket;
/*     */   private final ManagementRegistration mbeanHandle;
/*  68 */   private static final Set<Option<?>> options = Option.setBuilder()
/*  69 */     .add(Options.REUSE_ADDRESSES)
/*  70 */     .add(Options.RECEIVE_BUFFER)
/*  71 */     .add(Options.SEND_BUFFER)
/*  72 */     .add(Options.KEEP_ALIVE)
/*  73 */     .add(Options.TCP_OOB_INLINE)
/*  74 */     .add(Options.TCP_NODELAY)
/*  75 */     .add(Options.CONNECTION_HIGH_WATER)
/*  76 */     .add(Options.CONNECTION_LOW_WATER)
/*  77 */     .add(Options.READ_TIMEOUT)
/*  78 */     .add(Options.WRITE_TIMEOUT)
/*  79 */     .create();
/*     */   
/*     */   private volatile int keepAlive;
/*     */   
/*     */   private volatile int oobInline;
/*     */   
/*     */   private volatile int tcpNoDelay;
/*     */   
/*     */   private volatile int sendBuffer;
/*     */   
/*     */   private volatile long connectionStatus;
/*     */   
/*     */   private volatile int readTimeout;
/*     */   
/*     */   private volatile int writeTimeout;
/*     */   
/*     */   private volatile int tokenConnectionCount;
/*     */   
/*     */   volatile boolean resumed;
/*     */   
/*     */   private static final long CONN_LOW_MASK = 2147483647L;
/*     */   
/*     */   private static final long CONN_LOW_BIT = 0L;
/*     */   
/*     */   private static final long CONN_LOW_ONE = 1L;
/*     */   private static final long CONN_HIGH_MASK = 4611686016279904256L;
/*     */   private static final long CONN_HIGH_BIT = 31L;
/*     */   private static final long CONN_HIGH_ONE = 2147483648L;
/* 107 */   private static final AtomicIntegerFieldUpdater<NioTcpServer> keepAliveUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "keepAlive");
/* 108 */   private static final AtomicIntegerFieldUpdater<NioTcpServer> oobInlineUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "oobInline");
/* 109 */   private static final AtomicIntegerFieldUpdater<NioTcpServer> tcpNoDelayUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "tcpNoDelay");
/* 110 */   private static final AtomicIntegerFieldUpdater<NioTcpServer> sendBufferUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "sendBuffer");
/* 111 */   private static final AtomicIntegerFieldUpdater<NioTcpServer> readTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "readTimeout");
/* 112 */   private static final AtomicIntegerFieldUpdater<NioTcpServer> writeTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "writeTimeout");
/*     */   
/* 114 */   private static final AtomicLongFieldUpdater<NioTcpServer> connectionStatusUpdater = AtomicLongFieldUpdater.newUpdater(NioTcpServer.class, "connectionStatus");
/*     */   
/*     */   NioTcpServer(final NioXnioWorker worker, ServerSocketChannel channel, OptionMap optionMap, boolean useAcceptThreadOnly) throws IOException {
/* 117 */     super(worker); WorkerThread[] threads; int threadCount, tokens, connections, perThreadLow, perThreadLowRem, perThreadHigh, perThreadHighRem; this.sendBuffer = -1; this.connectionStatus = 4611686018427387903L;
/* 118 */     this.channel = channel;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 123 */     if (useAcceptThreadOnly) {
/* 124 */       threads = new WorkerThread[] { worker.getAcceptThread() };
/* 125 */       threadCount = 1;
/* 126 */       tokens = 0;
/* 127 */       connections = 0;
/*     */     } else {
/* 129 */       threads = worker.getAll();
/* 130 */       threadCount = threads.length;
/* 131 */       if (threadCount == 0) {
/* 132 */         throw Log.log.noThreads();
/*     */       }
/* 134 */       tokens = optionMap.get(Options.BALANCING_TOKENS, -1);
/* 135 */       connections = optionMap.get(Options.BALANCING_CONNECTIONS, 16);
/* 136 */       if (tokens != -1) {
/* 137 */         if (tokens < 1 || tokens >= threadCount) {
/* 138 */           throw Log.log.balancingTokens();
/*     */         }
/* 140 */         if (connections < 1) {
/* 141 */           throw Log.log.balancingConnectionCount();
/*     */         }
/* 143 */         this.tokenConnectionCount = connections;
/*     */       } 
/*     */     } 
/* 146 */     this.socket = channel.socket();
/* 147 */     if (optionMap.contains(Options.SEND_BUFFER)) {
/* 148 */       int sendBufferSize = optionMap.get(Options.SEND_BUFFER, 65536);
/* 149 */       if (sendBufferSize < 1) {
/* 150 */         throw Log.log.parameterOutOfRange("sendBufferSize");
/*     */       }
/* 152 */       sendBufferUpdater.set(this, sendBufferSize);
/*     */     } 
/* 154 */     if (optionMap.contains(Options.KEEP_ALIVE)) {
/* 155 */       keepAliveUpdater.lazySet(this, optionMap.get(Options.KEEP_ALIVE, false) ? 1 : 0);
/*     */     }
/* 157 */     if (optionMap.contains(Options.TCP_OOB_INLINE)) {
/* 158 */       oobInlineUpdater.lazySet(this, optionMap.get(Options.TCP_OOB_INLINE, false) ? 1 : 0);
/*     */     }
/* 160 */     if (optionMap.contains(Options.TCP_NODELAY)) {
/* 161 */       tcpNoDelayUpdater.lazySet(this, optionMap.get(Options.TCP_NODELAY, false) ? 1 : 0);
/*     */     }
/* 163 */     if (optionMap.contains(Options.READ_TIMEOUT)) {
/* 164 */       readTimeoutUpdater.lazySet(this, optionMap.get(Options.READ_TIMEOUT, 0));
/*     */     }
/* 166 */     if (optionMap.contains(Options.WRITE_TIMEOUT)) {
/* 167 */       writeTimeoutUpdater.lazySet(this, optionMap.get(Options.WRITE_TIMEOUT, 0));
/*     */     }
/*     */ 
/*     */     
/* 171 */     if (optionMap.contains(Options.CONNECTION_HIGH_WATER) || optionMap.contains(Options.CONNECTION_LOW_WATER)) {
/* 172 */       int highWater = optionMap.get(Options.CONNECTION_HIGH_WATER, 2147483647);
/* 173 */       int lowWater = optionMap.get(Options.CONNECTION_LOW_WATER, highWater);
/* 174 */       if (highWater <= 0) {
/* 175 */         throw badHighWater();
/*     */       }
/* 177 */       if (lowWater <= 0 || lowWater > highWater) {
/* 178 */         throw badLowWater(highWater);
/*     */       }
/* 180 */       long highLowWater = highWater << 31L | lowWater << 0L;
/* 181 */       connectionStatusUpdater.lazySet(this, highLowWater);
/* 182 */       perThreadLow = lowWater / threadCount;
/* 183 */       perThreadLowRem = lowWater % threadCount;
/* 184 */       perThreadHigh = highWater / threadCount;
/* 185 */       perThreadHighRem = highWater % threadCount;
/*     */     } else {
/* 187 */       perThreadLow = Integer.MAX_VALUE;
/* 188 */       perThreadLowRem = 0;
/* 189 */       perThreadHigh = Integer.MAX_VALUE;
/* 190 */       perThreadHighRem = 0;
/* 191 */       connectionStatusUpdater.lazySet(this, 4611686018427387903L);
/*     */     } 
/* 193 */     final NioTcpServerHandle[] handles = new NioTcpServerHandle[threadCount]; int i, length;
/* 194 */     for (i = 0, length = threadCount; i < length; i++) {
/* 195 */       SelectionKey key = threads[i].registerChannel(channel);
/* 196 */       handles[i] = new NioTcpServerHandle(this, key, threads[i], (i < perThreadHighRem) ? (perThreadHigh + 1) : perThreadHigh, (i < perThreadLowRem) ? (perThreadLow + 1) : perThreadLow);
/* 197 */       key.attach(handles[i]);
/*     */     } 
/* 199 */     this.handles = handles;
/* 200 */     if (tokens > 0) {
/* 201 */       for (i = 0; i < threadCount; i++) {
/* 202 */         handles[i].initializeTokenCount((i < tokens) ? connections : 0);
/*     */       }
/*     */     }
/* 205 */     this.mbeanHandle = worker.registerServerMXBean(new XnioServerMXBean()
/*     */         {
/*     */           public String getProviderName() {
/* 208 */             return "nio";
/*     */           }
/*     */           
/*     */           public String getWorkerName() {
/* 212 */             return worker.getName();
/*     */           }
/*     */           
/*     */           public String getBindAddress() {
/* 216 */             return String.valueOf(NioTcpServer.this.getLocalAddress());
/*     */           }
/*     */           
/*     */           public int getConnectionCount() {
/* 220 */             AtomicInteger counter = new AtomicInteger();
/* 221 */             CountDownLatch latch = new CountDownLatch(handles.length);
/* 222 */             for (NioTcpServerHandle handle : handles) {
/* 223 */               handle.getWorkerThread().execute(() -> {
/*     */                     counter.getAndAdd(handle.getConnectionCount());
/*     */                     latch.countDown();
/*     */                   });
/*     */             } 
/*     */             try {
/* 229 */               latch.await();
/* 230 */             } catch (InterruptedException e) {
/* 231 */               Thread.currentThread().interrupt();
/*     */             } 
/* 233 */             return counter.get();
/*     */           }
/*     */           
/*     */           public int getConnectionLimitHighWater() {
/* 237 */             return NioTcpServer.getHighWater(NioTcpServer.this.connectionStatus);
/*     */           }
/*     */           
/*     */           public int getConnectionLimitLowWater() {
/* 241 */             return NioTcpServer.getLowWater(NioTcpServer.this.connectionStatus);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static IllegalArgumentException badLowWater(int highWater) {
/* 249 */     return new IllegalArgumentException("Low water must be greater than 0 and less than or equal to high water (" + highWater + ")");
/*     */   }
/*     */   
/*     */   private static IllegalArgumentException badHighWater() {
/* 253 */     return new IllegalArgumentException("High water must be greater than 0");
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 258 */       this.channel.close();
/*     */     } finally {
/* 260 */       for (NioTcpServerHandle handle : this.handles) {
/* 261 */         handle.cancelKey(false);
/*     */       }
/* 263 */       IoUtils.safeClose((AutoCloseable)this.mbeanHandle);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 268 */     return options.contains(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws UnsupportedOptionException, IOException {
/* 272 */     if (option == Options.REUSE_ADDRESSES)
/* 273 */       return (T)option.cast(Boolean.valueOf(this.socket.getReuseAddress())); 
/* 274 */     if (option == Options.RECEIVE_BUFFER)
/* 275 */       return (T)option.cast(Integer.valueOf(this.socket.getReceiveBufferSize())); 
/* 276 */     if (option == Options.SEND_BUFFER) {
/* 277 */       int value = this.sendBuffer;
/* 278 */       return (value == -1) ? null : (T)option.cast(Integer.valueOf(value));
/* 279 */     }  if (option == Options.KEEP_ALIVE)
/* 280 */       return (T)option.cast(Boolean.valueOf((this.keepAlive != 0))); 
/* 281 */     if (option == Options.TCP_OOB_INLINE)
/* 282 */       return (T)option.cast(Boolean.valueOf((this.oobInline != 0))); 
/* 283 */     if (option == Options.TCP_NODELAY)
/* 284 */       return (T)option.cast(Boolean.valueOf((this.tcpNoDelay != 0))); 
/* 285 */     if (option == Options.READ_TIMEOUT)
/* 286 */       return (T)option.cast(Integer.valueOf(this.readTimeout)); 
/* 287 */     if (option == Options.WRITE_TIMEOUT)
/* 288 */       return (T)option.cast(Integer.valueOf(this.writeTimeout)); 
/* 289 */     if (option == Options.CONNECTION_HIGH_WATER)
/* 290 */       return (T)option.cast(Integer.valueOf(getHighWater(this.connectionStatus))); 
/* 291 */     if (option == Options.CONNECTION_LOW_WATER) {
/* 292 */       return (T)option.cast(Integer.valueOf(getLowWater(this.connectionStatus)));
/*     */     }
/* 294 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/*     */     Object old;
/* 300 */     if (option == Options.REUSE_ADDRESSES) {
/* 301 */       old = Boolean.valueOf(this.socket.getReuseAddress());
/* 302 */       this.socket.setReuseAddress(((Boolean)Options.REUSE_ADDRESSES.cast(value, Boolean.FALSE)).booleanValue());
/* 303 */     } else if (option == Options.RECEIVE_BUFFER) {
/* 304 */       old = Integer.valueOf(this.socket.getReceiveBufferSize());
/* 305 */       int newValue = ((Integer)Options.RECEIVE_BUFFER.cast(value, Integer.valueOf(65536))).intValue();
/* 306 */       if (newValue < 1) {
/* 307 */         throw Log.log.optionOutOfRange("RECEIVE_BUFFER");
/*     */       }
/* 309 */       this.socket.setReceiveBufferSize(newValue);
/* 310 */     } else if (option == Options.SEND_BUFFER) {
/* 311 */       int newValue = ((Integer)Options.SEND_BUFFER.cast(value, Integer.valueOf(65536))).intValue();
/* 312 */       if (newValue < 1) {
/* 313 */         throw Log.log.optionOutOfRange("SEND_BUFFER");
/*     */       }
/* 315 */       int oldValue = sendBufferUpdater.getAndSet(this, newValue);
/* 316 */       old = (oldValue == -1) ? null : Integer.valueOf(oldValue);
/* 317 */     } else if (option == Options.KEEP_ALIVE) {
/* 318 */       old = Boolean.valueOf((keepAliveUpdater.getAndSet(this, ((Boolean)Options.KEEP_ALIVE.cast(value, Boolean.FALSE)).booleanValue() ? 1 : 0) != 0));
/* 319 */     } else if (option == Options.TCP_OOB_INLINE) {
/* 320 */       old = Boolean.valueOf((oobInlineUpdater.getAndSet(this, ((Boolean)Options.TCP_OOB_INLINE.cast(value, Boolean.FALSE)).booleanValue() ? 1 : 0) != 0));
/* 321 */     } else if (option == Options.TCP_NODELAY) {
/* 322 */       old = Boolean.valueOf((tcpNoDelayUpdater.getAndSet(this, ((Boolean)Options.TCP_NODELAY.cast(value, Boolean.FALSE)).booleanValue() ? 1 : 0) != 0));
/* 323 */     } else if (option == Options.READ_TIMEOUT) {
/* 324 */       old = Integer.valueOf(readTimeoutUpdater.getAndSet(this, ((Integer)Options.READ_TIMEOUT.cast(value, Integer.valueOf(0))).intValue()));
/* 325 */     } else if (option == Options.WRITE_TIMEOUT) {
/* 326 */       old = Integer.valueOf(writeTimeoutUpdater.getAndSet(this, ((Integer)Options.WRITE_TIMEOUT.cast(value, Integer.valueOf(0))).intValue()));
/* 327 */     } else if (option == Options.CONNECTION_HIGH_WATER) {
/* 328 */       old = Integer.valueOf(getHighWater(updateWaterMark(-1, ((Integer)Options.CONNECTION_HIGH_WATER.cast(value, Integer.valueOf(2147483647))).intValue())));
/* 329 */     } else if (option == Options.CONNECTION_LOW_WATER) {
/* 330 */       old = Integer.valueOf(getLowWater(updateWaterMark(((Integer)Options.CONNECTION_LOW_WATER.cast(value, Integer.valueOf(2147483647))).intValue(), -1)));
/*     */     } else {
/* 332 */       return null;
/*     */     } 
/* 334 */     return (T)option.cast(old);
/*     */   }
/*     */   private long updateWaterMark(int reqNewLowWater, int reqNewHighWater) {
/*     */     long oldVal, newVal;
/*     */     int newLowWater, newHighWater;
/* 339 */     assert reqNewLowWater != -1 || reqNewHighWater != -1;
/*     */     
/* 341 */     assert reqNewLowWater == -1 || reqNewHighWater == -1 || reqNewLowWater <= reqNewHighWater;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     do {
/* 348 */       oldVal = this.connectionStatus;
/* 349 */       int oldLowWater = getLowWater(oldVal);
/* 350 */       int oldHighWater = getHighWater(oldVal);
/* 351 */       newLowWater = (reqNewLowWater == -1) ? oldLowWater : reqNewLowWater;
/* 352 */       newHighWater = (reqNewHighWater == -1) ? oldHighWater : reqNewHighWater;
/*     */       
/* 354 */       if (reqNewLowWater != -1 && newLowWater > newHighWater) {
/* 355 */         newHighWater = newLowWater;
/* 356 */       } else if (reqNewHighWater != -1 && newHighWater < newLowWater) {
/* 357 */         newLowWater = newHighWater;
/*     */       } 
/*     */       
/* 360 */       if (oldLowWater == newLowWater && oldHighWater == newHighWater) {
/* 361 */         return oldVal;
/*     */       }
/* 363 */       newVal = newLowWater << 0L | newHighWater << 31L;
/* 364 */     } while (!connectionStatusUpdater.compareAndSet(this, oldVal, newVal));
/*     */     
/* 366 */     NioTcpServerHandle[] conduits = this.handles;
/* 367 */     int threadCount = conduits.length;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 372 */     int perThreadLow = newLowWater / threadCount;
/* 373 */     int perThreadLowRem = newLowWater % threadCount;
/* 374 */     int perThreadHigh = newHighWater / threadCount;
/* 375 */     int perThreadHighRem = newHighWater % threadCount;
/*     */     
/* 377 */     for (int i = 0; i < conduits.length; i++) {
/* 378 */       NioTcpServerHandle conduit = conduits[i];
/* 379 */       conduit.executeSetTask((i < perThreadHighRem) ? (perThreadHigh + 1) : perThreadHigh, (i < perThreadLowRem) ? (perThreadLow + 1) : perThreadLow);
/*     */     } 
/*     */     
/* 382 */     return oldVal;
/*     */   }
/*     */   
/*     */   private static int getHighWater(long value) {
/* 386 */     return (int)((value & 0x3FFFFFFF80000000L) >> 31L);
/*     */   }
/*     */   
/*     */   private static int getLowWater(long value) {
/* 390 */     return (int)((value & 0x7FFFFFFFL) >> 0L);
/*     */   }
/*     */   public NioSocketStreamConnection accept() throws ClosedChannelException {
/*     */     NioTcpServerHandle handle;
/* 394 */     WorkerThread current = WorkerThread.getCurrent();
/* 395 */     if (current == null) {
/* 396 */       return null;
/*     */     }
/*     */     
/* 399 */     if (this.handles.length == 1) {
/* 400 */       handle = this.handles[0];
/*     */     } else {
/* 402 */       handle = this.handles[current.getNumber()];
/*     */     } 
/* 404 */     if (!handle.getConnection()) {
/* 405 */       return null;
/*     */     }
/*     */     
/* 408 */     boolean ok = false;
/*     */     try {
/* 410 */       SocketChannel accepted = this.channel.accept();
/* 411 */       if (accepted != null)
/* 412 */         try { int hash = ThreadLocalRandom.current().nextInt();
/* 413 */           accepted.configureBlocking(false);
/* 414 */           Socket socket = accepted.socket();
/* 415 */           socket.setKeepAlive((this.keepAlive != 0));
/* 416 */           socket.setOOBInline((this.oobInline != 0));
/* 417 */           socket.setTcpNoDelay((this.tcpNoDelay != 0));
/* 418 */           int sendBuffer = this.sendBuffer;
/* 419 */           if (sendBuffer > 0) socket.setSendBufferSize(sendBuffer); 
/* 420 */           WorkerThread ioThread = this.worker.getIoThread(hash);
/* 421 */           SelectionKey selectionKey = ioThread.registerChannel(accepted);
/* 422 */           NioSocketStreamConnection newConnection = new NioSocketStreamConnection(ioThread, selectionKey, handle);
/* 423 */           newConnection.setOption(Options.READ_TIMEOUT, Integer.valueOf(this.readTimeout));
/* 424 */           newConnection.setOption(Options.WRITE_TIMEOUT, Integer.valueOf(this.writeTimeout));
/* 425 */           ok = true;
/* 426 */           handle.resetBackOff();
/* 427 */           return newConnection; }
/*     */         finally
/* 429 */         { if (!ok) IoUtils.safeClose(accepted);  }
/*     */          
/* 431 */     } catch (ClosedChannelException e) {
/* 432 */       throw e;
/* 433 */     } catch (IOException e) {
/*     */ 
/*     */       
/* 436 */       handle.startBackOff();
/* 437 */       Log.log.acceptFailed(e, handle.getBackOffTime());
/* 438 */       return null;
/*     */     } finally {
/* 440 */       if (!ok) {
/* 441 */         handle.freeConnection();
/*     */       }
/*     */     } 
/*     */     
/* 445 */     return null;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 449 */     return String.format("TCP server (NIO) <%s>", new Object[] { Integer.toHexString(hashCode()) });
/*     */   }
/*     */   
/*     */   public ChannelListener<? super NioTcpServer> getAcceptListener() {
/* 453 */     return this.acceptListener;
/*     */   }
/*     */   
/*     */   public void setAcceptListener(ChannelListener<? super NioTcpServer> acceptListener) {
/* 457 */     this.acceptListener = acceptListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<NioTcpServer> getAcceptSetter() {
/* 461 */     return (ChannelListener.Setter<NioTcpServer>)new AcceptListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 465 */     return this.channel.isOpen();
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 469 */     return this.socket.getLocalSocketAddress();
/*     */   }
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 473 */     SocketAddress address = getLocalAddress();
/* 474 */     return type.isInstance(address) ? type.cast(address) : null;
/*     */   }
/*     */   
/*     */   public void suspendAccepts() {
/* 478 */     this.resumed = false;
/* 479 */     doResume(0);
/*     */   }
/*     */   
/*     */   public void resumeAccepts() {
/* 483 */     this.resumed = true;
/* 484 */     doResume(16);
/*     */   }
/*     */   
/*     */   public boolean isAcceptResumed() {
/* 488 */     return this.resumed;
/*     */   }
/*     */   
/*     */   private void doResume(int op) {
/* 492 */     if (op == 0) {
/* 493 */       for (NioTcpServerHandle handle : this.handles) {
/* 494 */         handle.suspend();
/*     */       }
/*     */     } else {
/* 497 */       for (NioTcpServerHandle handle : this.handles) {
/* 498 */         handle.resume();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void wakeupAccepts() {
/* 504 */     Log.tcpServerLog.logf(FQCN, Logger.Level.TRACE, null, "Wake up accepts on %s", this);
/* 505 */     resumeAccepts();
/* 506 */     NioTcpServerHandle[] handles = this.handles;
/* 507 */     int idx = IoUtils.getThreadLocalRandom().nextInt(handles.length);
/* 508 */     handles[idx].wakeup(16);
/*     */   }
/*     */   
/*     */   public void awaitAcceptable() throws IOException {
/* 512 */     throw Log.log.unsupported("awaitAcceptable");
/*     */   }
/*     */   
/*     */   public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
/* 516 */     throw Log.log.unsupported("awaitAcceptable");
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getAcceptThread() {
/* 521 */     return (XnioExecutor)getIoThread();
/*     */   }
/*     */   
/*     */   NioTcpServerHandle getHandle(int number) {
/* 525 */     return this.handles[number];
/*     */   }
/*     */   
/*     */   int getTokenConnectionCount() {
/* 529 */     return this.tokenConnectionCount;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioTcpServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */