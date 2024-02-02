/*    */ package oshi.driver.unix.solaris.disk;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.ParseUtil;
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
/*    */ public final class Lshal
/*    */ {
/*    */   private static final String LSHAL_CMD = "lshal";
/*    */   
/*    */   public static Map<String, Integer> queryDiskToMajorMap() {
/* 52 */     Map<String, Integer> majorMap = new HashMap<>();
/* 53 */     List<String> lshal = ExecutingCommand.runNative("lshal");
/* 54 */     String diskName = null;
/* 55 */     for (String line : lshal) {
/* 56 */       if (line.startsWith("udi ")) {
/* 57 */         String udi = ParseUtil.getSingleQuoteStringValue(line);
/* 58 */         diskName = udi.substring(udi.lastIndexOf('/') + 1); continue;
/*    */       } 
/* 60 */       line = line.trim();
/* 61 */       if (line.startsWith("block.major") && diskName != null) {
/* 62 */         majorMap.put(diskName, Integer.valueOf(ParseUtil.getFirstIntValue(line)));
/*    */       }
/*    */     } 
/*    */     
/* 66 */     return majorMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\solaris\disk\Lshal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */