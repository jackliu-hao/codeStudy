package org.h2.tools;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import org.h2.api.JavaObjectSerializer;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.SimpleColumnInfo;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueToObjectConverter;

public class SimpleResultSet implements ResultSet, ResultSetMetaData {
   private ArrayList<Object[]> rows;
   private Object[] currentRow;
   private int rowId = -1;
   private boolean wasNull;
   private SimpleRowSource source;
   private ArrayList<SimpleColumnInfo> columns = Utils.newSmallArrayList();
   private boolean autoClose = true;

   public SimpleResultSet() {
      this.rows = Utils.newSmallArrayList();
   }

   public SimpleResultSet(SimpleRowSource var1) {
      this.source = var1;
   }

   public void addColumn(String var1, int var2, int var3, int var4) {
      int var5 = DataType.convertSQLTypeToValueType(var2);
      this.addColumn(var1, var2, Value.getTypeName(var5), var3, var4);
   }

   public void addColumn(String var1, int var2, String var3, int var4, int var5) {
      if (this.rows != null && !this.rows.isEmpty()) {
         throw new IllegalStateException("Cannot add a column after adding rows");
      } else {
         if (var1 == null) {
            var1 = "C" + (this.columns.size() + 1);
         }

         this.columns.add(new SimpleColumnInfo(var1, var2, var3, var4, var5));
      }
   }

   public void addRow(Object... var1) {
      if (this.rows == null) {
         throw new IllegalStateException("Cannot add a row when using RowSource");
      } else {
         this.rows.add(var1);
      }
   }

   public int getConcurrency() {
      return 1007;
   }

   public int getFetchDirection() {
      return 1000;
   }

   public int getFetchSize() {
      return 0;
   }

   public int getRow() {
      return this.currentRow == null ? 0 : this.rowId + 1;
   }

   public int getType() {
      return this.autoClose ? 1003 : 1004;
   }

   public void close() {
      this.currentRow = null;
      this.rows = null;
      this.columns = null;
      this.rowId = -1;
      if (this.source != null) {
         this.source.close();
         this.source = null;
      }

   }

   public boolean next() throws SQLException {
      if (this.source != null) {
         ++this.rowId;
         this.currentRow = this.source.readRow();
         if (this.currentRow != null) {
            return true;
         }
      } else if (this.rows != null && this.rowId < this.rows.size()) {
         ++this.rowId;
         if (this.rowId < this.rows.size()) {
            this.currentRow = (Object[])this.rows.get(this.rowId);
            return true;
         }

         this.currentRow = null;
      }

      if (this.autoClose) {
         this.close();
      }

      return false;
   }

   public void beforeFirst() throws SQLException {
      if (this.autoClose) {
         throw DbException.getJdbcSQLException(90128);
      } else {
         this.rowId = -1;
         if (this.source != null) {
            this.source.reset();
         }

      }
   }

   public boolean wasNull() {
      return this.wasNull;
   }

   public int findColumn(String var1) throws SQLException {
      if (var1 != null && this.columns != null) {
         int var2 = 0;

         for(int var3 = this.columns.size(); var2 < var3; ++var2) {
            if (var1.equalsIgnoreCase(this.getColumn(var2).name)) {
               return var2 + 1;
            }
         }
      }

      throw DbException.getJdbcSQLException(42122, var1);
   }

   public ResultSetMetaData getMetaData() {
      return this;
   }

   public SQLWarning getWarnings() {
      return null;
   }

   public Statement getStatement() {
      return null;
   }

   public void clearWarnings() {
   }

   public Array getArray(int var1) throws SQLException {
      Object[] var2 = (Object[])((Object[])this.get(var1));
      return var2 == null ? null : new SimpleArray(var2);
   }

   public Array getArray(String var1) throws SQLException {
      return this.getArray(this.findColumn(var1));
   }

   public InputStream getAsciiStream(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public InputStream getAsciiStream(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public BigDecimal getBigDecimal(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof BigDecimal)) {
         var2 = new BigDecimal(var2.toString());
      }

      return (BigDecimal)var2;
   }

   public BigDecimal getBigDecimal(String var1) throws SQLException {
      return this.getBigDecimal(this.findColumn(var1));
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int var1, int var2) throws SQLException {
      throw getUnsupportedException();
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String var1, int var2) throws SQLException {
      throw getUnsupportedException();
   }

   public InputStream getBinaryStream(int var1) throws SQLException {
      return asInputStream(this.get(var1));
   }

   private static InputStream asInputStream(Object var0) throws SQLException {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof Blob ? ((Blob)var0).getBinaryStream() : (InputStream)var0;
      }
   }

   public InputStream getBinaryStream(String var1) throws SQLException {
      return this.getBinaryStream(this.findColumn(var1));
   }

   public Blob getBlob(int var1) throws SQLException {
      return (Blob)this.get(var1);
   }

   public Blob getBlob(String var1) throws SQLException {
      return this.getBlob(this.findColumn(var1));
   }

   public boolean getBoolean(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 == null) {
         return false;
      } else if (var2 instanceof Boolean) {
         return (Boolean)var2;
      } else if (var2 instanceof Number) {
         Number var3 = (Number)var2;
         if (!(var3 instanceof Double) && !(var3 instanceof Float)) {
            if (var3 instanceof BigDecimal) {
               return ((BigDecimal)var3).signum() != 0;
            } else if (var3 instanceof BigInteger) {
               return ((BigInteger)var3).signum() != 0;
            } else {
               return var3.longValue() != 0L;
            }
         } else {
            return var3.doubleValue() != 0.0;
         }
      } else {
         return Utils.parseBoolean(var2.toString(), false, true);
      }
   }

   public boolean getBoolean(String var1) throws SQLException {
      return this.getBoolean(this.findColumn(var1));
   }

   public byte getByte(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof Number)) {
         var2 = Byte.decode(var2.toString());
      }

      return var2 == null ? 0 : ((Number)var2).byteValue();
   }

   public byte getByte(String var1) throws SQLException {
      return this.getByte(this.findColumn(var1));
   }

   public byte[] getBytes(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof byte[])) {
         return var2 instanceof UUID ? Bits.uuidToBytes((UUID)var2) : JdbcUtils.serialize(var2, (JavaObjectSerializer)null);
      } else {
         return (byte[])((byte[])var2);
      }
   }

   public byte[] getBytes(String var1) throws SQLException {
      return this.getBytes(this.findColumn(var1));
   }

   public Reader getCharacterStream(int var1) throws SQLException {
      return asReader(this.get(var1));
   }

   private static Reader asReader(Object var0) throws SQLException {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof Clob ? ((Clob)var0).getCharacterStream() : (Reader)var0;
      }
   }

   public Reader getCharacterStream(String var1) throws SQLException {
      return this.getCharacterStream(this.findColumn(var1));
   }

   public Clob getClob(int var1) throws SQLException {
      return (Clob)this.get(var1);
   }

   public Clob getClob(String var1) throws SQLException {
      return this.getClob(this.findColumn(var1));
   }

   public Date getDate(int var1) throws SQLException {
      return (Date)this.get(var1);
   }

   public Date getDate(String var1) throws SQLException {
      return this.getDate(this.findColumn(var1));
   }

   public Date getDate(int var1, Calendar var2) throws SQLException {
      throw getUnsupportedException();
   }

   public Date getDate(String var1, Calendar var2) throws SQLException {
      throw getUnsupportedException();
   }

   public double getDouble(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof Number)) {
         return Double.parseDouble(var2.toString());
      } else {
         return var2 == null ? 0.0 : ((Number)var2).doubleValue();
      }
   }

   public double getDouble(String var1) throws SQLException {
      return this.getDouble(this.findColumn(var1));
   }

   public float getFloat(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof Number)) {
         return Float.parseFloat(var2.toString());
      } else {
         return var2 == null ? 0.0F : ((Number)var2).floatValue();
      }
   }

   public float getFloat(String var1) throws SQLException {
      return this.getFloat(this.findColumn(var1));
   }

   public int getInt(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof Number)) {
         var2 = Integer.decode(var2.toString());
      }

      return var2 == null ? 0 : ((Number)var2).intValue();
   }

   public int getInt(String var1) throws SQLException {
      return this.getInt(this.findColumn(var1));
   }

   public long getLong(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof Number)) {
         var2 = Long.decode(var2.toString());
      }

      return var2 == null ? 0L : ((Number)var2).longValue();
   }

   public long getLong(String var1) throws SQLException {
      return this.getLong(this.findColumn(var1));
   }

   public Reader getNCharacterStream(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public Reader getNCharacterStream(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public NClob getNClob(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public NClob getNClob(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public String getNString(int var1) throws SQLException {
      return this.getString(var1);
   }

   public String getNString(String var1) throws SQLException {
      return this.getString(var1);
   }

   public Object getObject(int var1) throws SQLException {
      return this.get(var1);
   }

   public Object getObject(String var1) throws SQLException {
      return this.getObject(this.findColumn(var1));
   }

   public <T> T getObject(int var1, Class<T> var2) throws SQLException {
      if (this.get(var1) == null) {
         return null;
      } else if (var2 == BigDecimal.class) {
         return this.getBigDecimal(var1);
      } else if (var2 == BigInteger.class) {
         return this.getBigDecimal(var1).toBigInteger();
      } else if (var2 == String.class) {
         return this.getString(var1);
      } else if (var2 == Boolean.class) {
         return this.getBoolean(var1);
      } else if (var2 == Byte.class) {
         return this.getByte(var1);
      } else if (var2 == Short.class) {
         return this.getShort(var1);
      } else if (var2 == Integer.class) {
         return this.getInt(var1);
      } else if (var2 == Long.class) {
         return this.getLong(var1);
      } else if (var2 == Float.class) {
         return this.getFloat(var1);
      } else if (var2 == Double.class) {
         return this.getDouble(var1);
      } else if (var2 == Date.class) {
         return this.getDate(var1);
      } else if (var2 == Time.class) {
         return this.getTime(var1);
      } else if (var2 == Timestamp.class) {
         return this.getTimestamp(var1);
      } else if (var2 == UUID.class) {
         return this.getObject(var1);
      } else if (var2 == byte[].class) {
         return this.getBytes(var1);
      } else if (var2 == Array.class) {
         return this.getArray(var1);
      } else if (var2 == Blob.class) {
         return this.getBlob(var1);
      } else if (var2 == Clob.class) {
         return this.getClob(var1);
      } else {
         throw getUnsupportedException();
      }
   }

   public <T> T getObject(String var1, Class<T> var2) throws SQLException {
      return this.getObject(this.findColumn(var1), var2);
   }

   public Object getObject(int var1, Map<String, Class<?>> var2) throws SQLException {
      throw getUnsupportedException();
   }

   public Object getObject(String var1, Map<String, Class<?>> var2) throws SQLException {
      throw getUnsupportedException();
   }

   public Ref getRef(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public Ref getRef(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public RowId getRowId(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public RowId getRowId(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public short getShort(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 != null && !(var2 instanceof Number)) {
         var2 = Short.decode(var2.toString());
      }

      return var2 == null ? 0 : ((Number)var2).shortValue();
   }

   public short getShort(String var1) throws SQLException {
      return this.getShort(this.findColumn(var1));
   }

   public SQLXML getSQLXML(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public SQLXML getSQLXML(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public String getString(int var1) throws SQLException {
      Object var2 = this.get(var1);
      if (var2 == null) {
         return null;
      } else {
         switch (((SimpleColumnInfo)this.columns.get(var1 - 1)).type) {
            case 2005:
               Clob var3 = (Clob)var2;
               return var3.getSubString(1L, MathUtils.convertLongToInt(var3.length()));
            default:
               return var2.toString();
         }
      }
   }

   public String getString(String var1) throws SQLException {
      return this.getString(this.findColumn(var1));
   }

   public Time getTime(int var1) throws SQLException {
      return (Time)this.get(var1);
   }

   public Time getTime(String var1) throws SQLException {
      return this.getTime(this.findColumn(var1));
   }

   public Time getTime(int var1, Calendar var2) throws SQLException {
      throw getUnsupportedException();
   }

   public Time getTime(String var1, Calendar var2) throws SQLException {
      throw getUnsupportedException();
   }

   public Timestamp getTimestamp(int var1) throws SQLException {
      return (Timestamp)this.get(var1);
   }

   public Timestamp getTimestamp(String var1) throws SQLException {
      return this.getTimestamp(this.findColumn(var1));
   }

   public Timestamp getTimestamp(int var1, Calendar var2) throws SQLException {
      throw getUnsupportedException();
   }

   public Timestamp getTimestamp(String var1, Calendar var2) throws SQLException {
      throw getUnsupportedException();
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public URL getURL(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public URL getURL(String var1) throws SQLException {
      throw getUnsupportedException();
   }

   public void updateArray(int var1, Array var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateArray(String var1, Array var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateAsciiStream(int var1, InputStream var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateAsciiStream(String var1, InputStream var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateAsciiStream(int var1, InputStream var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateAsciiStream(String var1, InputStream var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateAsciiStream(String var1, InputStream var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBigDecimal(int var1, BigDecimal var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBigDecimal(String var1, BigDecimal var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBinaryStream(int var1, InputStream var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBinaryStream(String var1, InputStream var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBinaryStream(int var1, InputStream var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBinaryStream(String var1, InputStream var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBinaryStream(String var1, InputStream var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBlob(int var1, Blob var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBlob(String var1, Blob var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBlob(int var1, InputStream var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBlob(String var1, InputStream var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBlob(int var1, InputStream var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBlob(String var1, InputStream var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBoolean(int var1, boolean var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBoolean(String var1, boolean var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateByte(int var1, byte var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateByte(String var1, byte var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBytes(int var1, byte[] var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateBytes(String var1, byte[] var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateCharacterStream(int var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateCharacterStream(String var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateCharacterStream(int var1, Reader var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateCharacterStream(String var1, Reader var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateClob(int var1, Clob var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateClob(String var1, Clob var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateClob(int var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateClob(String var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateClob(int var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateClob(String var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateDate(int var1, Date var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateDate(String var1, Date var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateDouble(int var1, double var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateDouble(String var1, double var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateFloat(int var1, float var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateFloat(String var1, float var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateInt(int var1, int var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateInt(String var1, int var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateLong(int var1, long var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateLong(String var1, long var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNCharacterStream(int var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNCharacterStream(String var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNCharacterStream(String var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNClob(int var1, NClob var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNClob(String var1, NClob var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNClob(int var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNClob(String var1, Reader var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNClob(int var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNClob(String var1, Reader var2, long var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNString(int var1, String var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNString(String var1, String var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateNull(int var1) throws SQLException {
      this.update(var1, (Object)null);
   }

   public void updateNull(String var1) throws SQLException {
      this.update(var1, (Object)null);
   }

   public void updateObject(int var1, Object var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateObject(String var1, Object var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateObject(int var1, Object var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateObject(String var1, Object var2, int var3) throws SQLException {
      this.update(var1, var2);
   }

   public void updateRef(int var1, Ref var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateRef(String var1, Ref var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateRowId(int var1, RowId var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateRowId(String var1, RowId var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateShort(int var1, short var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateShort(String var1, short var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateSQLXML(int var1, SQLXML var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateSQLXML(String var1, SQLXML var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateString(int var1, String var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateString(String var1, String var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateTime(int var1, Time var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateTime(String var1, Time var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateTimestamp(int var1, Timestamp var2) throws SQLException {
      this.update(var1, var2);
   }

   public void updateTimestamp(String var1, Timestamp var2) throws SQLException {
      this.update(var1, var2);
   }

   public int getColumnCount() {
      return this.columns.size();
   }

   public int getColumnDisplaySize(int var1) {
      return 15;
   }

   public int getColumnType(int var1) throws SQLException {
      return this.getColumn(var1 - 1).type;
   }

   public int getPrecision(int var1) throws SQLException {
      return this.getColumn(var1 - 1).precision;
   }

   public int getScale(int var1) throws SQLException {
      return this.getColumn(var1 - 1).scale;
   }

   public int isNullable(int var1) {
      return 2;
   }

   public boolean isAutoIncrement(int var1) {
      return false;
   }

   public boolean isCaseSensitive(int var1) {
      return true;
   }

   public boolean isCurrency(int var1) {
      return false;
   }

   public boolean isDefinitelyWritable(int var1) {
      return false;
   }

   public boolean isReadOnly(int var1) {
      return true;
   }

   public boolean isSearchable(int var1) {
      return true;
   }

   public boolean isSigned(int var1) {
      return true;
   }

   public boolean isWritable(int var1) {
      return false;
   }

   public String getCatalogName(int var1) {
      return "";
   }

   public String getColumnClassName(int var1) throws SQLException {
      int var2 = DataType.getValueTypeFromResultSet(this, var1);
      return ValueToObjectConverter.getDefaultClass(var2, true).getName();
   }

   public String getColumnLabel(int var1) throws SQLException {
      return this.getColumn(var1 - 1).name;
   }

   public String getColumnName(int var1) throws SQLException {
      return this.getColumnLabel(var1);
   }

   public String getColumnTypeName(int var1) throws SQLException {
      return this.getColumn(var1 - 1).typeName;
   }

   public String getSchemaName(int var1) {
      return "";
   }

   public String getTableName(int var1) {
      return "";
   }

   public void afterLast() throws SQLException {
      throw getUnsupportedException();
   }

   public void cancelRowUpdates() throws SQLException {
      throw getUnsupportedException();
   }

   public void deleteRow() throws SQLException {
      throw getUnsupportedException();
   }

   public void insertRow() throws SQLException {
      throw getUnsupportedException();
   }

   public void moveToCurrentRow() throws SQLException {
      throw getUnsupportedException();
   }

   public void moveToInsertRow() throws SQLException {
      throw getUnsupportedException();
   }

   public void refreshRow() throws SQLException {
      throw getUnsupportedException();
   }

   public void updateRow() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean first() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean isAfterLast() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean isBeforeFirst() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean isFirst() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean isLast() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean last() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean previous() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean rowDeleted() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean rowInserted() throws SQLException {
      throw getUnsupportedException();
   }

   public boolean rowUpdated() throws SQLException {
      return true;
   }

   public void setFetchDirection(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public void setFetchSize(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public boolean absolute(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public boolean relative(int var1) throws SQLException {
      throw getUnsupportedException();
   }

   public String getCursorName() throws SQLException {
      throw getUnsupportedException();
   }

   private void update(int var1, Object var2) throws SQLException {
      this.checkClosed();
      this.checkColumnIndex(var1);
      this.currentRow[var1 - 1] = var2;
   }

   private void update(String var1, Object var2) throws SQLException {
      this.currentRow[this.findColumn(var1) - 1] = var2;
   }

   static SQLException getUnsupportedException() {
      return DbException.getJdbcSQLException(50100);
   }

   private void checkClosed() throws SQLException {
      if (this.columns == null) {
         throw DbException.getJdbcSQLException(90007);
      }
   }

   private void checkColumnIndex(int var1) throws SQLException {
      if (var1 < 1 || var1 > this.columns.size()) {
         throw DbException.getInvalidValueException("columnIndex", var1).getSQLException();
      }
   }

   private Object get(int var1) throws SQLException {
      if (this.currentRow == null) {
         throw DbException.getJdbcSQLException(2000);
      } else {
         this.checkColumnIndex(var1);
         --var1;
         Object var2 = var1 < this.currentRow.length ? this.currentRow[var1] : null;
         this.wasNull = var2 == null;
         return var2;
      }
   }

   private SimpleColumnInfo getColumn(int var1) throws SQLException {
      this.checkColumnIndex(var1 + 1);
      return (SimpleColumnInfo)this.columns.get(var1);
   }

   public int getHoldability() {
      return 1;
   }

   public boolean isClosed() {
      return this.rows == null && this.source == null;
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      try {
         if (this.isWrapperFor(var1)) {
            return this;
         } else {
            throw DbException.getInvalidValueException("iface", var1);
         }
      } catch (Exception var3) {
         throw DbException.toSQLException(var3);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1 != null && var1.isAssignableFrom(this.getClass());
   }

   public void setAutoClose(boolean var1) {
      this.autoClose = var1;
   }

   public boolean getAutoClose() {
      return this.autoClose;
   }

   public static class SimpleArray implements Array {
      private final Object[] value;

      SimpleArray(Object[] var1) {
         this.value = var1;
      }

      public Object getArray() {
         return this.value;
      }

      public Object getArray(Map<String, Class<?>> var1) throws SQLException {
         throw SimpleResultSet.getUnsupportedException();
      }

      public Object getArray(long var1, int var3) throws SQLException {
         throw SimpleResultSet.getUnsupportedException();
      }

      public Object getArray(long var1, int var3, Map<String, Class<?>> var4) throws SQLException {
         throw SimpleResultSet.getUnsupportedException();
      }

      public int getBaseType() {
         return 0;
      }

      public String getBaseTypeName() {
         return "NULL";
      }

      public ResultSet getResultSet() throws SQLException {
         throw SimpleResultSet.getUnsupportedException();
      }

      public ResultSet getResultSet(Map<String, Class<?>> var1) throws SQLException {
         throw SimpleResultSet.getUnsupportedException();
      }

      public ResultSet getResultSet(long var1, int var3) throws SQLException {
         throw SimpleResultSet.getUnsupportedException();
      }

      public ResultSet getResultSet(long var1, int var3, Map<String, Class<?>> var4) throws SQLException {
         throw SimpleResultSet.getUnsupportedException();
      }

      public void free() {
      }
   }
}
