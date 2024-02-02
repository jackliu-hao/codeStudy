/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BufferedStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */ {
/*     */   private final Pooled<ByteBuffer> pooledBuffer;
/*     */   
/*     */   public BufferedStreamSourceConduit(StreamSourceConduit next, Pooled<ByteBuffer> pooledBuffer) {
/*  44 */     super(next);
/*  45 */     this.pooledBuffer = pooledBuffer;
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*     */     try {
/*  50 */       ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
/*  51 */       int lim = buffer.limit();
/*  52 */       int pos = buffer.position();
/*  53 */       int rem = lim - pos;
/*  54 */       if (rem > 0) {
/*  55 */         if (rem > count) {
/*  56 */           buffer.limit(pos + (int)count);
/*     */           try {
/*  58 */             return target.write(buffer, position);
/*     */           } finally {
/*  60 */             buffer.limit(lim);
/*     */           } 
/*     */         } 
/*  63 */         return target.write(buffer, position);
/*     */       } 
/*     */       
/*  66 */       return super.transferTo(position, count, target);
/*     */     }
/*  68 */     catch (IllegalStateException ignored) {
/*  69 */       return 0L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*     */     try {
/*  75 */       ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
/*     */ 
/*     */ 
/*     */       
/*  79 */       int lim = buffer.limit();
/*  80 */       int pos = buffer.position();
/*  81 */       int rem = lim - pos;
/*  82 */       throughBuffer.clear();
/*     */       
/*  84 */       long t = 0L;
/*  85 */       while (rem > 0) {
/*  86 */         int res; if (rem > count) {
/*  87 */           buffer.limit(pos + (int)count);
/*     */           try {
/*  89 */             t += (res = target.write(buffer));
/*     */           } finally {
/*  91 */             buffer.limit(lim);
/*     */           } 
/*     */         } else {
/*  94 */           t += (res = target.write(buffer));
/*     */         } 
/*  96 */         if (res == 0) {
/*  97 */           return t;
/*     */         }
/*  99 */         pos = buffer.position();
/* 100 */         rem = lim - pos;
/*     */       } 
/* 102 */       long lres = Conduits.transfer(this.next, count, throughBuffer, (WritableByteChannel)target);
/* 103 */       if (lres > 0L) t += lres; 
/* 104 */       return (t == 0L && lres == -1L) ? -1L : t;
/* 105 */     } catch (IllegalStateException ignored) {
/* 106 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*     */     try {
/* 112 */       ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
/* 113 */       int lim = buffer.limit();
/* 114 */       int pos = buffer.position();
/* 115 */       int rem = lim - pos;
/* 116 */       if (rem > 0) {
/* 117 */         return Buffers.copy(dst, buffer);
/*     */       }
/* 119 */       int dstRem = dst.remaining();
/* 120 */       buffer.clear();
/*     */       try {
/* 122 */         long rres = this.next.read(new ByteBuffer[] { dst, buffer }, 0, 2);
/* 123 */         if (rres == -1L) {
/* 124 */           return -1;
/*     */         }
/*     */       } finally {
/* 127 */         buffer.flip();
/*     */       } 
/* 129 */       return dst.remaining() - dstRem;
/*     */     }
/* 131 */     catch (IllegalStateException ignored) {
/* 132 */       return -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 137 */     if (len == 0)
/* 138 */       return 0L; 
/* 139 */     if (len == 1)
/* 140 */       return read(dsts[offs]); 
/*     */     try {
/* 142 */       ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
/* 143 */       int lim = buffer.limit();
/* 144 */       int pos = buffer.position();
/* 145 */       int rem = lim - pos;
/* 146 */       if (rem > 0) {
/* 147 */         return Buffers.copy(dsts, offs, len, buffer);
/*     */       }
/* 149 */       long dstRem = Buffers.remaining((Buffer[])dsts, offs, len);
/* 150 */       buffer.clear();
/*     */       try {
/* 152 */         ByteBuffer[] buffers = Arrays.<ByteBuffer>copyOfRange(dsts, offs, offs + len + 1);
/* 153 */         buffers[buffers.length - 1] = buffer;
/* 154 */         long rres = this.next.read(buffers, 0, buffers.length);
/* 155 */         if (rres == -1L) {
/* 156 */           return -1L;
/*     */         }
/*     */       } finally {
/* 159 */         buffer.flip();
/*     */       } 
/* 161 */       return Buffers.remaining((Buffer[])dsts, offs, len) - dstRem;
/*     */     }
/* 163 */     catch (IllegalStateException ignored) {
/* 164 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void terminateReads() throws IOException {
/* 169 */     this.pooledBuffer.free();
/* 170 */     super.terminateReads();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\BufferedStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */