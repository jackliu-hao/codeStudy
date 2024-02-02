package oshi.hardware.platform.linux;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class LinuxGlobalMemory extends AbstractGlobalMemory {
   public static final long PAGE_SIZE = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf PAGE_SIZE"), 4096L);
   private final Supplier<Pair<Long, Long>> availTotal = Memoizer.memoize(LinuxGlobalMemory::readMemInfo, Memoizer.defaultExpiration());
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);

   public long getAvailable() {
      return (Long)((Pair)this.availTotal.get()).getA();
   }

   public long getTotal() {
      return (Long)((Pair)this.availTotal.get()).getB();
   }

   public long getPageSize() {
      return PAGE_SIZE;
   }

   public VirtualMemory getVirtualMemory() {
      return (VirtualMemory)this.vm.get();
   }

   private static Pair<Long, Long> readMemInfo() {
      long memFree = 0L;
      long activeFile = 0L;
      long inactiveFile = 0L;
      long sReclaimable = 0L;
      long memTotal = 0L;
      List<String> procMemInfo = FileUtil.readFile(ProcPath.MEMINFO);
      Iterator var13 = procMemInfo.iterator();

      while(var13.hasNext()) {
         String checkLine = (String)var13.next();
         String[] memorySplit = ParseUtil.whitespaces.split(checkLine, 2);
         if (memorySplit.length > 1) {
            switch (memorySplit[0]) {
               case "MemTotal:":
                  memTotal = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
                  break;
               case "MemAvailable:":
                  long memAvailable = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
                  return new Pair(memAvailable, memTotal);
               case "MemFree:":
                  memFree = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
                  break;
               case "Active(file):":
                  activeFile = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
                  break;
               case "Inactive(file):":
                  inactiveFile = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
                  break;
               case "SReclaimable:":
                  sReclaimable = ParseUtil.parseDecimalMemorySizeToBinary(memorySplit[1]);
            }
         }
      }

      return new Pair(memFree + activeFile + inactiveFile + sReclaimable, memTotal);
   }

   private VirtualMemory createVirtualMemory() {
      return new LinuxVirtualMemory(this);
   }
}
