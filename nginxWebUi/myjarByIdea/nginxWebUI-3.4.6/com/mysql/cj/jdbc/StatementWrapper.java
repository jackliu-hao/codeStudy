package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashMap;

public class StatementWrapper extends WrapperBase implements Statement {
   protected Statement wrappedStmt;
   protected ConnectionWrapper wrappedConn;

   protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) throws SQLException {
      return new StatementWrapper(c, conn, toWrap);
   }

   public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap) {
      super(conn);
      this.wrappedStmt = toWrap;
      this.wrappedConn = c;
   }

   public Connection getConnection() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedConn;
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return null;
      }
   }

   public void setCursorName(String name) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setCursorName(name);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public void setEscapeProcessing(boolean enable) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setEscapeProcessing(enable);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public void setFetchDirection(int direction) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setFetchDirection(direction);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public int getFetchDirection() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getFetchDirection();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 1000;
      }
   }

   public void setFetchSize(int rows) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setFetchSize(rows);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public int getFetchSize() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getFetchSize();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 0;
      }
   }

   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getGeneratedKeys();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return null;
      }
   }

   public void setMaxFieldSize(int max) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setMaxFieldSize(max);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public int getMaxFieldSize() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getMaxFieldSize();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 0;
      }
   }

   public void setMaxRows(int max) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setMaxRows(max);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public int getMaxRows() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getMaxRows();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 0;
      }
   }

   public boolean getMoreResults() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getMoreResults();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return false;
      }
   }

   public boolean getMoreResults(int current) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getMoreResults(current);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return false;
      }
   }

   public void setQueryTimeout(int seconds) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setQueryTimeout(seconds);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public int getQueryTimeout() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getQueryTimeout();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 0;
      }
   }

   public ResultSet getResultSet() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            ResultSet rs = this.wrappedStmt.getResultSet();
            if (rs != null) {
               ((ResultSetInternalMethods)rs).setWrapperStatement(this);
            }

            return rs;
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return null;
      }
   }

   public int getResultSetConcurrency() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getResultSetConcurrency();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 0;
      }
   }

   public int getResultSetHoldability() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getResultSetHoldability();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 1;
      }
   }

   public int getResultSetType() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getResultSetType();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 1003;
      }
   }

   public int getUpdateCount() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getUpdateCount();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return -1;
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.getWarnings();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return null;
      }
   }

   public void addBatch(String sql) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.addBatch(sql);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public void cancel() throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.cancel();
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
      }

   }

   public void clearBatch() throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.clearBatch();
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
      }

   }

   public void clearWarnings() throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.clearWarnings();
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
      }

   }

   public void close() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            this.wrappedStmt.close();
         }
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      } finally {
         this.wrappedStmt = null;
         this.pooledConnection = null;
         this.unwrappedInterfaces = null;
      }

   }

   public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.execute(sql, autoGeneratedKeys);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return false;
      }
   }

   public boolean execute(String sql, int[] columnIndexes) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.execute(sql, columnIndexes);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return false;
      }
   }

   public boolean execute(String sql, String[] columnNames) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.execute(sql, columnNames);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return false;
      }
   }

   public boolean execute(String sql) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.execute(sql);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return false;
      }
   }

   public int[] executeBatch() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.executeBatch();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return null;
      }
   }

   public ResultSet executeQuery(String sql) throws SQLException {
      ResultSet rs = null;

      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         rs = this.wrappedStmt.executeQuery(sql);
         ((ResultSetInternalMethods)rs).setWrapperStatement(this);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

      return rs;
   }

   public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.executeUpdate(sql, autoGeneratedKeys);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return -1;
      }
   }

   public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.executeUpdate(sql, columnIndexes);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return -1;
      }
   }

   public int executeUpdate(String sql, String[] columnNames) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.executeUpdate(sql, columnNames);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return -1;
      }
   }

   public int executeUpdate(String sql) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.executeUpdate(sql);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return -1;
      }
   }

   public void enableStreamingResults() throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((JdbcStatement)this.wrappedStmt).enableStreamingResults();
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
      }

   }

   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         if (!"java.sql.Statement".equals(iface.getName()) && !"java.sql.Wrapper.class".equals(iface.getName())) {
            if (this.unwrappedInterfaces == null) {
               this.unwrappedInterfaces = new HashMap();
            }

            Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
            if (cachedUnwrapped == null) {
               cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[]{iface}, new WrapperBase.ConnectionErrorFiringInvocationHandler(this.wrappedStmt));
               this.unwrappedInterfaces.put(iface, cachedUnwrapped);
            }

            return iface.cast(cachedUnwrapped);
         } else {
            return iface.cast(this);
         }
      } catch (ClassCastException var3) {
         throw SQLError.createSQLException(Messages.getString("Common.UnableToUnwrap", new Object[]{iface.toString()}), "S1009", this.exceptionInterceptor);
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      boolean isInstance = iface.isInstance(this);
      if (isInstance) {
         return true;
      } else {
         String interfaceClassName = iface.getName();
         return interfaceClassName.equals("com.mysql.cj.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName.equals("java.sql.Wrapper");
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.isClosed();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return false;
      }
   }

   public void setPoolable(boolean poolable) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         this.wrappedStmt.setPoolable(poolable);
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
      }

   }

   public boolean isPoolable() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return this.wrappedStmt.isPoolable();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return false;
      }
   }

   public void closeOnCompletion() throws SQLException {
      if (this.wrappedStmt == null) {
         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      }
   }

   public boolean isCloseOnCompletion() throws SQLException {
      if (this.wrappedStmt == null) {
         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
      } else {
         return false;
      }
   }

   public long[] executeLargeBatch() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((StatementImpl)this.wrappedStmt).executeLargeBatch();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return null;
      }
   }

   public long executeLargeUpdate(String sql) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return -1L;
      }
   }

   public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, autoGeneratedKeys);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return -1L;
      }
   }

   public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnIndexes);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return -1L;
      }
   }

   public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnNames);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return -1L;
      }
   }

   public long getLargeMaxRows() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((StatementImpl)this.wrappedStmt).getLargeMaxRows();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return 0L;
      }
   }

   public long getLargeUpdateCount() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((StatementImpl)this.wrappedStmt).getLargeUpdateCount();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return -1L;
      }
   }

   public void setLargeMaxRows(long max) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1009", this.exceptionInterceptor);
         }

         ((StatementImpl)this.wrappedStmt).setLargeMaxRows(max);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }
}
