/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.common.AbstractVirtualMemory;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
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
/*     */ final class FreeBsdVirtualMemory
/*     */   extends AbstractVirtualMemory
/*     */ {
/*     */   FreeBsdGlobalMemory global;
/*  45 */   private final Supplier<Long> used = Memoizer.memoize(FreeBsdVirtualMemory::querySwapUsed, Memoizer.defaultExpiration());
/*     */   
/*  47 */   private final Supplier<Long> total = Memoizer.memoize(FreeBsdVirtualMemory::querySwapTotal, Memoizer.defaultExpiration());
/*     */   
/*  49 */   private final Supplier<Long> pagesIn = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesIn, Memoizer.defaultExpiration());
/*     */   
/*  51 */   private final Supplier<Long> pagesOut = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesOut, Memoizer.defaultExpiration());
/*     */   
/*     */   FreeBsdVirtualMemory(FreeBsdGlobalMemory freeBsdGlobalMemory) {
/*  54 */     this.global = freeBsdGlobalMemory;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapUsed() {
/*  59 */     return ((Long)this.used.get()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapTotal() {
/*  64 */     return ((Long)this.total.get()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualMax() {
/*  69 */     return this.global.getTotal() + getSwapTotal();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getVirtualInUse() {
/*  74 */     return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapPagesIn() {
/*  79 */     return ((Long)this.pagesIn.get()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSwapPagesOut() {
/*  84 */     return ((Long)this.pagesOut.get()).longValue();
/*     */   }
/*     */   
/*     */   private static long querySwapUsed() {
/*  88 */     String swapInfo = ExecutingCommand.getAnswerAt("swapinfo -k", 1);
/*  89 */     String[] split = ParseUtil.whitespaces.split(swapInfo);
/*  90 */     if (split.length < 5) {
/*  91 */       return 0L;
/*     */     }
/*  93 */     return ParseUtil.parseLongOrDefault(split[2], 0L) << 10L;
/*     */   }
/*     */   
/*     */   private static long querySwapTotal() {
/*  97 */     return BsdSysctlUtil.sysctl("vm.swap_total", 0L);
/*     */   }
/*     */   
/*     */   private static long queryPagesIn() {
/* 101 */     return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsin", 0L);
/*     */   }
/*     */   
/*     */   private static long queryPagesOut() {
/* 105 */     return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsout", 0L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */