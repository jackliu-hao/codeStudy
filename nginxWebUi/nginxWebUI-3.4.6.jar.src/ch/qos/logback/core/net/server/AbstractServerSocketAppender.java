/*     */ package ch.qos.logback.core.net.server;
/*     */ 
/*     */ import ch.qos.logback.core.AppenderBase;
/*     */ import ch.qos.logback.core.spi.PreSerializationTransformer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.net.ServerSocketFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractServerSocketAppender<E>
/*     */   extends AppenderBase<E>
/*     */ {
/*     */   public static final int DEFAULT_BACKLOG = 50;
/*     */   public static final int DEFAULT_CLIENT_QUEUE_SIZE = 100;
/*  48 */   private int port = 4560;
/*  49 */   private int backlog = 50;
/*  50 */   private int clientQueueSize = 100;
/*     */   
/*     */   private String address;
/*     */   
/*     */   private ServerRunner<RemoteReceiverClient> runner;
/*     */ 
/*     */   
/*     */   public void start() {
/*  58 */     if (isStarted())
/*     */       return; 
/*     */     try {
/*  61 */       ServerSocket socket = getServerSocketFactory().createServerSocket(getPort(), getBacklog(), getInetAddress());
/*  62 */       ServerListener<RemoteReceiverClient> listener = createServerListener(socket);
/*     */       
/*  64 */       this.runner = createServerRunner(listener, getContext().getExecutorService());
/*  65 */       this.runner.setContext(getContext());
/*  66 */       getContext().getExecutorService().execute(this.runner);
/*  67 */       super.start();
/*  68 */     } catch (Exception ex) {
/*  69 */       addError("server startup error: " + ex, ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected ServerListener<RemoteReceiverClient> createServerListener(ServerSocket socket) {
/*  74 */     return new RemoteReceiverServerListener(socket);
/*     */   }
/*     */   
/*     */   protected ServerRunner<RemoteReceiverClient> createServerRunner(ServerListener<RemoteReceiverClient> listener, Executor executor) {
/*  78 */     return new RemoteReceiverServerRunner(listener, executor, getClientQueueSize());
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/*  83 */     if (!isStarted())
/*     */       return; 
/*     */     try {
/*  86 */       this.runner.stop();
/*  87 */       super.stop();
/*  88 */     } catch (IOException ex) {
/*  89 */       addError("server shutdown error: " + ex, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void append(E event) {
/*  95 */     if (event == null)
/*     */       return; 
/*  97 */     postProcessEvent(event);
/*  98 */     final Serializable serEvent = getPST().transform(event);
/*  99 */     this.runner.accept(new ClientVisitor<RemoteReceiverClient>() {
/*     */           public void visit(RemoteReceiverClient client) {
/* 101 */             client.offer(serEvent);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void postProcessEvent(E paramE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract PreSerializationTransformer<E> getPST();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServerSocketFactory getServerSocketFactory() throws Exception {
/* 129 */     return ServerSocketFactory.getDefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InetAddress getInetAddress() throws UnknownHostException {
/* 138 */     if (getAddress() == null)
/* 139 */       return null; 
/* 140 */     return InetAddress.getByName(getAddress());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 148 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPort(int port) {
/* 156 */     this.port = port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBacklog() {
/* 168 */     return this.backlog;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBacklog(int backlog) {
/* 180 */     this.backlog = backlog;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAddress() {
/* 188 */     return this.address;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAddress(String address) {
/* 196 */     this.address = address;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getClientQueueSize() {
/* 204 */     return this.clientQueueSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setClientQueueSize(int clientQueueSize) {
/* 212 */     this.clientQueueSize = clientQueueSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\AbstractServerSocketAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */