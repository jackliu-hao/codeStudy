package oshi.hardware.platform.unix.aix;

import com.sun.jna.Native;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Lssrad;
import oshi.driver.unix.aix.perfstat.PerfstatConfig;
import oshi.driver.unix.aix.perfstat.PerfstatCpu;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.aix.Perfstat;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class AixCentralProcessor extends AbstractCentralProcessor {
   private final Supplier<Perfstat.perfstat_cpu_total_t> cpuTotal = Memoizer.memoize(PerfstatCpu::queryCpuTotal, Memoizer.defaultExpiration());
   private final Supplier<Perfstat.perfstat_cpu_t[]> cpuProc = Memoizer.memoize(PerfstatCpu::queryCpu, Memoizer.defaultExpiration());
   private static final int SBITS = querySbits();
   private Perfstat.perfstat_partition_config_t config;
   private static final long USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);

   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String cpuVendor = "unknown";
      String cpuName = "";
      String cpuFamily = "";
      boolean cpu64bit = false;
      String nameMarker = "Processor Type:";
      String familyMarker = "Processor Version:";
      String bitnessMarker = "CPU Type:";
      Iterator var8 = ExecutingCommand.runNative("prtconf").iterator();

      String checkLine;
      while(var8.hasNext()) {
         checkLine = (String)var8.next();
         if (checkLine.startsWith("Processor Type:")) {
            cpuName = checkLine.split("Processor Type:")[1].trim();
            if (cpuName.startsWith("P")) {
               cpuVendor = "IBM";
            } else if (cpuName.startsWith("I")) {
               cpuVendor = "Intel";
            }
         } else if (checkLine.startsWith("Processor Version:")) {
            cpuFamily = checkLine.split("Processor Version:")[1].trim();
         } else if (checkLine.startsWith("CPU Type:")) {
            cpu64bit = checkLine.split("CPU Type:")[1].contains("64");
         }
      }

      String cpuModel = "";
      checkLine = "";
      String machineId = Native.toString(this.config.machineID);
      if (machineId.isEmpty()) {
         machineId = ExecutingCommand.getFirstAnswer("uname -m");
      }

      if (machineId.length() > 10) {
         int m = machineId.length() - 4;
         int s = machineId.length() - 2;
         cpuModel = machineId.substring(m, s);
         checkLine = machineId.substring(s);
      }

      return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, checkLine, machineId, cpu64bit, (long)(this.config.processorMHz * 1000000.0));
   }

   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
      this.config = PerfstatConfig.queryConfig();
      int physProcs = (int)this.config.numProcessors.max;
      if (physProcs < 1) {
         physProcs = 1;
      }

      int lcpus = this.config.lcpus;
      if (lcpus < 1) {
         lcpus = 1;
      }

      Map<Integer, Pair<Integer, Integer>> nodePkgMap = Lssrad.queryNodesPackages();
      List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList();

      for(int proc = 0; proc < lcpus; ++proc) {
         Pair<Integer, Integer> nodePkg = (Pair)nodePkgMap.get(proc);
         logProcs.add(new CentralProcessor.LogicalProcessor(proc, proc / physProcs, nodePkg == null ? 0 : (Integer)nodePkg.getB(), nodePkg == null ? 0 : (Integer)nodePkg.getA()));
      }

      return logProcs;
   }

   public long[] querySystemCpuLoadTicks() {
      Perfstat.perfstat_cpu_total_t perfstat = (Perfstat.perfstat_cpu_total_t)this.cpuTotal.get();
      long[] ticks = new long[CentralProcessor.TickType.values().length];
      ticks[CentralProcessor.TickType.USER.ordinal()] = perfstat.user * 1000L / USER_HZ;
      ticks[CentralProcessor.TickType.SYSTEM.ordinal()] = perfstat.sys * 1000L / USER_HZ;
      ticks[CentralProcessor.TickType.IDLE.ordinal()] = perfstat.idle * 1000L / USER_HZ;
      ticks[CentralProcessor.TickType.IOWAIT.ordinal()] = perfstat.wait * 1000L / USER_HZ;
      ticks[CentralProcessor.TickType.IRQ.ordinal()] = perfstat.devintrs * 1000L / USER_HZ;
      ticks[CentralProcessor.TickType.SOFTIRQ.ordinal()] = perfstat.softintrs * 1000L / USER_HZ;
      ticks[CentralProcessor.TickType.STEAL.ordinal()] = (perfstat.idle_stolen_purr + perfstat.busy_stolen_purr) * 1000L / USER_HZ;
      return ticks;
   }

   public long[] queryCurrentFreq() {
      long[] freqs = new long[this.getLogicalProcessorCount()];
      Arrays.fill(freqs, -1L);
      String freqMarker = "runs at";
      int idx = 0;
      Iterator var4 = ExecutingCommand.runNative("pmcycles -m").iterator();

      while(var4.hasNext()) {
         String checkLine = (String)var4.next();
         if (checkLine.contains(freqMarker)) {
            freqs[idx++] = ParseUtil.parseHertz(checkLine.split(freqMarker)[1].trim());
            if (idx >= freqs.length) {
               break;
            }
         }
      }

      return freqs;
   }

   protected long queryMaxFreq() {
      Perfstat.perfstat_cpu_total_t perfstat = (Perfstat.perfstat_cpu_total_t)this.cpuTotal.get();
      return perfstat.processorHZ;
   }

   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] average = new double[nelem];
         long[] loadavg = ((Perfstat.perfstat_cpu_total_t)this.cpuTotal.get()).loadavg;

         for(int i = 0; i < nelem; ++i) {
            average[i] = (double)loadavg[i] / (double)(1L << SBITS);
         }

         return average;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   public long[][] queryProcessorCpuLoadTicks() {
      Perfstat.perfstat_cpu_t[] cpu = (Perfstat.perfstat_cpu_t[])this.cpuProc.get();
      long[][] ticks = new long[cpu.length][CentralProcessor.TickType.values().length];

      for(int i = 0; i < cpu.length; ++i) {
         ticks[i] = new long[CentralProcessor.TickType.values().length];
         ticks[i][CentralProcessor.TickType.USER.ordinal()] = cpu[i].user * 1000L / USER_HZ;
         ticks[i][CentralProcessor.TickType.SYSTEM.ordinal()] = cpu[i].sys * 1000L / USER_HZ;
         ticks[i][CentralProcessor.TickType.IDLE.ordinal()] = cpu[i].idle * 1000L / USER_HZ;
         ticks[i][CentralProcessor.TickType.IOWAIT.ordinal()] = cpu[i].wait * 1000L / USER_HZ;
         ticks[i][CentralProcessor.TickType.IRQ.ordinal()] = cpu[i].devintrs * 1000L / USER_HZ;
         ticks[i][CentralProcessor.TickType.SOFTIRQ.ordinal()] = cpu[i].softintrs * 1000L / USER_HZ;
         ticks[i][CentralProcessor.TickType.STEAL.ordinal()] = (cpu[i].idle_stolen_purr + cpu[i].busy_stolen_purr) * 1000L / USER_HZ;
      }

      return ticks;
   }

   public long queryContextSwitches() {
      return ((Perfstat.perfstat_cpu_total_t)this.cpuTotal.get()).pswitch;
   }

   public long queryInterrupts() {
      Perfstat.perfstat_cpu_total_t cpu = (Perfstat.perfstat_cpu_total_t)this.cpuTotal.get();
      return cpu.devintrs + cpu.softintrs;
   }

   private static int querySbits() {
      Iterator var0 = FileUtil.readFile("/usr/include/sys/proc.h").iterator();

      String s;
      do {
         if (!var0.hasNext()) {
            return 16;
         }

         s = (String)var0.next();
      } while(!s.contains("SBITS") || !s.contains("#define"));

      return ParseUtil.parseLastInt(s, 16);
   }
}
