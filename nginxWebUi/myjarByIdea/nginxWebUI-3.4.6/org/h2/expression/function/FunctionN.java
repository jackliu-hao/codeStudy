package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.OperationN;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public abstract class FunctionN extends OperationN implements NamedExpression {
   protected FunctionN(Expression[] var1) {
      super(var1);
   }

   public Value getValue(SessionLocal var1) {
      int var5 = this.args.length;
      Value var2;
      Value var3;
      Value var4;
      if (var5 >= 1) {
         var2 = this.args[0].getValue(var1);
         if (var2 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
         }

         if (var5 >= 2) {
            var3 = this.args[1].getValue(var1);
            if (var3 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            if (var5 >= 3) {
               var4 = this.args[2].getValue(var1);
               if (var4 == ValueNull.INSTANCE) {
                  return ValueNull.INSTANCE;
               }
            } else {
               var4 = null;
            }
         } else {
            var3 = null;
            var4 = null;
         }
      } else {
         var2 = null;
         var3 = null;
         var4 = null;
      }

      return this.getValue(var1, var2, var3, var4);
   }

   protected Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      throw DbException.getInternalError();
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return writeExpressions(var1.append(this.getName()).append('('), this.args, var2).append(')');
   }
}
