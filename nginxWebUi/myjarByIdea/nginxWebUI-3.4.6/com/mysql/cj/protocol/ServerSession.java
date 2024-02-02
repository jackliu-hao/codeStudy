package com.mysql.cj.protocol;

import com.mysql.cj.CharsetSettings;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import java.util.Map;
import java.util.TimeZone;

public interface ServerSession {
   int TRANSACTION_NOT_STARTED = 0;
   int TRANSACTION_IN_PROGRESS = 1;
   int TRANSACTION_STARTED = 2;
   int TRANSACTION_COMPLETED = 3;

   ServerCapabilities getCapabilities();

   void setCapabilities(ServerCapabilities var1);

   int getStatusFlags();

   void setStatusFlags(int var1);

   void setStatusFlags(int var1, boolean var2);

   int getOldStatusFlags();

   void setOldStatusFlags(int var1);

   int getTransactionState();

   boolean inTransactionOnServer();

   boolean cursorExists();

   boolean isAutocommit();

   boolean hasMoreResults();

   boolean isLastRowSent();

   boolean noGoodIndexUsed();

   boolean noIndexUsed();

   boolean queryWasSlow();

   long getClientParam();

   void setClientParam(long var1);

   boolean hasLongColumnInfo();

   boolean useMultiResults();

   boolean isEOFDeprecated();

   boolean supportsQueryAttributes();

   Map<String, String> getServerVariables();

   String getServerVariable(String var1);

   int getServerVariable(String var1, int var2);

   void setServerVariables(Map<String, String> var1);

   ServerVersion getServerVersion();

   boolean isVersion(ServerVersion var1);

   boolean isLowerCaseTableNames();

   boolean storesLowerCaseTableNames();

   boolean isQueryCacheEnabled();

   boolean isNoBackslashEscapesSet();

   boolean useAnsiQuotedIdentifiers();

   boolean isServerTruncatesFracSecs();

   boolean isAutoCommit();

   void setAutoCommit(boolean var1);

   TimeZone getSessionTimeZone();

   void setSessionTimeZone(TimeZone var1);

   TimeZone getDefaultTimeZone();

   default ServerSessionStateController getServerSessionStateController() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   CharsetSettings getCharsetSettings();

   void setCharsetSettings(CharsetSettings var1);
}
