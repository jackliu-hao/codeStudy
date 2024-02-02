/*      */ package org.xnio;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.SocketAddress;
/*      */ import java.nio.channels.Channel;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.AbstractExecutorService;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.ExecutorService;
/*      */ import java.util.concurrent.LinkedBlockingDeque;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*      */ import java.util.zip.Deflater;
/*      */ import java.util.zip.Inflater;
/*      */ import org.jboss.logging.Logger;
/*      */ import org.jboss.threads.EnhancedQueueExecutor;
/*      */ import org.wildfly.common.Assert;
/*      */ import org.wildfly.common.context.ContextManager;
/*      */ import org.wildfly.common.context.Contextual;
/*      */ import org.wildfly.common.net.CidrAddress;
/*      */ import org.wildfly.common.net.CidrAddressTable;
/*      */ import org.xnio._private.Messages;
/*      */ import org.xnio.channels.AcceptingChannel;
/*      */ import org.xnio.channels.AssembledConnectedMessageChannel;
/*      */ import org.xnio.channels.AssembledConnectedStreamChannel;
/*      */ import org.xnio.channels.BoundChannel;
/*      */ import org.xnio.channels.CloseableChannel;
/*      */ import org.xnio.channels.Configurable;
/*      */ import org.xnio.channels.ConnectedChannel;
/*      */ import org.xnio.channels.ConnectedMessageChannel;
/*      */ import org.xnio.channels.ConnectedStreamChannel;
/*      */ import org.xnio.channels.MulticastMessageChannel;
/*      */ import org.xnio.channels.ReadableMessageChannel;
/*      */ import org.xnio.channels.StreamChannel;
/*      */ import org.xnio.channels.StreamSinkChannel;
/*      */ import org.xnio.channels.StreamSourceChannel;
/*      */ import org.xnio.channels.WritableMessageChannel;
/*      */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*      */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*      */ import org.xnio.conduits.DeflatingStreamSinkConduit;
/*      */ import org.xnio.conduits.InflatingStreamSourceConduit;
/*      */ import org.xnio.conduits.StreamSinkChannelWrappingConduit;
/*      */ import org.xnio.conduits.StreamSinkConduit;
/*      */ import org.xnio.conduits.StreamSourceChannelWrappingConduit;
/*      */ import org.xnio.conduits.StreamSourceConduit;
/*      */ import org.xnio.management.XnioServerMXBean;
/*      */ import org.xnio.management.XnioWorkerMXBean;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class XnioWorker
/*      */   extends AbstractExecutorService
/*      */   implements Configurable, ExecutorService, XnioIoFactory, Contextual<XnioWorker>
/*      */ {
/*      */   private final Xnio xnio;
/*      */   private final TaskPool taskPool;
/*      */   private final String name;
/*      */   private final Runnable terminationTask;
/*      */   private final CidrAddressTable<InetSocketAddress> bindAddressTable;
/*      */   private volatile int taskSeq;
/*   93 */   private static final AtomicIntegerFieldUpdater<XnioWorker> taskSeqUpdater = AtomicIntegerFieldUpdater.newUpdater(XnioWorker.class, "taskSeq");
/*      */   
/*   95 */   private static final AtomicInteger seq = new AtomicInteger(1);
/*      */   
/*   97 */   private static final RuntimePermission CREATE_WORKER_PERMISSION = new RuntimePermission("createXnioWorker");
/*      */   
/*      */   private int getNextSeq() {
/*  100 */     return taskSeqUpdater.incrementAndGet(this);
/*      */   }
/*      */   
/*  103 */   private static final Logger log = Logger.getLogger("org.xnio");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected XnioWorker(Builder builder) {
/*  111 */     this.xnio = builder.xnio;
/*  112 */     this.terminationTask = builder.terminationTask;
/*  113 */     SecurityManager sm = System.getSecurityManager();
/*  114 */     if (sm != null) {
/*  115 */       sm.checkPermission(CREATE_WORKER_PERMISSION);
/*      */     }
/*  117 */     String workerName = builder.getWorkerName();
/*  118 */     if (workerName == null) {
/*  119 */       workerName = "XNIO-" + seq.getAndIncrement();
/*      */     }
/*  121 */     this.name = workerName;
/*  122 */     boolean markThreadAsDaemon = builder.isDaemon();
/*  123 */     this.bindAddressTable = builder.getBindAddressConfigurations();
/*  124 */     Runnable terminationTask = new Runnable() {
/*      */         public void run() {
/*  126 */           XnioWorker.this.taskPoolTerminated();
/*      */         }
/*      */       };
/*  129 */     ExecutorService executorService = builder.getExternalExecutorService();
/*  130 */     if (executorService != null) {
/*  131 */       if (executorService instanceof EnhancedQueueExecutor) {
/*  132 */         this.taskPool = new ExternalTaskPool(new EnhancedQueueExecutorTaskPool((EnhancedQueueExecutor)executorService));
/*      */       }
/*  134 */       else if (executorService instanceof ThreadPoolExecutor) {
/*  135 */         this.taskPool = new ExternalTaskPool(new ThreadPoolExecutorTaskPool((ThreadPoolExecutor)executorService));
/*      */       } else {
/*  137 */         this.taskPool = new ExternalTaskPool(new ExecutorServiceTaskPool(executorService));
/*      */       } 
/*  139 */     } else if (EnhancedQueueExecutor.DISABLE_HINT) {
/*  140 */       int poolSize = Math.max(builder.getMaxWorkerPoolSize(), builder.getCoreWorkerPoolSize());
/*  141 */       this
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  146 */         .taskPool = new ThreadPoolExecutorTaskPool(new DefaultThreadPoolExecutor(poolSize, poolSize, builder.getWorkerKeepAlive(), TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new WorkerThreadFactory(builder.getThreadGroup(), builder.getWorkerStackSize(), markThreadAsDaemon), terminationTask));
/*      */     } else {
/*      */       
/*  149 */       this
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  157 */         .taskPool = new EnhancedQueueExecutorTaskPool((new EnhancedQueueExecutor.Builder()).setCorePoolSize(builder.getCoreWorkerPoolSize()).setMaximumPoolSize(builder.getMaxWorkerPoolSize()).setKeepAliveTime(builder.getWorkerKeepAlive(), TimeUnit.MILLISECONDS).setThreadFactory(new WorkerThreadFactory(builder.getThreadGroup(), builder.getWorkerStackSize(), markThreadAsDaemon)).setTerminationTask(terminationTask).setRegisterMBean(true).setMBeanName(workerName).build());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  168 */   private static final ContextManager<XnioWorker> CONTEXT_MANAGER = AccessController.<ContextManager<XnioWorker>>doPrivileged(() -> new ContextManager(XnioWorker.class, "org.xnio.worker"));
/*      */   
/*      */   static {
/*  171 */     AccessController.doPrivileged(() -> {
/*      */           CONTEXT_MANAGER.setGlobalDefaultSupplier(());
/*      */           return null;
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ContextManager<XnioWorker> getContextManager() {
/*  183 */     return CONTEXT_MANAGER;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ContextManager<XnioWorker> getInstanceContextManager() {
/*  192 */     return getContextManager();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public AcceptingChannel<? extends ConnectedStreamChannel> createStreamServer(SocketAddress bindAddress, ChannelListener<? super AcceptingChannel<ConnectedStreamChannel>> acceptListener, OptionMap optionMap) throws IOException {
/*  214 */     final AcceptingChannel<StreamConnection> server = createStreamConnectionServer(bindAddress, null, optionMap);
/*  215 */     AcceptingChannel<ConnectedStreamChannel> acceptingChannel = new AcceptingChannel<ConnectedStreamChannel>() {
/*      */         public ConnectedStreamChannel accept() throws IOException {
/*  217 */           StreamConnection connection = (StreamConnection)server.accept();
/*  218 */           return (connection == null) ? null : (ConnectedStreamChannel)new AssembledConnectedStreamChannel(connection, (StreamSourceChannel)connection.getSourceChannel(), (StreamSinkChannel)connection.getSinkChannel());
/*      */         }
/*      */         
/*      */         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedStreamChannel>> getAcceptSetter() {
/*  222 */           return ChannelListeners.getDelegatingSetter(server.getAcceptSetter(), this);
/*      */         }
/*      */         
/*      */         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedStreamChannel>> getCloseSetter() {
/*  226 */           return ChannelListeners.getDelegatingSetter(server.getCloseSetter(), this);
/*      */         }
/*      */         
/*      */         public SocketAddress getLocalAddress() {
/*  230 */           return server.getLocalAddress();
/*      */         }
/*      */         
/*      */         public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/*  234 */           return (A)server.getLocalAddress(type);
/*      */         }
/*      */         
/*      */         public void suspendAccepts() {
/*  238 */           server.suspendAccepts();
/*      */         }
/*      */         
/*      */         public void resumeAccepts() {
/*  242 */           server.resumeAccepts();
/*      */         }
/*      */         
/*      */         public boolean isAcceptResumed() {
/*  246 */           return server.isAcceptResumed();
/*      */         }
/*      */         
/*      */         public void wakeupAccepts() {
/*  250 */           server.wakeupAccepts();
/*      */         }
/*      */         
/*      */         public void awaitAcceptable() throws IOException {
/*  254 */           server.awaitAcceptable();
/*      */         }
/*      */         
/*      */         public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
/*  258 */           server.awaitAcceptable(time, timeUnit);
/*      */         }
/*      */         
/*      */         public XnioWorker getWorker() {
/*  262 */           return server.getWorker();
/*      */         }
/*      */         
/*      */         @Deprecated
/*      */         public XnioExecutor getAcceptThread() {
/*  267 */           return server.getAcceptThread();
/*      */         }
/*      */         
/*      */         public XnioIoThread getIoThread() {
/*  271 */           return server.getIoThread();
/*      */         }
/*      */         
/*      */         public void close() throws IOException {
/*  275 */           server.close();
/*      */         }
/*      */         
/*      */         public boolean isOpen() {
/*  279 */           return server.isOpen();
/*      */         }
/*      */         
/*      */         public boolean supportsOption(Option<?> option) {
/*  283 */           return server.supportsOption(option);
/*      */         }
/*      */         
/*      */         public <T> T getOption(Option<T> option) throws IOException {
/*  287 */           return (T)server.getOption(option);
/*      */         }
/*      */         
/*      */         public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/*  291 */           return (T)server.setOption(option, value);
/*      */         }
/*      */       };
/*  294 */     acceptingChannel.getAcceptSetter().set(acceptListener);
/*  295 */     return acceptingChannel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AcceptingChannel<StreamConnection> createStreamConnectionServer(SocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
/*  308 */     Assert.checkNotNullParam("bindAddress", bindAddress);
/*  309 */     if (bindAddress instanceof InetSocketAddress)
/*  310 */       return createTcpConnectionServer((InetSocketAddress)bindAddress, acceptListener, optionMap); 
/*  311 */     if (bindAddress instanceof LocalSocketAddress) {
/*  312 */       return createLocalStreamConnectionServer((LocalSocketAddress)bindAddress, acceptListener, optionMap);
/*      */     }
/*  314 */     throw Messages.msg.badSockType(bindAddress.getClass());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AcceptingChannel<StreamConnection> createTcpConnectionServer(InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
/*  328 */     throw Messages.msg.unsupported("createTcpConnectionServer");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AcceptingChannel<StreamConnection> createLocalStreamConnectionServer(LocalSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<StreamConnection>> acceptListener, OptionMap optionMap) throws IOException {
/*  341 */     throw Messages.msg.unsupported("createLocalStreamConnectionServer");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public IoFuture<ConnectedStreamChannel> connectStream(SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, OptionMap optionMap) {
/*  356 */     FutureResult<ConnectedStreamChannel> futureResult = new FutureResult<>();
/*  357 */     ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
/*  358 */     IoFuture<StreamConnection> future = openStreamConnection(destination, nestedOpenListener, optionMap);
/*  359 */     future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
/*  360 */     futureResult.addCancelHandler(future);
/*  361 */     return futureResult.getIoFuture();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public IoFuture<ConnectedStreamChannel> connectStream(SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  375 */     FutureResult<ConnectedStreamChannel> futureResult = new FutureResult<>();
/*  376 */     ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
/*  377 */     IoFuture<StreamConnection> future = openStreamConnection(destination, nestedOpenListener, bindListener, optionMap);
/*  378 */     future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
/*  379 */     futureResult.addCancelHandler(future);
/*  380 */     return futureResult.getIoFuture();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public IoFuture<ConnectedStreamChannel> connectStream(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  396 */     FutureResult<ConnectedStreamChannel> futureResult = new FutureResult<>();
/*  397 */     ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
/*  398 */     IoFuture<StreamConnection> future = openStreamConnection(bindAddress, destination, nestedOpenListener, bindListener, optionMap);
/*  399 */     future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
/*  400 */     futureResult.addCancelHandler(future);
/*  401 */     return futureResult.getIoFuture();
/*      */   }
/*      */   
/*      */   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, OptionMap optionMap) {
/*  405 */     return chooseThread().openStreamConnection(destination, openListener, optionMap);
/*      */   }
/*      */   
/*      */   public IoFuture<StreamConnection> openStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  409 */     return chooseThread().openStreamConnection(destination, openListener, bindListener, optionMap);
/*      */   }
/*      */   
/*      */   public IoFuture<StreamConnection> openStreamConnection(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  413 */     return chooseThread().openStreamConnection(bindAddress, destination, openListener, bindListener, optionMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public IoFuture<ConnectedStreamChannel> acceptStream(SocketAddress destination, ChannelListener<? super ConnectedStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  430 */     FutureResult<ConnectedStreamChannel> futureResult = new FutureResult<>();
/*  431 */     ChannelListener<StreamConnection> nestedOpenListener = new StreamConnectionWrapListener(futureResult, openListener);
/*  432 */     IoFuture<StreamConnection> future = acceptStreamConnection(destination, nestedOpenListener, bindListener, optionMap);
/*  433 */     future.addNotifier(STREAM_WRAPPING_HANDLER, futureResult);
/*  434 */     futureResult.addCancelHandler(future);
/*  435 */     return futureResult.getIoFuture();
/*      */   }
/*      */   
/*      */   public IoFuture<StreamConnection> acceptStreamConnection(SocketAddress destination, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  439 */     return chooseThread().acceptStreamConnection(destination, openListener, bindListener, optionMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public IoFuture<ConnectedMessageChannel> connectDatagram(SocketAddress destination, ChannelListener<? super ConnectedMessageChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  460 */     FutureResult<ConnectedMessageChannel> futureResult = new FutureResult<>();
/*  461 */     ChannelListener<MessageConnection> nestedOpenListener = new MessageConnectionWrapListener(futureResult, openListener);
/*  462 */     IoFuture<MessageConnection> future = openMessageConnection(destination, nestedOpenListener, optionMap);
/*  463 */     future.addNotifier(MESSAGE_WRAPPING_HANDLER, futureResult);
/*  464 */     futureResult.addCancelHandler(future);
/*  465 */     return futureResult.getIoFuture();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public IoFuture<ConnectedMessageChannel> connectDatagram(SocketAddress bindAddress, SocketAddress destination, ChannelListener<? super ConnectedMessageChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  482 */     FutureResult<ConnectedMessageChannel> futureResult = new FutureResult<>();
/*  483 */     ChannelListener<MessageConnection> nestedOpenListener = new MessageConnectionWrapListener(futureResult, openListener);
/*  484 */     IoFuture<MessageConnection> future = openMessageConnection(destination, nestedOpenListener, optionMap);
/*  485 */     future.addNotifier(MESSAGE_WRAPPING_HANDLER, futureResult);
/*  486 */     futureResult.addCancelHandler(future);
/*  487 */     return futureResult.getIoFuture();
/*      */   }
/*      */   
/*      */   public IoFuture<MessageConnection> openMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, OptionMap optionMap) {
/*  491 */     return chooseThread().openMessageConnection(destination, openListener, optionMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public IoFuture<ConnectedMessageChannel> acceptDatagram(SocketAddress destination, ChannelListener<? super ConnectedMessageChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  508 */     FutureResult<ConnectedMessageChannel> futureResult = new FutureResult<>();
/*  509 */     ChannelListener<MessageConnection> nestedOpenListener = new MessageConnectionWrapListener(futureResult, openListener);
/*  510 */     IoFuture<MessageConnection> future = acceptMessageConnection(destination, nestedOpenListener, bindListener, optionMap);
/*  511 */     future.addNotifier(MESSAGE_WRAPPING_HANDLER, futureResult);
/*  512 */     futureResult.addCancelHandler(future);
/*  513 */     return futureResult.getIoFuture();
/*      */   }
/*      */   
/*      */   public IoFuture<MessageConnection> acceptMessageConnection(SocketAddress destination, ChannelListener<? super MessageConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/*  517 */     return chooseThread().acceptMessageConnection(destination, openListener, bindListener, optionMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MulticastMessageChannel createUdpServer(InetSocketAddress bindAddress, ChannelListener<? super MulticastMessageChannel> bindListener, OptionMap optionMap) throws IOException {
/*  540 */     throw Messages.msg.unsupported("createUdpServer");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MulticastMessageChannel createUdpServer(InetSocketAddress bindAddress, OptionMap optionMap) throws IOException {
/*  556 */     return createUdpServer(bindAddress, (ChannelListener)ChannelListeners.nullChannelListener(), optionMap);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void createPipe(ChannelListener<? super StreamChannel> leftOpenListener, ChannelListener<? super StreamChannel> rightOpenListener, OptionMap optionMap) throws IOException {
/*  576 */     ChannelPipe<StreamChannel, StreamChannel> pipe = createFullDuplexPipe();
/*  577 */     boolean establishWriting = optionMap.get(Options.WORKER_ESTABLISH_WRITING, false);
/*  578 */     StreamChannel left = pipe.getLeftSide();
/*  579 */     XnioExecutor leftExec = establishWriting ? left.getWriteThread() : left.getReadThread();
/*  580 */     StreamChannel right = pipe.getRightSide();
/*  581 */     XnioExecutor rightExec = establishWriting ? right.getWriteThread() : right.getReadThread();
/*      */ 
/*      */     
/*  584 */     leftExec.execute(ChannelListeners.getChannelListenerTask(left, leftOpenListener));
/*      */ 
/*      */     
/*  587 */     rightExec.execute(ChannelListeners.getChannelListenerTask(right, rightOpenListener));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void createOneWayPipe(ChannelListener<? super StreamSourceChannel> sourceListener, ChannelListener<? super StreamSinkChannel> sinkListener, OptionMap optionMap) throws IOException {
/*  601 */     ChannelPipe<StreamSourceChannel, StreamSinkChannel> pipe = createHalfDuplexPipe();
/*  602 */     StreamSourceChannel left = pipe.getLeftSide();
/*  603 */     XnioExecutor leftExec = left.getReadThread();
/*  604 */     StreamSinkChannel right = pipe.getRightSide();
/*  605 */     XnioExecutor rightExec = right.getWriteThread();
/*      */ 
/*      */     
/*  608 */     leftExec.execute(ChannelListeners.getChannelListenerTask(left, sourceListener));
/*      */ 
/*      */     
/*  611 */     rightExec.execute(ChannelListeners.getChannelListenerTask(right, sinkListener));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public StreamSourceChannel getInflatingChannel(StreamSourceChannel delegate, OptionMap options) throws IOException {
/*      */     boolean nowrap;
/*  630 */     switch ((CompressionType)options.get((Option)Options.COMPRESSION_TYPE, (T)CompressionType.DEFLATE)) { case DEFLATE:
/*  631 */         nowrap = false;
/*      */ 
/*      */ 
/*      */         
/*  635 */         return getInflatingChannel(delegate, new Inflater(nowrap));case GZIP: nowrap = true; return getInflatingChannel(delegate, new Inflater(nowrap)); }
/*      */     
/*      */     throw Messages.msg.badCompressionFormat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected StreamSourceChannel getInflatingChannel(StreamSourceChannel delegate, Inflater inflater) throws IOException {
/*  647 */     return (StreamSourceChannel)new ConduitStreamSourceChannel(Configurable.EMPTY, (StreamSourceConduit)new InflatingStreamSourceConduit((StreamSourceConduit)new StreamSourceChannelWrappingConduit(delegate), inflater));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public StreamSinkChannel getDeflatingChannel(StreamSinkChannel delegate, OptionMap options) throws IOException {
/*      */     boolean nowrap;
/*  659 */     int level = options.get(Options.COMPRESSION_LEVEL, -1);
/*      */     
/*  661 */     switch ((CompressionType)options.get((Option)Options.COMPRESSION_TYPE, (T)CompressionType.DEFLATE)) { case DEFLATE:
/*  662 */         nowrap = false;
/*      */ 
/*      */ 
/*      */         
/*  666 */         return getDeflatingChannel(delegate, new Deflater(level, nowrap));case GZIP: nowrap = true; return getDeflatingChannel(delegate, new Deflater(level, nowrap)); }
/*      */     
/*      */     throw Messages.msg.badCompressionFormat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected StreamSinkChannel getDeflatingChannel(StreamSinkChannel delegate, Deflater deflater) throws IOException {
/*  678 */     return (StreamSinkChannel)new ConduitStreamSinkChannel(Configurable.EMPTY, (StreamSinkConduit)new DeflatingStreamSinkConduit((StreamSinkConduit)new StreamSinkChannelWrappingConduit(delegate), deflater));
/*      */   }
/*      */   
/*      */   public ChannelPipe<StreamChannel, StreamChannel> createFullDuplexPipe() throws IOException {
/*  682 */     return chooseThread().createFullDuplexPipe();
/*      */   }
/*      */   
/*      */   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection() throws IOException {
/*  686 */     return chooseThread().createFullDuplexPipeConnection();
/*      */   }
/*      */   
/*      */   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe() throws IOException {
/*  690 */     return chooseThread().createHalfDuplexPipe();
/*      */   }
/*      */   
/*      */   public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory peer) throws IOException {
/*  694 */     return chooseThread().createFullDuplexPipeConnection(peer);
/*      */   }
/*      */   
/*      */   public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory peer) throws IOException {
/*  698 */     return chooseThread().createHalfDuplexPipe(peer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final XnioIoThread getIoThread() {
/*  768 */     return chooseThread();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Runnable getTerminationTask() {
/*  785 */     return this.terminationTask;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void taskPoolTerminated() {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void shutDownTaskPool() {
/*  798 */     if (isTaskPoolExternal()) {
/*  799 */       taskPoolTerminated();
/*      */     } else {
/*  801 */       AccessController.doPrivileged(new PrivilegedAction() {
/*      */             public Object run() {
/*  803 */               XnioWorker.this.taskPool.shutdown();
/*  804 */               return null;
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected List<Runnable> shutDownTaskPoolNow() {
/*  816 */     if (!isTaskPoolExternal()) return AccessController.<List<Runnable>>doPrivileged(new PrivilegedAction<List<Runnable>>() {
/*      */             public List<Runnable> run() {
/*  818 */               return XnioWorker.this.taskPool.shutdownNow();
/*      */             }
/*      */           }); 
/*  821 */     return Collections.emptyList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isTaskPoolExternal() {
/*  831 */     return this.taskPool instanceof ExternalTaskPool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void execute(Runnable command) {
/*  840 */     this.taskPool.execute(command);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  856 */   private static final Set<Option<?>> OPTIONS = Option.setBuilder()
/*  857 */     .add(Options.WORKER_TASK_CORE_THREADS)
/*  858 */     .add(Options.WORKER_TASK_MAX_THREADS)
/*  859 */     .add(Options.WORKER_TASK_KEEPALIVE)
/*  860 */     .create();
/*      */   
/*  862 */   private static final Set<Option<?>> EXTERNAL_POOL_OPTIONS = Option.setBuilder()
/*  863 */     .create();
/*      */   
/*      */   public boolean supportsOption(Option<?> option) {
/*  866 */     return (this.taskPool instanceof ExternalTaskPool) ? EXTERNAL_POOL_OPTIONS.contains(option) : OPTIONS.contains(option);
/*      */   }
/*      */   
/*      */   public <T> T getOption(Option<T> option) throws IOException {
/*  870 */     if (!supportsOption(option))
/*  871 */       return null; 
/*  872 */     if (option.equals(Options.WORKER_TASK_CORE_THREADS))
/*  873 */       return option.cast(Integer.valueOf(this.taskPool.getCorePoolSize())); 
/*  874 */     if (option.equals(Options.WORKER_TASK_MAX_THREADS))
/*  875 */       return option.cast(Integer.valueOf(this.taskPool.getMaximumPoolSize())); 
/*  876 */     if (option.equals(Options.WORKER_TASK_KEEPALIVE)) {
/*  877 */       return option.cast(Integer.valueOf((int)Math.min(2147483647L, this.taskPool.getKeepAliveTime(TimeUnit.MILLISECONDS))));
/*      */     }
/*  879 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/*  884 */     if (!supportsOption(option))
/*  885 */       return null; 
/*  886 */     if (option.equals(Options.WORKER_TASK_CORE_THREADS)) {
/*  887 */       int old = this.taskPool.getCorePoolSize();
/*  888 */       this.taskPool.setCorePoolSize(((Integer)Options.WORKER_TASK_CORE_THREADS.cast(value)).intValue());
/*  889 */       return option.cast(Integer.valueOf(old));
/*  890 */     }  if (option.equals(Options.WORKER_TASK_MAX_THREADS)) {
/*  891 */       int old = this.taskPool.getMaximumPoolSize();
/*  892 */       this.taskPool.setMaximumPoolSize(((Integer)Options.WORKER_TASK_MAX_THREADS.cast(value)).intValue());
/*  893 */       return option.cast(Integer.valueOf(old));
/*  894 */     }  if (option.equals(Options.WORKER_TASK_KEEPALIVE)) {
/*  895 */       long old = this.taskPool.getKeepAliveTime(TimeUnit.MILLISECONDS);
/*  896 */       this.taskPool.setKeepAliveTime(((Integer)Options.WORKER_TASK_KEEPALIVE.cast(value)).intValue(), TimeUnit.MILLISECONDS);
/*  897 */       return option.cast(Integer.valueOf((int)Math.min(2147483647L, old)));
/*      */     } 
/*  899 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Xnio getXnio() {
/*  915 */     return this.xnio;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  924 */     return this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int getCoreWorkerPoolSize() {
/*  946 */     return this.taskPool.getCorePoolSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int getBusyWorkerThreadCount() {
/*  955 */     return this.taskPool.getActiveCount();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int getWorkerPoolSize() {
/*  964 */     return this.taskPool.getPoolSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int getMaxWorkerPoolSize() {
/*  973 */     return this.taskPool.getMaximumPoolSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final int getWorkerQueueSize() {
/*  982 */     return this.taskPool.getQueueSize();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected CidrAddressTable<InetSocketAddress> getBindAddressTable() {
/*  997 */     return this.bindAddressTable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InetSocketAddress getBindAddress(InetAddress destination) {
/* 1006 */     return (InetSocketAddress)this.bindAddressTable.get(destination);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Builder
/*      */   {
/*      */     private final Xnio xnio;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private ExecutorService externalExecutorService;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Runnable terminationTask;
/*      */ 
/*      */ 
/*      */     
/*      */     private String workerName;
/*      */ 
/*      */ 
/*      */     
/* 1033 */     private int coreWorkerPoolSize = 4;
/* 1034 */     private int maxWorkerPoolSize = 16;
/*      */     private ThreadGroup threadGroup;
/*      */     private boolean daemon;
/* 1037 */     private int workerKeepAlive = 60000;
/* 1038 */     private int workerIoThreads = 1;
/* 1039 */     private long workerStackSize = 0L;
/* 1040 */     private CidrAddressTable<InetSocketAddress> bindAddressConfigurations = new CidrAddressTable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected Builder(Xnio xnio) {
/* 1048 */       this.xnio = xnio;
/*      */     }
/*      */     
/*      */     public Xnio getXnio() {
/* 1052 */       return this.xnio;
/*      */     }
/*      */     
/*      */     public Builder populateFromOptions(OptionMap optionMap) {
/* 1056 */       setWorkerName(optionMap.<String>get(Options.WORKER_NAME));
/* 1057 */       setCoreWorkerPoolSize(optionMap.get(Options.WORKER_TASK_CORE_THREADS, this.coreWorkerPoolSize));
/* 1058 */       setMaxWorkerPoolSize(optionMap.get(Options.WORKER_TASK_MAX_THREADS, this.maxWorkerPoolSize));
/* 1059 */       setDaemon(optionMap.get(Options.THREAD_DAEMON, this.daemon));
/* 1060 */       setWorkerKeepAlive(optionMap.get(Options.WORKER_TASK_KEEPALIVE, this.workerKeepAlive));
/* 1061 */       if (optionMap.contains(Options.WORKER_IO_THREADS)) {
/* 1062 */         setWorkerIoThreads(optionMap.get(Options.WORKER_IO_THREADS, 1));
/* 1063 */       } else if (optionMap.contains(Options.WORKER_READ_THREADS) || optionMap.contains(Options.WORKER_WRITE_THREADS)) {
/* 1064 */         setWorkerIoThreads(Math.max(optionMap.get(Options.WORKER_READ_THREADS, 1), optionMap.get(Options.WORKER_WRITE_THREADS, 1)));
/*      */       } 
/* 1066 */       setWorkerStackSize(optionMap.get(Options.STACK_SIZE, this.workerStackSize));
/* 1067 */       return this;
/*      */     }
/*      */     
/*      */     public Builder addBindAddressConfiguration(CidrAddress cidrAddress, InetAddress bindAddress) {
/* 1071 */       return addBindAddressConfiguration(cidrAddress, new InetSocketAddress(bindAddress, 0));
/*      */     }
/*      */     
/*      */     public Builder addBindAddressConfiguration(CidrAddress cidrAddress, InetSocketAddress bindAddress) {
/* 1075 */       Class<? extends InetAddress> networkAddrClass = (Class)cidrAddress.getNetworkAddress().getClass();
/* 1076 */       if (bindAddress.isUnresolved()) {
/* 1077 */         throw Messages.msg.addressUnresolved(bindAddress);
/*      */       }
/* 1079 */       if (networkAddrClass != bindAddress.getAddress().getClass()) {
/* 1080 */         throw Messages.msg.mismatchAddressType(networkAddrClass, bindAddress.getAddress().getClass());
/*      */       }
/* 1082 */       this.bindAddressConfigurations.put(cidrAddress, bindAddress);
/* 1083 */       return this;
/*      */     }
/*      */     
/*      */     public Builder setBindAddressConfigurations(CidrAddressTable<InetSocketAddress> newTable) {
/* 1087 */       this.bindAddressConfigurations = newTable;
/* 1088 */       return this;
/*      */     }
/*      */     
/*      */     public CidrAddressTable<InetSocketAddress> getBindAddressConfigurations() {
/* 1092 */       return this.bindAddressConfigurations;
/*      */     }
/*      */     
/*      */     public Runnable getTerminationTask() {
/* 1096 */       return this.terminationTask;
/*      */     }
/*      */     
/*      */     public Builder setTerminationTask(Runnable terminationTask) {
/* 1100 */       this.terminationTask = terminationTask;
/* 1101 */       return this;
/*      */     }
/*      */     
/*      */     public String getWorkerName() {
/* 1105 */       return this.workerName;
/*      */     }
/*      */     
/*      */     public Builder setWorkerName(String workerName) {
/* 1109 */       this.workerName = workerName;
/* 1110 */       return this;
/*      */     }
/*      */     
/*      */     public int getCoreWorkerPoolSize() {
/* 1114 */       return this.coreWorkerPoolSize;
/*      */     }
/*      */     
/*      */     public Builder setCoreWorkerPoolSize(int coreWorkerPoolSize) {
/* 1118 */       Assert.checkMinimumParameter("coreWorkerPoolSize", 0, coreWorkerPoolSize);
/* 1119 */       this.coreWorkerPoolSize = coreWorkerPoolSize;
/* 1120 */       return this;
/*      */     }
/*      */     
/*      */     public int getMaxWorkerPoolSize() {
/* 1124 */       return this.maxWorkerPoolSize;
/*      */     }
/*      */     
/*      */     public Builder setMaxWorkerPoolSize(int maxWorkerPoolSize) {
/* 1128 */       Assert.checkMinimumParameter("maxWorkerPoolSize", 0, maxWorkerPoolSize);
/* 1129 */       this.maxWorkerPoolSize = maxWorkerPoolSize;
/* 1130 */       return this;
/*      */     }
/*      */     
/*      */     public ThreadGroup getThreadGroup() {
/* 1134 */       return this.threadGroup;
/*      */     }
/*      */     
/*      */     public Builder setThreadGroup(ThreadGroup threadGroup) {
/* 1138 */       this.threadGroup = threadGroup;
/* 1139 */       return this;
/*      */     }
/*      */     
/*      */     public boolean isDaemon() {
/* 1143 */       return this.daemon;
/*      */     }
/*      */     
/*      */     public Builder setDaemon(boolean daemon) {
/* 1147 */       this.daemon = daemon;
/* 1148 */       return this;
/*      */     }
/*      */     
/*      */     public long getWorkerKeepAlive() {
/* 1152 */       return this.workerKeepAlive;
/*      */     }
/*      */     
/*      */     public Builder setWorkerKeepAlive(int workerKeepAlive) {
/* 1156 */       Assert.checkMinimumParameter("workerKeepAlive", 0, workerKeepAlive);
/* 1157 */       this.workerKeepAlive = workerKeepAlive;
/* 1158 */       return this;
/*      */     }
/*      */     
/*      */     public int getWorkerIoThreads() {
/* 1162 */       return this.workerIoThreads;
/*      */     }
/*      */     
/*      */     public Builder setWorkerIoThreads(int workerIoThreads) {
/* 1166 */       Assert.checkMinimumParameter("workerIoThreads", 0, workerIoThreads);
/* 1167 */       this.workerIoThreads = workerIoThreads;
/* 1168 */       return this;
/*      */     }
/*      */     
/*      */     public long getWorkerStackSize() {
/* 1172 */       return this.workerStackSize;
/*      */     }
/*      */     
/*      */     public Builder setWorkerStackSize(long workerStackSize) {
/* 1176 */       Assert.checkMinimumParameter("workerStackSize", 0L, workerStackSize);
/* 1177 */       this.workerStackSize = workerStackSize;
/* 1178 */       return this;
/*      */     }
/*      */     
/*      */     public ExecutorService getExternalExecutorService() {
/* 1182 */       return this.externalExecutorService;
/*      */     }
/*      */     
/*      */     public Builder setExternalExecutorService(ExecutorService executorService) {
/* 1186 */       this.externalExecutorService = executorService;
/* 1187 */       return this;
/*      */     }
/*      */     
/*      */     public XnioWorker build() {
/* 1191 */       return this.xnio.build(this);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class StreamConnectionWrapListener
/*      */     implements ChannelListener<StreamConnection>
/*      */   {
/*      */     private final FutureResult<ConnectedStreamChannel> futureResult;
/*      */ 
/*      */     
/*      */     private final ChannelListener<? super ConnectedStreamChannel> openListener;
/*      */ 
/*      */     
/*      */     public StreamConnectionWrapListener(FutureResult<ConnectedStreamChannel> futureResult, ChannelListener<? super ConnectedStreamChannel> openListener) {
/* 1207 */       this.futureResult = futureResult;
/* 1208 */       this.openListener = openListener;
/*      */     }
/*      */     
/*      */     public void handleEvent(StreamConnection channel) {
/* 1212 */       AssembledConnectedStreamChannel assembledChannel = new AssembledConnectedStreamChannel(channel, (StreamSourceChannel)channel.getSourceChannel(), (StreamSinkChannel)channel.getSinkChannel());
/* 1213 */       if (!this.futureResult.setResult(assembledChannel)) {
/* 1214 */         IoUtils.safeClose((Closeable)assembledChannel);
/*      */       } else {
/* 1216 */         ChannelListeners.invokeChannelListener(assembledChannel, this.openListener);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static class MessageConnectionWrapListener
/*      */     implements ChannelListener<MessageConnection> {
/*      */     private final FutureResult<ConnectedMessageChannel> futureResult;
/*      */     private final ChannelListener<? super ConnectedMessageChannel> openListener;
/*      */     
/*      */     public MessageConnectionWrapListener(FutureResult<ConnectedMessageChannel> futureResult, ChannelListener<? super ConnectedMessageChannel> openListener) {
/* 1227 */       this.futureResult = futureResult;
/* 1228 */       this.openListener = openListener;
/*      */     }
/*      */     
/*      */     public void handleEvent(MessageConnection channel) {
/* 1232 */       AssembledConnectedMessageChannel assembledChannel = new AssembledConnectedMessageChannel(channel, (ReadableMessageChannel)channel.getSourceChannel(), (WritableMessageChannel)channel.getSinkChannel());
/* 1233 */       if (!this.futureResult.setResult(assembledChannel)) {
/* 1234 */         IoUtils.safeClose((Closeable)assembledChannel);
/*      */       } else {
/* 1236 */         ChannelListeners.invokeChannelListener(assembledChannel, this.openListener);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/* 1241 */   private static final IoFuture.HandlingNotifier<StreamConnection, FutureResult<ConnectedStreamChannel>> STREAM_WRAPPING_HANDLER = new IoFuture.HandlingNotifier<StreamConnection, FutureResult<ConnectedStreamChannel>>() {
/*      */       public void handleCancelled(FutureResult<ConnectedStreamChannel> attachment) {
/* 1243 */         attachment.setCancelled();
/*      */       }
/*      */       
/*      */       public void handleFailed(IOException exception, FutureResult<ConnectedStreamChannel> attachment) {
/* 1247 */         attachment.setException(exception);
/*      */       }
/*      */     };
/*      */   
/* 1251 */   private static final IoFuture.HandlingNotifier<MessageConnection, FutureResult<ConnectedMessageChannel>> MESSAGE_WRAPPING_HANDLER = new IoFuture.HandlingNotifier<MessageConnection, FutureResult<ConnectedMessageChannel>>() {
/*      */       public void handleCancelled(FutureResult<ConnectedMessageChannel> attachment) {
/* 1253 */         attachment.setCancelled();
/*      */       }
/*      */       
/*      */       public void handleFailed(IOException exception, FutureResult<ConnectedMessageChannel> attachment) {
/* 1257 */         attachment.setException(exception);
/*      */       }
/*      */     }; public abstract void shutdown(); public abstract List<Runnable> shutdownNow(); public abstract boolean isShutdown(); public abstract boolean isTerminated(); public abstract boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
/*      */   public abstract void awaitTermination() throws InterruptedException;
/*      */   public abstract XnioIoThread getIoThread(int paramInt);
/*      */   public abstract int getIoThreadCount();
/*      */   protected abstract XnioIoThread chooseThread();
/*      */   public abstract XnioWorkerMXBean getMXBean();
/*      */   protected abstract ManagementRegistration registerServerMXBean(XnioServerMXBean paramXnioServerMXBean);
/*      */   class WorkerThreadFactory implements ThreadFactory { private final ThreadGroup threadGroup;
/*      */     WorkerThreadFactory(ThreadGroup threadGroup, long stackSize, boolean markThreadAsDaemon) {
/* 1268 */       this.threadGroup = threadGroup;
/* 1269 */       this.stackSize = stackSize;
/* 1270 */       this.markThreadAsDaemon = markThreadAsDaemon;
/*      */     }
/*      */     private final long stackSize; private final boolean markThreadAsDaemon;
/*      */     public Thread newThread(final Runnable r) {
/* 1274 */       return AccessController.<Thread>doPrivileged(new PrivilegedAction<Thread>()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             public Thread run()
/*      */             {
/* 1285 */               Thread taskThread = new Thread(XnioWorker.WorkerThreadFactory.this.threadGroup, new Runnable() { public void run() { try { r.run(); } finally { XnioWorker.this.xnio.handleThreadExit(); }  } }, XnioWorker.this.name + " task-" + XnioWorker.this.getNextSeq(), XnioWorker.WorkerThreadFactory.this.stackSize);
/*      */               
/* 1287 */               if (XnioWorker.WorkerThreadFactory.this.markThreadAsDaemon) {
/* 1288 */                 taskThread.setDaemon(true);
/*      */               }
/* 1290 */               return taskThread;
/*      */             }
/*      */           });
/*      */     } }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class DefaultThreadPoolExecutor
/*      */     extends ThreadPoolExecutor
/*      */   {
/*      */     private final Runnable terminationTask;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, Runnable terminationTask) {
/* 1327 */       super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
/* 1328 */       this.terminationTask = terminationTask;
/*      */     }
/*      */     
/*      */     protected void terminated() {
/* 1332 */       this.terminationTask.run();
/*      */     }
/*      */     
/*      */     public void setCorePoolSize(int size) {
/* 1336 */       setMaximumPoolSize(size);
/*      */     }
/*      */     
/*      */     public void setMaximumPoolSize(int size) {
/* 1340 */       if (size > getCorePoolSize()) {
/* 1341 */         super.setMaximumPoolSize(size);
/* 1342 */         super.setCorePoolSize(size);
/*      */       } else {
/* 1344 */         super.setCorePoolSize(size);
/* 1345 */         super.setMaximumPoolSize(size);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static class ThreadPoolExecutorTaskPool implements TaskPool {
/*      */     private final ThreadPoolExecutor delegate;
/*      */     
/*      */     ThreadPoolExecutorTaskPool(ThreadPoolExecutor delegate) {
/* 1354 */       this.delegate = delegate;
/*      */     }
/*      */ 
/*      */     
/*      */     public void shutdown() {
/* 1359 */       this.delegate.shutdown();
/*      */     }
/*      */ 
/*      */     
/*      */     public List<Runnable> shutdownNow() {
/* 1364 */       return this.delegate.shutdownNow();
/*      */     }
/*      */ 
/*      */     
/*      */     public void execute(Runnable command) {
/* 1369 */       this.delegate.execute(command);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getCorePoolSize() {
/* 1374 */       return this.delegate.getCorePoolSize();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMaximumPoolSize() {
/* 1379 */       return this.delegate.getMaximumPoolSize();
/*      */     }
/*      */ 
/*      */     
/*      */     public long getKeepAliveTime(TimeUnit unit) {
/* 1384 */       return this.delegate.getKeepAliveTime(unit);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setCorePoolSize(int size) {
/* 1389 */       this.delegate.setCorePoolSize(size);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setMaximumPoolSize(int size) {
/* 1394 */       this.delegate.setMaximumPoolSize(size);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setKeepAliveTime(long time, TimeUnit unit) {
/* 1399 */       this.delegate.setKeepAliveTime(time, unit);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getActiveCount() {
/* 1404 */       return this.delegate.getActiveCount();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getPoolSize() {
/* 1409 */       return this.delegate.getPoolSize();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getQueueSize() {
/* 1414 */       return this.delegate.getQueue().size();
/*      */     }
/*      */   }
/*      */   
/*      */   static class EnhancedQueueExecutorTaskPool implements TaskPool {
/*      */     private final EnhancedQueueExecutor executor;
/*      */     
/*      */     EnhancedQueueExecutorTaskPool(EnhancedQueueExecutor executor) {
/* 1422 */       this.executor = executor;
/*      */     }
/*      */     
/*      */     public void shutdown() {
/* 1426 */       this.executor.shutdown();
/*      */     }
/*      */     
/*      */     public List<Runnable> shutdownNow() {
/* 1430 */       return this.executor.shutdownNow();
/*      */     }
/*      */     
/*      */     public void execute(Runnable command) {
/* 1434 */       this.executor.execute(command);
/*      */     }
/*      */     
/*      */     public int getCorePoolSize() {
/* 1438 */       return this.executor.getCorePoolSize();
/*      */     }
/*      */     
/*      */     public int getMaximumPoolSize() {
/* 1442 */       return this.executor.getMaximumPoolSize();
/*      */     }
/*      */     
/*      */     public long getKeepAliveTime(TimeUnit unit) {
/* 1446 */       return this.executor.getKeepAliveTime(unit);
/*      */     }
/*      */     
/*      */     public void setCorePoolSize(int size) {
/* 1450 */       this.executor.setCorePoolSize(size);
/*      */     }
/*      */     
/*      */     public void setMaximumPoolSize(int size) {
/* 1454 */       this.executor.setMaximumPoolSize(size);
/*      */     }
/*      */     
/*      */     public void setKeepAliveTime(long time, TimeUnit unit) {
/* 1458 */       this.executor.setKeepAliveTime(time, unit);
/*      */     }
/*      */     
/*      */     public int getActiveCount() {
/* 1462 */       return this.executor.getActiveCount();
/*      */     }
/*      */     
/*      */     public int getPoolSize() {
/* 1466 */       return this.executor.getPoolSize();
/*      */     }
/*      */     
/*      */     public int getQueueSize() {
/* 1470 */       return this.executor.getQueueSize();
/*      */     }
/*      */   }
/*      */   
/*      */   static class ExecutorServiceTaskPool implements TaskPool {
/*      */     private final ExecutorService delegate;
/*      */     
/*      */     ExecutorServiceTaskPool(ExecutorService delegate) {
/* 1478 */       this.delegate = delegate;
/*      */     }
/*      */     
/*      */     public void shutdown() {
/* 1482 */       this.delegate.shutdown();
/*      */     }
/*      */     
/*      */     public List<Runnable> shutdownNow() {
/* 1486 */       return this.delegate.shutdownNow();
/*      */     }
/*      */     
/*      */     public void execute(Runnable command) {
/* 1490 */       this.delegate.execute(command);
/*      */     }
/*      */     
/*      */     public int getCorePoolSize() {
/* 1494 */       return -1;
/*      */     }
/*      */     
/*      */     public int getMaximumPoolSize() {
/* 1498 */       return -1;
/*      */     }
/*      */     
/*      */     public long getKeepAliveTime(TimeUnit unit) {
/* 1502 */       return -1L;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setCorePoolSize(int size) {}
/*      */ 
/*      */     
/*      */     public void setMaximumPoolSize(int size) {}
/*      */ 
/*      */     
/*      */     public void setKeepAliveTime(long time, TimeUnit unit) {}
/*      */     
/*      */     public int getActiveCount() {
/* 1515 */       return -1;
/*      */     }
/*      */     
/*      */     public int getPoolSize() {
/* 1519 */       return -1;
/*      */     }
/*      */     
/*      */     public int getQueueSize() {
/* 1523 */       return -1;
/*      */     }
/*      */   }
/*      */   
/*      */   static class ExternalTaskPool implements TaskPool {
/*      */     private final XnioWorker.TaskPool delegate;
/*      */     
/*      */     ExternalTaskPool(XnioWorker.TaskPool delegate) {
/* 1531 */       this.delegate = delegate;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void shutdown() {}
/*      */ 
/*      */ 
/*      */     
/*      */     public List<Runnable> shutdownNow() {
/* 1541 */       return Collections.emptyList();
/*      */     }
/*      */ 
/*      */     
/*      */     public void execute(Runnable command) {
/* 1546 */       this.delegate.execute(command);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getCorePoolSize() {
/* 1551 */       return this.delegate.getCorePoolSize();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getMaximumPoolSize() {
/* 1556 */       return this.delegate.getMaximumPoolSize();
/*      */     }
/*      */ 
/*      */     
/*      */     public long getKeepAliveTime(TimeUnit unit) {
/* 1561 */       return this.delegate.getKeepAliveTime(unit);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setCorePoolSize(int size) {
/* 1566 */       this.delegate.setCorePoolSize(size);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setMaximumPoolSize(int size) {
/* 1571 */       this.delegate.setMaximumPoolSize(size);
/*      */     }
/*      */ 
/*      */     
/*      */     public void setKeepAliveTime(long time, TimeUnit unit) {
/* 1576 */       this.delegate.setKeepAliveTime(time, unit);
/*      */     }
/*      */ 
/*      */     
/*      */     public int getActiveCount() {
/* 1581 */       return this.delegate.getActiveCount();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getPoolSize() {
/* 1586 */       return this.delegate.getPoolSize();
/*      */     }
/*      */ 
/*      */     
/*      */     public int getQueueSize() {
/* 1591 */       return this.delegate.getQueueSize();
/*      */     }
/*      */   }
/*      */   
/*      */   static interface TaskPool {
/*      */     void shutdown();
/*      */     
/*      */     List<Runnable> shutdownNow();
/*      */     
/*      */     void execute(Runnable param1Runnable);
/*      */     
/*      */     int getCorePoolSize();
/*      */     
/*      */     int getMaximumPoolSize();
/*      */     
/*      */     long getKeepAliveTime(TimeUnit param1TimeUnit);
/*      */     
/*      */     void setCorePoolSize(int param1Int);
/*      */     
/*      */     void setMaximumPoolSize(int param1Int);
/*      */     
/*      */     void setKeepAliveTime(long param1Long, TimeUnit param1TimeUnit);
/*      */     
/*      */     int getActiveCount();
/*      */     
/*      */     int getPoolSize();
/*      */     
/*      */     int getQueueSize();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\XnioWorker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */