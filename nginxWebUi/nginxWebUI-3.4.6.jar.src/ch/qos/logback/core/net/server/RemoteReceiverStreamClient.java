/*     */ package ch.qos.logback.core.net.server;
/*     */ 
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.util.CloseUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class RemoteReceiverStreamClient
/*     */   extends ContextAwareBase
/*     */   implements RemoteReceiverClient
/*     */ {
/*     */   private final String clientId;
/*     */   private final Socket socket;
/*     */   private final OutputStream outputStream;
/*     */   private BlockingQueue<Serializable> queue;
/*     */   
/*     */   public RemoteReceiverStreamClient(String id, Socket socket) {
/*  48 */     this.clientId = "client " + id + ": ";
/*  49 */     this.socket = socket;
/*  50 */     this.outputStream = null;
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
/*     */   RemoteReceiverStreamClient(String id, OutputStream outputStream) {
/*  63 */     this.clientId = "client " + id + ": ";
/*  64 */     this.socket = null;
/*  65 */     this.outputStream = outputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQueue(BlockingQueue<Serializable> queue) {
/*  72 */     this.queue = queue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean offer(Serializable event) {
/*  79 */     if (this.queue == null) {
/*  80 */       throw new IllegalStateException("client has no event queue");
/*     */     }
/*  82 */     return this.queue.offer(event);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  89 */     if (this.socket == null)
/*     */       return; 
/*  91 */     CloseUtil.closeQuietly(this.socket);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*  98 */     addInfo(this.clientId + "connected");
/*     */     
/* 100 */     ObjectOutputStream oos = null;
/*     */     try {
/* 102 */       int counter = 0;
/* 103 */       oos = createObjectOutputStream();
/* 104 */       while (!Thread.currentThread().isInterrupted()) {
/*     */         try {
/* 106 */           Serializable event = this.queue.take();
/* 107 */           oos.writeObject(event);
/* 108 */           oos.flush();
/* 109 */           if (++counter >= 70) {
/*     */ 
/*     */             
/* 112 */             counter = 0;
/* 113 */             oos.reset();
/*     */           } 
/* 115 */         } catch (InterruptedException ex) {
/* 116 */           Thread.currentThread().interrupt();
/*     */         } 
/*     */       } 
/* 119 */     } catch (SocketException ex) {
/* 120 */       addInfo(this.clientId + ex);
/* 121 */     } catch (IOException ex) {
/* 122 */       addError(this.clientId + ex);
/* 123 */     } catch (RuntimeException ex) {
/* 124 */       addError(this.clientId + ex);
/*     */     } finally {
/* 126 */       if (oos != null) {
/* 127 */         CloseUtil.closeQuietly(oos);
/*     */       }
/* 129 */       close();
/* 130 */       addInfo(this.clientId + "connection closed");
/*     */     } 
/*     */   }
/*     */   
/*     */   private ObjectOutputStream createObjectOutputStream() throws IOException {
/* 135 */     if (this.socket == null) {
/* 136 */       return new ObjectOutputStream(this.outputStream);
/*     */     }
/* 138 */     return new ObjectOutputStream(this.socket.getOutputStream());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\RemoteReceiverStreamClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */