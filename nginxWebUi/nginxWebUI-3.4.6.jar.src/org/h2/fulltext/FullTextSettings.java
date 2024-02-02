/*     */ package org.h2.fulltext;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.h2.util.SoftValuesHashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class FullTextSettings
/*     */ {
/*  28 */   private static final HashMap<String, FullTextSettings> SETTINGS = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean initialized;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  38 */   private final HashSet<String> ignoreList = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   private final HashMap<String, Integer> words = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   private final ConcurrentHashMap<Integer, IndexInfo> indexes = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private final WeakHashMap<Connection, SoftValuesHashMap<String, PreparedStatement>> cache = new WeakHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private String whitespaceChars = " \t\n\r\f+\"*%&/()=?'!,.;:-_#@|^~`{}[]<>\\";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearIgnored() {
/*  71 */     synchronized (this.ignoreList) {
/*  72 */       this.ignoreList.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addIgnored(Iterable<String> paramIterable) {
/*  81 */     synchronized (this.ignoreList) {
/*  82 */       for (String str : paramIterable) {
/*  83 */         str = normalizeWord(str);
/*  84 */         this.ignoreList.add(str);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearWordList() {
/*  93 */     synchronized (this.words) {
/*  94 */       this.words.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getWordId(String paramString) {
/* 104 */     synchronized (this.words) {
/* 105 */       return this.words.get(paramString);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addWord(String paramString, Integer paramInteger) {
/* 115 */     synchronized (this.words) {
/* 116 */       this.words.putIfAbsent(paramString, paramInteger);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   IndexInfo getIndexInfo(int paramInt) {
/* 127 */     return this.indexes.get(Integer.valueOf(paramInt));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addIndexInfo(IndexInfo paramIndexInfo) {
/* 136 */     this.indexes.put(Integer.valueOf(paramIndexInfo.id), paramIndexInfo);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String convertWord(String paramString) {
/* 147 */     paramString = normalizeWord(paramString);
/* 148 */     synchronized (this.ignoreList) {
/* 149 */       if (this.ignoreList.contains(paramString)) {
/* 150 */         return null;
/*     */       }
/*     */     } 
/* 153 */     return paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static FullTextSettings getInstance(Connection paramConnection) throws SQLException {
/*     */     FullTextSettings fullTextSettings;
/* 165 */     String str = getIndexPath(paramConnection);
/*     */     
/* 167 */     synchronized (SETTINGS) {
/* 168 */       fullTextSettings = SETTINGS.get(str);
/* 169 */       if (fullTextSettings == null) {
/* 170 */         fullTextSettings = new FullTextSettings();
/* 171 */         SETTINGS.put(str, fullTextSettings);
/*     */       } 
/*     */     } 
/* 174 */     return fullTextSettings;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getIndexPath(Connection paramConnection) throws SQLException {
/* 184 */     Statement statement = paramConnection.createStatement();
/* 185 */     ResultSet resultSet = statement.executeQuery("CALL COALESCE(DATABASE_PATH(), 'MEM:' || DATABASE())");
/*     */     
/* 187 */     resultSet.next();
/* 188 */     String str = resultSet.getString(1);
/* 189 */     if ("MEM:UNNAMED".equals(str)) {
/* 190 */       throw FullText.throwException("Fulltext search for private (unnamed) in-memory databases is not supported.");
/*     */     }
/*     */ 
/*     */     
/* 194 */     resultSet.close();
/* 195 */     return str;
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
/*     */   synchronized PreparedStatement prepare(Connection paramConnection, String paramString) throws SQLException {
/* 208 */     SoftValuesHashMap<String, PreparedStatement> softValuesHashMap = this.cache.get(paramConnection);
/* 209 */     if (softValuesHashMap == null) {
/* 210 */       softValuesHashMap = new SoftValuesHashMap();
/* 211 */       this.cache.put(paramConnection, softValuesHashMap);
/*     */     } 
/* 213 */     PreparedStatement preparedStatement = (PreparedStatement)softValuesHashMap.get(paramString);
/* 214 */     if (preparedStatement != null && preparedStatement.getConnection().isClosed()) {
/* 215 */       preparedStatement = null;
/*     */     }
/* 217 */     if (preparedStatement == null) {
/* 218 */       preparedStatement = paramConnection.prepareStatement(paramString);
/* 219 */       softValuesHashMap.put(paramString, preparedStatement);
/*     */     } 
/* 221 */     return preparedStatement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void removeAllIndexes() {
/* 228 */     this.indexes.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void removeIndexInfo(IndexInfo paramIndexInfo) {
/* 237 */     this.indexes.remove(Integer.valueOf(paramIndexInfo.id));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setInitialized(boolean paramBoolean) {
/* 246 */     this.initialized = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isInitialized() {
/* 255 */     return this.initialized;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void closeAll() {
/* 262 */     synchronized (SETTINGS) {
/* 263 */       SETTINGS.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void setWhitespaceChars(String paramString) {
/* 268 */     this.whitespaceChars = paramString;
/*     */   }
/*     */   
/*     */   protected String getWhitespaceChars() {
/* 272 */     return this.whitespaceChars;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String normalizeWord(String paramString) {
/* 277 */     return paramString.toUpperCase();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\fulltext\FullTextSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */