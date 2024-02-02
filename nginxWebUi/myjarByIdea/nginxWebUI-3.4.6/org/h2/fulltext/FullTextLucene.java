package org.h2.fulltext;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.h2.api.Trigger;
import org.h2.command.Parser;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionColumn;
import org.h2.jdbc.JdbcConnection;
import org.h2.store.fs.FileUtils;
import org.h2.tools.SimpleResultSet;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class FullTextLucene extends FullText {
   protected static final boolean STORE_DOCUMENT_TEXT_IN_INDEX = Utils.getProperty("h2.storeDocumentTextInIndex", false);
   private static final HashMap<String, IndexAccess> INDEX_ACCESS = new HashMap();
   private static final String TRIGGER_PREFIX = "FTL_";
   private static final String SCHEMA = "FTL";
   private static final String LUCENE_FIELD_DATA = "_DATA";
   private static final String LUCENE_FIELD_QUERY = "_QUERY";
   private static final String LUCENE_FIELD_MODIFIED = "_modified";
   private static final String LUCENE_FIELD_COLUMN_PREFIX = "_";
   private static final String IN_MEMORY_PREFIX = "mem:";

   public static void init(Connection var0) throws SQLException {
      Statement var1 = var0.createStatement();
      Throwable var2 = null;

      try {
         var1.execute("CREATE SCHEMA IF NOT EXISTS FTL");
         var1.execute("CREATE TABLE IF NOT EXISTS FTL.INDEXES(SCHEMA VARCHAR, `TABLE` VARCHAR, COLUMNS VARCHAR, PRIMARY KEY(SCHEMA, `TABLE`))");
         String var3 = FullTextLucene.class.getName();
         var1.execute("CREATE ALIAS IF NOT EXISTS FTL_CREATE_INDEX FOR '" + var3 + ".createIndex'");
         var1.execute("CREATE ALIAS IF NOT EXISTS FTL_DROP_INDEX FOR '" + var3 + ".dropIndex'");
         var1.execute("CREATE ALIAS IF NOT EXISTS FTL_SEARCH FOR '" + var3 + ".search'");
         var1.execute("CREATE ALIAS IF NOT EXISTS FTL_SEARCH_DATA FOR '" + var3 + ".searchData'");
         var1.execute("CREATE ALIAS IF NOT EXISTS FTL_REINDEX FOR '" + var3 + ".reindex'");
         var1.execute("CREATE ALIAS IF NOT EXISTS FTL_DROP_ALL FOR '" + var3 + ".dropAll'");
      } catch (Throwable var11) {
         var2 = var11;
         throw var11;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public static void createIndex(Connection var0, String var1, String var2, String var3) throws SQLException {
      init(var0);
      PreparedStatement var4 = var0.prepareStatement("INSERT INTO FTL.INDEXES(SCHEMA, `TABLE`, COLUMNS) VALUES(?, ?, ?)");
      var4.setString(1, var1);
      var4.setString(2, var2);
      var4.setString(3, var3);
      var4.execute();
      createTrigger(var0, var1, var2);
      indexExistingRows(var0, var1, var2);
   }

   public static void dropIndex(Connection var0, String var1, String var2) throws SQLException {
      init(var0);
      PreparedStatement var3 = var0.prepareStatement("DELETE FROM FTL.INDEXES WHERE SCHEMA=? AND `TABLE`=?");
      var3.setString(1, var1);
      var3.setString(2, var2);
      int var4 = var3.executeUpdate();
      if (var4 != 0) {
         reindex(var0);
      }

   }

   public static void reindex(Connection var0) throws SQLException {
      init(var0);
      removeAllTriggers(var0, "FTL_");
      removeIndexFiles(var0);
      Statement var1 = var0.createStatement();
      ResultSet var2 = var1.executeQuery("SELECT * FROM FTL.INDEXES");

      while(var2.next()) {
         String var3 = var2.getString("SCHEMA");
         String var4 = var2.getString("TABLE");
         createTrigger(var0, var3, var4);
         indexExistingRows(var0, var3, var4);
      }

   }

   public static void dropAll(Connection var0) throws SQLException {
      Statement var1 = var0.createStatement();
      var1.execute("DROP SCHEMA IF EXISTS FTL CASCADE");
      removeAllTriggers(var0, "FTL_");
      removeIndexFiles(var0);
   }

   public static ResultSet search(Connection var0, String var1, int var2, int var3) throws SQLException {
      return search(var0, var1, var2, var3, false);
   }

   public static ResultSet searchData(Connection var0, String var1, int var2, int var3) throws SQLException {
      return search(var0, var1, var2, var3, true);
   }

   protected static SQLException convertException(Exception var0) {
      return new SQLException("Error while indexing document", "FULLTEXT", var0);
   }

   private static void createTrigger(Connection var0, String var1, String var2) throws SQLException {
      createOrDropTrigger(var0, var1, var2, true);
   }

   private static void createOrDropTrigger(Connection var0, String var1, String var2, boolean var3) throws SQLException {
      Statement var4 = var0.createStatement();
      String var5 = StringUtils.quoteIdentifier(var1) + "." + StringUtils.quoteIdentifier("FTL_" + var2);
      var4.execute("DROP TRIGGER IF EXISTS " + var5);
      if (var3) {
         StringBuilder var6 = new StringBuilder("CREATE TRIGGER IF NOT EXISTS ");
         var6.append(var5).append(" AFTER INSERT, UPDATE, DELETE, ROLLBACK ON ");
         StringUtils.quoteIdentifier(var6, var1).append('.');
         StringUtils.quoteIdentifier(var6, var2).append(" FOR EACH ROW CALL \"").append(FullTextTrigger.class.getName()).append('"');
         var4.execute(var6.toString());
      }

   }

   protected static IndexAccess getIndexAccess(Connection var0) throws SQLException {
      String var1 = getIndexPath(var0);
      synchronized(INDEX_ACCESS) {
         IndexAccess var3 = (IndexAccess)INDEX_ACCESS.get(var1);

         while(var3 == null) {
            try {
               Object var4 = var1.startsWith("mem:") ? new ByteBuffersDirectory() : FSDirectory.open(Paths.get(var1));
               StandardAnalyzer var5 = new StandardAnalyzer();
               IndexWriterConfig var6 = new IndexWriterConfig(var5);
               var6.setOpenMode(OpenMode.CREATE_OR_APPEND);
               IndexWriter var7 = new IndexWriter((Directory)var4, var6);
               var3 = new IndexAccess(var7);
            } catch (IndexFormatTooOldException var9) {
               reindex(var0);
               continue;
            } catch (IOException var10) {
               throw convertException(var10);
            }

            INDEX_ACCESS.put(var1, var3);
            break;
         }

         return var3;
      }
   }

   protected static String getIndexPath(Connection var0) throws SQLException {
      Statement var1 = var0.createStatement();
      ResultSet var2 = var1.executeQuery("CALL DATABASE_PATH()");
      var2.next();
      String var3 = var2.getString(1);
      if (var3 == null) {
         return "mem:" + var0.getCatalog();
      } else {
         int var4 = var3.lastIndexOf(58);
         if (var4 > 1) {
            var3 = var3.substring(var4 + 1);
         }

         var2.close();
         return var3;
      }
   }

   private static void indexExistingRows(Connection var0, String var1, String var2) throws SQLException {
      FullTextTrigger var3 = new FullTextTrigger();
      var3.init(var0, var1, (String)null, var2, false, 1);
      String var4 = "SELECT * FROM " + StringUtils.quoteIdentifier(var1) + "." + StringUtils.quoteIdentifier(var2);
      ResultSet var5 = var0.createStatement().executeQuery(var4);
      int var6 = var5.getMetaData().getColumnCount();

      while(var5.next()) {
         Object[] var7 = new Object[var6];

         for(int var8 = 0; var8 < var6; ++var8) {
            var7[var8] = var5.getObject(var8 + 1);
         }

         var3.insert(var7, false);
      }

      var3.commitIndex();
   }

   private static void removeIndexFiles(Connection var0) throws SQLException {
      String var1 = getIndexPath(var0);
      removeIndexAccess(var1);
      if (!var1.startsWith("mem:")) {
         FileUtils.deleteRecursive(var1, false);
      }

   }

   protected static void removeIndexAccess(String var0) throws SQLException {
      synchronized(INDEX_ACCESS) {
         try {
            IndexAccess var2 = (IndexAccess)INDEX_ACCESS.remove(var0);
            if (var2 != null) {
               var2.close();
            }
         } catch (Exception var4) {
            throw convertException(var4);
         }

      }
   }

   protected static ResultSet search(Connection var0, String var1, int var2, int var3, boolean var4) throws SQLException {
      SimpleResultSet var5 = createResultSet(var4);
      if (var0.getMetaData().getURL().startsWith("jdbc:columnlist:")) {
         return var5;
      } else if (var1 != null && !StringUtils.isWhitespaceOrEmpty(var1)) {
         try {
            IndexAccess var6 = getIndexAccess(var0);
            IndexSearcher var7 = var6.getSearcher();

            try {
               Analyzer var8 = var6.writer.getAnalyzer();
               StandardQueryParser var9 = new StandardQueryParser(var8);
               Query var10 = var9.parse(var1, "_DATA");
               int var11 = (var2 == 0 ? 100 : var2) + var3;
               TopDocs var12 = var7.search(var10, var11);
               long var13 = var12.totalHits.value;
               if (var2 == 0) {
                  var2 = (int)var13;
               }

               int var15 = 0;

               for(int var16 = var12.scoreDocs.length; var15 < var2 && (long)(var15 + var3) < var13 && var15 + var3 < var16; ++var15) {
                  ScoreDoc var17 = var12.scoreDocs[var15 + var3];
                  Document var18 = var7.doc(var17.doc);
                  float var19 = var17.score;
                  String var20 = var18.get("_QUERY");
                  if (var4) {
                     int var21 = var20.indexOf(" WHERE ");
                     JdbcConnection var22 = (JdbcConnection)var0;
                     SessionLocal var23 = (SessionLocal)var22.getSession();
                     Parser var24 = new Parser(var23);
                     String var25 = var20.substring(0, var21);
                     ExpressionColumn var26 = (ExpressionColumn)var24.parseExpression(var25);
                     String var27 = var26.getOriginalTableAliasName();
                     String var28 = var26.getColumnName(var23, -1);
                     var20 = var20.substring(var21 + " WHERE ".length());
                     String[][] var29 = parseKey(var0, var20);
                     var5.addRow(var27, var28, var29[0], var29[1], var19);
                  } else {
                     var5.addRow(var20, var19);
                  }
               }
            } finally {
               var6.returnSearcher(var7);
            }

            return var5;
         } catch (Exception var34) {
            throw convertException(var34);
         }
      } else {
         return var5;
      }
   }

   private static final class IndexAccess {
      final IndexWriter writer;
      private IndexSearcher searcher;

      IndexAccess(IndexWriter var1) throws IOException {
         this.writer = var1;
         this.initializeSearcher();
      }

      synchronized IndexSearcher getSearcher() throws IOException {
         if (!this.searcher.getIndexReader().tryIncRef()) {
            this.initializeSearcher();
         }

         return this.searcher;
      }

      private void initializeSearcher() throws IOException {
         DirectoryReader var1 = DirectoryReader.open(this.writer);
         this.searcher = new IndexSearcher(var1);
      }

      synchronized void returnSearcher(IndexSearcher var1) throws IOException {
         var1.getIndexReader().decRef();
      }

      public synchronized void commit() throws IOException {
         this.writer.commit();
         this.returnSearcher(this.searcher);
         this.searcher = new IndexSearcher(DirectoryReader.open(this.writer));
      }

      public synchronized void close() throws IOException {
         this.searcher = null;
         this.writer.close();
      }
   }

   public static final class FullTextTrigger implements Trigger {
      private String schema;
      private String table;
      private int[] keys;
      private int[] indexColumns;
      private String[] columns;
      private int[] columnTypes;
      private String indexPath;
      private IndexAccess indexAccess;
      private final FieldType DOC_ID_FIELD_TYPE;

      public FullTextTrigger() {
         this.DOC_ID_FIELD_TYPE = new FieldType(TextField.TYPE_STORED);
         this.DOC_ID_FIELD_TYPE.setTokenized(false);
         this.DOC_ID_FIELD_TYPE.freeze();
      }

      public void init(Connection var1, String var2, String var3, String var4, boolean var5, int var6) throws SQLException {
         this.schema = var2;
         this.table = var4;
         this.indexPath = FullTextLucene.getIndexPath(var1);
         this.indexAccess = FullTextLucene.getIndexAccess(var1);
         ArrayList var7 = Utils.newSmallArrayList();
         DatabaseMetaData var8 = var1.getMetaData();
         ResultSet var9 = var8.getColumns((String)null, StringUtils.escapeMetaDataPattern(var2), StringUtils.escapeMetaDataPattern(var4), (String)null);
         ArrayList var10 = Utils.newSmallArrayList();

         while(var9.next()) {
            var10.add(var9.getString("COLUMN_NAME"));
         }

         this.columnTypes = new int[var10.size()];
         this.columns = (String[])var10.toArray(new String[0]);
         var9 = var8.getColumns((String)null, StringUtils.escapeMetaDataPattern(var2), StringUtils.escapeMetaDataPattern(var4), (String)null);

         for(int var11 = 0; var9.next(); ++var11) {
            this.columnTypes[var11] = var9.getInt("DATA_TYPE");
         }

         if (var7.isEmpty()) {
            var9 = var8.getPrimaryKeys((String)null, StringUtils.escapeMetaDataPattern(var2), var4);

            while(var9.next()) {
               var7.add(var9.getString("COLUMN_NAME"));
            }
         }

         if (var7.isEmpty()) {
            throw FullText.throwException("No primary key for table " + var4);
         } else {
            ArrayList var14 = Utils.newSmallArrayList();
            PreparedStatement var12 = var1.prepareStatement("SELECT COLUMNS FROM FTL.INDEXES WHERE SCHEMA=? AND `TABLE`=?");
            var12.setString(1, var2);
            var12.setString(2, var4);
            var9 = var12.executeQuery();
            if (var9.next()) {
               String var13 = var9.getString(1);
               if (var13 != null) {
                  Collections.addAll(var14, StringUtils.arraySplit(var13, ',', true));
               }
            }

            if (var14.isEmpty()) {
               var14.addAll(var10);
            }

            this.keys = new int[var7.size()];
            FullText.setColumns(this.keys, var7, var10);
            this.indexColumns = new int[var14.size()];
            FullText.setColumns(this.indexColumns, var14, var10);
         }
      }

      public void fire(Connection var1, Object[] var2, Object[] var3) throws SQLException {
         if (var2 != null) {
            if (var3 != null) {
               if (FullText.hasChanged(var2, var3, this.indexColumns)) {
                  this.delete(var2, false);
                  this.insert(var3, true);
               }
            } else {
               this.delete(var2, true);
            }
         } else if (var3 != null) {
            this.insert(var3, true);
         }

      }

      public void close() throws SQLException {
         FullTextLucene.removeIndexAccess(this.indexPath);
      }

      void commitIndex() throws SQLException {
         try {
            this.indexAccess.commit();
         } catch (IOException var2) {
            throw FullTextLucene.convertException(var2);
         }
      }

      void insert(Object[] var1, boolean var2) throws SQLException {
         String var3 = this.getQuery(var1);
         Document var4 = new Document();
         var4.add(new Field("_QUERY", var3, this.DOC_ID_FIELD_TYPE));
         long var5 = System.currentTimeMillis();
         var4.add(new Field("_modified", DateTools.timeToString(var5, Resolution.SECOND), TextField.TYPE_STORED));
         StringBuilder var7 = new StringBuilder();
         int var8 = 0;

         for(int var9 = this.indexColumns.length; var8 < var9; ++var8) {
            int var10 = this.indexColumns[var8];
            String var11 = this.columns[var10];
            String var12 = FullText.asString(var1[var10], this.columnTypes[var10]);
            if (var11.startsWith("_")) {
               var11 = "_" + var11;
            }

            var4.add(new Field(var11, var12, TextField.TYPE_NOT_STORED));
            if (var8 > 0) {
               var7.append(' ');
            }

            var7.append(var12);
         }

         FieldType var14 = FullTextLucene.STORE_DOCUMENT_TEXT_IN_INDEX ? TextField.TYPE_STORED : TextField.TYPE_NOT_STORED;
         var4.add(new Field("_DATA", var7.toString(), var14));

         try {
            this.indexAccess.writer.addDocument(var4);
            if (var2) {
               this.commitIndex();
            }

         } catch (IOException var13) {
            throw FullTextLucene.convertException(var13);
         }
      }

      private void delete(Object[] var1, boolean var2) throws SQLException {
         String var3 = this.getQuery(var1);

         try {
            Term var4 = new Term("_QUERY", var3);
            this.indexAccess.writer.deleteDocuments(new Term[]{var4});
            if (var2) {
               this.commitIndex();
            }

         } catch (IOException var5) {
            throw FullTextLucene.convertException(var5);
         }
      }

      private String getQuery(Object[] var1) throws SQLException {
         StringBuilder var2 = new StringBuilder();
         if (this.schema != null) {
            StringUtils.quoteIdentifier(var2, this.schema).append('.');
         }

         StringUtils.quoteIdentifier(var2, this.table).append(" WHERE ");
         int var3 = 0;

         for(int var4 = this.keys.length; var3 < var4; ++var3) {
            if (var3 > 0) {
               var2.append(" AND ");
            }

            int var5 = this.keys[var3];
            StringUtils.quoteIdentifier(var2, this.columns[var5]);
            Object var6 = var1[var5];
            if (var6 == null) {
               var2.append(" IS NULL");
            } else {
               var2.append('=').append(FullText.quoteSQL(var6, this.columnTypes[var5]));
            }
         }

         return var2.toString();
      }
   }
}
