/*     */ package oshi.software.common;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import oshi.software.os.OSThread;
/*     */ import oshi.util.Memoizer;
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
/*     */ public abstract class AbstractOSThread
/*     */   implements OSThread
/*     */ {
/*  35 */   private final Supplier<Double> cumulativeCpuLoad = Memoizer.memoize(this::queryCumulativeCpuLoad, Memoizer.defaultExpiration());
/*     */   
/*     */   private final int owningProcessId;
/*     */   
/*     */   protected AbstractOSThread(int processId) {
/*  40 */     this.owningProcessId = processId;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOwningProcessId() {
/*  45 */     return this.owningProcessId;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getThreadCpuLoadCumulative() {
/*  50 */     return ((Double)this.cumulativeCpuLoad.get()).doubleValue();
/*     */   }
/*     */   
/*     */   private double queryCumulativeCpuLoad() {
/*  54 */     return (getUpTime() > 0.0D) ? ((getKernelTime() + getUserTime()) / getUpTime()) : 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getThreadCpuLoadBetweenTicks(OSThread priorSnapshot) {
/*  59 */     if (priorSnapshot != null && this.owningProcessId == priorSnapshot.getOwningProcessId() && 
/*  60 */       getThreadId() == priorSnapshot.getThreadId() && getUpTime() > priorSnapshot.getUpTime()) {
/*  61 */       return (getUserTime() - priorSnapshot.getUserTime() + getKernelTime() - priorSnapshot.getKernelTime()) / (
/*  62 */         getUpTime() - priorSnapshot.getUpTime());
/*     */     }
/*  64 */     return getThreadCpuLoadCumulative();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  74 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartMemoryAddress() {
/*  79 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContextSwitches() {
/*  84 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMinorFaults() {
/*  89 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMajorFaults() {
/*  94 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/*  99 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     return "OSThread [threadId=" + getThreadId() + ", owningProcessId=" + getOwningProcessId() + ", name=" + 
/* 105 */       getName() + ", state=" + getState() + ", kernelTime=" + getKernelTime() + ", userTime=" + 
/* 106 */       getUserTime() + ", upTime=" + getUpTime() + ", startTime=" + getStartTime() + ", startMemoryAddress=0x" + 
/* 107 */       String.format("%x", new Object[] { Long.valueOf(getStartMemoryAddress()) }) + ", contextSwitches=" + 
/* 108 */       getContextSwitches() + ", minorFaults=" + getMinorFaults() + ", majorFaults=" + getMajorFaults() + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\common\AbstractOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */