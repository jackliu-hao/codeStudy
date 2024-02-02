/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.Message;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJCommunicationsException;
/*     */ import com.mysql.cj.exceptions.CJPacketTooBigException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.MessageSender;
/*     */ import com.mysql.cj.protocol.PacketSentTimeHolder;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.channels.CompletionHandler;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SyncMessageSender
/*     */   implements MessageSender<XMessage>, PacketSentTimeHolder
/*     */ {
/*     */   static final int HEADER_LEN = 5;
/*     */   private OutputStream outputStream;
/*  56 */   private long lastPacketSentTime = 0L;
/*  57 */   private long previousPacketSentTime = 0L;
/*  58 */   private int maxAllowedPacket = -1;
/*     */ 
/*     */   
/*  61 */   Object waitingAsyncOperationMonitor = new Object();
/*     */   
/*     */   public SyncMessageSender(OutputStream os) {
/*  64 */     this.outputStream = os;
/*     */   }
/*     */   
/*     */   public void send(XMessage message) {
/*  68 */     synchronized (this.waitingAsyncOperationMonitor) {
/*  69 */       Message message1 = message.getMessage();
/*     */       try {
/*  71 */         int type = MessageConstants.getTypeForMessageClass((Class)message1.getClass());
/*  72 */         int size = 1 + message1.getSerializedSize();
/*  73 */         if (this.maxAllowedPacket > 0 && size > this.maxAllowedPacket) {
/*  74 */           throw new CJPacketTooBigException(Messages.getString("PacketTooBigException.1", new Object[] { Integer.valueOf(size), Integer.valueOf(this.maxAllowedPacket) }));
/*     */         }
/*     */ 
/*     */         
/*  78 */         byte[] sizeHeader = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(size).array();
/*  79 */         this.outputStream.write(sizeHeader);
/*  80 */         this.outputStream.write(type);
/*  81 */         message1.writeTo(this.outputStream);
/*  82 */         this.outputStream.flush();
/*  83 */         this.previousPacketSentTime = this.lastPacketSentTime;
/*  84 */         this.lastPacketSentTime = System.currentTimeMillis();
/*  85 */       } catch (IOException ex) {
/*  86 */         throw new CJCommunicationsException("Unable to write message", ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public CompletableFuture<?> send(XMessage message, CompletableFuture<?> future, Runnable callback) {
/*  93 */     synchronized (this.waitingAsyncOperationMonitor) {
/*  94 */       CompletionHandler<Long, Void> resultHandler = new ErrorToFutureCompletionHandler<>(future, callback);
/*  95 */       Message message1 = message.getMessage();
/*     */       try {
/*  97 */         send(message);
/*  98 */         long result = (5 + message1.getSerializedSize());
/*  99 */         resultHandler.completed(Long.valueOf(result), null);
/* 100 */       } catch (Throwable t) {
/* 101 */         resultHandler.failed(t, null);
/*     */       } 
/* 103 */       return future;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getLastPacketSentTime() {
/* 108 */     return this.lastPacketSentTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPreviousPacketSentTime() {
/* 113 */     return this.previousPacketSentTime;
/*     */   }
/*     */   
/*     */   public void setMaxAllowedPacket(int maxAllowedPacket) {
/* 117 */     this.maxAllowedPacket = maxAllowedPacket;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\SyncMessageSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */