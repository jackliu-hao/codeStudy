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
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class PerfstatDisk
/*    */ {
/* 37 */   private static final Perfstat PERF = Perfstat.INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Perfstat.perfstat_disk_t[] queryDiskStats() {
/* 48 */     Perfstat.perfstat_disk_t diskStats = new Perfstat.perfstat_disk_t();
/*    */     
/* 50 */     int total = PERF.perfstat_disk(null, null, diskStats.size(), 0);
/* 51 */     if (total > 0) {
/* 52 */       Perfstat.perfstat_disk_t[] statp = (Perfstat.perfstat_disk_t[])diskStats.toArray(total);
/* 53 */       Perfstat.perfstat_id_t firstdiskStats = new Perfstat.perfstat_id_t();
/* 54 */       int ret = PERF.perfstat_disk(firstdiskStats, statp, diskStats.size(), total);
/* 55 */       if (ret > 0) {
/* 56 */         return statp;
/*    */       }
/*    */     } 
/* 59 */     return new Perfstat.perfstat_disk_t[0];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\perfstat\PerfstatDisk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */