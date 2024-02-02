/*     */ package cn.hutool.db.meta;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class Table
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = -810699625961392983L;
/*     */   private String schema;
/*     */   private String catalog;
/*     */   private String tableName;
/*     */   private String comment;
/*  38 */   private Set<String> pkNames = new LinkedHashSet<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private List<IndexInfo> indexInfoList;
/*     */ 
/*     */ 
/*     */   
/*  46 */   private final Map<String, Column> columns = new LinkedHashMap<>();
/*     */   
/*     */   public static Table create(String tableName) {
/*  49 */     return new Table(tableName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table(String tableName) {
/*  60 */     setTableName(tableName);
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
/*     */   public String getSchema() {
/*  73 */     return this.schema;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table setSchema(String schema) {
/*  84 */     this.schema = schema;
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCatalog() {
/*  95 */     return this.catalog;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table setCatalog(String catalog) {
/* 106 */     this.catalog = catalog;
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTableName() {
/* 116 */     return this.tableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTableName(String tableName) {
/* 125 */     this.tableName = tableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getComment() {
/* 134 */     return this.comment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table setComment(String comment) {
/* 144 */     this.comment = comment;
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getPkNames() {
/* 154 */     return this.pkNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPk(String columnName) {
/* 165 */     return getPkNames().contains(columnName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPkNames(Set<String> pkNames) {
/* 174 */     this.pkNames = pkNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table setColumn(Column column) {
/* 185 */     this.columns.put(column.getName(), column);
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column getColumn(String name) {
/* 197 */     return this.columns.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<Column> getColumns() {
/* 207 */     return this.columns.values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table addPk(String pkColumnName) {
/* 217 */     this.pkNames.add(pkColumnName);
/* 218 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<IndexInfo> getIndexInfoList() {
/* 228 */     return this.indexInfoList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIndexInfoList(List<IndexInfo> indexInfoList) {
/* 238 */     this.indexInfoList = indexInfoList;
/*     */   }
/*     */ 
/*     */   
/*     */   public Table clone() throws CloneNotSupportedException {
/* 243 */     return (Table)super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\meta\Table.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */