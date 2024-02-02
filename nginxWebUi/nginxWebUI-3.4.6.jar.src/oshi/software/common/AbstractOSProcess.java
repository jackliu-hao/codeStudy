/*    */ package oshi.software.common;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.software.os.OSProcess;
/*    */ import oshi.util.Memoizer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public abstract class AbstractOSProcess
/*    */   implements OSProcess
/*    */ {
/* 43 */   private final Supplier<Double> cumulativeCpuLoad = Memoizer.memoize(this::queryCumulativeCpuLoad, Memoizer.defaultExpiration());
/*    */   
/*    */   private int processID;
/*    */   
/*    */   protected AbstractOSProcess(int pid) {
/* 48 */     this.processID = pid;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getProcessID() {
/* 53 */     return this.processID;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getProcessCpuLoadCumulative() {
/* 58 */     return ((Double)this.cumulativeCpuLoad.get()).doubleValue();
/*    */   }
/*    */   
/*    */   private double queryCumulativeCpuLoad() {
/* 62 */     return (getUpTime() > 0.0D) ? ((getKernelTime() + getUserTime()) / getUpTime()) : 0.0D;
/*    */   }
/*    */ 
/*    */   
/*    */   public double getProcessCpuLoadBetweenTicks(OSProcess priorSnapshot) {
/* 67 */     if (priorSnapshot != null && this.processID == priorSnapshot.getProcessID() && 
/* 68 */       getUpTime() > priorSnapshot.getUpTime()) {
/* 69 */       return (getUserTime() - priorSnapshot.getUserTime() + getKernelTime() - priorSnapshot.getKernelTime()) / (
/* 70 */         getUpTime() - priorSnapshot.getUpTime());
/*    */     }
/* 72 */     return getProcessCpuLoadCumulative();
/*    */   }
/*    */ 
/*    */   
/*    */   public long getMinorFaults() {
/* 77 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getMajorFaults() {
/* 82 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 87 */     StringBuilder builder = new StringBuilder("OSProcess@");
/* 88 */     builder.append(Integer.toHexString(hashCode()));
/* 89 */     builder.append("[processID=").append(this.processID);
/* 90 */     builder.append(", name=").append(getName()).append(']');
/* 91 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\common\AbstractOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */