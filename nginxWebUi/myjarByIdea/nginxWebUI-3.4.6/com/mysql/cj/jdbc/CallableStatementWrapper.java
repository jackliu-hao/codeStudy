package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.jdbc.exceptions.SQLError;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CallableStatementWrapper extends PreparedStatementWrapper implements java.sql.CallableStatement {
   protected static CallableStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.CallableStatement toWrap) throws SQLException {
      return new CallableStatementWrapper(c, conn, toWrap);
   }

   public CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.CallableStatement toWrap) {
      super(c, conn, toWrap);
   }

   public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public boolean wasNull() throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).wasNull();
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var2) {
         this.checkAndFireConnectionError(var2);
         return false;
      }
   }

   public String getString(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getString(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public boolean getBoolean(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBoolean(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return false;
      }
   }

   public byte getByte(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getByte(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0;
      }
   }

   public short getShort(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getShort(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0;
      }
   }

   public int getInt(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getInt(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0;
      }
   }

   public long getLong(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getLong(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0L;
      }
   }

   public float getFloat(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getFloat(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0.0F;
      }
   }

   public double getDouble(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getDouble(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0.0;
      }
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex, scale);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public byte[] getBytes(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBytes(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Date getDate(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getDate(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Time getTime(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTime(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Timestamp getTimestamp(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Object getObject(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getObject(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBigDecimal(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Object getObject(int parameterIndex, Map<String, Class<?>> typeMap) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getObject(parameterIndex, typeMap);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public Ref getRef(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getRef(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public java.sql.Blob getBlob(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBlob(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public java.sql.Clob getClob(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getClob(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Array getArray(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getArray(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getDate(parameterIndex, cal);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTime(parameterIndex, cal);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTimestamp(parameterIndex, cal);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(paramIndex, sqlType, typeName);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public URL getURL(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getURL(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public void setURL(String parameterName, URL val) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setURL(parameterName, val);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setNull(String parameterName, int sqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setBoolean(String parameterName, boolean x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBoolean(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setByte(String parameterName, byte x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setByte(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setShort(String parameterName, short x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setShort(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setInt(String parameterName, int x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setInt(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setLong(String parameterName, long x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setLong(parameterName, x);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setFloat(String parameterName, float x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setFloat(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setDouble(String parameterName, double x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setDouble(parameterName, x);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBigDecimal(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setString(String parameterName, String x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setString(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setBytes(String parameterName, byte[] x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBytes(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setDate(String parameterName, Date x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setDate(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setTime(String parameterName, Time x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setTime(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scale);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setObject(String parameterName, Object x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setObject(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setDate(parameterName, x, cal);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setTime(parameterName, x, cal);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setTimestamp(parameterName, x, cal);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNull(parameterName, sqlType, typeName);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public String getString(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getString(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public boolean getBoolean(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBoolean(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return false;
      }
   }

   public byte getByte(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getByte(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0;
      }
   }

   public short getShort(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getShort(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0;
      }
   }

   public int getInt(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getInt(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0;
      }
   }

   public long getLong(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getLong(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0L;
      }
   }

   public float getFloat(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getFloat(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0.0F;
      }
   }

   public double getDouble(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getDouble(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return 0.0;
      }
   }

   public byte[] getBytes(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBytes(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Date getDate(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getDate(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Time getTime(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTime(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Timestamp getTimestamp(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTimestamp(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Object getObject(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getObject(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public BigDecimal getBigDecimal(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBigDecimal(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Object getObject(String parameterName, Map<String, Class<?>> typeMap) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getObject(parameterName, typeMap);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public Ref getRef(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getRef(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public java.sql.Blob getBlob(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getBlob(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public java.sql.Clob getClob(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getClob(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Array getArray(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getArray(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Date getDate(String parameterName, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getDate(parameterName, cal);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public Time getTime(String parameterName, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTime(parameterName, cal);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getTimestamp(parameterName, cal);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
         return null;
      }
   }

   public URL getURL(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getURL(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public RowId getRowId(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getRowId(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public RowId getRowId(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getRowId(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public void setRowId(String parameterName, RowId x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setRowId(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setNString(String parameterName, String value) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNString(parameterName, value);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader, length);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setNClob(String parameterName, java.sql.NClob value) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNClob(parameterName, value);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setClob(String parameterName, Reader reader, long length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setClob(parameterName, reader, length);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBlob(parameterName, x, length);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNClob(parameterName, reader, length);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public java.sql.NClob getNClob(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getNClob(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public java.sql.NClob getNClob(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getNClob(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setSQLXML(parameterName, xmlObject);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public SQLXML getSQLXML(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getSQLXML(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public SQLXML getSQLXML(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getSQLXML(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public String getNString(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getNString(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public String getNString(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getNString(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Reader getNCharacterStream(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getNCharacterStream(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Reader getNCharacterStream(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getNCharacterStream(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Reader getCharacterStream(int parameterIndex) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getCharacterStream(parameterIndex);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public Reader getCharacterStream(String parameterName) throws SQLException {
      try {
         if (this.wrappedStmt != null) {
            return ((java.sql.CallableStatement)this.wrappedStmt).getCharacterStream(parameterName);
         } else {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }
      } catch (SQLException var3) {
         this.checkAndFireConnectionError(var3);
         return null;
      }
   }

   public void setBlob(String parameterName, java.sql.Blob x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setClob(String parameterName, java.sql.Clob x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setClob(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setClob(String parameterName, Reader reader) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setClob(parameterName, reader);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setBlob(String parameterName, InputStream x) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void setNClob(String parameterName, Reader reader) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setNClob(parameterName, reader);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
      if (this.wrappedStmt != null) {
         return null;
      } else {
         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
      }
   }

   public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
      if (this.wrappedStmt != null) {
         return null;
      } else {
         throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
      }
   }

   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      boolean isInstance = iface.isInstance(this);
      if (isInstance) {
         return true;
      } else {
         String interfaceClassName = iface.getName();
         return interfaceClassName.equals("com.mysql.cj.jdbc.Statement") || interfaceClassName.equals("java.sql.Statement") || interfaceClassName.equals("java.sql.Wrapper") || interfaceClassName.equals("java.sql.PreparedStatement") || interfaceClassName.equals("java.sql.CallableStatement");
      }
   }

   public void close() throws SQLException {
      try {
         super.close();
      } finally {
         this.unwrappedInterfaces = null;
      }

   }

   public synchronized <T> T unwrap(Class<T> iface) throws SQLException {
      try {
         if (!"java.sql.Statement".equals(iface.getName()) && !"java.sql.CallableStatement".equals(iface.getName()) && !"java.sql.PreparedStatement".equals(iface.getName()) && !"java.sql.Wrapper.class".equals(iface.getName())) {
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

   public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, typeName);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
      } catch (SQLException var4) {
         this.checkAndFireConnectionError(var4);
      }

   }

   public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }

   public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
      } catch (SQLException var5) {
         this.checkAndFireConnectionError(var5);
      }

   }

   public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
      try {
         if (this.wrappedStmt == null) {
            throw SQLError.createSQLException(Messages.getString("Statement.AlreadyClosed"), "S1000", this.exceptionInterceptor);
         }

         ((java.sql.CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scaleOrLength);
      } catch (SQLException var6) {
         this.checkAndFireConnectionError(var6);
      }

   }
}
