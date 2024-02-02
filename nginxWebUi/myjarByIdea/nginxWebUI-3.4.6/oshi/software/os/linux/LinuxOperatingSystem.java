package oshi.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.Who;
import oshi.driver.linux.proc.CpuStat;
import oshi.driver.linux.proc.ProcessStat;
import oshi.driver.linux.proc.UpTime;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Triplet;

@ThreadSafe
public class LinuxOperatingSystem extends AbstractOperatingSystem {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxOperatingSystem.class);
   private static final String OS_RELEASE_LOG = "os-release: {}";
   private static final String LSB_RELEASE_A_LOG = "lsb_release -a: {}";
   private static final String LSB_RELEASE_LOG = "lsb-release: {}";
   private static final String RELEASE_DELIM = " release ";
   private static final String DOUBLE_QUOTES = "(?:^\")|(?:\"$)";
   private static final String FILENAME_PROPERTIES = "oshi.linux.filename.properties";
   private static final long USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);
   static final long BOOTTIME;
   private static final int[] PPID_INDEX;

   public LinuxOperatingSystem() {
      super.getVersionInfo();
   }

   public String queryManufacturer() {
      return "GNU/Linux";
   }

   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      Triplet<String, String, String> familyVersionCodename = queryFamilyVersionCodenameFromReleaseFiles();
      String buildNumber = null;
      List<String> procVersion = FileUtil.readFile(ProcPath.VERSION);
      if (!procVersion.isEmpty()) {
         String[] split = ParseUtil.whitespaces.split((CharSequence)procVersion.get(0));
         String[] var5 = split;
         int var6 = split.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String s = var5[var7];
            if (!"Linux".equals(s) && !"version".equals(s)) {
               buildNumber = s;
               break;
            }
         }
      }

      OperatingSystem.OSVersionInfo versionInfo = new OperatingSystem.OSVersionInfo((String)familyVersionCodename.getB(), (String)familyVersionCodename.getC(), buildNumber);
      return new AbstractOperatingSystem.FamilyVersionInfo((String)familyVersionCodename.getA(), versionInfo);
   }

   protected int queryBitness(int jvmBitness) {
      return jvmBitness < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1 ? jvmBitness : 64;
   }

   protected boolean queryElevated() {
      return System.getenv("SUDO_COMMAND") != null;
   }

   public FileSystem getFileSystem() {
      return new LinuxFileSystem();
   }

   public InternetProtocolStats getInternetProtocolStats() {
      return new LinuxInternetProtocolStats();
   }

   public List<OSSession> getSessions() {
      return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
   }

   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = new ArrayList();
      File[] pids = ProcessStat.getPidFiles();
      File[] var5 = pids;
      int var6 = pids.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         File pidFile = var5[var7];
         int pid = ParseUtil.parseIntOrDefault(pidFile.getName(), 0);
         OSProcess proc = new LinuxOSProcess(pid);
         if (!proc.getState().equals(OSProcess.State.INVALID)) {
            procs.add(proc);
         }
      }

      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   public OSProcess getProcess(int pid) {
      OSProcess proc = new LinuxOSProcess(pid);
      return !proc.getState().equals(OSProcess.State.INVALID) ? proc : null;
   }

   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = new ArrayList();
      File[] procFiles = ProcessStat.getPidFiles();
      File[] var6 = procFiles;
      int var7 = procFiles.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         File procFile = var6[var8];
         int pid = ParseUtil.parseIntOrDefault(procFile.getName(), 0);
         if (parentPid == getParentPidFromProcFile(pid)) {
            OSProcess proc = new LinuxOSProcess(pid);
            if (!proc.getState().equals(OSProcess.State.INVALID)) {
               procs.add(proc);
            }
         }
      }

      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   private static int getParentPidFromProcFile(int pid) {
      String stat = FileUtil.getStringFromFile(String.format("/proc/%d/stat", pid));
      if (stat.isEmpty()) {
         return 0;
      } else {
         long[] statArray = ParseUtil.parseStringToLongArray(stat, PPID_INDEX, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
         return (int)statArray[0];
      }
   }

   public int getProcessId() {
      return LinuxLibc.INSTANCE.getpid();
   }

   public int getProcessCount() {
      return ProcessStat.getPidFiles().length;
   }

   public int getThreadCount() {
      try {
         LibC.Sysinfo info = new LibC.Sysinfo();
         if (0 != LibC.INSTANCE.sysinfo(info)) {
            LOG.error((String)"Failed to get process thread count. Error code: {}", (Object)Native.getLastError());
            return 0;
         } else {
            return info.procs;
         }
      } catch (NoClassDefFoundError | UnsatisfiedLinkError var2) {
         LOG.error((String)"Failed to get procs from sysinfo. {}", (Object)var2.getMessage());
         return 0;
      }
   }

   public long getSystemUptime() {
      return (long)UpTime.getSystemUptimeSeconds();
   }

   public long getSystemBootTime() {
      return BOOTTIME;
   }

   public NetworkParams getNetworkParams() {
      return new LinuxNetworkParams();
   }

   private static Triplet<String, String, String> queryFamilyVersionCodenameFromReleaseFiles() {
      Triplet familyVersionCodename;
      if ((familyVersionCodename = readDistribRelease("/etc/system-release")) != null) {
         return familyVersionCodename;
      } else if ((familyVersionCodename = readOsRelease()) != null) {
         return familyVersionCodename;
      } else if ((familyVersionCodename = execLsbRelease()) != null) {
         return familyVersionCodename;
      } else if ((familyVersionCodename = readLsbRelease()) != null) {
         return familyVersionCodename;
      } else {
         String etcDistribRelease = getReleaseFilename();
         if ((familyVersionCodename = readDistribRelease(etcDistribRelease)) != null) {
            return familyVersionCodename;
         } else {
            String family = filenameToFamily(etcDistribRelease.replace("/etc/", "").replace("release", "").replace("version", "").replace("-", "").replace("_", ""));
            return new Triplet(family, "unknown", "unknown");
         }
      }
   }

   private static Triplet<String, String, String> readOsRelease() {
      String family = null;
      String versionId = "unknown";
      String codeName = "unknown";
      List<String> osRelease = FileUtil.readFile("/etc/os-release");
      Iterator var4 = osRelease.iterator();

      while(true) {
         while(var4.hasNext()) {
            String line = (String)var4.next();
            if (line.startsWith("VERSION=")) {
               LOG.debug((String)"os-release: {}", (Object)line);
               line = line.replace("VERSION=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
               String[] split = line.split("[()]");
               if (split.length <= 1) {
                  split = line.split(", ");
               }

               if (split.length > 0) {
                  versionId = split[0].trim();
               }

               if (split.length > 1) {
                  codeName = split[1].trim();
               }
            } else if (line.startsWith("NAME=") && family == null) {
               LOG.debug((String)"os-release: {}", (Object)line);
               family = line.replace("NAME=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
            } else if (line.startsWith("VERSION_ID=") && versionId.equals("unknown")) {
               LOG.debug((String)"os-release: {}", (Object)line);
               versionId = line.replace("VERSION_ID=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
            }
         }

         return family == null ? null : new Triplet(family, versionId, codeName);
      }
   }

   private static Triplet<String, String, String> execLsbRelease() {
      String family = null;
      String versionId = "unknown";
      String codeName = "unknown";
      Iterator var3 = ExecutingCommand.runNative("lsb_release -a").iterator();

      while(true) {
         while(var3.hasNext()) {
            String line = (String)var3.next();
            if (line.startsWith("Description:")) {
               LOG.debug((String)"lsb_release -a: {}", (Object)line);
               line = line.replace("Description:", "").trim();
               if (line.contains(" release ")) {
                  Triplet<String, String, String> triplet = parseRelease(line, " release ");
                  family = (String)triplet.getA();
                  if (versionId.equals("unknown")) {
                     versionId = (String)triplet.getB();
                  }

                  if (codeName.equals("unknown")) {
                     codeName = (String)triplet.getC();
                  }
               }
            } else if (line.startsWith("Distributor ID:") && family == null) {
               LOG.debug((String)"lsb_release -a: {}", (Object)line);
               family = line.replace("Distributor ID:", "").trim();
            } else if (line.startsWith("Release:") && versionId.equals("unknown")) {
               LOG.debug((String)"lsb_release -a: {}", (Object)line);
               versionId = line.replace("Release:", "").trim();
            } else if (line.startsWith("Codename:") && codeName.equals("unknown")) {
               LOG.debug((String)"lsb_release -a: {}", (Object)line);
               codeName = line.replace("Codename:", "").trim();
            }
         }

         return family == null ? null : new Triplet(family, versionId, codeName);
      }
   }

   private static Triplet<String, String, String> readLsbRelease() {
      String family = null;
      String versionId = "unknown";
      String codeName = "unknown";
      List<String> osRelease = FileUtil.readFile("/etc/lsb-release");
      Iterator var4 = osRelease.iterator();

      while(true) {
         while(var4.hasNext()) {
            String line = (String)var4.next();
            if (line.startsWith("DISTRIB_DESCRIPTION=")) {
               LOG.debug((String)"lsb-release: {}", (Object)line);
               line = line.replace("DISTRIB_DESCRIPTION=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
               if (line.contains(" release ")) {
                  Triplet<String, String, String> triplet = parseRelease(line, " release ");
                  family = (String)triplet.getA();
                  if (versionId.equals("unknown")) {
                     versionId = (String)triplet.getB();
                  }

                  if (codeName.equals("unknown")) {
                     codeName = (String)triplet.getC();
                  }
               }
            } else if (line.startsWith("DISTRIB_ID=") && family == null) {
               LOG.debug((String)"lsb-release: {}", (Object)line);
               family = line.replace("DISTRIB_ID=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
            } else if (line.startsWith("DISTRIB_RELEASE=") && versionId.equals("unknown")) {
               LOG.debug((String)"lsb-release: {}", (Object)line);
               versionId = line.replace("DISTRIB_RELEASE=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
            } else if (line.startsWith("DISTRIB_CODENAME=") && codeName.equals("unknown")) {
               LOG.debug((String)"lsb-release: {}", (Object)line);
               codeName = line.replace("DISTRIB_CODENAME=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
            }
         }

         return family == null ? null : new Triplet(family, versionId, codeName);
      }
   }

   private static Triplet<String, String, String> readDistribRelease(String filename) {
      if ((new File(filename)).exists()) {
         List<String> osRelease = FileUtil.readFile(filename);
         Iterator var2 = osRelease.iterator();

         while(var2.hasNext()) {
            String line = (String)var2.next();
            LOG.debug((String)"{}: {}", (Object)filename, (Object)line);
            if (line.contains(" release ")) {
               return parseRelease(line, " release ");
            }

            if (line.contains(" VERSION ")) {
               return parseRelease(line, " VERSION ");
            }
         }
      }

      return null;
   }

   private static Triplet<String, String, String> parseRelease(String line, String splitLine) {
      String[] split = line.split(splitLine);
      String family = split[0].trim();
      String versionId = "unknown";
      String codeName = "unknown";
      if (split.length > 1) {
         split = split[1].split("[()]");
         if (split.length > 0) {
            versionId = split[0].trim();
         }

         if (split.length > 1) {
            codeName = split[1].trim();
         }
      }

      return new Triplet(family, versionId, codeName);
   }

   protected static String getReleaseFilename() {
      File etc = new File("/etc");
      File[] matchingFiles = etc.listFiles((f) -> {
         return (f.getName().endsWith("-release") || f.getName().endsWith("-version") || f.getName().endsWith("_release") || f.getName().endsWith("_version")) && !f.getName().endsWith("os-release") && !f.getName().endsWith("lsb-release") && !f.getName().endsWith("system-release");
      });
      if (matchingFiles != null && matchingFiles.length > 0) {
         return matchingFiles[0].getPath();
      } else {
         return (new File("/etc/release")).exists() ? "/etc/release" : "/etc/issue";
      }
   }

   private static String filenameToFamily(String name) {
      if (name.isEmpty()) {
         return "Solaris";
      } else if ("issue".equalsIgnoreCase(name)) {
         return "Unknown";
      } else {
         Properties filenameProps = FileUtil.readPropertiesFromFilename("oshi.linux.filename.properties");
         String family = filenameProps.getProperty(name.toLowerCase());
         return family != null ? family : name.substring(0, 1).toUpperCase() + name.substring(1);
      }
   }

   public OSService[] getServices() {
      List<OSService> services = new ArrayList();
      Set<String> running = new HashSet();
      Iterator var3 = this.getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID).iterator();

      while(var3.hasNext()) {
         OSProcess p = (OSProcess)var3.next();
         OSService s = new OSService(p.getName(), p.getProcessID(), OSService.State.RUNNING);
         services.add(s);
         running.add(p.getName());
      }

      boolean systemctlFound = false;
      List<String> systemctl = ExecutingCommand.runNative("systemctl list-unit-files");
      Iterator var16 = systemctl.iterator();

      while(true) {
         String[] split;
         String name;
         do {
            do {
               do {
                  if (!var16.hasNext()) {
                     if (!systemctlFound) {
                        File dir = new File("/etc/init");
                        if (dir.exists() && dir.isDirectory()) {
                           File[] var18 = dir.listFiles((fx, namex) -> {
                              return namex.toLowerCase().endsWith(".conf");
                           });
                           int var19 = var18.length;

                           for(int var20 = 0; var20 < var19; ++var20) {
                              File f = var18[var20];
                              name = f.getName().substring(0, f.getName().length() - 5);
                              int index = name.lastIndexOf(46);
                              String shortName = index >= 0 && index <= name.length() - 2 ? name.substring(index + 1) : name;
                              if (!running.contains(name) && !running.contains(shortName)) {
                                 OSService s = new OSService(name, 0, OSService.State.STOPPED);
                                 services.add(s);
                              }
                           }
                        } else {
                           LOG.error("Directory: /etc/init does not exist");
                        }
                     }

                     return (OSService[])services.toArray(new OSService[0]);
                  }

                  String str = (String)var16.next();
                  split = ParseUtil.whitespaces.split(str);
               } while(split.length != 2);
            } while(!split[0].endsWith(".service"));
         } while(!"enabled".equals(split[1]));

         String name = split[0].substring(0, split[0].length() - 8);
         int index = name.lastIndexOf(46);
         name = index >= 0 && index <= name.length() - 2 ? name.substring(index + 1) : name;
         if (!running.contains(name) && !running.contains(name)) {
            OSService s = new OSService(name, 0, OSService.State.STOPPED);
            services.add(s);
            systemctlFound = true;
         }
      }
   }

   public static long getHz() {
      return USER_HZ;
   }

   static {
      long tempBT = CpuStat.getBootTime();
      if (tempBT == 0L) {
         tempBT = System.currentTimeMillis() / 1000L - (long)UpTime.getSystemUptimeSeconds();
      }

      BOOTTIME = tempBT;
      PPID_INDEX = new int[]{3};
   }
}
