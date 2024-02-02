/*    */ package oshi.driver.unix.freebsd.disk;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.ExecutingCommand;
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
/*    */ public final class Mount
/*    */ {
/*    */   private static final String MOUNT_CMD = "mount";
/* 41 */   private static final Pattern MOUNT_PATTERN = Pattern.compile("/dev/(\\S+p\\d+) on (\\S+) .*");
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
/*    */   public static Map<String, String> queryPartitionToMountMap() {
/* 53 */     Map<String, String> mountMap = new HashMap<>();
/* 54 */     for (String mnt : ExecutingCommand.runNative("mount")) {
/* 55 */       Matcher m = MOUNT_PATTERN.matcher(mnt);
/* 56 */       if (m.matches()) {
/* 57 */         mountMap.put(m.group(1), m.group(2));
/*    */       }
/*    */     } 
/* 60 */     return mountMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\freebsd\disk\Mount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */