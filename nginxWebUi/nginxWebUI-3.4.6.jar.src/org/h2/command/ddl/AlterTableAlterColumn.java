/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.CommandContainer;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.constraint.ConstraintReferential;
/*     */ import org.h2.constraint.ConstraintUnique;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableBase;
/*     */ import org.h2.table.TableView;
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
/*     */ public class AlterTableAlterColumn
/*     */   extends CommandWithColumns
/*     */ {
/*     */   private String tableName;
/*     */   private Column oldColumn;
/*     */   private Column newColumn;
/*     */   private int type;
/*     */   private Expression defaultExpression;
/*     */   private Expression newSelectivity;
/*     */   private Expression usingExpression;
/*     */   private boolean addFirst;
/*     */   private String addBefore;
/*     */   private String addAfter;
/*     */   private boolean ifTableExists;
/*     */   private boolean ifNotExists;
/*     */   private ArrayList<Column> columnsToAdd;
/*     */   private ArrayList<Column> columnsToRemove;
/*     */   private boolean booleanFlag;
/*     */   
/*     */   public AlterTableAlterColumn(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  78 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public void setIfTableExists(boolean paramBoolean) {
/*  82 */     this.ifTableExists = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setTableName(String paramString) {
/*  86 */     this.tableName = paramString;
/*     */   }
/*     */   
/*     */   public void setOldColumn(Column paramColumn) {
/*  90 */     this.oldColumn = paramColumn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAddFirst() {
/*  97 */     this.addFirst = true;
/*     */   }
/*     */   
/*     */   public void setAddBefore(String paramString) {
/* 101 */     this.addBefore = paramString;
/*     */   }
/*     */   
/*     */   public void setAddAfter(String paramString) {
/* 105 */     this.addAfter = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/* 110 */     Database database = this.session.getDatabase();
/* 111 */     Table table = getSchema().resolveTableOrView(this.session, this.tableName);
/* 112 */     if (table == null) {
/* 113 */       if (this.ifTableExists) {
/* 114 */         return 0L;
/*     */       }
/* 116 */       throw DbException.get(42102, this.tableName);
/*     */     } 
/* 118 */     this.session.getUser().checkTableRight(table, 32);
/* 119 */     table.checkSupportAlter();
/* 120 */     table.lock(this.session, 2);
/* 121 */     if (this.newColumn != null) {
/* 122 */       checkDefaultReferencesTable(table, this.newColumn.getDefaultExpression());
/* 123 */       checkClustering(this.newColumn);
/*     */     } 
/* 125 */     if (this.columnsToAdd != null) {
/* 126 */       for (Column column : this.columnsToAdd) {
/* 127 */         checkDefaultReferencesTable(table, column.getDefaultExpression());
/* 128 */         checkClustering(column);
/*     */       } 
/*     */     }
/* 131 */     switch (this.type) {
/*     */       case 8:
/* 133 */         if (this.oldColumn != null && this.oldColumn.isNullable()) {
/*     */ 
/*     */ 
/*     */           
/* 137 */           checkNoNullValues(table);
/* 138 */           this.oldColumn.setNullable(false);
/* 139 */           database.updateMeta(this.session, (DbObject)table);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 285 */         return 0L;case 9: if (this.oldColumn != null && !this.oldColumn.isNullable()) { checkNullable(table); this.oldColumn.setNullable(true); database.updateMeta(this.session, (DbObject)table); }  return 0L;case 10: case 98: if (this.oldColumn != null) if (!this.oldColumn.isIdentity()) { if (this.defaultExpression != null) { if (this.oldColumn.isGenerated()) return 0L;  checkDefaultReferencesTable(table, this.defaultExpression); this.oldColumn.setDefaultExpression(this.session, this.defaultExpression); } else { if (((this.type == 98)) != this.oldColumn.isGenerated()) return 0L;  this.oldColumn.setDefaultExpression(this.session, null); }  database.updateMeta(this.session, (DbObject)table); }   return 0L;case 99: if (this.oldColumn != null) { Sequence sequence = this.oldColumn.getSequence(); if (sequence != null) { this.oldColumn.setSequence(null, false); removeSequence(table, sequence); database.updateMeta(this.session, (DbObject)table); }  }  return 0L;case 90: if (this.oldColumn != null) { if (this.defaultExpression != null) { if (this.oldColumn.isIdentity() || this.oldColumn.isGenerated()) return 0L;  checkDefaultReferencesTable(table, this.defaultExpression); this.oldColumn.setOnUpdateExpression(this.session, this.defaultExpression); } else { this.oldColumn.setOnUpdateExpression(this.session, null); }  database.updateMeta(this.session, (DbObject)table); }  return 0L;case 11: if (this.oldColumn != null) { if (this.oldColumn.isWideningConversion(this.newColumn) && this.usingExpression == null) { convertIdentityColumn(table, this.newColumn); this.oldColumn.copy(this.newColumn); database.updateMeta(this.session, (DbObject)table); } else { this.oldColumn.setSequence(null, false); this.oldColumn.setDefaultExpression(this.session, null); if (this.oldColumn.isNullable() && !this.newColumn.isNullable()) { checkNoNullValues(table); } else if (!this.oldColumn.isNullable() && this.newColumn.isNullable()) { checkNullable(table); }  if ((this.oldColumn.getVisible() ^ this.newColumn.getVisible()) != 0) this.oldColumn.setVisible(this.newColumn.getVisible());  convertIdentityColumn(table, this.newColumn); copyData(table, (ArrayList<Sequence>)null, true); }  table.setModified(); }  return 0L;case 7: if (!this.ifNotExists || this.columnsToAdd == null || this.columnsToAdd.size() != 1 || !table.doesColumnExist(((Column)this.columnsToAdd.get(0)).getName())) { ArrayList<Sequence> arrayList = generateSequences(this.columnsToAdd, false); if (this.columnsToAdd != null) changePrimaryKeysToNotNull(this.columnsToAdd);  copyData(table, arrayList, true); }  return 0L;case 12: if ((table.getColumns()).length - this.columnsToRemove.size() < 1) throw DbException.get(90084, ((Column)this.columnsToRemove.get(0)).getTraceSQL());  table.dropMultipleColumnsConstraintsAndIndexes(this.session, this.columnsToRemove); copyData(table, (ArrayList<Sequence>)null, false); return 0L;case 13: if (this.oldColumn != null) { int i = this.newSelectivity.optimize(this.session).getValue(this.session).getInt(); this.oldColumn.setSelectivity(i); database.updateMeta(this.session, (DbObject)table); }  return 0L;case 87: if (this.oldColumn != null) if (this.oldColumn.getVisible() != this.booleanFlag) { this.oldColumn.setVisible(this.booleanFlag); table.setModified(); database.updateMeta(this.session, (DbObject)table); }   return 0L;case 100: if (this.oldColumn != null) if (this.oldColumn.isDefaultOnNull() != this.booleanFlag) { this.oldColumn.setDefaultOnNull(this.booleanFlag); table.setModified(); database.updateMeta(this.session, (DbObject)table); }   return 0L;
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + this.type);
/*     */   } private static void checkDefaultReferencesTable(Table paramTable, Expression paramExpression) {
/* 289 */     if (paramExpression == null) {
/*     */       return;
/*     */     }
/* 292 */     HashSet hashSet = new HashSet();
/*     */     
/* 294 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getDependenciesVisitor(hashSet);
/* 295 */     paramExpression.isEverything(expressionVisitor);
/* 296 */     if (hashSet.contains(paramTable)) {
/* 297 */       throw DbException.get(90083, paramExpression.getTraceSQL());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkClustering(Column paramColumn) {
/* 303 */     if (!"''".equals(this.session.getDatabase().getCluster()) && paramColumn
/* 304 */       .hasIdentityOptions()) {
/* 305 */       throw DbException.getUnsupportedException("CLUSTERING && identity columns");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void convertIdentityColumn(Table paramTable, Column paramColumn) {
/* 311 */     if (paramColumn.hasIdentityOptions()) {
/* 312 */       if (paramColumn.isPrimaryKey()) {
/* 313 */         addConstraintCommand(
/* 314 */             Parser.newPrimaryKeyConstraintCommand(this.session, paramTable.getSchema(), paramTable.getName(), paramColumn));
/*     */       }
/* 316 */       int i = getObjectId();
/* 317 */       paramColumn.initializeSequence(this.session, getSchema(), i, paramTable.isTemporary());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void removeSequence(Table paramTable, Sequence paramSequence) {
/* 322 */     if (paramSequence != null) {
/* 323 */       paramTable.removeSequence(paramSequence);
/* 324 */       paramSequence.setBelongsToTable(false);
/* 325 */       Database database = this.session.getDatabase();
/* 326 */       database.removeSchemaObject(this.session, (SchemaObject)paramSequence);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void copyData(Table paramTable, ArrayList<Sequence> paramArrayList, boolean paramBoolean) {
/* 331 */     if (paramTable.isTemporary()) {
/* 332 */       throw DbException.getUnsupportedException("TEMP TABLE");
/*     */     }
/* 334 */     Database database = this.session.getDatabase();
/* 335 */     String str1 = paramTable.getName();
/* 336 */     String str2 = database.getTempTableName(str1, this.session);
/* 337 */     Column[] arrayOfColumn = paramTable.getColumns();
/* 338 */     ArrayList<Column> arrayList = new ArrayList(arrayOfColumn.length);
/* 339 */     Table table = cloneTableStructure(paramTable, arrayOfColumn, database, str2, arrayList);
/* 340 */     if (paramArrayList != null) {
/* 341 */       for (Sequence sequence : paramArrayList) {
/* 342 */         paramTable.addSequence(sequence);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 348 */       checkViews((SchemaObject)paramTable, (SchemaObject)table);
/* 349 */     } catch (DbException dbException) {
/* 350 */       StringBuilder stringBuilder1 = new StringBuilder("DROP TABLE ");
/* 351 */       table.getSQL(stringBuilder1, 0);
/* 352 */       execute(stringBuilder1.toString());
/* 353 */       throw dbException;
/*     */     } 
/* 355 */     String str3 = paramTable.getName();
/* 356 */     ArrayList arrayList1 = new ArrayList(paramTable.getDependentViews());
/* 357 */     for (TableView tableView : arrayList1) {
/* 358 */       paramTable.removeDependentView(tableView);
/*     */     }
/* 360 */     StringBuilder stringBuilder = new StringBuilder("DROP TABLE ");
/* 361 */     paramTable.getSQL(stringBuilder, 0).append(" IGNORE");
/* 362 */     execute(stringBuilder.toString());
/* 363 */     database.renameSchemaObject(this.session, (SchemaObject)table, str3);
/* 364 */     for (DbObject dbObject : table.getChildren()) {
/* 365 */       if (dbObject instanceof Sequence) {
/*     */         continue;
/*     */       }
/* 368 */       String str = dbObject.getName();
/* 369 */       if (str == null || dbObject.getCreateSQL() == null) {
/*     */         continue;
/*     */       }
/* 372 */       if (str.startsWith(str2 + "_")) {
/* 373 */         str = str.substring(str2.length() + 1);
/* 374 */         SchemaObject schemaObject = (SchemaObject)dbObject;
/* 375 */         if (schemaObject instanceof Constraint) {
/* 376 */           if (schemaObject.getSchema().findConstraint(this.session, str) != null) {
/* 377 */             str = schemaObject.getSchema().getUniqueConstraintName(this.session, table);
/*     */           }
/* 379 */         } else if (schemaObject instanceof Index && 
/* 380 */           schemaObject.getSchema().findIndex(this.session, str) != null) {
/* 381 */           str = schemaObject.getSchema().getUniqueIndexName(this.session, table, str);
/*     */         } 
/*     */         
/* 384 */         database.renameSchemaObject(this.session, schemaObject, str);
/*     */       } 
/*     */     } 
/* 387 */     if (paramBoolean) {
/* 388 */       createConstraints();
/*     */     }
/* 390 */     for (TableView tableView : arrayList1) {
/* 391 */       String str = tableView.getCreateSQL(true, true);
/* 392 */       execute(str);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Table cloneTableStructure(Table paramTable, Column[] paramArrayOfColumn, Database paramDatabase, String paramString, ArrayList<Column> paramArrayList) {
/* 398 */     for (Column column : paramArrayOfColumn) {
/* 399 */       paramArrayList.add(column.getClone());
/*     */     }
/* 401 */     switch (this.type) {
/*     */       case 12:
/* 403 */         for (Column column1 : this.columnsToRemove) {
/* 404 */           Column column2 = null;
/* 405 */           for (Column column : paramArrayList) {
/* 406 */             if (column.getName().equals(column1.getName())) {
/* 407 */               column2 = column;
/*     */               break;
/*     */             } 
/*     */           } 
/* 411 */           if (column2 == null) {
/* 412 */             throw DbException.getInternalError(column1.getCreateSQL());
/*     */           }
/* 414 */           paramArrayList.remove(column2);
/*     */         } 
/*     */         break;
/*     */       
/*     */       case 7:
/* 419 */         if (this.addFirst) {
/* 420 */           i = 0;
/* 421 */         } else if (this.addBefore != null) {
/* 422 */           i = paramTable.getColumn(this.addBefore).getColumnId();
/* 423 */         } else if (this.addAfter != null) {
/* 424 */           i = paramTable.getColumn(this.addAfter).getColumnId() + 1;
/*     */         } else {
/* 426 */           i = paramArrayOfColumn.length;
/*     */         } 
/* 428 */         if (this.columnsToAdd != null) {
/* 429 */           for (Column column : this.columnsToAdd) {
/* 430 */             paramArrayList.add(i++, column);
/*     */           }
/*     */         }
/*     */         break;
/*     */       
/*     */       case 11:
/* 436 */         paramArrayList.set(this.oldColumn.getColumnId(), this.newColumn);
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 444 */     int i = paramDatabase.allocateObjectId();
/* 445 */     CreateTableData createTableData = new CreateTableData();
/* 446 */     createTableData.tableName = paramString;
/* 447 */     createTableData.id = i;
/* 448 */     createTableData.columns = paramArrayList;
/* 449 */     createTableData.temporary = paramTable.isTemporary();
/* 450 */     createTableData.persistData = paramTable.isPersistData();
/* 451 */     createTableData.persistIndexes = paramTable.isPersistIndexes();
/* 452 */     createTableData.isHidden = paramTable.isHidden();
/* 453 */     createTableData.session = this.session;
/* 454 */     Table table = getSchema().createTable(createTableData);
/* 455 */     table.setComment(paramTable.getComment());
/* 456 */     String str1 = table.getCreateSQLForMeta();
/* 457 */     StringBuilder stringBuilder1 = new StringBuilder();
/* 458 */     StringBuilder stringBuilder2 = new StringBuilder();
/* 459 */     for (Column column : paramArrayList) {
/* 460 */       if (column.isGenerated()) {
/*     */         continue;
/*     */       }
/* 463 */       switch (this.type) {
/*     */         case 7:
/* 465 */           if (this.columnsToAdd != null && this.columnsToAdd.contains(column)) {
/* 466 */             if (this.usingExpression != null) {
/* 467 */               this.usingExpression.getUnenclosedSQL(addColumn(column, stringBuilder1, stringBuilder2), 0);
/*     */             }
/*     */             continue;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 11:
/* 474 */           if (column.equals(this.newColumn) && this.usingExpression != null) {
/* 475 */             this.usingExpression.getUnenclosedSQL(addColumn(column, stringBuilder1, stringBuilder2), 0);
/*     */             continue;
/*     */           } 
/*     */           break;
/*     */       } 
/* 480 */       column.getSQL(addColumn(column, stringBuilder1, stringBuilder2), 0);
/*     */     } 
/* 482 */     String str2 = table.getName();
/* 483 */     Schema schema = table.getSchema();
/* 484 */     table.removeChildrenAndResources(this.session);
/*     */     
/* 486 */     execute(str1);
/* 487 */     table = schema.getTableOrView(this.session, str2);
/* 488 */     ArrayList<String> arrayList1 = Utils.newSmallArrayList();
/* 489 */     ArrayList<String> arrayList2 = Utils.newSmallArrayList();
/* 490 */     boolean bool = false;
/* 491 */     for (DbObject dbObject : paramTable.getChildren()) {
/* 492 */       if (dbObject instanceof Sequence)
/*     */         continue; 
/* 494 */       if (dbObject instanceof Index) {
/* 495 */         Index index = (Index)dbObject;
/* 496 */         if (index.getIndexType().getBelongsToConstraint()) {
/*     */           continue;
/*     */         }
/*     */       } 
/* 500 */       String str3 = dbObject.getCreateSQL();
/* 501 */       if (str3 == null) {
/*     */         continue;
/*     */       }
/* 504 */       if (dbObject instanceof TableView)
/*     */         continue; 
/* 506 */       if (dbObject.getType() == 0) {
/* 507 */         throw DbException.getInternalError();
/*     */       }
/* 509 */       String str4 = Parser.quoteIdentifier(paramString + "_" + dbObject.getName(), 0);
/* 510 */       String str5 = null;
/* 511 */       if (dbObject instanceof ConstraintReferential) {
/* 512 */         ConstraintReferential constraintReferential = (ConstraintReferential)dbObject;
/* 513 */         if (constraintReferential.getTable() != paramTable) {
/* 514 */           str5 = constraintReferential.getCreateSQLForCopy(constraintReferential.getTable(), table, str4, false);
/*     */         }
/*     */       } 
/* 517 */       if (str5 == null) {
/* 518 */         str5 = dbObject.getCreateSQLForCopy(table, str4);
/*     */       }
/* 520 */       if (str5 != null) {
/* 521 */         if (dbObject instanceof org.h2.schema.TriggerObject) {
/* 522 */           arrayList2.add(str5); continue;
/*     */         } 
/* 524 */         if (!bool) {
/* 525 */           Index index = null;
/* 526 */           if (dbObject instanceof ConstraintUnique) {
/* 527 */             ConstraintUnique constraintUnique = (ConstraintUnique)dbObject;
/* 528 */             if (constraintUnique.getConstraintType() == Constraint.Type.PRIMARY_KEY) {
/* 529 */               index = constraintUnique.getIndex();
/*     */             }
/* 531 */           } else if (dbObject instanceof Index) {
/* 532 */             index = (Index)dbObject;
/*     */           } 
/* 534 */           if (index != null && 
/* 535 */             TableBase.getMainIndexColumn(index.getIndexType(), index.getIndexColumns()) != -1) {
/*     */             
/* 537 */             execute(str5);
/* 538 */             bool = true;
/*     */             continue;
/*     */           } 
/*     */         } 
/* 542 */         arrayList1.add(str5);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 548 */     StringBuilder stringBuilder3 = table.getSQL((new StringBuilder(128)).append("INSERT INTO "), 0).append('(').append(stringBuilder1).append(") OVERRIDING SYSTEM VALUE SELECT ");
/* 549 */     if (stringBuilder2.length() == 0) {
/*     */       
/* 551 */       stringBuilder3.append('*');
/*     */     } else {
/* 553 */       stringBuilder3.append(stringBuilder2);
/*     */     } 
/* 555 */     paramTable.getSQL(stringBuilder3.append(" FROM "), 0);
/*     */     try {
/* 557 */       execute(stringBuilder3.toString());
/* 558 */     } catch (Throwable throwable) {
/*     */ 
/*     */       
/* 561 */       stringBuilder3 = new StringBuilder("DROP TABLE ");
/* 562 */       table.getSQL(stringBuilder3, 0);
/* 563 */       execute(stringBuilder3.toString());
/* 564 */       throw throwable;
/*     */     } 
/* 566 */     for (String str : arrayList1) {
/* 567 */       execute(str);
/*     */     }
/* 569 */     paramTable.setModified();
/*     */ 
/*     */     
/* 572 */     for (Column column : paramArrayList) {
/* 573 */       Sequence sequence = column.getSequence();
/* 574 */       if (sequence != null) {
/* 575 */         paramTable.removeSequence(sequence);
/* 576 */         column.setSequence(null, false);
/*     */       } 
/*     */     } 
/* 579 */     for (String str : arrayList2) {
/* 580 */       execute(str);
/*     */     }
/* 582 */     return table;
/*     */   }
/*     */   
/*     */   private static StringBuilder addColumn(Column paramColumn, StringBuilder paramStringBuilder1, StringBuilder paramStringBuilder2) {
/* 586 */     if (paramStringBuilder1.length() > 0) {
/* 587 */       paramStringBuilder1.append(", ");
/*     */     }
/* 589 */     paramColumn.getSQL(paramStringBuilder1, 0);
/* 590 */     if (paramStringBuilder2.length() > 0) {
/* 591 */       paramStringBuilder2.append(", ");
/*     */     }
/* 593 */     return paramStringBuilder2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkViews(SchemaObject paramSchemaObject1, SchemaObject paramSchemaObject2) {
/* 600 */     String str1 = paramSchemaObject1.getName();
/* 601 */     String str2 = paramSchemaObject2.getName();
/* 602 */     Database database = paramSchemaObject1.getDatabase();
/*     */     
/* 604 */     String str3 = database.getTempTableName(str1, this.session);
/* 605 */     database.renameSchemaObject(this.session, paramSchemaObject1, str3);
/*     */     
/*     */     try {
/* 608 */       database.renameSchemaObject(this.session, paramSchemaObject2, str1);
/* 609 */       checkViewsAreValid((DbObject)paramSchemaObject1);
/*     */     } finally {
/*     */       
/*     */       try {
/* 613 */         database.renameSchemaObject(this.session, paramSchemaObject2, str2);
/*     */       } finally {
/* 615 */         database.renameSchemaObject(this.session, paramSchemaObject1, str1);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkViewsAreValid(DbObject paramDbObject) {
/* 626 */     for (DbObject dbObject : paramDbObject.getChildren()) {
/* 627 */       if (dbObject instanceof TableView) {
/* 628 */         String str = ((TableView)dbObject).getQuery();
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 633 */           this.session.prepare(str);
/* 634 */         } catch (DbException dbException) {
/* 635 */           throw DbException.get(90083, dbException, new String[] { dbObject.getTraceSQL() });
/*     */         } 
/* 637 */         checkViewsAreValid(dbObject);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void execute(String paramString) {
/* 643 */     Prepared prepared = this.session.prepare(paramString);
/* 644 */     CommandContainer commandContainer = new CommandContainer(this.session, paramString, prepared);
/* 645 */     commandContainer.executeUpdate(null);
/*     */   }
/*     */   
/*     */   private void checkNullable(Table paramTable) {
/* 649 */     if (this.oldColumn.isIdentity()) {
/* 650 */       throw DbException.get(90023, this.oldColumn.getName());
/*     */     }
/* 652 */     for (Index index : paramTable.getIndexes()) {
/* 653 */       if (index.getColumnIndex(this.oldColumn) < 0) {
/*     */         continue;
/*     */       }
/* 656 */       IndexType indexType = index.getIndexType();
/* 657 */       if (indexType.isPrimaryKey()) {
/* 658 */         throw DbException.get(90023, this.oldColumn.getName());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkNoNullValues(Table paramTable) {
/* 664 */     StringBuilder stringBuilder = new StringBuilder("SELECT COUNT(*) FROM ");
/* 665 */     paramTable.getSQL(stringBuilder, 0).append(" WHERE ");
/* 666 */     this.oldColumn.getSQL(stringBuilder, 0).append(" IS NULL");
/* 667 */     String str = stringBuilder.toString();
/* 668 */     Prepared prepared = this.session.prepare(str);
/* 669 */     ResultInterface resultInterface = prepared.query(0L);
/* 670 */     resultInterface.next();
/* 671 */     if (resultInterface.currentRow()[0].getInt() > 0) {
/* 672 */       throw DbException.get(90081, this.oldColumn.getTraceSQL());
/*     */     }
/*     */   }
/*     */   
/*     */   public void setType(int paramInt) {
/* 677 */     this.type = paramInt;
/*     */   }
/*     */   
/*     */   public void setSelectivity(Expression paramExpression) {
/* 681 */     this.newSelectivity = paramExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultExpression(Expression paramExpression) {
/* 690 */     this.defaultExpression = paramExpression;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUsingExpression(Expression paramExpression) {
/* 699 */     this.usingExpression = paramExpression;
/*     */   }
/*     */   
/*     */   public void setNewColumn(Column paramColumn) {
/* 703 */     this.newColumn = paramColumn;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 708 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/* 712 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addColumn(Column paramColumn) {
/* 717 */     if (this.columnsToAdd == null) {
/* 718 */       this.columnsToAdd = new ArrayList<>();
/*     */     }
/* 720 */     this.columnsToAdd.add(paramColumn);
/*     */   }
/*     */   
/*     */   public void setColumnsToRemove(ArrayList<Column> paramArrayList) {
/* 724 */     this.columnsToRemove = paramArrayList;
/*     */   }
/*     */   
/*     */   public void setBooleanFlag(boolean paramBoolean) {
/* 728 */     this.booleanFlag = paramBoolean;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterTableAlterColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */