package oshi.driver.unix.aix.perfstat;

import java.util.Arrays;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.aix.Perfstat;

@ThreadSafe
public final class PerfstatProcess {
   private static final Perfstat PERF;

   private PerfstatProcess() {
   }

   public static Perfstat.perfstat_process_t[] queryProcesses() {
      Perfstat.perfstat_process_t process = new Perfstat.perfstat_process_t();
      int procCount = PERF.perfstat_process((Perfstat.perfstat_id_t)null, (Perfstat.perfstat_process_t[])null, process.size(), 0);
      if (procCount > 0) {
         Perfstat.perfstat_process_t[] proct = (Perfstat.perfstat_process_t[])process.toArray(procCount);
         Perfstat.perfstat_id_t firstprocess = new Perfstat.perfstat_id_t();
         int ret = PERF.perfstat_process(firstprocess, proct, process.size(), procCount);
         if (ret > 0) {
            return (Perfstat.perfstat_process_t[])Arrays.copyOf(proct, ret);
         }
      }

      return new Perfstat.perfstat_process_t[0];
   }

   static {
      PERF = Perfstat.INSTANCE;
   }
}
