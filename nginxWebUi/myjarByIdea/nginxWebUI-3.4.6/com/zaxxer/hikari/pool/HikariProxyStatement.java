package com.zaxxer.hikari.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Wrapper;

public final class HikariProxyStatement extends ProxyStatement implements Wrapper, AutoCloseable, Statement {
   public boolean isWrapperFor(Class var1) throws SQLException {
      try {
         return super.delegate.isWrapperFor(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public ResultSet executeQuery(String var1) throws SQLException {
      try {
         return super.executeQuery(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int executeUpdate(String var1) throws SQLException {
      try {
         return super.executeUpdate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getMaxFieldSize() throws SQLException {
      try {
         return super.delegate.getMaxFieldSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setMaxFieldSize(int var1) throws SQLException {
      try {
         super.delegate.setMaxFieldSize(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getMaxRows() throws SQLException {
      try {
         return super.delegate.getMaxRows();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setMaxRows(int var1) throws SQLException {
      try {
         super.delegate.setMaxRows(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setEscapeProcessing(boolean var1) throws SQLException {
      try {
         super.delegate.setEscapeProcessing(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getQueryTimeout() throws SQLException {
      try {
         return super.delegate.getQueryTimeout();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setQueryTimeout(int var1) throws SQLException {
      try {
         super.delegate.setQueryTimeout(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void cancel() throws SQLException {
      try {
         super.delegate.cancel();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         return super.delegate.getWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void clearWarnings() throws SQLException {
      try {
         super.delegate.clearWarnings();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setCursorName(String var1) throws SQLException {
      try {
         super.delegate.setCursorName(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean execute(String var1) throws SQLException {
      try {
         return super.execute(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public ResultSet getResultSet() throws SQLException {
      try {
         return super.getResultSet();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getUpdateCount() throws SQLException {
      try {
         return super.delegate.getUpdateCount();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean getMoreResults() throws SQLException {
      try {
         return super.delegate.getMoreResults();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchDirection(int var1) throws SQLException {
      try {
         super.delegate.setFetchDirection(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         return super.delegate.getFetchDirection();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setFetchSize(int var1) throws SQLException {
      try {
         super.delegate.setFetchSize(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         return super.delegate.getFetchSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getResultSetConcurrency() throws SQLException {
      try {
         return super.delegate.getResultSetConcurrency();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getResultSetType() throws SQLException {
      try {
         return super.delegate.getResultSetType();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void addBatch(String var1) throws SQLException {
      try {
         super.delegate.addBatch(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void clearBatch() throws SQLException {
      try {
         super.delegate.clearBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int[] executeBatch() throws SQLException {
      try {
         return super.executeBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Connection getConnection() throws SQLException {
      try {
         return super.getConnection();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean getMoreResults(int var1) throws SQLException {
      try {
         return super.delegate.getMoreResults(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public ResultSet getGeneratedKeys() throws SQLException {
      try {
         return super.getGeneratedKeys();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int executeUpdate(String var1, int var2) throws SQLException {
      try {
         return super.executeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int executeUpdate(String var1, int[] var2) throws SQLException {
      try {
         return super.executeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int executeUpdate(String var1, String[] var2) throws SQLException {
      try {
         return super.executeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean execute(String var1, int var2) throws SQLException {
      try {
         return super.execute(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean execute(String var1, int[] var2) throws SQLException {
      try {
         return super.execute(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean execute(String var1, String[] var2) throws SQLException {
      try {
         return super.execute(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int getResultSetHoldability() throws SQLException {
      try {
         return super.delegate.getResultSetHoldability();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return super.delegate.isClosed();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setPoolable(boolean var1) throws SQLException {
      try {
         super.delegate.setPoolable(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean isPoolable() throws SQLException {
      try {
         return super.delegate.isPoolable();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void closeOnCompletion() throws SQLException {
      try {
         super.delegate.closeOnCompletion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isCloseOnCompletion() throws SQLException {
      try {
         return super.delegate.isCloseOnCompletion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long getLargeUpdateCount() throws SQLException {
      try {
         return super.delegate.getLargeUpdateCount();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setLargeMaxRows(long var1) throws SQLException {
      try {
         super.delegate.setLargeMaxRows(var1);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long getLargeMaxRows() throws SQLException {
      try {
         return super.delegate.getLargeMaxRows();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long[] executeLargeBatch() throws SQLException {
      try {
         return super.delegate.executeLargeBatch();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long executeLargeUpdate(String var1) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public long executeLargeUpdate(String var1, int var2) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long executeLargeUpdate(String var1, int[] var2) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public long executeLargeUpdate(String var1, String[] var2) throws SQLException {
      try {
         return super.delegate.executeLargeUpdate(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   HikariProxyStatement(ProxyConnection var1, Statement var2) {
      super(var1, var2);
   }
}
