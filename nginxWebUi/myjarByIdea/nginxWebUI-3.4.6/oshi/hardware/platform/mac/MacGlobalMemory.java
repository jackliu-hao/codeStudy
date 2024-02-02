package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
final class MacGlobalMemory extends AbstractGlobalMemory {
   private static final Logger LOG = LoggerFactory.getLogger(MacGlobalMemory.class);
   private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
   private final Supplier<Long> total = Memoizer.memoize(MacGlobalMemory::queryPhysMem);
   private final Supplier<Long> pageSize = Memoizer.memoize(MacGlobalMemory::queryPageSize);
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

   public List<PhysicalMemory> getPhysicalMemory() {
      List<PhysicalMemory> pmList = new ArrayList();
      List<String> sp = ExecutingCommand.runNative("system_profiler SPMemoryDataType");
      int bank = 0;
      String bankLabel = "unknown";
      long capacity = 0L;
      long speed = 0L;
      String manufacturer = "unknown";
      String memoryType = "unknown";
      Iterator var11 = sp.iterator();

      while(var11.hasNext()) {
         String line = (String)var11.next();
         if (line.trim().startsWith("BANK")) {
            if (bank++ > 0) {
               pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
            }

            bankLabel = line.trim();
            int colon = bankLabel.lastIndexOf(58);
            if (colon > 0) {
               bankLabel = bankLabel.substring(0, colon - 1);
            }
         } else if (bank > 0) {
            String[] split = line.trim().split(":");
            if (split.length == 2) {
               switch (split[0]) {
                  case "Size":
                     capacity = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
                     break;
                  case "Type":
                     memoryType = split[1].trim();
                     break;
                  case "Speed":
                     speed = ParseUtil.parseHertz(split[1]);
                     break;
                  case "Manufacturer":
                     manufacturer = split[1].trim();
               }
            }
         }
      }

      pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
      return pmList;
   }

   private long queryVmStats() {
      SystemB.VMStatistics vmStats = new SystemB.VMStatistics();
      if (0 != SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, vmStats, new IntByReference(vmStats.size() / SystemB.INT_SIZE))) {
         LOG.error((String)"Failed to get host VM info. Error code: {}", (Object)Native.getLastError());
         return 0L;
      } else {
         return (long)(vmStats.free_count + vmStats.inactive_count) * this.getPageSize();
      }
   }

   private static long queryPhysMem() {
      return SysctlUtil.sysctl("hw.memsize", 0L);
   }

   private static long queryPageSize() {
      LongByReference pPageSize = new LongByReference();
      if (0 == SystemB.INSTANCE.host_page_size(SystemB.INSTANCE.mach_host_self(), pPageSize)) {
         return pPageSize.getValue();
      } else {
         LOG.error((String)"Failed to get host page size. Error code: {}", (Object)Native.getLastError());
         return 4098L;
      }
   }

   private VirtualMemory createVirtualMemory() {
      return new MacVirtualMemory(this);
   }
}
