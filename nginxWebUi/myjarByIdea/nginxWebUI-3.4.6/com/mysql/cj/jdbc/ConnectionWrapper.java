package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.Session;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ConnectionIsClosedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.jdbc.result.CachedResultSetMetaData;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.protocol.ServerSessionStateController;
import java.lang.reflect.Proxy;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Wrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ConnectionWrapper extends WrapperBase implements JdbcConnection {
   protected JdbcConnection mc = null;
   private String invalidHandleStr = "Logical handle no longer valid";
   private boolean closed;
   private boolean isForXa;

   protected static ConnectionWrapper getInstance(MysqlPooledConnection mysqlPooledConnection, JdbcConnection mysqlConnection, boolean forXa) throws SQLException {
      return new ConnectionWrapper(mysqlPooledConnection, mysqlConnection, forXa);
   }

   public ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, JdbcConnection mysqlConnection, boolean forXa) throws SQLException {
      super(mysqlPooledConnection);
      this.mc = mysqlConnection;
      this.closed = false;
      this.isForXa = forXa;
      if (this.isForXa) {
         this.setInGlobalTx(false);
      }

   }

   public void setAutoCommit(boolean autoCommit) throws SQLException {
      try {
         this.checkClosed();
         if (autoCommit && this.isInGlobalTx()) {
            throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.0"), "2D000", 1401, this.exceptionInterceptor);
         } else {
            try {
               this.mc.setAutoCommit(autoCommit);
            } catch (SQLException var4) {
               this.checkAndFireConnectionError(var4);
            }

         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public boolean getAutoCommit() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getAutoCommit();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return false;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public void setDatabase(String dbName) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.setDatabase(dbName);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public String getDatabase() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getDatabase();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public void setCatalog(String catalog) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.setCatalog(catalog);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public String getCatalog() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getCatalog();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return this.closed || this.mc.isClosed();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public boolean isSourceConnection() {
      return this.mc.isSourceConnection();
   }

   public void setHoldability(int arg0) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.setHoldability(arg0);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public int getHoldability() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getHoldability();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return 1;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public long getIdleFor() {
      return this.mc.getIdleFor();
   }

   public java.sql.DatabaseMetaData getMetaData() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getMetaData();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public void setReadOnly(boolean readOnly) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.setReadOnly(readOnly);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public boolean isReadOnly() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.isReadOnly();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return false;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public Savepoint setSavepoint() throws SQLException {
      try {
         this.checkClosed();
         if (this.isInGlobalTx()) {
            throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.0"), "2D000", 1401, this.exceptionInterceptor);
         } else {
            try {
               return this.mc.setSavepoint();
            } catch (SQLException var3) {
               this.checkAndFireConnectionError(var3);
               return null;
            }
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public Savepoint setSavepoint(String arg0) throws SQLException {
      try {
         this.checkClosed();
         if (this.isInGlobalTx()) {
            throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.0"), "2D000", 1401, this.exceptionInterceptor);
         } else {
            try {
               return this.mc.setSavepoint(arg0);
            } catch (SQLException var4) {
               this.checkAndFireConnectionError(var4);
               return null;
            }
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public void setTransactionIsolation(int level) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.setTransactionIsolation(level);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public int getTransactionIsolation() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getTransactionIsolation();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return 4;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public Map<String, Class<?>> getTypeMap() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getTypeMap();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getWarnings();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public void clearWarnings() throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.clearWarnings();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
         }

      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public void close() throws SQLException {
      try {
         try {
            this.close(true);
         } finally {
            this.unwrappedInterfaces = null;
         }

      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public void commit() throws SQLException {
      try {
         this.checkClosed();
         if (this.isInGlobalTx()) {
            throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.1"), "2D000", 1401, this.exceptionInterceptor);
         } else {
            try {
               this.mc.commit();
            } catch (SQLException var3) {
               this.checkAndFireConnectionError(var3);
            }

         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public Statement createStatement() throws SQLException {
      try {
         this.checkClosed();

         try {
            return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement());
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         this.checkClosed();

         try {
            return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(resultSetType, resultSetConcurrency));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
      try {
         this.checkClosed();

         try {
            return StatementWrapper.getInstance(this, this.pooledConnection, this.mc.createStatement(arg0, arg1, arg2));
         } catch (SQLException var6) {
            this.checkAndFireConnectionError(var6);
            return null;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, super.exceptionInterceptor);
      }
   }

   public String nativeSQL(String sql) throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.nativeSQL(sql);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
            return null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
      try {
         this.checkClosed();

         try {
            return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql));
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
            return null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         this.checkClosed();

         try {
            return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(sql, resultSetType, resultSetConcurrency));
         } catch (SQLException var6) {
            this.checkAndFireConnectionError(var6);
            return null;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, super.exceptionInterceptor);
      }
   }

   public java.sql.CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
      try {
         this.checkClosed();

         try {
            return CallableStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareCall(arg0, arg1, arg2, arg3));
         } catch (SQLException var7) {
            this.checkAndFireConnectionError(var7);
            return null;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, super.exceptionInterceptor);
      }
   }

   public PreparedStatement clientPrepare(String sql) throws SQLException {
      try {
         this.checkClosed();

         try {
            return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql));
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
            return null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public PreparedStatement clientPrepare(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         this.checkClosed();

         try {
            return new PreparedStatementWrapper(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
         } catch (SQLException var6) {
            this.checkAndFireConnectionError(var6);
            return null;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, super.exceptionInterceptor);
      }
   }

   public PreparedStatement prepareStatement(String sql) throws SQLException {
      try {
         this.checkClosed();
         PreparedStatement res = null;

         try {
            res = PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
         }

         return res;
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(sql, resultSetType, resultSetConcurrency));
         } catch (SQLException var6) {
            this.checkAndFireConnectionError(var6);
            return null;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, super.exceptionInterceptor);
      }
   }

   public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1, arg2, arg3));
         } catch (SQLException var7) {
            this.checkAndFireConnectionError(var7);
            return null;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, super.exceptionInterceptor);
      }
   }

   public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.prepareStatement(arg0, arg1));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public void releaseSavepoint(Savepoint arg0) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.releaseSavepoint(arg0);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public void rollback() throws SQLException {
      try {
         this.checkClosed();
         if (this.isInGlobalTx()) {
            throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.2"), "2D000", 1401, this.exceptionInterceptor);
         } else {
            try {
               this.mc.rollback();
            } catch (SQLException var3) {
               this.checkAndFireConnectionError(var3);
            }

         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public void rollback(Savepoint arg0) throws SQLException {
      try {
         this.checkClosed();
         if (this.isInGlobalTx()) {
            throw SQLError.createSQLException(Messages.getString("ConnectionWrapper.2"), "2D000", 1401, this.exceptionInterceptor);
         } else {
            try {
               this.mc.rollback(arg0);
            } catch (SQLException var4) {
               this.checkAndFireConnectionError(var4);
            }

         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public boolean isSameResource(JdbcConnection c) {
      return c instanceof ConnectionWrapper ? this.mc.isSameResource(((ConnectionWrapper)c).mc) : this.mc.isSameResource(c);
   }

   protected void close(boolean fireClosedEvent) throws SQLException {
      synchronized(this.pooledConnection) {
         if (!this.closed) {
            if (!this.isInGlobalTx() && (Boolean)this.mc.getPropertySet().getBooleanProperty(PropertyKey.rollbackOnPooledClose).getValue() && !this.getAutoCommit()) {
               this.rollback();
            }

            if (fireClosedEvent) {
               this.pooledConnection.callConnectionEventListeners(2, (SQLException)null);
            }

            this.closed = true;
         }
      }
   }

   public void checkClosed() {
      if (this.closed) {
         throw (ConnectionIsClosedException)ExceptionFactory.createException(ConnectionIsClosedException.class, this.invalidHandleStr, this.exceptionInterceptor);
      }
   }

   public boolean isInGlobalTx() {
      return this.mc.isInGlobalTx();
   }

   public void setInGlobalTx(boolean flag) {
      this.mc.setInGlobalTx(flag);
   }

   public void ping() throws SQLException {
      try {
         if (this.mc != null) {
            this.mc.ping();
         }

      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public void changeUser(String userName, String newPassword) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.changeUser(userName, newPassword);
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
         }

      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   /** @deprecated */
   @Deprecated
   public void clearHasTriedMaster() {
      this.mc.clearHasTriedMaster();
   }

   public PreparedStatement clientPrepareStatement(String sql) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql));
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
            return null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndex));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
         } catch (SQLException var6) {
            this.checkAndFireConnectionError(var6);
            return null;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, super.exceptionInterceptor);
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
         } catch (SQLException var7) {
            this.checkAndFireConnectionError(var7);
            return null;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, super.exceptionInterceptor);
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyIndexes));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public PreparedStatement clientPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.clientPrepareStatement(sql, autoGenKeyColNames));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public int getActiveStatementCount() {
      return this.mc.getActiveStatementCount();
   }

   public String getStatementComment() {
      return this.mc.getStatementComment();
   }

   /** @deprecated */
   @Deprecated
   public boolean hasTriedMaster() {
      return this.mc.hasTriedMaster();
   }

   public boolean lowerCaseTableNames() {
      return this.mc.lowerCaseTableNames();
   }

   public void resetServerState() throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.resetServerState();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
         }

      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public PreparedStatement serverPrepareStatement(String sql) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql));
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
            return null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int autoGenKeyIndex) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndex));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency));
         } catch (SQLException var6) {
            this.checkAndFireConnectionError(var6);
            return null;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, super.exceptionInterceptor);
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
         } catch (SQLException var7) {
            this.checkAndFireConnectionError(var7);
            return null;
         }
      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, super.exceptionInterceptor);
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, int[] autoGenKeyIndexes) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyIndexes));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public PreparedStatement serverPrepareStatement(String sql, String[] autoGenKeyColNames) throws SQLException {
      try {
         this.checkClosed();

         try {
            return PreparedStatementWrapper.getInstance(this, this.pooledConnection, this.mc.serverPrepareStatement(sql, autoGenKeyColNames));
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public void setFailedOver(boolean flag) {
      this.mc.setFailedOver(flag);
   }

   public void setStatementComment(String comment) {
      this.mc.setStatementComment(comment);
   }

   public void shutdownServer() throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.shutdownServer();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
         }

      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public int getAutoIncrementIncrement() {
      return this.mc.getAutoIncrementIncrement();
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.pooledConnection.getExceptionInterceptor();
   }

   public boolean hasSameProperties(JdbcConnection c) {
      return this.mc.hasSameProperties(c);
   }

   public Properties getProperties() {
      return this.mc.getProperties();
   }

   public String getHost() {
      return this.mc.getHost();
   }

   public void setProxy(JdbcConnection conn) {
      this.mc.setProxy(conn);
   }

   public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
      try {
         this.checkClosed();

         try {
            this.mc.setTypeMap(map);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
         }

      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public boolean isServerLocal() throws SQLException {
      try {
         return this.mc.isServerLocal();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public void setSchema(String schema) throws SQLException {
      try {
         this.checkClosed();
         this.mc.setSchema(schema);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, super.exceptionInterceptor);
      }
   }

   public String getSchema() throws SQLException {
      try {
         return this.mc.getSchema();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public void abort(Executor executor) throws SQLException {
      try {
         this.mc.abort(executor);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, super.exceptionInterceptor);
      }
   }

   public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
      try {
         this.mc.setNetworkTimeout(executor, milliseconds);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public int getNetworkTimeout() throws SQLException {
      try {
         return this.mc.getNetworkTimeout();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public void abortInternal() throws SQLException {
      try {
         this.mc.abortInternal();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public Object getConnectionMutex() {
      return this.mc.getConnectionMutex();
   }

   public int getSessionMaxRows() {
      return this.mc.getSessionMaxRows();
   }

   public void setSessionMaxRows(int max) throws SQLException {
      try {
         this.mc.setSessionMaxRows(max);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, super.exceptionInterceptor);
      }
   }

   public java.sql.Clob createClob() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.createClob();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public java.sql.Blob createBlob() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.createBlob();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public java.sql.NClob createNClob() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.createNClob();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public SQLXML createSQLXML() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.createSQLXML();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public synchronized boolean isValid(int timeout) throws SQLException {
      try {
         try {
            return this.mc.isValid(timeout);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
            return false;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public void setClientInfo(String name, String value) throws SQLClientInfoException {
      try {
         try {
            this.checkClosed();
            this.mc.setClientInfo(name, value);
         } catch (SQLException var8) {
            SQLException sqlException = var8;

            try {
               this.checkAndFireConnectionError(sqlException);
            } catch (SQLException var7) {
               SQLClientInfoException clientEx = new SQLClientInfoException();
               clientEx.initCause(var7);
               throw clientEx;
            }
         }

      } catch (CJException var9) {
         throw SQLExceptionsMapping.translateException(var9, super.exceptionInterceptor);
      }
   }

   public void setClientInfo(Properties properties) throws SQLClientInfoException {
      try {
         try {
            this.checkClosed();
            this.mc.setClientInfo(properties);
         } catch (SQLException var7) {
            SQLException sqlException = var7;

            try {
               this.checkAndFireConnectionError(sqlException);
            } catch (SQLException var6) {
               SQLClientInfoException clientEx = new SQLClientInfoException();
               clientEx.initCause(var6);
               throw clientEx;
            }
         }

      } catch (CJException var8) {
         throw SQLExceptionsMapping.translateException(var8, super.exceptionInterceptor);
      }
   }

   public String getClientInfo(String name) throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getClientInfo(name);
         } catch (SQLException var4) {
            this.checkAndFireConnectionError(var4);
            return null;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public Properties getClientInfo() throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.getClientInfo();
         } catch (SQLException var3) {
            this.checkAndFireConnectionError(var3);
            return null;
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.createArrayOf(typeName, elements);
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
      try {
         this.checkClosed();

         try {
            return this.mc.createStruct(typeName, attributes);
         } catch (SQLException var5) {
            this.checkAndFireConnectionError(var5);
            return null;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         try {
            if (!"java.sql.Connection".equals(iface.getName()) && !"java.sql.Wrapper.class".equals(iface.getName())) {
               if (this.unwrappedInterfaces == null) {
                  this.unwrappedInterfaces = new HashMap();
               }

               Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
               if (cachedUnwrapped == null) {
                  cachedUnwrapped = Proxy.newProxyInstance(this.mc.getClass().getClassLoader(), new Class[]{iface}, new WrapperBase.ConnectionErrorFiringInvocationHandler(this.mc));
                  this.unwrappedInterfaces.put(iface, cachedUnwrapped);
               }

               return iface.cast(cachedUnwrapped);
            } else {
               return iface.cast(this);
            }
         } catch (ClassCastException var4) {
            throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[]{iface.toString()}), "S1009", this.exceptionInterceptor);
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      try {
         boolean isInstance = iface.isInstance(this);
         if (isInstance) {
            return true;
         } else {
            return iface.getName().equals(JdbcConnection.class.getName()) || iface.getName().equals(MysqlConnection.class.getName()) || iface.getName().equals(Connection.class.getName()) || iface.getName().equals(Wrapper.class.getName()) || iface.getName().equals(AutoCloseable.class.getName());
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public Session getSession() {
      return this.mc.getSession();
   }

   public long getId() {
      return this.mc.getId();
   }

   public String getURL() {
      return this.mc.getURL();
   }

   public String getUser() {
      return this.mc.getUser();
   }

   public void createNewIO(boolean isForReconnect) {
      this.mc.createNewIO(isForReconnect);
   }

   public boolean isProxySet() {
      return this.mc.isProxySet();
   }

   public JdbcPropertySet getPropertySet() {
      return this.mc.getPropertySet();
   }

   public CachedResultSetMetaData getCachedMetaData(String sql) {
      return this.mc.getCachedMetaData(sql);
   }

   public String getCharacterSetMetadata() {
      return this.mc.getCharacterSetMetadata();
   }

   public Statement getMetadataSafeStatement() throws SQLException {
      try {
         return this.mc.getMetadataSafeStatement();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public ServerVersion getServerVersion() {
      return this.mc.getServerVersion();
   }

   public List<QueryInterceptor> getQueryInterceptorsInstances() {
      return this.mc.getQueryInterceptorsInstances();
   }

   public void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSetInternalMethods resultSet) throws SQLException {
      try {
         this.mc.initializeResultsMetadataFromCache(sql, cachedMetaData, resultSet);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, super.exceptionInterceptor);
      }
   }

   public void initializeSafeQueryInterceptors() throws SQLException {
      try {
         this.mc.initializeSafeQueryInterceptors();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public boolean isReadOnly(boolean useSessionStatus) throws SQLException {
      try {
         this.checkClosed();
         return this.mc.isReadOnly(useSessionStatus);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, super.exceptionInterceptor);
      }
   }

   public void pingInternal(boolean checkForClosedConnection, int timeoutMillis) throws SQLException {
      try {
         this.mc.pingInternal(checkForClosedConnection, timeoutMillis);
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, super.exceptionInterceptor);
      }
   }

   public void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason) throws SQLException {
      try {
         this.mc.realClose(calledExplicitly, issueRollback, skipLocalTeardown, reason);
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, super.exceptionInterceptor);
      }
   }

   public void recachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
      try {
         this.mc.recachePreparedStatement(pstmt);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, super.exceptionInterceptor);
      }
   }

   public void decachePreparedStatement(JdbcPreparedStatement pstmt) throws SQLException {
      try {
         this.mc.decachePreparedStatement(pstmt);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, super.exceptionInterceptor);
      }
   }

   public void registerStatement(JdbcStatement stmt) {
      this.mc.registerStatement(stmt);
   }

   public void setReadOnlyInternal(boolean readOnlyFlag) throws SQLException {
      try {
         this.mc.setReadOnlyInternal(readOnlyFlag);
      } catch (CJException var3) {
         throw SQLExceptionsMapping.translateException(var3, super.exceptionInterceptor);
      }
   }

   public boolean storesLowerCaseTableName() {
      return this.mc.storesLowerCaseTableName();
   }

   public void throwConnectionClosedException() throws SQLException {
      try {
         this.mc.throwConnectionClosedException();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public void transactionBegun() {
      this.mc.transactionBegun();
   }

   public void transactionCompleted() {
      this.mc.transactionCompleted();
   }

   public void unregisterStatement(JdbcStatement stmt) {
      this.mc.unregisterStatement(stmt);
   }

   public void unSafeQueryInterceptors() throws SQLException {
      try {
         this.mc.unSafeQueryInterceptors();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public JdbcConnection getMultiHostSafeProxy() {
      return this.mc.getMultiHostSafeProxy();
   }

   public JdbcConnection getMultiHostParentProxy() {
      return this.mc.getMultiHostParentProxy();
   }

   public JdbcConnection getActiveMySQLConnection() {
      return this.mc.getActiveMySQLConnection();
   }

   public ClientInfoProvider getClientInfoProviderImpl() throws SQLException {
      try {
         return this.mc.getClientInfoProviderImpl();
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, super.exceptionInterceptor);
      }
   }

   public String getHostPortPair() {
      return this.mc.getHostPortPair();
   }

   public void normalClose() {
      this.mc.normalClose();
   }

   public void cleanup(Throwable whyCleanedUp) {
      this.mc.cleanup(whyCleanedUp);
   }

   public ServerSessionStateController getServerSessionStateController() {
      return this.mc.getServerSessionStateController();
   }
}
