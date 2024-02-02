/*    */ package com.cym.sqlhelper.utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SnowFlake
/*    */ {
/*    */   private static final long START_STAMP = 1480166465631L;
/*    */   private static final long SEQUENCE_BIT = 12L;
/*    */   private static final long MACHINE_BIT = 5L;
/*    */   private static final long DATA_CENTER_BIT = 5L;
/*    */   private static final long MAX_DATA_CENTER_NUM = 31L;
/*    */   private static final long MAX_MACHINE_NUM = 31L;
/*    */   private static final long MAX_SEQUENCE = 4095L;
/*    */   private static final long MACHINE_LEFT = 12L;
/*    */   private static final long DATA_CENTER_LEFT = 17L;
/*    */   private static final long TIMESTAMP_LEFT = 22L;
/*    */   private long dataCenterId;
/*    */   private long machineId;
/* 23 */   private long sequence = 0L;
/* 24 */   private long lastStamp = -1L;
/*    */   
/*    */   public SnowFlake(long dataCenterId, long machineId) {
/* 27 */     if (dataCenterId > 31L || dataCenterId < 0L) {
/* 28 */       throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
/*    */     }
/* 30 */     if (machineId > 31L || machineId < 0L) {
/* 31 */       throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
/*    */     }
/* 33 */     this.dataCenterId = dataCenterId;
/* 34 */     this.machineId = machineId;
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized String nextId() {
/* 39 */     long currStamp = getNewStamp();
/* 40 */     if (currStamp < this.lastStamp) {
/* 41 */       throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
/*    */     }
/*    */     
/* 44 */     if (currStamp == this.lastStamp) {
/*    */       
/* 46 */       this.sequence = this.sequence + 1L & 0xFFFL;
/*    */       
/* 48 */       if (this.sequence == 0L) {
/* 49 */         currStamp = getNextMill();
/*    */       }
/*    */     } else {
/*    */       
/* 53 */       this.sequence = 0L;
/*    */     } 
/*    */     
/* 56 */     this.lastStamp = currStamp;
/*    */     
/* 58 */     long id = currStamp - 1480166465631L << 22L | this.dataCenterId << 17L | this.machineId << 12L | this.sequence;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 63 */     return String.valueOf(id);
/*    */   }
/*    */   
/*    */   private long getNextMill() {
/* 67 */     long mill = getNewStamp();
/* 68 */     while (mill <= this.lastStamp) {
/* 69 */       mill = getNewStamp();
/*    */     }
/* 71 */     return mill;
/*    */   }
/*    */   
/*    */   private long getNewStamp() {
/* 75 */     return System.currentTimeMillis();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\SnowFlake.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */