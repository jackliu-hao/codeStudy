package org.h2.expression.function.table;

import java.util.ArrayList;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.value.Value;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

public final class ArrayTableFunction extends TableFunction {
   public static final int UNNEST = 0;
   public static final int TABLE = 1;
   public static final int TABLE_DISTINCT = 2;
   private Column[] columns;
   private static final String[] NAMES = new String[]{"UNNEST", "TABLE", "TABLE_DISTINCT"};
   private final int function;

   public ArrayTableFunction(int var1) {
      super(new Expression[1]);
      this.function = var1;
   }

   public ResultInterface getValue(SessionLocal var1) {
      return this.getTable(var1, false);
   }

   public void optimize(SessionLocal var1) {
      super.optimize(var1);
      if (this.args.length < 1) {
         throw DbException.get(7001, (String[])(this.getName(), ">0"));
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      if (this.function == 0) {
         super.getSQL(var1, var2);
         if (this.args.length < this.columns.length) {
            var1.append(" WITH ORDINALITY");
         }
      } else {
         var1.append(this.getName()).append('(');

         for(int var3 = 0; var3 < this.args.length; ++var3) {
            if (var3 > 0) {
               var1.append(", ");
            }

            var1.append(this.columns[var3].getCreateSQL()).append('=');
            this.args[var3].getUnenclosedSQL(var1, var2);
         }

         var1.append(')');
      }

      return var1;
   }

   public ResultInterface getValueTemplate(SessionLocal var1) {
      return this.getTable(var1, true);
   }

   public void setColumns(ArrayList<Column> var1) {
      this.columns = (Column[])var1.toArray(new Column[0]);
   }

   private ResultInterface getTable(SessionLocal var1, boolean var2) {
      int var3 = this.columns.length;
      Expression[] var4 = new Expression[var3];
      Database var5 = var1.getDatabase();

      for(int var6 = 0; var6 < var3; ++var6) {
         Column var7 = this.columns[var6];
         ExpressionColumn var8 = new ExpressionColumn(var5, var7);
         var4[var6] = var8;
      }

      LocalResult var18 = new LocalResult(var1, var4, var3, var3);
      if (!var2 && this.function == 2) {
         var18.setDistinct();
      }

      if (!var2) {
         int var19 = var3;
         boolean var20 = this.function == 0;
         boolean var9 = false;
         if (var20) {
            var19 = this.args.length;
            if (var19 < var3) {
               var9 = true;
            }
         }

         Value[][] var10 = new Value[var19][];
         int var11 = 0;

         int var12;
         int var14;
         Value[] var15;
         for(var12 = 0; var12 < var19; ++var12) {
            Object var13 = this.args[var12].getValue(var1);
            if (var13 == ValueNull.INSTANCE) {
               var10[var12] = Value.EMPTY_VALUES;
            } else {
               var14 = ((Value)var13).getValueType();
               if (var14 != 40 && var14 != 41) {
                  var13 = ((Value)var13).convertToAnyArray(var1);
               }

               var15 = ((ValueCollectionBase)var13).getList();
               var10[var12] = var15;
               var11 = Math.max(var11, var15.length);
            }
         }

         for(var12 = 0; var12 < var11; ++var12) {
            Value[] var21 = new Value[var3];

            for(var14 = 0; var14 < var19; ++var14) {
               var15 = var10[var14];
               Object var16;
               if (var15.length <= var12) {
                  var16 = ValueNull.INSTANCE;
               } else {
                  Column var17 = this.columns[var14];
                  var16 = var15[var12];
                  if (!var20) {
                     var16 = ((Value)var16).convertForAssignTo(var17.getType(), var1, var17);
                  }
               }

               var21[var14] = (Value)var16;
            }

            if (var9) {
               var21[var19] = ValueInteger.get(var12 + 1);
            }

            var18.addRow(var21);
         }
      }

      var18.done();
      return var18;
   }

   public String getName() {
      return NAMES[this.function];
   }

   public boolean isDeterministic() {
      return true;
   }

   public int getFunctionType() {
      return this.function;
   }
}
