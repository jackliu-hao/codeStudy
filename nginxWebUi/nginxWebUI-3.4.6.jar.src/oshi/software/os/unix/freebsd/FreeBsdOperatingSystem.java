/*     */ package oshi.software.os.unix.freebsd;
/*     */ 
/*     */ import com.sun.jna.Structure;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.freebsd.Who;
/*     */ import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
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
/*     */ import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
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
/*     */ public class FreeBsdOperatingSystem
/*     */   extends AbstractOperatingSystem
/*     */ {
/*  64 */   private static final Logger LOG = LoggerFactory.getLogger(FreeBsdOperatingSystem.class);
/*     */   
/*  66 */   private static final long BOOTTIME = querySystemBootTime();
/*     */ 
/*     */   
/*     */   public String queryManufacturer() {
/*  70 */     return "Unix/BSD";
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
/*  75 */     String family = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
/*     */     
/*  77 */     String version = BsdSysctlUtil.sysctl("kern.osrelease", "");
/*  78 */     String versionInfo = BsdSysctlUtil.sysctl("kern.version", "");
/*  79 */     String buildNumber = versionInfo.split(":")[0].replace(family, "").replace(version, "").trim();
/*     */     
/*  81 */     return new AbstractOperatingSystem.FamilyVersionInfo(family, new OperatingSystem.OSVersionInfo(version, null, buildNumber));
/*     */   }
/*     */ 
/*     */   
/*     */   protected int queryBitness(int jvmBitness) {
/*  86 */     if (jvmBitness < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1) {
/*  87 */       return jvmBitness;
/*     */     }
/*  89 */     return 64;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean queryElevated() {
/*  94 */     return (System.getenv("SUDO_COMMAND") != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileSystem getFileSystem() {
/*  99 */     return (FileSystem)new FreeBsdFileSystem();
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats getInternetProtocolStats() {
/* 104 */     return new FreeBsdInternetProtocolStats();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSSession> getSessions() {
/* 109 */     return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
/* 114 */     List<OSProcess> procs = getProcessListFromPS(-1);
/* 115 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 116 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess getProcess(int pid) {
/* 121 */     List<OSProcess> procs = getProcessListFromPS(pid);
/* 122 */     if (procs.isEmpty()) {
/* 123 */       return null;
/*     */     }
/* 125 */     return procs.get(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
/* 130 */     List<OSProcess> procs = new ArrayList<>();
/* 131 */     for (OSProcess p : getProcesses(0, (OperatingSystem.ProcessSort)null)) {
/* 132 */       if (p.getParentProcessID() == parentPid) {
/* 133 */         procs.add(p);
/*     */       }
/*     */     } 
/* 136 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 137 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */   
/*     */   private static List<OSProcess> getProcessListFromPS(int pid) {
/* 141 */     List<OSProcess> procs = new ArrayList<>();
/* 142 */     String psCommand = "ps -awwxo state,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etimes,systime,time,comm,args";
/* 143 */     if (pid >= 0) {
/* 144 */       psCommand = psCommand + " -p " + pid;
/*     */     }
/* 146 */     List<String> procList = ExecutingCommand.runNative(psCommand);
/* 147 */     if (procList.isEmpty() || procList.size() < 2) {
/* 148 */       return procs;
/*     */     }
/*     */     
/* 151 */     procList.remove(0);
/*     */     
/* 153 */     for (String proc : procList) {
/* 154 */       String[] split = ParseUtil.whitespaces.split(proc.trim(), 16);
/*     */       
/* 156 */       if (split.length == 16) {
/* 157 */         procs.add(new FreeBsdOSProcess((pid < 0) ? ParseUtil.parseIntOrDefault(split[1], 0) : pid, split));
/*     */       }
/*     */     } 
/* 160 */     return procs;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessId() {
/* 165 */     return FreeBsdLibc.INSTANCE.getpid();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessCount() {
/* 170 */     List<String> procList = ExecutingCommand.runNative("ps -axo pid");
/* 171 */     if (!procList.isEmpty())
/*     */     {
/* 173 */       return procList.size() - 1;
/*     */     }
/* 175 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 180 */     int threads = 0;
/* 181 */     for (String proc : ExecutingCommand.runNative("ps -axo nlwp")) {
/* 182 */       threads += ParseUtil.parseIntOrDefault(proc.trim(), 0);
/*     */     }
/* 184 */     return threads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemUptime() {
/* 189 */     return System.currentTimeMillis() / 1000L - BOOTTIME;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemBootTime() {
/* 194 */     return BOOTTIME;
/*     */   }
/*     */   
/*     */   private static long querySystemBootTime() {
/* 198 */     FreeBsdLibc.Timeval tv = new FreeBsdLibc.Timeval();
/* 199 */     if (!BsdSysctlUtil.sysctl("kern.boottime", (Structure)tv) || tv.tv_sec == 0L)
/*     */     {
/*     */       
/* 202 */       return ParseUtil.parseLongOrDefault(
/* 203 */           ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), 
/* 204 */           System.currentTimeMillis() / 1000L);
/*     */     }
/*     */ 
/*     */     
/* 208 */     return tv.tv_sec;
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkParams getNetworkParams() {
/* 213 */     return (NetworkParams)new FreeBsdNetworkParams();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public OSService[] getServices() {
/* 219 */     List<OSService> services = new ArrayList<>();
/* 220 */     Set<String> running = new HashSet<>();
/* 221 */     for (OSProcess p : getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID)) {
/* 222 */       OSService s = new OSService(p.getName(), p.getProcessID(), OSService.State.RUNNING);
/* 223 */       services.add(s);
/* 224 */       running.add(p.getName());
/*     */     } 
/*     */     
/* 227 */     File dir = new File("/etc/rc.d");
/*     */     File[] listFiles;
/* 229 */     if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
/* 230 */       for (File f : listFiles) {
/* 231 */         String name = f.getName();
/* 232 */         if (!running.contains(name)) {
/* 233 */           OSService s = new OSService(name, 0, OSService.State.STOPPED);
/* 234 */           services.add(s);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 238 */       LOG.error("Directory: /etc/init does not exist");
/*     */     } 
/* 240 */     return services.<OSService>toArray(new OSService[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\o\\unix\freebsd\FreeBsdOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */