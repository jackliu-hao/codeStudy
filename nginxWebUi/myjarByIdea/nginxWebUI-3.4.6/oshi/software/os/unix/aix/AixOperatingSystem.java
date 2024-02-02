package oshi.software.os.unix.aix;

import com.sun.jna.Native;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Who;
import oshi.driver.unix.aix.perfstat.PerfstatConfig;
import oshi.driver.unix.aix.perfstat.PerfstatProcess;
import oshi.jna.platform.unix.aix.AixLibc;
import oshi.jna.platform.unix.aix.Perfstat;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Pair;

@ThreadSafe
public class AixOperatingSystem extends AbstractOperatingSystem {
   private final Supplier<Perfstat.perfstat_partition_config_t> config = Memoizer.memoize(PerfstatConfig::queryConfig);
   Supplier<Perfstat.perfstat_process_t[]> procCpu = Memoizer.memoize(PerfstatProcess::queryProcesses, Memoizer.defaultExpiration());
   private static final long BOOTTIME = querySystemBootTime();

   public String queryManufacturer() {
      return "IBM";
   }

   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
      Perfstat.perfstat_partition_config_t cfg = (Perfstat.perfstat_partition_config_t)this.config.get();
      String systemName = System.getProperty("os.name");
      String archName = System.getProperty("os.arch");
      String versionNumber = System.getProperty("os.version");
      if (Util.isBlank(versionNumber)) {
         versionNumber = ExecutingCommand.getFirstAnswer("oslevel");
      }

      String releaseNumber = Native.toString(cfg.OSBuild);
      if (Util.isBlank(releaseNumber)) {
         releaseNumber = ExecutingCommand.getFirstAnswer("oslevel -s");
      } else {
         int idx = releaseNumber.lastIndexOf(32);
         if (idx > 0 && idx < releaseNumber.length()) {
            releaseNumber = releaseNumber.substring(idx + 1);
         }
      }

      return new AbstractOperatingSystem.FamilyVersionInfo(systemName, new OperatingSystem.OSVersionInfo(versionNumber, archName, releaseNumber));
   }

   protected int queryBitness(int jvmBitness) {
      if (jvmBitness == 64) {
         return 64;
      } else {
         return (((Perfstat.perfstat_partition_config_t)this.config.get()).conf & 8388608) > 0 ? 64 : 32;
      }
   }

   protected boolean queryElevated() {
      return 0 == ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("id -u"), -1);
   }

   public FileSystem getFileSystem() {
      return new AixFileSystem();
   }

   public InternetProtocolStats getInternetProtocolStats() {
      return new AixInternetProtocolStats();
   }

   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> procs = this.getProcessListFromPS("ps -A -o st,pid,ppid,user,uid,group,gid,thcount,pri,vsize,rssize,etime,time,comm,pagein,args", -1);
      List<OSProcess> sorted = this.processSort(procs, limit, sort);
      return Collections.unmodifiableList(sorted);
   }

   public OSProcess getProcess(int pid) {
      List<OSProcess> procs = this.getProcessListFromPS("ps -o st,pid,ppid,user,uid,group,gid,thcount,pri,vsize,rssize,etime,time,comm,pagein,args -p ", pid);
      return procs.isEmpty() ? null : (OSProcess)procs.get(0);
   }

   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
      List<OSProcess> allProcs = this.getProcesses(limit, sort);
      return allProcs.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList((List)allProcs.stream().filter((proc) -> {
         return parentPid == proc.getParentProcessID();
      }).collect(Collectors.toList()));
   }

   private List<OSProcess> getProcessListFromPS(String psCommand, int pid) {
      Perfstat.perfstat_process_t[] perfstat = (Perfstat.perfstat_process_t[])this.procCpu.get();
      List<String> procList = ExecutingCommand.runNative(psCommand + (pid < 0 ? "" : pid));
      if (!procList.isEmpty() && procList.size() >= 2) {
         Map<Integer, Pair<Long, Long>> cpuMap = new HashMap();
         Perfstat.perfstat_process_t[] var6 = perfstat;
         int var7 = perfstat.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Perfstat.perfstat_process_t stat = var6[var8];
            cpuMap.put((int)stat.pid, new Pair((long)stat.ucpu_time, (long)stat.scpu_time));
         }

         procList.remove(0);
         List<OSProcess> procs = new ArrayList();
         Iterator var11 = procList.iterator();

         while(var11.hasNext()) {
            String proc = (String)var11.next();
            String[] split = ParseUtil.whitespaces.split(proc.trim(), 16);
            if (split.length == 16) {
               procs.add(new AixOSProcess(pid < 0 ? ParseUtil.parseIntOrDefault(split[1], 0) : pid, split, cpuMap, this.procCpu));
            }
         }

         return procs;
      } else {
         return Collections.emptyList();
      }
   }

   public int getProcessId() {
      return AixLibc.INSTANCE.getpid();
   }

   public int getProcessCount() {
      return ((Perfstat.perfstat_process_t[])this.procCpu.get()).length;
   }

   public int getThreadCount() {
      long tc = 0L;
      Perfstat.perfstat_process_t[] var3 = (Perfstat.perfstat_process_t[])this.procCpu.get();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Perfstat.perfstat_process_t proc = var3[var5];
         tc += proc.num_threads;
      }

      return (int)tc;
   }

   public long getSystemUptime() {
      return System.currentTimeMillis() / 1000L - BOOTTIME;
   }

   public long getSystemBootTime() {
      return BOOTTIME;
   }

   private static long querySystemBootTime() {
      return Who.queryBootTime() / 1000L;
   }

   public NetworkParams getNetworkParams() {
      return new AixNetworkParams();
   }

   public OSService[] getServices() {
      List<OSService> services = new ArrayList();
      List<String> systemServicesInfoList = ExecutingCommand.runNative("lssrc -a");
      if (systemServicesInfoList.size() > 1) {
         systemServicesInfoList.remove(0);
         Iterator var3 = systemServicesInfoList.iterator();

         while(var3.hasNext()) {
            String systemService = (String)var3.next();
            String[] serviceSplit = ParseUtil.whitespaces.split(systemService.trim());
            if (systemService.contains("active")) {
               if (serviceSplit.length == 4) {
                  services.add(new OSService(serviceSplit[0], ParseUtil.parseIntOrDefault(serviceSplit[2], 0), OSService.State.RUNNING));
               } else if (serviceSplit.length == 3) {
                  services.add(new OSService(serviceSplit[0], ParseUtil.parseIntOrDefault(serviceSplit[1], 0), OSService.State.RUNNING));
               }
            } else if (systemService.contains("inoperative")) {
               services.add(new OSService(serviceSplit[0], 0, OSService.State.STOPPED));
            }
         }
      }

      File dir = new File("/etc/rc.d/init.d");
      File[] listFiles;
      if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
         File[] var12 = listFiles;
         int var6 = listFiles.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File file = var12[var7];
            String installedService = ExecutingCommand.getFirstAnswer(file.getAbsolutePath() + " status");
            if (installedService.contains("running")) {
               services.add(new OSService(file.getName(), ParseUtil.parseLastInt(installedService, 0), OSService.State.RUNNING));
            } else {
               services.add(new OSService(file.getName(), 0, OSService.State.STOPPED));
            }
         }
      }

      return (OSService[])services.toArray(new OSService[0]);
   }
}
