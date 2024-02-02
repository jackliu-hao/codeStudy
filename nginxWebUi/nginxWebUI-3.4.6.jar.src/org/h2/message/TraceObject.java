/*     */ package org.h2.message;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Date;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicIntegerArray;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public abstract class TraceObject
/*     */ {
/*     */   protected static final int CALLABLE_STATEMENT = 0;
/*     */   protected static final int CONNECTION = 1;
/*     */   protected static final int DATABASE_META_DATA = 2;
/*     */   protected static final int PREPARED_STATEMENT = 3;
/*     */   protected static final int RESULT_SET = 4;
/*     */   protected static final int RESULT_SET_META_DATA = 5;
/*     */   protected static final int SAVEPOINT = 6;
/*     */   protected static final int STATEMENT = 8;
/*     */   protected static final int BLOB = 9;
/*     */   protected static final int CLOB = 10;
/*     */   protected static final int PARAMETER_META_DATA = 11;
/*     */   protected static final int DATA_SOURCE = 12;
/*     */   protected static final int XA_DATA_SOURCE = 13;
/*     */   protected static final int XID = 15;
/*     */   protected static final int ARRAY = 16;
/*     */   protected static final int SQLXML = 17;
/*     */   private static final int LAST = 18;
/* 102 */   private static final AtomicIntegerArray ID = new AtomicIntegerArray(18);
/*     */   
/* 104 */   private static final String[] PREFIX = new String[] { "call", "conn", "dbMeta", "prep", "rs", "rsMeta", "sp", "ex", "stat", "blob", "clob", "pMeta", "ds", "xads", "xares", "xid", "ar", "sqlxml" };
/*     */ 
/*     */ 
/*     */   
/* 108 */   private static final SQLException SQL_OOME = DbException.SQL_OOME;
/*     */ 
/*     */ 
/*     */   
/*     */   protected Trace trace;
/*     */ 
/*     */ 
/*     */   
/*     */   private int traceType;
/*     */ 
/*     */ 
/*     */   
/*     */   private int id;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setTrace(Trace paramTrace, int paramInt1, int paramInt2) {
/* 126 */     this.trace = paramTrace;
/* 127 */     this.traceType = paramInt1;
/* 128 */     this.id = paramInt2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTraceId() {
/* 136 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTraceObjectName() {
/* 144 */     return PREFIX[this.traceType] + this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int getNextId(int paramInt) {
/* 154 */     return ID.getAndIncrement(paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean isDebugEnabled() {
/* 163 */     return this.trace.isDebugEnabled();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final boolean isInfoEnabled() {
/* 172 */     return this.trace.isInfoEnabled();
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
/*     */   protected final void debugCodeAssign(String paramString1, int paramInt1, int paramInt2, String paramString2) {
/* 185 */     if (this.trace.isDebugEnabled()) {
/* 186 */       this.trace.debugCode(paramString1 + ' ' + PREFIX[paramInt1] + paramInt2 + " = " + getTraceObjectName() + '.' + paramString2 + ';');
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
/*     */   protected final void debugCodeCall(String paramString) {
/* 198 */     if (this.trace.isDebugEnabled()) {
/* 199 */       this.trace.debugCode(getTraceObjectName() + '.' + paramString + "();");
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
/*     */   protected final void debugCodeCall(String paramString, long paramLong) {
/* 212 */     if (this.trace.isDebugEnabled()) {
/* 213 */       this.trace.debugCode(getTraceObjectName() + '.' + paramString + '(' + paramLong + ");");
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
/*     */   protected final void debugCodeCall(String paramString1, String paramString2) {
/* 226 */     if (this.trace.isDebugEnabled()) {
/* 227 */       this.trace.debugCode(getTraceObjectName() + '.' + paramString1 + '(' + quote(paramString2) + ");");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void debugCode(String paramString) {
/* 237 */     if (this.trace.isDebugEnabled()) {
/* 238 */       this.trace.debugCode(getTraceObjectName() + '.' + paramString + ';');
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quote(String paramString) {
/* 249 */     return StringUtils.quoteJavaString(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteTime(Time paramTime) {
/* 259 */     if (paramTime == null) {
/* 260 */       return "null";
/*     */     }
/* 262 */     return "Time.valueOf(\"" + paramTime.toString() + "\")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteTimestamp(Timestamp paramTimestamp) {
/* 272 */     if (paramTimestamp == null) {
/* 273 */       return "null";
/*     */     }
/* 275 */     return "Timestamp.valueOf(\"" + paramTimestamp.toString() + "\")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteDate(Date paramDate) {
/* 285 */     if (paramDate == null) {
/* 286 */       return "null";
/*     */     }
/* 288 */     return "Date.valueOf(\"" + paramDate.toString() + "\")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteBigDecimal(BigDecimal paramBigDecimal) {
/* 298 */     if (paramBigDecimal == null) {
/* 299 */       return "null";
/*     */     }
/* 301 */     return "new BigDecimal(\"" + paramBigDecimal.toString() + "\")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteBytes(byte[] paramArrayOfbyte) {
/* 311 */     if (paramArrayOfbyte == null) {
/* 312 */       return "null";
/*     */     }
/*     */     
/* 315 */     StringBuilder stringBuilder = (new StringBuilder(paramArrayOfbyte.length * 2 + 45)).append("org.h2.util.StringUtils.convertHexToBytes(\"");
/* 316 */     return StringUtils.convertBytesToHex(stringBuilder, paramArrayOfbyte).append("\")").toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteArray(String[] paramArrayOfString) {
/* 327 */     return StringUtils.quoteJavaStringArray(paramArrayOfString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteIntArray(int[] paramArrayOfint) {
/* 337 */     return StringUtils.quoteJavaIntArray(paramArrayOfint);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String quoteMap(Map<String, Class<?>> paramMap) {
/* 347 */     if (paramMap == null) {
/* 348 */       return "null";
/*     */     }
/* 350 */     if (paramMap.size() == 0) {
/* 351 */       return "new Map()";
/*     */     }
/* 353 */     return "new Map() /* " + paramMap.toString() + " */";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SQLException logAndConvert(Throwable paramThrowable) {
/* 363 */     SQLException sQLException = null;
/*     */     try {
/* 365 */       sQLException = DbException.toSQLException(paramThrowable);
/* 366 */       if (this.trace == null) {
/* 367 */         DbException.traceThrowable(sQLException);
/*     */       } else {
/* 369 */         int i = sQLException.getErrorCode();
/* 370 */         if (i >= 23000 && i < 24000) {
/* 371 */           this.trace.info(sQLException, "exception");
/*     */         } else {
/* 373 */           this.trace.error(sQLException, "exception");
/*     */         } 
/*     */       } 
/* 376 */     } catch (Throwable throwable) {
/* 377 */       if (sQLException == null) {
/*     */         try {
/* 379 */           sQLException = new SQLException("GeneralError", "HY000", 50000, paramThrowable);
/* 380 */         } catch (OutOfMemoryError|NoClassDefFoundError outOfMemoryError) {
/* 381 */           return SQL_OOME;
/*     */         } 
/*     */       }
/* 384 */       sQLException.addSuppressed(throwable);
/*     */     } 
/* 386 */     return sQLException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SQLException unsupported(String paramString) {
/*     */     try {
/* 397 */       throw DbException.getUnsupportedException(paramString);
/* 398 */     } catch (Exception exception) {
/* 399 */       return logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\message\TraceObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */