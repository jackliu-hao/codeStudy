package freemarker.core;

import freemarker.template.utility.DateUtil;
import java.util.Date;
import java.util.TimeZone;

final class XSTemplateDateFormat extends ISOLikeTemplateDateFormat {
   XSTemplateDateFormat(String settingValue, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
      super(settingValue, parsingStart, dateType, zonelessInput, timeZone, factory, env);
   }

   protected String format(Date date, boolean datePart, boolean timePart, boolean offsetPart, int accuracy, TimeZone timeZone, DateUtil.DateToISO8601CalendarFactory calendarFactory) {
      return DateUtil.dateToXSString(date, datePart, timePart, offsetPart, accuracy, timeZone, calendarFactory);
   }

   protected Date parseDate(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
      return DateUtil.parseXSDate(s, tz, calToDateConverter);
   }

   protected Date parseTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
      return DateUtil.parseXSTime(s, tz, calToDateConverter);
   }

   protected Date parseDateTime(String s, TimeZone tz, DateUtil.CalendarFieldsToDateConverter calToDateConverter) throws DateUtil.DateParseException {
      return DateUtil.parseXSDateTime(s, tz, calToDateConverter);
   }

   protected String getDateDescription() {
      return "W3C XML Schema date";
   }

   protected String getTimeDescription() {
      return "W3C XML Schema time";
   }

   protected String getDateTimeDescription() {
      return "W3C XML Schema dateTime";
   }

   protected boolean isXSMode() {
      return true;
   }
}
