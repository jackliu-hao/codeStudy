package oshi.software.os.mac;

import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.Who;
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
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
public class MacOperatingSystem extends AbstractOperatingSystem {
   private static final Logger LOG = LoggerFactory.getLogger(MacOperatingSystem.class);
   public static final String MACOS_VERSIONS_PROPERTIES = "oshi.macos.versions.properties";
   private static final String SYSTEM_LIBRARY_LAUNCH_AGENTS = "/System/Library/LaunchAgents";
   private static final String SYSTEM_LIBRARY_LAUNCH_DAEMONS = "/System/Library/LaunchDaemons";
   private int maxProc = 1024;
   private final String osXVersion = System.getProperty("os.version");
   private final int major;
   private final int minor;
   private static final long BOOTTIME;

   public MacOperatingSystem() {
      this.major = ParseUtil.getFirstIntValue(this.osXVersion);
      this.minor = ParseUtil.getNthIntValue(this.osXVersion, 2);
      this.maxProc = SysctlUtil.sysctl("kern.maxproc", 4096);
   }

   public String queryManufacturer() {
      return "Apple";
   }

   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String family = this.major == 10 && this.minor >= 12 ? "macOS" : System.getProperty("os.name");
      String codeName = this.parseCodeName();
      String buildNumber = SysctlUtil.sysctl("kern.osversion", "");
      return new AbstractOperatingSystem.FamilyVersionInfo(family, new OperatingSystem.OSVersionInfo(this.osXVersion, codeName, buildNumber));
   }

   private String parseCodeName() {
      if (this.major == 10) {
         Properties verProps = FileUtil.readPropertiesFromFilename("oshi.macos.versions.properties");
         return verProps.getProperty(this.major + "." + this.minor);
      } else {
         LOG.warn((String)"Unable to parse version {}.{} to a codename.", (Object)this.major, (Object)this.minor);
         return "";
      }
   }

   protected int queryBitness(int jvmBitness) {
      return jvmBitness != 64 && (this.major != 10 || this.minor <= 6) ? ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf LONG_BIT"), 32) : 64;
   }

   protected boolean queryElevated() {
      return System.getenv("SUDO_COMMAND") != null;
   }

   public FileSystem getFileSystem() {
      return new MacFileSystem();
   }

   public InternetProtocolStats getInternetProtocolStats() {
      return new MacInternetProtocolStats(this.isElevated());
   }

   public List<OSSession> getSessions() {
      return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
   }

   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = new ArrayList();
      int[] pids = new int[this.maxProc];
      int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;

      for(int i = 0; i < numberOfProcesses; ++i) {
         if (pids[i] > 0) {
            OSProcess proc = this.getProcess(pids[i]);
            if (proc != null) {
               procs.add(proc);
            }
         }
      }

      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   public OSProcess getProcess(int pid) {
      OSProcess proc = new MacOSProcess(pid, this.minor);
      return proc.getState().equals(OSProcess.State.INVALID) ? null : proc;
   }

   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = new ArrayList();
      int[] pids = new int[this.maxProc];
      int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;

      for(int i = 0; i < numberOfProcesses; ++i) {
         if (pids[i] != 0 && parentPid == getParentProcessPid(pids[i])) {
            OSProcess proc = this.getProcess(pids[i]);
            if (proc != null) {
               procs.add(proc);
            }
         }
      }

      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   private static int getParentProcessPid(int pid) {
      SystemB.ProcTaskAllInfo taskAllInfo = new SystemB.ProcTaskAllInfo();
      return 0 > SystemB.INSTANCE.proc_pidinfo(pid, 2, 0L, taskAllInfo, taskAllInfo.size()) ? 0 : taskAllInfo.pbsd.pbi_ppid;
   }

   public int getProcessId() {
      return SystemB.INSTANCE.getpid();
   }

   public int getProcessCount() {
      return SystemB.INSTANCE.proc_listpids(1, 0, (int[])null, 0) / SystemB.INT_SIZE;
   }

   public int getThreadCount() {
      int[] pids = new int[this.getProcessCount() + 10];
      int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length) / SystemB.INT_SIZE;
      int numberOfThreads = 0;
      SystemB.ProcTaskInfo taskInfo = new SystemB.ProcTaskInfo();

      for(int i = 0; i < numberOfProcesses; ++i) {
         int exit = SystemB.INSTANCE.proc_pidinfo(pids[i], 4, 0L, taskInfo, taskInfo.size());
         if (exit != -1) {
            numberOfThreads += taskInfo.pti_threadnum;
         }
      }

      return numberOfThreads;
   }

   public long getSystemUptime() {
      return System.currentTimeMillis() / 1000L - BOOTTIME;
   }

   public long getSystemBootTime() {
      return BOOTTIME;
   }

   public NetworkParams getNetworkParams() {
      return new MacNetworkParams();
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

      ArrayList<File> files = new ArrayList();
      File dir = new File("/System/Library/LaunchAgents");
      if (dir.exists() && dir.isDirectory()) {
         files.addAll(Arrays.asList(dir.listFiles((fx, namex) -> {
            return namex.toLowerCase().endsWith(".plist");
         })));
      } else {
         LOG.error("Directory: /System/Library/LaunchAgents does not exist");
      }

      dir = new File("/System/Library/LaunchDaemons");
      if (dir.exists() && dir.isDirectory()) {
         files.addAll(Arrays.asList(dir.listFiles((fx, namex) -> {
            return namex.toLowerCase().endsWith(".plist");
         })));
      } else {
         LOG.error("Directory: /System/Library/LaunchDaemons does not exist");
      }

      Iterator var13 = files.iterator();

      while(var13.hasNext()) {
         File f = (File)var13.next();
         String name = f.getName().substring(0, f.getName().length() - 6);
         int index = name.lastIndexOf(46);
         String shortName = index >= 0 && index <= name.length() - 2 ? name.substring(index + 1) : name;
         if (!running.contains(name) && !running.contains(shortName)) {
            OSService s = new OSService(name, 0, OSService.State.STOPPED);
            services.add(s);
         }
      }

      return (OSService[])services.toArray(new OSService[0]);
   }

   static {
      SystemB.Timeval tv = new SystemB.Timeval();
      if (SysctlUtil.sysctl("kern.boottime", (Structure)tv) && tv.tv_sec.longValue() != 0L) {
         BOOTTIME = tv.tv_sec.longValue();
      } else {
         BOOTTIME = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L);
      }

   }
}
