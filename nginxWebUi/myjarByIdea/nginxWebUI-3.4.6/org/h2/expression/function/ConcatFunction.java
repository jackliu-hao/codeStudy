package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class ConcatFunction extends FunctionN {
   public static final int CONCAT = 0;
   public static final int CONCAT_WS = 1;
   private static final String[] NAMES = new String[]{"CONCAT", "CONCAT_WS"};
   private final int function;

   public ConcatFunction(int var1) {
      this(var1);
   }

   public ConcatFunction(int var1, Expression... var2) {
      super(var2);
      this.function = var1;
   }

   public Value getValue(SessionLocal var1) {
      int var2 = 0;
      String var3 = null;
      if (this.function == 1) {
         var2 = 1;
         var3 = this.args[0].getValue(var1).getString();
      }

      StringBuilder var4 = new StringBuilder();
      boolean var5 = false;

      for(int var6 = this.args.length; var2 < var6; ++var2) {
         Value var7 = this.args[var2].getValue(var1);
         if (var7 != ValueNull.INSTANCE) {
            if (var3 != null) {
               if (var5) {
                  var4.append(var3);
               }

               var5 = true;
            }

            var4.append(var7.getString());
         }
      }

      return ValueVarchar.get(var4.toString(), var1);
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      int var3 = 0;
      long var4 = 0L;
      if (this.function == 1) {
         var3 = 1;
         var4 = this.getPrecision(0);
      }

      long var6 = 0L;
      int var8 = this.args.length;

      for(boolean var9 = false; var3 < var8; ++var3) {
         if (!this.args[var3].isNullConstant()) {
            var6 = DataType.addPrecision(var6, this.getPrecision(var3));
            if (var4 != 0L && var9) {
               var6 = DataType.addPrecision(var6, var4);
            }

            var9 = true;
         }
      }

      this.type = TypeInfo.getTypeInfo(2, var6, 0, (ExtTypeInfo)null);
      if (var2) {
         return TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type);
      } else {
         return this;
      }
   }

   private long getPrecision(int var1) {
      TypeInfo var2 = this.args[var1].getType();
      int var3 = var2.getValueType();
      if (var3 == 0) {
         return 0L;
      } else {
         return DataType.isCharacterStringType(var3) ? var2.getPrecision() : Long.MAX_VALUE;
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
