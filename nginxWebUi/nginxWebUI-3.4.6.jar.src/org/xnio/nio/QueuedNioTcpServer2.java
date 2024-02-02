/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.channels.AcceptListenerSettable;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class QueuedNioTcpServer2
/*     */   extends AbstractNioChannel<QueuedNioTcpServer2>
/*     */   implements AcceptingChannel<StreamConnection>, AcceptListenerSettable<QueuedNioTcpServer2>
/*     */ {
/*     */   private final NioTcpServer realServer;
/*     */   private final List<Queue<StreamConnection>> acceptQueues;
/*  44 */   private final Runnable acceptTask = this::acceptTask;
/*     */   
/*     */   private volatile ChannelListener<? super QueuedNioTcpServer2> acceptListener;
/*     */   
/*     */   QueuedNioTcpServer2(NioTcpServer realServer) {
/*  49 */     super(realServer.getWorker());
/*  50 */     this.realServer = realServer;
/*  51 */     NioXnioWorker worker = realServer.getWorker();
/*  52 */     int cnt = worker.getIoThreadCount();
/*  53 */     this.acceptQueues = new ArrayList<>(cnt);
/*  54 */     for (int i = 0; i < cnt; i++) {
/*  55 */       this.acceptQueues.add(new LinkedBlockingQueue<>());
/*     */     }
/*  57 */     realServer.getCloseSetter().set(ignored -> invokeCloseHandler());
/*  58 */     realServer.getAcceptSetter().set(ignored -> handleReady());
/*     */   }
/*     */   
/*     */   public StreamConnection accept() throws IOException {
/*  62 */     WorkerThread current = WorkerThread.getCurrent();
/*  63 */     if (current == null) {
/*  64 */       return null;
/*     */     }
/*  66 */     Queue<StreamConnection> socketChannels = this.acceptQueues.get(current.getNumber());
/*  67 */     StreamConnection connection = socketChannels.poll();
/*  68 */     if (connection == null && 
/*  69 */       !this.realServer.isOpen()) {
/*  70 */       throw new ClosedChannelException();
/*     */     }
/*     */     
/*  73 */     return connection;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super QueuedNioTcpServer2> getAcceptListener() {
/*  77 */     return this.acceptListener;
/*     */   }
/*     */   
/*     */   public void setAcceptListener(ChannelListener<? super QueuedNioTcpServer2> listener) {
/*  81 */     this.acceptListener = listener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<QueuedNioTcpServer2> getAcceptSetter() {
/*  85 */     return (ChannelListener.Setter<QueuedNioTcpServer2>)new AcceptListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/*  89 */     return this.realServer.getLocalAddress();
/*     */   }
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/*  93 */     return this.realServer.getLocalAddress(type);
/*     */   }
/*     */   
/*     */   public void suspendAccepts() {
/*  97 */     this.realServer.suspendAccepts();
/*     */   }
/*     */   
/*     */   public void resumeAccepts() {
/* 101 */     this.realServer.resumeAccepts();
/*     */   }
/*     */   
/*     */   public boolean isAcceptResumed() {
/* 105 */     return this.realServer.isAcceptResumed();
/*     */   }
/*     */   
/*     */   public void wakeupAccepts() {
/* 109 */     this.realServer.wakeupAccepts();
/*     */   }
/*     */   
/*     */   public void awaitAcceptable() {
/* 113 */     throw Assert.unsupported();
/*     */   }
/*     */   
/*     */   public void awaitAcceptable(long time, TimeUnit timeUnit) {
/* 117 */     throw Assert.unsupported();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getAcceptThread() {
/* 122 */     return (XnioExecutor)getIoThread();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 126 */     this.realServer.close();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 130 */     return this.realServer.isOpen();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 134 */     return this.realServer.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 138 */     return this.realServer.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 142 */     return this.realServer.setOption(option, value);
/*     */   }
/*     */   void handleReady() {
/*     */     NioSocketStreamConnection connection;
/* 146 */     NioTcpServer realServer = this.realServer;
/*     */     
/*     */     try {
/* 149 */       connection = realServer.accept();
/* 150 */     } catch (ClosedChannelException e) {
/*     */       return;
/*     */     } 
/*     */     
/* 154 */     if (connection != null) {
/* 155 */       int i = 0;
/* 156 */       Runnable acceptTask = this.acceptTask;
/*     */       do {
/* 158 */         XnioIoThread thread = connection.getIoThread();
/* 159 */         ((Queue<NioSocketStreamConnection>)this.acceptQueues.get(thread.getNumber())).add(connection);
/* 160 */         thread.execute(acceptTask);
/* 161 */         if (++i == 128) {
/*     */           return;
/*     */         }
/*     */         
/*     */         try {
/* 166 */           connection = realServer.accept();
/* 167 */         } catch (ClosedChannelException e) {
/*     */           return;
/*     */         } 
/* 170 */       } while (connection != null);
/*     */     } 
/*     */   }
/*     */   
/*     */   void acceptTask() {
/* 175 */     WorkerThread current = WorkerThread.getCurrent();
/* 176 */     assert current != null;
/* 177 */     Queue<StreamConnection> queue = this.acceptQueues.get(current.getNumber());
/* 178 */     ChannelListeners.invokeChannelListener((Channel)this, getAcceptListener());
/* 179 */     if (!queue.isEmpty())
/* 180 */       current.execute(this.acceptTask); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\QueuedNioTcpServer2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */