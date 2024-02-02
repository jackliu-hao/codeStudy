/*    */ package oshi.hardware.platform.linux;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.Display;
/*    */ import oshi.hardware.common.AbstractDisplay;
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
/*    */ @Immutable
/*    */ final class LinuxDisplay
/*    */   extends AbstractDisplay
/*    */ {
/* 45 */   private static final Logger LOG = LoggerFactory.getLogger(LinuxDisplay.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   LinuxDisplay(byte[] edid) {
/* 54 */     super(edid);
/* 55 */     LOG.debug("Initialized LinuxDisplay");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Display> getDisplays() {
/* 64 */     List<String> xrandr = ExecutingCommand.runNative("xrandr --verbose");
/*    */ 
/*    */     
/* 67 */     if (xrandr.isEmpty()) {
/* 68 */       return Collections.emptyList();
/*    */     }
/* 70 */     List<Display> displays = new ArrayList<>();
/* 71 */     StringBuilder sb = null;
/* 72 */     for (String s : xrandr) {
/* 73 */       if (s.contains("EDID")) {
/* 74 */         sb = new StringBuilder(); continue;
/* 75 */       }  if (sb != null) {
/* 76 */         sb.append(s.trim());
/* 77 */         if (sb.length() < 256) {
/*    */           continue;
/*    */         }
/* 80 */         String edidStr = sb.toString();
/* 81 */         LOG.debug("Parsed EDID: {}", edidStr);
/* 82 */         byte[] edid = ParseUtil.hexStringToByteArray(edidStr);
/* 83 */         if (edid.length >= 128) {
/* 84 */           displays.add(new LinuxDisplay(edid));
/*    */         }
/* 86 */         sb = null;
/*    */       } 
/*    */     } 
/* 89 */     return Collections.unmodifiableList(displays);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */