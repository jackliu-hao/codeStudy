package org.xnio.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import org.jboss.logging.Logger;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.ManagementRegistration;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.channels.AcceptListenerSettable;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.UnsupportedOptionException;
import org.xnio.management.XnioServerMXBean;

final class NioTcpServer extends AbstractNioChannel<NioTcpServer> implements AcceptingChannel<StreamConnection>, AcceptListenerSettable<NioTcpServer> {
   private static final String FQCN = NioTcpServer.class.getName();
   private volatile ChannelListener<? super NioTcpServer> acceptListener;
   private final NioTcpServerHandle[] handles;
   private final ServerSocketChannel channel;
   private final ServerSocket socket;
   private final ManagementRegistration mbeanHandle;
   private static final Set<Option<?>> options;
   private volatile int keepAlive;
   private volatile int oobInline;
   private volatile int tcpNoDelay;
   private volatile int sendBuffer = -1;
   private volatile long connectionStatus = 4611686018427387903L;
   private volatile int readTimeout;
   private volatile int writeTimeout;
   private volatile int tokenConnectionCount;
   volatile boolean resumed;
   private static final long CONN_LOW_MASK = 2147483647L;
   private static final long CONN_LOW_BIT = 0L;
   private static final long CONN_LOW_ONE = 1L;
   private static final long CONN_HIGH_MASK = 4611686016279904256L;
   private static final long CONN_HIGH_BIT = 31L;
   private static final long CONN_HIGH_ONE = 2147483648L;
   private static final AtomicIntegerFieldUpdater<NioTcpServer> keepAliveUpdater;
   private static final AtomicIntegerFieldUpdater<NioTcpServer> oobInlineUpdater;
   private static final AtomicIntegerFieldUpdater<NioTcpServer> tcpNoDelayUpdater;
   private static final AtomicIntegerFieldUpdater<NioTcpServer> sendBufferUpdater;
   private static final AtomicIntegerFieldUpdater<NioTcpServer> readTimeoutUpdater;
   private static final AtomicIntegerFieldUpdater<NioTcpServer> writeTimeoutUpdater;
   private static final AtomicLongFieldUpdater<NioTcpServer> connectionStatusUpdater;

   NioTcpServer(final NioXnioWorker worker, ServerSocketChannel channel, OptionMap optionMap, boolean useAcceptThreadOnly) throws IOException {
      super(worker);
      this.channel = channel;
      WorkerThread[] threads;
      int threadCount;
      int tokens;
      int connections;
      if (useAcceptThreadOnly) {
         threads = new WorkerThread[]{worker.getAcceptThread()};
         threadCount = 1;
         tokens = 0;
         connections = 0;
      } else {
         threads = worker.getAll();
         threadCount = threads.length;
         if (threadCount == 0) {
            throw Log.log.noThreads();
         }

         tokens = optionMap.get(Options.BALANCING_TOKENS, -1);
         connections = optionMap.get(Options.BALANCING_CONNECTIONS, 16);
         if (tokens != -1) {
            if (tokens < 1 || tokens >= threadCount) {
               throw Log.log.balancingTokens();
            }

            if (connections < 1) {
               throw Log.log.balancingConnectionCount();
            }

            this.tokenConnectionCount = connections;
         }
      }

      this.socket = channel.socket();
      int perThreadLow;
      if (optionMap.contains(Options.SEND_BUFFER)) {
         perThreadLow = optionMap.get(Options.SEND_BUFFER, 65536);
         if (perThreadLow < 1) {
            throw Log.log.parameterOutOfRange("sendBufferSize");
         }

         sendBufferUpdater.set(this, perThreadLow);
      }

      if (optionMap.contains(Options.KEEP_ALIVE)) {
         keepAliveUpdater.lazySet(this, optionMap.get(Options.KEEP_ALIVE, false) ? 1 : 0);
      }

      if (optionMap.contains(Options.TCP_OOB_INLINE)) {
         oobInlineUpdater.lazySet(this, optionMap.get(Options.TCP_OOB_INLINE, false) ? 1 : 0);
      }

      if (optionMap.contains(Options.TCP_NODELAY)) {
         tcpNoDelayUpdater.lazySet(this, optionMap.get(Options.TCP_NODELAY, false) ? 1 : 0);
      }

      if (optionMap.contains(Options.READ_TIMEOUT)) {
         readTimeoutUpdater.lazySet(this, optionMap.get(Options.READ_TIMEOUT, 0));
      }

      if (optionMap.contains(Options.WRITE_TIMEOUT)) {
         writeTimeoutUpdater.lazySet(this, optionMap.get(Options.WRITE_TIMEOUT, 0));
      }

      int perThreadLowRem;
      int perThreadHigh;
      int perThreadHighRem;
      int i;
      if (!optionMap.contains(Options.CONNECTION_HIGH_WATER) && !optionMap.contains(Options.CONNECTION_LOW_WATER)) {
         perThreadLow = Integer.MAX_VALUE;
         perThreadLowRem = 0;
         perThreadHigh = Integer.MAX_VALUE;
         perThreadHighRem = 0;
         connectionStatusUpdater.lazySet(this, 4611686018427387903L);
      } else {
         int highWater = optionMap.get(Options.CONNECTION_HIGH_WATER, Integer.MAX_VALUE);
         i = optionMap.get(Options.CONNECTION_LOW_WATER, highWater);
         if (highWater <= 0) {
            throw badHighWater();
         }

         if (i <= 0 || i > highWater) {
            throw badLowWater(highWater);
         }

         long highLowWater = (long)highWater << 31 | (long)i << 0;
         connectionStatusUpdater.lazySet(this, highLowWater);
         perThreadLow = i / threadCount;
         perThreadLowRem = i % threadCount;
         perThreadHigh = highWater / threadCount;
         perThreadHighRem = highWater % threadCount;
      }

      final NioTcpServerHandle[] handles = new NioTcpServerHandle[threadCount];
      i = 0;

      for(int length = threadCount; i < length; ++i) {
         SelectionKey key = threads[i].registerChannel(channel);
         handles[i] = new NioTcpServerHandle(this, key, threads[i], i < perThreadHighRem ? perThreadHigh + 1 : perThreadHigh, i < perThreadLowRem ? perThreadLow + 1 : perThreadLow);
         key.attach(handles[i]);
      }

      this.handles = handles;
      if (tokens > 0) {
         for(i = 0; i < threadCount; ++i) {
            handles[i].initializeTokenCount(i < tokens ? connections : 0);
         }
      }

      this.mbeanHandle = worker.registerServerMXBean(new XnioServerMXBean() {
         public String getProviderName() {
            return "nio";
         }

         public String getWorkerName() {
            return worker.getName();
         }

         public String getBindAddress() {
            return String.valueOf(NioTcpServer.this.getLocalAddress());
         }

         public int getConnectionCount() {
            AtomicInteger counter = new AtomicInteger();
            CountDownLatch latch = new CountDownLatch(handles.length);
            NioTcpServerHandle[] var3 = handles;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               NioTcpServerHandle handle = var3[var5];
               handle.getWorkerThread().execute(() -> {
                  counter.getAndAdd(handle.getConnectionCount());
                  latch.countDown();
               });
            }

            try {
               latch.await();
            } catch (InterruptedException var7) {
               Thread.currentThread().interrupt();
            }

            return counter.get();
         }

         public int getConnectionLimitHighWater() {
            return NioTcpServer.getHighWater(NioTcpServer.this.connectionStatus);
         }

         public int getConnectionLimitLowWater() {
            return NioTcpServer.getLowWater(NioTcpServer.this.connectionStatus);
         }
      });
   }

   private static IllegalArgumentException badLowWater(int highWater) {
      return new IllegalArgumentException("Low water must be greater than 0 and less than or equal to high water (" + highWater + ")");
   }

   private static IllegalArgumentException badHighWater() {
      return new IllegalArgumentException("High water must be greater than 0");
   }

   public void close() throws IOException {
      boolean var11 = false;

      try {
         var11 = true;
         this.channel.close();
         var11 = false;
      } finally {
         if (var11) {
            NioTcpServerHandle[] var6 = this.handles;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               NioTcpServerHandle handle = var6[var8];
               handle.cancelKey(false);
            }

            IoUtils.safeClose((AutoCloseable)this.mbeanHandle);
         }
      }

      NioTcpServerHandle[] var1 = this.handles;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         NioTcpServerHandle handle = var1[var3];
         handle.cancelKey(false);
      }

      IoUtils.safeClose((AutoCloseable)this.mbeanHandle);
   }

   public boolean supportsOption(Option<?> option) {
      return options.contains(option);
   }

   public <T> T getOption(Option<T> option) throws UnsupportedOptionException, IOException {
      if (option == Options.REUSE_ADDRESSES) {
         return option.cast(this.socket.getReuseAddress());
      } else if (option == Options.RECEIVE_BUFFER) {
         return option.cast(this.socket.getReceiveBufferSize());
      } else if (option == Options.SEND_BUFFER) {
         int value = this.sendBuffer;
         return value == -1 ? null : option.cast(value);
      } else if (option == Options.KEEP_ALIVE) {
         return option.cast(this.keepAlive != 0);
      } else if (option == Options.TCP_OOB_INLINE) {
         return option.cast(this.oobInline != 0);
      } else if (option == Options.TCP_NODELAY) {
         return option.cast(this.tcpNoDelay != 0);
      } else if (option == Options.READ_TIMEOUT) {
         return option.cast(this.readTimeout);
      } else if (option == Options.WRITE_TIMEOUT) {
         return option.cast(this.writeTimeout);
      } else if (option == Options.CONNECTION_HIGH_WATER) {
         return option.cast(getHighWater(this.connectionStatus));
      } else {
         return option == Options.CONNECTION_LOW_WATER ? option.cast(getLowWater(this.connectionStatus)) : null;
      }
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      Object old;
      if (option == Options.REUSE_ADDRESSES) {
         old = this.socket.getReuseAddress();
         this.socket.setReuseAddress((Boolean)Options.REUSE_ADDRESSES.cast(value, Boolean.FALSE));
      } else {
         int newValue;
         if (option == Options.RECEIVE_BUFFER) {
            old = this.socket.getReceiveBufferSize();
            newValue = (Integer)Options.RECEIVE_BUFFER.cast(value, 65536);
            if (newValue < 1) {
               throw Log.log.optionOutOfRange("RECEIVE_BUFFER");
            }

            this.socket.setReceiveBufferSize(newValue);
         } else if (option == Options.SEND_BUFFER) {
            newValue = (Integer)Options.SEND_BUFFER.cast(value, 65536);
            if (newValue < 1) {
               throw Log.log.optionOutOfRange("SEND_BUFFER");
            }

            int oldValue = sendBufferUpdater.getAndSet(this, newValue);
            old = oldValue == -1 ? null : oldValue;
         } else if (option == Options.KEEP_ALIVE) {
            old = keepAliveUpdater.getAndSet(this, (Boolean)Options.KEEP_ALIVE.cast(value, Boolean.FALSE) ? 1 : 0) != 0;
         } else if (option == Options.TCP_OOB_INLINE) {
            old = oobInlineUpdater.getAndSet(this, (Boolean)Options.TCP_OOB_INLINE.cast(value, Boolean.FALSE) ? 1 : 0) != 0;
         } else if (option == Options.TCP_NODELAY) {
            old = tcpNoDelayUpdater.getAndSet(this, (Boolean)Options.TCP_NODELAY.cast(value, Boolean.FALSE) ? 1 : 0) != 0;
         } else if (option == Options.READ_TIMEOUT) {
            old = readTimeoutUpdater.getAndSet(this, (Integer)Options.READ_TIMEOUT.cast(value, 0));
         } else if (option == Options.WRITE_TIMEOUT) {
            old = writeTimeoutUpdater.getAndSet(this, (Integer)Options.WRITE_TIMEOUT.cast(value, 0));
         } else if (option == Options.CONNECTION_HIGH_WATER) {
            old = getHighWater(this.updateWaterMark(-1, (Integer)Options.CONNECTION_HIGH_WATER.cast(value, Integer.MAX_VALUE)));
         } else {
            if (option != Options.CONNECTION_LOW_WATER) {
               return null;
            }

            old = getLowWater(this.updateWaterMark((Integer)Options.CONNECTION_LOW_WATER.cast(value, Integer.MAX_VALUE), -1));
         }
      }

      return option.cast(old);
   }

   private long updateWaterMark(int reqNewLowWater, int reqNewHighWater) {
      assert reqNewLowWater != -1 || reqNewHighWater != -1;

      assert reqNewLowWater == -1 || reqNewHighWater == -1 || reqNewLowWater <= reqNewHighWater;

      long oldVal;
      long newVal;
      int newLowWater;
      int newHighWater;
      do {
         oldVal = this.connectionStatus;
         int oldLowWater = getLowWater(oldVal);
         int oldHighWater = getHighWater(oldVal);
         newLowWater = reqNewLowWater == -1 ? oldLowWater : reqNewLowWater;
         newHighWater = reqNewHighWater == -1 ? oldHighWater : reqNewHighWater;
         if (reqNewLowWater != -1 && newLowWater > newHighWater) {
            newHighWater = newLowWater;
         } else if (reqNewHighWater != -1 && newHighWater < newLowWater) {
            newLowWater = newHighWater;
         }

         if (oldLowWater == newLowWater && oldHighWater == newHighWater) {
            return oldVal;
         }

         newVal = (long)newLowWater << 0 | (long)newHighWater << 31;
      } while(!connectionStatusUpdater.compareAndSet(this, oldVal, newVal));

      NioTcpServerHandle[] conduits = this.handles;
      int threadCount = conduits.length;
      int perThreadLow = newLowWater / threadCount;
      int perThreadLowRem = newLowWater % threadCount;
      int perThreadHigh = newHighWater / threadCount;
      int perThreadHighRem = newHighWater % threadCount;

      for(int i = 0; i < conduits.length; ++i) {
         NioTcpServerHandle conduit = conduits[i];
         conduit.executeSetTask(i < perThreadHighRem ? perThreadHigh + 1 : perThreadHigh, i < perThreadLowRem ? perThreadLow + 1 : perThreadLow);
      }

      return oldVal;
   }

   private static int getHighWater(long value) {
      return (int)((value & 4611686016279904256L) >> 31);
   }

   private static int getLowWater(long value) {
      return (int)((value & 2147483647L) >> 0);
   }

   public NioSocketStreamConnection accept() throws ClosedChannelException {
      WorkerThread current = WorkerThread.getCurrent();
      if (current == null) {
         return null;
      } else {
         NioTcpServerHandle handle;
         if (this.handles.length == 1) {
            handle = this.handles[0];
         } else {
            handle = this.handles[current.getNumber()];
         }

         if (!handle.getConnection()) {
            return null;
         } else {
            boolean ok = false;

            NioSocketStreamConnection var11;
            try {
               Socket socket;
               try {
                  SocketChannel accepted = this.channel.accept();
                  if (accepted == null) {
                     return null;
                  }

                  try {
                     int hash = ThreadLocalRandom.current().nextInt();
                     accepted.configureBlocking(false);
                     socket = accepted.socket();
                     socket.setKeepAlive(this.keepAlive != 0);
                     socket.setOOBInline(this.oobInline != 0);
                     socket.setTcpNoDelay(this.tcpNoDelay != 0);
                     int sendBuffer = this.sendBuffer;
                     if (sendBuffer > 0) {
                        socket.setSendBufferSize(sendBuffer);
                     }

                     WorkerThread ioThread = this.worker.getIoThread(hash);
                     SelectionKey selectionKey = ioThread.registerChannel(accepted);
                     NioSocketStreamConnection newConnection = new NioSocketStreamConnection(ioThread, selectionKey, handle);
                     newConnection.setOption(Options.READ_TIMEOUT, this.readTimeout);
                     newConnection.setOption(Options.WRITE_TIMEOUT, this.writeTimeout);
                     ok = true;
                     handle.resetBackOff();
                     var11 = newConnection;
                  } finally {
                     if (!ok) {
                        IoUtils.safeClose((Closeable)accepted);
                     }

                  }
               } catch (ClosedChannelException var23) {
                  throw var23;
               } catch (IOException var24) {
                  handle.startBackOff();
                  Log.log.acceptFailed(var24, handle.getBackOffTime());
                  socket = null;
                  return socket;
               }
            } finally {
               if (!ok) {
                  handle.freeConnection();
               }

            }

            return var11;
         }
      }
   }

   public String toString() {
      return String.format("TCP server (NIO) <%s>", Integer.toHexString(this.hashCode()));
   }

   public ChannelListener<? super NioTcpServer> getAcceptListener() {
      return this.acceptListener;
   }

   public void setAcceptListener(ChannelListener<? super NioTcpServer> acceptListener) {
      this.acceptListener = acceptListener;
   }

   public ChannelListener.Setter<NioTcpServer> getAcceptSetter() {
      return new AcceptListenerSettable.Setter(this);
   }

   public boolean isOpen() {
      return this.channel.isOpen();
   }

   public SocketAddress getLocalAddress() {
      return this.socket.getLocalSocketAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      SocketAddress address = this.getLocalAddress();
      return type.isInstance(address) ? (SocketAddress)type.cast(address) : null;
   }

   public void suspendAccepts() {
      this.resumed = false;
      this.doResume(0);
   }

   public void resumeAccepts() {
      this.resumed = true;
      this.doResume(16);
   }

   public boolean isAcceptResumed() {
      return this.resumed;
   }

   private void doResume(int op) {
      NioTcpServerHandle[] var2;
      int var3;
      int var4;
      NioTcpServerHandle handle;
      if (op == 0) {
         var2 = this.handles;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            handle = var2[var4];
            handle.suspend();
         }
      } else {
         var2 = this.handles;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            handle = var2[var4];
            handle.resume();
         }
      }

   }

   public void wakeupAccepts() {
      Log.tcpServerLog.logf(FQCN, Logger.Level.TRACE, (Throwable)null, "Wake up accepts on %s", this);
      this.resumeAccepts();
      NioTcpServerHandle[] handles = this.handles;
      int idx = IoUtils.getThreadLocalRandom().nextInt(handles.length);
      handles[idx].wakeup(16);
   }

   public void awaitAcceptable() throws IOException {
      throw Log.log.unsupported("awaitAcceptable");
   }

   public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
      throw Log.log.unsupported("awaitAcceptable");
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getAcceptThread() {
      return this.getIoThread();
   }

   NioTcpServerHandle getHandle(int number) {
      return this.handles[number];
   }

   int getTokenConnectionCount() {
      return this.tokenConnectionCount;
   }

   static {
      options = Option.setBuilder().add(Options.REUSE_ADDRESSES).add(Options.RECEIVE_BUFFER).add(Options.SEND_BUFFER).add(Options.KEEP_ALIVE).add(Options.TCP_OOB_INLINE).add(Options.TCP_NODELAY).add(Options.CONNECTION_HIGH_WATER).add(Options.CONNECTION_LOW_WATER).add(Options.READ_TIMEOUT).add(Options.WRITE_TIMEOUT).create();
      keepAliveUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "keepAlive");
      oobInlineUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "oobInline");
      tcpNoDelayUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "tcpNoDelay");
      sendBufferUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "sendBuffer");
      readTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "readTimeout");
      writeTimeoutUpdater = AtomicIntegerFieldUpdater.newUpdater(NioTcpServer.class, "writeTimeout");
      connectionStatusUpdater = AtomicLongFieldUpdater.newUpdater(NioTcpServer.class, "connectionStatus");
   }
}
