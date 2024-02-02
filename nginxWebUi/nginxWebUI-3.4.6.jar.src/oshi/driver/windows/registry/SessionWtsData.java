/*     */ package oshi.driver.windows.registry;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.platform.win32.Wtsapi32;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.IntBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.os.OSSession;
/*     */ import oshi.util.ParseUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class SessionWtsData
/*     */ {
/*     */   private static final int WTS_ACTIVE = 0;
/*     */   private static final int WTS_CLIENTADDRESS = 14;
/*     */   private static final int WTS_SESSIONINFO = 24;
/*     */   private static final int WTS_CLIENTPROTOCOLTYPE = 16;
/*  63 */   private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
/*     */   
/*  65 */   private static final Wtsapi32 WTS = Wtsapi32.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<OSSession> queryUserSessions() {
/*  71 */     List<OSSession> sessions = new ArrayList<>();
/*  72 */     if (IS_VISTA_OR_GREATER) {
/*  73 */       PointerByReference ppSessionInfo = new PointerByReference();
/*  74 */       IntByReference pCount = new IntByReference();
/*  75 */       if (WTS.WTSEnumerateSessions(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, 0, 1, ppSessionInfo, pCount)) {
/*  76 */         Pointer pSessionInfo = ppSessionInfo.getValue();
/*  77 */         if (pCount.getValue() > 0) {
/*  78 */           Wtsapi32.WTS_SESSION_INFO sessionInfoRef = new Wtsapi32.WTS_SESSION_INFO(pSessionInfo);
/*  79 */           Wtsapi32.WTS_SESSION_INFO[] sessionInfo = (Wtsapi32.WTS_SESSION_INFO[])sessionInfoRef.toArray(pCount.getValue());
/*  80 */           for (Wtsapi32.WTS_SESSION_INFO session : sessionInfo) {
/*  81 */             if (session.State == 0) {
/*     */               
/*  83 */               PointerByReference ppBuffer = new PointerByReference();
/*  84 */               IntByReference pBytes = new IntByReference();
/*  85 */               WTS.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, session.SessionId, 16, ppBuffer, pBytes);
/*     */               
/*  87 */               Pointer pBuffer = ppBuffer.getValue();
/*  88 */               short protocolType = pBuffer.getShort(0L);
/*  89 */               WTS.WTSFreeMemory(pBuffer);
/*     */               
/*  91 */               if (protocolType > 0) {
/*     */                 
/*  93 */                 String device = session.pWinStationName;
/*     */                 
/*  95 */                 WTS.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, session.SessionId, 24, ppBuffer, pBytes);
/*     */                 
/*  97 */                 pBuffer = ppBuffer.getValue();
/*  98 */                 Wtsapi32.WTSINFO wtsInfo = new Wtsapi32.WTSINFO(pBuffer);
/*     */ 
/*     */                 
/* 101 */                 long logonTime = (new WinBase.FILETIME(new WinNT.LARGE_INTEGER(wtsInfo.LogonTime.getValue()))).toTime();
/* 102 */                 String userName = wtsInfo.getUserName();
/* 103 */                 WTS.WTSFreeMemory(pBuffer);
/*     */                 
/* 105 */                 WTS.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, session.SessionId, 14, ppBuffer, pBytes);
/*     */                 
/* 107 */                 pBuffer = ppBuffer.getValue();
/* 108 */                 Wtsapi32.WTS_CLIENT_ADDRESS addr = new Wtsapi32.WTS_CLIENT_ADDRESS(pBuffer);
/* 109 */                 WTS.WTSFreeMemory(pBuffer);
/* 110 */                 String host = "::";
/* 111 */                 if (addr.AddressFamily == 2) {
/*     */                   
/*     */                   try {
/* 114 */                     host = InetAddress.getByAddress(Arrays.copyOfRange(addr.Address, 2, 6)).getHostAddress();
/* 115 */                   } catch (UnknownHostException e) {
/*     */                     
/* 117 */                     host = "Illegal length IP Array";
/*     */                   } 
/* 119 */                 } else if (addr.AddressFamily == 23) {
/*     */                   
/* 121 */                   int[] ipArray = convertBytesToInts(addr.Address);
/* 122 */                   host = ParseUtil.parseUtAddrV6toIP(ipArray);
/*     */                 } 
/* 124 */                 sessions.add(new OSSession(userName, device, logonTime, host));
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/* 129 */         WTS.WTSFreeMemory(pSessionInfo);
/*     */       } 
/*     */     } 
/* 132 */     return sessions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int[] convertBytesToInts(byte[] address) {
/* 148 */     IntBuffer intBuf = ByteBuffer.wrap(Arrays.copyOfRange(address, 2, 18)).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
/* 149 */     int[] array = new int[intBuf.remaining()];
/* 150 */     intBuf.get(array);
/* 151 */     return array;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\registry\SessionWtsData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */