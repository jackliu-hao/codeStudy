/*     */ package org.h2.table;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.h2.command.ddl.CreateTableData;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.index.IndexType;
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
/*     */ public abstract class TableBase
/*     */   extends Table
/*     */ {
/*     */   private final String tableEngine;
/*     */   private final List<String> tableEngineParams;
/*     */   private final boolean globalTemporary;
/*     */   
/*     */   public static int getMainIndexColumn(IndexType paramIndexType, IndexColumn[] paramArrayOfIndexColumn) {
/*  44 */     if (!paramIndexType.isPrimaryKey() || paramArrayOfIndexColumn.length != 1) {
/*  45 */       return -1;
/*     */     }
/*  47 */     IndexColumn indexColumn = paramArrayOfIndexColumn[0];
/*  48 */     if ((indexColumn.sortType & 0x1) != 0) {
/*  49 */       return -1;
/*     */     }
/*  51 */     switch (indexColumn.column.getType().getValueType()) {
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/*  56 */         return indexColumn.column.getColumnId();
/*     */     } 
/*  58 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableBase(CreateTableData paramCreateTableData) {
/*  63 */     super(paramCreateTableData.schema, paramCreateTableData.id, paramCreateTableData.tableName, paramCreateTableData.persistIndexes, paramCreateTableData.persistData);
/*     */     
/*  65 */     this.tableEngine = paramCreateTableData.tableEngine;
/*  66 */     this.globalTemporary = paramCreateTableData.globalTemporary;
/*  67 */     if (paramCreateTableData.tableEngineParams != null) {
/*  68 */       this.tableEngineParams = paramCreateTableData.tableEngineParams;
/*     */     } else {
/*  70 */       this.tableEngineParams = Collections.emptyList();
/*     */     } 
/*  72 */     setTemporary(paramCreateTableData.temporary);
/*  73 */     setColumns((Column[])paramCreateTableData.columns.toArray((Object[])new Column[0]));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/*  78 */     StringBuilder stringBuilder = new StringBuilder("DROP TABLE IF EXISTS ");
/*  79 */     getSQL(stringBuilder, 0).append(" CASCADE");
/*  80 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForMeta() {
/*  85 */     return getCreateSQL(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  90 */     return getCreateSQL(false);
/*     */   }
/*     */   
/*     */   private String getCreateSQL(boolean paramBoolean) {
/*  94 */     Database database = getDatabase();
/*  95 */     if (database == null)
/*     */     {
/*  97 */       return null;
/*     */     }
/*  99 */     StringBuilder stringBuilder = new StringBuilder("CREATE ");
/* 100 */     if (isTemporary()) {
/* 101 */       if (isGlobalTemporary()) {
/* 102 */         stringBuilder.append("GLOBAL ");
/*     */       } else {
/* 104 */         stringBuilder.append("LOCAL ");
/*     */       } 
/* 106 */       stringBuilder.append("TEMPORARY ");
/* 107 */     } else if (isPersistIndexes()) {
/* 108 */       stringBuilder.append("CACHED ");
/*     */     } else {
/* 110 */       stringBuilder.append("MEMORY ");
/*     */     } 
/* 112 */     stringBuilder.append("TABLE ");
/* 113 */     if (this.isHidden) {
/* 114 */       stringBuilder.append("IF NOT EXISTS ");
/*     */     }
/* 116 */     getSQL(stringBuilder, 0);
/* 117 */     if (this.comment != null) {
/* 118 */       stringBuilder.append(" COMMENT ");
/* 119 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/* 121 */     stringBuilder.append("(\n    "); byte b; int i;
/* 122 */     for (b = 0, i = this.columns.length; b < i; b++) {
/* 123 */       if (b > 0) {
/* 124 */         stringBuilder.append(",\n    ");
/*     */       }
/* 126 */       stringBuilder.append(this.columns[b].getCreateSQL(paramBoolean));
/*     */     } 
/* 128 */     stringBuilder.append("\n)");
/* 129 */     if (this.tableEngine != null) {
/* 130 */       String str = (database.getSettings()).defaultTableEngine;
/* 131 */       if (str == null || !this.tableEngine.endsWith(str)) {
/* 132 */         stringBuilder.append("\nENGINE ");
/* 133 */         StringUtils.quoteIdentifier(stringBuilder, this.tableEngine);
/*     */       } 
/*     */     } 
/* 136 */     if (!this.tableEngineParams.isEmpty()) {
/* 137 */       stringBuilder.append("\nWITH ");
/* 138 */       for (b = 0, i = this.tableEngineParams.size(); b < i; b++) {
/* 139 */         if (b > 0) {
/* 140 */           stringBuilder.append(", ");
/*     */         }
/* 142 */         StringUtils.quoteIdentifier(stringBuilder, this.tableEngineParams.get(b));
/*     */       } 
/*     */     } 
/* 145 */     if (!isPersistIndexes() && !isPersistData()) {
/* 146 */       stringBuilder.append("\nNOT PERSISTENT");
/*     */     }
/* 148 */     if (this.isHidden) {
/* 149 */       stringBuilder.append("\nHIDDEN");
/*     */     }
/* 151 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isGlobalTemporary() {
/* 156 */     return this.globalTemporary;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\TableBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */