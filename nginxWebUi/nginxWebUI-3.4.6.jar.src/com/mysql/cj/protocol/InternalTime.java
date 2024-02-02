/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.util.TimeUtil;
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
/*     */ public class InternalTime
/*     */ {
/*     */   private boolean negative = false;
/*  37 */   private int hours = 0;
/*  38 */   private int minutes = 0;
/*  39 */   private int seconds = 0;
/*  40 */   private int nanos = 0;
/*  41 */   private int scale = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public InternalTime() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public InternalTime(int hours, int minutes, int seconds, int nanos, int scale) {
/*  50 */     this.hours = hours;
/*  51 */     this.minutes = minutes;
/*  52 */     this.seconds = seconds;
/*  53 */     this.nanos = nanos;
/*  54 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public boolean isNegative() {
/*  58 */     return this.negative;
/*     */   }
/*     */   
/*     */   public void setNegative(boolean negative) {
/*  62 */     this.negative = negative;
/*     */   }
/*     */   
/*     */   public int getHours() {
/*  66 */     return this.hours;
/*     */   }
/*     */   
/*     */   public void setHours(int hours) {
/*  70 */     this.hours = hours;
/*     */   }
/*     */   
/*     */   public int getMinutes() {
/*  74 */     return this.minutes;
/*     */   }
/*     */   
/*     */   public void setMinutes(int minutes) {
/*  78 */     this.minutes = minutes;
/*     */   }
/*     */   
/*     */   public int getSeconds() {
/*  82 */     return this.seconds;
/*     */   }
/*     */   
/*     */   public void setSeconds(int seconds) {
/*  86 */     this.seconds = seconds;
/*     */   }
/*     */   
/*     */   public int getNanos() {
/*  90 */     return this.nanos;
/*     */   }
/*     */   
/*     */   public void setNanos(int nanos) {
/*  94 */     this.nanos = nanos;
/*     */   }
/*     */   
/*     */   public boolean isZero() {
/*  98 */     return (this.hours == 0 && this.minutes == 0 && this.seconds == 0 && this.nanos == 0);
/*     */   }
/*     */   
/*     */   public int getScale() {
/* 102 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(int scale) {
/* 106 */     this.scale = scale;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 111 */     if (this.nanos > 0) {
/* 112 */       return String.format("%02d:%02d:%02d.%s", new Object[] { Integer.valueOf(this.hours), Integer.valueOf(this.minutes), Integer.valueOf(this.seconds), TimeUtil.formatNanos(this.nanos, this.scale, false) });
/*     */     }
/* 114 */     return String.format("%02d:%02d:%02d", new Object[] { Integer.valueOf(this.hours), Integer.valueOf(this.minutes), Integer.valueOf(this.seconds) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\InternalTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */