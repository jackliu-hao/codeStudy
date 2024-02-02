package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

public final class MathFunction1 extends Function1 {
   public static final int SIN = 0;
   public static final int COS = 1;
   public static final int TAN = 2;
   public static final int COT = 3;
   public static final int SINH = 4;
   public static final int COSH = 5;
   public static final int TANH = 6;
   public static final int ASIN = 7;
   public static final int ACOS = 8;
   public static final int ATAN = 9;
   public static final int LOG10 = 10;
   public static final int LN = 11;
   public static final int EXP = 12;
   public static final int SQRT = 13;
   public static final int DEGREES = 14;
   public static final int RADIANS = 15;
   private static final String[] NAMES = new String[]{"SIN", "COS", "TAN", "COT", "SINH", "COSH", "TANH", "ASIN", "ACOS", "ATAN", "LOG10", "LN", "EXP", "SQRT", "DEGREES", "RADIANS"};
   private final int function;

   public MathFunction1(Expression var1, int var2) {
      super(var1);
      this.function = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         double var3 = var2.getDouble();
         switch (this.function) {
            case 0:
               var3 = Math.sin(var3);
               break;
            case 1:
               var3 = Math.cos(var3);
               break;
            case 2:
               var3 = Math.tan(var3);
               break;
            case 3:
               var3 = Math.tan(var3);
               if (var3 == 0.0) {
                  throw DbException.get(22012, (String)this.getTraceSQL());
               }

               var3 = 1.0 / var3;
               break;
            case 4:
               var3 = Math.sinh(var3);
               break;
            case 5:
               var3 = Math.cosh(var3);
               break;
            case 6:
               var3 = Math.tanh(var3);
               break;
            case 7:
               var3 = Math.asin(var3);
               break;
            case 8:
               var3 = Math.acos(var3);
               break;
            case 9:
               var3 = Math.atan(var3);
               break;
            case 10:
               if (var3 <= 0.0) {
                  throw DbException.getInvalidValueException("LOG10() argument", var3);
               }

               var3 = Math.log10(var3);
               break;
            case 11:
               if (var3 <= 0.0) {
                  throw DbException.getInvalidValueException("LN() argument", var3);
               }

               var3 = Math.log(var3);
               break;
            case 12:
               var3 = Math.exp(var3);
               break;
            case 13:
               var3 = Math.sqrt(var3);
               break;
            case 14:
               var3 = Math.toDegrees(var3);
               break;
            case 15:
               var3 = Math.toRadians(var3);
               break;
            default:
               throw DbException.getInternalError("function=" + this.function);
         }

         return ValueDouble.get(var3);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      this.type = TypeInfo.TYPE_DOUBLE;
      return (Expression)(this.arg.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
