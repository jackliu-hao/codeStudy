package oshi.software.common;

import com.sun.jna.Platform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import oshi.driver.unix.Who;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;

public abstract class AbstractOperatingSystem implements OperatingSystem {
   public static final String OSHI_OS_UNIX_WHOCOMMAND = "oshi.os.unix.whoCommand";
   protected static final boolean USE_WHO_COMMAND = GlobalConfig.get("oshi.os.unix.whoCommand", false);
   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
   private final Supplier<FamilyVersionInfo> familyVersionInfo = Memoizer.memoize(this::queryFamilyVersionInfo);
   private final Supplier<Integer> bitness = Memoizer.memoize(this::queryPlatformBitness);
   private final Supplier<Boolean> elevated = Memoizer.memoize(this::queryElevated);
   private static final Comparator<OSProcess> CPU_DESC_SORT = Comparator.comparingDouble(OSProcess::getProcessCpuLoadCumulative).reversed();
   private static final Comparator<OSProcess> RSS_DESC_SORT = Comparator.comparingLong(OSProcess::getResidentSetSize).reversed();
   private static final Comparator<OSProcess> UPTIME_ASC_SORT = Comparator.comparingLong(OSProcess::getUpTime);
   private static final Comparator<OSProcess> UPTIME_DESC_SORT;
   private static final Comparator<OSProcess> PID_ASC_SORT;
   private static final Comparator<OSProcess> PARENTPID_ASC_SORT;
   private static final Comparator<OSProcess> NAME_ASC_SORT;

   public String getManufacturer() {
      return (String)this.manufacturer.get();
   }

   protected abstract String queryManufacturer();

   public String getFamily() {
      return ((FamilyVersionInfo)this.familyVersionInfo.get()).family;
   }

   public OperatingSystem.OSVersionInfo getVersionInfo() {
      return ((FamilyVersionInfo)this.familyVersionInfo.get()).versionInfo;
   }

   protected abstract FamilyVersionInfo queryFamilyVersionInfo();

   public int getBitness() {
      return (Integer)this.bitness.get();
   }

   private int queryPlatformBitness() {
      if (Platform.is64Bit()) {
         return 64;
      } else {
         int jvmBitness = System.getProperty("os.arch").indexOf("64") != -1 ? 64 : 32;
         return this.queryBitness(jvmBitness);
      }
   }

   protected abstract int queryBitness(int var1);

   public boolean isElevated() {
      return (Boolean)this.elevated.get();
   }

   public OSService[] getServices() {
      return new OSService[0];
   }

   protected abstract boolean queryElevated();

   protected List<OSProcess> processSort(List<OSProcess> processes, int limit, OperatingSystem.ProcessSort sort) {
      if (sort != null) {
         switch (sort) {
            case CPU:
               processes.sort(CPU_DESC_SORT);
               break;
            case MEMORY:
               processes.sort(RSS_DESC_SORT);
               break;
            case OLDEST:
               processes.sort(UPTIME_DESC_SORT);
               break;
            case NEWEST:
               processes.sort(UPTIME_ASC_SORT);
               break;
            case PID:
               processes.sort(PID_ASC_SORT);
               break;
            case PARENTPID:
               processes.sort(PARENTPID_ASC_SORT);
               break;
            case NAME:
               processes.sort(NAME_ASC_SORT);
               break;
            default:
               throw new IllegalArgumentException("Unimplemented enum type: " + sort.toString());
         }
      }

      int maxProcs = processes.size();
      if (limit > 0 && maxProcs > limit) {
         maxProcs = limit;
         ArrayList procs = new ArrayList();

         for(int i = 0; i < maxProcs; ++i) {
            procs.add((OSProcess)processes.get(i));
         }

         return procs;
      } else {
         return processes;
      }
   }

   public List<OSSession> getSessions() {
      return Collections.unmodifiableList(Who.queryWho());
   }

   public List<OSProcess> getProcesses() {
      return this.getProcesses(0, (OperatingSystem.ProcessSort)null);
   }

   public List<OSProcess> getProcesses(Collection<Integer> pids) {
      List<OSProcess> returnValue = new ArrayList(pids.size());
      Iterator var3 = pids.iterator();

      while(var3.hasNext()) {
         Integer pid = (Integer)var3.next();
         OSProcess process = this.getProcess(pid);
         if (process != null) {
            returnValue.add(process);
         }
      }

      return Collections.unmodifiableList(returnValue);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.getManufacturer()).append(' ').append(this.getFamily()).append(' ').append(this.getVersionInfo());
      return sb.toString();
   }

   static {
      UPTIME_DESC_SORT = UPTIME_ASC_SORT.reversed();
      PID_ASC_SORT = Comparator.comparingInt(OSProcess::getProcessID);
      PARENTPID_ASC_SORT = Comparator.comparingInt(OSProcess::getParentProcessID);
      NAME_ASC_SORT = Comparator.comparing(OSProcess::getName, String.CASE_INSENSITIVE_ORDER);
   }

   protected static final class FamilyVersionInfo {
      private final String family;
      private final OperatingSystem.OSVersionInfo versionInfo;

      public FamilyVersionInfo(String family, OperatingSystem.OSVersionInfo versionInfo) {
         this.family = family;
         this.versionInfo = versionInfo;
      }
   }
}
