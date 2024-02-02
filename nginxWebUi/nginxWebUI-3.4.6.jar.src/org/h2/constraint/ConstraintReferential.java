/*     */ package org.h2.constraint;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConstraintReferential
/*     */   extends Constraint
/*     */ {
/*     */   private IndexColumn[] columns;
/*     */   private IndexColumn[] refColumns;
/*  36 */   private ConstraintActionType deleteAction = ConstraintActionType.RESTRICT;
/*  37 */   private ConstraintActionType updateAction = ConstraintActionType.RESTRICT; private Table refTable;
/*     */   private Index index;
/*     */   private ConstraintUnique refConstraint;
/*     */   private boolean indexOwner;
/*     */   private String deleteSQL;
/*     */   private String updateSQL;
/*     */   private boolean skipOwnTable;
/*     */   
/*     */   public ConstraintReferential(Schema paramSchema, int paramInt, String paramString, Table paramTable) {
/*  46 */     super(paramSchema, paramInt, paramString, paramTable);
/*     */   }
/*     */ 
/*     */   
/*     */   public Constraint.Type getConstraintType() {
/*  51 */     return Constraint.Type.REFERENTIAL;
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
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  64 */     return getCreateSQLForCopy(paramTable, this.refTable, paramString, true);
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
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable1, Table paramTable2, String paramString, boolean paramBoolean) {
/*  79 */     StringBuilder stringBuilder = new StringBuilder("ALTER TABLE ");
/*  80 */     paramTable1.getSQL(stringBuilder, 0).append(" ADD CONSTRAINT ");
/*  81 */     if (paramTable1.isHidden()) {
/*  82 */       stringBuilder.append("IF NOT EXISTS ");
/*     */     }
/*  84 */     stringBuilder.append(paramString);
/*  85 */     if (this.comment != null) {
/*  86 */       stringBuilder.append(" COMMENT ");
/*  87 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/*  89 */     IndexColumn[] arrayOfIndexColumn1 = this.columns;
/*  90 */     IndexColumn[] arrayOfIndexColumn2 = this.refColumns;
/*  91 */     stringBuilder.append(" FOREIGN KEY(");
/*  92 */     IndexColumn.writeColumns(stringBuilder, arrayOfIndexColumn1, 0);
/*  93 */     stringBuilder.append(')');
/*  94 */     if (paramBoolean && this.indexOwner && paramTable1 == this.table) {
/*  95 */       stringBuilder.append(" INDEX ");
/*  96 */       this.index.getSQL(stringBuilder, 0);
/*     */     } 
/*  98 */     stringBuilder.append(" REFERENCES ");
/*  99 */     if (this.table == this.refTable) {
/*     */       
/* 101 */       paramTable1.getSQL(stringBuilder, 0);
/*     */     } else {
/* 103 */       paramTable2.getSQL(stringBuilder, 0);
/*     */     } 
/* 105 */     stringBuilder.append('(');
/* 106 */     IndexColumn.writeColumns(stringBuilder, arrayOfIndexColumn2, 0);
/* 107 */     stringBuilder.append(')');
/* 108 */     if (this.deleteAction != ConstraintActionType.RESTRICT) {
/* 109 */       stringBuilder.append(" ON DELETE ").append(this.deleteAction.getSqlName());
/*     */     }
/* 111 */     if (this.updateAction != ConstraintActionType.RESTRICT) {
/* 112 */       stringBuilder.append(" ON UPDATE ").append(this.updateAction.getSqlName());
/*     */     }
/* 114 */     return stringBuilder.append(" NOCHECK").toString();
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
/*     */   private String getShortDescription(Index paramIndex, SearchRow paramSearchRow) {
/* 127 */     StringBuilder stringBuilder = (new StringBuilder(getName())).append(": ");
/* 128 */     this.table.getSQL(stringBuilder, 3).append(" FOREIGN KEY(");
/* 129 */     IndexColumn.writeColumns(stringBuilder, this.columns, 3);
/* 130 */     stringBuilder.append(") REFERENCES ");
/* 131 */     this.refTable.getSQL(stringBuilder, 3).append('(');
/* 132 */     IndexColumn.writeColumns(stringBuilder, this.refColumns, 3);
/* 133 */     stringBuilder.append(')');
/* 134 */     if (paramIndex != null && paramSearchRow != null) {
/* 135 */       stringBuilder.append(" (");
/* 136 */       Column[] arrayOfColumn = paramIndex.getColumns();
/* 137 */       int i = Math.min(this.columns.length, arrayOfColumn.length);
/* 138 */       for (byte b = 0; b < i; b++) {
/* 139 */         int j = arrayOfColumn[b].getColumnId();
/* 140 */         Value value = paramSearchRow.getValue(j);
/* 141 */         if (b > 0) {
/* 142 */           stringBuilder.append(", ");
/*     */         }
/* 144 */         stringBuilder.append((value == null) ? "" : value.toString());
/*     */       } 
/* 146 */       stringBuilder.append(')');
/*     */     } 
/* 148 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLWithoutIndexes() {
/* 153 */     return getCreateSQLForCopy(this.table, this.refTable, getSQL(0), false);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 158 */     return getCreateSQLForCopy(this.table, getSQL(0));
/*     */   }
/*     */   
/*     */   public void setColumns(IndexColumn[] paramArrayOfIndexColumn) {
/* 162 */     this.columns = paramArrayOfIndexColumn;
/*     */   }
/*     */   
/*     */   public IndexColumn[] getColumns() {
/* 166 */     return this.columns;
/*     */   }
/*     */ 
/*     */   
/*     */   public HashSet<Column> getReferencedColumns(Table paramTable) {
/* 171 */     HashSet<Column> hashSet = new HashSet();
/* 172 */     if (paramTable == this.table) {
/* 173 */       for (IndexColumn indexColumn : this.columns) {
/* 174 */         hashSet.add(indexColumn.column);
/*     */       }
/* 176 */     } else if (paramTable == this.refTable) {
/* 177 */       for (IndexColumn indexColumn : this.refColumns) {
/* 178 */         hashSet.add(indexColumn.column);
/*     */       }
/*     */     } 
/* 181 */     return hashSet;
/*     */   }
/*     */   
/*     */   public void setRefColumns(IndexColumn[] paramArrayOfIndexColumn) {
/* 185 */     this.refColumns = paramArrayOfIndexColumn;
/*     */   }
/*     */   
/*     */   public IndexColumn[] getRefColumns() {
/* 189 */     return this.refColumns;
/*     */   }
/*     */   
/*     */   public void setRefTable(Table paramTable) {
/* 193 */     this.refTable = paramTable;
/* 194 */     if (paramTable.isTemporary()) {
/* 195 */       setTemporary(true);
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
/*     */   public void setIndex(Index paramIndex, boolean paramBoolean) {
/* 207 */     this.index = paramIndex;
/* 208 */     this.indexOwner = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRefConstraint(ConstraintUnique paramConstraintUnique) {
/* 219 */     this.refConstraint = paramConstraintUnique;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 224 */     this.table.removeConstraint(this);
/* 225 */     this.refTable.removeConstraint(this);
/* 226 */     if (this.indexOwner) {
/* 227 */       this.table.removeIndexOrTransferOwnership(paramSessionLocal, this.index);
/*     */     }
/* 229 */     this.database.removeMeta(paramSessionLocal, getId());
/* 230 */     this.refTable = null;
/* 231 */     this.index = null;
/* 232 */     this.refConstraint = null;
/* 233 */     this.columns = null;
/* 234 */     this.refColumns = null;
/* 235 */     this.deleteSQL = null;
/* 236 */     this.updateSQL = null;
/* 237 */     this.table = null;
/* 238 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkRow(SessionLocal paramSessionLocal, Table paramTable, Row paramRow1, Row paramRow2) {
/* 243 */     if (!this.database.getReferentialIntegrity()) {
/*     */       return;
/*     */     }
/* 246 */     if (!this.table.getCheckForeignKeyConstraints() || 
/* 247 */       !this.refTable.getCheckForeignKeyConstraints()) {
/*     */       return;
/*     */     }
/* 250 */     if (paramTable == this.table && 
/* 251 */       !this.skipOwnTable) {
/* 252 */       checkRowOwnTable(paramSessionLocal, paramRow1, paramRow2);
/*     */     }
/*     */     
/* 255 */     if (paramTable == this.refTable) {
/* 256 */       checkRowRefTable(paramSessionLocal, paramRow1, paramRow2);
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkRowOwnTable(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/* 261 */     if (paramRow2 == null) {
/*     */       return;
/*     */     }
/* 264 */     boolean bool = (paramRow1 != null) ? true : false;
/* 265 */     for (IndexColumn indexColumn : this.columns) {
/* 266 */       int j = indexColumn.column.getColumnId();
/* 267 */       Value value = paramRow2.getValue(j);
/* 268 */       if (value == ValueNull.INSTANCE) {
/*     */         return;
/*     */       }
/*     */       
/* 272 */       if (bool && 
/* 273 */         !paramSessionLocal.areEqual(value, paramRow1.getValue(j))) {
/* 274 */         bool = false;
/*     */       }
/*     */     } 
/*     */     
/* 278 */     if (bool) {
/*     */       return;
/*     */     }
/*     */     
/* 282 */     if (this.refTable == this.table) {
/*     */ 
/*     */       
/* 285 */       boolean bool1 = true; byte b1; int j;
/* 286 */       for (b1 = 0, j = this.columns.length; b1 < j; b1++) {
/* 287 */         int k = (this.columns[b1]).column.getColumnId();
/* 288 */         Value value1 = paramRow2.getValue(k);
/* 289 */         Column column = (this.refColumns[b1]).column;
/* 290 */         int m = column.getColumnId();
/* 291 */         Value value2 = paramRow2.getValue(m);
/* 292 */         if (!paramSessionLocal.areEqual(value2, value1)) {
/* 293 */           bool1 = false;
/*     */           break;
/*     */         } 
/*     */       } 
/* 297 */       if (bool1) {
/*     */         return;
/*     */       }
/*     */     } 
/* 301 */     Row row = this.refTable.getTemplateRow(); byte b; int i;
/* 302 */     for (b = 0, i = this.columns.length; b < i; b++) {
/* 303 */       int j = (this.columns[b]).column.getColumnId();
/* 304 */       Value value = paramRow2.getValue(j);
/* 305 */       Column column = (this.refColumns[b]).column;
/* 306 */       int k = column.getColumnId();
/* 307 */       row.setValue(k, column.convert((CastDataProvider)paramSessionLocal, value));
/*     */     } 
/* 309 */     Index index = this.refConstraint.getIndex();
/* 310 */     if (!existsRow(paramSessionLocal, index, (SearchRow)row, (Row)null)) {
/* 311 */       throw DbException.get(23506, 
/* 312 */           getShortDescription(index, row));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean existsRow(SessionLocal paramSessionLocal, Index paramIndex, SearchRow paramSearchRow, Row paramRow) {
/* 318 */     Table table = paramIndex.getTable();
/* 319 */     table.lock(paramSessionLocal, 0);
/* 320 */     Cursor cursor = paramIndex.find(paramSessionLocal, paramSearchRow, paramSearchRow);
/* 321 */     while (cursor.next()) {
/*     */       
/* 323 */       SearchRow searchRow = cursor.getSearchRow();
/* 324 */       if (paramRow != null && searchRow.getKey() == paramRow.getKey()) {
/*     */         continue;
/*     */       }
/* 327 */       Column[] arrayOfColumn = paramIndex.getColumns();
/* 328 */       boolean bool = true;
/* 329 */       int i = Math.min(this.columns.length, arrayOfColumn.length);
/* 330 */       for (byte b = 0; b < i; b++) {
/* 331 */         int j = arrayOfColumn[b].getColumnId();
/* 332 */         Value value1 = paramSearchRow.getValue(j);
/* 333 */         Value value2 = searchRow.getValue(j);
/* 334 */         if (table.compareValues((CastDataProvider)paramSessionLocal, value1, value2) != 0) {
/* 335 */           bool = false;
/*     */           break;
/*     */         } 
/*     */       } 
/* 339 */       if (bool) {
/* 340 */         return true;
/*     */       }
/*     */     } 
/* 343 */     return false;
/*     */   }
/*     */   
/*     */   private boolean isEqual(Row paramRow1, Row paramRow2) {
/* 347 */     return (this.refConstraint.getIndex().compareRows((SearchRow)paramRow1, (SearchRow)paramRow2) == 0);
/*     */   }
/*     */   
/*     */   private void checkRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 351 */     SearchRow searchRow = this.table.getRowFactory().createRow(); byte b; int i;
/* 352 */     for (b = 0, i = this.columns.length; b < i; b++) {
/* 353 */       Column column1 = (this.refColumns[b]).column;
/* 354 */       int j = column1.getColumnId();
/* 355 */       Column column2 = (this.columns[b]).column;
/* 356 */       Value value = column2.convert((CastDataProvider)paramSessionLocal, paramRow.getValue(j));
/* 357 */       if (value == ValueNull.INSTANCE) {
/*     */         return;
/*     */       }
/* 360 */       searchRow.setValue(column2.getColumnId(), value);
/*     */     } 
/*     */     
/* 363 */     Row row = (this.refTable == this.table) ? paramRow : null;
/* 364 */     if (existsRow(paramSessionLocal, this.index, searchRow, row)) {
/* 365 */       throw DbException.get(23503, 
/* 366 */           getShortDescription(this.index, searchRow));
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkRowRefTable(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/* 371 */     if (paramRow1 == null) {
/*     */       return;
/*     */     }
/*     */     
/* 375 */     if (paramRow2 != null && isEqual(paramRow1, paramRow2)) {
/*     */       return;
/*     */     }
/*     */     
/* 379 */     if (paramRow2 == null) {
/*     */       
/* 381 */       if (this.deleteAction == ConstraintActionType.RESTRICT) {
/* 382 */         checkRow(paramSessionLocal, paramRow1);
/*     */       } else {
/* 384 */         boolean bool = (this.deleteAction == ConstraintActionType.CASCADE) ? false : this.columns.length;
/* 385 */         Prepared prepared = getDelete(paramSessionLocal);
/* 386 */         setWhere(prepared, bool, paramRow1);
/* 387 */         updateWithSkipCheck(prepared);
/*     */       }
/*     */     
/*     */     }
/* 391 */     else if (this.updateAction == ConstraintActionType.RESTRICT) {
/* 392 */       checkRow(paramSessionLocal, paramRow1);
/*     */     } else {
/* 394 */       Prepared prepared = getUpdate(paramSessionLocal);
/* 395 */       if (this.updateAction == ConstraintActionType.CASCADE) {
/* 396 */         ArrayList<Parameter> arrayList = prepared.getParameters(); byte b; int i;
/* 397 */         for (b = 0, i = this.columns.length; b < i; b++) {
/* 398 */           Parameter parameter = arrayList.get(b);
/* 399 */           Column column = (this.refColumns[b]).column;
/* 400 */           parameter.setValue(paramRow2.getValue(column.getColumnId()));
/*     */         } 
/*     */       } 
/* 403 */       setWhere(prepared, this.columns.length, paramRow1);
/* 404 */       updateWithSkipCheck(prepared);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateWithSkipCheck(Prepared paramPrepared) {
/*     */     try {
/* 415 */       this.skipOwnTable = true;
/* 416 */       paramPrepared.update();
/*     */     } finally {
/* 418 */       this.skipOwnTable = false;
/*     */     } 
/*     */   } private void setWhere(Prepared paramPrepared, int paramInt, Row paramRow) {
/*     */     byte b;
/*     */     int i;
/* 423 */     for (b = 0, i = this.refColumns.length; b < i; b++) {
/* 424 */       int j = (this.refColumns[b]).column.getColumnId();
/* 425 */       Value value = paramRow.getValue(j);
/* 426 */       ArrayList<Parameter> arrayList = paramPrepared.getParameters();
/* 427 */       Parameter parameter = arrayList.get(paramInt + b);
/* 428 */       parameter.setValue(value);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ConstraintActionType getDeleteAction() {
/* 433 */     return this.deleteAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDeleteAction(ConstraintActionType paramConstraintActionType) {
/* 442 */     if (paramConstraintActionType == this.deleteAction && this.deleteSQL == null) {
/*     */       return;
/*     */     }
/* 445 */     if (this.deleteAction != ConstraintActionType.RESTRICT) {
/* 446 */       throw DbException.get(90045, "ON DELETE");
/*     */     }
/* 448 */     this.deleteAction = paramConstraintActionType;
/* 449 */     buildDeleteSQL();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateOnTableColumnRename() {
/* 456 */     if (this.deleteAction != null) {
/* 457 */       this.deleteSQL = null;
/* 458 */       buildDeleteSQL();
/*     */     } 
/* 460 */     if (this.updateAction != null) {
/* 461 */       this.updateSQL = null;
/* 462 */       buildUpdateSQL();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void buildDeleteSQL() {
/* 467 */     if (this.deleteAction == ConstraintActionType.RESTRICT) {
/*     */       return;
/*     */     }
/* 470 */     StringBuilder stringBuilder = new StringBuilder();
/* 471 */     if (this.deleteAction == ConstraintActionType.CASCADE) {
/* 472 */       stringBuilder.append("DELETE FROM ");
/* 473 */       this.table.getSQL(stringBuilder, 0);
/*     */     } else {
/* 475 */       appendUpdate(stringBuilder);
/*     */     } 
/* 477 */     appendWhere(stringBuilder);
/* 478 */     this.deleteSQL = stringBuilder.toString();
/*     */   }
/*     */   
/*     */   private Prepared getUpdate(SessionLocal paramSessionLocal) {
/* 482 */     return prepare(paramSessionLocal, this.updateSQL, this.updateAction);
/*     */   }
/*     */   
/*     */   private Prepared getDelete(SessionLocal paramSessionLocal) {
/* 486 */     return prepare(paramSessionLocal, this.deleteSQL, this.deleteAction);
/*     */   }
/*     */   
/*     */   public ConstraintActionType getUpdateAction() {
/* 490 */     return this.updateAction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUpdateAction(ConstraintActionType paramConstraintActionType) {
/* 499 */     if (paramConstraintActionType == this.updateAction && this.updateSQL == null) {
/*     */       return;
/*     */     }
/* 502 */     if (this.updateAction != ConstraintActionType.RESTRICT) {
/* 503 */       throw DbException.get(90045, "ON UPDATE");
/*     */     }
/* 505 */     this.updateAction = paramConstraintActionType;
/* 506 */     buildUpdateSQL();
/*     */   }
/*     */   
/*     */   private void buildUpdateSQL() {
/* 510 */     if (this.updateAction == ConstraintActionType.RESTRICT) {
/*     */       return;
/*     */     }
/* 513 */     StringBuilder stringBuilder = new StringBuilder();
/* 514 */     appendUpdate(stringBuilder);
/* 515 */     appendWhere(stringBuilder);
/* 516 */     this.updateSQL = stringBuilder.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void rebuild() {
/* 521 */     buildUpdateSQL();
/* 522 */     buildDeleteSQL();
/*     */   }
/*     */   
/*     */   private Prepared prepare(SessionLocal paramSessionLocal, String paramString, ConstraintActionType paramConstraintActionType) {
/* 526 */     Prepared prepared = paramSessionLocal.prepare(paramString);
/* 527 */     if (paramConstraintActionType != ConstraintActionType.CASCADE) {
/* 528 */       ArrayList<Parameter> arrayList = prepared.getParameters(); byte b; int i;
/* 529 */       for (b = 0, i = this.columns.length; b < i; b++) {
/* 530 */         Value value; Column column = (this.columns[b]).column;
/* 531 */         Parameter parameter = arrayList.get(b);
/*     */         
/* 533 */         if (paramConstraintActionType == ConstraintActionType.SET_NULL) {
/* 534 */           ValueNull valueNull = ValueNull.INSTANCE;
/*     */         } else {
/* 536 */           Expression expression = column.getEffectiveDefaultExpression();
/* 537 */           if (expression == null) {
/* 538 */             throw DbException.get(23507, column.getName());
/*     */           }
/* 540 */           value = expression.getValue(paramSessionLocal);
/*     */         } 
/* 542 */         parameter.setValue(value);
/*     */       } 
/*     */     } 
/* 545 */     return prepared;
/*     */   }
/*     */   
/*     */   private void appendUpdate(StringBuilder paramStringBuilder) {
/* 549 */     paramStringBuilder.append("UPDATE ");
/* 550 */     this.table.getSQL(paramStringBuilder, 0).append(" SET ");
/* 551 */     IndexColumn.writeColumns(paramStringBuilder, this.columns, ", ", "=?", -2147483648);
/*     */   }
/*     */   
/*     */   private void appendWhere(StringBuilder paramStringBuilder) {
/* 555 */     paramStringBuilder.append(" WHERE ");
/* 556 */     IndexColumn.writeColumns(paramStringBuilder, this.columns, " AND ", "=?", -2147483648);
/*     */   }
/*     */ 
/*     */   
/*     */   public Table getRefTable() {
/* 561 */     return this.refTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean usesIndex(Index paramIndex) {
/* 566 */     return (paramIndex == this.index);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIndexOwner(Index paramIndex) {
/* 571 */     if (this.index == paramIndex) {
/* 572 */       this.indexOwner = true;
/*     */     } else {
/* 574 */       throw DbException.getInternalError(paramIndex + " " + toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBefore() {
/* 580 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkExistingData(SessionLocal paramSessionLocal) {
/* 585 */     if (paramSessionLocal.getDatabase().isStarting()) {
/*     */       return;
/*     */     }
/*     */     
/* 589 */     StringBuilder stringBuilder = new StringBuilder("SELECT 1 FROM (SELECT ");
/* 590 */     IndexColumn.writeColumns(stringBuilder, this.columns, -2147483648);
/* 591 */     stringBuilder.append(" FROM ");
/* 592 */     this.table.getSQL(stringBuilder, 0).append(" WHERE ");
/* 593 */     IndexColumn.writeColumns(stringBuilder, this.columns, " AND ", " IS NOT NULL ", -2147483648);
/* 594 */     stringBuilder.append(" ORDER BY ");
/* 595 */     IndexColumn.writeColumns(stringBuilder, this.columns, 0);
/* 596 */     stringBuilder.append(") C WHERE NOT EXISTS(SELECT 1 FROM ");
/* 597 */     this.refTable.getSQL(stringBuilder, 0).append(" P WHERE "); byte b; int i;
/* 598 */     for (b = 0, i = this.columns.length; b < i; b++) {
/* 599 */       if (b > 0) {
/* 600 */         stringBuilder.append(" AND ");
/*     */       }
/* 602 */       stringBuilder.append("C.");
/* 603 */       (this.columns[b]).column.getSQL(stringBuilder, 0).append('=').append("P.");
/* 604 */       (this.refColumns[b]).column.getSQL(stringBuilder, 0);
/*     */     } 
/* 606 */     stringBuilder.append(')');
/*     */     
/* 608 */     paramSessionLocal.startStatementWithinTransaction(null);
/*     */     try {
/* 610 */       ResultInterface resultInterface = paramSessionLocal.prepare(stringBuilder.toString()).query(1L);
/* 611 */       if (resultInterface.next()) {
/* 612 */         throw DbException.get(23506, 
/* 613 */             getShortDescription(null, null));
/*     */       }
/*     */     } finally {
/* 616 */       paramSessionLocal.endStatement();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Index getIndex() {
/* 622 */     return this.index;
/*     */   }
/*     */ 
/*     */   
/*     */   public ConstraintUnique getReferencedConstraint() {
/* 627 */     return this.refConstraint;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\constraint\ConstraintReferential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */