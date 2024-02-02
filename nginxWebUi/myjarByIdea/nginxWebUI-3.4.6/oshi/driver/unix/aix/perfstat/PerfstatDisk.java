package oshi.driver.unix.aix.perfstat;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.aix.Perfstat;

@ThreadSafe
public final class PerfstatDisk {
   private static final Perfstat PERF;

   private PerfstatDisk() {
   }

   public static Perfstat.perfstat_disk_t[] queryDiskStats() {
      Perfstat.perfstat_disk_t diskStats = new Perfstat.perfstat_disk_t();
      int total = PERF.perfstat_disk((Perfstat.perfstat_id_t)null, (Perfstat.perfstat_disk_t[])null, diskStats.size(), 0);
      if (total > 0) {
         Perfstat.perfstat_disk_t[] statp = (Perfstat.perfstat_disk_t[])diskStats.toArray(total);
         Perfstat.perfstat_id_t firstdiskStats = new Perfstat.perfstat_id_t();
         int ret = PERF.perfstat_disk(firstdiskStats, statp, diskStats.size(), total);
         if (ret > 0) {
            return statp;
         }
      }

      return new Perfstat.perfstat_disk_t[0];
   }

   static {
      PERF = Perfstat.INSTANCE;
   }
}
