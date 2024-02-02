/*      */ package org.h2.command.query;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import org.h2.engine.Database;
/*      */ import org.h2.engine.DbObject;
/*      */ import org.h2.engine.Mode;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.expression.Alias;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.ExpressionColumn;
/*      */ import org.h2.expression.ExpressionList;
/*      */ import org.h2.expression.ExpressionVisitor;
/*      */ import org.h2.expression.Parameter;
/*      */ import org.h2.expression.Wildcard;
/*      */ import org.h2.expression.analysis.DataAnalysisOperation;
/*      */ import org.h2.expression.analysis.Window;
/*      */ import org.h2.expression.condition.Comparison;
/*      */ import org.h2.expression.condition.ConditionAndOr;
/*      */ import org.h2.expression.condition.ConditionLocalAndGlobal;
/*      */ import org.h2.expression.function.CoalesceFunction;
/*      */ import org.h2.index.Cursor;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.index.ViewIndex;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.mode.DefaultNullOrdering;
/*      */ import org.h2.result.LazyResult;
/*      */ import org.h2.result.LocalResult;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.result.ResultTarget;
/*      */ import org.h2.result.Row;
/*      */ import org.h2.result.SearchRow;
/*      */ import org.h2.result.SortOrder;
/*      */ import org.h2.table.Column;
/*      */ import org.h2.table.ColumnResolver;
/*      */ import org.h2.table.IndexColumn;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.table.TableFilter;
/*      */ import org.h2.table.TableType;
/*      */ import org.h2.table.TableView;
/*      */ import org.h2.util.ParserUtil;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueRow;
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
/*      */ public class Select
/*      */   extends Query
/*      */ {
/*      */   TableFilter topTableFilter;
/*   84 */   private final ArrayList<TableFilter> filters = Utils.newSmallArrayList();
/*   85 */   private final ArrayList<TableFilter> topFilters = Utils.newSmallArrayList();
/*      */ 
/*      */   
/*      */   private Select parentSelect;
/*      */ 
/*      */   
/*      */   private Expression condition;
/*      */ 
/*      */   
/*      */   private Expression having;
/*      */ 
/*      */   
/*      */   private Expression qualify;
/*      */ 
/*      */   
/*      */   private Expression[] distinctExpressions;
/*      */ 
/*      */   
/*      */   private int[] distinctIndexes;
/*      */ 
/*      */   
/*      */   private ArrayList<Expression> group;
/*      */ 
/*      */   
/*      */   int[] groupIndex;
/*      */ 
/*      */   
/*      */   boolean[] groupByExpression;
/*      */ 
/*      */   
/*      */   SelectGroups groupData;
/*      */ 
/*      */   
/*      */   private int havingIndex;
/*      */ 
/*      */   
/*      */   private int qualifyIndex;
/*      */ 
/*      */   
/*      */   private int[] groupByCopies;
/*      */ 
/*      */   
/*      */   private boolean isExplicitTable;
/*      */ 
/*      */   
/*      */   boolean isGroupQuery;
/*      */ 
/*      */   
/*      */   private boolean isGroupSortedQuery;
/*      */ 
/*      */   
/*      */   private boolean isWindowQuery;
/*      */ 
/*      */   
/*      */   private boolean isForUpdate;
/*      */ 
/*      */   
/*      */   private double cost;
/*      */ 
/*      */   
/*      */   private boolean isQuickAggregateQuery;
/*      */ 
/*      */   
/*      */   private boolean isDistinctQuery;
/*      */ 
/*      */   
/*      */   private boolean sortUsingIndex;
/*      */ 
/*      */   
/*      */   private boolean isGroupWindowStage2;
/*      */   
/*      */   private HashMap<String, Window> windows;
/*      */ 
/*      */   
/*      */   public Select(SessionLocal paramSessionLocal, Select paramSelect) {
/*  160 */     super(paramSessionLocal);
/*  161 */     this.parentSelect = paramSelect;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isUnion() {
/*  166 */     return false;
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
/*      */   public void addTableFilter(TableFilter paramTableFilter, boolean paramBoolean) {
/*  183 */     this.filters.add(paramTableFilter);
/*  184 */     if (paramBoolean) {
/*  185 */       this.topFilters.add(paramTableFilter);
/*      */     }
/*      */   }
/*      */   
/*      */   public ArrayList<TableFilter> getTopFilters() {
/*  190 */     return this.topFilters;
/*      */   }
/*      */   
/*      */   public void setExpressions(ArrayList<Expression> paramArrayList) {
/*  194 */     this.expressions = paramArrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExplicitTable() {
/*  201 */     setWildcard();
/*  202 */     this.isExplicitTable = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWildcard() {
/*  209 */     this.expressions = new ArrayList<>(1);
/*  210 */     this.expressions.add(new Wildcard(null, null));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGroupQuery() {
/*  218 */     this.isGroupQuery = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWindowQuery() {
/*  225 */     this.isWindowQuery = true;
/*      */   }
/*      */   
/*      */   public void setGroupBy(ArrayList<Expression> paramArrayList) {
/*  229 */     this.group = paramArrayList;
/*      */   }
/*      */   
/*      */   public ArrayList<Expression> getGroupBy() {
/*  233 */     return this.group;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SelectGroups getGroupDataIfCurrent(boolean paramBoolean) {
/*  243 */     return (this.groupData != null && (paramBoolean || this.groupData.isCurrentGroup())) ? this.groupData : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDistinct() {
/*  250 */     if (this.distinctExpressions != null) {
/*  251 */       throw DbException.getUnsupportedException("DISTINCT ON together with DISTINCT");
/*      */     }
/*  253 */     this.distinct = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDistinct(Expression[] paramArrayOfExpression) {
/*  262 */     if (this.distinct) {
/*  263 */       throw DbException.getUnsupportedException("DISTINCT ON together with DISTINCT");
/*      */     }
/*  265 */     this.distinctExpressions = paramArrayOfExpression;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isAnyDistinct() {
/*  270 */     return (this.distinct || this.distinctExpressions != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean addWindow(String paramString, Window paramWindow) {
/*  281 */     if (this.windows == null) {
/*  282 */       this.windows = new HashMap<>();
/*      */     }
/*  284 */     return (this.windows.put(paramString, paramWindow) == null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Window getWindow(String paramString) {
/*  294 */     return (this.windows != null) ? this.windows.get(paramString) : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addCondition(Expression paramExpression) {
/*  303 */     if (this.condition == null) {
/*  304 */       this.condition = paramExpression;
/*      */     } else {
/*  306 */       this.condition = (Expression)new ConditionAndOr(0, paramExpression, this.condition);
/*      */     } 
/*      */   }
/*      */   
/*      */   public Expression getCondition() {
/*  311 */     return this.condition;
/*      */   }
/*      */   
/*      */   private LazyResult queryGroupSorted(int paramInt, ResultTarget paramResultTarget, long paramLong, boolean paramBoolean) {
/*  315 */     LazyResultGroupSorted lazyResultGroupSorted = new LazyResultGroupSorted(this.expressionArray, paramInt);
/*  316 */     skipOffset(lazyResultGroupSorted, paramLong, paramBoolean);
/*  317 */     if (paramResultTarget == null) {
/*  318 */       return lazyResultGroupSorted;
/*      */     }
/*  320 */     while (lazyResultGroupSorted.next()) {
/*  321 */       paramResultTarget.addRow(lazyResultGroupSorted.currentRow());
/*      */     }
/*  323 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Value[] createGroupSortedRow(Value[] paramArrayOfValue, int paramInt) {
/*  334 */     Value[] arrayOfValue = constructGroupResultRow(paramArrayOfValue, paramInt);
/*  335 */     if (isHavingNullOrFalse(arrayOfValue)) {
/*  336 */       return null;
/*      */     }
/*  338 */     return rowForResult(arrayOfValue, paramInt);
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
/*      */   private Value[] rowForResult(Value[] paramArrayOfValue, int paramInt) {
/*  351 */     if (paramInt == this.resultColumnCount) {
/*  352 */       return paramArrayOfValue;
/*      */     }
/*  354 */     return Arrays.<Value>copyOf(paramArrayOfValue, this.resultColumnCount);
/*      */   }
/*      */   
/*      */   private boolean isHavingNullOrFalse(Value[] paramArrayOfValue) {
/*  358 */     return (this.havingIndex >= 0 && !paramArrayOfValue[this.havingIndex].isTrue());
/*      */   }
/*      */   
/*      */   private Index getGroupSortedIndex() {
/*  362 */     if (this.groupIndex == null || this.groupByExpression == null) {
/*  363 */       return null;
/*      */     }
/*  365 */     ArrayList arrayList = this.topTableFilter.getTable().getIndexes();
/*  366 */     if (arrayList != null) {
/*  367 */       for (Index index : arrayList) {
/*  368 */         if (index.getIndexType().isScan()) {
/*      */           continue;
/*      */         }
/*  371 */         if (index.getIndexType().isHash()) {
/*      */           continue;
/*      */         }
/*      */         
/*  375 */         if (isGroupSortedIndex(this.topTableFilter, index)) {
/*  376 */           return index;
/*      */         }
/*      */       } 
/*      */     }
/*  380 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isGroupSortedIndex(TableFilter paramTableFilter, Index paramIndex) {
/*  385 */     Column[] arrayOfColumn = paramIndex.getColumns();
/*      */     
/*  387 */     boolean[] arrayOfBoolean = new boolean[arrayOfColumn.length]; byte b;
/*      */     int i;
/*  389 */     for (b = 0, i = this.expressions.size(); b < i; b++) {
/*  390 */       if (this.groupByExpression[b]) {
/*      */ 
/*      */         
/*  393 */         Expression expression = ((Expression)this.expressions.get(b)).getNonAliasExpression();
/*  394 */         if (!(expression instanceof ExpressionColumn)) {
/*  395 */           return false;
/*      */         }
/*  397 */         ExpressionColumn expressionColumn = (ExpressionColumn)expression;
/*  398 */         byte b1 = 0; while (true) { if (b1 < arrayOfColumn.length) {
/*  399 */             if (paramTableFilter == expressionColumn.getTableFilter() && 
/*  400 */               arrayOfColumn[b1].equals(expressionColumn.getColumn())) {
/*  401 */               arrayOfBoolean[b1] = true;
/*      */               
/*      */               break;
/*      */             } 
/*      */             b1++;
/*      */             continue;
/*      */           } 
/*  408 */           return false; }
/*      */       
/*      */       } 
/*      */     } 
/*      */     
/*  413 */     for (b = 1; b < arrayOfBoolean.length; b++) {
/*  414 */       if (!arrayOfBoolean[b - 1] && arrayOfBoolean[b]) {
/*  415 */         return false;
/*      */       }
/*      */     } 
/*  418 */     return true;
/*      */   }
/*      */   
/*      */   boolean isConditionMetForUpdate() {
/*  422 */     if (isConditionMet()) {
/*  423 */       int i = this.filters.size();
/*  424 */       boolean bool = true;
/*  425 */       for (byte b = 0; b < i; b++) {
/*  426 */         TableFilter tableFilter = this.filters.get(b);
/*  427 */         if (!tableFilter.isJoinOuter() && !tableFilter.isJoinOuterIndirect()) {
/*  428 */           Row row = tableFilter.get();
/*  429 */           Table table = tableFilter.getTable();
/*      */           
/*  431 */           if (table.isRowLockable()) {
/*  432 */             Row row1 = table.lockRow(this.session, row);
/*  433 */             if (row1 == null) {
/*  434 */               return false;
/*      */             }
/*  436 */             if (!row.hasSharedData(row1)) {
/*  437 */               tableFilter.set(row1);
/*  438 */               bool = false;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*  443 */       return (bool || isConditionMet());
/*      */     } 
/*  445 */     return false;
/*      */   }
/*      */   
/*      */   boolean isConditionMet() {
/*  449 */     return (this.condition == null || this.condition.getBooleanValue(this.session));
/*      */   }
/*      */   
/*      */   private void queryWindow(int paramInt, LocalResult paramLocalResult, long paramLong, boolean paramBoolean) {
/*  453 */     initGroupData(paramInt);
/*      */     try {
/*  455 */       gatherGroup(paramInt, 2);
/*  456 */       processGroupResult(paramInt, paramLocalResult, paramLong, paramBoolean, false);
/*      */     } finally {
/*  458 */       this.groupData.reset();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void queryGroupWindow(int paramInt, LocalResult paramLocalResult, long paramLong, boolean paramBoolean) {
/*  463 */     initGroupData(paramInt);
/*      */     try {
/*  465 */       gatherGroup(paramInt, 1);
/*      */       try {
/*  467 */         this.isGroupWindowStage2 = true;
/*  468 */         while (this.groupData.next() != null) {
/*  469 */           if (this.havingIndex < 0 || ((Expression)this.expressions.get(this.havingIndex)).getBooleanValue(this.session)) {
/*  470 */             updateAgg(paramInt, 2); continue;
/*      */           } 
/*  472 */           this.groupData.remove();
/*      */         } 
/*      */         
/*  475 */         this.groupData.done();
/*  476 */         processGroupResult(paramInt, paramLocalResult, paramLong, paramBoolean, false);
/*      */       } finally {
/*  478 */         this.isGroupWindowStage2 = false;
/*      */       } 
/*      */     } finally {
/*  481 */       this.groupData.reset();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void queryGroup(int paramInt, LocalResult paramLocalResult, long paramLong, boolean paramBoolean) {
/*  486 */     initGroupData(paramInt);
/*      */     try {
/*  488 */       gatherGroup(paramInt, 1);
/*  489 */       processGroupResult(paramInt, paramLocalResult, paramLong, paramBoolean, true);
/*      */     } finally {
/*  491 */       this.groupData.reset();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initGroupData(int paramInt) {
/*  496 */     if (this.groupData == null) {
/*  497 */       setGroupData(SelectGroups.getInstance(this.session, this.expressions, this.isGroupQuery, this.groupIndex));
/*      */     } else {
/*  499 */       updateAgg(paramInt, 0);
/*      */     } 
/*  501 */     this.groupData.reset();
/*      */   }
/*      */   
/*      */   void setGroupData(SelectGroups paramSelectGroups) {
/*  505 */     this.groupData = paramSelectGroups;
/*  506 */     this.topTableFilter.visit(paramTableFilter -> {
/*      */           Select select = paramTableFilter.getSelect();
/*      */           if (select != null) {
/*      */             select.groupData = paramSelectGroups;
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   private void gatherGroup(int paramInt1, int paramInt2) {
/*  515 */     long l = 0L;
/*  516 */     setCurrentRowNumber(0L);
/*  517 */     while (this.topTableFilter.next()) {
/*  518 */       setCurrentRowNumber(l + 1L);
/*  519 */       if (this.isForUpdate ? isConditionMetForUpdate() : isConditionMet()) {
/*  520 */         l++;
/*  521 */         this.groupData.nextSource();
/*  522 */         updateAgg(paramInt1, paramInt2);
/*      */       } 
/*      */     } 
/*  525 */     this.groupData.done();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void updateAgg(int paramInt1, int paramInt2) {
/*  535 */     for (byte b = 0; b < paramInt1; b++) {
/*  536 */       if ((this.groupByExpression == null || !this.groupByExpression[b]) && (this.groupByCopies == null || this.groupByCopies[b] < 0)) {
/*      */         
/*  538 */         Expression expression = this.expressions.get(b);
/*  539 */         expression.updateAggregate(this.session, paramInt2);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void processGroupResult(int paramInt, LocalResult paramLocalResult, long paramLong, boolean paramBoolean1, boolean paramBoolean2) {
/*      */     ValueRow valueRow;
/*  546 */     while ((valueRow = this.groupData.next()) != null) {
/*  547 */       Value[] arrayOfValue = constructGroupResultRow(valueRow.getList(), paramInt);
/*  548 */       if (paramBoolean2 && isHavingNullOrFalse(arrayOfValue)) {
/*      */         continue;
/*      */       }
/*  551 */       if (this.qualifyIndex >= 0 && !arrayOfValue[this.qualifyIndex].isTrue()) {
/*      */         continue;
/*      */       }
/*  554 */       if (paramBoolean1 && paramLong > 0L) {
/*  555 */         paramLong--;
/*      */         continue;
/*      */       } 
/*  558 */       paramLocalResult.addRow(rowForResult(arrayOfValue, paramInt));
/*      */     } 
/*      */   }
/*      */   
/*      */   private Value[] constructGroupResultRow(Value[] paramArrayOfValue, int paramInt) {
/*  563 */     Value[] arrayOfValue = new Value[paramInt];
/*  564 */     if (this.groupIndex != null) {
/*  565 */       byte b1; int i; for (b1 = 0, i = this.groupIndex.length; b1 < i; b1++) {
/*  566 */         arrayOfValue[this.groupIndex[b1]] = paramArrayOfValue[b1];
/*      */       }
/*      */     } 
/*  569 */     for (byte b = 0; b < paramInt; b++) {
/*  570 */       if (this.groupByExpression != null && this.groupByExpression[b]) {
/*      */         continue;
/*      */       }
/*  573 */       if (this.groupByCopies != null) {
/*  574 */         int i = this.groupByCopies[b];
/*  575 */         if (i >= 0) {
/*  576 */           arrayOfValue[b] = arrayOfValue[i];
/*      */           continue;
/*      */         } 
/*      */       } 
/*  580 */       arrayOfValue[b] = ((Expression)this.expressions.get(b)).getValue(this.session); continue;
/*      */     } 
/*  582 */     return arrayOfValue;
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
/*      */   private Index getSortIndex() {
/*  594 */     if (this.sort == null) {
/*  595 */       return null;
/*      */     }
/*  597 */     ArrayList<Column> arrayList = Utils.newSmallArrayList();
/*  598 */     int[] arrayOfInt1 = this.sort.getQueryColumnIndexes();
/*  599 */     int i = arrayOfInt1.length;
/*  600 */     int[] arrayOfInt2 = new int[i];
/*  601 */     for (byte b1 = 0, b2 = 0; b1 < i; b1++) {
/*  602 */       int j = arrayOfInt1[b1];
/*  603 */       if (j < 0 || j >= this.expressions.size()) {
/*  604 */         throw DbException.getInvalidValueException("ORDER BY", Integer.valueOf(j + 1));
/*      */       }
/*  606 */       Expression expression = this.expressions.get(j);
/*  607 */       expression = expression.getNonAliasExpression();
/*  608 */       if (!expression.isConstant()) {
/*      */ 
/*      */         
/*  611 */         if (!(expression instanceof ExpressionColumn)) {
/*  612 */           return null;
/*      */         }
/*  614 */         ExpressionColumn expressionColumn = (ExpressionColumn)expression;
/*  615 */         if (expressionColumn.getTableFilter() != this.topTableFilter) {
/*  616 */           return null;
/*      */         }
/*  618 */         arrayList.add(expressionColumn.getColumn());
/*  619 */         arrayOfInt2[b2++] = b1;
/*      */       } 
/*  621 */     }  Column[] arrayOfColumn = arrayList.<Column>toArray(new Column[0]);
/*  622 */     if (arrayOfColumn.length == 0)
/*      */     {
/*  624 */       return this.topTableFilter.getTable().getScanIndex(this.session);
/*      */     }
/*  626 */     ArrayList arrayList1 = this.topTableFilter.getTable().getIndexes();
/*  627 */     if (arrayList1 != null) {
/*  628 */       int[] arrayOfInt = this.sort.getSortTypesWithNullOrdering();
/*  629 */       DefaultNullOrdering defaultNullOrdering = this.session.getDatabase().getDefaultNullOrdering();
/*  630 */       label60: for (Index index : arrayList1) {
/*  631 */         if (index.getCreateSQL() == null) {
/*      */           continue;
/*      */         }
/*      */         
/*  635 */         if (index.getIndexType().isHash()) {
/*      */           continue;
/*      */         }
/*  638 */         IndexColumn[] arrayOfIndexColumn = index.getIndexColumns();
/*  639 */         if (arrayOfIndexColumn.length < arrayOfColumn.length) {
/*      */           continue;
/*      */         }
/*  642 */         for (byte b = 0; b < arrayOfColumn.length; b++) {
/*      */ 
/*      */           
/*  645 */           IndexColumn indexColumn = arrayOfIndexColumn[b];
/*  646 */           Column column = arrayOfColumn[b];
/*  647 */           if (indexColumn.column != column) {
/*      */             continue label60;
/*      */           }
/*  650 */           int j = arrayOfInt[arrayOfInt2[b]];
/*  651 */           if (column.isNullable()) { if (defaultNullOrdering
/*  652 */               .addExplicitNullOrdering(indexColumn.sortType) != j) continue label60;  } else if ((indexColumn.sortType & 0x1) != (j & 0x1))
/*      */           { continue label60; }
/*      */         
/*      */         } 
/*      */         
/*  657 */         return index;
/*      */       } 
/*      */     } 
/*  660 */     if (arrayOfColumn.length == 1 && arrayOfColumn[0].getColumnId() == -1) {
/*      */       
/*  662 */       Index index = this.topTableFilter.getTable().getScanIndex(this.session);
/*  663 */       if (index.isRowIdIndex()) {
/*  664 */         return index;
/*      */       }
/*      */     } 
/*  667 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private void queryDistinct(ResultTarget paramResultTarget, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2) {
/*  672 */     if (paramLong2 > 0L && paramLong1 > 0L) {
/*  673 */       paramLong2 += paramLong1;
/*  674 */       if (paramLong2 < 0L)
/*      */       {
/*  676 */         paramLong2 = Long.MAX_VALUE;
/*      */       }
/*      */     } 
/*  679 */     long l = 0L;
/*  680 */     setCurrentRowNumber(0L);
/*  681 */     Index index = this.topTableFilter.getIndex();
/*  682 */     SearchRow searchRow = null;
/*  683 */     int i = index.getColumns()[0].getColumnId();
/*  684 */     if (!paramBoolean2) {
/*  685 */       paramLong1 = 0L;
/*      */     }
/*      */     
/*  688 */     setCurrentRowNumber(++l);
/*  689 */     Cursor cursor = index.findNext(this.session, searchRow, null);
/*  690 */     while (cursor.next()) {
/*      */ 
/*      */       
/*  693 */       SearchRow searchRow1 = cursor.getSearchRow();
/*  694 */       Value value = searchRow1.getValue(i);
/*  695 */       if (searchRow == null) {
/*  696 */         searchRow = index.getRowFactory().createRow();
/*      */       }
/*  698 */       searchRow.setValue(i, value);
/*  699 */       if (paramLong1 > 0L) {
/*  700 */         paramLong1--;
/*      */         continue;
/*      */       } 
/*  703 */       paramResultTarget.addRow(new Value[] { value });
/*  704 */       if ((this.sort == null || this.sortUsingIndex) && paramLong2 > 0L && l >= paramLong2 && !paramBoolean1) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private LazyResult queryFlat(int paramInt, ResultTarget paramResultTarget, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2) {
/*  712 */     if (paramLong2 > 0L && paramLong1 > 0L && !paramBoolean2) {
/*  713 */       paramLong2 += paramLong1;
/*  714 */       if (paramLong2 < 0L)
/*      */       {
/*  716 */         paramLong2 = Long.MAX_VALUE;
/*      */       }
/*      */     } 
/*  719 */     LazyResultQueryFlat lazyResultQueryFlat = new LazyResultQueryFlat(this.expressionArray, paramInt, this.isForUpdate);
/*  720 */     skipOffset(lazyResultQueryFlat, paramLong1, paramBoolean2);
/*  721 */     if (paramResultTarget == null) {
/*  722 */       return lazyResultQueryFlat;
/*      */     }
/*  724 */     if (paramLong2 < 0L || (this.sort != null && !this.sortUsingIndex) || (paramBoolean1 && !paramBoolean2)) {
/*  725 */       paramLong2 = Long.MAX_VALUE;
/*      */     }
/*  727 */     Value[] arrayOfValue = null;
/*  728 */     while (paramResultTarget.getRowCount() < paramLong2 && lazyResultQueryFlat.next()) {
/*  729 */       arrayOfValue = lazyResultQueryFlat.currentRow();
/*  730 */       paramResultTarget.addRow(arrayOfValue);
/*      */     } 
/*  732 */     if (paramLong2 != Long.MAX_VALUE && paramBoolean1 && this.sort != null && arrayOfValue != null) {
/*  733 */       Value[] arrayOfValue1 = arrayOfValue;
/*  734 */       while (lazyResultQueryFlat.next()) {
/*  735 */         arrayOfValue = lazyResultQueryFlat.currentRow();
/*  736 */         if (this.sort.compare(arrayOfValue1, arrayOfValue) != 0) {
/*      */           break;
/*      */         }
/*  739 */         paramResultTarget.addRow(arrayOfValue);
/*      */       } 
/*  741 */       paramResultTarget.limitsWereApplied();
/*      */     } 
/*  743 */     return null;
/*      */   }
/*      */   
/*      */   private static void skipOffset(LazyResultSelect paramLazyResultSelect, long paramLong, boolean paramBoolean) {
/*  747 */     if (paramBoolean) {
/*  748 */       while (paramLong > 0L && paramLazyResultSelect.skip()) {
/*  749 */         paramLong--;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void queryQuick(int paramInt, ResultTarget paramResultTarget, boolean paramBoolean) {
/*  755 */     Value[] arrayOfValue = new Value[paramInt];
/*  756 */     for (byte b = 0; b < paramInt; b++) {
/*  757 */       Expression expression = this.expressions.get(b);
/*  758 */       arrayOfValue[b] = expression.getValue(this.session);
/*      */     } 
/*  760 */     if (!paramBoolean) {
/*  761 */       paramResultTarget.addRow(arrayOfValue);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected ResultInterface queryWithoutCache(long paramLong, ResultTarget paramResultTarget) {
/*  767 */     disableLazyForJoinSubqueries(this.topTableFilter);
/*  768 */     Query.OffsetFetch offsetFetch = getOffsetFetch(paramLong);
/*  769 */     long l1 = offsetFetch.offset;
/*  770 */     long l2 = offsetFetch.fetch;
/*  771 */     boolean bool = offsetFetch.fetchPercent;
/*      */ 
/*      */     
/*  774 */     int i = (this.session.isLazyQueryExecution() && paramResultTarget == null && !this.isForUpdate && !this.isQuickAggregateQuery && l2 != 0L && !bool && !this.withTies && l1 == 0L && isReadOnly()) ? 1 : 0;
/*  775 */     int j = this.expressions.size();
/*  776 */     LocalResult localResult = null;
/*  777 */     if (!i && (paramResultTarget == null || 
/*  778 */       !(this.session.getDatabase().getSettings()).optimizeInsertFromSelect)) {
/*  779 */       localResult = createLocalResult(localResult);
/*      */     }
/*      */     
/*  782 */     boolean bool1 = !bool ? true : false;
/*  783 */     if (this.sort != null && (!this.sortUsingIndex || isAnyDistinct())) {
/*  784 */       localResult = createLocalResult(localResult);
/*  785 */       localResult.setSortOrder(this.sort);
/*  786 */       if (!this.sortUsingIndex) {
/*  787 */         bool1 = false;
/*      */       }
/*      */     } 
/*  790 */     if (this.distinct) {
/*  791 */       if (!this.isDistinctQuery) {
/*  792 */         bool1 = false;
/*  793 */         localResult = createLocalResult(localResult);
/*  794 */         localResult.setDistinct();
/*      */       } 
/*  796 */     } else if (this.distinctExpressions != null) {
/*  797 */       bool1 = false;
/*  798 */       localResult = createLocalResult(localResult);
/*  799 */       localResult.setDistinct(this.distinctIndexes);
/*      */     } 
/*  801 */     if (this.isWindowQuery || (this.isGroupQuery && !this.isGroupSortedQuery)) {
/*  802 */       localResult = createLocalResult(localResult);
/*      */     }
/*  804 */     if (!i && (l2 >= 0L || l1 > 0L)) {
/*  805 */       localResult = createLocalResult(localResult);
/*      */     }
/*  807 */     this.topTableFilter.startQuery(this.session);
/*  808 */     this.topTableFilter.reset();
/*  809 */     this.topTableFilter.lock(this.session);
/*  810 */     ResultTarget resultTarget = (ResultTarget)((localResult != null) ? localResult : paramResultTarget);
/*  811 */     i &= (resultTarget == null) ? 1 : 0;
/*  812 */     LazyResult lazyResult = null;
/*  813 */     if (l2 != 0L) {
/*      */       
/*  815 */       long l = bool ? -1L : l2;
/*  816 */       if (this.isQuickAggregateQuery) {
/*  817 */         queryQuick(j, resultTarget, (bool1 && l1 > 0L));
/*  818 */       } else if (this.isWindowQuery) {
/*  819 */         if (this.isGroupQuery) {
/*  820 */           queryGroupWindow(j, localResult, l1, bool1);
/*      */         } else {
/*  822 */           queryWindow(j, localResult, l1, bool1);
/*      */         } 
/*  824 */       } else if (this.isGroupQuery) {
/*  825 */         if (this.isGroupSortedQuery) {
/*  826 */           lazyResult = queryGroupSorted(j, resultTarget, l1, bool1);
/*      */         } else {
/*  828 */           queryGroup(j, localResult, l1, bool1);
/*      */         } 
/*  830 */       } else if (this.isDistinctQuery) {
/*  831 */         queryDistinct(resultTarget, l1, l, this.withTies, bool1);
/*      */       } else {
/*  833 */         lazyResult = queryFlat(j, resultTarget, l1, l, this.withTies, bool1);
/*      */       } 
/*  835 */       if (bool1) {
/*  836 */         l1 = 0L;
/*      */       }
/*      */     } 
/*  839 */     assert i == ((lazyResult != null) ? 1 : 0) : i;
/*  840 */     if (lazyResult != null) {
/*  841 */       if (l2 > 0L) {
/*  842 */         lazyResult.setLimit(l2);
/*      */       }
/*  844 */       if (this.randomAccessResult) {
/*  845 */         return (ResultInterface)convertToDistinct((ResultInterface)lazyResult);
/*      */       }
/*  847 */       return (ResultInterface)lazyResult;
/*      */     } 
/*      */     
/*  850 */     if (localResult != null) {
/*  851 */       return (ResultInterface)finishResult(localResult, l1, l2, bool, paramResultTarget);
/*      */     }
/*  853 */     return null;
/*      */   }
/*      */   
/*      */   private void disableLazyForJoinSubqueries(TableFilter paramTableFilter) {
/*  857 */     if (this.session.isLazyQueryExecution()) {
/*  858 */       paramTableFilter.visit(paramTableFilter2 -> {
/*      */             if (paramTableFilter2 != paramTableFilter1 && paramTableFilter2.getTable().getTableType() == TableType.VIEW) {
/*      */               ViewIndex viewIndex = (ViewIndex)paramTableFilter2.getIndex();
/*      */               if (viewIndex != null && viewIndex.getQuery() != null) {
/*      */                 viewIndex.getQuery().setNeverLazy(true);
/*      */               }
/*      */             } 
/*      */           });
/*      */     }
/*      */   }
/*      */   
/*      */   private LocalResult createLocalResult(LocalResult paramLocalResult) {
/*  870 */     return (paramLocalResult != null) ? paramLocalResult : new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
/*      */   }
/*      */ 
/*      */   
/*      */   private void expandColumnList() {
/*  875 */     for (int i = 0; i < this.expressions.size(); ) {
/*  876 */       Expression expression = this.expressions.get(i);
/*  877 */       if (!(expression instanceof Wildcard)) {
/*  878 */         i++;
/*      */         continue;
/*      */       } 
/*  881 */       this.expressions.remove(i);
/*  882 */       Wildcard wildcard = (Wildcard)expression;
/*  883 */       String str1 = wildcard.getTableAlias();
/*  884 */       boolean bool = (wildcard.getExceptColumns() != null) ? true : false;
/*  885 */       HashMap<Column, ExpressionColumn> hashMap = null;
/*  886 */       if (str1 == null) {
/*  887 */         if (bool) {
/*  888 */           for (TableFilter tableFilter1 : this.filters) {
/*  889 */             wildcard.mapColumns((ColumnResolver)tableFilter1, 1, 0);
/*      */           }
/*  891 */           hashMap = wildcard.mapExceptColumns();
/*      */         } 
/*  893 */         for (TableFilter tableFilter1 : this.filters)
/*  894 */           i = expandColumnList(tableFilter1, i, false, hashMap); 
/*      */         continue;
/*      */       } 
/*  897 */       Database database = this.session.getDatabase();
/*  898 */       String str2 = wildcard.getSchemaName();
/*  899 */       TableFilter tableFilter = null;
/*  900 */       for (TableFilter tableFilter1 : this.filters) {
/*  901 */         if (database.equalsIdentifiers(str1, tableFilter1.getTableAlias()) && (
/*  902 */           str2 == null || database.equalsIdentifiers(str2, tableFilter1.getSchemaName()))) {
/*  903 */           if (bool) {
/*  904 */             wildcard.mapColumns((ColumnResolver)tableFilter1, 1, 0);
/*  905 */             hashMap = wildcard.mapExceptColumns();
/*      */           } 
/*  907 */           tableFilter = tableFilter1;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*  912 */       if (tableFilter == null) {
/*  913 */         throw DbException.get(42102, str1);
/*      */       }
/*  915 */       i = expandColumnList(tableFilter, i, true, hashMap);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int expandColumnList(TableFilter paramTableFilter, int paramInt, boolean paramBoolean, HashMap<Column, ExpressionColumn> paramHashMap) {
/*  922 */     String str1 = paramTableFilter.getSchemaName();
/*  923 */     String str2 = paramTableFilter.getTableAlias();
/*  924 */     if (paramBoolean) {
/*  925 */       for (Column column : paramTableFilter.getTable().getColumns()) {
/*  926 */         paramInt = addExpandedColumn(paramTableFilter, paramInt, paramHashMap, str1, str2, column);
/*      */       }
/*      */     } else {
/*  929 */       LinkedHashMap linkedHashMap = paramTableFilter.getCommonJoinColumns();
/*  930 */       if (linkedHashMap != null) {
/*  931 */         TableFilter tableFilter = paramTableFilter.getCommonJoinColumnsFilter();
/*  932 */         String str3 = tableFilter.getSchemaName();
/*  933 */         String str4 = tableFilter.getTableAlias();
/*  934 */         for (Map.Entry entry : linkedHashMap.entrySet()) {
/*  935 */           Column column1 = (Column)entry.getKey(), column2 = (Column)entry.getValue();
/*  936 */           if (!paramTableFilter.isCommonJoinColumnToExclude(column2) && (paramHashMap == null || (paramHashMap
/*  937 */             .remove(column1) == null && paramHashMap.remove(column2) == null))) {
/*  938 */             Alias alias; Database database = this.session.getDatabase();
/*      */             
/*  940 */             if (column1 == column2 || (
/*  941 */               DataType.hasTotalOrdering(column1.getType().getValueType()) && 
/*  942 */               DataType.hasTotalOrdering(column2.getType().getValueType()))) {
/*      */               
/*  944 */               ExpressionColumn expressionColumn = new ExpressionColumn(database, str3, str4, tableFilter.getColumnName(column2));
/*      */             
/*      */             }
/*      */             else {
/*      */ 
/*      */               
/*  950 */               alias = new Alias((Expression)new CoalesceFunction(0, new Expression[] { (Expression)new ExpressionColumn(database, str1, str2, paramTableFilter.getColumnName(column1)), (Expression)new ExpressionColumn(database, str3, str4, tableFilter.getColumnName(column2)) }), column1.getName(), true);
/*      */             } 
/*  952 */             this.expressions.add(paramInt++, alias);
/*      */           } 
/*      */         } 
/*      */       } 
/*  956 */       for (Column column : paramTableFilter.getTable().getColumns()) {
/*  957 */         if ((linkedHashMap == null || !linkedHashMap.containsKey(column)) && 
/*  958 */           !paramTableFilter.isCommonJoinColumnToExclude(column)) {
/*  959 */           paramInt = addExpandedColumn(paramTableFilter, paramInt, paramHashMap, str1, str2, column);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  964 */     return paramInt;
/*      */   }
/*      */ 
/*      */   
/*      */   private int addExpandedColumn(TableFilter paramTableFilter, int paramInt, HashMap<Column, ExpressionColumn> paramHashMap, String paramString1, String paramString2, Column paramColumn) {
/*  969 */     if ((paramHashMap == null || paramHashMap.remove(paramColumn) == null) && paramColumn.getVisible()) {
/*  970 */       ExpressionColumn expressionColumn = new ExpressionColumn(this.session.getDatabase(), paramString1, paramString2, paramTableFilter.getColumnName(paramColumn));
/*  971 */       this.expressions.add(paramInt++, expressionColumn);
/*      */     } 
/*  973 */     return paramInt;
/*      */   }
/*      */   
/*      */   public void init() {
/*      */     ArrayList<String> arrayList;
/*  978 */     if (this.checkInit) {
/*  979 */       throw DbException.getInternalError();
/*      */     }
/*  981 */     this.filters.sort(TableFilter.ORDER_IN_FROM_COMPARATOR);
/*  982 */     expandColumnList();
/*  983 */     if ((this.visibleColumnCount = this.expressions.size()) > 16384) {
/*  984 */       throw DbException.get(54011, "16384");
/*      */     }
/*      */     
/*  987 */     if (this.distinctExpressions != null || this.orderList != null || this.group != null) {
/*  988 */       arrayList = new ArrayList(this.visibleColumnCount);
/*  989 */       for (byte b = 0; b < this.visibleColumnCount; b++) {
/*  990 */         Expression expression = this.expressions.get(b);
/*  991 */         expression = expression.getNonAliasExpression();
/*  992 */         arrayList.add(expression.getSQL(0, 2));
/*      */       } 
/*      */     } else {
/*  995 */       arrayList = null;
/*      */     } 
/*  997 */     if (this.distinctExpressions != null) {
/*  998 */       BitSet bitSet = new BitSet();
/*  999 */       for (Expression expression : this.distinctExpressions) {
/* 1000 */         bitSet.set(initExpression(arrayList, expression, false, this.filters));
/*      */       }
/* 1002 */       int i = 0, j = bitSet.cardinality();
/* 1003 */       this.distinctIndexes = new int[j];
/* 1004 */       for (byte b = 0; b < j; b++) {
/* 1005 */         i = bitSet.nextSetBit(i);
/* 1006 */         this.distinctIndexes[b] = i;
/* 1007 */         i++;
/*      */       } 
/*      */     } 
/* 1010 */     if (this.orderList != null) {
/* 1011 */       initOrder(arrayList, isAnyDistinct(), this.filters);
/*      */     }
/* 1013 */     this.resultColumnCount = this.expressions.size();
/* 1014 */     if (this.having != null) {
/* 1015 */       this.expressions.add(this.having);
/* 1016 */       this.havingIndex = this.expressions.size() - 1;
/* 1017 */       this.having = null;
/*      */     } else {
/* 1019 */       this.havingIndex = -1;
/*      */     } 
/* 1021 */     if (this.qualify != null) {
/* 1022 */       this.expressions.add(this.qualify);
/* 1023 */       this.qualifyIndex = this.expressions.size() - 1;
/* 1024 */       this.qualify = null;
/*      */     } else {
/* 1026 */       this.qualifyIndex = -1;
/*      */     } 
/*      */     
/* 1029 */     if (this.withTies && !hasOrder()) {
/* 1030 */       throw DbException.get(90122);
/*      */     }
/*      */     
/* 1033 */     Database database = this.session.getDatabase();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1039 */     if (this.group != null) {
/* 1040 */       int i = this.group.size();
/* 1041 */       int j = arrayList.size();
/* 1042 */       int k = this.expressions.size();
/* 1043 */       if (k > j) {
/* 1044 */         arrayList.ensureCapacity(k);
/* 1045 */         for (int m = j; m < k; m++) {
/* 1046 */           arrayList.add(((Expression)this.expressions.get(m)).getSQL(0, 2));
/*      */         }
/*      */       } 
/* 1049 */       this.groupIndex = new int[i];
/* 1050 */       for (byte b = 0; b < i; b++) {
/* 1051 */         Expression expression = this.group.get(b);
/* 1052 */         String str = expression.getSQL(0, 2);
/* 1053 */         int m = -1; int n;
/* 1054 */         for (n = 0; n < j; n++) {
/* 1055 */           String str1 = arrayList.get(n);
/* 1056 */           if (database.equalsIdentifiers(str1, str)) {
/* 1057 */             m = mergeGroupByExpressions(database, n, arrayList, false);
/*      */             break;
/*      */           } 
/*      */         } 
/* 1061 */         if (m < 0)
/*      */         {
/* 1063 */           for (n = 0; n < j; n++) {
/* 1064 */             Expression expression1 = this.expressions.get(n);
/* 1065 */             if (database.equalsIdentifiers(str, expression1.getAlias(this.session, n))) {
/* 1066 */               m = mergeGroupByExpressions(database, n, arrayList, true);
/*      */               break;
/*      */             } 
/* 1069 */             str = expression.getAlias(this.session, n);
/* 1070 */             if (database.equalsIdentifiers(str, expression1.getAlias(this.session, n))) {
/* 1071 */               m = mergeGroupByExpressions(database, n, arrayList, true);
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         }
/* 1076 */         if (m < 0) {
/* 1077 */           n = this.expressions.size();
/* 1078 */           this.groupIndex[b] = n;
/* 1079 */           this.expressions.add(expression);
/*      */         } else {
/* 1081 */           this.groupIndex[b] = m;
/*      */         } 
/*      */       } 
/* 1084 */       if (this.groupByCopies != null) {
/* 1085 */         int arrayOfInt[] = this.groupByCopies, m = arrayOfInt.length; byte b1 = 0; while (true) { if (b1 < m) { int n = arrayOfInt[b1];
/* 1086 */             if (n >= 0)
/*      */               break;  b1++;
/*      */             continue; }
/*      */           
/* 1090 */           this.groupByCopies = null; break; }
/*      */       
/* 1092 */       }  this.groupByExpression = new boolean[this.expressions.size()];
/* 1093 */       for (int m : this.groupIndex) {
/* 1094 */         this.groupByExpression[m] = true;
/*      */       }
/* 1096 */       this.group = null;
/*      */     } 
/*      */     
/* 1099 */     for (TableFilter tableFilter : this.filters) {
/* 1100 */       mapColumns((ColumnResolver)tableFilter, 0);
/*      */     }
/* 1102 */     mapCondition(this.havingIndex);
/* 1103 */     mapCondition(this.qualifyIndex);
/* 1104 */     this.checkInit = true;
/*      */   }
/*      */   
/*      */   private void mapCondition(int paramInt) {
/* 1108 */     if (paramInt >= 0) {
/* 1109 */       Expression expression = this.expressions.get(paramInt);
/* 1110 */       SelectListColumnResolver selectListColumnResolver = new SelectListColumnResolver(this);
/* 1111 */       expression.mapColumns(selectListColumnResolver, 0, 0);
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
/*      */   private int mergeGroupByExpressions(Database paramDatabase, int paramInt, ArrayList<String> paramArrayList, boolean paramBoolean) {
/* 1126 */     if (this.groupByCopies != null) {
/* 1127 */       int k = this.groupByCopies[paramInt];
/* 1128 */       if (k >= 0)
/* 1129 */         return k; 
/* 1130 */       if (k == -2) {
/* 1131 */         return paramInt;
/*      */       }
/*      */     } else {
/* 1134 */       this.groupByCopies = new int[paramArrayList.size()];
/* 1135 */       Arrays.fill(this.groupByCopies, -1);
/*      */     } 
/* 1137 */     String str = paramArrayList.get(paramInt);
/* 1138 */     if (paramBoolean)
/*      */     {
/*      */ 
/*      */ 
/*      */       
/* 1143 */       for (byte b = 0; b < paramInt; b++) {
/* 1144 */         if (paramDatabase.equalsIdentifiers(str, paramArrayList.get(b))) {
/* 1145 */           paramInt = b;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     }
/* 1150 */     int i = paramArrayList.size();
/* 1151 */     for (int j = paramInt + 1; j < i; j++) {
/* 1152 */       if (paramDatabase.equalsIdentifiers(str, paramArrayList.get(j))) {
/* 1153 */         this.groupByCopies[j] = paramInt;
/*      */       }
/*      */     } 
/* 1156 */     this.groupByCopies[paramInt] = -2;
/* 1157 */     return paramInt;
/*      */   }
/*      */ 
/*      */   
/*      */   public void prepare() {
/* 1162 */     if (this.isPrepared) {
/*      */       return;
/*      */     }
/*      */     
/* 1166 */     if (!this.checkInit) {
/* 1167 */       throw DbException.getInternalError("not initialized");
/*      */     }
/* 1169 */     if (this.orderList != null) {
/* 1170 */       prepareOrder(this.orderList, this.expressions.size());
/*      */     }
/* 1172 */     Mode.ExpressionNames expressionNames = (this.session.getMode()).expressionNames;
/* 1173 */     if (expressionNames == Mode.ExpressionNames.ORIGINAL_SQL || expressionNames == Mode.ExpressionNames.POSTGRESQL_STYLE) {
/* 1174 */       optimizeExpressionsAndPreserveAliases();
/*      */     } else {
/* 1176 */       for (byte b = 0; b < this.expressions.size(); b++) {
/* 1177 */         this.expressions.set(b, ((Expression)this.expressions.get(b)).optimize(this.session));
/*      */       }
/*      */     } 
/* 1180 */     if (this.sort != null) {
/* 1181 */       cleanupOrder();
/*      */     }
/* 1183 */     if (this.condition != null) {
/* 1184 */       this.condition = this.condition.optimizeCondition(this.session);
/* 1185 */       if (this.condition != null) {
/* 1186 */         for (TableFilter tableFilter : this.filters) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1194 */           if (!tableFilter.isJoinOuter() && !tableFilter.isJoinOuterIndirect()) {
/* 1195 */             this.condition.createIndexConditions(this.session, tableFilter);
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/* 1200 */     if (this.isGroupQuery && this.groupIndex == null && this.havingIndex < 0 && this.qualifyIndex < 0 && this.condition == null && this.filters
/* 1201 */       .size() == 1) {
/* 1202 */       this.isQuickAggregateQuery = isEverything(ExpressionVisitor.getOptimizableVisitor(((TableFilter)this.filters.get(0)).getTable()));
/*      */     }
/* 1204 */     this.cost = preparePlan(this.session.isParsingCreateView());
/* 1205 */     if (this.distinct && (this.session.getDatabase().getSettings()).optimizeDistinct && !this.isGroupQuery && this.filters
/* 1206 */       .size() == 1 && this.expressions
/* 1207 */       .size() == 1 && this.condition == null) {
/* 1208 */       Expression expression = this.expressions.get(0);
/* 1209 */       expression = expression.getNonAliasExpression();
/* 1210 */       if (expression instanceof ExpressionColumn) {
/* 1211 */         Column column = ((ExpressionColumn)expression).getColumn();
/* 1212 */         int i = column.getSelectivity();
/*      */         
/* 1214 */         Index index = this.topTableFilter.getTable().getIndexForColumn(column, false, true);
/* 1215 */         if (index != null && i != 50 && i < 20) {
/*      */ 
/*      */           
/* 1218 */           Index index1 = this.topTableFilter.getIndex();
/*      */           
/* 1220 */           if (index1 == null || index1.getIndexType().isScan() || index == index1) {
/* 1221 */             this.topTableFilter.setIndex(index);
/* 1222 */             this.isDistinctQuery = true;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 1227 */     if (this.sort != null && !this.isQuickAggregateQuery && !this.isGroupQuery) {
/* 1228 */       Index index1 = getSortIndex();
/* 1229 */       Index index2 = this.topTableFilter.getIndex();
/* 1230 */       if (index1 != null && index2 != null) {
/* 1231 */         if (index2.getIndexType().isScan() || index2 == index1) {
/* 1232 */           this.topTableFilter.setIndex(index1);
/* 1233 */           if (!this.topTableFilter.hasInComparisons())
/*      */           {
/*      */             
/* 1236 */             this.sortUsingIndex = true;
/*      */           }
/* 1238 */         } else if (index1.getIndexColumns() != null && (index1
/* 1239 */           .getIndexColumns()).length >= (index2
/* 1240 */           .getIndexColumns()).length) {
/* 1241 */           IndexColumn[] arrayOfIndexColumn1 = index1.getIndexColumns();
/* 1242 */           IndexColumn[] arrayOfIndexColumn2 = index2.getIndexColumns();
/* 1243 */           boolean bool = false;
/* 1244 */           for (byte b = 0; b < arrayOfIndexColumn2.length; b++) {
/* 1245 */             if ((arrayOfIndexColumn1[b]).column != (arrayOfIndexColumn2[b]).column) {
/* 1246 */               bool = false;
/*      */               break;
/*      */             } 
/* 1249 */             if ((arrayOfIndexColumn1[b]).sortType != (arrayOfIndexColumn2[b]).sortType) {
/* 1250 */               bool = true;
/*      */             }
/*      */           } 
/* 1253 */           if (bool) {
/* 1254 */             this.topTableFilter.setIndex(index1);
/* 1255 */             this.sortUsingIndex = true;
/*      */           } 
/*      */         } 
/*      */       }
/* 1259 */       if (this.sortUsingIndex && this.isForUpdate && !this.topTableFilter.getIndex().isRowIdIndex()) {
/* 1260 */         this.sortUsingIndex = false;
/*      */       }
/*      */     } 
/* 1263 */     if (!this.isQuickAggregateQuery && this.isGroupQuery) {
/* 1264 */       Index index = getGroupSortedIndex();
/* 1265 */       if (index != null) {
/* 1266 */         Index index1 = this.topTableFilter.getIndex();
/* 1267 */         if (index1 != null && (index1.getIndexType().isScan() || index1 == index)) {
/* 1268 */           this.topTableFilter.setIndex(index);
/* 1269 */           this.isGroupSortedQuery = true;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1273 */     this.expressionArray = this.expressions.<Expression>toArray(new Expression[0]);
/* 1274 */     this.isPrepared = true;
/*      */   }
/*      */   
/*      */   private void optimizeExpressionsAndPreserveAliases() {
/* 1278 */     for (byte b = 0; b < this.expressions.size(); b++) {
/* 1279 */       Alias alias; Expression expression = this.expressions.get(b);
/* 1280 */       String str = expression.getAlias(this.session, b);
/* 1281 */       expression = expression.optimize(this.session);
/* 1282 */       if (!expression.getAlias(this.session, b).equals(str)) {
/* 1283 */         alias = new Alias(expression, str, true);
/*      */       }
/* 1285 */       this.expressions.set(b, alias);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public double getCost() {
/* 1291 */     return this.cost;
/*      */   }
/*      */ 
/*      */   
/*      */   public HashSet<Table> getTables() {
/* 1296 */     HashSet<Table> hashSet = new HashSet();
/* 1297 */     for (TableFilter tableFilter : this.filters) {
/* 1298 */       hashSet.add(tableFilter.getTable());
/*      */     }
/* 1300 */     return hashSet;
/*      */   }
/*      */ 
/*      */   
/*      */   public void fireBeforeSelectTriggers() {
/* 1305 */     for (TableFilter tableFilter : this.filters) {
/* 1306 */       tableFilter.getTable().fire(this.session, 8, true);
/*      */     }
/*      */   }
/*      */   
/*      */   private double preparePlan(boolean paramBoolean) {
/* 1311 */     TableFilter[] arrayOfTableFilter = this.topFilters.<TableFilter>toArray(new TableFilter[0]);
/* 1312 */     for (TableFilter tableFilter : arrayOfTableFilter) {
/* 1313 */       tableFilter.createIndexConditions();
/* 1314 */       tableFilter.setFullCondition(this.condition);
/*      */     } 
/*      */     
/* 1317 */     Optimizer optimizer = new Optimizer(arrayOfTableFilter, this.condition, this.session);
/* 1318 */     optimizer.optimize(paramBoolean);
/* 1319 */     this.topTableFilter = optimizer.getTopFilter();
/* 1320 */     double d = optimizer.getCost();
/*      */     
/* 1322 */     setEvaluatableRecursive(this.topTableFilter);
/*      */     
/* 1324 */     if (!paramBoolean) {
/* 1325 */       this.topTableFilter.prepare();
/*      */     }
/* 1327 */     return d;
/*      */   }
/*      */   
/*      */   private void setEvaluatableRecursive(TableFilter paramTableFilter) {
/* 1331 */     for (; paramTableFilter != null; paramTableFilter = paramTableFilter.getJoin()) {
/* 1332 */       paramTableFilter.setEvaluatable(paramTableFilter, true);
/* 1333 */       if (this.condition != null) {
/* 1334 */         this.condition.setEvaluatable(paramTableFilter, true);
/*      */       }
/* 1336 */       TableFilter tableFilter = paramTableFilter.getNestedJoin();
/* 1337 */       if (tableFilter != null) {
/* 1338 */         setEvaluatableRecursive(tableFilter);
/*      */       }
/* 1340 */       Expression expression = paramTableFilter.getJoinCondition();
/* 1341 */       if (expression != null && 
/* 1342 */         !expression.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
/*      */         
/* 1344 */         expression = expression.optimize(this.session);
/* 1345 */         if (!paramTableFilter.isJoinOuter() && !paramTableFilter.isJoinOuterIndirect()) {
/* 1346 */           paramTableFilter.removeJoinCondition();
/* 1347 */           addCondition(expression);
/*      */         } 
/*      */       } 
/*      */       
/* 1351 */       expression = paramTableFilter.getFilterCondition();
/* 1352 */       if (expression != null && 
/* 1353 */         !expression.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
/* 1354 */         paramTableFilter.removeFilterCondition();
/* 1355 */         addCondition(expression);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1360 */       for (Expression expression1 : this.expressions) {
/* 1361 */         expression1.setEvaluatable(paramTableFilter, true);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPlanSQL(int paramInt) {
/* 1371 */     Expression[] arrayOfExpression = this.expressions.<Expression>toArray(new Expression[0]);
/* 1372 */     StringBuilder stringBuilder = new StringBuilder();
/* 1373 */     for (TableFilter tableFilter : this.topFilters) {
/* 1374 */       Table table = tableFilter.getTable();
/* 1375 */       TableView tableView = (table instanceof TableView) ? (TableView)table : null;
/* 1376 */       if (tableView != null && tableView.isRecursive() && tableView.isTableExpression()) {
/*      */         
/* 1378 */         if (!tableView.isTemporary()) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         
/* 1383 */         stringBuilder.append("WITH RECURSIVE ");
/* 1384 */         table.getSchema().getSQL(stringBuilder, paramInt).append('.');
/* 1385 */         ParserUtil.quoteIdentifier(stringBuilder, table.getName(), paramInt).append('(');
/* 1386 */         Column.writeColumns(stringBuilder, table.getColumns(), paramInt);
/* 1387 */         stringBuilder.append(") AS ");
/* 1388 */         table.getSQL(stringBuilder, paramInt).append('\n');
/*      */       } 
/*      */     } 
/*      */     
/* 1392 */     if (this.isExplicitTable) {
/* 1393 */       stringBuilder.append("TABLE ");
/* 1394 */       ((TableFilter)this.filters.get(0)).getPlanSQL(stringBuilder, false, paramInt);
/*      */     } else {
/* 1396 */       stringBuilder.append("SELECT");
/* 1397 */       if (isAnyDistinct()) {
/* 1398 */         stringBuilder.append(" DISTINCT");
/* 1399 */         if (this.distinctExpressions != null) {
/* 1400 */           Expression.writeExpressions(stringBuilder.append(" ON("), this.distinctExpressions, paramInt).append(')');
/*      */         }
/*      */       } 
/* 1403 */       for (byte b = 0; b < this.visibleColumnCount; b++) {
/* 1404 */         if (b > 0) {
/* 1405 */           stringBuilder.append(',');
/*      */         }
/* 1407 */         stringBuilder.append('\n');
/* 1408 */         StringUtils.indent(stringBuilder, arrayOfExpression[b].getSQL(paramInt, 2), 4, false);
/*      */       } 
/* 1410 */       TableFilter tableFilter = this.topTableFilter;
/* 1411 */       if (tableFilter == null) {
/* 1412 */         int i = this.topFilters.size();
/* 1413 */         if (i != 1 || !((TableFilter)this.topFilters.get(0)).isNoFromClauseFilter()) {
/* 1414 */           stringBuilder.append("\nFROM ");
/* 1415 */           boolean bool = false;
/* 1416 */           for (byte b1 = 0; b1 < i; b1++) {
/* 1417 */             bool = getPlanFromFilter(stringBuilder, paramInt, this.topFilters.get(b1), bool);
/*      */           }
/*      */         } 
/* 1420 */       } else if (!tableFilter.isNoFromClauseFilter()) {
/* 1421 */         getPlanFromFilter(stringBuilder.append("\nFROM "), paramInt, tableFilter, false);
/*      */       } 
/* 1423 */       if (this.condition != null) {
/* 1424 */         getFilterSQL(stringBuilder, "\nWHERE ", this.condition, paramInt);
/*      */       }
/* 1426 */       if (this.groupIndex != null) {
/* 1427 */         stringBuilder.append("\nGROUP BY "); byte b1; int i;
/* 1428 */         for (b1 = 0, i = this.groupIndex.length; b1 < i; b1++) {
/* 1429 */           if (b1 > 0) {
/* 1430 */             stringBuilder.append(", ");
/*      */           }
/* 1432 */           arrayOfExpression[this.groupIndex[b1]].getNonAliasExpression().getUnenclosedSQL(stringBuilder, paramInt);
/*      */         } 
/* 1434 */       } else if (this.group != null) {
/* 1435 */         stringBuilder.append("\nGROUP BY "); byte b1; int i;
/* 1436 */         for (b1 = 0, i = this.group.size(); b1 < i; b1++) {
/* 1437 */           if (b1 > 0) {
/* 1438 */             stringBuilder.append(", ");
/*      */           }
/* 1440 */           ((Expression)this.group.get(b1)).getUnenclosedSQL(stringBuilder, paramInt);
/*      */         } 
/* 1442 */       } else if (this.isGroupQuery && this.having == null && this.havingIndex < 0) {
/* 1443 */         byte b1 = 0; while (true) { if (b1 < this.visibleColumnCount) {
/* 1444 */             if (containsAggregate(arrayOfExpression[b1]))
/*      */               break;  b1++;
/*      */             continue;
/*      */           } 
/* 1448 */           stringBuilder.append("\nGROUP BY ()"); break; }
/*      */       
/* 1450 */       }  getFilterSQL(stringBuilder, "\nHAVING ", arrayOfExpression, this.having, this.havingIndex, paramInt);
/* 1451 */       getFilterSQL(stringBuilder, "\nQUALIFY ", arrayOfExpression, this.qualify, this.qualifyIndex, paramInt);
/*      */     } 
/* 1453 */     appendEndOfQueryToSQL(stringBuilder, paramInt, arrayOfExpression);
/* 1454 */     if (this.isForUpdate) {
/* 1455 */       stringBuilder.append("\nFOR UPDATE");
/*      */     }
/* 1457 */     if ((paramInt & 0x8) != 0) {
/* 1458 */       if (this.isQuickAggregateQuery) {
/* 1459 */         stringBuilder.append("\n/* direct lookup */");
/*      */       }
/* 1461 */       if (this.isDistinctQuery) {
/* 1462 */         stringBuilder.append("\n/* distinct */");
/*      */       }
/* 1464 */       if (this.sortUsingIndex) {
/* 1465 */         stringBuilder.append("\n/* index sorted */");
/*      */       }
/* 1467 */       if (this.isGroupQuery && 
/* 1468 */         this.isGroupSortedQuery) {
/* 1469 */         stringBuilder.append("\n/* group sorted */");
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1474 */     return stringBuilder.toString();
/*      */   }
/*      */   
/*      */   private static boolean getPlanFromFilter(StringBuilder paramStringBuilder, int paramInt, TableFilter paramTableFilter, boolean paramBoolean) {
/*      */     while (true) {
/* 1479 */       if (paramBoolean) {
/* 1480 */         paramStringBuilder.append('\n');
/*      */       }
/* 1482 */       paramTableFilter.getPlanSQL(paramStringBuilder, paramBoolean, paramInt);
/* 1483 */       paramBoolean = true;
/* 1484 */       if ((paramTableFilter = paramTableFilter.getJoin()) == null)
/* 1485 */         return paramBoolean; 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void getFilterSQL(StringBuilder paramStringBuilder, String paramString, Expression[] paramArrayOfExpression, Expression paramExpression, int paramInt1, int paramInt2) {
/* 1490 */     if (paramExpression != null) {
/* 1491 */       getFilterSQL(paramStringBuilder, paramString, paramExpression, paramInt2);
/* 1492 */     } else if (paramInt1 >= 0) {
/* 1493 */       getFilterSQL(paramStringBuilder, paramString, paramArrayOfExpression[paramInt1], paramInt2);
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void getFilterSQL(StringBuilder paramStringBuilder, String paramString, Expression paramExpression, int paramInt) {
/* 1498 */     paramExpression.getUnenclosedSQL(paramStringBuilder.append(paramString), paramInt);
/*      */   }
/*      */   
/*      */   private static boolean containsAggregate(Expression paramExpression) {
/* 1502 */     if (paramExpression instanceof DataAnalysisOperation && (
/* 1503 */       (DataAnalysisOperation)paramExpression).isAggregate())
/* 1504 */       return true; 
/*      */     byte b;
/*      */     int i;
/* 1507 */     for (b = 0, i = paramExpression.getSubexpressionCount(); b < i; b++) {
/* 1508 */       if (containsAggregate(paramExpression.getSubexpression(b))) {
/* 1509 */         return true;
/*      */       }
/*      */     } 
/* 1512 */     return false;
/*      */   }
/*      */   
/*      */   public void setHaving(Expression paramExpression) {
/* 1516 */     this.having = paramExpression;
/*      */   }
/*      */   
/*      */   public Expression getHaving() {
/* 1520 */     return this.having;
/*      */   }
/*      */   
/*      */   public void setQualify(Expression paramExpression) {
/* 1524 */     this.qualify = paramExpression;
/*      */   }
/*      */   
/*      */   public Expression getQualify() {
/* 1528 */     return this.qualify;
/*      */   }
/*      */   
/*      */   public TableFilter getTopTableFilter() {
/* 1532 */     return this.topTableFilter;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setForUpdate(boolean paramBoolean) {
/* 1537 */     if (paramBoolean && (isAnyDistinct() || this.isGroupQuery)) {
/* 1538 */       throw DbException.get(90145);
/*      */     }
/* 1540 */     this.isForUpdate = paramBoolean;
/*      */   }
/*      */ 
/*      */   
/*      */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt) {
/* 1545 */     for (Expression expression : this.expressions) {
/* 1546 */       expression.mapColumns(paramColumnResolver, paramInt, 0);
/*      */     }
/* 1548 */     if (this.condition != null) {
/* 1549 */       this.condition.mapColumns(paramColumnResolver, paramInt, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 1555 */     for (Expression expression : this.expressions) {
/* 1556 */       expression.setEvaluatable(paramTableFilter, paramBoolean);
/*      */     }
/* 1558 */     if (this.condition != null) {
/* 1559 */       this.condition.setEvaluatable(paramTableFilter, paramBoolean);
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
/*      */   public boolean isQuickAggregateQuery() {
/* 1571 */     return this.isQuickAggregateQuery;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isGroupQuery() {
/* 1580 */     return this.isGroupQuery;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWindowQuery() {
/* 1589 */     return this.isWindowQuery;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isGroupWindowStage2() {
/* 1599 */     return this.isGroupWindowStage2;
/*      */   }
/*      */   
/*      */   public void addGlobalCondition(Parameter paramParameter, int paramInt1, int paramInt2) {
/*      */     Comparison comparison;
/* 1604 */     addParameter(paramParameter);
/*      */     
/* 1606 */     Expression expression2 = this.expressions.get(paramInt1);
/* 1607 */     expression2 = expression2.getNonAliasExpression();
/* 1608 */     if (expression2.isEverything(ExpressionVisitor.QUERY_COMPARABLE_VISITOR)) {
/* 1609 */       comparison = new Comparison(paramInt2, expression2, (Expression)paramParameter, false);
/*      */     }
/*      */     else {
/*      */       
/* 1613 */       comparison = new Comparison(6, (Expression)paramParameter, (Expression)paramParameter, false);
/*      */     } 
/* 1615 */     Expression expression1 = comparison.optimize(this.session);
/* 1616 */     if (this.isWindowQuery) {
/* 1617 */       this.qualify = addGlobalCondition(this.qualify, expression1);
/* 1618 */     } else if (this.isGroupQuery) {
/* 1619 */       for (byte b = 0; this.groupIndex != null && b < this.groupIndex.length; b++) {
/* 1620 */         if (this.groupIndex[b] == paramInt1) {
/* 1621 */           this.condition = addGlobalCondition(this.condition, expression1);
/*      */           return;
/*      */         } 
/*      */       } 
/* 1625 */       if (this.havingIndex >= 0) {
/* 1626 */         this.having = this.expressions.get(this.havingIndex);
/*      */       }
/* 1628 */       this.having = addGlobalCondition(this.having, expression1);
/*      */     } else {
/* 1630 */       this.condition = addGlobalCondition(this.condition, expression1);
/*      */     } 
/*      */   } private static Expression addGlobalCondition(Expression paramExpression1, Expression paramExpression2) {
/*      */     Expression expression1;
/*      */     Expression expression2;
/* 1635 */     if (!(paramExpression1 instanceof ConditionLocalAndGlobal)) {
/* 1636 */       return (Expression)new ConditionLocalAndGlobal(paramExpression1, paramExpression2);
/*      */     }
/*      */     
/* 1639 */     if (paramExpression1.getSubexpressionCount() == 1) {
/* 1640 */       expression1 = null;
/* 1641 */       expression2 = paramExpression1.getSubexpression(0);
/*      */     } else {
/* 1643 */       expression1 = paramExpression1.getSubexpression(0);
/* 1644 */       expression2 = paramExpression1.getSubexpression(1);
/*      */     } 
/* 1646 */     return (Expression)new ConditionLocalAndGlobal(expression1, (Expression)new ConditionAndOr(0, expression2, paramExpression2));
/*      */   }
/*      */ 
/*      */   
/*      */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 1651 */     for (Expression expression : this.expressions) {
/* 1652 */       expression.updateAggregate(paramSessionLocal, paramInt);
/*      */     }
/* 1654 */     if (this.condition != null) {
/* 1655 */       this.condition.updateAggregate(paramSessionLocal, paramInt);
/*      */     }
/* 1657 */     if (this.having != null) {
/* 1658 */       this.having.updateAggregate(paramSessionLocal, paramInt);
/*      */     }
/* 1660 */     if (this.qualify != null) {
/* 1661 */       this.qualify.updateAggregate(paramSessionLocal, paramInt);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 1667 */     switch (paramExpressionVisitor.getType()) {
/*      */       case 2:
/* 1669 */         if (this.isForUpdate) {
/* 1670 */           return false;
/*      */         }
/* 1672 */         for (TableFilter tableFilter : this.filters) {
/* 1673 */           if (!tableFilter.getTable().isDeterministic()) {
/* 1674 */             return false;
/*      */           }
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 4:
/* 1680 */         for (TableFilter tableFilter : this.filters) {
/* 1681 */           long l = tableFilter.getTable().getMaxDataModificationId();
/* 1682 */           paramExpressionVisitor.addDataModificationId(l);
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 3:
/* 1687 */         if (!(this.session.getDatabase().getSettings()).optimizeEvaluatableSubqueries) {
/* 1688 */           return false;
/*      */         }
/*      */         break;
/*      */       
/*      */       case 7:
/* 1693 */         for (TableFilter tableFilter : this.filters) {
/* 1694 */           Table table = tableFilter.getTable();
/* 1695 */           paramExpressionVisitor.addDependency((DbObject)table);
/* 1696 */           table.addDependencies(paramExpressionVisitor.getDependencies());
/*      */         } 
/*      */         break;
/*      */     } 
/*      */ 
/*      */     
/* 1702 */     ExpressionVisitor expressionVisitor = paramExpressionVisitor.incrementQueryLevel(1);
/* 1703 */     for (Expression expression : this.expressions) {
/* 1704 */       if (!expression.isEverything(expressionVisitor)) {
/* 1705 */         return false;
/*      */       }
/*      */     } 
/* 1708 */     if (this.condition != null && !this.condition.isEverything(expressionVisitor)) {
/* 1709 */       return false;
/*      */     }
/* 1711 */     if (this.having != null && !this.having.isEverything(expressionVisitor)) {
/* 1712 */       return false;
/*      */     }
/* 1714 */     if (this.qualify != null && !this.qualify.isEverything(expressionVisitor)) {
/* 1715 */       return false;
/*      */     }
/* 1717 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCacheable() {
/* 1723 */     return !this.isForUpdate;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean allowGlobalConditions() {
/* 1728 */     return (this.offsetExpr == null && this.fetchExpr == null && this.distinctExpressions == null);
/*      */   }
/*      */   
/*      */   public SortOrder getSortOrder() {
/* 1732 */     return this.sort;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Select getParentSelect() {
/* 1741 */     return this.parentSelect;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isConstantQuery() {
/* 1746 */     if (!super.isConstantQuery() || this.distinctExpressions != null || this.condition != null || this.isGroupQuery || this.isWindowQuery || 
/* 1747 */       !isNoFromClause()) {
/* 1748 */       return false;
/*      */     }
/* 1750 */     for (byte b = 0; b < this.visibleColumnCount; b++) {
/* 1751 */       if (!((Expression)this.expressions.get(b)).isConstant()) {
/* 1752 */         return false;
/*      */       }
/*      */     } 
/* 1755 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public Expression getIfSingleRow() {
/* 1760 */     if (this.offsetExpr != null || this.fetchExpr != null || this.condition != null || this.isGroupQuery || this.isWindowQuery || 
/* 1761 */       !isNoFromClause()) {
/* 1762 */       return null;
/*      */     }
/* 1764 */     if (this.visibleColumnCount == 1) {
/* 1765 */       return this.expressions.get(0);
/*      */     }
/* 1767 */     Expression[] arrayOfExpression = new Expression[this.visibleColumnCount];
/* 1768 */     for (byte b = 0; b < this.visibleColumnCount; b++) {
/* 1769 */       arrayOfExpression[b] = this.expressions.get(b);
/*      */     }
/* 1771 */     return (Expression)new ExpressionList(arrayOfExpression, false);
/*      */   }
/*      */   
/*      */   private boolean isNoFromClause() {
/* 1775 */     if (this.topTableFilter != null)
/* 1776 */       return this.topTableFilter.isNoFromClauseFilter(); 
/* 1777 */     if (this.topFilters.size() == 1) {
/* 1778 */       return ((TableFilter)this.topFilters.get(0)).isNoFromClauseFilter();
/*      */     }
/* 1780 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private abstract class LazyResultSelect
/*      */     extends LazyResult
/*      */   {
/*      */     long rowNumber;
/*      */     
/*      */     int columnCount;
/*      */     
/*      */     LazyResultSelect(Expression[] param1ArrayOfExpression, int param1Int) {
/* 1792 */       super(Select.this.getSession(), param1ArrayOfExpression);
/* 1793 */       this.columnCount = param1Int;
/* 1794 */       Select.this.setCurrentRowNumber(0L);
/*      */     }
/*      */ 
/*      */     
/*      */     public final int getVisibleColumnCount() {
/* 1799 */       return Select.this.visibleColumnCount;
/*      */     }
/*      */ 
/*      */     
/*      */     public void reset() {
/* 1804 */       super.reset();
/* 1805 */       Select.this.topTableFilter.reset();
/* 1806 */       Select.this.setCurrentRowNumber(0L);
/* 1807 */       this.rowNumber = 0L;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private final class LazyResultQueryFlat
/*      */     extends LazyResultSelect
/*      */   {
/*      */     private boolean forUpdate;
/*      */ 
/*      */     
/*      */     LazyResultQueryFlat(Expression[] param1ArrayOfExpression, int param1Int, boolean param1Boolean) {
/* 1819 */       super(param1ArrayOfExpression, param1Int);
/* 1820 */       this.forUpdate = param1Boolean;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Value[] fetchNextRow() {
/* 1825 */       while (Select.this.topTableFilter.next()) {
/* 1826 */         Select.this.setCurrentRowNumber(this.rowNumber + 1L);
/*      */         
/* 1828 */         if (this.forUpdate ? Select.this.isConditionMetForUpdate() : Select.this.isConditionMet()) {
/* 1829 */           this.rowNumber++;
/* 1830 */           Value[] arrayOfValue = new Value[this.columnCount];
/* 1831 */           for (byte b = 0; b < this.columnCount; b++) {
/* 1832 */             Expression expression = Select.this.expressions.get(b);
/* 1833 */             arrayOfValue[b] = expression.getValue(Select.this.getSession());
/*      */           } 
/* 1835 */           return arrayOfValue;
/*      */         } 
/*      */       } 
/* 1838 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean skipNextRow() {
/* 1843 */       while (Select.this.topTableFilter.next()) {
/* 1844 */         Select.this.setCurrentRowNumber(this.rowNumber + 1L);
/*      */         
/* 1846 */         if (Select.this.isConditionMet()) {
/* 1847 */           this.rowNumber++;
/* 1848 */           return true;
/*      */         } 
/*      */       } 
/* 1851 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final class LazyResultGroupSorted
/*      */     extends LazyResultSelect
/*      */   {
/*      */     private Value[] previousKeyValues;
/*      */ 
/*      */     
/*      */     LazyResultGroupSorted(Expression[] param1ArrayOfExpression, int param1Int) {
/* 1864 */       super(param1ArrayOfExpression, param1Int);
/* 1865 */       if (Select.this.groupData == null) {
/* 1866 */         Select.this.setGroupData(SelectGroups.getInstance(Select.this.getSession(), Select.this.expressions, Select.this.isGroupQuery, Select.this.groupIndex));
/*      */       } else {
/*      */         
/* 1869 */         Select.this.updateAgg(param1Int, 0);
/* 1870 */         Select.this.groupData.resetLazy();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void reset() {
/* 1876 */       super.reset();
/* 1877 */       Select.this.groupData.resetLazy();
/* 1878 */       this.previousKeyValues = null;
/*      */     }
/*      */ 
/*      */     
/*      */     protected Value[] fetchNextRow() {
/* 1883 */       while (Select.this.topTableFilter.next()) {
/* 1884 */         Select.this.setCurrentRowNumber(this.rowNumber + 1L);
/* 1885 */         if (Select.this.isConditionMet()) {
/* 1886 */           this.rowNumber++;
/* 1887 */           int i = Select.this.groupIndex.length;
/* 1888 */           Value[] arrayOfValue1 = new Value[i];
/*      */           
/* 1890 */           for (byte b = 0; b < i; b++) {
/* 1891 */             int j = Select.this.groupIndex[b];
/* 1892 */             Expression expression = Select.this.expressions.get(j);
/* 1893 */             arrayOfValue1[b] = expression.getValue(Select.this.getSession());
/*      */           } 
/*      */           
/* 1896 */           Value[] arrayOfValue2 = null;
/* 1897 */           if (this.previousKeyValues == null) {
/* 1898 */             this.previousKeyValues = arrayOfValue1;
/* 1899 */             Select.this.groupData.nextLazyGroup();
/*      */           } else {
/* 1901 */             SessionLocal sessionLocal = Select.this.getSession();
/* 1902 */             for (byte b1 = 0; b1 < i; b1++) {
/* 1903 */               if (sessionLocal.compare(this.previousKeyValues[b1], arrayOfValue1[b1]) != 0) {
/* 1904 */                 arrayOfValue2 = Select.this.createGroupSortedRow(this.previousKeyValues, this.columnCount);
/* 1905 */                 this.previousKeyValues = arrayOfValue1;
/* 1906 */                 Select.this.groupData.nextLazyGroup();
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           } 
/* 1911 */           Select.this.groupData.nextLazyRow();
/* 1912 */           Select.this.updateAgg(this.columnCount, 1);
/* 1913 */           if (arrayOfValue2 != null) {
/* 1914 */             return arrayOfValue2;
/*      */           }
/*      */         } 
/*      */       } 
/* 1918 */       Value[] arrayOfValue = null;
/* 1919 */       if (this.previousKeyValues != null) {
/* 1920 */         arrayOfValue = Select.this.createGroupSortedRow(this.previousKeyValues, this.columnCount);
/* 1921 */         this.previousKeyValues = null;
/*      */       } 
/* 1923 */       return arrayOfValue;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\Select.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */