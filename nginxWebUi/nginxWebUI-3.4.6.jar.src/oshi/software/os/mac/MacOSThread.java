/*    */ package oshi.software.os.mac;
/*    */ 
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.software.common.AbstractOSThread;
/*    */ import oshi.software.os.OSProcess;
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
/*    */ @Immutable
/*    */ public class MacOSThread
/*    */   extends AbstractOSThread
/*    */ {
/*    */   private final int threadId;
/*    */   private final OSProcess.State state;
/*    */   private final long kernelTime;
/*    */   private final long userTime;
/*    */   private final long startTime;
/*    */   private final long upTime;
/*    */   private final int priority;
/*    */   
/*    */   public MacOSThread(int pid, int threadId, OSProcess.State state, long kernelTime, long userTime, long startTime, long upTime, int priority) {
/* 43 */     super(pid);
/* 44 */     this.threadId = threadId;
/* 45 */     this.state = state;
/* 46 */     this.kernelTime = kernelTime;
/* 47 */     this.userTime = userTime;
/* 48 */     this.startTime = startTime;
/* 49 */     this.upTime = upTime;
/* 50 */     this.priority = priority;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getThreadId() {
/* 55 */     return this.threadId;
/*    */   }
/*    */ 
/*    */   
/*    */   public OSProcess.State getState() {
/* 60 */     return this.state;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getKernelTime() {
/* 65 */     return this.kernelTime;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getUserTime() {
/* 70 */     return this.userTime;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getStartTime() {
/* 75 */     return this.startTime;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getUpTime() {
/* 80 */     return this.upTime;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getPriority() {
/* 85 */     return this.priority;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\mac\MacOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */