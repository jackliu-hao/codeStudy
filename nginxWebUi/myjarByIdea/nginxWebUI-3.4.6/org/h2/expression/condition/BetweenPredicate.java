package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class BetweenPredicate extends Condition {
   private Expression left;
   private final boolean not;
   private final boolean whenOperand;
   private boolean symmetric;
   private Expression a;
   private Expression b;

   public BetweenPredicate(Expression var1, boolean var2, boolean var3, boolean var4, Expression var5, Expression var6) {
      this.left = var1;
      this.not = var2;
      this.whenOperand = var3;
      this.symmetric = var4;
      this.a = var5;
      this.b = var6;
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      if (this.not) {
         var1.append(" NOT");
      }

      var1.append(" BETWEEN ");
      if (this.symmetric) {
         var1.append("SYMMETRIC ");
      }

      this.a.getSQL(var1, var2, 0).append(" AND ");
      return this.b.getSQL(var1, var2, 0);
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.a = this.a.optimize(var1);
      this.b = this.b.optimize(var1);
      TypeInfo var2 = this.left.getType();
      TypeInfo.checkComparable(var2, this.a.getType());
      TypeInfo.checkComparable(var2, this.b.getType());
      if (this.whenOperand) {
         return this;
      } else {
         Value var3 = this.left.isConstant() ? this.left.getValue(var1) : null;
         Value var4 = this.a.isConstant() ? this.a.getValue(var1) : null;
         Value var5 = this.b.isConstant() ? this.b.getValue(var1) : null;
         if (var3 != null) {
            if (var3 == ValueNull.INSTANCE) {
               return TypedValueExpression.UNKNOWN;
            }

            if (var4 != null && var5 != null) {
               return ValueExpression.getBoolean(this.getValue(var1, var3, var4, var5));
            }
         }

         if (this.symmetric) {
            if (var4 == ValueNull.INSTANCE || var5 == ValueNull.INSTANCE) {
               return TypedValueExpression.UNKNOWN;
            }
         } else if (var4 == ValueNull.INSTANCE && var5 == ValueNull.INSTANCE) {
            return TypedValueExpression.UNKNOWN;
         }

         if (var4 != null && var5 != null && var1.compareWithNull(var4, var5, false) == 0) {
            return (new Comparison(this.not ? 1 : 0, this.left, this.a, false)).optimize(var1);
         } else {
            return this;
         }
      }
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      return (Value)(var2 == ValueNull.INSTANCE ? ValueNull.INSTANCE : this.getValue(var1, var2, this.a.getValue(var1), this.b.getValue(var1)));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      if (!this.whenOperand) {
         return super.getWhenValue(var1, var2);
      } else {
         return var2 == ValueNull.INSTANCE ? false : this.getValue(var1, var2, this.a.getValue(var1), this.b.getValue(var1)).isTrue();
      }
   }

   private Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      int var5 = var1.compareWithNull(var3, var2, false);
      int var6 = var1.compareWithNull(var2, var4, false);
      if (var5 == Integer.MIN_VALUE) {
         return (Value)(!this.symmetric && var6 > 0 ? ValueBoolean.get(this.not) : ValueNull.INSTANCE);
      } else if (var6 == Integer.MIN_VALUE) {
         return (Value)(!this.symmetric && var5 > 0 ? ValueBoolean.get(this.not) : ValueNull.INSTANCE);
      } else {
         return ValueBoolean.get(this.not ^ (this.symmetric ? var5 <= 0 && var6 <= 0 || var5 >= 0 && var6 >= 0 : var5 <= 0 && var6 <= 0));
      }
   }

   public boolean isWhenConditionOperand() {
      return this.whenOperand;
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return this.whenOperand ? null : new BetweenPredicate(this.left, !this.not, false, this.symmetric, this.a, this.b);
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.not && !this.whenOperand && !this.symmetric) {
         Comparison.createIndexConditions(var2, this.a, this.left, 4);
         Comparison.createIndexConditions(var2, this.left, this.b, 4);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      this.a.setEvaluatable(var1, var2);
      this.b.setEvaluatable(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      this.a.updateAggregate(var1, var2);
      this.b.updateAggregate(var1, var2);
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      this.a.mapColumns(var1, var2, var3);
      this.b.mapColumns(var1, var2, var3);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && this.a.isEverything(var1) && this.b.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost() + this.a.getCost() + this.b.getCost() + 1;
   }

   public int getSubexpressionCount() {
      return 3;
   }

   public Expression getSubexpression(int var1) {
      switch (var1) {
         case 0:
            return this.left;
         case 1:
            return this.a;
         case 2:
            return this.b;
         default:
            throw new IndexOutOfBoundsException();
      }
   }
}
