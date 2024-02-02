/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
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
/*     */ 
/*     */ public final class FixedLengthStreamSourceConduit
/*     */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*     */   implements StreamSourceConduit
/*     */ {
/*     */   private long remaining;
/*     */   
/*     */   public FixedLengthStreamSourceConduit(StreamSourceConduit next, long remaining) {
/*  41 */     super(next);
/*  42 */     this.remaining = remaining;
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  46 */     long length = this.remaining;
/*  47 */     if (length > 0L) {
/*  48 */       long res = this.next.transferTo(position, Math.min(count, length), target);
/*  49 */       if (res > 0L) {
/*  50 */         this.remaining = length - res;
/*     */       }
/*  52 */       return res;
/*     */     } 
/*  54 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  59 */     long length = this.remaining;
/*  60 */     if (length > 0L) {
/*  61 */       long res = this.next.transferTo(Math.min(count, length), throughBuffer, target);
/*  62 */       if (res > 0L) {
/*  63 */         this.remaining = length - res;
/*     */       }
/*  65 */       return res;
/*     */     } 
/*  67 */     return -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/*  72 */     int res, limit = dst.limit();
/*  73 */     int pos = dst.position();
/*     */     
/*  75 */     long length = this.remaining;
/*  76 */     if (length == 0L) {
/*  77 */       return -1;
/*     */     }
/*  79 */     if ((limit - pos) > length) {
/*  80 */       dst.limit(pos + (int)length);
/*     */       try {
/*  82 */         res = this.next.read(dst);
/*     */       } finally {
/*  84 */         dst.limit(limit);
/*     */       } 
/*     */     } else {
/*  87 */       res = this.next.read(dst);
/*     */     } 
/*  89 */     if (res > 0L) {
/*  90 */       this.remaining = length - res;
/*     */     }
/*  92 */     return res;
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/*  96 */     if (len == 0)
/*  97 */       return 0L; 
/*  98 */     if (len == 1) {
/*  99 */       return read(dsts[offs]);
/*     */     }
/* 101 */     long length = this.remaining;
/* 102 */     if (length == 0L) {
/* 103 */       return -1L;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 108 */     long t = 0L;
/* 109 */     for (int i = 0; i < length; i++) {
/* 110 */       ByteBuffer buffer = dsts[i + offs];
/*     */       
/*     */       int lim;
/* 113 */       t += ((lim = buffer.limit()) - buffer.position());
/* 114 */       if (t > length) {
/*     */         
/* 116 */         buffer.limit(lim - (int)(t - length));
/*     */         try {
/* 118 */           long l = this.next.read(dsts, offs, i + 1);
/* 119 */           if (l > 0L) {
/* 120 */             this.remaining = length - l;
/*     */           }
/* 122 */           return l;
/*     */         } finally {
/*     */           
/* 125 */           buffer.limit(lim);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 130 */     long res = (t == 0L) ? 0L : this.next.read(dsts, offs, len);
/* 131 */     if (res > 0L) {
/* 132 */       this.remaining = length - res;
/*     */     }
/* 134 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 143 */     return this.remaining;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\FixedLengthStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */