/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.Entity;
/*     */ import cn.hutool.db.Page;
/*     */ import java.util.Collection;
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
/*     */ public class Query
/*     */ {
/*     */   Collection<String> fields;
/*     */   String[] tableNames;
/*     */   Condition[] where;
/*     */   Page page;
/*     */   
/*     */   public static Query of(Entity where) {
/*  37 */     return new Query(SqlUtil.buildConditions(where), new String[] { where.getTableName() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query(String... tableNames) {
/*  47 */     this(null, tableNames);
/*  48 */     this.tableNames = tableNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query(Condition[] where, String... tableNames) {
/*  58 */     this(where, null, tableNames);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query(Condition[] where, Page page, String... tableNames) {
/*  69 */     this(null, tableNames, where, page);
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
/*     */   public Query(Collection<String> fields, String[] tableNames, Condition[] where, Page page) {
/*  81 */     this.fields = fields;
/*  82 */     this.tableNames = tableNames;
/*  83 */     this.where = where;
/*  84 */     this.page = page;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> getFields() {
/*  95 */     return this.fields;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query setFields(Collection<String> fields) {
/* 105 */     this.fields = fields;
/* 106 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query setFields(String... fields) {
/* 116 */     this.fields = CollectionUtil.newArrayList((Object[])fields);
/* 117 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getTableNames() {
/* 126 */     return this.tableNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query setTableNames(String... tableNames) {
/* 136 */     this.tableNames = tableNames;
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Condition[] getWhere() {
/* 146 */     return this.where;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query setWhere(Condition... where) {
/* 156 */     this.where = where;
/* 157 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Page getPage() {
/* 166 */     return this.page;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query setPage(Page page) {
/* 176 */     this.page = page;
/* 177 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFirstTableName() throws DbRuntimeException {
/* 188 */     if (ArrayUtil.isEmpty((Object[])this.tableNames)) {
/* 189 */       throw new DbRuntimeException("No tableName!");
/*     */     }
/* 191 */     return this.tableNames[0];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\Query.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */