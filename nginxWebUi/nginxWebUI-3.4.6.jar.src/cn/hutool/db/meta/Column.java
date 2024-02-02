/*     */ package cn.hutool.db.meta;
/*     */ 
/*     */ import cn.hutool.core.util.BooleanUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import java.io.Serializable;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Column
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = 577527740359719367L;
/*     */   private String tableName;
/*     */   private String name;
/*     */   private int type;
/*     */   private String typeName;
/*     */   private long size;
/*     */   private Integer digit;
/*     */   private boolean isNullable;
/*     */   private String comment;
/*     */   private boolean autoIncrement;
/*     */   private String columnDef;
/*     */   private boolean isPk;
/*     */   
/*     */   public static Column create(Table table, ResultSet columnMetaRs) {
/*  74 */     return new Column(table, columnMetaRs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column(Table table, ResultSet columnMetaRs) {
/*     */     try {
/*  94 */       init(table, columnMetaRs);
/*  95 */     } catch (SQLException e) {
/*  96 */       throw new DbRuntimeException(e, "Get table [{}] meta info error!", new Object[] { this.tableName });
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
/*     */   public void init(Table table, ResultSet columnMetaRs) throws SQLException {
/* 109 */     this.tableName = table.getTableName();
/*     */     
/* 111 */     this.name = columnMetaRs.getString("COLUMN_NAME");
/* 112 */     this.isPk = table.isPk(this.name);
/*     */     
/* 114 */     this.type = columnMetaRs.getInt("DATA_TYPE");
/*     */     
/* 116 */     String typeName = columnMetaRs.getString("TYPE_NAME");
/*     */     
/* 118 */     typeName = ReUtil.delLast("\\(\\d+\\)", typeName);
/* 119 */     this.typeName = typeName;
/*     */     
/* 121 */     this.size = columnMetaRs.getLong("COLUMN_SIZE");
/* 122 */     this.isNullable = columnMetaRs.getBoolean("NULLABLE");
/* 123 */     this.comment = columnMetaRs.getString("REMARKS");
/* 124 */     this.columnDef = columnMetaRs.getString("COLUMN_DEF");
/*     */ 
/*     */     
/*     */     try {
/* 128 */       this.digit = Integer.valueOf(columnMetaRs.getInt("DECIMAL_DIGITS"));
/* 129 */     } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 135 */       String auto = columnMetaRs.getString("IS_AUTOINCREMENT");
/* 136 */       if (BooleanUtil.toBoolean(auto)) {
/* 137 */         this.autoIncrement = true;
/*     */       }
/* 139 */     } catch (SQLException sQLException) {}
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
/*     */   public String getTableName() {
/* 152 */     return this.tableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setTableName(String tableName) {
/* 162 */     this.tableName = tableName;
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 172 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setName(String name) {
/* 182 */     this.name = name;
/* 183 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdbcType getTypeEnum() {
/* 193 */     return JdbcType.valueOf(this.type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 202 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setType(int type) {
/* 212 */     this.type = type;
/* 213 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTypeName() {
/* 222 */     return this.typeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setTypeName(String typeName) {
/* 232 */     this.typeName = typeName;
/* 233 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getSize() {
/* 242 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setSize(int size) {
/* 252 */     this.size = size;
/* 253 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDigit() {
/* 262 */     return this.digit.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setDigit(int digit) {
/* 272 */     this.digit = Integer.valueOf(digit);
/* 273 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNullable() {
/* 282 */     return this.isNullable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setNullable(boolean isNullable) {
/* 292 */     this.isNullable = isNullable;
/* 293 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getComment() {
/* 302 */     return this.comment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setComment(String comment) {
/* 312 */     this.comment = comment;
/* 313 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAutoIncrement() {
/* 323 */     return this.autoIncrement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setAutoIncrement(boolean autoIncrement) {
/* 334 */     this.autoIncrement = autoIncrement;
/* 335 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPk() {
/* 345 */     return this.isPk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setPk(boolean isPk) {
/* 356 */     this.isPk = isPk;
/* 357 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getColumnDef() {
/* 365 */     return this.columnDef;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column setColumnDef(String columnDef) {
/* 375 */     this.columnDef = columnDef;
/* 376 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 383 */     return "Column [tableName=" + this.tableName + ", name=" + this.name + ", type=" + this.type + ", size=" + this.size + ", isNullable=" + this.isNullable + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public Column clone() throws CloneNotSupportedException {
/* 388 */     return (Column)super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\meta\Column.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */