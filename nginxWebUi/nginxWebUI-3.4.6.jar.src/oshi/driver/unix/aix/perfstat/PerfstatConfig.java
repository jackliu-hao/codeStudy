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
/*    */ public final class PerfstatConfig
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
/*    */   public static Perfstat.perfstat_partition_config_t queryConfig() {
/* 47 */     Perfstat.perfstat_partition_config_t config = new Perfstat.perfstat_partition_config_t();
/* 48 */     int ret = PERF.perfstat_partition_config(null, config, config.size(), 1);
/* 49 */     if (ret > 0) {
/* 50 */       return config;
/*    */     }
/* 52 */     return new Perfstat.perfstat_partition_config_t();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\perfstat\PerfstatConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */