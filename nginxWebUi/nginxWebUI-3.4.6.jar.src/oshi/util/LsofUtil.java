/*     */ package oshi.util;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
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
/*     */ public final class LsofUtil
/*     */ {
/*     */   public static Map<Integer, String> getCwdMap(int pid) {
/*  51 */     List<String> lsof = ExecutingCommand.runNative("lsof -Fn -d cwd" + ((pid < 0) ? "" : (" -p " + pid)));
/*  52 */     Map<Integer, String> cwdMap = new HashMap<>();
/*  53 */     Integer key = Integer.valueOf(-1);
/*  54 */     for (String line : lsof) {
/*  55 */       if (line.isEmpty()) {
/*     */         continue;
/*     */       }
/*  58 */       switch (line.charAt(0)) {
/*     */         case 'p':
/*  60 */           key = Integer.valueOf(ParseUtil.parseIntOrDefault(line.substring(1), -1));
/*     */         
/*     */         case 'n':
/*  63 */           cwdMap.put(key, line.substring(1));
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/*  71 */     return cwdMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCwd(int pid) {
/*  82 */     List<String> lsof = ExecutingCommand.runNative("lsof -Fn -d cwd -p " + pid);
/*  83 */     for (String line : lsof) {
/*  84 */       if (!line.isEmpty() && line.charAt(0) == 'n') {
/*  85 */         return line.substring(1).trim();
/*     */       }
/*     */     } 
/*  88 */     return "";
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
/*     */   public static long getOpenFiles(int pid) {
/* 100 */     return ExecutingCommand.runNative(String.format("lsof -p %d", new Object[] { Integer.valueOf(pid) })).size() - 1L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\LsofUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */