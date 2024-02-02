package oshi.hardware.platform.unix.freebsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
final class FreeBsdVirtualMemory extends AbstractVirtualMemory {
   FreeBsdGlobalMemory global;
   private final Supplier<Long> used = Memoizer.memoize(FreeBsdVirtualMemory::querySwapUsed, Memoizer.defaultExpiration());
   private final Supplier<Long> total = Memoizer.memoize(FreeBsdVirtualMemory::querySwapTotal, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesIn = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesIn, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesOut = Memoizer.memoize(FreeBsdVirtualMemory::queryPagesOut, Memoizer.defaultExpiration());

   FreeBsdVirtualMemory(FreeBsdGlobalMemory freeBsdGlobalMemory) {
      this.global = freeBsdGlobalMemory;
   }

   public long getSwapUsed() {
      return (Long)this.used.get();
   }

   public long getSwapTotal() {
      return (Long)this.total.get();
   }

   public long getVirtualMax() {
      return this.global.getTotal() + this.getSwapTotal();
   }

   public long getVirtualInUse() {
      return this.global.getTotal() - this.global.getAvailable() + this.getSwapUsed();
   }

   public long getSwapPagesIn() {
      return (Long)this.pagesIn.get();
   }

   public long getSwapPagesOut() {
      return (Long)this.pagesOut.get();
   }

   private static long querySwapUsed() {
      String swapInfo = ExecutingCommand.getAnswerAt("swapinfo -k", 1);
      String[] split = ParseUtil.whitespaces.split(swapInfo);
      return split.length < 5 ? 0L : ParseUtil.parseLongOrDefault(split[2], 0L) << 10;
   }

   private static long querySwapTotal() {
      return BsdSysctlUtil.sysctl("vm.swap_total", 0L);
   }

   private static long queryPagesIn() {
      return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsin", 0L);
   }

   private static long queryPagesOut() {
      return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsout", 0L);
   }
}
