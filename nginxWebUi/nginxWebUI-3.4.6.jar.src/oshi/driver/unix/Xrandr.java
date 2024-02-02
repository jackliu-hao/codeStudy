/*    */ package oshi.driver.unix;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ @ThreadSafe
/*    */ public final class Xrandr
/*    */ {
/*    */   public static List<byte[]> getEdidArrays() {
/* 44 */     List<String> xrandr = ExecutingCommand.runNative("xrandr --verbose");
/*    */ 
/*    */     
/* 47 */     if (xrandr.isEmpty()) {
/* 48 */       return (List)Collections.emptyList();
/*    */     }
/* 50 */     List<byte[]> displays = (List)new ArrayList<>();
/* 51 */     StringBuilder sb = null;
/* 52 */     for (String s : xrandr) {
/* 53 */       if (s.contains("EDID")) {
/* 54 */         sb = new StringBuilder(); continue;
/* 55 */       }  if (sb != null) {
/* 56 */         sb.append(s.trim());
/* 57 */         if (sb.length() < 256) {
/*    */           continue;
/*    */         }
/* 60 */         String edidStr = sb.toString();
/* 61 */         byte[] edid = ParseUtil.hexStringToByteArray(edidStr);
/* 62 */         if (edid.length >= 128) {
/* 63 */           displays.add(edid);
/*    */         }
/* 65 */         sb = null;
/*    */       } 
/*    */     } 
/* 68 */     return displays;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\Xrandr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */