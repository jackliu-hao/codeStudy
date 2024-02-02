/*     */ package org.wildfly.common.cpu;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.AccessController;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProcessorInfo
/*     */ {
/*     */   private static final String CPUS_ALLOWED = "Cpus_allowed:";
/*  39 */   private static final byte[] BITS = new byte[] { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
/*  40 */   private static final Charset ASCII = Charset.forName("US-ASCII");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int availableProcessors() {
/*  55 */     if (System.getSecurityManager() != null) {
/*  56 */       return ((Integer)AccessController.<Integer>doPrivileged(() -> Integer.valueOf(determineProcessors()))).intValue();
/*     */     }
/*     */     
/*  59 */     return determineProcessors();
/*     */   }
/*     */   
/*     */   private static int determineProcessors() {
/*  63 */     int javaProcs = Runtime.getRuntime().availableProcessors();
/*  64 */     if (!isLinux()) {
/*  65 */       return javaProcs;
/*     */     }
/*     */     
/*  68 */     int maskProcs = 0;
/*     */     
/*     */     try {
/*  71 */       maskProcs = readCPUMask();
/*  72 */     } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */     
/*  76 */     return (maskProcs > 0) ? Math.min(javaProcs, maskProcs) : javaProcs;
/*     */   }
/*     */   
/*     */   private static int readCPUMask() throws IOException {
/*  80 */     FileInputStream stream = new FileInputStream("/proc/self/status");
/*  81 */     InputStreamReader inputReader = new InputStreamReader(stream, ASCII);
/*     */     
/*  83 */     BufferedReader reader = new BufferedReader(inputReader); 
/*     */     try { String line;
/*  85 */       while ((line = reader.readLine()) != null)
/*  86 */       { if (line.startsWith("Cpus_allowed:"))
/*  87 */         { int count = 0;
/*  88 */           int start = "Cpus_allowed:".length(); int i;
/*  89 */           for (i = start; i < line.length(); i++) {
/*  90 */             char ch = line.charAt(i);
/*  91 */             if (ch >= '0' && ch <= '9') {
/*  92 */               count += BITS[ch - 48];
/*  93 */             } else if (ch >= 'a' && ch <= 'f') {
/*  94 */               count += BITS[ch - 97 + 10];
/*  95 */             } else if (ch >= 'A' && ch <= 'F') {
/*  96 */               count += BITS[ch - 65 + 10];
/*     */             } 
/*     */           } 
/*  99 */           i = count;
/*     */ 
/*     */           
/* 102 */           reader.close(); return i; }  }  reader.close(); } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/* 104 */      return -1;
/*     */   }
/*     */   
/*     */   private static boolean isLinux() {
/* 108 */     String osArch = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);
/* 109 */     return osArch.contains("linux");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\cpu\ProcessorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */