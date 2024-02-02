package freemarker.core;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;
import java.util.Arrays;

final class PropertySetting extends TemplateElement {
   private final String key;
   private final Expression value;
   static final String[] SETTING_NAMES = new String[]{"booleanFormat", "boolean_format", "classicCompatible", "classic_compatible", "dateFormat", "date_format", "datetimeFormat", "datetime_format", "locale", "numberFormat", "number_format", "outputEncoding", "output_encoding", "sqlDateAndTimeTimeZone", "sql_date_and_time_time_zone", "timeFormat", "timeZone", "time_format", "time_zone", "urlEscapingCharset", "url_escaping_charset"};

   PropertySetting(Token keyTk, FMParserTokenManager tokenManager, Expression value, Configuration cfg) throws ParseException {
      String key = keyTk.image;
      if (Arrays.binarySearch(SETTING_NAMES, key) >= 0) {
         this.key = key;
         this.value = value;
      } else {
         StringBuilder sb = new StringBuilder();
         if (!_TemplateAPI.getConfigurationSettingNames(cfg, true).contains(key) && !_TemplateAPI.getConfigurationSettingNames(cfg, false).contains(key)) {
            sb.append("Unknown setting name: ");
            sb.append(StringUtil.jQuote(key)).append(".");
            sb.append(" The allowed setting names are: ");
            int namingConvention = tokenManager.namingConvention;
            int shownNamingConvention = namingConvention != 10 ? namingConvention : 11;
            boolean first = true;

            for(int i = 0; i < SETTING_NAMES.length; ++i) {
               String correctName = SETTING_NAMES[i];
               int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(correctName);
               if (shownNamingConvention == 12) {
                  if (correctNameNamingConvetion == 11) {
                     continue;
                  }
               } else if (correctNameNamingConvetion == 12) {
                  continue;
               }

               if (first) {
                  first = false;
               } else {
                  sb.append(", ");
               }

               sb.append(SETTING_NAMES[i]);
            }
         } else {
            sb.append("The setting name is recognized, but changing this setting from inside a template isn't supported.");
         }

         throw new ParseException(sb.toString(), (Template)null, keyTk);
      }
   }

   TemplateElement[] accept(Environment env) throws TemplateException {
      TemplateModel mval = this.value.eval(env);
      String strval;
      if (mval instanceof TemplateScalarModel) {
         strval = ((TemplateScalarModel)mval).getAsString();
      } else if (mval instanceof TemplateBooleanModel) {
         strval = ((TemplateBooleanModel)mval).getAsBoolean() ? "true" : "false";
      } else if (mval instanceof TemplateNumberModel) {
         strval = ((TemplateNumberModel)mval).getAsNumber().toString();
      } else {
         strval = this.value.evalAndCoerceToStringOrUnsupportedMarkup(env);
      }

      env.setSetting(this.key, strval);
      return null;
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      sb.append(' ');
      sb.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.key));
      sb.append('=');
      sb.append(this.value.getCanonicalForm());
      if (canonical) {
         sb.append("/>");
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "#setting";
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.key;
         case 1:
            return this.value;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.ITEM_KEY;
         case 1:
            return ParameterRole.ITEM_VALUE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
