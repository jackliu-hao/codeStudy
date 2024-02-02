/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.util.ImmediatePooled;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Pooled;
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
/*     */ public class BufferedBinaryMessage
/*     */ {
/*     */   private final boolean bufferFullMessage;
/*  40 */   private List<PooledByteBuffer> data = new ArrayList<>(1);
/*     */   
/*     */   private PooledByteBuffer current;
/*     */   
/*     */   private final long maxMessageSize;
/*     */   private long currentSize;
/*     */   private boolean complete;
/*     */   
/*     */   public BufferedBinaryMessage(long maxMessageSize, boolean bufferFullMessage) {
/*  49 */     this.bufferFullMessage = bufferFullMessage;
/*  50 */     this.maxMessageSize = maxMessageSize;
/*     */   }
/*     */   
/*     */   public BufferedBinaryMessage(boolean bufferFullMessage) {
/*  54 */     this(-1L, bufferFullMessage);
/*     */   }
/*     */   
/*     */   public void readBlocking(StreamSourceFrameChannel channel) throws IOException {
/*  58 */     if (this.current == null) {
/*  59 */       this.current = channel.getWebSocketChannel().getBufferPool().allocate();
/*     */     }
/*     */     while (true) {
/*  62 */       int res = channel.read(this.current.getBuffer());
/*  63 */       if (res == -1) {
/*  64 */         this.complete = true; return;
/*     */       } 
/*  66 */       if (res == 0) {
/*  67 */         channel.awaitReadable();
/*     */       }
/*  69 */       checkMaxSize(channel, res);
/*  70 */       if (this.bufferFullMessage) {
/*  71 */         dealWithFullBuffer(channel); continue;
/*  72 */       }  if (!this.current.getBuffer().hasRemaining()) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void dealWithFullBuffer(StreamSourceFrameChannel channel) {
/*  79 */     if (!this.current.getBuffer().hasRemaining()) {
/*  80 */       this.current.getBuffer().flip();
/*  81 */       this.data.add(this.current);
/*  82 */       this.current = channel.getWebSocketChannel().getBufferPool().allocate();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void read(StreamSourceFrameChannel channel, final WebSocketCallback<BufferedBinaryMessage> callback) {
/*     */     try {
/*     */       while (true) {
/*  89 */         if (this.current == null) {
/*  90 */           this.current = channel.getWebSocketChannel().getBufferPool().allocate();
/*     */         }
/*  92 */         int res = channel.read(this.current.getBuffer());
/*  93 */         if (res == -1) {
/*  94 */           this.complete = true;
/*  95 */           callback.complete(channel.getWebSocketChannel(), this); return;
/*     */         } 
/*  97 */         if (res == 0) {
/*  98 */           if (!this.bufferFullMessage) {
/*  99 */             callback.complete(channel.getWebSocketChannel(), this);
/*     */           }
/* 101 */           channel.getReadSetter().set(new ChannelListener<StreamSourceFrameChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSourceFrameChannel channel) {
/* 104 */                   if (BufferedBinaryMessage.this.complete) {
/*     */                     return;
/*     */                   }
/*     */                   try {
/*     */                     while (true) {
/* 109 */                       if (BufferedBinaryMessage.this.current == null) {
/* 110 */                         BufferedBinaryMessage.this.current = channel.getWebSocketChannel().getBufferPool().allocate();
/*     */                       }
/* 112 */                       int res = channel.read(BufferedBinaryMessage.this.current.getBuffer());
/* 113 */                       if (res == -1) {
/* 114 */                         BufferedBinaryMessage.this.complete = true;
/* 115 */                         channel.suspendReads();
/* 116 */                         callback.complete(channel.getWebSocketChannel(), BufferedBinaryMessage.this); return;
/*     */                       } 
/* 118 */                       if (res == 0) {
/*     */                         return;
/*     */                       }
/*     */                       
/* 122 */                       BufferedBinaryMessage.this.checkMaxSize(channel, res);
/* 123 */                       if (BufferedBinaryMessage.this.bufferFullMessage) {
/* 124 */                         BufferedBinaryMessage.this.dealWithFullBuffer(channel); continue;
/* 125 */                       }  if (!BufferedBinaryMessage.this.current.getBuffer().hasRemaining()) {
/* 126 */                         callback.complete(channel.getWebSocketChannel(), BufferedBinaryMessage.this);
/*     */                       }
/*     */                     } 
/* 129 */                   } catch (IOException e) {
/* 130 */                     channel.suspendReads();
/* 131 */                     callback.onError(channel.getWebSocketChannel(), BufferedBinaryMessage.this, e);
/*     */                     return;
/*     */                   }  }
/*     */               });
/* 135 */           channel.resumeReads();
/*     */           
/*     */           return;
/*     */         } 
/* 139 */         checkMaxSize(channel, res);
/* 140 */         if (this.bufferFullMessage) {
/* 141 */           dealWithFullBuffer(channel); continue;
/* 142 */         }  if (!this.current.getBuffer().hasRemaining()) {
/* 143 */           callback.complete(channel.getWebSocketChannel(), this);
/*     */         }
/*     */       } 
/* 146 */     } catch (IOException e) {
/* 147 */       callback.onError(channel.getWebSocketChannel(), this, e);
/*     */       return;
/*     */     } 
/*     */   }
/*     */   private void checkMaxSize(StreamSourceFrameChannel channel, int res) throws IOException {
/* 152 */     this.currentSize += res;
/* 153 */     if (this.maxMessageSize > 0L && this.currentSize > this.maxMessageSize) {
/* 154 */       getData().free();
/* 155 */       WebSockets.sendClose(new CloseMessage(1009, WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize)), channel.getWebSocketChannel(), (WebSocketCallback<Void>)null);
/* 156 */       throw new IOException(WebSocketMessages.MESSAGES.messageToBig(this.maxMessageSize));
/*     */     } 
/*     */   }
/*     */   
/*     */   public Pooled<ByteBuffer[]> getData() {
/* 161 */     if (this.current == null) {
/* 162 */       return (Pooled<ByteBuffer[]>)new ImmediatePooled(new ByteBuffer[0]);
/*     */     }
/* 164 */     if (this.data.isEmpty()) {
/* 165 */       PooledByteBuffer current = this.current;
/* 166 */       current.getBuffer().flip();
/* 167 */       this.current = null;
/* 168 */       ByteBuffer[] arrayOfByteBuffer = { current.getBuffer() };
/* 169 */       return new PooledByteBufferArray(Collections.singletonList(current), arrayOfByteBuffer);
/*     */     } 
/* 171 */     this.current.getBuffer().flip();
/* 172 */     this.data.add(this.current);
/* 173 */     this.current = null;
/* 174 */     ByteBuffer[] ret = new ByteBuffer[this.data.size()];
/* 175 */     for (int i = 0; i < this.data.size(); i++) {
/* 176 */       ret[i] = ((PooledByteBuffer)this.data.get(i)).getBuffer();
/*     */     }
/* 178 */     List<PooledByteBuffer> data = this.data;
/* 179 */     this.data = new ArrayList<>();
/*     */     
/* 181 */     return new PooledByteBufferArray(data, ret);
/*     */   }
/*     */   
/*     */   public boolean isComplete() {
/* 185 */     return this.complete;
/*     */   }
/*     */   
/*     */   private static final class PooledByteBufferArray
/*     */     implements Pooled<ByteBuffer[]> {
/*     */     private final List<PooledByteBuffer> pooled;
/*     */     private final ByteBuffer[] data;
/*     */     
/*     */     private PooledByteBufferArray(List<PooledByteBuffer> pooled, ByteBuffer[] data) {
/* 194 */       this.pooled = pooled;
/* 195 */       this.data = data;
/*     */     }
/*     */ 
/*     */     
/*     */     public void discard() {
/* 200 */       for (PooledByteBuffer item : this.pooled) {
/* 201 */         item.close();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void free() {
/* 207 */       for (PooledByteBuffer item : this.pooled) {
/* 208 */         item.close();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuffer[] getResource() throws IllegalStateException {
/* 214 */       return this.data;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {
/* 219 */       free();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\BufferedBinaryMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */