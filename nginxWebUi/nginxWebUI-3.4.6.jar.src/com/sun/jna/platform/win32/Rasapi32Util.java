/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Rasapi32Util
/*     */ {
/*     */   private static final int RASP_PppIp = 32801;
/*  47 */   private static Object phoneBookMutex = new Object();
/*     */   
/*  49 */   public static final Map CONNECTION_STATE_TEXT = new HashMap<Object, Object>();
/*     */   
/*     */   static {
/*  52 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(0), "Opening the port...");
/*  53 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(1), "Port has been opened successfully");
/*  54 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(2), "Connecting to the device...");
/*  55 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(3), "The device has connected successfully.");
/*  56 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(4), "All devices in the device chain have successfully connected.");
/*  57 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(5), "Verifying the user name and password...");
/*  58 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(6), "An authentication event has occurred.");
/*  59 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(7), "Requested another validation attempt with a new user.");
/*  60 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(8), "Server has requested a callback number.");
/*  61 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(9), "The client has requested to change the password");
/*  62 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(10), "Registering your computer on the network...");
/*  63 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(11), "The link-speed calculation phase is starting...");
/*  64 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(12), "An authentication request is being acknowledged.");
/*  65 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(13), "Reauthentication (after callback) is starting.");
/*  66 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(14), "The client has successfully completed authentication.");
/*  67 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(15), "The line is about to disconnect for callback.");
/*  68 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(16), "Delaying to give the modem time to reset for callback.");
/*  69 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(17), "Waiting for an incoming call from server.");
/*  70 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(18), "Projection result information is available.");
/*  71 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(19), "User authentication is being initiated or retried.");
/*  72 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(20), "Client has been called back and is about to resume authentication.");
/*  73 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(21), "Logging on to the network...");
/*  74 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(22), "Subentry has been connected");
/*  75 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(23), "Subentry has been disconnected");
/*  76 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(4096), "Terminal state supported by RASPHONE.EXE.");
/*  77 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(4097), "Retry authentication state supported by RASPHONE.EXE.");
/*  78 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(4098), "Callback state supported by RASPHONE.EXE.");
/*  79 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(4099), "Change password state supported by RASPHONE.EXE.");
/*  80 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(4100), "Displaying authentication UI");
/*  81 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(8192), "Connected to remote server successfully");
/*  82 */     CONNECTION_STATE_TEXT.put(Integer.valueOf(8193), "Disconnected");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Ras32Exception
/*     */     extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     private final int code;
/*     */ 
/*     */ 
/*     */     
/*     */     public int getCode() {
/* 100 */       return this.code;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Ras32Exception(int code) {
/* 109 */       super(Rasapi32Util.getRasErrorString(code));
/* 110 */       this.code = code;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getRasErrorString(int code) {
/* 120 */     char[] msg = new char[1024];
/* 121 */     int err = Rasapi32.INSTANCE.RasGetErrorString(code, msg, msg.length);
/* 122 */     if (err != 0) return "Unknown error " + code; 
/* 123 */     int len = 0;
/* 124 */     for (; len < msg.length && msg[len] != '\000'; len++);
/* 125 */     return new String(msg, 0, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getRasConnectionStatusText(int connStatus) {
/* 134 */     if (!CONNECTION_STATE_TEXT.containsKey(Integer.valueOf(connStatus))) return Integer.toString(connStatus); 
/* 135 */     return (String)CONNECTION_STATE_TEXT.get(Integer.valueOf(connStatus));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WinNT.HANDLE getRasConnection(String connName) throws Ras32Exception {
/* 146 */     IntByReference lpcb = new IntByReference(0);
/* 147 */     IntByReference lpcConnections = new IntByReference();
/* 148 */     int err = Rasapi32.INSTANCE.RasEnumConnections(null, lpcb, lpcConnections);
/* 149 */     if (err != 0 && err != 603) throw new Ras32Exception(err); 
/* 150 */     if (lpcb.getValue() == 0) return null;
/*     */ 
/*     */     
/* 153 */     WinRas.RASCONN[] connections = new WinRas.RASCONN[lpcConnections.getValue()]; int i;
/* 154 */     for (i = 0; i < lpcConnections.getValue(); ) { connections[i] = new WinRas.RASCONN(); i++; }
/* 155 */      lpcb = new IntByReference((connections[0]).dwSize * lpcConnections.getValue());
/* 156 */     err = Rasapi32.INSTANCE.RasEnumConnections(connections, lpcb, lpcConnections);
/* 157 */     if (err != 0) throw new Ras32Exception(err);
/*     */ 
/*     */     
/* 160 */     for (i = 0; i < lpcConnections.getValue(); i++) {
/* 161 */       if ((new String((connections[i]).szEntryName)).equals(connName)) return (connections[i]).hrasconn; 
/*     */     } 
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void hangupRasConnection(String connName) throws Ras32Exception {
/* 172 */     WinNT.HANDLE hrasConn = getRasConnection(connName);
/* 173 */     if (hrasConn == null)
/* 174 */       return;  int err = Rasapi32.INSTANCE.RasHangUp(hrasConn);
/* 175 */     if (err != 0) throw new Ras32Exception(err);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void hangupRasConnection(WinNT.HANDLE hrasConn) throws Ras32Exception {
/* 184 */     if (hrasConn == null)
/* 185 */       return;  int err = Rasapi32.INSTANCE.RasHangUp(hrasConn);
/* 186 */     if (err != 0) throw new Ras32Exception(err);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WinRas.RASPPPIP getIPProjection(WinNT.HANDLE hrasConn) throws Ras32Exception {
/* 196 */     WinRas.RASPPPIP pppIpProjection = new WinRas.RASPPPIP();
/* 197 */     IntByReference lpcb = new IntByReference(pppIpProjection.size());
/* 198 */     pppIpProjection.write();
/* 199 */     int err = Rasapi32.INSTANCE.RasGetProjectionInfo(hrasConn, 32801, pppIpProjection.getPointer(), lpcb);
/* 200 */     if (err != 0) throw new Ras32Exception(err); 
/* 201 */     pppIpProjection.read();
/* 202 */     return pppIpProjection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WinRas.RASENTRY.ByReference getPhoneBookEntry(String entryName) throws Ras32Exception {
/* 212 */     synchronized (phoneBookMutex) {
/* 213 */       WinRas.RASENTRY.ByReference rasEntry = new WinRas.RASENTRY.ByReference();
/* 214 */       IntByReference lpdwEntryInfoSize = new IntByReference(rasEntry.size());
/* 215 */       int err = Rasapi32.INSTANCE.RasGetEntryProperties(null, entryName, rasEntry, lpdwEntryInfoSize, null, null);
/* 216 */       if (err != 0) throw new Ras32Exception(err); 
/* 217 */       return rasEntry;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setPhoneBookEntry(String entryName, WinRas.RASENTRY.ByReference rasEntry) throws Ras32Exception {
/* 228 */     synchronized (phoneBookMutex) {
/* 229 */       int err = Rasapi32.INSTANCE.RasSetEntryProperties(null, entryName, rasEntry, rasEntry.size(), null, 0);
/* 230 */       if (err != 0) throw new Ras32Exception(err);
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WinRas.RASDIALPARAMS getPhoneBookDialingParams(String entryName) throws Ras32Exception {
/* 241 */     synchronized (phoneBookMutex) {
/* 242 */       WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
/* 243 */       System.arraycopy(rasDialParams.szEntryName, 0, entryName.toCharArray(), 0, entryName.length());
/* 244 */       WinDef.BOOLByReference lpfPassword = new WinDef.BOOLByReference();
/* 245 */       int err = Rasapi32.INSTANCE.RasGetEntryDialParams(null, rasDialParams, lpfPassword);
/* 246 */       if (err != 0) throw new Ras32Exception(err); 
/* 247 */       return rasDialParams;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WinNT.HANDLE dialEntry(String entryName) throws Ras32Exception {
/* 259 */     WinRas.RASCREDENTIALS.ByReference credentials = new WinRas.RASCREDENTIALS.ByReference();
/* 260 */     synchronized (phoneBookMutex) {
/* 261 */       credentials.dwMask = 7;
/* 262 */       int i = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
/* 263 */       if (i != 0) throw new Ras32Exception(i);
/*     */     
/*     */     } 
/*     */     
/* 267 */     WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
/* 268 */     System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
/* 269 */     System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
/* 270 */     System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
/* 271 */     System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);
/*     */ 
/*     */     
/* 274 */     WinNT.HANDLEByReference hrasConn = new WinNT.HANDLEByReference();
/* 275 */     int err = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 0, null, hrasConn);
/* 276 */     if (err != 0) {
/* 277 */       if (hrasConn.getValue() != null) Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue()); 
/* 278 */       throw new Ras32Exception(err);
/*     */     } 
/* 280 */     return hrasConn.getValue();
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
/*     */   public static WinNT.HANDLE dialEntry(String entryName, WinRas.RasDialFunc2 func2) throws Ras32Exception {
/* 292 */     WinRas.RASCREDENTIALS.ByReference credentials = new WinRas.RASCREDENTIALS.ByReference();
/* 293 */     synchronized (phoneBookMutex) {
/* 294 */       credentials.dwMask = 7;
/* 295 */       int i = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
/* 296 */       if (i != 0) throw new Ras32Exception(i);
/*     */     
/*     */     } 
/*     */     
/* 300 */     WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
/* 301 */     System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
/* 302 */     System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
/* 303 */     System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
/* 304 */     System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);
/*     */ 
/*     */     
/* 307 */     WinNT.HANDLEByReference hrasConn = new WinNT.HANDLEByReference();
/* 308 */     int err = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 2, func2, hrasConn);
/* 309 */     if (err != 0) {
/* 310 */       if (hrasConn.getValue() != null) Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue()); 
/* 311 */       throw new Ras32Exception(err);
/*     */     } 
/* 313 */     return hrasConn.getValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Rasapi32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */