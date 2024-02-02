/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.CharsetSettings;
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.ServerCapabilities;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.protocol.ServerSessionStateController;
/*     */ import com.mysql.cj.util.TimeUtil;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
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
/*     */ public class NativeServerSession
/*     */   implements ServerSession
/*     */ {
/*     */   public static final int SERVER_STATUS_IN_TRANS = 1;
/*     */   public static final int SERVER_STATUS_AUTOCOMMIT = 2;
/*     */   public static final int SERVER_MORE_RESULTS_EXISTS = 8;
/*     */   public static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
/*     */   public static final int SERVER_QUERY_NO_INDEX_USED = 32;
/*     */   public static final int SERVER_STATUS_CURSOR_EXISTS = 64;
/*     */   public static final int SERVER_STATUS_LAST_ROW_SENT = 128;
/*     */   public static final int SERVER_QUERY_WAS_SLOW = 2048;
/*     */   public static final int SERVER_SESSION_STATE_CHANGED = 16384;
/*     */   public static final int CLIENT_LONG_PASSWORD = 1;
/*     */   public static final int CLIENT_FOUND_ROWS = 2;
/*     */   public static final int CLIENT_LONG_FLAG = 4;
/*     */   public static final int CLIENT_CONNECT_WITH_DB = 8;
/*     */   public static final int CLIENT_COMPRESS = 32;
/*     */   public static final int CLIENT_LOCAL_FILES = 128;
/*     */   public static final int CLIENT_PROTOCOL_41 = 512;
/*     */   public static final int CLIENT_INTERACTIVE = 1024;
/*     */   public static final int CLIENT_SSL = 2048;
/*     */   public static final int CLIENT_TRANSACTIONS = 8192;
/*     */   public static final int CLIENT_RESERVED = 16384;
/*     */   public static final int CLIENT_SECURE_CONNECTION = 32768;
/*     */   public static final int CLIENT_MULTI_STATEMENTS = 65536;
/*     */   public static final int CLIENT_MULTI_RESULTS = 131072;
/*     */   public static final int CLIENT_PS_MULTI_RESULTS = 262144;
/*     */   public static final int CLIENT_PLUGIN_AUTH = 524288;
/*     */   public static final int CLIENT_CONNECT_ATTRS = 1048576;
/*     */   public static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 2097152;
/*     */   public static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 4194304;
/*     */   public static final int CLIENT_SESSION_TRACK = 8388608;
/*     */   public static final int CLIENT_DEPRECATE_EOF = 16777216;
/*     */   public static final int CLIENT_QUERY_ATTRIBUTES = 134217728;
/*     */   public static final int CLIENT_MULTI_FACTOR_AUTHENTICATION = 268435456;
/*     */   private PropertySet propertySet;
/*     */   private NativeCapabilities capabilities;
/*  86 */   private int oldStatusFlags = 0;
/*  87 */   private int statusFlags = 0;
/*  88 */   private long clientParam = 0L;
/*     */   
/*     */   private NativeServerSessionStateController serverSessionStateController;
/*     */   
/*  92 */   private Map<String, String> serverVariables = new HashMap<>();
/*     */ 
/*     */   
/*     */   private CharsetSettings charsetSettings;
/*     */ 
/*     */   
/*     */   private boolean autoCommit = true;
/*     */   
/* 100 */   private TimeZone sessionTimeZone = null;
/*     */   
/* 102 */   private TimeZone defaultTimeZone = TimeZone.getDefault();
/*     */   
/* 104 */   private RuntimeProperty<Boolean> cacheDefaultTimeZone = null;
/*     */   
/*     */   public NativeServerSession(PropertySet propertySet) {
/* 107 */     this.propertySet = propertySet;
/* 108 */     this.cacheDefaultTimeZone = this.propertySet.getBooleanProperty(PropertyKey.cacheDefaultTimeZone);
/* 109 */     this.serverSessionStateController = new NativeServerSessionStateController();
/*     */   }
/*     */ 
/*     */   
/*     */   public NativeCapabilities getCapabilities() {
/* 114 */     return this.capabilities;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCapabilities(ServerCapabilities capabilities) {
/* 119 */     this.capabilities = (NativeCapabilities)capabilities;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStatusFlags() {
/* 124 */     return this.statusFlags;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusFlags(int statusFlags) {
/* 129 */     setStatusFlags(statusFlags, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusFlags(int statusFlags, boolean saveOldStatus) {
/* 134 */     if (saveOldStatus) {
/* 135 */       this.oldStatusFlags = this.statusFlags;
/*     */     }
/* 137 */     this.statusFlags = statusFlags;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOldStatusFlags() {
/* 142 */     return this.oldStatusFlags;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOldStatusFlags(int oldStatusFlags) {
/* 147 */     this.oldStatusFlags = oldStatusFlags;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTransactionState() {
/* 152 */     if ((this.oldStatusFlags & 0x1) == 0) {
/* 153 */       if ((this.statusFlags & 0x1) == 0) {
/* 154 */         return 0;
/*     */       }
/* 156 */       return 2;
/*     */     } 
/* 158 */     if ((this.statusFlags & 0x1) == 0) {
/* 159 */       return 3;
/*     */     }
/* 161 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean inTransactionOnServer() {
/* 166 */     return ((this.statusFlags & 0x1) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean cursorExists() {
/* 171 */     return ((this.statusFlags & 0x40) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAutocommit() {
/* 176 */     return ((this.statusFlags & 0x2) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMoreResults() {
/* 181 */     return ((this.statusFlags & 0x8) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean noGoodIndexUsed() {
/* 186 */     return ((this.statusFlags & 0x10) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean noIndexUsed() {
/* 191 */     return ((this.statusFlags & 0x20) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean queryWasSlow() {
/* 196 */     return ((this.statusFlags & 0x800) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLastRowSent() {
/* 201 */     return ((this.statusFlags & 0x80) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getClientParam() {
/* 206 */     return this.clientParam;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClientParam(long clientParam) {
/* 211 */     this.clientParam = clientParam;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasLongColumnInfo() {
/* 216 */     return ((this.clientParam & 0x4L) != 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean useMultiResults() {
/* 221 */     return ((this.clientParam & 0x20000L) != 0L || (this.clientParam & 0x40000L) != 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEOFDeprecated() {
/* 226 */     return ((this.clientParam & 0x1000000L) != 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsQueryAttributes() {
/* 231 */     return ((this.clientParam & 0x8000000L) != 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> getServerVariables() {
/* 236 */     return this.serverVariables;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getServerVariable(String name) {
/* 241 */     return this.serverVariables.get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getServerVariable(String variableName, int fallbackValue) {
/*     */     try {
/* 247 */       return Integer.valueOf(getServerVariable(variableName)).intValue();
/* 248 */     } catch (NumberFormatException numberFormatException) {
/*     */ 
/*     */ 
/*     */       
/* 252 */       return fallbackValue;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setServerVariables(Map<String, String> serverVariables) {
/* 257 */     this.serverVariables = serverVariables;
/*     */   }
/*     */   
/*     */   public final ServerVersion getServerVersion() {
/* 261 */     return this.capabilities.getServerVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVersion(ServerVersion version) {
/* 266 */     return getServerVersion().equals(version);
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
/*     */   public boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag, boolean elideSetAutoCommitsFlag) {
/* 279 */     if (elideSetAutoCommitsFlag) {
/* 280 */       boolean autoCommitModeOnServer = isAutocommit();
/*     */       
/* 282 */       if (autoCommitModeOnServer && !autoCommitFlag)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 287 */         return !inTransactionOnServer();
/*     */       }
/*     */       
/* 290 */       return (autoCommitModeOnServer != autoCommitFlag);
/*     */     } 
/*     */     
/* 293 */     return true;
/*     */   }
/*     */   
/*     */   public void preserveOldTransactionState() {
/* 297 */     this.statusFlags |= this.oldStatusFlags & 0x1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLowerCaseTableNames() {
/* 302 */     String lowerCaseTables = this.serverVariables.get("lower_case_table_names");
/* 303 */     return ("on".equalsIgnoreCase(lowerCaseTables) || "1".equalsIgnoreCase(lowerCaseTables) || "2".equalsIgnoreCase(lowerCaseTables));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean storesLowerCaseTableNames() {
/* 308 */     String lowerCaseTables = this.serverVariables.get("lower_case_table_names");
/* 309 */     return ("1".equalsIgnoreCase(lowerCaseTables) || "on".equalsIgnoreCase(lowerCaseTables));
/*     */   }
/*     */   
/*     */   public boolean isQueryCacheEnabled() {
/* 313 */     return ("ON".equalsIgnoreCase(this.serverVariables.get("query_cache_type")) && !"0".equalsIgnoreCase(this.serverVariables.get("query_cache_size")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNoBackslashEscapesSet() {
/* 322 */     String sqlModeAsString = this.serverVariables.get("sql_mode");
/* 323 */     return (sqlModeAsString != null && sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1);
/*     */   }
/*     */   
/*     */   public boolean useAnsiQuotedIdentifiers() {
/* 327 */     String sqlModeAsString = this.serverVariables.get("sql_mode");
/* 328 */     return (sqlModeAsString != null && sqlModeAsString.indexOf("ANSI_QUOTES") != -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isServerTruncatesFracSecs() {
/* 333 */     String sqlModeAsString = this.serverVariables.get("sql_mode");
/* 334 */     return (sqlModeAsString != null && sqlModeAsString.indexOf("TIME_TRUNCATE_FRACTIONAL") != -1);
/*     */   }
/*     */   
/*     */   public boolean isAutoCommit() {
/* 338 */     return this.autoCommit;
/*     */   }
/*     */   
/*     */   public void setAutoCommit(boolean autoCommit) {
/* 342 */     this.autoCommit = autoCommit;
/*     */   }
/*     */   
/*     */   public TimeZone getSessionTimeZone() {
/* 346 */     if (this.sessionTimeZone == null) {
/* 347 */       String configuredTimeZoneOnServer = getServerVariable("time_zone");
/* 348 */       if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
/* 349 */         configuredTimeZoneOnServer = getServerVariable("system_time_zone");
/*     */       }
/* 351 */       if (configuredTimeZoneOnServer != null) {
/*     */         try {
/* 353 */           this.sessionTimeZone = TimeZone.getTimeZone(TimeUtil.getCanonicalTimeZone(configuredTimeZoneOnServer, null));
/* 354 */         } catch (IllegalArgumentException iae) {
/* 355 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, iae.getMessage());
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 360 */     return this.sessionTimeZone;
/*     */   }
/*     */   
/*     */   public void setSessionTimeZone(TimeZone sessionTimeZone) {
/* 364 */     this.sessionTimeZone = sessionTimeZone;
/*     */   }
/*     */   
/*     */   public TimeZone getDefaultTimeZone() {
/* 368 */     if (((Boolean)this.cacheDefaultTimeZone.getValue()).booleanValue()) {
/* 369 */       return this.defaultTimeZone;
/*     */     }
/* 371 */     return TimeZone.getDefault();
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerSessionStateController getServerSessionStateController() {
/* 376 */     return this.serverSessionStateController;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharsetSettings getCharsetSettings() {
/* 381 */     return this.charsetSettings;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharsetSettings(CharsetSettings charsetSettings) {
/* 386 */     this.charsetSettings = charsetSettings;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeServerSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */