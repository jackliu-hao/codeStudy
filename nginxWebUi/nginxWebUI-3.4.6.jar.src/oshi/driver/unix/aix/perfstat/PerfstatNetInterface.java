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
/*    */ public final class PerfstatNetInterface
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
/*    */   public static Perfstat.perfstat_netinterface_t[] queryNetInterfaces() {
/* 48 */     Perfstat.perfstat_netinterface_t netinterface = new Perfstat.perfstat_netinterface_t();
/*    */     
/* 50 */     int total = PERF.perfstat_netinterface(null, null, netinterface.size(), 0);
/* 51 */     if (total > 0) {
/* 52 */       Perfstat.perfstat_netinterface_t[] statp = (Perfstat.perfstat_netinterface_t[])netinterface.toArray(total);
/* 53 */       Perfstat.perfstat_id_t firstnetinterface = new Perfstat.perfstat_id_t();
/* 54 */       int ret = PERF.perfstat_netinterface(firstnetinterface, statp, netinterface.size(), total);
/* 55 */       if (ret > 0) {
/* 56 */         return statp;
/*    */       }
/*    */     } 
/* 59 */     return new Perfstat.perfstat_netinterface_t[0];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\perfstat\PerfstatNetInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */