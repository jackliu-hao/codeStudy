package oshi.hardware.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@ThreadSafe
public abstract class AbstractCentralProcessor implements CentralProcessor {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractCentralProcessor.class);
   private final Supplier<CentralProcessor.ProcessorIdentifier> cpuid = Memoizer.memoize(this::queryProcessorId);
   private final Supplier<Long> maxFreq = Memoizer.memoize(this::queryMaxFreq, Memoizer.defaultExpiration());
   private final Supplier<long[]> currentFreq = Memoizer.memoize(this::queryCurrentFreq, Memoizer.defaultExpiration());
   private final Supplier<Long> contextSwitches = Memoizer.memoize(this::queryContextSwitches, Memoizer.defaultExpiration());
   private final Supplier<Long> interrupts = Memoizer.memoize(this::queryInterrupts, Memoizer.defaultExpiration());
   private final Supplier<long[]> systemCpuLoadTicks = Memoizer.memoize(this::querySystemCpuLoadTicks, Memoizer.defaultExpiration());
   private final Supplier<long[][]> processorCpuLoadTicks = Memoizer.memoize(this::queryProcessorCpuLoadTicks, Memoizer.defaultExpiration());
   private final int physicalPackageCount;
   private final int physicalProcessorCount;
   private final int logicalProcessorCount;
   private final List<CentralProcessor.LogicalProcessor> logicalProcessors = Collections.unmodifiableList(this.initProcessorCounts());

   protected AbstractCentralProcessor() {
      Set<String> physProcPkgs = new HashSet();
      Set<Integer> physPkgs = new HashSet();
      Iterator var3 = this.logicalProcessors.iterator();

      while(var3.hasNext()) {
         CentralProcessor.LogicalProcessor logProc = (CentralProcessor.LogicalProcessor)var3.next();
         int pkg = logProc.getPhysicalPackageNumber();
         physProcPkgs.add(logProc.getPhysicalProcessorNumber() + ":" + pkg);
         physPkgs.add(pkg);
      }

      this.logicalProcessorCount = this.logicalProcessors.size();
      this.physicalProcessorCount = physProcPkgs.size();
      this.physicalPackageCount = physPkgs.size();
   }

   protected abstract List<CentralProcessor.LogicalProcessor> initProcessorCounts();

   protected abstract CentralProcessor.ProcessorIdentifier queryProcessorId();

   public CentralProcessor.ProcessorIdentifier getProcessorIdentifier() {
      return (CentralProcessor.ProcessorIdentifier)this.cpuid.get();
   }

   public long getMaxFreq() {
      return (Long)this.maxFreq.get();
   }

   protected abstract long queryMaxFreq();

   public long[] getCurrentFreq() {
      return (long[])this.currentFreq.get();
   }

   protected abstract long[] queryCurrentFreq();

   public long getContextSwitches() {
      return (Long)this.contextSwitches.get();
   }

   protected abstract long queryContextSwitches();

   public long getInterrupts() {
      return (Long)this.interrupts.get();
   }

   protected abstract long queryInterrupts();

   public List<CentralProcessor.LogicalProcessor> getLogicalProcessors() {
      return this.logicalProcessors;
   }

   public long[] getSystemCpuLoadTicks() {
      return (long[])this.systemCpuLoadTicks.get();
   }

   protected abstract long[] querySystemCpuLoadTicks();

   public long[][] getProcessorCpuLoadTicks() {
      return (long[][])this.processorCpuLoadTicks.get();
   }

   protected abstract long[][] queryProcessorCpuLoadTicks();

   public double getSystemCpuLoadBetweenTicks(long[] oldTicks) {
      if (oldTicks.length != CentralProcessor.TickType.values().length) {
         throw new IllegalArgumentException("Tick array " + oldTicks.length + " should have " + CentralProcessor.TickType.values().length + " elements");
      } else {
         long[] ticks = this.getSystemCpuLoadTicks();
         long total = 0L;

         for(int i = 0; i < ticks.length; ++i) {
            total += ticks[i] - oldTicks[i];
         }

         long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] + ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
         LOG.trace((String)"Total ticks: {}  Idle ticks: {}", (Object)total, (Object)idle);
         return total > 0L && idle >= 0L ? (double)(total - idle) / (double)total : 0.0;
      }
   }

   public double[] getProcessorCpuLoadBetweenTicks(long[][] oldTicks) {
      if (oldTicks.length == this.logicalProcessorCount && oldTicks[0].length == CentralProcessor.TickType.values().length) {
         long[][] ticks = this.getProcessorCpuLoadTicks();
         double[] load = new double[this.logicalProcessorCount];

         for(int cpu = 0; cpu < this.logicalProcessorCount; ++cpu) {
            long total = 0L;

            for(int i = 0; i < ticks[cpu].length; ++i) {
               total += ticks[cpu][i] - oldTicks[cpu][i];
            }

            long idle = ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] + ticks[cpu][CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[cpu][CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[cpu][CentralProcessor.TickType.IOWAIT.getIndex()];
            LOG.trace("CPU: {}  Total ticks: {}  Idle ticks: {}", cpu, total, idle);
            load[cpu] = total > 0L && idle >= 0L ? (double)(total - idle) / (double)total : 0.0;
         }

         return load;
      } else {
         throw new IllegalArgumentException("Tick array " + oldTicks.length + " should have " + this.logicalProcessorCount + " arrays, each of which has " + CentralProcessor.TickType.values().length + " elements");
      }
   }

   public int getLogicalProcessorCount() {
      return this.logicalProcessorCount;
   }

   public int getPhysicalProcessorCount() {
      return this.physicalProcessorCount;
   }

   public int getPhysicalPackageCount() {
      return this.physicalPackageCount;
   }

   protected static String createProcessorID(String stepping, String model, String family, String[] flags) {
      long processorIdBytes = 0L;
      long steppingL = ParseUtil.parseLongOrDefault(stepping, 0L);
      long modelL = ParseUtil.parseLongOrDefault(model, 0L);
      long familyL = ParseUtil.parseLongOrDefault(family, 0L);
      processorIdBytes |= steppingL & 15L;
      processorIdBytes |= (modelL & 15L) << 4;
      processorIdBytes |= (modelL & 240L) << 16;
      processorIdBytes |= (familyL & 15L) << 8;
      processorIdBytes |= (familyL & 240L) << 20;
      String[] var12 = flags;
      int var13 = flags.length;

      for(int var14 = 0; var14 < var13; ++var14) {
         switch (var12[var14]) {
            case "fpu":
               processorIdBytes |= 4294967296L;
               break;
            case "vme":
               processorIdBytes |= 8589934592L;
               break;
            case "de":
               processorIdBytes |= 17179869184L;
               break;
            case "pse":
               processorIdBytes |= 34359738368L;
               break;
            case "tsc":
               processorIdBytes |= 68719476736L;
               break;
            case "msr":
               processorIdBytes |= 137438953472L;
               break;
            case "pae":
               processorIdBytes |= 274877906944L;
               break;
            case "mce":
               processorIdBytes |= 549755813888L;
               break;
            case "cx8":
               processorIdBytes |= 1099511627776L;
               break;
            case "apic":
               processorIdBytes |= 2199023255552L;
               break;
            case "sep":
               processorIdBytes |= 8796093022208L;
               break;
            case "mtrr":
               processorIdBytes |= 17592186044416L;
               break;
            case "pge":
               processorIdBytes |= 35184372088832L;
               break;
            case "mca":
               processorIdBytes |= 70368744177664L;
               break;
            case "cmov":
               processorIdBytes |= 140737488355328L;
               break;
            case "pat":
               processorIdBytes |= 281474976710656L;
               break;
            case "pse-36":
               processorIdBytes |= 562949953421312L;
               break;
            case "psn":
               processorIdBytes |= 1125899906842624L;
               break;
            case "clfsh":
               processorIdBytes |= 2251799813685248L;
               break;
            case "ds":
               processorIdBytes |= 9007199254740992L;
               break;
            case "acpi":
               processorIdBytes |= 18014398509481984L;
               break;
            case "mmx":
               processorIdBytes |= 36028797018963968L;
               break;
            case "fxsr":
               processorIdBytes |= 72057594037927936L;
               break;
            case "sse":
               processorIdBytes |= 144115188075855872L;
               break;
            case "sse2":
               processorIdBytes |= 288230376151711744L;
               break;
            case "ss":
               processorIdBytes |= 576460752303423488L;
               break;
            case "htt":
               processorIdBytes |= 1152921504606846976L;
               break;
            case "tm":
               processorIdBytes |= 2305843009213693952L;
               break;
            case "ia64":
               processorIdBytes |= 4611686018427387904L;
               break;
            case "pbe":
               processorIdBytes |= Long.MIN_VALUE;
         }
      }

      return String.format("%016X", processorIdBytes);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(this.getProcessorIdentifier().getName());
      sb.append("\n ").append(this.getPhysicalPackageCount()).append(" physical CPU package(s)");
      sb.append("\n ").append(this.getPhysicalProcessorCount()).append(" physical CPU core(s)");
      sb.append("\n ").append(this.getLogicalProcessorCount()).append(" logical CPU(s)");
      sb.append('\n').append("Identifier: ").append(this.getProcessorIdentifier().getIdentifier());
      sb.append('\n').append("ProcessorID: ").append(this.getProcessorIdentifier().getProcessorID());
      sb.append('\n').append("Microarchitecture: ").append(this.getProcessorIdentifier().getMicroarchitecture());
      return sb.toString();
   }
}
