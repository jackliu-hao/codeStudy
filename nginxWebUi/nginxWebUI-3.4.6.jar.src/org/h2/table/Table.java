/*      */ package org.h2.table;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import org.h2.command.Prepared;
/*      */ import org.h2.command.query.AllColumnsForPlan;
/*      */ import org.h2.constraint.Constraint;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.DbObject;
/*      */ import org.h2.engine.Right;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.ExpressionVisitor;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.index.IndexType;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.message.Trace;
/*      */ import org.h2.result.DefaultRow;
/*      */ import org.h2.result.LocalResult;
/*      */ import org.h2.result.Row;
/*      */ import org.h2.result.RowFactory;
/*      */ import org.h2.result.SearchRow;
/*      */ import org.h2.result.SimpleRowValue;
/*      */ import org.h2.result.SortOrder;
/*      */ import org.h2.schema.Schema;
/*      */ import org.h2.schema.SchemaObject;
/*      */ import org.h2.schema.Sequence;
/*      */ import org.h2.schema.TriggerObject;
/*      */ import org.h2.store.DataHandler;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.Typed;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueNull;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class Table
/*      */   extends SchemaObject
/*      */ {
/*      */   public static final int TYPE_CACHED = 0;
/*      */   public static final int TYPE_MEMORY = 1;
/*      */   public static final int READ_LOCK = 0;
/*      */   public static final int WRITE_LOCK = 1;
/*      */   public static final int EXCLUSIVE_LOCK = 2;
/*      */   protected Column[] columns;
/*      */   protected CompareMode compareMode;
/*      */   protected boolean isHidden;
/*      */   private final HashMap<String, Column> columnMap;
/*      */   private final boolean persistIndexes;
/*      */   private final boolean persistData;
/*      */   private ArrayList<TriggerObject> triggers;
/*      */   private ArrayList<Constraint> constraints;
/*      */   private ArrayList<Sequence> sequences;
/*  102 */   private final CopyOnWriteArrayList<TableView> dependentViews = new CopyOnWriteArrayList<>();
/*      */   private ArrayList<TableSynonym> synonyms;
/*      */   private boolean checkForeignKeyConstraints = true;
/*      */   private boolean onCommitDrop;
/*      */   private boolean onCommitTruncate;
/*      */   private volatile Row nullRow;
/*  108 */   private RowFactory rowFactory = RowFactory.getRowFactory();
/*      */   private boolean tableExpression;
/*      */   
/*      */   protected Table(Schema paramSchema, int paramInt, String paramString, boolean paramBoolean1, boolean paramBoolean2) {
/*  112 */     super(paramSchema, paramInt, paramString, 11);
/*  113 */     this.columnMap = paramSchema.getDatabase().newStringMap();
/*  114 */     this.persistIndexes = paramBoolean1;
/*  115 */     this.persistData = paramBoolean2;
/*  116 */     this.compareMode = paramSchema.getDatabase().getCompareMode();
/*      */   }
/*      */ 
/*      */   
/*      */   public void rename(String paramString) {
/*  121 */     super.rename(paramString);
/*  122 */     if (this.constraints != null) {
/*  123 */       for (Constraint constraint : this.constraints) {
/*  124 */         constraint.rebuild();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isView() {
/*  130 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean lock(SessionLocal paramSessionLocal, int paramInt) {
/*  143 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void close(SessionLocal paramSessionLocal);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unlock(SessionLocal paramSessionLocal) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Index addIndex(SessionLocal paramSessionLocal, String paramString1, int paramInt1, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType, boolean paramBoolean, String paramString2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Row getRow(SessionLocal paramSessionLocal, long paramLong) {
/*  186 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isInsertable() {
/*  195 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void removeRow(SessionLocal paramSessionLocal, Row paramRow);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Row lockRow(SessionLocal paramSessionLocal, Row paramRow) {
/*  214 */     throw DbException.getUnsupportedException("lockRow()");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long truncate(SessionLocal paramSessionLocal);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void addRow(SessionLocal paramSessionLocal, Row paramRow);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRow(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/*  243 */     paramRow2.setKey(paramRow1.getKey());
/*  244 */     removeRow(paramSessionLocal, paramRow1);
/*  245 */     addRow(paramSessionLocal, paramRow2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void checkSupportAlter();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract TableType getTableType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSQLTableType() {
/*  268 */     if (isView()) {
/*  269 */       return "VIEW";
/*      */     }
/*  271 */     if (isTemporary()) {
/*  272 */       return isGlobalTemporary() ? "GLOBAL TEMPORARY" : "LOCAL TEMPORARY";
/*      */     }
/*  274 */     return "BASE TABLE";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Index getScanIndex(SessionLocal paramSessionLocal);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Index getScanIndex(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/*  300 */     return getScanIndex(paramSessionLocal);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract ArrayList<Index> getIndexes();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Index getIndex(String paramString) {
/*  317 */     ArrayList<Index> arrayList = getIndexes();
/*  318 */     if (arrayList != null) {
/*  319 */       for (Index index : arrayList) {
/*  320 */         if (index.getName().equals(paramString)) {
/*  321 */           return index;
/*      */         }
/*      */       } 
/*      */     }
/*  325 */     throw DbException.get(42112, paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLockedExclusively() {
/*  334 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long getMaxDataModificationId();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isDeterministic();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean canGetRowCount(SessionLocal paramSessionLocal);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canReference() {
/*  365 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean canDrop();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long getRowCount(SessionLocal paramSessionLocal);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract long getRowCountApproximation(SessionLocal paramSessionLocal);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDiskSpaceUsed() {
/*  392 */     return 0L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Column getRowIdColumn() {
/*  401 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  406 */     throw DbException.getInternalError(toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isQueryComparable() {
/*  417 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addDependencies(HashSet<DbObject> paramHashSet) {
/*  426 */     if (paramHashSet.contains(this)) {
/*      */       return;
/*      */     }
/*      */     
/*  430 */     if (this.sequences != null) {
/*  431 */       paramHashSet.addAll(this.sequences);
/*      */     }
/*  433 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getDependenciesVisitor(paramHashSet);
/*      */     
/*  435 */     for (Column column : this.columns) {
/*  436 */       column.isEverything(expressionVisitor);
/*      */     }
/*  438 */     if (this.constraints != null) {
/*  439 */       for (Constraint constraint : this.constraints) {
/*  440 */         constraint.isEverything(expressionVisitor);
/*      */       }
/*      */     }
/*  443 */     paramHashSet.add(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public ArrayList<DbObject> getChildren() {
/*  448 */     ArrayList<Index> arrayList1 = Utils.newSmallArrayList();
/*  449 */     ArrayList<Index> arrayList2 = getIndexes();
/*  450 */     if (arrayList2 != null) {
/*  451 */       arrayList1.addAll(arrayList2);
/*      */     }
/*  453 */     if (this.constraints != null) {
/*  454 */       arrayList1.addAll(this.constraints);
/*      */     }
/*  456 */     if (this.triggers != null) {
/*  457 */       arrayList1.addAll(this.triggers);
/*      */     }
/*  459 */     if (this.sequences != null) {
/*  460 */       arrayList1.addAll(this.sequences);
/*      */     }
/*  462 */     arrayList1.addAll(this.dependentViews);
/*  463 */     if (this.synonyms != null) {
/*  464 */       arrayList1.addAll(this.synonyms);
/*      */     }
/*  466 */     ArrayList arrayList = this.database.getAllRights();
/*  467 */     for (Right right : arrayList) {
/*  468 */       if (right.getGrantedObject() == this) {
/*  469 */         arrayList1.add(right);
/*      */       }
/*      */     } 
/*  472 */     return (ArrayList)arrayList1;
/*      */   }
/*      */   
/*      */   protected void setColumns(Column[] paramArrayOfColumn) {
/*  476 */     if (paramArrayOfColumn.length > 16384) {
/*  477 */       throw DbException.get(54011, "16384");
/*      */     }
/*  479 */     this.columns = paramArrayOfColumn;
/*  480 */     if (this.columnMap.size() > 0) {
/*  481 */       this.columnMap.clear();
/*      */     }
/*  483 */     for (byte b = 0; b < paramArrayOfColumn.length; b++) {
/*  484 */       Column column = paramArrayOfColumn[b];
/*  485 */       int i = column.getType().getValueType();
/*  486 */       if (i == -1) {
/*  487 */         throw DbException.get(50004, column.getTraceSQL());
/*      */       }
/*  489 */       column.setTable(this, b);
/*  490 */       String str = column.getName();
/*  491 */       if (this.columnMap.putIfAbsent(str, column) != null) {
/*  492 */         throw DbException.get(42121, str);
/*      */       }
/*      */     } 
/*  495 */     this.rowFactory = this.database.getRowFactory().createRowFactory((CastDataProvider)this.database, this.database.getCompareMode(), (DataHandler)this.database, (Typed[])paramArrayOfColumn, null, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void renameColumn(Column paramColumn, String paramString) {
/*  506 */     for (Column column : this.columns) {
/*  507 */       if (column != paramColumn)
/*      */       {
/*      */         
/*  510 */         if (column.getName().equals(paramString)) {
/*  511 */           throw DbException.get(42121, paramString);
/*      */         }
/*      */       }
/*      */     } 
/*  515 */     this.columnMap.remove(paramColumn.getName());
/*  516 */     paramColumn.rename(paramString);
/*  517 */     this.columnMap.put(paramString, paramColumn);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLockedExclusivelyBy(SessionLocal paramSessionLocal) {
/*  528 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateRows(Prepared paramPrepared, SessionLocal paramSessionLocal, LocalResult paramLocalResult) {
/*  541 */     SessionLocal.Savepoint savepoint = paramSessionLocal.setSavepoint();
/*      */     
/*  543 */     byte b = 0;
/*  544 */     while (paramLocalResult.next()) {
/*  545 */       if ((++b & 0x7F) == 0) {
/*  546 */         paramPrepared.checkCanceled();
/*      */       }
/*  548 */       Row row = paramLocalResult.currentRowForTable();
/*  549 */       paramLocalResult.next();
/*      */       try {
/*  551 */         removeRow(paramSessionLocal, row);
/*  552 */       } catch (DbException dbException) {
/*  553 */         if (dbException.getErrorCode() == 90131 || dbException
/*  554 */           .getErrorCode() == 90112) {
/*  555 */           paramSessionLocal.rollbackTo(savepoint);
/*      */         }
/*  557 */         throw dbException;
/*      */       } 
/*      */     } 
/*      */     
/*  561 */     paramLocalResult.reset();
/*  562 */     while (paramLocalResult.next()) {
/*  563 */       if ((++b & 0x7F) == 0) {
/*  564 */         paramPrepared.checkCanceled();
/*      */       }
/*  566 */       paramLocalResult.next();
/*  567 */       Row row = paramLocalResult.currentRowForTable();
/*      */       try {
/*  569 */         addRow(paramSessionLocal, row);
/*  570 */       } catch (DbException dbException) {
/*  571 */         if (dbException.getErrorCode() == 90131) {
/*  572 */           paramSessionLocal.rollbackTo(savepoint);
/*      */         }
/*  574 */         throw dbException;
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   public CopyOnWriteArrayList<TableView> getDependentViews() {
/*  580 */     return this.dependentViews;
/*      */   }
/*      */ 
/*      */   
/*      */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/*  585 */     while (!this.dependentViews.isEmpty()) {
/*  586 */       TableView tableView = this.dependentViews.get(0);
/*  587 */       this.dependentViews.remove(0);
/*  588 */       this.database.removeSchemaObject(paramSessionLocal, tableView);
/*      */     } 
/*  590 */     while (this.synonyms != null && !this.synonyms.isEmpty()) {
/*  591 */       TableSynonym tableSynonym = this.synonyms.remove(0);
/*  592 */       this.database.removeSchemaObject(paramSessionLocal, tableSynonym);
/*      */     } 
/*  594 */     while (this.triggers != null && !this.triggers.isEmpty()) {
/*  595 */       TriggerObject triggerObject = this.triggers.remove(0);
/*  596 */       this.database.removeSchemaObject(paramSessionLocal, (SchemaObject)triggerObject);
/*      */     } 
/*  598 */     while (this.constraints != null && !this.constraints.isEmpty()) {
/*  599 */       Constraint constraint = this.constraints.remove(0);
/*  600 */       this.database.removeSchemaObject(paramSessionLocal, (SchemaObject)constraint);
/*      */     } 
/*  602 */     for (Right right : this.database.getAllRights()) {
/*  603 */       if (right.getGrantedObject() == this) {
/*  604 */         this.database.removeDatabaseObject(paramSessionLocal, (DbObject)right);
/*      */       }
/*      */     } 
/*  607 */     this.database.removeMeta(paramSessionLocal, getId());
/*      */ 
/*      */     
/*  610 */     while (this.sequences != null && !this.sequences.isEmpty()) {
/*  611 */       Sequence sequence = this.sequences.remove(0);
/*      */ 
/*      */       
/*  614 */       if (this.database.getDependentTable((SchemaObject)sequence, this) == null) {
/*  615 */         this.database.removeSchemaObject(paramSessionLocal, (SchemaObject)sequence);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void dropMultipleColumnsConstraintsAndIndexes(SessionLocal paramSessionLocal, ArrayList<Column> paramArrayList) {
/*  632 */     HashSet<Constraint> hashSet = new HashSet();
/*  633 */     if (this.constraints != null) {
/*  634 */       for (Column column : paramArrayList) {
/*  635 */         for (Constraint constraint : this.constraints) {
/*  636 */           HashSet hashSet2 = constraint.getReferencedColumns(this);
/*  637 */           if (!hashSet2.contains(column)) {
/*      */             continue;
/*      */           }
/*  640 */           if (hashSet2.size() == 1) {
/*  641 */             hashSet.add(constraint); continue;
/*      */           } 
/*  643 */           throw DbException.get(90083, constraint.getTraceSQL());
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  648 */     HashSet<Index> hashSet1 = new HashSet();
/*  649 */     ArrayList<Index> arrayList = getIndexes();
/*  650 */     if (arrayList != null) {
/*  651 */       for (Column column : paramArrayList) {
/*  652 */         for (Index index : arrayList) {
/*  653 */           if (index.getCreateSQL() == null) {
/*      */             continue;
/*      */           }
/*  656 */           if (index.getColumnIndex(column) < 0) {
/*      */             continue;
/*      */           }
/*  659 */           if ((index.getColumns()).length == 1) {
/*  660 */             hashSet1.add(index); continue;
/*      */           } 
/*  662 */           throw DbException.get(90083, index.getTraceSQL());
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  667 */     for (Constraint constraint : hashSet) {
/*  668 */       if (constraint.isValid()) {
/*  669 */         paramSessionLocal.getDatabase().removeSchemaObject(paramSessionLocal, (SchemaObject)constraint);
/*      */       }
/*      */     } 
/*  672 */     for (Index index : hashSet1) {
/*      */ 
/*      */       
/*  675 */       if (getIndexes().contains(index)) {
/*  676 */         paramSessionLocal.getDatabase().removeSchemaObject(paramSessionLocal, (SchemaObject)index);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public RowFactory getRowFactory() {
/*  682 */     return this.rowFactory;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Row createRow(Value[] paramArrayOfValue, int paramInt) {
/*  693 */     return this.rowFactory.createRow(paramArrayOfValue, paramInt);
/*      */   }
/*      */   
/*      */   public Row getTemplateRow() {
/*  697 */     return createRow(new Value[(getColumns()).length], -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SearchRow getTemplateSimpleRow(boolean paramBoolean) {
/*  707 */     if (paramBoolean) {
/*  708 */       return (SearchRow)new SimpleRowValue(this.columns.length);
/*      */     }
/*  710 */     return (SearchRow)new DefaultRow(new Value[this.columns.length]);
/*      */   }
/*      */   
/*      */   public Row getNullRow() {
/*  714 */     Row row = this.nullRow;
/*  715 */     if (row == null) {
/*      */ 
/*      */       
/*  718 */       Value[] arrayOfValue = new Value[this.columns.length];
/*  719 */       Arrays.fill((Object[])arrayOfValue, ValueNull.INSTANCE);
/*  720 */       this.nullRow = row = createRow(arrayOfValue, 1);
/*      */     } 
/*  722 */     return row;
/*      */   }
/*      */   
/*      */   public Column[] getColumns() {
/*  726 */     return this.columns;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getType() {
/*  731 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Column getColumn(int paramInt) {
/*  741 */     return this.columns[paramInt];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Column getColumn(String paramString) {
/*  752 */     Column column = this.columnMap.get(paramString);
/*  753 */     if (column == null) {
/*  754 */       throw DbException.get(42122, paramString);
/*      */     }
/*  756 */     return column;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Column getColumn(String paramString, boolean paramBoolean) {
/*  768 */     Column column = this.columnMap.get(paramString);
/*  769 */     if (column == null && !paramBoolean) {
/*  770 */       throw DbException.get(42122, paramString);
/*      */     }
/*  772 */     return column;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Column findColumn(String paramString) {
/*  782 */     return this.columnMap.get(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean doesColumnExist(String paramString) {
/*  792 */     return this.columnMap.containsKey(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Column getIdentityColumn() {
/*  801 */     for (Column column : this.columns) {
/*  802 */       if (column.isIdentity()) {
/*  803 */         return column;
/*      */       }
/*      */     } 
/*  806 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PlanItem getBestPlanItem(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/*  824 */     PlanItem planItem = new PlanItem();
/*  825 */     planItem.setIndex(getScanIndex(paramSessionLocal));
/*  826 */     planItem.cost = planItem.getIndex().getCost(paramSessionLocal, null, paramArrayOfTableFilter, paramInt, null, paramAllColumnsForPlan);
/*  827 */     Trace trace = paramSessionLocal.getTrace();
/*  828 */     if (trace.isDebugEnabled())
/*  829 */       trace.debug("Table      :     potential plan item cost {0} index {1}", new Object[] {
/*  830 */             Double.valueOf(planItem.cost), planItem.getIndex().getPlanSQL()
/*      */           }); 
/*  832 */     ArrayList<Index> arrayList = getIndexes();
/*  833 */     IndexHints indexHints = getIndexHints(paramArrayOfTableFilter, paramInt);
/*      */     
/*  835 */     if (arrayList != null && paramArrayOfint != null) {
/*  836 */       byte b; int i; for (b = 1, i = arrayList.size(); b < i; b++) {
/*  837 */         Index index = arrayList.get(b);
/*      */         
/*  839 */         if (!isIndexExcludedByHints(indexHints, index)) {
/*      */ 
/*      */ 
/*      */           
/*  843 */           double d = index.getCost(paramSessionLocal, paramArrayOfint, paramArrayOfTableFilter, paramInt, paramSortOrder, paramAllColumnsForPlan);
/*      */           
/*  845 */           if (trace.isDebugEnabled())
/*  846 */             trace.debug("Table      :     potential plan item cost {0} index {1}", new Object[] {
/*  847 */                   Double.valueOf(d), index.getPlanSQL()
/*      */                 }); 
/*  849 */           if (d < planItem.cost) {
/*  850 */             planItem.cost = d;
/*  851 */             planItem.setIndex(index);
/*      */           } 
/*      */         } 
/*      */       } 
/*  855 */     }  return planItem;
/*      */   }
/*      */   
/*      */   private static boolean isIndexExcludedByHints(IndexHints paramIndexHints, Index paramIndex) {
/*  859 */     return (paramIndexHints != null && !paramIndexHints.allowIndex(paramIndex));
/*      */   }
/*      */   
/*      */   private static IndexHints getIndexHints(TableFilter[] paramArrayOfTableFilter, int paramInt) {
/*  863 */     return (paramArrayOfTableFilter == null) ? null : paramArrayOfTableFilter[paramInt].getIndexHints();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Index findPrimaryKey() {
/*  872 */     ArrayList<Index> arrayList = getIndexes();
/*  873 */     if (arrayList != null) {
/*  874 */       for (Index index : arrayList) {
/*  875 */         if (index.getIndexType().isPrimaryKey()) {
/*  876 */           return index;
/*      */         }
/*      */       } 
/*      */     }
/*  880 */     return null;
/*      */   }
/*      */   
/*      */   public Index getPrimaryKey() {
/*  884 */     Index index = findPrimaryKey();
/*  885 */     if (index != null) {
/*  886 */       return index;
/*      */     }
/*  888 */     throw DbException.get(42112, "PRIMARY_KEY_");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void convertInsertRow(SessionLocal paramSessionLocal, Row paramRow, Boolean paramBoolean) {
/*  907 */     int i = this.columns.length; byte b1 = 0; byte b2;
/*  908 */     for (b2 = 0; b2 < i; b2++) {
/*  909 */       Value value1 = paramRow.getValue(b2);
/*  910 */       Column column = this.columns[b2];
/*  911 */       if (value1 == ValueNull.INSTANCE && column.isDefaultOnNull()) {
/*  912 */         value1 = null;
/*      */       }
/*  914 */       if (column.isIdentity()) {
/*  915 */         if (paramBoolean != null) {
/*  916 */           if (!paramBoolean.booleanValue()) {
/*  917 */             value1 = null;
/*      */           }
/*  919 */         } else if (value1 != null && column.isGeneratedAlways()) {
/*  920 */           throw DbException.get(90154, column
/*  921 */               .getSQLWithTable(new StringBuilder(), 3).toString());
/*      */         } 
/*  923 */       } else if (column.isGeneratedAlways()) {
/*  924 */         if (value1 != null) {
/*  925 */           throw DbException.get(90154, column
/*  926 */               .getSQLWithTable(new StringBuilder(), 3).toString());
/*      */         }
/*  928 */         b1++;
/*      */         continue;
/*      */       } 
/*  931 */       Value value2 = column.validateConvertUpdateSequence(paramSessionLocal, value1, paramRow);
/*  932 */       if (value2 != value1)
/*  933 */         paramRow.setValue(b2, value2); 
/*      */       continue;
/*      */     } 
/*  936 */     if (b1 > 0) {
/*  937 */       for (b2 = 0; b2 < i; b2++) {
/*  938 */         Value value = paramRow.getValue(b2);
/*  939 */         if (value == null) {
/*  940 */           paramRow.setValue(b2, this.columns[b2].validateConvertUpdateSequence(paramSessionLocal, (Value)null, paramRow));
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void convertUpdateRow(SessionLocal paramSessionLocal, Row paramRow, boolean paramBoolean) {
/*  958 */     int i = this.columns.length; byte b1 = 0; byte b2;
/*  959 */     for (b2 = 0; b2 < i; b2++) {
/*  960 */       Value value = paramRow.getValue(b2);
/*  961 */       Column column = this.columns[b2];
/*  962 */       if (column.isGenerated()) {
/*  963 */         if (value != null) {
/*  964 */           if (!paramBoolean) {
/*  965 */             throw DbException.get(90154, column
/*  966 */                 .getSQLWithTable(new StringBuilder(), 3).toString());
/*      */           }
/*  968 */           paramRow.setValue(b2, null);
/*      */         } 
/*  970 */         b1++;
/*      */       } else {
/*      */         
/*  973 */         Value value1 = column.validateConvertUpdateSequence(paramSessionLocal, value, paramRow);
/*  974 */         if (value1 != value)
/*  975 */           paramRow.setValue(b2, value1); 
/*      */       } 
/*      */     } 
/*  978 */     if (b1 > 0) {
/*  979 */       for (b2 = 0; b2 < i; b2++) {
/*  980 */         Value value = paramRow.getValue(b2);
/*  981 */         if (value == null) {
/*  982 */           paramRow.setValue(b2, this.columns[b2].validateConvertUpdateSequence(paramSessionLocal, (Value)null, paramRow));
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static void remove(ArrayList<? extends DbObject> paramArrayList, DbObject paramDbObject) {
/*  989 */     if (paramArrayList != null) {
/*  990 */       paramArrayList.remove(paramDbObject);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeIndex(Index paramIndex) {
/* 1000 */     ArrayList<Index> arrayList = getIndexes();
/* 1001 */     if (arrayList != null) {
/* 1002 */       remove((ArrayList)arrayList, (DbObject)paramIndex);
/* 1003 */       if (paramIndex.getIndexType().isPrimaryKey()) {
/* 1004 */         for (Column column : paramIndex.getColumns()) {
/* 1005 */           column.setPrimaryKey(false);
/*      */         }
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeDependentView(TableView paramTableView) {
/* 1017 */     this.dependentViews.remove(paramTableView);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeSynonym(TableSynonym paramTableSynonym) {
/* 1026 */     remove((ArrayList)this.synonyms, (DbObject)paramTableSynonym);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeConstraint(Constraint paramConstraint) {
/* 1035 */     remove((ArrayList)this.constraints, (DbObject)paramConstraint);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void removeSequence(Sequence paramSequence) {
/* 1044 */     remove((ArrayList)this.sequences, (DbObject)paramSequence);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeTrigger(TriggerObject paramTriggerObject) {
/* 1053 */     remove((ArrayList)this.triggers, (DbObject)paramTriggerObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addDependentView(TableView paramTableView) {
/* 1062 */     this.dependentViews.add(paramTableView);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addSynonym(TableSynonym paramTableSynonym) {
/* 1071 */     this.synonyms = add(this.synonyms, paramTableSynonym);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addConstraint(Constraint paramConstraint) {
/* 1080 */     if (this.constraints == null || !this.constraints.contains(paramConstraint)) {
/* 1081 */       this.constraints = add(this.constraints, paramConstraint);
/*      */     }
/*      */   }
/*      */   
/*      */   public ArrayList<Constraint> getConstraints() {
/* 1086 */     return this.constraints;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addSequence(Sequence paramSequence) {
/* 1095 */     this.sequences = add(this.sequences, paramSequence);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addTrigger(TriggerObject paramTriggerObject) {
/* 1104 */     this.triggers = add(this.triggers, paramTriggerObject);
/*      */   }
/*      */   
/*      */   private static <T> ArrayList<T> add(ArrayList<T> paramArrayList, T paramT) {
/* 1108 */     if (paramArrayList == null) {
/* 1109 */       paramArrayList = Utils.newSmallArrayList();
/*      */     }
/*      */     
/* 1112 */     paramArrayList.add(paramT);
/* 1113 */     return paramArrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fire(SessionLocal paramSessionLocal, int paramInt, boolean paramBoolean) {
/* 1124 */     if (this.triggers != null) {
/* 1125 */       for (TriggerObject triggerObject : this.triggers) {
/* 1126 */         triggerObject.fire(paramSessionLocal, paramInt, paramBoolean);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasSelectTrigger() {
/* 1137 */     if (this.triggers != null) {
/* 1138 */       for (TriggerObject triggerObject : this.triggers) {
/* 1139 */         if (triggerObject.isSelectTrigger()) {
/* 1140 */           return true;
/*      */         }
/*      */       } 
/*      */     }
/* 1144 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean fireRow() {
/* 1154 */     return ((this.constraints != null && !this.constraints.isEmpty()) || (this.triggers != null && 
/* 1155 */       !this.triggers.isEmpty()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean fireBeforeRow(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/* 1167 */     boolean bool = fireRow(paramSessionLocal, paramRow1, paramRow2, true, false);
/* 1168 */     fireConstraints(paramSessionLocal, paramRow1, paramRow2, true);
/* 1169 */     return bool;
/*      */   }
/*      */ 
/*      */   
/*      */   private void fireConstraints(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2, boolean paramBoolean) {
/* 1174 */     if (this.constraints != null) {
/* 1175 */       for (Constraint constraint : this.constraints) {
/* 1176 */         if (constraint.isBefore() == paramBoolean) {
/* 1177 */           constraint.checkRow(paramSessionLocal, this, paramRow1, paramRow2);
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void fireAfterRow(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2, boolean paramBoolean) {
/* 1193 */     fireRow(paramSessionLocal, paramRow1, paramRow2, false, paramBoolean);
/* 1194 */     if (!paramBoolean) {
/* 1195 */       fireConstraints(paramSessionLocal, paramRow1, paramRow2, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean fireRow(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2, boolean paramBoolean1, boolean paramBoolean2) {
/* 1201 */     if (this.triggers != null) {
/* 1202 */       for (TriggerObject triggerObject : this.triggers) {
/* 1203 */         boolean bool = triggerObject.fireRow(paramSessionLocal, this, paramRow1, paramRow2, paramBoolean1, paramBoolean2);
/* 1204 */         if (bool) {
/* 1205 */           return true;
/*      */         }
/*      */       } 
/*      */     }
/* 1209 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isGlobalTemporary() {
/* 1213 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canTruncate() {
/* 1222 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCheckForeignKeyConstraints(SessionLocal paramSessionLocal, boolean paramBoolean1, boolean paramBoolean2) {
/* 1234 */     if (paramBoolean1 && paramBoolean2 && 
/* 1235 */       this.constraints != null) {
/* 1236 */       for (Constraint constraint : this.constraints) {
/* 1237 */         if (constraint.getConstraintType() == Constraint.Type.REFERENTIAL) {
/* 1238 */           constraint.checkExistingData(paramSessionLocal);
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/* 1243 */     this.checkForeignKeyConstraints = paramBoolean1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getCheckForeignKeyConstraints() {
/* 1250 */     return this.checkForeignKeyConstraints;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Index getIndexForColumn(Column paramColumn, boolean paramBoolean1, boolean paramBoolean2) {
/* 1266 */     ArrayList<Index> arrayList = getIndexes();
/* 1267 */     Index index = null;
/* 1268 */     if (arrayList != null) {
/* 1269 */       byte b; int i; for (b = 1, i = arrayList.size(); b < i; b++) {
/* 1270 */         Index index1 = arrayList.get(b);
/* 1271 */         if (!paramBoolean1 || index1.canGetFirstOrLast())
/*      */         {
/*      */           
/* 1274 */           if (!paramBoolean2 || index1.canFindNext())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1280 */             if (index1.isFirstColumn(paramColumn) && (index == null || (index
/* 1281 */               .getColumns()).length > (index1.getColumns()).length))
/* 1282 */               index = index1;  } 
/*      */         }
/*      */       } 
/*      */     } 
/* 1286 */     return index;
/*      */   }
/*      */   
/*      */   public boolean getOnCommitDrop() {
/* 1290 */     return this.onCommitDrop;
/*      */   }
/*      */   
/*      */   public void setOnCommitDrop(boolean paramBoolean) {
/* 1294 */     this.onCommitDrop = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean getOnCommitTruncate() {
/* 1298 */     return this.onCommitTruncate;
/*      */   }
/*      */   
/*      */   public void setOnCommitTruncate(boolean paramBoolean) {
/* 1302 */     this.onCommitTruncate = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeIndexOrTransferOwnership(SessionLocal paramSessionLocal, Index paramIndex) {
/* 1313 */     boolean bool = false;
/* 1314 */     if (this.constraints != null) {
/* 1315 */       for (Constraint constraint : this.constraints) {
/* 1316 */         if (constraint.usesIndex(paramIndex)) {
/* 1317 */           constraint.setIndexOwner(paramIndex);
/* 1318 */           this.database.updateMeta(paramSessionLocal, (DbObject)constraint);
/* 1319 */           bool = true;
/*      */         } 
/*      */       } 
/*      */     }
/* 1323 */     if (!bool) {
/* 1324 */       this.database.removeSchemaObject(paramSessionLocal, (SchemaObject)paramIndex);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeColumnExpressionsDependencies(SessionLocal paramSessionLocal) {
/* 1335 */     for (Column column : this.columns) {
/* 1336 */       column.setDefaultExpression(paramSessionLocal, (Expression)null);
/* 1337 */       column.setOnUpdateExpression(paramSessionLocal, (Expression)null);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayList<SessionLocal> checkDeadlock(SessionLocal paramSessionLocal1, SessionLocal paramSessionLocal2, Set<SessionLocal> paramSet) {
/* 1360 */     return null;
/*      */   }
/*      */   
/*      */   public boolean isPersistIndexes() {
/* 1364 */     return this.persistIndexes;
/*      */   }
/*      */   
/*      */   public boolean isPersistData() {
/* 1368 */     return this.persistData;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareValues(CastDataProvider paramCastDataProvider, Value paramValue1, Value paramValue2) {
/* 1382 */     return paramValue1.compareTo(paramValue2, paramCastDataProvider, this.compareMode);
/*      */   }
/*      */   
/*      */   public CompareMode getCompareMode() {
/* 1386 */     return this.compareMode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void checkWritingAllowed() {
/* 1395 */     this.database.checkWritingAllowed();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isHidden() {
/* 1400 */     return this.isHidden;
/*      */   }
/*      */   
/*      */   public void setHidden(boolean paramBoolean) {
/* 1404 */     this.isHidden = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isRowLockable() {
/* 1412 */     return false;
/*      */   }
/*      */   
/*      */   public void setTableExpression(boolean paramBoolean) {
/* 1416 */     this.tableExpression = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean isTableExpression() {
/* 1420 */     return this.tableExpression;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayList<TriggerObject> getTriggers() {
/* 1429 */     return this.triggers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMainIndexColumn() {
/* 1438 */     return -1;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\Table.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */