/*     */ package oshi.driver.windows.wmi;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import java.util.Collection;
/*     */ import java.util.Set;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.platform.windows.WmiQueryHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class Win32Process
/*     */ {
/*     */   private static final String WIN32_PROCESS = "Win32_Process";
/*     */   
/*     */   public enum CommandLineProperty
/*     */   {
/*  47 */     PROCESSID, COMMANDLINE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum ProcessXPProperty
/*     */   {
/*  54 */     PROCESSID, NAME, KERNELMODETIME, USERMODETIME, THREADCOUNT, PAGEFILEUSAGE, HANDLECOUNT, EXECUTABLEPATH;
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
/*     */   public static WbemcliUtil.WmiResult<CommandLineProperty> queryCommandLines(Set<Integer> pidsToQuery) {
/*  70 */     StringBuilder sb = new StringBuilder("Win32_Process");
/*  71 */     if (pidsToQuery != null) {
/*  72 */       boolean first = true;
/*  73 */       for (Integer pid : pidsToQuery) {
/*  74 */         if (first) {
/*  75 */           sb.append(" WHERE ProcessID=");
/*  76 */           first = false;
/*     */         } else {
/*  78 */           sb.append(" OR ProcessID=");
/*     */         } 
/*  80 */         sb.append(pid);
/*     */       } 
/*     */     } 
/*  83 */     WbemcliUtil.WmiQuery<CommandLineProperty> commandLineQuery = new WbemcliUtil.WmiQuery(sb.toString(), CommandLineProperty.class);
/*  84 */     return WmiQueryHandler.createInstance().queryWMI(commandLineQuery);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WbemcliUtil.WmiResult<ProcessXPProperty> queryProcesses(Collection<Integer> pids) {
/*  95 */     StringBuilder sb = new StringBuilder("Win32_Process");
/*  96 */     if (pids != null) {
/*  97 */       boolean first = true;
/*  98 */       for (Integer pid : pids) {
/*  99 */         if (first) {
/* 100 */           sb.append(" WHERE ProcessID=");
/* 101 */           first = false;
/*     */         } else {
/* 103 */           sb.append(" OR ProcessID=");
/*     */         } 
/* 105 */         sb.append(pid);
/*     */       } 
/*     */     } 
/* 108 */     WbemcliUtil.WmiQuery<ProcessXPProperty> processQueryXP = new WbemcliUtil.WmiQuery(sb.toString(), ProcessXPProperty.class);
/* 109 */     return WmiQueryHandler.createInstance().queryWMI(processQueryXP);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\wmi\Win32Process.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */