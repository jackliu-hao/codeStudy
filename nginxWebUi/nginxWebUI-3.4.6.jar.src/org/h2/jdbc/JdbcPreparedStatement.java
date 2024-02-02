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
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.RowId;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLType;
/*      */ import java.sql.SQLXML;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import org.h2.command.CommandInterface;
/*      */ import org.h2.expression.ParameterInterface;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.result.MergedResult;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.result.ResultWithGeneratedKeys;
/*      */ import org.h2.util.IOUtils;
/*      */ import org.h2.util.LegacyDateTimeUtils;
/*      */ import org.h2.util.Utils;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class JdbcPreparedStatement
/*      */   extends JdbcStatement
/*      */   implements PreparedStatement
/*      */ {
/*      */   protected CommandInterface command;
/*      */   private ArrayList<Value[]> batchParameters;
/*      */   private MergedResult batchIdentities;
/*      */   private HashMap<String, Integer> cachedColumnLabelMap;
/*      */   private final Object generatedKeysRequest;
/*      */   
/*      */   JdbcPreparedStatement(JdbcConnection paramJdbcConnection, String paramString, int paramInt1, int paramInt2, int paramInt3, Object paramObject) {
/*   89 */     super(paramJdbcConnection, paramInt1, paramInt2, paramInt3);
/*   90 */     this.generatedKeysRequest = paramObject;
/*   91 */     setTrace(this.session.getTrace(), 3, paramInt1);
/*   92 */     this.command = paramJdbcConnection.prepareCommand(paramString, this.fetchSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setCachedColumnLabelMap(HashMap<String, Integer> paramHashMap) {
/*  102 */     this.cachedColumnLabelMap = paramHashMap;
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
/*      */   public ResultSet executeQuery() throws SQLException {
/*      */     try {
/*  116 */       int i = getNextId(4);
/*  117 */       debugCodeAssign("ResultSet", 4, i, "executeQuery()");
/*  118 */       this.batchIdentities = null;
/*  119 */       synchronized (this.session) {
/*  120 */         ResultInterface resultInterface; checkClosed();
/*  121 */         closeOldResultSet();
/*      */         
/*  123 */         boolean bool = false;
/*  124 */         boolean bool1 = (this.resultSetType != 1003) ? true : false;
/*  125 */         boolean bool2 = (this.resultSetConcurrency == 1008) ? true : false;
/*      */         try {
/*  127 */           setExecutingStatement(this.command);
/*  128 */           resultInterface = this.command.executeQuery(this.maxRows, bool1);
/*  129 */           bool = resultInterface.isLazy();
/*      */         } finally {
/*  131 */           if (!bool) {
/*  132 */             setExecutingStatement((CommandInterface)null);
/*      */           }
/*      */         } 
/*  135 */         this.resultSet = new JdbcResultSet(this.conn, this, this.command, resultInterface, i, bool1, bool2, this.cachedColumnLabelMap);
/*      */       } 
/*      */       
/*  138 */       return this.resultSet;
/*  139 */     } catch (Exception exception) {
/*  140 */       throw logAndConvert(exception);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int executeUpdate() throws SQLException {
/*      */     try {
/*  166 */       debugCodeCall("executeUpdate");
/*  167 */       checkClosed();
/*  168 */       this.batchIdentities = null;
/*  169 */       long l = executeUpdateInternal();
/*  170 */       return (l <= 2147483647L) ? (int)l : -2;
/*  171 */     } catch (Exception exception) {
/*  172 */       throw logAndConvert(exception);
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
/*      */   
/*      */   public long executeLargeUpdate() throws SQLException {
/*      */     try {
/*  195 */       debugCodeCall("executeLargeUpdate");
/*  196 */       checkClosed();
/*  197 */       this.batchIdentities = null;
/*  198 */       return executeUpdateInternal();
/*  199 */     } catch (Exception exception) {
/*  200 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private long executeUpdateInternal() {
/*  205 */     closeOldResultSet();
/*  206 */     synchronized (this.session) {
/*      */       try {
/*  208 */         setExecutingStatement(this.command);
/*  209 */         ResultWithGeneratedKeys resultWithGeneratedKeys = this.command.executeUpdate(this.generatedKeysRequest);
/*  210 */         this.updateCount = resultWithGeneratedKeys.getUpdateCount();
/*  211 */         ResultInterface resultInterface = resultWithGeneratedKeys.getGeneratedKeys();
/*  212 */         if (resultInterface != null) {
/*  213 */           int i = getNextId(4);
/*  214 */           this.generatedKeys = new JdbcResultSet(this.conn, this, this.command, resultInterface, i, true, false, false);
/*      */         } 
/*      */       } finally {
/*  217 */         setExecutingStatement((CommandInterface)null);
/*      */       } 
/*      */     } 
/*  220 */     return this.updateCount;
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
/*      */   public boolean execute() throws SQLException {
/*      */     try {
/*      */       boolean bool;
/*  235 */       int i = getNextId(4);
/*  236 */       debugCodeCall("execute");
/*  237 */       checkClosed();
/*      */       
/*  239 */       synchronized (this.session) {
/*  240 */         closeOldResultSet();
/*  241 */         boolean bool1 = false;
/*      */         try {
/*  243 */           setExecutingStatement(this.command);
/*  244 */           if (this.command.isQuery()) {
/*  245 */             bool = true;
/*  246 */             boolean bool2 = (this.resultSetType != 1003) ? true : false;
/*  247 */             boolean bool3 = (this.resultSetConcurrency == 1008) ? true : false;
/*  248 */             ResultInterface resultInterface = this.command.executeQuery(this.maxRows, bool2);
/*  249 */             bool1 = resultInterface.isLazy();
/*  250 */             this.resultSet = new JdbcResultSet(this.conn, this, this.command, resultInterface, i, bool2, bool3, this.cachedColumnLabelMap);
/*      */           } else {
/*      */             
/*  253 */             bool = false;
/*  254 */             ResultWithGeneratedKeys resultWithGeneratedKeys = this.command.executeUpdate(this.generatedKeysRequest);
/*  255 */             this.updateCount = resultWithGeneratedKeys.getUpdateCount();
/*  256 */             ResultInterface resultInterface = resultWithGeneratedKeys.getGeneratedKeys();
/*  257 */             if (resultInterface != null) {
/*  258 */               this.generatedKeys = new JdbcResultSet(this.conn, this, this.command, resultInterface, i, true, false, false);
/*      */             }
/*      */           } 
/*      */         } finally {
/*  262 */           if (!bool1) {
/*  263 */             setExecutingStatement((CommandInterface)null);
/*      */           }
/*      */         } 
/*      */       } 
/*  267 */       return bool;
/*  268 */     } catch (Throwable throwable) {
/*  269 */       throw logAndConvert(throwable);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearParameters() throws SQLException {
/*      */     try {
/*  281 */       debugCodeCall("clearParameters");
/*  282 */       checkClosed();
/*  283 */       ArrayList arrayList = this.command.getParameters();
/*  284 */       for (ParameterInterface parameterInterface : arrayList)
/*      */       {
/*  286 */         parameterInterface.setValue(null, (this.batchParameters == null));
/*      */       }
/*  288 */     } catch (Exception exception) {
/*  289 */       throw logAndConvert(exception);
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
/*      */   public ResultSet executeQuery(String paramString) throws SQLException {
/*      */     try {
/*  302 */       debugCodeCall("executeQuery", paramString);
/*  303 */       throw DbException.get(90130);
/*  304 */     } catch (Exception exception) {
/*  305 */       throw logAndConvert(exception);
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
/*      */   public void addBatch(String paramString) throws SQLException {
/*      */     try {
/*  318 */       debugCodeCall("addBatch", paramString);
/*  319 */       throw DbException.get(90130);
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
/*      */   
/*      */   public void setNull(int paramInt1, int paramInt2) throws SQLException {
/*      */     try {
/*  337 */       if (isDebugEnabled()) {
/*  338 */         debugCode("setNull(" + paramInt1 + ", " + paramInt2 + ')');
/*      */       }
/*  340 */       setParameter(paramInt1, (Value)ValueNull.INSTANCE);
/*  341 */     } catch (Exception exception) {
/*  342 */       throw logAndConvert(exception);
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
/*      */   public void setInt(int paramInt1, int paramInt2) throws SQLException {
/*      */     try {
/*  356 */       if (isDebugEnabled()) {
/*  357 */         debugCode("setInt(" + paramInt1 + ", " + paramInt2 + ')');
/*      */       }
/*  359 */       setParameter(paramInt1, (Value)ValueInteger.get(paramInt2));
/*  360 */     } catch (Exception exception) {
/*  361 */       throw logAndConvert(exception);
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
/*      */   public void setString(int paramInt, String paramString) throws SQLException {
/*      */     try {
/*  375 */       if (isDebugEnabled()) {
/*  376 */         debugCode("setString(" + paramInt + ", " + quote(paramString) + ')');
/*      */       }
/*  378 */       setParameter(paramInt, (paramString == null) ? (Value)ValueNull.INSTANCE : ValueVarchar.get(paramString, this.conn));
/*  379 */     } catch (Exception exception) {
/*  380 */       throw logAndConvert(exception);
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
/*      */   public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
/*      */     try {
/*  394 */       if (isDebugEnabled()) {
/*  395 */         debugCode("setBigDecimal(" + paramInt + ", " + quoteBigDecimal(paramBigDecimal) + ')');
/*      */       }
/*  397 */       setParameter(paramInt, (paramBigDecimal == null) ? (Value)ValueNull.INSTANCE : (Value)ValueNumeric.getAnyScale(paramBigDecimal));
/*  398 */     } catch (Exception exception) {
/*  399 */       throw logAndConvert(exception);
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
/*      */   public void setDate(int paramInt, Date paramDate) throws SQLException {
/*      */     try {
/*  419 */       if (isDebugEnabled()) {
/*  420 */         debugCode("setDate(" + paramInt + ", " + quoteDate(paramDate) + ')');
/*      */       }
/*  422 */       setParameter(paramInt, (paramDate == null) ? (Value)ValueNull.INSTANCE : (Value)LegacyDateTimeUtils.fromDate(this.conn, null, paramDate));
/*  423 */     } catch (Exception exception) {
/*  424 */       throw logAndConvert(exception);
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
/*      */   public void setTime(int paramInt, Time paramTime) throws SQLException {
/*      */     try {
/*  444 */       if (isDebugEnabled()) {
/*  445 */         debugCode("setTime(" + paramInt + ", " + quoteTime(paramTime) + ')');
/*      */       }
/*  447 */       setParameter(paramInt, (paramTime == null) ? (Value)ValueNull.INSTANCE : (Value)LegacyDateTimeUtils.fromTime(this.conn, null, paramTime));
/*  448 */     } catch (Exception exception) {
/*  449 */       throw logAndConvert(exception);
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
/*      */   public void setTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
/*      */     try {
/*  469 */       if (isDebugEnabled()) {
/*  470 */         debugCode("setTimestamp(" + paramInt + ", " + quoteTimestamp(paramTimestamp) + ')');
/*      */       }
/*  472 */       setParameter(paramInt, (paramTimestamp == null) ? (Value)ValueNull.INSTANCE : 
/*  473 */           (Value)LegacyDateTimeUtils.fromTimestamp(this.conn, null, paramTimestamp));
/*  474 */     } catch (Exception exception) {
/*  475 */       throw logAndConvert(exception);
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
/*      */   public void setObject(int paramInt, Object paramObject) throws SQLException {
/*      */     try {
/*  490 */       if (isDebugEnabled()) {
/*  491 */         debugCode("setObject(" + paramInt + ", x)");
/*      */       }
/*  493 */       if (paramObject == null) {
/*  494 */         setParameter(paramInt, (Value)ValueNull.INSTANCE);
/*      */       } else {
/*  496 */         setParameter(paramInt, ValueToObjectConverter.objectToValue(this.session, paramObject, -1));
/*      */       } 
/*  498 */     } catch (Exception exception) {
/*  499 */       throw logAndConvert(exception);
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
/*      */   public void setObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
/*      */     try {
/*  517 */       if (isDebugEnabled()) {
/*  518 */         debugCode("setObject(" + paramInt1 + ", x, " + paramInt2 + ')');
/*      */       }
/*  520 */       setObjectWithType(paramInt1, paramObject, DataType.convertSQLTypeToValueType(paramInt2));
/*  521 */     } catch (Exception exception) {
/*  522 */       throw logAndConvert(exception);
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
/*      */   public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3) throws SQLException {
/*      */     try {
/*  541 */       if (isDebugEnabled()) {
/*  542 */         debugCode("setObject(" + paramInt1 + ", x, " + paramInt2 + ", " + paramInt3 + ')');
/*      */       }
/*  544 */       setObjectWithType(paramInt1, paramObject, DataType.convertSQLTypeToValueType(paramInt2));
/*  545 */     } catch (Exception exception) {
/*  546 */       throw logAndConvert(exception);
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
/*      */   public void setObject(int paramInt, Object paramObject, SQLType paramSQLType) throws SQLException {
/*      */     try {
/*  563 */       if (isDebugEnabled()) {
/*  564 */         debugCode("setObject(" + paramInt + ", x, " + DataType.sqlTypeToString(paramSQLType) + ')');
/*      */       }
/*  566 */       setObjectWithType(paramInt, paramObject, DataType.convertSQLTypeToValueType(paramSQLType));
/*  567 */     } catch (Exception exception) {
/*  568 */       throw logAndConvert(exception);
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
/*      */   public void setObject(int paramInt1, Object paramObject, SQLType paramSQLType, int paramInt2) throws SQLException {
/*      */     try {
/*  586 */       if (isDebugEnabled()) {
/*  587 */         debugCode("setObject(" + paramInt1 + ", x, " + DataType.sqlTypeToString(paramSQLType) + ", " + paramInt2 + ')');
/*      */       }
/*      */       
/*  590 */       setObjectWithType(paramInt1, paramObject, DataType.convertSQLTypeToValueType(paramSQLType));
/*  591 */     } catch (Exception exception) {
/*  592 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void setObjectWithType(int paramInt1, Object paramObject, int paramInt2) {
/*  597 */     if (paramObject == null) {
/*  598 */       setParameter(paramInt1, (Value)ValueNull.INSTANCE);
/*      */     } else {
/*  600 */       Value value = ValueToObjectConverter.objectToValue(this.conn.getSession(), paramObject, paramInt2);
/*  601 */       if (paramInt2 != -1) {
/*  602 */         value = value.convertTo(paramInt2, this.conn);
/*      */       }
/*  604 */       setParameter(paramInt1, value);
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
/*      */   public void setBoolean(int paramInt, boolean paramBoolean) throws SQLException {
/*      */     try {
/*  618 */       if (isDebugEnabled()) {
/*  619 */         debugCode("setBoolean(" + paramInt + ", " + paramBoolean + ')');
/*      */       }
/*  621 */       setParameter(paramInt, (Value)ValueBoolean.get(paramBoolean));
/*  622 */     } catch (Exception exception) {
/*  623 */       throw logAndConvert(exception);
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
/*      */   public void setByte(int paramInt, byte paramByte) throws SQLException {
/*      */     try {
/*  637 */       if (isDebugEnabled()) {
/*  638 */         debugCode("setByte(" + paramInt + ", " + paramByte + ')');
/*      */       }
/*  640 */       setParameter(paramInt, (Value)ValueTinyint.get(paramByte));
/*  641 */     } catch (Exception exception) {
/*  642 */       throw logAndConvert(exception);
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
/*      */   public void setShort(int paramInt, short paramShort) throws SQLException {
/*      */     try {
/*  656 */       if (isDebugEnabled()) {
/*  657 */         debugCode("setShort(" + paramInt + ", (short) " + paramShort + ')');
/*      */       }
/*  659 */       setParameter(paramInt, (Value)ValueSmallint.get(paramShort));
/*  660 */     } catch (Exception exception) {
/*  661 */       throw logAndConvert(exception);
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
/*      */   public void setLong(int paramInt, long paramLong) throws SQLException {
/*      */     try {
/*  675 */       if (isDebugEnabled()) {
/*  676 */         debugCode("setLong(" + paramInt + ", " + paramLong + "L)");
/*      */       }
/*  678 */       setParameter(paramInt, (Value)ValueBigint.get(paramLong));
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
/*      */   public void setFloat(int paramInt, float paramFloat) throws SQLException {
/*      */     try {
/*  694 */       if (isDebugEnabled()) {
/*  695 */         debugCode("setFloat(" + paramInt + ", " + paramFloat + "f)");
/*      */       }
/*  697 */       setParameter(paramInt, (Value)ValueReal.get(paramFloat));
/*  698 */     } catch (Exception exception) {
/*  699 */       throw logAndConvert(exception);
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
/*      */   public void setDouble(int paramInt, double paramDouble) throws SQLException {
/*      */     try {
/*  713 */       if (isDebugEnabled()) {
/*  714 */         debugCode("setDouble(" + paramInt + ", " + paramDouble + "d)");
/*      */       }
/*  716 */       setParameter(paramInt, (Value)ValueDouble.get(paramDouble));
/*  717 */     } catch (Exception exception) {
/*  718 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRef(int paramInt, Ref paramRef) throws SQLException {
/*  727 */     throw unsupported("ref");
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
/*      */   public void setDate(int paramInt, Date paramDate, Calendar paramCalendar) throws SQLException {
/*      */     try {
/*  748 */       if (isDebugEnabled()) {
/*  749 */         debugCode("setDate(" + paramInt + ", " + quoteDate(paramDate) + ", calendar)");
/*      */       }
/*  751 */       if (paramDate == null) {
/*  752 */         setParameter(paramInt, (Value)ValueNull.INSTANCE);
/*      */       } else {
/*  754 */         setParameter(paramInt, 
/*  755 */             (Value)LegacyDateTimeUtils.fromDate(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, paramDate));
/*      */       } 
/*  757 */     } catch (Exception exception) {
/*  758 */       throw logAndConvert(exception);
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
/*      */   public void setTime(int paramInt, Time paramTime, Calendar paramCalendar) throws SQLException {
/*      */     try {
/*  780 */       if (isDebugEnabled()) {
/*  781 */         debugCode("setTime(" + paramInt + ", " + quoteTime(paramTime) + ", calendar)");
/*      */       }
/*  783 */       if (paramTime == null) {
/*  784 */         setParameter(paramInt, (Value)ValueNull.INSTANCE);
/*      */       } else {
/*  786 */         setParameter(paramInt, 
/*  787 */             (Value)LegacyDateTimeUtils.fromTime(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, paramTime));
/*      */       } 
/*  789 */     } catch (Exception exception) {
/*  790 */       throw logAndConvert(exception);
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
/*      */   public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException {
/*      */     try {
/*  812 */       if (isDebugEnabled()) {
/*  813 */         debugCode("setTimestamp(" + paramInt + ", " + quoteTimestamp(paramTimestamp) + ", calendar)");
/*      */       }
/*  815 */       if (paramTimestamp == null) {
/*  816 */         setParameter(paramInt, (Value)ValueNull.INSTANCE);
/*      */       } else {
/*  818 */         setParameter(paramInt, 
/*  819 */             (Value)LegacyDateTimeUtils.fromTimestamp(this.conn, (paramCalendar != null) ? paramCalendar.getTimeZone() : null, paramTimestamp));
/*      */       } 
/*  821 */     } catch (Exception exception) {
/*  822 */       throw logAndConvert(exception);
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
/*      */   @Deprecated
/*      */   public void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
/*  835 */     throw unsupported("unicodeStream");
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
/*      */   public void setNull(int paramInt1, int paramInt2, String paramString) throws SQLException {
/*      */     try {
/*  850 */       if (isDebugEnabled()) {
/*  851 */         debugCode("setNull(" + paramInt1 + ", " + paramInt2 + ", " + quote(paramString) + ')');
/*      */       }
/*  853 */       setNull(paramInt1, paramInt2);
/*  854 */     } catch (Exception exception) {
/*  855 */       throw logAndConvert(exception);
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
/*      */   public void setBlob(int paramInt, Blob paramBlob) throws SQLException {
/*      */     try {
/*      */       Value value;
/*  869 */       if (isDebugEnabled()) {
/*  870 */         debugCode("setBlob(" + paramInt + ", x)");
/*      */       }
/*  872 */       checkClosed();
/*      */       
/*  874 */       if (paramBlob == null) {
/*  875 */         ValueNull valueNull = ValueNull.INSTANCE;
/*      */       } else {
/*  877 */         value = this.conn.createBlob(paramBlob.getBinaryStream(), -1L);
/*      */       } 
/*  879 */       setParameter(paramInt, value);
/*  880 */     } catch (Exception exception) {
/*  881 */       throw logAndConvert(exception);
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
/*      */   public void setBlob(int paramInt, InputStream paramInputStream) throws SQLException {
/*      */     try {
/*  897 */       if (isDebugEnabled()) {
/*  898 */         debugCode("setBlob(" + paramInt + ", x)");
/*      */       }
/*  900 */       checkClosed();
/*  901 */       Value value = this.conn.createBlob(paramInputStream, -1L);
/*  902 */       setParameter(paramInt, value);
/*  903 */     } catch (Exception exception) {
/*  904 */       throw logAndConvert(exception);
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
/*      */   public void setClob(int paramInt, Clob paramClob) throws SQLException {
/*      */     try {
/*      */       Value value;
/*  918 */       if (isDebugEnabled()) {
/*  919 */         debugCode("setClob(" + paramInt + ", x)");
/*      */       }
/*  921 */       checkClosed();
/*      */       
/*  923 */       if (paramClob == null) {
/*  924 */         ValueNull valueNull = ValueNull.INSTANCE;
/*      */       } else {
/*  926 */         value = this.conn.createClob(paramClob.getCharacterStream(), -1L);
/*      */       } 
/*  928 */       setParameter(paramInt, value);
/*  929 */     } catch (Exception exception) {
/*  930 */       throw logAndConvert(exception);
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
/*      */   public void setClob(int paramInt, Reader paramReader) throws SQLException {
/*      */     try {
/*      */       Value value;
/*  946 */       if (isDebugEnabled()) {
/*  947 */         debugCode("setClob(" + paramInt + ", x)");
/*      */       }
/*  949 */       checkClosed();
/*      */       
/*  951 */       if (paramReader == null) {
/*  952 */         ValueNull valueNull = ValueNull.INSTANCE;
/*      */       } else {
/*  954 */         value = this.conn.createClob(paramReader, -1L);
/*      */       } 
/*  956 */       setParameter(paramInt, value);
/*  957 */     } catch (Exception exception) {
/*  958 */       throw logAndConvert(exception);
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
/*      */   public void setArray(int paramInt, Array paramArray) throws SQLException {
/*      */     try {
/*      */       Value value;
/*  972 */       if (isDebugEnabled()) {
/*  973 */         debugCode("setArray(" + paramInt + ", x)");
/*      */       }
/*  975 */       checkClosed();
/*      */       
/*  977 */       if (paramArray == null) {
/*  978 */         ValueNull valueNull = ValueNull.INSTANCE;
/*      */       } else {
/*  980 */         value = ValueToObjectConverter.objectToValue(this.session, paramArray.getArray(), 40);
/*      */       } 
/*  982 */       setParameter(paramInt, value);
/*  983 */     } catch (Exception exception) {
/*  984 */       throw logAndConvert(exception);
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
/*      */   public void setBytes(int paramInt, byte[] paramArrayOfbyte) throws SQLException {
/*      */     try {
/*  998 */       if (isDebugEnabled()) {
/*  999 */         debugCode("setBytes(" + paramInt + ", " + quoteBytes(paramArrayOfbyte) + ')');
/*      */       }
/* 1001 */       setParameter(paramInt, (paramArrayOfbyte == null) ? (Value)ValueNull.INSTANCE : (Value)ValueVarbinary.get(paramArrayOfbyte));
/* 1002 */     } catch (Exception exception) {
/* 1003 */       throw logAndConvert(exception);
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
/*      */   public void setBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 1021 */       if (isDebugEnabled()) {
/* 1022 */         debugCode("setBinaryStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 1024 */       checkClosed();
/* 1025 */       Value value = this.conn.createBlob(paramInputStream, paramLong);
/* 1026 */       setParameter(paramInt, value);
/* 1027 */     } catch (Exception exception) {
/* 1028 */       throw logAndConvert(exception);
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
/*      */   public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
/* 1045 */     setBinaryStream(paramInt1, paramInputStream, paramInt2);
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
/*      */   public void setBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException {
/* 1060 */     setBinaryStream(paramInt, paramInputStream, -1);
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
/*      */   public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
/* 1076 */     setAsciiStream(paramInt1, paramInputStream, paramInt2);
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
/*      */   public void setAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 1093 */       if (isDebugEnabled()) {
/* 1094 */         debugCode("setAsciiStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 1096 */       checkClosed();
/* 1097 */       Value value = this.conn.createClob(IOUtils.getAsciiReader(paramInputStream), paramLong);
/* 1098 */       setParameter(paramInt, value);
/* 1099 */     } catch (Exception exception) {
/* 1100 */       throw logAndConvert(exception);
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
/*      */   public void setAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException {
/* 1116 */     setAsciiStream(paramInt, paramInputStream, -1);
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
/*      */   public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
/* 1132 */     setCharacterStream(paramInt1, paramReader, paramInt2);
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
/*      */   public void setCharacterStream(int paramInt, Reader paramReader) throws SQLException {
/* 1147 */     setCharacterStream(paramInt, paramReader, -1);
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
/*      */   public void setCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 1164 */       if (isDebugEnabled()) {
/* 1165 */         debugCode("setCharacterStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 1167 */       checkClosed();
/* 1168 */       Value value = this.conn.createClob(paramReader, paramLong);
/* 1169 */       setParameter(paramInt, value);
/* 1170 */     } catch (Exception exception) {
/* 1171 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setURL(int paramInt, URL paramURL) throws SQLException {
/* 1180 */     throw unsupported("url");
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
/*      */   public ResultSetMetaData getMetaData() throws SQLException {
/*      */     try {
/* 1193 */       debugCodeCall("getMetaData");
/* 1194 */       checkClosed();
/* 1195 */       ResultInterface resultInterface = this.command.getMetaData();
/* 1196 */       if (resultInterface == null) {
/* 1197 */         return null;
/*      */       }
/* 1199 */       int i = getNextId(5);
/* 1200 */       debugCodeAssign("ResultSetMetaData", 5, i, "getMetaData()");
/* 1201 */       String str = this.conn.getCatalog();
/* 1202 */       return new JdbcResultSetMetaData(null, this, resultInterface, str, this.session.getTrace(), i);
/* 1203 */     } catch (Exception exception) {
/* 1204 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearBatch() throws SQLException {
/*      */     try {
/* 1214 */       debugCodeCall("clearBatch");
/* 1215 */       checkClosed();
/* 1216 */       this.batchParameters = null;
/* 1217 */     } catch (Exception exception) {
/* 1218 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws SQLException {
/*      */     try {
/* 1230 */       super.close();
/* 1231 */       this.batchParameters = null;
/* 1232 */       this.batchIdentities = null;
/* 1233 */       if (this.command != null) {
/* 1234 */         this.command.close();
/* 1235 */         this.command = null;
/*      */       } 
/* 1237 */     } catch (Exception exception) {
/* 1238 */       throw logAndConvert(exception);
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
/*      */   public int[] executeBatch() throws SQLException {
/*      */     try {
/* 1252 */       debugCodeCall("executeBatch");
/* 1253 */       if (this.batchParameters == null)
/*      */       {
/* 1255 */         this.batchParameters = (ArrayList)new ArrayList<>();
/*      */       }
/* 1257 */       this.batchIdentities = new MergedResult();
/* 1258 */       int i = this.batchParameters.size();
/* 1259 */       int[] arrayOfInt = new int[i];
/* 1260 */       SQLException sQLException = new SQLException();
/* 1261 */       checkClosed();
/* 1262 */       for (byte b = 0; b < i; b++) {
/* 1263 */         long l = executeBatchElement(this.batchParameters.get(b), sQLException);
/* 1264 */         arrayOfInt[b] = (l <= 2147483647L) ? (int)l : -2;
/*      */       } 
/* 1266 */       this.batchParameters = null;
/* 1267 */       sQLException = sQLException.getNextException();
/* 1268 */       if (sQLException != null) {
/* 1269 */         throw new JdbcBatchUpdateException(sQLException, arrayOfInt);
/*      */       }
/* 1271 */       return arrayOfInt;
/* 1272 */     } catch (Exception exception) {
/* 1273 */       throw logAndConvert(exception);
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
/*      */   public long[] executeLargeBatch() throws SQLException {
/*      */     try {
/* 1286 */       debugCodeCall("executeLargeBatch");
/* 1287 */       if (this.batchParameters == null)
/*      */       {
/* 1289 */         this.batchParameters = (ArrayList)new ArrayList<>();
/*      */       }
/* 1291 */       this.batchIdentities = new MergedResult();
/* 1292 */       int i = this.batchParameters.size();
/* 1293 */       long[] arrayOfLong = new long[i];
/* 1294 */       SQLException sQLException = new SQLException();
/* 1295 */       checkClosed();
/* 1296 */       for (byte b = 0; b < i; b++) {
/* 1297 */         arrayOfLong[b] = executeBatchElement((Value[])this.batchParameters.get(b), sQLException);
/*      */       }
/* 1299 */       this.batchParameters = null;
/* 1300 */       sQLException = sQLException.getNextException();
/* 1301 */       if (sQLException != null) {
/* 1302 */         throw new JdbcBatchUpdateException(sQLException, arrayOfLong);
/*      */       }
/* 1304 */       return arrayOfLong;
/* 1305 */     } catch (Exception exception) {
/* 1306 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */   private long executeBatchElement(Value[] paramArrayOfValue, SQLException paramSQLException) {
/*      */     long l;
/* 1311 */     ArrayList<ParameterInterface> arrayList = this.command.getParameters(); byte b; int i;
/* 1312 */     for (b = 0, i = paramArrayOfValue.length; b < i; b++) {
/* 1313 */       ((ParameterInterface)arrayList.get(b)).setValue(paramArrayOfValue[b], false);
/*      */     }
/*      */     
/*      */     try {
/* 1317 */       l = executeUpdateInternal();
/*      */       
/* 1319 */       ResultSet resultSet = super.getGeneratedKeys();
/* 1320 */       this.batchIdentities.add(((JdbcResultSet)resultSet).result);
/* 1321 */     } catch (Exception exception) {
/* 1322 */       paramSQLException.setNextException(logAndConvert(exception));
/* 1323 */       l = -3L;
/*      */     } 
/* 1325 */     return l;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getGeneratedKeys() throws SQLException {
/* 1330 */     if (this.batchIdentities != null) {
/*      */       try {
/* 1332 */         int i = getNextId(4);
/* 1333 */         debugCodeAssign("ResultSet", 4, i, "getGeneratedKeys()");
/* 1334 */         checkClosed();
/* 1335 */         this.generatedKeys = new JdbcResultSet(this.conn, this, null, (ResultInterface)this.batchIdentities.getResult(), i, true, false, false);
/*      */       }
/* 1337 */       catch (Exception exception) {
/* 1338 */         throw logAndConvert(exception);
/*      */       } 
/*      */     }
/* 1341 */     return super.getGeneratedKeys();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addBatch() throws SQLException {
/*      */     try {
/* 1350 */       debugCodeCall("addBatch");
/* 1351 */       checkClosed();
/*      */       
/* 1353 */       ArrayList<ParameterInterface> arrayList = this.command.getParameters();
/* 1354 */       int i = arrayList.size();
/* 1355 */       Value[] arrayOfValue = new Value[i];
/* 1356 */       for (byte b = 0; b < i; b++) {
/* 1357 */         ParameterInterface parameterInterface = arrayList.get(b);
/* 1358 */         parameterInterface.checkSet();
/* 1359 */         Value value = parameterInterface.getParamValue();
/* 1360 */         arrayOfValue[b] = value;
/*      */       } 
/* 1362 */       if (this.batchParameters == null) {
/* 1363 */         this.batchParameters = Utils.newSmallArrayList();
/*      */       }
/* 1365 */       this.batchParameters.add(arrayOfValue);
/* 1366 */     } catch (Exception exception) {
/* 1367 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ParameterMetaData getParameterMetaData() throws SQLException {
/*      */     try {
/* 1379 */       int i = getNextId(11);
/* 1380 */       debugCodeAssign("ParameterMetaData", 11, i, "getParameterMetaData()");
/* 1381 */       checkClosed();
/* 1382 */       return new JdbcParameterMetaData(this.session.getTrace(), this, this.command, i);
/* 1383 */     } catch (Exception exception) {
/* 1384 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void setParameter(int paramInt, Value paramValue) {
/* 1391 */     checkClosed();
/* 1392 */     paramInt--;
/* 1393 */     ArrayList<ParameterInterface> arrayList = this.command.getParameters();
/* 1394 */     if (paramInt < 0 || paramInt >= arrayList.size()) {
/* 1395 */       throw DbException.getInvalidValueException("parameterIndex", 
/* 1396 */           Integer.valueOf(paramInt + 1));
/*      */     }
/* 1398 */     ParameterInterface parameterInterface = arrayList.get(paramInt);
/*      */     
/* 1400 */     parameterInterface.setValue(paramValue, (this.batchParameters == null));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRowId(int paramInt, RowId paramRowId) throws SQLException {
/* 1408 */     throw unsupported("rowId");
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
/*      */   public void setNString(int paramInt, String paramString) throws SQLException {
/*      */     try {
/* 1421 */       if (isDebugEnabled()) {
/* 1422 */         debugCode("setNString(" + paramInt + ", " + quote(paramString) + ')');
/*      */       }
/* 1424 */       setParameter(paramInt, (paramString == null) ? (Value)ValueNull.INSTANCE : ValueVarchar.get(paramString, this.conn));
/* 1425 */     } catch (Exception exception) {
/* 1426 */       throw logAndConvert(exception);
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
/*      */   public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 1444 */       if (isDebugEnabled()) {
/* 1445 */         debugCode("setNCharacterStream(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 1447 */       checkClosed();
/* 1448 */       Value value = this.conn.createClob(paramReader, paramLong);
/* 1449 */       setParameter(paramInt, value);
/* 1450 */     } catch (Exception exception) {
/* 1451 */       throw logAndConvert(exception);
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
/*      */   public void setNCharacterStream(int paramInt, Reader paramReader) throws SQLException {
/* 1467 */     setNCharacterStream(paramInt, paramReader, -1L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNClob(int paramInt, NClob paramNClob) throws SQLException {
/*      */     try {
/*      */       Value value;
/* 1480 */       if (isDebugEnabled()) {
/* 1481 */         debugCode("setNClob(" + paramInt + ", x)");
/*      */       }
/* 1483 */       checkClosed();
/*      */       
/* 1485 */       if (paramNClob == null) {
/* 1486 */         ValueNull valueNull = ValueNull.INSTANCE;
/*      */       } else {
/* 1488 */         value = this.conn.createClob(paramNClob.getCharacterStream(), -1L);
/*      */       } 
/* 1490 */       setParameter(paramInt, value);
/* 1491 */     } catch (Exception exception) {
/* 1492 */       throw logAndConvert(exception);
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
/*      */   public void setNClob(int paramInt, Reader paramReader) throws SQLException {
/*      */     try {
/* 1508 */       if (isDebugEnabled()) {
/* 1509 */         debugCode("setNClob(" + paramInt + ", x)");
/*      */       }
/* 1511 */       checkClosed();
/* 1512 */       Value value = this.conn.createClob(paramReader, -1L);
/* 1513 */       setParameter(paramInt, value);
/* 1514 */     } catch (Exception exception) {
/* 1515 */       throw logAndConvert(exception);
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
/*      */   public void setClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 1532 */       if (isDebugEnabled()) {
/* 1533 */         debugCode("setClob(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 1535 */       checkClosed();
/* 1536 */       Value value = this.conn.createClob(paramReader, paramLong);
/* 1537 */       setParameter(paramInt, value);
/* 1538 */     } catch (Exception exception) {
/* 1539 */       throw logAndConvert(exception);
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
/*      */   public void setBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {
/*      */     try {
/* 1557 */       if (isDebugEnabled()) {
/* 1558 */         debugCode("setBlob(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 1560 */       checkClosed();
/* 1561 */       Value value = this.conn.createBlob(paramInputStream, paramLong);
/* 1562 */       setParameter(paramInt, value);
/* 1563 */     } catch (Exception exception) {
/* 1564 */       throw logAndConvert(exception);
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
/*      */   public void setNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException {
/*      */     try {
/* 1582 */       if (isDebugEnabled()) {
/* 1583 */         debugCode("setNClob(" + paramInt + ", x, " + paramLong + "L)");
/*      */       }
/* 1585 */       checkClosed();
/* 1586 */       Value value = this.conn.createClob(paramReader, paramLong);
/* 1587 */       setParameter(paramInt, value);
/* 1588 */     } catch (Exception exception) {
/* 1589 */       throw logAndConvert(exception);
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
/*      */   public void setSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException {
/*      */     try {
/*      */       Value value;
/* 1603 */       if (isDebugEnabled()) {
/* 1604 */         debugCode("setSQLXML(" + paramInt + ", x)");
/*      */       }
/* 1606 */       checkClosed();
/*      */       
/* 1608 */       if (paramSQLXML == null) {
/* 1609 */         ValueNull valueNull = ValueNull.INSTANCE;
/*      */       } else {
/* 1611 */         value = this.conn.createClob(paramSQLXML.getCharacterStream(), -1L);
/*      */       } 
/* 1613 */       setParameter(paramInt, value);
/* 1614 */     } catch (Exception exception) {
/* 1615 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1624 */     return getTraceObjectName() + ": " + this.command;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */