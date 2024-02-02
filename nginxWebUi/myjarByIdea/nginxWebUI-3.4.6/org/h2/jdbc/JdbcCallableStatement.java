package org.h2.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.value.ValueNull;

public final class JdbcCallableStatement extends JdbcPreparedStatement implements CallableStatement {
   private BitSet outParameters;
   private int maxOutParameters;
   private HashMap<String, Integer> namedParameters;

   JdbcCallableStatement(JdbcConnection var1, String var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5, (Object)null);
      this.setTrace(this.session.getTrace(), 0, var3);
   }

   public int executeUpdate() throws SQLException {
      try {
         this.checkClosed();
         if (this.command.isQuery()) {
            super.executeQuery();
            return 0;
         } else {
            return super.executeUpdate();
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public long executeLargeUpdate() throws SQLException {
      try {
         this.checkClosed();
         if (this.command.isQuery()) {
            super.executeQuery();
            return 0L;
         } else {
            return super.executeLargeUpdate();
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void registerOutParameter(int var1, int var2) throws SQLException {
      this.registerOutParameter(var1);
   }

   public void registerOutParameter(int var1, int var2, String var3) throws SQLException {
      this.registerOutParameter(var1);
   }

   public void registerOutParameter(int var1, int var2, int var3) throws SQLException {
      this.registerOutParameter(var1);
   }

   public void registerOutParameter(String var1, int var2, String var3) throws SQLException {
      this.registerOutParameter(this.getIndexForName(var1), var2, var3);
   }

   public void registerOutParameter(String var1, int var2, int var3) throws SQLException {
      this.registerOutParameter(this.getIndexForName(var1), var2, var3);
   }

   public void registerOutParameter(String var1, int var2) throws SQLException {
      this.registerOutParameter(this.getIndexForName(var1), var2);
   }

   public boolean wasNull() throws SQLException {
      return this.getOpenResultSet().wasNull();
   }

   public URL getURL(int var1) throws SQLException {
      throw this.unsupported("url");
   }

   public String getString(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getString(var1);
   }

   public boolean getBoolean(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getBoolean(var1);
   }

   public byte getByte(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getByte(var1);
   }

   public short getShort(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getShort(var1);
   }

   public int getInt(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getInt(var1);
   }

   public long getLong(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getLong(var1);
   }

   public float getFloat(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getFloat(var1);
   }

   public double getDouble(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getBigDecimal(var1, var2);
   }

   public byte[] getBytes(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getBytes(var1);
   }

   public Date getDate(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getDate(var1);
   }

   public Time getTime(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getTime(var1);
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getTimestamp(var1);
   }

   public Object getObject(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getObject(var1);
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getBigDecimal(var1);
   }

   public Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException {
      throw this.unsupported("map");
   }

   public Ref getRef(int var1) throws SQLException {
      throw this.unsupported("ref");
   }

   public Blob getBlob(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getBlob(var1);
   }

   public Clob getClob(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getClob(var1);
   }

   public Array getArray(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getArray(var1);
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getDate(var1, var2);
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getTime(var1, var2);
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getTimestamp(var1, var2);
   }

   public URL getURL(String var1) throws SQLException {
      throw this.unsupported("url");
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      return this.getTimestamp(this.getIndexForName(var1), var2);
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      return this.getTime(this.getIndexForName(var1), var2);
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      return this.getDate(this.getIndexForName(var1), var2);
   }

   public Array getArray(String var1) throws SQLException {
      return this.getArray(this.getIndexForName(var1));
   }

   public Clob getClob(String var1) throws SQLException {
      return this.getClob(this.getIndexForName(var1));
   }

   public Blob getBlob(String var1) throws SQLException {
      return this.getBlob(this.getIndexForName(var1));
   }

   public Ref getRef(String var1) throws SQLException {
      throw this.unsupported("ref");
   }

   public Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException {
      throw this.unsupported("map");
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      return this.getBigDecimal(this.getIndexForName(var1));
   }

   public Object getObject(String var1) throws SQLException {
      return this.getObject(this.getIndexForName(var1));
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      return this.getTimestamp(this.getIndexForName(var1));
   }

   public Time getTime(String var1) throws SQLException {
      return this.getTime(this.getIndexForName(var1));
   }

   public Date getDate(String var1) throws SQLException {
      return this.getDate(this.getIndexForName(var1));
   }

   public byte[] getBytes(String var1) throws SQLException {
      return this.getBytes(this.getIndexForName(var1));
   }

   public double getDouble(String var1) throws SQLException {
      return this.getDouble(this.getIndexForName(var1));
   }

   public float getFloat(String var1) throws SQLException {
      return this.getFloat(this.getIndexForName(var1));
   }

   public long getLong(String var1) throws SQLException {
      return this.getLong(this.getIndexForName(var1));
   }

   public int getInt(String var1) throws SQLException {
      return this.getInt(this.getIndexForName(var1));
   }

   public short getShort(String var1) throws SQLException {
      return this.getShort(this.getIndexForName(var1));
   }

   public byte getByte(String var1) throws SQLException {
      return this.getByte(this.getIndexForName(var1));
   }

   public boolean getBoolean(String var1) throws SQLException {
      return this.getBoolean(this.getIndexForName(var1));
   }

   public String getString(String var1) throws SQLException {
      return this.getString(this.getIndexForName(var1));
   }

   public RowId getRowId(int var1) throws SQLException {
      throw this.unsupported("rowId");
   }

   public RowId getRowId(String var1) throws SQLException {
      throw this.unsupported("rowId");
   }

   public NClob getNClob(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getNClob(var1);
   }

   public NClob getNClob(String var1) throws SQLException {
      return this.getNClob(this.getIndexForName(var1));
   }

   public SQLXML getSQLXML(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getSQLXML(var1);
   }

   public SQLXML getSQLXML(String var1) throws SQLException {
      return this.getSQLXML(this.getIndexForName(var1));
   }

   public String getNString(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getNString(var1);
   }

   public String getNString(String var1) throws SQLException {
      return this.getNString(this.getIndexForName(var1));
   }

   public Reader getNCharacterStream(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getNCharacterStream(var1);
   }

   public Reader getNCharacterStream(String var1) throws SQLException {
      return this.getNCharacterStream(this.getIndexForName(var1));
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      this.checkRegistered(var1);
      return this.getOpenResultSet().getCharacterStream(var1);
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      return this.getCharacterStream(this.getIndexForName(var1));
   }

   public void setNull(String var1, int var2, String var3) throws SQLException {
      this.setNull(this.getIndexForName(var1), var2, var3);
   }

   public void setNull(String var1, int var2) throws SQLException {
      this.setNull(this.getIndexForName(var1), var2);
   }

   public void setTimestamp(String var1, Timestamp var2, Calendar var3) throws SQLException {
      this.setTimestamp(this.getIndexForName(var1), var2, var3);
   }

   public void setTime(String var1, Time var2, Calendar var3) throws SQLException {
      this.setTime(this.getIndexForName(var1), var2, var3);
   }

   public void setDate(String var1, Date var2, Calendar var3) throws SQLException {
      this.setDate(this.getIndexForName(var1), var2, var3);
   }

   public void setCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      this.setCharacterStream(this.getIndexForName(var1), var2, var3);
   }

   public void setObject(String var1, Object var2) throws SQLException {
      this.setObject(this.getIndexForName(var1), var2);
   }

   public void setObject(String var1, Object var2, int var3) throws SQLException {
      this.setObject(this.getIndexForName(var1), var2, var3);
   }

   public void setObject(String var1, Object var2, int var3, int var4) throws SQLException {
      this.setObject(this.getIndexForName(var1), var2, var3, var4);
   }

   public void setObject(String var1, Object var2, SQLType var3) throws SQLException {
      this.setObject(this.getIndexForName(var1), var2, var3);
   }

   public void setObject(String var1, Object var2, SQLType var3, int var4) throws SQLException {
      this.setObject(this.getIndexForName(var1), var2, var3, var4);
   }

   public void setBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      this.setBinaryStream(this.getIndexForName(var1), var2, var3);
   }

   public void setAsciiStream(String var1, InputStream var2, long var3) throws SQLException {
      this.setAsciiStream(this.getIndexForName(var1), var2, var3);
   }

   public void setTimestamp(String var1, Timestamp var2) throws SQLException {
      this.setTimestamp(this.getIndexForName(var1), var2);
   }

   public void setTime(String var1, Time var2) throws SQLException {
      this.setTime(this.getIndexForName(var1), var2);
   }

   public void setDate(String var1, Date var2) throws SQLException {
      this.setDate(this.getIndexForName(var1), var2);
   }

   public void setBytes(String var1, byte[] var2) throws SQLException {
      this.setBytes(this.getIndexForName(var1), var2);
   }

   public void setString(String var1, String var2) throws SQLException {
      this.setString(this.getIndexForName(var1), var2);
   }

   public void setBigDecimal(String var1, BigDecimal var2) throws SQLException {
      this.setBigDecimal(this.getIndexForName(var1), var2);
   }

   public void setDouble(String var1, double var2) throws SQLException {
      this.setDouble(this.getIndexForName(var1), var2);
   }

   public void setFloat(String var1, float var2) throws SQLException {
      this.setFloat(this.getIndexForName(var1), var2);
   }

   public void setLong(String var1, long var2) throws SQLException {
      this.setLong(this.getIndexForName(var1), var2);
   }

   public void setInt(String var1, int var2) throws SQLException {
      this.setInt(this.getIndexForName(var1), var2);
   }

   public void setShort(String var1, short var2) throws SQLException {
      this.setShort(this.getIndexForName(var1), var2);
   }

   public void setByte(String var1, byte var2) throws SQLException {
      this.setByte(this.getIndexForName(var1), var2);
   }

   public void setBoolean(String var1, boolean var2) throws SQLException {
      this.setBoolean(this.getIndexForName(var1), var2);
   }

   public void setURL(String var1, URL var2) throws SQLException {
      throw this.unsupported("url");
   }

   public void setRowId(String var1, RowId var2) throws SQLException {
      throw this.unsupported("rowId");
   }

   public void setNString(String var1, String var2) throws SQLException {
      this.setNString(this.getIndexForName(var1), var2);
   }

   public void setNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      this.setNCharacterStream(this.getIndexForName(var1), var2, var3);
   }

   public void setNClob(String var1, NClob var2) throws SQLException {
      this.setNClob(this.getIndexForName(var1), var2);
   }

   public void setClob(String var1, Reader var2, long var3) throws SQLException {
      this.setClob(this.getIndexForName(var1), var2, var3);
   }

   public void setBlob(String var1, InputStream var2, long var3) throws SQLException {
      this.setBlob(this.getIndexForName(var1), var2, var3);
   }

   public void setNClob(String var1, Reader var2, long var3) throws SQLException {
      this.setNClob(this.getIndexForName(var1), var2, var3);
   }

   public void setBlob(String var1, Blob var2) throws SQLException {
      this.setBlob(this.getIndexForName(var1), var2);
   }

   public void setClob(String var1, Clob var2) throws SQLException {
      this.setClob(this.getIndexForName(var1), var2);
   }

   public void setAsciiStream(String var1, InputStream var2) throws SQLException {
      this.setAsciiStream(this.getIndexForName(var1), var2);
   }

   public void setAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      this.setAsciiStream(this.getIndexForName(var1), var2, var3);
   }

   public void setBinaryStream(String var1, InputStream var2) throws SQLException {
      this.setBinaryStream(this.getIndexForName(var1), var2);
   }

   public void setBinaryStream(String var1, InputStream var2, long var3) throws SQLException {
      this.setBinaryStream(this.getIndexForName(var1), var2, var3);
   }

   public void setBlob(String var1, InputStream var2) throws SQLException {
      this.setBlob(this.getIndexForName(var1), var2);
   }

   public void setCharacterStream(String var1, Reader var2) throws SQLException {
      this.setCharacterStream(this.getIndexForName(var1), var2);
   }

   public void setCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      this.setCharacterStream(this.getIndexForName(var1), var2, var3);
   }

   public void setClob(String var1, Reader var2) throws SQLException {
      this.setClob(this.getIndexForName(var1), var2);
   }

   public void setNCharacterStream(String var1, Reader var2) throws SQLException {
      this.setNCharacterStream(this.getIndexForName(var1), var2);
   }

   public void setNClob(String var1, Reader var2) throws SQLException {
      this.setNClob(this.getIndexForName(var1), var2);
   }

   public void setSQLXML(String var1, SQLXML var2) throws SQLException {
      this.setSQLXML(this.getIndexForName(var1), var2);
   }

   public <T> T getObject(int var1, Class<T> var2) throws SQLException {
      return this.getOpenResultSet().getObject(var1, var2);
   }

   public <T> T getObject(String var1, Class<T> var2) throws SQLException {
      return this.getObject(this.getIndexForName(var1), var2);
   }

   private ResultSetMetaData getCheckedMetaData() throws SQLException {
      ResultSetMetaData var1 = this.getMetaData();
      if (var1 == null) {
         throw DbException.getUnsupportedException("Supported only for calling stored procedures");
      } else {
         return var1;
      }
   }

   private void checkIndexBounds(int var1) {
      this.checkClosed();
      if (var1 < 1 || var1 > this.maxOutParameters) {
         throw DbException.getInvalidValueException("parameterIndex", var1);
      }
   }

   private void registerOutParameter(int var1) throws SQLException {
      try {
         this.checkClosed();
         if (this.outParameters == null) {
            this.maxOutParameters = Math.min(this.getParameterMetaData().getParameterCount(), this.getCheckedMetaData().getColumnCount());
            this.outParameters = new BitSet();
         }

         this.checkIndexBounds(var1);
         ArrayList var10000 = this.command.getParameters();
         --var1;
         ParameterInterface var2 = (ParameterInterface)var10000.get(var1);
         if (!var2.isValueSet()) {
            var2.setValue(ValueNull.INSTANCE, false);
         }

         this.outParameters.set(var1);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private void checkRegistered(int var1) throws SQLException {
      try {
         this.checkIndexBounds(var1);
         if (!this.outParameters.get(var1 - 1)) {
            throw DbException.getInvalidValueException("parameterIndex", var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private int getIndexForName(String var1) throws SQLException {
      try {
         this.checkClosed();
         if (this.namedParameters == null) {
            ResultSetMetaData var2 = this.getCheckedMetaData();
            int var3 = var2.getColumnCount();
            HashMap var4 = new HashMap();

            for(int var5 = 1; var5 <= var3; ++var5) {
               var4.put(var2.getColumnLabel(var5), var5);
            }

            this.namedParameters = var4;
         }

         Integer var7 = (Integer)this.namedParameters.get(var1);
         if (var7 == null) {
            throw DbException.getInvalidValueException("parameterName", var1);
         } else {
            return var7;
         }
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   private JdbcResultSet getOpenResultSet() throws SQLException {
      try {
         this.checkClosed();
         if (this.resultSet == null) {
            throw DbException.get(2000);
         } else {
            if (this.resultSet.isBeforeFirst()) {
               this.resultSet.next();
            }

            return this.resultSet;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }
}
