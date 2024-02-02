package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

public final class StringFunction2 extends Function2 {
   public static final int LEFT = 0;
   public static final int RIGHT = 1;
   public static final int REPEAT = 2;
   private static final String[] NAMES = new String[]{"LEFT", "RIGHT", "REPEAT"};
   private final int function;

   public StringFunction2(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      String var4 = var2.getString();
      int var5 = var3.getInt();
      if (var5 <= 0) {
         return ValueVarchar.get("", var1);
      } else {
         int var6 = var4.length();
         switch (this.function) {
            case 0:
               if (var5 > var6) {
                  var5 = var6;
               }

               var4 = var4.substring(0, var5);
               break;
            case 1:
               if (var5 > var6) {
                  var5 = var6;
               }

               var4 = var4.substring(var6 - var5);
               break;
            case 2:
               StringBuilder var7 = new StringBuilder(var6 * var5);

               while(var5-- > 0) {
                  var7.append(var4);
               }

               var4 = var7.toString();
               break;
            default:
               throw DbException.getInternalError("function=" + this.function);
         }

         return ValueVarchar.get(var4, var1);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      switch (this.function) {
         case 0:
         case 1:
            this.type = TypeInfo.getTypeInfo(2, this.left.getType().getPrecision(), 0, (ExtTypeInfo)null);
            break;
         case 2:
            this.type = TypeInfo.TYPE_VARCHAR;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(this.left.isConstant() && this.right.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
