/*    */ package oshi.driver.mac.disk;
/*    */ 
/*    */ import com.sun.jna.platform.mac.SystemB;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
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
/*    */ public final class Fsstat
/*    */ {
/*    */   public static Map<String, String> queryPartitionToMountMap() {
/* 50 */     Map<String, String> mountPointMap = new HashMap<>();
/*    */     
/* 52 */     int numfs = SystemB.INSTANCE.getfsstat64(null, 0, 0);
/*    */     
/* 54 */     SystemB.Statfs[] fs = new SystemB.Statfs[numfs];
/*    */     
/* 56 */     SystemB.INSTANCE.getfsstat64(fs, numfs * (new SystemB.Statfs()).size(), 16);
/*    */     
/* 58 */     for (SystemB.Statfs f : fs) {
/* 59 */       String mntFrom = (new String(f.f_mntfromname, StandardCharsets.UTF_8)).trim();
/* 60 */       mountPointMap.put(mntFrom.replace("/dev/", ""), (new String(f.f_mntonname, StandardCharsets.UTF_8)).trim());
/*    */     } 
/* 62 */     return mountPointMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\mac\disk\Fsstat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */