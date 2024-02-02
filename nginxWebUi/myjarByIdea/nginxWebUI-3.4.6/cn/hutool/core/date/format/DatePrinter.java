package cn.hutool.core.date.format;

import java.util.Calendar;
import java.util.Date;

public interface DatePrinter extends DateBasic {
   String format(long var1);

   String format(Date var1);

   String format(Calendar var1);

   <B extends Appendable> B format(long var1, B var3);

   <B extends Appendable> B format(Date var1, B var2);

   <B extends Appendable> B format(Calendar var1, B var2);
}
