package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.util.FastList;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Wrapper;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public final class HikariProxyConnection extends ProxyConnection implements Wrapper, AutoCloseable, Connection {
   public Statement createStatement() throws SQLException {
      try {
         return super.createStatement();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public PreparedStatement prepareStatement(String var1) throws SQLException {
      try {
         return super.prepareStatement(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public CallableStatement prepareCall(String var1) throws SQLException {
      try {
         return super.prepareCall(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public String nativeSQL(String var1) throws SQLException {
      try {
         return super.delegate.nativeSQL(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setAutoCommit(boolean var1) throws SQLException {
      try {
         super.setAutoCommit(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean getAutoCommit() throws SQLException {
      try {
         return super.delegate.getAutoCommit();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void commit() throws SQLException {
      try {
         super.commit();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void rollback() throws SQLException {
      try {
         super.rollback();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         return super.isClosed();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public DatabaseMetaData getMetaData() throws SQLException {
      try {
         return super.getMetaData();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setReadOnly(boolean var1) throws SQLException {
      try {
         super.setReadOnly(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean isReadOnly() throws SQLException {
      try {
         return super.delegate.isReadOnly();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setCatalog(String var1) throws SQLException {
      try {
         super.setCatalog(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public String getCatalog() throws SQLException {
      try {
         return super.delegate.getCatalog();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setTransactionIsolation(int var1) throws SQLException {
      try {
         super.setTransactionIsolation(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getTransactionIsolation() throws SQLException {
      try {
         return super.delegate.getTransactionIsolation();
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

   public Statement createStatement(int var1, int var2) throws SQLException {
      try {
         return super.createStatement(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public PreparedStatement prepareStatement(String var1, int var2, int var3) throws SQLException {
      try {
         return super.prepareStatement(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public CallableStatement prepareCall(String var1, int var2, int var3) throws SQLException {
      try {
         return super.prepareCall(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public Map getTypeMap() throws SQLException {
      try {
         return super.delegate.getTypeMap();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void setTypeMap(Map var1) throws SQLException {
      try {
         super.delegate.setTypeMap(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setHoldability(int var1) throws SQLException {
      try {
         super.delegate.setHoldability(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getHoldability() throws SQLException {
      try {
         return super.delegate.getHoldability();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Savepoint setSavepoint() throws SQLException {
      try {
         return super.delegate.setSavepoint();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Savepoint setSavepoint(String var1) throws SQLException {
      try {
         return super.delegate.setSavepoint(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void rollback(Savepoint var1) throws SQLException {
      try {
         super.rollback(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void releaseSavepoint(Savepoint var1) throws SQLException {
      try {
         super.delegate.releaseSavepoint(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Statement createStatement(int var1, int var2, int var3) throws SQLException {
      try {
         return super.createStatement(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public PreparedStatement prepareStatement(String var1, int var2, int var3, int var4) throws SQLException {
      try {
         return super.prepareStatement(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public CallableStatement prepareCall(String var1, int var2, int var3, int var4) throws SQLException {
      try {
         return super.prepareCall(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public PreparedStatement prepareStatement(String var1, int var2) throws SQLException {
      try {
         return super.prepareStatement(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public PreparedStatement prepareStatement(String var1, int[] var2) throws SQLException {
      try {
         return super.prepareStatement(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public PreparedStatement prepareStatement(String var1, String[] var2) throws SQLException {
      try {
         return super.prepareStatement(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Clob createClob() throws SQLException {
      try {
         return super.delegate.createClob();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Blob createBlob() throws SQLException {
      try {
         return super.delegate.createBlob();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public NClob createNClob() throws SQLException {
      try {
         return super.delegate.createNClob();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public SQLXML createSQLXML() throws SQLException {
      try {
         return super.delegate.createSQLXML();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isValid(int var1) throws SQLException {
      try {
         return super.delegate.isValid(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setClientInfo(String var1, String var2) throws SQLClientInfoException {
      ((Connection)super.delegate).setClientInfo(var1, var2);
   }

   public void setClientInfo(Properties var1) throws SQLClientInfoException {
      ((Connection)super.delegate).setClientInfo(var1);
   }

   public String getClientInfo(String var1) throws SQLException {
      try {
         return super.delegate.getClientInfo(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public Properties getClientInfo() throws SQLException {
      try {
         return super.delegate.getClientInfo();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public Array createArrayOf(String var1, Object[] var2) throws SQLException {
      try {
         return super.delegate.createArrayOf(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public Struct createStruct(String var1, Object[] var2) throws SQLException {
      try {
         return super.delegate.createStruct(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public void setSchema(String var1) throws SQLException {
      try {
         super.setSchema(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public String getSchema() throws SQLException {
      try {
         return super.delegate.getSchema();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public void abort(Executor var1) throws SQLException {
      try {
         super.delegate.abort(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public void setNetworkTimeout(Executor var1, int var2) throws SQLException {
      try {
         super.setNetworkTimeout(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public int getNetworkTimeout() throws SQLException {
      try {
         return super.delegate.getNetworkTimeout();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   protected HikariProxyConnection(PoolEntry var1, Connection var2, FastList var3, ProxyLeakTask var4, long var5, boolean var7, boolean var8) {
      super(var1, var2, var3, var4, var5, var7, var8);
   }
}
