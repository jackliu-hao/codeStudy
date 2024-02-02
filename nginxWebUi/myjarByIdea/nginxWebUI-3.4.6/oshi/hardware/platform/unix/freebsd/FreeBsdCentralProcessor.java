package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
final class FreeBsdCentralProcessor extends AbstractCentralProcessor {
   private static final Logger LOG = LoggerFactory.getLogger(FreeBsdCentralProcessor.class);
   private static final Pattern CPUMASK = Pattern.compile(".*<cpu\\s.*mask=\"(?:0x)?(\\p{XDigit}+)\".*>.*</cpu>.*");

   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
      Pattern identifierPattern = Pattern.compile("Origin=\"([^\"]*)\".*Id=(\\S+).*Family=(\\S+).*Model=(\\S+).*Stepping=(\\S+).*");
      Pattern featuresPattern = Pattern.compile("Features=(\\S+)<.*");
      String cpuVendor = "";
      String cpuName = BsdSysctlUtil.sysctl("hw.model", "");
      String cpuFamily = "";
      String cpuModel = "";
      String cpuStepping = "";
      long processorIdBits = 0L;
      List<String> cpuInfo = FileUtil.readFile("/var/run/dmesg.boot");
      Iterator var13 = cpuInfo.iterator();

      while(var13.hasNext()) {
         String line = (String)var13.next();
         line = line.trim();
         if (line.startsWith("CPU:") && cpuName.isEmpty()) {
            cpuName = line.replace("CPU:", "").trim();
         } else {
            Matcher m;
            if (line.startsWith("Origin=")) {
               m = identifierPattern.matcher(line);
               if (m.matches()) {
                  cpuVendor = m.group(1);
                  processorIdBits |= Long.decode(m.group(2));
                  cpuFamily = Integer.decode(m.group(3)).toString();
                  cpuModel = Integer.decode(m.group(4)).toString();
                  cpuStepping = Integer.decode(m.group(5)).toString();
               }
            } else if (line.startsWith("Features=")) {
               m = featuresPattern.matcher(line);
               if (m.matches()) {
                  processorIdBits |= Long.decode(m.group(1)) << 32;
               }
               break;
            }
         }
      }

      boolean cpu64bit = ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64");
      String processorID = getProcessorIDfromDmiDecode(processorIdBits);
      return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
   }

   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
      List<CentralProcessor.LogicalProcessor> logProcs = parseTopology();
      if (logProcs.isEmpty()) {
         logProcs.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
      }

      return logProcs;
   }

   private static List<CentralProcessor.LogicalProcessor> parseTopology() {
      String[] topology = BsdSysctlUtil.sysctl("kern.sched.topology_spec", "").split("\\n|\\r");
      long group1 = 1L;
      List<Long> group2 = new ArrayList();
      List<Long> group3 = new ArrayList();
      int groupLevel = 0;
      String[] var6 = topology;
      int var7 = topology.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String topo = var6[var8];
         if (topo.contains("<group level=")) {
            ++groupLevel;
         } else if (topo.contains("</group>")) {
            --groupLevel;
         } else if (topo.contains("<cpu")) {
            Matcher m = CPUMASK.matcher(topo);
            if (m.matches()) {
               switch (groupLevel) {
                  case 1:
                     group1 = Long.parseLong(m.group(1), 16);
                     break;
                  case 2:
                     group2.add(Long.parseLong(m.group(1), 16));
                     break;
                  case 3:
                     group3.add(Long.parseLong(m.group(1), 16));
               }
            }
         }
      }

      return matchBitmasks(group1, group2, group3);
   }

   private static List<CentralProcessor.LogicalProcessor> matchBitmasks(long group1, List<Long> group2, List<Long> group3) {
      List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList();
      int lowBit = Long.numberOfTrailingZeros(group1);
      int hiBit = 63 - Long.numberOfLeadingZeros(group1);

      for(int i = lowBit; i <= hiBit; ++i) {
         if ((group1 & 1L << i) > 0L) {
            int numaNode = 0;
            CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(i, getMatchingBitmask(group3, i), getMatchingBitmask(group2, i), numaNode);
            logProcs.add(logProc);
         }
      }

      return logProcs;
   }

   private static int getMatchingBitmask(List<Long> bitmasks, int lp) {
      for(int j = 0; j < bitmasks.size(); ++j) {
         if (((Long)bitmasks.get(j) & 1L << lp) > 0L) {
            return j;
         }
      }

      return 0;
   }

   public long[] querySystemCpuLoadTicks() {
      long[] ticks = new long[CentralProcessor.TickType.values().length];
      FreeBsdLibc.CpTime cpTime = new FreeBsdLibc.CpTime();
      BsdSysctlUtil.sysctl("kern.cp_time", (Structure)cpTime);
      ticks[CentralProcessor.TickType.USER.getIndex()] = cpTime.cpu_ticks[0];
      ticks[CentralProcessor.TickType.NICE.getIndex()] = cpTime.cpu_ticks[1];
      ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = cpTime.cpu_ticks[2];
      ticks[CentralProcessor.TickType.IRQ.getIndex()] = cpTime.cpu_ticks[3];
      ticks[CentralProcessor.TickType.IDLE.getIndex()] = cpTime.cpu_ticks[4];
      return ticks;
   }

   public long[] queryCurrentFreq() {
      long freq = BsdSysctlUtil.sysctl("dev.cpu.0.freq", -1L);
      if (freq > 0L) {
         freq *= 1000000L;
      } else {
         freq = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
      }

      long[] freqs = new long[this.getLogicalProcessorCount()];
      Arrays.fill(freqs, freq);
      return freqs;
   }

   public long queryMaxFreq() {
      long max = -1L;
      String freqLevels = BsdSysctlUtil.sysctl("dev.cpu.0.freq_levels", "");
      String[] var4 = ParseUtil.whitespaces.split(freqLevels);
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         long freq = ParseUtil.parseLongOrDefault(s.split("/")[0], -1L);
         if (max < freq) {
            max = freq;
         }
      }

      if (max > 0L) {
         max *= 1000000L;
      } else {
         max = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
      }

      return max;
   }

   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] average = new double[nelem];
         int retval = FreeBsdLibc.INSTANCE.getloadavg(average, nelem);
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
      long size = (long)(new FreeBsdLibc.CpTime()).size();
      long arraySize = size * (long)this.getLogicalProcessorCount();
      Pointer p = new Memory(arraySize);
      String name = "kern.cp_times";
      if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, p, new IntByReference((int)arraySize), (Pointer)null, 0)) {
         LOG.error((String)"Failed syctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
         return ticks;
      } else {
         for(int cpu = 0; cpu < this.getLogicalProcessorCount(); ++cpu) {
            ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = p.getLong(size * (long)cpu + (long)(0 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.NICE.getIndex()] = p.getLong(size * (long)cpu + (long)(1 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = p.getLong(size * (long)cpu + (long)(2 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = p.getLong(size * (long)cpu + (long)(3 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = p.getLong(size * (long)cpu + (long)(4 * FreeBsdLibc.UINT64_SIZE));
         }

         return ticks;
      }
   }

   private static String getProcessorIDfromDmiDecode(long processorID) {
      boolean procInfo = false;
      String marker = "Processor Information";
      Iterator var4 = ExecutingCommand.runNative("dmidecode -t system").iterator();

      while(true) {
         while(var4.hasNext()) {
            String checkLine = (String)var4.next();
            if (!procInfo && checkLine.contains(marker)) {
               marker = "ID:";
               procInfo = true;
            } else if (procInfo && checkLine.contains(marker)) {
               return checkLine.split(marker)[1].trim();
            }
         }

         return String.format("%016X", processorID);
      }
   }

   public long queryContextSwitches() {
      String name = "vm.stats.sys.v_swtch";
      IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
      Pointer p = new Memory((long)size.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, p, size, (Pointer)null, 0) ? -1L : ParseUtil.unsignedIntToLong(p.getInt(0L));
   }

   public long queryInterrupts() {
      String name = "vm.stats.sys.v_intr";
      IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
      Pointer p = new Memory((long)size.getValue());
      return 0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, p, size, (Pointer)null, 0) ? -1L : ParseUtil.unsignedIntToLong(p.getInt(0L));
   }
}
