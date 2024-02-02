package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class CoalesceFunction extends FunctionN {
   public static final int COALESCE = 0;
   public static final int GREATEST = 1;
   public static final int LEAST = 2;
   private static final String[] NAMES = new String[]{"COALESCE", "GREATEST", "LEAST"};
   private final int function;

   public CoalesceFunction(int var1) {
      this(var1);
   }

   public CoalesceFunction(int var1, Expression... var2) {
      super(var2);
      this.function = var1;
   }

   public Value getValue(SessionLocal var1) {
      Object var2 = ValueNull.INSTANCE;
      int var3;
      int var4;
      Value var5;
      switch (this.function) {
         case 0:
            var3 = 0;

            for(var4 = this.args.length; var3 < var4; ++var3) {
               var5 = this.args[var3].getValue(var1);
               if (var5 != ValueNull.INSTANCE) {
                  var2 = var5.convertTo(this.type, var1);
                  return (Value)var2;
               }
            }

            return (Value)var2;
         case 1:
         case 2:
            var3 = 0;

            for(var4 = this.args.length; var3 < var4; ++var3) {
               var5 = this.args[var3].getValue(var1);
               if (var5 != ValueNull.INSTANCE) {
                  var5 = var5.convertTo(this.type, var1);
                  if (var2 == ValueNull.INSTANCE) {
                     var2 = var5;
                  } else {
                     int var6 = var1.compareTypeSafe((Value)var2, var5);
                     if (this.function == 1) {
                        if (var6 < 0) {
                           var2 = var5;
                        }
                     } else if (var6 > 0) {
                        var2 = var5;
                     }
                  }
               }
            }

            return (Value)var2;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      this.type = TypeInfo.getHigherType(this.args);
      if (this.type.getValueType() <= 0) {
         this.type = TypeInfo.TYPE_VARCHAR;
      }

      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
