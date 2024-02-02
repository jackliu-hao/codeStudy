/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.VirtualMemory;
/*     */ import oshi.hardware.common.AbstractGlobalMemory;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ public final class LinuxGlobalMemory
/*     */   extends AbstractGlobalMemory
/*     */ {
/*  48 */   public static final long PAGE_SIZE = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf PAGE_SIZE"), 4096L);
/*     */   
/*  50 */   private final Supplier<Pair<Long, Long>> availTotal = Memoizer.memoize(LinuxGlobalMemory::readMemInfo, Memoizer.defaultExpiration());
/*     */   
/*  52 */   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
/*     */ 
/*     */   
/*     */   public long getAvailable() {
/*  56 */     return ((Long)((Pair)this.availTotal.get()).getA()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotal() {
/*  61 */     return ((Long)((Pair)this.availTotal.get()).getB()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPageSize() {
/*  66 */     return PAGE_SIZE;
/*     */   }
/*     */ 
/*     */   
/*     */   public VirtualMemory getVirtualMemory() {
/*  71 */     return this.vm.get();
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
/*     */   private static Pair<Long, Long> readMemInfo() {
/*  89 */     long memFree = 0L;
/*  90 */     long activeFile = 0L;
/*  91 */     long inactiveFile = 0L;
/*  92 */     long sReclaimable = 0L;
/*     */     
/*  94 */     long memTotal = 0L;
/*     */ 
/*     */     
/*  97 */     List<String> procMemInfo = FileUtil.readFile(ProcPath.MEMINFO);
/*  98 */     for (String checkLine : procMemInfo) {
/*  99 */       String[] memorySplit = ParseUtil.whitespaces.split(checkLine, 2);
/* 100 */       if (memorySplit.length > 1) {
/* 101 */         long memAvailable; switch (memorySplit[0]) {
/*     */           case "MemTotal:":
/* 103 */             memTotal = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
/*     */           
/*     */           case "MemAvailable:":
/* 106 */             memAvailable = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
/*     */             
/* 108 */             return new Pair(Long.valueOf(memAvailable), Long.valueOf(memTotal));
/*     */           case "MemFree:":
/* 110 */             memFree = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
/*     */           
/*     */           case "Active(file):":
/* 113 */             activeFile = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
/*     */           
/*     */           case "Inactive(file):":
/* 116 */             inactiveFile = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
/*     */           
/*     */           case "SReclaimable:":
/* 119 */             sReclaimable = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       } 
/*     */     } 
/* 128 */     return new Pair(Long.valueOf(memFree + activeFile + inactiveFile + sReclaimable), Long.valueOf(memTotal));
/*     */   }
/*     */   
/*     */   private VirtualMemory createVirtualMemory() {
/* 132 */     return (VirtualMemory)new LinuxVirtualMemory(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */