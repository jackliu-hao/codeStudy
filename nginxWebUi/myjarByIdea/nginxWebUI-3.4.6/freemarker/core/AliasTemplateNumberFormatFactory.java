package freemarker.core;

import freemarker.template.utility.StringUtil;
import java.util.Locale;
import java.util.Map;

public final class AliasTemplateNumberFormatFactory extends TemplateNumberFormatFactory {
   private final String defaultTargetFormatString;
   private final Map<Locale, String> localizedTargetFormatStrings;

   public AliasTemplateNumberFormatFactory(String targetFormatString) {
      this.defaultTargetFormatString = targetFormatString;
      this.localizedTargetFormatStrings = null;
   }

   public AliasTemplateNumberFormatFactory(String defaultTargetFormatString, Map<Locale, String> localizedTargetFormatStrings) {
      this.defaultTargetFormatString = defaultTargetFormatString;
      this.localizedTargetFormatStrings = localizedTargetFormatStrings;
   }

   public TemplateNumberFormat get(String params, Locale locale, Environment env) throws TemplateValueFormatException {
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

         return env.getTemplateNumberFormat(targetFormatString, locale);
      } catch (TemplateValueFormatException var6) {
         throw new AliasTargetTemplateValueFormatException("Failed to create format based on target format string,  " + StringUtil.jQuote(params) + ". Reason given: " + var6.getMessage(), var6);
      }
   }
}
