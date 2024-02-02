/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
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
/*     */ public class CreateIndex
/*     */   extends SchemaCommand
/*     */ {
/*     */   private String tableName;
/*     */   private String indexName;
/*     */   private IndexColumn[] indexColumns;
/*     */   private int uniqueColumnCount;
/*     */   private boolean primaryKey;
/*     */   private boolean hash;
/*     */   private boolean spatial;
/*     */   private boolean ifTableExists;
/*     */   private boolean ifNotExists;
/*     */   private String comment;
/*     */   
/*     */   public CreateIndex(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  36 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public void setIfTableExists(boolean paramBoolean) {
/*  40 */     this.ifTableExists = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  44 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTableName(String paramString) {
/*  48 */     this.tableName = paramString;
/*     */   }
/*     */   
/*     */   public void setIndexName(String paramString) {
/*  52 */     this.indexName = paramString;
/*     */   }
/*     */   
/*     */   public void setIndexColumns(IndexColumn[] paramArrayOfIndexColumn) {
/*  56 */     this.indexColumns = paramArrayOfIndexColumn;
/*     */   }
/*     */   
/*     */   public long update() {
/*     */     IndexType indexType;
/*  61 */     Database database = this.session.getDatabase();
/*  62 */     boolean bool = database.isPersistent();
/*  63 */     Table table = getSchema().findTableOrView(this.session, this.tableName);
/*  64 */     if (table == null) {
/*  65 */       if (this.ifTableExists) {
/*  66 */         return 0L;
/*     */       }
/*  68 */       throw DbException.get(42102, this.tableName);
/*     */     } 
/*  70 */     if (this.indexName != null && getSchema().findIndex(this.session, this.indexName) != null) {
/*  71 */       if (this.ifNotExists) {
/*  72 */         return 0L;
/*     */       }
/*  74 */       throw DbException.get(42111, this.indexName);
/*     */     } 
/*  76 */     this.session.getUser().checkTableRight(table, 32);
/*  77 */     table.lock(this.session, 2);
/*  78 */     if (!table.isPersistIndexes()) {
/*  79 */       bool = false;
/*     */     }
/*  81 */     int i = getObjectId();
/*  82 */     if (this.indexName == null) {
/*  83 */       if (this.primaryKey) {
/*  84 */         this.indexName = table.getSchema().getUniqueIndexName(this.session, table, "PRIMARY_KEY_");
/*     */       } else {
/*     */         
/*  87 */         this.indexName = table.getSchema().getUniqueIndexName(this.session, table, "INDEX_");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  92 */     if (this.primaryKey) {
/*  93 */       if (table.findPrimaryKey() != null) {
/*  94 */         throw DbException.get(90017);
/*     */       }
/*  96 */       indexType = IndexType.createPrimaryKey(bool, this.hash);
/*  97 */     } else if (this.uniqueColumnCount > 0) {
/*  98 */       indexType = IndexType.createUnique(bool, this.hash);
/*     */     } else {
/* 100 */       indexType = IndexType.createNonUnique(bool, this.hash, this.spatial);
/*     */     } 
/* 102 */     IndexColumn.mapColumns(this.indexColumns, table);
/* 103 */     table.addIndex(this.session, this.indexName, i, this.indexColumns, this.uniqueColumnCount, indexType, this.create, this.comment);
/* 104 */     return 0L;
/*     */   }
/*     */   
/*     */   public void setPrimaryKey(boolean paramBoolean) {
/* 108 */     this.primaryKey = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setUniqueColumnCount(int paramInt) {
/* 112 */     this.uniqueColumnCount = paramInt;
/*     */   }
/*     */   
/*     */   public void setHash(boolean paramBoolean) {
/* 116 */     this.hash = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setSpatial(boolean paramBoolean) {
/* 120 */     this.spatial = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/* 124 */     this.comment = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 129 */     return 25;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */