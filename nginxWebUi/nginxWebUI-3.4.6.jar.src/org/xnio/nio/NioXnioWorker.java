/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.StandardProtocolFamily;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import java.util.concurrent.locks.LockSupport;
/*     */ import org.wildfly.common.net.CidrAddressTable;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.ClosedWorkerException;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.ManagementRegistration;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.MulticastMessageChannel;
/*     */ import org.xnio.management.XnioServerMXBean;
/*     */ import org.xnio.management.XnioWorkerMXBean;
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
/*     */ final class NioXnioWorker
/*     */   extends XnioWorker
/*     */ {
/*     */   private static final int CLOSE_REQ = -2147483648;
/*     */   private static final int CLOSE_COMP = 1073741824;
/*     */   private final long workerStackSize;
/*  69 */   private volatile int state = 1;
/*     */   
/*     */   private final WorkerThread[] workerThreads;
/*     */   
/*     */   private final WorkerThread acceptThread;
/*     */   
/*     */   private final NioWorkerMetrics metrics;
/*     */   
/*     */   private volatile Thread shutdownWaiter;
/*  78 */   private static final AtomicReferenceFieldUpdater<NioXnioWorker, Thread> shutdownWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(NioXnioWorker.class, Thread.class, "shutdownWaiter");
/*     */   
/*  80 */   private static final AtomicIntegerFieldUpdater<NioXnioWorker> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(NioXnioWorker.class, "state");
/*     */ 
/*     */   
/*     */   NioXnioWorker(XnioWorker.Builder builder) {
/*  84 */     super(builder);
/*  85 */     NioXnio xnio = (NioXnio)builder.getXnio();
/*  86 */     int threadCount = builder.getWorkerIoThreads();
/*  87 */     this.workerStackSize = builder.getWorkerStackSize();
/*  88 */     String workerName = getName();
/*     */     
/*  90 */     WorkerThread[] workerThreads = new WorkerThread[threadCount];
/*  91 */     ThreadGroup threadGroup = builder.getThreadGroup();
/*  92 */     boolean markWorkerThreadAsDaemon = builder.isDaemon();
/*  93 */     boolean ok = false; try {
/*     */       Selector threadSelector;
/*  95 */       for (int i = 0; i < threadCount; i++) {
/*     */         Selector selector;
/*     */         try {
/*  98 */           selector = xnio.mainSelectorCreator.open();
/*  99 */         } catch (IOException e) {
/* 100 */           throw Log.log.unexpectedSelectorOpenProblem(e);
/*     */         } 
/* 102 */         WorkerThread workerThread = new WorkerThread(this, selector, String.format("%s I/O-%d", new Object[] { workerName, Integer.valueOf(i + 1) }), threadGroup, this.workerStackSize, i);
/*     */         
/* 104 */         if (markWorkerThreadAsDaemon) {
/* 105 */           workerThread.setDaemon(true);
/*     */         }
/* 107 */         workerThreads[i] = workerThread;
/*     */       } 
/*     */       
/*     */       try {
/* 111 */         threadSelector = xnio.mainSelectorCreator.open();
/* 112 */       } catch (IOException e) {
/* 113 */         Selector selector; throw Log.log.unexpectedSelectorOpenProblem(selector);
/*     */       } 
/* 115 */       this.acceptThread = new WorkerThread(this, threadSelector, String.format("%s Accept", new Object[] { workerName }), threadGroup, this.workerStackSize, threadCount);
/* 116 */       if (markWorkerThreadAsDaemon) {
/* 117 */         this.acceptThread.setDaemon(true);
/*     */       }
/* 119 */       ok = true;
/*     */     } finally {
/* 121 */       if (!ok)
/* 122 */         for (WorkerThread worker : workerThreads) {
/* 123 */           if (worker != null) IoUtils.safeClose(worker.getSelector());
/*     */         
/*     */         }  
/*     */     } 
/* 127 */     this.workerThreads = workerThreads;
/* 128 */     this.metrics = new NioWorkerMetrics(workerName);
/* 129 */     this.metrics.register();
/*     */   }
/*     */   
/*     */   void start() {
/* 133 */     for (WorkerThread worker : this.workerThreads) {
/* 134 */       openResourceUnconditionally();
/* 135 */       worker.start();
/*     */     } 
/* 137 */     openResourceUnconditionally();
/* 138 */     this.acceptThread.start();
/*     */   }
/*     */   
/*     */   protected CidrAddressTable<InetSocketAddress> getBindAddressTable() {
/* 142 */     return super.getBindAddressTable();
/*     */   }
/*     */   
/*     */   protected WorkerThread chooseThread() {
/* 146 */     return getIoThread(ThreadLocalRandom.current().nextInt());
/*     */   }
/*     */   
/*     */   public WorkerThread getIoThread(int hashCode) {
/* 150 */     WorkerThread[] workerThreads = this.workerThreads;
/* 151 */     int length = workerThreads.length;
/* 152 */     if (length == 0) {
/* 153 */       throw Log.log.noThreads();
/*     */     }
/* 155 */     if (length == 1) {
/* 156 */       return workerThreads[0];
/*     */     }
/* 158 */     return workerThreads[Math.abs(hashCode % length)];
/*     */   }
/*     */   
/*     */   public int getIoThreadCount() {
/* 162 */     return this.workerThreads.length;
/*     */   }
/*     */   
/*     */   WorkerThread[] getAll() {
/* 166 */     return this.workerThreads;
/*     */   }
/*     */   
/*     */   protected AcceptingChannel<StreamConnection> createTcpConnectionServer(InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
/* 170 */     checkShutdown();
/* 171 */     boolean ok = false;
/* 172 */     ServerSocketChannel channel = ServerSocketChannel.open();
/*     */     try {
/* 174 */       if (optionMap.contains(Options.RECEIVE_BUFFER)) channel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1)); 
/* 175 */       channel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, true));
/* 176 */       channel.configureBlocking(false);
/* 177 */       if (optionMap.contains(Options.BACKLOG)) {
/* 178 */         channel.socket().bind(bindAddress, optionMap.get(Options.BACKLOG, 128));
/*     */       } else {
/* 180 */         channel.socket().bind(bindAddress);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 188 */       QueuedNioTcpServer2 server = new QueuedNioTcpServer2(new NioTcpServer(this, channel, optionMap, true));
/* 189 */       server.setAcceptListener((ChannelListener)acceptListener);
/* 190 */       ok = true;
/* 191 */       return server;
/*     */     } finally {
/*     */       
/* 194 */       if (!ok) {
/* 195 */         IoUtils.safeClose(channel);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public MulticastMessageChannel createUdpServer(InetSocketAddress bindAddress, ChannelListener<? super MulticastMessageChannel> bindListener, OptionMap optionMap) throws IOException {
/*     */     DatagramChannel channel;
/* 203 */     checkShutdown();
/*     */     
/* 205 */     if (bindAddress != null) {
/* 206 */       InetAddress address = bindAddress.getAddress();
/* 207 */       if (address instanceof java.net.Inet6Address) {
/* 208 */         channel = DatagramChannel.open(StandardProtocolFamily.INET6);
/*     */       } else {
/* 210 */         channel = DatagramChannel.open(StandardProtocolFamily.INET);
/*     */       } 
/*     */     } else {
/* 213 */       channel = DatagramChannel.open();
/*     */     } 
/* 215 */     channel.configureBlocking(false);
/* 216 */     if (optionMap.contains(Options.BROADCAST)) channel.socket().setBroadcast(optionMap.get(Options.BROADCAST, false)); 
/* 217 */     if (optionMap.contains(Options.IP_TRAFFIC_CLASS)) channel.socket().setTrafficClass(optionMap.get(Options.IP_TRAFFIC_CLASS, -1)); 
/* 218 */     if (optionMap.contains(Options.RECEIVE_BUFFER)) channel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1)); 
/* 219 */     channel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, true));
/* 220 */     if (optionMap.contains(Options.SEND_BUFFER)) channel.socket().setSendBufferSize(optionMap.get(Options.SEND_BUFFER, -1)); 
/* 221 */     channel.socket().bind(bindAddress);
/* 222 */     NioUdpChannel udpChannel = new NioUdpChannel(this, channel);
/* 223 */     ChannelListeners.invokeChannelListener((Channel)udpChannel, bindListener);
/* 224 */     return udpChannel;
/*     */   }
/*     */   
/*     */   public boolean isShutdown() {
/* 228 */     return ((this.state & Integer.MIN_VALUE) != 0);
/*     */   }
/*     */   
/*     */   public boolean isTerminated() {
/* 232 */     return ((this.state & 0x40000000) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void openResourceUnconditionally() {
/* 239 */     int oldState = stateUpdater.getAndIncrement(this);
/* 240 */     if (Log.log.isTraceEnabled()) {
/* 241 */       Log.log.tracef("CAS %s %08x -> %08x", this, Integer.valueOf(oldState), Integer.valueOf(oldState + 1));
/*     */     }
/*     */   }
/*     */   
/*     */   void checkShutdown() throws ClosedWorkerException {
/* 246 */     if (isShutdown())
/* 247 */       throw Log.log.workerShutDown(this); 
/*     */   }
/*     */   
/*     */   void closeResource() {
/* 251 */     int oldState = stateUpdater.decrementAndGet(this);
/* 252 */     if (Log.log.isTraceEnabled()) {
/* 253 */       Log.log.tracef("CAS %s %08x -> %08x", this, Integer.valueOf(oldState + 1), Integer.valueOf(oldState));
/*     */     }
/* 255 */     while (oldState == Integer.MIN_VALUE) {
/* 256 */       if (stateUpdater.compareAndSet(this, -2147483648, -1073741824)) {
/* 257 */         Log.log.tracef("CAS %s %08x -> %08x (close complete)", this, Integer.valueOf(-2147483648), Integer.valueOf(-1073741824));
/* 258 */         safeUnpark(shutdownWaiterUpdater.getAndSet(this, null));
/* 259 */         Runnable task = getTerminationTask();
/* 260 */         if (task != null)
/* 261 */           try { task.run(); }
/* 262 */           catch (Throwable throwable) {} 
/*     */         return;
/*     */       } 
/* 265 */       oldState = this.state;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 271 */     int oldState = this.state;
/* 272 */     while ((oldState & Integer.MIN_VALUE) == 0) {
/*     */       
/* 274 */       if (!stateUpdater.compareAndSet(this, oldState, oldState | Integer.MIN_VALUE)) {
/*     */         
/* 276 */         oldState = this.state;
/*     */         continue;
/*     */       } 
/* 279 */       Log.log.tracef("Initiating shutdown of %s", this);
/* 280 */       for (WorkerThread worker : this.workerThreads) {
/* 281 */         worker.shutdown();
/*     */       }
/* 283 */       this.acceptThread.shutdown();
/* 284 */       shutDownTaskPool();
/*     */       return;
/*     */     } 
/* 287 */     Log.log.tracef("Idempotent shutdown of %s", this);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Runnable> shutdownNow() {
/* 292 */     shutdown();
/* 293 */     return shutDownTaskPoolNow();
/*     */   }
/*     */   
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
/* 297 */     int oldState = this.state;
/* 298 */     if (Bits.allAreSet(oldState, 1073741824)) {
/* 299 */       return true;
/*     */     }
/* 301 */     long then = System.nanoTime();
/* 302 */     long duration = unit.toNanos(timeout);
/* 303 */     Thread myThread = Thread.currentThread();
/* 304 */     while (Bits.allAreClear(oldState = this.state, 1073741824)) {
/* 305 */       Thread oldThread = shutdownWaiterUpdater.getAndSet(this, myThread);
/*     */       
/* 307 */       try { if (Bits.allAreSet(oldState = this.state, 1073741824))
/*     */         
/*     */         { 
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
/* 321 */           safeUnpark(oldThread); break; }  LockSupport.parkNanos(this, duration); if (Thread.interrupted()) throw new InterruptedException();  long now = System.nanoTime(); duration -= now - then; if (duration < 0L) { oldState = this.state; safeUnpark(oldThread); break; }  } finally { safeUnpark(oldThread); }
/*     */     
/*     */     } 
/* 324 */     return Bits.allAreSet(oldState, 1073741824);
/*     */   }
/*     */   
/*     */   public void awaitTermination() throws InterruptedException {
/* 328 */     int oldState = this.state;
/* 329 */     if (Bits.allAreSet(oldState, 1073741824)) {
/*     */       return;
/*     */     }
/* 332 */     Thread myThread = Thread.currentThread();
/* 333 */     while (Bits.allAreClear(this.state, 1073741824)) {
/* 334 */       Thread oldThread = shutdownWaiterUpdater.getAndSet(this, myThread);
/*     */       
/* 336 */       try { if (Bits.allAreSet(this.state, 1073741824))
/*     */         
/*     */         { 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 344 */           safeUnpark(oldThread); break; }  LockSupport.park(this); if (Thread.interrupted()) throw new InterruptedException();  } finally { safeUnpark(oldThread); }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void safeUnpark(Thread waiter) {
/* 350 */     if (waiter != null) LockSupport.unpark(waiter); 
/*     */   }
/*     */   
/*     */   protected void taskPoolTerminated() {
/* 354 */     IoUtils.safeClose(this.metrics);
/* 355 */     closeResource();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 360 */     if (option.equals(Options.WORKER_IO_THREADS))
/* 361 */       return (T)option.cast(Integer.valueOf(this.workerThreads.length)); 
/* 362 */     if (option.equals(Options.STACK_SIZE)) {
/* 363 */       return (T)option.cast(Long.valueOf(this.workerStackSize));
/*     */     }
/* 365 */     return (T)super.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public NioXnio getXnio() {
/* 370 */     return (NioXnio)super.getXnio();
/*     */   }
/*     */   
/*     */   WorkerThread getAcceptThread() {
/* 374 */     return this.acceptThread;
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorkerMXBean getMXBean() {
/* 379 */     return this.metrics;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ManagementRegistration registerServerMXBean(XnioServerMXBean serverMXBean) {
/* 384 */     return this.metrics.registerServerMXBean(serverMXBean);
/*     */   }
/*     */   
/*     */   private class NioWorkerMetrics implements XnioWorkerMXBean, Closeable {
/*     */     private final String workerName;
/* 389 */     private final CopyOnWriteArrayList<XnioServerMXBean> serverMetrics = new CopyOnWriteArrayList<>();
/*     */     private Closeable mbeanHandle;
/*     */     
/*     */     private NioWorkerMetrics(String workerName) {
/* 393 */       this.workerName = workerName;
/*     */     }
/*     */     
/*     */     public String getProviderName() {
/* 397 */       return "nio";
/*     */     }
/*     */     
/*     */     public String getName() {
/* 401 */       return this.workerName;
/*     */     }
/*     */     
/*     */     public boolean isShutdownRequested() {
/* 405 */       return NioXnioWorker.this.isShutdown();
/*     */     }
/*     */     
/*     */     public int getCoreWorkerPoolSize() {
/* 409 */       return NioXnioWorker.this.getCoreWorkerPoolSize();
/*     */     }
/*     */     
/*     */     public int getMaxWorkerPoolSize() {
/* 413 */       return NioXnioWorker.this.getMaxWorkerPoolSize();
/*     */     }
/*     */     
/*     */     public int getWorkerPoolSize() {
/* 417 */       return NioXnioWorker.this.getWorkerPoolSize();
/*     */     }
/*     */     
/*     */     public int getBusyWorkerThreadCount() {
/* 421 */       return NioXnioWorker.this.getBusyWorkerThreadCount();
/*     */     }
/*     */     
/*     */     public int getIoThreadCount() {
/* 425 */       return NioXnioWorker.this.getIoThreadCount();
/*     */     }
/*     */     
/*     */     public int getWorkerQueueSize() {
/* 429 */       return NioXnioWorker.this.getWorkerQueueSize();
/*     */     }
/*     */     
/*     */     private ManagementRegistration registerServerMXBean(XnioServerMXBean serverMXBean) {
/* 433 */       this.serverMetrics.addIfAbsent(serverMXBean);
/* 434 */       Closeable handle = NioXnio.register(serverMXBean);
/* 435 */       return () -> {
/*     */           this.serverMetrics.remove(serverMXBean);
/*     */           IoUtils.safeClose(handle);
/*     */         };
/*     */     }
/*     */     
/*     */     public Set<XnioServerMXBean> getServerMXBeans() {
/* 442 */       return new LinkedHashSet<>(this.serverMetrics);
/*     */     }
/*     */     private void register() {
/* 445 */       this.mbeanHandle = NioXnio.register(this);
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 450 */       IoUtils.safeClose(this.mbeanHandle);
/* 451 */       this.serverMetrics.clear();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioXnioWorker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */