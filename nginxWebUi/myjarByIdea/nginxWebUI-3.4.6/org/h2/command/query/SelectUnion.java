package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashSet;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.result.LazyResult;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class SelectUnion extends Query {
   private final UnionType unionType;
   final Query left;
   final Query right;
   private boolean isForUpdate;

   public SelectUnion(SessionLocal var1, UnionType var2, Query var3, Query var4) {
      super(var1);
      this.unionType = var2;
      this.left = var3;
      this.right = var4;
   }

   public boolean isUnion() {
      return true;
   }

   public UnionType getUnionType() {
      return this.unionType;
   }

   public Query getLeft() {
      return this.left;
   }

   public Query getRight() {
      return this.right;
   }

   private Value[] convert(Value[] var1, int var2) {
      Value[] var3;
      if (var2 == var1.length) {
         var3 = var1;
      } else {
         var3 = new Value[var2];
      }

      for(int var4 = 0; var4 < var2; ++var4) {
         Expression var5 = (Expression)this.expressions.get(var4);
         var3[var4] = var1[var4].convertTo(var5.getType(), this.session);
      }

      return var3;
   }

   public LocalResult getEmptyResult() {
      int var1 = this.left.getColumnCount();
      return this.createLocalResult(var1);
   }

   protected ResultInterface queryWithoutCache(long var1, ResultTarget var3) {
      Query.OffsetFetch var4 = this.getOffsetFetch(var1);
      long var5 = var4.offset;
      long var7 = var4.fetch;
      boolean var9 = var4.fetchPercent;
      Database var10 = this.session.getDatabase();
      if (var10.getSettings().optimizeInsertFromSelect && this.unionType == SelectUnion.UnionType.UNION_ALL && var3 != null && this.sort == null && !this.distinct && var7 < 0L && var5 == 0L) {
         this.left.query(0L, var3);
         this.right.query(0L, var3);
         return null;
      } else {
         int var11 = this.left.getColumnCount();
         if (this.session.isLazyQueryExecution() && this.unionType == SelectUnion.UnionType.UNION_ALL && !this.distinct && this.sort == null && !this.randomAccessResult && !this.isForUpdate && var5 == 0L && !var9 && !this.withTies && this.isReadOnly() && var7 != 0L) {
            LazyResultUnion var17 = new LazyResultUnion(this.expressionArray, var11);
            if (var7 > 0L) {
               var17.setLimit(var7);
            }

            return var17;
         } else {
            LocalResult var12 = this.createLocalResult(var11);
            if (this.sort != null) {
               var12.setSortOrder(this.sort);
            }

            if (this.distinct) {
               this.left.setDistinctIfPossible();
               this.right.setDistinctIfPossible();
               var12.setDistinct();
            }

            switch (this.unionType) {
               case UNION:
               case EXCEPT:
                  this.left.setDistinctIfPossible();
                  this.right.setDistinctIfPossible();
                  var12.setDistinct();
               case UNION_ALL:
                  break;
               case INTERSECT:
                  this.left.setDistinctIfPossible();
                  this.right.setDistinctIfPossible();
                  break;
               default:
                  throw DbException.getInternalError("type=" + this.unionType);
            }

            ResultInterface var13;
            ResultInterface var14;
            var13 = this.left.query(0L);
            var14 = this.right.query(0L);
            var13.reset();
            var14.reset();
            label102:
            switch (this.unionType) {
               case UNION:
               case UNION_ALL:
                  while(var13.next()) {
                     var12.addRow(this.convert(var13.currentRow(), var11));
                  }

                  while(var14.next()) {
                     var12.addRow(this.convert(var14.currentRow(), var11));
                  }
                  break;
               case EXCEPT:
                  while(var13.next()) {
                     var12.addRow(this.convert(var13.currentRow(), var11));
                  }

                  while(true) {
                     if (!var14.next()) {
                        break label102;
                     }

                     var12.removeDistinct(this.convert(var14.currentRow(), var11));
                  }
               case INTERSECT:
                  LocalResult var15 = this.createLocalResult(var11);
                  var15.setDistinct();

                  while(var13.next()) {
                     var15.addRow(this.convert(var13.currentRow(), var11));
                  }

                  while(var14.next()) {
                     Value[] var16 = this.convert(var14.currentRow(), var11);
                     if (var15.containsDistinct(var16)) {
                        var12.addRow(var16);
                     }
                  }

                  var15.close();
                  break;
               default:
                  throw DbException.getInternalError("type=" + this.unionType);
            }

            var13.close();
            var14.close();
            return this.finishResult(var12, var5, var7, var9, var3);
         }
      }
   }

   private LocalResult createLocalResult(int var1) {
      return new LocalResult(this.session, this.expressionArray, var1, var1);
   }

   public void init() {
      if (this.checkInit) {
         throw DbException.getInternalError();
      } else {
         this.checkInit = true;
         this.left.init();
         this.right.init();
         int var1 = this.left.getColumnCount();
         if (var1 != this.right.getColumnCount()) {
            throw DbException.get(21002);
         } else {
            ArrayList var2 = this.left.getExpressions();
            this.expressions = new ArrayList(var1);

            for(int var3 = 0; var3 < var1; ++var3) {
               Expression var4 = (Expression)var2.get(var3);
               this.expressions.add(var4);
            }

            this.visibleColumnCount = var1;
            if (this.withTies && !this.hasOrder()) {
               throw DbException.get(90122);
            }
         }
      }
   }

   public void prepare() {
      if (!this.isPrepared) {
         if (!this.checkInit) {
            throw DbException.getInternalError("not initialized");
         } else {
            this.isPrepared = true;
            this.left.prepare();
            this.right.prepare();
            int var1 = this.left.getColumnCount();
            this.expressions = new ArrayList(var1);
            ArrayList var2 = this.left.getExpressions();
            ArrayList var3 = this.right.getExpressions();

            for(int var4 = 0; var4 < var1; ++var4) {
               Expression var5 = (Expression)var2.get(var4);
               Expression var6 = (Expression)var3.get(var4);
               Column var7 = new Column(var5.getAlias(this.session, var4), TypeInfo.getHigherType(var5.getType(), var6.getType()));
               ExpressionColumn var8 = new ExpressionColumn(this.session.getDatabase(), var7);
               this.expressions.add(var8);
            }

            if (this.orderList != null && this.initOrder((ArrayList)null, true, (ArrayList)null)) {
               this.prepareOrder(this.orderList, this.expressions.size());
               this.cleanupOrder();
            }

            this.resultColumnCount = this.expressions.size();
            this.expressionArray = (Expression[])this.expressions.toArray(new Expression[0]);
         }
      }
   }

   public double getCost() {
      return this.left.getCost() + this.right.getCost();
   }

   public HashSet<Table> getTables() {
      HashSet var1 = this.left.getTables();
      var1.addAll(this.right.getTables());
      return var1;
   }

   public void setForUpdate(boolean var1) {
      this.left.setForUpdate(var1);
      this.right.setForUpdate(var1);
      this.isForUpdate = var1;
   }

   public void mapColumns(ColumnResolver var1, int var2) {
      this.left.mapColumns(var1, var2);
      this.right.mapColumns(var1, var2);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      this.right.setEvaluatable(var1, var2);
   }

   public void addGlobalCondition(Parameter var1, int var2, int var3) {
      this.addParameter(var1);
      switch (this.unionType) {
         case UNION:
         case UNION_ALL:
         case INTERSECT:
            this.left.addGlobalCondition(var1, var2, var3);
            this.right.addGlobalCondition(var1, var2, var3);
            break;
         case EXCEPT:
            this.left.addGlobalCondition(var1, var2, var3);
            break;
         default:
            throw DbException.getInternalError("type=" + this.unionType);
      }

   }

   public String getPlanSQL(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append('(').append(this.left.getPlanSQL(var1)).append(')');
      switch (this.unionType) {
         case UNION:
            var2.append("\nUNION\n");
            break;
         case EXCEPT:
            var2.append("\nEXCEPT\n");
            break;
         case UNION_ALL:
            var2.append("\nUNION ALL\n");
            break;
         case INTERSECT:
            var2.append("\nINTERSECT\n");
            break;
         default:
            throw DbException.getInternalError("type=" + this.unionType);
      }

      var2.append('(').append(this.right.getPlanSQL(var1)).append(')');
      this.appendEndOfQueryToSQL(var2, var1, (Expression[])this.expressions.toArray(new Expression[0]));
      if (this.isForUpdate) {
         var2.append("\nFOR UPDATE");
      }

      return var2.toString();
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && this.right.isEverything(var1);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      this.right.updateAggregate(var1, var2);
   }

   public void fireBeforeSelectTriggers() {
      this.left.fireBeforeSelectTriggers();
      this.right.fireBeforeSelectTriggers();
   }

   public boolean allowGlobalConditions() {
      return this.left.allowGlobalConditions() && this.right.allowGlobalConditions();
   }

   public boolean isConstantQuery() {
      return super.isConstantQuery() && this.left.isConstantQuery() && this.right.isConstantQuery();
   }

   private final class LazyResultUnion extends LazyResult {
      int columnCount;
      ResultInterface l;
      ResultInterface r;
      boolean leftDone;
      boolean rightDone;

      LazyResultUnion(Expression[] var2, int var3) {
         super(SelectUnion.this.getSession(), var2);
         this.columnCount = var3;
      }

      public int getVisibleColumnCount() {
         return this.columnCount;
      }

      protected Value[] fetchNextRow() {
         if (this.rightDone) {
            return null;
         } else {
            if (!this.leftDone) {
               if (this.l == null) {
                  this.l = SelectUnion.this.left.query(0L);
                  this.l.reset();
               }

               if (this.l.next()) {
                  return this.l.currentRow();
               }

               this.leftDone = true;
            }

            if (this.r == null) {
               this.r = SelectUnion.this.right.query(0L);
               this.r.reset();
            }

            if (this.r.next()) {
               return this.r.currentRow();
            } else {
               this.rightDone = true;
               return null;
            }
         }
      }

      public void close() {
         super.close();
         if (this.l != null) {
            this.l.close();
         }

         if (this.r != null) {
            this.r.close();
         }

      }

      public void reset() {
         super.reset();
         if (this.l != null) {
            this.l.reset();
         }

         if (this.r != null) {
            this.r.reset();
         }

         this.leftDone = false;
         this.rightDone = false;
      }
   }

   public static enum UnionType {
      UNION,
      UNION_ALL,
      EXCEPT,
      INTERSECT;
   }
}
