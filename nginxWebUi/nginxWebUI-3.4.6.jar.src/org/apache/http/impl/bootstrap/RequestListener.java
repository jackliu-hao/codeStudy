/*     */ package org.apache.http.impl.bootstrap;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.apache.http.ExceptionLogger;
/*     */ import org.apache.http.HttpConnectionFactory;
/*     */ import org.apache.http.HttpServerConnection;
/*     */ import org.apache.http.config.SocketConfig;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class RequestListener
/*     */   implements Runnable
/*     */ {
/*     */   private final SocketConfig socketConfig;
/*     */   private final ServerSocket serversocket;
/*     */   private final HttpService httpService;
/*     */   private final HttpConnectionFactory<? extends HttpServerConnection> connectionFactory;
/*     */   private final ExceptionLogger exceptionLogger;
/*     */   private final ExecutorService executorService;
/*     */   private final AtomicBoolean terminated;
/*     */   
/*     */   public RequestListener(SocketConfig socketConfig, ServerSocket serversocket, HttpService httpService, HttpConnectionFactory<? extends HttpServerConnection> connectionFactory, ExceptionLogger exceptionLogger, ExecutorService executorService) {
/*  61 */     this.socketConfig = socketConfig;
/*  62 */     this.serversocket = serversocket;
/*  63 */     this.connectionFactory = connectionFactory;
/*  64 */     this.httpService = httpService;
/*  65 */     this.exceptionLogger = exceptionLogger;
/*  66 */     this.executorService = executorService;
/*  67 */     this.terminated = new AtomicBoolean(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*  73 */       while (!isTerminated() && !Thread.interrupted()) {
/*  74 */         Socket socket = this.serversocket.accept();
/*  75 */         socket.setSoTimeout(this.socketConfig.getSoTimeout());
/*  76 */         socket.setKeepAlive(this.socketConfig.isSoKeepAlive());
/*  77 */         socket.setTcpNoDelay(this.socketConfig.isTcpNoDelay());
/*  78 */         if (this.socketConfig.getRcvBufSize() > 0) {
/*  79 */           socket.setReceiveBufferSize(this.socketConfig.getRcvBufSize());
/*     */         }
/*  81 */         if (this.socketConfig.getSndBufSize() > 0) {
/*  82 */           socket.setSendBufferSize(this.socketConfig.getSndBufSize());
/*     */         }
/*  84 */         if (this.socketConfig.getSoLinger() >= 0) {
/*  85 */           socket.setSoLinger(true, this.socketConfig.getSoLinger());
/*     */         }
/*  87 */         HttpServerConnection conn = (HttpServerConnection)this.connectionFactory.createConnection(socket);
/*  88 */         Worker worker = new Worker(this.httpService, conn, this.exceptionLogger);
/*  89 */         this.executorService.execute(worker);
/*     */       } 
/*  91 */     } catch (Exception ex) {
/*  92 */       this.exceptionLogger.log(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isTerminated() {
/*  97 */     return this.terminated.get();
/*     */   }
/*     */   
/*     */   public void terminate() throws IOException {
/* 101 */     if (this.terminated.compareAndSet(false, true))
/* 102 */       this.serversocket.close(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\bootstrap\RequestListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */