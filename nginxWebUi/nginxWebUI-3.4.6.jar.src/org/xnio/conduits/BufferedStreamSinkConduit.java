/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.Arrays;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public final class BufferedStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */ {
/*     */   private final Pooled<ByteBuffer> pooledBuffer;
/*     */   private boolean terminate;
/*     */   
/*     */   public BufferedStreamSinkConduit(StreamSinkConduit next, Pooled<ByteBuffer> pooledBuffer) {
/*  47 */     super(next);
/*  48 */     this.pooledBuffer = pooledBuffer;
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  52 */     return flushLocal() ? super.transferFrom(src, position, count) : 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  57 */     if (flushLocal()) {
/*  58 */       return super.transferFrom(source, count, throughBuffer);
/*     */     }
/*  60 */     throughBuffer.limit(0);
/*  61 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*     */     try {
/*  67 */       ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
/*  68 */       int pos = buffer.position();
/*  69 */       int lim = buffer.limit();
/*  70 */       int srcRem = src.remaining();
/*  71 */       int ourRem = lim - pos;
/*  72 */       if (srcRem < ourRem) {
/*  73 */         buffer.put(src);
/*  74 */         return srcRem;
/*  75 */       }  if (buffer.position() == 0) {
/*  76 */         int res = super.write(src);
/*  77 */         if (srcRem > res) {
/*  78 */           int cnt = Buffers.copy(buffer, src);
/*  79 */           return res + cnt;
/*     */         } 
/*  81 */         return res;
/*     */       } 
/*     */       
/*  84 */       buffer.flip();
/*     */       try {
/*  86 */         super.write(new ByteBuffer[] { buffer, src }, 0, 2);
/*     */       } finally {
/*  88 */         buffer.compact();
/*     */       } 
/*  90 */       if (src.hasRemaining()) {
/*  91 */         Buffers.copy(buffer, src);
/*     */       }
/*  93 */       return srcRem - src.remaining();
/*     */     }
/*  95 */     catch (IllegalStateException ignored) {
/*  96 */       throw new ClosedChannelException();
/*     */     } 
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 101 */     if (len == 0)
/* 102 */       return 0L; 
/* 103 */     if (len == 1)
/* 104 */       return write(srcs[offs]); 
/*     */     try {
/* 106 */       ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
/* 107 */       int pos = buffer.position();
/* 108 */       int lim = buffer.limit();
/* 109 */       long srcRem = Buffers.remaining((Buffer[])srcs, offs, len);
/* 110 */       int ourRem = lim - pos;
/* 111 */       if (srcRem < ourRem) {
/* 112 */         for (int i = 0; i < len; i++) {
/* 113 */           buffer.put(srcs[i]);
/*     */         }
/* 115 */         return srcRem;
/* 116 */       }  if (buffer.position() == 0) {
/* 117 */         long res = super.write(srcs, offs, len);
/* 118 */         if (srcRem > res) {
/* 119 */           int cnt = Buffers.copy(buffer, srcs, offs, len);
/* 120 */           return res + cnt;
/*     */         } 
/* 122 */         return res;
/*     */       } 
/*     */       
/* 125 */       buffer.flip();
/*     */       try {
/*     */         ByteBuffer[] buffers;
/* 128 */         if (offs > 0) {
/* 129 */           buffers = Arrays.<ByteBuffer>copyOfRange(srcs, offs - 1, offs + len);
/*     */         } else {
/* 131 */           buffers = new ByteBuffer[len + 1];
/* 132 */           System.arraycopy(srcs, offs, buffers, 1, len);
/*     */         } 
/* 134 */         buffers[0] = buffer;
/* 135 */         super.write(buffers, 0, buffers.length);
/*     */       } finally {
/* 137 */         buffer.compact();
/*     */       } 
/* 139 */       Buffers.copy(buffer, srcs, offs, len);
/* 140 */       return srcRem - Buffers.remaining((Buffer[])srcs, offs, len);
/*     */     }
/* 142 */     catch (IllegalStateException ignored) {
/* 143 */       throw new ClosedChannelException();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean flushLocal() throws IOException {
/*     */     try {
/* 149 */       ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
/* 150 */       if (buffer.position() > 0) {
/* 151 */         buffer.flip();
/*     */         try {
/*     */           while (true) {
/* 154 */             super.write(buffer);
/* 155 */             if (!buffer.hasRemaining()) {
/* 156 */               if (this.terminate) {
/* 157 */                 this.pooledBuffer.free();
/*     */               }
/* 159 */               return true;
/*     */             } 
/*     */           } 
/*     */         } finally {
/* 163 */           buffer.compact();
/*     */         } 
/*     */       } 
/* 166 */       return true;
/*     */     }
/* 168 */     catch (IllegalStateException ignored) {
/* 169 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 176 */     return Conduits.writeFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 182 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 186 */     return (flushLocal() && super.flush());
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 190 */     this.pooledBuffer.free();
/* 191 */     super.truncateWrites();
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 195 */     this.terminate = true;
/* 196 */     super.terminateWrites();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\BufferedStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */