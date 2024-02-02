/*     */ package org.h2.bnf;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import org.h2.bnf.context.DbSchema;
/*     */ import org.h2.bnf.context.DbTableOrView;
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
/*     */ public class Sentence
/*     */ {
/*     */   public static final int CONTEXT = 0;
/*     */   public static final int KEYWORD = 1;
/*     */   public static final int FUNCTION = 2;
/*     */   private static final int MAX_PROCESSING_TIME = 100;
/*  44 */   private final HashMap<String, String> next = new HashMap<>();
/*     */ 
/*     */   
/*     */   private String query;
/*     */ 
/*     */   
/*     */   private String queryUpper;
/*     */ 
/*     */   
/*     */   private long stopAtNs;
/*     */   
/*     */   private DbSchema lastMatchedSchema;
/*     */   
/*     */   private DbTableOrView lastMatchedTable;
/*     */   
/*     */   private DbTableOrView lastTable;
/*     */   
/*     */   private HashSet<DbTableOrView> tables;
/*     */   
/*     */   private HashMap<String, DbTableOrView> aliases;
/*     */ 
/*     */   
/*     */   public void start() {
/*  67 */     this.stopAtNs = System.nanoTime() + 100000000L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stopIfRequired() {
/*  76 */     if (System.nanoTime() - this.stopAtNs > 0L) {
/*  77 */       throw new IllegalStateException();
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
/*     */   public void add(String paramString1, String paramString2, int paramInt) {
/*  89 */     this.next.put(paramInt + "#" + paramString1, paramString2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAlias(String paramString, DbTableOrView paramDbTableOrView) {
/*  99 */     if (this.aliases == null) {
/* 100 */       this.aliases = new HashMap<>();
/*     */     }
/* 102 */     this.aliases.put(paramString, paramDbTableOrView);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTable(DbTableOrView paramDbTableOrView) {
/* 111 */     this.lastTable = paramDbTableOrView;
/* 112 */     if (this.tables == null) {
/* 113 */       this.tables = new HashSet<>();
/*     */     }
/* 115 */     this.tables.add(paramDbTableOrView);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashSet<DbTableOrView> getTables() {
/* 124 */     return this.tables;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<String, DbTableOrView> getAliases() {
/* 133 */     return this.aliases;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbTableOrView getLastTable() {
/* 142 */     return this.lastTable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbSchema getLastMatchedSchema() {
/* 151 */     return this.lastMatchedSchema;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastMatchedSchema(DbSchema paramDbSchema) {
/* 161 */     this.lastMatchedSchema = paramDbSchema;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLastMatchedTable(DbTableOrView paramDbTableOrView) {
/* 170 */     this.lastMatchedTable = paramDbTableOrView;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbTableOrView getLastMatchedTable() {
/* 179 */     return this.lastMatchedTable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setQuery(String paramString) {
/* 188 */     if (!Objects.equals(this.query, paramString)) {
/* 189 */       this.query = paramString;
/* 190 */       this.queryUpper = StringUtils.toUpperEnglish(paramString);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQuery() {
/* 200 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQueryUpper() {
/* 209 */     return this.queryUpper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<String, String> getNext() {
/* 218 */     return this.next;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\Sentence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */