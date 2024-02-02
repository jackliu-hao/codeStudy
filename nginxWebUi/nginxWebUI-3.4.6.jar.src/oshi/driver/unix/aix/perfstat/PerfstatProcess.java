/*    */ package oshi.driver.unix.aix.perfstat;
/*    */ 
/*    */ import java.util.Arrays;
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
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class PerfstatProcess
/*    */ {
/* 39 */   private static final Perfstat PERF = Perfstat.INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Perfstat.perfstat_process_t[] queryProcesses() {
/* 50 */     Perfstat.perfstat_process_t process = new Perfstat.perfstat_process_t();
/*    */     
/* 52 */     int procCount = PERF.perfstat_process(null, null, process.size(), 0);
/* 53 */     if (procCount > 0) {
/* 54 */       Perfstat.perfstat_process_t[] proct = (Perfstat.perfstat_process_t[])process.toArray(procCount);
/* 55 */       Perfstat.perfstat_id_t firstprocess = new Perfstat.perfstat_id_t();
/* 56 */       int ret = PERF.perfstat_process(firstprocess, proct, process.size(), procCount);
/* 57 */       if (ret > 0) {
/* 58 */         return Arrays.<Perfstat.perfstat_process_t>copyOf(proct, ret);
/*    */       }
/*    */     } 
/* 61 */     return new Perfstat.perfstat_process_t[0];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\perfstat\PerfstatProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */