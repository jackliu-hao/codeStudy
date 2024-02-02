package oshi.hardware.platform.unix.aix;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.jna.platform.unix.aix.Perfstat;

@ThreadSafe
final class AixVirtualMemory extends AbstractVirtualMemory {
   private final Supplier<Perfstat.perfstat_memory_total_t> perfstatMem;
   private static final long PAGESIZE = 4096L;

   AixVirtualMemory(Supplier<Perfstat.perfstat_memory_total_t> perfstatMem) {
      this.perfstatMem = perfstatMem;
   }

   public long getSwapUsed() {
      Perfstat.perfstat_memory_total_t perfstat = (Perfstat.perfstat_memory_total_t)this.perfstatMem.get();
      return (perfstat.pgsp_total - perfstat.pgsp_free) * 4096L;
   }

   public long getSwapTotal() {
      return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgsp_total * 4096L;
   }

   public long getVirtualMax() {
      return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).virt_total * 4096L;
   }

   public long getVirtualInUse() {
      return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).virt_active * 4096L;
   }

   public long getSwapPagesIn() {
      return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgspins;
   }

   public long getSwapPagesOut() {
      return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).pgspouts;
   }
}
