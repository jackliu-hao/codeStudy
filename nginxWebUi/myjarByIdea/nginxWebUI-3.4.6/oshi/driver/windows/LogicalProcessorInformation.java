package oshi.driver.windows;

import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinNT;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;

@ThreadSafe
public final class LogicalProcessorInformation {
   private LogicalProcessorInformation() {
   }

   public static List<CentralProcessor.LogicalProcessor> getLogicalProcessorInformationEx() {
      WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] procInfo = Kernel32Util.getLogicalProcessorInformationEx(65535);
      List<WinNT.GROUP_AFFINITY[]> packages = new ArrayList();
      List<WinNT.NUMA_NODE_RELATIONSHIP> numaNodes = new ArrayList();
      List<WinNT.GROUP_AFFINITY> cores = new ArrayList();

      for(int i = 0; i < procInfo.length; ++i) {
         switch (procInfo[i].relationship) {
            case 0:
               cores.add(((WinNT.PROCESSOR_RELATIONSHIP)procInfo[i]).groupMask[0]);
               break;
            case 1:
               numaNodes.add((WinNT.NUMA_NODE_RELATIONSHIP)procInfo[i]);
            case 2:
            default:
               break;
            case 3:
               packages.add(((WinNT.PROCESSOR_RELATIONSHIP)procInfo[i]).groupMask);
         }
      }

      cores.sort(Comparator.comparing((c) -> {
         return (long)c.group * 64L + c.mask.longValue();
      }));
      packages.sort(Comparator.comparing((p) -> {
         return (long)p[0].group * 64L + p[0].mask.longValue();
      }));
      List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList();
      Iterator var5 = cores.iterator();

      while(var5.hasNext()) {
         WinNT.GROUP_AFFINITY coreMask = (WinNT.GROUP_AFFINITY)var5.next();
         int group = coreMask.group;
         long mask = coreMask.mask.longValue();
         int lowBit = Long.numberOfTrailingZeros(mask);
         int hiBit = 63 - Long.numberOfLeadingZeros(mask);

         for(int lp = lowBit; lp <= hiBit; ++lp) {
            if ((mask & 1L << lp) > 0L) {
               CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(lp, getMatchingCore(cores, group, lp), getMatchingPackage(packages, group, lp), getMatchingNumaNode(numaNodes, group, lp), group);
               logProcs.add(logProc);
            }
         }
      }

      logProcs.sort(Comparator.comparing(CentralProcessor.LogicalProcessor::getNumaNode).thenComparing(CentralProcessor.LogicalProcessor::getProcessorNumber));
      return logProcs;
   }

   private static int getMatchingPackage(List<WinNT.GROUP_AFFINITY[]> packages, int g, int lp) {
      for(int i = 0; i < packages.size(); ++i) {
         for(int j = 0; j < ((WinNT.GROUP_AFFINITY[])packages.get(i)).length; ++j) {
            if ((((WinNT.GROUP_AFFINITY[])packages.get(i))[j].mask.longValue() & 1L << lp) > 0L && ((WinNT.GROUP_AFFINITY[])packages.get(i))[j].group == g) {
               return i;
            }
         }
      }

      return 0;
   }

   private static int getMatchingNumaNode(List<WinNT.NUMA_NODE_RELATIONSHIP> numaNodes, int g, int lp) {
      for(int j = 0; j < numaNodes.size(); ++j) {
         if ((((WinNT.NUMA_NODE_RELATIONSHIP)numaNodes.get(j)).groupMask.mask.longValue() & 1L << lp) > 0L && ((WinNT.NUMA_NODE_RELATIONSHIP)numaNodes.get(j)).groupMask.group == g) {
            return ((WinNT.NUMA_NODE_RELATIONSHIP)numaNodes.get(j)).nodeNumber;
         }
      }

      return 0;
   }

   private static int getMatchingCore(List<WinNT.GROUP_AFFINITY> cores, int g, int lp) {
      for(int j = 0; j < cores.size(); ++j) {
         if ((((WinNT.GROUP_AFFINITY)cores.get(j)).mask.longValue() & 1L << lp) > 0L && ((WinNT.GROUP_AFFINITY)cores.get(j)).group == g) {
            return j;
         }
      }

      return 0;
   }

   public static List<CentralProcessor.LogicalProcessor> getLogicalProcessorInformation() {
      List<Long> packageMaskList = new ArrayList();
      List<Long> coreMaskList = new ArrayList();
      WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] processors = Kernel32Util.getLogicalProcessorInformation();
      WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] var3 = processors;
      int core = processors.length;

      for(int var5 = 0; var5 < core; ++var5) {
         WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION proc = var3[var5];
         if (proc.relationship == 3) {
            packageMaskList.add(proc.processorMask.longValue());
         } else if (proc.relationship == 0) {
            coreMaskList.add(proc.processorMask.longValue());
         }
      }

      coreMaskList.sort((Comparator)null);
      packageMaskList.sort((Comparator)null);
      List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList();

      for(core = 0; core < coreMaskList.size(); ++core) {
         long coreMask = (Long)coreMaskList.get(core);
         int lowBit = Long.numberOfTrailingZeros(coreMask);
         int hiBit = 63 - Long.numberOfLeadingZeros(coreMask);

         for(int i = lowBit; i <= hiBit; ++i) {
            if ((coreMask & 1L << i) > 0L) {
               CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(i, core, getBitMatchingPackageNumber(packageMaskList, i));
               logProcs.add(logProc);
            }
         }
      }

      return logProcs;
   }

   private static int getBitMatchingPackageNumber(List<Long> packageMaskList, int logProc) {
      for(int i = 0; i < packageMaskList.size(); ++i) {
         if (((Long)packageMaskList.get(i) & 1L << logProc) > 0L) {
            return i;
         }
      }

      return 0;
   }
}
