package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

public abstract class Operation1_2 extends Expression {
   protected Expression left;
   protected Expression right;
   protected TypeInfo type;

   protected Operation1_2(Expression var1, Expression var2) {
      this.left = var1;
      this.right = var2;
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      if (this.right != null) {
         this.right.mapColumns(var1, var2, var3);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      if (this.right != null) {
         this.right.setEvaluatable(var1, var2);
      }

   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      if (this.right != null) {
         this.right.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && (this.right == null || this.right.isEverything(var1));
   }

   public int getCost() {
      int var1 = this.left.getCost() + 1;
      if (this.right != null) {
         var1 += this.right.getCost();
      }

      return var1;
   }

   public int getSubexpressionCount() {
      return this.right != null ? 2 : 1;
   }

   public Expression getSubexpression(int var1) {
      if (var1 == 0) {
         return this.left;
      } else if (var1 == 1 && this.right != null) {
         return this.right;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
