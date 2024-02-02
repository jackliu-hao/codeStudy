/*    */ package oshi.driver.unix.freebsd;
/*    */ 
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
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
/* 44 */   private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;
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
/*    */       FreeBsdLibc.FreeBsdUtmpx ut;
/* 61 */       while ((ut = LIBC.getutxent()) != null) {
/* 62 */         if (ut.ut_type == 7 || ut.ut_type == 6) {
/* 63 */           String user = (new String(ut.ut_user, StandardCharsets.US_ASCII)).trim();
/* 64 */           String device = (new String(ut.ut_line, StandardCharsets.US_ASCII)).trim();
/* 65 */           String host = (new String(ut.ut_host, StandardCharsets.US_ASCII)).trim();
/* 66 */           long loginTime = ut.ut_tv.tv_sec * 1000L + ut.ut_tv.tv_usec / 1000L;
/*    */           
/* 68 */           if (user.isEmpty() || device.isEmpty() || loginTime < 0L || loginTime > System.currentTimeMillis()) {
/* 69 */             return oshi.driver.unix.Who.queryWho();
/*    */           }
/* 71 */           whoList.add(new OSSession(user, device, loginTime, host));
/*    */         } 
/*    */       } 
/*    */     } finally {
/*    */       
/* 76 */       LIBC.endutxent();
/*    */     } 
/* 78 */     return whoList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\freebsd\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */