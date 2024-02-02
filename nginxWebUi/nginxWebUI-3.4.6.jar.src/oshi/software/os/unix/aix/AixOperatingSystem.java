/*     */ package oshi.software.os.unix.aix;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.aix.Who;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatConfig;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatProcess;
/*     */ import oshi.jna.platform.unix.aix.AixLibc;
/*     */ import oshi.jna.platform.unix.aix.Perfstat;
/*     */ import oshi.software.common.AbstractOperatingSystem;
/*     */ import oshi.software.os.FileSystem;
/*     */ import oshi.software.os.InternetProtocolStats;
/*     */ import oshi.software.os.NetworkParams;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSService;
/*     */ import oshi.software.os.OperatingSystem;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.Util;
/*     */ import oshi.util.tuples.Pair;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class AixOperatingSystem
/*     */   extends AbstractOperatingSystem
/*     */ {
/*  68 */   private final Supplier<Perfstat.perfstat_partition_config_t> config = Memoizer.memoize(PerfstatConfig::queryConfig);
/*  69 */   Supplier<Perfstat.perfstat_process_t[]> procCpu = Memoizer.memoize(PerfstatProcess::queryProcesses, Memoizer.defaultExpiration());
/*     */   
/*  71 */   private static final long BOOTTIME = querySystemBootTime();
/*     */ 
/*     */   
/*     */   public String queryManufacturer() {
/*  75 */     return "IBM";
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
/*  80 */     Perfstat.perfstat_partition_config_t cfg = this.config.get();
/*     */     
/*  82 */     String systemName = System.getProperty("os.name");
/*  83 */     String archName = System.getProperty("os.arch");
/*  84 */     String versionNumber = System.getProperty("os.version");
/*  85 */     if (Util.isBlank(versionNumber)) {
/*  86 */       versionNumber = ExecutingCommand.getFirstAnswer("oslevel");
/*     */     }
/*  88 */     String releaseNumber = Native.toString(cfg.OSBuild);
/*  89 */     if (Util.isBlank(releaseNumber)) {
/*  90 */       releaseNumber = ExecutingCommand.getFirstAnswer("oslevel -s");
/*     */     } else {
/*     */       
/*  93 */       int idx = releaseNumber.lastIndexOf(' ');
/*  94 */       if (idx > 0 && idx < releaseNumber.length()) {
/*  95 */         releaseNumber = releaseNumber.substring(idx + 1);
/*     */       }
/*     */     } 
/*  98 */     return new AbstractOperatingSystem.FamilyVersionInfo(systemName, new OperatingSystem.OSVersionInfo(versionNumber, archName, releaseNumber));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int queryBitness(int jvmBitness) {
/* 103 */     if (jvmBitness == 64) {
/* 104 */       return 64;
/*     */     }
/*     */     
/* 107 */     return ((((Perfstat.perfstat_partition_config_t)this.config.get()).conf & 0x800000) > 0) ? 64 : 32;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean queryElevated() {
/* 112 */     return (0 == ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("id -u"), -1));
/*     */   }
/*     */ 
/*     */   
/*     */   public FileSystem getFileSystem() {
/* 117 */     return (FileSystem)new AixFileSystem();
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats getInternetProtocolStats() {
/* 122 */     return new AixInternetProtocolStats();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
/* 127 */     List<OSProcess> procs = getProcessListFromPS("ps -A -o st,pid,ppid,user,uid,group,gid,thcount,pri,vsize,rssize,etime,time,comm,pagein,args", -1);
/*     */     
/* 129 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 130 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess getProcess(int pid) {
/* 135 */     List<OSProcess> procs = getProcessListFromPS("ps -o st,pid,ppid,user,uid,group,gid,thcount,pri,vsize,rssize,etime,time,comm,pagein,args -p ", pid);
/*     */     
/* 137 */     if (procs.isEmpty()) {
/* 138 */       return null;
/*     */     }
/* 140 */     return procs.get(0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
/* 146 */     List<OSProcess> allProcs = getProcesses(limit, sort);
/*     */     
/* 148 */     return allProcs.isEmpty() ? Collections.<OSProcess>emptyList() : 
/* 149 */       Collections.<OSProcess>unmodifiableList((List<? extends OSProcess>)allProcs.stream().filter(proc -> (parentPid == proc.getParentProcessID()))
/* 150 */         .collect(Collectors.toList()));
/*     */   }
/*     */   
/*     */   private List<OSProcess> getProcessListFromPS(String psCommand, int pid) {
/* 154 */     Perfstat.perfstat_process_t[] perfstat = this.procCpu.get();
/* 155 */     List<String> procList = ExecutingCommand.runNative(psCommand + ((pid < 0) ? "" : (String)Integer.valueOf(pid)));
/* 156 */     if (procList.isEmpty() || procList.size() < 2) {
/* 157 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 160 */     Map<Integer, Pair<Long, Long>> cpuMap = new HashMap<>();
/* 161 */     for (Perfstat.perfstat_process_t stat : perfstat) {
/* 162 */       cpuMap.put(Integer.valueOf((int)stat.pid), new Pair(Long.valueOf((long)stat.ucpu_time), Long.valueOf((long)stat.scpu_time)));
/*     */     }
/*     */     
/* 165 */     procList.remove(0);
/*     */     
/* 167 */     List<OSProcess> procs = new ArrayList<>();
/* 168 */     for (String proc : procList) {
/* 169 */       String[] split = ParseUtil.whitespaces.split(proc.trim(), 16);
/*     */       
/* 171 */       if (split.length == 16) {
/* 172 */         procs.add(new AixOSProcess((pid < 0) ? ParseUtil.parseIntOrDefault(split[1], 0) : pid, split, cpuMap, this.procCpu));
/*     */       }
/*     */     } 
/*     */     
/* 176 */     return procs;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessId() {
/* 181 */     return AixLibc.INSTANCE.getpid();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessCount() {
/* 186 */     return ((Perfstat.perfstat_process_t[])this.procCpu.get()).length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 191 */     long tc = 0L;
/* 192 */     for (Perfstat.perfstat_process_t proc : (Perfstat.perfstat_process_t[])this.procCpu.get()) {
/* 193 */       tc += proc.num_threads;
/*     */     }
/* 195 */     return (int)tc;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemUptime() {
/* 200 */     return System.currentTimeMillis() / 1000L - BOOTTIME;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemBootTime() {
/* 205 */     return BOOTTIME;
/*     */   }
/*     */   
/*     */   private static long querySystemBootTime() {
/* 209 */     return Who.queryBootTime() / 1000L;
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkParams getNetworkParams() {
/* 214 */     return (NetworkParams)new AixNetworkParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public OSService[] getServices() {
/* 219 */     List<OSService> services = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     List<String> systemServicesInfoList = ExecutingCommand.runNative("lssrc -a");
/* 235 */     if (systemServicesInfoList.size() > 1) {
/* 236 */       systemServicesInfoList.remove(0);
/* 237 */       for (String systemService : systemServicesInfoList) {
/* 238 */         String[] serviceSplit = ParseUtil.whitespaces.split(systemService.trim());
/* 239 */         if (systemService.contains("active")) {
/* 240 */           if (serviceSplit.length == 4) {
/* 241 */             services.add(new OSService(serviceSplit[0], ParseUtil.parseIntOrDefault(serviceSplit[2], 0), OSService.State.RUNNING)); continue;
/*     */           } 
/* 243 */           if (serviceSplit.length == 3)
/* 244 */             services.add(new OSService(serviceSplit[0], ParseUtil.parseIntOrDefault(serviceSplit[1], 0), OSService.State.RUNNING)); 
/*     */           continue;
/*     */         } 
/* 247 */         if (systemService.contains("inoperative")) {
/* 248 */           services.add(new OSService(serviceSplit[0], 0, OSService.State.STOPPED));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 253 */     File dir = new File("/etc/rc.d/init.d");
/*     */     File[] listFiles;
/* 255 */     if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
/* 256 */       for (File file : listFiles) {
/* 257 */         String installedService = ExecutingCommand.getFirstAnswer(file.getAbsolutePath() + " status");
/*     */         
/* 259 */         if (installedService.contains("running")) {
/* 260 */           services.add(new OSService(file.getName(), ParseUtil.parseLastInt(installedService, 0), OSService.State.RUNNING));
/*     */         } else {
/* 262 */           services.add(new OSService(file.getName(), 0, OSService.State.STOPPED));
/*     */         } 
/*     */       } 
/*     */     }
/* 266 */     return services.<OSService>toArray(new OSService[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\aix\AixOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */