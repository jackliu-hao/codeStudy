/*      */ package org.h2.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
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
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.h2.command.CommandInterface;
/*      */ import org.h2.engine.Session;
/*      */ import org.h2.engine.SysProperties;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.message.TraceObject;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.result.UpdatableRow;
/*      */ import org.h2.util.IOUtils;
/*      */ import org.h2.util.LegacyDateTimeUtils;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueBoolean;
/*      */ import org.h2.value.ValueDouble;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueNumeric;
/*      */ import org.h2.value.ValueReal;
/*      */ import org.h2.value.ValueSmallint;
/*      */ import org.h2.value.ValueTinyint;
/*      */ import org.h2.value.ValueToObjectConverter;
/*      */ import org.h2.value.ValueVarbinary;
/*      */ import org.h2.value.ValueVarchar;
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
/*      */ public final class JdbcResultSet
/*      */   extends TraceObject
/*      */   implements ResultSet
/*      */ {
/*      */   private final boolean scrollable;
/*      */   private final boolean updatable;
/*      */   private final boolean triggerUpdatable;
/*      */   ResultInterface result;
/*      */   private JdbcConnection conn;
/*      */   private JdbcStatement stat;
/*      */   private int columnCount;
/*      */   private boolean wasNull;
/*      */   private Value[] insertRow;
/*      */   private Value[] updateRow;
/*      */   private HashMap<String, Integer> columnLabelMap;
/*      */   private HashMap<Long, Value[]> patchedRows;
/*      */   private JdbcPreparedStatement preparedStatement;
/*      */   private final CommandInterface command;
/*      */   
/*      */   public JdbcResultSet(JdbcConnection paramJdbcConnection, JdbcStatement paramJdbcStatement, CommandInterface paramCommandInterface, ResultInterface paramResultInterface, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/*   96 */     setTrace(paramJdbcConnection.getSession().getTrace(), 4, paramInt);
/*   97 */     this.conn = paramJdbcConnection;
/*   98 */     this.stat = paramJdbcStatement;
/*   99 */     this.command = paramCommandInterface;
/*  100 */     this.result = paramResultInterface;
/*  101 */     this.columnCount = paramResultInterface.getVisibleColumnCount();
/*  102 */     this.scrollable = paramBoolean1;
/*  103 */     this.updatable = paramBoolean2;
/*  104 */     this.triggerUpdatable = paramBoolean3;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   JdbcResultSet(JdbcConnection paramJdbcConnection, JdbcPreparedStatement paramJdbcPreparedStatement, CommandInterface paramCommandInterface, ResultInterface paramResultInterface, int paramInt, boolean paramBoolean1, boolean paramBoolean2, HashMap<String, Integer> paramHashMap) {
/*  110 */     this(paramJdbcConnection, paramJdbcPreparedStatement, paramCommandInterface, paramResultInterface, paramInt, paramBoolean1, paramBoolean2, false);
/*  111 */     this.columnLabelMap = paramHashMap;
/*  112 */     this.preparedStatement = paramJdbcPreparedStatement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean next() throws SQLException {
/*      */     try {
/*  123 */       debugCodeCall("next");
/*  124 */       checkClosed();
/*  125 */       return nextRow();
/*  126 */     } catch (Exception exception) {
/*  127 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSetMetaData getMetaData() throws SQLException {
/*      */     try {
/*  139 */       int i = getNextId(5);
/*  140 */       debugCodeAssign("ResultSetMetaData", 5, i, "getMetaData()");
/*  141 */       checkClosed();
/*  142 */       String str = this.conn.getCatalog();
/*  143 */       return new JdbcResultSetMetaData(this, null, this.result, str, this.conn.getSession().getTrace(), i);
/*  144 */     } catch (Exception exception) {
/*  145 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean wasNull() throws SQLException {
/*      */     try {
/*  157 */       debugCodeCall("wasNull");
/*  158 */       checkClosed();
/*  159 */       return this.wasNull;
/*  160 */     } catch (Exception exception) {
/*  161 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public int findColumn(String paramString) throws SQLException {
/*      */     try {
/*  177 */       debugCodeCall("findColumn", paramString);
/*  178 */       return getColumnIndex(paramString);
/*  179 */     } catch (Exception exception) {
/*  180 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws SQLException {
/*      */     try {
/*  190 */       debugCodeCall("close");
/*  191 */       closeInternal(false);
/*  192 */     } catch (Exception exception) {
/*  193 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void closeInternal(boolean paramBoolean) {
/*  202 */     if (this.result != null) {
/*      */       try {
/*  204 */         if (this.result.isLazy()) {
/*  205 */           this.stat.onLazyResultSetClose(this.command, (this.preparedStatement == null));
/*      */         }
/*  207 */         this.result.close();
/*      */       } finally {
/*  209 */         JdbcStatement jdbcStatement = this.stat;
/*  210 */         this.columnCount = 0;
/*  211 */         this.result = null;
/*  212 */         this.stat = null;
/*  213 */         this.conn = null;
/*  214 */         this.insertRow = null;
/*  215 */         this.updateRow = null;
/*  216 */         if (!paramBoolean && jdbcStatement != null) {
/*  217 */           jdbcStatement.closeIfCloseOnCompletion();
/*      */         }
/*      */       } 
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
/*      */   public Statement getStatement() throws SQLException {
/*      */     try {
/*  232 */       debugCodeCall("getStatement");
/*  233 */       checkClosed();
/*  234 */       return this.stat;
/*  235 */     } catch (Exception exception) {
/*  236 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SQLWarning getWarnings() throws SQLException {
/*      */     try {
/*  248 */       debugCodeCall("getWarnings");
/*  249 */       checkClosed();
/*  250 */       return null;
/*  251 */     } catch (Exception exception) {
/*  252 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearWarnings() throws SQLException {
/*      */     try {
/*  262 */       debugCodeCall("clearWarnings");
/*  263 */       checkClosed();
/*  264 */     } catch (Exception exception) {
/*  265 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString(int paramInt) throws SQLException {
/*      */     try {
/*  282 */       debugCodeCall("getString", paramInt);
/*  283 */       return get(checkColumnIndex(paramInt)).getString();
/*  284 */     } catch (Exception exception) {
/*  285 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public String getString(String paramString) throws SQLException {
/*      */     try {
/*  300 */       debugCodeCall("getString", paramString);
/*  301 */       return get(getColumnIndex(paramString)).getString();
/*  302 */     } catch (Exception exception) {
/*  303 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public int getInt(int paramInt) throws SQLException {
/*      */     try {
/*  318 */       debugCodeCall("getInt", paramInt);
/*  319 */       return getIntInternal(checkColumnIndex(paramInt));
/*  320 */     } catch (Exception exception) {
/*  321 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public int getInt(String paramString) throws SQLException {
/*      */     try {
/*  336 */       debugCodeCall("getInt", paramString);
/*  337 */       return getIntInternal(getColumnIndex(paramString));
/*  338 */     } catch (Exception exception) {
/*  339 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private int getIntInternal(int paramInt) {
/*      */     boolean bool;
/*  344 */     Value value = getInternal(paramInt);
/*      */     
/*  346 */     if (value != ValueNull.INSTANCE) {
/*  347 */       this.wasNull = false;
/*  348 */       bool = value.getInt();
/*      */     } else {
/*  350 */       this.wasNull = true;
/*  351 */       bool = false;
/*      */     } 
/*  353 */     return bool;
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
/*      */   public BigDecimal getBigDecimal(int paramInt) throws SQLException {
/*      */     try {
/*  367 */       debugCodeCall("getBigDecimal", paramInt);
/*  368 */       return get(checkColumnIndex(paramInt)).getBigDecimal();
/*  369 */     } catch (Exception exception) {
/*  370 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(int paramInt) throws SQLException {
/*      */     try {
/*  390 */       debugCodeCall("getDate", paramInt);
/*  391 */       return LegacyDateTimeUtils.toDate(this.conn, null, get(checkColumnIndex(paramInt)));
/*  392 */     } catch (Exception exception) {
/*  393 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(int paramInt) throws SQLException {
/*      */     try {
/*  413 */       debugCodeCall("getTime", paramInt);
/*  414 */       return LegacyDateTimeUtils.toTime(this.conn, null, get(checkColumnIndex(paramInt)));
/*  415 */     } catch (Exception exception) {
/*  416 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(int paramInt) throws SQLException {
/*      */     try {
/*  436 */       debugCodeCall("getTimestamp", paramInt);
/*  437 */       return LegacyDateTimeUtils.toTimestamp(this.conn, null, get(checkColumnIndex(paramInt)));
/*  438 */     } catch (Exception exception) {
/*  439 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public BigDecimal getBigDecimal(String paramString) throws SQLException {
/*      */     try {
/*  454 */       debugCodeCall("getBigDecimal", paramString);
/*  455 */       return get(getColumnIndex(paramString)).getBigDecimal();
/*  456 */     } catch (Exception exception) {
/*  457 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(String paramString) throws SQLException {
/*      */     try {
/*  477 */       debugCodeCall("getDate", paramString);
/*  478 */       return LegacyDateTimeUtils.toDate(this.conn, null, get(getColumnIndex(paramString)));
/*  479 */     } catch (Exception exception) {
/*  480 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(String paramString) throws SQLException {
/*      */     try {
/*  500 */       debugCodeCall("getTime", paramString);
/*  501 */       return LegacyDateTimeUtils.toTime(this.conn, null, get(getColumnIndex(paramString)));
/*  502 */     } catch (Exception exception) {
/*  503 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(String paramString) throws SQLException {
/*      */     try {
/*  523 */       debugCodeCall("getTimestamp", paramString);
/*  524 */       return LegacyDateTimeUtils.toTimestamp(this.conn, null, get(getColumnIndex(paramString)));
/*  525 */     } catch (Exception exception) {
/*  526 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(int paramInt) throws SQLException {
/*      */     try {
/*  542 */       debugCodeCall("getObject", paramInt);
/*  543 */       return ValueToObjectConverter.valueToDefaultObject(get(checkColumnIndex(paramInt)), this.conn, true);
/*  544 */     } catch (Exception exception) {
/*  545 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(String paramString) throws SQLException {
/*      */     try {
/*  561 */       debugCodeCall("getObject", paramString);
/*  562 */       return ValueToObjectConverter.valueToDefaultObject(get(getColumnIndex(paramString)), this.conn, true);
/*  563 */     } catch (Exception exception) {
/*  564 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int paramInt) throws SQLException {
/*      */     try {
/*  579 */       debugCodeCall("getBoolean", paramInt);
/*  580 */       return getBooleanInternal(checkColumnIndex(paramInt));
/*  581 */     } catch (Exception exception) {
/*  582 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public boolean getBoolean(String paramString) throws SQLException {
/*      */     try {
/*  597 */       debugCodeCall("getBoolean", paramString);
/*  598 */       return getBooleanInternal(getColumnIndex(paramString));
/*  599 */     } catch (Exception exception) {
/*  600 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private boolean getBooleanInternal(int paramInt) {
/*      */     boolean bool;
/*  605 */     Value value = getInternal(paramInt);
/*      */     
/*  607 */     if (value != ValueNull.INSTANCE) {
/*  608 */       this.wasNull = false;
/*  609 */       bool = value.getBoolean();
/*      */     } else {
/*  611 */       this.wasNull = true;
/*  612 */       bool = false;
/*      */     } 
/*  614 */     return bool;
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
/*      */   public byte getByte(int paramInt) throws SQLException {
/*      */     try {
/*  628 */       debugCodeCall("getByte", paramInt);
/*  629 */       return getByteInternal(checkColumnIndex(paramInt));
/*  630 */     } catch (Exception exception) {
/*  631 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public byte getByte(String paramString) throws SQLException {
/*      */     try {
/*  646 */       debugCodeCall("getByte", paramString);
/*  647 */       return getByteInternal(getColumnIndex(paramString));
/*  648 */     } catch (Exception exception) {
/*  649 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private byte getByteInternal(int paramInt) {
/*      */     boolean bool;
/*  654 */     Value value = getInternal(paramInt);
/*      */     
/*  656 */     if (value != ValueNull.INSTANCE) {
/*  657 */       this.wasNull = false;
/*  658 */       bool = value.getByte();
/*      */     } else {
/*  660 */       this.wasNull = true;
/*  661 */       bool = false;
/*      */     } 
/*  663 */     return bool;
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
/*      */   public short getShort(int paramInt) throws SQLException {
/*      */     try {
/*  677 */       debugCodeCall("getShort", paramInt);
/*  678 */       return getShortInternal(checkColumnIndex(paramInt));
/*  679 */     } catch (Exception exception) {
/*  680 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public short getShort(String paramString) throws SQLException {
/*      */     try {
/*  695 */       debugCodeCall("getShort", paramString);
/*  696 */       return getShortInternal(getColumnIndex(paramString));
/*  697 */     } catch (Exception exception) {
/*  698 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private short getShortInternal(int paramInt) {
/*      */     boolean bool;
/*  703 */     Value value = getInternal(paramInt);
/*      */     
/*  705 */     if (value != ValueNull.INSTANCE) {
/*  706 */       this.wasNull = false;
/*  707 */       bool = value.getShort();
/*      */     } else {
/*  709 */       this.wasNull = true;
/*  710 */       bool = false;
/*      */     } 
/*  712 */     return bool;
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
/*      */   public long getLong(int paramInt) throws SQLException {
/*      */     try {
/*  726 */       debugCodeCall("getLong", paramInt);
/*  727 */       return getLongInternal(checkColumnIndex(paramInt));
/*  728 */     } catch (Exception exception) {
/*  729 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public long getLong(String paramString) throws SQLException {
/*      */     try {
/*  744 */       debugCodeCall("getLong", paramString);
/*  745 */       return getLongInternal(getColumnIndex(paramString));
/*  746 */     } catch (Exception exception) {
/*  747 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private long getLongInternal(int paramInt) {
/*      */     long l;
/*  752 */     Value value = getInternal(paramInt);
/*      */     
/*  754 */     if (value != ValueNull.INSTANCE) {
/*  755 */       this.wasNull = false;
/*  756 */       l = value.getLong();
/*      */     } else {
/*  758 */       this.wasNull = true;
/*  759 */       l = 0L;
/*      */     } 
/*  761 */     return l;
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
/*      */   public float getFloat(int paramInt) throws SQLException {
/*      */     try {
/*  775 */       debugCodeCall("getFloat", paramInt);
/*  776 */       return getFloatInternal(checkColumnIndex(paramInt));
/*  777 */     } catch (Exception exception) {
/*  778 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public float getFloat(String paramString) throws SQLException {
/*      */     try {
/*  793 */       debugCodeCall("getFloat", paramString);
/*  794 */       return getFloatInternal(getColumnIndex(paramString));
/*  795 */     } catch (Exception exception) {
/*  796 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private float getFloatInternal(int paramInt) {
/*      */     float f;
/*  801 */     Value value = getInternal(paramInt);
/*      */     
/*  803 */     if (value != ValueNull.INSTANCE) {
/*  804 */       this.wasNull = false;
/*  805 */       f = value.getFloat();
/*      */     } else {
/*  807 */       this.wasNull = true;
/*  808 */       f = 0.0F;
/*      */     } 
/*  810 */     return f;
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
/*      */   public double getDouble(int paramInt) throws SQLException {
/*      */     try {
/*  824 */       debugCodeCall("getDouble", paramInt);
/*  825 */       return getDoubleInternal(checkColumnIndex(paramInt));
/*  826 */     } catch (Exception exception) {
/*  827 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public double getDouble(String paramString) throws SQLException {
/*      */     try {
/*  842 */       debugCodeCall("getDouble", paramString);
/*  843 */       return getDoubleInternal(getColumnIndex(paramString));
/*  844 */     } catch (Exception exception) {
/*  845 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private double getDoubleInternal(int paramInt) {
/*      */     double d;
/*  850 */     Value value = getInternal(paramInt);
/*      */     
/*  852 */     if (value != ValueNull.INSTANCE) {
/*  853 */       this.wasNull = false;
/*  854 */       d = value.getDouble();
/*      */     } else {
/*  856 */       this.wasNull = true;
/*  857 */       d = 0.0D;
/*      */     } 
/*  859 */     return d;
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
/*      */   
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException {
/*      */     try {
/*  877 */       if (isDebugEnabled()) {
/*  878 */         debugCode("getBigDecimal(" + quote(paramString) + ", " + paramInt + ')');
/*      */       }
/*  880 */       if (paramInt < 0) {
/*  881 */         throw DbException.getInvalidValueException("scale", Integer.valueOf(paramInt));
/*      */       }
/*  883 */       BigDecimal bigDecimal = get(getColumnIndex(paramString)).getBigDecimal();
/*  884 */       return (bigDecimal == null) ? null : ValueNumeric.setScale(bigDecimal, paramInt);
/*  885 */     } catch (Exception exception) {
/*  886 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException {
/*      */     try {
/*  905 */       if (isDebugEnabled()) {
/*  906 */         debugCode("getBigDecimal(" + paramInt1 + ", " + paramInt2 + ')');
/*      */       }
/*  908 */       if (paramInt2 < 0) {
/*  909 */         throw DbException.getInvalidValueException("scale", Integer.valueOf(paramInt2));
/*      */       }
/*  911 */       BigDecimal bigDecimal = get(checkColumnIndex(paramInt1)).getBigDecimal();
/*  912 */       return (bigDecimal == null) ? null : ValueNumeric.setScale(bigDecimal, paramInt2);
/*  913 */     } catch (Exception exception) {
/*  914 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(int paramInt) throws SQLException {
/*  925 */     throw unsupported("unicodeStream");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public InputStream getUnicodeStream(String paramString) throws SQLException {
/*  935 */     throw unsupported("unicodeStream");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
/*  945 */     throw unsupported("map");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException {
/*  955 */     throw unsupported("map");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(int paramInt) throws SQLException {
/*  963 */     throw unsupported("ref");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Ref getRef(String paramString) throws SQLException {
/*  971 */     throw unsupported("ref");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException {
/*      */     try {
/*  992 */       if (isDebugEnabled()) {
/*  993 */         debugCode("getDate(" + paramInt + ", calendar)");
/*      */       }
/*  995 */       return LegacyDateTimeUtils.toDate(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, 
/*  996 */           get(checkColumnIndex(paramInt)));
/*  997 */     } catch (Exception exception) {
/*  998 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate(String paramString, Calendar paramCalendar) throws SQLException {
/*      */     try {
/* 1020 */       if (isDebugEnabled()) {
/* 1021 */         debugCode("getDate(" + quote(paramString) + ", calendar)");
/*      */       }
/* 1023 */       return LegacyDateTimeUtils.toDate(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, 
/* 1024 */           get(getColumnIndex(paramString)));
/* 1025 */     } catch (Exception exception) {
/* 1026 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException {
/*      */     try {
/* 1048 */       if (isDebugEnabled()) {
/* 1049 */         debugCode("getTime(" + paramInt + ", calendar)");
/*      */       }
/* 1051 */       return LegacyDateTimeUtils.toTime(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, 
/* 1052 */           get(checkColumnIndex(paramInt)));
/* 1053 */     } catch (Exception exception) {
/* 1054 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Time getTime(String paramString, Calendar paramCalendar) throws SQLException {
/*      */     try {
/* 1076 */       if (isDebugEnabled()) {
/* 1077 */         debugCode("getTime(" + quote(paramString) + ", calendar)");
/*      */       }
/* 1079 */       return LegacyDateTimeUtils.toTime(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, 
/* 1080 */           get(getColumnIndex(paramString)));
/* 1081 */     } catch (Exception exception) {
/* 1082 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException {
/*      */     try {
/* 1104 */       if (isDebugEnabled()) {
/* 1105 */         debugCode("getTimestamp(" + paramInt + ", calendar)");
/*      */       }
/* 1107 */       return LegacyDateTimeUtils.toTimestamp(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, 
/* 1108 */           get(checkColumnIndex(paramInt)));
/* 1109 */     } catch (Exception exception) {
/* 1110 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException {
/*      */     try {
/* 1131 */       if (isDebugEnabled()) {
/* 1132 */         debugCode("getTimestamp(" + quote(paramString) + ", calendar)");
/*      */       }
/* 1134 */       return LegacyDateTimeUtils.toTimestamp(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, 
/* 1135 */           get(getColumnIndex(paramString)));
/* 1136 */     } catch (Exception exception) {
/* 1137 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Blob getBlob(int paramInt) throws SQLException {
/*      */     try {
/* 1152 */       int i = getNextId(9);
/* 1153 */       if (isDebugEnabled()) {
/* 1154 */         debugCodeAssign("Blob", 9, i, "getBlob(" + paramInt + ')');
/*      */       }
/* 1156 */       return getBlob(i, checkColumnIndex(paramInt));
/* 1157 */     } catch (Exception exception) {
/* 1158 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Blob getBlob(String paramString) throws SQLException {
/*      */     try {
/* 1173 */       int i = getNextId(9);
/* 1174 */       if (isDebugEnabled()) {
/* 1175 */         debugCodeAssign("Blob", 9, i, "getBlob(" + quote(paramString) + ')');
/*      */       }
/* 1177 */       return getBlob(i, getColumnIndex(paramString));
/* 1178 */     } catch (Exception exception) {
/* 1179 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private JdbcBlob getBlob(int paramInt1, int paramInt2) {
/*      */     JdbcBlob jdbcBlob;
/* 1184 */     Value value = getInternal(paramInt2);
/*      */     
/* 1186 */     if (value != ValueNull.INSTANCE) {
/* 1187 */       this.wasNull = false;
/* 1188 */       jdbcBlob = new JdbcBlob(this.conn, value, JdbcLob.State.WITH_VALUE, paramInt1);
/*      */     } else {
/* 1190 */       this.wasNull = true;
/* 1191 */       jdbcBlob = null;
/*      */     } 
/* 1193 */     return jdbcBlob;
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
/*      */   public byte[] getBytes(int paramInt) throws SQLException {
/*      */     try {
/* 1207 */       debugCodeCall("getBytes", paramInt);
/* 1208 */       return get(checkColumnIndex(paramInt)).getBytes();
/* 1209 */     } catch (Exception exception) {
/* 1210 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public byte[] getBytes(String paramString) throws SQLException {
/*      */     try {
/* 1225 */       debugCodeCall("getBytes", paramString);
/* 1226 */       return get(getColumnIndex(paramString)).getBytes();
/* 1227 */     } catch (Exception exception) {
/* 1228 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public InputStream getBinaryStream(int paramInt) throws SQLException {
/*      */     try {
/* 1243 */       debugCodeCall("getBinaryStream", paramInt);
/* 1244 */       return get(checkColumnIndex(paramInt)).getInputStream();
/* 1245 */     } catch (Exception exception) {
/* 1246 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public InputStream getBinaryStream(String paramString) throws SQLException {
/*      */     try {
/* 1261 */       debugCodeCall("getBinaryStream", paramString);
/* 1262 */       return get(getColumnIndex(paramString)).getInputStream();
/* 1263 */     } catch (Exception exception) {
/* 1264 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public Clob getClob(int paramInt) throws SQLException {
/*      */     try {
/* 1280 */       int i = getNextId(10);
/* 1281 */       if (isDebugEnabled()) {
/* 1282 */         debugCodeAssign("Clob", 10, i, "getClob(" + paramInt + ')');
/*      */       }
/* 1284 */       return getClob(i, checkColumnIndex(paramInt));
/* 1285 */     } catch (Exception exception) {
/* 1286 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Clob getClob(String paramString) throws SQLException {
/*      */     try {
/* 1301 */       int i = getNextId(10);
/* 1302 */       if (isDebugEnabled()) {
/* 1303 */         debugCodeAssign("Clob", 10, i, "getClob(" + quote(paramString) + ')');
/*      */       }
/* 1305 */       return getClob(i, getColumnIndex(paramString));
/* 1306 */     } catch (Exception exception) {
/* 1307 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Array getArray(int paramInt) throws SQLException {
/*      */     try {
/* 1322 */       int i = getNextId(16);
/* 1323 */       if (isDebugEnabled()) {
/* 1324 */         debugCodeAssign("Array", 16, i, "getArray(" + paramInt + ')');
/*      */       }
/* 1326 */       return getArray(i, checkColumnIndex(paramInt));
/* 1327 */     } catch (Exception exception) {
/* 1328 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Array getArray(String paramString) throws SQLException {
/*      */     try {
/* 1343 */       int i = getNextId(16);
/* 1344 */       if (isDebugEnabled()) {
/* 1345 */         debugCodeAssign("Array", 16, i, "getArray(" + quote(paramString) + ')');
/*      */       }
/* 1347 */       return getArray(i, getColumnIndex(paramString));
/* 1348 */     } catch (Exception exception) {
/* 1349 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private Array getArray(int paramInt1, int paramInt2) {
/*      */     Array array;
/* 1354 */     Value value = getInternal(paramInt2);
/*      */     
/* 1356 */     if (value != ValueNull.INSTANCE) {
/* 1357 */       this.wasNull = false;
/* 1358 */       array = new JdbcArray(this.conn, value, paramInt1);
/*      */     } else {
/* 1360 */       this.wasNull = true;
/* 1361 */       array = null;
/*      */     } 
/* 1363 */     return array;
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
/*      */   public InputStream getAsciiStream(int paramInt) throws SQLException {
/*      */     try {
/* 1377 */       debugCodeCall("getAsciiStream", paramInt);
/* 1378 */       String str = get(checkColumnIndex(paramInt)).getString();
/* 1379 */       return (str == null) ? null : IOUtils.getInputStreamFromString(str);
/* 1380 */     } catch (Exception exception) {
/* 1381 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public InputStream getAsciiStream(String paramString) throws SQLException {
/*      */     try {
/* 1396 */       debugCodeCall("getAsciiStream", paramString);
/* 1397 */       String str = get(getColumnIndex(paramString)).getString();
/* 1398 */       return IOUtils.getInputStreamFromString(str);
/* 1399 */     } catch (Exception exception) {
/* 1400 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Reader getCharacterStream(int paramInt) throws SQLException {
/*      */     try {
/* 1415 */       debugCodeCall("getCharacterStream", paramInt);
/* 1416 */       return get(checkColumnIndex(paramInt)).getReader();
/* 1417 */     } catch (Exception exception) {
/* 1418 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Reader getCharacterStream(String paramString) throws SQLException {
/*      */     try {
/* 1433 */       debugCodeCall("getCharacterStream", paramString);
/* 1434 */       return get(getColumnIndex(paramString)).getReader();
/* 1435 */     } catch (Exception exception) {
/* 1436 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(int paramInt) throws SQLException {
/* 1445 */     throw unsupported("url");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public URL getURL(String paramString) throws SQLException {
/* 1453 */     throw unsupported("url");
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
/*      */   public void updateNull(int paramInt) throws SQLException {
/*      */     try {
/* 1467 */       debugCodeCall("updateNull", paramInt);
/* 1468 */       update(checkColumnIndex(paramInt), (Value)ValueNull.INSTANCE);
/* 1469 */     } catch (Exception exception) {
/* 1470 */       throw logAndConvert(exception);
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
/*      */   public void updateNull(String paramString) throws SQLException {
/*      */     try {
/* 1483 */       debugCodeCall("updateNull", paramString);
/* 1484 */       update(getColumnIndex(paramString), (Value)ValueNull.INSTANCE);
/* 1485 */     } catch (Exception exception) {
/* 1486 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException {
/*      */     try {
/* 1500 */       if (isDebugEnabled()) {
/* 1501 */         debugCode("updateBoolean(" + paramInt + ", " + paramBoolean + ')');
/*      */       }
/* 1503 */       update(checkColumnIndex(paramInt), (Value)ValueBoolean.get(paramBoolean));
/* 1504 */     } catch (Exception exception) {
/* 1505 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException {
/*      */     try {
/* 1519 */       if (isDebugEnabled()) {
/* 1520 */         debugCode("updateBoolean(" + quote(paramString) + ", " + paramBoolean + ')');
/*      */       }
/* 1522 */       update(getColumnIndex(paramString), (Value)ValueBoolean.get(paramBoolean));
/* 1523 */     } catch (Exception exception) {
/* 1524 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateByte(int paramInt, byte paramByte) throws SQLException {
/*      */     try {
/* 1538 */       if (isDebugEnabled()) {
/* 1539 */         debugCode("updateByte(" + paramInt + ", " + paramByte + ')');
/*      */       }
/* 1541 */       update(checkColumnIndex(paramInt), (Value)ValueTinyint.get(paramByte));
/* 1542 */     } catch (Exception exception) {
/* 1543 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateByte(String paramString, byte paramByte) throws SQLException {
/*      */     try {
/* 1557 */       if (isDebugEnabled()) {
/* 1558 */         debugCode("updateByte(" + quote(paramString) + ", " + paramByte + ')');
/*      */       }
/* 1560 */       update(getColumnIndex(paramString), (Value)ValueTinyint.get(paramByte));
/* 1561 */     } catch (Exception exception) {
/* 1562 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBytes(int paramInt, byte[] paramArrayOfbyte) throws SQLException {
/*      */     try {
/* 1576 */       if (isDebugEnabled()) {
/* 1577 */         debugCode("updateBytes(" + paramInt + ", x)");
/*      */       }
/* 1579 */       update(checkColumnIndex(paramInt), (paramArrayOfbyte == null) ? (Value)ValueNull.INSTANCE : (Value)ValueVarbinary.get(paramArrayOfbyte));
/* 1580 */     } catch (Exception exception) {
/* 1581 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBytes(String paramString, byte[] paramArrayOfbyte) throws SQLException {
/*      */     try {
/* 1595 */       if (isDebugEnabled()) {
/* 1596 */         debugCode("updateBytes(" + quote(paramString) + ", x)");
/*      */       }
/* 1598 */       update(getColumnIndex(paramString), (paramArrayOfbyte == null) ? (Value)ValueNull.INSTANCE : (Value)ValueVarbinary.get(paramArrayOfbyte));
/* 1599 */     } catch (Exception exception) {
/* 1600 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateShort(int paramInt, short paramShort) throws SQLException {
/*      */     try {
/* 1614 */       if (isDebugEnabled()) {
/* 1615 */         debugCode("updateShort(" + paramInt + ", (short) " + paramShort + ')');
/*      */       }
/* 1617 */       update(checkColumnIndex(paramInt), (Value)ValueSmallint.get(paramShort));
/* 1618 */     } catch (Exception exception) {
/* 1619 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateShort(String paramString, short paramShort) throws SQLException {
/*      */     try {
/* 1633 */       if (isDebugEnabled()) {
/* 1634 */         debugCode("updateShort(" + quote(paramString) + ", (short) " + paramShort + ')');
/*      */       }
/* 1636 */       update(getColumnIndex(paramString), (Value)ValueSmallint.get(paramShort));
/* 1637 */     } catch (Exception exception) {
/* 1638 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateInt(int paramInt1, int paramInt2) throws SQLException {
/*      */     try {
/* 1652 */       if (isDebugEnabled()) {
/* 1653 */         debugCode("updateInt(" + paramInt1 + ", " + paramInt2 + ')');
/*      */       }
/* 1655 */       update(checkColumnIndex(paramInt1), (Value)ValueInteger.get(paramInt2));
/* 1656 */     } catch (Exception exception) {
/* 1657 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateInt(String paramString, int paramInt) throws SQLException {
/*      */     try {
/* 1671 */       if (isDebugEnabled()) {
/* 1672 */         debugCode("updateInt(" + quote(paramString) + ", " + paramInt + ')');
/*      */       }
/* 1674 */       update(getColumnIndex(paramString), (Value)ValueInteger.get(paramInt));
/* 1675 */     } catch (Exception exception) {
/* 1676 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateLong(int paramInt, long paramLong) throws SQLException {
/*      */     try {
/* 1690 */       if (isDebugEnabled()) {
/* 1691 */         debugCode("updateLong(" + paramInt + ", " + paramLong + "L)");
/*      */       }
/* 1693 */       update(checkColumnIndex(paramInt), (Value)ValueBigint.get(paramLong));
/* 1694 */     } catch (Exception exception) {
/* 1695 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateLong(String paramString, long paramLong) throws SQLException {
/*      */     try {
/* 1709 */       if (isDebugEnabled()) {
/* 1710 */         debugCode("updateLong(" + quote(paramString) + ", " + paramLong + "L)");
/*      */       }
/* 1712 */       update(getColumnIndex(paramString), (Value)ValueBigint.get(paramLong));
/* 1713 */     } catch (Exception exception) {
/* 1714 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateFloat(int paramInt, float paramFloat) throws SQLException {
/*      */     try {
/* 1728 */       if (isDebugEnabled()) {
/* 1729 */         debugCode("updateFloat(" + paramInt + ", " + paramFloat + "f)");
/*      */       }
/* 1731 */       update(checkColumnIndex(paramInt), (Value)ValueReal.get(paramFloat));
/* 1732 */     } catch (Exception exception) {
/* 1733 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateFloat(String paramString, float paramFloat) throws SQLException {
/*      */     try {
/* 1747 */       if (isDebugEnabled()) {
/* 1748 */         debugCode("updateFloat(" + quote(paramString) + ", " + paramFloat + "f)");
/*      */       }
/* 1750 */       update(getColumnIndex(paramString), (Value)ValueReal.get(paramFloat));
/* 1751 */     } catch (Exception exception) {
/* 1752 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateDouble(int paramInt, double paramDouble) throws SQLException {
/*      */     try {
/* 1766 */       if (isDebugEnabled()) {
/* 1767 */         debugCode("updateDouble(" + paramInt + ", " + paramDouble + "d)");
/*      */       }
/* 1769 */       update(checkColumnIndex(paramInt), (Value)ValueDouble.get(paramDouble));
/* 1770 */     } catch (Exception exception) {
/* 1771 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateDouble(String paramString, double paramDouble) throws SQLException {
/*      */     try {
/* 1785 */       if (isDebugEnabled()) {
/* 1786 */         debugCode("updateDouble(" + quote(paramString) + ", " + paramDouble + "d)");
/*      */       }
/* 1788 */       update(getColumnIndex(paramString), (Value)ValueDouble.get(paramDouble));
/* 1789 */     } catch (Exception exception) {
/* 1790 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
/*      */     try {
/* 1804 */       if (isDebugEnabled()) {
/* 1805 */         debugCode("updateBigDecimal(" + paramInt + ", " + quoteBigDecimal(paramBigDecimal) + ')');
/*      */       }
/* 1807 */       update(checkColumnIndex(paramInt), (paramBigDecimal == null) ? (Value)ValueNull.INSTANCE : (Value)ValueNumeric.getAnyScale(paramBigDecimal));
/* 1808 */     } catch (Exception exception) {
/* 1809 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException {
/*      */     try {
/* 1823 */       if (isDebugEnabled()) {
/* 1824 */         debugCode("updateBigDecimal(" + quote(paramString) + ", " + quoteBigDecimal(paramBigDecimal) + ')');
/*      */       }
/* 1826 */       update(getColumnIndex(paramString), (paramBigDecimal == null) ? (Value)ValueNull.INSTANCE : (Value)ValueNumeric.getAnyScale(paramBigDecimal));
/* 1827 */     } catch (Exception exception) {
/* 1828 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateString(int paramInt, String paramString) throws SQLException {
/*      */     try {
/* 1842 */       if (isDebugEnabled()) {
/* 1843 */         debugCode("updateString(" + paramInt + ", " + quote(paramString) + ')');
/*      */       }
/* 1845 */       update(checkColumnIndex(paramInt), (paramString == null) ? (Value)ValueNull.INSTANCE : ValueVarchar.get(paramString, this.conn));
/* 1846 */     } catch (Exception exception) {
/* 1847 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateString(String paramString1, String paramString2) throws SQLException {
/*      */     try {
/* 1861 */       if (isDebugEnabled()) {
/* 1862 */         debugCode("updateString(" + quote(paramString1) + ", " + quote(paramString2) + ')');
/*      */       }
/* 1864 */       update(getColumnIndex(paramString1), (paramString2 == null) ? (Value)ValueNull.INSTANCE : ValueVarchar.get(paramString2, this.conn));
/* 1865 */     } catch (Exception exception) {
/* 1866 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDate(int paramInt, Date paramDate) throws SQLException {
/*      */     try {
/* 1886 */       if (isDebugEnabled()) {
/* 1887 */         debugCode("updateDate(" + paramInt + ", " + quoteDate(paramDate) + ')');
/*      */       }
/* 1889 */       update(checkColumnIndex(paramInt), (paramDate == null) ? (Value)ValueNull.INSTANCE : 
/* 1890 */           (Value)LegacyDateTimeUtils.fromDate(this.conn, null, paramDate));
/* 1891 */     } catch (Exception exception) {
/* 1892 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateDate(String paramString, Date paramDate) throws SQLException {
/*      */     try {
/* 1912 */       if (isDebugEnabled()) {
/* 1913 */         debugCode("updateDate(" + quote(paramString) + ", " + quoteDate(paramDate) + ')');
/*      */       }
/* 1915 */       update(getColumnIndex(paramString), (paramDate == null) ? (Value)ValueNull.INSTANCE : 
/* 1916 */           (Value)LegacyDateTimeUtils.fromDate(this.conn, null, paramDate));
/* 1917 */     } catch (Exception exception) {
/* 1918 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTime(int paramInt, Time paramTime) throws SQLException {
/*      */     try {
/* 1938 */       if (isDebugEnabled()) {
/* 1939 */         debugCode("updateTime(" + paramInt + ", " + quoteTime(paramTime) + ')');
/*      */       }
/* 1941 */       update(checkColumnIndex(paramInt), (paramTime == null) ? (Value)ValueNull.INSTANCE : 
/* 1942 */           (Value)LegacyDateTimeUtils.fromTime(this.conn, null, paramTime));
/* 1943 */     } catch (Exception exception) {
/* 1944 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTime(String paramString, Time paramTime) throws SQLException {
/*      */     try {
/* 1964 */       if (isDebugEnabled()) {
/* 1965 */         debugCode("updateTime(" + quote(paramString) + ", " + quoteTime(paramTime) + ')');
/*      */       }
/* 1967 */       update(getColumnIndex(paramString), (paramTime == null) ? (Value)ValueNull.INSTANCE : 
/* 1968 */           (Value)LegacyDateTimeUtils.fromTime(this.conn, null, paramTime));
/* 1969 */     } catch (Exception exception) {
/* 1970 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
/*      */     try {
/* 1990 */       if (isDebugEnabled()) {
/* 1991 */         debugCode("updateTimestamp(" + paramInt + ", " + quoteTimestamp(paramTimestamp) + ')');
/*      */       }
/* 1993 */       update(checkColumnIndex(paramInt), (paramTimestamp == null) ? (Value)ValueNull.INSTANCE : 
/* 1994 */           (Value)LegacyDateTimeUtils.fromTimestamp(this.conn, null, paramTimestamp));
/* 1995 */     } catch (Exception exception) {
/* 1996 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException {
/*      */     try {
/* 2016 */       if (isDebugEnabled()) {
/* 2017 */         debugCode("updateTimestamp(" + quote(paramString) + ", " + quoteTimestamp(paramTimestamp) + ')');
/*      */       }
/* 2019 */       update(getColumnIndex(paramString), (paramTimestamp == null) ? (Value)ValueNull.INSTANCE : 
/* 2020 */           (Value)LegacyDateTimeUtils.fromTimestamp(this.conn, null, paramTimestamp));
/* 2021 */     } catch (Exception exception) {
/* 2022 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
/*      */     try {
/* 2037 */       if (isDebugEnabled()) {
/* 2038 */         debugCode("updateAsciiStream(" + paramInt1 + ", x, " + paramInt2 + ')');
/*      */       }
/* 2040 */       updateAscii(checkColumnIndex(paramInt1), paramInputStream, paramInt2);
/* 2041 */     } catch (Exception exception) {
/* 2042 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException {
/*      */     try {
/* 2056 */       if (isDebugEnabled()) {
/* 2057 */         debugCode("updateAsciiStream(" + paramInt + ", x)");
/*      */       }
/* 2059 */       updateAscii(checkColumnIndex(paramInt), paramInputStream, -1L);
/* 2060 */     } catch (Exception exception) {
/* 2061 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 2076 */       if (isDebugEnabled()) {
/* 2077 */         debugCode("updateAsciiStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 2079 */       updateAscii(checkColumnIndex(paramInt), paramInputStream, paramLong);
/* 2080 */     } catch (Exception exception) {
/* 2081 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
/*      */     try {
/* 2096 */       if (isDebugEnabled()) {
/* 2097 */         debugCode("updateAsciiStream(" + quote(paramString) + ", x, " + paramInt + ')');
/*      */       }
/* 2099 */       updateAscii(getColumnIndex(paramString), paramInputStream, paramInt);
/* 2100 */     } catch (Exception exception) {
/* 2101 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream) throws SQLException {
/*      */     try {
/* 2115 */       if (isDebugEnabled()) {
/* 2116 */         debugCode("updateAsciiStream(" + quote(paramString) + ", x)");
/*      */       }
/* 2118 */       updateAscii(getColumnIndex(paramString), paramInputStream, -1L);
/* 2119 */     } catch (Exception exception) {
/* 2120 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 2135 */       if (isDebugEnabled()) {
/* 2136 */         debugCode("updateAsciiStream(" + quote(paramString) + ", x, " + paramLong + "L)");
/*      */       }
/* 2138 */       updateAscii(getColumnIndex(paramString), paramInputStream, paramLong);
/* 2139 */     } catch (Exception exception) {
/* 2140 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void updateAscii(int paramInt, InputStream paramInputStream, long paramLong) {
/* 2145 */     update(paramInt, this.conn.createClob(IOUtils.getAsciiReader(paramInputStream), paramLong));
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
/*      */   public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
/*      */     try {
/* 2159 */       if (isDebugEnabled()) {
/* 2160 */         debugCode("updateBinaryStream(" + paramInt1 + ", x, " + paramInt2 + ')');
/*      */       }
/* 2162 */       updateBlobImpl(checkColumnIndex(paramInt1), paramInputStream, paramInt2);
/* 2163 */     } catch (Exception exception) {
/* 2164 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException {
/*      */     try {
/* 2178 */       if (isDebugEnabled()) {
/* 2179 */         debugCode("updateBinaryStream(" + paramInt + ", x)");
/*      */       }
/* 2181 */       updateBlobImpl(checkColumnIndex(paramInt), paramInputStream, -1L);
/* 2182 */     } catch (Exception exception) {
/* 2183 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 2198 */       if (isDebugEnabled()) {
/* 2199 */         debugCode("updateBinaryStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 2201 */       updateBlobImpl(checkColumnIndex(paramInt), paramInputStream, paramLong);
/* 2202 */     } catch (Exception exception) {
/* 2203 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream) throws SQLException {
/*      */     try {
/* 2217 */       if (isDebugEnabled()) {
/* 2218 */         debugCode("updateBinaryStream(" + quote(paramString) + ", x)");
/*      */       }
/* 2220 */       updateBlobImpl(getColumnIndex(paramString), paramInputStream, -1L);
/* 2221 */     } catch (Exception exception) {
/* 2222 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException {
/*      */     try {
/* 2237 */       if (isDebugEnabled()) {
/* 2238 */         debugCode("updateBinaryStream(" + quote(paramString) + ", x, " + paramInt + ')');
/*      */       }
/* 2240 */       updateBlobImpl(getColumnIndex(paramString), paramInputStream, paramInt);
/* 2241 */     } catch (Exception exception) {
/* 2242 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 2257 */       if (isDebugEnabled()) {
/* 2258 */         debugCode("updateBinaryStream(" + quote(paramString) + ", x, " + paramLong + "L)");
/*      */       }
/* 2260 */       updateBlobImpl(getColumnIndex(paramString), paramInputStream, paramLong);
/* 2261 */     } catch (Exception exception) {
/* 2262 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 2277 */       if (isDebugEnabled()) {
/* 2278 */         debugCode("updateCharacterStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 2280 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, paramLong);
/* 2281 */     } catch (Exception exception) {
/* 2282 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
/*      */     try {
/* 2297 */       if (isDebugEnabled()) {
/* 2298 */         debugCode("updateCharacterStream(" + paramInt1 + ", x, " + paramInt2 + ')');
/*      */       }
/* 2300 */       updateClobImpl(checkColumnIndex(paramInt1), paramReader, paramInt2);
/* 2301 */     } catch (Exception exception) {
/* 2302 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateCharacterStream(int paramInt, Reader paramReader) throws SQLException {
/*      */     try {
/* 2316 */       if (isDebugEnabled()) {
/* 2317 */         debugCode("updateCharacterStream(" + paramInt + ", x)");
/*      */       }
/* 2319 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, -1L);
/* 2320 */     } catch (Exception exception) {
/* 2321 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException {
/*      */     try {
/* 2336 */       if (isDebugEnabled()) {
/* 2337 */         debugCode("updateCharacterStream(" + quote(paramString) + ", x, " + paramInt + ')');
/*      */       }
/* 2339 */       updateClobImpl(getColumnIndex(paramString), paramReader, paramInt);
/* 2340 */     } catch (Exception exception) {
/* 2341 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateCharacterStream(String paramString, Reader paramReader) throws SQLException {
/*      */     try {
/* 2355 */       if (isDebugEnabled()) {
/* 2356 */         debugCode("updateCharacterStream(" + quote(paramString) + ", x)");
/*      */       }
/* 2358 */       updateClobImpl(getColumnIndex(paramString), paramReader, -1L);
/* 2359 */     } catch (Exception exception) {
/* 2360 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 2375 */       if (isDebugEnabled()) {
/* 2376 */         debugCode("updateCharacterStream(" + quote(paramString) + ", x, " + paramLong + "L)");
/*      */       }
/* 2378 */       updateClobImpl(getColumnIndex(paramString), paramReader, paramLong);
/* 2379 */     } catch (Exception exception) {
/* 2380 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
/*      */     try {
/* 2395 */       if (isDebugEnabled()) {
/* 2396 */         debugCode("updateObject(" + paramInt1 + ", x, " + paramInt2 + ')');
/*      */       }
/* 2398 */       update(checkColumnIndex(paramInt1), convertToUnknownValue(paramObject));
/* 2399 */     } catch (Exception exception) {
/* 2400 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException {
/*      */     try {
/* 2415 */       if (isDebugEnabled()) {
/* 2416 */         debugCode("updateObject(" + quote(paramString) + ", x, " + paramInt + ')');
/*      */       }
/* 2418 */       update(getColumnIndex(paramString), convertToUnknownValue(paramObject));
/* 2419 */     } catch (Exception exception) {
/* 2420 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateObject(int paramInt, Object paramObject) throws SQLException {
/*      */     try {
/* 2434 */       if (isDebugEnabled()) {
/* 2435 */         debugCode("updateObject(" + paramInt + ", x)");
/*      */       }
/* 2437 */       update(checkColumnIndex(paramInt), convertToUnknownValue(paramObject));
/* 2438 */     } catch (Exception exception) {
/* 2439 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateObject(String paramString, Object paramObject) throws SQLException {
/*      */     try {
/* 2453 */       if (isDebugEnabled()) {
/* 2454 */         debugCode("updateObject(" + quote(paramString) + ", x)");
/*      */       }
/* 2456 */       update(getColumnIndex(paramString), convertToUnknownValue(paramObject));
/* 2457 */     } catch (Exception exception) {
/* 2458 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateObject(int paramInt, Object paramObject, SQLType paramSQLType) throws SQLException {
/*      */     try {
/* 2473 */       if (isDebugEnabled()) {
/* 2474 */         debugCode("updateObject(" + paramInt + ", x, " + DataType.sqlTypeToString(paramSQLType) + ')');
/*      */       }
/* 2476 */       update(checkColumnIndex(paramInt), convertToValue(paramObject, paramSQLType));
/* 2477 */     } catch (Exception exception) {
/* 2478 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateObject(int paramInt1, Object paramObject, SQLType paramSQLType, int paramInt2) throws SQLException {
/*      */     try {
/* 2494 */       if (isDebugEnabled()) {
/* 2495 */         debugCode("updateObject(" + paramInt1 + ", x, " + DataType.sqlTypeToString(paramSQLType) + ", " + paramInt2 + ')');
/*      */       }
/*      */       
/* 2498 */       update(checkColumnIndex(paramInt1), convertToValue(paramObject, paramSQLType));
/* 2499 */     } catch (Exception exception) {
/* 2500 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateObject(String paramString, Object paramObject, SQLType paramSQLType) throws SQLException {
/*      */     try {
/* 2515 */       if (isDebugEnabled()) {
/* 2516 */         debugCode("updateObject(" + quote(paramString) + ", x, " + DataType.sqlTypeToString(paramSQLType) + ')');
/*      */       }
/*      */       
/* 2519 */       update(getColumnIndex(paramString), convertToValue(paramObject, paramSQLType));
/* 2520 */     } catch (Exception exception) {
/* 2521 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateObject(String paramString, Object paramObject, SQLType paramSQLType, int paramInt) throws SQLException {
/*      */     try {
/* 2538 */       if (isDebugEnabled()) {
/* 2539 */         debugCode("updateObject(" + quote(paramString) + ", x, " + DataType.sqlTypeToString(paramSQLType) + ", " + paramInt + ')');
/*      */       }
/*      */       
/* 2542 */       update(getColumnIndex(paramString), convertToValue(paramObject, paramSQLType));
/* 2543 */     } catch (Exception exception) {
/* 2544 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRef(int paramInt, Ref paramRef) throws SQLException {
/* 2553 */     throw unsupported("ref");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRef(String paramString, Ref paramRef) throws SQLException {
/* 2561 */     throw unsupported("ref");
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
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream) throws SQLException {
/*      */     try {
/* 2574 */       if (isDebugEnabled()) {
/* 2575 */         debugCode("updateBlob(" + paramInt + ", (InputStream) x)");
/*      */       }
/* 2577 */       updateBlobImpl(checkColumnIndex(paramInt), paramInputStream, -1L);
/* 2578 */     } catch (Exception exception) {
/* 2579 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 2594 */       if (isDebugEnabled()) {
/* 2595 */         debugCode("updateBlob(" + paramInt + ", (InputStream) x, " + paramLong + "L)");
/*      */       }
/* 2597 */       updateBlobImpl(checkColumnIndex(paramInt), paramInputStream, paramLong);
/* 2598 */     } catch (Exception exception) {
/* 2599 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBlob(int paramInt, Blob paramBlob) throws SQLException {
/*      */     try {
/* 2613 */       if (isDebugEnabled()) {
/* 2614 */         debugCode("updateBlob(" + paramInt + ", (Blob) x)");
/*      */       }
/* 2616 */       updateBlobImpl(checkColumnIndex(paramInt), paramBlob, -1L);
/* 2617 */     } catch (Exception exception) {
/* 2618 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateBlob(String paramString, Blob paramBlob) throws SQLException {
/*      */     try {
/* 2632 */       if (isDebugEnabled()) {
/* 2633 */         debugCode("updateBlob(" + quote(paramString) + ", (Blob) x)");
/*      */       }
/* 2635 */       updateBlobImpl(getColumnIndex(paramString), paramBlob, -1L);
/* 2636 */     } catch (Exception exception) {
/* 2637 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void updateBlobImpl(int paramInt, Blob paramBlob, long paramLong) throws SQLException {
/* 2642 */     update(paramInt, (paramBlob == null) ? (Value)ValueNull.INSTANCE : this.conn.createBlob(paramBlob.getBinaryStream(), paramLong));
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
/*      */   public void updateBlob(String paramString, InputStream paramInputStream) throws SQLException {
/*      */     try {
/* 2655 */       if (isDebugEnabled()) {
/* 2656 */         debugCode("updateBlob(" + quote(paramString) + ", (InputStream) x)");
/*      */       }
/* 2658 */       updateBlobImpl(getColumnIndex(paramString), paramInputStream, -1L);
/* 2659 */     } catch (Exception exception) {
/* 2660 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 2675 */       if (isDebugEnabled()) {
/* 2676 */         debugCode("updateBlob(" + quote(paramString) + ", (InputStream) x, " + paramLong + "L)");
/*      */       }
/* 2678 */       updateBlobImpl(getColumnIndex(paramString), paramInputStream, paramLong);
/* 2679 */     } catch (Exception exception) {
/* 2680 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void updateBlobImpl(int paramInt, InputStream paramInputStream, long paramLong) {
/* 2685 */     update(paramInt, this.conn.createBlob(paramInputStream, paramLong));
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
/*      */   public void updateClob(int paramInt, Clob paramClob) throws SQLException {
/*      */     try {
/* 2698 */       if (isDebugEnabled()) {
/* 2699 */         debugCode("updateClob(" + paramInt + ", (Clob) x)");
/*      */       }
/* 2701 */       updateClobImpl(checkColumnIndex(paramInt), paramClob);
/* 2702 */     } catch (Exception exception) {
/* 2703 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateClob(int paramInt, Reader paramReader) throws SQLException {
/*      */     try {
/* 2717 */       if (isDebugEnabled()) {
/* 2718 */         debugCode("updateClob(" + paramInt + ", (Reader) x)");
/*      */       }
/* 2720 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, -1L);
/* 2721 */     } catch (Exception exception) {
/* 2722 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 2737 */       if (isDebugEnabled()) {
/* 2738 */         debugCode("updateClob(" + paramInt + ", (Reader) x, " + paramLong + "L)");
/*      */       }
/* 2740 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, paramLong);
/* 2741 */     } catch (Exception exception) {
/* 2742 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateClob(String paramString, Clob paramClob) throws SQLException {
/*      */     try {
/* 2756 */       if (isDebugEnabled()) {
/* 2757 */         debugCode("updateClob(" + quote(paramString) + ", (Clob) x)");
/*      */       }
/* 2759 */       updateClobImpl(getColumnIndex(paramString), paramClob);
/* 2760 */     } catch (Exception exception) {
/* 2761 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateClob(String paramString, Reader paramReader) throws SQLException {
/*      */     try {
/* 2775 */       if (isDebugEnabled()) {
/* 2776 */         debugCode("updateClob(" + quote(paramString) + ", (Reader) x)");
/*      */       }
/* 2778 */       updateClobImpl(getColumnIndex(paramString), paramReader, -1L);
/* 2779 */     } catch (Exception exception) {
/* 2780 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 2795 */       if (isDebugEnabled()) {
/* 2796 */         debugCode("updateClob(" + quote(paramString) + ", (Reader) x, " + paramLong + "L)");
/*      */       }
/* 2798 */       updateClobImpl(getColumnIndex(paramString), paramReader, paramLong);
/* 2799 */     } catch (Exception exception) {
/* 2800 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateArray(int paramInt, Array paramArray) throws SQLException {
/*      */     try {
/* 2814 */       if (isDebugEnabled()) {
/* 2815 */         debugCode("updateArray(" + paramInt + ", x)");
/*      */       }
/* 2817 */       updateArrayImpl(checkColumnIndex(paramInt), paramArray);
/* 2818 */     } catch (Exception exception) {
/* 2819 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateArray(String paramString, Array paramArray) throws SQLException {
/*      */     try {
/* 2833 */       if (isDebugEnabled()) {
/* 2834 */         debugCode("updateArray(" + quote(paramString) + ", x)");
/*      */       }
/* 2836 */       updateArrayImpl(getColumnIndex(paramString), paramArray);
/* 2837 */     } catch (Exception exception) {
/* 2838 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void updateArrayImpl(int paramInt, Array paramArray) throws SQLException {
/* 2843 */     update(paramInt, (paramArray == null) ? (Value)ValueNull.INSTANCE : 
/* 2844 */         ValueToObjectConverter.objectToValue(this.stat.session, paramArray.getArray(), 40));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCursorName() throws SQLException {
/* 2854 */     throw unsupported("cursorName");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getRow() throws SQLException {
/*      */     try {
/* 2866 */       debugCodeCall("getRow");
/* 2867 */       checkClosed();
/* 2868 */       if (this.result.isAfterLast()) {
/* 2869 */         return 0;
/*      */       }
/* 2871 */       long l = this.result.getRowId() + 1L;
/* 2872 */       return (l <= 2147483647L) ? (int)l : -2;
/* 2873 */     } catch (Exception exception) {
/* 2874 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public int getConcurrency() throws SQLException {
/*      */     try {
/* 2889 */       debugCodeCall("getConcurrency");
/* 2890 */       checkClosed();
/* 2891 */       if (!this.updatable) {
/* 2892 */         return 1007;
/*      */       }
/* 2894 */       UpdatableRow updatableRow = new UpdatableRow(this.conn, this.result);
/* 2895 */       return updatableRow.isUpdatable() ? 1008 : 1007;
/*      */     }
/* 2897 */     catch (Exception exception) {
/* 2898 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFetchDirection() throws SQLException {
/*      */     try {
/* 2910 */       debugCodeCall("getFetchDirection");
/* 2911 */       checkClosed();
/* 2912 */       return 1000;
/* 2913 */     } catch (Exception exception) {
/* 2914 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getFetchSize() throws SQLException {
/*      */     try {
/* 2926 */       debugCodeCall("getFetchSize");
/* 2927 */       checkClosed();
/* 2928 */       return this.result.getFetchSize();
/* 2929 */     } catch (Exception exception) {
/* 2930 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFetchSize(int paramInt) throws SQLException {
/*      */     try {
/* 2946 */       debugCodeCall("setFetchSize", paramInt);
/* 2947 */       checkClosed();
/* 2948 */       if (paramInt < 0)
/* 2949 */         throw DbException.getInvalidValueException("rows", Integer.valueOf(paramInt)); 
/* 2950 */       if (paramInt > 0) {
/* 2951 */         if (this.stat != null) {
/* 2952 */           int i = this.stat.getMaxRows();
/* 2953 */           if (i > 0 && paramInt > i) {
/* 2954 */             throw DbException.getInvalidValueException("rows", Integer.valueOf(paramInt));
/*      */           }
/*      */         } 
/*      */       } else {
/* 2958 */         paramInt = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
/*      */       } 
/* 2960 */       this.result.setFetchSize(paramInt);
/* 2961 */     } catch (Exception exception) {
/* 2962 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFetchDirection(int paramInt) throws SQLException {
/* 2978 */     debugCodeCall("setFetchDirection", paramInt);
/*      */     
/* 2980 */     if (paramInt != 1000) {
/* 2981 */       throw unsupported("setFetchDirection");
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
/*      */ 
/*      */   
/*      */   public int getType() throws SQLException {
/*      */     try {
/* 2996 */       debugCodeCall("getType");
/* 2997 */       checkClosed();
/* 2998 */       return (this.stat == null) ? 1003 : this.stat.resultSetType;
/* 2999 */     } catch (Exception exception) {
/* 3000 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public boolean isBeforeFirst() throws SQLException {
/*      */     try {
/* 3015 */       debugCodeCall("isBeforeFirst");
/* 3016 */       checkClosed();
/* 3017 */       return (this.result.getRowId() < 0L && this.result.hasNext());
/* 3018 */     } catch (Exception exception) {
/* 3019 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public boolean isAfterLast() throws SQLException {
/*      */     try {
/* 3034 */       debugCodeCall("isAfterLast");
/* 3035 */       checkClosed();
/* 3036 */       return (this.result.getRowId() > 0L && this.result.isAfterLast());
/* 3037 */     } catch (Exception exception) {
/* 3038 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public boolean isFirst() throws SQLException {
/*      */     try {
/* 3052 */       debugCodeCall("isFirst");
/* 3053 */       checkClosed();
/* 3054 */       return (this.result.getRowId() == 0L && !this.result.isAfterLast());
/* 3055 */     } catch (Exception exception) {
/* 3056 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public boolean isLast() throws SQLException {
/*      */     try {
/* 3070 */       debugCodeCall("isLast");
/* 3071 */       checkClosed();
/* 3072 */       long l = this.result.getRowId();
/* 3073 */       return (l >= 0L && !this.result.isAfterLast() && !this.result.hasNext());
/* 3074 */     } catch (Exception exception) {
/* 3075 */       throw logAndConvert(exception);
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
/*      */   public void beforeFirst() throws SQLException {
/*      */     try {
/* 3088 */       debugCodeCall("beforeFirst");
/* 3089 */       checkClosed();
/* 3090 */       if (this.result.getRowId() >= 0L) {
/* 3091 */         resetResult();
/*      */       }
/* 3093 */     } catch (Exception exception) {
/* 3094 */       throw logAndConvert(exception);
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
/*      */   public void afterLast() throws SQLException {
/*      */     try {
/* 3107 */       debugCodeCall("afterLast");
/* 3108 */       checkClosed();
/* 3109 */       while (nextRow());
/*      */     
/*      */     }
/* 3112 */     catch (Exception exception) {
/* 3113 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public boolean first() throws SQLException {
/*      */     try {
/* 3127 */       debugCodeCall("first");
/* 3128 */       checkClosed();
/* 3129 */       if (this.result.getRowId() >= 0L) {
/* 3130 */         resetResult();
/*      */       }
/* 3132 */       return nextRow();
/* 3133 */     } catch (Exception exception) {
/* 3134 */       throw logAndConvert(exception);
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
/*      */   public boolean last() throws SQLException {
/*      */     try {
/* 3147 */       debugCodeCall("last");
/* 3148 */       checkClosed();
/* 3149 */       if (this.result.isAfterLast()) {
/* 3150 */         resetResult();
/*      */       }
/* 3152 */       while (this.result.hasNext()) {
/* 3153 */         nextRow();
/*      */       }
/* 3155 */       return isOnValidRow();
/* 3156 */     } catch (Exception exception) {
/* 3157 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean absolute(int paramInt) throws SQLException {
/*      */     try {
/* 3175 */       debugCodeCall("absolute", paramInt);
/* 3176 */       checkClosed();
/* 3177 */       long l = (paramInt >= 0) ? paramInt : (this.result.getRowCount() + paramInt + 1L);
/* 3178 */       if (--l < this.result.getRowId()) {
/* 3179 */         resetResult();
/*      */       }
/* 3181 */       while (this.result.getRowId() < l) {
/* 3182 */         if (!nextRow()) {
/* 3183 */           return false;
/*      */         }
/*      */       } 
/* 3186 */       return isOnValidRow();
/* 3187 */     } catch (Exception exception) {
/* 3188 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean relative(int paramInt) throws SQLException {
/*      */     try {
/*      */       long l;
/* 3205 */       debugCodeCall("relative", paramInt);
/* 3206 */       checkClosed();
/*      */       
/* 3208 */       if (paramInt < 0) {
/* 3209 */         l = this.result.getRowId() + paramInt + 1L;
/* 3210 */         resetResult();
/*      */       } else {
/* 3212 */         l = paramInt;
/*      */       } 
/* 3214 */       while (l-- > 0L) {
/* 3215 */         if (!nextRow()) {
/* 3216 */           return false;
/*      */         }
/*      */       } 
/* 3219 */       return isOnValidRow();
/* 3220 */     } catch (Exception exception) {
/* 3221 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public boolean previous() throws SQLException {
/*      */     try {
/* 3235 */       debugCodeCall("previous");
/* 3236 */       checkClosed();
/* 3237 */       return relative(-1);
/* 3238 */     } catch (Exception exception) {
/* 3239 */       throw logAndConvert(exception);
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
/*      */   public void moveToInsertRow() throws SQLException {
/*      */     try {
/* 3252 */       debugCodeCall("moveToInsertRow");
/* 3253 */       checkUpdatable();
/* 3254 */       this.insertRow = new Value[this.columnCount];
/* 3255 */     } catch (Exception exception) {
/* 3256 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void moveToCurrentRow() throws SQLException {
/*      */     try {
/* 3268 */       debugCodeCall("moveToCurrentRow");
/* 3269 */       checkUpdatable();
/* 3270 */       this.insertRow = null;
/* 3271 */     } catch (Exception exception) {
/* 3272 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean rowUpdated() throws SQLException {
/*      */     try {
/* 3284 */       debugCodeCall("rowUpdated");
/* 3285 */       return false;
/* 3286 */     } catch (Exception exception) {
/* 3287 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean rowInserted() throws SQLException {
/*      */     try {
/* 3299 */       debugCodeCall("rowInserted");
/* 3300 */       return false;
/* 3301 */     } catch (Exception exception) {
/* 3302 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean rowDeleted() throws SQLException {
/*      */     try {
/* 3314 */       debugCodeCall("rowDeleted");
/* 3315 */       return false;
/* 3316 */     } catch (Exception exception) {
/* 3317 */       throw logAndConvert(exception);
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
/*      */   public void insertRow() throws SQLException {
/*      */     try {
/* 3330 */       debugCodeCall("insertRow");
/* 3331 */       checkUpdatable();
/* 3332 */       if (this.insertRow == null) {
/* 3333 */         throw DbException.get(90029);
/*      */       }
/* 3335 */       getUpdatableRow().insertRow(this.insertRow);
/* 3336 */       this.insertRow = null;
/* 3337 */     } catch (Exception exception) {
/* 3338 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateRow() throws SQLException {
/*      */     try {
/* 3352 */       debugCodeCall("updateRow");
/* 3353 */       checkUpdatable();
/* 3354 */       if (this.insertRow != null) {
/* 3355 */         throw DbException.get(90029);
/*      */       }
/* 3357 */       checkOnValidRow();
/* 3358 */       if (this.updateRow != null) {
/* 3359 */         UpdatableRow updatableRow = getUpdatableRow();
/* 3360 */         Value[] arrayOfValue1 = new Value[this.columnCount]; byte b;
/* 3361 */         for (b = 0; b < this.updateRow.length; b++) {
/* 3362 */           arrayOfValue1[b] = getInternal(checkColumnIndex(b + 1));
/*      */         }
/* 3364 */         updatableRow.updateRow(arrayOfValue1, this.updateRow);
/* 3365 */         for (b = 0; b < this.updateRow.length; b++) {
/* 3366 */           if (this.updateRow[b] == null) {
/* 3367 */             this.updateRow[b] = arrayOfValue1[b];
/*      */           }
/*      */         } 
/* 3370 */         Value[] arrayOfValue2 = updatableRow.readRow(this.updateRow);
/* 3371 */         patchCurrentRow(arrayOfValue2);
/* 3372 */         this.updateRow = null;
/*      */       } 
/* 3374 */     } catch (Exception exception) {
/* 3375 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void deleteRow() throws SQLException {
/*      */     try {
/* 3389 */       debugCodeCall("deleteRow");
/* 3390 */       checkUpdatable();
/* 3391 */       if (this.insertRow != null) {
/* 3392 */         throw DbException.get(90029);
/*      */       }
/* 3394 */       checkOnValidRow();
/* 3395 */       getUpdatableRow().deleteRow(this.result.currentRow());
/* 3396 */       this.updateRow = null;
/* 3397 */     } catch (Exception exception) {
/* 3398 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void refreshRow() throws SQLException {
/*      */     try {
/* 3412 */       debugCodeCall("refreshRow");
/* 3413 */       checkClosed();
/* 3414 */       if (this.insertRow != null) {
/* 3415 */         throw DbException.get(2000);
/*      */       }
/* 3417 */       checkOnValidRow();
/* 3418 */       patchCurrentRow(getUpdatableRow().readRow(this.result.currentRow()));
/* 3419 */       this.updateRow = null;
/* 3420 */     } catch (Exception exception) {
/* 3421 */       throw logAndConvert(exception);
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
/*      */   public void cancelRowUpdates() throws SQLException {
/*      */     try {
/* 3434 */       debugCodeCall("cancelRowUpdates");
/* 3435 */       checkClosed();
/* 3436 */       if (this.insertRow != null) {
/* 3437 */         throw DbException.get(2000);
/*      */       }
/* 3439 */       this.updateRow = null;
/* 3440 */     } catch (Exception exception) {
/* 3441 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private UpdatableRow getUpdatableRow() throws SQLException {
/* 3448 */     UpdatableRow updatableRow = new UpdatableRow(this.conn, this.result);
/* 3449 */     if (!updatableRow.isUpdatable()) {
/* 3450 */       throw DbException.get(90127);
/*      */     }
/* 3452 */     return updatableRow;
/*      */   }
/*      */   
/*      */   private int getColumnIndex(String paramString) {
/* 3456 */     checkClosed();
/* 3457 */     if (paramString == null) {
/* 3458 */       throw DbException.getInvalidValueException("columnLabel", null);
/*      */     }
/* 3460 */     if (this.columnCount >= 3) {
/*      */       
/* 3462 */       if (this.columnLabelMap == null) {
/* 3463 */         HashMap<Object, Object> hashMap = new HashMap<>();
/*      */         byte b;
/* 3465 */         for (b = 0; b < this.columnCount; b++) {
/* 3466 */           String str = StringUtils.toUpperEnglish(this.result.getAlias(b));
/*      */           
/* 3468 */           hashMap.putIfAbsent(str, Integer.valueOf(b));
/*      */         } 
/* 3470 */         for (b = 0; b < this.columnCount; b++) {
/* 3471 */           String str = this.result.getColumnName(b);
/* 3472 */           if (str != null) {
/* 3473 */             str = StringUtils.toUpperEnglish(str);
/*      */             
/* 3475 */             hashMap.putIfAbsent(str, Integer.valueOf(b));
/* 3476 */             String str1 = this.result.getTableName(b);
/* 3477 */             if (str1 != null) {
/* 3478 */               str = StringUtils.toUpperEnglish(str1) + '.' + str;
/*      */               
/* 3480 */               hashMap.putIfAbsent(str, Integer.valueOf(b));
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/* 3485 */         this.columnLabelMap = (HashMap)hashMap;
/* 3486 */         if (this.preparedStatement != null) {
/* 3487 */           this.preparedStatement.setCachedColumnLabelMap(this.columnLabelMap);
/*      */         }
/*      */       } 
/* 3490 */       Integer integer = this.columnLabelMap.get(StringUtils.toUpperEnglish(paramString));
/* 3491 */       if (integer == null) {
/* 3492 */         throw DbException.get(42122, paramString);
/*      */       }
/* 3494 */       return integer.intValue() + 1;
/*      */     }  int i;
/* 3496 */     for (i = 0; i < this.columnCount; i++) {
/* 3497 */       if (paramString.equalsIgnoreCase(this.result.getAlias(i))) {
/* 3498 */         return i + 1;
/*      */       }
/*      */     } 
/* 3501 */     i = paramString.indexOf('.');
/* 3502 */     if (i > 0) {
/* 3503 */       String str1 = paramString.substring(0, i);
/* 3504 */       String str2 = paramString.substring(i + 1);
/* 3505 */       for (byte b = 0; b < this.columnCount; b++) {
/* 3506 */         if (str1.equalsIgnoreCase(this.result.getTableName(b)) && str2
/* 3507 */           .equalsIgnoreCase(this.result.getColumnName(b))) {
/* 3508 */           return b + 1;
/*      */         }
/*      */       } 
/*      */     } else {
/* 3512 */       for (byte b = 0; b < this.columnCount; b++) {
/* 3513 */         if (paramString.equalsIgnoreCase(this.result.getColumnName(b))) {
/* 3514 */           return b + 1;
/*      */         }
/*      */       } 
/*      */     } 
/* 3518 */     throw DbException.get(42122, paramString);
/*      */   }
/*      */   
/*      */   private int checkColumnIndex(int paramInt) {
/* 3522 */     checkClosed();
/* 3523 */     if (paramInt < 1 || paramInt > this.columnCount) {
/* 3524 */       throw DbException.getInvalidValueException("columnIndex", Integer.valueOf(paramInt));
/*      */     }
/* 3526 */     return paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void checkClosed() {
/* 3535 */     if (this.result == null) {
/* 3536 */       throw DbException.get(90007);
/*      */     }
/* 3538 */     if (this.stat != null) {
/* 3539 */       this.stat.checkClosed();
/*      */     }
/* 3541 */     if (this.conn != null) {
/* 3542 */       this.conn.checkClosed();
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isOnValidRow() {
/* 3547 */     return (this.result.getRowId() >= 0L && !this.result.isAfterLast());
/*      */   }
/*      */   
/*      */   private void checkOnValidRow() {
/* 3551 */     if (!isOnValidRow()) {
/* 3552 */       throw DbException.get(2000);
/*      */     }
/*      */   }
/*      */   
/*      */   private Value get(int paramInt) {
/* 3557 */     Value value = getInternal(paramInt);
/* 3558 */     this.wasNull = (value == ValueNull.INSTANCE);
/* 3559 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value getInternal(int paramInt) {
/* 3570 */     checkOnValidRow();
/*      */     Value[] arrayOfValue;
/* 3572 */     if (this.patchedRows == null || (arrayOfValue = this.patchedRows.get(Long.valueOf(this.result.getRowId()))) == null) {
/* 3573 */       arrayOfValue = this.result.currentRow();
/*      */     }
/* 3575 */     return arrayOfValue[paramInt - 1];
/*      */   }
/*      */   
/*      */   private void update(int paramInt, Value paramValue) {
/* 3579 */     if (!this.triggerUpdatable) {
/* 3580 */       checkUpdatable();
/*      */     }
/* 3582 */     if (this.insertRow != null) {
/* 3583 */       this.insertRow[paramInt - 1] = paramValue;
/*      */     } else {
/* 3585 */       if (this.updateRow == null) {
/* 3586 */         this.updateRow = new Value[this.columnCount];
/*      */       }
/* 3588 */       this.updateRow[paramInt - 1] = paramValue;
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean nextRow() {
/* 3593 */     boolean bool = this.result.isLazy() ? nextLazyRow() : this.result.next();
/* 3594 */     if (!bool && !this.scrollable) {
/* 3595 */       this.result.close();
/*      */     }
/* 3597 */     return bool;
/*      */   }
/*      */   private boolean nextLazyRow() {
/*      */     boolean bool;
/*      */     Session session1;
/* 3602 */     if (this.stat.isCancelled() || this.conn == null || (session1 = this.conn.getSession()) == null) {
/* 3603 */       throw DbException.get(57014);
/*      */     }
/* 3605 */     Session session2 = session1.setThreadLocalSession();
/*      */     
/*      */     try {
/* 3608 */       bool = this.result.next();
/*      */     } finally {
/* 3610 */       session1.resetThreadLocalSession(session2);
/*      */     } 
/* 3612 */     return bool;
/*      */   }
/*      */   
/*      */   private void resetResult() {
/* 3616 */     if (!this.scrollable) {
/* 3617 */       throw DbException.get(90128);
/*      */     }
/* 3619 */     this.result.reset();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowId getRowId(int paramInt) throws SQLException {
/* 3629 */     throw unsupported("rowId");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowId getRowId(String paramString) throws SQLException {
/* 3639 */     throw unsupported("rowId");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRowId(int paramInt, RowId paramRowId) throws SQLException {
/* 3650 */     throw unsupported("rowId");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRowId(String paramString, RowId paramRowId) throws SQLException {
/* 3661 */     throw unsupported("rowId");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHoldability() throws SQLException {
/*      */     try {
/* 3673 */       debugCodeCall("getHoldability");
/* 3674 */       checkClosed();
/* 3675 */       return this.conn.getHoldability();
/* 3676 */     } catch (Exception exception) {
/* 3677 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClosed() throws SQLException {
/*      */     try {
/* 3689 */       debugCodeCall("isClosed");
/* 3690 */       return (this.result == null);
/* 3691 */     } catch (Exception exception) {
/* 3692 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNString(int paramInt, String paramString) throws SQLException {
/*      */     try {
/* 3706 */       if (isDebugEnabled()) {
/* 3707 */         debugCode("updateNString(" + paramInt + ", " + quote(paramString) + ')');
/*      */       }
/* 3709 */       update(checkColumnIndex(paramInt), (paramString == null) ? (Value)ValueNull.INSTANCE : ValueVarchar.get(paramString, this.conn));
/* 3710 */     } catch (Exception exception) {
/* 3711 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNString(String paramString1, String paramString2) throws SQLException {
/*      */     try {
/* 3725 */       if (isDebugEnabled()) {
/* 3726 */         debugCode("updateNString(" + quote(paramString1) + ", " + quote(paramString2) + ')');
/*      */       }
/* 3728 */       update(getColumnIndex(paramString1), (paramString2 == null) ? (Value)ValueNull.INSTANCE : ValueVarchar.get(paramString2, this.conn));
/* 3729 */     } catch (Exception exception) {
/* 3730 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNClob(int paramInt, NClob paramNClob) throws SQLException {
/*      */     try {
/* 3744 */       if (isDebugEnabled()) {
/* 3745 */         debugCode("updateNClob(" + paramInt + ", (NClob) x)");
/*      */       }
/* 3747 */       updateClobImpl(checkColumnIndex(paramInt), paramNClob);
/* 3748 */     } catch (Exception exception) {
/* 3749 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNClob(int paramInt, Reader paramReader) throws SQLException {
/*      */     try {
/* 3763 */       if (isDebugEnabled()) {
/* 3764 */         debugCode("updateNClob(" + paramInt + ", (Reader) x)");
/*      */       }
/* 3766 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, -1L);
/* 3767 */     } catch (Exception exception) {
/* 3768 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 3783 */       if (isDebugEnabled()) {
/* 3784 */         debugCode("updateNClob(" + paramInt + ", (Reader) x, " + paramLong + "L)");
/*      */       }
/* 3786 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, paramLong);
/* 3787 */     } catch (Exception exception) {
/* 3788 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNClob(String paramString, Reader paramReader) throws SQLException {
/*      */     try {
/* 3802 */       if (isDebugEnabled()) {
/* 3803 */         debugCode("updateNClob(" + quote(paramString) + ", (Reader) x)");
/*      */       }
/* 3805 */       updateClobImpl(getColumnIndex(paramString), paramReader, -1L);
/* 3806 */     } catch (Exception exception) {
/* 3807 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateNClob(String paramString, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 3822 */       if (isDebugEnabled()) {
/* 3823 */         debugCode("updateNClob(" + quote(paramString) + ", (Reader) x, " + paramLong + "L)");
/*      */       }
/* 3825 */       updateClobImpl(getColumnIndex(paramString), paramReader, paramLong);
/* 3826 */     } catch (Exception exception) {
/* 3827 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNClob(String paramString, NClob paramNClob) throws SQLException {
/*      */     try {
/* 3841 */       if (isDebugEnabled()) {
/* 3842 */         debugCode("updateNClob(" + quote(paramString) + ", (NClob) x)");
/*      */       }
/* 3844 */       updateClobImpl(getColumnIndex(paramString), paramNClob);
/* 3845 */     } catch (Exception exception) {
/* 3846 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void updateClobImpl(int paramInt, Clob paramClob) throws SQLException {
/* 3851 */     update(paramInt, (paramClob == null) ? (Value)ValueNull.INSTANCE : this.conn.createClob(paramClob.getCharacterStream(), -1L));
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
/*      */   public NClob getNClob(int paramInt) throws SQLException {
/*      */     try {
/* 3865 */       int i = getNextId(10);
/* 3866 */       if (isDebugEnabled()) {
/* 3867 */         debugCodeAssign("NClob", 10, i, "getNClob(" + paramInt + ')');
/*      */       }
/* 3869 */       return getClob(i, checkColumnIndex(paramInt));
/* 3870 */     } catch (Exception exception) {
/* 3871 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public NClob getNClob(String paramString) throws SQLException {
/*      */     try {
/* 3886 */       int i = getNextId(10);
/* 3887 */       if (isDebugEnabled()) {
/* 3888 */         debugCodeAssign("NClob", 10, i, "getNClob(" + quote(paramString) + ')');
/*      */       }
/* 3890 */       return getClob(i, getColumnIndex(paramString));
/* 3891 */     } catch (Exception exception) {
/* 3892 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private JdbcClob getClob(int paramInt1, int paramInt2) {
/*      */     JdbcClob jdbcClob;
/* 3897 */     Value value = getInternal(paramInt2);
/*      */     
/* 3899 */     if (value != ValueNull.INSTANCE) {
/* 3900 */       this.wasNull = false;
/* 3901 */       jdbcClob = new JdbcClob(this.conn, value, JdbcLob.State.WITH_VALUE, paramInt1);
/*      */     } else {
/* 3903 */       this.wasNull = true;
/* 3904 */       jdbcClob = null;
/*      */     } 
/* 3906 */     return jdbcClob;
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
/*      */   public SQLXML getSQLXML(int paramInt) throws SQLException {
/*      */     try {
/* 3920 */       int i = getNextId(17);
/* 3921 */       if (isDebugEnabled()) {
/* 3922 */         debugCodeAssign("SQLXML", 17, i, "getSQLXML(" + paramInt + ')');
/*      */       }
/* 3924 */       Value value = get(checkColumnIndex(paramInt));
/* 3925 */       return (value == ValueNull.INSTANCE) ? null : new JdbcSQLXML(this.conn, value, JdbcLob.State.WITH_VALUE, i);
/* 3926 */     } catch (Exception exception) {
/* 3927 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public SQLXML getSQLXML(String paramString) throws SQLException {
/*      */     try {
/* 3942 */       int i = getNextId(17);
/* 3943 */       if (isDebugEnabled()) {
/* 3944 */         debugCodeAssign("SQLXML", 17, i, "getSQLXML(" + quote(paramString) + ')');
/*      */       }
/* 3946 */       Value value = get(getColumnIndex(paramString));
/* 3947 */       return (value == ValueNull.INSTANCE) ? null : new JdbcSQLXML(this.conn, value, JdbcLob.State.WITH_VALUE, i);
/* 3948 */     } catch (Exception exception) {
/* 3949 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException {
/*      */     try {
/* 3963 */       if (isDebugEnabled()) {
/* 3964 */         debugCode("updateSQLXML(" + paramInt + ", x)");
/*      */       }
/* 3966 */       updateSQLXMLImpl(checkColumnIndex(paramInt), paramSQLXML);
/* 3967 */     } catch (Exception exception) {
/* 3968 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException {
/*      */     try {
/* 3982 */       if (isDebugEnabled()) {
/* 3983 */         debugCode("updateSQLXML(" + quote(paramString) + ", x)");
/*      */       }
/* 3985 */       updateSQLXMLImpl(getColumnIndex(paramString), paramSQLXML);
/* 3986 */     } catch (Exception exception) {
/* 3987 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void updateSQLXMLImpl(int paramInt, SQLXML paramSQLXML) throws SQLException {
/* 3992 */     update(paramInt, (paramSQLXML == null) ? (Value)ValueNull.INSTANCE : this.conn
/* 3993 */         .createClob(paramSQLXML.getCharacterStream(), -1L));
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
/*      */   public String getNString(int paramInt) throws SQLException {
/*      */     try {
/* 4007 */       debugCodeCall("getNString", paramInt);
/* 4008 */       return get(checkColumnIndex(paramInt)).getString();
/* 4009 */     } catch (Exception exception) {
/* 4010 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public String getNString(String paramString) throws SQLException {
/*      */     try {
/* 4025 */       debugCodeCall("getNString", paramString);
/* 4026 */       return get(getColumnIndex(paramString)).getString();
/* 4027 */     } catch (Exception exception) {
/* 4028 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Reader getNCharacterStream(int paramInt) throws SQLException {
/*      */     try {
/* 4043 */       debugCodeCall("getNCharacterStream", paramInt);
/* 4044 */       return get(checkColumnIndex(paramInt)).getReader();
/* 4045 */     } catch (Exception exception) {
/* 4046 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public Reader getNCharacterStream(String paramString) throws SQLException {
/*      */     try {
/* 4061 */       debugCodeCall("getNCharacterStream", paramString);
/* 4062 */       return get(getColumnIndex(paramString)).getReader();
/* 4063 */     } catch (Exception exception) {
/* 4064 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNCharacterStream(int paramInt, Reader paramReader) throws SQLException {
/*      */     try {
/* 4078 */       if (isDebugEnabled()) {
/* 4079 */         debugCode("updateNCharacterStream(" + paramInt + ", x)");
/*      */       }
/* 4081 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, -1L);
/* 4082 */     } catch (Exception exception) {
/* 4083 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 4098 */       if (isDebugEnabled()) {
/* 4099 */         debugCode("updateNCharacterStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 4101 */       updateClobImpl(checkColumnIndex(paramInt), paramReader, paramLong);
/* 4102 */     } catch (Exception exception) {
/* 4103 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader) throws SQLException {
/*      */     try {
/* 4117 */       if (isDebugEnabled()) {
/* 4118 */         debugCode("updateNCharacterStream(" + quote(paramString) + ", x)");
/*      */       }
/* 4120 */       updateClobImpl(getColumnIndex(paramString), paramReader, -1L);
/* 4121 */     } catch (Exception exception) {
/* 4122 */       throw logAndConvert(exception);
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
/*      */ 
/*      */   
/*      */   public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 4137 */       if (isDebugEnabled()) {
/* 4138 */         debugCode("updateNCharacterStream(" + quote(paramString) + ", x, " + paramLong + "L)");
/*      */       }
/* 4140 */       updateClobImpl(getColumnIndex(paramString), paramReader, paramLong);
/* 4141 */     } catch (Exception exception) {
/* 4142 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void updateClobImpl(int paramInt, Reader paramReader, long paramLong) {
/* 4147 */     update(paramInt, this.conn.createClob(paramReader, paramLong));
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
/* 4160 */       if (isWrapperFor(paramClass)) {
/* 4161 */         return (T)this;
/*      */       }
/* 4163 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 4164 */     } catch (Exception exception) {
/* 4165 */       throw logAndConvert(exception);
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
/* 4177 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
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
/*      */   public <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException {
/*      */     try {
/* 4192 */       if (paramClass == null) {
/* 4193 */         throw DbException.getInvalidValueException("type", paramClass);
/*      */       }
/* 4195 */       debugCodeCall("getObject", paramInt);
/* 4196 */       return (T)ValueToObjectConverter.valueToObject(paramClass, get(checkColumnIndex(paramInt)), this.conn);
/* 4197 */     } catch (Exception exception) {
/* 4198 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public <T> T getObject(String paramString, Class<T> paramClass) throws SQLException {
/*      */     try {
/* 4212 */       if (paramClass == null) {
/* 4213 */         throw DbException.getInvalidValueException("type", paramClass);
/*      */       }
/* 4215 */       debugCodeCall("getObject", paramString);
/* 4216 */       return (T)ValueToObjectConverter.valueToObject(paramClass, get(getColumnIndex(paramString)), this.conn);
/* 4217 */     } catch (Exception exception) {
/* 4218 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 4227 */     return getTraceObjectName() + ": " + this.result;
/*      */   }
/*      */   
/*      */   private void patchCurrentRow(Value[] paramArrayOfValue) {
/* 4231 */     boolean bool = false;
/* 4232 */     Value[] arrayOfValue = this.result.currentRow();
/* 4233 */     CompareMode compareMode = this.conn.getCompareMode();
/* 4234 */     for (byte b = 0; b < paramArrayOfValue.length; b++) {
/* 4235 */       if (paramArrayOfValue[b].compareTo(arrayOfValue[b], this.conn, compareMode) != 0) {
/* 4236 */         bool = true;
/*      */         break;
/*      */       } 
/*      */     } 
/* 4240 */     if (this.patchedRows == null) {
/* 4241 */       this.patchedRows = (HashMap)new HashMap<>();
/*      */     }
/* 4243 */     Long long_ = Long.valueOf(this.result.getRowId());
/* 4244 */     if (!bool) {
/* 4245 */       this.patchedRows.remove(long_);
/*      */     } else {
/* 4247 */       this.patchedRows.put(long_, paramArrayOfValue);
/*      */     } 
/*      */   }
/*      */   
/*      */   private Value convertToValue(Object paramObject, SQLType paramSQLType) {
/* 4252 */     if (paramObject == null) {
/* 4253 */       return (Value)ValueNull.INSTANCE;
/*      */     }
/* 4255 */     int i = DataType.convertSQLTypeToValueType(paramSQLType);
/* 4256 */     Value value = ValueToObjectConverter.objectToValue(this.conn.getSession(), paramObject, i);
/* 4257 */     return value.convertTo(i, this.conn);
/*      */   }
/*      */ 
/*      */   
/*      */   private Value convertToUnknownValue(Object paramObject) {
/* 4262 */     return ValueToObjectConverter.objectToValue(this.conn.getSession(), paramObject, -1);
/*      */   }
/*      */   
/*      */   private void checkUpdatable() {
/* 4266 */     checkClosed();
/* 4267 */     if (!this.updatable) {
/* 4268 */       throw DbException.get(90140);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value[] getUpdateRow() {
/* 4278 */     return this.updateRow;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getResult() {
/* 4287 */     return this.result;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcResultSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */