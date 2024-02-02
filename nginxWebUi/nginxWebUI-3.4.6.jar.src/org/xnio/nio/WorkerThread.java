/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.Pipe;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.nio.channels.spi.AbstractSelectableChannel;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.locks.LockSupport;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.Cancellable;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.ChannelPipe;
/*     */ import org.xnio.ClosedWorkerException;
/*     */ import org.xnio.FailedIoFuture;
/*     */ import org.xnio.FinishedIoFuture;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.ReadPropertyAction;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoFactory;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.BoundChannel;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ final class WorkerThread
/*     */   extends XnioIoThread
/*     */   implements XnioExecutor
/*     */ {
/*     */   private static final long LONGEST_DELAY = 9223372036853L;
/*  81 */   private static final String FQCN = WorkerThread.class.getName();
/*     */   private static final boolean OLD_LOCKING;
/*     */   private static final boolean THREAD_SAFE_SELECTION_KEYS;
/*  84 */   private static final long START_TIME = System.nanoTime();
/*     */   
/*     */   private final Selector selector;
/*  87 */   private final Object workLock = new Object();
/*     */   
/*  89 */   private final Queue<Runnable> selectorWorkQueue = new ArrayDeque<>();
/*  90 */   private final TreeSet<TimeKey> delayWorkQueue = new TreeSet<>();
/*     */   
/*     */   private volatile int state;
/*     */   
/*     */   private static final int SHUTDOWN = -2147483648;
/*     */   
/*  96 */   private static final AtomicIntegerFieldUpdater<WorkerThread> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(WorkerThread.class, "state"); volatile boolean polling;
/*     */   static final AtomicLong seqGen;
/*     */   
/*  99 */   static { OLD_LOCKING = Boolean.parseBoolean(AccessController.<String>doPrivileged((PrivilegedAction<String>)new ReadPropertyAction("xnio.nio.old-locking", "false")));
/* 100 */     THREAD_SAFE_SELECTION_KEYS = Boolean.parseBoolean(AccessController.<String>doPrivileged((PrivilegedAction<String>)new ReadPropertyAction("xnio.nio.thread-safe-selection-keys", "false")));
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
/* 857 */     seqGen = new AtomicLong(); } WorkerThread(NioXnioWorker worker, Selector selector, String name, ThreadGroup group, long stackSize, int number) { super(worker, number, group, name, stackSize); this.selector = selector; } static WorkerThread getCurrent() { XnioIoThread xnioIoThread = currentThread(); return (xnioIoThread instanceof WorkerThread) ? (WorkerThread)xnioIoThread : null; } public NioXnioWorker getWorker() { return (NioXnioWorker)super.getWorker(); } protected IoFuture<StreamConnection> acceptTcpStreamConnection(InetSocketAddress destination, final ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, final OptionMap optionMap) { try { getWorker().checkShutdown(); } catch (ClosedWorkerException e) { return (IoFuture<StreamConnection>)new FailedIoFuture((IOException)e); }  final FutureResult<StreamConnection> futureResult = new FutureResult((Executor)this); try { boolean ok = false; final ServerSocketChannel serverChannel = ServerSocketChannel.open(); try { serverChannel.configureBlocking(false); if (optionMap.contains(Options.RECEIVE_BUFFER)) serverChannel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1));  serverChannel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, true)); serverChannel.bind(destination); if (bindListener != null) ChannelListeners.invokeChannelListener((Channel)new BoundChannel() { public SocketAddress getLocalAddress() { return serverChannel.socket().getLocalSocketAddress(); } public <A extends SocketAddress> A getLocalAddress(Class<A> type) { SocketAddress address = getLocalAddress(); return type.isInstance(address) ? type.cast(address) : null; } public ChannelListener.Setter<? extends BoundChannel> getCloseSetter() { return (ChannelListener.Setter<? extends BoundChannel>)new ChannelListener.SimpleSetter(); } public XnioWorker getWorker() { return WorkerThread.this.getWorker(); } public XnioIoThread getIoThread() { return WorkerThread.this; } public void close() throws IOException { serverChannel.close(); } public boolean isOpen() { return serverChannel.isOpen(); } public boolean supportsOption(Option<?> option) { return false; } public <T> T getOption(Option<T> option) throws IOException { return null; } public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException { return null; }
/*     */               },  bindListener);  SelectionKey key = registerChannel(serverChannel); NioHandle handle = new NioHandle(this, key) { void handleReady(int ops) { boolean ok = false; try { SocketChannel channel = serverChannel.accept(); if (channel == null) { ok = true; return; }  IoUtils.safeClose(serverChannel); try { channel.configureBlocking(false); if (optionMap.contains(Options.TCP_OOB_INLINE)) channel.socket().setOOBInline(optionMap.get(Options.TCP_OOB_INLINE, false));  if (optionMap.contains(Options.TCP_NODELAY)) channel.socket().setTcpNoDelay(optionMap.get(Options.TCP_NODELAY, false));  if (optionMap.contains(Options.IP_TRAFFIC_CLASS)) channel.socket().setTrafficClass(optionMap.get(Options.IP_TRAFFIC_CLASS, -1));  if (optionMap.contains(Options.CLOSE_ABORT)) channel.socket().setSoLinger(optionMap.get(Options.CLOSE_ABORT, false), 0);  if (optionMap.contains(Options.KEEP_ALIVE)) channel.socket().setKeepAlive(optionMap.get(Options.KEEP_ALIVE, false));  if (optionMap.contains(Options.SEND_BUFFER)) channel.socket().setSendBufferSize(optionMap.get(Options.SEND_BUFFER, -1));  SelectionKey selectionKey = WorkerThread.this.registerChannel(channel); NioSocketStreamConnection connection = new NioSocketStreamConnection(WorkerThread.this, selectionKey, null); if (optionMap.contains(Options.READ_TIMEOUT)) connection.setOption(Options.READ_TIMEOUT, Integer.valueOf(optionMap.get(Options.READ_TIMEOUT, 0)));  if (optionMap.contains(Options.WRITE_TIMEOUT)) connection.setOption(Options.WRITE_TIMEOUT, Integer.valueOf(optionMap.get(Options.WRITE_TIMEOUT, 0)));  if (futureResult.setResult(connection)) { ok = true; ChannelListeners.invokeChannelListener((Channel)connection, openListener); }  } finally { if (!ok) IoUtils.safeClose(channel);  }  } catch (IOException e) { futureResult.setException(e); } finally { if (!ok) IoUtils.safeClose(serverChannel);  }  } void terminated() {} void forceTermination() { futureResult.setCancelled(); } }
/*     */           ; key.attach(handle); handle.resume(16); ok = true; futureResult.addCancelHandler(new Cancellable() { public Cancellable cancel() { if (futureResult.setCancelled()) IoUtils.safeClose(serverChannel);  return this; } }); return futureResult.getIoFuture(); } finally { if (!ok) IoUtils.safeClose(serverChannel);  }  } catch (IOException e) { return (IoFuture<StreamConnection>)new FailedIoFuture(e); }  } protected IoFuture<StreamConnection> openTcpStreamConnection(InetSocketAddress bindAddress, InetSocketAddress destinationAddress, ChannelListener<? super StreamConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) { try { getWorker().checkShutdown(); } catch (ClosedWorkerException e) { return (IoFuture<StreamConnection>)new FailedIoFuture((IOException)e); }  try { SocketChannel channel = SocketChannel.open(); boolean ok = false; try { channel.configureBlocking(false); if (optionMap.contains(Options.TCP_OOB_INLINE)) channel.socket().setOOBInline(optionMap.get(Options.TCP_OOB_INLINE, false));  if (optionMap.contains(Options.TCP_NODELAY)) channel.socket().setTcpNoDelay(optionMap.get(Options.TCP_NODELAY, false));  if (optionMap.contains(Options.IP_TRAFFIC_CLASS)) channel.socket().setTrafficClass(optionMap.get(Options.IP_TRAFFIC_CLASS, -1));  if (optionMap.contains(Options.CLOSE_ABORT)) channel.socket().setSoLinger(optionMap.get(Options.CLOSE_ABORT, false), 0);  if (optionMap.contains(Options.KEEP_ALIVE)) channel.socket().setKeepAlive(optionMap.get(Options.KEEP_ALIVE, false));  if (optionMap.contains(Options.RECEIVE_BUFFER)) channel.socket().setReceiveBufferSize(optionMap.get(Options.RECEIVE_BUFFER, -1));  if (optionMap.contains(Options.REUSE_ADDRESSES)) channel.socket().setReuseAddress(optionMap.get(Options.REUSE_ADDRESSES, false));  if (optionMap.contains(Options.SEND_BUFFER)) channel.socket().setSendBufferSize(optionMap.get(Options.SEND_BUFFER, -1));  SelectionKey key = registerChannel(channel); final NioSocketStreamConnection connection = new NioSocketStreamConnection(this, key, null); if (optionMap.contains(Options.READ_TIMEOUT)) connection.setOption(Options.READ_TIMEOUT, Integer.valueOf(optionMap.get(Options.READ_TIMEOUT, 0)));  if (optionMap.contains(Options.WRITE_TIMEOUT)) connection.setOption(Options.WRITE_TIMEOUT, Integer.valueOf(optionMap.get(Options.WRITE_TIMEOUT, 0)));  if (bindAddress != null || bindListener != null) { channel.socket().bind(bindAddress); ChannelListeners.invokeChannelListener((Channel)connection, bindListener); }  if (channel.connect(destinationAddress)) { Log.selectorLog.tracef("Synchronous connect", new Object[0]); execute(ChannelListeners.getChannelListenerTask((Channel)connection, openListener)); FinishedIoFuture<StreamConnection> finishedIoFuture = new FinishedIoFuture(connection); ok = true; return (IoFuture<StreamConnection>)finishedIoFuture; }  Log.selectorLog.tracef("Asynchronous connect", new Object[0]); final FutureResult<StreamConnection> futureResult = new FutureResult((Executor)this); ConnectHandle connectHandle = new ConnectHandle(this, key, futureResult, connection, openListener); key.attach(connectHandle); futureResult.addCancelHandler(new Cancellable() { public Cancellable cancel() { if (futureResult.setCancelled()) IoUtils.safeClose((Closeable)connection);  return this; } }); connectHandle.resume(8); ok = true; return futureResult.getIoFuture(); } finally { if (!ok) IoUtils.safeClose(channel);  }  } catch (IOException e) { return (IoFuture<StreamConnection>)new FailedIoFuture(e); }  } WorkerThread getNextThread() { WorkerThread[] all = getWorker().getAll(); int number = getNumber(); if (number == all.length - 1) return all[0];  return all[number + 1]; } static final class ConnectHandle extends NioHandle {
/*     */     private final FutureResult<StreamConnection> futureResult; private final NioSocketStreamConnection connection; private final ChannelListener<? super StreamConnection> openListener; ConnectHandle(WorkerThread workerThread, SelectionKey selectionKey, FutureResult<StreamConnection> futureResult, NioSocketStreamConnection connection, ChannelListener<? super StreamConnection> openListener) { super(workerThread, selectionKey); this.futureResult = futureResult; this.connection = connection; this.openListener = openListener; } void handleReady(int ops) { SocketChannel channel = getChannel(); boolean ok = false; try { if (channel.finishConnect()) { Log.selectorLog.tracef("handleReady connect finished", new Object[0]); suspend(8); getSelectionKey().attach(this.connection.getConduit()); if (this.futureResult.setResult(this.connection)) { ok = true; ChannelListeners.invokeChannelListener((Channel)this.connection, this.openListener); }  }  } catch (IOException e) { Log.selectorLog.tracef("ConnectHandle.handleReady Exception, %s", e); this.futureResult.setException(e); } finally { if (!ok) { Log.selectorLog.tracef("!OK, closing connection", new Object[0]); IoUtils.safeClose((Closeable)this.connection); }  }  } private SocketChannel getChannel() { return (SocketChannel)getSelectionKey().channel(); } void forceTermination() { this.futureResult.setCancelled(); IoUtils.safeClose(getChannel()); } void terminated() {} } private static WorkerThread getPeerThread(XnioIoFactory peer) throws ClosedWorkerException { WorkerThread peerThread; if (peer instanceof NioXnioWorker) { NioXnioWorker peerWorker = (NioXnioWorker)peer; peerWorker.checkShutdown(); peerThread = peerWorker.chooseThread(); } else if (peer instanceof WorkerThread) { peerThread = (WorkerThread)peer; peerThread.getWorker().checkShutdown(); } else { throw Log.log.notNioProvider(); }  return peerThread; } public ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory peer) throws IOException { getWorker().checkShutdown(); boolean ok = false; Pipe topPipe = Pipe.open(); try { topPipe.source().configureBlocking(false); topPipe.sink().configureBlocking(false); Pipe bottomPipe = Pipe.open(); } finally { if (!ok) { IoUtils.safeClose(topPipe.sink()); IoUtils.safeClose(topPipe.source()); }  }  } public ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory peer) throws IOException { getWorker().checkShutdown(); Pipe pipe = Pipe.open(); boolean ok = false; try { pipe.source().configureBlocking(false); pipe.sink().configureBlocking(false); WorkerThread peerThread = getPeerThread(peer); SelectionKey readKey = registerChannel(pipe.source()); SelectionKey writeKey = peerThread.registerChannel(pipe.sink()); NioPipeStreamConnection leftConnection = new NioPipeStreamConnection(this, readKey, null); NioPipeStreamConnection rightConnection = new NioPipeStreamConnection(this, null, writeKey); leftConnection.writeClosed(); rightConnection.readClosed(); ChannelPipe<StreamSourceChannel, StreamSinkChannel> result = new ChannelPipe((CloseableChannel)leftConnection.getSourceChannel(), (CloseableChannel)rightConnection.getSinkChannel()); ok = true; return result; } finally { if (!ok) { IoUtils.safeClose(pipe.sink()); IoUtils.safeClose(pipe.source()); }  }  } public void run() { Selector selector = this.selector; try { Log.log.tracef("Starting worker thread %s", this); Object lock = this.workLock; Queue<Runnable> workQueue = this.selectorWorkQueue; TreeSet<TimeKey> delayQueue = this.delayWorkQueue; Log.log.debugf("Started channel thread '%s', selector %s", currentThread().getName(), selector); long delayTime = Long.MAX_VALUE; SelectionKey[] keys = new SelectionKey[16]; while (true) { Runnable task; synchronized (lock) { task = workQueue.poll(); if (task == null) { Iterator<TimeKey> iterator = delayQueue.iterator(); delayTime = Long.MAX_VALUE; if (iterator.hasNext()) { long now = System.nanoTime(); do { TimeKey key = iterator.next(); if (key.deadline <= now - START_TIME) { workQueue.add(key.command); iterator.remove(); } else { delayTime = key.deadline - now - START_TIME; break; }  } while (iterator.hasNext()); }  task = workQueue.poll(); }  }  Thread.interrupted(); safeRun(task); if (task == null) { int oldState = this.state; if ((oldState & Integer.MIN_VALUE) != 0) { synchronized (lock) { int keyCount = selector.keys().size(); this.state = keyCount | Integer.MIN_VALUE; if (keyCount == 0 && workQueue.isEmpty()) return;  }  synchronized (selector) { Set<SelectionKey> keySet = selector.keys(); synchronized (keySet) { keys = keySet.<SelectionKey>toArray(keys); Arrays.fill((Object[])keys, keySet.size(), keys.length, (Object)null); }  }  for (int j = 0; j < keys.length; j++) { SelectionKey key = keys[j]; if (key == null) break;  keys[j] = null; NioHandle attachment = (NioHandle)key.attachment(); if (attachment != null) { IoUtils.safeClose(key.channel()); attachment.forceTermination(); }  }  Arrays.fill((Object[])keys, 0, keys.length, (Object)null); }  Thread.interrupted(); try { if ((oldState & Integer.MIN_VALUE) != 0) { Log.selectorLog.tracef("Beginning select on %s (shutdown in progress)", selector); selector.selectNow(); } else if (delayTime == Long.MAX_VALUE) { Log.selectorLog.tracef("Beginning select on %s", selector); this.polling = true; try { Runnable item = null; synchronized (lock) { item = workQueue.peek(); }  if (item != null) { Log.log.tracef("SelectNow, queue is not empty", new Object[0]); selector.selectNow(); } else { Log.log.tracef("Select, queue is empty", new Object[0]); selector.select(); }  } finally { this.polling = false; }  } else { long millis = 1L + delayTime / 1000000L; Log.selectorLog.tracef("Beginning select on %s (with timeout)", selector); this.polling = true; try { Runnable item = null; synchronized (lock) { item = workQueue.peek(); }  if (item != null) { Log.log.tracef("SelectNow, queue is not empty", new Object[0]); selector.selectNow(); } else { Log.log.tracef("Select, queue is empty", new Object[0]); selector.select(millis); }  } finally { this.polling = false; }  }  } catch (CancelledKeyException ignored) { Log.selectorLog.trace("Spurious cancelled key exception"); } catch (IOException e) { Log.selectorLog.selectionError(e); }  Log.selectorLog.tracef("Selected on %s", selector); synchronized (selector) { Set<SelectionKey> selectedKeys = selector.selectedKeys(); synchronized (selectedKeys) { keys = selectedKeys.<SelectionKey>toArray(keys); Arrays.fill((Object[])keys, selectedKeys.size(), keys.length, (Object)null); selectedKeys.clear(); }  }  for (int i = 0; i < keys.length; i++) { SelectionKey key = keys[i]; if (key == null) break;  keys[i] = null; try { int ops = key.interestOps(); if (ops != 0) { Log.selectorLog.tracef("Selected key %s for %s", key, key.channel()); NioHandle handle = (NioHandle)key.attachment(); if (handle == null) { cancelKey(key, false); } else { Thread.interrupted(); Log.selectorLog.tracef("Calling handleReady key %s for %s", key.readyOps(), key.channel()); handle.handleReady(key.readyOps()); }  }  } catch (CancelledKeyException ignored) { Log.selectorLog.tracef("Skipping selection of cancelled key %s", key); } catch (Throwable t) { Log.selectorLog.tracef(t, "Unexpected failure of selection of key %s", key); }  }  }  }  } finally { Log.log.tracef("Shutting down channel thread \"%s\"", this); IoUtils.safeClose(selector); getWorker().closeResource(); }  } private static void safeRun(Runnable command) { if (command != null) try { Log.log.tracef("Running task %s", command); command.run(); } catch (Throwable t) { Log.log.taskFailed(command, t); }   } public void execute(Runnable command) { if ((this.state & Integer.MIN_VALUE) != 0) throw Log.log.threadExiting();  synchronized (this.workLock) { this.selectorWorkQueue.add(command); Log.log.tracef("Added task %s", command); }  if (this.polling) { this.selector.wakeup(); } else { Log.log.tracef("Not polling, no wakeup", new Object[0]); }  } void shutdown() { while (true) { int oldState = this.state; if ((oldState & Integer.MIN_VALUE) != 0) return;  if (stateUpdater.compareAndSet(this, oldState, oldState | Integer.MIN_VALUE)) { if (currentThread() != this) this.selector.wakeup();  return; }  }  } public XnioExecutor.Key executeAfter(Runnable command, long time, TimeUnit unit) { long millis = unit.toMillis(time); if ((this.state & Integer.MIN_VALUE) != 0) throw Log.log.threadExiting();  if (millis <= 0L) { execute(command); return XnioExecutor.Key.IMMEDIATE; }  long deadline = System.nanoTime() - START_TIME + Math.min(millis, 9223372036853L) * 1000000L; TimeKey key = new TimeKey(deadline, command); synchronized (this.workLock) { TreeSet<TimeKey> queue = this.delayWorkQueue; queue.add(key); if (queue.iterator().next() == key) if (this.polling) this.selector.wakeup();   return key; }  } class RepeatKey implements XnioExecutor.Key, Runnable {
/* 861 */     private final Runnable command; private final long millis; private final AtomicReference<XnioExecutor.Key> current = new AtomicReference<>(); RepeatKey(Runnable command, long millis) { this.command = command; this.millis = millis; } public boolean remove() { XnioExecutor.Key removed = this.current.getAndSet(this); assert removed != null; return (removed != this && removed.remove()); } void setFirst(XnioExecutor.Key key) { this.current.compareAndSet(null, key); } public void run() { try { this.command.run(); } finally { XnioExecutor.Key o = this.current.get(); if (o != this) { XnioExecutor.Key n = WorkerThread.this.executeAfter(this, this.millis, TimeUnit.MILLISECONDS); if (!this.current.compareAndSet(o, n)) n.remove();  }  }  } } public XnioExecutor.Key executeAtInterval(Runnable command, long time, TimeUnit unit) { long millis = unit.toMillis(time); RepeatKey repeatKey = new RepeatKey(command, millis); XnioExecutor.Key firstKey = executeAfter(repeatKey, millis, TimeUnit.MILLISECONDS); repeatKey.setFirst(firstKey); return repeatKey; } SelectionKey registerChannel(AbstractSelectableChannel channel) throws ClosedChannelException { if (currentThread() == this) return channel.register(this.selector, 0);  if (THREAD_SAFE_SELECTION_KEYS) try { return channel.register(this.selector, 0); } finally { if (this.polling) this.selector.wakeup();  }   SynchTask task = new SynchTask(); queueTask(task); try { this.selector.wakeup(); return channel.register(this.selector, 0); } finally { task.done(); }  } void queueTask(Runnable task) { synchronized (this.workLock) { this.selectorWorkQueue.add(task); }  } void cancelKey(SelectionKey key, boolean block) { assert key.selector() == this.selector; SelectableChannel channel = key.channel(); if (currentThread() == this) { Log.log.logf(FQCN, Logger.Level.TRACE, null, "Cancelling key %s of %s (same thread)", key, channel); try { key.cancel(); try { this.selector.selectNow(); } catch (IOException e) { Log.log.selectionError(e); }  } catch (Throwable t) { Log.log.logf(FQCN, Logger.Level.TRACE, t, "Error cancelling key %s of %s (same thread)", key, channel); }  } else if (OLD_LOCKING) { Log.log.logf(FQCN, Logger.Level.TRACE, null, "Cancelling key %s of %s (same thread, old locking)", key, channel); SynchTask task = new SynchTask(); queueTask(task); try { this.selector.wakeup(); key.cancel(); } catch (Throwable t) { Log.log.logf(FQCN, Logger.Level.TRACE, t, "Error cancelling key %s of %s (same thread, old locking)", key, channel); } finally { task.done(); }  } else { Log.log.logf(FQCN, Logger.Level.TRACE, null, "Cancelling key %s of %s (other thread)", key, channel); try { key.cancel(); if (block) { SelectNowTask task = new SelectNowTask(); queueTask(task); this.selector.wakeup(); task.doWait(); } else { this.selector.wakeup(); }  } catch (Throwable t) { Log.log.logf(FQCN, Logger.Level.TRACE, t, "Error cancelling key %s of %s (other thread)", key, channel); }  }  } void setOps(SelectionKey key, int ops) { if (currentThread() == this) { try { synchronized (key) { key.interestOps(key.interestOps() | ops); }  } catch (CancelledKeyException cancelledKeyException) {} } else if (OLD_LOCKING) { SynchTask task = new SynchTask(); queueTask(task); try { this.selector.wakeup(); synchronized (key) { key.interestOps(key.interestOps() | ops); }  } catch (CancelledKeyException cancelledKeyException) {  } finally { task.done(); }  } else { try { synchronized (key) { key.interestOps(key.interestOps() | ops); }  if (this.polling) this.selector.wakeup();  } catch (CancelledKeyException cancelledKeyException) {} }  } void clearOps(SelectionKey key, int ops) { if (currentThread() == this || !OLD_LOCKING) { try { synchronized (key) { key.interestOps(key.interestOps() & (ops ^ 0xFFFFFFFF)); }  } catch (CancelledKeyException cancelledKeyException) {} } else { SynchTask task = new SynchTask(); queueTask(task); try { this.selector.wakeup(); synchronized (key) { key.interestOps(key.interestOps() & (ops ^ 0xFFFFFFFF)); }  } catch (CancelledKeyException cancelledKeyException) {  } finally { task.done(); }  }  } Selector getSelector() { return this.selector; } public boolean equals(Object obj) { return (obj == this); } public int hashCode() { return System.identityHashCode(this); } final class TimeKey implements XnioExecutor.Key, Comparable<TimeKey> { private final long seq = WorkerThread.seqGen.incrementAndGet(); private final long deadline;
/*     */     private final Runnable command;
/*     */     
/*     */     TimeKey(long deadline, Runnable command) {
/* 865 */       this.deadline = deadline;
/* 866 */       this.command = command;
/*     */     }
/*     */     
/*     */     public boolean remove() {
/* 870 */       synchronized (WorkerThread.this.workLock) {
/* 871 */         return WorkerThread.this.delayWorkQueue.remove(this);
/*     */       } 
/*     */     }
/*     */     
/*     */     public int compareTo(TimeKey o) {
/* 876 */       int r = Long.signum(this.deadline - o.deadline);
/* 877 */       if (r == 0) r = Long.signum(this.seq - o.seq); 
/* 878 */       return r;
/*     */     } }
/*     */ 
/*     */   
/*     */   final class SynchTask implements Runnable {
/*     */     volatile boolean done;
/*     */     
/*     */     public void run() {
/* 886 */       while (!this.done) {
/* 887 */         LockSupport.park();
/*     */       }
/*     */     }
/*     */     
/*     */     void done() {
/* 892 */       this.done = true;
/* 893 */       LockSupport.unpark((Thread)WorkerThread.this);
/*     */     }
/*     */   }
/*     */   
/*     */   final class SelectNowTask implements Runnable {
/* 898 */     final Thread thread = Thread.currentThread();
/*     */     volatile boolean done;
/*     */     
/*     */     void doWait() {
/* 902 */       while (!this.done) {
/* 903 */         LockSupport.park();
/*     */       }
/*     */     }
/*     */     
/*     */     public void run() {
/*     */       try {
/* 909 */         WorkerThread.this.selector.selectNow();
/* 910 */       } catch (IOException iOException) {}
/*     */       
/* 912 */       this.done = true;
/* 913 */       LockSupport.unpark(this.thread);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\WorkerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */