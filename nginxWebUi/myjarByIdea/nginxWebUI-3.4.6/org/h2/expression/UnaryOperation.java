package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public class UnaryOperation extends Operation1 {
   public UnaryOperation(Expression var1) {
      super(var1);
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.arg.getSQL(var1.append("- "), var2, 0);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1).convertTo(this.type, var1);
      return var2 == ValueNull.INSTANCE ? var2 : var2.negate();
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      this.type = this.arg.getType();
      if (this.type.getValueType() == -1) {
         this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
      } else if (this.type.getValueType() == 36) {
         this.type = TypeInfo.TYPE_INTEGER;
      }

      return (Expression)(this.arg.isConstant() ? ValueExpression.get(this.getValue(var1)) : this);
   }
}
