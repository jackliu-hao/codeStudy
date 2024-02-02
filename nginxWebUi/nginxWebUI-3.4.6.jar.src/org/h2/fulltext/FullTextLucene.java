/*     */ package org.h2.fulltext;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Paths;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import org.apache.lucene.analysis.Analyzer;
/*     */ import org.apache.lucene.analysis.standard.StandardAnalyzer;
/*     */ import org.apache.lucene.document.DateTools;
/*     */ import org.apache.lucene.document.Document;
/*     */ import org.apache.lucene.document.Field;
/*     */ import org.apache.lucene.document.FieldType;
/*     */ import org.apache.lucene.document.TextField;
/*     */ import org.apache.lucene.index.DirectoryReader;
/*     */ import org.apache.lucene.index.IndexFormatTooOldException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.IndexWriter;
/*     */ import org.apache.lucene.index.IndexWriterConfig;
/*     */ import org.apache.lucene.index.IndexableField;
/*     */ import org.apache.lucene.index.IndexableFieldType;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
/*     */ import org.apache.lucene.search.IndexSearcher;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.search.ScoreDoc;
/*     */ import org.apache.lucene.search.TopDocs;
/*     */ import org.apache.lucene.store.ByteBuffersDirectory;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.FSDirectory;
/*     */ import org.h2.api.Trigger;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.tools.SimpleResultSet;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
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
/*     */ public class FullTextLucene
/*     */   extends FullText
/*     */ {
/*  61 */   protected static final boolean STORE_DOCUMENT_TEXT_IN_INDEX = Utils.getProperty("h2.storeDocumentTextInIndex", false);
/*     */   
/*  63 */   private static final HashMap<String, IndexAccess> INDEX_ACCESS = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String TRIGGER_PREFIX = "FTL_";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String SCHEMA = "FTL";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String LUCENE_FIELD_DATA = "_DATA";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String LUCENE_FIELD_QUERY = "_QUERY";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String LUCENE_FIELD_MODIFIED = "_modified";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String LUCENE_FIELD_COLUMN_PREFIX = "_";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String IN_MEMORY_PREFIX = "mem:";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void init(Connection paramConnection) throws SQLException {
/* 101 */     try (Statement null = paramConnection.createStatement()) {
/* 102 */       statement.execute("CREATE SCHEMA IF NOT EXISTS FTL");
/* 103 */       statement.execute("CREATE TABLE IF NOT EXISTS FTL.INDEXES(SCHEMA VARCHAR, `TABLE` VARCHAR, COLUMNS VARCHAR, PRIMARY KEY(SCHEMA, `TABLE`))");
/*     */ 
/*     */       
/* 106 */       String str = FullTextLucene.class.getName();
/* 107 */       statement.execute("CREATE ALIAS IF NOT EXISTS FTL_CREATE_INDEX FOR '" + str + ".createIndex'");
/* 108 */       statement.execute("CREATE ALIAS IF NOT EXISTS FTL_DROP_INDEX FOR '" + str + ".dropIndex'");
/* 109 */       statement.execute("CREATE ALIAS IF NOT EXISTS FTL_SEARCH FOR '" + str + ".search'");
/* 110 */       statement.execute("CREATE ALIAS IF NOT EXISTS FTL_SEARCH_DATA FOR '" + str + ".searchData'");
/* 111 */       statement.execute("CREATE ALIAS IF NOT EXISTS FTL_REINDEX FOR '" + str + ".reindex'");
/* 112 */       statement.execute("CREATE ALIAS IF NOT EXISTS FTL_DROP_ALL FOR '" + str + ".dropAll'");
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
/*     */   public static void createIndex(Connection paramConnection, String paramString1, String paramString2, String paramString3) throws SQLException {
/* 128 */     init(paramConnection);
/* 129 */     PreparedStatement preparedStatement = paramConnection.prepareStatement("INSERT INTO FTL.INDEXES(SCHEMA, `TABLE`, COLUMNS) VALUES(?, ?, ?)");
/*     */     
/* 131 */     preparedStatement.setString(1, paramString1);
/* 132 */     preparedStatement.setString(2, paramString2);
/* 133 */     preparedStatement.setString(3, paramString3);
/* 134 */     preparedStatement.execute();
/* 135 */     createTrigger(paramConnection, paramString1, paramString2);
/* 136 */     indexExistingRows(paramConnection, paramString1, paramString2);
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
/*     */   public static void dropIndex(Connection paramConnection, String paramString1, String paramString2) throws SQLException {
/* 150 */     init(paramConnection);
/*     */     
/* 152 */     PreparedStatement preparedStatement = paramConnection.prepareStatement("DELETE FROM FTL.INDEXES WHERE SCHEMA=? AND `TABLE`=?");
/*     */     
/* 154 */     preparedStatement.setString(1, paramString1);
/* 155 */     preparedStatement.setString(2, paramString2);
/* 156 */     int i = preparedStatement.executeUpdate();
/* 157 */     if (i != 0) {
/* 158 */       reindex(paramConnection);
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
/*     */   public static void reindex(Connection paramConnection) throws SQLException {
/* 170 */     init(paramConnection);
/* 171 */     removeAllTriggers(paramConnection, "FTL_");
/* 172 */     removeIndexFiles(paramConnection);
/* 173 */     Statement statement = paramConnection.createStatement();
/* 174 */     ResultSet resultSet = statement.executeQuery("SELECT * FROM FTL.INDEXES");
/* 175 */     while (resultSet.next()) {
/* 176 */       String str1 = resultSet.getString("SCHEMA");
/* 177 */       String str2 = resultSet.getString("TABLE");
/* 178 */       createTrigger(paramConnection, str1, str2);
/* 179 */       indexExistingRows(paramConnection, str1, str2);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dropAll(Connection paramConnection) throws SQLException {
/* 190 */     Statement statement = paramConnection.createStatement();
/* 191 */     statement.execute("DROP SCHEMA IF EXISTS FTL CASCADE");
/* 192 */     removeAllTriggers(paramConnection, "FTL_");
/* 193 */     removeIndexFiles(paramConnection);
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
/*     */   public static ResultSet search(Connection paramConnection, String paramString, int paramInt1, int paramInt2) throws SQLException {
/* 214 */     return search(paramConnection, paramString, paramInt1, paramInt2, false);
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
/*     */   public static ResultSet searchData(Connection paramConnection, String paramString, int paramInt1, int paramInt2) throws SQLException {
/* 240 */     return search(paramConnection, paramString, paramInt1, paramInt2, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static SQLException convertException(Exception paramException) {
/* 250 */     return new SQLException("Error while indexing document", "FULLTEXT", paramException);
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
/*     */   private static void createTrigger(Connection paramConnection, String paramString1, String paramString2) throws SQLException {
/* 263 */     createOrDropTrigger(paramConnection, paramString1, paramString2, true);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void createOrDropTrigger(Connection paramConnection, String paramString1, String paramString2, boolean paramBoolean) throws SQLException {
/* 268 */     Statement statement = paramConnection.createStatement();
/*     */     
/* 270 */     String str = StringUtils.quoteIdentifier(paramString1) + "." + StringUtils.quoteIdentifier("FTL_" + paramString2);
/* 271 */     statement.execute("DROP TRIGGER IF EXISTS " + str);
/* 272 */     if (paramBoolean) {
/* 273 */       StringBuilder stringBuilder = new StringBuilder("CREATE TRIGGER IF NOT EXISTS ");
/*     */ 
/*     */ 
/*     */       
/* 277 */       stringBuilder.append(str)
/* 278 */         .append(" AFTER INSERT, UPDATE, DELETE, ROLLBACK ON ");
/* 279 */       StringUtils.quoteIdentifier(stringBuilder, paramString1)
/* 280 */         .append('.');
/* 281 */       StringUtils.quoteIdentifier(stringBuilder, paramString2)
/* 282 */         .append(" FOR EACH ROW CALL \"")
/* 283 */         .append(FullTextTrigger.class.getName())
/* 284 */         .append('"');
/* 285 */       statement.execute(stringBuilder.toString());
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
/*     */   protected static IndexAccess getIndexAccess(Connection paramConnection) throws SQLException {
/* 298 */     String str = getIndexPath(paramConnection);
/* 299 */     synchronized (INDEX_ACCESS) {
/* 300 */       IndexAccess indexAccess = INDEX_ACCESS.get(str);
/* 301 */       while (indexAccess == null)
/*     */       
/*     */       { try {
/* 304 */           Directory directory = (Directory)(str.startsWith("mem:") ? new ByteBuffersDirectory() : FSDirectory.open(Paths.get(str, new String[0])));
/* 305 */           StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
/* 306 */           IndexWriterConfig indexWriterConfig = new IndexWriterConfig((Analyzer)standardAnalyzer);
/* 307 */           indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
/* 308 */           IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
/*     */           
/* 310 */           indexAccess = new IndexAccess(indexWriter);
/* 311 */         } catch (IndexFormatTooOldException indexFormatTooOldException) {
/* 312 */           reindex(paramConnection);
/*     */           continue;
/* 314 */         } catch (IOException iOException) {
/* 315 */           throw convertException(iOException);
/*     */         } 
/* 317 */         INDEX_ACCESS.put(str, indexAccess);
/*     */ 
/*     */         
/* 320 */         return indexAccess; }  return indexAccess;
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
/*     */   protected static String getIndexPath(Connection paramConnection) throws SQLException {
/* 332 */     Statement statement = paramConnection.createStatement();
/* 333 */     ResultSet resultSet = statement.executeQuery("CALL DATABASE_PATH()");
/* 334 */     resultSet.next();
/* 335 */     String str = resultSet.getString(1);
/* 336 */     if (str == null) {
/* 337 */       return "mem:" + paramConnection.getCatalog();
/*     */     }
/* 339 */     int i = str.lastIndexOf(':');
/*     */     
/* 341 */     if (i > 1) {
/* 342 */       str = str.substring(i + 1);
/*     */     }
/* 344 */     resultSet.close();
/* 345 */     return str;
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
/*     */   private static void indexExistingRows(Connection paramConnection, String paramString1, String paramString2) throws SQLException {
/* 358 */     FullTextTrigger fullTextTrigger = new FullTextTrigger();
/* 359 */     fullTextTrigger.init(paramConnection, paramString1, null, paramString2, false, 1);
/*     */     
/* 361 */     String str = "SELECT * FROM " + StringUtils.quoteIdentifier(paramString1) + "." + StringUtils.quoteIdentifier(paramString2);
/* 362 */     ResultSet resultSet = paramConnection.createStatement().executeQuery(str);
/* 363 */     int i = resultSet.getMetaData().getColumnCount();
/* 364 */     while (resultSet.next()) {
/* 365 */       Object[] arrayOfObject = new Object[i];
/* 366 */       for (byte b = 0; b < i; b++) {
/* 367 */         arrayOfObject[b] = resultSet.getObject(b + 1);
/*     */       }
/* 369 */       fullTextTrigger.insert(arrayOfObject, false);
/*     */     } 
/* 371 */     fullTextTrigger.commitIndex();
/*     */   }
/*     */   
/*     */   private static void removeIndexFiles(Connection paramConnection) throws SQLException {
/* 375 */     String str = getIndexPath(paramConnection);
/* 376 */     removeIndexAccess(str);
/* 377 */     if (!str.startsWith("mem:")) {
/* 378 */       FileUtils.deleteRecursive(str, false);
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
/*     */   protected static void removeIndexAccess(String paramString) throws SQLException {
/* 391 */     synchronized (INDEX_ACCESS) {
/*     */       try {
/* 393 */         IndexAccess indexAccess = INDEX_ACCESS.remove(paramString);
/* 394 */         if (indexAccess != null) {
/* 395 */           indexAccess.close();
/*     */         }
/* 397 */       } catch (Exception exception) {
/* 398 */         throw convertException(exception);
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
/*     */   protected static ResultSet search(Connection paramConnection, String paramString, int paramInt1, int paramInt2, boolean paramBoolean) throws SQLException {
/* 416 */     SimpleResultSet simpleResultSet = createResultSet(paramBoolean);
/* 417 */     if (paramConnection.getMetaData().getURL().startsWith("jdbc:columnlist:"))
/*     */     {
/* 419 */       return (ResultSet)simpleResultSet;
/*     */     }
/* 421 */     if (paramString == null || StringUtils.isWhitespaceOrEmpty(paramString)) {
/* 422 */       return (ResultSet)simpleResultSet;
/*     */     }
/*     */     try {
/* 425 */       IndexAccess indexAccess = getIndexAccess(paramConnection);
/*     */       
/* 427 */       IndexSearcher indexSearcher = indexAccess.getSearcher();
/*     */ 
/*     */       
/*     */       try {
/* 431 */         Analyzer analyzer = indexAccess.writer.getAnalyzer();
/* 432 */         StandardQueryParser standardQueryParser = new StandardQueryParser(analyzer);
/* 433 */         Query query = standardQueryParser.parse(paramString, "_DATA");
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 438 */         int i = ((paramInt1 == 0) ? 100 : paramInt1) + paramInt2;
/* 439 */         TopDocs topDocs = indexSearcher.search(query, i);
/* 440 */         long l = topDocs.totalHits.value;
/* 441 */         if (paramInt1 == 0)
/*     */         {
/* 443 */           paramInt1 = (int)l;
/*     */         }
/* 445 */         byte b = 0; int j = topDocs.scoreDocs.length;
/*     */         
/* 447 */         for (; b < paramInt1 && (b + paramInt2) < l && b + paramInt2 < j; b++) {
/* 448 */           ScoreDoc scoreDoc = topDocs.scoreDocs[b + paramInt2];
/* 449 */           Document document = indexSearcher.doc(scoreDoc.doc);
/* 450 */           float f = scoreDoc.score;
/* 451 */           String str = document.get("_QUERY");
/* 452 */           if (paramBoolean) {
/* 453 */             int k = str.indexOf(" WHERE ");
/* 454 */             JdbcConnection jdbcConnection = (JdbcConnection)paramConnection;
/* 455 */             SessionLocal sessionLocal = (SessionLocal)jdbcConnection.getSession();
/* 456 */             Parser parser = new Parser(sessionLocal);
/* 457 */             String str1 = str.substring(0, k);
/*     */             
/* 459 */             ExpressionColumn expressionColumn = (ExpressionColumn)parser.parseExpression(str1);
/* 460 */             String str2 = expressionColumn.getOriginalTableAliasName();
/* 461 */             String str3 = expressionColumn.getColumnName(sessionLocal, -1);
/* 462 */             str = str.substring(k + " WHERE ".length());
/* 463 */             String[][] arrayOfString = parseKey(paramConnection, str);
/* 464 */             simpleResultSet.addRow(new Object[] { str2, str3, arrayOfString[0], arrayOfString[1], 
/* 465 */                   Float.valueOf(f) });
/*     */           } else {
/* 467 */             simpleResultSet.addRow(new Object[] { str, Float.valueOf(f) });
/*     */           } 
/*     */         } 
/*     */       } finally {
/* 471 */         indexAccess.returnSearcher(indexSearcher);
/*     */       } 
/* 473 */     } catch (Exception exception) {
/* 474 */       throw convertException(exception);
/*     */     } 
/* 476 */     return (ResultSet)simpleResultSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class FullTextTrigger
/*     */     implements Trigger
/*     */   {
/*     */     private String schema;
/*     */     
/*     */     private String table;
/*     */     
/*     */     private int[] keys;
/*     */     private int[] indexColumns;
/*     */     private String[] columns;
/*     */     private int[] columnTypes;
/*     */     private String indexPath;
/*     */     private FullTextLucene.IndexAccess indexAccess;
/*     */     private final FieldType DOC_ID_FIELD_TYPE;
/*     */     
/*     */     public FullTextTrigger() {
/* 496 */       this.DOC_ID_FIELD_TYPE = new FieldType((IndexableFieldType)TextField.TYPE_STORED);
/* 497 */       this.DOC_ID_FIELD_TYPE.setTokenized(false);
/* 498 */       this.DOC_ID_FIELD_TYPE.freeze();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void init(Connection param1Connection, String param1String1, String param1String2, String param1String3, boolean param1Boolean, int param1Int) throws SQLException {
/* 508 */       this.schema = param1String1;
/* 509 */       this.table = param1String3;
/* 510 */       this.indexPath = FullTextLucene.getIndexPath(param1Connection);
/* 511 */       this.indexAccess = FullTextLucene.getIndexAccess(param1Connection);
/* 512 */       ArrayList<String> arrayList1 = Utils.newSmallArrayList();
/* 513 */       DatabaseMetaData databaseMetaData = param1Connection.getMetaData();
/* 514 */       ResultSet resultSet = databaseMetaData.getColumns(null, 
/* 515 */           StringUtils.escapeMetaDataPattern(param1String1), 
/* 516 */           StringUtils.escapeMetaDataPattern(param1String3), null);
/*     */       
/* 518 */       ArrayList<String> arrayList2 = Utils.newSmallArrayList();
/* 519 */       while (resultSet.next()) {
/* 520 */         arrayList2.add(resultSet.getString("COLUMN_NAME"));
/*     */       }
/* 522 */       this.columnTypes = new int[arrayList2.size()];
/* 523 */       this.columns = arrayList2.<String>toArray(new String[0]);
/* 524 */       resultSet = databaseMetaData.getColumns(null, 
/* 525 */           StringUtils.escapeMetaDataPattern(param1String1), 
/* 526 */           StringUtils.escapeMetaDataPattern(param1String3), null);
/*     */       
/* 528 */       for (byte b = 0; resultSet.next(); b++) {
/* 529 */         this.columnTypes[b] = resultSet.getInt("DATA_TYPE");
/*     */       }
/* 531 */       if (arrayList1.isEmpty()) {
/* 532 */         resultSet = databaseMetaData.getPrimaryKeys(null, 
/* 533 */             StringUtils.escapeMetaDataPattern(param1String1), param1String3);
/*     */         
/* 535 */         while (resultSet.next()) {
/* 536 */           arrayList1.add(resultSet.getString("COLUMN_NAME"));
/*     */         }
/*     */       } 
/* 539 */       if (arrayList1.isEmpty()) {
/* 540 */         throw FullText.throwException("No primary key for table " + param1String3);
/*     */       }
/* 542 */       ArrayList<? super String> arrayList = Utils.newSmallArrayList();
/* 543 */       PreparedStatement preparedStatement = param1Connection.prepareStatement("SELECT COLUMNS FROM FTL.INDEXES WHERE SCHEMA=? AND `TABLE`=?");
/*     */ 
/*     */       
/* 546 */       preparedStatement.setString(1, param1String1);
/* 547 */       preparedStatement.setString(2, param1String3);
/* 548 */       resultSet = preparedStatement.executeQuery();
/* 549 */       if (resultSet.next()) {
/* 550 */         String str = resultSet.getString(1);
/* 551 */         if (str != null) {
/* 552 */           Collections.addAll(arrayList, 
/* 553 */               StringUtils.arraySplit(str, ',', true));
/*     */         }
/*     */       } 
/* 556 */       if (arrayList.isEmpty()) {
/* 557 */         arrayList.addAll(arrayList2);
/*     */       }
/* 559 */       this.keys = new int[arrayList1.size()];
/* 560 */       FullText.setColumns(this.keys, arrayList1, arrayList2);
/* 561 */       this.indexColumns = new int[arrayList.size()];
/* 562 */       FullText.setColumns(this.indexColumns, (ArrayList)arrayList, arrayList2);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void fire(Connection param1Connection, Object[] param1ArrayOfObject1, Object[] param1ArrayOfObject2) throws SQLException {
/* 572 */       if (param1ArrayOfObject1 != null) {
/* 573 */         if (param1ArrayOfObject2 != null) {
/*     */           
/* 575 */           if (FullText.hasChanged(param1ArrayOfObject1, param1ArrayOfObject2, this.indexColumns)) {
/* 576 */             delete(param1ArrayOfObject1, false);
/* 577 */             insert(param1ArrayOfObject2, true);
/*     */           } 
/*     */         } else {
/*     */           
/* 581 */           delete(param1ArrayOfObject1, true);
/*     */         } 
/* 583 */       } else if (param1ArrayOfObject2 != null) {
/*     */         
/* 585 */         insert(param1ArrayOfObject2, true);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws SQLException {
/* 594 */       FullTextLucene.removeIndexAccess(this.indexPath);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void commitIndex() throws SQLException {
/*     */       try {
/* 603 */         this.indexAccess.commit();
/* 604 */       } catch (IOException iOException) {
/* 605 */         throw FullTextLucene.convertException(iOException);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void insert(Object[] param1ArrayOfObject, boolean param1Boolean) throws SQLException {
/* 617 */       String str = getQuery(param1ArrayOfObject);
/* 618 */       Document document = new Document();
/* 619 */       document.add((IndexableField)new Field("_QUERY", str, (IndexableFieldType)this.DOC_ID_FIELD_TYPE));
/* 620 */       long l = System.currentTimeMillis();
/* 621 */       document.add((IndexableField)new Field("_modified", 
/* 622 */             DateTools.timeToString(l, DateTools.Resolution.SECOND), (IndexableFieldType)TextField.TYPE_STORED));
/*     */       
/* 624 */       StringBuilder stringBuilder = new StringBuilder(); byte b; int i;
/* 625 */       for (b = 0, i = this.indexColumns.length; b < i; b++) {
/* 626 */         int j = this.indexColumns[b];
/* 627 */         String str1 = this.columns[j];
/* 628 */         String str2 = FullText.asString(param1ArrayOfObject[j], this.columnTypes[j]);
/*     */ 
/*     */ 
/*     */         
/* 632 */         if (str1.startsWith("_")) {
/* 633 */           str1 = "_" + str1;
/*     */         }
/* 635 */         document.add((IndexableField)new Field(str1, str2, (IndexableFieldType)TextField.TYPE_NOT_STORED));
/* 636 */         if (b > 0) {
/* 637 */           stringBuilder.append(' ');
/*     */         }
/* 639 */         stringBuilder.append(str2);
/*     */       } 
/* 641 */       FieldType fieldType = FullTextLucene.STORE_DOCUMENT_TEXT_IN_INDEX ? TextField.TYPE_STORED : TextField.TYPE_NOT_STORED;
/*     */       
/* 643 */       document.add((IndexableField)new Field("_DATA", stringBuilder.toString(), (IndexableFieldType)fieldType));
/*     */       try {
/* 645 */         this.indexAccess.writer.addDocument((Iterable)document);
/* 646 */         if (param1Boolean) {
/* 647 */           commitIndex();
/*     */         }
/* 649 */       } catch (IOException iOException) {
/* 650 */         throw FullTextLucene.convertException(iOException);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void delete(Object[] param1ArrayOfObject, boolean param1Boolean) throws SQLException {
/* 662 */       String str = getQuery(param1ArrayOfObject);
/*     */       try {
/* 664 */         Term term = new Term("_QUERY", str);
/* 665 */         this.indexAccess.writer.deleteDocuments(new Term[] { term });
/* 666 */         if (param1Boolean) {
/* 667 */           commitIndex();
/*     */         }
/* 669 */       } catch (IOException iOException) {
/* 670 */         throw FullTextLucene.convertException(iOException);
/*     */       } 
/*     */     }
/*     */     
/*     */     private String getQuery(Object[] param1ArrayOfObject) throws SQLException {
/* 675 */       StringBuilder stringBuilder = new StringBuilder();
/* 676 */       if (this.schema != null) {
/* 677 */         StringUtils.quoteIdentifier(stringBuilder, this.schema).append('.');
/*     */       }
/* 679 */       StringUtils.quoteIdentifier(stringBuilder, this.table).append(" WHERE "); byte b; int i;
/* 680 */       for (b = 0, i = this.keys.length; b < i; b++) {
/* 681 */         if (b > 0) {
/* 682 */           stringBuilder.append(" AND ");
/*     */         }
/* 684 */         int j = this.keys[b];
/* 685 */         StringUtils.quoteIdentifier(stringBuilder, this.columns[j]);
/* 686 */         Object object = param1ArrayOfObject[j];
/* 687 */         if (object == null) {
/* 688 */           stringBuilder.append(" IS NULL");
/*     */         } else {
/* 690 */           stringBuilder.append('=').append(FullText.quoteSQL(object, this.columnTypes[j]));
/*     */         } 
/*     */       } 
/* 693 */       return stringBuilder.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class IndexAccess
/*     */   {
/*     */     final IndexWriter writer;
/*     */ 
/*     */ 
/*     */     
/*     */     private IndexSearcher searcher;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     IndexAccess(IndexWriter param1IndexWriter) throws IOException {
/* 713 */       this.writer = param1IndexWriter;
/* 714 */       initializeSearcher();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     synchronized IndexSearcher getSearcher() throws IOException {
/* 724 */       if (!this.searcher.getIndexReader().tryIncRef()) {
/* 725 */         initializeSearcher();
/*     */       }
/* 727 */       return this.searcher;
/*     */     }
/*     */     
/*     */     private void initializeSearcher() throws IOException {
/* 731 */       DirectoryReader directoryReader = DirectoryReader.open(this.writer);
/* 732 */       this.searcher = new IndexSearcher((IndexReader)directoryReader);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     synchronized void returnSearcher(IndexSearcher param1IndexSearcher) throws IOException {
/* 742 */       param1IndexSearcher.getIndexReader().decRef();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized void commit() throws IOException {
/* 750 */       this.writer.commit();
/* 751 */       returnSearcher(this.searcher);
/* 752 */       this.searcher = new IndexSearcher((IndexReader)DirectoryReader.open(this.writer));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized void close() throws IOException {
/* 760 */       this.searcher = null;
/* 761 */       this.writer.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\fulltext\FullTextLucene.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */