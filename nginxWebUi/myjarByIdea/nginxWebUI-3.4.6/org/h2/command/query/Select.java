package org.h2.command.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.h2.engine.Database;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.Wildcard;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.Window;
import org.h2.expression.condition.Comparison;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.expression.condition.ConditionLocalAndGlobal;
import org.h2.expression.function.CoalesceFunction;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.index.ViewIndex;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.LazyResult;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableType;
import org.h2.table.TableView;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueRow;

public class Select extends Query {
   TableFilter topTableFilter;
   private final ArrayList<TableFilter> filters = Utils.newSmallArrayList();
   private final ArrayList<TableFilter> topFilters = Utils.newSmallArrayList();
   private Select parentSelect;
   private Expression condition;
   private Expression having;
   private Expression qualify;
   private Expression[] distinctExpressions;
   private int[] distinctIndexes;
   private ArrayList<Expression> group;
   int[] groupIndex;
   boolean[] groupByExpression;
   SelectGroups groupData;
   private int havingIndex;
   private int qualifyIndex;
   private int[] groupByCopies;
   private boolean isExplicitTable;
   boolean isGroupQuery;
   private boolean isGroupSortedQuery;
   private boolean isWindowQuery;
   private boolean isForUpdate;
   private double cost;
   private boolean isQuickAggregateQuery;
   private boolean isDistinctQuery;
   private boolean sortUsingIndex;
   private boolean isGroupWindowStage2;
   private HashMap<String, Window> windows;

   public Select(SessionLocal var1, Select var2) {
      super(var1);
      this.parentSelect = var2;
   }

   public boolean isUnion() {
      return false;
   }

   public void addTableFilter(TableFilter var1, boolean var2) {
      this.filters.add(var1);
      if (var2) {
         this.topFilters.add(var1);
      }

   }

   public ArrayList<TableFilter> getTopFilters() {
      return this.topFilters;
   }

   public void setExpressions(ArrayList<Expression> var1) {
      this.expressions = var1;
   }

   public void setExplicitTable() {
      this.setWildcard();
      this.isExplicitTable = true;
   }

   public void setWildcard() {
      this.expressions = new ArrayList(1);
      this.expressions.add(new Wildcard((String)null, (String)null));
   }

   public void setGroupQuery() {
      this.isGroupQuery = true;
   }

   public void setWindowQuery() {
      this.isWindowQuery = true;
   }

   public void setGroupBy(ArrayList<Expression> var1) {
      this.group = var1;
   }

   public ArrayList<Expression> getGroupBy() {
      return this.group;
   }

   public SelectGroups getGroupDataIfCurrent(boolean var1) {
      return this.groupData == null || !var1 && !this.groupData.isCurrentGroup() ? null : this.groupData;
   }

   public void setDistinct() {
      if (this.distinctExpressions != null) {
         throw DbException.getUnsupportedException("DISTINCT ON together with DISTINCT");
      } else {
         this.distinct = true;
      }
   }

   public void setDistinct(Expression[] var1) {
      if (this.distinct) {
         throw DbException.getUnsupportedException("DISTINCT ON together with DISTINCT");
      } else {
         this.distinctExpressions = var1;
      }
   }

   public boolean isAnyDistinct() {
      return this.distinct || this.distinctExpressions != null;
   }

   public boolean addWindow(String var1, Window var2) {
      if (this.windows == null) {
         this.windows = new HashMap();
      }

      return this.windows.put(var1, var2) == null;
   }

   public Window getWindow(String var1) {
      return this.windows != null ? (Window)this.windows.get(var1) : null;
   }

   public void addCondition(Expression var1) {
      if (this.condition == null) {
         this.condition = var1;
      } else {
         this.condition = new ConditionAndOr(0, var1, this.condition);
      }

   }

   public Expression getCondition() {
      return this.condition;
   }

   private LazyResult queryGroupSorted(int var1, ResultTarget var2, long var3, boolean var5) {
      LazyResultGroupSorted var6 = new LazyResultGroupSorted(this.expressionArray, var1);
      skipOffset(var6, var3, var5);
      if (var2 == null) {
         return var6;
      } else {
         while(var6.next()) {
            var2.addRow(var6.currentRow());
         }

         return null;
      }
   }

   Value[] createGroupSortedRow(Value[] var1, int var2) {
      Value[] var3 = this.constructGroupResultRow(var1, var2);
      return this.isHavingNullOrFalse(var3) ? null : this.rowForResult(var3, var2);
   }

   private Value[] rowForResult(Value[] var1, int var2) {
      return var2 == this.resultColumnCount ? var1 : (Value[])Arrays.copyOf(var1, this.resultColumnCount);
   }

   private boolean isHavingNullOrFalse(Value[] var1) {
      return this.havingIndex >= 0 && !var1[this.havingIndex].isTrue();
   }

   private Index getGroupSortedIndex() {
      if (this.groupIndex != null && this.groupByExpression != null) {
         ArrayList var1 = this.topTableFilter.getTable().getIndexes();
         if (var1 != null) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               Index var3 = (Index)var2.next();
               if (!var3.getIndexType().isScan() && !var3.getIndexType().isHash() && this.isGroupSortedIndex(this.topTableFilter, var3)) {
                  return var3;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean isGroupSortedIndex(TableFilter var1, Index var2) {
      Column[] var3 = var2.getColumns();
      boolean[] var4 = new boolean[var3.length];
      int var5 = 0;

      label47:
      for(int var6 = this.expressions.size(); var5 < var6; ++var5) {
         if (this.groupByExpression[var5]) {
            Expression var7 = ((Expression)this.expressions.get(var5)).getNonAliasExpression();
            if (!(var7 instanceof ExpressionColumn)) {
               return false;
            }

            ExpressionColumn var8 = (ExpressionColumn)var7;

            for(int var9 = 0; var9 < var3.length; ++var9) {
               if (var1 == var8.getTableFilter() && var3[var9].equals(var8.getColumn())) {
                  var4[var9] = true;
                  continue label47;
               }
            }

            return false;
         }
      }

      for(var5 = 1; var5 < var4.length; ++var5) {
         if (!var4[var5 - 1] && var4[var5]) {
            return false;
         }
      }

      return true;
   }

   boolean isConditionMetForUpdate() {
      if (!this.isConditionMet()) {
         return false;
      } else {
         int var1 = this.filters.size();
         boolean var2 = true;

         for(int var3 = 0; var3 < var1; ++var3) {
            TableFilter var4 = (TableFilter)this.filters.get(var3);
            if (!var4.isJoinOuter() && !var4.isJoinOuterIndirect()) {
               Row var5 = var4.get();
               Table var6 = var4.getTable();
               if (var6.isRowLockable()) {
                  Row var7 = var6.lockRow(this.session, var5);
                  if (var7 == null) {
                     return false;
                  }

                  if (!var5.hasSharedData(var7)) {
                     var4.set(var7);
                     var2 = false;
                  }
               }
            }
         }

         return var2 || this.isConditionMet();
      }
   }

   boolean isConditionMet() {
      return this.condition == null || this.condition.getBooleanValue(this.session);
   }

   private void queryWindow(int var1, LocalResult var2, long var3, boolean var5) {
      this.initGroupData(var1);

      try {
         this.gatherGroup(var1, 2);
         this.processGroupResult(var1, var2, var3, var5, false);
      } finally {
         this.groupData.reset();
      }

   }

   private void queryGroupWindow(int var1, LocalResult var2, long var3, boolean var5) {
      this.initGroupData(var1);

      try {
         this.gatherGroup(var1, 1);

         try {
            this.isGroupWindowStage2 = true;

            while(this.groupData.next() != null) {
               if (this.havingIndex >= 0 && !((Expression)this.expressions.get(this.havingIndex)).getBooleanValue(this.session)) {
                  this.groupData.remove();
               } else {
                  this.updateAgg(var1, 2);
               }
            }

            this.groupData.done();
            this.processGroupResult(var1, var2, var3, var5, false);
         } finally {
            this.isGroupWindowStage2 = false;
         }
      } finally {
         this.groupData.reset();
      }
   }

   private void queryGroup(int var1, LocalResult var2, long var3, boolean var5) {
      this.initGroupData(var1);

      try {
         this.gatherGroup(var1, 1);
         this.processGroupResult(var1, var2, var3, var5, true);
      } finally {
         this.groupData.reset();
      }

   }

   private void initGroupData(int var1) {
      if (this.groupData == null) {
         this.setGroupData(SelectGroups.getInstance(this.session, this.expressions, this.isGroupQuery, this.groupIndex));
      } else {
         this.updateAgg(var1, 0);
      }

      this.groupData.reset();
   }

   void setGroupData(SelectGroups var1) {
      this.groupData = var1;
      this.topTableFilter.visit((var1x) -> {
         Select var2 = var1x.getSelect();
         if (var2 != null) {
            var2.groupData = var1;
         }

      });
   }

   private void gatherGroup(int var1, int var2) {
      long var3 = 0L;
      this.setCurrentRowNumber(0L);

      while(true) {
         while(true) {
            if (!this.topTableFilter.next()) {
               this.groupData.done();
               return;
            }

            this.setCurrentRowNumber(var3 + 1L);
            if (this.isForUpdate) {
               if (this.isConditionMetForUpdate()) {
                  break;
               }
            } else if (this.isConditionMet()) {
               break;
            }
         }

         ++var3;
         this.groupData.nextSource();
         this.updateAgg(var1, var2);
      }
   }

   void updateAgg(int var1, int var2) {
      for(int var3 = 0; var3 < var1; ++var3) {
         if ((this.groupByExpression == null || !this.groupByExpression[var3]) && (this.groupByCopies == null || this.groupByCopies[var3] < 0)) {
            Expression var4 = (Expression)this.expressions.get(var3);
            var4.updateAggregate(this.session, var2);
         }
      }

   }

   private void processGroupResult(int var1, LocalResult var2, long var3, boolean var5, boolean var6) {
      ValueRow var7;
      while((var7 = this.groupData.next()) != null) {
         Value[] var8 = this.constructGroupResultRow(var7.getList(), var1);
         if ((!var6 || !this.isHavingNullOrFalse(var8)) && (this.qualifyIndex < 0 || var8[this.qualifyIndex].isTrue())) {
            if (var5 && var3 > 0L) {
               --var3;
            } else {
               var2.addRow(this.rowForResult(var8, var1));
            }
         }
      }

   }

   private Value[] constructGroupResultRow(Value[] var1, int var2) {
      Value[] var3 = new Value[var2];
      int var4;
      int var5;
      if (this.groupIndex != null) {
         var4 = 0;

         for(var5 = this.groupIndex.length; var4 < var5; ++var4) {
            var3[this.groupIndex[var4]] = var1[var4];
         }
      }

      for(var4 = 0; var4 < var2; ++var4) {
         if (this.groupByExpression == null || !this.groupByExpression[var4]) {
            if (this.groupByCopies != null) {
               var5 = this.groupByCopies[var4];
               if (var5 >= 0) {
                  var3[var4] = var3[var5];
                  continue;
               }
            }

            var3[var4] = ((Expression)this.expressions.get(var4)).getValue(this.session);
         }
      }

      return var3;
   }

   private Index getSortIndex() {
      if (this.sort == null) {
         return null;
      } else {
         ArrayList var1 = Utils.newSmallArrayList();
         int[] var2 = this.sort.getQueryColumnIndexes();
         int var3 = var2.length;
         int[] var4 = new int[var3];
         int var5 = 0;

         for(int var6 = 0; var5 < var3; ++var5) {
            int var7 = var2[var5];
            if (var7 < 0 || var7 >= this.expressions.size()) {
               throw DbException.getInvalidValueException("ORDER BY", var7 + 1);
            }

            Expression var8 = (Expression)this.expressions.get(var7);
            var8 = var8.getNonAliasExpression();
            if (!var8.isConstant()) {
               if (!(var8 instanceof ExpressionColumn)) {
                  return null;
               }

               ExpressionColumn var9 = (ExpressionColumn)var8;
               if (var9.getTableFilter() != this.topTableFilter) {
                  return null;
               }

               var1.add(var9.getColumn());
               var4[var6++] = var5;
            }
         }

         Column[] var16 = (Column[])var1.toArray(new Column[0]);
         if (var16.length == 0) {
            return this.topTableFilter.getTable().getScanIndex(this.session);
         } else {
            ArrayList var17 = this.topTableFilter.getTable().getIndexes();
            if (var17 != null) {
               int[] var18 = this.sort.getSortTypesWithNullOrdering();
               DefaultNullOrdering var20 = this.session.getDatabase().getDefaultNullOrdering();
               Iterator var21 = var17.iterator();

               label85:
               while(true) {
                  Index var10;
                  IndexColumn[] var11;
                  do {
                     do {
                        do {
                           if (!var21.hasNext()) {
                              break label85;
                           }

                           var10 = (Index)var21.next();
                        } while(var10.getCreateSQL() == null);
                     } while(var10.getIndexType().isHash());

                     var11 = var10.getIndexColumns();
                  } while(var11.length < var16.length);

                  for(int var12 = 0; var12 < var16.length; ++var12) {
                     IndexColumn var13 = var11[var12];
                     Column var14 = var16[var12];
                     if (var13.column != var14) {
                        continue label85;
                     }

                     int var15 = var18[var4[var12]];
                     if (var14.isNullable()) {
                        if (var20.addExplicitNullOrdering(var13.sortType) != var15) {
                           continue label85;
                        }
                     } else if ((var13.sortType & 1) != (var15 & 1)) {
                        continue label85;
                     }
                  }

                  return var10;
               }
            }

            if (var16.length == 1 && var16[0].getColumnId() == -1) {
               Index var19 = this.topTableFilter.getTable().getScanIndex(this.session);
               if (var19.isRowIdIndex()) {
                  return var19;
               }
            }

            return null;
         }
      }
   }

   private void queryDistinct(ResultTarget var1, long var2, long var4, boolean var6, boolean var7) {
      if (var4 > 0L && var2 > 0L) {
         var4 += var2;
         if (var4 < 0L) {
            var4 = Long.MAX_VALUE;
         }
      }

      long var8 = 0L;
      this.setCurrentRowNumber(0L);
      Index var10 = this.topTableFilter.getIndex();
      SearchRow var11 = null;
      int var12 = var10.getColumns()[0].getColumnId();
      if (!var7) {
         var2 = 0L;
      }

      while(true) {
         this.setCurrentRowNumber(++var8);
         Cursor var13 = var10.findNext(this.session, var11, (SearchRow)null);
         if (!var13.next()) {
            break;
         }

         SearchRow var14 = var13.getSearchRow();
         Value var15 = var14.getValue(var12);
         if (var11 == null) {
            var11 = var10.getRowFactory().createRow();
         }

         var11.setValue(var12, var15);
         if (var2 > 0L) {
            --var2;
         } else {
            var1.addRow(var15);
            if ((this.sort == null || this.sortUsingIndex) && var4 > 0L && var8 >= var4 && !var6) {
               break;
            }
         }
      }

   }

   private LazyResult queryFlat(int var1, ResultTarget var2, long var3, long var5, boolean var7, boolean var8) {
      if (var5 > 0L && var3 > 0L && !var8) {
         var5 += var3;
         if (var5 < 0L) {
            var5 = Long.MAX_VALUE;
         }
      }

      LazyResultQueryFlat var9 = new LazyResultQueryFlat(this.expressionArray, var1, this.isForUpdate);
      skipOffset(var9, var3, var8);
      if (var2 == null) {
         return var9;
      } else {
         if (var5 < 0L || this.sort != null && !this.sortUsingIndex || var7 && !var8) {
            var5 = Long.MAX_VALUE;
         }

         Value[] var10 = null;

         while(var2.getRowCount() < var5 && var9.next()) {
            var10 = var9.currentRow();
            var2.addRow(var10);
         }

         if (var5 != Long.MAX_VALUE && var7 && this.sort != null && var10 != null) {
            Value[] var11 = var10;

            while(var9.next()) {
               var10 = var9.currentRow();
               if (this.sort.compare(var11, var10) != 0) {
                  break;
               }

               var2.addRow(var10);
            }

            var2.limitsWereApplied();
         }

         return null;
      }
   }

   private static void skipOffset(LazyResultSelect var0, long var1, boolean var3) {
      if (var3) {
         while(var1 > 0L && var0.skip()) {
            --var1;
         }
      }

   }

   private void queryQuick(int var1, ResultTarget var2, boolean var3) {
      Value[] var4 = new Value[var1];

      for(int var5 = 0; var5 < var1; ++var5) {
         Expression var6 = (Expression)this.expressions.get(var5);
         var4[var5] = var6.getValue(this.session);
      }

      if (!var3) {
         var2.addRow(var4);
      }

   }

   protected ResultInterface queryWithoutCache(long var1, ResultTarget var3) {
      this.disableLazyForJoinSubqueries(this.topTableFilter);
      Query.OffsetFetch var4 = this.getOffsetFetch(var1);
      long var5 = var4.offset;
      long var7 = var4.fetch;
      boolean var9 = var4.fetchPercent;
      boolean var10 = this.session.isLazyQueryExecution() && var3 == null && !this.isForUpdate && !this.isQuickAggregateQuery && var7 != 0L && !var9 && !this.withTies && var5 == 0L && this.isReadOnly();
      int var11 = this.expressions.size();
      LocalResult var12 = null;
      if (!var10 && (var3 == null || !this.session.getDatabase().getSettings().optimizeInsertFromSelect)) {
         var12 = this.createLocalResult(var12);
      }

      boolean var13 = !var9;
      if (this.sort != null && (!this.sortUsingIndex || this.isAnyDistinct())) {
         var12 = this.createLocalResult(var12);
         var12.setSortOrder(this.sort);
         if (!this.sortUsingIndex) {
            var13 = false;
         }
      }

      if (this.distinct) {
         if (!this.isDistinctQuery) {
            var13 = false;
            var12 = this.createLocalResult(var12);
            var12.setDistinct();
         }
      } else if (this.distinctExpressions != null) {
         var13 = false;
         var12 = this.createLocalResult(var12);
         var12.setDistinct(this.distinctIndexes);
      }

      if (this.isWindowQuery || this.isGroupQuery && !this.isGroupSortedQuery) {
         var12 = this.createLocalResult(var12);
      }

      if (!var10 && (var7 >= 0L || var5 > 0L)) {
         var12 = this.createLocalResult(var12);
      }

      this.topTableFilter.startQuery(this.session);
      this.topTableFilter.reset();
      this.topTableFilter.lock(this.session);
      Object var14 = var12 != null ? var12 : var3;
      var10 &= var14 == null;
      LazyResult var15 = null;
      if (var7 != 0L) {
         long var16 = var9 ? -1L : var7;
         if (!this.isQuickAggregateQuery) {
            if (this.isWindowQuery) {
               if (this.isGroupQuery) {
                  this.queryGroupWindow(var11, var12, var5, var13);
               } else {
                  this.queryWindow(var11, var12, var5, var13);
               }
            } else if (this.isGroupQuery) {
               if (this.isGroupSortedQuery) {
                  var15 = this.queryGroupSorted(var11, (ResultTarget)var14, var5, var13);
               } else {
                  this.queryGroup(var11, var12, var5, var13);
               }
            } else if (this.isDistinctQuery) {
               this.queryDistinct((ResultTarget)var14, var5, var16, this.withTies, var13);
            } else {
               var15 = this.queryFlat(var11, (ResultTarget)var14, var5, var16, this.withTies, var13);
            }
         } else {
            this.queryQuick(var11, (ResultTarget)var14, var13 && var5 > 0L);
         }

         if (var13) {
            var5 = 0L;
         }
      }

      assert var10 == (var15 != null) : var10;

      if (var15 != null) {
         if (var7 > 0L) {
            var15.setLimit(var7);
         }

         return (ResultInterface)(this.randomAccessResult ? this.convertToDistinct(var15) : var15);
      } else {
         return var12 != null ? this.finishResult(var12, var5, var7, var9, var3) : null;
      }
   }

   private void disableLazyForJoinSubqueries(TableFilter var1) {
      if (this.session.isLazyQueryExecution()) {
         var1.visit((var1x) -> {
            if (var1x != var1 && var1x.getTable().getTableType() == TableType.VIEW) {
               ViewIndex var2 = (ViewIndex)var1x.getIndex();
               if (var2 != null && var2.getQuery() != null) {
                  var2.getQuery().setNeverLazy(true);
               }
            }

         });
      }

   }

   private LocalResult createLocalResult(LocalResult var1) {
      return var1 != null ? var1 : new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
   }

   private void expandColumnList() {
      int var1 = 0;

      while(true) {
         while(var1 < this.expressions.size()) {
            Expression var2 = (Expression)this.expressions.get(var1);
            if (!(var2 instanceof Wildcard)) {
               ++var1;
            } else {
               this.expressions.remove(var1);
               Wildcard var3 = (Wildcard)var2;
               String var4 = var3.getTableAlias();
               boolean var5 = var3.getExceptColumns() != null;
               HashMap var6 = null;
               if (var4 == null) {
                  Iterator var12;
                  TableFilter var13;
                  if (var5) {
                     var12 = this.filters.iterator();

                     while(var12.hasNext()) {
                        var13 = (TableFilter)var12.next();
                        var3.mapColumns(var13, 1, 0);
                     }

                     var6 = var3.mapExceptColumns();
                  }

                  for(var12 = this.filters.iterator(); var12.hasNext(); var1 = this.expandColumnList(var13, var1, false, var6)) {
                     var13 = (TableFilter)var12.next();
                  }
               } else {
                  Database var7 = this.session.getDatabase();
                  String var8 = var3.getSchemaName();
                  TableFilter var9 = null;
                  Iterator var10 = this.filters.iterator();

                  while(var10.hasNext()) {
                     TableFilter var11 = (TableFilter)var10.next();
                     if (var7.equalsIdentifiers(var4, var11.getTableAlias()) && (var8 == null || var7.equalsIdentifiers(var8, var11.getSchemaName()))) {
                        if (var5) {
                           var3.mapColumns(var11, 1, 0);
                           var6 = var3.mapExceptColumns();
                        }

                        var9 = var11;
                        break;
                     }
                  }

                  if (var9 == null) {
                     throw DbException.get(42102, (String)var4);
                  }

                  var1 = this.expandColumnList(var9, var1, true, var6);
               }
            }
         }

         return;
      }
   }

   private int expandColumnList(TableFilter var1, int var2, boolean var3, HashMap<Column, ExpressionColumn> var4) {
      String var5 = var1.getSchemaName();
      String var6 = var1.getTableAlias();
      int var9;
      if (var3) {
         Column[] var7 = var1.getTable().getColumns();
         int var8 = var7.length;

         for(var9 = 0; var9 < var8; ++var9) {
            Column var10 = var7[var9];
            var2 = this.addExpandedColumn(var1, var2, var4, var5, var6, var10);
         }
      } else {
         LinkedHashMap var17 = var1.getCommonJoinColumns();
         if (var17 != null) {
            TableFilter var18 = var1.getCommonJoinColumnsFilter();
            String var20 = var18.getSchemaName();
            String var21 = var18.getTableAlias();
            Iterator var11 = var17.entrySet().iterator();

            label69:
            while(true) {
               Column var13;
               Column var14;
               do {
                  do {
                     if (!var11.hasNext()) {
                        break label69;
                     }

                     Map.Entry var12 = (Map.Entry)var11.next();
                     var13 = (Column)var12.getKey();
                     var14 = (Column)var12.getValue();
                  } while(var1.isCommonJoinColumnToExclude(var14));
               } while(var4 != null && (var4.remove(var13) != null || var4.remove(var14) != null));

               Database var15 = this.session.getDatabase();
               Object var16;
               if (var13 != var14 && (!DataType.hasTotalOrdering(var13.getType().getValueType()) || !DataType.hasTotalOrdering(var14.getType().getValueType()))) {
                  var16 = new Alias(new CoalesceFunction(0, new Expression[]{new ExpressionColumn(var15, var5, var6, var1.getColumnName(var13)), new ExpressionColumn(var15, var20, var21, var18.getColumnName(var14))}), var13.getName(), true);
               } else {
                  var16 = new ExpressionColumn(var15, var20, var21, var18.getColumnName(var14));
               }

               this.expressions.add(var2++, var16);
            }
         }

         Column[] var19 = var1.getTable().getColumns();
         var9 = var19.length;

         for(int var22 = 0; var22 < var9; ++var22) {
            Column var23 = var19[var22];
            if ((var17 == null || !var17.containsKey(var23)) && !var1.isCommonJoinColumnToExclude(var23)) {
               var2 = this.addExpandedColumn(var1, var2, var4, var5, var6, var23);
            }
         }
      }

      return var2;
   }

   private int addExpandedColumn(TableFilter var1, int var2, HashMap<Column, ExpressionColumn> var3, String var4, String var5, Column var6) {
      if ((var3 == null || var3.remove(var6) == null) && var6.getVisible()) {
         ExpressionColumn var7 = new ExpressionColumn(this.session.getDatabase(), var4, var5, var1.getColumnName(var6));
         this.expressions.add(var2++, var7);
      }

      return var2;
   }

   public void init() {
      if (this.checkInit) {
         throw DbException.getInternalError();
      } else {
         this.filters.sort(TableFilter.ORDER_IN_FROM_COMPARATOR);
         this.expandColumnList();
         if ((this.visibleColumnCount = this.expressions.size()) > 16384) {
            throw DbException.get(54011, (String)"16384");
         } else {
            ArrayList var1;
            if (this.distinctExpressions == null && this.orderList == null && this.group == null) {
               var1 = null;
            } else {
               var1 = new ArrayList(this.visibleColumnCount);

               for(int var2 = 0; var2 < this.visibleColumnCount; ++var2) {
                  Expression var3 = (Expression)this.expressions.get(var2);
                  var3 = var3.getNonAliasExpression();
                  var1.add(var3.getSQL(0, 2));
               }
            }

            int var4;
            int var5;
            int var15;
            if (this.distinctExpressions != null) {
               BitSet var12 = new BitSet();
               Expression[] var14 = this.distinctExpressions;
               var4 = var14.length;

               for(var5 = 0; var5 < var4; ++var5) {
                  Expression var6 = var14[var5];
                  var12.set(this.initExpression(var1, var6, false, this.filters));
               }

               var15 = 0;
               var4 = var12.cardinality();
               this.distinctIndexes = new int[var4];

               for(var5 = 0; var5 < var4; ++var5) {
                  var15 = var12.nextSetBit(var15);
                  this.distinctIndexes[var5] = var15++;
               }
            }

            if (this.orderList != null) {
               this.initOrder(var1, this.isAnyDistinct(), this.filters);
            }

            this.resultColumnCount = this.expressions.size();
            if (this.having != null) {
               this.expressions.add(this.having);
               this.havingIndex = this.expressions.size() - 1;
               this.having = null;
            } else {
               this.havingIndex = -1;
            }

            if (this.qualify != null) {
               this.expressions.add(this.qualify);
               this.qualifyIndex = this.expressions.size() - 1;
               this.qualify = null;
            } else {
               this.qualifyIndex = -1;
            }

            if (this.withTies && !this.hasOrder()) {
               throw DbException.get(90122);
            } else {
               Database var13 = this.session.getDatabase();
               if (this.group != null) {
                  var15 = this.group.size();
                  var4 = var1.size();
                  var5 = this.expressions.size();
                  int var18;
                  if (var5 > var4) {
                     var1.ensureCapacity(var5);

                     for(var18 = var4; var18 < var5; ++var18) {
                        var1.add(((Expression)this.expressions.get(var18)).getSQL(0, 2));
                     }
                  }

                  this.groupIndex = new int[var15];

                  int var9;
                  for(var18 = 0; var18 < var15; ++var18) {
                     Expression var7 = (Expression)this.group.get(var18);
                     String var8 = var7.getSQL(0, 2);
                     var9 = -1;

                     int var10;
                     for(var10 = 0; var10 < var4; ++var10) {
                        String var11 = (String)var1.get(var10);
                        if (var13.equalsIdentifiers(var11, var8)) {
                           var9 = this.mergeGroupByExpressions(var13, var10, var1, false);
                           break;
                        }
                     }

                     if (var9 < 0) {
                        for(var10 = 0; var10 < var4; ++var10) {
                           Expression var22 = (Expression)this.expressions.get(var10);
                           if (var13.equalsIdentifiers(var8, var22.getAlias(this.session, var10))) {
                              var9 = this.mergeGroupByExpressions(var13, var10, var1, true);
                              break;
                           }

                           var8 = var7.getAlias(this.session, var10);
                           if (var13.equalsIdentifiers(var8, var22.getAlias(this.session, var10))) {
                              var9 = this.mergeGroupByExpressions(var13, var10, var1, true);
                              break;
                           }
                        }
                     }

                     if (var9 < 0) {
                        var10 = this.expressions.size();
                        this.groupIndex[var18] = var10;
                        this.expressions.add(var7);
                     } else {
                        this.groupIndex[var18] = var9;
                     }
                  }

                  int var19;
                  int[] var20;
                  int var21;
                  if (this.groupByCopies != null) {
                     var20 = this.groupByCopies;
                     var19 = var20.length;
                     var21 = 0;

                     while(true) {
                        if (var21 >= var19) {
                           this.groupByCopies = null;
                           break;
                        }

                        var9 = var20[var21];
                        if (var9 >= 0) {
                           break;
                        }

                        ++var21;
                     }
                  }

                  this.groupByExpression = new boolean[this.expressions.size()];
                  var20 = this.groupIndex;
                  var19 = var20.length;

                  for(var21 = 0; var21 < var19; ++var21) {
                     var9 = var20[var21];
                     this.groupByExpression[var9] = true;
                  }

                  this.group = null;
               }

               Iterator var17 = this.filters.iterator();

               while(var17.hasNext()) {
                  TableFilter var16 = (TableFilter)var17.next();
                  this.mapColumns(var16, 0);
               }

               this.mapCondition(this.havingIndex);
               this.mapCondition(this.qualifyIndex);
               this.checkInit = true;
            }
         }
      }
   }

   private void mapCondition(int var1) {
      if (var1 >= 0) {
         Expression var2 = (Expression)this.expressions.get(var1);
         SelectListColumnResolver var3 = new SelectListColumnResolver(this);
         var2.mapColumns(var3, 0, 0);
      }

   }

   private int mergeGroupByExpressions(Database var1, int var2, ArrayList<String> var3, boolean var4) {
      if (this.groupByCopies != null) {
         int var5 = this.groupByCopies[var2];
         if (var5 >= 0) {
            return var5;
         }

         if (var5 == -2) {
            return var2;
         }
      } else {
         this.groupByCopies = new int[var3.size()];
         Arrays.fill(this.groupByCopies, -1);
      }

      String var8 = (String)var3.get(var2);
      int var6;
      if (var4) {
         for(var6 = 0; var6 < var2; ++var6) {
            if (var1.equalsIdentifiers(var8, (String)var3.get(var6))) {
               var2 = var6;
               break;
            }
         }
      }

      var6 = var3.size();

      for(int var7 = var2 + 1; var7 < var6; ++var7) {
         if (var1.equalsIdentifiers(var8, (String)var3.get(var7))) {
            this.groupByCopies[var7] = var2;
         }
      }

      this.groupByCopies[var2] = -2;
      return var2;
   }

   public void prepare() {
      if (!this.isPrepared) {
         if (!this.checkInit) {
            throw DbException.getInternalError("not initialized");
         } else {
            if (this.orderList != null) {
               this.prepareOrder(this.orderList, this.expressions.size());
            }

            Mode.ExpressionNames var1 = this.session.getMode().expressionNames;
            if (var1 != Mode.ExpressionNames.ORIGINAL_SQL && var1 != Mode.ExpressionNames.POSTGRESQL_STYLE) {
               for(int var2 = 0; var2 < this.expressions.size(); ++var2) {
                  this.expressions.set(var2, ((Expression)this.expressions.get(var2)).optimize(this.session));
               }
            } else {
               this.optimizeExpressionsAndPreserveAliases();
            }

            if (this.sort != null) {
               this.cleanupOrder();
            }

            if (this.condition != null) {
               this.condition = this.condition.optimizeCondition(this.session);
               if (this.condition != null) {
                  Iterator var8 = this.filters.iterator();

                  while(var8.hasNext()) {
                     TableFilter var3 = (TableFilter)var8.next();
                     if (!var3.isJoinOuter() && !var3.isJoinOuterIndirect()) {
                        this.condition.createIndexConditions(this.session, var3);
                     }
                  }
               }
            }

            if (this.isGroupQuery && this.groupIndex == null && this.havingIndex < 0 && this.qualifyIndex < 0 && this.condition == null && this.filters.size() == 1) {
               this.isQuickAggregateQuery = this.isEverything(ExpressionVisitor.getOptimizableVisitor(((TableFilter)this.filters.get(0)).getTable()));
            }

            this.cost = this.preparePlan(this.session.isParsingCreateView());
            if (this.distinct && this.session.getDatabase().getSettings().optimizeDistinct && !this.isGroupQuery && this.filters.size() == 1 && this.expressions.size() == 1 && this.condition == null) {
               Expression var9 = (Expression)this.expressions.get(0);
               var9 = var9.getNonAliasExpression();
               if (var9 instanceof ExpressionColumn) {
                  Column var10 = ((ExpressionColumn)var9).getColumn();
                  int var4 = var10.getSelectivity();
                  Index var5 = this.topTableFilter.getTable().getIndexForColumn(var10, false, true);
                  if (var5 != null && var4 != 50 && var4 < 20) {
                     Index var6 = this.topTableFilter.getIndex();
                     if (var6 == null || var6.getIndexType().isScan() || var5 == var6) {
                        this.topTableFilter.setIndex(var5);
                        this.isDistinctQuery = true;
                     }
                  }
               }
            }

            Index var11;
            Index var12;
            if (this.sort != null && !this.isQuickAggregateQuery && !this.isGroupQuery) {
               var11 = this.getSortIndex();
               var12 = this.topTableFilter.getIndex();
               if (var11 != null && var12 != null) {
                  if (!var12.getIndexType().isScan() && var12 != var11) {
                     if (var11.getIndexColumns() != null && var11.getIndexColumns().length >= var12.getIndexColumns().length) {
                        IndexColumn[] var13 = var11.getIndexColumns();
                        IndexColumn[] var14 = var12.getIndexColumns();
                        boolean var15 = false;

                        for(int var7 = 0; var7 < var14.length; ++var7) {
                           if (var13[var7].column != var14[var7].column) {
                              var15 = false;
                              break;
                           }

                           if (var13[var7].sortType != var14[var7].sortType) {
                              var15 = true;
                           }
                        }

                        if (var15) {
                           this.topTableFilter.setIndex(var11);
                           this.sortUsingIndex = true;
                        }
                     }
                  } else {
                     this.topTableFilter.setIndex(var11);
                     if (!this.topTableFilter.hasInComparisons()) {
                        this.sortUsingIndex = true;
                     }
                  }
               }

               if (this.sortUsingIndex && this.isForUpdate && !this.topTableFilter.getIndex().isRowIdIndex()) {
                  this.sortUsingIndex = false;
               }
            }

            if (!this.isQuickAggregateQuery && this.isGroupQuery) {
               var11 = this.getGroupSortedIndex();
               if (var11 != null) {
                  var12 = this.topTableFilter.getIndex();
                  if (var12 != null && (var12.getIndexType().isScan() || var12 == var11)) {
                     this.topTableFilter.setIndex(var11);
                     this.isGroupSortedQuery = true;
                  }
               }
            }

            this.expressionArray = (Expression[])this.expressions.toArray(new Expression[0]);
            this.isPrepared = true;
         }
      }
   }

   private void optimizeExpressionsAndPreserveAliases() {
      for(int var1 = 0; var1 < this.expressions.size(); ++var1) {
         Expression var2 = (Expression)this.expressions.get(var1);
         String var3 = var2.getAlias(this.session, var1);
         Object var4 = var2.optimize(this.session);
         if (!((Expression)var4).getAlias(this.session, var1).equals(var3)) {
            var4 = new Alias((Expression)var4, var3, true);
         }

         this.expressions.set(var1, var4);
      }

   }

   public double getCost() {
      return this.cost;
   }

   public HashSet<Table> getTables() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.filters.iterator();

      while(var2.hasNext()) {
         TableFilter var3 = (TableFilter)var2.next();
         var1.add(var3.getTable());
      }

      return var1;
   }

   public void fireBeforeSelectTriggers() {
      Iterator var1 = this.filters.iterator();

      while(var1.hasNext()) {
         TableFilter var2 = (TableFilter)var1.next();
         var2.getTable().fire(this.session, 8, true);
      }

   }

   private double preparePlan(boolean var1) {
      TableFilter[] var2 = (TableFilter[])this.topFilters.toArray(new TableFilter[0]);
      TableFilter[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TableFilter var6 = var3[var5];
         var6.createIndexConditions();
         var6.setFullCondition(this.condition);
      }

      Optimizer var7 = new Optimizer(var2, this.condition, this.session);
      var7.optimize(var1);
      this.topTableFilter = var7.getTopFilter();
      double var8 = var7.getCost();
      this.setEvaluatableRecursive(this.topTableFilter);
      if (!var1) {
         this.topTableFilter.prepare();
      }

      return var8;
   }

   private void setEvaluatableRecursive(TableFilter var1) {
      while(var1 != null) {
         var1.setEvaluatable(var1, true);
         if (this.condition != null) {
            this.condition.setEvaluatable(var1, true);
         }

         TableFilter var2 = var1.getNestedJoin();
         if (var2 != null) {
            this.setEvaluatableRecursive(var2);
         }

         Expression var3 = var1.getJoinCondition();
         if (var3 != null && !var3.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
            var3 = var3.optimize(this.session);
            if (!var1.isJoinOuter() && !var1.isJoinOuterIndirect()) {
               var1.removeJoinCondition();
               this.addCondition(var3);
            }
         }

         var3 = var1.getFilterCondition();
         if (var3 != null && !var3.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
            var1.removeFilterCondition();
            this.addCondition(var3);
         }

         Iterator var4 = this.expressions.iterator();

         while(var4.hasNext()) {
            Expression var5 = (Expression)var4.next();
            var5.setEvaluatable(var1, true);
         }

         var1 = var1.getJoin();
      }

   }

   public String getPlanSQL(int var1) {
      Expression[] var2 = (Expression[])this.expressions.toArray(new Expression[0]);
      StringBuilder var3 = new StringBuilder();
      Iterator var4 = this.topFilters.iterator();

      while(var4.hasNext()) {
         TableFilter var5 = (TableFilter)var4.next();
         Table var6 = var5.getTable();
         TableView var7 = var6 instanceof TableView ? (TableView)var6 : null;
         if (var7 != null && var7.isRecursive() && var7.isTableExpression() && var7.isTemporary()) {
            var3.append("WITH RECURSIVE ");
            var6.getSchema().getSQL(var3, var1).append('.');
            ParserUtil.quoteIdentifier(var3, var6.getName(), var1).append('(');
            Column.writeColumns(var3, var6.getColumns(), var1);
            var3.append(") AS ");
            var6.getSQL(var3, var1).append('\n');
         }
      }

      if (this.isExplicitTable) {
         var3.append("TABLE ");
         ((TableFilter)this.filters.get(0)).getPlanSQL(var3, false, var1);
      } else {
         var3.append("SELECT");
         if (this.isAnyDistinct()) {
            var3.append(" DISTINCT");
            if (this.distinctExpressions != null) {
               Expression.writeExpressions(var3.append(" ON("), this.distinctExpressions, var1).append(')');
            }
         }

         for(int var8 = 0; var8 < this.visibleColumnCount; ++var8) {
            if (var8 > 0) {
               var3.append(',');
            }

            var3.append('\n');
            StringUtils.indent(var3, var2[var8].getSQL(var1, 2), 4, false);
         }

         TableFilter var9 = this.topTableFilter;
         int var10;
         if (var9 == null) {
            var10 = this.topFilters.size();
            if (var10 != 1 || !((TableFilter)this.topFilters.get(0)).isNoFromClauseFilter()) {
               var3.append("\nFROM ");
               boolean var11 = false;

               for(int var13 = 0; var13 < var10; ++var13) {
                  var11 = getPlanFromFilter(var3, var1, (TableFilter)this.topFilters.get(var13), var11);
               }
            }
         } else if (!var9.isNoFromClauseFilter()) {
            getPlanFromFilter(var3.append("\nFROM "), var1, var9, false);
         }

         if (this.condition != null) {
            getFilterSQL(var3, "\nWHERE ", this.condition, var1);
         }

         int var12;
         if (this.groupIndex != null) {
            var3.append("\nGROUP BY ");
            var10 = 0;

            for(var12 = this.groupIndex.length; var10 < var12; ++var10) {
               if (var10 > 0) {
                  var3.append(", ");
               }

               var2[this.groupIndex[var10]].getNonAliasExpression().getUnenclosedSQL(var3, var1);
            }
         } else if (this.group != null) {
            var3.append("\nGROUP BY ");
            var10 = 0;

            for(var12 = this.group.size(); var10 < var12; ++var10) {
               if (var10 > 0) {
                  var3.append(", ");
               }

               ((Expression)this.group.get(var10)).getUnenclosedSQL(var3, var1);
            }
         } else if (this.isGroupQuery && this.having == null && this.havingIndex < 0) {
            var10 = 0;

            while(true) {
               if (var10 >= this.visibleColumnCount) {
                  var3.append("\nGROUP BY ()");
                  break;
               }

               if (containsAggregate(var2[var10])) {
                  break;
               }

               ++var10;
            }
         }

         getFilterSQL(var3, "\nHAVING ", var2, this.having, this.havingIndex, var1);
         getFilterSQL(var3, "\nQUALIFY ", var2, this.qualify, this.qualifyIndex, var1);
      }

      this.appendEndOfQueryToSQL(var3, var1, var2);
      if (this.isForUpdate) {
         var3.append("\nFOR UPDATE");
      }

      if ((var1 & 8) != 0) {
         if (this.isQuickAggregateQuery) {
            var3.append("\n/* direct lookup */");
         }

         if (this.isDistinctQuery) {
            var3.append("\n/* distinct */");
         }

         if (this.sortUsingIndex) {
            var3.append("\n/* index sorted */");
         }

         if (this.isGroupQuery && this.isGroupSortedQuery) {
            var3.append("\n/* group sorted */");
         }
      }

      return var3.toString();
   }

   private static boolean getPlanFromFilter(StringBuilder var0, int var1, TableFilter var2, boolean var3) {
      do {
         if (var3) {
            var0.append('\n');
         }

         var2.getPlanSQL(var0, var3, var1);
         var3 = true;
      } while((var2 = var2.getJoin()) != null);

      return var3;
   }

   private static void getFilterSQL(StringBuilder var0, String var1, Expression[] var2, Expression var3, int var4, int var5) {
      if (var3 != null) {
         getFilterSQL(var0, var1, var3, var5);
      } else if (var4 >= 0) {
         getFilterSQL(var0, var1, var2[var4], var5);
      }

   }

   private static void getFilterSQL(StringBuilder var0, String var1, Expression var2, int var3) {
      var2.getUnenclosedSQL(var0.append(var1), var3);
   }

   private static boolean containsAggregate(Expression var0) {
      if (var0 instanceof DataAnalysisOperation && ((DataAnalysisOperation)var0).isAggregate()) {
         return true;
      } else {
         int var1 = 0;

         for(int var2 = var0.getSubexpressionCount(); var1 < var2; ++var1) {
            if (containsAggregate(var0.getSubexpression(var1))) {
               return true;
            }
         }

         return false;
      }
   }

   public void setHaving(Expression var1) {
      this.having = var1;
   }

   public Expression getHaving() {
      return this.having;
   }

   public void setQualify(Expression var1) {
      this.qualify = var1;
   }

   public Expression getQualify() {
      return this.qualify;
   }

   public TableFilter getTopTableFilter() {
      return this.topTableFilter;
   }

   public void setForUpdate(boolean var1) {
      if (!var1 || !this.isAnyDistinct() && !this.isGroupQuery) {
         this.isForUpdate = var1;
      } else {
         throw DbException.get(90145);
      }
   }

   public void mapColumns(ColumnResolver var1, int var2) {
      Iterator var3 = this.expressions.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         var4.mapColumns(var1, var2, 0);
      }

      if (this.condition != null) {
         this.condition.mapColumns(var1, var2, 0);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      Iterator var3 = this.expressions.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         var4.setEvaluatable(var1, var2);
      }

      if (this.condition != null) {
         this.condition.setEvaluatable(var1, var2);
      }

   }

   public boolean isQuickAggregateQuery() {
      return this.isQuickAggregateQuery;
   }

   public boolean isGroupQuery() {
      return this.isGroupQuery;
   }

   public boolean isWindowQuery() {
      return this.isWindowQuery;
   }

   public boolean isGroupWindowStage2() {
      return this.isGroupWindowStage2;
   }

   public void addGlobalCondition(Parameter var1, int var2, int var3) {
      this.addParameter(var1);
      Expression var5 = (Expression)this.expressions.get(var2);
      var5 = var5.getNonAliasExpression();
      Comparison var4;
      if (var5.isEverything(ExpressionVisitor.QUERY_COMPARABLE_VISITOR)) {
         var4 = new Comparison(var3, var5, var1, false);
      } else {
         var4 = new Comparison(6, var1, var1, false);
      }

      Expression var7 = var4.optimize(this.session);
      if (this.isWindowQuery) {
         this.qualify = addGlobalCondition(this.qualify, var7);
      } else if (this.isGroupQuery) {
         for(int var6 = 0; this.groupIndex != null && var6 < this.groupIndex.length; ++var6) {
            if (this.groupIndex[var6] == var2) {
               this.condition = addGlobalCondition(this.condition, var7);
               return;
            }
         }

         if (this.havingIndex >= 0) {
            this.having = (Expression)this.expressions.get(this.havingIndex);
         }

         this.having = addGlobalCondition(this.having, var7);
      } else {
         this.condition = addGlobalCondition(this.condition, var7);
      }

   }

   private static Expression addGlobalCondition(Expression var0, Expression var1) {
      if (!(var0 instanceof ConditionLocalAndGlobal)) {
         return new ConditionLocalAndGlobal(var0, var1);
      } else {
         Expression var2;
         Expression var3;
         if (var0.getSubexpressionCount() == 1) {
            var2 = null;
            var3 = var0.getSubexpression(0);
         } else {
            var2 = var0.getSubexpression(0);
            var3 = var0.getSubexpression(1);
         }

         return new ConditionLocalAndGlobal(var2, new ConditionAndOr(0, var3, var1));
      }
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      Iterator var3 = this.expressions.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         var4.updateAggregate(var1, var2);
      }

      if (this.condition != null) {
         this.condition.updateAggregate(var1, var2);
      }

      if (this.having != null) {
         this.having.updateAggregate(var1, var2);
      }

      if (this.qualify != null) {
         this.qualify.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      Iterator var2;
      TableFilter var3;
      label71:
      switch (var1.getType()) {
         case 2:
            if (this.isForUpdate) {
               return false;
            }

            var2 = this.filters.iterator();

            do {
               if (!var2.hasNext()) {
                  break label71;
               }

               var3 = (TableFilter)var2.next();
            } while(var3.getTable().isDeterministic());

            return false;
         case 3:
            if (!this.session.getDatabase().getSettings().optimizeEvaluatableSubqueries) {
               return false;
            }
            break;
         case 4:
            var2 = this.filters.iterator();

            while(var2.hasNext()) {
               var3 = (TableFilter)var2.next();
               long var8 = var3.getTable().getMaxDataModificationId();
               var1.addDataModificationId(var8);
            }
         case 5:
         case 6:
         default:
            break;
         case 7:
            var2 = this.filters.iterator();

            while(var2.hasNext()) {
               var3 = (TableFilter)var2.next();
               Table var4 = var3.getTable();
               var1.addDependency(var4);
               var4.addDependencies(var1.getDependencies());
            }
      }

      ExpressionVisitor var6 = var1.incrementQueryLevel(1);
      Iterator var7 = this.expressions.iterator();

      Expression var9;
      do {
         if (!var7.hasNext()) {
            if (this.condition != null && !this.condition.isEverything(var6)) {
               return false;
            }

            if (this.having != null && !this.having.isEverything(var6)) {
               return false;
            }

            if (this.qualify != null && !this.qualify.isEverything(var6)) {
               return false;
            }

            return true;
         }

         var9 = (Expression)var7.next();
      } while(var9.isEverything(var6));

      return false;
   }

   public boolean isCacheable() {
      return !this.isForUpdate;
   }

   public boolean allowGlobalConditions() {
      return this.offsetExpr == null && this.fetchExpr == null && this.distinctExpressions == null;
   }

   public SortOrder getSortOrder() {
      return this.sort;
   }

   public Select getParentSelect() {
      return this.parentSelect;
   }

   public boolean isConstantQuery() {
      if (super.isConstantQuery() && this.distinctExpressions == null && this.condition == null && !this.isGroupQuery && !this.isWindowQuery && this.isNoFromClause()) {
         for(int var1 = 0; var1 < this.visibleColumnCount; ++var1) {
            if (!((Expression)this.expressions.get(var1)).isConstant()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public Expression getIfSingleRow() {
      if (this.offsetExpr == null && this.fetchExpr == null && this.condition == null && !this.isGroupQuery && !this.isWindowQuery && this.isNoFromClause()) {
         if (this.visibleColumnCount == 1) {
            return (Expression)this.expressions.get(0);
         } else {
            Expression[] var1 = new Expression[this.visibleColumnCount];

            for(int var2 = 0; var2 < this.visibleColumnCount; ++var2) {
               var1[var2] = (Expression)this.expressions.get(var2);
            }

            return new ExpressionList(var1, false);
         }
      } else {
         return null;
      }
   }

   private boolean isNoFromClause() {
      if (this.topTableFilter != null) {
         return this.topTableFilter.isNoFromClauseFilter();
      } else {
         return this.topFilters.size() == 1 ? ((TableFilter)this.topFilters.get(0)).isNoFromClauseFilter() : false;
      }
   }

   private final class LazyResultGroupSorted extends LazyResultSelect {
      private Value[] previousKeyValues;

      LazyResultGroupSorted(Expression[] var2, int var3) {
         super(var2, var3);
         if (Select.this.groupData == null) {
            Select.this.setGroupData(SelectGroups.getInstance(Select.this.getSession(), Select.this.expressions, Select.this.isGroupQuery, Select.this.groupIndex));
         } else {
            Select.this.updateAgg(var3, 0);
            Select.this.groupData.resetLazy();
         }

      }

      public void reset() {
         super.reset();
         Select.this.groupData.resetLazy();
         this.previousKeyValues = null;
      }

      protected Value[] fetchNextRow() {
         while(Select.this.topTableFilter.next()) {
            Select.this.setCurrentRowNumber(this.rowNumber + 1L);
            if (Select.this.isConditionMet()) {
               ++this.rowNumber;
               int var1 = Select.this.groupIndex.length;
               Value[] var2 = new Value[var1];

               for(int var3 = 0; var3 < var1; ++var3) {
                  int var4 = Select.this.groupIndex[var3];
                  Expression var5 = (Expression)Select.this.expressions.get(var4);
                  var2[var3] = var5.getValue(Select.this.getSession());
               }

               Value[] var7 = null;
               if (this.previousKeyValues == null) {
                  this.previousKeyValues = var2;
                  Select.this.groupData.nextLazyGroup();
               } else {
                  SessionLocal var8 = Select.this.getSession();

                  for(int var9 = 0; var9 < var1; ++var9) {
                     if (var8.compare(this.previousKeyValues[var9], var2[var9]) != 0) {
                        var7 = Select.this.createGroupSortedRow(this.previousKeyValues, this.columnCount);
                        this.previousKeyValues = var2;
                        Select.this.groupData.nextLazyGroup();
                        break;
                     }
                  }
               }

               Select.this.groupData.nextLazyRow();
               Select.this.updateAgg(this.columnCount, 1);
               if (var7 != null) {
                  return var7;
               }
            }
         }

         Value[] var6 = null;
         if (this.previousKeyValues != null) {
            var6 = Select.this.createGroupSortedRow(this.previousKeyValues, this.columnCount);
            this.previousKeyValues = null;
         }

         return var6;
      }
   }

   private final class LazyResultQueryFlat extends LazyResultSelect {
      private boolean forUpdate;

      LazyResultQueryFlat(Expression[] var2, int var3, boolean var4) {
         super(var2, var3);
         this.forUpdate = var4;
      }

      protected Value[] fetchNextRow() {
         while(Select.this.topTableFilter.next()) {
            Select.this.setCurrentRowNumber(this.rowNumber + 1L);
            if (this.forUpdate) {
               if (!Select.this.isConditionMetForUpdate()) {
                  continue;
               }
            } else if (!Select.this.isConditionMet()) {
               continue;
            }

            ++this.rowNumber;
            Value[] var1 = new Value[this.columnCount];

            for(int var2 = 0; var2 < this.columnCount; ++var2) {
               Expression var3 = (Expression)Select.this.expressions.get(var2);
               var1[var2] = var3.getValue(Select.this.getSession());
            }

            return var1;
         }

         return null;
      }

      protected boolean skipNextRow() {
         while(true) {
            if (Select.this.topTableFilter.next()) {
               Select.this.setCurrentRowNumber(this.rowNumber + 1L);
               if (!Select.this.isConditionMet()) {
                  continue;
               }

               ++this.rowNumber;
               return true;
            }

            return false;
         }
      }
   }

   private abstract class LazyResultSelect extends LazyResult {
      long rowNumber;
      int columnCount;

      LazyResultSelect(Expression[] var2, int var3) {
         super(Select.this.getSession(), var2);
         this.columnCount = var3;
         Select.this.setCurrentRowNumber(0L);
      }

      public final int getVisibleColumnCount() {
         return Select.this.visibleColumnCount;
      }

      public void reset() {
         super.reset();
         Select.this.topTableFilter.reset();
         Select.this.setCurrentRowNumber(0L);
         this.rowNumber = 0L;
      }
   }
}
