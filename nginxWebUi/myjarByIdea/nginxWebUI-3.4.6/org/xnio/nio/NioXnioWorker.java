package org.xnio.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import org.wildfly.common.net.CidrAddressTable;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.ClosedWorkerException;
import org.xnio.IoUtils;
import org.xnio.ManagementRegistration;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioWorker;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.MulticastMessageChannel;
import org.xnio.management.XnioServerMXBean;
import org.xnio.management.XnioWorkerMXBean;

final class NioXnioWorker extends XnioWorker {
   private static final int CLOSE_REQ = Integer.MIN_VALUE;
   private static final int CLOSE_COMP = 1073741824;
   private final long workerStackSize;
   private volatile int state = 1;
   private final WorkerThread[] workerThreads;
   private final WorkerThread acceptThread;
   private final NioWorkerMetrics metrics;
   private volatile Thread shutdownWaiter;
   private static final AtomicReferenceFieldUpdater<NioXnioWorker, Thread> shutdownWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(NioXnioWorker.class, Thread.class, "shutdownWaiter");
   private static final AtomicIntegerFieldUpdater<NioXnioWorker> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(NioXnioWorker.class, "state");

   NioXnioWorker(XnioWorker.Builder builder) {
      super(builder);
      NioXnio xnio = (NioXnio)builder.getXnio();
      int threadCount = builder.getWorkerIoThreads();
      this.workerStackSize = builder.getWorkerStackSize();
      String workerName = this.getName();
      WorkerThread[] workerThreads = new WorkerThread[threadCount];
      ThreadGroup threadGroup = builder.getThreadGroup();
      boolean markWorkerThreadAsDaemon = builder.isDaemon();
      boolean ok = false;
      boolean var21 = false;

      try {
         var21 = true;
         int i = 0;

         while(true) {
            if (i >= threadCount) {
               Selector threadSelector;
               try {
                  threadSelector = xnio.mainSelectorCreator.open();
               } catch (IOException var22) {
                  throw Log.log.unexpectedSelectorOpenProblem(var22);
               }

               this.acceptThread = new WorkerThread(this, threadSelector, String.format("%s Accept", workerName), threadGroup, this.workerStackSize, threadCount);
               if (markWorkerThreadAsDaemon) {
                  this.acceptThread.setDaemon(true);
               }

               ok = true;
               var21 = false;
               break;
            }

            Selector threadSelector;
            try {
               threadSelector = xnio.mainSelectorCreator.open();
            } catch (IOException var23) {
               throw Log.log.unexpectedSelectorOpenProblem(var23);
            }

            WorkerThread workerThread = new WorkerThread(this, threadSelector, String.format("%s I/O-%d", workerName, i + 1), threadGroup, this.workerStackSize, i);
            if (markWorkerThreadAsDaemon) {
               workerThread.setDaemon(true);
            }

            workerThreads[i] = workerThread;
            ++i;
         }
      } finally {
         if (var21) {
            if (!ok) {
               WorkerThread[] var14 = workerThreads;
               int var15 = workerThreads.length;

               for(int var16 = 0; var16 < var15; ++var16) {
                  WorkerThread worker = var14[var16];
                  if (worker != null) {
                     IoUtils.safeClose(worker.getSelector());
                  }
               }
            }

         }
      }

      if (!ok) {
         WorkerThread[] var26 = workerThreads;
         int var27 = workerThreads.length;

         for(int var28 = 0; var28 < var27; ++var28) {
            WorkerThread worker = var26[var28];
            if (worker != null) {
               IoUtils.safeClose(worker.getSelector());
            }
         }
      }

      this.workerThreads = workerThreads;
      this.metrics = new NioWorkerMetrics(workerName);
      this.metrics.register();
   }

   void start() {
      WorkerThread[] var1 = this.workerThreads;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         WorkerThread worker = var1[var3];
         this.openResourceUnconditionally();
         worker.start();
      }

      this.openResourceUnconditionally();
      this.acceptThread.start();
   }

   protected CidrAddressTable<InetSocketAddress> getBindAddressTable() {
      return super.getBindAddressTable();
   }

   protected WorkerThread chooseThread() {
      return this.getIoThread(ThreadLocalRandom.current().nextInt());
   }

   public WorkerThread getIoThread(int hashCode) {
      WorkerThread[] workerThreads = this.workerThreads;
      int length = workerThreads.length;
      if (length == 0) {
         throw Log.log.noThreads();
      } else {
         return length == 1 ? workerThreads[0] : workerThreads[Math.abs(hashCode % length)];
      }
   }

   public int getIoThreadCount() {
      return this.workerThreads.length;
   }

   WorkerThread[] getAll() {
      return this.workerThreads;
   }

   protected AcceptingChannel<StreamConnection> createTcpConnectionServer(InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
      this.checkShutdown();
      boolean ok = false;
      ServerSocketChannel channel = ServerSocketChannel.open();

      QueuedNioTcpServer2 var7;
      try {
         if (optionMap.contains(Options.RECEIVE_BUFFER)) {
            channel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1));
         }

         channel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, true));
         channel.configureBlocking(false);
         if (optionMap.contains(Options.BACKLOG)) {
            channel.socket().bind(bindAddress, optionMap.get(Options.BACKLOG, 128));
         } else {
            channel.socket().bind(bindAddress);
         }

         QueuedNioTcpServer2 server = new QueuedNioTcpServer2(new NioTcpServer(this, channel, optionMap, true));
         server.setAcceptListener(acceptListener);
         ok = true;
         var7 = server;
      } finally {
         if (!ok) {
            IoUtils.safeClose((Closeable)channel);
         }

      }

      return var7;
   }

   public MulticastMessageChannel createUdpServer(InetSocketAddress bindAddress, ChannelListener<? super MulticastMessageChannel> bindListener, OptionMap optionMap) throws IOException {
      this.checkShutdown();
      DatagramChannel channel;
      if (bindAddress != null) {
         InetAddress address = bindAddress.getAddress();
         if (address instanceof Inet6Address) {
            channel = DatagramChannel.open(StandardProtocolFamily.INET6);
         } else {
            channel = DatagramChannel.open(StandardProtocolFamily.INET);
         }
      } else {
         channel = DatagramChannel.open();
      }

      channel.configureBlocking(false);
      if (optionMap.contains(Options.BROADCAST)) {
         channel.socket().setBroadcast(optionMap.get(Options.BROADCAST, false));
      }

      if (optionMap.contains(Options.IP_TRAFFIC_CLASS)) {
         channel.socket().setTrafficClass(optionMap.get(Options.IP_TRAFFIC_CLASS, -1));
      }

      if (optionMap.contains(Options.RECEIVE_BUFFER)) {
         channel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1));
      }

      channel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, true));
      if (optionMap.contains(Options.SEND_BUFFER)) {
         channel.socket().setSendBufferSize(optionMap.get(Options.SEND_BUFFER, -1));
      }

      channel.socket().bind(bindAddress);
      NioUdpChannel udpChannel = new NioUdpChannel(this, channel);
      ChannelListeners.invokeChannelListener(udpChannel, bindListener);
      return udpChannel;
   }

   public boolean isShutdown() {
      return (this.state & Integer.MIN_VALUE) != 0;
   }

   public boolean isTerminated() {
      return (this.state & 1073741824) != 0;
   }

   void openResourceUnconditionally() {
      int oldState = stateUpdater.getAndIncrement(this);
      if (Log.log.isTraceEnabled()) {
         Log.log.tracef("CAS %s %08x -> %08x", this, oldState, oldState + 1);
      }

   }

   void checkShutdown() throws ClosedWorkerException {
      if (this.isShutdown()) {
         throw Log.log.workerShutDown(this);
      }
   }

   void closeResource() {
      int oldState = stateUpdater.decrementAndGet(this);
      if (Log.log.isTraceEnabled()) {
         Log.log.tracef("CAS %s %08x -> %08x", this, oldState + 1, oldState);
      }

      while(oldState == Integer.MIN_VALUE) {
         if (stateUpdater.compareAndSet(this, Integer.MIN_VALUE, -1073741824)) {
            Log.log.tracef("CAS %s %08x -> %08x (close complete)", this, Integer.MIN_VALUE, -1073741824);
            safeUnpark((Thread)shutdownWaiterUpdater.getAndSet(this, (Object)null));
            Runnable task = this.getTerminationTask();
            if (task != null) {
               try {
                  task.run();
               } catch (Throwable var4) {
               }
            }

            return;
         }

         oldState = this.state;
      }

   }

   public void shutdown() {
      for(int oldState = this.state; (oldState & Integer.MIN_VALUE) == 0; oldState = this.state) {
         if (stateUpdater.compareAndSet(this, oldState, oldState | Integer.MIN_VALUE)) {
            Log.log.tracef("Initiating shutdown of %s", this);
            WorkerThread[] var2 = this.workerThreads;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               WorkerThread worker = var2[var4];
               worker.shutdown();
            }

            this.acceptThread.shutdown();
            this.shutDownTaskPool();
            return;
         }
      }

      Log.log.tracef("Idempotent shutdown of %s", this);
   }

   public List<Runnable> shutdownNow() {
      this.shutdown();
      return this.shutDownTaskPoolNow();
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      int oldState = this.state;
      if (Bits.allAreSet(oldState, 1073741824)) {
         return true;
      } else {
         long then = System.nanoTime();
         long duration = unit.toNanos(timeout);
         Thread myThread = Thread.currentThread();

         while(Bits.allAreClear(oldState = this.state, 1073741824)) {
            Thread oldThread = (Thread)shutdownWaiterUpdater.getAndSet(this, myThread);

            try {
               if (Bits.allAreSet(oldState = this.state, 1073741824)) {
                  break;
               }

               LockSupport.parkNanos(this, duration);
               if (Thread.interrupted()) {
                  throw new InterruptedException();
               }

               long now = System.nanoTime();
               duration -= now - then;
               if (duration < 0L) {
                  oldState = this.state;
                  break;
               }
            } finally {
               safeUnpark(oldThread);
            }
         }

         return Bits.allAreSet(oldState, 1073741824);
      }
   }

   public void awaitTermination() throws InterruptedException {
      int oldState = this.state;
      if (!Bits.allAreSet(oldState, 1073741824)) {
         Thread myThread = Thread.currentThread();

         while(Bits.allAreClear(this.state, 1073741824)) {
            Thread oldThread = (Thread)shutdownWaiterUpdater.getAndSet(this, myThread);

            try {
               if (Bits.allAreSet(this.state, 1073741824)) {
                  break;
               }

               LockSupport.park(this);
               if (Thread.interrupted()) {
                  throw new InterruptedException();
               }
            } finally {
               safeUnpark(oldThread);
            }
         }

      }
   }

   private static void safeUnpark(Thread waiter) {
      if (waiter != null) {
         LockSupport.unpark(waiter);
      }

   }

   protected void taskPoolTerminated() {
      IoUtils.safeClose((Closeable)this.metrics);
      this.closeResource();
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (option.equals(Options.WORKER_IO_THREADS)) {
         return option.cast(this.workerThreads.length);
      } else {
         return option.equals(Options.STACK_SIZE) ? option.cast(this.workerStackSize) : super.getOption(option);
      }
   }

   public NioXnio getXnio() {
      return (NioXnio)super.getXnio();
   }

   WorkerThread getAcceptThread() {
      return this.acceptThread;
   }

   public XnioWorkerMXBean getMXBean() {
      return this.metrics;
   }

   protected ManagementRegistration registerServerMXBean(XnioServerMXBean serverMXBean) {
      return this.metrics.registerServerMXBean(serverMXBean);
   }

   private class NioWorkerMetrics implements XnioWorkerMXBean, Closeable {
      private final String workerName;
      private final CopyOnWriteArrayList<XnioServerMXBean> serverMetrics;
      private Closeable mbeanHandle;

      private NioWorkerMetrics(String workerName) {
         this.serverMetrics = new CopyOnWriteArrayList();
         this.workerName = workerName;
      }

      public String getProviderName() {
         return "nio";
      }

      public String getName() {
         return this.workerName;
      }

      public boolean isShutdownRequested() {
         return NioXnioWorker.this.isShutdown();
      }

      public int getCoreWorkerPoolSize() {
         return NioXnioWorker.this.getCoreWorkerPoolSize();
      }

      public int getMaxWorkerPoolSize() {
         return NioXnioWorker.this.getMaxWorkerPoolSize();
      }

      public int getWorkerPoolSize() {
         return NioXnioWorker.this.getWorkerPoolSize();
      }

      public int getBusyWorkerThreadCount() {
         return NioXnioWorker.this.getBusyWorkerThreadCount();
      }

      public int getIoThreadCount() {
         return NioXnioWorker.this.getIoThreadCount();
      }

      public int getWorkerQueueSize() {
         return NioXnioWorker.this.getWorkerQueueSize();
      }

      private ManagementRegistration registerServerMXBean(XnioServerMXBean serverMXBean) {
         this.serverMetrics.addIfAbsent(serverMXBean);
         Closeable handle = NioXnio.register(serverMXBean);
         return () -> {
            this.serverMetrics.remove(serverMXBean);
            IoUtils.safeClose(handle);
         };
      }

      public Set<XnioServerMXBean> getServerMXBeans() {
         return new LinkedHashSet(this.serverMetrics);
      }

      private void register() {
         this.mbeanHandle = NioXnio.register((XnioWorkerMXBean)this);
      }

      public void close() throws IOException {
         IoUtils.safeClose(this.mbeanHandle);
         this.serverMetrics.clear();
      }

      // $FF: synthetic method
      NioWorkerMetrics(String x1, Object x2) {
         this(x1);
      }
   }
}
