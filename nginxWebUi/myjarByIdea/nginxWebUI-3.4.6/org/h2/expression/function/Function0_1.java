package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

public abstract class Function0_1 extends Expression implements NamedExpression {
   protected Expression arg;
   protected TypeInfo type;

   protected Function0_1(Expression var1) {
      this.arg = var1;
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      if (this.arg != null) {
         this.arg.mapColumns(var1, var2, var3);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      if (this.arg != null) {
         this.arg.setEvaluatable(var1, var2);
      }

   }

   public void updateAggregate(SessionLocal var1, int var2) {
      if (this.arg != null) {
         this.arg.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.arg == null || this.arg.isEverything(var1);
   }

   public int getCost() {
      int var1 = 1;
      if (this.arg != null) {
         var1 += this.arg.getCost();
      }

      return var1;
   }

   public int getSubexpressionCount() {
      return this.arg != null ? 1 : 0;
   }

   public Expression getSubexpression(int var1) {
      if (var1 == 0 && this.arg != null) {
         return this.arg;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append(this.getName()).append('(');
      if (this.arg != null) {
         this.arg.getUnenclosedSQL(var1, var2);
      }

      return var1.append(')');
   }
}
