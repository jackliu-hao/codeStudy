/*    */ package oshi.driver.mac;
/*    */ 
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.jna.platform.mac.SystemB;
/*    */ import oshi.software.os.OSSession;
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
/*    */ public final class Who
/*    */ {
/* 44 */   private static final SystemB SYS = SystemB.INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized List<OSSession> queryUtxent() {
/* 55 */     List<OSSession> whoList = new ArrayList<>();
/*    */ 
/*    */     
/* 58 */     SYS.setutxent(); try {
/*    */       SystemB.MacUtmpx ut;
/* 60 */       while ((ut = SYS.getutxent()) != null) {
/* 61 */         if (ut.ut_type == 7 || ut.ut_type == 6) {
/* 62 */           String user = (new String(ut.ut_user, StandardCharsets.US_ASCII)).trim();
/* 63 */           String device = (new String(ut.ut_line, StandardCharsets.US_ASCII)).trim();
/* 64 */           String host = (new String(ut.ut_host, StandardCharsets.US_ASCII)).trim();
/* 65 */           long loginTime = ut.ut_tv.tv_sec.longValue() * 1000L + ut.ut_tv.tv_usec / 1000L;
/*    */           
/* 67 */           if (user.isEmpty() || device.isEmpty() || loginTime < 0L || loginTime > System.currentTimeMillis()) {
/* 68 */             return oshi.driver.unix.Who.queryWho();
/*    */           }
/* 70 */           whoList.add(new OSSession(user, device, loginTime, host));
/*    */         } 
/*    */       } 
/*    */     } finally {
/*    */       
/* 75 */       SYS.endutxent();
/*    */     } 
/* 77 */     return whoList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\mac\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */