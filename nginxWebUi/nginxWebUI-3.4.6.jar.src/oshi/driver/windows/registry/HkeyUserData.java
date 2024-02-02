/*     */ package oshi.driver.windows.registry;
/*     */ 
/*     */ import com.sun.jna.platform.win32.Advapi32Util;
/*     */ import com.sun.jna.platform.win32.Win32Exception;
/*     */ import com.sun.jna.platform.win32.WinReg;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.os.OSSession;
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
/*     */ public final class HkeyUserData
/*     */ {
/*     */   private static final String DEFAULT_DEVICE = "Console";
/*     */   private static final String VOLATILE_ENV_SUBKEY = "Volatile Environment";
/*     */   private static final String CLIENTNAME = "CLIENTNAME";
/*     */   private static final String SESSIONNAME = "SESSIONNAME";
/*  54 */   private static final Logger LOG = LoggerFactory.getLogger(HkeyUserData.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<OSSession> queryUserSessions() {
/*  60 */     List<OSSession> sessions = new ArrayList<>();
/*  61 */     for (String sidKey : Advapi32Util.registryGetKeys(WinReg.HKEY_USERS)) {
/*  62 */       if (!sidKey.startsWith(".") && !sidKey.endsWith("_Classes")) {
/*     */         try {
/*  64 */           Advapi32Util.Account a = Advapi32Util.getAccountBySid(sidKey);
/*  65 */           String name = a.name;
/*  66 */           String device = "Console";
/*  67 */           String host = a.domain;
/*  68 */           long loginTime = 0L;
/*  69 */           String keyPath = sidKey + "\\" + "Volatile Environment";
/*  70 */           if (Advapi32Util.registryKeyExists(WinReg.HKEY_USERS, keyPath)) {
/*  71 */             WinReg.HKEY hKey = Advapi32Util.registryGetKey(WinReg.HKEY_USERS, keyPath, 131097).getValue();
/*     */             
/*  73 */             Advapi32Util.InfoKey info = Advapi32Util.registryQueryInfoKey(hKey, 0);
/*  74 */             loginTime = info.lpftLastWriteTime.toTime();
/*  75 */             for (String subKey : Advapi32Util.registryGetKeys(hKey)) {
/*  76 */               String subKeyPath = keyPath + "\\" + subKey;
/*     */               
/*  78 */               if (Advapi32Util.registryValueExists(WinReg.HKEY_USERS, subKeyPath, "SESSIONNAME")) {
/*  79 */                 String session = Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, subKeyPath, "SESSIONNAME");
/*     */                 
/*  81 */                 if (!session.isEmpty()) {
/*  82 */                   device = session;
/*     */                 }
/*     */               } 
/*  85 */               if (Advapi32Util.registryValueExists(WinReg.HKEY_USERS, subKeyPath, "CLIENTNAME")) {
/*  86 */                 String client = Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, subKeyPath, "CLIENTNAME");
/*     */                 
/*  88 */                 if (!client.isEmpty() && !"Console".equals(client)) {
/*  89 */                   host = client;
/*     */                 }
/*     */               } 
/*     */             } 
/*  93 */             Advapi32Util.registryCloseKey(hKey);
/*     */           } 
/*  95 */           sessions.add(new OSSession(name, device, loginTime, host));
/*  96 */         } catch (Win32Exception ex) {
/*  97 */           LOG.warn("Error querying SID {} from registry: {}", sidKey, ex.getMessage());
/*     */         } 
/*     */       }
/*     */     } 
/* 101 */     return sessions;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\registry\HkeyUserData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */