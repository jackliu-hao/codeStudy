package org.h2.expression.function;

import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class StringFunction extends FunctionN {
   public static final int LOCATE = 0;
   public static final int INSERT = 1;
   public static final int REPLACE = 2;
   public static final int LPAD = 3;
   public static final int RPAD = 4;
   public static final int TRANSLATE = 5;
   private static final String[] NAMES = new String[]{"LOCATE", "INSERT", "REPLACE", "LPAD", "RPAD", "TRANSLATE"};
   private final int function;

   public StringFunction(Expression var1, Expression var2, Expression var3, int var4) {
      super(var3 == null ? new Expression[]{var1, var2} : new Expression[]{var1, var2, var3});
      this.function = var4;
   }

   public StringFunction(Expression var1, Expression var2, Expression var3, Expression var4, int var5) {
      super(new Expression[]{var1, var2, var3, var4});
      this.function = var5;
   }

   public StringFunction(Expression[] var1, int var2) {
      super(var1);
      this.function = var2;
   }

   public Value getValue(SessionLocal var1) {
      Object var2 = this.args[0].getValue(var1);
      Value var3 = this.args[1].getValue(var1);
      String var4;
      Value var5;
      String var6;
      Value var9;
      switch (this.function) {
         case 0:
            if (var2 == ValueNull.INSTANCE || var3 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            var9 = this.args.length >= 3 ? this.args[2].getValue(var1) : null;
            if (var9 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            var2 = ValueInteger.get(locate(((Value)var2).getString(), var3.getString(), var9 == null ? 1 : var9.getInt()));
            break;
         case 1:
            var9 = this.args[2].getValue(var1);
            var5 = this.args[3].getValue(var1);
            if (var3 != ValueNull.INSTANCE && var9 != ValueNull.INSTANCE) {
               var6 = insert(((Value)var2).getString(), var3.getInt(), var9.getInt(), var5.getString());
               var2 = var6 != null ? ValueVarchar.get(var6, var1) : ValueNull.INSTANCE;
            }
            break;
         case 2:
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
               if (this.args.length >= 3) {
                  var5 = this.args[2].getValue(var1);
                  if (var5 == ValueNull.INSTANCE && var1.getMode().getEnum() != Mode.ModeEnum.Oracle) {
                     return ValueNull.INSTANCE;
                  }

                  var4 = var5.getString();
                  if (var4 == null) {
                     var4 = "";
                  }
               } else {
                  var4 = "";
               }

               var2 = ValueVarchar.get(StringUtils.replaceAll(((Value)var2).getString(), var3.getString(), var4), var1);
               break;
            }

            return ValueNull.INSTANCE;
         case 3:
         case 4:
            if (var2 == ValueNull.INSTANCE || var3 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            if (this.args.length >= 3) {
               var5 = this.args[2].getValue(var1);
               if (var5 == ValueNull.INSTANCE) {
                  return ValueNull.INSTANCE;
               }

               var4 = var5.getString();
            } else {
               var4 = null;
            }

            var2 = ValueVarchar.get(StringUtils.pad(((Value)var2).getString(), var3.getInt(), var4, this.function == 4), var1);
            break;
         case 5:
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
               var5 = this.args[2].getValue(var1);
               if (var5 == ValueNull.INSTANCE) {
                  return ValueNull.INSTANCE;
               }

               var6 = var3.getString();
               String var7 = var5.getString();
               if (var1.getMode().getEnum() == Mode.ModeEnum.DB2) {
                  String var8 = var6;
                  var6 = var7;
                  var7 = var8;
               }

               var2 = ValueVarchar.get(translate(((Value)var2).getString(), var6, var7), var1);
               break;
            }

            return ValueNull.INSTANCE;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var2;
   }

   private static int locate(String var0, String var1, int var2) {
      return var2 < 0 ? var1.lastIndexOf(var0, var1.length() + var2) + 1 : var1.indexOf(var0, var2 == 0 ? 0 : var2 - 1) + 1;
   }

   private static String insert(String var0, int var1, int var2, String var3) {
      if (var0 == null) {
         return var3;
      } else if (var3 == null) {
         return var0;
      } else {
         int var4 = var0.length();
         int var5 = var3.length();
         --var1;
         if (var1 >= 0 && var2 > 0 && var5 != 0 && var1 <= var4) {
            if (var1 + var2 > var4) {
               var2 = var4 - var1;
            }

            return var0.substring(0, var1) + var3 + var0.substring(var1 + var2);
         } else {
            return var0;
         }
      }
   }

   private static String translate(String var0, String var1, String var2) {
      if (!StringUtils.isNullOrEmpty(var0) && !StringUtils.isNullOrEmpty(var1)) {
         StringBuilder var3 = null;
         int var4 = var2 == null ? 0 : var2.length();
         int var5 = 0;

         for(int var6 = var0.length(); var5 < var6; ++var5) {
            char var7 = var0.charAt(var5);
            int var8 = var1.indexOf(var7);
            if (var8 >= 0) {
               if (var3 == null) {
                  var3 = new StringBuilder(var6);
                  if (var5 > 0) {
                     var3.append(var0, 0, var5);
                  }
               }

               if (var8 < var4) {
                  var7 = var2.charAt(var8);
               }
            }

            if (var3 != null) {
               var3.append(var7);
            }
         }

         return var3 == null ? var0 : var3.toString();
      } else {
         return var0;
      }
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      switch (this.function) {
         case 0:
            this.type = TypeInfo.TYPE_INTEGER;
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            this.type = TypeInfo.TYPE_VARCHAR;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(var2 ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
