package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

public abstract class Operation1 extends Expression {
   protected Expression arg;
   protected TypeInfo type;

   protected Operation1(Expression var1) {
      this.arg = var1;
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.arg.mapColumns(var1, var2, var3);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.arg.setEvaluatable(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.arg.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.arg.isEverything(var1);
   }

   public int getCost() {
      return this.arg.getCost() + 1;
   }

   public int getSubexpressionCount() {
      return 1;
   }

   public Expression getSubexpression(int var1) {
      if (var1 == 0) {
         return this.arg;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
