/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.constraint.ConstraintActionType;
/*     */ import org.h2.constraint.ConstraintCheck;
/*     */ import org.h2.constraint.ConstraintReferential;
/*     */ import org.h2.constraint.ConstraintUnique;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.DataType;
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
/*     */ public class AlterTableAddConstraint
/*     */   extends AlterTable
/*     */ {
/*     */   private final int type;
/*     */   private String constraintName;
/*     */   private IndexColumn[] indexColumns;
/*  42 */   private ConstraintActionType deleteAction = ConstraintActionType.RESTRICT;
/*  43 */   private ConstraintActionType updateAction = ConstraintActionType.RESTRICT; private Schema refSchema;
/*     */   private String refTableName;
/*     */   private IndexColumn[] refIndexColumns;
/*     */   private Expression checkExpression;
/*     */   private Index index;
/*     */   private Index refIndex;
/*     */   private String comment;
/*     */   private boolean checkExisting;
/*     */   private boolean primaryKeyHash;
/*     */   private final boolean ifNotExists;
/*  53 */   private final ArrayList<Index> createdIndexes = new ArrayList<>();
/*     */   private ConstraintUnique createdUniqueConstraint;
/*     */   
/*     */   public AlterTableAddConstraint(SessionLocal paramSessionLocal, Schema paramSchema, int paramInt, boolean paramBoolean) {
/*  57 */     super(paramSessionLocal, paramSchema);
/*  58 */     this.ifNotExists = paramBoolean;
/*  59 */     this.type = paramInt;
/*     */   }
/*     */   
/*     */   private String generateConstraintName(Table paramTable) {
/*  63 */     if (this.constraintName == null) {
/*  64 */       this.constraintName = getSchema().getUniqueConstraintName(this.session, paramTable);
/*     */     }
/*  66 */     return this.constraintName;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update(Table paramTable) {
/*     */     try {
/*  72 */       return tryUpdate(paramTable);
/*  73 */     } catch (DbException dbException) {
/*     */       try {
/*  75 */         if (this.createdUniqueConstraint != null) {
/*  76 */           Index index = this.createdUniqueConstraint.getIndex();
/*  77 */           this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)this.createdUniqueConstraint);
/*  78 */           this.createdIndexes.remove(index);
/*     */         } 
/*  80 */         for (Index index : this.createdIndexes) {
/*  81 */           this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)index);
/*     */         }
/*  83 */       } catch (Throwable throwable) {
/*  84 */         dbException.addSuppressed(throwable);
/*     */       } 
/*  86 */       throw dbException;
/*     */     } finally {
/*  88 */       getSchema().freeUniqueName(this.constraintName);
/*     */     }  } private int tryUpdate(Table paramTable) { ConstraintUnique constraintUnique1; ConstraintCheck constraintCheck1; ConstraintReferential constraintReferential1; ArrayList<Constraint> arrayList; int i; Table table; int j; String str1; boolean bool; String str2; ConstraintCheck constraintCheck2;
/*     */     int k;
/*     */     ConstraintUnique constraintUnique3;
/*     */     TableFilter tableFilter;
/*     */     byte b;
/*     */     ConstraintUnique constraintUnique2;
/*     */     int m;
/*     */     String str3;
/*     */     ConstraintReferential constraintReferential2;
/*  98 */     if (this.constraintName != null && getSchema().findConstraint(this.session, this.constraintName) != null) {
/*  99 */       if (this.ifNotExists) {
/* 100 */         return 0;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 108 */       if (!this.session.isQuirksMode()) {
/* 109 */         throw DbException.get(90045, this.constraintName);
/*     */       }
/* 111 */       this.constraintName = null;
/*     */     } 
/* 113 */     Database database = this.session.getDatabase();
/* 114 */     database.lockMeta(this.session);
/* 115 */     paramTable.lock(this.session, 2);
/*     */     
/* 117 */     switch (this.type) {
/*     */       case 6:
/* 119 */         IndexColumn.mapColumns(this.indexColumns, paramTable);
/* 120 */         this.index = paramTable.findPrimaryKey();
/* 121 */         arrayList = paramTable.getConstraints();
/* 122 */         for (j = 0; arrayList != null && j < arrayList.size(); j++) {
/* 123 */           Constraint constraint = arrayList.get(j);
/* 124 */           if (Constraint.Type.PRIMARY_KEY == constraint.getConstraintType()) {
/* 125 */             throw DbException.get(90017);
/*     */           }
/*     */         } 
/* 128 */         if (this.index != null) {
/*     */ 
/*     */           
/* 131 */           IndexColumn[] arrayOfIndexColumn = this.index.getIndexColumns();
/* 132 */           if (arrayOfIndexColumn.length != this.indexColumns.length) {
/* 133 */             throw DbException.get(90017);
/*     */           }
/* 135 */           for (byte b1 = 0; b1 < arrayOfIndexColumn.length; b1++) {
/* 136 */             if ((arrayOfIndexColumn[b1]).column != (this.indexColumns[b1]).column) {
/* 137 */               throw DbException.get(90017);
/*     */             }
/*     */           } 
/*     */         } else {
/* 141 */           IndexType indexType = IndexType.createPrimaryKey(paramTable
/* 142 */               .isPersistIndexes(), this.primaryKeyHash);
/* 143 */           String str = paramTable.getSchema().getUniqueIndexName(this.session, paramTable, "PRIMARY_KEY_");
/*     */           
/* 145 */           int n = this.session.getDatabase().allocateObjectId();
/*     */           try {
/* 147 */             this.index = paramTable.addIndex(this.session, str, n, this.indexColumns, this.indexColumns.length, indexType, true, null);
/*     */           } finally {
/*     */             
/* 150 */             getSchema().freeUniqueName(str);
/*     */           } 
/*     */         } 
/* 153 */         this.index.getIndexType().setBelongsToConstraint(true);
/* 154 */         j = getObjectId();
/* 155 */         str2 = generateConstraintName(paramTable);
/* 156 */         constraintUnique3 = new ConstraintUnique(getSchema(), j, str2, paramTable, true);
/*     */         
/* 158 */         constraintUnique3.setColumns(this.indexColumns);
/* 159 */         constraintUnique3.setIndex(this.index, true);
/* 160 */         constraintUnique1 = constraintUnique3;
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
/* 299 */         constraintUnique1.setComment(this.comment);
/* 300 */         addConstraintToTable(database, paramTable, (Constraint)constraintUnique1);
/* 301 */         return 0;case 4: if (this.indexColumns == null) { Column[] arrayOfColumn = paramTable.getColumns(); j = arrayOfColumn.length; ArrayList<IndexColumn> arrayList1 = new ArrayList(j); for (byte b1 = 0; b1 < j; b1++) { Column column = arrayOfColumn[b1]; if (column.getVisible()) { IndexColumn indexColumn = new IndexColumn(column.getName()); indexColumn.column = column; arrayList1.add(indexColumn); }  }  if (arrayList1.isEmpty()) throw DbException.get(42000, "UNIQUE(VALUE) on table without columns");  this.indexColumns = arrayList1.<IndexColumn>toArray(new IndexColumn[0]); } else { IndexColumn.mapColumns(this.indexColumns, paramTable); }  constraintUnique1 = createUniqueConstraint(paramTable, this.index, this.indexColumns, false); constraintUnique1.setComment(this.comment); addConstraintToTable(database, paramTable, (Constraint)constraintUnique1); return 0;case 3: i = getObjectId(); str1 = generateConstraintName(paramTable); constraintCheck2 = new ConstraintCheck(getSchema(), i, str1, paramTable); tableFilter = new TableFilter(this.session, paramTable, null, false, null, 0, null); this.checkExpression.mapColumns((ColumnResolver)tableFilter, 0, 0); this.checkExpression = this.checkExpression.optimize(this.session); constraintCheck2.setExpression(this.checkExpression); constraintCheck2.setTableFilter(tableFilter); constraintCheck1 = constraintCheck2; if (this.checkExisting) constraintCheck2.checkExistingData(this.session);  constraintCheck1.setComment(this.comment); addConstraintToTable(database, paramTable, (Constraint)constraintCheck1); return 0;case 5: table = this.refSchema.resolveTableOrView(this.session, this.refTableName); if (table == null) throw DbException.get(42102, this.refTableName);  if (table != paramTable) this.session.getUser().checkTableRight(table, 32);  if (!table.canReference()) { StringBuilder stringBuilder = new StringBuilder("Reference "); table.getSQL(stringBuilder, 3); throw DbException.getUnsupportedException(stringBuilder.toString()); }  bool = false; IndexColumn.mapColumns(this.indexColumns, paramTable); if (this.refIndexColumns == null) { this.refIndexColumns = table.getPrimaryKey().getIndexColumns(); } else { IndexColumn.mapColumns(this.refIndexColumns, table); }  k = this.indexColumns.length; if (this.refIndexColumns.length != k) throw DbException.get(21002);  for (IndexColumn indexColumn : this.indexColumns) { Column column = indexColumn.column; if (column.isGeneratedAlways()) { switch (this.deleteAction) { case SET_DEFAULT: case SET_NULL: throw DbException.get(90155, new String[] { column.getSQLWithTable(new StringBuilder(), 3).toString(), "ON DELETE " + this.deleteAction.getSqlName() }); }  switch (this.updateAction) { case SET_DEFAULT: case SET_NULL: case CASCADE: throw DbException.get(90155, new String[] { column.getSQLWithTable(new StringBuilder(), 3).toString(), "ON UPDATE " + this.updateAction.getSqlName() }); }  }  }  for (b = 0; b < k; b++) { Column column1 = (this.indexColumns[b]).column, column2 = (this.refIndexColumns[b]).column; if (!DataType.areStableComparable(column1.getType(), column2.getType())) throw DbException.get(90153, new String[] { column1.getCreateSQL(), column2.getCreateSQL() });  }  constraintUnique2 = getUniqueConstraint(table, this.refIndexColumns); if (constraintUnique2 == null && !this.session.isQuirksMode() && !(this.session.getMode()).createUniqueConstraintForReferencedColumns) throw DbException.get(90057, IndexColumn.writeColumns(new StringBuilder("PRIMARY KEY | UNIQUE ("), this.refIndexColumns, 3).append(')').toString());  if (this.index != null && canUseIndex(this.index, paramTable, this.indexColumns, false)) { bool = true; this.index.getIndexType().setBelongsToConstraint(true); } else { this.index = getIndex(paramTable, this.indexColumns, false); if (this.index == null) { this.index = createIndex(paramTable, this.indexColumns, false); bool = true; }  }  m = getObjectId(); str3 = generateConstraintName(paramTable); constraintReferential2 = new ConstraintReferential(getSchema(), m, str3, paramTable); constraintReferential2.setColumns(this.indexColumns); constraintReferential2.setIndex(this.index, bool); constraintReferential2.setRefTable(table); constraintReferential2.setRefColumns(this.refIndexColumns); if (constraintUnique2 == null) { constraintUnique2 = createUniqueConstraint(table, this.refIndex, this.refIndexColumns, true); addConstraintToTable(database, table, (Constraint)constraintUnique2); this.createdUniqueConstraint = constraintUnique2; }  constraintReferential2.setRefConstraint(constraintUnique2); if (this.checkExisting) constraintReferential2.checkExistingData(this.session);  table.addConstraint((Constraint)constraintReferential2); constraintReferential2.setDeleteAction(this.deleteAction); constraintReferential2.setUpdateAction(this.updateAction); constraintReferential1 = constraintReferential2; constraintReferential1.setComment(this.comment); addConstraintToTable(database, paramTable, (Constraint)constraintReferential1); return 0;
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + this.type); } private ConstraintUnique createUniqueConstraint(Table paramTable, Index paramIndex, IndexColumn[] paramArrayOfIndexColumn, boolean paramBoolean) {
/*     */     int i;
/*     */     String str;
/* 306 */     boolean bool = false;
/* 307 */     if (paramIndex != null && canUseIndex(paramIndex, paramTable, paramArrayOfIndexColumn, true)) {
/* 308 */       bool = true;
/* 309 */       paramIndex.getIndexType().setBelongsToConstraint(true);
/*     */     } else {
/* 311 */       paramIndex = getIndex(paramTable, paramArrayOfIndexColumn, true);
/* 312 */       if (paramIndex == null) {
/* 313 */         paramIndex = createIndex(paramTable, paramArrayOfIndexColumn, true);
/* 314 */         bool = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 319 */     Schema schema = paramTable.getSchema();
/* 320 */     if (paramBoolean) {
/* 321 */       i = this.session.getDatabase().allocateObjectId();
/*     */       try {
/* 323 */         schema.reserveUniqueName(this.constraintName);
/* 324 */         str = schema.getUniqueConstraintName(this.session, paramTable);
/*     */       } finally {
/* 326 */         schema.freeUniqueName(this.constraintName);
/*     */       } 
/*     */     } else {
/* 329 */       i = getObjectId();
/* 330 */       str = generateConstraintName(paramTable);
/*     */     } 
/* 332 */     ConstraintUnique constraintUnique = new ConstraintUnique(schema, i, str, paramTable, false);
/* 333 */     constraintUnique.setColumns(paramArrayOfIndexColumn);
/* 334 */     constraintUnique.setIndex(paramIndex, bool);
/* 335 */     return constraintUnique;
/*     */   }
/*     */   
/*     */   private void addConstraintToTable(Database paramDatabase, Table paramTable, Constraint paramConstraint) {
/* 339 */     if (paramTable.isTemporary() && !paramTable.isGlobalTemporary()) {
/* 340 */       this.session.addLocalTempTableConstraint(paramConstraint);
/*     */     } else {
/* 342 */       paramDatabase.addSchemaObject(this.session, (SchemaObject)paramConstraint);
/*     */     } 
/* 344 */     paramTable.addConstraint(paramConstraint);
/*     */   }
/*     */   private Index createIndex(Table paramTable, IndexColumn[] paramArrayOfIndexColumn, boolean paramBoolean) {
/*     */     IndexType indexType;
/* 348 */     int i = this.session.getDatabase().allocateObjectId();
/*     */     
/* 350 */     if (paramBoolean) {
/*     */       
/* 352 */       indexType = IndexType.createUnique(paramTable.isPersistIndexes(), false);
/*     */     } else {
/*     */       
/* 355 */       indexType = IndexType.createNonUnique(paramTable.isPersistIndexes());
/*     */     } 
/* 357 */     indexType.setBelongsToConstraint(true);
/* 358 */     String str1 = (this.constraintName == null) ? "CONSTRAINT" : this.constraintName;
/* 359 */     String str2 = paramTable.getSchema().getUniqueIndexName(this.session, paramTable, str1 + "_INDEX_");
/*     */     
/*     */     try {
/* 362 */       Index index = paramTable.addIndex(this.session, str2, i, paramArrayOfIndexColumn, paramBoolean ? paramArrayOfIndexColumn.length : 0, indexType, true, null);
/*     */       
/* 364 */       this.createdIndexes.add(index);
/* 365 */       return index;
/*     */     } finally {
/* 367 */       getSchema().freeUniqueName(str2);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setDeleteAction(ConstraintActionType paramConstraintActionType) {
/* 372 */     this.deleteAction = paramConstraintActionType;
/*     */   }
/*     */   
/*     */   public void setUpdateAction(ConstraintActionType paramConstraintActionType) {
/* 376 */     this.updateAction = paramConstraintActionType;
/*     */   }
/*     */   
/*     */   private static ConstraintUnique getUniqueConstraint(Table paramTable, IndexColumn[] paramArrayOfIndexColumn) {
/* 380 */     ArrayList arrayList = paramTable.getConstraints();
/* 381 */     if (arrayList != null) {
/* 382 */       for (Constraint constraint : arrayList) {
/* 383 */         if (constraint.getTable() == paramTable) {
/* 384 */           Constraint.Type type = constraint.getConstraintType();
/* 385 */           if ((type == Constraint.Type.PRIMARY_KEY || type == Constraint.Type.UNIQUE) && 
/* 386 */             canUseIndex(constraint.getIndex(), paramTable, paramArrayOfIndexColumn, true)) {
/* 387 */             return (ConstraintUnique)constraint;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 393 */     return null;
/*     */   }
/*     */   
/*     */   private static Index getIndex(Table paramTable, IndexColumn[] paramArrayOfIndexColumn, boolean paramBoolean) {
/* 397 */     ArrayList arrayList = paramTable.getIndexes();
/* 398 */     Index index = null;
/* 399 */     if (arrayList != null) {
/* 400 */       for (Index index1 : arrayList) {
/* 401 */         if (canUseIndex(index1, paramTable, paramArrayOfIndexColumn, paramBoolean) && (
/* 402 */           index == null || (index1.getIndexColumns()).length < (index.getIndexColumns()).length)) {
/* 403 */           index = index1;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 408 */     return index;
/*     */   }
/*     */   private static boolean canUseIndex(Index paramIndex, Table paramTable, IndexColumn[] paramArrayOfIndexColumn, boolean paramBoolean) {
/*     */     int i;
/* 412 */     if (paramIndex.getTable() != paramTable) {
/* 413 */       return false;
/*     */     }
/*     */     
/* 416 */     if (paramBoolean) {
/* 417 */       i = paramIndex.getUniqueColumnCount();
/* 418 */       if (i != paramArrayOfIndexColumn.length) {
/* 419 */         return false;
/*     */       }
/*     */     }
/* 422 */     else if (paramIndex.getCreateSQL() == null || (i = (paramIndex.getColumns()).length) != paramArrayOfIndexColumn.length) {
/* 423 */       return false;
/*     */     } 
/*     */     
/* 426 */     for (IndexColumn indexColumn : paramArrayOfIndexColumn) {
/*     */       
/* 428 */       int j = paramIndex.getColumnIndex(indexColumn.column);
/* 429 */       if (j < 0 || j >= i) {
/* 430 */         return false;
/*     */       }
/*     */     } 
/* 433 */     return true;
/*     */   }
/*     */   
/*     */   public void setConstraintName(String paramString) {
/* 437 */     this.constraintName = paramString;
/*     */   }
/*     */   
/*     */   public String getConstraintName() {
/* 441 */     return this.constraintName;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 446 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setCheckExpression(Expression paramExpression) {
/* 450 */     this.checkExpression = paramExpression;
/*     */   }
/*     */   
/*     */   public void setIndexColumns(IndexColumn[] paramArrayOfIndexColumn) {
/* 454 */     this.indexColumns = paramArrayOfIndexColumn;
/*     */   }
/*     */   
/*     */   public IndexColumn[] getIndexColumns() {
/* 458 */     return this.indexColumns;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRefTableName(Schema paramSchema, String paramString) {
/* 468 */     this.refSchema = paramSchema;
/* 469 */     this.refTableName = paramString;
/*     */   }
/*     */   
/*     */   public void setRefIndexColumns(IndexColumn[] paramArrayOfIndexColumn) {
/* 473 */     this.refIndexColumns = paramArrayOfIndexColumn;
/*     */   }
/*     */   
/*     */   public void setIndex(Index paramIndex) {
/* 477 */     this.index = paramIndex;
/*     */   }
/*     */   
/*     */   public void setRefIndex(Index paramIndex) {
/* 481 */     this.refIndex = paramIndex;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/* 485 */     this.comment = paramString;
/*     */   }
/*     */   
/*     */   public void setCheckExisting(boolean paramBoolean) {
/* 489 */     this.checkExisting = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setPrimaryKeyHash(boolean paramBoolean) {
/* 493 */     this.primaryKeyHash = paramBoolean;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterTableAddConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */