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
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.h2.command.CommandInterface;
import org.h2.engine.Session;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.result.UpdatableRow;
import org.h2.util.IOUtils;
import org.h2.util.LegacyDateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.value.CompareMode;
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

public final class JdbcResultSet extends TraceObject implements ResultSet {
   private final boolean scrollable;
   private final boolean updatable;
   private final boolean triggerUpdatable;
   ResultInterface result;
   private JdbcConnection conn;
   private JdbcStatement stat;
   private int columnCount;
   private boolean wasNull;
   private Value[] insertRow;
   private Value[] updateRow;
   private HashMap<String, Integer> columnLabelMap;
   private HashMap<Long, Value[]> patchedRows;
   private JdbcPreparedStatement preparedStatement;
   private final CommandInterface command;

   public JdbcResultSet(JdbcConnection var1, JdbcStatement var2, CommandInterface var3, ResultInterface var4, int var5, boolean var6, boolean var7, boolean var8) {
      this.setTrace(var1.getSession().getTrace(), 4, var5);
      this.conn = var1;
      this.stat = var2;
      this.command = var3;
      this.result = var4;
      this.columnCount = var4.getVisibleColumnCount();
      this.scrollable = var6;
      this.updatable = var7;
      this.triggerUpdatable = var8;
   }

   JdbcResultSet(JdbcConnection var1, JdbcPreparedStatement var2, CommandInterface var3, ResultInterface var4, int var5, boolean var6, boolean var7, HashMap<String, Integer> var8) {
      this(var1, var2, var3, var4, var5, var6, var7, false);
      this.columnLabelMap = var8;
      this.preparedStatement = var2;
   }

