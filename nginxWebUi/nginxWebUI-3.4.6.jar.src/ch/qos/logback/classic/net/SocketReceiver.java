/*     */ package ch.qos.logback.classic.net;
/*     */ 
/*     */ import ch.qos.logback.classic.Logger;
/*     */ import ch.qos.logback.classic.LoggerContext;
/*     */ import ch.qos.logback.classic.net.server.HardenedLoggingEventInputStream;
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.core.net.DefaultSocketConnector;
/*     */ import ch.qos.logback.core.net.SocketConnector;
/*     */ import ch.qos.logback.core.util.CloseUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.RejectedExecutionException;
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
/*     */ public class SocketReceiver
/*     */   extends ReceiverBase
/*     */   implements Runnable, SocketConnector.ExceptionHandler
/*     */ {
/*     */   private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
/*     */   private String remoteHost;
/*     */   private InetAddress address;
/*     */   private int port;
/*     */   private int reconnectionDelay;
/*  52 */   private int acceptConnectionTimeout = 5000;
/*     */   
/*     */   private String receiverId;
/*     */   
/*     */   private volatile Socket socket;
/*     */   
/*     */   private Future<Socket> connectorTask;
/*     */ 
/*     */   
/*     */   protected boolean shouldStart() {
/*  62 */     int errorCount = 0;
/*  63 */     if (this.port == 0) {
/*  64 */       errorCount++;
/*  65 */       addError("No port was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_port");
/*     */     } 
/*     */     
/*  68 */     if (this.remoteHost == null) {
/*  69 */       errorCount++;
/*  70 */       addError("No host name or address was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_host");
/*     */     } 
/*     */ 
/*     */     
/*  74 */     if (this.reconnectionDelay == 0) {
/*  75 */       this.reconnectionDelay = 30000;
/*     */     }
/*     */     
/*  78 */     if (errorCount == 0) {
/*     */       try {
/*  80 */         this.address = InetAddress.getByName(this.remoteHost);
/*  81 */       } catch (UnknownHostException ex) {
/*  82 */         addError("unknown host: " + this.remoteHost);
/*  83 */         errorCount++;
/*     */       } 
/*     */     }
/*     */     
/*  87 */     if (errorCount == 0) {
/*  88 */       this.receiverId = "receiver " + this.remoteHost + ":" + this.port + ": ";
/*     */     }
/*     */     
/*  91 */     return (errorCount == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onStop() {
/*  98 */     if (this.socket != null) {
/*  99 */       CloseUtil.closeQuietly(this.socket);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected Runnable getRunnableTask() {
/* 105 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/* 113 */       LoggerContext lc = (LoggerContext)getContext();
/* 114 */       while (!Thread.currentThread().isInterrupted()) {
/* 115 */         SocketConnector connector = createConnector(this.address, this.port, 0, this.reconnectionDelay);
/* 116 */         this.connectorTask = activateConnector(connector);
/* 117 */         if (this.connectorTask == null) {
/*     */           break;
/*     */         }
/* 120 */         this.socket = waitForConnectorToReturnASocket();
/* 121 */         if (this.socket == null)
/*     */           break; 
/* 123 */         dispatchEvents(lc);
/*     */       } 
/* 125 */     } catch (InterruptedException interruptedException) {}
/*     */ 
/*     */     
/* 128 */     addInfo("shutting down");
/*     */   }
/*     */   
/*     */   private SocketConnector createConnector(InetAddress address, int port, int initialDelay, int retryDelay) {
/* 132 */     SocketConnector connector = newConnector(address, port, initialDelay, retryDelay);
/* 133 */     connector.setExceptionHandler(this);
/* 134 */     connector.setSocketFactory(getSocketFactory());
/* 135 */     return connector;
/*     */   }
/*     */   
/*     */   private Future<Socket> activateConnector(SocketConnector connector) {
/*     */     try {
/* 140 */       return getContext().getScheduledExecutorService().submit((Callable<Socket>)connector);
/* 141 */     } catch (RejectedExecutionException ex) {
/* 142 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Socket waitForConnectorToReturnASocket() throws InterruptedException {
/*     */     try {
/* 148 */       Socket s = this.connectorTask.get();
/* 149 */       this.connectorTask = null;
/* 150 */       return s;
/* 151 */     } catch (ExecutionException e) {
/* 152 */       return null;
/*     */     } 
/*     */   }
/*     */   private void dispatchEvents(LoggerContext lc) {
/*     */     HardenedLoggingEventInputStream hardenedLoggingEventInputStream;
/* 157 */     ObjectInputStream ois = null;
/*     */     try {
/* 159 */       this.socket.setSoTimeout(this.acceptConnectionTimeout);
/* 160 */       hardenedLoggingEventInputStream = new HardenedLoggingEventInputStream(this.socket.getInputStream());
/* 161 */       this.socket.setSoTimeout(0);
/* 162 */       addInfo(this.receiverId + "connection established");
/*     */       while (true) {
/* 164 */         ILoggingEvent event = (ILoggingEvent)hardenedLoggingEventInputStream.readObject();
/* 165 */         Logger remoteLogger = lc.getLogger(event.getLoggerName());
/* 166 */         if (remoteLogger.isEnabledFor(event.getLevel())) {
/* 167 */           remoteLogger.callAppenders(event);
/*     */         }
/*     */       } 
/* 170 */     } catch (EOFException ex) {
/* 171 */       addInfo(this.receiverId + "end-of-stream detected");
/* 172 */     } catch (IOException ex) {
/* 173 */       addInfo(this.receiverId + "connection failed: " + ex);
/* 174 */     } catch (ClassNotFoundException ex) {
/* 175 */       addInfo(this.receiverId + "unknown event class: " + ex);
/*     */     } finally {
/* 177 */       CloseUtil.closeQuietly((Closeable)hardenedLoggingEventInputStream);
/* 178 */       CloseUtil.closeQuietly(this.socket);
/* 179 */       this.socket = null;
/* 180 */       addInfo(this.receiverId + "connection closed");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connectionFailed(SocketConnector connector, Exception ex) {
/* 188 */     if (ex instanceof InterruptedException) {
/* 189 */       addInfo("connector interrupted");
/* 190 */     } else if (ex instanceof java.net.ConnectException) {
/* 191 */       addInfo(this.receiverId + "connection refused");
/*     */     } else {
/* 193 */       addInfo(this.receiverId + ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected SocketConnector newConnector(InetAddress address, int port, int initialDelay, int retryDelay) {
/* 198 */     return (SocketConnector)new DefaultSocketConnector(address, port, initialDelay, retryDelay);
/*     */   }
/*     */   
/*     */   protected SocketFactory getSocketFactory() {
/* 202 */     return SocketFactory.getDefault();
/*     */   }
/*     */   
/*     */   public void setRemoteHost(String remoteHost) {
/* 206 */     this.remoteHost = remoteHost;
/*     */   }
/*     */   
/*     */   public void setPort(int port) {
/* 210 */     this.port = port;
/*     */   }
/*     */   
/*     */   public void setReconnectionDelay(int reconnectionDelay) {
/* 214 */     this.reconnectionDelay = reconnectionDelay;
/*     */   }
/*     */   
/*     */   public void setAcceptConnectionTimeout(int acceptConnectionTimeout) {
/* 218 */     this.acceptConnectionTimeout = acceptConnectionTimeout;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SocketReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */