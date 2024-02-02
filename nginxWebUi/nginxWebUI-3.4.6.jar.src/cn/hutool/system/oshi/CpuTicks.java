/*     */ package cn.hutool.system.oshi;
/*     */ 
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.util.Util;
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
/*     */ public class CpuTicks
/*     */ {
/*     */   long idle;
/*     */   long nice;
/*     */   long irq;
/*     */   long softIrq;
/*     */   long steal;
/*     */   long cSys;
/*     */   long user;
/*     */   long ioWait;
/*     */   
/*     */   public CpuTicks(CentralProcessor processor, long waitingTime) {
/*  31 */     long[] prevTicks = processor.getSystemCpuLoadTicks();
/*     */     
/*  33 */     Util.sleep(waitingTime);
/*  34 */     long[] ticks = processor.getSystemCpuLoadTicks();
/*     */     
/*  36 */     this.idle = tick(prevTicks, ticks, CentralProcessor.TickType.IDLE);
/*  37 */     this.nice = tick(prevTicks, ticks, CentralProcessor.TickType.NICE);
/*  38 */     this.irq = tick(prevTicks, ticks, CentralProcessor.TickType.IRQ);
/*  39 */     this.softIrq = tick(prevTicks, ticks, CentralProcessor.TickType.SOFTIRQ);
/*  40 */     this.steal = tick(prevTicks, ticks, CentralProcessor.TickType.STEAL);
/*  41 */     this.cSys = tick(prevTicks, ticks, CentralProcessor.TickType.SYSTEM);
/*  42 */     this.user = tick(prevTicks, ticks, CentralProcessor.TickType.USER);
/*  43 */     this.ioWait = tick(prevTicks, ticks, CentralProcessor.TickType.IOWAIT);
/*     */   }
/*     */   
/*     */   public long getIdle() {
/*  47 */     return this.idle;
/*     */   }
/*     */   
/*     */   public void setIdle(long idle) {
/*  51 */     this.idle = idle;
/*     */   }
/*     */   
/*     */   public long getNice() {
/*  55 */     return this.nice;
/*     */   }
/*     */   
/*     */   public void setNice(long nice) {
/*  59 */     this.nice = nice;
/*     */   }
/*     */   
/*     */   public long getIrq() {
/*  63 */     return this.irq;
/*     */   }
/*     */   
/*     */   public void setIrq(long irq) {
/*  67 */     this.irq = irq;
/*     */   }
/*     */   
/*     */   public long getSoftIrq() {
/*  71 */     return this.softIrq;
/*     */   }
/*     */   
/*     */   public void setSoftIrq(long softIrq) {
/*  75 */     this.softIrq = softIrq;
/*     */   }
/*     */   
/*     */   public long getSteal() {
/*  79 */     return this.steal;
/*     */   }
/*     */   
/*     */   public void setSteal(long steal) {
/*  83 */     this.steal = steal;
/*     */   }
/*     */   
/*     */   public long getcSys() {
/*  87 */     return this.cSys;
/*     */   }
/*     */   
/*     */   public void setcSys(long cSys) {
/*  91 */     this.cSys = cSys;
/*     */   }
/*     */   
/*     */   public long getUser() {
/*  95 */     return this.user;
/*     */   }
/*     */   
/*     */   public void setUser(long user) {
/*  99 */     this.user = user;
/*     */   }
/*     */   
/*     */   public long getIoWait() {
/* 103 */     return this.ioWait;
/*     */   }
/*     */   
/*     */   public void setIoWait(long ioWait) {
/* 107 */     this.ioWait = ioWait;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long totalCpu() {
/* 116 */     return Math.max(this.user + this.nice + this.cSys + this.idle + this.ioWait + this.irq + this.softIrq + this.steal, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     return "CpuTicks{idle=" + this.idle + ", nice=" + this.nice + ", irq=" + this.irq + ", softIrq=" + this.softIrq + ", steal=" + this.steal + ", cSys=" + this.cSys + ", user=" + this.user + ", ioWait=" + this.ioWait + '}';
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long tick(long[] prevTicks, long[] ticks, CentralProcessor.TickType tickType) {
/* 143 */     return ticks[tickType.getIndex()] - prevTicks[tickType.getIndex()];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\oshi\CpuTicks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */