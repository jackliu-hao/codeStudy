/*     */ package oshi.software.os.linux;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.platform.linux.LibC;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.linux.Who;
/*     */ import oshi.driver.linux.proc.CpuStat;
/*     */ import oshi.driver.linux.proc.ProcessStat;
/*     */ import oshi.driver.linux.proc.UpTime;
/*     */ import oshi.jna.platform.linux.LinuxLibc;
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
/*     */ import oshi.util.platform.linux.ProcPath;
/*     */ import oshi.util.tuples.Triplet;
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
/*     */ public class LinuxOperatingSystem
/*     */   extends AbstractOperatingSystem
/*     */ {
/*  73 */   private static final Logger LOG = LoggerFactory.getLogger(LinuxOperatingSystem.class);
/*     */   
/*     */   private static final String OS_RELEASE_LOG = "os-release: {}";
/*     */   
/*     */   private static final String LSB_RELEASE_A_LOG = "lsb_release -a: {}";
/*     */   
/*     */   private static final String LSB_RELEASE_LOG = "lsb-release: {}";
/*     */   
/*     */   private static final String RELEASE_DELIM = " release ";
/*     */   
/*     */   private static final String DOUBLE_QUOTES = "(?:^\")|(?:\"$)";
/*     */   private static final String FILENAME_PROPERTIES = "oshi.linux.filename.properties";
/*  85 */   private static final long USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);
/*     */   
/*     */   static final long BOOTTIME;
/*     */ 
/*     */   
/*     */   static {
/*  91 */     long tempBT = CpuStat.getBootTime();
/*     */     
/*  93 */     if (tempBT == 0L) {
/*  94 */       tempBT = System.currentTimeMillis() / 1000L - (long)UpTime.getSystemUptimeSeconds();
/*     */     }
/*  96 */     BOOTTIME = tempBT;
/*     */   }
/*     */ 
/*     */   
/* 100 */   private static final int[] PPID_INDEX = new int[] { 3 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LinuxOperatingSystem() {
/* 108 */     getVersionInfo();
/*     */   }
/*     */ 
/*     */   
/*     */   public String queryManufacturer() {
/* 113 */     return "GNU/Linux";
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractOperatingSystem.FamilyVersionInfo queryFamilyVersionInfo() {
/* 118 */     Triplet<String, String, String> familyVersionCodename = queryFamilyVersionCodenameFromReleaseFiles();
/* 119 */     String buildNumber = null;
/* 120 */     List<String> procVersion = FileUtil.readFile(ProcPath.VERSION);
/* 121 */     if (!procVersion.isEmpty()) {
/* 122 */       String[] split = ParseUtil.whitespaces.split(procVersion.get(0));
/* 123 */       for (String s : split) {
/* 124 */         if (!"Linux".equals(s) && !"version".equals(s)) {
/* 125 */           buildNumber = s;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 130 */     OperatingSystem.OSVersionInfo versionInfo = new OperatingSystem.OSVersionInfo((String)familyVersionCodename.getB(), (String)familyVersionCodename.getC(), buildNumber);
/*     */     
/* 132 */     return new AbstractOperatingSystem.FamilyVersionInfo((String)familyVersionCodename.getA(), versionInfo);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int queryBitness(int jvmBitness) {
/* 137 */     if (jvmBitness < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1) {
/* 138 */       return jvmBitness;
/*     */     }
/* 140 */     return 64;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean queryElevated() {
/* 145 */     return (System.getenv("SUDO_COMMAND") != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileSystem getFileSystem() {
/* 150 */     return (FileSystem)new LinuxFileSystem();
/*     */   }
/*     */ 
/*     */   
/*     */   public InternetProtocolStats getInternetProtocolStats() {
/* 155 */     return new LinuxInternetProtocolStats();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSSession> getSessions() {
/* 160 */     return Collections.unmodifiableList(USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(int limit, OperatingSystem.ProcessSort sort) {
/* 165 */     List<OSProcess> procs = new ArrayList<>();
/* 166 */     File[] pids = ProcessStat.getPidFiles();
/*     */ 
/*     */     
/* 169 */     for (File pidFile : pids) {
/* 170 */       int pid = ParseUtil.parseIntOrDefault(pidFile.getName(), 0);
/* 171 */       LinuxOSProcess linuxOSProcess = new LinuxOSProcess(pid);
/* 172 */       if (!linuxOSProcess.getState().equals(OSProcess.State.INVALID)) {
/* 173 */         procs.add(linuxOSProcess);
/*     */       }
/*     */     } 
/*     */     
/* 177 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 178 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */ 
/*     */   
/*     */   public OSProcess getProcess(int pid) {
/* 183 */     LinuxOSProcess linuxOSProcess = new LinuxOSProcess(pid);
/* 184 */     if (!linuxOSProcess.getState().equals(OSProcess.State.INVALID)) {
/* 185 */       return (OSProcess)linuxOSProcess;
/*     */     }
/* 187 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getChildProcesses(int parentPid, int limit, OperatingSystem.ProcessSort sort) {
/* 192 */     List<OSProcess> procs = new ArrayList<>();
/* 193 */     File[] procFiles = ProcessStat.getPidFiles();
/*     */     
/* 195 */     for (File procFile : procFiles) {
/* 196 */       int pid = ParseUtil.parseIntOrDefault(procFile.getName(), 0);
/* 197 */       if (parentPid == getParentPidFromProcFile(pid)) {
/* 198 */         LinuxOSProcess linuxOSProcess = new LinuxOSProcess(pid);
/* 199 */         if (!linuxOSProcess.getState().equals(OSProcess.State.INVALID)) {
/* 200 */           procs.add(linuxOSProcess);
/*     */         }
/*     */       } 
/*     */     } 
/* 204 */     List<OSProcess> sorted = processSort(procs, limit, sort);
/* 205 */     return Collections.unmodifiableList(sorted);
/*     */   }
/*     */   
/*     */   private static int getParentPidFromProcFile(int pid) {
/* 209 */     String stat = FileUtil.getStringFromFile(String.format("/proc/%d/stat", new Object[] { Integer.valueOf(pid) }));
/*     */     
/* 211 */     if (stat.isEmpty()) {
/* 212 */       return 0;
/*     */     }
/*     */     
/* 215 */     long[] statArray = ParseUtil.parseStringToLongArray(stat, PPID_INDEX, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
/* 216 */     return (int)statArray[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessId() {
/* 221 */     return LinuxLibc.INSTANCE.getpid();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getProcessCount() {
/* 226 */     return (ProcessStat.getPidFiles()).length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getThreadCount() {
/*     */     try {
/* 232 */       LibC.Sysinfo info = new LibC.Sysinfo();
/* 233 */       if (0 != LibC.INSTANCE.sysinfo(info)) {
/* 234 */         LOG.error("Failed to get process thread count. Error code: {}", Integer.valueOf(Native.getLastError()));
/* 235 */         return 0;
/*     */       } 
/* 237 */       return info.procs;
/* 238 */     } catch (UnsatisfiedLinkError|NoClassDefFoundError e) {
/* 239 */       LOG.error("Failed to get procs from sysinfo. {}", e.getMessage());
/*     */       
/* 241 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getSystemUptime() {
/* 246 */     return (long)UpTime.getSystemUptimeSeconds();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSystemBootTime() {
/* 251 */     return BOOTTIME;
/*     */   }
/*     */ 
/*     */   
/*     */   public NetworkParams getNetworkParams() {
/* 256 */     return (NetworkParams)new LinuxNetworkParams();
/*     */   }
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
/*     */   private static Triplet<String, String, String> queryFamilyVersionCodenameFromReleaseFiles() {
/*     */     Triplet<String, String, String> familyVersionCodename;
/* 275 */     if ((familyVersionCodename = readDistribRelease("/etc/system-release")) != null)
/*     */     {
/*     */       
/* 278 */       return familyVersionCodename;
/*     */     }
/*     */ 
/*     */     
/* 282 */     if ((familyVersionCodename = readOsRelease()) != null)
/*     */     {
/*     */       
/* 285 */       return familyVersionCodename;
/*     */     }
/*     */ 
/*     */     
/* 289 */     if ((familyVersionCodename = execLsbRelease()) != null)
/*     */     {
/*     */       
/* 292 */       return familyVersionCodename;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 298 */     if ((familyVersionCodename = readLsbRelease()) != null)
/*     */     {
/*     */       
/* 301 */       return familyVersionCodename;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 308 */     String etcDistribRelease = getReleaseFilename();
/* 309 */     if ((familyVersionCodename = readDistribRelease(etcDistribRelease)) != null)
/*     */     {
/*     */       
/* 312 */       return familyVersionCodename;
/*     */     }
/*     */ 
/*     */     
/* 316 */     String family = filenameToFamily(etcDistribRelease.replace("/etc/", "").replace("release", "")
/* 317 */         .replace("version", "").replace("-", "").replace("_", ""));
/* 318 */     return new Triplet(family, "unknown", "unknown");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Triplet<String, String, String> readOsRelease() {
/* 328 */     String family = null;
/* 329 */     String versionId = "unknown";
/* 330 */     String codeName = "unknown";
/* 331 */     List<String> osRelease = FileUtil.readFile("/etc/os-release");
/*     */     
/* 333 */     for (String line : osRelease) {
/* 334 */       if (line.startsWith("VERSION=")) {
/* 335 */         LOG.debug("os-release: {}", line);
/*     */ 
/*     */ 
/*     */         
/* 339 */         line = line.replace("VERSION=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
/* 340 */         String[] split = line.split("[()]");
/* 341 */         if (split.length <= 1)
/*     */         {
/* 343 */           split = line.split(", ");
/*     */         }
/* 345 */         if (split.length > 0) {
/* 346 */           versionId = split[0].trim();
/*     */         }
/* 348 */         if (split.length > 1)
/* 349 */           codeName = split[1].trim();  continue;
/*     */       } 
/* 351 */       if (line.startsWith("NAME=") && family == null) {
/* 352 */         LOG.debug("os-release: {}", line);
/*     */ 
/*     */         
/* 355 */         family = line.replace("NAME=", "").replaceAll("(?:^\")|(?:\"$)", "").trim(); continue;
/* 356 */       }  if (line.startsWith("VERSION_ID=") && versionId.equals("unknown")) {
/* 357 */         LOG.debug("os-release: {}", line);
/*     */ 
/*     */         
/* 360 */         versionId = line.replace("VERSION_ID=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
/*     */       } 
/*     */     } 
/* 363 */     return (family == null) ? null : new Triplet(family, versionId, codeName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Triplet<String, String, String> execLsbRelease() {
/* 374 */     String family = null;
/* 375 */     String versionId = "unknown";
/* 376 */     String codeName = "unknown";
/*     */ 
/*     */ 
/*     */     
/* 380 */     for (String line : ExecutingCommand.runNative("lsb_release -a")) {
/* 381 */       if (line.startsWith("Description:")) {
/* 382 */         LOG.debug("lsb_release -a: {}", line);
/* 383 */         line = line.replace("Description:", "").trim();
/* 384 */         if (line.contains(" release ")) {
/* 385 */           Triplet<String, String, String> triplet = parseRelease(line, " release ");
/* 386 */           family = (String)triplet.getA();
/* 387 */           if (versionId.equals("unknown")) {
/* 388 */             versionId = (String)triplet.getB();
/*     */           }
/* 390 */           if (codeName.equals("unknown"))
/* 391 */             codeName = (String)triplet.getC(); 
/*     */         }  continue;
/*     */       } 
/* 394 */       if (line.startsWith("Distributor ID:") && family == null) {
/* 395 */         LOG.debug("lsb_release -a: {}", line);
/* 396 */         family = line.replace("Distributor ID:", "").trim(); continue;
/* 397 */       }  if (line.startsWith("Release:") && versionId.equals("unknown")) {
/* 398 */         LOG.debug("lsb_release -a: {}", line);
/* 399 */         versionId = line.replace("Release:", "").trim(); continue;
/* 400 */       }  if (line.startsWith("Codename:") && codeName.equals("unknown")) {
/* 401 */         LOG.debug("lsb_release -a: {}", line);
/* 402 */         codeName = line.replace("Codename:", "").trim();
/*     */       } 
/*     */     } 
/* 405 */     return (family == null) ? null : new Triplet(family, versionId, codeName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Triplet<String, String, String> readLsbRelease() {
/* 416 */     String family = null;
/* 417 */     String versionId = "unknown";
/* 418 */     String codeName = "unknown";
/* 419 */     List<String> osRelease = FileUtil.readFile("/etc/lsb-release");
/*     */     
/* 421 */     for (String line : osRelease) {
/* 422 */       if (line.startsWith("DISTRIB_DESCRIPTION=")) {
/* 423 */         LOG.debug("lsb-release: {}", line);
/* 424 */         line = line.replace("DISTRIB_DESCRIPTION=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
/* 425 */         if (line.contains(" release ")) {
/* 426 */           Triplet<String, String, String> triplet = parseRelease(line, " release ");
/* 427 */           family = (String)triplet.getA();
/* 428 */           if (versionId.equals("unknown")) {
/* 429 */             versionId = (String)triplet.getB();
/*     */           }
/* 431 */           if (codeName.equals("unknown"))
/* 432 */             codeName = (String)triplet.getC(); 
/*     */         }  continue;
/*     */       } 
/* 435 */       if (line.startsWith("DISTRIB_ID=") && family == null) {
/* 436 */         LOG.debug("lsb-release: {}", line);
/* 437 */         family = line.replace("DISTRIB_ID=", "").replaceAll("(?:^\")|(?:\"$)", "").trim(); continue;
/* 438 */       }  if (line.startsWith("DISTRIB_RELEASE=") && versionId.equals("unknown")) {
/* 439 */         LOG.debug("lsb-release: {}", line);
/* 440 */         versionId = line.replace("DISTRIB_RELEASE=", "").replaceAll("(?:^\")|(?:\"$)", "").trim(); continue;
/* 441 */       }  if (line.startsWith("DISTRIB_CODENAME=") && codeName.equals("unknown")) {
/* 442 */         LOG.debug("lsb-release: {}", line);
/* 443 */         codeName = line.replace("DISTRIB_CODENAME=", "").replaceAll("(?:^\")|(?:\"$)", "").trim();
/*     */       } 
/*     */     } 
/* 446 */     return (family == null) ? null : new Triplet(family, versionId, codeName);
/*     */   }
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
/*     */   private static Triplet<String, String, String> readDistribRelease(String filename) {
/* 459 */     if ((new File(filename)).exists()) {
/* 460 */       List<String> osRelease = FileUtil.readFile(filename);
/*     */       
/* 462 */       for (String line : osRelease) {
/* 463 */         LOG.debug("{}: {}", filename, line);
/* 464 */         if (line.contains(" release "))
/*     */         {
/* 466 */           return parseRelease(line, " release "); } 
/* 467 */         if (line.contains(" VERSION "))
/*     */         {
/* 469 */           return parseRelease(line, " VERSION ");
/*     */         }
/*     */       } 
/*     */     } 
/* 473 */     return null;
/*     */   }
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
/*     */   private static Triplet<String, String, String> parseRelease(String line, String splitLine) {
/* 486 */     String[] split = line.split(splitLine);
/* 487 */     String family = split[0].trim();
/* 488 */     String versionId = "unknown";
/* 489 */     String codeName = "unknown";
/* 490 */     if (split.length > 1) {
/* 491 */       split = split[1].split("[()]");
/* 492 */       if (split.length > 0) {
/* 493 */         versionId = split[0].trim();
/*     */       }
/* 495 */       if (split.length > 1) {
/* 496 */         codeName = split[1].trim();
/*     */       }
/*     */     } 
/* 499 */     return new Triplet(family, versionId, codeName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getReleaseFilename() {
/* 509 */     File etc = new File("/etc");
/*     */     
/* 511 */     File[] matchingFiles = etc.listFiles(f -> 
/* 512 */         ((f.getName().endsWith("-release") || f.getName().endsWith("-version") || f.getName().endsWith("_release") || f.getName().endsWith("_version")) && !f.getName().endsWith("os-release") && !f.getName().endsWith("lsb-release") && !f.getName().endsWith("system-release")));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 519 */     if (matchingFiles != null && matchingFiles.length > 0) {
/* 520 */       return matchingFiles[0].getPath();
/*     */     }
/* 522 */     if ((new File("/etc/release")).exists()) {
/* 523 */       return "/etc/release";
/*     */     }
/*     */     
/* 526 */     return "/etc/issue";
/*     */   }
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
/*     */   private static String filenameToFamily(String name) {
/* 539 */     if (name.isEmpty())
/* 540 */       return "Solaris"; 
/* 541 */     if ("issue".equalsIgnoreCase(name))
/*     */     {
/* 543 */       return "Unknown";
/*     */     }
/* 545 */     Properties filenameProps = FileUtil.readPropertiesFromFilename("oshi.linux.filename.properties");
/* 546 */     String family = filenameProps.getProperty(name.toLowerCase());
/* 547 */     return (family != null) ? family : (name.substring(0, 1).toUpperCase() + name.substring(1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OSService[] getServices() {
/* 554 */     List<OSService> services = new ArrayList<>();
/* 555 */     Set<String> running = new HashSet<>();
/* 556 */     for (OSProcess p : getChildProcesses(1, 0, OperatingSystem.ProcessSort.PID)) {
/* 557 */       OSService s = new OSService(p.getName(), p.getProcessID(), OSService.State.RUNNING);
/* 558 */       services.add(s);
/* 559 */       running.add(p.getName());
/*     */     } 
/* 561 */     boolean systemctlFound = false;
/* 562 */     List<String> systemctl = ExecutingCommand.runNative("systemctl list-unit-files");
/* 563 */     for (String str : systemctl) {
/* 564 */       String[] split = ParseUtil.whitespaces.split(str);
/* 565 */       if (split.length == 2 && split[0].endsWith(".service") && "enabled".equals(split[1])) {
/*     */         
/* 567 */         String name = split[0].substring(0, split[0].length() - 8);
/* 568 */         int index = name.lastIndexOf('.');
/* 569 */         String shortName = (index < 0 || index > name.length() - 2) ? name : name.substring(index + 1);
/* 570 */         if (!running.contains(name) && !running.contains(shortName)) {
/* 571 */           OSService s = new OSService(name, 0, OSService.State.STOPPED);
/* 572 */           services.add(s);
/* 573 */           systemctlFound = true;
/*     */         } 
/*     */       } 
/*     */     } 
/* 577 */     if (!systemctlFound) {
/*     */       
/* 579 */       File dir = new File("/etc/init");
/* 580 */       if (dir.exists() && dir.isDirectory()) {
/* 581 */         for (File f : dir.listFiles((f, name) -> name.toLowerCase().endsWith(".conf"))) {
/*     */           
/* 583 */           String name = f.getName().substring(0, f.getName().length() - 5);
/* 584 */           int index = name.lastIndexOf('.');
/* 585 */           String shortName = (index < 0 || index > name.length() - 2) ? name : name.substring(index + 1);
/* 586 */           if (!running.contains(name) && !running.contains(shortName)) {
/* 587 */             OSService s = new OSService(name, 0, OSService.State.STOPPED);
/* 588 */             services.add(s);
/*     */           } 
/*     */         } 
/*     */       } else {
/* 592 */         LOG.error("Directory: /etc/init does not exist");
/*     */       } 
/*     */     } 
/* 595 */     return services.<OSService>toArray(new OSService[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getHz() {
/* 605 */     return USER_HZ;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\linux\LinuxOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */