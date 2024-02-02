package freemarker.core;

import freemarker.template.utility.DateUtil;

abstract class ISOLikeTemplateDateFormatFactory extends TemplateDateFormatFactory {
   private static final Object DATE_TO_CAL_CONVERTER_KEY = new Object();
   private static final Object CAL_TO_DATE_CONVERTER_KEY = new Object();

   protected ISOLikeTemplateDateFormatFactory() {
   }

   public DateUtil.DateToISO8601CalendarFactory getISOBuiltInCalendar(Environment env) {
      DateUtil.DateToISO8601CalendarFactory r = (DateUtil.DateToISO8601CalendarFactory)env.getCustomState(DATE_TO_CAL_CONVERTER_KEY);
      if (r == null) {
         r = new DateUtil.TrivialDateToISO8601CalendarFactory();
         env.setCustomState(DATE_TO_CAL_CONVERTER_KEY, r);
      }

      return (DateUtil.DateToISO8601CalendarFactory)r;
   }

   public DateUtil.CalendarFieldsToDateConverter getCalendarFieldsToDateCalculator(Environment env) {
      DateUtil.CalendarFieldsToDateConverter r = (DateUtil.CalendarFieldsToDateConverter)env.getCustomState(CAL_TO_DATE_CONVERTER_KEY);
      if (r == null) {
         r = new DateUtil.TrivialCalendarFieldsToDateConverter();
         env.setCustomState(CAL_TO_DATE_CONVERTER_KEY, r);
      }

      return (DateUtil.CalendarFieldsToDateConverter)r;
   }
}
