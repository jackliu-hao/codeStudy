/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.util.EscapeTokenizer;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.util.TimeUtil;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
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
/*     */ class EscapeProcessor
/*     */ {
/*     */   private static Map<String, String> JDBC_CONVERT_TO_MYSQL_TYPE_MAP;
/*     */   
/*     */   static {
/*  57 */     Map<String, String> tempMap = new HashMap<>();
/*     */     
/*  59 */     tempMap.put("BIGINT", "0 + ?");
/*  60 */     tempMap.put("BINARY", "BINARY");
/*  61 */     tempMap.put("BIT", "0 + ?");
/*  62 */     tempMap.put("CHAR", "CHAR");
/*  63 */     tempMap.put("DATE", "DATE");
/*  64 */     tempMap.put("DECIMAL", "0.0 + ?");
/*  65 */     tempMap.put("DOUBLE", "0.0 + ?");
/*  66 */     tempMap.put("FLOAT", "0.0 + ?");
/*  67 */     tempMap.put("INTEGER", "0 + ?");
/*  68 */     tempMap.put("LONGVARBINARY", "BINARY");
/*  69 */     tempMap.put("LONGVARCHAR", "CONCAT(?)");
/*  70 */     tempMap.put("REAL", "0.0 + ?");
/*  71 */     tempMap.put("SMALLINT", "CONCAT(?)");
/*  72 */     tempMap.put("TIME", "TIME");
/*  73 */     tempMap.put("TIMESTAMP", "DATETIME");
/*  74 */     tempMap.put("TINYINT", "CONCAT(?)");
/*  75 */     tempMap.put("VARBINARY", "BINARY");
/*  76 */     tempMap.put("VARCHAR", "CONCAT(?)");
/*     */     
/*  78 */     JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Object escapeSQL(String sql, TimeZone connectionTimeZone, boolean serverSupportsFractionalSecond, boolean serverTruncatesFractionalSecond, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/* 103 */     boolean replaceEscapeSequence = false;
/* 104 */     String escapeSequence = null;
/*     */     
/* 106 */     if (sql == null) {
/* 107 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 113 */     int beginBrace = sql.indexOf('{');
/* 114 */     int nextEndBrace = (beginBrace == -1) ? -1 : sql.indexOf('}', beginBrace);
/*     */     
/* 116 */     if (nextEndBrace == -1) {
/* 117 */       return sql;
/*     */     }
/*     */     
/* 120 */     StringBuilder newSql = new StringBuilder();
/*     */     
/* 122 */     EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
/*     */     
/* 124 */     byte usesVariables = 0;
/* 125 */     boolean callingStoredFunction = false;
/*     */     
/* 127 */     while (escapeTokenizer.hasMoreTokens()) {
/* 128 */       String token = escapeTokenizer.nextToken();
/*     */       
/* 130 */       if (token.length() != 0) {
/* 131 */         if (token.charAt(0) == '{') {
/*     */           
/* 133 */           if (!token.endsWith("}")) {
/* 134 */             throw SQLError.createSQLException(Messages.getString("EscapeProcessor.0", new Object[] { token }), exceptionInterceptor);
/*     */           }
/*     */           
/* 137 */           if (token.length() > 2) {
/* 138 */             int nestedBrace = token.indexOf('{', 2);
/*     */             
/* 140 */             if (nestedBrace != -1) {
/* 141 */               StringBuilder buf = new StringBuilder(token.substring(0, 1));
/*     */               
/* 143 */               Object remainingResults = escapeSQL(token.substring(1, token.length() - 1), connectionTimeZone, serverSupportsFractionalSecond, serverTruncatesFractionalSecond, exceptionInterceptor);
/*     */ 
/*     */               
/* 146 */               String remaining = null;
/*     */               
/* 148 */               if (remainingResults instanceof String) {
/* 149 */                 remaining = (String)remainingResults;
/*     */               } else {
/* 151 */                 remaining = ((EscapeProcessorResult)remainingResults).escapedSql;
/*     */                 
/* 153 */                 if (usesVariables != 1) {
/* 154 */                   usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
/*     */                 }
/*     */               } 
/*     */               
/* 158 */               buf.append(remaining);
/*     */               
/* 160 */               buf.append('}');
/*     */               
/* 162 */               token = buf.toString();
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 168 */           String collapsedToken = removeWhitespace(token);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 173 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{escape")) {
/*     */             try {
/* 175 */               StringTokenizer st = new StringTokenizer(token, " '");
/* 176 */               st.nextToken();
/* 177 */               escapeSequence = st.nextToken();
/*     */               
/* 179 */               if (escapeSequence.length() < 3) {
/* 180 */                 newSql.append(token);
/*     */                 continue;
/*     */               } 
/* 183 */               escapeSequence = escapeSequence.substring(1, escapeSequence.length() - 1);
/* 184 */               replaceEscapeSequence = true;
/*     */             }
/* 186 */             catch (NoSuchElementException e) {
/* 187 */               newSql.append(token);
/*     */             }  continue;
/* 189 */           }  if (StringUtils.startsWithIgnoreCase(collapsedToken, "{fn")) {
/* 190 */             int startPos = token.toLowerCase().indexOf("fn ") + 3;
/* 191 */             int endPos = token.length() - 1;
/*     */             
/* 193 */             String fnToken = token.substring(startPos, endPos);
/*     */ 
/*     */ 
/*     */             
/* 197 */             if (StringUtils.startsWithIgnoreCaseAndWs(fnToken, "convert")) {
/* 198 */               newSql.append(processConvertToken(fnToken, exceptionInterceptor));
/*     */               continue;
/*     */             } 
/* 201 */             newSql.append(fnToken); continue;
/*     */           } 
/* 203 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{d")) {
/* 204 */             int startPos = token.indexOf('\'') + 1;
/* 205 */             int endPos = token.lastIndexOf('\'');
/*     */             
/* 207 */             if (startPos == -1 || endPos == -1) {
/* 208 */               newSql.append(token);
/*     */               continue;
/*     */             } 
/* 211 */             String argument = token.substring(startPos, endPos);
/*     */             
/*     */             try {
/* 214 */               StringTokenizer st = new StringTokenizer(argument, " -");
/* 215 */               String year4 = st.nextToken();
/* 216 */               String month2 = st.nextToken();
/* 217 */               String day2 = st.nextToken();
/* 218 */               String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
/* 219 */               newSql.append(dateString);
/* 220 */             } catch (NoSuchElementException e) {
/* 221 */               throw SQLError.createSQLException(Messages.getString("EscapeProcessor.1", new Object[] { argument }), "42000", exceptionInterceptor);
/*     */             } 
/*     */             continue;
/*     */           } 
/* 225 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{ts")) {
/* 226 */             processTimestampToken(connectionTimeZone, newSql, token, serverSupportsFractionalSecond, serverTruncatesFractionalSecond, exceptionInterceptor); continue;
/*     */           } 
/* 228 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{t")) {
/* 229 */             processTimeToken(newSql, token, serverSupportsFractionalSecond, exceptionInterceptor); continue;
/* 230 */           }  if (StringUtils.startsWithIgnoreCase(collapsedToken, "{call") || StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")) {
/*     */             
/* 232 */             int startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
/* 233 */             int endPos = token.length() - 1;
/*     */             
/* 235 */             if (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")) {
/* 236 */               callingStoredFunction = true;
/* 237 */               newSql.append("SELECT ");
/* 238 */               newSql.append(token.substring(startPos, endPos));
/*     */             } else {
/* 240 */               callingStoredFunction = false;
/* 241 */               newSql.append("CALL ");
/* 242 */               newSql.append(token.substring(startPos, endPos));
/*     */             } 
/*     */             
/* 245 */             for (int i = endPos - 1; i >= startPos; ) {
/* 246 */               char c = token.charAt(i);
/*     */               
/* 248 */               if (Character.isWhitespace(c)) {
/*     */                 i--;
/*     */                 continue;
/*     */               } 
/* 252 */               if (c != ')') {
/* 253 */                 newSql.append("()");
/*     */               }
/*     */             } 
/*     */             continue;
/*     */           } 
/* 258 */           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{oj")) {
/*     */             
/* 260 */             newSql.append(token);
/*     */             continue;
/*     */           } 
/* 263 */           newSql.append(token);
/*     */           continue;
/*     */         } 
/* 266 */         newSql.append(token);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 271 */     String escapedSql = newSql.toString();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 276 */     if (replaceEscapeSequence) {
/* 277 */       String currentSql = escapedSql;
/*     */       
/* 279 */       while (currentSql.indexOf(escapeSequence) != -1) {
/* 280 */         int escapePos = currentSql.indexOf(escapeSequence);
/* 281 */         String lhs = currentSql.substring(0, escapePos);
/* 282 */         String rhs = currentSql.substring(escapePos + 1, currentSql.length());
/* 283 */         currentSql = lhs + "\\" + rhs;
/*     */       } 
/*     */       
/* 286 */       escapedSql = currentSql;
/*     */     } 
/*     */     
/* 289 */     EscapeProcessorResult epr = new EscapeProcessorResult();
/* 290 */     epr.escapedSql = escapedSql;
/* 291 */     epr.callingStoredFunction = callingStoredFunction;
/*     */     
/* 293 */     if (usesVariables != 1) {
/* 294 */       if (escapeTokenizer.sawVariableUse()) {
/* 295 */         epr.usesVariables = 1;
/*     */       } else {
/* 297 */         epr.usesVariables = 0;
/*     */       } 
/*     */     }
/*     */     
/* 301 */     return epr;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void processTimeToken(StringBuilder newSql, String token, boolean serverSupportsFractionalSecond, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/* 306 */     int startPos = token.indexOf('\'') + 1;
/* 307 */     int endPos = token.lastIndexOf('\'');
/*     */     
/* 309 */     if (startPos == -1 || endPos == -1) {
/* 310 */       newSql.append(token);
/*     */     } else {
/*     */       
/* 313 */       String argument = token.substring(startPos, endPos);
/*     */       
/*     */       try {
/* 316 */         StringTokenizer st = new StringTokenizer(argument, " :.");
/* 317 */         String hour = st.nextToken();
/* 318 */         String minute = st.nextToken();
/* 319 */         String second = st.nextToken();
/*     */         
/* 321 */         String fractionalSecond = "";
/*     */         
/* 323 */         if (serverSupportsFractionalSecond && st.hasMoreTokens()) {
/* 324 */           fractionalSecond = "." + st.nextToken();
/*     */         }
/*     */         
/* 327 */         newSql.append("'");
/* 328 */         newSql.append(hour);
/* 329 */         newSql.append(":");
/* 330 */         newSql.append(minute);
/* 331 */         newSql.append(":");
/* 332 */         newSql.append(second);
/* 333 */         if (serverSupportsFractionalSecond) {
/* 334 */           newSql.append(fractionalSecond);
/*     */         }
/* 336 */         newSql.append("'");
/* 337 */       } catch (NoSuchElementException e) {
/* 338 */         throw SQLError.createSQLException(Messages.getString("EscapeProcessor.3", new Object[] { argument }), "42000", exceptionInterceptor);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void processTimestampToken(TimeZone tz, StringBuilder newSql, String token, boolean serverSupportsFractionalSecond, boolean serverTruncatesFractionalSecond, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/* 346 */     int startPos = token.indexOf('\'') + 1;
/* 347 */     int endPos = token.lastIndexOf('\'');
/*     */     
/* 349 */     if (startPos == -1 || endPos == -1) {
/* 350 */       newSql.append(token);
/*     */     } else {
/*     */       
/* 353 */       String argument = token.substring(startPos, endPos);
/*     */       
/*     */       try {
/* 356 */         Timestamp ts = Timestamp.valueOf(argument);
/* 357 */         ts = TimeUtil.adjustNanosPrecision(ts, 6, !serverTruncatesFractionalSecond);
/* 358 */         SimpleDateFormat tsdf = TimeUtil.getSimpleDateFormat(null, "''yyyy-MM-dd HH:mm:ss", tz);
/*     */         
/* 360 */         newSql.append(tsdf.format(ts));
/*     */         
/* 362 */         if (serverSupportsFractionalSecond && ts.getNanos() > 0) {
/* 363 */           newSql.append('.');
/* 364 */           newSql.append(TimeUtil.formatNanos(ts.getNanos(), 6));
/*     */         } 
/*     */         
/* 367 */         newSql.append('\'');
/* 368 */       } catch (IllegalArgumentException illegalArgumentException) {
/* 369 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("EscapeProcessor.2", new Object[] { argument }), "42000", exceptionInterceptor);
/*     */         
/* 371 */         sqlEx.initCause(illegalArgumentException);
/*     */         
/* 373 */         throw sqlEx;
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
/*     */   private static String processConvertToken(String functionToken, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/* 421 */     int firstIndexOfParen = functionToken.indexOf("(");
/*     */     
/* 423 */     if (firstIndexOfParen == -1) {
/* 424 */       throw SQLError.createSQLException(Messages.getString("EscapeProcessor.4", new Object[] { functionToken }), "42000", exceptionInterceptor);
/*     */     }
/*     */ 
/*     */     
/* 428 */     int indexOfComma = functionToken.lastIndexOf(",");
/*     */     
/* 430 */     if (indexOfComma == -1) {
/* 431 */       throw SQLError.createSQLException(Messages.getString("EscapeProcessor.5", new Object[] { functionToken }), "42000", exceptionInterceptor);
/*     */     }
/*     */ 
/*     */     
/* 435 */     int indexOfCloseParen = functionToken.indexOf(')', indexOfComma);
/*     */     
/* 437 */     if (indexOfCloseParen == -1) {
/* 438 */       throw SQLError.createSQLException(Messages.getString("EscapeProcessor.6", new Object[] { functionToken }), "42000", exceptionInterceptor);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 443 */     String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
/* 444 */     String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
/*     */     
/* 446 */     String newType = null;
/*     */     
/* 448 */     String trimmedType = type.trim();
/*     */     
/* 450 */     if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
/* 451 */       trimmedType = trimmedType.substring(4, trimmedType.length());
/*     */     }
/*     */     
/* 454 */     newType = JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
/*     */     
/* 456 */     if (newType == null) {
/* 457 */       throw SQLError.createSQLException(Messages.getString("EscapeProcessor.7", new Object[] { type.trim() }), "S1000", exceptionInterceptor);
/*     */     }
/*     */ 
/*     */     
/* 461 */     int replaceIndex = newType.indexOf("?");
/*     */     
/* 463 */     if (replaceIndex != -1) {
/* 464 */       StringBuilder convertRewrite = new StringBuilder(newType.substring(0, replaceIndex));
/* 465 */       convertRewrite.append(expression);
/* 466 */       convertRewrite.append(newType.substring(replaceIndex + 1, newType.length()));
/*     */       
/* 468 */       return convertRewrite.toString();
/*     */     } 
/*     */     
/* 471 */     StringBuilder castRewrite = new StringBuilder("CAST(");
/* 472 */     castRewrite.append(expression);
/* 473 */     castRewrite.append(" AS ");
/* 474 */     castRewrite.append(newType);
/* 475 */     castRewrite.append(")");
/*     */     
/* 477 */     return castRewrite.toString();
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
/*     */   private static String removeWhitespace(String toCollapse) {
/* 491 */     if (toCollapse == null) {
/* 492 */       return null;
/*     */     }
/*     */     
/* 495 */     int length = toCollapse.length();
/*     */     
/* 497 */     StringBuilder collapsed = new StringBuilder(length);
/*     */     
/* 499 */     for (int i = 0; i < length; i++) {
/* 500 */       char c = toCollapse.charAt(i);
/*     */       
/* 502 */       if (!Character.isWhitespace(c)) {
/* 503 */         collapsed.append(c);
/*     */       }
/*     */     } 
/*     */     
/* 507 */     return collapsed.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\EscapeProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */