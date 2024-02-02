/*     */ package oshi.driver.mac;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ @ThreadSafe
/*     */ public final class ThreadInfo
/*     */ {
/*  51 */   private static final Pattern PS_M = Pattern.compile("\\D+(\\d+).+(\\d+\\.\\d)\\s+(\\w)\\s+(\\d+)\\D+(\\d+:\\d{2}\\.\\d{2})\\s+(\\d+:\\d{2}\\.\\d{2}).+");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<ThreadStats> queryTaskThreads(int pid) {
/*  58 */     String pidStr = " " + pid + " ";
/*  59 */     List<ThreadStats> taskThreads = new ArrayList<>();
/*     */ 
/*     */ 
/*     */     
/*  63 */     List<String> psThread = (List<String>)ExecutingCommand.runNative("ps -awwxM").stream().filter(s -> s.contains(pidStr)).collect(Collectors.toList());
/*  64 */     int tid = 0;
/*  65 */     for (String thread : psThread) {
/*  66 */       Matcher m = PS_M.matcher(thread);
/*  67 */       if (m.matches() && pid == ParseUtil.parseIntOrDefault(m.group(1), -1)) {
/*  68 */         double cpu = ParseUtil.parseDoubleOrDefault(m.group(2), 0.0D);
/*  69 */         char state = m.group(3).charAt(0);
/*  70 */         int pri = ParseUtil.parseIntOrDefault(m.group(4), 0);
/*  71 */         long sTime = ParseUtil.parseDHMSOrDefault(m.group(5), 0L);
/*  72 */         long uTime = ParseUtil.parseDHMSOrDefault(m.group(6), 0L);
/*  73 */         taskThreads.add(new ThreadStats(tid++, cpu, state, sTime, uTime, pri));
/*     */       } 
/*     */     } 
/*  76 */     return taskThreads;
/*     */   }
/*     */ 
/*     */   
/*     */   @Immutable
/*     */   public static class ThreadStats
/*     */   {
/*     */     private final int threadId;
/*     */     
/*     */     private final long userTime;
/*     */     private final long systemTime;
/*     */     private final long upTime;
/*     */     private final OSProcess.State state;
/*     */     private final int priority;
/*     */     
/*     */     public ThreadStats(int tid, double cpu, char state, long sTime, long uTime, int pri) {
/*  92 */       this.threadId = tid;
/*  93 */       this.userTime = uTime;
/*  94 */       this.systemTime = sTime;
/*     */ 
/*     */       
/*  97 */       this.upTime = (long)((uTime + sTime) / (cpu / 100.0D + 5.0E-4D));
/*  98 */       switch (state) {
/*     */         case 'I':
/*     */         case 'S':
/* 101 */           this.state = OSProcess.State.SLEEPING;
/*     */           break;
/*     */         case 'U':
/* 104 */           this.state = OSProcess.State.WAITING;
/*     */           break;
/*     */         case 'R':
/* 107 */           this.state = OSProcess.State.RUNNING;
/*     */           break;
/*     */         case 'Z':
/* 110 */           this.state = OSProcess.State.ZOMBIE;
/*     */           break;
/*     */         case 'T':
/* 113 */           this.state = OSProcess.State.STOPPED;
/*     */           break;
/*     */         default:
/* 116 */           this.state = OSProcess.State.OTHER;
/*     */           break;
/*     */       } 
/* 119 */       this.priority = pri;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getThreadId() {
/* 126 */       return this.threadId;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getUserTime() {
/* 133 */       return this.userTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getSystemTime() {
/* 140 */       return this.systemTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getUpTime() {
/* 147 */       return this.upTime;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public OSProcess.State getState() {
/* 154 */       return this.state;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPriority() {
/* 161 */       return this.priority;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\mac\ThreadInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */