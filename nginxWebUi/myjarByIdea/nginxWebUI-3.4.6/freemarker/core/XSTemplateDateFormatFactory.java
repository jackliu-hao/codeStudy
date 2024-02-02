package freemarker.core;

import java.util.Locale;
import java.util.TimeZone;

class XSTemplateDateFormatFactory extends ISOLikeTemplateDateFormatFactory {
   static final XSTemplateDateFormatFactory INSTANCE = new XSTemplateDateFormatFactory();

   private XSTemplateDateFormatFactory() {
   }

   public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
      return new XSTemplateDateFormat(params, 2, dateType, zonelessInput, timeZone, this, env);
   }
}
