package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.solaris.SolarisLibc;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
final class SolarisCentralProcessor extends AbstractCentralProcessor {
   private static final String CPU_INFO = "cpu_info";

   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String cpuVendor = "";
      String cpuName = "";
      String cpuFamily = "";
      String cpuModel = "";
      String cpuStepping = "";
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      try {
         LibKstat.Kstat ksp = kc.lookup("cpu_info", -1, (String)null);
         if (ksp != null && kc.read(ksp)) {
            cpuVendor = KstatUtil.dataLookupString(ksp, "vendor_id");
            cpuName = KstatUtil.dataLookupString(ksp, "brand");
            cpuFamily = KstatUtil.dataLookupString(ksp, "family");
            cpuModel = KstatUtil.dataLookupString(ksp, "model");
            cpuStepping = KstatUtil.dataLookupString(ksp, "stepping");
         }
      } catch (Throwable var10) {
         if (kc != null) {
            try {
               kc.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (kc != null) {
         kc.close();
      }

      boolean cpu64bit = "64".equals(ExecutingCommand.getFirstAnswer("isainfo -b").trim());
      String processorID = getProcessorID(cpuStepping, cpuModel, cpuFamily);
      return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
   }

   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
      Map<Integer, Integer> numaNodeMap = mapNumaNodes();
      List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList();
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      try {
         List<LibKstat.Kstat> kstats = kc.lookupAll("cpu_info", -1, (String)null);
         Iterator var5 = kstats.iterator();

         while(var5.hasNext()) {
            LibKstat.Kstat ksp = (LibKstat.Kstat)var5.next();
            if (ksp != null && kc.read(ksp)) {
               int procId = logProcs.size();
               String chipId = KstatUtil.dataLookupString(ksp, "chip_id");
               String coreId = KstatUtil.dataLookupString(ksp, "core_id");
               CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(procId, ParseUtil.parseIntOrDefault(coreId, 0), ParseUtil.parseIntOrDefault(chipId, 0), (Integer)numaNodeMap.getOrDefault(procId, 0));
               logProcs.add(logProc);
            }
         }
      } catch (Throwable var12) {
         if (kc != null) {
            try {
               kc.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (kc != null) {
         kc.close();
      }

      if (logProcs.isEmpty()) {
         logProcs.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
      }

      return logProcs;
   }

   private static Map<Integer, Integer> mapNumaNodes() {
      Map<Integer, Integer> numaNodeMap = new HashMap();
      int lgroup = 0;
      Iterator var2 = ExecutingCommand.runNative("lgrpinfo -c leaves").iterator();

      while(true) {
         while(var2.hasNext()) {
            String line = (String)var2.next();
            if (line.startsWith("lgroup")) {
               lgroup = ParseUtil.getFirstIntValue(line);
            } else if (line.contains("CPUs:") || line.contains("CPU:")) {
               Iterator var4 = ParseUtil.parseHyphenatedIntList(line.split(":")[1]).iterator();

               while(var4.hasNext()) {
                  Integer cpu = (Integer)var4.next();
                  numaNodeMap.put(cpu, lgroup);
               }
            }
         }

         return numaNodeMap;
      }
   }

   public long[] querySystemCpuLoadTicks() {
      long[] ticks = new long[CentralProcessor.TickType.values().length];
      long[][] procTicks = this.getProcessorCpuLoadTicks();

      for(int i = 0; i < ticks.length; ++i) {
         long[][] var4 = procTicks;
         int var5 = procTicks.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            long[] procTick = var4[var6];
            ticks[i] += procTick[i];
         }

         ticks[i] /= (long)procTicks.length;
      }

      return ticks;
   }

   public long[] queryCurrentFreq() {
      long[] freqs = new long[this.getLogicalProcessorCount()];
      Arrays.fill(freqs, -1L);
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      try {
         for(int i = 0; i < freqs.length; ++i) {
            Iterator var4 = kc.lookupAll("cpu_info", i, (String)null).iterator();

            while(var4.hasNext()) {
               LibKstat.Kstat ksp = (LibKstat.Kstat)var4.next();
               if (kc.read(ksp)) {
                  freqs[i] = KstatUtil.dataLookupLong(ksp, "current_clock_Hz");
               }
            }
         }
      } catch (Throwable var7) {
         if (kc != null) {
            try {
               kc.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (kc != null) {
         kc.close();
      }

      return freqs;
   }

   public long queryMaxFreq() {
      long max = -1L;
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      try {
         Iterator var4 = kc.lookupAll("cpu_info", 0, (String)null).iterator();

         while(var4.hasNext()) {
            LibKstat.Kstat ksp = (LibKstat.Kstat)var4.next();
            if (kc.read(ksp)) {
               String suppFreq = KstatUtil.dataLookupString(ksp, "supported_frequencies_Hz");
               if (!suppFreq.isEmpty()) {
                  String[] var7 = suppFreq.split(":");
                  int var8 = var7.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     String s = var7[var9];
                     long freq = ParseUtil.parseLongOrDefault(s, -1L);
                     if (max < freq) {
                        max = freq;
                     }
                  }
               }
            }
         }
      } catch (Throwable var14) {
         if (kc != null) {
            try {
               kc.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (kc != null) {
         kc.close();
      }

      return max;
   }

   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] average = new double[nelem];
         int retval = SolarisLibc.INSTANCE.getloadavg(average, nelem);
         if (retval < nelem) {
            for(int i = Math.max(retval, 0); i < average.length; ++i) {
               average[i] = -1.0;
            }
         }

         return average;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   public long[][] queryProcessorCpuLoadTicks() {
      long[][] ticks = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      int cpu = -1;
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      try {
         Iterator var4 = kc.lookupAll("cpu", -1, "sys").iterator();

         while(var4.hasNext()) {
            LibKstat.Kstat ksp = (LibKstat.Kstat)var4.next();
            ++cpu;
            if (cpu >= ticks.length) {
               break;
            }

            if (kc.read(ksp)) {
               ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = KstatUtil.dataLookupLong(ksp, "cpu_ticks_idle");
               ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = KstatUtil.dataLookupLong(ksp, "cpu_ticks_kernel");
               ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = KstatUtil.dataLookupLong(ksp, "cpu_ticks_user");
            }
         }
      } catch (Throwable var7) {
         if (kc != null) {
            try {
               kc.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (kc != null) {
         kc.close();
      }

      return ticks;
   }

   private static String getProcessorID(String stepping, String model, String family) {
      List<String> isainfo = ExecutingCommand.runNative("isainfo -v");
      StringBuilder flags = new StringBuilder();
      Iterator var5 = isainfo.iterator();

      while(var5.hasNext()) {
         String line = (String)var5.next();
         if (line.startsWith("32-bit")) {
            break;
         }

         if (!line.startsWith("64-bit")) {
            flags.append(' ').append(line.trim());
         }
      }

      return createProcessorID(stepping, model, family, ParseUtil.whitespaces.split(flags.toString().toLowerCase()));
   }

   public long queryContextSwitches() {
      long swtch = 0L;
      List<String> kstat = ExecutingCommand.runNative("kstat -p cpu_stat:::/pswitch\\\\|inv_swtch/");

      String s;
      for(Iterator var4 = kstat.iterator(); var4.hasNext(); swtch += ParseUtil.parseLastLong(s, 0L)) {
         s = (String)var4.next();
      }

      return swtch > 0L ? swtch : -1L;
   }

   public long queryInterrupts() {
      long intr = 0L;
      List<String> kstat = ExecutingCommand.runNative("kstat -p cpu_stat:::/intr/");

      String s;
      for(Iterator var4 = kstat.iterator(); var4.hasNext(); intr += ParseUtil.parseLastLong(s, 0L)) {
         s = (String)var4.next();
      }

      return intr > 0L ? intr : -1L;
   }
}
