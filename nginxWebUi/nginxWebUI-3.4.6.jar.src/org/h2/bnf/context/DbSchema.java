/*     */ package org.h2.bnf.context;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLSyntaxErrorException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
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
/*     */ public class DbSchema
/*     */ {
/*     */   private static final String COLUMNS_QUERY_H2_197 = "SELECT COLUMN_NAME, ORDINAL_POSITION, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2";
/*     */   private static final String COLUMNS_QUERY_H2_202 = "SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE_SQL(?1, ?2, 'TABLE', ORDINAL_POSITION) COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2";
/*     */   public final String name;
/*     */   public final boolean isDefault;
/*     */   public final boolean isSystem;
/*     */   public final String quotedName;
/*     */   private final DbContents contents;
/*     */   private DbTableOrView[] tables;
/*     */   private DbProcedure[] procedures;
/*     */   
/*     */   DbSchema(DbContents paramDbContents, String paramString, boolean paramBoolean) {
/*  69 */     this.contents = paramDbContents;
/*  70 */     this.name = paramString;
/*  71 */     this.quotedName = paramDbContents.quoteIdentifier(paramString);
/*  72 */     this.isDefault = paramBoolean;
/*  73 */     if (paramString == null) {
/*     */       
/*  75 */       this.isSystem = true;
/*  76 */     } else if ("INFORMATION_SCHEMA".equalsIgnoreCase(paramString)) {
/*  77 */       this.isSystem = true;
/*  78 */     } else if (!paramDbContents.isH2() && 
/*  79 */       StringUtils.toUpperEnglish(paramString).startsWith("INFO")) {
/*  80 */       this.isSystem = true;
/*  81 */     } else if (paramDbContents.isPostgreSQL() && 
/*  82 */       StringUtils.toUpperEnglish(paramString).startsWith("PG_")) {
/*  83 */       this.isSystem = true;
/*  84 */     } else if (paramDbContents.isDerby() && paramString.startsWith("SYS")) {
/*  85 */       this.isSystem = true;
/*     */     } else {
/*  87 */       this.isSystem = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbContents getContents() {
/*  95 */     return this.contents;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbTableOrView[] getTables() {
/* 102 */     return this.tables;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbProcedure[] getProcedures() {
/* 109 */     return this.procedures;
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
/*     */   public void readTables(DatabaseMetaData paramDatabaseMetaData, String[] paramArrayOfString) throws SQLException {
/* 121 */     ResultSet resultSet = paramDatabaseMetaData.getTables(null, this.name, null, paramArrayOfString);
/* 122 */     ArrayList<DbTableOrView> arrayList = new ArrayList();
/* 123 */     while (resultSet.next()) {
/* 124 */       DbTableOrView dbTableOrView = new DbTableOrView(this, resultSet);
/* 125 */       if (this.contents.isOracle() && dbTableOrView.getName().indexOf('$') > 0) {
/*     */         continue;
/*     */       }
/* 128 */       arrayList.add(dbTableOrView);
/*     */     } 
/* 130 */     resultSet.close();
/* 131 */     this.tables = arrayList.<DbTableOrView>toArray(new DbTableOrView[0]);
/* 132 */     if (this.tables.length < SysProperties.CONSOLE_MAX_TABLES_LIST_COLUMNS) {
/* 133 */       try (PreparedStatement null = this.contents.isH2() ? prepareColumnsQueryH2(paramDatabaseMetaData.getConnection()) : null) {
/* 134 */         for (DbTableOrView dbTableOrView : this.tables) {
/*     */           try {
/* 136 */             dbTableOrView.readColumns(paramDatabaseMetaData, preparedStatement);
/* 137 */           } catch (SQLException sQLException) {}
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static PreparedStatement prepareColumnsQueryH2(Connection paramConnection) throws SQLException {
/*     */     try {
/* 151 */       return paramConnection.prepareStatement("SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE_SQL(?1, ?2, 'TABLE', ORDINAL_POSITION) COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2");
/* 152 */     } catch (SQLSyntaxErrorException sQLSyntaxErrorException) {
/* 153 */       return paramConnection.prepareStatement("SELECT COLUMN_NAME, ORDINAL_POSITION, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void readProcedures(DatabaseMetaData paramDatabaseMetaData) throws SQLException {
/* 164 */     ResultSet resultSet = paramDatabaseMetaData.getProcedures(null, this.name, null);
/* 165 */     ArrayList<DbProcedure> arrayList = Utils.newSmallArrayList();
/* 166 */     while (resultSet.next()) {
/* 167 */       arrayList.add(new DbProcedure(this, resultSet));
/*     */     }
/* 169 */     resultSet.close();
/* 170 */     this.procedures = arrayList.<DbProcedure>toArray(new DbProcedure[0]);
/* 171 */     if (this.procedures.length < SysProperties.CONSOLE_MAX_PROCEDURES_LIST_COLUMNS)
/* 172 */       for (DbProcedure dbProcedure : this.procedures)
/* 173 */         dbProcedure.readParameters(paramDatabaseMetaData);  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\context\DbSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */