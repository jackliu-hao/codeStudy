/*     */ package oshi.software.os.unix.aix;
/*     */ 
/*     */ import java.util.List;
/*     */ import oshi.software.common.AbstractOSThread;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public class AixOSThread
/*     */   extends AbstractOSThread
/*     */ {
/*     */   private int threadId;
/*  36 */   private OSProcess.State state = OSProcess.State.INVALID;
/*     */   private long contextSwitches;
/*     */   private long kernelTime;
/*     */   private long userTime;
/*     */   private long startTime;
/*     */   private long upTime;
/*     */   private int priority;
/*     */   
/*     */   public AixOSThread(int pid, String[] split) {
/*  45 */     super(pid);
/*  46 */     updateAttributes(split);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadId() {
/*  51 */     return this.threadId;
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess.State getState() {
/*  56 */     return this.state;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContextSwitches() {
/*  61 */     return this.contextSwitches;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getKernelTime() {
/*  66 */     return this.kernelTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUserTime() {
/*  71 */     return this.userTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getUpTime() {
/*  76 */     return this.upTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getStartTime() {
/*  81 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPriority() {
/*  86 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/*  91 */     List<String> threadListInfoPs = ExecutingCommand.runNative("ps -m -o THREAD -p " + getOwningProcessId());
/*     */     
/*  93 */     if (threadListInfoPs.size() > 2) {
/*  94 */       threadListInfoPs.remove(0);
/*  95 */       threadListInfoPs.remove(0);
/*  96 */       for (String threadInfo : threadListInfoPs) {
/*     */         
/*  98 */         String[] threadInfoSplit = ParseUtil.whitespaces.split(threadInfo.trim());
/*  99 */         if (threadInfoSplit.length == 13 && threadInfoSplit[3].equals(String.valueOf(getThreadId()))) {
/* 100 */           String[] split = new String[3];
/* 101 */           split[0] = threadInfoSplit[3];
/* 102 */           split[1] = threadInfoSplit[4];
/* 103 */           split[2] = threadInfoSplit[6];
/* 104 */           updateAttributes(split);
/*     */         } 
/*     */       } 
/*     */     } 
/* 108 */     this.state = OSProcess.State.INVALID;
/* 109 */     return false;
/*     */   }
/*     */   
/*     */   private boolean updateAttributes(String[] split) {
/* 113 */     this.threadId = ParseUtil.parseIntOrDefault(split[0], 0);
/* 114 */     this.state = AixOSProcess.getStateFromOutput(split[1].charAt(0));
/* 115 */     this.priority = ParseUtil.parseIntOrDefault(split[2], 0);
/* 116 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\aix\AixOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */