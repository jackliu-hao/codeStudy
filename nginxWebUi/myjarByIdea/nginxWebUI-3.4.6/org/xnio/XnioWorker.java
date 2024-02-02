package org.xnio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.jboss.logging.Logger;
import org.jboss.threads.EnhancedQueueExecutor;
import org.wildfly.common.Assert;
import org.wildfly.common.context.ContextManager;
import org.wildfly.common.context.Contextual;
import org.wildfly.common.net.CidrAddress;
import org.wildfly.common.net.CidrAddressTable;
import org.xnio._private.Messages;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.AssembledConnectedMessageChannel;
import org.xnio.channels.AssembledConnectedStreamChannel;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.Configurable;
import org.xnio.channels.ConnectedMessageChannel;
import org.xnio.channels.ConnectedStreamChannel;
import org.xnio.channels.MulticastMessageChannel;
import org.xnio.channels.StreamChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.DeflatingStreamSinkConduit;
import org.xnio.conduits.InflatingStreamSourceConduit;
import org.xnio.conduits.StreamSinkChannelWrappingConduit;
import org.xnio.conduits.StreamSourceChannelWrappingConduit;
import org.xnio.management.XnioServerMXBean;
import org.xnio.management.XnioWorkerMXBean;

public abstract class XnioWorker extends AbstractExecutorService implements Configurable, ExecutorService, XnioIoFactory, Contextual<XnioWorker> {
   private final Xnio xnio;
   private final TaskPool taskPool;
   private final String name;
   private final Runnable terminationTask;
   private final CidrAddressTable<InetSocketAddress> bindAddressTable;
   private volatile int taskSeq;
   private static final AtomicIntegerFieldUpdater<XnioWorker> taskSeqUpdater = AtomicIntegerFieldUpdater.newUpdater(XnioWorker.class, "taskSeq");
   private static final AtomicInteger seq = new AtomicInteger(1);
   private static final RuntimePermission CREATE_WORKER_PERMISSION = new RuntimePermission("createXnioWorker");
   private static final Logger log = Logger.getLogger("org.xnio");
   private static final ContextManager<XnioWorker> CONTEXT_MANAGER = (ContextManager)AccessController.doPrivileged(() -> {
      return new ContextManager(XnioWorker.class, "org.xnio.worker");
   });
   private static final Set<Option<?>> OPTIONS;
   private static final Set<Option<?>> EXTERNAL_POOL_OPTIONS;
   private static final IoFuture.HandlingNotifier<StreamConnection, FutureResult<ConnectedStreamChannel>> STREAM_WRAPPING_HANDLER;
   private static final IoFuture.HandlingNotifier<MessageConnection, FutureResult<ConnectedMessageChannel>> MESSAGE_WRAPPING_HANDLER;

   private int getNextSeq() {
      return taskSeqUpdater.incrementAndGet(this);
   }

   protected XnioWorker(Builder builder) {
      this.xnio = builder.xnio;
      this.terminationTask = builder.terminationTask;
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(CREATE_WORKER_PERMISSION);
      }

      String workerName = builder.getWorkerName();
      if (workerName == null) {
         workerName = "XNIO-" + seq.getAndIncrement();
      }

      this.name = workerName;
      boolean markThreadAsDaemon = builder.isDaemon();
      this.bindAddressTable = builder.getBindAddressConfigurations();
      Runnable terminationTask = new Runnable() {
         public void run() {
            XnioWorker.this.taskPoolTerminated();
         }
      };
      ExecutorService executorService = builder.getExternalExecutorService();
      if (executorService != null) {
         if (executorService instanceof EnhancedQueueExecutor) {
            this.taskPool = new ExternalTaskPool(new EnhancedQueueExecutorTaskPool((EnhancedQueueExecutor)executorService));
         } else if (executorService instanceof ThreadPoolExecutor) {
            this.taskPool = new ExternalTaskPool(new ThreadPoolExecutorTaskPool((ThreadPoolExecutor)executorService));
         } else {
            this.taskPool = new ExternalTaskPool(new ExecutorServiceTaskPool(executorService));
         }
      } else if (EnhancedQueueExecutor.DISABLE_HINT) {
         int poolSize = Math.max(builder.getMaxWorkerPoolSize(), builder.getCoreWorkerPoolSize());
         this.taskPool = new ThreadPoolExecutorTaskPool(new DefaultThreadPoolExecutor(poolSize, poolSize, builder.getWorkerKeepAlive(), TimeUnit.MILLISECONDS, new LinkedBlockingDeque(), new WorkerThreadFactory(builder.getThreadGroup(), builder.getWorkerStackSize(), markThreadAsDaemon), terminationTask));
      } else {
         this.taskPool = new EnhancedQueueExecutorTaskPool((new EnhancedQueueExecutor.Builder()).setCorePoolSize(builder.getCoreWorkerPoolSize()).setMaximumPoolSize(builder.getMaxWorkerPoolSize()).setKeepAliveTime(builder.getWorkerKeepAlive(), TimeUnit.MILLISECONDS).setThreadFactory(new WorkerThreadFactory(builder.getThreadGroup(), builder.getWorkerStackSize(), markThreadAsDaemon)).setTerminationTask(terminationTask).setRegisterMBean(true).setMBeanName(workerName).build());
      }

   }

   public static ContextManager<XnioWorker> getContextManager() {
      return CONTEXT_MANAGER;
   }

   public ContextManager<XnioWorker> getInstanceContextManager() {
      return getContextManager();
   }

   /** @deprecated */
   @Deprecated
   public AcceptingChannel<? extends ConnectedStreamChannel> createStreamServer(SocketAddress bindAddress, ChannelListener<? super AcceptingChannel<ConnectedStreamChannel>> acceptListener, OptionMap optionMap) throws IOException {
      final AcceptingChannel<StreamConnection> server = this.createStreamConnectionServer(bindAddress, (ChannelListener)null, optionMap);
      AcceptingChannel<ConnectedStreamChannel> acceptingChannel = new AcceptingChannel<ConnectedStreamChannel>() {
         public ConnectedStreamChannel accept() throws IOException {
            StreamConnection connection = (StreamConnection)server.accept();
            return connection == null ? null : new AssembledConnectedStreamChannel(connection, connection.getSourceChannel(), connection.getSinkChannel());
         }

         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedStreamChannel>> getAcceptSetter() {
            return ChannelListeners.getDelegatingSetter(server.getAcceptSetter(), this);
         }

         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedStreamChannel>> getCloseSetter() {
            return ChannelListeners.getDelegatingSetter(server.getCloseSetter(), this);
         }

         public SocketAddress getLocalAddress() {
            return server.getLocalAddress();
         }

         public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
            return server.getLocalAddress(type);
         }

         public void suspendAccepts() {
            server.suspendAccepts();
         }

         public void resumeAccepts() {
            server.resumeAccepts();
         }

         public boolean isAcceptResumed() {
            return server.isAcceptResumed();
         }

         public void wakeupAccepts() {
            server.wakeupAccepts();
         }

         public void awaitAcceptable() throws IOException {
            server.awaitAcceptable();
         }

         public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
            server.awaitAcceptable(time, timeUnit);
         }

         public XnioWorker getWorker() {
            return server.getWorker();
         }

         /** @deprecated */
         @Deprecated
         public XnioExecutor getAcceptThread() {
            return server.getAcceptThread();
         }

         public XnioIoThread getIoThread() {
            return server.getIoThread();
         }

         public void close() throws IOException {
            server.close();
         }

         public boolean isOpen() {
            return server.isOpen();
         }

         public boolean supportsOption(Option<?> option) {
            return server.supportsOption(option);
         }

         public <T> T getOption(Option<T> option) throws IOException {
            return server.getOption(option);
         }

         public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
            return server.setOption(option, value);
         }
      };
      acceptingChannel.getAcceptSetter().set(acceptListener);
      return acceptingChannel;
   }

   public AcceptingChannel<StreamConnection> createStreamConnectionServer(SocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
      Assert.checkNotNullParam("bindAddress", bindAddress);
      if (bindAddress instanceof InetSocketAddress) {
         return this.createTcpConnectionServer((InetSocketAddress)bindAddress, acceptListener, optionMap);
      } else if (bindAddress instanceof LocalSocketAddress) {
         return this.createLocalStreamConnectionServer((LocalSocketAddress)bindAddress, acceptListener, optionMap);
      } else {
         throw Messages.msg.badSockType(bindAddress.getClass());
      }
   }

   protected AcceptingChannel<StreamConnection> createTcpConnectionServer(InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
      throw Messages.msg.unsupported("createTcpConnectionServer");
   }

   protected AcceptingChannel<StreamConnection> createLocalStreamConnectionServer(LocalSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
      throw Messages.msg.unsupported("createLocalStreamConnectionServer");
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedStreamChannel> connectStream(SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, OptionMap optionMap) {
      FutureResult<ConnectedStreamChannel> futureResult = new FutureResult();
      ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
      IoFuture<StreamConnection> future = this.openStreamConnection(destination, nestedOpenListener, optionMap);
      future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
      futureResult.addCancelHandler(future);
      return futureResult.getIoFuture();
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedStreamChannel> connectStream(SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      FutureResult<ConnectedStreamChannel> futureResult = new FutureResult();
      ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
      IoFuture<StreamConnection> future = this.openStreamConnection(destination, nestedOpenListener, bindListener, optionMap);
      future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
      futureResult.addCancelHandler(future);
      return futureResult.getIoFuture();
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedStreamChannel> connectStream(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      FutureResult<ConnectedStreamChannel> futureResult = new FutureResult();
      ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
      IoFuture<StreamConnection> future = this.openStreamConnection(bindAddress, destination, nestedOpenListener, bindListener, optionMap);
      future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
      futureResult.addCancelHandler(future);
      return futureResult.getIoFuture();
   }

   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, OptionMap optionMap) {
      return this.chooseThread().openStreamConnection(destination, openListener, optionMap);
   }

   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.chooseThread().openStreamConnection(destination, openListener, bindListener, optionMap);
   }

   public IoFuture<StreamConnection> openStreamConnection(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.chooseThread().openStreamConnection(bindAddress, destination, openListener, bindListener, optionMap);
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedStreamChannel> acceptStream(SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      FutureResult<ConnectedStreamChannel> futureResult = new FutureResult();
      ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
      IoFuture<StreamConnection> future = this.acceptStreamConnection(destination, nestedOpenListener, bindListener, optionMap);
      future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
      futureResult.addCancelHandler(future);
      return futureResult.getIoFuture();
   }

   public IoFuture<StreamConnection> acceptStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.chooseThread().acceptStreamConnection(destination, openListener, bindListener, optionMap);
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedMessageChannel> connectDatagram(SocketAddress destination, ChannelListener<? super ConnectedMessageChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      FutureResult<ConnectedMessageChannel> futureResult = new FutureResult();
      ChannelListener<MessageConnection> nestedOpenListener = new MessageConnectionWrapListener(futureResult, openListener);
      IoFuture<MessageConnection> future = this.openMessageConnection(destination, nestedOpenListener, optionMap);
      future.addNotifier(MESSAGE_WRAPPING_HANDLER, futureResult);
      futureResult.addCancelHandler(future);
      return futureResult.getIoFuture();
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedMessageChannel> connectDatagram(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super ConnectedMessageChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      FutureResult<ConnectedMessageChannel> futureResult = new FutureResult();
      ChannelListener<MessageConnection> nestedOpenListener = new MessageConnectionWrapListener(futureResult, openListener);
      IoFuture<MessageConnection> future = this.openMessageConnection(destination, nestedOpenListener, optionMap);
      future.addNotifier(MESSAGE_WRAPPING_HANDLER, futureResult);
      futureResult.addCancelHandler(future);
      return futureResult.getIoFuture();
   }

   public IoFuture<MessageConnection> openMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, OptionMap optionMap) {
      return this.chooseThread().openMessageConnection(destination, openListener, optionMap);
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedMessageChannel> acceptDatagram(SocketAddress destination, ChannelListener<? super ConnectedMessageChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      FutureResult<ConnectedMessageChannel> futureResult = new FutureResult();
      ChannelListener<MessageConnection> nestedOpenListener = new MessageConnectionWrapListener(futureResult, openListener);
      IoFuture<MessageConnection> future = this.acceptMessageConnection(destination, nestedOpenListener, bindListener, optionMap);
      future.addNotifier(MESSAGE_WRAPPING_HANDLER, futureResult);
      futureResult.addCancelHandler(future);
      return futureResult.getIoFuture();
   }

   public IoFuture<MessageConnection> acceptMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.chooseThread().acceptMessageConnection(destination, openListener, bindListener, optionMap);
   }

   public MulticastMessageChannel createUdpServer(InetSocketAddress bindAddress, ChannelListener<? super MulticastMessageChannel> bindListener, OptionMap optionMap) throws IOException {
      throw Messages.msg.unsupported("createUdpServer");
   }

   public MulticastMessageChannel createUdpServer(InetSocketAddress bindAddress, OptionMap optionMap) throws IOException {
      return this.createUdpServer(bindAddress, ChannelListeners.nullChannelListener(), optionMap);
   }

   /** @deprecated */
   @Deprecated
   public void createPipe(ChannelListener<? super StreamChannel> leftOpenListener, ChannelListener<? super StreamChannel> rightOpenListener, OptionMap optionMap) throws IOException {
      ChannelPipe<StreamChannel, StreamChannel> pipe = this.createFullDuplexPipe();
      boolean establishWriting = optionMap.get(Options.WORKER_ESTABLISH_WRITING, false);
      StreamChannel left = (StreamChannel)pipe.getLeftSide();
      XnioExecutor leftExec = establishWriting ? left.getWriteThread() : left.getReadThread();
      StreamChannel right = (StreamChannel)pipe.getRightSide();
      XnioExecutor rightExec = establishWriting ? right.getWriteThread() : right.getReadThread();
      leftExec.execute(ChannelListeners.getChannelListenerTask(left, (ChannelListener)leftOpenListener));
      rightExec.execute(ChannelListeners.getChannelListenerTask(right, (ChannelListener)rightOpenListener));
   }

   /** @deprecated */
   @Deprecated
   public void createOneWayPipe(ChannelListener<? super StreamSourceChannel> sourceListener, ChannelListener<? super StreamSinkChannel> sinkListener, OptionMap optionMap) throws IOException {
      ChannelPipe<StreamSourceChannel, StreamSinkChannel> pipe = this.createHalfDuplexPipe();
      StreamSourceChannel left = (StreamSourceChannel)pipe.getLeftSide();
      XnioExecutor leftExec = left.getReadThread();
      StreamSinkChannel right = (StreamSinkChannel)pipe.getRightSide();
      XnioExecutor rightExec = right.getWriteThread();
      leftExec.execute(ChannelListeners.getChannelListenerTask(left, (ChannelListener)sourceListener));
      rightExec.execute(ChannelListeners.getChannelListenerTask(right, (ChannelListener)sinkListener));
   }

   public StreamSourceChannel getInflatingChannel(StreamSourceChannel delegate, OptionMap options) throws IOException {
      boolean nowrap;
      switch ((CompressionType)options.get(Options.COMPRESSION_TYPE, CompressionType.DEFLATE)) {
         case DEFLATE:
            nowrap = false;
            break;
         case GZIP:
            nowrap = true;
            break;
         default:
            throw Messages.msg.badCompressionFormat();
      }

      return this.getInflatingChannel(delegate, new Inflater(nowrap));
   }

   protected StreamSourceChannel getInflatingChannel(StreamSourceChannel delegate, Inflater inflater) throws IOException {
      return new ConduitStreamSourceChannel(Configurable.EMPTY, new InflatingStreamSourceConduit(new StreamSourceChannelWrappingConduit(delegate), inflater));
   }

   public StreamSinkChannel getDeflatingChannel(StreamSinkChannel delegate, OptionMap options) throws IOException {
      int level = options.get(Options.COMPRESSION_LEVEL, -1);
      boolean nowrap;
      switch ((CompressionType)options.get(Options.COMPRESSION_TYPE, CompressionType.DEFLATE)) {
         case DEFLATE:
            nowrap = false;
            break;
         case GZIP:
            nowrap = true;
            break;
         default:
            throw Messages.msg.badCompressionFormat();
      }

      return this.getDeflatingChannel(delegate, new Deflater(level, nowrap));
   }

   protected StreamSinkChannel getDeflatingChannel(StreamSinkChannel delegate, Deflater deflater) throws IOException {
      return new ConduitStreamSinkChannel(Configurable.EMPTY, new DeflatingStreamSinkConduit(new StreamSinkChannelWrappingConduit(delegate), deflater));
   }

   public ChannelPipe<StreamChannel, StreamChannel> createFullDuplexPipe() throws IOException {
      return this.chooseThread().createFullDuplexPipe();
   }

   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection() throws IOException {
      return this.chooseThread().createFullDuplexPipeConnection();
   }

   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe() throws IOException {
      return this.chooseThread().createHalfDuplexPipe();
   }

   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory peer) throws IOException {
      return this.chooseThread().createFullDuplexPipeConnection(peer);
   }

   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory peer) throws IOException {
      return this.chooseThread().createHalfDuplexPipe(peer);
   }

   public abstract void shutdown();

   public abstract List<Runnable> shutdownNow();

   public abstract boolean isShutdown();

   public abstract boolean isTerminated();

   public abstract boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException;

   public abstract void awaitTermination() throws InterruptedException;

   public final XnioIoThread getIoThread() {
      return this.chooseThread();
   }

   public abstract XnioIoThread getIoThread(int var1);

   protected Runnable getTerminationTask() {
      return this.terminationTask;
   }

   protected void taskPoolTerminated() {
   }

   protected void shutDownTaskPool() {
      if (this.isTaskPoolExternal()) {
         this.taskPoolTerminated();
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               XnioWorker.this.taskPool.shutdown();
               return null;
            }
         });
      }

   }

   protected List<Runnable> shutDownTaskPoolNow() {
      return !this.isTaskPoolExternal() ? (List)AccessController.doPrivileged(new PrivilegedAction<List<Runnable>>() {
         public List<Runnable> run() {
            return XnioWorker.this.taskPool.shutdownNow();
         }
      }) : Collections.emptyList();
   }

   protected boolean isTaskPoolExternal() {
      return this.taskPool instanceof ExternalTaskPool;
   }

   public void execute(Runnable command) {
      this.taskPool.execute(command);
   }

   public abstract int getIoThreadCount();

   public boolean supportsOption(Option<?> option) {
      return this.taskPool instanceof ExternalTaskPool ? EXTERNAL_POOL_OPTIONS.contains(option) : OPTIONS.contains(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (!this.supportsOption(option)) {
         return null;
      } else if (option.equals(Options.WORKER_TASK_CORE_THREADS)) {
         return option.cast(this.taskPool.getCorePoolSize());
      } else if (option.equals(Options.WORKER_TASK_MAX_THREADS)) {
         return option.cast(this.taskPool.getMaximumPoolSize());
      } else {
         return option.equals(Options.WORKER_TASK_KEEPALIVE) ? option.cast((int)Math.min(2147483647L, this.taskPool.getKeepAliveTime(TimeUnit.MILLISECONDS))) : null;
      }
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      if (!this.supportsOption(option)) {
         return null;
      } else {
         int old;
         if (option.equals(Options.WORKER_TASK_CORE_THREADS)) {
            old = this.taskPool.getCorePoolSize();
            this.taskPool.setCorePoolSize((Integer)Options.WORKER_TASK_CORE_THREADS.cast(value));
            return option.cast(old);
         } else if (option.equals(Options.WORKER_TASK_MAX_THREADS)) {
            old = this.taskPool.getMaximumPoolSize();
            this.taskPool.setMaximumPoolSize((Integer)Options.WORKER_TASK_MAX_THREADS.cast(value));
            return option.cast(old);
         } else if (option.equals(Options.WORKER_TASK_KEEPALIVE)) {
            long old = this.taskPool.getKeepAliveTime(TimeUnit.MILLISECONDS);
            this.taskPool.setKeepAliveTime((long)(Integer)Options.WORKER_TASK_KEEPALIVE.cast(value), TimeUnit.MILLISECONDS);
            return option.cast((int)Math.min(2147483647L, old));
         } else {
            return null;
         }
      }
   }

   public Xnio getXnio() {
      return this.xnio;
   }

   public String getName() {
      return this.name;
   }

   protected abstract XnioIoThread chooseThread();

   protected final int getCoreWorkerPoolSize() {
      return this.taskPool.getCorePoolSize();
   }

   protected final int getBusyWorkerThreadCount() {
      return this.taskPool.getActiveCount();
   }

   protected final int getWorkerPoolSize() {
      return this.taskPool.getPoolSize();
   }

   protected final int getMaxWorkerPoolSize() {
      return this.taskPool.getMaximumPoolSize();
   }

   protected final int getWorkerQueueSize() {
      return this.taskPool.getQueueSize();
   }

   protected CidrAddressTable<InetSocketAddress> getBindAddressTable() {
      return this.bindAddressTable;
   }

   public InetSocketAddress getBindAddress(InetAddress destination) {
      return (InetSocketAddress)this.bindAddressTable.get(destination);
   }

   public abstract XnioWorkerMXBean getMXBean();

   protected abstract ManagementRegistration registerServerMXBean(XnioServerMXBean var1);

   static {
      AccessController.doPrivileged(() -> {
         CONTEXT_MANAGER.setGlobalDefaultSupplier(() -> {
            return DefaultXnioWorkerHolder.INSTANCE;
         });
         return null;
      });
      OPTIONS = Option.setBuilder().add(Options.WORKER_TASK_CORE_THREADS).add(Options.WORKER_TASK_MAX_THREADS).add(Options.WORKER_TASK_KEEPALIVE).create();
      EXTERNAL_POOL_OPTIONS = Option.setBuilder().create();
      STREAM_WRAPPING_HANDLER = new IoFuture.HandlingNotifier<StreamConnection, FutureResult<ConnectedStreamChannel>>() {
         public void handleCancelled(FutureResult<ConnectedStreamChannel> attachment) {
            attachment.setCancelled();
         }

         public void handleFailed(IOException exception, FutureResult<ConnectedStreamChannel> attachment) {
            attachment.setException(exception);
         }
      };
      MESSAGE_WRAPPING_HANDLER = new IoFuture.HandlingNotifier<MessageConnection, FutureResult<ConnectedMessageChannel>>() {
         public void handleCancelled(FutureResult<ConnectedMessageChannel> attachment) {
            attachment.setCancelled();
         }

         public void handleFailed(IOException exception, FutureResult<ConnectedMessageChannel> attachment) {
            attachment.setException(exception);
         }
      };
   }

   static class ExternalTaskPool implements TaskPool {
      private final TaskPool delegate;

      ExternalTaskPool(TaskPool delegate) {
         this.delegate = delegate;
      }

      public void shutdown() {
      }

      public List<Runnable> shutdownNow() {
         return Collections.emptyList();
      }

      public void execute(Runnable command) {
         this.delegate.execute(command);
      }

      public int getCorePoolSize() {
         return this.delegate.getCorePoolSize();
      }

      public int getMaximumPoolSize() {
         return this.delegate.getMaximumPoolSize();
      }

      public long getKeepAliveTime(TimeUnit unit) {
         return this.delegate.getKeepAliveTime(unit);
      }

      public void setCorePoolSize(int size) {
         this.delegate.setCorePoolSize(size);
      }

      public void setMaximumPoolSize(int size) {
         this.delegate.setMaximumPoolSize(size);
      }

      public void setKeepAliveTime(long time, TimeUnit unit) {
         this.delegate.setKeepAliveTime(time, unit);
      }

      public int getActiveCount() {
         return this.delegate.getActiveCount();
      }

      public int getPoolSize() {
         return this.delegate.getPoolSize();
      }

      public int getQueueSize() {
         return this.delegate.getQueueSize();
      }
   }

   static class ExecutorServiceTaskPool implements TaskPool {
      private final ExecutorService delegate;

      ExecutorServiceTaskPool(ExecutorService delegate) {
         this.delegate = delegate;
      }

      public void shutdown() {
         this.delegate.shutdown();
      }

      public List<Runnable> shutdownNow() {
         return this.delegate.shutdownNow();
      }

      public void execute(Runnable command) {
         this.delegate.execute(command);
      }

      public int getCorePoolSize() {
         return -1;
      }

      public int getMaximumPoolSize() {
         return -1;
      }

      public long getKeepAliveTime(TimeUnit unit) {
         return -1L;
      }

      public void setCorePoolSize(int size) {
      }

      public void setMaximumPoolSize(int size) {
      }

      public void setKeepAliveTime(long time, TimeUnit unit) {
      }

      public int getActiveCount() {
         return -1;
      }

      public int getPoolSize() {
         return -1;
      }

      public int getQueueSize() {
         return -1;
      }
   }

   static class EnhancedQueueExecutorTaskPool implements TaskPool {
      private final EnhancedQueueExecutor executor;

      EnhancedQueueExecutorTaskPool(EnhancedQueueExecutor executor) {
         this.executor = executor;
      }

      public void shutdown() {
         this.executor.shutdown();
      }

      public List<Runnable> shutdownNow() {
         return this.executor.shutdownNow();
      }

      public void execute(Runnable command) {
         this.executor.execute(command);
      }

      public int getCorePoolSize() {
         return this.executor.getCorePoolSize();
      }

      public int getMaximumPoolSize() {
         return this.executor.getMaximumPoolSize();
      }

      public long getKeepAliveTime(TimeUnit unit) {
         return this.executor.getKeepAliveTime(unit);
      }

      public void setCorePoolSize(int size) {
         this.executor.setCorePoolSize(size);
      }

      public void setMaximumPoolSize(int size) {
         this.executor.setMaximumPoolSize(size);
      }

      public void setKeepAliveTime(long time, TimeUnit unit) {
         this.executor.setKeepAliveTime(time, unit);
      }

      public int getActiveCount() {
         return this.executor.getActiveCount();
      }

      public int getPoolSize() {
         return this.executor.getPoolSize();
      }

      public int getQueueSize() {
         return this.executor.getQueueSize();
      }
   }

   static class ThreadPoolExecutorTaskPool implements TaskPool {
      private final ThreadPoolExecutor delegate;

      ThreadPoolExecutorTaskPool(ThreadPoolExecutor delegate) {
         this.delegate = delegate;
      }

      public void shutdown() {
         this.delegate.shutdown();
      }

      public List<Runnable> shutdownNow() {
         return this.delegate.shutdownNow();
      }

      public void execute(Runnable command) {
         this.delegate.execute(command);
      }

      public int getCorePoolSize() {
         return this.delegate.getCorePoolSize();
      }

      public int getMaximumPoolSize() {
         return this.delegate.getMaximumPoolSize();
      }

      public long getKeepAliveTime(TimeUnit unit) {
         return this.delegate.getKeepAliveTime(unit);
      }

      public void setCorePoolSize(int size) {
         this.delegate.setCorePoolSize(size);
      }

      public void setMaximumPoolSize(int size) {
         this.delegate.setMaximumPoolSize(size);
      }

      public void setKeepAliveTime(long time, TimeUnit unit) {
         this.delegate.setKeepAliveTime(time, unit);
      }

      public int getActiveCount() {
         return this.delegate.getActiveCount();
      }

      public int getPoolSize() {
         return this.delegate.getPoolSize();
      }

      public int getQueueSize() {
         return this.delegate.getQueue().size();
      }
   }

   static class DefaultThreadPoolExecutor extends ThreadPoolExecutor {
      private final Runnable terminationTask;

      DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, Runnable terminationTask) {
         super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
         this.terminationTask = terminationTask;
      }

      protected void terminated() {
         this.terminationTask.run();
      }

      public void setCorePoolSize(int size) {
         this.setMaximumPoolSize(size);
      }

      public void setMaximumPoolSize(int size) {
         if (size > this.getCorePoolSize()) {
            super.setMaximumPoolSize(size);
            super.setCorePoolSize(size);
         } else {
            super.setCorePoolSize(size);
            super.setMaximumPoolSize(size);
         }

      }
   }

   interface TaskPool {
      void shutdown();

      List<Runnable> shutdownNow();

      void execute(Runnable var1);

      int getCorePoolSize();

      int getMaximumPoolSize();

      long getKeepAliveTime(TimeUnit var1);

      void setCorePoolSize(int var1);

      void setMaximumPoolSize(int var1);

      void setKeepAliveTime(long var1, TimeUnit var3);

      int getActiveCount();

      int getPoolSize();

      int getQueueSize();
   }

   class WorkerThreadFactory implements ThreadFactory {
      private final ThreadGroup threadGroup;
      private final long stackSize;
      private final boolean markThreadAsDaemon;

      WorkerThreadFactory(ThreadGroup threadGroup, long stackSize, boolean markThreadAsDaemon) {
         this.threadGroup = threadGroup;
         this.stackSize = stackSize;
         this.markThreadAsDaemon = markThreadAsDaemon;
      }

      public Thread newThread(final Runnable r) {
         return (Thread)AccessController.doPrivileged(new PrivilegedAction<Thread>() {
            public Thread run() {
               Thread taskThread = new Thread(WorkerThreadFactory.this.threadGroup, new Runnable() {
                  public void run() {
                     try {
                        r.run();
                     } finally {
                        XnioWorker.this.xnio.handleThreadExit();
                     }

                  }
               }, XnioWorker.this.name + " task-" + XnioWorker.this.getNextSeq(), WorkerThreadFactory.this.stackSize);
               if (WorkerThreadFactory.this.markThreadAsDaemon) {
                  taskThread.setDaemon(true);
               }

               return taskThread;
            }
         });
      }
   }

   static class MessageConnectionWrapListener implements ChannelListener<MessageConnection> {
      private final FutureResult<ConnectedMessageChannel> futureResult;
      private final ChannelListener<? super ConnectedMessageChannel> openListener;

      public MessageConnectionWrapListener(FutureResult<ConnectedMessageChannel> futureResult, ChannelListener<? super ConnectedMessageChannel> openListener) {
         this.futureResult = futureResult;
         this.openListener = openListener;
      }

      public void handleEvent(MessageConnection channel) {
         AssembledConnectedMessageChannel assembledChannel = new AssembledConnectedMessageChannel(channel, channel.getSourceChannel(), channel.getSinkChannel());
         if (!this.futureResult.setResult(assembledChannel)) {
            IoUtils.safeClose((Closeable)assembledChannel);
         } else {
            ChannelListeners.invokeChannelListener(assembledChannel, this.openListener);
         }

      }
   }

   static class StreamConnectionWrapListener implements ChannelListener<StreamConnection> {
      private final FutureResult<ConnectedStreamChannel> futureResult;
      private final ChannelListener<? super ConnectedStreamChannel> openListener;

      public StreamConnectionWrapListener(FutureResult<ConnectedStreamChannel> futureResult, ChannelListener<? super ConnectedStreamChannel> openListener) {
         this.futureResult = futureResult;
         this.openListener = openListener;
      }

      public void handleEvent(StreamConnection channel) {
         AssembledConnectedStreamChannel assembledChannel = new AssembledConnectedStreamChannel(channel, channel.getSourceChannel(), channel.getSinkChannel());
         if (!this.futureResult.setResult(assembledChannel)) {
            IoUtils.safeClose((Closeable)assembledChannel);
         } else {
            ChannelListeners.invokeChannelListener(assembledChannel, this.openListener);
         }

      }
   }

   public static class Builder {
      private final Xnio xnio;
      private ExecutorService externalExecutorService;
      private Runnable terminationTask;
      private String workerName;
      private int coreWorkerPoolSize = 4;
      private int maxWorkerPoolSize = 16;
      private ThreadGroup threadGroup;
      private boolean daemon;
      private int workerKeepAlive = 60000;
      private int workerIoThreads = 1;
      private long workerStackSize = 0L;
      private CidrAddressTable<InetSocketAddress> bindAddressConfigurations = new CidrAddressTable();

      protected Builder(Xnio xnio) {
         this.xnio = xnio;
      }

      public Xnio getXnio() {
         return this.xnio;
      }

      public Builder populateFromOptions(OptionMap optionMap) {
         this.setWorkerName((String)optionMap.get(Options.WORKER_NAME));
         this.setCoreWorkerPoolSize(optionMap.get(Options.WORKER_TASK_CORE_THREADS, this.coreWorkerPoolSize));
         this.setMaxWorkerPoolSize(optionMap.get(Options.WORKER_TASK_MAX_THREADS, this.maxWorkerPoolSize));
         this.setDaemon(optionMap.get(Options.THREAD_DAEMON, this.daemon));
         this.setWorkerKeepAlive(optionMap.get(Options.WORKER_TASK_KEEPALIVE, this.workerKeepAlive));
         if (optionMap.contains(Options.WORKER_IO_THREADS)) {
            this.setWorkerIoThreads(optionMap.get(Options.WORKER_IO_THREADS, 1));
         } else if (optionMap.contains(Options.WORKER_READ_THREADS) || optionMap.contains(Options.WORKER_WRITE_THREADS)) {
            this.setWorkerIoThreads(Math.max(optionMap.get(Options.WORKER_READ_THREADS, 1), optionMap.get(Options.WORKER_WRITE_THREADS, 1)));
         }

         this.setWorkerStackSize(optionMap.get(Options.STACK_SIZE, this.workerStackSize));
         return this;
      }

      public Builder addBindAddressConfiguration(CidrAddress cidrAddress, InetAddress bindAddress) {
         return this.addBindAddressConfiguration(cidrAddress, new InetSocketAddress(bindAddress, 0));
      }

      public Builder addBindAddressConfiguration(CidrAddress cidrAddress, InetSocketAddress bindAddress) {
         Class<? extends InetAddress> networkAddrClass = cidrAddress.getNetworkAddress().getClass();
         if (bindAddress.isUnresolved()) {
            throw Messages.msg.addressUnresolved(bindAddress);
         } else if (networkAddrClass != bindAddress.getAddress().getClass()) {
            throw Messages.msg.mismatchAddressType(networkAddrClass, bindAddress.getAddress().getClass());
         } else {
            this.bindAddressConfigurations.put(cidrAddress, bindAddress);
            return this;
         }
      }

      public Builder setBindAddressConfigurations(CidrAddressTable<InetSocketAddress> newTable) {
         this.bindAddressConfigurations = newTable;
         return this;
      }

      public CidrAddressTable<InetSocketAddress> getBindAddressConfigurations() {
         return this.bindAddressConfigurations;
      }

      public Runnable getTerminationTask() {
         return this.terminationTask;
      }

      public Builder setTerminationTask(Runnable terminationTask) {
         this.terminationTask = terminationTask;
         return this;
      }

      public String getWorkerName() {
         return this.workerName;
      }

      public Builder setWorkerName(String workerName) {
         this.workerName = workerName;
         return this;
      }

      public int getCoreWorkerPoolSize() {
         return this.coreWorkerPoolSize;
      }

      public Builder setCoreWorkerPoolSize(int coreWorkerPoolSize) {
         Assert.checkMinimumParameter("coreWorkerPoolSize", 0, coreWorkerPoolSize);
         this.coreWorkerPoolSize = coreWorkerPoolSize;
         return this;
      }

      public int getMaxWorkerPoolSize() {
         return this.maxWorkerPoolSize;
      }

      public Builder setMaxWorkerPoolSize(int maxWorkerPoolSize) {
         Assert.checkMinimumParameter("maxWorkerPoolSize", 0, maxWorkerPoolSize);
         this.maxWorkerPoolSize = maxWorkerPoolSize;
         return this;
      }

      public ThreadGroup getThreadGroup() {
         return this.threadGroup;
      }

      public Builder setThreadGroup(ThreadGroup threadGroup) {
         this.threadGroup = threadGroup;
         return this;
      }

      public boolean isDaemon() {
         return this.daemon;
      }

      public Builder setDaemon(boolean daemon) {
         this.daemon = daemon;
         return this;
      }

      public long getWorkerKeepAlive() {
         return (long)this.workerKeepAlive;
      }

      public Builder setWorkerKeepAlive(int workerKeepAlive) {
         Assert.checkMinimumParameter("workerKeepAlive", 0, workerKeepAlive);
         this.workerKeepAlive = workerKeepAlive;
         return this;
      }

      public int getWorkerIoThreads() {
         return this.workerIoThreads;
      }

      public Builder setWorkerIoThreads(int workerIoThreads) {
         Assert.checkMinimumParameter("workerIoThreads", 0, workerIoThreads);
         this.workerIoThreads = workerIoThreads;
         return this;
      }

      public long getWorkerStackSize() {
         return this.workerStackSize;
      }

      public Builder setWorkerStackSize(long workerStackSize) {
         Assert.checkMinimumParameter("workerStackSize", 0L, workerStackSize);
         this.workerStackSize = workerStackSize;
         return this;
      }

      public ExecutorService getExternalExecutorService() {
         return this.externalExecutorService;
      }

      public Builder setExternalExecutorService(ExecutorService executorService) {
         this.externalExecutorService = executorService;
         return this;
      }

      public XnioWorker build() {
         return this.xnio.build(this);
      }
   }
}
