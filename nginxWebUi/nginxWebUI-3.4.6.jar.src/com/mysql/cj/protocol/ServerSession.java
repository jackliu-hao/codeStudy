/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.CharsetSettings;
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
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
/*     */ public interface ServerSession
/*     */ {
/*     */   public static final int TRANSACTION_NOT_STARTED = 0;
/*     */   public static final int TRANSACTION_IN_PROGRESS = 1;
/*     */   public static final int TRANSACTION_STARTED = 2;
/*     */   public static final int TRANSACTION_COMPLETED = 3;
/*     */   
/*     */   ServerCapabilities getCapabilities();
/*     */   
/*     */   void setCapabilities(ServerCapabilities paramServerCapabilities);
/*     */   
/*     */   int getStatusFlags();
/*     */   
/*     */   void setStatusFlags(int paramInt);
/*     */   
/*     */   void setStatusFlags(int paramInt, boolean paramBoolean);
/*     */   
/*     */   int getOldStatusFlags();
/*     */   
/*     */   void setOldStatusFlags(int paramInt);
/*     */   
/*     */   int getTransactionState();
/*     */   
/*     */   boolean inTransactionOnServer();
/*     */   
/*     */   boolean cursorExists();
/*     */   
/*     */   boolean isAutocommit();
/*     */   
/*     */   boolean hasMoreResults();
/*     */   
/*     */   boolean isLastRowSent();
/*     */   
/*     */   boolean noGoodIndexUsed();
/*     */   
/*     */   boolean noIndexUsed();
/*     */   
/*     */   boolean queryWasSlow();
/*     */   
/*     */   long getClientParam();
/*     */   
/*     */   void setClientParam(long paramLong);
/*     */   
/*     */   boolean hasLongColumnInfo();
/*     */   
/*     */   boolean useMultiResults();
/*     */   
/*     */   boolean isEOFDeprecated();
/*     */   
/*     */   boolean supportsQueryAttributes();
/*     */   
/*     */   Map<String, String> getServerVariables();
/*     */   
/*     */   String getServerVariable(String paramString);
/*     */   
/*     */   int getServerVariable(String paramString, int paramInt);
/*     */   
/*     */   void setServerVariables(Map<String, String> paramMap);
/*     */   
/*     */   ServerVersion getServerVersion();
/*     */   
/*     */   boolean isVersion(ServerVersion paramServerVersion);
/*     */   
/*     */   boolean isLowerCaseTableNames();
/*     */   
/*     */   boolean storesLowerCaseTableNames();
/*     */   
/*     */   boolean isQueryCacheEnabled();
/*     */   
/*     */   boolean isNoBackslashEscapesSet();
/*     */   
/*     */   boolean useAnsiQuotedIdentifiers();
/*     */   
/*     */   boolean isServerTruncatesFracSecs();
/*     */   
/*     */   boolean isAutoCommit();
/*     */   
/*     */   void setAutoCommit(boolean paramBoolean);
/*     */   
/*     */   TimeZone getSessionTimeZone();
/*     */   
/*     */   void setSessionTimeZone(TimeZone paramTimeZone);
/*     */   
/*     */   TimeZone getDefaultTimeZone();
/*     */   
/*     */   default ServerSessionStateController getServerSessionStateController() {
/* 194 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */   
/*     */   CharsetSettings getCharsetSettings();
/*     */   
/*     */   void setCharsetSettings(CharsetSettings paramCharsetSettings);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ServerSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */