package org.h2.expression.condition;

import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.IndexCondition;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

public final class ConditionInQuery extends PredicateWithSubquery {
   private Expression left;
   private final boolean not;
   private final boolean whenOperand;
   private final boolean all;
   private final int compareType;

   public ConditionInQuery(Expression var1, boolean var2, boolean var3, Query var4, boolean var5, int var6) {
      super(var4);
      this.left = var1;
      this.not = var2;
      this.whenOperand = var3;
      var4.setRandomAccessResult(true);
      var4.setNeverLazy(true);
      var4.setDistinctIfPossible();
      this.all = var5;
      this.compareType = var6;
   }

   public Value getValue(SessionLocal var1) {
      return this.getValue(var1, this.left.getValue(var1));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      return !this.whenOperand ? super.getWhenValue(var1, var2) : this.getValue(var1, var2).isTrue();
   }

   private Value getValue(SessionLocal var1, Value var2) {
      this.query.setSession(var1);
      LocalResult var3 = (LocalResult)this.query.query(0L);
      if (!var3.hasNext()) {
         return ValueBoolean.get(this.not ^ this.all);
      } else if ((this.compareType & -2) == 6) {
         return this.getNullSafeValueSlow(var1, var3, var2);
      } else if (var2.containsNull()) {
         return ValueNull.INSTANCE;
      } else if (!this.all && this.compareType == 0 && var1.getDatabase().getSettings().optimizeInSelect) {
         int var4 = this.query.getColumnCount();
         if (var4 != 1) {
            Value[] var5 = var2.convertToAnyRow().getList();
            if (var4 == var5.length && var3.containsDistinct(var5)) {
               return ValueBoolean.get(!this.not);
            }
         } else {
            TypeInfo var6 = var3.getColumnType(0);
            if (var6.getValueType() == 0) {
               return ValueNull.INSTANCE;
            }

            if (var2.getValueType() == 41) {
               var2 = ((ValueRow)var2).getList()[0];
            }

            if (var3.containsDistinct(new Value[]{var2})) {
               return ValueBoolean.get(!this.not);
            }
         }

         return (Value)(var3.containsNull() ? ValueNull.INSTANCE : ValueBoolean.get(this.not));
      } else {
         return this.getValueSlow(var1, var3, var2);
      }
   }

   private Value getValueSlow(SessionLocal var1, ResultInterface var2, Value var3) {
      boolean var4 = var3.getValueType() != 41 && this.query.getColumnCount() == 1;
      boolean var5 = false;
      ValueBoolean var6 = ValueBoolean.get(!this.all);

      while(var2.next()) {
         Value[] var7 = var2.currentRow();
         Value var8 = Comparison.compare(var1, var3, (Value)(var4 ? var7[0] : ValueRow.get(var7)), this.compareType);
         if (var8 == ValueNull.INSTANCE) {
            var5 = true;
         } else if (var8 == var6) {
            return ValueBoolean.get(this.not == this.all);
         }
      }

      if (var5) {
         return ValueNull.INSTANCE;
      } else {
         return ValueBoolean.get(this.not ^ this.all);
      }
   }

   private Value getNullSafeValueSlow(SessionLocal var1, ResultInterface var2, Value var3) {
      boolean var4 = var3.getValueType() != 41 && this.query.getColumnCount() == 1;
      boolean var5 = this.all == (this.compareType == 7);

      Value[] var6;
      do {
         if (!var2.next()) {
            return ValueBoolean.get(this.not ^ this.all);
         }

         var6 = var2.currentRow();
      } while(var1.areEqual(var3, (Value)(var4 ? var6[0] : ValueRow.get(var6))) != var5);

      return ValueBoolean.get(this.not == this.all);
   }

   public boolean isWhenConditionOperand() {
      return this.whenOperand;
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new ConditionInQuery(this.left, !this.not, false, this.query, this.all, this.compareType);
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      super.mapColumns(var1, var2, var3);
   }

   public Expression optimize(SessionLocal var1) {
      super.optimize(var1);
      this.left = this.left.optimize(var1);
      TypeInfo.checkComparable(this.left.getType(), this.query.getRowDataType());
      return this;
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      super.setEvaluatable(var1, var2);
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      boolean var3 = this.not && (this.all || this.compareType != 0);
      if (var3) {
         var1.append("NOT (");
      }

      this.left.getSQL(var1, var2, 0);
      this.getWhenSQL(var1, var2);
      if (var3) {
         var1.append(')');
      }

      return var1;
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      if (this.all) {
         var1.append(Comparison.COMPARE_TYPES[this.compareType]).append(" ALL");
      } else if (this.compareType == 0) {
         if (this.not) {
            var1.append(" NOT");
         }

         var1.append(" IN");
      } else {
         var1.append(' ').append(Comparison.COMPARE_TYPES[this.compareType]).append(" ANY");
      }

      return super.getUnenclosedSQL(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      super.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && super.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost() + super.getCost();
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (var1.getDatabase().getSettings().optimizeInList) {
         if (!this.not && this.compareType == 0) {
            if (this.query.getColumnCount() == 1) {
               if (this.left instanceof ExpressionColumn) {
                  TypeInfo var3 = this.left.getType();
                  TypeInfo var4 = ((Expression)this.query.getExpressions().get(0)).getType();
                  if (TypeInfo.haveSameOrdering(var3, TypeInfo.getHigherType(var3, var4))) {
                     int var5 = var3.getValueType();
                     if (DataType.hasTotalOrdering(var5) || var5 == var4.getValueType()) {
                        ExpressionColumn var6 = (ExpressionColumn)this.left;
                        if (var2 == var6.getTableFilter()) {
                           ExpressionVisitor var7 = ExpressionVisitor.getNotFromResolverVisitor(var2);
                           if (this.query.isEverything(var7)) {
                              var2.addIndexCondition(IndexCondition.getInQuery(var6, this.query));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
