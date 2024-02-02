/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.common.AbstractVirtualMemory;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
/*     */ import oshi.util.tuples.Pair;
/*     */ import oshi.util.tuples.Triplet;
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
/*     */ final class LinuxVirtualMemory
/*     */   extends AbstractVirtualMemory
/*     */ {
/*     */   private final LinuxGlobalMemory global;
/*  48 */   private final Supplier<Triplet<Long, Long, Long>> usedTotalCommitLim = Memoizer.memoize(LinuxVirtualMemory::queryMemInfo, 
/*  49 */       Memoizer.defaultExpiration());
/*     */   
/*  51 */   private final Supplier<Pair<Long, Long>> inOut = Memoizer.memoize(LinuxVirtualMemory::queryVmStat, Memoizer.defaultExpiration());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   LinuxVirtualMemory(LinuxGlobalMemory linuxGlobalMemory) {
/*  60 */     this.global = linuxGlobalMemory;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapUsed() {
/*  65 */     return ((Long)((Triplet)this.usedTotalCommitLim.get()).getA()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapTotal() {
/*  70 */     return ((Long)((Triplet)this.usedTotalCommitLim.get()).getB()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualMax() {
/*  75 */     return ((Long)((Triplet)this.usedTotalCommitLim.get()).getC()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualInUse() {
/*  80 */     return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapPagesIn() {
/*  85 */     return ((Long)((Pair)this.inOut.get()).getA()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapPagesOut() {
/*  90 */     return ((Long)((Pair)this.inOut.get()).getB()).longValue();
/*     */   }
/*     */   
/*     */   private static Triplet<Long, Long, Long> queryMemInfo() {
/*  94 */     long swapFree = 0L;
/*  95 */     long swapTotal = 0L;
/*  96 */     long commitLimit = 0L;
/*     */     
/*  98 */     List<String> procMemInfo = FileUtil.readFile(ProcPath.MEMINFO);
/*  99 */     for (String checkLine : procMemInfo) {
/* 100 */       String[] memorySplit = ParseUtil.whitespaces.split(checkLine);
/* 101 */       if (memorySplit.length > 1) {
/* 102 */         switch (memorySplit[0]) {
/*     */           case "SwapTotal:":
/* 104 */             swapTotal = parseMeminfo(memorySplit);
/*     */           
/*     */           case "SwapFree:":
/* 107 */             swapFree = parseMeminfo(memorySplit);
/*     */           
/*     */           case "CommitLimit:":
/* 110 */             commitLimit = parseMeminfo(memorySplit);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */     } 
/* 118 */     return new Triplet(Long.valueOf(swapTotal - swapFree), Long.valueOf(swapTotal), Long.valueOf(commitLimit));
/*     */   }
/*     */   
/*     */   private static Pair<Long, Long> queryVmStat() {
/* 122 */     long swapPagesIn = 0L;
/* 123 */     long swapPagesOut = 0L;
/* 124 */     List<String> procVmStat = FileUtil.readFile(ProcPath.VMSTAT);
/* 125 */     for (String checkLine : procVmStat) {
/* 126 */       String[] memorySplit = ParseUtil.whitespaces.split(checkLine);
/* 127 */       if (memorySplit.length > 1) {
/* 128 */         switch (memorySplit[0]) {
/*     */           case "pswpin":
/* 130 */             swapPagesIn = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
/*     */           
/*     */           case "pswpout":
/* 133 */             swapPagesOut = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       }
/*     */     } 
/* 141 */     return new Pair(Long.valueOf(swapPagesIn), Long.valueOf(swapPagesOut));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static long parseMeminfo(String[] memorySplit) {
/* 152 */     if (memorySplit.length < 2) {
/* 153 */       return 0L;
/*     */     }
/* 155 */     long memory = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
/* 156 */     if (memorySplit.length > 2 && "kB".equals(memorySplit[2])) {
/* 157 */       memory *= 1024L;
/*     */     }
/* 159 */     return memory;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */