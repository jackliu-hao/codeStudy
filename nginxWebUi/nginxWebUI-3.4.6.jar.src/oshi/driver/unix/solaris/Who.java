/*    */ package oshi.driver.unix.solaris;
/*    */ 
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.jna.platform.unix.solaris.SolarisLibc;
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
/* 44 */   private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;
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
/* 58 */     LIBC.setutxent();
/*    */     try {
/*    */       SolarisLibc.SolarisUtmpx ut;
/* 61 */       while ((ut = LIBC.getutxent()) != null) {
/* 62 */         if (ut.ut_type == 7 || ut.ut_type == 6) {
/* 63 */           String user = (new String(ut.ut_user, StandardCharsets.US_ASCII)).trim();
/* 64 */           if (!"LOGIN".equals(user)) {
/* 65 */             String device = (new String(ut.ut_line, StandardCharsets.US_ASCII)).trim();
/* 66 */             String host = (new String(ut.ut_host, StandardCharsets.US_ASCII)).trim();
/* 67 */             long loginTime = ut.ut_tv.tv_sec.longValue() * 1000L + ut.ut_tv.tv_usec.longValue() / 1000L;
/*    */             
/* 69 */             if (user.isEmpty() || device.isEmpty() || loginTime < 0L || loginTime > 
/* 70 */               System.currentTimeMillis()) {
/* 71 */               return oshi.driver.unix.Who.queryWho();
/*    */             }
/* 73 */             whoList.add(new OSSession(user, device, loginTime, host));
/*    */           } 
/*    */         } 
/*    */       } 
/*    */     } finally {
/*    */       
/* 79 */       LIBC.endutxent();
/*    */     } 
/* 81 */     return whoList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\solaris\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */