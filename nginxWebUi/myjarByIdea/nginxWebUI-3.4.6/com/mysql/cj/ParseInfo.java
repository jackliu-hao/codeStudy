package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.SearchMode;
import com.mysql.cj.util.StringInspector;
import com.mysql.cj.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ParseInfo {
   private static final String OPENING_MARKERS = "`'\"";
   private static final String CLOSING_MARKERS = "`'\"";
   private static final String OVERRIDING_MARKERS = "";
   private static final String[] ON_DUPLICATE_KEY_UPDATE_CLAUSE = new String[]{"ON", "DUPLICATE", "KEY", "UPDATE"};
   private static final String[] LOAD_DATA_CLAUSE = new String[]{"LOAD", "DATA"};
   private String charEncoding;
   private int statementLength;
   private int statementStartPos;
   private char firstStmtChar;
   private QueryReturnType queryReturnType;
   private boolean hasParameters;
   private boolean parametersInDuplicateKeyClause;
   private boolean isLoadData;
   private boolean isOnDuplicateKeyUpdate;
   private int locationOfOnDuplicateKeyUpdate;
   private int numberOfQueries;
   private boolean canRewriteAsMultiValueInsert;
   private String valuesClause;
   private ParseInfo batchHead;
   private ParseInfo batchValues;
   private ParseInfo batchODKUClause;
   private byte[][] staticSql;

   private ParseInfo(byte[][] staticSql, char firstStmtChar, QueryReturnType queryReturnType, boolean isLoadData, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementLength, int statementStartPos) {
      this.statementLength = 0;
      this.statementStartPos = 0;
      this.firstStmtChar = 0;
      this.queryReturnType = null;
      this.hasParameters = false;
      this.parametersInDuplicateKeyClause = false;
      this.isLoadData = false;
      this.isOnDuplicateKeyUpdate = false;
      this.locationOfOnDuplicateKeyUpdate = -1;
      this.numberOfQueries = 1;
      this.canRewriteAsMultiValueInsert = false;
      this.staticSql = (byte[][])null;
      this.firstStmtChar = firstStmtChar;
      this.queryReturnType = queryReturnType;
      this.isLoadData = isLoadData;
      this.isOnDuplicateKeyUpdate = isOnDuplicateKeyUpdate;
      this.locationOfOnDuplicateKeyUpdate = locationOfOnDuplicateKeyUpdate;
      this.statementLength = statementLength;
      this.statementStartPos = statementStartPos;
      this.staticSql = staticSql;
   }

   public ParseInfo(String sql, Session session, String encoding) {
      this(sql, session, encoding, true);
   }

   public ParseInfo(String sql, Session session, String encoding, boolean buildRewriteInfo) {
      this.statementLength = 0;
      this.statementStartPos = 0;
      this.firstStmtChar = 0;
      this.queryReturnType = null;
      this.hasParameters = false;
      this.parametersInDuplicateKeyClause = false;
      this.isLoadData = false;
      this.isOnDuplicateKeyUpdate = false;
      this.locationOfOnDuplicateKeyUpdate = -1;
      this.numberOfQueries = 1;
      this.canRewriteAsMultiValueInsert = false;
      this.staticSql = (byte[][])null;

      try {
         if (sql == null) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.61"), session.getExceptionInterceptor());
         }

         this.charEncoding = encoding;
         this.statementLength = sql.length();
         boolean noBackslashEscapes = session.getServerSession().isNoBackslashEscapesSet();
         this.queryReturnType = getQueryReturnType(sql, noBackslashEscapes);
         this.statementStartPos = indexOfStartOfStatement(sql, session.getServerSession().isNoBackslashEscapesSet());
         if (this.statementStartPos == -1) {
            this.statementStartPos = this.statementLength;
         }

         int statementKeywordPos = StringUtils.indexOfNextAlphanumericChar(this.statementStartPos, sql, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
         if (statementKeywordPos >= 0) {
            this.firstStmtChar = Character.toUpperCase(sql.charAt(statementKeywordPos));
         }

         this.isLoadData = this.firstStmtChar == 'L' && StringUtils.indexOfIgnoreCase(this.statementStartPos, sql, LOAD_DATA_CLAUSE, "`'\"", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL) == this.statementStartPos;
         if (this.firstStmtChar == 'I' && StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", this.statementStartPos)) {
            this.locationOfOnDuplicateKeyUpdate = getOnDuplicateKeyLocation(sql, (Boolean)session.getPropertySet().getBooleanProperty(PropertyKey.dontCheckOnDuplicateKeyUpdateInSQL).getValue(), (Boolean)session.getPropertySet().getBooleanProperty(PropertyKey.rewriteBatchedStatements).getValue(), session.getServerSession().isNoBackslashEscapesSet());
            this.isOnDuplicateKeyUpdate = this.locationOfOnDuplicateKeyUpdate != -1;
         }

         StringInspector strInspector = new StringInspector(sql, this.statementStartPos, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL);
         int pos = this.statementStartPos;
         int prevParamEnd = 0;
         ArrayList<int[]> endpointList = new ArrayList();

         while((pos = strInspector.indexOfNextNonWsChar()) >= 0) {
            if (strInspector.getChar() == '?') {
               endpointList.add(new int[]{prevParamEnd, pos});
               prevParamEnd = pos + 1;
               if (this.isOnDuplicateKeyUpdate && pos > this.locationOfOnDuplicateKeyUpdate) {
                  this.parametersInDuplicateKeyClause = true;
               }

               strInspector.incrementPosition();
            } else if (strInspector.getChar() == ';') {
               strInspector.incrementPosition();
               pos = strInspector.indexOfNextNonWsChar();
               if (pos > 0) {
                  ++this.numberOfQueries;
               }
            } else {
               strInspector.incrementPosition();
            }
         }

         endpointList.add(new int[]{prevParamEnd, this.statementLength});
         this.staticSql = new byte[endpointList.size()][];
         this.hasParameters = this.staticSql.length > 1;

         for(int i = 0; i < this.staticSql.length; ++i) {
            int[] ep = (int[])endpointList.get(i);
            int end = ep[1];
            int begin = ep[0];
            int len = end - begin;
            if (this.isLoadData) {
               this.staticSql[i] = StringUtils.getBytes(sql, begin, len);
            } else if (encoding != null) {
               this.staticSql[i] = StringUtils.getBytes(sql, begin, len, encoding);
            } else {
               byte[] buf = new byte[len];

               for(int j = 0; j < len; ++j) {
                  buf[j] = (byte)sql.charAt(begin + j);
               }

               this.staticSql[i] = buf;
            }
         }
      } catch (Exception var18) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.62", new Object[]{sql}), var18, session.getExceptionInterceptor());
      }

      if (buildRewriteInfo) {
         this.canRewriteAsMultiValueInsert = this.numberOfQueries == 1 && !this.parametersInDuplicateKeyClause && canRewrite(sql, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementStartPos);
         if (this.canRewriteAsMultiValueInsert && (Boolean)session.getPropertySet().getBooleanProperty(PropertyKey.rewriteBatchedStatements).getValue()) {
            this.buildRewriteBatchedParams(sql, session, encoding);
         }
      }

   }

   public int getNumberOfQueries() {
      return this.numberOfQueries;
   }

   public byte[][] getStaticSql() {
      return this.staticSql;
   }

   public String getValuesClause() {
      return this.valuesClause;
   }

   public int getLocationOfOnDuplicateKeyUpdate() {
      return this.locationOfOnDuplicateKeyUpdate;
   }

   public QueryReturnType getQueryReturnType() {
      return this.queryReturnType;
   }

   public boolean canRewriteAsMultiValueInsertAtSqlLevel() {
      return this.canRewriteAsMultiValueInsert;
   }

   public boolean containsOnDuplicateKeyUpdateInSQL() {
      return this.isOnDuplicateKeyUpdate;
   }

   private void buildRewriteBatchedParams(String sql, Session session, String encoding) {
      this.valuesClause = this.extractValuesClause(sql, session.getIdentifierQuoteString());
      String odkuClause = this.isOnDuplicateKeyUpdate ? sql.substring(this.locationOfOnDuplicateKeyUpdate) : null;
      String headSql = null;
      if (this.isOnDuplicateKeyUpdate) {
         headSql = sql.substring(0, this.locationOfOnDuplicateKeyUpdate);
      } else {
         headSql = sql;
      }

      this.batchHead = new ParseInfo(headSql, session, encoding, false);
      this.batchValues = new ParseInfo("," + this.valuesClause, session, encoding, false);
      this.batchODKUClause = null;
      if (odkuClause != null && odkuClause.length() > 0) {
         this.batchODKUClause = new ParseInfo("," + this.valuesClause + " " + odkuClause, session, encoding, false);
      }

   }

   private String extractValuesClause(String sql, String quoteCharStr) {
      int indexOfValues = -1;
      int valuesSearchStart = this.statementStartPos;
      int indexOfFirstEqualsChar = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "=", quoteCharStr, quoteCharStr, SearchMode.__MRK_COM_MYM_HNT_WS);

      int c;
      while(indexOfValues == -1) {
         if (quoteCharStr.length() > 0) {
            indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUE", quoteCharStr, quoteCharStr, SearchMode.__MRK_COM_MYM_HNT_WS);
         } else {
            indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUE");
         }

         if (indexOfFirstEqualsChar > 0 && indexOfValues > indexOfFirstEqualsChar) {
            indexOfValues = -1;
         }

         if (indexOfValues <= 0) {
            break;
         }

         c = sql.charAt(indexOfValues - 1);
         if (!Character.isWhitespace((char)c) && c != 41 && c != 96) {
            valuesSearchStart = indexOfValues + 6;
            indexOfValues = -1;
         } else {
            c = sql.charAt(indexOfValues + 6);
            if (!Character.isWhitespace((char)c) && c != 40) {
               valuesSearchStart = indexOfValues + 6;
               indexOfValues = -1;
            }
         }
      }

      if (indexOfValues == -1) {
         return null;
      } else {
         c = sql.indexOf(40, indexOfValues + 6);
         if (c == -1) {
            return null;
         } else {
            int endOfValuesClause = this.isOnDuplicateKeyUpdate ? this.locationOfOnDuplicateKeyUpdate : sql.length();
            return sql.substring(c, endOfValuesClause);
         }
      }
   }

   public synchronized ParseInfo getParseInfoForBatch(int numBatch) {
      AppendingBatchVisitor apv = new AppendingBatchVisitor();
      this.buildInfoForBatch(numBatch, apv);
      ParseInfo batchParseInfo = new ParseInfo(apv.getStaticSqlStrings(), this.firstStmtChar, this.queryReturnType, this.isLoadData, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementLength, this.statementStartPos);
      return batchParseInfo;
   }

   public String getSqlForBatch(int numBatch) throws UnsupportedEncodingException {
      ParseInfo batchInfo = this.getParseInfoForBatch(numBatch);
      return batchInfo.getSqlForBatch();
   }

   public String getSqlForBatch() throws UnsupportedEncodingException {
      int size = 0;
      byte[][] sqlStrings = this.staticSql;
      int sqlStringsLength = sqlStrings.length;

      for(int i = 0; i < sqlStringsLength; ++i) {
         size += sqlStrings[i].length;
         ++size;
      }

      StringBuilder buf = new StringBuilder(size);

      for(int i = 0; i < sqlStringsLength - 1; ++i) {
         buf.append(StringUtils.toString(sqlStrings[i], this.charEncoding));
         buf.append("?");
      }

      buf.append(StringUtils.toString(sqlStrings[sqlStringsLength - 1]));
      return buf.toString();
   }

   private void buildInfoForBatch(int numBatch, BatchVisitor visitor) {
      int numValueRepeats;
      byte[] endOfHead;
      int numValueRepeats;
      if (!this.hasParameters) {
         if (numBatch == 1) {
            visitor.append(this.staticSql[0]);
         } else {
            byte[] headStaticSql = this.batchHead.staticSql[0];
            visitor.append(headStaticSql).increment();
            numValueRepeats = numBatch - 1;
            if (this.batchODKUClause != null) {
               --numValueRepeats;
            }

            endOfHead = this.batchValues.staticSql[0];

            for(numValueRepeats = 0; numValueRepeats < numValueRepeats; ++numValueRepeats) {
               visitor.mergeWithLast(endOfHead).increment();
            }

            if (this.batchODKUClause != null) {
               byte[] batchOdkuStaticSql = this.batchODKUClause.staticSql[0];
               visitor.mergeWithLast(batchOdkuStaticSql).increment();
            }

         }
      } else {
         byte[][] headStaticSql = this.batchHead.staticSql;
         numValueRepeats = headStaticSql.length;
         endOfHead = headStaticSql[numValueRepeats - 1];

         for(numValueRepeats = 0; numValueRepeats < numValueRepeats - 1; ++numValueRepeats) {
            visitor.append(headStaticSql[numValueRepeats]).increment();
         }

         numValueRepeats = numBatch - 1;
         if (this.batchODKUClause != null) {
            --numValueRepeats;
         }

         byte[][] valuesStaticSql = this.batchValues.staticSql;
         int valuesStaticSqlLength = valuesStaticSql.length;
         byte[] beginOfValues = valuesStaticSql[0];
         byte[] endOfValues = valuesStaticSql[valuesStaticSqlLength - 1];

         int batchOdkuStaticSqlLength;
         for(int i = 0; i < numValueRepeats; ++i) {
            visitor.merge(endOfValues, beginOfValues).increment();

            for(batchOdkuStaticSqlLength = 1; batchOdkuStaticSqlLength < valuesStaticSqlLength - 1; ++batchOdkuStaticSqlLength) {
               visitor.append(valuesStaticSql[batchOdkuStaticSqlLength]).increment();
            }
         }

         if (this.batchODKUClause != null) {
            byte[][] batchOdkuStaticSql = this.batchODKUClause.staticSql;
            batchOdkuStaticSqlLength = batchOdkuStaticSql.length;
            byte[] beginOfOdku = batchOdkuStaticSql[0];
            byte[] endOfOdku = batchOdkuStaticSql[batchOdkuStaticSqlLength - 1];
            if (numBatch > 1) {
               visitor.merge(numValueRepeats > 0 ? endOfValues : endOfHead, beginOfOdku).increment();

               for(int i = 1; i < batchOdkuStaticSqlLength; ++i) {
                  visitor.append(batchOdkuStaticSql[i]).increment();
               }
            } else {
               visitor.append(endOfOdku).increment();
            }
         } else {
            visitor.append(endOfHead);
         }

      }
   }

   public boolean isLoadData() {
      return this.isLoadData;
   }

   public char getFirstStmtChar() {
      return this.firstStmtChar;
   }

   public static int indexOfStartOfStatement(String sql, boolean noBackslashEscapes) {
      return StringUtils.indexOfNextNonWsChar(0, sql, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
   }

   public static int indexOfStatementKeyword(String sql, boolean noBackslashEscapes) {
      return StringUtils.indexOfNextAlphanumericChar(0, sql, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
   }

   public static char firstCharOfStatementUc(String sql, boolean noBackslashEscapes) {
      int statementKeywordPos = indexOfStatementKeyword(sql, noBackslashEscapes);
      return statementKeywordPos == -1 ? '\u0000' : Character.toUpperCase(sql.charAt(statementKeywordPos));
   }

   public static boolean isReadOnlySafeQuery(String sql, boolean noBackslashEscapes) {
      int statementKeywordPos = indexOfStatementKeyword(sql, noBackslashEscapes);
      if (statementKeywordPos == -1) {
         return true;
      } else {
         char firstStatementChar = Character.toUpperCase(sql.charAt(statementKeywordPos));
         if (firstStatementChar == 'A' && StringUtils.startsWithIgnoreCaseAndWs(sql, "ALTER", statementKeywordPos)) {
            return false;
         } else if (firstStatementChar == 'C' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "CHANGE", statementKeywordPos) || StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE", statementKeywordPos))) {
            return false;
         } else if (firstStatementChar != 'D' || !StringUtils.startsWithIgnoreCaseAndWs(sql, "DELETE", statementKeywordPos) && !StringUtils.startsWithIgnoreCaseAndWs(sql, "DROP", statementKeywordPos)) {
            if (firstStatementChar == 'G' && StringUtils.startsWithIgnoreCaseAndWs(sql, "GRANT", statementKeywordPos)) {
               return false;
            } else if (firstStatementChar == 'I' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "IMPORT", statementKeywordPos) || StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementKeywordPos) || StringUtils.startsWithIgnoreCaseAndWs(sql, "INSTALL", statementKeywordPos))) {
               return false;
            } else if (firstStatementChar == 'L' && StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD", statementKeywordPos)) {
               return false;
            } else if (firstStatementChar == 'O' && StringUtils.startsWithIgnoreCaseAndWs(sql, "OPTIMIZE", statementKeywordPos)) {
               return false;
            } else if (firstStatementChar != 'R' || !StringUtils.startsWithIgnoreCaseAndWs(sql, "RENAME", statementKeywordPos) && !StringUtils.startsWithIgnoreCaseAndWs(sql, "REPAIR", statementKeywordPos) && !StringUtils.startsWithIgnoreCaseAndWs(sql, "REPLACE", statementKeywordPos) && !StringUtils.startsWithIgnoreCaseAndWs(sql, "RESET", statementKeywordPos) && !StringUtils.startsWithIgnoreCaseAndWs(sql, "REVOKE", statementKeywordPos)) {
               if (firstStatementChar == 'T' && StringUtils.startsWithIgnoreCaseAndWs(sql, "TRUNCATE", statementKeywordPos)) {
                  return false;
               } else if (firstStatementChar != 'U' || !StringUtils.startsWithIgnoreCaseAndWs(sql, "UNINSTALL", statementKeywordPos) && !StringUtils.startsWithIgnoreCaseAndWs(sql, "UPDATE", statementKeywordPos)) {
                  if (firstStatementChar == 'W' && StringUtils.startsWithIgnoreCaseAndWs(sql, "WITH", statementKeywordPos)) {
                     String context = getContextForWithStatement(sql, noBackslashEscapes);
                     return context == null || !context.equalsIgnoreCase("DELETE") && !context.equalsIgnoreCase("UPDATE");
                  } else {
                     return true;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public static QueryReturnType getQueryReturnType(String sql, boolean noBackslashEscapes) {
      int statementKeywordPos = indexOfStatementKeyword(sql, noBackslashEscapes);
      if (statementKeywordPos == -1) {
         return QueryReturnType.NONE;
      } else {
         char firstStatementChar = Character.toUpperCase(sql.charAt(statementKeywordPos));
         if (firstStatementChar == 'A' && StringUtils.startsWithIgnoreCaseAndWs(sql, "ANALYZE", statementKeywordPos)) {
            return QueryReturnType.PRODUCES_RESULT_SET;
         } else if (firstStatementChar == 'C' && StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL", statementKeywordPos)) {
            return QueryReturnType.MAY_PRODUCE_RESULT_SET;
         } else if (firstStatementChar == 'C' && StringUtils.startsWithIgnoreCaseAndWs(sql, "CHECK", statementKeywordPos)) {
            return QueryReturnType.PRODUCES_RESULT_SET;
         } else if (firstStatementChar == 'D' && StringUtils.startsWithIgnoreCaseAndWs(sql, "DESC", statementKeywordPos)) {
            return QueryReturnType.PRODUCES_RESULT_SET;
         } else if (firstStatementChar == 'E' && StringUtils.startsWithIgnoreCaseAndWs(sql, "EXPLAIN", statementKeywordPos)) {
            return QueryReturnType.PRODUCES_RESULT_SET;
         } else if (firstStatementChar == 'E' && StringUtils.startsWithIgnoreCaseAndWs(sql, "EXECUTE", statementKeywordPos)) {
            return QueryReturnType.MAY_PRODUCE_RESULT_SET;
         } else if (firstStatementChar == 'H' && StringUtils.startsWithIgnoreCaseAndWs(sql, "HELP", statementKeywordPos)) {
            return QueryReturnType.PRODUCES_RESULT_SET;
         } else if (firstStatementChar == 'O' && StringUtils.startsWithIgnoreCaseAndWs(sql, "OPTIMIZE", statementKeywordPos)) {
            return QueryReturnType.PRODUCES_RESULT_SET;
         } else if (firstStatementChar == 'R' && StringUtils.startsWithIgnoreCaseAndWs(sql, "REPAIR", statementKeywordPos)) {
            return QueryReturnType.PRODUCES_RESULT_SET;
         } else if (firstStatementChar != 'S' || !StringUtils.startsWithIgnoreCaseAndWs(sql, "SELECT", statementKeywordPos) && !StringUtils.startsWithIgnoreCaseAndWs(sql, "SHOW", statementKeywordPos)) {
            if (firstStatementChar == 'T' && StringUtils.startsWithIgnoreCaseAndWs(sql, "TABLE", statementKeywordPos)) {
               return QueryReturnType.PRODUCES_RESULT_SET;
            } else if (firstStatementChar == 'V' && StringUtils.startsWithIgnoreCaseAndWs(sql, "VALUES", statementKeywordPos)) {
               return QueryReturnType.PRODUCES_RESULT_SET;
            } else if (firstStatementChar == 'W' && StringUtils.startsWithIgnoreCaseAndWs(sql, "WITH", statementKeywordPos)) {
               String context = getContextForWithStatement(sql, noBackslashEscapes);
               if (context == null) {
                  return QueryReturnType.MAY_PRODUCE_RESULT_SET;
               } else {
                  return !context.equalsIgnoreCase("SELECT") && !context.equalsIgnoreCase("TABLE") && !context.equalsIgnoreCase("VALUES") ? QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET : QueryReturnType.PRODUCES_RESULT_SET;
               }
            } else {
               return firstStatementChar == 'X' && StringUtils.indexOfIgnoreCase(statementKeywordPos, sql, new String[]{"XA", "RECOVER"}, "`'\"", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL) == statementKeywordPos ? QueryReturnType.PRODUCES_RESULT_SET : QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET;
            }
         } else {
            return QueryReturnType.PRODUCES_RESULT_SET;
         }
      }
   }

   private static String getContextForWithStatement(String sql, boolean noBackslashEscapes) {
      String commentsFreeSql = StringUtils.stripCommentsAndHints(sql, "`'\"", "`'\"", !noBackslashEscapes);
      StringInspector strInspector = new StringInspector(commentsFreeSql, "`'\"(", "`'\")", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
      boolean asFound = false;

      while(true) {
         while(true) {
            int nws = strInspector.indexOfNextNonWsChar();
            if (nws == -1) {
               return null;
            }

            int ws = strInspector.indexOfNextWsChar();
            if (ws == -1) {
               ws = commentsFreeSql.length();
            }

            String section = commentsFreeSql.substring(nws, ws);
            if (!asFound && section.equalsIgnoreCase("AS")) {
               asFound = true;
            } else if (asFound) {
               if (!section.equalsIgnoreCase(",")) {
                  return section;
               }

               asFound = false;
            }
         }
      }
   }

   public static int getOnDuplicateKeyLocation(String sql, boolean dontCheckOnDuplicateKeyUpdateInSQL, boolean rewriteBatchedStatements, boolean noBackslashEscapes) {
      return dontCheckOnDuplicateKeyUpdateInSQL && !rewriteBatchedStatements ? -1 : StringUtils.indexOfIgnoreCase(0, sql, (String[])ON_DUPLICATE_KEY_UPDATE_CLAUSE, "`'\"", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
   }

   protected static boolean canRewrite(String sql, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementStartPos) {
      if (StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementStartPos)) {
         if (StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "`'\"", "`'\"", SearchMode.__MRK_COM_MYM_HNT_WS) != -1) {
            return false;
         } else {
            if (isOnDuplicateKeyUpdate) {
               int updateClausePos = StringUtils.indexOfIgnoreCase(locationOfOnDuplicateKeyUpdate, sql, " UPDATE ");
               if (updateClausePos != -1) {
                  return StringUtils.indexOfIgnoreCase(updateClausePos, sql, "LAST_INSERT_ID", "`'\"", "`'\"", SearchMode.__MRK_COM_MYM_HNT_WS) == -1;
               }
            }

            return true;
         }
      } else {
         return StringUtils.startsWithIgnoreCaseAndWs(sql, "REPLACE", statementStartPos) && StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "`'\"", "`'\"", SearchMode.__MRK_COM_MYM_HNT_WS) == -1;
      }
   }
}
