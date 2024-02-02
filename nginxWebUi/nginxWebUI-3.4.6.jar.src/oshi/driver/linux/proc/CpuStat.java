/*     */ package oshi.driver.linux.proc;
/*     */ 
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
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
/*     */ public final class CpuStat
/*     */ {
/*     */   public static long[] getSystemCpuLoadTicks() {
/*     */     String tickStr;
/*  49 */     long[] ticks = new long[(CentralProcessor.TickType.values()).length];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     List<String> procStat = FileUtil.readFile(ProcPath.STAT);
/*  55 */     if (!procStat.isEmpty()) {
/*  56 */       tickStr = procStat.get(0);
/*     */     } else {
/*  58 */       return ticks;
/*     */     } 
/*     */ 
/*     */     
/*  62 */     String[] tickArr = ParseUtil.whitespaces.split(tickStr);
/*  63 */     if (tickArr.length <= CentralProcessor.TickType.IDLE.getIndex())
/*     */     {
/*  65 */       return ticks;
/*     */     }
/*     */     
/*  68 */     for (int i = 0; i < (CentralProcessor.TickType.values()).length; i++) {
/*  69 */       ticks[i] = ParseUtil.parseLongOrDefault(tickArr[i + 1], 0L);
/*     */     }
/*     */     
/*  72 */     return ticks;
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
/*     */   public static long[][] getProcessorCpuLoadTicks(int logicalProcessorCount) {
/*  84 */     long[][] ticks = new long[logicalProcessorCount][(CentralProcessor.TickType.values()).length];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  89 */     int cpu = 0;
/*  90 */     List<String> procStat = FileUtil.readFile(ProcPath.STAT);
/*  91 */     for (String stat : procStat) {
/*  92 */       if (stat.startsWith("cpu") && !stat.startsWith("cpu ")) {
/*     */ 
/*     */ 
/*     */         
/*  96 */         String[] tickArr = ParseUtil.whitespaces.split(stat);
/*  97 */         if (tickArr.length <= CentralProcessor.TickType.IDLE.getIndex())
/*     */         {
/*  99 */           return ticks;
/*     */         }
/*     */         
/* 102 */         for (int i = 0; i < (CentralProcessor.TickType.values()).length; i++) {
/* 103 */           ticks[cpu][i] = ParseUtil.parseLongOrDefault(tickArr[i + 1], 0L);
/*     */         }
/*     */         
/* 106 */         if (++cpu >= logicalProcessorCount) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/* 111 */     return ticks;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getContextSwitches() {
/* 120 */     List<String> procStat = FileUtil.readFile(ProcPath.STAT);
/* 121 */     for (String stat : procStat) {
/* 122 */       if (stat.startsWith("ctxt ")) {
/* 123 */         String[] ctxtArr = ParseUtil.whitespaces.split(stat);
/* 124 */         if (ctxtArr.length == 2) {
/* 125 */           return ParseUtil.parseLongOrDefault(ctxtArr[1], 0L);
/*     */         }
/*     */       } 
/*     */     } 
/* 129 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getInterrupts() {
/* 138 */     List<String> procStat = FileUtil.readFile(ProcPath.STAT);
/* 139 */     for (String stat : procStat) {
/* 140 */       if (stat.startsWith("intr ")) {
/* 141 */         String[] intrArr = ParseUtil.whitespaces.split(stat);
/* 142 */         if (intrArr.length > 2) {
/* 143 */           return ParseUtil.parseLongOrDefault(intrArr[1], 0L);
/*     */         }
/*     */       } 
/*     */     } 
/* 147 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getBootTime() {
/* 157 */     List<String> procStat = FileUtil.readFile(ProcPath.STAT);
/* 158 */     for (String stat : procStat) {
/* 159 */       if (stat.startsWith("btime")) {
/* 160 */         String[] bTime = ParseUtil.whitespaces.split(stat);
/* 161 */         return ParseUtil.parseLongOrDefault(bTime[1], 0L);
/*     */       } 
/*     */     } 
/* 164 */     return 0L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\proc\CpuStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */