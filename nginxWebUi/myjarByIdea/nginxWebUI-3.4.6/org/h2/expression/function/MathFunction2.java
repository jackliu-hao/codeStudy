package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDouble;

public final class MathFunction2 extends Function2 {
   public static final int ATAN2 = 0;
   public static final int LOG = 1;
   public static final int POWER = 2;
   private static final String[] NAMES = new String[]{"ATAN2", "LOG", "POWER"};
   private final int function;

   public MathFunction2(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      double var4 = var2.getDouble();
      double var6 = var3.getDouble();
      switch (this.function) {
         case 0:
            var4 = Math.atan2(var4, var6);
            break;
         case 1:
            if (var1.getMode().swapLogFunctionParameters) {
               double var8 = var6;
               var6 = var4;
               var4 = var8;
            }

            if (var6 <= 0.0) {
               throw DbException.getInvalidValueException("LOG() argument", var6);
            }

            if (!(var4 <= 0.0) && var4 != 1.0) {
               if (var4 == Math.E) {
                  var4 = Math.log(var6);
               } else if (var4 == 10.0) {
                  var4 = Math.log10(var6);
               } else {
                  var4 = Math.log(var6) / Math.log(var4);
               }
               break;
            }

            throw DbException.getInvalidValueException("LOG() base", var4);
         case 2:
            var4 = Math.pow(var4, var6);
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return ValueDouble.get(var4);
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      this.type = TypeInfo.TYPE_DOUBLE;
      return (Expression)(this.left.isConstant() && this.right.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
