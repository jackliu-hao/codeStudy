package oshi.software.os.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.driver.unix.solaris.Who;
import oshi.jna.platform.unix.solaris.SolarisLibc;
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
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public class SolarisOperatingSystem extends AbstractOperatingSystem {
   private static final long BOOTTIME = querySystemBootTime();

   public String queryManufacturer() {
      return "Oracle";
   }

   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      String[] split = ParseUtil.whitespaces.split(ExecutingCommand.getFirstAnswer("uname -rv"));
      String version = split[0];
      String buildNumber = null;
      if (split.length > 1) {
         buildNumber = split[1];
      }

      return new AbstractOperatingSystem.FamilyVersionInfo("SunOS", new OperatingSystem.OSVersionInfo(version, "Solaris", buildNumber));
   }

   protected int queryBitness(int jvmBitness) {
      return jvmBitness == 64 ? 64 : ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("isainfo -b"), 32);
   }

   protected boolean queryElevated() {
      return System.getenv("SUDO_COMMAND") != null;
   }

   public FileSystem getFileSystem() {
      return new SolarisFileSystem();
   }

   public InternetProtocolStats getInternetProtocolStats() {
      return new SolarisInternetProtocolStats();
   }

   public List<OSSession> getSessions() {
      return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
   }

   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = getProcessListFromPS("ps -eo s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args", -1);
      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   public OSProcess getProcess(int pid) {
      List<OSProcess> procs = getProcessListFromPS("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p ", pid);
      return procs.isEmpty() ? null : (OSProcess)procs.get(0);
   }

   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
      List<String> childPids = ExecutingCommand.runNative("pgrep -P " + parentPid);
      if (childPids.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<OSProcess> procs = getProcessListFromPS("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p " + String.join(",", childPids), -1);
         List<OSProcess> sorted = this.processSort(procs, limit, sort);
         return Collections.unmodifiableList(sorted);
      }
   }

   private static List<OSProcess> getProcessListFromPS(String psCommand, int pid) {
      List<OSProcess> procs = new ArrayList();
      List<String> procList = ExecutingCommand.runNative(psCommand + (pid < 0 ? "" : pid));
      if (!procList.isEmpty() && procList.size() >= 2) {
         procList.remove(0);
         Iterator var4 = procList.iterator();

         while(var4.hasNext()) {
            String proc = (String)var4.next();
            String[] split = ParseUtil.whitespaces.split(proc.trim(), 15);
            if (split.length == 15) {
               procs.add(new SolarisOSProcess(pid < 0 ? ParseUtil.parseIntOrDefault(split[1], 0) : pid, split));
            }
         }

         return procs;
      } else {
         return procs;
      }
   }

   public int getProcessId() {
      return SolarisLibc.INSTANCE.getpid();
   }

   public int getProcessCount() {
      return ProcessStat.getPidFiles().length;
   }

   public int getThreadCount() {
      List<String> threadList = ExecutingCommand.runNative("ps -eLo pid");
      return !threadList.isEmpty() ? threadList.size() - 1 : this.getProcessCount();
   }

   public long getSystemUptime() {
      return querySystemUptime();
   }

   private static long querySystemUptime() {
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      long var2;
      label40: {
         try {
            LibKstat.Kstat ksp = kc.lookup("unix", 0, "system_misc");
            if (ksp != null) {
               var2 = ksp.ks_snaptime / 1000000000L;
               break label40;
            }
         } catch (Throwable var5) {
            if (kc != null) {
               try {
                  kc.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (kc != null) {
            kc.close();
         }

         return 0L;
      }

      if (kc != null) {
         kc.close();
      }

      return var2;
   }

   public long getSystemBootTime() {
      return BOOTTIME;
   }

   private static long querySystemBootTime() {
      KstatUtil.KstatChain kc = KstatUtil.openChain();

      long var2;
      label42: {
         try {
            LibKstat.Kstat ksp = kc.lookup("unix", 0, "system_misc");
            if (ksp != null && kc.read(ksp)) {
               var2 = KstatUtil.dataLookupLong(ksp, "boot_time");
               break label42;
            }
         } catch (Throwable var5) {
            if (kc != null) {
               try {
                  kc.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (kc != null) {
            kc.close();
         }

         return System.currentTimeMillis() / 1000L - querySystemUptime();
      }

      if (kc != null) {
         kc.close();
      }

      return var2;
   }

   public NetworkParams getNetworkParams() {
      return new SolarisNetworkParams();
   }

   public OSService[] getServices() {
      List<OSService> services = new ArrayList();
      List<String> legacySvcs = new ArrayList();
      File dir = new File("/etc/init.d");
      File[] listFiles;
      if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
         File[] var5 = listFiles;
         int var6 = listFiles.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File f = var5[var7];
            legacySvcs.add(f.getName());
         }
      }

      List<String> svcs = ExecutingCommand.runNative("svcs -p");
      Iterator var11 = svcs.iterator();

      while(true) {
         while(var11.hasNext()) {
            String line = (String)var11.next();
            String svc;
            if (line.startsWith("online")) {
               int delim = line.lastIndexOf(":/");
               if (delim > 0) {
                  svc = line.substring(delim + 1);
                  if (svc.endsWith(":default")) {
                     svc = svc.substring(0, svc.length() - 8);
                  }

                  services.add(new OSService(svc, 0, OSService.State.STOPPED));
               }
            } else if (line.startsWith(" ")) {
               String[] split = ParseUtil.whitespaces.split(line.trim());
               if (split.length == 3) {
                  services.add(new OSService(split[2], ParseUtil.parseIntOrDefault(split[1], 0), OSService.State.RUNNING));
               }
            } else if (line.startsWith("legacy_run")) {
               Iterator var13 = legacySvcs.iterator();

               while(var13.hasNext()) {
                  svc = (String)var13.next();
                  if (line.endsWith(svc)) {
                     services.add(new OSService(svc, 0, OSService.State.STOPPED));
                     break;
                  }
               }
            }
         }

         return (OSService[])services.toArray(new OSService[0]);
      }
   }
}
