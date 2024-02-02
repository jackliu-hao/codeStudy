/*    */ package oshi.driver.unix.aix.perfstat;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
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
/*    */ @ThreadSafe
/*    */ public final class PerfstatMemory
/*    */ {
/* 36 */   private static final Perfstat PERF = Perfstat.INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Perfstat.perfstat_memory_total_t queryMemoryTotal() {
/* 47 */     Perfstat.perfstat_memory_total_t memory = new Perfstat.perfstat_memory_total_t();
/* 48 */     int ret = PERF.perfstat_memory_total(null, memory, memory.size(), 1);
/* 49 */     if (ret > 0) {
/* 50 */       return memory;
/*    */     }
/* 52 */     return new Perfstat.perfstat_memory_total_t();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\perfstat\PerfstatMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */