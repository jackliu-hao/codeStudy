package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Operation1_2;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public abstract class Function1_2 extends Operation1_2 implements NamedExpression {
   protected Function1_2(Expression var1, Expression var2) {
      super(var1, var2);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         Value var3;
         if (this.right != null) {
            var3 = this.right.getValue(var1);
            if (var3 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }
         } else {
            var3 = null;
         }

         return this.getValue(var1, var2, var3);
      }
   }

   protected Value getValue(SessionLocal var1, Value var2, Value var3) {
      throw DbException.getInternalError();
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.left.getUnenclosedSQL(var1.append(this.getName()).append('('), var2);
      if (this.right != null) {
         this.right.getUnenclosedSQL(var1.append(", "), var2);
      }

      return var1.append(')');
   }
}
