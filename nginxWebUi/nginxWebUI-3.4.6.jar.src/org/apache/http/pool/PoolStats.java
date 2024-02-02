/*     */ package org.apache.http.pool;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class PoolStats
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2807686144795228544L;
/*     */   private final int leased;
/*     */   private final int pending;
/*     */   private final int available;
/*     */   private final int max;
/*     */   
/*     */   public PoolStats(int leased, int pending, int free, int max) {
/*  54 */     this.leased = leased;
/*  55 */     this.pending = pending;
/*  56 */     this.available = free;
/*  57 */     this.max = max;
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
/*     */   public int getLeased() {
/*  70 */     return this.leased;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPending() {
/*  80 */     return this.pending;
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
/*     */   public int getAvailable() {
/*  92 */     return this.available;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMax() {
/* 101 */     return this.max;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 106 */     StringBuilder buffer = new StringBuilder();
/* 107 */     buffer.append("[leased: ");
/* 108 */     buffer.append(this.leased);
/* 109 */     buffer.append("; pending: ");
/* 110 */     buffer.append(this.pending);
/* 111 */     buffer.append("; available: ");
/* 112 */     buffer.append(this.available);
/* 113 */     buffer.append("; max: ");
/* 114 */     buffer.append(this.max);
/* 115 */     buffer.append("]");
/* 116 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\pool\PoolStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */