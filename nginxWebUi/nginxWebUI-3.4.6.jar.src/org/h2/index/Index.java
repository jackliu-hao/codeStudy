/*     */ package org.h2.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Mode;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.RowFactory;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.Typed;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Index
/*     */   extends SchemaObject
/*     */ {
/*     */   protected IndexColumn[] indexColumns;
/*     */   protected Column[] columns;
/*     */   protected int[] columnIds;
/*     */   protected final int uniqueColumnColumn;
/*     */   protected final Table table;
/*     */   protected final IndexType indexType;
/*     */   private final RowFactory rowFactory;
/*     */   private final RowFactory uniqueRowFactory;
/*     */   
/*     */   protected static void checkIndexColumnTypes(IndexColumn[] paramArrayOfIndexColumn) {
/*  44 */     for (IndexColumn indexColumn : paramArrayOfIndexColumn) {
/*  45 */       if (!DataType.isIndexable(indexColumn.column.getType())) {
/*  46 */         throw DbException.getUnsupportedException("Index on column: " + indexColumn.column.getCreateSQL());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Index(Table paramTable, int paramInt1, String paramString, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType) {
/*  99 */     super(paramTable.getSchema(), paramInt1, paramString, 5); RowFactory rowFactory2;
/* 100 */     this.uniqueColumnColumn = paramInt2;
/* 101 */     this.indexType = paramIndexType;
/* 102 */     this.table = paramTable;
/* 103 */     if (paramArrayOfIndexColumn != null) {
/* 104 */       this.indexColumns = paramArrayOfIndexColumn;
/* 105 */       this.columns = new Column[paramArrayOfIndexColumn.length];
/* 106 */       int i = this.columns.length;
/* 107 */       this.columnIds = new int[i];
/* 108 */       for (byte b = 0; b < i; b++) {
/* 109 */         Column column = (paramArrayOfIndexColumn[b]).column;
/* 110 */         this.columns[b] = column;
/* 111 */         this.columnIds[b] = column.getColumnId();
/*     */       } 
/*     */     } 
/* 114 */     RowFactory rowFactory1 = this.database.getRowFactory();
/* 115 */     CompareMode compareMode = this.database.getCompareMode();
/* 116 */     Column[] arrayOfColumn = this.table.getColumns();
/* 117 */     this.rowFactory = rowFactory1.createRowFactory((CastDataProvider)this.database, compareMode, (DataHandler)this.database, (Typed[])arrayOfColumn, 
/* 118 */         paramIndexType.isScan() ? null : paramArrayOfIndexColumn, true);
/*     */     
/* 120 */     if (paramInt2 > 0) {
/* 121 */       if (paramArrayOfIndexColumn == null || paramInt2 == paramArrayOfIndexColumn.length) {
/* 122 */         rowFactory2 = this.rowFactory;
/*     */       } else {
/* 124 */         rowFactory2 = rowFactory1.createRowFactory((CastDataProvider)this.database, compareMode, (DataHandler)this.database, (Typed[])arrayOfColumn, 
/* 125 */             Arrays.<IndexColumn>copyOf(paramArrayOfIndexColumn, paramInt2), true);
/*     */       } 
/*     */     } else {
/* 128 */       rowFactory2 = null;
/*     */     } 
/* 130 */     this.uniqueRowFactory = rowFactory2;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getType() {
/* 135 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 140 */     this.table.removeIndex(this);
/* 141 */     remove(paramSessionLocal);
/* 142 */     this.database.removeMeta(paramSessionLocal, getId());
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isHidden() {
/* 147 */     return this.table.isHidden();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 152 */     StringBuilder stringBuilder = new StringBuilder("CREATE ");
/* 153 */     stringBuilder.append(this.indexType.getSQL());
/* 154 */     stringBuilder.append(' ');
/* 155 */     if (this.table.isHidden()) {
/* 156 */       stringBuilder.append("IF NOT EXISTS ");
/*     */     }
/* 158 */     stringBuilder.append(paramString);
/* 159 */     stringBuilder.append(" ON ");
/* 160 */     paramTable.getSQL(stringBuilder, 0);
/* 161 */     if (this.comment != null) {
/* 162 */       stringBuilder.append(" COMMENT ");
/* 163 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/* 165 */     return getColumnListSQL(stringBuilder, 0).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private StringBuilder getColumnListSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 175 */     paramStringBuilder.append('(');
/* 176 */     int i = this.indexColumns.length;
/* 177 */     if (this.uniqueColumnColumn > 0 && this.uniqueColumnColumn < i) {
/* 178 */       IndexColumn.writeColumns(paramStringBuilder, this.indexColumns, 0, this.uniqueColumnColumn, paramInt).append(") INCLUDE(");
/* 179 */       IndexColumn.writeColumns(paramStringBuilder, this.indexColumns, this.uniqueColumnColumn, i, paramInt);
/*     */     } else {
/* 181 */       IndexColumn.writeColumns(paramStringBuilder, this.indexColumns, 0, i, paramInt);
/*     */     } 
/* 183 */     return paramStringBuilder.append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 188 */     return getCreateSQLForCopy(this.table, getSQL(0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPlanSQL() {
/* 197 */     return getSQL(11);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void close(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void add(SessionLocal paramSessionLocal, Row paramRow);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void remove(SessionLocal paramSessionLocal, Row paramRow);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/* 231 */     remove(paramSessionLocal, paramRow1);
/* 232 */     add(paramSessionLocal, paramRow2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFindUsingFullTableScan() {
/* 243 */     return false;
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
/*     */   public abstract Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void remove(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void truncate(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canGetFirstOrLast() {
/* 295 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canFindNext() {
/* 304 */     return false;
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
/*     */   public Cursor findNext(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 317 */     throw DbException.getInternalError(toString());
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
/*     */   public Cursor findFirstOrLast(SessionLocal paramSessionLocal, boolean paramBoolean) {
/* 330 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean needRebuild();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract long getRowCount(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract long getRowCountApproximation(SessionLocal paramSessionLocal);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDiskSpaceUsed() {
/* 363 */     return 0L;
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
/*     */   public final int compareRows(SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 375 */     if (paramSearchRow1 == paramSearchRow2)
/* 376 */       return 0;  byte b;
/*     */     int i;
/* 378 */     for (b = 0, i = this.indexColumns.length; b < i; b++) {
/* 379 */       int j = this.columnIds[b];
/* 380 */       Value value1 = paramSearchRow1.getValue(j);
/* 381 */       Value value2 = paramSearchRow2.getValue(j);
/* 382 */       if (value1 == null || value2 == null)
/*     */       {
/* 384 */         return 0;
/*     */       }
/* 386 */       int k = compareValues(value1, value2, (this.indexColumns[b]).sortType);
/* 387 */       if (k != 0) {
/* 388 */         return k;
/*     */       }
/*     */     } 
/* 391 */     return 0;
/*     */   }
/*     */   
/*     */   private int compareValues(Value paramValue1, Value paramValue2, int paramInt) {
/* 395 */     if (paramValue1 == paramValue2) {
/* 396 */       return 0;
/*     */     }
/* 398 */     boolean bool = (paramValue1 == ValueNull.INSTANCE) ? true : false;
/* 399 */     if (bool || paramValue2 == ValueNull.INSTANCE) {
/* 400 */       return this.table.getDatabase().getDefaultNullOrdering().compareNull(bool, paramInt);
/*     */     }
/* 402 */     int i = this.table.compareValues((CastDataProvider)this.database, paramValue1, paramValue2);
/* 403 */     if ((paramInt & 0x1) != 0) {
/* 404 */       i = -i;
/*     */     }
/* 406 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnIndex(Column paramColumn) {
/*     */     byte b;
/*     */     int i;
/* 416 */     for (b = 0, i = this.columns.length; b < i; b++) {
/* 417 */       if (this.columns[b].equals(paramColumn)) {
/* 418 */         return b;
/*     */       }
/*     */     } 
/* 421 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFirstColumn(Column paramColumn) {
/* 431 */     return paramColumn.equals(this.columns[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final IndexColumn[] getIndexColumns() {
/* 440 */     return this.indexColumns;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Column[] getColumns() {
/* 449 */     return this.columns;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getUniqueColumnCount() {
/* 460 */     return this.uniqueColumnColumn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final IndexType getIndexType() {
/* 469 */     return this.indexType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table getTable() {
/* 478 */     return this.table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Row getRow(SessionLocal paramSessionLocal, long paramLong) {
/* 489 */     throw DbException.getUnsupportedException(toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRowIdIndex() {
/* 498 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canScan() {
/* 507 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DbException getDuplicateKeyException(String paramString) {
/* 518 */     StringBuilder stringBuilder = new StringBuilder();
/* 519 */     getSQL(stringBuilder, 3).append(" ON ");
/* 520 */     this.table.getSQL(stringBuilder, 3);
/* 521 */     getColumnListSQL(stringBuilder, 3);
/* 522 */     if (paramString != null) {
/* 523 */       stringBuilder.append(" VALUES ").append(paramString);
/*     */     }
/* 525 */     DbException dbException = DbException.get(23505, stringBuilder.toString());
/* 526 */     dbException.setSource(this);
/* 527 */     return dbException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StringBuilder getDuplicatePrimaryKeyMessage(int paramInt) {
/* 537 */     StringBuilder stringBuilder = new StringBuilder("PRIMARY KEY ON ");
/* 538 */     this.table.getSQL(stringBuilder, 3);
/* 539 */     if (paramInt >= 0 && paramInt < this.indexColumns.length) {
/* 540 */       stringBuilder.append('(');
/* 541 */       this.indexColumns[paramInt].getSQL(stringBuilder, 3).append(')');
/*     */     } 
/* 543 */     return stringBuilder;
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected final long getCostRangeIndex(int[] paramArrayOfint, long paramLong, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, boolean paramBoolean, AllColumnsForPlan paramAllColumnsForPlan) {
/*     */     boolean bool;
/*     */     long l3;
/* 563 */     paramLong += 1000L;
/* 564 */     int i = 0;
/* 565 */     long l1 = paramLong;
/* 566 */     if (paramArrayOfint != null) {
/* 567 */       byte b = 0; int j = this.columns.length;
/* 568 */       bool = false;
/* 569 */       while (b < j) {
/* 570 */         Column column = this.columns[b++];
/* 571 */         int k = column.getColumnId();
/* 572 */         int m = paramArrayOfint[k];
/* 573 */         if ((m & 0x1) == 1) {
/* 574 */           if (b > 0 && b == this.uniqueColumnColumn) {
/* 575 */             l1 = 3L;
/*     */             
/*     */             break;
/*     */           } 
/* 579 */           i = 100 - (100 - i) * (100 - column.getSelectivity()) / 100;
/* 580 */           long l = paramLong * i / 100L;
/* 581 */           if (l <= 0L) {
/* 582 */             l = 1L;
/*     */           }
/* 584 */           l1 = 2L + Math.max(paramLong / l, 1L); continue;
/* 585 */         }  if ((m & 0x6) == 6) {
/* 586 */           l1 = 2L + l1 / 4L;
/* 587 */           bool = true; break;
/*     */         } 
/* 589 */         if ((m & 0x2) == 2) {
/* 590 */           l1 = 2L + l1 / 3L;
/* 591 */           bool = true; break;
/*     */         } 
/* 593 */         if ((m & 0x4) == 4) {
/* 594 */           l1 /= 3L;
/* 595 */           bool = true;
/*     */           break;
/*     */         } 
/* 598 */         if (m == 0)
/*     */         {
/* 600 */           b--;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 606 */       if (bool) {
/* 607 */         while (b < j && paramArrayOfint[this.columns[b].getColumnId()] != 0) {
/* 608 */           b++;
/* 609 */           l1--;
/*     */         } 
/*     */       }
/*     */       
/* 613 */       l1 += (j - b);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 618 */     long l2 = 0L;
/* 619 */     if (paramSortOrder != null) {
/* 620 */       l2 = 100L + paramLong / 10L;
/*     */     }
/* 622 */     if (paramSortOrder != null && !paramBoolean) {
/* 623 */       bool = true;
/* 624 */       byte b1 = 0;
/* 625 */       int[] arrayOfInt = paramSortOrder.getSortTypesWithNullOrdering();
/* 626 */       TableFilter tableFilter = (paramArrayOfTableFilter == null) ? null : paramArrayOfTableFilter[paramInt]; byte b2; int j;
/* 627 */       for (b2 = 0, j = arrayOfInt.length; b2 < j && 
/* 628 */         b2 < this.indexColumns.length; b2++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 635 */         Column column = paramSortOrder.getColumn(b2, tableFilter);
/* 636 */         if (column == null) {
/* 637 */           bool = false;
/*     */           break;
/*     */         } 
/* 640 */         IndexColumn indexColumn = this.indexColumns[b2];
/* 641 */         if (!column.equals(indexColumn.column)) {
/* 642 */           bool = false;
/*     */           break;
/*     */         } 
/* 645 */         int k = arrayOfInt[b2];
/* 646 */         if (k != indexColumn.sortType) {
/* 647 */           bool = false;
/*     */           break;
/*     */         } 
/* 650 */         b1++;
/*     */       } 
/* 652 */       if (bool)
/*     */       {
/*     */ 
/*     */         
/* 656 */         l2 = (100 - b1);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 663 */     if (!paramBoolean && paramAllColumnsForPlan != null) {
/* 664 */       bool = false;
/* 665 */       ArrayList arrayList = paramAllColumnsForPlan.get(getTable());
/* 666 */       if (arrayList != null) {
/* 667 */         int j = this.table.getMainIndexColumn();
/* 668 */         label97: for (Column column : arrayList) {
/* 669 */           int k = column.getColumnId();
/* 670 */           if (k == -1 || k == j) {
/*     */             continue;
/*     */           }
/* 673 */           for (Column column1 : this.columns) {
/* 674 */             if (column == column1) {
/*     */               continue label97;
/*     */             }
/*     */           } 
/* 678 */           bool = true;
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 683 */       bool = true;
/*     */     } 
/*     */     
/* 686 */     if (paramBoolean) {
/* 687 */       l3 = l1 + l2 + 20L;
/* 688 */     } else if (bool) {
/* 689 */       l3 = l1 + l1 + l2 + 20L;
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 696 */       l3 = l1 + l2 + this.columns.length;
/*     */     } 
/* 698 */     return l3;
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
/*     */   public final boolean mayHaveNullDuplicates(SearchRow paramSearchRow) {
/*     */     byte b;
/* 713 */     switch ((this.database.getMode()).uniqueIndexNullsHandling) {
/*     */       case ALLOW_DUPLICATES_WITH_ANY_NULL:
/* 715 */         for (b = 0; b < this.uniqueColumnColumn; b++) {
/* 716 */           int i = this.columnIds[b];
/* 717 */           if (paramSearchRow.getValue(i) == ValueNull.INSTANCE) {
/* 718 */             return true;
/*     */           }
/*     */         } 
/* 721 */         return false;
/*     */       case ALLOW_DUPLICATES_WITH_ALL_NULLS:
/* 723 */         for (b = 0; b < this.uniqueColumnColumn; b++) {
/* 724 */           int i = this.columnIds[b];
/* 725 */           if (paramSearchRow.getValue(i) != ValueNull.INSTANCE) {
/* 726 */             return false;
/*     */           }
/*     */         } 
/* 729 */         return true;
/*     */     } 
/* 731 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public RowFactory getRowFactory() {
/* 736 */     return this.rowFactory;
/*     */   }
/*     */   
/*     */   public RowFactory getUniqueRowFactory() {
/* 740 */     return this.uniqueRowFactory;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\Index.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */