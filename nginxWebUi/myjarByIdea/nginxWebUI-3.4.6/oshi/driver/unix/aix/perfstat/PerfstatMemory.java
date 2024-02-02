package oshi.driver.unix.aix.perfstat;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.aix.Perfstat;

@ThreadSafe
public final class PerfstatMemory {
   private static final Perfstat PERF;

   private PerfstatMemory() {
   }

   public static Perfstat.perfstat_memory_total_t queryMemoryTotal() {
      Perfstat.perfstat_memory_total_t memory = new Perfstat.perfstat_memory_total_t();
      int ret = PERF.perfstat_memory_total((Perfstat.perfstat_id_t)null, memory, memory.size(), 1);
      return ret > 0 ? memory : new Perfstat.perfstat_memory_total_t();
   }

   static {
      PERF = Perfstat.INSTANCE;
   }
}
