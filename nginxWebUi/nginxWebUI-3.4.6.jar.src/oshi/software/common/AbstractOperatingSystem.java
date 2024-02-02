/*     */ package oshi.software.common;
/*     */ 
/*     */ import com.sun.jna.Platform;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.driver.unix.Who;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.software.os.OSService;
/*     */ import oshi.software.os.OSSession;
/*     */ import oshi.software.os.OperatingSystem;
/*     */ import oshi.util.GlobalConfig;
/*     */ import oshi.util.Memoizer;
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
/*     */ public abstract class AbstractOperatingSystem
/*     */   implements OperatingSystem
/*     */ {
/*     */   public static final String OSHI_OS_UNIX_WHOCOMMAND = "oshi.os.unix.whoCommand";
/*  47 */   protected static final boolean USE_WHO_COMMAND = GlobalConfig.get("oshi.os.unix.whoCommand", false);
/*     */   
/*  49 */   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
/*  50 */   private final Supplier<FamilyVersionInfo> familyVersionInfo = Memoizer.memoize(this::queryFamilyVersionInfo);
/*  51 */   private final Supplier<Integer> bitness = Memoizer.memoize(this::queryPlatformBitness);
/*     */   
/*  53 */   private final Supplier<Boolean> elevated = Memoizer.memoize(this::queryElevated);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   private static final Comparator<OSProcess> CPU_DESC_SORT = Comparator.<OSProcess>comparingDouble(OSProcess::getProcessCpuLoadCumulative).reversed();
/*     */   
/*  61 */   private static final Comparator<OSProcess> RSS_DESC_SORT = Comparator.<OSProcess>comparingLong(OSProcess::getResidentSetSize)
/*  62 */     .reversed();
/*     */   
/*  64 */   private static final Comparator<OSProcess> UPTIME_ASC_SORT = Comparator.comparingLong(OSProcess::getUpTime);
/*     */   
/*  66 */   private static final Comparator<OSProcess> UPTIME_DESC_SORT = UPTIME_ASC_SORT.reversed();
/*     */   
/*  68 */   private static final Comparator<OSProcess> PID_ASC_SORT = Comparator.comparingInt(OSProcess::getProcessID);
/*     */ 
/*     */   
/*  71 */   private static final Comparator<OSProcess> PARENTPID_ASC_SORT = Comparator.comparingInt(OSProcess::getParentProcessID);
/*     */   
/*  73 */   private static final Comparator<OSProcess> NAME_ASC_SORT = Comparator.comparing(OSProcess::getName, String.CASE_INSENSITIVE_ORDER);
/*     */ 
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  78 */     return this.manufacturer.get();
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract String queryManufacturer();
/*     */   
/*     */   public String getFamily() {
/*  85 */     return (this.familyVersionInfo.get()).family;
/*     */   }
/*     */ 
/*     */   
/*     */   public OperatingSystem.OSVersionInfo getVersionInfo() {
/*  90 */     return (this.familyVersionInfo.get()).versionInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract FamilyVersionInfo queryFamilyVersionInfo();
/*     */   
/*     */   public int getBitness() {
/*  97 */     return ((Integer)this.bitness.get()).intValue();
/*     */   }
/*     */   
/*     */   private int queryPlatformBitness() {
/* 101 */     if (Platform.is64Bit()) {
/* 102 */       return 64;
/*     */     }
/*     */ 
/*     */     
/* 106 */     int jvmBitness = (System.getProperty("os.arch").indexOf("64") != -1) ? 64 : 32;
/* 107 */     return queryBitness(jvmBitness);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract int queryBitness(int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isElevated() {
/* 121 */     return ((Boolean)this.elevated.get()).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public OSService[] getServices() {
/* 126 */     return new OSService[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean queryElevated();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<OSProcess> processSort(List<OSProcess> processes, int limit, OperatingSystem.ProcessSort sort) {
/* 146 */     if (sort != null) {
/* 147 */       switch (sort) {
/*     */         case CPU:
/* 149 */           processes.sort(CPU_DESC_SORT);
/*     */           break;
/*     */         case MEMORY:
/* 152 */           processes.sort(RSS_DESC_SORT);
/*     */           break;
/*     */         case OLDEST:
/* 155 */           processes.sort(UPTIME_DESC_SORT);
/*     */           break;
/*     */         case NEWEST:
/* 158 */           processes.sort(UPTIME_ASC_SORT);
/*     */           break;
/*     */         case PID:
/* 161 */           processes.sort(PID_ASC_SORT);
/*     */           break;
/*     */         case PARENTPID:
/* 164 */           processes.sort(PARENTPID_ASC_SORT);
/*     */           break;
/*     */         case NAME:
/* 167 */           processes.sort(NAME_ASC_SORT);
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 172 */           throw new IllegalArgumentException("Unimplemented enum type: " + sort.toString());
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/* 177 */     int maxProcs = processes.size();
/* 178 */     if (limit > 0 && maxProcs > limit) {
/* 179 */       maxProcs = limit;
/*     */     } else {
/* 181 */       return processes;
/*     */     } 
/* 183 */     List<OSProcess> procs = new ArrayList<>();
/* 184 */     for (int i = 0; i < maxProcs; i++) {
/* 185 */       procs.add(processes.get(i));
/*     */     }
/* 187 */     return procs;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSSession> getSessions() {
/* 192 */     return Collections.unmodifiableList(Who.queryWho());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses() {
/* 197 */     return getProcesses(0, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<OSProcess> getProcesses(Collection<Integer> pids) {
/* 202 */     List<OSProcess> returnValue = new ArrayList<>(pids.size());
/* 203 */     for (Integer pid : pids) {
/* 204 */       OSProcess process = getProcess(pid.intValue());
/* 205 */       if (process != null) {
/* 206 */         returnValue.add(process);
/*     */       }
/*     */     } 
/* 209 */     return Collections.unmodifiableList(returnValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 214 */     StringBuilder sb = new StringBuilder();
/* 215 */     sb.append(getManufacturer()).append(' ').append(getFamily()).append(' ').append(getVersionInfo());
/* 216 */     return sb.toString();
/*     */   }
/*     */   
/*     */   protected static final class FamilyVersionInfo {
/*     */     private final String family;
/*     */     private final OperatingSystem.OSVersionInfo versionInfo;
/*     */     
/*     */     public FamilyVersionInfo(String family, OperatingSystem.OSVersionInfo versionInfo) {
/* 224 */       this.family = family;
/* 225 */       this.versionInfo = versionInfo;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\common\AbstractOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */