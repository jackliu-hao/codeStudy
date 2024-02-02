package oshi.hardware.platform.linux;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.Lshw;
import oshi.driver.linux.proc.CpuStat;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.GlobalConfig;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
final class LinuxCentralProcessor extends AbstractCentralProcessor {
   private static final String CPUFREQ_PATH = "oshi.cpu.freq.path";

   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String cpuVendor = "";
      String cpuName = "";
      String cpuFamily = "";
      String cpuModel = "";
      String cpuStepping = "";
      boolean cpu64bit = false;
      StringBuilder armStepping = new StringBuilder();
      String[] flags = new String[0];
      List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
      Iterator var11 = cpuInfo.iterator();

      while(true) {
         label97:
         while(var11.hasNext()) {
            String line = (String)var11.next();
            String[] splitLine = ParseUtil.whitespacesColonWhitespace.split(line);
            if (splitLine.length < 2) {
               if (line.startsWith("CPU architecture: ")) {
                  cpuFamily = line.replace("CPU architecture: ", "").trim();
               }
            } else {
               switch (splitLine[0]) {
                  case "vendor_id":
                  case "CPU implementer":
                     cpuVendor = splitLine[1];
                     break;
                  case "model name":
                     cpuName = splitLine[1];
                     break;
                  case "flags":
                     flags = splitLine[1].toLowerCase().split(" ");
                     String[] var16 = flags;
                     int var17 = flags.length;
                     int var18 = 0;

                     while(true) {
                        if (var18 >= var17) {
                           continue label97;
                        }

                        String flag = var16[var18];
                        if ("lm".equals(flag)) {
                           cpu64bit = true;
                           continue label97;
                        }

                        ++var18;
                     }
                  case "stepping":
                     cpuStepping = splitLine[1];
                     break;
                  case "CPU variant":
                     if (!armStepping.toString().startsWith("r")) {
                        armStepping.insert(0, "r" + splitLine[1]);
                     }
                     break;
                  case "CPU revision":
                     if (!armStepping.toString().contains("p")) {
                        armStepping.append('p').append(splitLine[1]);
                     }
                     break;
                  case "model":
                  case "CPU part":
                     cpuModel = splitLine[1];
                     break;
                  case "cpu family":
                     cpuFamily = splitLine[1];
               }
            }
         }

         if (cpuStepping.isEmpty()) {
            cpuStepping = armStepping.toString();
         }

         String processorID = getProcessorID(cpuVendor, cpuStepping, cpuModel, cpuFamily, flags);
         if (cpuVendor.startsWith("0x")) {
            List<String> lscpu = ExecutingCommand.runNative("lscpu");
            Iterator var21 = lscpu.iterator();

            while(var21.hasNext()) {
               String line = (String)var21.next();
               if (line.startsWith("Architecture:")) {
                  cpuVendor = line.replace("Architecture:", "").trim();
               }
            }
         }

         return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
      }
   }

   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
      Map<Integer, Integer> numaNodeMap = mapNumaNodes();
      List<String> procCpu = FileUtil.readFile(ProcPath.CPUINFO);
      List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList();
      int currentProcessor = 0;
      int currentCore = 0;
      int currentPackage = 0;
      boolean first = true;
      Iterator var8 = procCpu.iterator();

      while(true) {
         while(var8.hasNext()) {
            String cpu = (String)var8.next();
            if (cpu.startsWith("processor")) {
               if (!first) {
                  logProcs.add(new CentralProcessor.LogicalProcessor(currentProcessor, currentCore, currentPackage, (Integer)numaNodeMap.getOrDefault(currentProcessor, 0)));
               } else {
                  first = false;
               }

               currentProcessor = ParseUtil.parseLastInt(cpu, 0);
            } else if (!cpu.startsWith("core id") && !cpu.startsWith("cpu number")) {
               if (cpu.startsWith("physical id")) {
                  currentPackage = ParseUtil.parseLastInt(cpu, 0);
               }
            } else {
               currentCore = ParseUtil.parseLastInt(cpu, 0);
            }
         }

         logProcs.add(new CentralProcessor.LogicalProcessor(currentProcessor, currentCore, currentPackage, (Integer)numaNodeMap.getOrDefault(currentProcessor, 0)));
         return logProcs;
      }
   }

   private static Map<Integer, Integer> mapNumaNodes() {
      Map<Integer, Integer> numaNodeMap = new HashMap();
      List<String> lscpu = ExecutingCommand.runNative("lscpu -p=cpu,node");
      Iterator var2 = lscpu.iterator();

      while(var2.hasNext()) {
         String line = (String)var2.next();
         if (!line.startsWith("#")) {
            String[] split = line.split(",");
            if (split.length == 2) {
               numaNodeMap.put(ParseUtil.parseIntOrDefault(split[0], 0), ParseUtil.parseIntOrDefault(split[1], 0));
            }
         }
      }

      return numaNodeMap;
   }

   public long[] querySystemCpuLoadTicks() {
      long[] ticks = CpuStat.getSystemCpuLoadTicks();
      long hz = LinuxOperatingSystem.getHz();

      for(int i = 0; i < ticks.length; ++i) {
         ticks[i] = ticks[i] * 1000L / hz;
      }

      return ticks;
   }

   public long[] queryCurrentFreq() {
      String cpuFreqPath = GlobalConfig.get("oshi.cpu.freq.path", "");
      long[] freqs = new long[this.getLogicalProcessorCount()];
      long max = 0L;

      int i;
      for(i = 0; i < freqs.length; ++i) {
         freqs[i] = FileUtil.getLongFromFile(cpuFreqPath + "/cpu" + i + "/cpufreq/scaling_cur_freq");
         if (freqs[i] == 0L) {
            freqs[i] = FileUtil.getLongFromFile(cpuFreqPath + "/cpu" + i + "/cpufreq/cpuinfo_cur_freq");
         }

         if (max < freqs[i]) {
            max = freqs[i];
         }
      }

      if (max > 0L) {
         for(i = 0; i < freqs.length; ++i) {
            freqs[i] *= 1000L;
         }

         return freqs;
      } else {
         Arrays.fill(freqs, -1L);
         List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
         int proc = 0;
         Iterator var7 = cpuInfo.iterator();

         while(var7.hasNext()) {
            String s = (String)var7.next();
            if (s.toLowerCase().contains("cpu mhz")) {
               freqs[proc] = (long)(ParseUtil.parseLastDouble(s, 0.0) * 1000000.0);
               ++proc;
               if (proc >= freqs.length) {
                  break;
               }
            }
         }

         return freqs;
      }
   }

   public long queryMaxFreq() {
      String cpuFreqPath = GlobalConfig.get("oshi.cpu.freq.path", "");
      long max = Arrays.stream(this.getCurrentFreq()).max().orElse(-1L);
      File cpufreqdir = new File(cpuFreqPath + "/cpufreq");
      File[] policies = cpufreqdir.listFiles();
      if (policies != null) {
         for(int i = 0; i < policies.length; ++i) {
            File f = policies[i];
            if (f.getName().startsWith("policy")) {
               long freq = FileUtil.getLongFromFile(cpuFreqPath + "/cpufreq/" + f.getName() + "/scaling_max_freq");
               if (freq == 0L) {
                  freq = FileUtil.getLongFromFile(cpuFreqPath + "/cpufreq/" + f.getName() + "/cpuinfo_max_freq");
               }

               if (max < freq) {
                  max = freq;
               }
            }
         }
      }

      if (max > 0L) {
         max *= 1000L;
         long lshwMax = Lshw.queryCpuCapacity();
         return lshwMax > max ? lshwMax : max;
      } else {
         return -1L;
      }
   }

   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] average = new double[nelem];
         int retval = LinuxLibc.INSTANCE.getloadavg(average, nelem);
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
      long[][] ticks = CpuStat.getProcessorCpuLoadTicks(this.getLogicalProcessorCount());
      long hz = LinuxOperatingSystem.getHz();

      for(int i = 0; i < ticks.length; ++i) {
         for(int j = 0; j < ticks[i].length; ++j) {
            ticks[i][j] = ticks[i][j] * 1000L / hz;
         }
      }

      return ticks;
   }

   private static String getProcessorID(String vendor, String stepping, String model, String family, String[] flags) {
      boolean procInfo = false;
      String marker = "Processor Information";
      Iterator var7 = ExecutingCommand.runNative("dmidecode -t 4").iterator();

      while(true) {
         String checkLine;
         while(var7.hasNext()) {
            checkLine = (String)var7.next();
            if (!procInfo && checkLine.contains(marker)) {
               marker = "ID:";
               procInfo = true;
            } else if (procInfo && checkLine.contains(marker)) {
               return checkLine.split(marker)[1].trim();
            }
         }

         marker = "eax=";
         var7 = ExecutingCommand.runNative("cpuid -1r").iterator();

         do {
            if (!var7.hasNext()) {
               if (vendor.startsWith("0x")) {
                  return createMIDR(vendor, stepping, model, family) + "00000000";
               }

               return createProcessorID(stepping, model, family, flags);
            }

            checkLine = (String)var7.next();
         } while(!checkLine.contains(marker) || !checkLine.trim().startsWith("0x00000001"));

         String eax = "";
         String edx = "";
         String[] var11 = ParseUtil.whitespaces.split(checkLine);
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            String register = var11[var13];
            if (register.startsWith("eax=")) {
               eax = ParseUtil.removeMatchingString(register, "eax=0x");
            } else if (register.startsWith("edx=")) {
               edx = ParseUtil.removeMatchingString(register, "edx=0x");
            }
         }

         return edx + eax;
      }
   }

   private static String createMIDR(String vendor, String stepping, String model, String family) {
      int midrBytes = 0;
      if (stepping.startsWith("r") && stepping.contains("p")) {
         String[] rev = stepping.substring(1).split("p");
         midrBytes |= ParseUtil.parseLastInt(rev[1], 0);
         midrBytes |= ParseUtil.parseLastInt(rev[0], 0) << 20;
      }

      midrBytes |= ParseUtil.parseLastInt(model, 0) << 4;
      midrBytes |= ParseUtil.parseLastInt(family, 0) << 16;
      midrBytes |= ParseUtil.parseLastInt(vendor, 0) << 24;
      return String.format("%08X", midrBytes);
   }

   public long queryContextSwitches() {
      return CpuStat.getContextSwitches();
   }

   public long queryInterrupts() {
      return CpuStat.getInterrupts();
   }
}
