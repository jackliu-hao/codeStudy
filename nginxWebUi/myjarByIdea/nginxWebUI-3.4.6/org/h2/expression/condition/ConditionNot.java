package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public class ConditionNot extends Condition {
   private Expression condition;

   public ConditionNot(Expression var1) {
      this.condition = var1;
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      return castToBoolean(var1, this.condition.optimize(var1));
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.condition.getValue(var1);
      return var2 == ValueNull.INSTANCE ? var2 : var2.convertToBoolean().negate();
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.condition.mapColumns(var1, var2, var3);
   }

   public Expression optimize(SessionLocal var1) {
      Expression var2 = this.condition.getNotIfPossible(var1);
      if (var2 != null) {
         return var2.optimize(var1);
      } else {
         Expression var3 = this.condition.optimize(var1);
         if (var3.isConstant()) {
            Value var4 = var3.getValue(var1);
            return (Expression)(var4 == ValueNull.INSTANCE ? TypedValueExpression.UNKNOWN : ValueExpression.getBoolean(!var4.getBoolean()));
         } else {
            this.condition = var3;
            return this;
         }
      }
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.condition.setEvaluatable(var1, var2);
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.condition.getSQL(var1.append("NOT "), var2, 0);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.condition.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.condition.isEverything(var1);
   }

   public int getCost() {
      return this.condition.getCost();
   }

   public int getSubexpressionCount() {
      return 1;
   }

   public Expression getSubexpression(int var1) {
      if (var1 == 0) {
         return this.condition;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
