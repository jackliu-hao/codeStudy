package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class AliasTemplateDateFormatFactory extends TemplateDateFormatFactory {
   private final String defaultTargetFormatString;
   private final Map<Locale, String> localizedTargetFormatStrings;

   public AliasTemplateDateFormatFactory(String targetFormatString) {
      this.defaultTargetFormatString = targetFormatString;
      this.localizedTargetFormatStrings = null;
   }

   public AliasTemplateDateFormatFactory(String defaultTargetFormatString, Map<Locale, String> localizedTargetFormatStrings) {
      this.defaultTargetFormatString = defaultTargetFormatString;
      this.localizedTargetFormatStrings = localizedTargetFormatStrings;
   }

   public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws TemplateValueFormatException {
      TemplateFormatUtil.checkHasNoParameters(params);

      try {
         String targetFormatString;
         if (this.localizedTargetFormatStrings != null) {
            Locale lookupLocale = locale;

            for(targetFormatString = (String)this.localizedTargetFormatStrings.get(locale); targetFormatString == null && (lookupLocale = _CoreLocaleUtils.getLessSpecificLocale(lookupLocale)) != null; targetFormatString = (String)this.localizedTargetFormatStrings.get(lookupLocale)) {
            }
         } else {
            targetFormatString = null;
         }

         if (targetFormatString == null) {
            targetFormatString = this.defaultTargetFormatString;
         }

         return env.getTemplateDateFormat(targetFormatString, dateType, locale, timeZone, zonelessInput);
      } catch (TemplateValueFormatException var9) {
         throw new AliasTargetTemplateValueFormatException("Failed to create format based on target format string,  " + StringUtil.jQuote(params) + ". Reason given: " + var9.getMessage(), var9);
      }
   }
}
