package org.h2.expression.function;

import java.text.DateFormatSymbols;
import java.util.Locale;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class DayMonthNameFunction extends Function1 {
   public static final int DAYNAME = 0;
   public static final int MONTHNAME = 1;
   private static final String[] NAMES = new String[]{"DAYNAME", "MONTHNAME"};
   private static volatile String[][] MONTHS_AND_WEEKS;
   private final int function;

   public DayMonthNameFunction(Expression var1, int var2) {
      super(var1);
      this.function = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         long var3 = DateTimeUtils.dateAndTimeFromValue(var2, var1)[0];
         String var5;
         switch (this.function) {
            case 0:
               var5 = getMonthsAndWeeks(1)[DateTimeUtils.getDayOfWeek(var3, 0)];
               break;
            case 1:
               var5 = getMonthsAndWeeks(0)[DateTimeUtils.monthFromDateValue(var3) - 1];
               break;
            default:
               throw DbException.getInternalError("function=" + this.function);
         }

         return ValueVarchar.get(var5, var1);
      }
   }

   private static String[] getMonthsAndWeeks(int var0) {
      String[][] var1 = MONTHS_AND_WEEKS;
      if (var1 == null) {
         var1 = new String[2][];
         DateFormatSymbols var2 = DateFormatSymbols.getInstance(Locale.ENGLISH);
         var1[0] = var2.getMonths();
         var1[1] = var2.getWeekdays();
         MONTHS_AND_WEEKS = var1;
      }

      return var1[var0];
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      this.type = TypeInfo.getTypeInfo(2, 20L, 0, (ExtTypeInfo)null);
      return (Expression)(this.arg.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return NAMES[this.function];
   }
}
