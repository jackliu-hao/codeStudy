/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.MysqlConnection;
/*     */ import com.mysql.cj.ServerVersion;
/*     */ import com.mysql.cj.TransactionEventHandler;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.interceptors.QueryInterceptor;
/*     */ import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.List;
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
/*     */ public interface JdbcConnection
/*     */   extends Connection, MysqlConnection, TransactionEventHandler
/*     */ {
/*     */   String getDatabase() throws SQLException;
/*     */   
/*     */   void setDatabase(String paramString) throws SQLException;
/*     */   
/*     */   ClientInfoProvider getClientInfoProviderImpl() throws SQLException;
/*     */   
/*     */   JdbcConnection getActiveMySQLConnection();
/*     */   
/*     */   JdbcConnection getMultiHostParentProxy();
/*     */   
/*     */   JdbcConnection getMultiHostSafeProxy();
/*     */   
/*     */   void unSafeQueryInterceptors() throws SQLException;
/*     */   
/*     */   void unregisterStatement(JdbcStatement paramJdbcStatement);
/*     */   
/*     */   void throwConnectionClosedException() throws SQLException;
/*     */   
/*     */   boolean storesLowerCaseTableName();
/*     */   
/*     */   void setReadOnlyInternal(boolean paramBoolean) throws SQLException;
/*     */   
/*     */   void registerStatement(JdbcStatement paramJdbcStatement);
/*     */   
/*     */   void decachePreparedStatement(JdbcPreparedStatement paramJdbcPreparedStatement) throws SQLException;
/*     */   
/*     */   void recachePreparedStatement(JdbcPreparedStatement paramJdbcPreparedStatement) throws SQLException;
/*     */   
/*     */   void realClose(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Throwable paramThrowable) throws SQLException;
/*     */   
/*     */   void pingInternal(boolean paramBoolean, int paramInt) throws SQLException;
/*     */   
/*     */   boolean isReadOnly(boolean paramBoolean) throws SQLException;
/*     */   
/*     */   void initializeSafeQueryInterceptors() throws SQLException;
/*     */   
/*     */   void initializeResultsMetadataFromCache(String paramString, CachedResultSetMetaData paramCachedResultSetMetaData, ResultSetInternalMethods paramResultSetInternalMethods) throws SQLException;
/*     */   
/*     */   List<QueryInterceptor> getQueryInterceptorsInstances();
/*     */   
/*     */   ServerVersion getServerVersion();
/*     */   
/*     */   Statement getMetadataSafeStatement() throws SQLException;
/*     */   
/*     */   String getCharacterSetMetadata();
/*     */   
/*     */   CachedResultSetMetaData getCachedMetaData(String paramString);
/*     */   
/*     */   boolean isProxySet();
/*     */   
/*     */   void abortInternal() throws SQLException;
/*     */   
/*     */   void setSessionMaxRows(int paramInt) throws SQLException;
/*     */   
/*     */   int getSessionMaxRows();
/*     */   
/*     */   boolean isServerLocal() throws SQLException;
/*     */   
/*     */   void setProxy(JdbcConnection paramJdbcConnection);
/*     */   
/*     */   String getHostPortPair();
/*     */   
/*     */   String getHost();
/*     */   
/*     */   boolean hasSameProperties(JdbcConnection paramJdbcConnection);
/*     */   
/*     */   int getAutoIncrementIncrement();
/*     */   
/*     */   void shutdownServer() throws SQLException;
/*     */   
/*     */   void setStatementComment(String paramString);
/*     */   
/*     */   void setFailedOver(boolean paramBoolean);
/*     */   
/*     */   PreparedStatement serverPrepareStatement(String paramString, String[] paramArrayOfString) throws SQLException;
/*     */   
/*     */   PreparedStatement serverPrepareStatement(String paramString, int[] paramArrayOfint) throws SQLException;
/*     */   
/*     */   PreparedStatement serverPrepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException;
/*     */   
/*     */   PreparedStatement serverPrepareStatement(String paramString, int paramInt1, int paramInt2) throws SQLException;
/*     */   
/*     */   PreparedStatement serverPrepareStatement(String paramString, int paramInt) throws SQLException;
/*     */   
/*     */   PreparedStatement serverPrepareStatement(String paramString) throws SQLException;
/*     */   
/*     */   void resetServerState() throws SQLException;
/*     */   
/*     */   void ping() throws SQLException;
/*     */   
/*     */   boolean lowerCaseTableNames();
/*     */   
/*     */   boolean isSameResource(JdbcConnection paramJdbcConnection);
/*     */   
/*     */   @Deprecated
/*     */   default boolean isMasterConnection() {
/* 248 */     return isSourceConnection();
/*     */   }
/*     */   
/*     */   boolean isSourceConnection();
/*     */   
/*     */   void setInGlobalTx(boolean paramBoolean);
/*     */   
/*     */   boolean isInGlobalTx();
/*     */   
/*     */   @Deprecated
/*     */   boolean hasTriedMaster();
/*     */   
/*     */   String getStatementComment();
/*     */   
/*     */   long getIdleFor();
/*     */   
/*     */   int getActiveStatementCount();
/*     */   
/*     */   PreparedStatement clientPrepareStatement(String paramString, String[] paramArrayOfString) throws SQLException;
/*     */   
/*     */   PreparedStatement clientPrepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3) throws SQLException;
/*     */   
/*     */   PreparedStatement clientPrepareStatement(String paramString, int[] paramArrayOfint) throws SQLException;
/*     */   
/*     */   PreparedStatement clientPrepareStatement(String paramString, int paramInt1, int paramInt2) throws SQLException;
/*     */   
/*     */   PreparedStatement clientPrepareStatement(String paramString, int paramInt) throws SQLException;
/*     */   
/*     */   PreparedStatement clientPrepareStatement(String paramString) throws SQLException;
/*     */   
/*     */   @Deprecated
/*     */   void clearHasTriedMaster();
/*     */   
/*     */   void changeUser(String paramString1, String paramString2) throws SQLException;
/*     */   
/*     */   JdbcPropertySet getPropertySet();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\JdbcConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */