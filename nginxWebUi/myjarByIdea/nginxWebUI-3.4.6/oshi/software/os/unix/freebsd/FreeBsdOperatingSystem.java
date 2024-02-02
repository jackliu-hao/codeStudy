package oshi.software.os.unix.freebsd;

import com.sun.jna.Structure;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.freebsd.Who;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;

@ThreadSafe
public class FreeBsdOperatingSystem extends AbstractOperatingSystem {
   private static final Logger LOG = LoggerFactory.getLogger(FreeBsdOperatingSystem.class);
   private static final long BOOTTIME = querySystemBootTime();

   public String queryManufacturer() {
      return "Unix/BSD";
   }

   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String family = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
      String version = BsdSysctlUtil.sysctl("kern.osrelease", "");
      String versionInfo = BsdSysctlUtil.sysctl("kern.version", "");
      String buildNumber = versionInfo.split(":")[0].replace(family, "").replace(version, "").trim();
      return new AbstractOperatingSystem.FamilyVersionInfo(family, new OperatingSystem.OSVersionInfo(version, (String)null, buildNumber));
   }

   protected int queryBitness(int jvmBitness) {
      return jvmBitness < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1 ? jvmBitness : 64;
   }

   protected boolean queryElevated() {
      return System.getenv("SUDO_COMMAND") != null;
   }

   public FileSystem getFileSystem() {
      return new FreeBsdFileSystem();
   }

   public InternetProtocolStats getInternetProtocolStats() {
      return new FreeBsdInternetProtocolStats();
   }

   public List<OSSession> getSessions() {
      return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
   }

   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = getProcessListFromPS(-1);
      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   public OSProcess getProcess(int pid) {
      List<OSProcess> procs = getProcessListFromPS(pid);
      return procs.isEmpty() ? null : (OSProcess)procs.get(0);
   }

   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = new ArrayList();
      Iterator var5 = this.getProcesses(0, (OperatingSystem.ProcessSort)null).iterator();

      while(var5.hasNext()) {
         OSProcess p = (OSProcess)var5.next();
         if (p.getParentProcessID() == parentPid) {
            procs.add(p);
         }
      }

      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   private static List<OSProcess> getProcessListFromPS(int pid) {
      List<OSProcess> procs = new ArrayList();
      String psCommand = "ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args";
      if (pid >= 0) {
         psCommand = psCommand + " -p " + pid;
      }

      List<String> procList = ExecutingCommand.runNative(psCommand);
      if (!procList.isEmpty() && procList.size() >= 2) {
         procList.remove(0);
         Iterator var4 = procList.iterator();

         while(var4.hasNext()) {
            String proc = (String)var4.next();
            String[] split = ParseUtil.whitespaces.split(proc.trim(), 16);
            if (split.length == 16) {
               procs.add(new FreeBsdOSProcess(pid < 0 ? ParseUtil.parseIntOrDefault(split[1], 0) : pid, split));
            }
         }

         return procs;
      } else {
         return procs;
      }
   }

   public int getProcessId() {
      return FreeBsdLibc.INSTANCE.getpid();
   }

   public int getProcessCount() {
      List<String> procList = ExecutingCommand.runNative("ps -axo pid");
      return !procList.isEmpty() ? procList.size() - 1 : 0;
   }

   public int getThreadCount() {
      int threads = 0;

      String proc;
      for(Iterator var2 = ExecutingCommand.runNative("ps -axo nlwp").iterator(); var2.hasNext(); threads += ParseUtil.parseIntOrDefault(proc.trim(), 0)) {
         proc = (String)var2.next();
      }

      return threads;
   }

   public long getSystemUptime() {
      return System.currentTimeMillis() / 1000L - BOOTTIME;
   }

   public long getSystemBootTime() {
      return BOOTTIME;
   }

   private static long querySystemBootTime() {
      FreeBsdLibc.Timeval tv = new FreeBsdLibc.Timeval();
      return BsdSysctlUtil.sysctl("kern.boottime", (Structure)tv) && tv.tv_sec != 0L ? tv.tv_sec : ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L);
   }

   public NetworkParams getNetworkParams() {
      return new FreeBsdNetworkParams();
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

      File dir = new File("/etc/rc.d");
      File[] listFiles;
      if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
         File[] var13 = listFiles;
         int var6 = listFiles.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File f = var13[var7];
            String name = f.getName();
            if (!running.contains(name)) {
               OSService s = new OSService(name, 0, OSService.State.STOPPED);
               services.add(s);
            }
         }
      } else {
         LOG.error("Directory: /etc/init does not exist");
      }

      return (OSService[])services.toArray(new OSService[0]);
   }
}
