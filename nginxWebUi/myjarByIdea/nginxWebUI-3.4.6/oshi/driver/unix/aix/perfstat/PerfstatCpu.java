package oshi.driver.unix.aix.perfstat;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.aix.Perfstat;

@ThreadSafe
public final class PerfstatCpu {
   private static final Perfstat PERF;

   private PerfstatCpu() {
   }

   public static Perfstat.perfstat_cpu_total_t queryCpuTotal() {
      Perfstat.perfstat_cpu_total_t cpu = new Perfstat.perfstat_cpu_total_t();
      int ret = PERF.perfstat_cpu_total((Perfstat.perfstat_id_t)null, cpu, cpu.size(), 1);
      return ret > 0 ? cpu : new Perfstat.perfstat_cpu_total_t();
   }

   public static Perfstat.perfstat_cpu_t[] queryCpu() {
      Perfstat.perfstat_cpu_t cpu = new Perfstat.perfstat_cpu_t();
      int cputotal = PERF.perfstat_cpu((Perfstat.perfstat_id_t)null, (Perfstat.perfstat_cpu_t[])null, cpu.size(), 0);
      if (cputotal > 0) {
         Perfstat.perfstat_cpu_t[] statp = (Perfstat.perfstat_cpu_t[])cpu.toArray(cputotal);
         Perfstat.perfstat_id_t firstcpu = new Perfstat.perfstat_id_t();
         int ret = PERF.perfstat_cpu(firstcpu, statp, cpu.size(), cputotal);
         if (ret > 0) {
            return statp;
         }
      }

      return new Perfstat.perfstat_cpu_t[0];
   }

   public static long queryCpuAffinityMask() {
      int cpus = queryCpuTotal().ncpus;
      if (cpus < 63) {
         return (1L << cpus) - 1L;
      } else {
         return cpus == 63 ? Long.MAX_VALUE : -1L;
      }
   }

   static {
      PERF = Perfstat.INSTANCE;
   }
}
