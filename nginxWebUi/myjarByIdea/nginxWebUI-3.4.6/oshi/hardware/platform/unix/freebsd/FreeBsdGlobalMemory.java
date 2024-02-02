package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.Memoizer;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
final class FreeBsdGlobalMemory extends AbstractGlobalMemory {
   private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
   private final Supplier<Long> total = Memoizer.memoize(FreeBsdGlobalMemory::queryPhysMem);
   private final Supplier<Long> pageSize = Memoizer.memoize(FreeBsdGlobalMemory::queryPageSize);
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);

   public long getAvailable() {
      return (Long)this.available.get();
   }

   public long getTotal() {
      return (Long)this.total.get();
   }

   public long getPageSize() {
      return (Long)this.pageSize.get();
   }

   public VirtualMemory getVirtualMemory() {
      return (VirtualMemory)this.vm.get();
   }

   private long queryVmStats() {
      long inactive = BsdSysctlUtil.sysctl("vm.stats.vm.v_inactive_count", 0L);
      long cache = BsdSysctlUtil.sysctl("vm.stats.vm.v_cache_count", 0L);
      long free = BsdSysctlUtil.sysctl("vm.stats.vm.v_free_count", 0L);
      return (inactive + cache + free) * this.getPageSize();
   }

   private static long queryPhysMem() {
      return BsdSysctlUtil.sysctl("hw.physmem", 0L);
   }

   private static long queryPageSize() {
      return BsdSysctlUtil.sysctl("hw.pagesize", 4096L);
   }

   private VirtualMemory createVirtualMemory() {
      return new FreeBsdVirtualMemory(this);
   }
}
