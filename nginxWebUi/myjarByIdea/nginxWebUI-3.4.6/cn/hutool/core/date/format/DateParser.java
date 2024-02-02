package cn.hutool.core.date.format;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

public interface DateParser extends DateBasic {
   Date parse(String var1) throws ParseException;

   Date parse(String var1, ParsePosition var2);

   boolean parse(String var1, ParsePosition var2, Calendar var3);

   default Object parseObject(String source) throws ParseException {
      return this.parse(source);
   }

   default Object parseObject(String source, ParsePosition pos) {
      return this.parse(source, pos);
   }
}
