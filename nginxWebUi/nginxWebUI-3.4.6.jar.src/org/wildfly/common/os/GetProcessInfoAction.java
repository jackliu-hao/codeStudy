/*     */ package org.wildfly.common.os;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.RuntimeMXBean;
/*     */ import java.security.PrivilegedAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class GetProcessInfoAction
/*     */   implements PrivilegedAction<Object[]>
/*     */ {
/*     */   public Object[] run() {
/*     */     RuntimeMXBean runtime;
/*  35 */     long pid = -1L;
/*  36 */     String processName = "<unknown>";
/*     */     
/*     */     try {
/*  39 */       runtime = ManagementFactory.<RuntimeMXBean>getPlatformMXBean(RuntimeMXBean.class);
/*  40 */     } catch (Exception ignored) {
/*  41 */       return new Object[] { Long.valueOf(pid), processName };
/*     */     } 
/*     */ 
/*     */     
/*  45 */     String name = runtime.getName();
/*  46 */     if (name != null) {
/*  47 */       int idx = name.indexOf('@');
/*  48 */       if (idx != -1) {
/*  49 */         try { pid = Long.parseLong(name.substring(0, idx)); }
/*  50 */         catch (NumberFormatException numberFormatException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  55 */     processName = System.getProperty("jboss.process.name");
/*  56 */     if (processName == null) {
/*  57 */       String classPath = System.getProperty("java.class.path");
/*  58 */       String commandLine = System.getProperty("sun.java.command");
/*  59 */       if (commandLine != null) {
/*  60 */         if (classPath != null && commandLine.startsWith(classPath)) {
/*     */           
/*  62 */           int sepIdx = classPath.lastIndexOf(File.separatorChar);
/*  63 */           if (sepIdx > 0) {
/*  64 */             processName = classPath.substring(sepIdx + 1);
/*     */           } else {
/*  66 */             processName = classPath;
/*     */           } 
/*     */         } else {
/*     */           String className;
/*     */           
/*  71 */           int firstSpace = commandLine.indexOf(' ');
/*     */           
/*  73 */           if (firstSpace > 0) {
/*  74 */             className = commandLine.substring(0, firstSpace);
/*     */           } else {
/*  76 */             className = commandLine;
/*     */           } 
/*     */           
/*  79 */           int lastDot = className.lastIndexOf('.', firstSpace);
/*  80 */           if (lastDot > 0) {
/*  81 */             processName = className.substring(lastDot + 1);
/*  82 */             if (processName.equalsIgnoreCase("jar") || processName.equalsIgnoreCase("ȷar")) {
/*     */               
/*  84 */               int secondLastDot = className.lastIndexOf('.', lastDot - 1);
/*  85 */               int sepIdx = className.lastIndexOf(File.separatorChar);
/*  86 */               int lastSep = (secondLastDot == -1) ? sepIdx : ((sepIdx == -1) ? secondLastDot : Math.max(sepIdx, secondLastDot));
/*  87 */               if (lastSep > 0) {
/*  88 */                 processName = className.substring(lastSep + 1);
/*     */               } else {
/*  90 */                 processName = className;
/*     */               } 
/*     */             } 
/*     */           } else {
/*  94 */             processName = className;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*  99 */     if (processName == null) {
/* 100 */       processName = "<unknown>";
/*     */     }
/* 102 */     return new Object[] { Long.valueOf(pid), processName };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\os\GetProcessInfoAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */