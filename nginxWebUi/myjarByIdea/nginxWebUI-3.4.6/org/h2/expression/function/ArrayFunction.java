package org.h2.expression.function;

import java.util.Arrays;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueNull;

public final class ArrayFunction extends FunctionN {
   public static final int TRIM_ARRAY = 0;
   public static final int ARRAY_CONTAINS = 1;
   public static final int ARRAY_SLICE = 2;
   private static final String[] NAMES = new String[]{"TRIM_ARRAY", "ARRAY_CONTAINS", "ARRAY_SLICE"};
   private final int function;

   public ArrayFunction(Expression var1, Expression var2, Expression var3, int var4) {
      super(var3 == null ? new Expression[]{var1, var2} : new Expression[]{var1, var2, var3});
      this.function = var4;
   }

   public Value getValue(SessionLocal var1) {
      Object var2 = this.args[0].getValue(var1);
      Value var3 = this.args[1].getValue(var1);
      ValueArray var5;
      int var7;
      int var10;
      Value[] var12;
      switch (this.function) {
         case 0:
            if (var3 == ValueNull.INSTANCE) {
               var2 = ValueNull.INSTANCE;
            } else {
               var10 = var3.getInt();
               if (var10 < 0) {
                  throw DbException.get(22034, (String[])(Integer.toString(var10), "0..CARDINALITY(array)"));
               }

               if (var2 != ValueNull.INSTANCE) {
                  var5 = ((Value)var2).convertToAnyArray(var1);
                  var12 = var5.getList();
                  var7 = var12.length;
                  if (var10 > var7) {
                     throw DbException.get(22034, (String[])(Integer.toString(var10), "0.." + var7));
                  }

                  if (var10 == 0) {
                     var2 = var5;
                  } else {
                     var2 = ValueArray.get(var5.getComponentType(), (Value[])Arrays.copyOf(var12, var7 - var10), var1);
                  }
               }
            }
            break;
         case 1:
            var10 = ((Value)var2).getValueType();
            if (var10 != 40 && var10 != 41) {
               var2 = ValueNull.INSTANCE;
               break;
            } else {
               Value[] var11 = ((ValueCollectionBase)var2).getList();
               var2 = ValueBoolean.FALSE;
               var12 = var11;
               var7 = var11.length;

               for(int var13 = 0; var13 < var7; ++var13) {
                  Value var9 = var12[var13];
                  if (var1.areEqual(var9, var3)) {
                     var2 = ValueBoolean.TRUE;
                     return (Value)var2;
                  }
               }

               return (Value)var2;
            }
         case 2:
            Value var4;
            if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE && (var4 = this.args[2].getValue(var1)) != ValueNull.INSTANCE) {
               var5 = ((Value)var2).convertToAnyArray(var1);
               int var6 = var3.getInt() - 1;
               var7 = var4.getInt();
               boolean var8 = var1.getMode().getEnum() == Mode.ModeEnum.PostgreSQL;
               if (var6 > var7) {
                  var2 = var8 ? ValueArray.get(var5.getComponentType(), Value.EMPTY_VALUES, var1) : ValueNull.INSTANCE;
               } else {
                  if (var6 < 0) {
                     if (!var8) {
                        var2 = ValueNull.INSTANCE;
                        break;
                     }

                     var6 = 0;
                  }

                  if (var7 > var5.getList().length) {
                     if (!var8) {
                        var2 = ValueNull.INSTANCE;
                        break;
                     }

                     var7 = var5.getList().length;
                  }

                  var2 = ValueArray.get(var5.getComponentType(), (Value[])Arrays.copyOfRange(var5.getList(), var6, var7), var1);
               }
            } else {
               var2 = ValueNull.INSTANCE;
            }
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var2;
   }

   public Expression optimize(SessionLocal var1) {
      boolean var2 = this.optimizeArguments(var1, true);
      switch (this.function) {
         case 0:
         case 2:
            Expression var3 = this.args[0];
            this.type = var3.getType();
            int var4 = this.type.getValueType();
            if (var4 != 40 && var4 != 0) {
               throw DbException.getInvalidExpressionTypeException(this.getName() + " array argument", var3);
            }
            break;
         case 1:
            this.type = TypeInfo.TYPE_BOOLEAN;
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
