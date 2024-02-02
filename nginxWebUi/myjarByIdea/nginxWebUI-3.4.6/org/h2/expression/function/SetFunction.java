package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Variable;
import org.h2.message.DbException;
import org.h2.value.Value;

public final class SetFunction extends Function2 {
   public SetFunction(Expression var1, Expression var2) {
      super(var1, var2);
   }

   public Value getValue(SessionLocal var1) {
      Variable var2 = (Variable)this.left;
      Value var3 = this.right.getValue(var1);
      var1.setVariable(var2.getName(), var3);
      return var3;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      this.type = this.right.getType();
      if (!(this.left instanceof Variable)) {
         throw DbException.get(90137, this.left.getTraceSQL());
      } else {
         return this;
      }
   }

   public String getName() {
      return "SET";
   }

   public boolean isEverything(ExpressionVisitor var1) {
      if (!super.isEverything(var1)) {
         return false;
      } else {
         switch (var1.getType()) {
            case 2:
            case 5:
            case 8:
               return false;
            default:
               return true;
         }
      }
   }
}
