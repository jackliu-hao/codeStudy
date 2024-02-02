package oshi.hardware.platform.unix.solaris;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.solaris.kstat.SystemPages;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class SolarisVirtualMemory extends AbstractVirtualMemory {
   private static final Pattern SWAP_INFO = Pattern.compile(".+\\s(\\d+)K\\s+(\\d+)K$");
   private final SolarisGlobalMemory global;
   private final Supplier<Pair<Long, Long>> availTotal = Memoizer.memoize(SystemPages::queryAvailableTotal, Memoizer.defaultExpiration());
   private final Supplier<Pair<Long, Long>> usedTotal = Memoizer.memoize(SolarisVirtualMemory::querySwapInfo, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesIn = Memoizer.memoize(SolarisVirtualMemory::queryPagesIn, Memoizer.defaultExpiration());
   private final Supplier<Long> pagesOut = Memoizer.memoize(SolarisVirtualMemory::queryPagesOut, Memoizer.defaultExpiration());

   SolarisVirtualMemory(SolarisGlobalMemory solarisGlobalMemory) {
      this.global = solarisGlobalMemory;
   }

   public long getSwapUsed() {
      return (Long)((Pair)this.usedTotal.get()).getA();
   }

   public long getSwapTotal() {
      return (Long)((Pair)this.usedTotal.get()).getB();
   }

   public long getVirtualMax() {
      return this.global.getPageSize() * (Long)((Pair)this.availTotal.get()).getB() + this.getSwapTotal();
   }

   public long getVirtualInUse() {
      return this.global.getPageSize() * ((Long)((Pair)this.availTotal.get()).getB() - (Long)((Pair)this.availTotal.get()).getA()) + this.getSwapUsed();
   }

   public long getSwapPagesIn() {
      return (Long)this.pagesIn.get();
   }

   public long getSwapPagesOut() {
      return (Long)this.pagesOut.get();
   }

   private static long queryPagesIn() {
      long swapPagesIn = 0L;

      String s;
      for(Iterator var2 = ExecutingCommand.runNative("kstat -p cpu_stat:::pgswapin").iterator(); var2.hasNext(); swapPagesIn += ParseUtil.parseLastLong(s, 0L)) {
         s = (String)var2.next();
      }

      return swapPagesIn;
   }

   private static long queryPagesOut() {
      long swapPagesOut = 0L;

      String s;
      for(Iterator var2 = ExecutingCommand.runNative("kstat -p cpu_stat:::pgswapout").iterator(); var2.hasNext(); swapPagesOut += ParseUtil.parseLastLong(s, 0L)) {
         s = (String)var2.next();
      }

      return swapPagesOut;
   }

   private static Pair<Long, Long> querySwapInfo() {
      long swapTotal = 0L;
      long swapUsed = 0L;
      String swap = ExecutingCommand.getAnswerAt("swap -lk", 1);
      Matcher m = SWAP_INFO.matcher(swap);
      if (m.matches()) {
         swapTotal = ParseUtil.parseLongOrDefault(m.group(1), 0L) << 10;
         swapUsed = swapTotal - (ParseUtil.parseLongOrDefault(m.group(2), 0L) << 10);
      }

      return new Pair(swapUsed, swapTotal);
   }
}
