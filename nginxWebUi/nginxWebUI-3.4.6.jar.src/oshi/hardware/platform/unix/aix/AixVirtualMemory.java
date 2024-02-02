/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.common.AbstractVirtualMemory;
/*    */ import oshi.jna.platform.unix.aix.Perfstat;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ final class AixVirtualMemory
/*    */   extends AbstractVirtualMemory
/*    */ {
/*    */   private final Supplier<Perfstat.perfstat_memory_total_t> perfstatMem;
/*    */   private static final long PAGESIZE = 4096L;
/*    */   
/*    */   AixVirtualMemory(Supplier<Perfstat.perfstat_memory_total_t> perfstatMem) {
/* 52 */     this.perfstatMem = perfstatMem;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getSwapUsed() {
/* 57 */     Perfstat.perfstat_memory_total_t perfstat = this.perfstatMem.get();
/* 58 */     return (perfstat.pgsp_total - perfstat.pgsp_free) * 4096L;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getSwapTotal() {
/* 63 */     return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgsp_total * 4096L;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getVirtualMax() {
/* 68 */     return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).virt_total * 4096L;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getVirtualInUse() {
/* 73 */     return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).virt_active * 4096L;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getSwapPagesIn() {
/* 78 */     return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgspins;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getSwapPagesOut() {
/* 83 */     return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgspouts;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */