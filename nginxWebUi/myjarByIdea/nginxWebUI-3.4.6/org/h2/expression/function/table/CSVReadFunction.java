package org.h2.expression.function.table;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.function.CSVWriteFunction;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.FunctionAlias;
import org.h2.tools.Csv;
import org.h2.util.StringUtils;

public final class CSVReadFunction extends TableFunction {
   public CSVReadFunction() {
      super(new Expression[4]);
   }

   public ResultInterface getValue(SessionLocal var1) {
      var1.getUser().checkAdmin();
      String var2 = this.getValue(var1, 0);
      String var3 = this.getValue(var1, 1);
      Csv var4 = new Csv();
      String var5 = this.getValue(var1, 2);
      String var6 = null;
      if (var5 != null && var5.indexOf(61) >= 0) {
         var6 = var4.setOptions(var5);
      } else {
         var6 = var5;
         String var7 = this.getValue(var1, 3);
         String var8 = this.getValue(var1, 4);
         String var9 = this.getValue(var1, 5);
         String var10 = this.getValue(var1, 6);
         CSVWriteFunction.setCsvDelimiterEscape(var4, var7, var8, var9);
         var4.setNullString(var10);
      }

      char var12 = var4.getFieldSeparatorRead();
      String[] var13 = StringUtils.arraySplit(var3, var12, true);

      try {
         return FunctionAlias.JavaMethod.resultSetToResult(var1, var4.read(var2, var13, var6), Integer.MAX_VALUE);
      } catch (SQLException var11) {
         throw DbException.convert(var11);
      }
   }

   private String getValue(SessionLocal var1, int var2) {
      return getValue(var1, this.args, var2);
   }

   public void optimize(SessionLocal var1) {
      super.optimize(var1);
      int var2 = this.args.length;
      if (var2 < 1 || var2 > 7) {
         throw DbException.get(7001, (String[])(this.getName(), "1..7"));
      }
   }

   public ResultInterface getValueTemplate(SessionLocal var1) {
      var1.getUser().checkAdmin();
      String var2 = getValue(var1, this.args, 0);
      if (var2 == null) {
         throw DbException.get(90012, "fileName");
      } else {
         String var3 = getValue(var1, this.args, 1);
         Csv var4 = new Csv();
         String var5 = getValue(var1, this.args, 2);
         String var6 = null;
         Object var9;
         if (var5 != null && var5.indexOf(61) >= 0) {
            var6 = var4.setOptions(var5);
         } else {
            var6 = var5;
            String var7 = getValue(var1, this.args, 3);
            String var8 = getValue(var1, this.args, 4);
            var9 = getValue(var1, this.args, 5);
            CSVWriteFunction.setCsvDelimiterEscape(var4, var7, var8, (String)var9);
         }

         char var33 = var4.getFieldSeparatorRead();
         String[] var34 = StringUtils.arraySplit(var3, var33, true);

         try {
            ResultSet var10 = var4.read(var2, var34, var6);
            Throwable var11 = null;

            try {
               var9 = FunctionAlias.JavaMethod.resultSetToResult(var1, var10, 0);
            } catch (Throwable var29) {
               var11 = var29;
               throw var29;
            } finally {
               if (var10 != null) {
                  if (var11 != null) {
                     try {
                        var10.close();
                     } catch (Throwable var28) {
                        var11.addSuppressed(var28);
                     }
                  } else {
                     var10.close();
                  }
               }

            }
         } catch (SQLException var31) {
            throw DbException.convert(var31);
         } finally {
            var4.close();
         }

         return (ResultInterface)var9;
      }
   }

   private static String getValue(SessionLocal var0, Expression[] var1, int var2) {
      return var2 < var1.length ? var1[var2].getValue(var0).getString() : null;
   }

   public String getName() {
      return "CSVREAD";
   }

   public boolean isDeterministic() {
      return false;
   }
}
