/*     */ package org.h2.bnf.context;
/*     */ 
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
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
/*     */ public class DbTableOrView
/*     */ {
/*     */   private final DbSchema schema;
/*     */   private final String name;
/*     */   private final String quotedName;
/*     */   private final boolean isView;
/*     */   private DbColumn[] columns;
/*     */   
/*     */   public DbTableOrView(DbSchema paramDbSchema, ResultSet paramResultSet) throws SQLException {
/*  46 */     this.schema = paramDbSchema;
/*  47 */     this.name = paramResultSet.getString("TABLE_NAME");
/*  48 */     String str = paramResultSet.getString("TABLE_TYPE");
/*  49 */     this.isView = "VIEW".equals(str);
/*  50 */     this.quotedName = paramDbSchema.getContents().quoteIdentifier(this.name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbSchema getSchema() {
/*  57 */     return this.schema;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbColumn[] getColumns() {
/*  64 */     return this.columns;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  71 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isView() {
/*  78 */     return this.isView;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQuotedName() {
/*  85 */     return this.quotedName;
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
/*     */   public void readColumns(DatabaseMetaData paramDatabaseMetaData, PreparedStatement paramPreparedStatement) throws SQLException {
/*     */     ResultSet resultSet;
/*  98 */     if (this.schema.getContents().isH2()) {
/*  99 */       paramPreparedStatement.setString(1, this.schema.name);
/* 100 */       paramPreparedStatement.setString(2, this.name);
/* 101 */       resultSet = paramPreparedStatement.executeQuery();
/*     */     } else {
/* 103 */       resultSet = paramDatabaseMetaData.getColumns(null, this.schema.name, this.name, null);
/*     */     } 
/* 105 */     ArrayList<DbColumn> arrayList = new ArrayList();
/* 106 */     while (resultSet.next()) {
/* 107 */       DbColumn dbColumn = DbColumn.getColumn(this.schema.getContents(), resultSet);
/* 108 */       arrayList.add(dbColumn);
/*     */     } 
/* 110 */     resultSet.close();
/* 111 */     this.columns = arrayList.<DbColumn>toArray(new DbColumn[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\context\DbTableOrView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */