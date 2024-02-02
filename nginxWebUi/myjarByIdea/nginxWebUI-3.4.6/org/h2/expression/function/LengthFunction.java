package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

public final class LengthFunction extends Function1 {
   public static final int CHAR_LENGTH = 0;
   public static final int OCTET_LENGTH = 1;
   public static final int BIT_LENGTH = 2;
   private static final String[] NAMES = new String[]{"CHAR_LENGTH", "OCTET_LENGTH", "BIT_LENGTH"};
   private final int function;

   public LengthFunction(Expression var1, int var2) {
      super(var1);
      this.function = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         long var3;
         switch (this.function) {
            case 0:
               var3 = var2.charLength();
               break;
            case 1:
               var3 = var2.octetLength();
               break;
            case 2:
               var3 = var2.octetLength() * 8L;
               break;
            default:
               throw DbException.getInternalError("function=" + this.function);
         }

         return ValueBigint.get(var3);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      this.type = TypeInfo.TYPE_BIGINT;
      return (Expression)(this.arg.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
