/*     */ package com.mysql.cj.protocol;
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
/*     */ public class InternalTimestamp
/*     */   extends InternalDate
/*     */ {
/*  34 */   private int hours = 0;
/*  35 */   private int minutes = 0;
/*  36 */   private int seconds = 0;
/*  37 */   private int nanos = 0;
/*  38 */   private int scale = 0;
/*  39 */   private int offset = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InternalTimestamp() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public InternalTimestamp(int year, int month, int day, int hours, int minutes, int seconds, int nanos, int scale) {
/*  49 */     this.year = year;
/*  50 */     this.month = month;
/*  51 */     this.day = day;
/*  52 */     this.hours = hours;
/*  53 */     this.minutes = minutes;
/*  54 */     this.seconds = seconds;
/*  55 */     this.nanos = nanos;
/*  56 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public int getHours() {
/*  60 */     return this.hours;
/*     */   }
/*     */   
/*     */   public void setHours(int hours) {
/*  64 */     this.hours = hours;
/*     */   }
/*     */   
/*     */   public int getMinutes() {
/*  68 */     return this.minutes;
/*     */   }
/*     */   
/*     */   public void setMinutes(int minutes) {
/*  72 */     this.minutes = minutes;
/*     */   }
/*     */   
/*     */   public int getSeconds() {
/*  76 */     return this.seconds;
/*     */   }
/*     */   
/*     */   public void setSeconds(int seconds) {
/*  80 */     this.seconds = seconds;
/*     */   }
/*     */   
/*     */   public int getNanos() {
/*  84 */     return this.nanos;
/*     */   }
/*     */   
/*     */   public void setNanos(int nanos) {
/*  88 */     this.nanos = nanos;
/*     */   }
/*     */   
/*     */   public int getScale() {
/*  92 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(int scale) {
/*  96 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public int getOffset() {
/* 100 */     return this.offset;
/*     */   }
/*     */   
/*     */   public void setOffset(int offset) {
/* 104 */     this.offset = offset;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isZero() {
/* 109 */     return (super.isZero() && this.hours == 0 && this.minutes == 0 && this.seconds == 0 && this.nanos == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\InternalTimestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */