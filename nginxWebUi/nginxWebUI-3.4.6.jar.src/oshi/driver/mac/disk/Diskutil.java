/*    */ package oshi.driver.mac.disk;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ @ThreadSafe
/*    */ public final class Diskutil
/*    */ {
/*    */   private static final String DISKUTIL_CS_LIST = "diskutil cs list";
/*    */   private static final String LOGICAL_VOLUME_FAMILY = "Logical Volume Family";
/*    */   private static final String LOGICAL_VOLUME_GROUP = "Logical Volume Group";
/*    */   
/*    */   public static Map<String, String> queryLogicalVolumeMap() {
/* 54 */     Map<String, String> logicalVolumeMap = new HashMap<>();
/*    */     
/* 56 */     Set<String> physicalVolumes = new HashSet<>();
/* 57 */     boolean logicalVolume = false;
/* 58 */     for (String line : ExecutingCommand.runNative("diskutil cs list")) {
/* 59 */       if (line.contains("Logical Volume Group")) {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 65 */         physicalVolumes.clear();
/* 66 */         logicalVolume = false; continue;
/* 67 */       }  if (line.contains("Logical Volume Family")) {
/*    */ 
/*    */         
/* 70 */         logicalVolume = true; continue;
/* 71 */       }  if (line.contains("Disk:")) {
/* 72 */         String volume = ParseUtil.parseLastString(line);
/* 73 */         if (logicalVolume) {
/*    */ 
/*    */           
/* 76 */           for (String pv : physicalVolumes) {
/* 77 */             logicalVolumeMap.put(pv, volume);
/*    */           }
/* 79 */           physicalVolumes.clear(); continue;
/*    */         } 
/* 81 */         physicalVolumes.add(ParseUtil.parseLastString(line));
/*    */       } 
/*    */     } 
/*    */     
/* 85 */     return logicalVolumeMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\mac\disk\Diskutil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */