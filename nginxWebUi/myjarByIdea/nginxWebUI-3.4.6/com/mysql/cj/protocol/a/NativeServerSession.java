package com.mysql.cj.protocol.a;

import com.mysql.cj.CharsetSettings;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ServerCapabilities;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.ServerSessionStateController;
import com.mysql.cj.util.TimeUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class NativeServerSession implements ServerSession {
   public static final int SERVER_STATUS_IN_TRANS = 1;
   public static final int SERVER_STATUS_AUTOCOMMIT = 2;
   public static final int SERVER_MORE_RESULTS_EXISTS = 8;
   public static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
   public static final int SERVER_QUERY_NO_INDEX_USED = 32;
   public static final int SERVER_STATUS_CURSOR_EXISTS = 64;
   public static final int SERVER_STATUS_LAST_ROW_SENT = 128;
   public static final int SERVER_QUERY_WAS_SLOW = 2048;
   public static final int SERVER_SESSION_STATE_CHANGED = 16384;
   public static final int CLIENT_LONG_PASSWORD = 1;
   public static final int CLIENT_FOUND_ROWS = 2;
   public static final int CLIENT_LONG_FLAG = 4;
   public static final int CLIENT_CONNECT_WITH_DB = 8;
   public static final int CLIENT_COMPRESS = 32;
   public static final int CLIENT_LOCAL_FILES = 128;
   public static final int CLIENT_PROTOCOL_41 = 512;
   public static final int CLIENT_INTERACTIVE = 1024;
   public static final int CLIENT_SSL = 2048;
   public static final int CLIENT_TRANSACTIONS = 8192;
   public static final int CLIENT_RESERVED = 16384;
   public static final int CLIENT_SECURE_CONNECTION = 32768;
   public static final int CLIENT_MULTI_STATEMENTS = 65536;
   public static final int CLIENT_MULTI_RESULTS = 131072;
   public static final int CLIENT_PS_MULTI_RESULTS = 262144;
   public static final int CLIENT_PLUGIN_AUTH = 524288;
   public static final int CLIENT_CONNECT_ATTRS = 1048576;
   public static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 2097152;
   public static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 4194304;
   public static final int CLIENT_SESSION_TRACK = 8388608;
   public static final int CLIENT_DEPRECATE_EOF = 16777216;
   public static final int CLIENT_QUERY_ATTRIBUTES = 134217728;
   public static final int CLIENT_MULTI_FACTOR_AUTHENTICATION = 268435456;
   private PropertySet propertySet;
   private NativeCapabilities capabilities;
   private int oldStatusFlags = 0;
   private int statusFlags = 0;
   private long clientParam = 0L;
   private NativeServerSessionStateController serverSessionStateController;
   private Map<String, String> serverVariables = new HashMap();
   private CharsetSettings charsetSettings;
   private boolean autoCommit = true;
   private TimeZone sessionTimeZone = null;
   private TimeZone defaultTimeZone = TimeZone.getDefault();
   private RuntimeProperty<Boolean> cacheDefaultTimeZone = null;

   public NativeServerSession(PropertySet propertySet) {
      this.propertySet = propertySet;
      this.cacheDefaultTimeZone = this.propertySet.getBooleanProperty(PropertyKey.cacheDefaultTimeZone);
      this.serverSessionStateController = new NativeServerSessionStateController();
   }

   public NativeCapabilities getCapabilities() {
      return this.capabilities;
   }

   public void setCapabilities(ServerCapabilities capabilities) {
      this.capabilities = (NativeCapabilities)capabilities;
   }

   public int getStatusFlags() {
      return this.statusFlags;
   }

   public void setStatusFlags(int statusFlags) {
      this.setStatusFlags(statusFlags, false);
   }

   public void setStatusFlags(int statusFlags, boolean saveOldStatus) {
      if (saveOldStatus) {
         this.oldStatusFlags = this.statusFlags;
      }

      this.statusFlags = statusFlags;
   }

   public int getOldStatusFlags() {
      return this.oldStatusFlags;
   }

   public void setOldStatusFlags(int oldStatusFlags) {
      this.oldStatusFlags = oldStatusFlags;
   }

   public int getTransactionState() {
      if ((this.oldStatusFlags & 1) == 0) {
         return (this.statusFlags & 1) == 0 ? 0 : 2;
      } else {
         return (this.statusFlags & 1) == 0 ? 3 : 1;
      }
   }

   public boolean inTransactionOnServer() {
      return (this.statusFlags & 1) != 0;
   }

   public boolean cursorExists() {
      return (this.statusFlags & 64) != 0;
   }

   public boolean isAutocommit() {
      return (this.statusFlags & 2) != 0;
   }

   public boolean hasMoreResults() {
      return (this.statusFlags & 8) != 0;
   }

   public boolean noGoodIndexUsed() {
      return (this.statusFlags & 16) != 0;
   }

   public boolean noIndexUsed() {
      return (this.statusFlags & 32) != 0;
   }

   public boolean queryWasSlow() {
      return (this.statusFlags & 2048) != 0;
   }

   public boolean isLastRowSent() {
      return (this.statusFlags & 128) != 0;
   }

   public long getClientParam() {
      return this.clientParam;
   }

   public void setClientParam(long clientParam) {
      this.clientParam = clientParam;
   }

   public boolean hasLongColumnInfo() {
      return (this.clientParam & 4L) != 0L;
   }

   public boolean useMultiResults() {
      return (this.clientParam & 131072L) != 0L || (this.clientParam & 262144L) != 0L;
   }

   public boolean isEOFDeprecated() {
      return (this.clientParam & 16777216L) != 0L;
   }

   public boolean supportsQueryAttributes() {
      return (this.clientParam & 134217728L) != 0L;
   }

   public Map<String, String> getServerVariables() {
      return this.serverVariables;
   }

   public String getServerVariable(String name) {
      return (String)this.serverVariables.get(name);
   }

   public int getServerVariable(String variableName, int fallbackValue) {
      try {
         return Integer.valueOf(this.getServerVariable(variableName));
      } catch (NumberFormatException var4) {
         return fallbackValue;
      }
   }

   public void setServerVariables(Map<String, String> serverVariables) {
      this.serverVariables = serverVariables;
   }

   public final ServerVersion getServerVersion() {
      return this.capabilities.getServerVersion();
   }

   public boolean isVersion(ServerVersion version) {
      return this.getServerVersion().equals(version);
   }

   public boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag, boolean elideSetAutoCommitsFlag) {
      if (elideSetAutoCommitsFlag) {
         boolean autoCommitModeOnServer = this.isAutocommit();
         if (autoCommitModeOnServer && !autoCommitFlag) {
            return !this.inTransactionOnServer();
         } else {
            return autoCommitModeOnServer != autoCommitFlag;
         }
      } else {
         return true;
      }
   }

   public void preserveOldTransactionState() {
      this.statusFlags |= this.oldStatusFlags & 1;
   }

   public boolean isLowerCaseTableNames() {
      String lowerCaseTables = (String)this.serverVariables.get("lower_case_table_names");
      return "on".equalsIgnoreCase(lowerCaseTables) || "1".equalsIgnoreCase(lowerCaseTables) || "2".equalsIgnoreCase(lowerCaseTables);
   }

   public boolean storesLowerCaseTableNames() {
      String lowerCaseTables = (String)this.serverVariables.get("lower_case_table_names");
      return "1".equalsIgnoreCase(lowerCaseTables) || "on".equalsIgnoreCase(lowerCaseTables);
   }

   public boolean isQueryCacheEnabled() {
      return "ON".equalsIgnoreCase((String)this.serverVariables.get("query_cache_type")) && !"0".equalsIgnoreCase((String)this.serverVariables.get("query_cache_size"));
   }

   public boolean isNoBackslashEscapesSet() {
      String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
      return sqlModeAsString != null && sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1;
   }

   public boolean useAnsiQuotedIdentifiers() {
      String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
      return sqlModeAsString != null && sqlModeAsString.indexOf("ANSI_QUOTES") != -1;
   }

   public boolean isServerTruncatesFracSecs() {
      String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
      return sqlModeAsString != null && sqlModeAsString.indexOf("TIME_TRUNCATE_FRACTIONAL") != -1;
   }

   public boolean isAutoCommit() {
      return this.autoCommit;
   }

   public void setAutoCommit(boolean autoCommit) {
      this.autoCommit = autoCommit;
   }

   public TimeZone getSessionTimeZone() {
      if (this.sessionTimeZone == null) {
         String configuredTimeZoneOnServer = this.getServerVariable("time_zone");
         if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
            configuredTimeZoneOnServer = this.getServerVariable("system_time_zone");
         }

         if (configuredTimeZoneOnServer != null) {
            try {
               this.sessionTimeZone = TimeZone.getTimeZone(TimeUtil.getCanonicalTimeZone(configuredTimeZoneOnServer, (ExceptionInterceptor)null));
            } catch (IllegalArgumentException var3) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, var3.getMessage());
            }
         }
      }

      return this.sessionTimeZone;
   }

   public void setSessionTimeZone(TimeZone sessionTimeZone) {
      this.sessionTimeZone = sessionTimeZone;
   }

   public TimeZone getDefaultTimeZone() {
      return (Boolean)this.cacheDefaultTimeZone.getValue() ? this.defaultTimeZone : TimeZone.getDefault();
   }

   public ServerSessionStateController getServerSessionStateController() {
      return this.serverSessionStateController;
   }

   public CharsetSettings getCharsetSettings() {
      return this.charsetSettings;
   }

   public void setCharsetSettings(CharsetSettings charsetSettings) {
      this.charsetSettings = charsetSettings;
   }
}
