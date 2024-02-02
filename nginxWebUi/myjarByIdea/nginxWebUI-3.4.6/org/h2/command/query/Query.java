package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableView;
import org.h2.util.Utils;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

public abstract class Query extends Prepared {
   ArrayList<Expression> expressions;
   Expression[] expressionArray;
   ArrayList<QueryOrderBy> orderList;
   SortOrder sort;
   Expression fetchExpr;
   boolean fetchPercent;
   boolean withTies;
   Expression offsetExpr;
   boolean distinct;
   boolean randomAccessResult;
   int visibleColumnCount;
   int resultColumnCount;
   private boolean noCache;
   private long lastLimit;
   private long lastEvaluated;
   private ResultInterface lastResult;
   private Boolean lastExists;
   private Value[] lastParameters;
   private boolean cacheableChecked;
   private boolean neverLazy;
   boolean checkInit;
   boolean isPrepared;

   Query(SessionLocal var1) {
      super(var1);
   }

   public void setNeverLazy(boolean var1) {
      this.neverLazy = var1;
   }

   public boolean isNeverLazy() {
      return this.neverLazy;
   }

   public abstract boolean isUnion();

   public ResultInterface queryMeta() {
      LocalResult var1 = new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
      var1.done();
      return var1;
   }

   protected abstract ResultInterface queryWithoutCache(long var1, ResultTarget var3);

   private ResultInterface queryWithoutCacheLazyCheck(long var1, ResultTarget var3) {
      boolean var4 = this.neverLazy && this.session.isLazyQueryExecution();
      if (var4) {
         this.session.setLazyQueryExecution(false);
      }

      ResultInterface var5;
      try {
         var5 = this.queryWithoutCache(var1, var3);
      } finally {
         if (var4) {
            this.session.setLazyQueryExecution(true);
         }

      }

      return var5;
   }

   public abstract void init();

   public ArrayList<Expression> getExpressions() {
      return this.expressions;
   }

   public abstract double getCost();

   public int getCostAsExpression() {
      return (int)Math.min(1000000.0, 10.0 + 10.0 * this.getCost());
   }

   public abstract HashSet<Table> getTables();

   public void setOrder(ArrayList<QueryOrderBy> var1) {
      this.orderList = var1;
   }

   public boolean hasOrder() {
      return this.orderList != null || this.sort != null;
   }

   public abstract void setForUpdate(boolean var1);

   public int getColumnCount() {
      return this.visibleColumnCount;
   }

   public TypeInfo getRowDataType() {
      return this.visibleColumnCount == 1 ? this.expressionArray[0].getType() : TypeInfo.getTypeInfo(41, -1L, -1, new ExtTypeInfoRow(this.expressionArray, this.visibleColumnCount));
   }

   public abstract void mapColumns(ColumnResolver var1, int var2);

   public abstract void setEvaluatable(TableFilter var1, boolean var2);

   public abstract void addGlobalCondition(Parameter var1, int var2, int var3);

   public abstract boolean allowGlobalConditions();

   public abstract boolean isEverything(ExpressionVisitor var1);

   public boolean isReadOnly() {
      return this.isEverything(ExpressionVisitor.READONLY_VISITOR);
   }

   public abstract void updateAggregate(SessionLocal var1, int var2);

   public abstract void fireBeforeSelectTriggers();

   public void setDistinctIfPossible() {
      if (!this.isAnyDistinct() && this.offsetExpr == null && this.fetchExpr == null) {
         this.distinct = true;
      }

   }

   public boolean isStandardDistinct() {
      return this.distinct;
   }

   public boolean isAnyDistinct() {
      return this.distinct;
   }

   public boolean isRandomAccessResult() {
      return this.randomAccessResult;
   }

   public void setRandomAccessResult(boolean var1) {
      this.randomAccessResult = var1;
   }

   public boolean isQuery() {
      return true;
   }

   public boolean isTransactional() {
      return true;
   }

   public void disableCache() {
      this.noCache = true;
   }

   private boolean sameResultAsLast(Value[] var1, Value[] var2, long var3) {
      if (!this.cacheableChecked) {
         long var5 = this.getMaxDataModificationId();
         this.noCache = var5 == Long.MAX_VALUE;
         if (!this.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) || !this.isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
            this.noCache = true;
         }

         this.cacheableChecked = true;
      }

      if (this.noCache) {
         return false;
      } else {
         for(int var8 = 0; var8 < var1.length; ++var8) {
            Value var6 = var2[var8];
            Value var7 = var1[var8];
            if (var6.getValueType() != var7.getValueType() || !this.session.areEqual(var6, var7)) {
               return false;
            }
         }

         return this.getMaxDataModificationId() <= var3;
      }
   }

   private Value[] getParameterValues() {
      ArrayList var1 = this.getParameters();
      if (var1 == null) {
         return Value.EMPTY_VALUES;
      } else {
         int var2 = var1.size();
         Value[] var3 = new Value[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            Value var5 = ((Parameter)var1.get(var4)).getParamValue();
            var3[var4] = var5;
         }

         return var3;
      }
   }

   public final ResultInterface query(long var1) {
      return this.query(var1, (ResultTarget)null);
   }

   public final ResultInterface query(long var1, ResultTarget var3) {
      if (this.isUnion()) {
         return this.queryWithoutCacheLazyCheck(var1, var3);
      } else {
         this.fireBeforeSelectTriggers();
         if (!this.noCache && this.session.getDatabase().getOptimizeReuseResults() && (!this.session.isLazyQueryExecution() || this.neverLazy)) {
            Value[] var4 = this.getParameterValues();
            long var5 = this.session.getDatabase().getModificationDataId();
            if (this.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && this.lastResult != null && !this.lastResult.isClosed() && var1 == this.lastLimit && this.sameResultAsLast(var4, this.lastParameters, this.lastEvaluated)) {
               this.lastResult = this.lastResult.createShallowCopy(this.session);
               if (this.lastResult != null) {
                  this.lastResult.reset();
                  return this.lastResult;
               }
            }

            this.lastParameters = var4;
            this.closeLastResult();
            ResultInterface var7 = this.queryWithoutCacheLazyCheck(var1, var3);
            this.lastResult = var7;
            this.lastExists = null;
            this.lastEvaluated = var5;
            this.lastLimit = var1;
            return var7;
         } else {
            return this.queryWithoutCacheLazyCheck(var1, var3);
         }
      }
   }

   private void closeLastResult() {
      if (this.lastResult != null) {
         this.lastResult.close();
      }

   }

   public final boolean exists() {
      if (this.isUnion()) {
         return this.executeExists();
      } else {
         this.fireBeforeSelectTriggers();
         if (!this.noCache && this.session.getDatabase().getOptimizeReuseResults()) {
            Value[] var1 = this.getParameterValues();
            long var2 = this.session.getDatabase().getModificationDataId();
            if (this.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && this.lastExists != null && this.sameResultAsLast(var1, this.lastParameters, this.lastEvaluated)) {
               return this.lastExists;
            } else {
               this.lastParameters = var1;
               boolean var4 = this.executeExists();
               this.lastExists = var4;
               this.lastResult = null;
               this.lastEvaluated = var2;
               return var4;
            }
         } else {
            return this.executeExists();
         }
      }
   }

   private boolean executeExists() {
      ResultInterface var1 = this.queryWithoutCacheLazyCheck(1L, (ResultTarget)null);
      boolean var2 = var1.hasNext();
      var1.close();
      return var2;
   }

   boolean initOrder(ArrayList<String> var1, boolean var2, ArrayList<TableFilter> var3) {
      Iterator var4 = this.orderList.iterator();

      while(var4.hasNext()) {
         QueryOrderBy var5 = (QueryOrderBy)var4.next();
         Expression var6 = var5.expression;
         if (var6 != null) {
            if (var6.isConstant()) {
               var4.remove();
            } else {
               int var7 = this.initExpression(var1, var6, var2, var3);
               var5.columnIndexExpr = ValueExpression.get(ValueInteger.get(var7 + 1));
               var5.expression = ((Expression)this.expressions.get(var7)).getNonAliasExpression();
            }
         }
      }

      if (this.orderList.isEmpty()) {
         this.orderList = null;
         return false;
      } else {
         return true;
      }
   }

   int initExpression(ArrayList<String> var1, Expression var2, boolean var3, ArrayList<TableFilter> var4) {
      Database var5 = this.session.getDatabase();
      if (var2 instanceof ExpressionColumn) {
         ExpressionColumn var6 = (ExpressionColumn)var2;
         String var7 = var6.getOriginalTableAliasName();
         String var8 = var6.getOriginalColumnName();
         int var9 = 0;

         for(int var10 = this.getColumnCount(); var9 < var10; ++var9) {
            Expression var11 = (Expression)this.expressions.get(var9);
            if (var11 instanceof ExpressionColumn) {
               ExpressionColumn var21 = (ExpressionColumn)var11;
               if (var5.equalsIdentifiers(var8, var21.getColumnName(this.session, var9))) {
                  if (var7 == null) {
                     return var9;
                  }

                  String var22 = var21.getOriginalTableAliasName();
                  if (var22 != null) {
                     if (var5.equalsIdentifiers(var22, var7)) {
                        return var9;
                     }
                  } else if (var4 != null) {
                     Iterator var23 = var4.iterator();

                     while(var23.hasNext()) {
                        TableFilter var24 = (TableFilter)var23.next();
                        if (var5.equalsIdentifiers(var24.getTableAlias(), var7)) {
                           return var9;
                        }
                     }
                  }
               }
            } else if (var11 instanceof Alias) {
               if (var7 == null && var5.equalsIdentifiers(var8, var11.getAlias(this.session, var9))) {
                  return var9;
               }

               Expression var12 = var11.getNonAliasExpression();
               if (var12 instanceof ExpressionColumn) {
                  ExpressionColumn var13 = (ExpressionColumn)var12;
                  String var14 = var6.getSQL(0, 2);
                  String var15 = var13.getSQL(0, 2);
                  String var16 = var13.getColumnName(this.session, var9);
                  if (var5.equalsIdentifiers(var8, var16) && var5.equalsIdentifiers(var14, var15)) {
                     return var9;
                  }
               }
            }
         }
      } else if (var1 != null) {
         String var17 = var2.getSQL(0, 2);
         int var19 = 0;

         for(int var20 = var1.size(); var19 < var20; ++var19) {
            if (var5.equalsIdentifiers((String)var1.get(var19), var17)) {
               return var19;
            }
         }
      }

      if (var1 != null && (!var3 || var5.getMode().allowUnrelatedOrderByExpressionsInDistinctQueries || checkOrderOther(this.session, var2, var1))) {
         int var18 = this.expressions.size();
         this.expressions.add(var2);
         var1.add(var2.getSQL(0, 2));
         return var18;
      } else {
         throw DbException.get(90068, var2.getTraceSQL());
      }
   }

   private static boolean checkOrderOther(SessionLocal var0, Expression var1, ArrayList<String> var2) {
      if (var1 != null && !var1.isConstant()) {
         String var3 = var1.getSQL(0, 2);
         Iterator var4 = var2.iterator();

         String var5;
         do {
            if (!var4.hasNext()) {
               int var6 = var1.getSubexpressionCount();
               if (!var1.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
                  return false;
               }

               if (var6 <= 0) {
                  return false;
               }

               for(int var7 = 0; var7 < var6; ++var7) {
                  if (!checkOrderOther(var0, var1.getSubexpression(var7), var2)) {
                     return false;
                  }
               }

               return true;
            }

            var5 = (String)var4.next();
         } while(!var0.getDatabase().equalsIdentifiers(var3, var5));

         return true;
      } else {
         return true;
      }
   }

   void prepareOrder(ArrayList<QueryOrderBy> var1, int var2) {
      int var3 = var1.size();
      int[] var4 = new int[var3];
      int[] var5 = new int[var3];

      for(int var6 = 0; var6 < var3; ++var6) {
         QueryOrderBy var7 = (QueryOrderBy)var1.get(var6);
         boolean var9 = false;
         Value var10 = var7.columnIndexExpr.getValue((SessionLocal)null);
         int var8;
         if (var10 == ValueNull.INSTANCE) {
            var8 = 0;
         } else {
            var8 = var10.getInt();
            if (var8 < 0) {
               var9 = true;
               var8 = -var8;
            }

            --var8;
            if (var8 < 0 || var8 >= var2) {
               throw DbException.get(90068, Integer.toString(var8 + 1));
            }
         }

         var4[var6] = var8;
         int var11 = var7.sortType;
         if (var9) {
            var11 ^= 1;
         }

         var5[var6] = var11;
      }

      this.sort = new SortOrder(this.session, var4, var5, var1);
      this.orderList = null;
   }

   void cleanupOrder() {
      int[] var1 = this.sort.getQueryColumnIndexes();
      int var2 = var1.length;
      int var3 = 0;

      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         if (((Expression)this.expressions.get(var1[var4])).isConstant()) {
            ++var3;
         }
      }

      if (var3 != 0) {
         if (var3 == var2) {
            this.sort = null;
         } else {
            var4 = var2 - var3;
            int[] var5 = new int[var4];
            int[] var6 = new int[var4];
            int[] var7 = this.sort.getSortTypes();
            ArrayList var8 = this.sort.getOrderList();
            int var9 = 0;

            for(int var10 = 0; var10 < var4; ++var9) {
               if (!((Expression)this.expressions.get(var1[var9])).isConstant()) {
                  var5[var10] = var1[var9];
                  var6[var10] = var7[var9];
                  ++var10;
               } else {
                  var8.remove(var10);
               }
            }

            this.sort = new SortOrder(this.session, var5, var6, var8);
         }
      }
   }

   public int getType() {
      return 66;
   }

   public void setOffset(Expression var1) {
      this.offsetExpr = var1;
   }

   public Expression getOffset() {
      return this.offsetExpr;
   }

   public void setFetch(Expression var1) {
      this.fetchExpr = var1;
   }

   public Expression getFetch() {
      return this.fetchExpr;
   }

   public void setFetchPercent(boolean var1) {
      this.fetchPercent = var1;
   }

   public boolean isFetchPercent() {
      return this.fetchPercent;
   }

   public void setWithTies(boolean var1) {
      this.withTies = var1;
   }

   public boolean isWithTies() {
      return this.withTies;
   }

   void addParameter(Parameter var1) {
      if (this.parameters == null) {
         this.parameters = Utils.newSmallArrayList();
      }

      this.parameters.add(var1);
   }

   public final long getMaxDataModificationId() {
      ExpressionVisitor var1 = ExpressionVisitor.getMaxModificationIdVisitor();
      this.isEverything(var1);
      return Math.max(var1.getMaxDataModificationId(), this.session.getSnapshotDataModificationId());
   }

   void appendEndOfQueryToSQL(StringBuilder var1, int var2, Expression[] var3) {
      if (this.sort != null) {
         this.sort.getSQL(var1.append("\nORDER BY "), var3, this.visibleColumnCount, var2);
      } else if (this.orderList != null) {
         var1.append("\nORDER BY ");
         int var4 = 0;

         for(int var5 = this.orderList.size(); var4 < var5; ++var4) {
            if (var4 > 0) {
               var1.append(", ");
            }

            ((QueryOrderBy)this.orderList.get(var4)).getSQL(var1, var2);
         }
      }

      String var6;
      if (this.offsetExpr != null) {
         var6 = this.offsetExpr.getSQL(var2, 2);
         var1.append("\nOFFSET ").append(var6).append("1".equals(var6) ? " ROW" : " ROWS");
      }

      if (this.fetchExpr != null) {
         var1.append("\nFETCH ").append(this.offsetExpr != null ? "NEXT" : "FIRST");
         var6 = this.fetchExpr.getSQL(var2, 2);
         boolean var7 = this.fetchPercent || !"1".equals(var6);
         if (var7) {
            var1.append(' ').append(var6);
            if (this.fetchPercent) {
               var1.append(" PERCENT");
            }
         }

         var1.append(!var7 ? " ROW" : " ROWS").append(this.withTies ? " WITH TIES" : " ONLY");
      }

   }

   OffsetFetch getOffsetFetch(long var1) {
      long var3;
      if (this.offsetExpr != null) {
         Value var5 = this.offsetExpr.getValue(this.session);
         if (var5 == ValueNull.INSTANCE || (var3 = var5.getLong()) < 0L) {
            throw DbException.getInvalidValueException("result OFFSET", var5);
         }
      } else {
         var3 = 0L;
      }

      long var10 = var1 == 0L ? -1L : var1;
      if (this.fetchExpr != null) {
         Value var7 = this.fetchExpr.getValue(this.session);
         long var8;
         if (var7 == ValueNull.INSTANCE || (var8 = var7.getLong()) < 0L) {
            throw DbException.getInvalidValueException("result FETCH", var7);
         }

         var10 = var10 < 0L ? var8 : Math.min(var8, var10);
      }

      boolean var11 = this.fetchPercent;
      if (var11) {
         if (var10 > 100L) {
            throw DbException.getInvalidValueException("result FETCH PERCENT", var10);
         }

         if (var10 == 0L) {
            var11 = false;
         }
      }

      return new OffsetFetch(var3, var10, var11);
   }

   LocalResult finishResult(LocalResult var1, long var2, long var4, boolean var6, ResultTarget var7) {
      if (var2 != 0L) {
         var1.setOffset(var2);
      }

      if (var4 >= 0L) {
         var1.setLimit(var4);
         var1.setFetchPercent(var6);
         if (this.withTies) {
            var1.setWithTies(this.sort);
         }
      }

      var1.done();
      if (this.randomAccessResult && !this.distinct) {
         var1 = this.convertToDistinct(var1);
      }

      if (var7 == null) {
         return var1;
      } else {
         while(var1.next()) {
            var7.addRow(var1.currentRow());
         }

         var1.close();
         return null;
      }
   }

   LocalResult convertToDistinct(ResultInterface var1) {
      LocalResult var2 = new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
      var2.setDistinct();
      var1.reset();

      while(var1.next()) {
         var2.addRow(var1.currentRow());
      }

      var1.close();
      var2.done();
      return var2;
   }

   public Table toTable(String var1, Column[] var2, ArrayList<Parameter> var3, boolean var4, Query var5) {
      this.setParameterList(new ArrayList(var3));
      if (!this.checkInit) {
         this.init();
      }

      return TableView.createTempView(var4 ? this.session.getDatabase().getSystemSession() : this.session, this.session.getUser(), var1, var2, this, var5);
   }

   public void collectDependencies(HashSet<DbObject> var1) {
      ExpressionVisitor var2 = ExpressionVisitor.getDependenciesVisitor(var1);
      this.isEverything(var2);
   }

   public boolean isConstantQuery() {
      return !this.hasOrder() && (this.offsetExpr == null || this.offsetExpr.isConstant()) && (this.fetchExpr == null || this.fetchExpr.isConstant());
   }

   public Expression getIfSingleRow() {
      return null;
   }

   static final class OffsetFetch {
      final long offset;
      final long fetch;
      final boolean fetchPercent;

      OffsetFetch(long var1, long var3, boolean var5) {
         this.offset = var1;
         this.fetch = var3;
         this.fetchPercent = var5;
      }
   }
}