   public boolean next() throws SQLException {
      try {
         this.debugCodeCall("next");
         this.checkClosed();
         return this.nextRow();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      try {
         int var1 = getNextId(5);
         this.debugCodeAssign("ResultSetMetaData", 5, var1, "getMetaData()");
         this.checkClosed();
         String var2 = this.conn.getCatalog();
         return new JdbcResultSetMetaData(this, (JdbcPreparedStatement)null, this.result, var2, this.conn.getSession().getTrace(), var1);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean wasNull() throws SQLException {
      try {
         this.debugCodeCall("wasNull");
         this.checkClosed();
         return this.wasNull;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int findColumn(String var1) throws SQLException {
      try {
         this.debugCodeCall("findColumn", var1);
         return this.getColumnIndex(var1);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void close() throws SQLException {
      try {
         this.debugCodeCall("close");
         this.closeInternal(false);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   void closeInternal(boolean var1) {
      if (this.result != null) {
         boolean var6 = false;

         try {
            var6 = true;
            if (this.result.isLazy()) {
               this.stat.onLazyResultSetClose(this.command, this.preparedStatement == null);
            }

            this.result.close();
            var6 = false;
         } finally {
            if (var6) {
               JdbcStatement var4 = this.stat;
               this.columnCount = 0;
               this.result = null;
               this.stat = null;
               this.conn = null;
               this.insertRow = null;
               this.updateRow = null;
               if (!var1 && var4 != null) {
                  var4.closeIfCloseOnCompletion();
               }

            }
         }

         JdbcStatement var2 = this.stat;
         this.columnCount = 0;
         this.result = null;
         this.stat = null;
         this.conn = null;
         this.insertRow = null;
         this.updateRow = null;
         if (!var1 && var2 != null) {
            var2.closeIfCloseOnCompletion();
         }
      }

   }

   public Statement getStatement() throws SQLException {
      try {
         this.debugCodeCall("getStatement");
         this.checkClosed();
         return this.stat;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public SQLWarning getWarnings() throws SQLException {
      try {
         this.debugCodeCall("getWarnings");
         this.checkClosed();
         return null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void clearWarnings() throws SQLException {
      try {
         this.debugCodeCall("clearWarnings");
         this.checkClosed();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getString(int var1) throws SQLException {
      try {
         this.debugCodeCall("getString", (long)var1);
         return this.get(this.checkColumnIndex(var1)).getString();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getString(String var1) throws SQLException {
      try {
         this.debugCodeCall("getString", var1);
         return this.get(this.getColumnIndex(var1)).getString();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getInt(int var1) throws SQLException {
      try {
         this.debugCodeCall("getInt", (long)var1);
         return this.getIntInternal(this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getInt(String var1) throws SQLException {
      try {
         this.debugCodeCall("getInt", var1);
         return this.getIntInternal(this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private int getIntInternal(int var1) {
      Value var2 = this.getInternal(var1);
      int var3;
      if (var2 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var3 = var2.getInt();
      } else {
         this.wasNull = true;
         var3 = 0;
      }

      return var3;
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      try {
         this.debugCodeCall("getBigDecimal", (long)var1);
         return this.get(this.checkColumnIndex(var1)).getBigDecimal();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Date getDate(int var1) throws SQLException {
      try {
         this.debugCodeCall("getDate", (long)var1);
         return LegacyDateTimeUtils.toDate(this.conn, (TimeZone)null, this.get(this.checkColumnIndex(var1)));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Time getTime(int var1) throws SQLException {
      try {
         this.debugCodeCall("getTime", (long)var1);
         return LegacyDateTimeUtils.toTime(this.conn, (TimeZone)null, this.get(this.checkColumnIndex(var1)));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      try {
         this.debugCodeCall("getTimestamp", (long)var1);
         return LegacyDateTimeUtils.toTimestamp(this.conn, (TimeZone)null, this.get(this.checkColumnIndex(var1)));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      try {
         this.debugCodeCall("getBigDecimal", var1);
         return this.get(this.getColumnIndex(var1)).getBigDecimal();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Date getDate(String var1) throws SQLException {
      try {
         this.debugCodeCall("getDate", var1);
         return LegacyDateTimeUtils.toDate(this.conn, (TimeZone)null, this.get(this.getColumnIndex(var1)));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Time getTime(String var1) throws SQLException {
      try {
         this.debugCodeCall("getTime", var1);
         return LegacyDateTimeUtils.toTime(this.conn, (TimeZone)null, this.get(this.getColumnIndex(var1)));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      try {
         this.debugCodeCall("getTimestamp", var1);
         return LegacyDateTimeUtils.toTimestamp(this.conn, (TimeZone)null, this.get(this.getColumnIndex(var1)));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Object getObject(int var1) throws SQLException {
      try {
         this.debugCodeCall("getObject", (long)var1);
         return ValueToObjectConverter.valueToDefaultObject(this.get(this.checkColumnIndex(var1)), this.conn, true);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Object getObject(String var1) throws SQLException {
      try {
         this.debugCodeCall("getObject", var1);
         return ValueToObjectConverter.valueToDefaultObject(this.get(this.getColumnIndex(var1)), this.conn, true);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean getBoolean(int var1) throws SQLException {
      try {
         this.debugCodeCall("getBoolean", (long)var1);
         return this.getBooleanInternal(this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean getBoolean(String var1) throws SQLException {
      try {
         this.debugCodeCall("getBoolean", var1);
         return this.getBooleanInternal(this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private boolean getBooleanInternal(int var1) {
      Value var2 = this.getInternal(var1);
      boolean var3;
      if (var2 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var3 = var2.getBoolean();
      } else {
         this.wasNull = true;
         var3 = false;
      }

      return var3;
   }

   public byte getByte(int var1) throws SQLException {
      try {
         this.debugCodeCall("getByte", (long)var1);
         return this.getByteInternal(this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public byte getByte(String var1) throws SQLException {
      try {
         this.debugCodeCall("getByte", var1);
         return this.getByteInternal(this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private byte getByteInternal(int var1) {
      Value var2 = this.getInternal(var1);
      byte var3;
      if (var2 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var3 = var2.getByte();
      } else {
         this.wasNull = true;
         var3 = 0;
      }

      return var3;
   }

   public short getShort(int var1) throws SQLException {
      try {
         this.debugCodeCall("getShort", (long)var1);
         return this.getShortInternal(this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public short getShort(String var1) throws SQLException {
      try {
         this.debugCodeCall("getShort", var1);
         return this.getShortInternal(this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private short getShortInternal(int var1) {
      Value var2 = this.getInternal(var1);
      short var3;
      if (var2 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var3 = var2.getShort();
      } else {
         this.wasNull = true;
         var3 = 0;
      }

      return var3;
   }

   public long getLong(int var1) throws SQLException {
      try {
         this.debugCodeCall("getLong", (long)var1);
         return this.getLongInternal(this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public long getLong(String var1) throws SQLException {
      try {
         this.debugCodeCall("getLong", var1);
         return this.getLongInternal(this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private long getLongInternal(int var1) {
      Value var2 = this.getInternal(var1);
      long var3;
      if (var2 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var3 = var2.getLong();
      } else {
         this.wasNull = true;
         var3 = 0L;
      }

      return var3;
   }

   public float getFloat(int var1) throws SQLException {
      try {
         this.debugCodeCall("getFloat", (long)var1);
         return this.getFloatInternal(this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public float getFloat(String var1) throws SQLException {
      try {
         this.debugCodeCall("getFloat", var1);
         return this.getFloatInternal(this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private float getFloatInternal(int var1) {
      Value var2 = this.getInternal(var1);
      float var3;
      if (var2 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var3 = var2.getFloat();
      } else {
         this.wasNull = true;
         var3 = 0.0F;
      }

      return var3;
   }

   public double getDouble(int var1) throws SQLException {
      try {
         this.debugCodeCall("getDouble", (long)var1);
         return this.getDoubleInternal(this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public double getDouble(String var1) throws SQLException {
      try {
         this.debugCodeCall("getDouble", var1);
         return this.getDoubleInternal(this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private double getDoubleInternal(int var1) {
      Value var2 = this.getInternal(var1);
      double var3;
      if (var2 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var3 = var2.getDouble();
      } else {
         this.wasNull = true;
         var3 = 0.0;
      }

      return var3;
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getBigDecimal(" + quote(var1) + ", " + var2 + ')');
         }

         if (var2 < 0) {
            throw DbException.getInvalidValueException("scale", var2);
         } else {
            BigDecimal var3 = this.get(this.getColumnIndex(var1)).getBigDecimal();
            return var3 == null ? null : ValueNumeric.setScale(var3, var2);
         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getBigDecimal(" + var1 + ", " + var2 + ')');
         }

         if (var2 < 0) {
            throw DbException.getInvalidValueException("scale", var2);
         } else {
            BigDecimal var3 = this.get(this.checkColumnIndex(var1)).getBigDecimal();
            return var3 == null ? null : ValueNumeric.setScale(var3, var2);
         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int var1) throws SQLException {
      throw this.unsupported("unicodeStream");
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String var1) throws SQLException {
      throw this.unsupported("unicodeStream");
   }

   public Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException {
      throw this.unsupported("map");
   }

   public Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException {
      throw this.unsupported("map");
   }

   public Ref getRef(int var1) throws SQLException {
      throw this.unsupported("ref");
   }

   public Ref getRef(String var1) throws SQLException {
      throw this.unsupported("ref");
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getDate(" + var1 + ", calendar)");
         }

         return LegacyDateTimeUtils.toDate(this.conn, var2 != null ? var2.getTimeZone() : null, this.get(this.checkColumnIndex(var1)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getDate(" + quote(var1) + ", calendar)");
         }

         return LegacyDateTimeUtils.toDate(this.conn, var2 != null ? var2.getTimeZone() : null, this.get(this.getColumnIndex(var1)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getTime(" + var1 + ", calendar)");
         }

         return LegacyDateTimeUtils.toTime(this.conn, var2 != null ? var2.getTimeZone() : null, this.get(this.checkColumnIndex(var1)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getTime(" + quote(var1) + ", calendar)");
         }

         return LegacyDateTimeUtils.toTime(this.conn, var2 != null ? var2.getTimeZone() : null, this.get(this.getColumnIndex(var1)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getTimestamp(" + var1 + ", calendar)");
         }

         return LegacyDateTimeUtils.toTimestamp(this.conn, var2 != null ? var2.getTimeZone() : null, this.get(this.checkColumnIndex(var1)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getTimestamp(" + quote(var1) + ", calendar)");
         }

         return LegacyDateTimeUtils.toTimestamp(this.conn, var2 != null ? var2.getTimeZone() : null, this.get(this.getColumnIndex(var1)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public Blob getBlob(int var1) throws SQLException {
      try {
         int var2 = getNextId(9);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Blob", 9, var2, "getBlob(" + var1 + ')');
         }

         return this.getBlob(var2, this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Blob getBlob(String var1) throws SQLException {
      try {
         int var2 = getNextId(9);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Blob", 9, var2, "getBlob(" + quote(var1) + ')');
         }

         return this.getBlob(var2, this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private JdbcBlob getBlob(int var1, int var2) {
      Value var3 = this.getInternal(var2);
      JdbcBlob var4;
      if (var3 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var4 = new JdbcBlob(this.conn, var3, JdbcLob.State.WITH_VALUE, var1);
      } else {
         this.wasNull = true;
         var4 = null;
      }

      return var4;
   }

   public byte[] getBytes(int var1) throws SQLException {
      try {
         this.debugCodeCall("getBytes", (long)var1);
         return this.get(this.checkColumnIndex(var1)).getBytes();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public byte[] getBytes(String var1) throws SQLException {
      try {
         this.debugCodeCall("getBytes", var1);
         return this.get(this.getColumnIndex(var1)).getBytes();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public InputStream getBinaryStream(int var1) throws SQLException {
      try {
         this.debugCodeCall("getBinaryStream", (long)var1);
         return this.get(this.checkColumnIndex(var1)).getInputStream();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public InputStream getBinaryStream(String var1) throws SQLException {
      try {
         this.debugCodeCall("getBinaryStream", var1);
         return this.get(this.getColumnIndex(var1)).getInputStream();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Clob getClob(int var1) throws SQLException {
      try {
         int var2 = getNextId(10);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Clob", 10, var2, "getClob(" + var1 + ')');
         }

         return this.getClob(var2, this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Clob getClob(String var1) throws SQLException {
      try {
         int var2 = getNextId(10);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Clob", 10, var2, "getClob(" + quote(var1) + ')');
         }

         return this.getClob(var2, this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Array getArray(int var1) throws SQLException {
      try {
         int var2 = getNextId(16);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Array", 16, var2, "getArray(" + var1 + ')');
         }

         return this.getArray(var2, this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Array getArray(String var1) throws SQLException {
      try {
         int var2 = getNextId(16);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("Array", 16, var2, "getArray(" + quote(var1) + ')');
         }

         return this.getArray(var2, this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private Array getArray(int var1, int var2) {
      Value var3 = this.getInternal(var2);
      JdbcArray var4;
      if (var3 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var4 = new JdbcArray(this.conn, var3, var1);
      } else {
         this.wasNull = true;
         var4 = null;
      }

      return var4;
   }

   public InputStream getAsciiStream(int var1) throws SQLException {
      try {
         this.debugCodeCall("getAsciiStream", (long)var1);
         String var2 = this.get(this.checkColumnIndex(var1)).getString();
         return var2 == null ? null : IOUtils.getInputStreamFromString(var2);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public InputStream getAsciiStream(String var1) throws SQLException {
      try {
         this.debugCodeCall("getAsciiStream", var1);
         String var2 = this.get(this.getColumnIndex(var1)).getString();
         return IOUtils.getInputStreamFromString(var2);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      try {
         this.debugCodeCall("getCharacterStream", (long)var1);
         return this.get(this.checkColumnIndex(var1)).getReader();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      try {
         this.debugCodeCall("getCharacterStream", var1);
         return this.get(this.getColumnIndex(var1)).getReader();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public URL getURL(int var1) throws SQLException {
      throw this.unsupported("url");
   }

   public URL getURL(String var1) throws SQLException {
      throw this.unsupported("url");
   }

   public void updateNull(int var1) throws SQLException {
      try {
         this.debugCodeCall("updateNull", (long)var1);
         this.update(this.checkColumnIndex(var1), ValueNull.INSTANCE);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void updateNull(String var1) throws SQLException {
      try {
         this.debugCodeCall("updateNull", var1);
         this.update(this.getColumnIndex(var1), ValueNull.INSTANCE);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBoolean(" + var1 + ", " + var2 + ')');
         }

         this.update(this.checkColumnIndex(var1), ValueBoolean.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBoolean(" + quote(var1) + ", " + var2 + ')');
         }

         this.update(this.getColumnIndex(var1), ValueBoolean.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateByte(" + var1 + ", " + var2 + ')');
         }

         this.update(this.checkColumnIndex(var1), ValueTinyint.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateByte(" + quote(var1) + ", " + var2 + ')');
         }

         this.update(this.getColumnIndex(var1), ValueTinyint.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBytes(" + var1 + ", x)");
         }

         this.update(this.checkColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarbinary.get(var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBytes(" + quote(var1) + ", x)");
         }

         this.update(this.getColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarbinary.get(var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateShort(int var1, short var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateShort(" + var1 + ", (short) " + var2 + ')');
         }

         this.update(this.checkColumnIndex(var1), ValueSmallint.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateShort(String var1, short var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateShort(" + quote(var1) + ", (short) " + var2 + ')');
         }

         this.update(this.getColumnIndex(var1), ValueSmallint.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateInt(int var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateInt(" + var1 + ", " + var2 + ')');
         }

         this.update(this.checkColumnIndex(var1), ValueInteger.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateInt(String var1, int var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateInt(" + quote(var1) + ", " + var2 + ')');
         }

         this.update(this.getColumnIndex(var1), ValueInteger.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateLong(int var1, long var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateLong(" + var1 + ", " + var2 + "L)");
         }

         this.update(this.checkColumnIndex(var1), ValueBigint.get(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateLong(String var1, long var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateLong(" + quote(var1) + ", " + var2 + "L)");
         }

         this.update(this.getColumnIndex(var1), ValueBigint.get(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateFloat(" + var1 + ", " + var2 + "f)");
         }

         this.update(this.checkColumnIndex(var1), ValueReal.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateFloat(" + quote(var1) + ", " + var2 + "f)");
         }

         this.update(this.getColumnIndex(var1), ValueReal.get(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateDouble(" + var1 + ", " + var2 + "d)");
         }

         this.update(this.checkColumnIndex(var1), ValueDouble.get(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateDouble(" + quote(var1) + ", " + var2 + "d)");
         }

         this.update(this.getColumnIndex(var1), ValueDouble.get(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBigDecimal(" + var1 + ", " + quoteBigDecimal(var2) + ')');
         }

         this.update(this.checkColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBigDecimal(" + quote(var1) + ", " + quoteBigDecimal(var2) + ')');
         }

         this.update(this.getColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateString(int var1, String var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateString(" + var1 + ", " + quote(var2) + ')');
         }

         this.update(this.checkColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarchar.get(var2, this.conn)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateString(String var1, String var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateString(" + quote(var1) + ", " + quote(var2) + ')');
         }

         this.update(this.getColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarchar.get(var2, this.conn)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateDate(" + var1 + ", " + quoteDate(var2) + ')');
         }

         this.update(this.checkColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateDate(" + quote(var1) + ", " + quoteDate(var2) + ')');
         }

         this.update(this.getColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateTime(" + var1 + ", " + quoteTime(var2) + ')');
         }

         this.update(this.checkColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateTime(" + quote(var1) + ", " + quoteTime(var2) + ')');
         }

         this.update(this.getColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateTimestamp(" + var1 + ", " + quoteTimestamp(var2) + ')');
         }

         this.update(this.checkColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateTimestamp(" + quote(var1) + ", " + quoteTimestamp(var2) + ')');
         }

         this.update(this.getColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(this.conn, (TimeZone)null, var2)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateAsciiStream(" + var1 + ", x, " + var3 + ')');
         }

         this.updateAscii(this.checkColumnIndex(var1), var2, (long)var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateAsciiStream(int var1, InputStream var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateAsciiStream(" + var1 + ", x)");
         }

         this.updateAscii(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateAsciiStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.updateAscii(this.checkColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateAsciiStream(" + quote(var1) + ", x, " + var3 + ')');
         }

         this.updateAscii(this.getColumnIndex(var1), var2, (long)var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateAsciiStream(String var1, InputStream var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateAsciiStream(" + quote(var1) + ", x)");
         }

         this.updateAscii(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateAsciiStream(String var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateAsciiStream(" + quote(var1) + ", x, " + var3 + "L)");
         }

         this.updateAscii(this.getColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   private void updateAscii(int var1, InputStream var2, long var3) {
      this.update(var1, this.conn.createClob(IOUtils.getAsciiReader(var2), var3));
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBinaryStream(" + var1 + ", x, " + var3 + ')');
         }

         this.updateBlobImpl(this.checkColumnIndex(var1), var2, (long)var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateBinaryStream(int var1, InputStream var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBinaryStream(" + var1 + ", x)");
         }

         this.updateBlobImpl(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBinaryStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.updateBlobImpl(this.checkColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateBinaryStream(String var1, InputStream var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBinaryStream(" + quote(var1) + ", x)");
         }

         this.updateBlobImpl(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBinaryStream(" + quote(var1) + ", x, " + var3 + ')');
         }

         this.updateBlobImpl(this.getColumnIndex(var1), var2, (long)var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateBinaryStream(String var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBinaryStream(" + quote(var1) + ", x, " + var3 + "L)");
         }

         this.updateBlobImpl(this.getColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateCharacterStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateCharacterStream(" + var1 + ", x, " + var3 + ')');
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, (long)var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateCharacterStream(" + var1 + ", x)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateCharacterStream(" + quote(var1) + ", x, " + var3 + ')');
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, (long)var3);
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateCharacterStream(String var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateCharacterStream(" + quote(var1) + ", x)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateCharacterStream(" + quote(var1) + ", x, " + var3 + "L)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + var1 + ", x, " + var3 + ')');
         }

         this.update(this.checkColumnIndex(var1), this.convertToUnknownValue(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + quote(var1) + ", x, " + var3 + ')');
         }

         this.update(this.getColumnIndex(var1), this.convertToUnknownValue(var2));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + var1 + ", x)");
         }

         this.update(this.checkColumnIndex(var1), this.convertToUnknownValue(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + quote(var1) + ", x)");
         }

         this.update(this.getColumnIndex(var1), this.convertToUnknownValue(var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateObject(int var1, Object var2, SQLType var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + var1 + ", x, " + DataType.sqlTypeToString(var3) + ')');
         }

         this.update(this.checkColumnIndex(var1), this.convertToValue(var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateObject(int var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + var1 + ", x, " + DataType.sqlTypeToString(var3) + ", " + var4 + ')');
         }

         this.update(this.checkColumnIndex(var1), this.convertToValue(var2, var3));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateObject(String var1, Object var2, SQLType var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + quote(var1) + ", x, " + DataType.sqlTypeToString(var3) + ')');
         }

         this.update(this.getColumnIndex(var1), this.convertToValue(var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public void updateObject(String var1, Object var2, SQLType var3, int var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateObject(" + quote(var1) + ", x, " + DataType.sqlTypeToString(var3) + ", " + var4 + ')');
         }

         this.update(this.getColumnIndex(var1), this.convertToValue(var2, var3));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateRef(int var1, Ref var2) throws SQLException {
      throw this.unsupported("ref");
   }

   public void updateRef(String var1, Ref var2) throws SQLException {
      throw this.unsupported("ref");
   }

   public void updateBlob(int var1, InputStream var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBlob(" + var1 + ", (InputStream) x)");
         }

         this.updateBlobImpl(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBlob(int var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBlob(" + var1 + ", (InputStream) x, " + var3 + "L)");
         }

         this.updateBlobImpl(this.checkColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateBlob(int var1, Blob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBlob(" + var1 + ", (Blob) x)");
         }

         this.updateBlobImpl(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBlob(String var1, Blob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBlob(" + quote(var1) + ", (Blob) x)");
         }

         this.updateBlobImpl(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   private void updateBlobImpl(int var1, Blob var2, long var3) throws SQLException {
      this.update(var1, (Value)(var2 == null ? ValueNull.INSTANCE : this.conn.createBlob(var2.getBinaryStream(), var3)));
   }

   public void updateBlob(String var1, InputStream var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBlob(" + quote(var1) + ", (InputStream) x)");
         }

         this.updateBlobImpl(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateBlob(String var1, InputStream var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateBlob(" + quote(var1) + ", (InputStream) x, " + var3 + "L)");
         }

         this.updateBlobImpl(this.getColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   private void updateBlobImpl(int var1, InputStream var2, long var3) {
      this.update(var1, this.conn.createBlob(var2, var3));
   }

   public void updateClob(int var1, Clob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateClob(" + var1 + ", (Clob) x)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateClob(int var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateClob(" + var1 + ", (Reader) x)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateClob(" + var1 + ", (Reader) x, " + var3 + "L)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateClob(String var1, Clob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateClob(" + quote(var1) + ", (Clob) x)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateClob(String var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateClob(" + quote(var1) + ", (Reader) x)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateClob(String var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateClob(" + quote(var1) + ", (Reader) x, " + var3 + "L)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateArray(int var1, Array var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateArray(" + var1 + ", x)");
         }

         this.updateArrayImpl(this.checkColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateArray(String var1, Array var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateArray(" + quote(var1) + ", x)");
         }

         this.updateArrayImpl(this.getColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   private void updateArrayImpl(int var1, Array var2) throws SQLException {
      this.update(var1, (Value)(var2 == null ? ValueNull.INSTANCE : ValueToObjectConverter.objectToValue(this.stat.session, var2.getArray(), 40)));
   }

   public String getCursorName() throws SQLException {
      throw this.unsupported("cursorName");
   }

   public int getRow() throws SQLException {
      try {
         this.debugCodeCall("getRow");
         this.checkClosed();
         if (this.result.isAfterLast()) {
            return 0;
         } else {
            long var1 = this.result.getRowId() + 1L;
            return var1 <= 2147483647L ? (int)var1 : -2;
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public int getConcurrency() throws SQLException {
      try {
         this.debugCodeCall("getConcurrency");
         this.checkClosed();
         if (!this.updatable) {
            return 1007;
         } else {
            UpdatableRow var1 = new UpdatableRow(this.conn, this.result);
            return var1.isUpdatable() ? 1008 : 1007;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getFetchDirection() throws SQLException {
      try {
         this.debugCodeCall("getFetchDirection");
         this.checkClosed();
         return 1000;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getFetchSize() throws SQLException {
      try {
         this.debugCodeCall("getFetchSize");
         this.checkClosed();
         return this.result.getFetchSize();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void setFetchSize(int var1) throws SQLException {
      try {
         this.debugCodeCall("setFetchSize", (long)var1);
         this.checkClosed();
         if (var1 < 0) {
            throw DbException.getInvalidValueException("rows", var1);
         } else {
            if (var1 > 0) {
               if (this.stat != null) {
                  int var2 = this.stat.getMaxRows();
                  if (var2 > 0 && var1 > var2) {
                     throw DbException.getInvalidValueException("rows", var1);
                  }
               }
            } else {
               var1 = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
            }

            this.result.setFetchSize(var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void setFetchDirection(int var1) throws SQLException {
      this.debugCodeCall("setFetchDirection", (long)var1);
      if (var1 != 1000) {
         throw this.unsupported("setFetchDirection");
      }
   }

   public int getType() throws SQLException {
      try {
         this.debugCodeCall("getType");
         this.checkClosed();
         return this.stat == null ? 1003 : this.stat.resultSetType;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isBeforeFirst() throws SQLException {
      try {
         this.debugCodeCall("isBeforeFirst");
         this.checkClosed();
         return this.result.getRowId() < 0L && this.result.hasNext();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isAfterLast() throws SQLException {
      try {
         this.debugCodeCall("isAfterLast");
         this.checkClosed();
         return this.result.getRowId() > 0L && this.result.isAfterLast();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isFirst() throws SQLException {
      try {
         this.debugCodeCall("isFirst");
         this.checkClosed();
         return this.result.getRowId() == 0L && !this.result.isAfterLast();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isLast() throws SQLException {
      try {
         this.debugCodeCall("isLast");
         this.checkClosed();
         long var1 = this.result.getRowId();
         return var1 >= 0L && !this.result.isAfterLast() && !this.result.hasNext();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void beforeFirst() throws SQLException {
      try {
         this.debugCodeCall("beforeFirst");
         this.checkClosed();
         if (this.result.getRowId() >= 0L) {
            this.resetResult();
         }

      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void afterLast() throws SQLException {
      try {
         this.debugCodeCall("afterLast");
         this.checkClosed();

         while(this.nextRow()) {
         }

      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean first() throws SQLException {
      try {
         this.debugCodeCall("first");
         this.checkClosed();
         if (this.result.getRowId() >= 0L) {
            this.resetResult();
         }

         return this.nextRow();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean last() throws SQLException {
      try {
         this.debugCodeCall("last");
         this.checkClosed();
         if (this.result.isAfterLast()) {
            this.resetResult();
         }

         while(this.result.hasNext()) {
            this.nextRow();
         }

         return this.isOnValidRow();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean absolute(int var1) throws SQLException {
      try {
         this.debugCodeCall("absolute", (long)var1);
         this.checkClosed();
         long var2 = var1 >= 0 ? (long)var1 : this.result.getRowCount() + (long)var1 + 1L;
         if (--var2 < this.result.getRowId()) {
            this.resetResult();
         }

         do {
            if (this.result.getRowId() >= var2) {
               return this.isOnValidRow();
            }
         } while(this.nextRow());

         return false;
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public boolean relative(int var1) throws SQLException {
      try {
         this.debugCodeCall("relative", (long)var1);
         this.checkClosed();
         long var2;
         if (var1 < 0) {
            var2 = this.result.getRowId() + (long)var1 + 1L;
            this.resetResult();
         } else {
            var2 = (long)var1;
         }

         do {
            if (var2-- <= 0L) {
               return this.isOnValidRow();
            }
         } while(this.nextRow());

         return false;
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public boolean previous() throws SQLException {
      try {
         this.debugCodeCall("previous");
         this.checkClosed();
         return this.relative(-1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void moveToInsertRow() throws SQLException {
      try {
         this.debugCodeCall("moveToInsertRow");
         this.checkUpdatable();
         this.insertRow = new Value[this.columnCount];
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void moveToCurrentRow() throws SQLException {
      try {
         this.debugCodeCall("moveToCurrentRow");
         this.checkUpdatable();
         this.insertRow = null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean rowUpdated() throws SQLException {
      try {
         this.debugCodeCall("rowUpdated");
         return false;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean rowInserted() throws SQLException {
      try {
         this.debugCodeCall("rowInserted");
         return false;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean rowDeleted() throws SQLException {
      try {
         this.debugCodeCall("rowDeleted");
         return false;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void insertRow() throws SQLException {
      try {
         this.debugCodeCall("insertRow");
         this.checkUpdatable();
         if (this.insertRow == null) {
            throw DbException.get(90029);
         } else {
            this.getUpdatableRow().insertRow(this.insertRow);
            this.insertRow = null;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void updateRow() throws SQLException {
      try {
         this.debugCodeCall("updateRow");
         this.checkUpdatable();
         if (this.insertRow != null) {
            throw DbException.get(90029);
         } else {
            this.checkOnValidRow();
            if (this.updateRow != null) {
               UpdatableRow var1 = this.getUpdatableRow();
               Value[] var2 = new Value[this.columnCount];

               int var3;
               for(var3 = 0; var3 < this.updateRow.length; ++var3) {
                  var2[var3] = this.getInternal(this.checkColumnIndex(var3 + 1));
               }

               var1.updateRow(var2, this.updateRow);

               for(var3 = 0; var3 < this.updateRow.length; ++var3) {
                  if (this.updateRow[var3] == null) {
                     this.updateRow[var3] = var2[var3];
                  }
               }

               Value[] var5 = var1.readRow(this.updateRow);
               this.patchCurrentRow(var5);
               this.updateRow = null;
            }

         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void deleteRow() throws SQLException {
      try {
         this.debugCodeCall("deleteRow");
         this.checkUpdatable();
         if (this.insertRow != null) {
            throw DbException.get(90029);
         } else {
            this.checkOnValidRow();
            this.getUpdatableRow().deleteRow(this.result.currentRow());
            this.updateRow = null;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void refreshRow() throws SQLException {
      try {
         this.debugCodeCall("refreshRow");
         this.checkClosed();
         if (this.insertRow != null) {
            throw DbException.get(2000);
         } else {
            this.checkOnValidRow();
            this.patchCurrentRow(this.getUpdatableRow().readRow(this.result.currentRow()));
            this.updateRow = null;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void cancelRowUpdates() throws SQLException {
      try {
         this.debugCodeCall("cancelRowUpdates");
         this.checkClosed();
         if (this.insertRow != null) {
            throw DbException.get(2000);
         } else {
            this.updateRow = null;
         }
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   private UpdatableRow getUpdatableRow() throws SQLException {
      UpdatableRow var1 = new UpdatableRow(this.conn, this.result);
      if (!var1.isUpdatable()) {
         throw DbException.get(90127);
      } else {
         return var1;
      }
   }

   private int getColumnIndex(String var1) {
      this.checkClosed();
      if (var1 == null) {
         throw DbException.getInvalidValueException("columnLabel", (Object)null);
      } else {
         String var4;
         int var8;
         if (this.columnCount >= 3) {
            if (this.columnLabelMap == null) {
               HashMap var6 = new HashMap();

               for(var8 = 0; var8 < this.columnCount; ++var8) {
                  var4 = StringUtils.toUpperEnglish(this.result.getAlias(var8));
                  var6.putIfAbsent(var4, var8);
               }

               for(var8 = 0; var8 < this.columnCount; ++var8) {
                  var4 = this.result.getColumnName(var8);
                  if (var4 != null) {
                     var4 = StringUtils.toUpperEnglish(var4);
                     var6.putIfAbsent(var4, var8);
                     String var9 = this.result.getTableName(var8);
                     if (var9 != null) {
                        var4 = StringUtils.toUpperEnglish(var9) + '.' + var4;
                        var6.putIfAbsent(var4, var8);
                     }
                  }
               }

               this.columnLabelMap = var6;
               if (this.preparedStatement != null) {
                  this.preparedStatement.setCachedColumnLabelMap(this.columnLabelMap);
               }
            }

            Integer var7 = (Integer)this.columnLabelMap.get(StringUtils.toUpperEnglish(var1));
            if (var7 == null) {
               throw DbException.get(42122, (String)var1);
            } else {
               return var7 + 1;
            }
         } else {
            int var2;
            for(var2 = 0; var2 < this.columnCount; ++var2) {
               if (var1.equalsIgnoreCase(this.result.getAlias(var2))) {
                  return var2 + 1;
               }
            }

            var2 = var1.indexOf(46);
            if (var2 > 0) {
               String var3 = var1.substring(0, var2);
               var4 = var1.substring(var2 + 1);

               for(int var5 = 0; var5 < this.columnCount; ++var5) {
                  if (var3.equalsIgnoreCase(this.result.getTableName(var5)) && var4.equalsIgnoreCase(this.result.getColumnName(var5))) {
                     return var5 + 1;
                  }
               }
            } else {
               for(var8 = 0; var8 < this.columnCount; ++var8) {
                  if (var1.equalsIgnoreCase(this.result.getColumnName(var8))) {
                     return var8 + 1;
                  }
               }
            }

            throw DbException.get(42122, (String)var1);
         }
      }
   }

   private int checkColumnIndex(int var1) {
      this.checkClosed();
      if (var1 >= 1 && var1 <= this.columnCount) {
         return var1;
      } else {
         throw DbException.getInvalidValueException("columnIndex", var1);
      }
   }

   void checkClosed() {
      if (this.result == null) {
         throw DbException.get(90007);
      } else {
         if (this.stat != null) {
            this.stat.checkClosed();
         }

         if (this.conn != null) {
            this.conn.checkClosed();
         }

      }
   }

   private boolean isOnValidRow() {
      return this.result.getRowId() >= 0L && !this.result.isAfterLast();
   }

   private void checkOnValidRow() {
      if (!this.isOnValidRow()) {
         throw DbException.get(2000);
      }
   }

   private Value get(int var1) {
      Value var2 = this.getInternal(var1);
      this.wasNull = var2 == ValueNull.INSTANCE;
      return var2;
   }

   public Value getInternal(int var1) {
      this.checkOnValidRow();
      Value[] var2;
      if (this.patchedRows == null || (var2 = (Value[])this.patchedRows.get(this.result.getRowId())) == null) {
         var2 = this.result.currentRow();
      }

      return var2[var1 - 1];
   }

   private void update(int var1, Value var2) {
      if (!this.triggerUpdatable) {
         this.checkUpdatable();
      }

      if (this.insertRow != null) {
         this.insertRow[var1 - 1] = var2;
      } else {
         if (this.updateRow == null) {
            this.updateRow = new Value[this.columnCount];
         }

         this.updateRow[var1 - 1] = var2;
      }

   }

   private boolean nextRow() {
      boolean var1 = this.result.isLazy() ? this.nextLazyRow() : this.result.next();
      if (!var1 && !this.scrollable) {
         this.result.close();
      }

      return var1;
   }

   private boolean nextLazyRow() {
      Session var1;
      if (!this.stat.isCancelled() && this.conn != null && (var1 = this.conn.getSession()) != null) {
         Session var2 = var1.setThreadLocalSession();

         boolean var3;
         try {
            var3 = this.result.next();
         } finally {
            var1.resetThreadLocalSession(var2);
         }

         return var3;
      } else {
         throw DbException.get(57014);
      }
   }

   private void resetResult() {
      if (!this.scrollable) {
         throw DbException.get(90128);
      } else {
         this.result.reset();
      }
   }

   public RowId getRowId(int var1) throws SQLException {
      throw this.unsupported("rowId");
   }

   public RowId getRowId(String var1) throws SQLException {
      throw this.unsupported("rowId");
   }

   public void updateRowId(int var1, RowId var2) throws SQLException {
      throw this.unsupported("rowId");
   }

   public void updateRowId(String var1, RowId var2) throws SQLException {
      throw this.unsupported("rowId");
   }

   public int getHoldability() throws SQLException {
      try {
         this.debugCodeCall("getHoldability");
         this.checkClosed();
         return this.conn.getHoldability();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isClosed() throws SQLException {
      try {
         this.debugCodeCall("isClosed");
         return this.result == null;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public void updateNString(int var1, String var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNString(" + var1 + ", " + quote(var2) + ')');
         }

         this.update(this.checkColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarchar.get(var2, this.conn)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateNString(String var1, String var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNString(" + quote(var1) + ", " + quote(var2) + ')');
         }

         this.update(this.getColumnIndex(var1), (Value)(var2 == null ? ValueNull.INSTANCE : ValueVarchar.get(var2, this.conn)));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateNClob(int var1, NClob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNClob(" + var1 + ", (NClob) x)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateNClob(int var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNClob(" + var1 + ", (Reader) x)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateNClob(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNClob(" + var1 + ", (Reader) x, " + var3 + "L)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateNClob(String var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNClob(" + quote(var1) + ", (Reader) x)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateNClob(String var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNClob(" + quote(var1) + ", (Reader) x, " + var3 + "L)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateNClob(String var1, NClob var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNClob(" + quote(var1) + ", (NClob) x)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   private void updateClobImpl(int var1, Clob var2) throws SQLException {
      this.update(var1, (Value)(var2 == null ? ValueNull.INSTANCE : this.conn.createClob(var2.getCharacterStream(), -1L)));
   }

   public NClob getNClob(int var1) throws SQLException {
      try {
         int var2 = getNextId(10);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("NClob", 10, var2, "getNClob(" + var1 + ')');
         }

         return this.getClob(var2, this.checkColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public NClob getNClob(String var1) throws SQLException {
      try {
         int var2 = getNextId(10);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("NClob", 10, var2, "getNClob(" + quote(var1) + ')');
         }

         return this.getClob(var2, this.getColumnIndex(var1));
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   private JdbcClob getClob(int var1, int var2) {
      Value var3 = this.getInternal(var2);
      JdbcClob var4;
      if (var3 != ValueNull.INSTANCE) {
         this.wasNull = false;
         var4 = new JdbcClob(this.conn, var3, JdbcLob.State.WITH_VALUE, var1);
      } else {
         this.wasNull = true;
         var4 = null;
      }

      return var4;
   }

   public SQLXML getSQLXML(int var1) throws SQLException {
      try {
         int var2 = getNextId(17);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("SQLXML", 17, var2, "getSQLXML(" + var1 + ')');
         }

         Value var3 = this.get(this.checkColumnIndex(var1));
         return var3 == ValueNull.INSTANCE ? null : new JdbcSQLXML(this.conn, var3, JdbcLob.State.WITH_VALUE, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public SQLXML getSQLXML(String var1) throws SQLException {
      try {
         int var2 = getNextId(17);
         if (this.isDebugEnabled()) {
            this.debugCodeAssign("SQLXML", 17, var2, "getSQLXML(" + quote(var1) + ')');
         }

         Value var3 = this.get(this.getColumnIndex(var1));
         return var3 == ValueNull.INSTANCE ? null : new JdbcSQLXML(this.conn, var3, JdbcLob.State.WITH_VALUE, var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateSQLXML(int var1, SQLXML var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateSQLXML(" + var1 + ", x)");
         }

         this.updateSQLXMLImpl(this.checkColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateSQLXML(String var1, SQLXML var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateSQLXML(" + quote(var1) + ", x)");
         }

         this.updateSQLXMLImpl(this.getColumnIndex(var1), var2);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   private void updateSQLXMLImpl(int var1, SQLXML var2) throws SQLException {
      this.update(var1, (Value)(var2 == null ? ValueNull.INSTANCE : this.conn.createClob(var2.getCharacterStream(), -1L)));
   }

   public String getNString(int var1) throws SQLException {
      try {
         this.debugCodeCall("getNString", (long)var1);
         return this.get(this.checkColumnIndex(var1)).getString();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public String getNString(String var1) throws SQLException {
      try {
         this.debugCodeCall("getNString", var1);
         return this.get(this.getColumnIndex(var1)).getString();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Reader getNCharacterStream(int var1) throws SQLException {
      try {
         this.debugCodeCall("getNCharacterStream", (long)var1);
         return this.get(this.checkColumnIndex(var1)).getReader();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public Reader getNCharacterStream(String var1) throws SQLException {
      try {
         this.debugCodeCall("getNCharacterStream", var1);
         return this.get(this.getColumnIndex(var1)).getReader();
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void updateNCharacterStream(int var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNCharacterStream(" + var1 + ", x)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNCharacterStream(" + var1 + ", x, " + var3 + "L)");
         }

         this.updateClobImpl(this.checkColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public void updateNCharacterStream(String var1, Reader var2) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNCharacterStream(" + quote(var1) + ", x)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, -1L);
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public void updateNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("updateNCharacterStream(" + quote(var1) + ", x, " + var3 + "L)");
         }

         this.updateClobImpl(this.getColumnIndex(var1), var2, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   private void updateClobImpl(int var1, Reader var2, long var3) {
      this.update(var1, this.conn.createClob(var2, var3));
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      try {
         if (this.isWrapperFor(var1)) {
            return this;
         } else {
            throw DbException.getInvalidValueException("iface", var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1 != null && var1.isAssignableFrom(this.getClass());
   }

   public <T> T getObject(int var1, Class<T> var2) throws SQLException {
      try {
         if (var2 == null) {
            throw DbException.getInvalidValueException("type", var2);
         } else {
            this.debugCodeCall("getObject", (long)var1);
            return ValueToObjectConverter.valueToObject(var2, this.get(this.checkColumnIndex(var1)), this.conn);
         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public <T> T getObject(String var1, Class<T> var2) throws SQLException {
      try {
         if (var2 == null) {
            throw DbException.getInvalidValueException("type", var2);
         } else {
            this.debugCodeCall("getObject", var1);
            return ValueToObjectConverter.valueToObject(var2, this.get(this.getColumnIndex(var1)), this.conn);
         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public String toString() {
      return this.getTraceObjectName() + ": " + this.result;
   }

   private void patchCurrentRow(Value[] var1) {
      boolean var2 = false;
      Value[] var3 = this.result.currentRow();
      CompareMode var4 = this.conn.getCompareMode();

      for(int var5 = 0; var5 < var1.length; ++var5) {
         if (var1[var5].compareTo(var3[var5], this.conn, var4) != 0) {
            var2 = true;
            break;
         }
      }

      if (this.patchedRows == null) {
         this.patchedRows = new HashMap();
      }

      Long var6 = this.result.getRowId();
      if (!var2) {
         this.patchedRows.remove(var6);
      } else {
         this.patchedRows.put(var6, var1);
      }

   }

   private Value convertToValue(Object var1, SQLType var2) {
      if (var1 == null) {
         return ValueNull.INSTANCE;
      } else {
         int var3 = DataType.convertSQLTypeToValueType(var2);
         Value var4 = ValueToObjectConverter.objectToValue(this.conn.getSession(), var1, var3);
         return var4.convertTo(var3, this.conn);
      }
   }

   private Value convertToUnknownValue(Object var1) {
      return ValueToObjectConverter.objectToValue(this.conn.getSession(), var1, -1);
   }

   private void checkUpdatable() {
      this.checkClosed();
      if (!this.updatable) {
         throw DbException.get(90140);
      }
   }

   public Value[] getUpdateRow() {
      return this.updateRow;
   }

   public ResultInterface getResult() {
      return this.result;
   }
}
