package org.h2.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import org.h2.command.CommandInterface;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.result.MergedResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.util.IOUtils;
import org.h2.util.LegacyDateTimeUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

public class JdbcPreparedStatement extends JdbcStatement implements PreparedStatement {
   protected CommandInterface command;
   private ArrayList<Value[]> batchParameters;
   private MergedResult batchIdentities;
   private HashMap<String, Integer> cachedColumnLabelMap;
   private final Object generatedKeysRequest;

   JdbcPreparedStatement(JdbcConnection var1, String var2, int var3, int var4, int var5, Object var6) {
      super(var1, var3, var4, var5);
      this.generatedKeysRequest = var6;
      this.setTrace(this.session.getTrace(), 3, var3);
      this.command = var1.prepareCommand(var2, this.fetchSize);
   }

   void setCachedColumnLabelMap(HashMap<String, Integer> var1) {
      this.cachedColumnLabelMap = var1;
   }

   public ResultSet executeQuery() throws SQLException {
      try {
         int var1 = getNextId(4);
         this.debugCodeAssign("ResultSet", 4, var1, "executeQuery()");
         this.batchIdentities = null;
         synchronized(this.session) {
            this.checkClosed();
            this.closeOldResultSet();
            boolean var4 = false;
            boolean var5 = this.resultSetType != 1003;
            boolean var6 = this.resultSetConcurrency == 1008;

            ResultInterface var3;
            try {
               this.setExecutingStatement(this.command);
               var3 = this.command.executeQuery(this.maxRows, var5);
               var4 = var3.isLazy();
            } finally {
               if (!var4) {
                  this.setExecutingStatement((CommandInterface)null);
               }

            }

            this.resultSet = new JdbcResultSet(this.conn, this, this.command, var3, var1, var5, var6, this.cachedColumnLabelMap);
         }

         return this.resultSet;
      } catch (Exception var14) {
         throw this.logAndConvert(var14);
      }
   }

   public int executeUpdate() throws SQLException {
      try {
         this.debugCodeCall("executeUpdate");
         this.checkClosed();
         this.batchIdentities = null;
         long var1 = this.executeUpdateInternal();
         return var1 <= 2147483647L ? (int)var1 : -2;
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public long executeLargeUpdate() throws SQLException {
      try {
         this.debugCodeCall("executeLargeUpdate");
         this.checkClosed();
         this.batchIdentities = null;
         return this.executeUpdateInternal();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   private long executeUpdateInternal() {
      this.closeOldResultSet();
      synchronized(this.session) {
         try {
            this.setExecutingStatement(this.command);
            ResultWithGeneratedKeys var2 = this.command.executeUpdate(this.generatedKeysRequest);
            this.updateCount = var2.getUpdateCount();
            ResultInterface var3 = var2.getGeneratedKeys();
            if (var3 != null) {
               int var4 = getNextId(4);
               this.generatedKeys = new JdbcResultSet(this.conn, this, this.command, var3, var4, true, false, false);
            }
         } finally {
            this.setExecutingStatement((CommandInterface)null);
         }
      }

      return this.updateCount;
   }

   public boolean execute() throws SQLException {
      try {
         int var1 = getNextId(4);
         this.debugCodeCall("execute");
         this.checkClosed();
         boolean var2;
         synchronized(this.session) {
            this.closeOldResultSet();
            boolean var4 = false;

            try {
               this.setExecutingStatement(this.command);
               if (this.command.isQuery()) {
                  var2 = true;
                  boolean var5 = this.resultSetType != 1003;
                  boolean var6 = this.resultSetConcurrency == 1008;
                  ResultInterface var7 = this.command.executeQuery(this.maxRows, var5);
                  var4 = var7.isLazy();
                  this.resultSet = new JdbcResultSet(this.conn, this, this.command, var7, var1, var5, var6, this.cachedColumnLabelMap);
               } else {
                  var2 = false;
                  ResultWithGeneratedKeys var16 = this.command.executeUpdate(this.generatedKeysRequest);
                  this.updateCount = var16.getUpdateCount();
                  ResultInterface var17 = var16.getGeneratedKeys();
                  if (var17 != null) {
                     this.generatedKeys = new JdbcResultSet(this.conn, this, this.command, var17, var1, true, false, false);
                  }
               }
            } finally {
               if (!var4) {
                  this.setExecutingStatement((CommandInterface)null);
               }

            }
         }

         return var2;
      } catch (Throwable var15) {
         throw this.logAndConvert(var15);
      }
   }

   public void clearParameters() throws SQLException {
      try {
         this.debugCodeCall("clearParameters");
         this.checkClosed();
         ArrayList var1 = this.command.getParameters();
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ParameterInterface var3 = (ParameterInterface)var2.next();
            var3.setValue((Value)null, this.batchParameters == null);
         }

      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public ResultSet executeQuery(String var1) throws SQLException {
      try {
         this.debugCodeCall("executeQuery", var1);
         throw DbException.get(90130);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void addBatch(String var1) throws SQLException {
      try {
         this.debugCodeCall("addBatch", var1);
         throw DbException.get(90130);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void setNull(int var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setNull(" + var1 + ", " + var2 + ')');
         }

         this.setParameter(var1, ValueNull.INSTANCE);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setInt(int var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setInt(" + var1 + ", " + var2 + ')');
         }

         this.setParameter(var1, ValueInteger.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setString(int var1, String var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setString(" + var1 + ", " + quote(var2) + ')');
         }

         this.setParameter(var1, (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarchar.get(var2, this.conn)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setBigDecimal(int var1, BigDecimal var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setBigDecimal(" + var1 + ", " + quoteBigDecimal(var2) + ')');
         }

         this.setParameter(var1, (Value)(var2 == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setDate(int var1, Date var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setDate(" + var1 + ", " + quoteDate(var2) + ')');
         }

         this.setParameter(var1, (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setTime(int var1, Time var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setTime(" + var1 + ", " + quoteTime(var2) + ')');
         }

         this.setParameter(var1, (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setTimestamp(int var1, Timestamp var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setTimestamp(" + var1 + ", " + quoteTimestamp(var2) + ')');
         }

         this.setParameter(var1, (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setObject(int var1, Object var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setObject(" + var1 + ", x)");
         }

         if (var2 == null) {
            this.setParameter(var1, ValueNull.INSTANCE);
         } else {
            this.setParameter(var1, ValueToObjectConverter.objectToValue(this.session, var2, -1));
         }

      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setObject(int var1, Object var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setObject(" + var1 + ", x, " + var3 + ')');
         }

         this.setObjectWithType(var1, var2, DataType.convertSQLTypeToValueType(var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setObject(int var1, Object var2, int var3, int var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setObject(" + var1 + ", x, " + var3 + ", " + var4 + ')');
         }

         this.setObjectWithType(var1, var2, DataType.convertSQLTypeToValueType(var3));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setObject(int var1, Object var2, SQLType var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setObject(" + var1 + ", x, " + DataType.sqlTypeToString(var3) + ')');
         }

         this.setObjectWithType(var1, var2, DataType.convertSQLTypeToValueType(var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setObject(int var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setObject(" + var1 + ", x, " + DataType.sqlTypeToString(var3) + ", " + var4 + ')');
         }

         this.setObjectWithType(var1, var2, DataType.convertSQLTypeToValueType(var3));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   private void setObjectWithType(int var1, Object var2, int var3) {
      if (var2 == null) {
         this.setParameter(var1, ValueNull.INSTANCE);
      } else {
         Value var4 = ValueToObjectConverter.objectToValue(this.conn.getSession(), var2, var3);
         if (var3 != -1) {
            var4 = var4.convertTo(var3, this.conn);
         }

         this.setParameter(var1, var4);
      }

   }

   public void setBoolean(int var1, boolean var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setBoolean(" + var1 + ", " + var2 + ')');
         }

         this.setParameter(var1, ValueBoolean.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setByte(int var1, byte var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setByte(" + var1 + ", " + var2 + ')');
         }

         this.setParameter(var1, ValueTinyint.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setShort(int var1, short var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setShort(" + var1 + ", (short) " + var2 + ')');
         }

         this.setParameter(var1, ValueSmallint.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setLong(int var1, long var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setLong(" + var1 + ", " + var2 + "L)");
         }

         this.setParameter(var1, ValueBigint.get(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setFloat(int var1, float var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setFloat(" + var1 + ", " + var2 + "f)");
         }

         this.setParameter(var1, ValueReal.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setDouble(int var1, double var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setDouble(" + var1 + ", " + var2 + "d)");
         }

         this.setParameter(var1, ValueDouble.get(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setRef(int var1, Ref var2) throws SQLException {
      throw this.unsupported("ref");
   }

   public void setDate(int var1, Date var2, Calendar var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setDate(" + var1 + ", " + quoteDate(var2) + ", calendar)");
         }

         if (var2 == null) {
            this.setParameter(var1, ValueNull.INSTANCE);
         } else {
            this.setParameter(var1, LegacyDateTimeUtils.fromDate(this.conn, var3 != null ? var3.getTimeZone() : null, var2));
         }

      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setTime(int var1, Time var2, Calendar var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setTime(" + var1 + ", " + quoteTime(var2) + ", calendar)");
         }

         if (var2 == null) {
            this.setParameter(var1, ValueNull.INSTANCE);
         } else {
            this.setParameter(var1, LegacyDateTimeUtils.fromTime(this.conn, var3 != null ? var3.getTimeZone() : null, var2));
         }

      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setTimestamp(int var1, Timestamp var2, Calendar var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setTimestamp(" + var1 + ", " + quoteTimestamp(var2) + ", calendar)");
         }

         if (var2 == null) {
            this.setParameter(var1, ValueNull.INSTANCE);
         } else {
            this.setParameter(var1, LegacyDateTimeUtils.fromTimestamp(this.conn, var3 != null ? var3.getTimeZone() : null, var2));
         }

      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   /** @deprecated */
   @Deprecated
   public void setUnicodeStream(int var1, InputStream var2, int var3) throws SQLException {
      throw this.unsupported("unicodeStream");
   }

   public void setNull(int var1, int var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setNull(" + var1 + ", " + var2 + ", " + quote(var3) + ')');
         }

         this.setNull(var1, var2);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void setBlob(int var1, Blob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setBlob(" + var1 + ", x)");
         }

         this.checkClosed();
         Object var3;
         if (var2 == null) {
            var3 = ValueNull.INSTANCE;
         } else {
            var3 = this.conn.createBlob(var2.getBinaryStream(), -1L);
         }

         this.setParameter(var1, (Value)var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setBlob(int var1, InputStream var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setBlob(" + var1 + ", x)");
         }

         this.checkClosed();
         Value var3 = this.conn.createBlob(var2, -1L);
         this.setParameter(var1, var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setClob(int var1, Clob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setClob(" + var1 + ", x)");
         }

         this.checkClosed();
         Object var3;
         if (var2 == null) {
            var3 = ValueNull.INSTANCE;
         } else {
            var3 = this.conn.createClob(var2.getCharacterStream(), -1L);
         }

         this.setParameter(var1, (Value)var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setClob(int var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setClob(" + var1 + ", x)");
         }

         this.checkClosed();
         Object var3;
         if (var2 == null) {
            var3 = ValueNull.INSTANCE;
         } else {
            var3 = this.conn.createClob(var2, -1L);
         }

         this.setParameter(var1, (Value)var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setArray(int var1, Array var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setArray(" + var1 + ", x)");
         }

         this.checkClosed();
         Object var3;
         if (var2 == null) {
            var3 = ValueNull.INSTANCE;
         } else {
            var3 = ValueToObjectConverter.objectToValue(this.session, var2.getArray(), 40);
         }

         this.setParameter(var1, (Value)var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setBytes(int var1, byte[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setBytes(" + var1 + ", " + quoteBytes(var2) + ')');
         }

         this.setParameter(var1, (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarbinary.get(var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setBinaryStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.checkClosed();
         Value var5 = this.conn.createBlob(var2, var3);
         this.setParameter(var1, var5);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      this.setBinaryStream(var1, var2, (long)var3);
   }

   public void setBinaryStream(int var1, InputStream var2) throws SQLException {
      this.setBinaryStream(var1, var2, -1);
   }

   public void setAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      this.setAsciiStream(var1, var2, (long)var3);
   }

   public void setAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setAsciiStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.checkClosed();
         Value var5 = this.conn.createClob(IOUtils.getAsciiReader(var2), var3);
         this.setParameter(var1, var5);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setAsciiStream(int var1, InputStream var2) throws SQLException {
      this.setAsciiStream(var1, var2, -1);
   }

   public void setCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      this.setCharacterStream(var1, var2, (long)var3);
   }

   public void setCharacterStream(int var1, Reader var2) throws SQLException {
      this.setCharacterStream(var1, var2, -1);
   }

   public void setCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setCharacterStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.checkClosed();
         Value var5 = this.conn.createClob(var2, var3);
         this.setParameter(var1, var5);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setURL(int var1, URL var2) throws SQLException {
      throw this.unsupported("url");
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         this.debugCodeCall("getMetaData");
         this.checkClosed();
         ResultInterface var1 = this.command.getMetaData();
         if (var1 == null) {
            return null;
         } else {
            int var2 = getNextId(5);
            this.debugCodeAssign("ResultSetMetaData", 5, var2, "getMetaData()");
            String var3 = this.conn.getCatalog();
            return new JdbcResultSetMetaData((JdbcResultSet)null, this, var1, var3, this.session.getTrace(), var2);
         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void clearBatch() throws SQLException {
      try {
         this.debugCodeCall("clearBatch");
         this.checkClosed();
         this.batchParameters = null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void close() throws SQLException {
      try {
         super.close();
         this.batchParameters = null;
         this.batchIdentities = null;
         if (this.command != null) {
            this.command.close();
            this.command = null;
         }

      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int[] executeBatch() throws SQLException {
      try {
         this.debugCodeCall("executeBatch");
         if (this.batchParameters == null) {
            this.batchParameters = new ArrayList();
         }

         this.batchIdentities = new MergedResult();
         int var1 = this.batchParameters.size();
         int[] var2 = new int[var1];
         SQLException var3 = new SQLException();
         this.checkClosed();

         for(int var4 = 0; var4 < var1; ++var4) {
            long var5 = this.executeBatchElement((Value[])this.batchParameters.get(var4), var3);
            var2[var4] = var5 <= 2147483647L ? (int)var5 : -2;
         }

         this.batchParameters = null;
         var3 = var3.getNextException();
         if (var3 != null) {
            throw new JdbcBatchUpdateException(var3, var2);
         } else {
            return var2;
         }
      } catch (Exception var7) {
         throw this.logAndConvert(var7);
      }
   }

   public long[] executeLargeBatch() throws SQLException {
      try {
         this.debugCodeCall("executeLargeBatch");
         if (this.batchParameters == null) {
            this.batchParameters = new ArrayList();
         }

         this.batchIdentities = new MergedResult();
         int var1 = this.batchParameters.size();
         long[] var2 = new long[var1];
         SQLException var3 = new SQLException();
         this.checkClosed();

         for(int var4 = 0; var4 < var1; ++var4) {
            var2[var4] = this.executeBatchElement((Value[])this.batchParameters.get(var4), var3);
         }

         this.batchParameters = null;
         var3 = var3.getNextException();
         if (var3 != null) {
            throw new JdbcBatchUpdateException(var3, var2);
         } else {
            return var2;
         }
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   private long executeBatchElement(Value[] var1, SQLException var2) {
      ArrayList var3 = this.command.getParameters();
      int var4 = 0;

      for(int var5 = var1.length; var4 < var5; ++var4) {
         ((ParameterInterface)var3.get(var4)).setValue(var1[var4], false);
      }

      long var8;
      try {
         var8 = this.executeUpdateInternal();
         ResultSet var6 = super.getGeneratedKeys();
         this.batchIdentities.add(((JdbcResultSet)var6).result);
      } catch (Exception var7) {
         var2.setNextException(this.logAndConvert(var7));
         var8 = -3L;
      }

      return var8;
   }

   public ResultSet getGeneratedKeys() throws SQLException {
      if (this.batchIdentities != null) {
         try {
            int var1 = getNextId(4);
            this.debugCodeAssign("ResultSet", 4, var1, "getGeneratedKeys()");
            this.checkClosed();
            this.generatedKeys = new JdbcResultSet(this.conn, this, (CommandInterface)null, this.batchIdentities.getResult(), var1, true, false, false);
         } catch (Exception var2) {
            throw this.logAndConvert(var2);
         }
      }

      return super.getGeneratedKeys();
   }

   public void addBatch() throws SQLException {
      try {
         this.debugCodeCall("addBatch");
         this.checkClosed();
         ArrayList var1 = this.command.getParameters();
         int var2 = var1.size();
         Value[] var3 = new Value[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            ParameterInterface var5 = (ParameterInterface)var1.get(var4);
            var5.checkSet();
            Value var6 = var5.getParamValue();
            var3[var4] = var6;
         }

         if (this.batchParameters == null) {
            this.batchParameters = Utils.newSmallArrayList();
         }

         this.batchParameters.add(var3);
      } catch (Exception var7) {
         throw this.logAndConvert(var7);
      }
   }

   public ParameterMetaData getParameterMetaData() throws SQLException {
      try {
         int var1 = getNextId(11);
         this.debugCodeAssign("ParameterMetaData", 11, var1, "getParameterMetaData()");
         this.checkClosed();
         return new JdbcParameterMetaData(this.session.getTrace(), this, this.command, var1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   private void setParameter(int var1, Value var2) {
      this.checkClosed();
      --var1;
      ArrayList var3 = this.command.getParameters();
      if (var1 >= 0 && var1 < var3.size()) {
         ParameterInterface var4 = (ParameterInterface)var3.get(var1);
         var4.setValue(var2, this.batchParameters == null);
      } else {
         throw DbException.getInvalidValueException("parameterIndex", var1 + 1);
      }
   }

   public void setRowId(int var1, RowId var2) throws SQLException {
      throw this.unsupported("rowId");
   }

   public void setNString(int var1, String var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setNString(" + var1 + ", " + quote(var2) + ')');
         }

         this.setParameter(var1, (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarchar.get(var2, this.conn)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setNCharacterStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.checkClosed();
         Value var5 = this.conn.createClob(var2, var3);
         this.setParameter(var1, var5);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setNCharacterStream(int var1, Reader var2) throws SQLException {
      this.setNCharacterStream(var1, var2, -1L);
   }

   public void setNClob(int var1, NClob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setNClob(" + var1 + ", x)");
         }

         this.checkClosed();
         Object var3;
         if (var2 == null) {
            var3 = ValueNull.INSTANCE;
         } else {
            var3 = this.conn.createClob(var2.getCharacterStream(), -1L);
         }

         this.setParameter(var1, (Value)var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setNClob(int var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setNClob(" + var1 + ", x)");
         }

         this.checkClosed();
         Value var3 = this.conn.createClob(var2, -1L);
         this.setParameter(var1, var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void setClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setClob(" + var1 + ", x, " + var3 + "L)");
         }

         this.checkClosed();
         Value var5 = this.conn.createClob(var2, var3);
         this.setParameter(var1, var5);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setBlob(int var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setBlob(" + var1 + ", x, " + var3 + "L)");
         }

         this.checkClosed();
         Value var5 = this.conn.createBlob(var2, var3);
         this.setParameter(var1, var5);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setNClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setNClob(" + var1 + ", x, " + var3 + "L)");
         }

         this.checkClosed();
         Value var5 = this.conn.createClob(var2, var3);
         this.setParameter(var1, var5);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void setSQLXML(int var1, SQLXML var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setSQLXML(" + var1 + ", x)");
         }

         this.checkClosed();
         Object var3;
         if (var2 == null) {
            var3 = ValueNull.INSTANCE;
         } else {
            var3 = this.conn.createClob(var2.getCharacterStream(), -1L);
         }

         this.setParameter(var1, (Value)var3);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public String toString() {
      return this.getTraceObjectName() + ": " + this.command;
   }
}
