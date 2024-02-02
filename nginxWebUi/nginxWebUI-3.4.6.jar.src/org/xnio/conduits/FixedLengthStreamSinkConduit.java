/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import org.xnio._private.Messages;
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
/*     */ 
/*     */ 
/*     */ public final class FixedLengthStreamSinkConduit
/*     */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*     */   implements StreamSinkConduit
/*     */ {
/*     */   private long remaining;
/*     */   
/*     */   public FixedLengthStreamSinkConduit(FixedLengthStreamSinkConduit next) {
/*  45 */     super(next);
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  49 */     if (count == 0L) return 0L; 
/*  50 */     long remaining = this.remaining;
/*  51 */     if (remaining == 0L) {
/*  52 */       throw Messages.msg.fixedOverflow();
/*     */     }
/*  54 */     long res = 0L;
/*     */     try {
/*  56 */       return res = this.next.transferFrom(src, position, Math.min(count, remaining));
/*     */     } finally {
/*  58 */       this.remaining = remaining - res;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  63 */     if (count == 0L) return 0L; 
/*  64 */     long remaining = this.remaining;
/*  65 */     if (remaining == 0L) {
/*  66 */       throw Messages.msg.fixedOverflow();
/*     */     }
/*  68 */     long res = 0L;
/*     */     try {
/*  70 */       return res = this.next.transferFrom(source, Math.min(count, remaining), throughBuffer);
/*     */     } finally {
/*  72 */       this.remaining = remaining - res;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*  78 */     return write(src, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  83 */     return write(srcs, offset, length, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  88 */     return write(src, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  93 */     return write(srcs, offs, len, false);
/*     */   }
/*     */   
/*     */   private int write(ByteBuffer src, boolean writeFinal) throws IOException {
/*  97 */     if (!src.hasRemaining()) {
/*  98 */       return 0;
/*     */     }
/* 100 */     int res = 0;
/* 101 */     long remaining = this.remaining;
/* 102 */     if (remaining == 0L) {
/* 103 */       throw Messages.msg.fixedOverflow();
/*     */     }
/*     */     try {
/* 106 */       int lim = src.limit();
/* 107 */       int pos = src.position();
/* 108 */       if ((lim - pos) > remaining) {
/* 109 */         src.limit((int)(remaining - pos));
/*     */         try {
/* 111 */           return res = doWrite(src, writeFinal);
/*     */         } finally {
/* 113 */           src.limit(lim);
/*     */         } 
/*     */       } 
/* 116 */       return res = doWrite(src, writeFinal);
/*     */     } finally {
/*     */       
/* 119 */       this.remaining = remaining - res;
/*     */     } 
/*     */   }
/*     */   
/*     */   private long write(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
/* 124 */     if (len == 0)
/* 125 */       return 0L; 
/* 126 */     if (len == 1) {
/* 127 */       return write(srcs[offs], writeFinal);
/*     */     }
/* 129 */     long remaining = this.remaining;
/* 130 */     if (remaining == 0L) {
/* 131 */       throw Messages.msg.fixedOverflow();
/*     */     }
/* 133 */     long res = 0L;
/*     */ 
/*     */     
/*     */     try {
/* 137 */       long t = 0L;
/* 138 */       for (int i = 0; i < len; i++) {
/* 139 */         ByteBuffer buffer = srcs[i + offs];
/*     */         
/*     */         int lim;
/* 142 */         t += ((lim = buffer.limit()) - buffer.position());
/* 143 */         if (t > remaining) {
/*     */           
/* 145 */           buffer.limit(lim - (int)(t - remaining));
/*     */           try {
/* 147 */             return res = doWrite(srcs, offs, i + 1, writeFinal);
/*     */           } finally {
/*     */             
/* 150 */             buffer.limit(lim);
/*     */           } 
/*     */         } 
/*     */       } 
/* 154 */       if (t == 0L) {
/* 155 */         return 0L;
/*     */       }
/*     */       
/* 158 */       return res = doWrite(srcs, offs, len, writeFinal);
/*     */     } finally {
/* 160 */       this.remaining = remaining - res;
/*     */     } 
/*     */   }
/*     */   
/*     */   private long doWrite(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
/* 165 */     if (writeFinal) {
/* 166 */       return this.next.writeFinal(srcs, offs, len);
/*     */     }
/* 168 */     return this.next.write(srcs, offs, len);
/*     */   }
/*     */ 
/*     */   
/*     */   private int doWrite(ByteBuffer src, boolean writeFinal) throws IOException {
/* 173 */     if (writeFinal) {
/* 174 */       return this.next.writeFinal(src);
/*     */     }
/* 176 */     return this.next.write(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 181 */     this.next.terminateWrites();
/* 182 */     if (this.remaining > 0L) {
/* 183 */       throw Messages.msg.fixedOverflow();
/*     */     }
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 188 */     this.next.terminateWrites();
/* 189 */     if (this.remaining > 0L) {
/* 190 */       throw Messages.msg.fixedOverflow();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 200 */     return this.remaining;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\FixedLengthStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */