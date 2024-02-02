/*    */ package oshi.driver.windows.registry;
/*    */ 
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.platform.win32.Netapi32;
/*    */ import com.sun.jna.ptr.IntByReference;
/*    */ import com.sun.jna.ptr.PointerByReference;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
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
/*    */ @ThreadSafe
/*    */ public final class NetSessionData
/*    */ {
/* 45 */   private static final Netapi32 NET = Netapi32.INSTANCE;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<OSSession> queryUserSessions() {
/* 51 */     List<OSSession> sessions = new ArrayList<>();
/* 52 */     PointerByReference bufptr = new PointerByReference();
/* 53 */     IntByReference entriesread = new IntByReference();
/* 54 */     IntByReference totalentries = new IntByReference();
/* 55 */     if (0 == NET.NetSessionEnum(null, null, null, 10, bufptr, -1, entriesread, totalentries, null)) {
/*    */       
/* 57 */       Pointer buf = bufptr.getValue();
/* 58 */       Netapi32.SESSION_INFO_10 si10 = new Netapi32.SESSION_INFO_10(buf);
/* 59 */       if (entriesread.getValue() > 0) {
/* 60 */         Netapi32.SESSION_INFO_10[] sessionInfo = (Netapi32.SESSION_INFO_10[])si10.toArray(entriesread.getValue());
/* 61 */         for (Netapi32.SESSION_INFO_10 si : sessionInfo) {
/*    */           
/* 63 */           long logonTime = System.currentTimeMillis() - 1000L * si.sesi10_time;
/* 64 */           sessions.add(new OSSession(si.sesi10_username, "Network session", logonTime, si.sesi10_cname));
/*    */         } 
/*    */       } 
/* 67 */       NET.NetApiBufferFree(buf);
/*    */     } 
/* 69 */     return sessions;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\registry\NetSessionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */