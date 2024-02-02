/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.Psapi;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.perfmon.MemoryInformation;
/*     */ import oshi.driver.windows.perfmon.PagingFile;
/*     */ import oshi.hardware.common.AbstractVirtualMemory;
/*     */ import oshi.util.Memoizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ final class WindowsVirtualMemory
/*     */   extends AbstractVirtualMemory
/*     */ {
/*  54 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsVirtualMemory.class);
/*     */   
/*     */   private final WindowsGlobalMemory global;
/*     */   
/*  58 */   private final Supplier<Long> used = Memoizer.memoize(WindowsVirtualMemory::querySwapUsed, Memoizer.defaultExpiration());
/*     */   
/*  60 */   private final Supplier<Triplet<Long, Long, Long>> totalVmaxVused = Memoizer.memoize(WindowsVirtualMemory::querySwapTotalVirtMaxVirtUsed, 
/*  61 */       Memoizer.defaultExpiration());
/*     */   
/*  63 */   private final Supplier<Pair<Long, Long>> swapInOut = Memoizer.memoize(WindowsVirtualMemory::queryPageSwaps, 
/*  64 */       Memoizer.defaultExpiration());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   WindowsVirtualMemory(WindowsGlobalMemory windowsGlobalMemory) {
/*  73 */     this.global = windowsGlobalMemory;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapUsed() {
/*  78 */     return this.global.getPageSize() * ((Long)this.used.get()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapTotal() {
/*  83 */     return this.global.getPageSize() * ((Long)((Triplet)this.totalVmaxVused.get()).getA()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualMax() {
/*  88 */     return this.global.getPageSize() * ((Long)((Triplet)this.totalVmaxVused.get()).getB()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualInUse() {
/*  93 */     return this.global.getPageSize() * ((Long)((Triplet)this.totalVmaxVused.get()).getC()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapPagesIn() {
/*  98 */     return ((Long)((Pair)this.swapInOut.get()).getA()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapPagesOut() {
/* 103 */     return ((Long)((Pair)this.swapInOut.get()).getB()).longValue();
/*     */   }
/*     */   
/*     */   private static long querySwapUsed() {
/* 107 */     return ((Long)PagingFile.querySwapUsed().getOrDefault(PagingFile.PagingPercentProperty.PERCENTUSAGE, Long.valueOf(0L))).longValue();
/*     */   }
/*     */   
/*     */   private static Triplet<Long, Long, Long> querySwapTotalVirtMaxVirtUsed() {
/* 111 */     Psapi.PERFORMANCE_INFORMATION perfInfo = new Psapi.PERFORMANCE_INFORMATION();
/* 112 */     if (!Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size())) {
/* 113 */       LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/* 114 */       return new Triplet(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(0L));
/*     */     } 
/* 116 */     return new Triplet(Long.valueOf(perfInfo.CommitLimit.longValue() - perfInfo.PhysicalTotal.longValue()), 
/* 117 */         Long.valueOf(perfInfo.CommitLimit.longValue()), Long.valueOf(perfInfo.CommitTotal.longValue()));
/*     */   }
/*     */   
/*     */   private static Pair<Long, Long> queryPageSwaps() {
/* 121 */     Map<MemoryInformation.PageSwapProperty, Long> valueMap = MemoryInformation.queryPageSwaps();
/* 122 */     return new Pair(valueMap.getOrDefault(MemoryInformation.PageSwapProperty.PAGESINPUTPERSEC, Long.valueOf(0L)), valueMap
/* 123 */         .getOrDefault(MemoryInformation.PageSwapProperty.PAGESOUTPUTPERSEC, Long.valueOf(0L)));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */