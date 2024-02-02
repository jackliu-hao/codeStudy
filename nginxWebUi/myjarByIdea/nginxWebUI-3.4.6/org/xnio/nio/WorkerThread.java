package org.xnio.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.security.AccessController;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import org.jboss.logging.Logger;
import org.xnio.Cancellable;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.ChannelPipe;
import org.xnio.ClosedWorkerException;
import org.xnio.FailedIoFuture;
import org.xnio.FinishedIoFuture;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.ReadPropertyAction;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoFactory;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

final class WorkerThread extends XnioIoThread implements XnioExecutor {
   private static final long LONGEST_DELAY = 9223372036853L;
   private static final String FQCN = WorkerThread.class.getName();
   private static final boolean OLD_LOCKING = Boolean.parseBoolean((String)AccessController.doPrivileged(new ReadPropertyAction("xnio.nio.old-locking", "false")));
   private static final boolean THREAD_SAFE_SELECTION_KEYS = Boolean.parseBoolean((String)AccessController.doPrivileged(new ReadPropertyAction("xnio.nio.thread-safe-selection-keys", "false")));
   private static final long START_TIME = System.nanoTime();
   private final Selector selector;
   private final Object workLock = new Object();
   private final Queue<Runnable> selectorWorkQueue = new ArrayDeque();
   private final TreeSet<TimeKey> delayWorkQueue = new TreeSet();
   private volatile int state;
   private static final int SHUTDOWN = Integer.MIN_VALUE;
   private static final AtomicIntegerFieldUpdater<WorkerThread> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(WorkerThread.class, "state");
   volatile boolean polling;
   static final AtomicLong seqGen = new AtomicLong();

   WorkerThread(NioXnioWorker worker, Selector selector, String name, ThreadGroup group, long stackSize, int number) {
      super(worker, number, group, name, stackSize);
      this.selector = selector;
   }

   static WorkerThread getCurrent() {
      Thread thread = currentThread();
      return thread instanceof WorkerThread ? (WorkerThread)thread : null;
   }

   public NioXnioWorker getWorker() {
      return (NioXnioWorker)super.getWorker();
   }

   protected IoFuture<StreamConnection> acceptTcpStreamConnection(InetSocketAddress destination, final ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, final OptionMap optionMap) {
      try {
         this.getWorker().checkShutdown();
      } catch (ClosedWorkerException var15) {
         return new FailedIoFuture(var15);
      }

      final FutureResult<StreamConnection> futureResult = new FutureResult(this);

      try {
         boolean ok = false;
         final ServerSocketChannel serverChannel = ServerSocketChannel.open();

         IoFuture var10;
         try {
            serverChannel.configureBlocking(false);
            if (optionMap.contains(Options.RECEIVE_BUFFER)) {
               serverChannel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1));
            }

            serverChannel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, true));
            serverChannel.bind(destination);
            if (bindListener != null) {
               ChannelListeners.invokeChannelListener(new BoundChannel() {
                  public SocketAddress getLocalAddress() {
                     return serverChannel.socket().getLocalSocketAddress();
                  }

                  public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
                     SocketAddress address = this.getLocalAddress();
                     return type.isInstance(address) ? (SocketAddress)type.cast(address) : null;
                  }

                  public ChannelListener.Setter<? extends BoundChannel> getCloseSetter() {
                     return new ChannelListener.SimpleSetter();
                  }

                  public XnioWorker getWorker() {
                     return WorkerThread.this.getWorker();
                  }

                  public XnioIoThread getIoThread() {
                     return WorkerThread.this;
                  }

                  public void close() throws IOException {
                     serverChannel.close();
                  }

                  public boolean isOpen() {
                     return serverChannel.isOpen();
                  }

                  public boolean supportsOption(Option<?> option) {
                     return false;
                  }

                  public <T> T getOption(Option<T> option) throws IOException {
                     return null;
                  }

                  public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
                     return null;
                  }
               }, bindListener);
            }

            SelectionKey key = this.registerChannel(serverChannel);
            NioHandle handle = new NioHandle(this, key) {
               void handleReady(int ops) {
                  boolean ok = false;

                  try {
                     SocketChannel channel = serverChannel.accept();
                     if (channel == null) {
                        ok = true;
                        return;
                     }

                     IoUtils.safeClose((Closeable)serverChannel);

                     try {
                        channel.configureBlocking(false);
                        if (optionMap.contains(Options.TCP_OOB_INLINE)) {
                           channel.socket().setOOBInline(optionMap.get(Options.TCP_OOB_INLINE, false));
                        }

                        if (optionMap.contains(Options.TCP_NODELAY)) {
                           channel.socket().setTcpNoDelay(optionMap.get(Options.TCP_NODELAY, false));
                        }

                        if (optionMap.contains(Options.IP_TRAFFIC_CLASS)) {
                           channel.socket().setTrafficClass(optionMap.get(Options.IP_TRAFFIC_CLASS, -1));
                        }

                        if (optionMap.contains(Options.CLOSE_ABORT)) {
                           channel.socket().setSoLinger(optionMap.get(Options.CLOSE_ABORT, false), 0);
                        }

                        if (optionMap.contains(Options.KEEP_ALIVE)) {
                           channel.socket().setKeepAlive(optionMap.get(Options.KEEP_ALIVE, false));
                        }

                        if (optionMap.contains(Options.SEND_BUFFER)) {
                           channel.socket().setSendBufferSize(optionMap.get(Options.SEND_BUFFER, -1));
                        }

                        SelectionKey selectionKey = WorkerThread.this.registerChannel(channel);
                        NioSocketStreamConnection connection = new NioSocketStreamConnection(WorkerThread.this, selectionKey, (ChannelClosed)null);
                        if (optionMap.contains(Options.READ_TIMEOUT)) {
                           connection.setOption(Options.READ_TIMEOUT, optionMap.get(Options.READ_TIMEOUT, 0));
                        }

                        if (optionMap.contains(Options.WRITE_TIMEOUT)) {
                           connection.setOption(Options.WRITE_TIMEOUT, optionMap.get(Options.WRITE_TIMEOUT, 0));
                        }

                        if (futureResult.setResult(connection)) {
                           ok = true;
                           ChannelListeners.invokeChannelListener(connection, openListener);
                        }
                     } finally {
                        if (!ok) {
                           IoUtils.safeClose((Closeable)channel);
                        }

                     }
                  } catch (IOException var15) {
                     futureResult.setException(var15);
                  } finally {
                     if (!ok) {
                        IoUtils.safeClose((Closeable)serverChannel);
                     }

                  }

               }

               void terminated() {
               }

               void forceTermination() {
                  futureResult.setCancelled();
               }
            };
            key.attach(handle);
            handle.resume(16);
            ok = true;
            futureResult.addCancelHandler(new Cancellable() {
               public Cancellable cancel() {
                  if (futureResult.setCancelled()) {
                     IoUtils.safeClose((Closeable)serverChannel);
                  }

                  return this;
               }
            });
            var10 = futureResult.getIoFuture();
         } finally {
            if (!ok) {
               IoUtils.safeClose((Closeable)serverChannel);
            }

         }

         return var10;
      } catch (IOException var17) {
         return new FailedIoFuture(var17);
      }
   }

   protected IoFuture<StreamConnection> openTcpStreamConnection(InetSocketAddress bindAddress, InetSocketAddress destinationAddress, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      try {
         this.getWorker().checkShutdown();
      } catch (ClosedWorkerException var17) {
         return new FailedIoFuture(var17);
      }

      try {
         SocketChannel channel = SocketChannel.open();
         boolean ok = false;

         IoFuture var12;
         try {
            channel.configureBlocking(false);
            if (optionMap.contains(Options.TCP_OOB_INLINE)) {
               channel.socket().setOOBInline(optionMap.get(Options.TCP_OOB_INLINE, false));
            }

            if (optionMap.contains(Options.TCP_NODELAY)) {
               channel.socket().setTcpNoDelay(optionMap.get(Options.TCP_NODELAY, false));
            }

            if (optionMap.contains(Options.IP_TRAFFIC_CLASS)) {
               channel.socket().setTrafficClass(optionMap.get(Options.IP_TRAFFIC_CLASS, -1));
            }

            if (optionMap.contains(Options.CLOSE_ABORT)) {
               channel.socket().setSoLinger(optionMap.get(Options.CLOSE_ABORT, false), 0);
            }

            if (optionMap.contains(Options.KEEP_ALIVE)) {
               channel.socket().setKeepAlive(optionMap.get(Options.KEEP_ALIVE, false));
            }

            if (optionMap.contains(Options.RECEIVE_BUFFER)) {
               channel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1));
            }

            if (optionMap.contains(Options.REUSE_ADDRESSES)) {
               channel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, false));
            }

            if (optionMap.contains(Options.SEND_BUFFER)) {
               channel.socket().setSendBufferSize(optionMap.get(Options.SEND_BUFFER, -1));
            }

            SelectionKey key = this.registerChannel(channel);
            final NioSocketStreamConnection connection = new NioSocketStreamConnection(this, key, (ChannelClosed)null);
            if (optionMap.contains(Options.READ_TIMEOUT)) {
               connection.setOption(Options.READ_TIMEOUT, optionMap.get(Options.READ_TIMEOUT, 0));
            }

            if (optionMap.contains(Options.WRITE_TIMEOUT)) {
               connection.setOption(Options.WRITE_TIMEOUT, optionMap.get(Options.WRITE_TIMEOUT, 0));
            }

            if (bindAddress != null || bindListener != null) {
               channel.socket().bind(bindAddress);
               ChannelListeners.invokeChannelListener(connection, bindListener);
            }

            if (channel.connect(destinationAddress)) {
               Log.selectorLog.tracef("Synchronous connect", new Object[0]);
               this.execute(ChannelListeners.getChannelListenerTask(connection, (ChannelListener)openListener));
               FinishedIoFuture<StreamConnection> finishedIoFuture = new FinishedIoFuture(connection);
               ok = true;
               FinishedIoFuture var21 = finishedIoFuture;
               return var21;
            }

            Log.selectorLog.tracef("Asynchronous connect", new Object[0]);
            final FutureResult<StreamConnection> futureResult = new FutureResult(this);
            ConnectHandle connectHandle = new ConnectHandle(this, key, futureResult, connection, openListener);
            key.attach(connectHandle);
            futureResult.addCancelHandler(new Cancellable() {
               public Cancellable cancel() {
                  if (futureResult.setCancelled()) {
                     IoUtils.safeClose((Closeable)connection);
                  }

                  return this;
               }
            });
            connectHandle.resume(8);
            ok = true;
            var12 = futureResult.getIoFuture();
         } finally {
            if (!ok) {
               IoUtils.safeClose((Closeable)channel);
            }

         }

         return var12;
      } catch (IOException var19) {
         return new FailedIoFuture(var19);
      }
   }

   WorkerThread getNextThread() {
      WorkerThread[] all = this.getWorker().getAll();
      int number = this.getNumber();
      return number == all.length - 1 ? all[0] : all[number + 1];
   }

   private static WorkerThread getPeerThread(XnioIoFactory peer) throws ClosedWorkerException {
      WorkerThread peerThread;
      if (peer instanceof NioXnioWorker) {
         NioXnioWorker peerWorker = (NioXnioWorker)peer;
         peerWorker.checkShutdown();
         peerThread = peerWorker.chooseThread();
      } else {
         if (!(peer instanceof WorkerThread)) {
            throw Log.log.notNioProvider();
         }

         peerThread = (WorkerThread)peer;
         peerThread.getWorker().checkShutdown();
      }

      return peerThread;
   }

   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory peer) throws IOException {
      this.getWorker().checkShutdown();
      boolean ok = false;
      Pipe topPipe = Pipe.open();

      ChannelPipe var13;
      try {
         topPipe.source().configureBlocking(false);
         topPipe.sink().configureBlocking(false);
         Pipe bottomPipe = Pipe.open();

         try {
            bottomPipe.source().configureBlocking(false);
            bottomPipe.sink().configureBlocking(false);
            WorkerThread peerThread = getPeerThread(peer);
            SelectionKey topSourceKey = this.registerChannel(topPipe.source());
            SelectionKey topSinkKey = peerThread.registerChannel(topPipe.sink());
            SelectionKey bottomSourceKey = peerThread.registerChannel(bottomPipe.source());
            SelectionKey bottomSinkKey = this.registerChannel(bottomPipe.sink());
            NioPipeStreamConnection leftConnection = new NioPipeStreamConnection(this, bottomSourceKey, topSinkKey);
            NioPipeStreamConnection rightConnection = new NioPipeStreamConnection(this, topSourceKey, bottomSinkKey);
            ChannelPipe<StreamConnection, StreamConnection> result = new ChannelPipe(leftConnection, rightConnection);
            ok = true;
            var13 = result;
         } finally {
            if (!ok) {
               IoUtils.safeClose((Closeable)bottomPipe.sink());
               IoUtils.safeClose((Closeable)bottomPipe.source());
            }

         }
      } finally {
         if (!ok) {
            IoUtils.safeClose((Closeable)topPipe.sink());
            IoUtils.safeClose((Closeable)topPipe.source());
         }

      }

      return var13;
   }

   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory peer) throws IOException {
      this.getWorker().checkShutdown();
      Pipe pipe = Pipe.open();
      boolean ok = false;

      ChannelPipe var10;
      try {
         pipe.source().configureBlocking(false);
         pipe.sink().configureBlocking(false);
         WorkerThread peerThread = getPeerThread(peer);
         SelectionKey readKey = this.registerChannel(pipe.source());
         SelectionKey writeKey = peerThread.registerChannel(pipe.sink());
         NioPipeStreamConnection leftConnection = new NioPipeStreamConnection(this, readKey, (SelectionKey)null);
         NioPipeStreamConnection rightConnection = new NioPipeStreamConnection(this, (SelectionKey)null, writeKey);
         leftConnection.writeClosed();
         rightConnection.readClosed();
         ChannelPipe<StreamSourceChannel, StreamSinkChannel> result = new ChannelPipe(leftConnection.getSourceChannel(), rightConnection.getSinkChannel());
         ok = true;
         var10 = result;
      } finally {
         if (!ok) {
            IoUtils.safeClose((Closeable)pipe.sink());
            IoUtils.safeClose((Closeable)pipe.source());
         }

      }

      return var10;
   }

   public void run() {
      Selector selector = this.selector;

      try {
         Log.log.tracef("Starting worker thread %s", this);
         Object lock = this.workLock;
         Queue<Runnable> workQueue = this.selectorWorkQueue;
         TreeSet<TimeKey> delayQueue = this.delayWorkQueue;
         Log.log.debugf("Started channel thread '%s', selector %s", currentThread().getName(), selector);
         long delayTime = Long.MAX_VALUE;
         SelectionKey[] keys = new SelectionKey[16];

         while(true) {
            Runnable task;
            do {
               synchronized(lock) {
                  task = (Runnable)workQueue.poll();
                  if (task == null) {
                     Iterator<TimeKey> iterator = delayQueue.iterator();
                     delayTime = Long.MAX_VALUE;
                     if (iterator.hasNext()) {
                        long now = System.nanoTime();

                        do {
                           TimeKey key = (TimeKey)iterator.next();
                           if (key.deadline > now - START_TIME) {
                              delayTime = key.deadline - (now - START_TIME);
                              break;
                           }

                           workQueue.add(key.command);
                           iterator.remove();
                        } while(iterator.hasNext());
                     }

                     task = (Runnable)workQueue.poll();
                  }
               }

               Thread.interrupted();
               safeRun(task);
            } while(task != null);

            int oldState = this.state;
            int i;
            NioHandle attachment;
            SelectionKey key;
            if ((oldState & Integer.MIN_VALUE) != 0) {
               synchronized(lock) {
                  int keyCount = selector.keys().size();
                  this.state = keyCount | Integer.MIN_VALUE;
                  if (keyCount == 0 && workQueue.isEmpty()) {
                     return;
                  }
               }

               synchronized(selector) {
                  Set<SelectionKey> keySet = selector.keys();
                  synchronized(keySet) {
                     keys = (SelectionKey[])keySet.toArray(keys);
                     Arrays.fill(keys, keySet.size(), keys.length, (Object)null);
                  }
               }

               for(i = 0; i < keys.length; ++i) {
                  key = keys[i];
                  if (key == null) {
                     break;
                  }

                  keys[i] = null;
                  attachment = (NioHandle)key.attachment();
                  if (attachment != null) {
                     IoUtils.safeClose((Closeable)key.channel());
                     attachment.forceTermination();
                  }
               }

               Arrays.fill(keys, 0, keys.length, (Object)null);
            }

            Thread.interrupted();

            try {
               if ((oldState & Integer.MIN_VALUE) != 0) {
                  Log.selectorLog.tracef("Beginning select on %s (shutdown in progress)", selector);
                  selector.selectNow();
               } else if (delayTime == Long.MAX_VALUE) {
                  Log.selectorLog.tracef("Beginning select on %s", selector);
                  this.polling = true;

                  try {
                     Runnable item = null;
                     synchronized(lock) {
                        item = (Runnable)workQueue.peek();
                     }

                     if (item != null) {
                        Log.log.tracef("SelectNow, queue is not empty", new Object[0]);
                        selector.selectNow();
                     } else {
                        Log.log.tracef("Select, queue is empty", new Object[0]);
                        selector.select();
                     }
                  } finally {
                     this.polling = false;
                  }
               } else {
                  long millis = 1L + delayTime / 1000000L;
                  Log.selectorLog.tracef("Beginning select on %s (with timeout)", selector);
                  this.polling = true;

                  try {
                     attachment = null;
                     Runnable item;
                     synchronized(lock) {
                        item = (Runnable)workQueue.peek();
                     }

                     if (item != null) {
                        Log.log.tracef("SelectNow, queue is not empty", new Object[0]);
                        selector.selectNow();
                     } else {
                        Log.log.tracef("Select, queue is empty", new Object[0]);
                        selector.select(millis);
                     }
                  } finally {
                     this.polling = false;
                  }
               }
            } catch (CancelledKeyException var81) {
               Log.selectorLog.trace("Spurious cancelled key exception");
            } catch (IOException var82) {
               Log.selectorLog.selectionError(var82);
            }

            Log.selectorLog.tracef("Selected on %s", selector);
            synchronized(selector) {
               Set<SelectionKey> selectedKeys = selector.selectedKeys();
               synchronized(selectedKeys) {
                  keys = (SelectionKey[])selectedKeys.toArray(keys);
                  Arrays.fill(keys, selectedKeys.size(), keys.length, (Object)null);
                  selectedKeys.clear();
               }
            }

            for(i = 0; i < keys.length; ++i) {
               key = keys[i];
               if (key == null) {
                  break;
               }

               keys[i] = null;

               try {
                  int ops = key.interestOps();
                  if (ops != 0) {
                     Log.selectorLog.tracef("Selected key %s for %s", key, key.channel());
                     NioHandle handle = (NioHandle)key.attachment();
                     if (handle == null) {
                        this.cancelKey(key, false);
                     } else {
                        Thread.interrupted();
                        Log.selectorLog.tracef("Calling handleReady key %s for %s", key.readyOps(), key.channel());
                        handle.handleReady(key.readyOps());
                     }
                  }
               } catch (CancelledKeyException var73) {
                  Log.selectorLog.tracef("Skipping selection of cancelled key %s", key);
               } catch (Throwable var74) {
                  Log.selectorLog.tracef(var74, "Unexpected failure of selection of key %s", key);
               }
            }
         }
      } finally {
         Log.log.tracef("Shutting down channel thread \"%s\"", this);
         IoUtils.safeClose(selector);
         this.getWorker().closeResource();
      }
   }

   private static void safeRun(Runnable command) {
      if (command != null) {
         try {
            Log.log.tracef("Running task %s", command);
            command.run();
         } catch (Throwable var2) {
            Log.log.taskFailed(command, var2);
         }
      }

   }

   public void execute(Runnable command) {
      if ((this.state & Integer.MIN_VALUE) != 0) {
         throw Log.log.threadExiting();
      } else {
         synchronized(this.workLock) {
            this.selectorWorkQueue.add(command);
            Log.log.tracef("Added task %s", command);
         }

         if (this.polling) {
            this.selector.wakeup();
         } else {
            Log.log.tracef("Not polling, no wakeup", new Object[0]);
         }

      }
   }

   void shutdown() {
      int oldState;
      do {
         oldState = this.state;
         if ((oldState & Integer.MIN_VALUE) != 0) {
            return;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, oldState | Integer.MIN_VALUE));

      if (currentThread() != this) {
         this.selector.wakeup();
      }

   }

   public XnioExecutor.Key executeAfter(Runnable command, long time, TimeUnit unit) {
      long millis = unit.toMillis(time);
      if ((this.state & Integer.MIN_VALUE) != 0) {
         throw Log.log.threadExiting();
      } else if (millis <= 0L) {
         this.execute(command);
         return XnioExecutor.Key.IMMEDIATE;
      } else {
         long deadline = System.nanoTime() - START_TIME + Math.min(millis, 9223372036853L) * 1000000L;
         TimeKey key = new TimeKey(deadline, command);
         synchronized(this.workLock) {
            TreeSet<TimeKey> queue = this.delayWorkQueue;
            queue.add(key);
            if (queue.iterator().next() == key && this.polling) {
               this.selector.wakeup();
            }

            return key;
         }
      }
   }

   public XnioExecutor.Key executeAtInterval(Runnable command, long time, TimeUnit unit) {
      long millis = unit.toMillis(time);
      RepeatKey repeatKey = new RepeatKey(command, millis);
      XnioExecutor.Key firstKey = this.executeAfter(repeatKey, millis, TimeUnit.MILLISECONDS);
      repeatKey.setFirst(firstKey);
      return repeatKey;
   }

   SelectionKey registerChannel(AbstractSelectableChannel channel) throws ClosedChannelException {
      if (currentThread() == this) {
         return channel.register(this.selector, 0);
      } else if (THREAD_SAFE_SELECTION_KEYS) {
         SelectionKey var11;
         try {
            var11 = channel.register(this.selector, 0);
         } finally {
            if (this.polling) {
               this.selector.wakeup();
            }

         }

         return var11;
      } else {
         SynchTask task = new SynchTask();
         this.queueTask(task);

         SelectionKey var3;
         try {
            this.selector.wakeup();
            var3 = channel.register(this.selector, 0);
         } finally {
            task.done();
         }

         return var3;
      }
   }

   void queueTask(Runnable task) {
      synchronized(this.workLock) {
         this.selectorWorkQueue.add(task);
      }
   }

   void cancelKey(SelectionKey key, boolean block) {
      assert key.selector() == this.selector;

      SelectableChannel channel = key.channel();
      if (currentThread() == this) {
         Log.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, "Cancelling key %s of %s (same thread)", key, channel);

         try {
            key.cancel();

            try {
               this.selector.selectNow();
            } catch (IOException var15) {
               Log.log.selectionError(var15);
            }
         } catch (Throwable var16) {
            Log.log.logf(FQCN, Logger.Level.TRACE, var16, "Error cancelling key %s of %s (same thread)", key, channel);
         }
      } else if (OLD_LOCKING) {
         Log.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, "Cancelling key %s of %s (same thread, old locking)", key, channel);
         SynchTask task = new SynchTask();
         this.queueTask(task);

         try {
            this.selector.wakeup();
            key.cancel();
         } catch (Throwable var13) {
            Log.log.logf(FQCN, Logger.Level.TRACE, var13, "Error cancelling key %s of %s (same thread, old locking)", key, channel);
         } finally {
            task.done();
         }
      } else {
         Log.log.logf(FQCN, Logger.Level.TRACE, (Throwable)null, "Cancelling key %s of %s (other thread)", key, channel);

         try {
            key.cancel();
            if (block) {
               SelectNowTask task = new SelectNowTask();
               this.queueTask(task);
               this.selector.wakeup();
               task.doWait();
            } else {
               this.selector.wakeup();
            }
         } catch (Throwable var12) {
            Log.log.logf(FQCN, Logger.Level.TRACE, var12, "Error cancelling key %s of %s (other thread)", key, channel);
         }
      }

   }

   void setOps(SelectionKey key, int ops) {
      if (currentThread() == this) {
         try {
            synchronized(key) {
               key.interestOps(key.interestOps() | ops);
            }
         } catch (CancelledKeyException var21) {
         }
      } else if (OLD_LOCKING) {
         SynchTask task = new SynchTask();
         this.queueTask(task);

         try {
            this.selector.wakeup();
            synchronized(key) {
               key.interestOps(key.interestOps() | ops);
            }
         } catch (CancelledKeyException var18) {
         } finally {
            task.done();
         }
      } else {
         try {
            synchronized(key) {
               key.interestOps(key.interestOps() | ops);
            }

            if (this.polling) {
               this.selector.wakeup();
            }
         } catch (CancelledKeyException var16) {
         }
      }

   }

   void clearOps(SelectionKey key, int ops) {
      if (currentThread() != this && OLD_LOCKING) {
         SynchTask task = new SynchTask();
         this.queueTask(task);

         try {
            this.selector.wakeup();
            synchronized(key) {
               key.interestOps(key.interestOps() & ~ops);
            }
         } catch (CancelledKeyException var13) {
         } finally {
            task.done();
         }
      } else {
         try {
            synchronized(key) {
               key.interestOps(key.interestOps() & ~ops);
            }
         } catch (CancelledKeyException var16) {
         }
      }

   }

   Selector getSelector() {
      return this.selector;
   }

   public boolean equals(Object obj) {
      return obj == this;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   final class SelectNowTask implements Runnable {
      final Thread thread = Thread.currentThread();
      volatile boolean done;

      void doWait() {
         while(!this.done) {
            LockSupport.park();
         }

      }

      public void run() {
         try {
            WorkerThread.this.selector.selectNow();
         } catch (IOException var2) {
         }

         this.done = true;
         LockSupport.unpark(this.thread);
      }
   }

   final class SynchTask implements Runnable {
      volatile boolean done;

      public void run() {
         while(!this.done) {
            LockSupport.park();
         }

      }

      void done() {
         this.done = true;
         LockSupport.unpark(WorkerThread.this);
      }
   }

   final class TimeKey implements XnioExecutor.Key, Comparable<TimeKey> {
      private final long deadline;
      private final long seq;
      private final Runnable command;

      TimeKey(long deadline, Runnable command) {
         this.seq = WorkerThread.seqGen.incrementAndGet();
         this.deadline = deadline;
         this.command = command;
      }

      public boolean remove() {
         synchronized(WorkerThread.this.workLock) {
            return WorkerThread.this.delayWorkQueue.remove(this);
         }
      }

      public int compareTo(TimeKey o) {
         int r = Long.signum(this.deadline - o.deadline);
         if (r == 0) {
            r = Long.signum(this.seq - o.seq);
         }

         return r;
      }
   }

   class RepeatKey implements XnioExecutor.Key, Runnable {
      private final Runnable command;
      private final long millis;
      private final AtomicReference<XnioExecutor.Key> current = new AtomicReference();

      RepeatKey(Runnable command, long millis) {
         this.command = command;
         this.millis = millis;
      }

      public boolean remove() {
         XnioExecutor.Key removed = (XnioExecutor.Key)this.current.getAndSet(this);

         assert removed != null;

         return removed != this && removed.remove();
      }

      void setFirst(XnioExecutor.Key key) {
         this.current.compareAndSet((Object)null, key);
      }

      public void run() {
         boolean var7 = false;

         try {
            var7 = true;
            this.command.run();
            var7 = false;
         } finally {
            if (var7) {
               XnioExecutor.Key ox = (XnioExecutor.Key)this.current.get();
               if (ox != this) {
                  XnioExecutor.Key nx = WorkerThread.this.executeAfter(this, this.millis, TimeUnit.MILLISECONDS);
                  if (!this.current.compareAndSet(ox, nx)) {
                     nx.remove();
                  }
               }

            }
         }

         XnioExecutor.Key o = (XnioExecutor.Key)this.current.get();
         if (o != this) {
            XnioExecutor.Key n = WorkerThread.this.executeAfter(this, this.millis, TimeUnit.MILLISECONDS);
            if (!this.current.compareAndSet(o, n)) {
               n.remove();
            }
         }

      }
   }

   static final class ConnectHandle extends NioHandle {
      private final FutureResult<StreamConnection> futureResult;
      private final NioSocketStreamConnection connection;
      private final ChannelListener<? super StreamConnection> openListener;

      ConnectHandle(WorkerThread workerThread, SelectionKey selectionKey, FutureResult<StreamConnection> futureResult, NioSocketStreamConnection connection, ChannelListener<? super StreamConnection> openListener) {
         super(workerThread, selectionKey);
         this.futureResult = futureResult;
         this.connection = connection;
         this.openListener = openListener;
      }

      void handleReady(int ops) {
         SocketChannel channel = this.getChannel();
         boolean ok = false;

         try {
            if (channel.finishConnect()) {
               Log.selectorLog.tracef("handleReady connect finished", new Object[0]);
               this.suspend(8);
               this.getSelectionKey().attach(this.connection.getConduit());
               if (this.futureResult.setResult(this.connection)) {
                  ok = true;
                  ChannelListeners.invokeChannelListener(this.connection, this.openListener);
               }
            }
         } catch (IOException var8) {
            Log.selectorLog.tracef("ConnectHandle.handleReady Exception, %s", var8);
            this.futureResult.setException(var8);
         } finally {
            if (!ok) {
               Log.selectorLog.tracef("!OK, closing connection", new Object[0]);
               IoUtils.safeClose((Closeable)this.connection);
            }

         }

      }

      private SocketChannel getChannel() {
         return (SocketChannel)this.getSelectionKey().channel();
      }

      void forceTermination() {
         this.futureResult.setCancelled();
         IoUtils.safeClose((Closeable)this.getChannel());
      }

      void terminated() {
      }
   }
}
