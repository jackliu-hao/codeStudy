package com.mysql.cj.jdbc;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.TransactionEventHandler;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface JdbcConnection extends Connection, MysqlConnection, TransactionEventHandler {
   JdbcPropertySet getPropertySet();

   void changeUser(String var1, String var2) throws SQLException;

   /** @deprecated */
   @Deprecated
   void clearHasTriedMaster();

   PreparedStatement clientPrepareStatement(String var1) throws SQLException;

   PreparedStatement clientPrepareStatement(String var1, int var2) throws SQLException;

   PreparedStatement clientPrepareStatement(String var1, int var2, int var3) throws SQLException;

   PreparedStatement clientPrepareStatement(String var1, int[] var2) throws SQLException;

   PreparedStatement clientPrepareStatement(String var1, int var2, int var3, int var4) throws SQLException;

   PreparedStatement clientPrepareStatement(String var1, String[] var2) throws SQLException;

   int getActiveStatementCount();

   long getIdleFor();

   String getStatementComment();

   /** @deprecated */
   @Deprecated
   boolean hasTriedMaster();

   boolean isInGlobalTx();

   void setInGlobalTx(boolean var1);

   boolean isSourceConnection();

   /** @deprecated */
   @Deprecated
   default boolean isMasterConnection() {
      return this.isSourceConnection();
   }

   boolean isSameResource(JdbcConnection var1);

   boolean lowerCaseTableNames();

   void ping() throws SQLException;

   void resetServerState() throws SQLException;

   PreparedStatement serverPrepareStatement(String var1) throws SQLException;

   PreparedStatement serverPrepareStatement(String var1, int var2) throws SQLException;

   PreparedStatement serverPrepareStatement(String var1, int var2, int var3) throws SQLException;

   PreparedStatement serverPrepareStatement(String var1, int var2, int var3, int var4) throws SQLException;

   PreparedStatement serverPrepareStatement(String var1, int[] var2) throws SQLException;

   PreparedStatement serverPrepareStatement(String var1, String[] var2) throws SQLException;

   void setFailedOver(boolean var1);

   void setStatementComment(String var1);

   void shutdownServer() throws SQLException;

   int getAutoIncrementIncrement();

   boolean hasSameProperties(JdbcConnection var1);

   String getHost();

   String getHostPortPair();

   void setProxy(JdbcConnection var1);

   boolean isServerLocal() throws SQLException;

   int getSessionMaxRows();

   void setSessionMaxRows(int var1) throws SQLException;

   void abortInternal() throws SQLException;

   boolean isProxySet();

   CachedResultSetMetaData getCachedMetaData(String var1);

   String getCharacterSetMetadata();

   Statement getMetadataSafeStatement() throws SQLException;

   ServerVersion getServerVersion();

   List<QueryInterceptor> getQueryInterceptorsInstances();

   void initializeResultsMetadataFromCache(String var1, CachedResultSetMetaData var2, ResultSetInternalMethods var3) throws SQLException;

   void initializeSafeQueryInterceptors() throws SQLException;

   boolean isReadOnly(boolean var1) throws SQLException;

   void pingInternal(boolean var1, int var2) throws SQLException;

   void realClose(boolean var1, boolean var2, boolean var3, Throwable var4) throws SQLException;

   void recachePreparedStatement(JdbcPreparedStatement var1) throws SQLException;

   void decachePreparedStatement(JdbcPreparedStatement var1) throws SQLException;

   void registerStatement(JdbcStatement var1);

   void setReadOnlyInternal(boolean var1) throws SQLException;

   boolean storesLowerCaseTableName();

   void throwConnectionClosedException() throws SQLException;

   void unregisterStatement(JdbcStatement var1);

   void unSafeQueryInterceptors() throws SQLException;

   JdbcConnection getMultiHostSafeProxy();

   JdbcConnection getMultiHostParentProxy();

   JdbcConnection getActiveMySQLConnection();

   ClientInfoProvider getClientInfoProviderImpl() throws SQLException;

   void setDatabase(String var1) throws SQLException;

   String getDatabase() throws SQLException;
}
