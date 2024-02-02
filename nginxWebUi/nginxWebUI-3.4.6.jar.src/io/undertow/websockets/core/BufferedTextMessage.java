/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferedTextMessage
/*     */ {
/*  34 */   private final UTF8Output data = new UTF8Output();
/*     */   
/*     */   private final boolean bufferFullMessage;
/*     */   
/*     */   private final long maxMessageSize;
/*     */   
/*     */   private boolean complete;
/*     */   
/*     */   long currentSize;
/*     */ 
/*     */   
/*     */   public BufferedTextMessage(long maxMessageSize, boolean bufferFullMessage) {
/*  46 */     this.maxMessageSize = maxMessageSize;
/*  47 */     this.bufferFullMessage = bufferFullMessage;
/*     */   }
/*     */   
/*     */   public BufferedTextMessage(boolean bufferFullMessage) {
/*  51 */     this(-1L, bufferFullMessage);
/*     */   }
/*     */   
/*     */   private void checkMaxSize(StreamSourceFrameChannel channel, int res) throws IOException {
/*  55 */     if (res > 0) {
/*  56 */       this.currentSize += res;
/*     */     }
/*  58 */     if (this.maxMessageSize > 0L && this.currentSize > this.maxMessageSize) {
/*  59 */       WebSockets.sendClose(new CloseMessage(1009, WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize)), channel.getWebSocketChannel(), (WebSocketCallback<Void>)null);
/*  60 */       throw new IOException(WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void readBlocking(StreamSourceFrameChannel channel) throws IOException {
/*  65 */     PooledByteBuffer pooled = channel.getWebSocketChannel().getBufferPool().allocate();
/*  66 */     ByteBuffer buffer = pooled.getBuffer();
/*     */     try {
/*     */       while (true) {
/*  69 */         int res = channel.read(buffer);
/*  70 */         if (res == -1) {
/*  71 */           buffer.flip();
/*  72 */           this.data.write(new ByteBuffer[] { buffer });
/*  73 */           this.complete = true; return;
/*     */         } 
/*  75 */         if (res == 0) {
/*  76 */           channel.awaitReadable();
/*     */         }
/*  78 */         checkMaxSize(channel, res);
/*  79 */         if (!buffer.hasRemaining()) {
/*  80 */           buffer.flip();
/*  81 */           this.data.write(new ByteBuffer[] { buffer });
/*  82 */           buffer.compact();
/*  83 */           if (!this.bufferFullMessage) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */       } 
/*     */       return;
/*     */     } finally {
/*  90 */       pooled.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void read(StreamSourceFrameChannel channel, final WebSocketCallback<BufferedTextMessage> callback) {
/*  95 */     PooledByteBuffer pooled = channel.getWebSocketChannel().getBufferPool().allocate();
/*  96 */     ByteBuffer buffer = pooled.getBuffer();
/*     */     
/*     */     try {
/*     */       while (true) {
/* 100 */         int res = channel.read(buffer);
/* 101 */         if (res == -1) {
/* 102 */           this.complete = true;
/* 103 */           buffer.flip();
/* 104 */           this.data.write(new ByteBuffer[] { buffer });
/* 105 */           callback.complete(channel.getWebSocketChannel(), this); return;
/*     */         } 
/* 107 */         if (res == 0) {
/* 108 */           buffer.flip();
/* 109 */           if (buffer.hasRemaining()) {
/* 110 */             this.data.write(new ByteBuffer[] { buffer });
/* 111 */             if (!this.bufferFullMessage) {
/* 112 */               callback.complete(channel.getWebSocketChannel(), this);
/*     */             }
/*     */           } 
/* 115 */           channel.getReadSetter().set(new ChannelListener<StreamSourceFrameChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSourceFrameChannel channel) {
/* 118 */                   if (BufferedTextMessage.this.complete) {
/*     */                     return;
/*     */                   }
/* 121 */                   PooledByteBuffer pooled = channel.getWebSocketChannel().getBufferPool().allocate();
/* 122 */                   ByteBuffer buffer = pooled.getBuffer();
/*     */                   
/*     */                   try {
/*     */                     while (true) {
/* 126 */                       int res = channel.read(buffer);
/* 127 */                       if (res == -1) {
/* 128 */                         BufferedTextMessage.this.checkMaxSize(channel, res);
/* 129 */                         buffer.flip();
/* 130 */                         BufferedTextMessage.this.data.write(new ByteBuffer[] { buffer });
/* 131 */                         BufferedTextMessage.this.complete = true;
/* 132 */                         callback.complete(channel.getWebSocketChannel(), BufferedTextMessage.this); return;
/*     */                       } 
/* 134 */                       if (res == 0) {
/* 135 */                         buffer.flip();
/* 136 */                         if (buffer.hasRemaining()) {
/* 137 */                           BufferedTextMessage.this.data.write(new ByteBuffer[] { buffer });
/* 138 */                           if (!BufferedTextMessage.this.bufferFullMessage) {
/* 139 */                             callback.complete(channel.getWebSocketChannel(), BufferedTextMessage.this);
/*     */                           }
/*     */                         } 
/*     */                         return;
/*     */                       } 
/* 144 */                       if (!buffer.hasRemaining()) {
/* 145 */                         buffer.flip();
/* 146 */                         BufferedTextMessage.this.data.write(new ByteBuffer[] { buffer });
/* 147 */                         buffer.clear();
/* 148 */                         if (!BufferedTextMessage.this.bufferFullMessage) {
/* 149 */                           callback.complete(channel.getWebSocketChannel(), BufferedTextMessage.this);
/*     */                         }
/*     */                       } 
/*     */                     } 
/* 153 */                   } catch (IOException e) {
/* 154 */                     callback.onError(channel.getWebSocketChannel(), BufferedTextMessage.this, e);
/*     */                   } finally {
/*     */                     
/* 157 */                     pooled.close();
/*     */                   } 
/*     */                 }
/*     */               });
/* 161 */           channel.resumeReads();
/*     */           return;
/*     */         } 
/* 164 */         checkMaxSize(channel, res);
/* 165 */         if (!buffer.hasRemaining()) {
/* 166 */           buffer.flip();
/* 167 */           this.data.write(new ByteBuffer[] { buffer });
/* 168 */           buffer.clear();
/* 169 */           if (!this.bufferFullMessage) {
/* 170 */             callback.complete(channel.getWebSocketChannel(), this);
/*     */           }
/*     */         } 
/*     */       } 
/* 174 */     } catch (IOException e) {
/* 175 */       callback.onError(channel.getWebSocketChannel(), this, e);
/*     */     } finally {
/*     */       
/* 178 */       pooled.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getData() {
/* 189 */     return this.data.extract();
/*     */   }
/*     */   
/*     */   public boolean isComplete() {
/* 193 */     return this.complete;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\BufferedTextMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */