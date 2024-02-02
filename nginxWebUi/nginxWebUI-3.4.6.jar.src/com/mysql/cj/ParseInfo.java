/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.util.SearchMode;
/*     */ import com.mysql.cj.util.StringInspector;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
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
/*     */ public class ParseInfo
/*     */ {
/*     */   private static final String OPENING_MARKERS = "`'\"";
/*     */   private static final String CLOSING_MARKERS = "`'\"";
/*     */   private static final String OVERRIDING_MARKERS = "";
/*  50 */   private static final String[] ON_DUPLICATE_KEY_UPDATE_CLAUSE = new String[] { "ON", "DUPLICATE", "KEY", "UPDATE" };
/*  51 */   private static final String[] LOAD_DATA_CLAUSE = new String[] { "LOAD", "DATA" };
/*     */   
/*     */   private String charEncoding;
/*  54 */   private int statementLength = 0;
/*  55 */   private int statementStartPos = 0;
/*  56 */   private char firstStmtChar = Character.MIN_VALUE;
/*  57 */   private QueryReturnType queryReturnType = null;
/*     */   private boolean hasParameters = false;
/*     */   private boolean parametersInDuplicateKeyClause = false;
/*     */   private boolean isLoadData = false;
/*     */   private boolean isOnDuplicateKeyUpdate = false;
/*  62 */   private int locationOfOnDuplicateKeyUpdate = -1;
/*     */   
/*  64 */   private int numberOfQueries = 1;
/*     */   
/*     */   private boolean canRewriteAsMultiValueInsert = false;
/*     */   
/*     */   private String valuesClause;
/*     */   private ParseInfo batchHead;
/*     */   private ParseInfo batchValues;
/*     */   private ParseInfo batchODKUClause;
/*  72 */   private byte[][] staticSql = (byte[][])null;
/*     */ 
/*     */   
/*     */   private ParseInfo(byte[][] staticSql, char firstStmtChar, QueryReturnType queryReturnType, boolean isLoadData, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementLength, int statementStartPos) {
/*  76 */     this.firstStmtChar = firstStmtChar;
/*  77 */     this.queryReturnType = queryReturnType;
/*  78 */     this.isLoadData = isLoadData;
/*  79 */     this.isOnDuplicateKeyUpdate = isOnDuplicateKeyUpdate;
/*  80 */     this.locationOfOnDuplicateKeyUpdate = locationOfOnDuplicateKeyUpdate;
/*  81 */     this.statementLength = statementLength;
/*  82 */     this.statementStartPos = statementStartPos;
/*  83 */     this.staticSql = staticSql;
/*     */   }
/*     */   
/*     */   public ParseInfo(String sql, Session session, String encoding) {
/*  87 */     this(sql, session, encoding, true);
/*     */   }
/*     */   
/*     */   public ParseInfo(String sql, Session session, String encoding, boolean buildRewriteInfo) {
/*     */     try {
/*  92 */       if (sql == null) {
/*  93 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.61"), session
/*  94 */             .getExceptionInterceptor());
/*     */       }
/*     */       
/*  97 */       this.charEncoding = encoding;
/*  98 */       this.statementLength = sql.length();
/*     */       
/* 100 */       boolean noBackslashEscapes = session.getServerSession().isNoBackslashEscapesSet();
/* 101 */       this.queryReturnType = getQueryReturnType(sql, noBackslashEscapes);
/*     */ 
/*     */       
/* 104 */       this.statementStartPos = indexOfStartOfStatement(sql, session.getServerSession().isNoBackslashEscapesSet());
/* 105 */       if (this.statementStartPos == -1) {
/* 106 */         this.statementStartPos = this.statementLength;
/*     */       }
/*     */ 
/*     */       
/* 110 */       int statementKeywordPos = StringUtils.indexOfNextAlphanumericChar(this.statementStartPos, sql, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*     */       
/* 112 */       if (statementKeywordPos >= 0) {
/* 113 */         this.firstStmtChar = Character.toUpperCase(sql.charAt(statementKeywordPos));
/*     */       }
/*     */ 
/*     */       
/* 117 */       this.isLoadData = (this.firstStmtChar == 'L' && StringUtils.indexOfIgnoreCase(this.statementStartPos, sql, LOAD_DATA_CLAUSE, "`'\"", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL) == this.statementStartPos);
/*     */ 
/*     */ 
/*     */       
/* 121 */       if (this.firstStmtChar == 'I' && StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", this.statementStartPos)) {
/* 122 */         this.locationOfOnDuplicateKeyUpdate = getOnDuplicateKeyLocation(sql, ((Boolean)session
/* 123 */             .getPropertySet().getBooleanProperty(PropertyKey.dontCheckOnDuplicateKeyUpdateInSQL).getValue()).booleanValue(), ((Boolean)session
/* 124 */             .getPropertySet().getBooleanProperty(PropertyKey.rewriteBatchedStatements).getValue()).booleanValue(), session
/* 125 */             .getServerSession().isNoBackslashEscapesSet());
/* 126 */         this.isOnDuplicateKeyUpdate = (this.locationOfOnDuplicateKeyUpdate != -1);
/*     */       } 
/*     */       
/* 129 */       StringInspector strInspector = new StringInspector(sql, this.statementStartPos, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL);
/*     */       
/* 131 */       int pos = this.statementStartPos;
/* 132 */       int prevParamEnd = 0;
/* 133 */       ArrayList<int[]> endpointList = (ArrayList)new ArrayList<>();
/* 134 */       while ((pos = strInspector.indexOfNextNonWsChar()) >= 0) {
/* 135 */         if (strInspector.getChar() == '?') {
/* 136 */           endpointList.add(new int[] { prevParamEnd, pos });
/* 137 */           prevParamEnd = pos + 1;
/*     */           
/* 139 */           if (this.isOnDuplicateKeyUpdate && pos > this.locationOfOnDuplicateKeyUpdate) {
/* 140 */             this.parametersInDuplicateKeyClause = true;
/*     */           }
/* 142 */           strInspector.incrementPosition(); continue;
/*     */         } 
/* 144 */         if (strInspector.getChar() == ';') {
/* 145 */           strInspector.incrementPosition();
/* 146 */           pos = strInspector.indexOfNextNonWsChar();
/* 147 */           if (pos > 0) {
/* 148 */             this.numberOfQueries++;
/*     */           }
/*     */           continue;
/*     */         } 
/* 152 */         strInspector.incrementPosition();
/*     */       } 
/*     */ 
/*     */       
/* 156 */       endpointList.add(new int[] { prevParamEnd, this.statementLength });
/* 157 */       this.staticSql = new byte[endpointList.size()][];
/* 158 */       this.hasParameters = (this.staticSql.length > 1);
/*     */       
/* 160 */       for (int i = 0; i < this.staticSql.length; i++) {
/* 161 */         int[] ep = endpointList.get(i);
/* 162 */         int end = ep[1];
/* 163 */         int begin = ep[0];
/* 164 */         int len = end - begin;
/*     */         
/* 166 */         if (this.isLoadData) {
/* 167 */           this.staticSql[i] = StringUtils.getBytes(sql, begin, len);
/*     */         }
/* 169 */         else if (encoding == null) {
/* 170 */           byte[] buf = new byte[len];
/* 171 */           for (int j = 0; j < len; j++) {
/* 172 */             buf[j] = (byte)sql.charAt(begin + j);
/*     */           }
/* 174 */           this.staticSql[i] = buf;
/*     */         } else {
/*     */           
/* 177 */           this.staticSql[i] = StringUtils.getBytes(sql, begin, len, encoding);
/*     */         } 
/*     */       } 
/* 180 */     } catch (Exception oobEx) {
/* 181 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.62", new Object[] { sql }), oobEx, session
/* 182 */           .getExceptionInterceptor());
/*     */     } 
/*     */     
/* 185 */     if (buildRewriteInfo) {
/* 186 */       this
/* 187 */         .canRewriteAsMultiValueInsert = (this.numberOfQueries == 1 && !this.parametersInDuplicateKeyClause && canRewrite(sql, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementStartPos));
/* 188 */       if (this.canRewriteAsMultiValueInsert && ((Boolean)session.getPropertySet().getBooleanProperty(PropertyKey.rewriteBatchedStatements).getValue()).booleanValue()) {
/* 189 */         buildRewriteBatchedParams(sql, session, encoding);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNumberOfQueries() {
/* 195 */     return this.numberOfQueries;
/*     */   }
/*     */   
/*     */   public byte[][] getStaticSql() {
/* 199 */     return this.staticSql;
/*     */   }
/*     */   
/*     */   public String getValuesClause() {
/* 203 */     return this.valuesClause;
/*     */   }
/*     */   
/*     */   public int getLocationOfOnDuplicateKeyUpdate() {
/* 207 */     return this.locationOfOnDuplicateKeyUpdate;
/*     */   }
/*     */   
/*     */   public QueryReturnType getQueryReturnType() {
/* 211 */     return this.queryReturnType;
/*     */   }
/*     */   
/*     */   public boolean canRewriteAsMultiValueInsertAtSqlLevel() {
/* 215 */     return this.canRewriteAsMultiValueInsert;
/*     */   }
/*     */   
/*     */   public boolean containsOnDuplicateKeyUpdateInSQL() {
/* 219 */     return this.isOnDuplicateKeyUpdate;
/*     */   }
/*     */   
/*     */   private void buildRewriteBatchedParams(String sql, Session session, String encoding) {
/* 223 */     this.valuesClause = extractValuesClause(sql, session.getIdentifierQuoteString());
/* 224 */     String odkuClause = this.isOnDuplicateKeyUpdate ? sql.substring(this.locationOfOnDuplicateKeyUpdate) : null;
/*     */     
/* 226 */     String headSql = null;
/*     */     
/* 228 */     if (this.isOnDuplicateKeyUpdate) {
/* 229 */       headSql = sql.substring(0, this.locationOfOnDuplicateKeyUpdate);
/*     */     } else {
/* 231 */       headSql = sql;
/*     */     } 
/*     */     
/* 234 */     this.batchHead = new ParseInfo(headSql, session, encoding, false);
/* 235 */     this.batchValues = new ParseInfo("," + this.valuesClause, session, encoding, false);
/* 236 */     this.batchODKUClause = null;
/*     */     
/* 238 */     if (odkuClause != null && odkuClause.length() > 0) {
/* 239 */       this.batchODKUClause = new ParseInfo("," + this.valuesClause + " " + odkuClause, session, encoding, false);
/*     */     }
/*     */   }
/*     */   
/*     */   private String extractValuesClause(String sql, String quoteCharStr) {
/* 244 */     int indexOfValues = -1;
/* 245 */     int valuesSearchStart = this.statementStartPos;
/*     */     
/* 247 */     int indexOfFirstEqualsChar = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "=", quoteCharStr, quoteCharStr, SearchMode.__MRK_COM_MYM_HNT_WS);
/*     */     
/* 249 */     while (indexOfValues == -1) {
/*     */       
/* 251 */       if (quoteCharStr.length() > 0) {
/* 252 */         indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUE", quoteCharStr, quoteCharStr, SearchMode.__MRK_COM_MYM_HNT_WS);
/*     */       } else {
/* 254 */         indexOfValues = StringUtils.indexOfIgnoreCase(valuesSearchStart, sql, "VALUE");
/*     */       } 
/*     */       
/* 257 */       if (indexOfFirstEqualsChar > 0 && indexOfValues > indexOfFirstEqualsChar)
/*     */       {
/* 259 */         indexOfValues = -1;
/*     */       }
/*     */ 
/*     */       
/* 263 */       if (indexOfValues > 0) {
/*     */         
/* 265 */         char c = sql.charAt(indexOfValues - 1);
/* 266 */         if (!Character.isWhitespace(c) && c != ')' && c != '`') {
/* 267 */           valuesSearchStart = indexOfValues + 6;
/* 268 */           indexOfValues = -1;
/*     */           continue;
/*     */         } 
/* 271 */         c = sql.charAt(indexOfValues + 6);
/* 272 */         if (!Character.isWhitespace(c) && c != '(') {
/* 273 */           valuesSearchStart = indexOfValues + 6;
/* 274 */           indexOfValues = -1;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 282 */     if (indexOfValues == -1) {
/* 283 */       return null;
/*     */     }
/*     */     
/* 286 */     int indexOfFirstParen = sql.indexOf('(', indexOfValues + 6);
/*     */     
/* 288 */     if (indexOfFirstParen == -1) {
/* 289 */       return null;
/*     */     }
/*     */     
/* 292 */     int endOfValuesClause = this.isOnDuplicateKeyUpdate ? this.locationOfOnDuplicateKeyUpdate : sql.length();
/*     */     
/* 294 */     return sql.substring(indexOfFirstParen, endOfValuesClause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized ParseInfo getParseInfoForBatch(int numBatch) {
/* 305 */     AppendingBatchVisitor apv = new AppendingBatchVisitor();
/* 306 */     buildInfoForBatch(numBatch, apv);
/*     */     
/* 308 */     ParseInfo batchParseInfo = new ParseInfo(apv.getStaticSqlStrings(), this.firstStmtChar, this.queryReturnType, this.isLoadData, this.isOnDuplicateKeyUpdate, this.locationOfOnDuplicateKeyUpdate, this.statementLength, this.statementStartPos);
/*     */ 
/*     */     
/* 311 */     return batchParseInfo;
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
/*     */   public String getSqlForBatch(int numBatch) throws UnsupportedEncodingException {
/* 325 */     ParseInfo batchInfo = getParseInfoForBatch(numBatch);
/*     */     
/* 327 */     return batchInfo.getSqlForBatch();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSqlForBatch() throws UnsupportedEncodingException {
/* 338 */     int size = 0;
/* 339 */     byte[][] sqlStrings = this.staticSql;
/* 340 */     int sqlStringsLength = sqlStrings.length;
/*     */     
/* 342 */     for (int i = 0; i < sqlStringsLength; i++) {
/* 343 */       size += (sqlStrings[i]).length;
/* 344 */       size++;
/*     */     } 
/*     */     
/* 347 */     StringBuilder buf = new StringBuilder(size);
/*     */     
/* 349 */     for (int j = 0; j < sqlStringsLength - 1; j++) {
/* 350 */       buf.append(StringUtils.toString(sqlStrings[j], this.charEncoding));
/* 351 */       buf.append("?");
/*     */     } 
/*     */     
/* 354 */     buf.append(StringUtils.toString(sqlStrings[sqlStringsLength - 1]));
/*     */     
/* 356 */     return buf.toString();
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
/*     */   private void buildInfoForBatch(int numBatch, BatchVisitor visitor) {
/* 370 */     if (!this.hasParameters) {
/* 371 */       if (numBatch == 1) {
/*     */ 
/*     */         
/* 374 */         visitor.append(this.staticSql[0]);
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */ 
/*     */       
/* 381 */       byte[] arrayOfByte1 = this.batchHead.staticSql[0];
/* 382 */       visitor.append(arrayOfByte1).increment();
/*     */       
/* 384 */       int k = numBatch - 1;
/* 385 */       if (this.batchODKUClause != null) {
/* 386 */         k--;
/*     */       }
/*     */       
/* 389 */       byte[] arrayOfByte2 = this.batchValues.staticSql[0];
/* 390 */       for (int m = 0; m < k; m++) {
/* 391 */         visitor.mergeWithLast(arrayOfByte2).increment();
/*     */       }
/*     */       
/* 394 */       if (this.batchODKUClause != null) {
/* 395 */         byte[] batchOdkuStaticSql = this.batchODKUClause.staticSql[0];
/* 396 */         visitor.mergeWithLast(batchOdkuStaticSql).increment();
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 405 */     byte[][] headStaticSql = this.batchHead.staticSql;
/* 406 */     int headStaticSqlLength = headStaticSql.length;
/* 407 */     byte[] endOfHead = headStaticSql[headStaticSqlLength - 1];
/*     */     
/* 409 */     for (int i = 0; i < headStaticSqlLength - 1; i++) {
/* 410 */       visitor.append(headStaticSql[i]).increment();
/*     */     }
/*     */ 
/*     */     
/* 414 */     int numValueRepeats = numBatch - 1;
/* 415 */     if (this.batchODKUClause != null) {
/* 416 */       numValueRepeats--;
/*     */     }
/*     */     
/* 419 */     byte[][] valuesStaticSql = this.batchValues.staticSql;
/* 420 */     int valuesStaticSqlLength = valuesStaticSql.length;
/* 421 */     byte[] beginOfValues = valuesStaticSql[0];
/* 422 */     byte[] endOfValues = valuesStaticSql[valuesStaticSqlLength - 1];
/*     */     
/* 424 */     for (int j = 0; j < numValueRepeats; j++) {
/* 425 */       visitor.merge(endOfValues, beginOfValues).increment();
/* 426 */       for (int k = 1; k < valuesStaticSqlLength - 1; k++) {
/* 427 */         visitor.append(valuesStaticSql[k]).increment();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 432 */     if (this.batchODKUClause != null) {
/* 433 */       byte[][] batchOdkuStaticSql = this.batchODKUClause.staticSql;
/* 434 */       int batchOdkuStaticSqlLength = batchOdkuStaticSql.length;
/* 435 */       byte[] beginOfOdku = batchOdkuStaticSql[0];
/* 436 */       byte[] endOfOdku = batchOdkuStaticSql[batchOdkuStaticSqlLength - 1];
/*     */       
/* 438 */       if (numBatch > 1) {
/* 439 */         visitor.merge((numValueRepeats > 0) ? endOfValues : endOfHead, beginOfOdku).increment();
/* 440 */         for (int k = 1; k < batchOdkuStaticSqlLength; k++) {
/* 441 */           visitor.append(batchOdkuStaticSql[k]).increment();
/*     */         }
/*     */       } else {
/* 444 */         visitor.append(endOfOdku).increment();
/*     */       } 
/*     */     } else {
/* 447 */       visitor.append(endOfHead);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isLoadData() {
/* 452 */     return this.isLoadData;
/*     */   }
/*     */   
/*     */   public char getFirstStmtChar() {
/* 456 */     return this.firstStmtChar;
/*     */   }
/*     */   
/*     */   public static int indexOfStartOfStatement(String sql, boolean noBackslashEscapes) {
/* 460 */     return StringUtils.indexOfNextNonWsChar(0, sql, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int indexOfStatementKeyword(String sql, boolean noBackslashEscapes) {
/* 465 */     return StringUtils.indexOfNextAlphanumericChar(0, sql, "`'\"", "`'\"", "", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*     */   }
/*     */ 
/*     */   
/*     */   public static char firstCharOfStatementUc(String sql, boolean noBackslashEscapes) {
/* 470 */     int statementKeywordPos = indexOfStatementKeyword(sql, noBackslashEscapes);
/* 471 */     if (statementKeywordPos == -1) {
/* 472 */       return Character.MIN_VALUE;
/*     */     }
/* 474 */     return Character.toUpperCase(sql.charAt(statementKeywordPos));
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
/*     */   public static boolean isReadOnlySafeQuery(String sql, boolean noBackslashEscapes) {
/* 498 */     int statementKeywordPos = indexOfStatementKeyword(sql, noBackslashEscapes);
/* 499 */     if (statementKeywordPos == -1) {
/* 500 */       return true;
/*     */     }
/* 502 */     char firstStatementChar = Character.toUpperCase(sql.charAt(statementKeywordPos));
/* 503 */     if (firstStatementChar == 'A' && StringUtils.startsWithIgnoreCaseAndWs(sql, "ALTER", statementKeywordPos))
/* 504 */       return false; 
/* 505 */     if (firstStatementChar == 'C' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "CHANGE", statementKeywordPos) || 
/* 506 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE", statementKeywordPos)))
/* 507 */       return false; 
/* 508 */     if (firstStatementChar == 'D' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "DELETE", statementKeywordPos) || 
/* 509 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "DROP", statementKeywordPos)))
/* 510 */       return false; 
/* 511 */     if (firstStatementChar == 'G' && StringUtils.startsWithIgnoreCaseAndWs(sql, "GRANT", statementKeywordPos))
/* 512 */       return false; 
/* 513 */     if (firstStatementChar == 'I' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "IMPORT", statementKeywordPos) || 
/* 514 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementKeywordPos) || 
/* 515 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "INSTALL", statementKeywordPos)))
/* 516 */       return false; 
/* 517 */     if (firstStatementChar == 'L' && StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD", statementKeywordPos))
/* 518 */       return false; 
/* 519 */     if (firstStatementChar == 'O' && StringUtils.startsWithIgnoreCaseAndWs(sql, "OPTIMIZE", statementKeywordPos))
/* 520 */       return false; 
/* 521 */     if (firstStatementChar == 'R' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "RENAME", statementKeywordPos) || 
/* 522 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "REPAIR", statementKeywordPos) || 
/* 523 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "REPLACE", statementKeywordPos) || 
/* 524 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "RESET", statementKeywordPos) || 
/* 525 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "REVOKE", statementKeywordPos)))
/* 526 */       return false; 
/* 527 */     if (firstStatementChar == 'T' && StringUtils.startsWithIgnoreCaseAndWs(sql, "TRUNCATE", statementKeywordPos))
/* 528 */       return false; 
/* 529 */     if (firstStatementChar == 'U' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "UNINSTALL", statementKeywordPos) || 
/* 530 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "UPDATE", statementKeywordPos)))
/* 531 */       return false; 
/* 532 */     if (firstStatementChar == 'W' && StringUtils.startsWithIgnoreCaseAndWs(sql, "WITH", statementKeywordPos)) {
/* 533 */       String context = getContextForWithStatement(sql, noBackslashEscapes);
/* 534 */       return (context == null || (!context.equalsIgnoreCase("DELETE") && !context.equalsIgnoreCase("UPDATE")));
/*     */     } 
/* 536 */     return true;
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
/*     */   public static QueryReturnType getQueryReturnType(String sql, boolean noBackslashEscapes) {
/* 562 */     int statementKeywordPos = indexOfStatementKeyword(sql, noBackslashEscapes);
/* 563 */     if (statementKeywordPos == -1) {
/* 564 */       return QueryReturnType.NONE;
/*     */     }
/* 566 */     char firstStatementChar = Character.toUpperCase(sql.charAt(statementKeywordPos));
/* 567 */     if (firstStatementChar == 'A' && StringUtils.startsWithIgnoreCaseAndWs(sql, "ANALYZE", statementKeywordPos))
/* 568 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 569 */     if (firstStatementChar == 'C' && StringUtils.startsWithIgnoreCaseAndWs(sql, "CALL", statementKeywordPos))
/* 570 */       return QueryReturnType.MAY_PRODUCE_RESULT_SET; 
/* 571 */     if (firstStatementChar == 'C' && StringUtils.startsWithIgnoreCaseAndWs(sql, "CHECK", statementKeywordPos))
/* 572 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 573 */     if (firstStatementChar == 'D' && StringUtils.startsWithIgnoreCaseAndWs(sql, "DESC", statementKeywordPos))
/* 574 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 575 */     if (firstStatementChar == 'E' && StringUtils.startsWithIgnoreCaseAndWs(sql, "EXPLAIN", statementKeywordPos))
/* 576 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 577 */     if (firstStatementChar == 'E' && StringUtils.startsWithIgnoreCaseAndWs(sql, "EXECUTE", statementKeywordPos))
/* 578 */       return QueryReturnType.MAY_PRODUCE_RESULT_SET; 
/* 579 */     if (firstStatementChar == 'H' && StringUtils.startsWithIgnoreCaseAndWs(sql, "HELP", statementKeywordPos))
/* 580 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 581 */     if (firstStatementChar == 'O' && StringUtils.startsWithIgnoreCaseAndWs(sql, "OPTIMIZE", statementKeywordPos))
/* 582 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 583 */     if (firstStatementChar == 'R' && StringUtils.startsWithIgnoreCaseAndWs(sql, "REPAIR", statementKeywordPos))
/* 584 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 585 */     if (firstStatementChar == 'S' && (StringUtils.startsWithIgnoreCaseAndWs(sql, "SELECT", statementKeywordPos) || 
/* 586 */       StringUtils.startsWithIgnoreCaseAndWs(sql, "SHOW", statementKeywordPos)))
/* 587 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 588 */     if (firstStatementChar == 'T' && StringUtils.startsWithIgnoreCaseAndWs(sql, "TABLE", statementKeywordPos))
/* 589 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 590 */     if (firstStatementChar == 'V' && StringUtils.startsWithIgnoreCaseAndWs(sql, "VALUES", statementKeywordPos))
/* 591 */       return QueryReturnType.PRODUCES_RESULT_SET; 
/* 592 */     if (firstStatementChar == 'W' && StringUtils.startsWithIgnoreCaseAndWs(sql, "WITH", statementKeywordPos)) {
/* 593 */       String context = getContextForWithStatement(sql, noBackslashEscapes);
/* 594 */       if (context == null)
/* 595 */         return QueryReturnType.MAY_PRODUCE_RESULT_SET; 
/* 596 */       if (context.equalsIgnoreCase("SELECT") || context.equalsIgnoreCase("TABLE") || context.equalsIgnoreCase("VALUES")) {
/* 597 */         return QueryReturnType.PRODUCES_RESULT_SET;
/*     */       }
/* 599 */       return QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET;
/*     */     } 
/* 601 */     if (firstStatementChar == 'X' && StringUtils.indexOfIgnoreCase(statementKeywordPos, sql, new String[] { "XA", "RECOVER" }, "`'\"", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__FULL) == statementKeywordPos)
/*     */     {
/* 603 */       return QueryReturnType.PRODUCES_RESULT_SET;
/*     */     }
/* 605 */     return QueryReturnType.DOES_NOT_PRODUCE_RESULT_SET;
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
/*     */   private static String getContextForWithStatement(String sql, boolean noBackslashEscapes) {
/* 620 */     String section, commentsFreeSql = StringUtils.stripCommentsAndHints(sql, "`'\"", "`'\"", !noBackslashEscapes);
/*     */ 
/*     */     
/* 623 */     StringInspector strInspector = new StringInspector(commentsFreeSql, "`'\"(", "`'\")", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*     */     
/* 625 */     boolean asFound = false;
/*     */     while (true) {
/* 627 */       int nws = strInspector.indexOfNextNonWsChar();
/* 628 */       if (nws == -1) {
/* 629 */         return null;
/*     */       }
/* 631 */       int ws = strInspector.indexOfNextWsChar();
/* 632 */       if (ws == -1) {
/* 633 */         ws = commentsFreeSql.length();
/*     */       }
/* 635 */       section = commentsFreeSql.substring(nws, ws);
/* 636 */       if (!asFound && section.equalsIgnoreCase("AS")) {
/* 637 */         asFound = true; continue;
/* 638 */       }  if (asFound) {
/* 639 */         if (section.equalsIgnoreCase(","))
/* 640 */         { asFound = false; continue; }  break;
/*     */       } 
/* 642 */     }  return section;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getOnDuplicateKeyLocation(String sql, boolean dontCheckOnDuplicateKeyUpdateInSQL, boolean rewriteBatchedStatements, boolean noBackslashEscapes) {
/* 650 */     return (dontCheckOnDuplicateKeyUpdateInSQL && !rewriteBatchedStatements) ? -1 : 
/* 651 */       StringUtils.indexOfIgnoreCase(0, sql, ON_DUPLICATE_KEY_UPDATE_CLAUSE, "`'\"", "`'\"", noBackslashEscapes ? SearchMode.__MRK_COM_MYM_HNT_WS : SearchMode.__BSE_MRK_COM_MYM_HNT_WS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean canRewrite(String sql, boolean isOnDuplicateKeyUpdate, int locationOfOnDuplicateKeyUpdate, int statementStartPos) {
/* 659 */     if (StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT", statementStartPos)) {
/* 660 */       if (StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "`'\"", "`'\"", SearchMode.__MRK_COM_MYM_HNT_WS) != -1) {
/* 661 */         return false;
/*     */       }
/* 663 */       if (isOnDuplicateKeyUpdate) {
/* 664 */         int updateClausePos = StringUtils.indexOfIgnoreCase(locationOfOnDuplicateKeyUpdate, sql, " UPDATE ");
/* 665 */         if (updateClausePos != -1) {
/* 666 */           return (StringUtils.indexOfIgnoreCase(updateClausePos, sql, "LAST_INSERT_ID", "`'\"", "`'\"", SearchMode.__MRK_COM_MYM_HNT_WS) == -1);
/*     */         }
/*     */       } 
/*     */       
/* 670 */       return true;
/*     */     } 
/*     */     
/* 673 */     return (StringUtils.startsWithIgnoreCaseAndWs(sql, "REPLACE", statementStartPos) && 
/* 674 */       StringUtils.indexOfIgnoreCase(statementStartPos, sql, "SELECT", "`'\"", "`'\"", SearchMode.__MRK_COM_MYM_HNT_WS) == -1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ParseInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */