/*    */ package oshi.driver.unix.aix;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ @ThreadSafe
/*    */ public final class Ls
/*    */ {
/*    */   public static Map<String, Pair<Integer, Integer>> queryDeviceMajorMinor() {
/* 55 */     Map<String, Pair<Integer, Integer>> majMinMap = new HashMap<>();
/* 56 */     for (String s : ExecutingCommand.runNative("ls -l /dev")) {
/*    */       
/* 58 */       if (!s.isEmpty() && s.charAt(0) == 'b') {
/*    */         
/* 60 */         int idx = s.lastIndexOf(' ');
/* 61 */         if (idx > 0 && idx < s.length()) {
/* 62 */           String device = s.substring(idx + 1);
/* 63 */           int major = ParseUtil.getNthIntValue(s, 2);
/* 64 */           int minor = ParseUtil.getNthIntValue(s, 3);
/* 65 */           majMinMap.put(device, new Pair(Integer.valueOf(major), Integer.valueOf(minor)));
/*    */         } 
/*    */       } 
/*    */     } 
/* 69 */     return majMinMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\Ls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */