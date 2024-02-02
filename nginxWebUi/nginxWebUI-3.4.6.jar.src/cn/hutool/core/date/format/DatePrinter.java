package cn.hutool.core.date.format;

import java.util.Calendar;
import java.util.Date;

public interface DatePrinter extends DateBasic {
  String format(long paramLong);
  
  String format(Date paramDate);
  
  String format(Calendar paramCalendar);
  
  <B extends Appendable> B format(long paramLong, B paramB);
  
  <B extends Appendable> B format(Date paramDate, B paramB);
  
  <B extends Appendable> B format(Calendar paramCalendar, B paramB);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\DatePrinter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */