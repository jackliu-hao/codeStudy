package org.h2.fulltext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.util.SoftValuesHashMap;

final class FullTextSettings {
   private static final HashMap<String, FullTextSettings> SETTINGS = new HashMap();
   private boolean initialized;
   private final HashSet<String> ignoreList = new HashSet();
   private final HashMap<String, Integer> words = new HashMap();
   private final ConcurrentHashMap<Integer, IndexInfo> indexes = new ConcurrentHashMap();
   private final WeakHashMap<Connection, SoftValuesHashMap<String, PreparedStatement>> cache = new WeakHashMap();
   private String whitespaceChars = " \t\n\r\f+\"*%&/()=?'!,.;:-_#@|^~`{}[]<>\\";

   private FullTextSettings() {
   }

   public void clearIgnored() {
      synchronized(this.ignoreList) {
         this.ignoreList.clear();
      }
   }

   public void addIgnored(Iterable<String> var1) {
      synchronized(this.ignoreList) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var4 = normalizeWord(var4);
            this.ignoreList.add(var4);
         }

      }
   }

   public void clearWordList() {
      synchronized(this.words) {
         this.words.clear();
      }
   }

   public Integer getWordId(String var1) {
      synchronized(this.words) {
         return (Integer)this.words.get(var1);
      }
   }

   public void addWord(String var1, Integer var2) {
      synchronized(this.words) {
         this.words.putIfAbsent(var1, var2);
      }
   }

   IndexInfo getIndexInfo(int var1) {
      return (IndexInfo)this.indexes.get(var1);
   }

   void addIndexInfo(IndexInfo var1) {
      this.indexes.put(var1.id, var1);
   }

   String convertWord(String var1) {
      var1 = normalizeWord(var1);
      synchronized(this.ignoreList) {
         return this.ignoreList.contains(var1) ? null : var1;
      }
   }

   static FullTextSettings getInstance(Connection var0) throws SQLException {
      String var1 = getIndexPath(var0);
      synchronized(SETTINGS) {
         FullTextSettings var2 = (FullTextSettings)SETTINGS.get(var1);
         if (var2 == null) {
            var2 = new FullTextSettings();
            SETTINGS.put(var1, var2);
         }

         return var2;
      }
   }

   private static String getIndexPath(Connection var0) throws SQLException {
      Statement var1 = var0.createStatement();
      ResultSet var2 = var1.executeQuery("CALL COALESCE(DATABASE_PATH(), 'MEM:' || DATABASE())");
      var2.next();
      String var3 = var2.getString(1);
      if ("MEM:UNNAMED".equals(var3)) {
         throw FullText.throwException("Fulltext search for private (unnamed) in-memory databases is not supported.");
      } else {
         var2.close();
         return var3;
      }
   }

   synchronized PreparedStatement prepare(Connection var1, String var2) throws SQLException {
      SoftValuesHashMap var3 = (SoftValuesHashMap)this.cache.get(var1);
      if (var3 == null) {
         var3 = new SoftValuesHashMap();
         this.cache.put(var1, var3);
      }

      PreparedStatement var4 = (PreparedStatement)var3.get(var2);
      if (var4 != null && var4.getConnection().isClosed()) {
         var4 = null;
      }

      if (var4 == null) {
         var4 = var1.prepareStatement(var2);
         var3.put(var2, var4);
      }

      return var4;
   }

   protected void removeAllIndexes() {
      this.indexes.clear();
   }

   protected void removeIndexInfo(IndexInfo var1) {
      this.indexes.remove(var1.id);
   }

   protected void setInitialized(boolean var1) {
      this.initialized = var1;
   }

   protected boolean isInitialized() {
      return this.initialized;
   }

   protected static void closeAll() {
      synchronized(SETTINGS) {
         SETTINGS.clear();
      }
   }

   protected void setWhitespaceChars(String var1) {
      this.whitespaceChars = var1;
   }

   protected String getWhitespaceChars() {
      return this.whitespaceChars;
   }

   private static String normalizeWord(String var0) {
      return var0.toUpperCase();
   }
}
