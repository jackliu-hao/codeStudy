package com.mysql.cj.protocol.x;

import com.mysql.cj.CharsetSettings;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.ServerCapabilities;
import com.mysql.cj.protocol.ServerSession;
import java.util.Map;
import java.util.TimeZone;

public class XServerSession implements ServerSession {
   XServerCapabilities serverCapabilities = null;
   private TimeZone defaultTimeZone = TimeZone.getDefault();

   public ServerCapabilities getCapabilities() {
      return this.serverCapabilities;
   }

   public void setCapabilities(ServerCapabilities capabilities) {
      this.serverCapabilities = (XServerCapabilities)capabilities;
   }

   public int getStatusFlags() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void setStatusFlags(int statusFlags) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void setStatusFlags(int statusFlags, boolean saveOldStatusFlags) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public int getOldStatusFlags() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void setOldStatusFlags(int statusFlags) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public int getTransactionState() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean inTransactionOnServer() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean cursorExists() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isAutocommit() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean hasMoreResults() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isLastRowSent() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean noGoodIndexUsed() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean noIndexUsed() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean queryWasSlow() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public long getClientParam() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void setClientParam(long clientParam) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean hasLongColumnInfo() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean useMultiResults() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isEOFDeprecated() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean supportsQueryAttributes() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public Map<String, String> getServerVariables() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public String getServerVariable(String name) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public int getServerVariable(String variableName, int fallbackValue) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void setServerVariables(Map<String, String> serverVariables) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public ServerVersion getServerVersion() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isVersion(ServerVersion version) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isLowerCaseTableNames() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean storesLowerCaseTableNames() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isQueryCacheEnabled() {
      return false;
   }

   public boolean isNoBackslashEscapesSet() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean useAnsiQuotedIdentifiers() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isServerTruncatesFracSecs() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public boolean isAutoCommit() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void setAutoCommit(boolean autoCommit) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public TimeZone getSessionTimeZone() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public void setSessionTimeZone(TimeZone sessionTimeZone) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public TimeZone getDefaultTimeZone() {
      return this.defaultTimeZone;
   }

   public CharsetSettings getCharsetSettings() {
      return null;
   }

   public void setCharsetSettings(CharsetSettings charsetSettings) {
   }
}
