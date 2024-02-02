/*    */ package oshi.hardware.platform.unix.freebsd;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.VirtualMemory;
/*    */ import oshi.hardware.common.AbstractGlobalMemory;
/*    */ import oshi.util.Memoizer;
/*    */ import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
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
/*    */ final class FreeBsdGlobalMemory
/*    */   extends AbstractGlobalMemory
/*    */ {
/* 42 */   private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
/*    */   
/* 44 */   private final Supplier<Long> total = Memoizer.memoize(FreeBsdGlobalMemory::queryPhysMem);
/*    */   
/* 46 */   private final Supplier<Long> pageSize = Memoizer.memoize(FreeBsdGlobalMemory::queryPageSize);
/*    */   
/* 48 */   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
/*    */ 
/*    */   
/*    */   public long getAvailable() {
/* 52 */     return ((Long)this.available.get()).longValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public long getTotal() {
/* 57 */     return ((Long)this.total.get()).longValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public long getPageSize() {
/* 62 */     return ((Long)this.pageSize.get()).longValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public VirtualMemory getVirtualMemory() {
/* 67 */     return this.vm.get();
/*    */   }
/*    */   
/*    */   private long queryVmStats() {
/* 71 */     long inactive = BsdSysctlUtil.sysctl("vm.stats.vm.v_inactive_count", 0L);
/* 72 */     long cache = BsdSysctlUtil.sysctl("vm.stats.vm.v_cache_count", 0L);
/* 73 */     long free = BsdSysctlUtil.sysctl("vm.stats.vm.v_free_count", 0L);
/* 74 */     return (inactive + cache + free) * getPageSize();
/*    */   }
/*    */   
/*    */   private static long queryPhysMem() {
/* 78 */     return BsdSysctlUtil.sysctl("hw.physmem", 0L);
/*    */   }
/*    */   
/*    */   private static long queryPageSize() {
/* 82 */     return BsdSysctlUtil.sysctl("hw.pagesize", 4096L);
/*    */   }
/*    */   
/*    */   private VirtualMemory createVirtualMemory() {
/* 86 */     return (VirtualMemory)new FreeBsdVirtualMemory(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */