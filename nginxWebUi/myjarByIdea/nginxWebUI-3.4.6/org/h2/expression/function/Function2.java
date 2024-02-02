package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Operation2;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public abstract class Function2 extends Operation2 implements NamedExpression {
   protected Function2(Expression var1, Expression var2) {
      super(var1, var2);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         Value var3 = this.right.getValue(var1);
         return (Value)(var3 == ValueNull.INSTANCE ? ValueNull.INSTANCE : this.getValue(var1, var2, var3));
      }
   }

   protected Value getValue(SessionLocal var1, Value var2, Value var3) {
      throw DbException.getInternalError();
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.left.getUnenclosedSQL(var1.append(this.getName()).append('('), var2).append(", ");
      return this.right.getUnenclosedSQL(var1, var2).append(')');
   }
}
