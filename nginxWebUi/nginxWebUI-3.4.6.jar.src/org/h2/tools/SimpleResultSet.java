/*      */ package org.h2.tools;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.NClob;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Map;
/*      */ import java.util.UUID;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.util.Bits;
/*      */ import org.h2.util.JdbcUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.SimpleColumnInfo;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueToObjectConverter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SimpleResultSet
/*      */   implements ResultSet, ResultSetMetaData
/*      */ {
/*      */   private ArrayList<Object[]> rows;
/*      */   private Object[] currentRow;
/*   66 */   private int rowId = -1;
/*      */   private boolean wasNull;
/*      */   private SimpleRowSource source;
/*   69 */   private ArrayList<SimpleColumnInfo> columns = Utils.newSmallArrayList();
/*      */ 
/*      */   
/*      */   private boolean autoClose = true;
/*      */ 
/*      */ 
/*      */   
/*      */   public SimpleResultSet() {
/*   77 */     this.rows = Utils.newSmallArrayList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SimpleResultSet(SimpleRowSource paramSimpleRowSource) {
/*   87 */     this.source = paramSimpleRowSource;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addColumn(String paramString, int paramInt1, int paramInt2, int paramInt3) {
/*  101 */     int i = DataType.convertSQLTypeToValueType(paramInt1);
/*  102 */     addColumn(paramString, paramInt1, Value.getTypeName(i), paramInt2, paramInt3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addColumn(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3) {
/*  117 */     if (this.rows != null && !this.rows.isEmpty()) {
/*  118 */       throw new IllegalStateException("Cannot add a column after adding rows");
/*      */     }
/*      */     
/*  121 */     if (paramString1 == null) {
/*  122 */       paramString1 = "C" + (this.columns.size() + 1);
/*      */     }
/*  124 */     this.columns.add(new SimpleColumnInfo(paramString1, paramInt1, paramString2, paramInt2, paramInt3));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addRow(Object... paramVarArgs) {
/*  134 */     if (this.rows == null) {
/*  135 */       throw new IllegalStateException("Cannot add a row when using RowSource");
/*      */     }
/*      */     
/*  138 */     this.rows.add(paramVarArgs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getConcurrency() {
/*  148 */     return 1007;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFetchDirection() {
/*  158 */     return 1000;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFetchSize() {
/*  168 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getRow() {
/*  178 */     return (this.currentRow == null) ? 0 : (this.rowId + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getType() {
/*  189 */     if (this.autoClose) {
/*  190 */       return 1003;
/*      */     }
/*  192 */     return 1004;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() {
/*  200 */     this.currentRow = null;
/*  201 */     this.rows = null;
/*  202 */     this.columns = null;
/*  203 */     this.rowId = -1;
/*  204 */     if (this.source != null) {
/*  205 */       this.source.close();
/*  206 */       this.source = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean next() throws SQLException {
/*  217 */     if (this.source != null) {
/*  218 */       this.rowId++;
/*  219 */       this.currentRow = this.source.readRow();
/*  220 */       if (this.currentRow != null) {
/*  221 */         return true;
/*      */       }
/*  223 */     } else if (this.rows != null && this.rowId < this.rows.size()) {
/*  224 */       this.rowId++;
/*  225 */       if (this.rowId < this.rows.size()) {
/*  226 */         this.currentRow = this.rows.get(this.rowId);
/*  227 */         return true;
/*      */       } 
/*  229 */       this.currentRow = null;
/*      */     } 
/*  231 */     if (this.autoClose) {
/*  232 */       close();
/*      */     }
/*  234 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void beforeFirst() throws SQLException {
/*  243 */     if (this.autoClose) {
/*  244 */       throw DbException.getJdbcSQLException(90128);
/*      */     }
/*  246 */     this.rowId = -1;
/*  247 */     if (this.source != null) {
/*  248 */       this.source.reset();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean wasNull() {
/*  259 */     return this.wasNull;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int findColumn(String paramString) throws SQLException {
/*  273 */     if (paramString != null && this.columns != null) {
/*  274 */       byte b; int i; for (b = 0, i = this.columns.size(); b < i; b++) {
/*  275 */         if (paramString.equalsIgnoreCase((getColumn(b)).name)) {
/*  276 */           return b + 1;
/*      */         }
/*      */       } 
/*      */     } 
/*  280 */     throw DbException.getJdbcSQLException(42122, paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSetMetaData getMetaData() {
/*  290 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLWarning getWarnings() {
/*  300 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Statement getStatement() {
/*  310 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearWarnings() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Array getArray(int paramInt) throws SQLException {
/*  331 */     Object[] arrayOfObject = (Object[])get(paramInt);
/*  332 */     return (arrayOfObject == null) ? null : new SimpleArray(arrayOfObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Array getArray(String paramString) throws SQLException {
/*  343 */     return getArray(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getAsciiStream(int paramInt) throws SQLException {
/*  351 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getAsciiStream(String paramString) throws SQLException {
/*  359 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getBigDecimal(int paramInt) throws SQLException {
/*  370 */     Object object = get(paramInt);
/*  371 */     if (object != null && !(object instanceof BigDecimal)) {
/*  372 */       object = new BigDecimal(object.toString());
/*      */     }
/*  374 */     return (BigDecimal)object;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigDecimal getBigDecimal(String paramString) throws SQLException {
/*  385 */     return getBigDecimal(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException {
/*  395 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException {
/*  405 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getBinaryStream(int paramInt) throws SQLException {
/*  416 */     return asInputStream(get(paramInt));
/*      */   }
/*      */   
/*      */   private static InputStream asInputStream(Object paramObject) throws SQLException {
/*  420 */     if (paramObject == null)
/*  421 */       return null; 
/*  422 */     if (paramObject instanceof Blob) {
/*  423 */       return ((Blob)paramObject).getBinaryStream();
/*      */     }
/*  425 */     return (InputStream)paramObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getBinaryStream(String paramString) throws SQLException {
/*  436 */     return getBinaryStream(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Blob getBlob(int paramInt) throws SQLException {
/*  449 */     return (Blob)get(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Blob getBlob(String paramString) throws SQLException {
/*  462 */     return getBlob(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int paramInt) throws SQLException {
/*  473 */     Object object = get(paramInt);
/*  474 */     if (object == null) {
/*  475 */       return false;
/*      */     }
/*  477 */     if (object instanceof Boolean) {
/*  478 */       return ((Boolean)object).booleanValue();
/*      */     }
/*  480 */     if (object instanceof Number) {
/*  481 */       Number number = (Number)object;
/*  482 */       if (number instanceof Double || number instanceof Float) {
/*  483 */         return (number.doubleValue() != 0.0D);
/*      */       }
/*  485 */       if (number instanceof BigDecimal) {
/*  486 */         return (((BigDecimal)number).signum() != 0);
/*      */       }
/*  488 */       if (number instanceof BigInteger) {
/*  489 */         return (((BigInteger)number).signum() != 0);
/*      */       }
/*  491 */       return (number.longValue() != 0L);
/*      */     } 
/*  493 */     return Utils.parseBoolean(object.toString(), false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getBoolean(String paramString) throws SQLException {
/*  504 */     return getBoolean(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte getByte(int paramInt) throws SQLException {
/*  515 */     Object object = get(paramInt);
/*  516 */     if (object != null && !(object instanceof Number)) {
/*  517 */       object = Byte.decode(object.toString());
/*      */     }
/*  519 */     return (object == null) ? 0 : ((Number)object).byteValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte getByte(String paramString) throws SQLException {
/*  530 */     return getByte(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBytes(int paramInt) throws SQLException {
/*  541 */     Object object = get(paramInt);
/*  542 */     if (object == null || object instanceof byte[]) {
/*  543 */       return (byte[])object;
/*      */     }
/*  545 */     if (object instanceof UUID) {
/*  546 */       return Bits.uuidToBytes((UUID)object);
/*      */     }
/*  548 */     return JdbcUtils.serialize(object, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] getBytes(String paramString) throws SQLException {
/*  559 */     return getBytes(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getCharacterStream(int paramInt) throws SQLException {
/*  572 */     return asReader(get(paramInt));
/*      */   }
/*      */   
/*      */   private static Reader asReader(Object paramObject) throws SQLException {
/*  576 */     if (paramObject == null)
/*  577 */       return null; 
/*  578 */     if (paramObject instanceof Clob) {
/*  579 */       return ((Clob)paramObject).getCharacterStream();
/*      */     }
/*  581 */     return (Reader)paramObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getCharacterStream(String paramString) throws SQLException {
/*  594 */     return getCharacterStream(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Clob getClob(int paramInt) throws SQLException {
/*  607 */     return (Clob)get(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Clob getClob(String paramString) throws SQLException {
/*  620 */     return getClob(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(int paramInt) throws SQLException {
/*  631 */     return (Date)get(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(String paramString) throws SQLException {
/*  642 */     return getDate(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException {
/*  650 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(String paramString, Calendar paramCalendar) throws SQLException {
/*  658 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDouble(int paramInt) throws SQLException {
/*  669 */     Object object = get(paramInt);
/*  670 */     if (object != null && !(object instanceof Number)) {
/*  671 */       return Double.parseDouble(object.toString());
/*      */     }
/*  673 */     return (object == null) ? 0.0D : ((Number)object).doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDouble(String paramString) throws SQLException {
/*  684 */     return getDouble(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloat(int paramInt) throws SQLException {
/*  695 */     Object object = get(paramInt);
/*  696 */     if (object != null && !(object instanceof Number)) {
/*  697 */       return Float.parseFloat(object.toString());
/*      */     }
/*  699 */     return (object == null) ? 0.0F : ((Number)object).floatValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloat(String paramString) throws SQLException {
/*  710 */     return getFloat(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInt(int paramInt) throws SQLException {
/*  721 */     Object object = get(paramInt);
/*  722 */     if (object != null && !(object instanceof Number)) {
/*  723 */       object = Integer.decode(object.toString());
/*      */     }
/*  725 */     return (object == null) ? 0 : ((Number)object).intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInt(String paramString) throws SQLException {
/*  736 */     return getInt(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLong(int paramInt) throws SQLException {
/*  747 */     Object object = get(paramInt);
/*  748 */     if (object != null && !(object instanceof Number)) {
/*  749 */       object = Long.decode(object.toString());
/*      */     }
/*  751 */     return (object == null) ? 0L : ((Number)object).longValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLong(String paramString) throws SQLException {
/*  762 */     return getLong(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getNCharacterStream(int paramInt) throws SQLException {
/*  770 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getNCharacterStream(String paramString) throws SQLException {
/*  778 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NClob getNClob(int paramInt) throws SQLException {
/*  786 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public NClob getNClob(String paramString) throws SQLException {
/*  794 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNString(int paramInt) throws SQLException {
/*  802 */     return getString(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNString(String paramString) throws SQLException {
/*  810 */     return getString(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(int paramInt) throws SQLException {
/*  821 */     return get(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(String paramString) throws SQLException {
/*  832 */     return getObject(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException {
/*  845 */     if (get(paramInt) == null) {
/*  846 */       return null;
/*      */     }
/*  848 */     if (paramClass == BigDecimal.class)
/*  849 */       return (T)getBigDecimal(paramInt); 
/*  850 */     if (paramClass == BigInteger.class)
/*  851 */       return (T)getBigDecimal(paramInt).toBigInteger(); 
/*  852 */     if (paramClass == String.class)
/*  853 */       return (T)getString(paramInt); 
/*  854 */     if (paramClass == Boolean.class)
/*  855 */       return (T)Boolean.valueOf(getBoolean(paramInt)); 
/*  856 */     if (paramClass == Byte.class)
/*  857 */       return (T)Byte.valueOf(getByte(paramInt)); 
/*  858 */     if (paramClass == Short.class)
/*  859 */       return (T)Short.valueOf(getShort(paramInt)); 
/*  860 */     if (paramClass == Integer.class)
/*  861 */       return (T)Integer.valueOf(getInt(paramInt)); 
/*  862 */     if (paramClass == Long.class)
/*  863 */       return (T)Long.valueOf(getLong(paramInt)); 
/*  864 */     if (paramClass == Float.class)
/*  865 */       return (T)Float.valueOf(getFloat(paramInt)); 
/*  866 */     if (paramClass == Double.class)
/*  867 */       return (T)Double.valueOf(getDouble(paramInt)); 
/*  868 */     if (paramClass == Date.class)
/*  869 */       return (T)getDate(paramInt); 
/*  870 */     if (paramClass == Time.class)
/*  871 */       return (T)getTime(paramInt); 
/*  872 */     if (paramClass == Timestamp.class)
/*  873 */       return (T)getTimestamp(paramInt); 
/*  874 */     if (paramClass == UUID.class)
/*  875 */       return (T)getObject(paramInt); 
/*  876 */     if (paramClass == byte[].class)
/*  877 */       return (T)getBytes(paramInt); 
/*  878 */     if (paramClass == Array.class)
/*  879 */       return (T)getArray(paramInt); 
/*  880 */     if (paramClass == Blob.class)
/*  881 */       return (T)getBlob(paramInt); 
/*  882 */     if (paramClass == Clob.class) {
/*  883 */       return (T)getClob(paramInt);
/*      */     }
/*  885 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T getObject(String paramString, Class<T> paramClass) throws SQLException {
/*  898 */     return getObject(findColumn(paramString), paramClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
/*  907 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException {
/*  916 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(int paramInt) throws SQLException {
/*  924 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(String paramString) throws SQLException {
/*  932 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowId getRowId(int paramInt) throws SQLException {
/*  940 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowId getRowId(String paramString) throws SQLException {
/*  948 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short getShort(int paramInt) throws SQLException {
/*  959 */     Object object = get(paramInt);
/*  960 */     if (object != null && !(object instanceof Number)) {
/*  961 */       object = Short.decode(object.toString());
/*      */     }
/*  963 */     return (object == null) ? 0 : ((Number)object).shortValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short getShort(String paramString) throws SQLException {
/*  974 */     return getShort(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLXML getSQLXML(int paramInt) throws SQLException {
/*  982 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLXML getSQLXML(String paramString) throws SQLException {
/*  990 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString(int paramInt) throws SQLException {
/*      */     Clob clob;
/* 1001 */     Object object = get(paramInt);
/* 1002 */     if (object == null) {
/* 1003 */       return null;
/*      */     }
/* 1005 */     switch (((SimpleColumnInfo)this.columns.get(paramInt - 1)).type) {
/*      */       case 2005:
/* 1007 */         clob = (Clob)object;
/* 1008 */         return clob.getSubString(1L, MathUtils.convertLongToInt(clob.length()));
/*      */     } 
/* 1010 */     return object.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString(String paramString) throws SQLException {
/* 1021 */     return getString(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(int paramInt) throws SQLException {
/* 1032 */     return (Time)get(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(String paramString) throws SQLException {
/* 1043 */     return getTime(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException {
/* 1051 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(String paramString, Calendar paramCalendar) throws SQLException {
/* 1059 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(int paramInt) throws SQLException {
/* 1070 */     return (Timestamp)get(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(String paramString) throws SQLException {
/* 1081 */     return getTimestamp(findColumn(paramString));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException {
/* 1090 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException {
/* 1099 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(int paramInt) throws SQLException {
/* 1108 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(String paramString) throws SQLException {
/* 1117 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(int paramInt) throws SQLException {
/* 1125 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(String paramString) throws SQLException {
/* 1133 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateArray(int paramInt, Array paramArray) throws SQLException {
/* 1143 */     update(paramInt, paramArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateArray(String paramString, Array paramArray) throws SQLException {
/* 1151 */     update(paramString, paramArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException {
/* 1160 */     update(paramInt, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream) throws SQLException {
/* 1169 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
/* 1178 */     update(paramInt1, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
/* 1187 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1196 */     update(paramInt, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1205 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
/* 1214 */     update(paramInt, paramBigDecimal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException {
/* 1223 */     update(paramString, paramBigDecimal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException {
/* 1232 */     update(paramInt, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream) throws SQLException {
/* 1241 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
/* 1250 */     update(paramInt1, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
/* 1259 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1268 */     update(paramInt, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1277 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(int paramInt, Blob paramBlob) throws SQLException {
/* 1285 */     update(paramInt, paramBlob);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(String paramString, Blob paramBlob) throws SQLException {
/* 1293 */     update(paramString, paramBlob);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream) throws SQLException {
/* 1301 */     update(paramInt, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(String paramString, InputStream paramInputStream) throws SQLException {
/* 1310 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1319 */     update(paramInt, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/* 1328 */     update(paramString, paramInputStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException {
/* 1336 */     update(paramInt, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException {
/* 1345 */     update(paramString, Boolean.valueOf(paramBoolean));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateByte(int paramInt, byte paramByte) throws SQLException {
/* 1353 */     update(paramInt, Byte.valueOf(paramByte));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateByte(String paramString, byte paramByte) throws SQLException {
/* 1361 */     update(paramString, Byte.valueOf(paramByte));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBytes(int paramInt, byte[] paramArrayOfbyte) throws SQLException {
/* 1369 */     update(paramInt, paramArrayOfbyte);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateBytes(String paramString, byte[] paramArrayOfbyte) throws SQLException {
/* 1377 */     update(paramString, paramArrayOfbyte);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader) throws SQLException {
/* 1386 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(String paramString, Reader paramReader) throws SQLException {
/* 1395 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
/* 1404 */     update(paramInt1, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException {
/* 1413 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/* 1422 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1431 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(int paramInt, Clob paramClob) throws SQLException {
/* 1439 */     update(paramInt, paramClob);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(String paramString, Clob paramClob) throws SQLException {
/* 1447 */     update(paramString, paramClob);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(int paramInt, Reader paramReader) throws SQLException {
/* 1455 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(String paramString, Reader paramReader) throws SQLException {
/* 1463 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/* 1472 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1481 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDate(int paramInt, Date paramDate) throws SQLException {
/* 1489 */     update(paramInt, paramDate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDate(String paramString, Date paramDate) throws SQLException {
/* 1497 */     update(paramString, paramDate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDouble(int paramInt, double paramDouble) throws SQLException {
/* 1505 */     update(paramInt, Double.valueOf(paramDouble));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDouble(String paramString, double paramDouble) throws SQLException {
/* 1513 */     update(paramString, Double.valueOf(paramDouble));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateFloat(int paramInt, float paramFloat) throws SQLException {
/* 1521 */     update(paramInt, Float.valueOf(paramFloat));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateFloat(String paramString, float paramFloat) throws SQLException {
/* 1529 */     update(paramString, Float.valueOf(paramFloat));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateInt(int paramInt1, int paramInt2) throws SQLException {
/* 1537 */     update(paramInt1, Integer.valueOf(paramInt2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateInt(String paramString, int paramInt) throws SQLException {
/* 1545 */     update(paramString, Integer.valueOf(paramInt));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateLong(int paramInt, long paramLong) throws SQLException {
/* 1553 */     update(paramInt, Long.valueOf(paramLong));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateLong(String paramString, long paramLong) throws SQLException {
/* 1561 */     update(paramString, Long.valueOf(paramLong));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNCharacterStream(int paramInt, Reader paramReader) throws SQLException {
/* 1570 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader) throws SQLException {
/* 1579 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/* 1588 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1597 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNClob(int paramInt, NClob paramNClob) throws SQLException {
/* 1605 */     update(paramInt, paramNClob);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNClob(String paramString, NClob paramNClob) throws SQLException {
/* 1613 */     update(paramString, paramNClob);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNClob(int paramInt, Reader paramReader) throws SQLException {
/* 1621 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNClob(String paramString, Reader paramReader) throws SQLException {
/* 1629 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/* 1638 */     update(paramInt, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
/* 1647 */     update(paramString, paramReader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNString(int paramInt, String paramString) throws SQLException {
/* 1655 */     update(paramInt, paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNString(String paramString1, String paramString2) throws SQLException {
/* 1663 */     update(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNull(int paramInt) throws SQLException {
/* 1671 */     update(paramInt, (Object)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateNull(String paramString) throws SQLException {
/* 1679 */     update(paramString, (Object)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateObject(int paramInt, Object paramObject) throws SQLException {
/* 1687 */     update(paramInt, paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateObject(String paramString, Object paramObject) throws SQLException {
/* 1695 */     update(paramString, paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
/* 1704 */     update(paramInt1, paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException {
/* 1713 */     update(paramString, paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRef(int paramInt, Ref paramRef) throws SQLException {
/* 1721 */     update(paramInt, paramRef);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRef(String paramString, Ref paramRef) throws SQLException {
/* 1729 */     update(paramString, paramRef);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRowId(int paramInt, RowId paramRowId) throws SQLException {
/* 1737 */     update(paramInt, paramRowId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRowId(String paramString, RowId paramRowId) throws SQLException {
/* 1745 */     update(paramString, paramRowId);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateShort(int paramInt, short paramShort) throws SQLException {
/* 1753 */     update(paramInt, Short.valueOf(paramShort));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateShort(String paramString, short paramShort) throws SQLException {
/* 1761 */     update(paramString, Short.valueOf(paramShort));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException {
/* 1769 */     update(paramInt, paramSQLXML);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException {
/* 1777 */     update(paramString, paramSQLXML);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateString(int paramInt, String paramString) throws SQLException {
/* 1785 */     update(paramInt, paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateString(String paramString1, String paramString2) throws SQLException {
/* 1793 */     update(paramString1, paramString2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTime(int paramInt, Time paramTime) throws SQLException {
/* 1801 */     update(paramInt, paramTime);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTime(String paramString, Time paramTime) throws SQLException {
/* 1809 */     update(paramString, paramTime);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
/* 1818 */     update(paramInt, paramTimestamp);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException {
/* 1827 */     update(paramString, paramTimestamp);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getColumnCount() {
/* 1839 */     return this.columns.size();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getColumnDisplaySize(int paramInt) {
/* 1850 */     return 15;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getColumnType(int paramInt) throws SQLException {
/* 1861 */     return (getColumn(paramInt - 1)).type;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPrecision(int paramInt) throws SQLException {
/* 1872 */     return (getColumn(paramInt - 1)).precision;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getScale(int paramInt) throws SQLException {
/* 1883 */     return (getColumn(paramInt - 1)).scale;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int isNullable(int paramInt) {
/* 1894 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutoIncrement(int paramInt) {
/* 1905 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCaseSensitive(int paramInt) {
/* 1916 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCurrency(int paramInt) {
/* 1927 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDefinitelyWritable(int paramInt) {
/* 1938 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly(int paramInt) {
/* 1949 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSearchable(int paramInt) {
/* 1960 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSigned(int paramInt) {
/* 1971 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWritable(int paramInt) {
/* 1982 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCatalogName(int paramInt) {
/* 1993 */     return "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getColumnClassName(int paramInt) throws SQLException {
/* 2004 */     int i = DataType.getValueTypeFromResultSet(this, paramInt);
/* 2005 */     return ValueToObjectConverter.getDefaultClass(i, true).getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getColumnLabel(int paramInt) throws SQLException {
/* 2016 */     return (getColumn(paramInt - 1)).name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getColumnName(int paramInt) throws SQLException {
/* 2027 */     return getColumnLabel(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getColumnTypeName(int paramInt) throws SQLException {
/* 2038 */     return (getColumn(paramInt - 1)).typeName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSchemaName(int paramInt) {
/* 2049 */     return "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTableName(int paramInt) {
/* 2060 */     return "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void afterLast() throws SQLException {
/* 2070 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cancelRowUpdates() throws SQLException {
/* 2078 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void deleteRow() throws SQLException {
/* 2086 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void insertRow() throws SQLException {
/* 2094 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void moveToCurrentRow() throws SQLException {
/* 2102 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void moveToInsertRow() throws SQLException {
/* 2110 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void refreshRow() throws SQLException {
/* 2118 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRow() throws SQLException {
/* 2126 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean first() throws SQLException {
/* 2134 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAfterLast() throws SQLException {
/* 2142 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBeforeFirst() throws SQLException {
/* 2150 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isFirst() throws SQLException {
/* 2158 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLast() throws SQLException {
/* 2166 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean last() throws SQLException {
/* 2174 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean previous() throws SQLException {
/* 2182 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean rowDeleted() throws SQLException {
/* 2190 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean rowInserted() throws SQLException {
/* 2198 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean rowUpdated() throws SQLException {
/* 2206 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFetchDirection(int paramInt) throws SQLException {
/* 2214 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFetchSize(int paramInt) throws SQLException {
/* 2222 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean absolute(int paramInt) throws SQLException {
/* 2230 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean relative(int paramInt) throws SQLException {
/* 2238 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCursorName() throws SQLException {
/* 2246 */     throw getUnsupportedException();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void update(int paramInt, Object paramObject) throws SQLException {
/* 2252 */     checkClosed();
/* 2253 */     checkColumnIndex(paramInt);
/* 2254 */     this.currentRow[paramInt - 1] = paramObject;
/*      */   }
/*      */   
/*      */   private void update(String paramString, Object paramObject) throws SQLException {
/* 2258 */     this.currentRow[findColumn(paramString) - 1] = paramObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static SQLException getUnsupportedException() {
/* 2265 */     return DbException.getJdbcSQLException(50100);
/*      */   }
/*      */   
/*      */   private void checkClosed() throws SQLException {
/* 2269 */     if (this.columns == null) {
/* 2270 */       throw DbException.getJdbcSQLException(90007);
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkColumnIndex(int paramInt) throws SQLException {
/* 2275 */     if (paramInt < 1 || paramInt > this.columns.size()) {
/* 2276 */       throw DbException.getInvalidValueException("columnIndex", 
/* 2277 */           Integer.valueOf(paramInt)).getSQLException();
/*      */     }
/*      */   }
/*      */   
/*      */   private Object get(int paramInt) throws SQLException {
/* 2282 */     if (this.currentRow == null) {
/* 2283 */       throw DbException.getJdbcSQLException(2000);
/*      */     }
/* 2285 */     checkColumnIndex(paramInt);
/* 2286 */     paramInt--;
/* 2287 */     Object object = (paramInt < this.currentRow.length) ? this.currentRow[paramInt] : null;
/*      */     
/* 2289 */     this.wasNull = (object == null);
/* 2290 */     return object;
/*      */   }
/*      */   
/*      */   private SimpleColumnInfo getColumn(int paramInt) throws SQLException {
/* 2294 */     checkColumnIndex(paramInt + 1);
/* 2295 */     return this.columns.get(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHoldability() {
/* 2305 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() {
/* 2315 */     return (this.rows == null && this.source == null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T unwrap(Class<T> paramClass) throws SQLException {
/*      */     try {
/* 2328 */       if (isWrapperFor(paramClass)) {
/* 2329 */         return (T)this;
/*      */       }
/* 2331 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 2332 */     } catch (Exception exception) {
/* 2333 */       throw DbException.toSQLException(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
/* 2345 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoClose(boolean paramBoolean) {
/* 2355 */     this.autoClose = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoClose() {
/* 2364 */     return this.autoClose;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class SimpleArray
/*      */     implements Array
/*      */   {
/*      */     private final Object[] value;
/*      */ 
/*      */     
/*      */     SimpleArray(Object[] param1ArrayOfObject) {
/* 2376 */       this.value = param1ArrayOfObject;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getArray() {
/* 2386 */       return this.value;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getArray(Map<String, Class<?>> param1Map) throws SQLException {
/* 2394 */       throw SimpleResultSet.getUnsupportedException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getArray(long param1Long, int param1Int) throws SQLException {
/* 2402 */       throw SimpleResultSet.getUnsupportedException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Object getArray(long param1Long, int param1Int, Map<String, Class<?>> param1Map) throws SQLException {
/* 2411 */       throw SimpleResultSet.getUnsupportedException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getBaseType() {
/* 2421 */       return 0;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getBaseTypeName() {
/* 2431 */       return "NULL";
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ResultSet getResultSet() throws SQLException {
/* 2439 */       throw SimpleResultSet.getUnsupportedException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ResultSet getResultSet(Map<String, Class<?>> param1Map) throws SQLException {
/* 2448 */       throw SimpleResultSet.getUnsupportedException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ResultSet getResultSet(long param1Long, int param1Int) throws SQLException {
/* 2457 */       throw SimpleResultSet.getUnsupportedException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ResultSet getResultSet(long param1Long, int param1Int, Map<String, Class<?>> param1Map) throws SQLException {
/* 2466 */       throw SimpleResultSet.getUnsupportedException();
/*      */     }
/*      */     
/*      */     public void free() {}
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\SimpleResultSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */