package cn.hutool.core.date.format;

import java.util.Locale;
import java.util.TimeZone;

public interface DateBasic {
   String getPattern();

   TimeZone getTimeZone();

   Locale getLocale();
}
