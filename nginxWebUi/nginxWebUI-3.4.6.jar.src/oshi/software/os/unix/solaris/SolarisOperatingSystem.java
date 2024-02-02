/*     */ package oshi.software.os.unix.solaris;
/*     */ 
/*     */ import com.sun.jna.platform.unix.solaris.LibKstat;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.linux.proc.ProcessStat;
/*     */ import oshi.driver.unix.solaris.Who;
/*     */ import oshi.jna.platform.unix.solaris.SolarisLibc;
/*     */ import oshi.software.common.AbstractOperatingSystem;
/*     */ import oshi.software.os.FileSystem;
/*     */ import oshi.software.os.InternetProtocolStats;
/*     */ import oshi.software.os.NetworkParams;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSService;
/*     */ import oshi.software.os.OSSession;
/*     */ import oshi.software.os.OperatingSystem;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.unix.solaris.KstatUtil;
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
/*     */ public class SolarisOperatingSystem
/*     */   extends AbstractOperatingSystem
/*     */ {
/*  60 */   private static final long BOOTTIME = querySystemBootTime();
/*     */ 
/*     */   
/*     */   public String queryManufacturer() {
/*  64 */     return "Oracle";
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
/*  69 */     String[] split = ParseUtil.whitespaces.split(ExecutingCommand.getFirstAnswer("uname -rv"));
/*  70 */     String version = split[0];
/*  71 */     String buildNumber = null;
/*  72 */     if (split.length > 1) {
/*  73 */       buildNumber = split[1];
/*     */     }
/*  75 */     return new AbstractOperatingSystem.FamilyVersionInfo("SunOS", new OperatingSystem.OSVersionInfo(version, "Solaris", buildNumber));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int queryBitness(int jvmBitness) {
/*  80 */     if (jvmBitness == 64) {
/*  81 */       return 64;
/*     */     }
/*  83 */     return ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("isainfo -b"), 32);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean queryElevated() {
/*  88 */     return (System.getenv("SUDO_COMMAND") != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileSystem getFileSystem() {
/*  93 */     return (FileSystem)new SolarisFileSystem();
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats getInternetProtocolStats() {
/*  98 */     return new SolarisInternetProtocolStats();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSSession> getSessions() {
/* 103 */     return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
/* 108 */     List<OSProcess> procs = getProcessListFromPS("ps -eo s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args", -1);
/*     */     
/* 110 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 111 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess getProcess(int pid) {
/* 116 */     List<OSProcess> procs = getProcessListFromPS("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p ", pid);
/*     */     
/* 118 */     if (procs.isEmpty()) {
/* 119 */       return null;
/*     */     }
/* 121 */     return procs.get(0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
/* 127 */     List<String> childPids = ExecutingCommand.runNative("pgrep -P " + parentPid);
/* 128 */     if (childPids.isEmpty()) {
/* 129 */       return Collections.emptyList();
/*     */     }
/* 131 */     List<OSProcess> procs = getProcessListFromPS("ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p " + 
/*     */         
/* 133 */         String.join(",", (Iterable)childPids), -1);
/*     */     
/* 135 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 136 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */   
/*     */   private static List<OSProcess> getProcessListFromPS(String psCommand, int pid) {
/* 140 */     List<OSProcess> procs = new ArrayList<>();
/* 141 */     List<String> procList = ExecutingCommand.runNative(psCommand + ((pid < 0) ? "" : (String)Integer.valueOf(pid)));
/* 142 */     if (procList.isEmpty() || procList.size() < 2) {
/* 143 */       return procs;
/*     */     }
/*     */     
/* 146 */     procList.remove(0);
/*     */     
/* 148 */     for (String proc : procList) {
/* 149 */       String[] split = ParseUtil.whitespaces.split(proc.trim(), 15);
/*     */       
/* 151 */       if (split.length == 15) {
/* 152 */         procs.add(new SolarisOSProcess((pid < 0) ? ParseUtil.parseIntOrDefault(split[1], 0) : pid, split));
/*     */       }
/*     */     } 
/* 155 */     return procs;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessId() {
/* 160 */     return SolarisLibc.INSTANCE.getpid();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessCount() {
/* 165 */     return (ProcessStat.getPidFiles()).length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 170 */     List<String> threadList = ExecutingCommand.runNative("ps -eLo pid");
/* 171 */     if (!threadList.isEmpty())
/*     */     {
/* 173 */       return threadList.size() - 1;
/*     */     }
/* 175 */     return getProcessCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemUptime() {
/* 180 */     return querySystemUptime();
/*     */   }
/*     */   
/*     */   private static long querySystemUptime() {
/* 184 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 185 */     try { LibKstat.Kstat ksp = kc.lookup("unix", 0, "system_misc");
/* 186 */       if (ksp != null)
/*     */       
/* 188 */       { long l = ksp.ks_snaptime / 1000000000L;
/*     */         
/* 190 */         if (kc != null) kc.close();  return l; }  if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 191 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemBootTime() {
/* 196 */     return BOOTTIME;
/*     */   }
/*     */   
/*     */   private static long querySystemBootTime() {
/* 200 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 201 */     try { LibKstat.Kstat ksp = kc.lookup("unix", 0, "system_misc");
/* 202 */       if (ksp != null && kc.read(ksp))
/* 203 */       { long l = KstatUtil.dataLookupLong(ksp, "boot_time");
/*     */         
/* 205 */         if (kc != null) kc.close();  return l; }  if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 206 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return System.currentTimeMillis() / 1000L - querySystemUptime();
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkParams getNetworkParams() {
/* 211 */     return (NetworkParams)new SolarisNetworkParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public OSService[] getServices() {
/* 216 */     List<OSService> services = new ArrayList<>();
/*     */     
/* 218 */     List<String> legacySvcs = new ArrayList<>();
/* 219 */     File dir = new File("/etc/init.d");
/*     */     File[] listFiles;
/* 221 */     if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
/* 222 */       for (File f : listFiles) {
/* 223 */         legacySvcs.add(f.getName());
/*     */       }
/*     */     }
/*     */     
/* 227 */     List<String> svcs = ExecutingCommand.runNative("svcs -p");
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
/* 239 */     for (String line : svcs) {
/* 240 */       if (line.startsWith("online")) {
/* 241 */         int delim = line.lastIndexOf(":/");
/* 242 */         if (delim > 0) {
/* 243 */           String name = line.substring(delim + 1);
/* 244 */           if (name.endsWith(":default")) {
/* 245 */             name = name.substring(0, name.length() - 8);
/*     */           }
/* 247 */           services.add(new OSService(name, 0, OSService.State.STOPPED));
/*     */         }  continue;
/* 249 */       }  if (line.startsWith(" ")) {
/* 250 */         String[] split = ParseUtil.whitespaces.split(line.trim());
/* 251 */         if (split.length == 3)
/* 252 */           services.add(new OSService(split[2], ParseUtil.parseIntOrDefault(split[1], 0), OSService.State.RUNNING));  continue;
/*     */       } 
/* 254 */       if (line.startsWith("legacy_run")) {
/* 255 */         for (String svc : legacySvcs) {
/* 256 */           if (line.endsWith(svc)) {
/* 257 */             services.add(new OSService(svc, 0, OSService.State.STOPPED));
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 263 */     return services.<OSService>toArray(new OSService[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\solaris\SolarisOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */