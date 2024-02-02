package oshi.driver.linux.proc;

import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public final class CpuStat {
   private CpuStat() {
   }

   public static long[] getSystemCpuLoadTicks() {
      long[] ticks = new long[CentralProcessor.TickType.values().length];
      List<String> procStat = FileUtil.readFile(ProcPath.STAT);
      if (procStat.isEmpty()) {
         return ticks;
      } else {
         String tickStr = (String)procStat.get(0);
         String[] tickArr = ParseUtil.whitespaces.split(tickStr);
         if (tickArr.length <= CentralProcessor.TickType.IDLE.getIndex()) {
            return ticks;
         } else {
            for(int i = 0; i < CentralProcessor.TickType.values().length; ++i) {
               ticks[i] = ParseUtil.parseLongOrDefault(tickArr[i + 1], 0L);
            }

            return ticks;
         }
      }
   }

   public static long[][] getProcessorCpuLoadTicks(int logicalProcessorCount) {
      long[][] ticks = new long[logicalProcessorCount][CentralProcessor.TickType.values().length];
      int cpu = 0;
      List<String> procStat = FileUtil.readFile(ProcPath.STAT);
      Iterator var4 = procStat.iterator();

      while(var4.hasNext()) {
         String stat = (String)var4.next();
         if (stat.startsWith("cpu") && !stat.startsWith("cpu ")) {
            String[] tickArr = ParseUtil.whitespaces.split(stat);
            if (tickArr.length <= CentralProcessor.TickType.IDLE.getIndex()) {
               return ticks;
            }

            for(int i = 0; i < CentralProcessor.TickType.values().length; ++i) {
               ticks[cpu][i] = ParseUtil.parseLongOrDefault(tickArr[i + 1], 0L);
            }

            ++cpu;
            if (cpu >= logicalProcessorCount) {
               break;
            }
         }
      }

      return ticks;
   }

   public static long getContextSwitches() {
      List<String> procStat = FileUtil.readFile(ProcPath.STAT);
      Iterator var1 = procStat.iterator();

      while(var1.hasNext()) {
         String stat = (String)var1.next();
         if (stat.startsWith("ctxt ")) {
            String[] ctxtArr = ParseUtil.whitespaces.split(stat);
            if (ctxtArr.length == 2) {
               return ParseUtil.parseLongOrDefault(ctxtArr[1], 0L);
            }
         }
      }

      return -1L;
   }

   public static long getInterrupts() {
      List<String> procStat = FileUtil.readFile(ProcPath.STAT);
      Iterator var1 = procStat.iterator();

      while(var1.hasNext()) {
         String stat = (String)var1.next();
         if (stat.startsWith("intr ")) {
            String[] intrArr = ParseUtil.whitespaces.split(stat);
            if (intrArr.length > 2) {
               return ParseUtil.parseLongOrDefault(intrArr[1], 0L);
            }
         }
      }

      return -1L;
   }

   public static long getBootTime() {
      List<String> procStat = FileUtil.readFile(ProcPath.STAT);
      Iterator var1 = procStat.iterator();

      String stat;
      do {
         if (!var1.hasNext()) {
            return 0L;
         }

         stat = (String)var1.next();
      } while(!stat.startsWith("btime"));

      String[] bTime = ParseUtil.whitespaces.split(stat);
      return ParseUtil.parseLongOrDefault(bTime[1], 0L);
   }
}
