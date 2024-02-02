/*     */ package org.h2.bnf.context;
/*     */ 
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
/*     */ public class DbColumn
/*     */ {
/*     */   private final String name;
/*     */   private final String quotedName;
/*     */   private final String dataType;
/*     */   private final int position;
/*     */   
/*     */   private DbColumn(DbContents paramDbContents, ResultSet paramResultSet, boolean paramBoolean) throws SQLException {
/*     */     String str2, str3;
/*  28 */     this.name = paramResultSet.getString("COLUMN_NAME");
/*  29 */     this.quotedName = paramDbContents.quoteIdentifier(this.name);
/*  30 */     this.position = paramResultSet.getInt("ORDINAL_POSITION");
/*  31 */     if (paramDbContents.isH2() && !paramBoolean) {
/*  32 */       this.dataType = paramResultSet.getString("COLUMN_TYPE");
/*     */       return;
/*     */     } 
/*  35 */     String str1 = paramResultSet.getString("TYPE_NAME");
/*     */ 
/*     */ 
/*     */     
/*  39 */     if (paramBoolean) {
/*  40 */       str2 = "PRECISION";
/*  41 */       str3 = "SCALE";
/*     */     } else {
/*  43 */       str2 = "COLUMN_SIZE";
/*  44 */       str3 = "DECIMAL_DIGITS";
/*     */     } 
/*  46 */     int i = paramResultSet.getInt(str2);
/*  47 */     if (i > 0 && !paramDbContents.isSQLite()) {
/*  48 */       int j = paramResultSet.getInt(str3);
/*  49 */       if (j > 0) {
/*  50 */         str1 = str1 + '(' + i + ", " + j + ')';
/*     */       } else {
/*  52 */         str1 = str1 + '(' + i + ')';
/*     */       } 
/*     */     } 
/*  55 */     if (paramResultSet.getInt("NULLABLE") == 0) {
/*  56 */       str1 = str1 + " NOT NULL";
/*     */     }
/*  58 */     this.dataType = str1;
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
/*     */   public static DbColumn getProcedureColumn(DbContents paramDbContents, ResultSet paramResultSet) throws SQLException {
/*  71 */     return new DbColumn(paramDbContents, paramResultSet, true);
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
/*     */   public static DbColumn getColumn(DbContents paramDbContents, ResultSet paramResultSet) throws SQLException {
/*  84 */     return new DbColumn(paramDbContents, paramResultSet, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDataType() {
/*  92 */     return this.dataType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  99 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQuotedName() {
/* 106 */     return this.quotedName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPosition() {
/* 113 */     return this.position;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\context\DbColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */