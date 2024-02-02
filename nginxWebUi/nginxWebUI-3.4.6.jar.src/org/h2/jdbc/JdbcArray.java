/*     */ package org.h2.jdbc;
/*     */ 
/*     */ import java.sql.Array;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.SimpleResult;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueToObjectConverter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JdbcArray
/*     */   extends TraceObject
/*     */   implements Array
/*     */ {
/*     */   private ValueArray value;
/*     */   private final JdbcConnection conn;
/*     */   
/*     */   public JdbcArray(JdbcConnection paramJdbcConnection, Value paramValue, int paramInt) {
/*  39 */     setTrace(paramJdbcConnection.getSession().getTrace(), 16, paramInt);
/*  40 */     this.conn = paramJdbcConnection;
/*  41 */     this.value = paramValue.convertToAnyArray(paramJdbcConnection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getArray() throws SQLException {
/*     */     try {
/*  53 */       debugCodeCall("getArray");
/*  54 */       checkClosed();
/*  55 */       return get();
/*  56 */     } catch (Exception exception) {
/*  57 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getArray(Map<String, Class<?>> paramMap) throws SQLException {
/*     */     try {
/*  71 */       if (isDebugEnabled()) {
/*  72 */         debugCode("getArray(" + quoteMap(paramMap) + ')');
/*     */       }
/*  74 */       JdbcConnection.checkMap(paramMap);
/*  75 */       checkClosed();
/*  76 */       return get();
/*  77 */     } catch (Exception exception) {
/*  78 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getArray(long paramLong, int paramInt) throws SQLException {
/*     */     try {
/*  94 */       if (isDebugEnabled()) {
/*  95 */         debugCode("getArray(" + paramLong + ", " + paramInt + ')');
/*     */       }
/*  97 */       checkClosed();
/*  98 */       return get(paramLong, paramInt);
/*  99 */     } catch (Exception exception) {
/* 100 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getArray(long paramLong, int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
/*     */     try {
/* 118 */       if (isDebugEnabled()) {
/* 119 */         debugCode("getArray(" + paramLong + ", " + paramInt + ", " + quoteMap(paramMap) + ')');
/*     */       }
/* 121 */       checkClosed();
/* 122 */       JdbcConnection.checkMap(paramMap);
/* 123 */       return get(paramLong, paramInt);
/* 124 */     } catch (Exception exception) {
/* 125 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBaseType() throws SQLException {
/*     */     try {
/* 137 */       debugCodeCall("getBaseType");
/* 138 */       checkClosed();
/* 139 */       return DataType.convertTypeToSQLType(this.value.getComponentType());
/* 140 */     } catch (Exception exception) {
/* 141 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBaseTypeName() throws SQLException {
/*     */     try {
/* 154 */       debugCodeCall("getBaseTypeName");
/* 155 */       checkClosed();
/* 156 */       return this.value.getComponentType().getDeclaredTypeName();
/* 157 */     } catch (Exception exception) {
/* 158 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getResultSet() throws SQLException {
/*     */     try {
/* 172 */       debugCodeCall("getResultSet");
/* 173 */       checkClosed();
/* 174 */       return getResultSetImpl(1L, 2147483647);
/* 175 */     } catch (Exception exception) {
/* 176 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getResultSet(Map<String, Class<?>> paramMap) throws SQLException {
/*     */     try {
/* 190 */       if (isDebugEnabled()) {
/* 191 */         debugCode("getResultSet(" + quoteMap(paramMap) + ')');
/*     */       }
/* 193 */       checkClosed();
/* 194 */       JdbcConnection.checkMap(paramMap);
/* 195 */       return getResultSetImpl(1L, 2147483647);
/* 196 */     } catch (Exception exception) {
/* 197 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getResultSet(long paramLong, int paramInt) throws SQLException {
/*     */     try {
/* 214 */       if (isDebugEnabled()) {
/* 215 */         debugCode("getResultSet(" + paramLong + ", " + paramInt + ')');
/*     */       }
/* 217 */       checkClosed();
/* 218 */       return getResultSetImpl(paramLong, paramInt);
/* 219 */     } catch (Exception exception) {
/* 220 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultSet getResultSet(long paramLong, int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
/*     */     try {
/* 240 */       if (isDebugEnabled()) {
/* 241 */         debugCode("getResultSet(" + paramLong + ", " + paramInt + ", " + quoteMap(paramMap) + ')');
/*     */       }
/* 243 */       checkClosed();
/* 244 */       JdbcConnection.checkMap(paramMap);
/* 245 */       return getResultSetImpl(paramLong, paramInt);
/* 246 */     } catch (Exception exception) {
/* 247 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void free() {
/* 256 */     debugCodeCall("free");
/* 257 */     this.value = null;
/*     */   }
/*     */   
/*     */   private ResultSet getResultSetImpl(long paramLong, int paramInt) {
/* 261 */     int i = getNextId(4);
/* 262 */     SimpleResult simpleResult = new SimpleResult();
/* 263 */     simpleResult.addColumn("INDEX", TypeInfo.TYPE_BIGINT);
/* 264 */     simpleResult.addColumn("VALUE", this.value.getComponentType());
/* 265 */     Value[] arrayOfValue = this.value.getList();
/* 266 */     paramInt = checkRange(paramLong, paramInt, arrayOfValue.length);
/* 267 */     for (int j = (int)paramLong; j < paramLong + paramInt; j++) {
/* 268 */       simpleResult.addRow(new Value[] { (Value)ValueBigint.get(j), arrayOfValue[j - 1] });
/*     */     } 
/* 270 */     return new JdbcResultSet(this.conn, null, null, (ResultInterface)simpleResult, i, true, false, false);
/*     */   }
/*     */   
/*     */   private void checkClosed() {
/* 274 */     this.conn.checkClosed();
/* 275 */     if (this.value == null) {
/* 276 */       throw DbException.get(90007);
/*     */     }
/*     */   }
/*     */   
/*     */   private Object get() {
/* 281 */     return ValueToObjectConverter.valueToDefaultArray((Value)this.value, this.conn, true);
/*     */   }
/*     */   
/*     */   private Object get(long paramLong, int paramInt) {
/* 285 */     Value[] arrayOfValue = this.value.getList();
/* 286 */     paramInt = checkRange(paramLong, paramInt, arrayOfValue.length);
/* 287 */     Object[] arrayOfObject = new Object[paramInt]; byte b; int i;
/* 288 */     for (b = 0, i = (int)paramLong - 1; b < paramInt; b++, i++) {
/* 289 */       arrayOfObject[b] = ValueToObjectConverter.valueToDefaultObject(arrayOfValue[i], this.conn, true);
/*     */     }
/* 291 */     return arrayOfObject;
/*     */   }
/*     */   
/*     */   private static int checkRange(long paramLong, int paramInt1, int paramInt2) {
/* 295 */     if (paramLong < 1L || (paramLong != 1L && paramLong > paramInt2)) {
/* 296 */       throw DbException.getInvalidValueException("index (1.." + paramInt2 + ')', Long.valueOf(paramLong));
/*     */     }
/* 298 */     int i = paramInt2 - (int)paramLong + 1;
/* 299 */     if (paramInt1 < 0) {
/* 300 */       throw DbException.getInvalidValueException("count (0.." + i + ')', Integer.valueOf(paramInt1));
/*     */     }
/* 302 */     return Math.min(i, paramInt1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 310 */     return (this.value == null) ? "null" : (
/* 311 */       getTraceObjectName() + ": " + this.value.getTraceSQL());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */