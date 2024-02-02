package freemarker.core;

import java.util.Locale;
import java.util.TimeZone;

class ISOTemplateDateFormatFactory extends ISOLikeTemplateDateFormatFactory {
   static final ISOTemplateDateFormatFactory INSTANCE = new ISOTemplateDateFormatFactory();

   private ISOTemplateDateFormatFactory() {
   }

   public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
      return new ISOTemplateDateFormat(params, 3, dateType, zonelessInput, timeZone, this, env);
   }
}
