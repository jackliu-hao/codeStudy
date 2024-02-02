/*     */ package oshi.software.os.mac;
/*     */ 
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.mac.SystemB;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.mac.Who;
/*     */ import oshi.software.common.AbstractOperatingSystem;
/*     */ import oshi.software.os.FileSystem;
/*     */ import oshi.software.os.InternetProtocolStats;
/*     */ import oshi.software.os.NetworkParams;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSService;
/*     */ import oshi.software.os.OSSession;
/*     */ import oshi.software.os.OperatingSystem;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.mac.SysctlUtil;
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
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class MacOperatingSystem
/*     */   extends AbstractOperatingSystem
/*     */ {
/*  69 */   private static final Logger LOG = LoggerFactory.getLogger(MacOperatingSystem.class);
/*     */   
/*     */   public static final String MACOS_VERSIONS_PROPERTIES = "oshi.macos.versions.properties";
/*     */   
/*     */   private static final String SYSTEM_LIBRARY_LAUNCH_AGENTS = "/System/Library/LaunchAgents";
/*     */   
/*     */   private static final String SYSTEM_LIBRARY_LAUNCH_DAEMONS = "/System/Library/LaunchDaemons";
/*  76 */   private int maxProc = 1024;
/*     */   
/*     */   private final String osXVersion;
/*     */   private final int major;
/*     */   private final int minor;
/*     */   private static final long BOOTTIME;
/*     */   
/*     */   static {
/*  84 */     SystemB.Timeval tv = new SystemB.Timeval();
/*  85 */     if (!SysctlUtil.sysctl("kern.boottime", (Structure)tv) || tv.tv_sec.longValue() == 0L) {
/*     */ 
/*     */       
/*  88 */       BOOTTIME = ParseUtil.parseLongOrDefault(
/*  89 */           ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), 
/*  90 */           System.currentTimeMillis() / 1000L);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/*  95 */       BOOTTIME = tv.tv_sec.longValue();
/*     */     } 
/*     */   }
/*     */   
/*     */   public MacOperatingSystem() {
/* 100 */     this.osXVersion = System.getProperty("os.version");
/* 101 */     this.major = ParseUtil.getFirstIntValue(this.osXVersion);
/* 102 */     this.minor = ParseUtil.getNthIntValue(this.osXVersion, 2);
/*     */     
/* 104 */     this.maxProc = SysctlUtil.sysctl("kern.maxproc", 4096);
/*     */   }
/*     */ 
/*     */   
/*     */   public String queryManufacturer() {
/* 109 */     return "Apple";
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
/* 114 */     String family = (this.major == 10 && this.minor >= 12) ? "macOS" : System.getProperty("os.name");
/* 115 */     String codeName = parseCodeName();
/* 116 */     String buildNumber = SysctlUtil.sysctl("kern.osversion", "");
/* 117 */     return new AbstractOperatingSystem.FamilyVersionInfo(family, new OperatingSystem.OSVersionInfo(this.osXVersion, codeName, buildNumber));
/*     */   }
/*     */   
/*     */   private String parseCodeName() {
/* 121 */     if (this.major == 10) {
/* 122 */       Properties verProps = FileUtil.readPropertiesFromFilename("oshi.macos.versions.properties");
/* 123 */       return verProps.getProperty(this.major + "." + this.minor);
/*     */     } 
/* 125 */     LOG.warn("Unable to parse version {}.{} to a codename.", Integer.valueOf(this.major), Integer.valueOf(this.minor));
/* 126 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   protected int queryBitness(int jvmBitness) {
/* 131 */     if (jvmBitness == 64 || (this.major == 10 && this.minor > 6)) {
/* 132 */       return 64;
/*     */     }
/* 134 */     return ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf LONG_BIT"), 32);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean queryElevated() {
/* 139 */     return (System.getenv("SUDO_COMMAND") != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileSystem getFileSystem() {
/* 144 */     return (FileSystem)new MacFileSystem();
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats getInternetProtocolStats() {
/* 149 */     return new MacInternetProtocolStats(isElevated());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSSession> getSessions() {
/* 154 */     return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
/* 159 */     List<OSProcess> procs = new ArrayList<>();
/* 160 */     int[] pids = new int[this.maxProc];
/* 161 */     int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;
/*     */     
/* 163 */     for (int i = 0; i < numberOfProcesses; i++) {
/*     */ 
/*     */       
/* 166 */       if (pids[i] > 0) {
/* 167 */         OSProcess proc = getProcess(pids[i]);
/* 168 */         if (proc != null) {
/* 169 */           procs.add(proc);
/*     */         }
/*     */       } 
/*     */     } 
/* 173 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 174 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess getProcess(int pid) {
/* 179 */     MacOSProcess macOSProcess = new MacOSProcess(pid, this.minor);
/* 180 */     return macOSProcess.getState().equals(OSProcess.State.INVALID) ? null : (OSProcess)macOSProcess;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
/* 185 */     List<OSProcess> procs = new ArrayList<>();
/* 186 */     int[] pids = new int[this.maxProc];
/* 187 */     int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;
/*     */     
/* 189 */     for (int i = 0; i < numberOfProcesses; i++) {
/*     */ 
/*     */       
/* 192 */       if (pids[i] != 0)
/*     */       {
/*     */         
/* 195 */         if (parentPid == getParentProcessPid(pids[i])) {
/* 196 */           OSProcess proc = getProcess(pids[i]);
/* 197 */           if (proc != null)
/* 198 */             procs.add(proc); 
/*     */         } 
/*     */       }
/*     */     } 
/* 202 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 203 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */   
/*     */   private static int getParentProcessPid(int pid) {
/* 207 */     SystemB.ProcTaskAllInfo taskAllInfo = new SystemB.ProcTaskAllInfo();
/* 208 */     if (0 > SystemB.INSTANCE.proc_pidinfo(pid, 2, 0L, (Structure)taskAllInfo, taskAllInfo.size())) {
/* 209 */       return 0;
/*     */     }
/* 211 */     return taskAllInfo.pbsd.pbi_ppid;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessId() {
/* 216 */     return SystemB.INSTANCE.getpid();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessCount() {
/* 221 */     return SystemB.INSTANCE.proc_listpids(1, 0, null, 0) / SystemB.INT_SIZE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/* 228 */     int[] pids = new int[getProcessCount() + 10];
/* 229 */     int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length) / SystemB.INT_SIZE;
/*     */     
/* 231 */     int numberOfThreads = 0;
/* 232 */     SystemB.ProcTaskInfo taskInfo = new SystemB.ProcTaskInfo();
/* 233 */     for (int i = 0; i < numberOfProcesses; i++) {
/* 234 */       int exit = SystemB.INSTANCE.proc_pidinfo(pids[i], 4, 0L, (Structure)taskInfo, taskInfo.size());
/* 235 */       if (exit != -1) {
/* 236 */         numberOfThreads += taskInfo.pti_threadnum;
/*     */       }
/*     */     } 
/* 239 */     return numberOfThreads;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemUptime() {
/* 244 */     return System.currentTimeMillis() / 1000L - BOOTTIME;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemBootTime() {
/* 249 */     return BOOTTIME;
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkParams getNetworkParams() {
/* 254 */     return (NetworkParams)new MacNetworkParams();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public OSService[] getServices() {
/* 260 */     List<OSService> services = new ArrayList<>();
/* 261 */     Set<String> running = new HashSet<>();
/* 262 */     for (OSProcess p : getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID)) {
/* 263 */       OSService s = new OSService(p.getName(), p.getProcessID(), OSService.State.RUNNING);
/* 264 */       services.add(s);
/* 265 */       running.add(p.getName());
/*     */     } 
/*     */     
/* 268 */     ArrayList<File> files = new ArrayList<>();
/* 269 */     File dir = new File("/System/Library/LaunchAgents");
/* 270 */     if (dir.exists() && dir.isDirectory()) {
/* 271 */       files.addAll(Arrays.asList(dir.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
/*     */     } else {
/* 273 */       LOG.error("Directory: /System/Library/LaunchAgents does not exist");
/*     */     } 
/* 275 */     dir = new File("/System/Library/LaunchDaemons");
/* 276 */     if (dir.exists() && dir.isDirectory()) {
/* 277 */       files.addAll(Arrays.asList(dir.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
/*     */     } else {
/* 279 */       LOG.error("Directory: /System/Library/LaunchDaemons does not exist");
/*     */     } 
/* 281 */     for (File f : files) {
/*     */       
/* 283 */       String name = f.getName().substring(0, f.getName().length() - 6);
/* 284 */       int index = name.lastIndexOf('.');
/* 285 */       String shortName = (index < 0 || index > name.length() - 2) ? name : name.substring(index + 1);
/* 286 */       if (!running.contains(name) && !running.contains(shortName)) {
/* 287 */         OSService s = new OSService(name, 0, OSService.State.STOPPED);
/* 288 */         services.add(s);
/*     */       } 
/*     */     } 
/* 291 */     return services.<OSService>toArray(new OSService[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\mac\MacOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */