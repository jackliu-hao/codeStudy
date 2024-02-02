/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*     */ public final class PushBackStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */   implements StreamSourceConduit
/*     */ {
/*  35 */   private StreamSourceConduit current = this.next;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shutdown;
/*     */ 
/*     */ 
/*     */   
/*     */   public PushBackStreamSourceConduit(StreamSourceConduit next) {
/*  44 */     super(next);
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/*  48 */     this.current.resumeReads();
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  52 */     return this.current.read(dst);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*  56 */     return this.current.read(dsts, offs, len);
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  60 */     return this.current.transferTo(position, count, target);
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  64 */     return this.current.transferTo(count, throughBuffer, target);
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/*  68 */     this.current.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/*  72 */     this.current.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   public void terminateReads() throws IOException {
/*  76 */     this.shutdown = true;
/*  77 */     this.current.terminateReads();
/*     */   }
/*     */   
/*     */   public void setReadReadyHandler(ReadReadyHandler handler) {
/*  81 */     this.current.setReadReadyHandler(handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pushBack(Pooled<ByteBuffer> pooledBuffer) {
/*  91 */     if (pooledBuffer == null) {
/*     */       return;
/*     */     }
/*  94 */     if (this.shutdown || !((ByteBuffer)pooledBuffer.getResource()).hasRemaining()) {
/*  95 */       pooledBuffer.free();
/*     */     } else {
/*  97 */       this.current = new BufferConduit(this.current, pooledBuffer);
/*     */     } 
/*     */   }
/*     */   
/*     */   class BufferConduit
/*     */     extends AbstractStreamSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
/*     */     private final Pooled<ByteBuffer> pooledBuffer;
/*     */     
/*     */     BufferConduit(StreamSourceConduit next, Pooled<ByteBuffer> pooledBuffer) {
/* 106 */       super(next);
/* 107 */       this.pooledBuffer = pooledBuffer;
/*     */     }
/*     */     
/*     */     public void resumeReads() {
/* 111 */       this.next.wakeupReads();
/*     */     }
/*     */     
/*     */     public void terminateReads() throws IOException {
/*     */       try {
/* 116 */         super.terminateReads();
/*     */       } finally {
/* 118 */         if (this.pooledBuffer != null) {
/* 119 */           this.pooledBuffer.free();
/*     */         }
/* 121 */         this.next.terminateReads();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {}
/*     */ 
/*     */     
/*     */     public void awaitReadable() throws IOException {}
/*     */ 
/*     */     
/*     */     public int read(ByteBuffer dst) throws IOException {
/*     */       int cnt;
/* 135 */       if (!dst.hasRemaining()) {
/* 136 */         return 0;
/*     */       }
/* 138 */       StreamSourceConduit next = this.next;
/*     */       try {
/* 140 */         ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
/* 141 */         cnt = Buffers.copy(dst, src);
/* 142 */         if (src.hasRemaining()) {
/* 143 */           return cnt;
/*     */         }
/* 145 */         moveToNext();
/* 146 */         if (cnt > 0 && next == PushBackStreamSourceConduit.this.next)
/*     */         {
/* 148 */           return cnt;
/*     */         }
/* 150 */       } catch (IllegalStateException ignored) {
/* 151 */         moveToNext();
/* 152 */         cnt = 0;
/*     */       } 
/* 154 */       int res = next.read(dst);
/* 155 */       return (res > 0) ? (res + cnt) : ((cnt > 0) ? cnt : res);
/*     */     }
/*     */     
/*     */     public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*     */       long cnt;
/* 160 */       StreamSourceConduit next = this.next;
/*     */       try {
/* 162 */         ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
/* 163 */         cnt = Buffers.copy(dsts, offs, len, src);
/* 164 */         if (src.hasRemaining()) {
/* 165 */           return cnt;
/*     */         }
/* 167 */         moveToNext();
/* 168 */         if (cnt > 0L && next == PushBackStreamSourceConduit.this.next)
/*     */         {
/* 170 */           return cnt;
/*     */         }
/* 172 */       } catch (IllegalStateException ignored) {
/* 173 */         moveToNext();
/* 174 */         cnt = 0L;
/*     */       } 
/* 176 */       long res = next.read(dsts, offs, len);
/* 177 */       return (res > 0L) ? (res + cnt) : ((cnt > 0L) ? cnt : res);
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferTo(long position, long count, FileChannel target) throws IOException {
/*     */       long cnt;
/*     */       try {
/* 184 */         ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
/* 185 */         int pos = src.position();
/* 186 */         int rem = src.remaining();
/* 187 */         if (rem > count) {
/*     */           try {
/* 189 */             src.limit(pos + (int)count);
/* 190 */             return target.write(src, position);
/*     */           } finally {
/* 192 */             src.limit(pos + rem);
/*     */           } 
/*     */         }
/* 195 */         cnt = target.write(src, position);
/* 196 */         if (cnt == rem) {
/*     */           
/* 198 */           moveToNext();
/*     */         } else {
/* 200 */           return cnt;
/*     */         } 
/* 202 */         position += cnt;
/* 203 */         count -= cnt;
/*     */       }
/* 205 */       catch (IllegalStateException ignored) {
/* 206 */         moveToNext();
/* 207 */         cnt = 0L;
/*     */       } 
/* 209 */       return cnt + this.next.transferTo(position, count, target);
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*     */       long cnt;
/*     */       try {
/* 216 */         ByteBuffer src = (ByteBuffer)this.pooledBuffer.getResource();
/* 217 */         int pos = src.position();
/* 218 */         int rem = src.remaining();
/* 219 */         if (rem > count) {
/*     */           try {
/* 221 */             src.limit(pos + (int)count);
/* 222 */             int i = target.write(src);
/* 223 */             if (i == 0) {
/*     */ 
/*     */               
/* 226 */               throughBuffer.clear();
/* 227 */               Buffers.copy(throughBuffer, src);
/* 228 */               throughBuffer.flip();
/*     */             } else {
/*     */               
/* 231 */               throughBuffer.clear();
/* 232 */               throughBuffer.flip();
/*     */             } 
/* 234 */             return i;
/*     */           } finally {
/* 236 */             src.limit(pos + rem);
/*     */           } 
/*     */         }
/* 239 */         cnt = target.write(src);
/* 240 */         if (cnt == rem) {
/*     */           
/* 242 */           moveToNext();
/*     */         } else {
/* 244 */           if (cnt == 0L) {
/*     */ 
/*     */             
/* 247 */             throughBuffer.clear();
/* 248 */             Buffers.copy(throughBuffer, src);
/* 249 */             throughBuffer.flip();
/*     */           } else {
/*     */             
/* 252 */             throughBuffer.clear();
/* 253 */             throughBuffer.flip();
/*     */           } 
/* 255 */           return cnt;
/*     */         }
/*     */       
/* 258 */       } catch (IllegalStateException ignored) {
/* 259 */         moveToNext();
/* 260 */         cnt = 0L;
/*     */       } 
/* 262 */       long res = this.next.transferTo(count - cnt, throughBuffer, target);
/* 263 */       return (res > 0L) ? (cnt + res) : ((cnt > 0L) ? cnt : res);
/*     */     }
/*     */     
/*     */     private final void moveToNext() {
/* 267 */       PushBackStreamSourceConduit.this.current = this.next;
/* 268 */       this.pooledBuffer.free();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\PushBackStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */