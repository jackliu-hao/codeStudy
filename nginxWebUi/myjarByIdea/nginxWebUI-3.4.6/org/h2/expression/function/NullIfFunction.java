package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class NullIfFunction extends Function2 {
   public NullIfFunction(Expression var1, Expression var2) {
      super(var1, var2);
   }

   public Value getValue(SessionLocal var1) {
      Object var2 = this.left.getValue(var1);
      if (var1.compareWithNull((Value)var2, this.right.getValue(var1), true) == 0) {
         var2 = ValueNull.INSTANCE;
      }

      return (Value)var2;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      this.type = this.left.getType();
      TypeInfo.checkComparable(this.type, this.right.getType());
      return (Expression)(this.left.isConstant() && this.right.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return "NULLIF";
   }
}
