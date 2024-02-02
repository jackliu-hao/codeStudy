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
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class PerfstatCpu
/*    */ {
/* 38 */   private static final Perfstat PERF = Perfstat.INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Perfstat.perfstat_cpu_total_t queryCpuTotal() {
/* 49 */     Perfstat.perfstat_cpu_total_t cpu = new Perfstat.perfstat_cpu_total_t();
/* 50 */     int ret = PERF.perfstat_cpu_total(null, cpu, cpu.size(), 1);
/* 51 */     if (ret > 0) {
/* 52 */       return cpu;
/*    */     }
/* 54 */     return new Perfstat.perfstat_cpu_total_t();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Perfstat.perfstat_cpu_t[] queryCpu() {
/* 63 */     Perfstat.perfstat_cpu_t cpu = new Perfstat.perfstat_cpu_t();
/*    */     
/* 65 */     int cputotal = PERF.perfstat_cpu(null, null, cpu.size(), 0);
/* 66 */     if (cputotal > 0) {
/* 67 */       Perfstat.perfstat_cpu_t[] statp = (Perfstat.perfstat_cpu_t[])cpu.toArray(cputotal);
/* 68 */       Perfstat.perfstat_id_t firstcpu = new Perfstat.perfstat_id_t();
/* 69 */       int ret = PERF.perfstat_cpu(firstcpu, statp, cpu.size(), cputotal);
/* 70 */       if (ret > 0) {
/* 71 */         return statp;
/*    */       }
/*    */     } 
/* 74 */     return new Perfstat.perfstat_cpu_t[0];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static long queryCpuAffinityMask() {
/* 83 */     int cpus = (queryCpuTotal()).ncpus;
/* 84 */     if (cpus < 63) {
/* 85 */       return (1L << cpus) - 1L;
/*    */     }
/* 87 */     return (cpus == 63) ? Long.MAX_VALUE : -1L;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\perfstat\PerfstatCpu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */