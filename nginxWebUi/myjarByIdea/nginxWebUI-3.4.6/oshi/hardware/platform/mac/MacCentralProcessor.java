package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
final class MacCentralProcessor extends AbstractCentralProcessor {
   private static final Logger LOG = LoggerFactory.getLogger(MacCentralProcessor.class);

   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
      String cpuVendor = SysctlUtil.sysctl("machdep.cpu.vendor", "");
      String cpuName = SysctlUtil.sysctl("machdep.cpu.brand_string", "");
      int i = SysctlUtil.sysctl("machdep.cpu.stepping", -1);
      String cpuStepping = i < 0 ? "" : Integer.toString(i);
      i = SysctlUtil.sysctl("machdep.cpu.model", -1);
      String cpuModel = i < 0 ? "" : Integer.toString(i);
      i = SysctlUtil.sysctl("machdep.cpu.family", -1);
      String cpuFamily = i < 0 ? "" : Integer.toString(i);
      long processorIdBits = 0L;
      processorIdBits |= (long)SysctlUtil.sysctl("machdep.cpu.signature", 0);
      processorIdBits |= (SysctlUtil.sysctl("machdep.cpu.feature_bits", 0L) & -1L) << 32;
      String processorID = String.format("%016X", processorIdBits);
      boolean cpu64bit = SysctlUtil.sysctl("hw.cpu64bit_capable", 0) != 0;
      return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
   }

   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
      int logicalProcessorCount = SysctlUtil.sysctl("hw.logicalcpu", 1);
      int physicalProcessorCount = SysctlUtil.sysctl("hw.physicalcpu", 1);
      int physicalPackageCount = SysctlUtil.sysctl("hw.packages", 1);
      List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList(logicalProcessorCount);

      for(int i = 0; i < logicalProcessorCount; ++i) {
         logProcs.add(new CentralProcessor.LogicalProcessor(i, i * physicalProcessorCount / logicalProcessorCount, i * physicalPackageCount / logicalProcessorCount));
      }

      return logProcs;
   }

   public long[] querySystemCpuLoadTicks() {
      long[] ticks = new long[CentralProcessor.TickType.values().length];
      int machPort = SystemB.INSTANCE.mach_host_self();
      SystemB.HostCpuLoadInfo cpuLoadInfo = new SystemB.HostCpuLoadInfo();
      if (0 != SystemB.INSTANCE.host_statistics(machPort, 3, cpuLoadInfo, new IntByReference(cpuLoadInfo.size()))) {
         LOG.error((String)"Failed to get System CPU ticks. Error code: {} ", (Object)Native.getLastError());
         return ticks;
      } else {
         ticks[CentralProcessor.TickType.USER.getIndex()] = (long)cpuLoadInfo.cpu_ticks[0];
         ticks[CentralProcessor.TickType.NICE.getIndex()] = (long)cpuLoadInfo.cpu_ticks[3];
         ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = (long)cpuLoadInfo.cpu_ticks[1];
         ticks[CentralProcessor.TickType.IDLE.getIndex()] = (long)cpuLoadInfo.cpu_ticks[2];
         return ticks;
      }
   }

   public long[] queryCurrentFreq() {
      long[] freqs = new long[this.getLogicalProcessorCount()];
      Arrays.fill(freqs, SysctlUtil.sysctl("hw.cpufrequency", -1L));
      return freqs;
   }

   public long queryMaxFreq() {
      return SysctlUtil.sysctl("hw.cpufrequency_max", -1L);
   }

   public double[] getSystemLoadAverage(int nelem) {
      if (nelem >= 1 && nelem <= 3) {
         double[] average = new double[nelem];
         int retval = SystemB.INSTANCE.getloadavg(average, nelem);
         if (retval < nelem) {
            Arrays.fill(average, -1.0);
         }

         return average;
      } else {
         throw new IllegalArgumentException("Must include from one to three elements.");
      }
   }

   public long[][] queryProcessorCpuLoadTicks() {
      long[][] ticks = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
      int machPort = SystemB.INSTANCE.mach_host_self();
      IntByReference procCount = new IntByReference();
      PointerByReference procCpuLoadInfo = new PointerByReference();
      IntByReference procInfoCount = new IntByReference();
      if (0 != SystemB.INSTANCE.host_processor_info(machPort, 2, procCount, procCpuLoadInfo, procInfoCount)) {
         LOG.error((String)"Failed to update CPU Load. Error code: {}", (Object)Native.getLastError());
         return ticks;
      } else {
         int[] cpuTicks = procCpuLoadInfo.getValue().getIntArray(0L, procInfoCount.getValue());

         for(int cpu = 0; cpu < procCount.getValue(); ++cpu) {
            int offset = cpu * 4;
            ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = FormatUtil.getUnsignedInt(cpuTicks[offset + 0]);
            ticks[cpu][CentralProcessor.TickType.NICE.getIndex()] = FormatUtil.getUnsignedInt(cpuTicks[offset + 3]);
            ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = FormatUtil.getUnsignedInt(cpuTicks[offset + 1]);
            ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = FormatUtil.getUnsignedInt(cpuTicks[offset + 2]);
         }

         return ticks;
      }
   }

   public long queryContextSwitches() {
      int machPort = SystemB.INSTANCE.mach_host_self();
      SystemB.VMMeter vmstats = new SystemB.VMMeter();
      if (0 != SystemB.INSTANCE.host_statistics(machPort, 2, vmstats, new IntByReference(vmstats.size()))) {
         LOG.error((String)"Failed to update vmstats. Error code: {}", (Object)Native.getLastError());
         return -1L;
      } else {
         return ParseUtil.unsignedIntToLong(vmstats.v_swtch);
      }
   }

   public long queryInterrupts() {
      int machPort = SystemB.INSTANCE.mach_host_self();
      SystemB.VMMeter vmstats = new SystemB.VMMeter();
      if (0 != SystemB.INSTANCE.host_statistics(machPort, 2, vmstats, new IntByReference(vmstats.size()))) {
         LOG.error((String)"Failed to update vmstats. Error code: {}", (Object)Native.getLastError());
         return -1L;
      } else {
         return ParseUtil.unsignedIntToLong(vmstats.v_intr);
      }
   }
}
