package org.h2.expression.function;

import java.sql.SQLException;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.tools.Csv;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;

public final class CSVWriteFunction extends FunctionN {
   public CSVWriteFunction() {
      super(new Expression[4]);
   }

   public Value getValue(SessionLocal var1) {
      var1.getUser().checkAdmin();
      JdbcConnection var2 = var1.createConnection(false);
      Csv var3 = new Csv();
      String var4 = this.getValue(var1, 2);
      String var5 = null;
      if (var4 != null && var4.indexOf(61) >= 0) {
         var5 = var3.setOptions(var4);
      } else {
         var5 = var4;
         String var6 = this.getValue(var1, 3);
         String var7 = this.getValue(var1, 4);
         String var8 = this.getValue(var1, 5);
         String var9 = this.getValue(var1, 6);
         String var10 = this.getValue(var1, 7);
         setCsvDelimiterEscape(var3, var6, var7, var8);
         var3.setNullString(var9);
         if (var10 != null) {
            var3.setLineSeparator(var10);
         }
      }

      try {
         return ValueInteger.get(var3.write(var2, this.args[0].getValue(var1).getString(), this.args[1].getValue(var1).getString(), var5));
      } catch (SQLException var11) {
         throw DbException.convert(var11);
      }
   }

   private String getValue(SessionLocal var1, int var2) {
      return var2 < this.args.length ? this.args[var2].getValue(var1).getString() : null;
   }

   public static void setCsvDelimiterEscape(Csv var0, String var1, String var2, String var3) {
      char var4;
      if (var1 != null) {
         var0.setFieldSeparatorWrite(var1);
         if (!var1.isEmpty()) {
            var4 = var1.charAt(0);
            var0.setFieldSeparatorRead(var4);
         }
      }

      if (var2 != null) {
         var4 = var2.isEmpty() ? 0 : var2.charAt(0);
         var0.setFieldDelimiter(var4);
      }

      if (var3 != null) {
         var4 = var3.isEmpty() ? 0 : var3.charAt(0);
         var0.setEscapeCharacter(var4);
      }

   }

   public Expression optimize(SessionLocal var1) {
      this.optimizeArguments(var1, false);
      int var2 = this.args.length;
      if (var2 >= 2 && var2 <= 8) {
         this.type = TypeInfo.TYPE_INTEGER;
         return this;
      } else {
         throw DbException.get(7001, (String[])(this.getName(), "2..8"));
      }
   }

   public String getName() {
      return "CSVWRITE";
   }

   public boolean isEverything(ExpressionVisitor var1) {
      if (!super.isEverything(var1)) {
         return false;
      } else {
         switch (var1.getType()) {
            case 2:
            case 5:
            case 8:
               return false;
            default:
               return true;
         }
      }
   }
}
