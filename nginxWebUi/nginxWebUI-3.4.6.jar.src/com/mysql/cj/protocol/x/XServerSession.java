/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.mysql.cj.CharsetSettings;
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.protocol.ServerCapabilities;
/*     */ import com.mysql.cj.protocol.ServerSession;
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
/*     */ public class XServerSession
/*     */   implements ServerSession
/*     */ {
/*  44 */   XServerCapabilities serverCapabilities = null;
/*     */   
/*  46 */   private TimeZone defaultTimeZone = TimeZone.getDefault();
/*     */ 
/*     */   
/*     */   public ServerCapabilities getCapabilities() {
/*  50 */     return this.serverCapabilities;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCapabilities(ServerCapabilities capabilities) {
/*  55 */     this.serverCapabilities = (XServerCapabilities)capabilities;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStatusFlags() {
/*  60 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusFlags(int statusFlags) {
/*  65 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusFlags(int statusFlags, boolean saveOldStatusFlags) {
/*  70 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public int getOldStatusFlags() {
/*  75 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOldStatusFlags(int statusFlags) {
/*  80 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTransactionState() {
/*  85 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean inTransactionOnServer() {
/*  90 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean cursorExists() {
/*  95 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAutocommit() {
/* 100 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMoreResults() {
/* 105 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLastRowSent() {
/* 110 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean noGoodIndexUsed() {
/* 115 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean noIndexUsed() {
/* 120 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean queryWasSlow() {
/* 125 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public long getClientParam() {
/* 130 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClientParam(long clientParam) {
/* 135 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasLongColumnInfo() {
/* 140 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean useMultiResults() {
/* 145 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEOFDeprecated() {
/* 150 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsQueryAttributes() {
/* 155 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, String> getServerVariables() {
/* 160 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getServerVariable(String name) {
/* 165 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public int getServerVariable(String variableName, int fallbackValue) {
/* 170 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setServerVariables(Map<String, String> serverVariables) {
/* 175 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerVersion getServerVersion() {
/* 180 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isVersion(ServerVersion version) {
/* 185 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLowerCaseTableNames() {
/* 190 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean storesLowerCaseTableNames() {
/* 195 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQueryCacheEnabled() {
/* 200 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNoBackslashEscapesSet() {
/* 205 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean useAnsiQuotedIdentifiers() {
/* 210 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isServerTruncatesFracSecs() {
/* 215 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAutoCommit() {
/* 220 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAutoCommit(boolean autoCommit) {
/* 225 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */   
/*     */   public TimeZone getSessionTimeZone() {
/* 229 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */   
/*     */   public void setSessionTimeZone(TimeZone sessionTimeZone) {
/* 233 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */   
/*     */   public TimeZone getDefaultTimeZone() {
/* 237 */     return this.defaultTimeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public CharsetSettings getCharsetSettings() {
/* 243 */     return null;
/*     */   }
/*     */   
/*     */   public void setCharsetSettings(CharsetSettings charsetSettings) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XServerSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */