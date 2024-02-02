/*     */ package cn.hutool.db.meta;
/*     */ 
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndexInfo
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private boolean nonUnique;
/*     */   private String indexName;
/*     */   private String tableName;
/*     */   private String schema;
/*     */   private String catalog;
/*     */   private List<ColumnIndexInfo> columnIndexInfoList;
/*     */   
/*     */   public IndexInfo(boolean nonUnique, String indexName, String tableName, String schema, String catalog) {
/*  59 */     this.nonUnique = nonUnique;
/*  60 */     this.indexName = indexName;
/*  61 */     this.tableName = tableName;
/*  62 */     this.schema = schema;
/*  63 */     this.catalog = catalog;
/*  64 */     setColumnIndexInfoList(new ArrayList<>());
/*     */   }
/*     */   
/*     */   public boolean isNonUnique() {
/*  68 */     return this.nonUnique;
/*     */   }
/*     */   
/*     */   public void setNonUnique(boolean nonUnique) {
/*  72 */     this.nonUnique = nonUnique;
/*     */   }
/*     */   
/*     */   public String getIndexName() {
/*  76 */     return this.indexName;
/*     */   }
/*     */   
/*     */   public void setIndexName(String indexName) {
/*  80 */     this.indexName = indexName;
/*     */   }
/*     */   
/*     */   public String getTableName() {
/*  84 */     return this.tableName;
/*     */   }
/*     */   
/*     */   public void setTableName(String tableName) {
/*  88 */     this.tableName = tableName;
/*     */   }
/*     */   
/*     */   public String getSchema() {
/*  92 */     return this.schema;
/*     */   }
/*     */   
/*     */   public void setSchema(String schema) {
/*  96 */     this.schema = schema;
/*     */   }
/*     */   
/*     */   public String getCatalog() {
/* 100 */     return this.catalog;
/*     */   }
/*     */   
/*     */   public void setCatalog(String catalog) {
/* 104 */     this.catalog = catalog;
/*     */   }
/*     */   
/*     */   public List<ColumnIndexInfo> getColumnIndexInfoList() {
/* 108 */     return this.columnIndexInfoList;
/*     */   }
/*     */   
/*     */   public void setColumnIndexInfoList(List<ColumnIndexInfo> columnIndexInfoList) {
/* 112 */     this.columnIndexInfoList = columnIndexInfoList;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 117 */     if (this == o) {
/* 118 */       return true;
/*     */     }
/* 120 */     if (o == null || getClass() != o.getClass()) {
/* 121 */       return false;
/*     */     }
/* 123 */     IndexInfo indexInfo = (IndexInfo)o;
/* 124 */     return (ObjectUtil.equals(this.indexName, indexInfo.indexName) && 
/* 125 */       ObjectUtil.equals(this.tableName, indexInfo.tableName));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     return Objects.hash(new Object[] { this.indexName, this.tableName });
/*     */   }
/*     */ 
/*     */   
/*     */   public IndexInfo clone() throws CloneNotSupportedException {
/* 135 */     return (IndexInfo)super.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 140 */     return "IndexInfo{nonUnique=" + this.nonUnique + ", indexName='" + this.indexName + '\'' + ", tableName='" + this.tableName + '\'' + ", schema='" + this.schema + '\'' + ", catalog='" + this.catalog + '\'' + ", columnIndexInfoList=" + this.columnIndexInfoList + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\meta\IndexInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */