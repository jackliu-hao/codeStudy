/*     */ package ch.qos.logback.core.net;
/*     */ 
/*     */ import ch.qos.logback.core.util.DelayStrategy;
/*     */ import ch.qos.logback.core.util.FixedDelay;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import javax.net.SocketFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultSocketConnector
/*     */   implements SocketConnector
/*     */ {
/*     */   private final InetAddress address;
/*     */   private final int port;
/*     */   private final DelayStrategy delayStrategy;
/*     */   private SocketConnector.ExceptionHandler exceptionHandler;
/*     */   private SocketFactory socketFactory;
/*     */   
/*     */   public DefaultSocketConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
/*  49 */     this(address, port, (DelayStrategy)new FixedDelay(initialDelay, retryDelay));
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
/*     */   public DefaultSocketConnector(InetAddress address, int port, DelayStrategy delayStrategy) {
/*  61 */     this.address = address;
/*  62 */     this.port = port;
/*  63 */     this.delayStrategy = delayStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket call() throws InterruptedException {
/*  70 */     useDefaultsForMissingFields();
/*  71 */     Socket socket = createSocket();
/*  72 */     while (socket == null && !Thread.currentThread().isInterrupted()) {
/*  73 */       Thread.sleep(this.delayStrategy.nextDelay());
/*  74 */       socket = createSocket();
/*     */     } 
/*  76 */     return socket;
/*     */   }
/*     */   
/*     */   private Socket createSocket() {
/*  80 */     Socket newSocket = null;
/*     */     try {
/*  82 */       newSocket = this.socketFactory.createSocket(this.address, this.port);
/*  83 */     } catch (IOException ioex) {
/*  84 */       this.exceptionHandler.connectionFailed(this, ioex);
/*     */     } 
/*  86 */     return newSocket;
/*     */   }
/*     */   
/*     */   private void useDefaultsForMissingFields() {
/*  90 */     if (this.exceptionHandler == null) {
/*  91 */       this.exceptionHandler = new ConsoleExceptionHandler();
/*     */     }
/*  93 */     if (this.socketFactory == null) {
/*  94 */       this.socketFactory = SocketFactory.getDefault();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExceptionHandler(SocketConnector.ExceptionHandler exceptionHandler) {
/* 102 */     this.exceptionHandler = exceptionHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSocketFactory(SocketFactory socketFactory) {
/* 109 */     this.socketFactory = socketFactory;
/*     */   }
/*     */   
/*     */   private static class ConsoleExceptionHandler
/*     */     implements SocketConnector.ExceptionHandler
/*     */   {
/*     */     private ConsoleExceptionHandler() {}
/*     */     
/*     */     public void connectionFailed(SocketConnector connector, Exception ex) {
/* 118 */       System.out.println(ex);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\DefaultSocketConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */