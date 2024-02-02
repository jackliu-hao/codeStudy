/*     */ package oshi.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.PlatformEnum;
/*     */ import oshi.SystemInfo;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class ExecutingCommand
/*     */ {
/*  48 */   private static final Logger LOG = LoggerFactory.getLogger(ExecutingCommand.class);
/*     */   
/*  50 */   private static final String[] DEFAULT_ENV = getDefaultEnv();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] getDefaultEnv() {
/*  56 */     PlatformEnum platform = SystemInfo.getCurrentPlatformEnum();
/*  57 */     if (platform == PlatformEnum.WINDOWS)
/*  58 */       return new String[] { "LANGUAGE=C" }; 
/*  59 */     if (platform != PlatformEnum.UNKNOWN) {
/*  60 */       return new String[] { "LC_ALL=C" };
/*     */     }
/*  62 */     return null;
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
/*     */   public static List<String> runNative(String cmdToRun) {
/*  79 */     String[] cmd = cmdToRun.split(" ");
/*  80 */     return runNative(cmd);
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
/*     */   public static List<String> runNative(String[] cmdToRunWithArgs) {
/*  97 */     Process p = null;
/*     */     try {
/*  99 */       p = Runtime.getRuntime().exec(cmdToRunWithArgs, DEFAULT_ENV);
/* 100 */     } catch (SecurityException|IOException e) {
/* 101 */       LOG.trace("Couldn't run command {}: {}", Arrays.toString((Object[])cmdToRunWithArgs), e.getMessage());
/* 102 */       return new ArrayList<>(0);
/*     */     } 
/*     */     
/* 105 */     ArrayList<String> sa = new ArrayList<>();
/*     */     
/* 107 */     try { BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.defaultCharset())); 
/*     */       try { String line;
/* 109 */         while ((line = reader.readLine()) != null) {
/* 110 */           sa.add(line);
/*     */         }
/* 112 */         p.waitFor();
/* 113 */         reader.close(); } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
/* 114 */     { LOG.trace("Problem reading output from {}: {}", Arrays.toString((Object[])cmdToRunWithArgs), e.getMessage());
/* 115 */       return new ArrayList<>(0); }
/* 116 */     catch (InterruptedException ie)
/* 117 */     { LOG.trace("Interrupted while reading output from {}: {}", Arrays.toString((Object[])cmdToRunWithArgs), ie
/* 118 */           .getMessage());
/* 119 */       Thread.currentThread().interrupt(); }
/*     */     
/* 121 */     return sa;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getFirstAnswer(String cmd2launch) {
/* 132 */     return getAnswerAt(cmd2launch, 0);
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
/*     */   public static String getAnswerAt(String cmd2launch, int answerIdx) {
/* 147 */     List<String> sa = runNative(cmd2launch);
/*     */     
/* 149 */     if (answerIdx >= 0 && answerIdx < sa.size()) {
/* 150 */       return sa.get(answerIdx);
/*     */     }
/* 152 */     return "";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\ExecutingCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */