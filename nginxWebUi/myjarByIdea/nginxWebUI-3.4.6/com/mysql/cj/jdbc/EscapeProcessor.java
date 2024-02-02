package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.util.EscapeTokenizer;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.TimeUtil;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

class EscapeProcessor {
   private static Map<String, String> JDBC_CONVERT_TO_MYSQL_TYPE_MAP;

   public static final Object escapeSQL(String sql, TimeZone connectionTimeZone, boolean serverSupportsFractionalSecond, boolean serverTruncatesFractionalSecond, ExceptionInterceptor exceptionInterceptor) throws SQLException {
      boolean replaceEscapeSequence = false;
      String escapeSequence = null;
      if (sql == null) {
         return null;
      } else {
         int beginBrace = sql.indexOf(123);
         int nextEndBrace = beginBrace == -1 ? -1 : sql.indexOf(125, beginBrace);
         if (nextEndBrace == -1) {
            return sql;
         } else {
            StringBuilder newSql = new StringBuilder();
            EscapeTokenizer escapeTokenizer = new EscapeTokenizer(sql);
            byte usesVariables = 0;
            boolean callingStoredFunction = false;

            while(true) {
               while(true) {
                  String token;
                  String argument;
                  String collapsedToken;
                  int startPos;
                  do {
                     if (!escapeTokenizer.hasMoreTokens()) {
                        token = newSql.toString();
                        if (replaceEscapeSequence) {
                           String lhs;
                           for(collapsedToken = token; collapsedToken.indexOf(escapeSequence) != -1; collapsedToken = lhs + "\\" + argument) {
                              startPos = collapsedToken.indexOf(escapeSequence);
                              lhs = collapsedToken.substring(0, startPos);
                              argument = collapsedToken.substring(startPos + 1, collapsedToken.length());
                           }

                           token = collapsedToken;
                        }

                        EscapeProcessorResult epr = new EscapeProcessorResult();
                        epr.escapedSql = token;
                        epr.callingStoredFunction = callingStoredFunction;
                        if (usesVariables != 1) {
                           if (escapeTokenizer.sawVariableUse()) {
                              epr.usesVariables = 1;
                           } else {
                              epr.usesVariables = 0;
                           }
                        }

                        return epr;
                     }

                     token = escapeTokenizer.nextToken();
                  } while(token.length() == 0);

                  if (token.charAt(0) == '{') {
                     if (!token.endsWith("}")) {
                        throw SQLError.createSQLException(Messages.getString("EscapeProcessor.0", new Object[]{token}), exceptionInterceptor);
                     }

                     if (token.length() > 2) {
                        int nestedBrace = token.indexOf(123, 2);
                        if (nestedBrace != -1) {
                           StringBuilder buf = new StringBuilder(token.substring(0, 1));
                           Object remainingResults = escapeSQL(token.substring(1, token.length() - 1), connectionTimeZone, serverSupportsFractionalSecond, serverTruncatesFractionalSecond, exceptionInterceptor);
                           argument = null;
                           if (remainingResults instanceof String) {
                              argument = (String)remainingResults;
                           } else {
                              argument = ((EscapeProcessorResult)remainingResults).escapedSql;
                              if (usesVariables != 1) {
                                 usesVariables = ((EscapeProcessorResult)remainingResults).usesVariables;
                              }
                           }

                           buf.append(argument);
                           buf.append('}');
                           token = buf.toString();
                        }
                     }

                     collapsedToken = removeWhitespace(token);
                     if (StringUtils.startsWithIgnoreCase(collapsedToken, "{escape")) {
                        try {
                           StringTokenizer st = new StringTokenizer(token, " '");
                           st.nextToken();
                           escapeSequence = st.nextToken();
                           if (escapeSequence.length() < 3) {
                              newSql.append(token);
                           } else {
                              escapeSequence = escapeSequence.substring(1, escapeSequence.length() - 1);
                              replaceEscapeSequence = true;
                           }
                        } catch (NoSuchElementException var24) {
                           newSql.append(token);
                        }
                     } else {
                        int endPos;
                        if (StringUtils.startsWithIgnoreCase(collapsedToken, "{fn")) {
                           startPos = token.toLowerCase().indexOf("fn ") + 3;
                           endPos = token.length() - 1;
                           argument = token.substring(startPos, endPos);
                           if (StringUtils.startsWithIgnoreCaseAndWs(argument, "convert")) {
                              newSql.append(processConvertToken(argument, exceptionInterceptor));
                           } else {
                              newSql.append(argument);
                           }
                        } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{d")) {
                           startPos = token.indexOf(39) + 1;
                           endPos = token.lastIndexOf(39);
                           if (startPos != -1 && endPos != -1) {
                              argument = token.substring(startPos, endPos);

                              try {
                                 StringTokenizer st = new StringTokenizer(argument, " -");
                                 String year4 = st.nextToken();
                                 String month2 = st.nextToken();
                                 String day2 = st.nextToken();
                                 String dateString = "'" + year4 + "-" + month2 + "-" + day2 + "'";
                                 newSql.append(dateString);
                              } catch (NoSuchElementException var23) {
                                 throw SQLError.createSQLException(Messages.getString("EscapeProcessor.1", new Object[]{argument}), "42000", exceptionInterceptor);
                              }
                           } else {
                              newSql.append(token);
                           }
                        } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{ts")) {
                           processTimestampToken(connectionTimeZone, newSql, token, serverSupportsFractionalSecond, serverTruncatesFractionalSecond, exceptionInterceptor);
                        } else if (StringUtils.startsWithIgnoreCase(collapsedToken, "{t")) {
                           processTimeToken(newSql, token, serverSupportsFractionalSecond, exceptionInterceptor);
                        } else if (!StringUtils.startsWithIgnoreCase(collapsedToken, "{call") && !StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")) {
                           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{oj")) {
                              newSql.append(token);
                           } else {
                              newSql.append(token);
                           }
                        } else {
                           startPos = StringUtils.indexOfIgnoreCase(token, "CALL") + 5;
                           endPos = token.length() - 1;
                           if (StringUtils.startsWithIgnoreCase(collapsedToken, "{?=call")) {
                              callingStoredFunction = true;
                              newSql.append("SELECT ");
                              newSql.append(token.substring(startPos, endPos));
                           } else {
                              callingStoredFunction = false;
                              newSql.append("CALL ");
                              newSql.append(token.substring(startPos, endPos));
                           }

                           for(int i = endPos - 1; i >= startPos; --i) {
                              char c = token.charAt(i);
                              if (!Character.isWhitespace(c)) {
                                 if (c != ')') {
                                    newSql.append("()");
                                 }
                                 break;
                              }
                           }
                        }
                     }
                  } else {
                     newSql.append(token);
                  }
               }
            }
         }
      }
   }

   private static void processTimeToken(StringBuilder newSql, String token, boolean serverSupportsFractionalSecond, ExceptionInterceptor exceptionInterceptor) throws SQLException {
      int startPos = token.indexOf(39) + 1;
      int endPos = token.lastIndexOf(39);
      if (startPos != -1 && endPos != -1) {
         String argument = token.substring(startPos, endPos);

         try {
            StringTokenizer st = new StringTokenizer(argument, " :.");
            String hour = st.nextToken();
            String minute = st.nextToken();
            String second = st.nextToken();
            String fractionalSecond = "";
            if (serverSupportsFractionalSecond && st.hasMoreTokens()) {
               fractionalSecond = "." + st.nextToken();
            }

            newSql.append("'");
            newSql.append(hour);
            newSql.append(":");
            newSql.append(minute);
            newSql.append(":");
            newSql.append(second);
            if (serverSupportsFractionalSecond) {
               newSql.append(fractionalSecond);
            }

            newSql.append("'");
         } catch (NoSuchElementException var12) {
            throw SQLError.createSQLException(Messages.getString("EscapeProcessor.3", new Object[]{argument}), "42000", exceptionInterceptor);
         }
      } else {
         newSql.append(token);
      }

   }

   private static void processTimestampToken(TimeZone tz, StringBuilder newSql, String token, boolean serverSupportsFractionalSecond, boolean serverTruncatesFractionalSecond, ExceptionInterceptor exceptionInterceptor) throws SQLException {
      int startPos = token.indexOf(39) + 1;
      int endPos = token.lastIndexOf(39);
      if (startPos != -1 && endPos != -1) {
         String argument = token.substring(startPos, endPos);

         try {
            Timestamp ts = Timestamp.valueOf(argument);
            ts = TimeUtil.adjustNanosPrecision((Timestamp)ts, 6, !serverTruncatesFractionalSecond);
            SimpleDateFormat tsdf = TimeUtil.getSimpleDateFormat((SimpleDateFormat)null, "''yyyy-MM-dd HH:mm:ss", tz);
            newSql.append(tsdf.format(ts));
            if (serverSupportsFractionalSecond && ts.getNanos() > 0) {
               newSql.append('.');
               newSql.append(TimeUtil.formatNanos(ts.getNanos(), 6));
            }

            newSql.append('\'');
         } catch (IllegalArgumentException var11) {
            SQLException sqlEx = SQLError.createSQLException(Messages.getString("EscapeProcessor.2", new Object[]{argument}), "42000", exceptionInterceptor);
            sqlEx.initCause(var11);
            throw sqlEx;
         }
      } else {
         newSql.append(token);
      }

   }

   private static String processConvertToken(String functionToken, ExceptionInterceptor exceptionInterceptor) throws SQLException {
      int firstIndexOfParen = functionToken.indexOf("(");
      if (firstIndexOfParen == -1) {
         throw SQLError.createSQLException(Messages.getString("EscapeProcessor.4", new Object[]{functionToken}), "42000", exceptionInterceptor);
      } else {
         int indexOfComma = functionToken.lastIndexOf(",");
         if (indexOfComma == -1) {
            throw SQLError.createSQLException(Messages.getString("EscapeProcessor.5", new Object[]{functionToken}), "42000", exceptionInterceptor);
         } else {
            int indexOfCloseParen = functionToken.indexOf(41, indexOfComma);
            if (indexOfCloseParen == -1) {
               throw SQLError.createSQLException(Messages.getString("EscapeProcessor.6", new Object[]{functionToken}), "42000", exceptionInterceptor);
            } else {
               String expression = functionToken.substring(firstIndexOfParen + 1, indexOfComma);
               String type = functionToken.substring(indexOfComma + 1, indexOfCloseParen);
               String newType = null;
               String trimmedType = type.trim();
               if (StringUtils.startsWithIgnoreCase(trimmedType, "SQL_")) {
                  trimmedType = trimmedType.substring(4, trimmedType.length());
               }

               newType = (String)JDBC_CONVERT_TO_MYSQL_TYPE_MAP.get(trimmedType.toUpperCase(Locale.ENGLISH));
               if (newType == null) {
                  throw SQLError.createSQLException(Messages.getString("EscapeProcessor.7", new Object[]{type.trim()}), "S1000", exceptionInterceptor);
               } else {
                  int replaceIndex = newType.indexOf("?");
                  StringBuilder castRewrite;
                  if (replaceIndex != -1) {
                     castRewrite = new StringBuilder(newType.substring(0, replaceIndex));
                     castRewrite.append(expression);
                     castRewrite.append(newType.substring(replaceIndex + 1, newType.length()));
                     return castRewrite.toString();
                  } else {
                     castRewrite = new StringBuilder("CAST(");
                     castRewrite.append(expression);
                     castRewrite.append(" AS ");
                     castRewrite.append(newType);
                     castRewrite.append(")");
                     return castRewrite.toString();
                  }
               }
            }
         }
      }
   }

   private static String removeWhitespace(String toCollapse) {
      if (toCollapse == null) {
         return null;
      } else {
         int length = toCollapse.length();
         StringBuilder collapsed = new StringBuilder(length);

         for(int i = 0; i < length; ++i) {
            char c = toCollapse.charAt(i);
            if (!Character.isWhitespace(c)) {
               collapsed.append(c);
            }
         }

         return collapsed.toString();
      }
   }

   static {
      Map<String, String> tempMap = new HashMap();
      tempMap.put("BIGINT", "0 + ?");
      tempMap.put("BINARY", "BINARY");
      tempMap.put("BIT", "0 + ?");
      tempMap.put("CHAR", "CHAR");
      tempMap.put("DATE", "DATE");
      tempMap.put("DECIMAL", "0.0 + ?");
      tempMap.put("DOUBLE", "0.0 + ?");
      tempMap.put("FLOAT", "0.0 + ?");
      tempMap.put("INTEGER", "0 + ?");
      tempMap.put("LONGVARBINARY", "BINARY");
      tempMap.put("LONGVARCHAR", "CONCAT(?)");
      tempMap.put("REAL", "0.0 + ?");
      tempMap.put("SMALLINT", "CONCAT(?)");
      tempMap.put("TIME", "TIME");
      tempMap.put("TIMESTAMP", "DATETIME");
      tempMap.put("TINYINT", "CONCAT(?)");
      tempMap.put("VARBINARY", "BINARY");
      tempMap.put("VARCHAR", "CONCAT(?)");
      JDBC_CONVERT_TO_MYSQL_TYPE_MAP = Collections.unmodifiableMap(tempMap);
   }
}
