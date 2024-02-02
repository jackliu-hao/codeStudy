/*     */ package ch.qos.logback.classic.net.server;
/*     */ 
/*     */ import ch.qos.logback.classic.net.ReceiverBase;
/*     */ import ch.qos.logback.core.net.server.ServerListener;
/*     */ import ch.qos.logback.core.net.server.ServerRunner;
/*     */ import ch.qos.logback.core.util.CloseUtil;
/*     */ import java.io.IOException;
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
/*     */ public class ServerSocketReceiver
/*     */   extends ReceiverBase
/*     */ {
/*     */   public static final int DEFAULT_BACKLOG = 50;
/*  42 */   private int port = 4560;
/*  43 */   private int backlog = 50;
/*     */ 
/*     */   
/*     */   private String address;
/*     */   
/*     */   private ServerSocket serverSocket;
/*     */   
/*     */   private ServerRunner runner;
/*     */ 
/*     */   
/*     */   protected boolean shouldStart() {
/*     */     try {
/*  55 */       ServerSocket serverSocket = getServerSocketFactory().createServerSocket(getPort(), getBacklog(), getInetAddress());
/*     */       
/*  57 */       ServerListener<RemoteAppenderClient> listener = createServerListener(serverSocket);
/*     */       
/*  59 */       this.runner = createServerRunner(listener, getContext().getExecutorService());
/*  60 */       this.runner.setContext(getContext());
/*  61 */       return true;
/*  62 */     } catch (Exception ex) {
/*  63 */       addError("server startup error: " + ex, ex);
/*  64 */       CloseUtil.closeQuietly(this.serverSocket);
/*  65 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected ServerListener<RemoteAppenderClient> createServerListener(ServerSocket socket) {
/*  70 */     return (ServerListener<RemoteAppenderClient>)new RemoteAppenderServerListener(socket);
/*     */   }
/*     */   
/*     */   protected ServerRunner createServerRunner(ServerListener<RemoteAppenderClient> listener, Executor executor) {
/*  74 */     return (ServerRunner)new RemoteAppenderServerRunner(listener, executor);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Runnable getRunnableTask() {
/*  79 */     return (Runnable)this.runner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onStop() {
/*     */     try {
/*  87 */       if (this.runner == null)
/*     */         return; 
/*  89 */       this.runner.stop();
/*  90 */     } catch (IOException ex) {
/*  91 */       addError("server shutdown error: " + ex, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ServerSocketFactory getServerSocketFactory() throws Exception {
/* 103 */     return ServerSocketFactory.getDefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InetAddress getInetAddress() throws UnknownHostException {
/* 112 */     if (getAddress() == null)
/* 113 */       return null; 
/* 114 */     return InetAddress.getByName(getAddress());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 122 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPort(int port) {
/* 130 */     this.port = port;
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
/* 142 */     return this.backlog;
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
/* 154 */     this.backlog = backlog;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAddress() {
/* 162 */     return this.address;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAddress(String address) {
/* 170 */     this.address = address;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\server\ServerSocketReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */