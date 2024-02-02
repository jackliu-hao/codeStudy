package oshi.hardware.platform.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.LogicalProcessorInformation;
import oshi.driver.windows.perfmon.ProcessorInformation;
import oshi.driver.windows.perfmon.SystemInformation;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.windows.PowrProf;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class WindowsCentralProcessor extends AbstractCentralProcessor {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsCentralProcessor.class);
   private Map<String, Integer> numaNodeProcToLogicalProcMap;

   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String cpuVendor = "";
      String cpuName = "";
      String cpuIdentifier = "";
      String cpuFamily = "";
      String cpuModel = "";
      String cpuStepping = "";
      boolean cpu64bit = false;
      String cpuRegistryRoot = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\";
      String[] processorIds = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\");
      if (processorIds.length > 0) {
         String cpuRegistryPath = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\" + processorIds[0];
         cpuVendor = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "VendorIdentifier");
         cpuName = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "ProcessorNameString");
         cpuIdentifier = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "Identifier");
      }

      if (!cpuIdentifier.isEmpty()) {
         cpuFamily = parseIdentifier(cpuIdentifier, "Family");
         cpuModel = parseIdentifier(cpuIdentifier, "Model");
         cpuStepping = parseIdentifier(cpuIdentifier, "Stepping");
      }

      WinBase.SYSTEM_INFO sysinfo = new WinBase.SYSTEM_INFO();
      Kernel32.INSTANCE.GetNativeSystemInfo(sysinfo);
      int processorArchitecture = sysinfo.processorArchitecture.pi.wProcessorArchitecture.intValue();
      if (processorArchitecture == 9 || processorArchitecture == 12 || processorArchitecture == 6) {
         cpu64bit = true;
      }

      WbemcliUtil.WmiResult<Win32Processor.ProcessorIdProperty> processorId = Win32Processor.queryProcessorId();
      String processorID;
      if (processorId.getResultCount() > 0) {
         processorID = WmiUtil.getString(processorId, Win32Processor.ProcessorIdProperty.PROCESSORID, 0);
      } else {
         processorID = createProcessorID(cpuStepping, cpuModel, cpuFamily, cpu64bit ? new String[]{"ia64"} : new String[0]);
      }

      return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
   }

   private static String parseIdentifier(String identifier, String key) {
      String[] idSplit = ParseUtil.whitespaces.split(identifier);
      boolean found = false;
      String[] var4 = idSplit;
      int var5 = idSplit.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         if (found) {
            return s;
         }

         found = s.equals(key);
      }

      return "";
   }

   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
      if (!VersionHelpers.IsWindows7OrGreater()) {
         return LogicalProcessorInformation.getLogicalProcessorInformation();
      } else {
         List<CentralProcessor.LogicalProcessor> logProcs = LogicalProcessorInformation.getLogicalProcessorInformationEx();
         this.numaNodeProcToLogicalProcMap = new HashMap();
         int lp = 0;
         Iterator var3 = logProcs.iterator();

         while(var3.hasNext()) {
            CentralProcessor.LogicalProcessor logProc = (CentralProcessor.LogicalProcessor)var3.next();
            this.numaNodeProcToLogicalProcMap.put(String.format("%d,%d", logProc.getNumaNode(), logProc.getProcessorNumber()), lp++);
         }

         return logProcs;
      }
   }

   public long[] querySystemCpuLoadTicks() {
      long[] ticks = new long[CentralProcessor.TickType.values().length];
      WinBase.FILETIME lpIdleTime = new WinBase.FILETIME();
      WinBase.FILETIME lpKernelTime = new WinBase.FILETIME();
      WinBase.FILETIME lpUserTime = new WinBase.FILETIME();
      if (!Kernel32.INSTANCE.GetSystemTimes(lpIdleTime, lpKernelTime, lpUserTime)) {
         LOG.error((String)"Failed to update system idle/kernel/user times. Error code: {}", (Object)Native.getLastError());
         return ticks;
      } else {
         Map<ProcessorInformation.SystemTickCountProperty, Long> valueMap = ProcessorInformation.querySystemCounters();
         ticks[CentralProcessor.TickType.IRQ.getIndex()] = (Long)valueMap.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTINTERRUPTTIME, 0L) / 10000L;
         ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] = (Long)valueMap.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTDPCTIME, 0L) / 10000L;
         ticks[CentralProcessor.TickType.IDLE.getIndex()] = lpIdleTime.toDWordLong().longValue() / 10000L;
         ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = lpKernelTime.toDWordLong().longValue() / 10000L - ticks[CentralProcessor.TickType.IDLE.getIndex()];
         ticks[CentralProcessor.TickType.USER.getIndex()] = lpUserTime.toDWordLong().longValue() / 10000L;
         int var10001 = CentralProcessor.TickType.SYSTEM.getIndex();
         ticks[var10001] -= ticks[CentralProcessor.TickType.IRQ.getIndex()] + ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
         return ticks;
      }
   }

   public long[] queryCurrentFreq() {
      if (VersionHelpers.IsWindows7OrGreater()) {
         Pair<List<String>, Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>>> instanceValuePair = ProcessorInformation.queryFrequencyCounters();
         List<String> instances = (List)instanceValuePair.getA();
         Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>> valueMap = (Map)instanceValuePair.getB();
         List<Long> percentMaxList = (List)valueMap.get(ProcessorInformation.ProcessorFrequencyProperty.PERCENTOFMAXIMUMFREQUENCY);
         if (!instances.isEmpty()) {
            long maxFreq = this.getMaxFreq();
            long[] freqs = new long[this.getLogicalProcessorCount()];

            for(int p = 0; p < instances.size(); ++p) {
               int cpu = ((String)instances.get(p)).contains(",") ? (Integer)this.numaNodeProcToLogicalProcMap.getOrDefault(instances.get(p), 0) : ParseUtil.parseIntOrDefault((String)instances.get(p), 0);
               if (cpu < this.getLogicalProcessorCount()) {
                  freqs[cpu] = (Long)percentMaxList.get(cpu) * maxFreq / 100L;
               }
            }

            return freqs;
         }
      }

      return this.queryNTPower(2);
   }

   public long queryMaxFreq() {
      long[] freqs = this.queryNTPower(1);
      return Arrays.stream(freqs).max().orElse(-1L);
   }

   private long[] queryNTPower(int fieldIndex) {
      PowrProf.ProcessorPowerInformation ppi = new PowrProf.ProcessorPowerInformation();
      long[] freqs = new long[this.getLogicalProcessorCount()];
      int bufferSize = ppi.size() * freqs.length;
      Memory mem = new Memory((long)bufferSize);
      if (0 != PowrProf.INSTANCE.CallNtPowerInformation(11, (Pointer)null, 0, mem, bufferSize)) {
         LOG.error("Unable to get Processor Information");
         Arrays.fill(freqs, -1L);
         return freqs;
      } else {
         for(int i = 0; i < freqs.length; ++i) {
            ppi = new PowrProf.ProcessorPowerInformation(mem.share((long)i * (long)ppi.size()));
            if (fieldIndex == 1) {
               freqs[i] = (long)ppi.maxMhz * 1000000L;
            } else if (fieldIndex == 2) {
               freqs[i] = (long)ppi.currentMhz * 1000000L;
            } else {
               freqs[i] = -1L;
            }
         }

         return freqs;
      }
   }

   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] average = new double[nelem];

         for(int i = 0; i < average.length; ++i) {
            average[i] = -1.0;
         }

         return average;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   public long[][] queryProcessorCpuLoadTicks() {
      Pair<List<String>, Map<ProcessorInformation.ProcessorTickCountProperty, List<Long>>> instanceValuePair = ProcessorInformation.queryProcessorCounters();
      List<String> instances = (List)instanceValuePair.getA();
      Map<ProcessorInformation.ProcessorTickCountProperty, List<Long>> valueMap = (Map)instanceValuePair.getB();
      List<Long> systemList = (List)valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPRIVILEGEDTIME);
      List<Long> userList = (List)valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTUSERTIME);
      List<Long> irqList = (List)valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTINTERRUPTTIME);
      List<Long> softIrqList = (List)valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTDPCTIME);
      List<Long> idleList = (List)valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPROCESSORTIME);
      long[][] ticks = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      if (!instances.isEmpty() && systemList != null && userList != null && irqList != null && softIrqList != null && idleList != null) {
         for(int p = 0; p < instances.size(); ++p) {
            int cpu = ((String)instances.get(p)).contains(",") ? (Integer)this.numaNodeProcToLogicalProcMap.getOrDefault(instances.get(p), 0) : ParseUtil.parseIntOrDefault((String)instances.get(p), 0);
            if (cpu < this.getLogicalProcessorCount()) {
               ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = (Long)systemList.get(cpu);
               ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = (Long)userList.get(cpu);
               ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = (Long)irqList.get(cpu);
               ticks[cpu][CentralProcessor.TickType.SOFTIRQ.getIndex()] = (Long)softIrqList.get(cpu);
               ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = (Long)idleList.get(cpu);
               long[] var10000 = ticks[cpu];
               int var10001 = CentralProcessor.TickType.SYSTEM.getIndex();
               var10000[var10001] -= ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] + ticks[cpu][CentralProcessor.TickType.SOFTIRQ.getIndex()];
               var10000 = ticks[cpu];
               var10001 = CentralProcessor.TickType.SYSTEM.getIndex();
               var10000[var10001] /= 10000L;
               var10000 = ticks[cpu];
               var10001 = CentralProcessor.TickType.USER.getIndex();
               var10000[var10001] /= 10000L;
               var10000 = ticks[cpu];
               var10001 = CentralProcessor.TickType.IRQ.getIndex();
               var10000[var10001] /= 10000L;
               var10000 = ticks[cpu];
               var10001 = CentralProcessor.TickType.SOFTIRQ.getIndex();
               var10000[var10001] /= 10000L;
               var10000 = ticks[cpu];
               var10001 = CentralProcessor.TickType.IDLE.getIndex();
               var10000[var10001] /= 10000L;
            }
         }

         return ticks;
      } else {
         return ticks;
      }
   }

   public long queryContextSwitches() {
      return (Long)SystemInformation.queryContextSwitchCounters().getOrDefault(SystemInformation.ContextSwitchProperty.CONTEXTSWITCHESPERSEC, 0L);
   }

   public long queryInterrupts() {
      return (Long)ProcessorInformation.queryInterruptCounters().getOrDefault(ProcessorInformation.InterruptsProperty.INTERRUPTSPERSEC, 0L);
   }
}
