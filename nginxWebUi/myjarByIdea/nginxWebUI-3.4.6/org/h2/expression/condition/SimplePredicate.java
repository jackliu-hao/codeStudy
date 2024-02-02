package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;

public abstract class SimplePredicate extends Condition {
   Expression left;
   final boolean not;
   final boolean whenOperand;

   SimplePredicate(Expression var1, boolean var2, boolean var3) {
      this.left = var1;
      this.not = var2;
      this.whenOperand = var3;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      return (Expression)(!this.whenOperand && this.left.isConstant() ? ValueExpression.getBoolean(this.getValue(var1)) : this);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
   }

   public final boolean needParentheses() {
      return true;
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost() + 1;
   }

   public int getSubexpressionCount() {
      return 1;
   }

   public Expression getSubexpression(int var1) {
      if (var1 == 0) {
         return this.left;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final boolean isWhenConditionOperand() {
      return this.whenOperand;
   }
}
