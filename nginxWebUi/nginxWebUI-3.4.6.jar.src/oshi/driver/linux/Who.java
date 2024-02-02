/*    */ package oshi.driver.linux;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.jna.platform.linux.LinuxLibc;
/*    */ import oshi.software.os.OSSession;
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
/*    */ @ThreadSafe
/*    */ public final class Who
/*    */ {
/* 45 */   private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;
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
/* 56 */     List<OSSession> whoList = new ArrayList<>();
/*    */ 
/*    */     
/* 59 */     LIBC.setutxent();
/*    */     try {
/*    */       LinuxLibc.LinuxUtmpx ut;
/* 62 */       while ((ut = LIBC.getutxent()) != null) {
/* 63 */         if (ut.ut_type == 7 || ut.ut_type == 6) {
/* 64 */           String user = (new String(ut.ut_user, Charset.defaultCharset())).trim();
/* 65 */           String device = (new String(ut.ut_line, Charset.defaultCharset())).trim();
/* 66 */           String host = ParseUtil.parseUtAddrV6toIP(ut.ut_addr_v6);
/* 67 */           long loginTime = ut.ut_tv.tv_sec * 1000L + ut.ut_tv.tv_usec / 1000L;
/*    */           
/* 69 */           if (user.isEmpty() || device.isEmpty() || loginTime < 0L || loginTime > System.currentTimeMillis()) {
/* 70 */             return oshi.driver.unix.Who.queryWho();
/*    */           }
/* 72 */           whoList.add(new OSSession(user, device, loginTime, host));
/*    */         } 
/*    */       } 
/*    */     } finally {
/*    */       
/* 77 */       LIBC.endutxent();
/*    */     } 
/* 79 */     return whoList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */