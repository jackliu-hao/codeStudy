package oshi.software.os;

import java.util.Collection;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Util;

@ThreadSafe
public interface OperatingSystem {
   String getFamily();

   String getManufacturer();

   OSVersionInfo getVersionInfo();

   FileSystem getFileSystem();

   InternetProtocolStats getInternetProtocolStats();

   List<OSSession> getSessions();

   List<OSProcess> getProcesses();

   List<OSProcess> getProcesses(int var1, ProcessSort var2);

   List<OSProcess> getProcesses(Collection<Integer> var1);

   OSProcess getProcess(int var1);

   List<OSProcess> getChildProcesses(int var1, int var2, ProcessSort var3);

   int getProcessId();

   int getProcessCount();

   int getThreadCount();

   int getBitness();

   long getSystemUptime();

   long getSystemBootTime();

   boolean isElevated();

   NetworkParams getNetworkParams();

   OSService[] getServices();

   @Immutable
   public static class OSVersionInfo {
      private final String version;
      private final String codeName;
      private final String buildNumber;
      private final String versionStr;

      public OSVersionInfo(String version, String codeName, String buildNumber) {
         this.version = version;
         this.codeName = codeName;
         this.buildNumber = buildNumber;
         StringBuilder sb = new StringBuilder(this.getVersion() != null ? this.getVersion() : "unknown");
         if (!Util.isBlank(this.getCodeName())) {
            sb.append(" (").append(this.getCodeName()).append(')');
         }

         if (!Util.isBlank(this.getBuildNumber())) {
            sb.append(" build ").append(this.getBuildNumber());
         }

         this.versionStr = sb.toString();
      }

      public String getVersion() {
         return this.version;
      }

      public String getCodeName() {
         return this.codeName;
      }

      public String getBuildNumber() {
         return this.buildNumber;
      }

      public String toString() {
         return this.versionStr;
      }
   }

   public static enum ProcessSort {
      CPU,
      MEMORY,
      OLDEST,
      NEWEST,
      PID,
      PARENTPID,
      NAME;
   }
}
