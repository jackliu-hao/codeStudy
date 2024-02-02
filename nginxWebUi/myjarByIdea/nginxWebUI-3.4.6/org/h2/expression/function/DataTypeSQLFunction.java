package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.schema.Constant;
import org.h2.schema.Domain;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter2;
import org.h2.value.ValueVarchar;

public final class DataTypeSQLFunction extends FunctionN {
   public DataTypeSQLFunction(Expression var1, Expression var2, Expression var3, Expression var4) {
      super(new Expression[]{var1, var2, var3, var4});
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3, Value var4) {
      Schema var5 = var1.getDatabase().findSchema(var2.getString());
      if (var5 == null) {
         return ValueNull.INSTANCE;
      } else {
         String var6 = var3.getString();
         String var7 = var4.getString();
         String var8 = this.args[3].getValue(var1).getString();
         if (var8 == null) {
            return ValueNull.INSTANCE;
         } else {
            TypeInfo var9;
            switch (var7) {
               case "CONSTANT":
                  Constant var24 = var5.findConstant(var6);
                  if (var24 == null || !var8.equals("TYPE")) {
                     return ValueNull.INSTANCE;
                  }

                  var9 = var24.getValue().getType();
                  break;
               case "DOMAIN":
                  Domain var23 = var5.findDomain(var6);
                  if (var23 == null || !var8.equals("TYPE")) {
                     return ValueNull.INSTANCE;
                  }

                  var9 = var23.getDataType();
                  break;
               case "ROUTINE":
                  int var22 = var6.lastIndexOf(95);
                  if (var22 < 0) {
                     return ValueNull.INSTANCE;
                  }

                  FunctionAlias var25 = var5.findFunction(var6.substring(0, var22));
                  if (var25 == null) {
                     return ValueNull.INSTANCE;
                  }

                  int var26;
                  try {
                     var26 = Integer.parseInt(var6.substring(var22 + 1));
                  } catch (NumberFormatException var21) {
                     return ValueNull.INSTANCE;
                  }

                  FunctionAlias.JavaMethod[] var15;
                  try {
                     var15 = var25.getJavaMethods();
                  } catch (DbException var20) {
                     return ValueNull.INSTANCE;
                  }

                  if (var26 < 1 || var26 > var15.length) {
                     return ValueNull.INSTANCE;
                  }

                  FunctionAlias.JavaMethod var16 = var15[var26 - 1];
                  if (var8.equals("RESULT")) {
                     var9 = var16.getDataType();
                  } else {
                     try {
                        var26 = Integer.parseInt(var8);
                     } catch (NumberFormatException var19) {
                        return ValueNull.INSTANCE;
                     }

                     if (var26 < 1) {
                        return ValueNull.INSTANCE;
                     }

                     if (!var16.hasConnectionParam()) {
                        --var26;
                     }

                     Class[] var17 = var16.getColumnClasses();
                     if (var26 >= var17.length) {
                        return ValueNull.INSTANCE;
                     }

                     var9 = ValueToObjectConverter2.classToType(var17[var26]);
                  }
                  break;
               case "TABLE":
                  Table var12 = var5.findTableOrView(var1, var6);
                  if (var12 == null) {
                     return ValueNull.INSTANCE;
                  }

                  int var13;
                  try {
                     var13 = Integer.parseInt(var8);
                  } catch (NumberFormatException var18) {
                     return ValueNull.INSTANCE;
                  }

                  Column[] var14 = var12.getColumns();
                  if (var13 < 1 || var13 > var14.length) {
                     return ValueNull.INSTANCE;
                  }

                  var9 = var14[var13 - 1].getType();
                  break;
               default:
                  return ValueNull.INSTANCE;
            }

            return ValueVarchar.get(var9.getSQL(0));
         }
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.optimizeArguments(var1, false);
      this.type = TypeInfo.TYPE_VARCHAR;
      return this;
   }

   public String getName() {
      return "DATA_TYPE_SQL";
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
            return false;
         default:
            return true;
      }
   }
}
