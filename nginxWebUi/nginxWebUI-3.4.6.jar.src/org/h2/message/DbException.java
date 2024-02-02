/*     */ package org.h2.message;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.h2.api.ErrorCode;
/*     */ import org.h2.jdbc.JdbcException;
/*     */ import org.h2.jdbc.JdbcSQLDataException;
/*     */ import org.h2.jdbc.JdbcSQLException;
/*     */ import org.h2.jdbc.JdbcSQLFeatureNotSupportedException;
/*     */ import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
/*     */ import org.h2.jdbc.JdbcSQLInvalidAuthorizationSpecException;
/*     */ import org.h2.jdbc.JdbcSQLNonTransientConnectionException;
/*     */ import org.h2.jdbc.JdbcSQLNonTransientException;
/*     */ import org.h2.jdbc.JdbcSQLSyntaxErrorException;
/*     */ import org.h2.jdbc.JdbcSQLTimeoutException;
/*     */ import org.h2.jdbc.JdbcSQLTransactionRollbackException;
/*     */ import org.h2.jdbc.JdbcSQLTransientException;
/*     */ import org.h2.util.SortedProperties;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
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
/*     */ public class DbException
/*     */   extends RuntimeException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final String HIDE_SQL = "--hide--";
/*  60 */   private static final Properties MESSAGES = new Properties();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   public static final SQLException SQL_OOME = new SQLException("OutOfMemoryError", "HY000", 90108, new OutOfMemoryError());
/*     */   
/*  68 */   private static final DbException OOME = new DbException(SQL_OOME);
/*     */   
/*     */   private Object source;
/*     */   
/*     */   static {
/*     */     try {
/*  74 */       byte[] arrayOfByte = Utils.getResource("/org/h2/res/_messages_en.prop");
/*  75 */       if (arrayOfByte != null) {
/*  76 */         MESSAGES.load(new ByteArrayInputStream(arrayOfByte));
/*     */       }
/*  78 */       String str = Locale.getDefault().getLanguage();
/*  79 */       if (!"en".equals(str)) {
/*  80 */         byte[] arrayOfByte1 = Utils.getResource("/org/h2/res/_messages_" + str + ".prop");
/*     */ 
/*     */ 
/*     */         
/*  84 */         if (arrayOfByte1 != null) {
/*  85 */           SortedProperties sortedProperties = SortedProperties.fromLines(new String(arrayOfByte1, StandardCharsets.UTF_8));
/*     */           
/*  87 */           for (Map.Entry<Object, Object> entry : sortedProperties.entrySet()) {
/*  88 */             String str1 = (String)entry.getKey();
/*  89 */             String str2 = (String)entry.getValue();
/*  90 */             if (str2 != null && !str2.startsWith("#")) {
/*  91 */               String str3 = MESSAGES.getProperty(str1);
/*  92 */               String str4 = str2 + "\n" + str3;
/*  93 */               MESSAGES.put(str1, str4);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*  98 */     } catch (OutOfMemoryError|IOException outOfMemoryError) {
/*  99 */       traceThrowable(outOfMemoryError);
/*     */     } 
/*     */   }
/*     */   
/*     */   private DbException(SQLException paramSQLException) {
/* 104 */     super(paramSQLException.getMessage(), paramSQLException);
/*     */   }
/*     */   
/*     */   private static String translate(String paramString, String... paramVarArgs) {
/* 108 */     String str = MESSAGES.getProperty(paramString);
/* 109 */     if (str == null) {
/* 110 */       str = "(Message " + paramString + " not found)";
/*     */     }
/* 112 */     if (paramVarArgs != null) {
/* 113 */       for (byte b = 0; b < paramVarArgs.length; b++) {
/* 114 */         String str1 = paramVarArgs[b];
/* 115 */         if (str1 != null && str1.length() > 0) {
/* 116 */           paramVarArgs[b] = quote(str1);
/*     */         }
/*     */       } 
/* 119 */       str = MessageFormat.format(str, (Object[])paramVarArgs);
/*     */     } 
/* 121 */     return str;
/*     */   }
/*     */   
/*     */   private static String quote(String paramString) {
/* 125 */     int i = paramString.length();
/* 126 */     StringBuilder stringBuilder = (new StringBuilder(i + 2)).append('"');
/* 127 */     for (int j = 0; j < i; ) {
/* 128 */       int k = paramString.codePointAt(j);
/* 129 */       j += Character.charCount(k);
/* 130 */       int m = Character.getType(k);
/* 131 */       if (m == 0 || (m >= 12 && m <= 19 && k != 32)) {
/* 132 */         if (k <= 65535) {
/* 133 */           StringUtils.appendHex(stringBuilder.append('\\'), k, 2); continue;
/*     */         } 
/* 135 */         StringUtils.appendHex(stringBuilder.append("\\+"), k, 3);
/*     */         continue;
/*     */       } 
/* 138 */       if (k == 34 || k == 92) {
/* 139 */         stringBuilder.append((char)k);
/*     */       }
/* 141 */       stringBuilder.appendCodePoint(k);
/*     */     } 
/*     */     
/* 144 */     return stringBuilder.append('"').toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SQLException getSQLException() {
/* 153 */     return (SQLException)getCause();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getErrorCode() {
/* 162 */     return getSQLException().getErrorCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbException addSQL(String paramString) {
/* 173 */     SQLException sQLException = getSQLException();
/* 174 */     if (sQLException instanceof JdbcException) {
/* 175 */       JdbcException jdbcException = (JdbcException)sQLException;
/* 176 */       if (jdbcException.getSQL() == null) {
/* 177 */         jdbcException.setSQL(filterSQL(paramString));
/*     */       }
/* 179 */       return this;
/*     */     } 
/* 181 */     sQLException = getJdbcSQLException(sQLException.getMessage(), paramString, sQLException.getSQLState(), sQLException.getErrorCode(), sQLException, (String)null);
/* 182 */     return new DbException(sQLException);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException get(int paramInt) {
/* 192 */     return get(paramInt, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException get(int paramInt, String paramString) {
/* 203 */     return get(paramInt, new String[] { paramString });
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
/*     */   public static DbException get(int paramInt, Throwable paramThrowable, String... paramVarArgs) {
/* 216 */     return new DbException(getJdbcSQLException(paramInt, paramThrowable, paramVarArgs));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException get(int paramInt, String... paramVarArgs) {
/* 227 */     return new DbException(getJdbcSQLException(paramInt, (Throwable)null, paramVarArgs));
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
/*     */   public static DbException fromUser(String paramString1, String paramString2) {
/* 239 */     return new DbException(getJdbcSQLException(paramString2, (String)null, paramString1, 0, (Throwable)null, (String)null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException getSyntaxError(String paramString, int paramInt) {
/* 250 */     paramString = StringUtils.addAsterisk(paramString, paramInt);
/* 251 */     return get(42000, paramString);
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
/*     */   public static DbException getSyntaxError(String paramString1, int paramInt, String paramString2) {
/* 264 */     paramString1 = StringUtils.addAsterisk(paramString1, paramInt);
/* 265 */     return new DbException(getJdbcSQLException(42001, (Throwable)null, new String[] { paramString1, paramString2 }));
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
/*     */   public static DbException getSyntaxError(int paramInt1, String paramString, int paramInt2, String... paramVarArgs) {
/* 278 */     paramString = StringUtils.addAsterisk(paramString, paramInt2);
/* 279 */     String str1 = ErrorCode.getState(paramInt1);
/* 280 */     String str2 = translate(str1, paramVarArgs);
/* 281 */     return new DbException(getJdbcSQLException(str2, paramString, str1, paramInt1, (Throwable)null, (String)null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException getUnsupportedException(String paramString) {
/* 291 */     return get(50100, paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException getInvalidValueException(String paramString, Object paramObject) {
/* 302 */     return get(90008, new String[] { (paramObject == null) ? "null" : paramObject.toString(), paramString });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException getInvalidExpressionTypeException(String paramString, Typed paramTyped) {
/* 313 */     TypeInfo typeInfo = paramTyped.getType();
/* 314 */     if (typeInfo.getValueType() == -1) {
/* 315 */       return get(50004, ((paramTyped instanceof org.h2.util.HasSQL) ? paramTyped : typeInfo).getTraceSQL());
/*     */     }
/* 317 */     return get(90008, new String[] { typeInfo.getTraceSQL(), paramString });
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
/*     */   public static DbException getValueTooLongException(String paramString1, String paramString2, long paramLong) {
/* 333 */     int i = paramString2.length();
/* 334 */     byte b = (paramLong >= 0L) ? 22 : 0;
/*     */ 
/*     */     
/* 337 */     StringBuilder stringBuilder = (i > 80) ? (new StringBuilder(83 + b)).append(paramString2, 0, 80).append("...") : (new StringBuilder(i + b)).append(paramString2);
/* 338 */     if (paramLong >= 0L) {
/* 339 */       stringBuilder.append(" (").append(paramLong).append(')');
/*     */     }
/* 341 */     return get(22001, new String[] { paramString1, stringBuilder.toString() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException getFileVersionError(String paramString) {
/* 351 */     return get(90048, "Old database: " + paramString + " - please convert the database to a SQL script and re-create it.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RuntimeException getInternalError(String paramString) {
/* 362 */     RuntimeException runtimeException = new RuntimeException(paramString);
/* 363 */     traceThrowable(runtimeException);
/* 364 */     return runtimeException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RuntimeException getInternalError() {
/* 373 */     return getInternalError("Unexpected code path");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SQLException toSQLException(Throwable paramThrowable) {
/* 383 */     if (paramThrowable instanceof SQLException) {
/* 384 */       return (SQLException)paramThrowable;
/*     */     }
/* 386 */     return convert(paramThrowable).getSQLException();
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
/*     */   public static DbException convert(Throwable paramThrowable) {
/*     */     try {
/* 399 */       if (paramThrowable instanceof DbException)
/* 400 */         return (DbException)paramThrowable; 
/* 401 */       if (paramThrowable instanceof SQLException)
/* 402 */         return new DbException((SQLException)paramThrowable); 
/* 403 */       if (paramThrowable instanceof InvocationTargetException)
/* 404 */         return convertInvocation((InvocationTargetException)paramThrowable, (String)null); 
/* 405 */       if (paramThrowable instanceof IOException)
/* 406 */         return get(90028, paramThrowable, new String[] { paramThrowable.toString() }); 
/* 407 */       if (paramThrowable instanceof OutOfMemoryError)
/* 408 */         return get(90108, paramThrowable, new String[0]); 
/* 409 */       if (paramThrowable instanceof StackOverflowError || paramThrowable instanceof LinkageError)
/* 410 */         return get(50000, paramThrowable, new String[] { paramThrowable.toString() }); 
/* 411 */       if (paramThrowable instanceof Error) {
/* 412 */         throw (Error)paramThrowable;
/*     */       }
/* 414 */       return get(50000, paramThrowable, new String[] { paramThrowable.toString() });
/* 415 */     } catch (OutOfMemoryError outOfMemoryError) {
/* 416 */       return OOME;
/* 417 */     } catch (Throwable throwable) {
/*     */       try {
/* 419 */         DbException dbException = new DbException(new SQLException("GeneralError", "HY000", 50000, paramThrowable));
/*     */         
/* 421 */         dbException.addSuppressed(throwable);
/* 422 */         return dbException;
/* 423 */       } catch (OutOfMemoryError outOfMemoryError) {
/* 424 */         return OOME;
/*     */       } 
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
/*     */   public static DbException convertInvocation(InvocationTargetException paramInvocationTargetException, String paramString) {
/* 438 */     Throwable throwable = paramInvocationTargetException.getTargetException();
/* 439 */     if (throwable instanceof SQLException || throwable instanceof DbException) {
/* 440 */       return convert(throwable);
/*     */     }
/* 442 */     paramString = (paramString == null) ? throwable.getMessage() : (paramString + ": " + throwable.getMessage());
/* 443 */     return get(90105, throwable, new String[] { paramString });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DbException convertIOException(IOException paramIOException, String paramString) {
/* 454 */     if (paramString == null) {
/* 455 */       Throwable throwable = paramIOException.getCause();
/* 456 */       if (throwable instanceof DbException) {
/* 457 */         return (DbException)throwable;
/*     */       }
/* 459 */       return get(90028, paramIOException, new String[] { paramIOException.toString() });
/*     */     } 
/* 461 */     return get(90031, paramIOException, new String[] { paramIOException.toString(), paramString });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SQLException getJdbcSQLException(int paramInt) {
/* 471 */     return getJdbcSQLException(paramInt, (Throwable)null, new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SQLException getJdbcSQLException(int paramInt, String paramString) {
/* 482 */     return getJdbcSQLException(paramInt, (Throwable)null, new String[] { paramString });
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
/*     */   public static SQLException getJdbcSQLException(int paramInt, Throwable paramThrowable, String... paramVarArgs) {
/* 494 */     String str1 = ErrorCode.getState(paramInt);
/* 495 */     String str2 = translate(str1, paramVarArgs);
/* 496 */     return getJdbcSQLException(str2, (String)null, str1, paramInt, paramThrowable, (String)null);
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
/*     */   public static SQLException getJdbcSQLException(String paramString1, String paramString2, String paramString3, int paramInt, Throwable paramThrowable, String paramString4) {
/* 512 */     paramString2 = filterSQL(paramString2);
/*     */     
/* 514 */     switch (paramInt / 1000) {
/*     */       case 2:
/* 516 */         return (SQLException)new JdbcSQLNonTransientException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 7:
/*     */       case 21:
/*     */       case 42:
/*     */       case 54:
/* 521 */         return (SQLException)new JdbcSQLSyntaxErrorException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 8:
/* 523 */         return (SQLException)new JdbcSQLNonTransientConnectionException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 22:
/* 525 */         return (SQLException)new JdbcSQLDataException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 23:
/* 527 */         return (SQLException)new JdbcSQLIntegrityConstraintViolationException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 28:
/* 529 */         return (SQLException)new JdbcSQLInvalidAuthorizationSpecException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 40:
/* 531 */         return (SQLException)new JdbcSQLTransactionRollbackException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */     } 
/*     */     
/* 534 */     switch (paramInt) {
/*     */       case 50000:
/*     */       case 50004:
/*     */       case 90001:
/*     */       case 90002:
/*     */       case 90006:
/*     */       case 90007:
/*     */       case 90019:
/*     */       case 90021:
/*     */       case 90024:
/*     */       case 90025:
/*     */       case 90028:
/*     */       case 90029:
/*     */       case 90031:
/*     */       case 90034:
/*     */       case 90040:
/*     */       case 90044:
/*     */       case 90058:
/*     */       case 90062:
/*     */       case 90063:
/*     */       case 90064:
/*     */       case 90065:
/*     */       case 90096:
/*     */       case 90097:
/*     */       case 90101:
/*     */       case 90102:
/*     */       case 90103:
/*     */       case 90104:
/*     */       case 90105:
/*     */       case 90111:
/*     */       case 90124:
/*     */       case 90125:
/*     */       case 90126:
/*     */       case 90127:
/*     */       case 90128:
/*     */       case 90130:
/*     */       case 90134:
/*     */       case 90140:
/*     */       case 90148:
/* 573 */         return (SQLException)new JdbcSQLNonTransientException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 50100:
/* 575 */         return (SQLException)new JdbcSQLFeatureNotSupportedException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 50200:
/*     */       case 57014:
/*     */       case 90039:
/* 579 */         return (SQLException)new JdbcSQLTimeoutException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 90000:
/*     */       case 90005:
/*     */       case 90015:
/*     */       case 90016:
/*     */       case 90017:
/*     */       case 90022:
/*     */       case 90023:
/*     */       case 90032:
/*     */       case 90033:
/*     */       case 90035:
/*     */       case 90036:
/*     */       case 90037:
/*     */       case 90038:
/*     */       case 90041:
/*     */       case 90042:
/*     */       case 90043:
/*     */       case 90045:
/*     */       case 90052:
/*     */       case 90054:
/*     */       case 90057:
/*     */       case 90059:
/*     */       case 90068:
/*     */       case 90069:
/*     */       case 90070:
/*     */       case 90071:
/*     */       case 90072:
/*     */       case 90073:
/*     */       case 90074:
/*     */       case 90075:
/*     */       case 90076:
/*     */       case 90077:
/*     */       case 90078:
/*     */       case 90079:
/*     */       case 90080:
/*     */       case 90081:
/*     */       case 90082:
/*     */       case 90083:
/*     */       case 90084:
/*     */       case 90085:
/*     */       case 90086:
/*     */       case 90087:
/*     */       case 90089:
/*     */       case 90090:
/*     */       case 90091:
/*     */       case 90106:
/*     */       case 90107:
/*     */       case 90109:
/*     */       case 90110:
/*     */       case 90114:
/*     */       case 90115:
/*     */       case 90116:
/*     */       case 90118:
/*     */       case 90119:
/*     */       case 90120:
/*     */       case 90122:
/*     */       case 90123:
/*     */       case 90129:
/*     */       case 90132:
/*     */       case 90136:
/*     */       case 90137:
/*     */       case 90139:
/*     */       case 90141:
/*     */       case 90145:
/*     */       case 90150:
/*     */       case 90151:
/*     */       case 90152:
/*     */       case 90153:
/*     */       case 90154:
/*     */       case 90155:
/*     */       case 90156:
/*     */       case 90157:
/* 651 */         return (SQLException)new JdbcSQLSyntaxErrorException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 90003:
/*     */       case 90004:
/*     */       case 90008:
/*     */       case 90009:
/*     */       case 90010:
/*     */       case 90012:
/*     */       case 90014:
/*     */       case 90026:
/*     */       case 90027:
/*     */       case 90053:
/*     */       case 90056:
/*     */       case 90095:
/*     */       case 90142:
/* 665 */         return (SQLException)new JdbcSQLDataException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 90011:
/*     */       case 90013:
/*     */       case 90018:
/*     */       case 90020:
/*     */       case 90030:
/*     */       case 90046:
/*     */       case 90047:
/*     */       case 90048:
/*     */       case 90049:
/*     */       case 90050:
/*     */       case 90055:
/*     */       case 90060:
/*     */       case 90061:
/*     */       case 90066:
/*     */       case 90067:
/*     */       case 90088:
/*     */       case 90093:
/*     */       case 90094:
/*     */       case 90098:
/*     */       case 90099:
/*     */       case 90108:
/*     */       case 90113:
/*     */       case 90117:
/*     */       case 90121:
/*     */       case 90133:
/*     */       case 90135:
/*     */       case 90138:
/*     */       case 90144:
/*     */       case 90146:
/*     */       case 90147:
/*     */       case 90149:
/* 697 */         return (SQLException)new JdbcSQLNonTransientConnectionException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */       case 90112:
/*     */       case 90131:
/*     */       case 90143:
/* 701 */         return (SQLException)new JdbcSQLTransientException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */     } 
/*     */     
/* 704 */     return (SQLException)new JdbcSQLException(paramString1, paramString2, paramString3, paramInt, paramThrowable, paramString4);
/*     */   }
/*     */   
/*     */   private static String filterSQL(String paramString) {
/* 708 */     return (paramString == null || !paramString.contains("--hide--")) ? paramString : "-";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String buildMessageForException(JdbcException paramJdbcException) {
/* 718 */     String str = paramJdbcException.getOriginalMessage();
/* 719 */     StringBuilder stringBuilder = new StringBuilder((str != null) ? str : "- ");
/* 720 */     str = paramJdbcException.getSQL();
/* 721 */     if (str != null) {
/* 722 */       stringBuilder.append("; SQL statement:\n").append(str);
/*     */     }
/* 724 */     stringBuilder.append(" [").append(paramJdbcException.getErrorCode()).append('-').append(210).append(']');
/* 725 */     return stringBuilder.toString();
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
/*     */   public static void printNextExceptions(SQLException paramSQLException, PrintWriter paramPrintWriter) {
/* 737 */     byte b = 0;
/* 738 */     while ((paramSQLException = paramSQLException.getNextException()) != null) {
/* 739 */       if (b++ == 100) {
/* 740 */         paramPrintWriter.println("(truncated)");
/*     */         return;
/*     */       } 
/* 743 */       paramPrintWriter.println(paramSQLException.toString());
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
/*     */   public static void printNextExceptions(SQLException paramSQLException, PrintStream paramPrintStream) {
/* 756 */     byte b = 0;
/* 757 */     while ((paramSQLException = paramSQLException.getNextException()) != null) {
/* 758 */       if (b++ == 100) {
/* 759 */         paramPrintStream.println("(truncated)");
/*     */         return;
/*     */       } 
/* 762 */       paramPrintStream.println(paramSQLException.toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   public Object getSource() {
/* 767 */     return this.source;
/*     */   }
/*     */   
/*     */   public void setSource(Object paramObject) {
/* 771 */     this.source = paramObject;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void traceThrowable(Throwable paramThrowable) {
/* 780 */     PrintWriter printWriter = DriverManager.getLogWriter();
/* 781 */     if (printWriter != null)
/* 782 */       paramThrowable.printStackTrace(printWriter); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\message\DbException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */