/*     */ package oshi.software.os;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import oshi.util.Util;
/*     */ 
/*     */ @ThreadSafe
/*     */ public interface OperatingSystem {
/*     */   String getFamily();
/*     */   
/*     */   String getManufacturer();
/*     */   
/*     */   OSVersionInfo getVersionInfo();
/*     */   
/*     */   FileSystem getFileSystem();
/*     */   
/*     */   InternetProtocolStats getInternetProtocolStats();
/*     */   
/*     */   List<OSSession> getSessions();
/*     */   
/*     */   List<OSProcess> getProcesses();
/*     */   
/*     */   List<OSProcess> getProcesses(int paramInt, ProcessSort paramProcessSort);
/*     */   
/*     */   List<OSProcess> getProcesses(Collection<Integer> paramCollection);
/*     */   
/*     */   OSProcess getProcess(int paramInt);
/*     */   
/*     */   List<OSProcess> getChildProcesses(int paramInt1, int paramInt2, ProcessSort paramProcessSort);
/*     */   
/*     */   int getProcessId();
/*     */   
/*     */   int getProcessCount();
/*     */   
/*     */   int getThreadCount();
/*     */   
/*     */   int getBitness();
/*     */   
/*     */   long getSystemUptime();
/*     */   
/*     */   long getSystemBootTime();
/*     */   
/*     */   boolean isElevated();
/*     */   
/*     */   NetworkParams getNetworkParams();
/*     */   
/*     */   OSService[] getServices();
/*     */   
/*     */   public enum ProcessSort {
/*  49 */     CPU, MEMORY, OLDEST, NEWEST, PID, PARENTPID, NAME;
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
/*     */   @Immutable
/*     */   public static class OSVersionInfo
/*     */   {
/*     */     private final String version;
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
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final String codeName;
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
/*     */ 
/*     */ 
/*     */     
/*     */     private final String buildNumber;
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
/*     */ 
/*     */ 
/*     */     
/*     */     private final String versionStr;
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
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public OSVersionInfo(String version, String codeName, String buildNumber) {
/* 258 */       this.version = version;
/* 259 */       this.codeName = codeName;
/* 260 */       this.buildNumber = buildNumber;
/*     */       
/* 262 */       StringBuilder sb = new StringBuilder((getVersion() != null) ? getVersion() : "unknown");
/* 263 */       if (!Util.isBlank(getCodeName())) {
/* 264 */         sb.append(" (").append(getCodeName()).append(')');
/*     */       }
/* 266 */       if (!Util.isBlank(getBuildNumber())) {
/* 267 */         sb.append(" build ").append(getBuildNumber());
/*     */       }
/* 269 */       this.versionStr = sb.toString();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getVersion() {
/* 278 */       return this.version;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getCodeName() {
/* 287 */       return this.codeName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getBuildNumber() {
/* 296 */       return this.buildNumber;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 301 */       return this.versionStr;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\OperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */