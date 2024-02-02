package org.h2.expression.function;

import java.util.Random;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;
import org.h2.value.ValueUuid;
import org.h2.value.ValueVarbinary;

public final class RandFunction extends Function0_1 {
   public static final int RAND = 0;
   public static final int SECURE_RAND = 1;
   public static final int RANDOM_UUID = 2;
   private static final String[] NAMES = new String[]{"RAND", "SECURE_RAND", "RANDOM_UUID"};
   private final int function;

   public RandFunction(Expression var1, int var2) {
      super(var1);
      this.function = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2;
      if (this.arg != null) {
         var2 = this.arg.getValue(var1);
         if (var2 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
         }
      } else {
         var2 = null;
      }

      Object var4;
      switch (this.function) {
         case 0:
            Random var3 = var1.getRandom();
            if (var2 != null) {
               var3.setSeed((long)var2.getInt());
            }

            var4 = ValueDouble.get(var3.nextDouble());
            break;
         case 1:
            var4 = ValueVarbinary.getNoCopy(MathUtils.secureRandomBytes(var2.getInt()));
            break;
         case 2:
            var4 = ValueUuid.getNewRandom();
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var4;
   }

   public Expression optimize(SessionLocal var1) {
      if (this.arg != null) {
         this.arg = this.arg.optimize(var1);
      }

      switch (this.function) {
         case 0:
            this.type = TypeInfo.TYPE_DOUBLE;
            break;
         case 1:
            Value var2;
            this.type = this.arg.isConstant() && (var2 = this.arg.getValue(var1)) != ValueNull.INSTANCE ? TypeInfo.getTypeInfo(6, (long)Math.max(var2.getInt(), 1), 0, (ExtTypeInfo)null) : TypeInfo.TYPE_VARBINARY;
            break;
         case 2:
            this.type = TypeInfo.TYPE_UUID;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return this;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
            return false;
         default:
            return super.isEverything(var1);
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
