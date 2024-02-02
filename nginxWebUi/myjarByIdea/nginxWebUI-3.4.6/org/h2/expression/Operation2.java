package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

public abstract class Operation2 extends Expression {
   protected Expression left;
   protected Expression right;
   protected TypeInfo type;

   protected Operation2(Expression var1, Expression var2) {
      this.left = var1;
      this.right = var2;
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      this.right.mapColumns(var1, var2, var3);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      this.right.setEvaluatable(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      this.right.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && this.right.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost() + this.right.getCost() + 1;
   }

   public int getSubexpressionCount() {
      return 2;
   }

   public Expression getSubexpression(int var1) {
      switch (var1) {
         case 0:
            return this.left;
         case 1:
            return this.right;
         default:
            throw new IndexOutOfBoundsException();
      }
   }
}
