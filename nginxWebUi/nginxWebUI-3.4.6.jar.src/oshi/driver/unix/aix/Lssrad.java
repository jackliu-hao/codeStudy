/*    */ package oshi.driver.unix.aix;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.ParseUtil;
/*    */ import oshi.util.tuples.Pair;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class Lssrad
/*    */ {
/*    */   public static Map<Integer, Pair<Integer, Integer>> queryNodesPackages() {
/* 64 */     int node = 0;
/* 65 */     int slot = 0;
/* 66 */     Map<Integer, Pair<Integer, Integer>> nodeMap = new HashMap<>();
/* 67 */     List<String> lssrad = ExecutingCommand.runNative("lssrad -av");
/*    */     
/* 69 */     if (!lssrad.isEmpty()) {
/* 70 */       lssrad.remove(0);
/*    */     }
/* 72 */     for (String s : lssrad) {
/* 73 */       String t = s.trim();
/* 74 */       if (!t.isEmpty()) {
/* 75 */         if (Character.isDigit(s.charAt(0))) {
/* 76 */           node = ParseUtil.parseIntOrDefault(t, 0); continue;
/*    */         } 
/* 78 */         if (t.contains(".")) {
/* 79 */           String[] split = ParseUtil.whitespaces.split(t, 3);
/* 80 */           slot = ParseUtil.parseIntOrDefault(split[0], 0);
/* 81 */           t = (split.length > 2) ? split[2] : "";
/*    */         } 
/* 83 */         for (Integer proc : ParseUtil.parseHyphenatedIntList(t)) {
/* 84 */           nodeMap.put(proc, new Pair(Integer.valueOf(node), Integer.valueOf(slot)));
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 89 */     return nodeMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\Lssrad.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */