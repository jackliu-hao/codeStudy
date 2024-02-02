/*     */ package org.apache.http.impl.bootstrap;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.net.ServerSocketFactory;
/*     */ import javax.net.ssl.SSLServerSocket;
/*     */ import org.apache.http.ExceptionLogger;
/*     */ import org.apache.http.HttpConnectionFactory;
/*     */ import org.apache.http.HttpServerConnection;
/*     */ import org.apache.http.config.SocketConfig;
/*     */ import org.apache.http.impl.DefaultBHttpServerConnection;
/*     */ import org.apache.http.protocol.HttpService;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpServer
/*     */ {
/*     */   private final int port;
/*     */   private final InetAddress ifAddress;
/*     */   private final SocketConfig socketConfig;
/*     */   private final ServerSocketFactory serverSocketFactory;
/*     */   private final HttpService httpService;
/*     */   private final HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory;
/*     */   private final SSLServerSetupHandler sslSetupHandler;
/*     */   private final ExceptionLogger exceptionLogger;
/*     */   private final ThreadPoolExecutor listenerExecutorService;
/*     */   private final ThreadGroup workerThreads;
/*     */   private final WorkerPoolExecutor workerExecutorService;
/*     */   private final AtomicReference<Status> status;
/*     */   private volatile ServerSocket serverSocket;
/*     */   private volatile RequestListener requestListener;
/*     */   
/*     */   enum Status
/*     */   {
/*  53 */     READY, ACTIVE, STOPPING;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   HttpServer(int port, InetAddress ifAddress, SocketConfig socketConfig, ServerSocketFactory serverSocketFactory, HttpService httpService, HttpConnectionFactory<? extends DefaultBHttpServerConnection> connectionFactory, SSLServerSetupHandler sslSetupHandler, ExceptionLogger exceptionLogger) {
/*  80 */     this.port = port;
/*  81 */     this.ifAddress = ifAddress;
/*  82 */     this.socketConfig = socketConfig;
/*  83 */     this.serverSocketFactory = serverSocketFactory;
/*  84 */     this.httpService = httpService;
/*  85 */     this.connectionFactory = connectionFactory;
/*  86 */     this.sslSetupHandler = sslSetupHandler;
/*  87 */     this.exceptionLogger = exceptionLogger;
/*  88 */     this.listenerExecutorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryImpl("HTTP-listener-" + this.port));
/*     */ 
/*     */ 
/*     */     
/*  92 */     this.workerThreads = new ThreadGroup("HTTP-workers");
/*  93 */     this.workerExecutorService = new WorkerPoolExecutor(0, 2147483647, 1L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryImpl("HTTP-worker", this.workerThreads));
/*     */ 
/*     */ 
/*     */     
/*  97 */     this.status = new AtomicReference<Status>(Status.READY);
/*     */   }
/*     */   
/*     */   public InetAddress getInetAddress() {
/* 101 */     ServerSocket localSocket = this.serverSocket;
/* 102 */     return (localSocket != null) ? localSocket.getInetAddress() : null;
/*     */   }
/*     */   
/*     */   public int getLocalPort() {
/* 106 */     ServerSocket localSocket = this.serverSocket;
/* 107 */     return (localSocket != null) ? localSocket.getLocalPort() : -1;
/*     */   }
/*     */   
/*     */   public void start() throws IOException {
/* 111 */     if (this.status.compareAndSet(Status.READY, Status.ACTIVE)) {
/* 112 */       this.serverSocket = this.serverSocketFactory.createServerSocket(this.port, this.socketConfig.getBacklogSize(), this.ifAddress);
/*     */       
/* 114 */       this.serverSocket.setReuseAddress(this.socketConfig.isSoReuseAddress());
/* 115 */       if (this.socketConfig.getRcvBufSize() > 0) {
/* 116 */         this.serverSocket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
/*     */       }
/* 118 */       if (this.sslSetupHandler != null && this.serverSocket instanceof SSLServerSocket) {
/* 119 */         this.sslSetupHandler.initialize((SSLServerSocket)this.serverSocket);
/*     */       }
/* 121 */       this.requestListener = new RequestListener(this.socketConfig, this.serverSocket, this.httpService, (HttpConnectionFactory)this.connectionFactory, this.exceptionLogger, this.workerExecutorService);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 128 */       this.listenerExecutorService.execute(this.requestListener);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void stop() {
/* 133 */     if (this.status.compareAndSet(Status.ACTIVE, Status.STOPPING)) {
/* 134 */       this.listenerExecutorService.shutdown();
/* 135 */       this.workerExecutorService.shutdown();
/* 136 */       RequestListener local = this.requestListener;
/* 137 */       if (local != null) {
/*     */         try {
/* 139 */           local.terminate();
/* 140 */         } catch (IOException ex) {
/* 141 */           this.exceptionLogger.log(ex);
/*     */         } 
/*     */       }
/* 144 */       this.workerThreads.interrupt();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
/* 149 */     this.workerExecutorService.awaitTermination(timeout, timeUnit);
/*     */   }
/*     */   
/*     */   public void shutdown(long gracePeriod, TimeUnit timeUnit) {
/* 153 */     stop();
/* 154 */     if (gracePeriod > 0L) {
/*     */       try {
/* 156 */         awaitTermination(gracePeriod, timeUnit);
/* 157 */       } catch (InterruptedException ex) {
/* 158 */         Thread.currentThread().interrupt();
/*     */       } 
/*     */     }
/* 161 */     Set<Worker> workers = this.workerExecutorService.getWorkers();
/* 162 */     for (Worker worker : workers) {
/* 163 */       HttpServerConnection conn = worker.getConnection();
/*     */       try {
/* 165 */         conn.shutdown();
/* 166 */       } catch (IOException ex) {
/* 167 */         this.exceptionLogger.log(ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\bootstrap\HttpServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */