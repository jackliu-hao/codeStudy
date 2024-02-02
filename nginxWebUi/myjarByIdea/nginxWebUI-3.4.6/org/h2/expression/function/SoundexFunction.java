package org.h2.expression.function;

import java.nio.charset.StandardCharsets;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarchar;

public final class SoundexFunction extends Function1_2 {
   public static final int SOUNDEX = 0;
   public static final int DIFFERENCE = 1;
   private static final String[] NAMES = new String[]{"SOUNDEX", "DIFFERENCE"};
   private static final byte[] SOUNDEX_INDEX;
   private final int function;

   public SoundexFunction(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      Object var4;
      switch (this.function) {
         case 0:
            var4 = ValueVarchar.get(new String(getSoundex(var2.getString()), StandardCharsets.ISO_8859_1), var1);
            break;
         case 1:
            var4 = ValueInteger.get(getDifference(var2.getString(), var3.getString()));
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var4;
   }

   private static int getDifference(String var0, String var1) {
      byte[] var2 = getSoundex(var0);
      byte[] var3 = getSoundex(var1);
      int var4 = 0;

      for(int var5 = 0; var5 < 4; ++var5) {
         if (var2[var5] == var3[var5]) {
            ++var4;
         }
      }

      return var4;
   }

   private static byte[] getSoundex(String var0) {
      byte[] var1 = new byte[]{48, 48, 48, 48};
      byte var2 = 48;
      int var3 = 0;
      int var4 = 0;

      for(int var5 = var0.length(); var3 < var5 && var4 < 4; ++var3) {
         char var6 = var0.charAt(var3);
         if (var6 >= 'A' && var6 <= 'z') {
            byte var7 = SOUNDEX_INDEX[var6 - 65];
            if (var7 != 0) {
               if (var4 == 0) {
                  var1[var4++] = (byte)var6;
                  var2 = var7;
               } else if (var7 <= 54) {
                  if (var7 != var2) {
                     int var10001 = var4++;
                     var2 = var7;
                     var1[var10001] = var7;
                  }
               } else if (var7 == 55) {
                  var2 = var7;
               }
            }
         }
      }

      return var1;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      switch (this.function) {
         case 0:
            this.type = TypeInfo.getTypeInfo(2, 4L, 0, (ExtTypeInfo)null);
            break;
         case 1:
            this.type = TypeInfo.TYPE_INTEGER;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type));
   }

   public String getName() {
      return NAMES[this.function];
   }

   static {
      SOUNDEX_INDEX = "71237128722455712623718272\u0000\u0000\u0000\u0000\u0000\u000071237128722455712623718272".getBytes(StandardCharsets.ISO_8859_1);
   }
}
