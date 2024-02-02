package freemarker.core;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.AttemptExceptionReporter;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

public class Configurable {
   static final String C_TRUE_FALSE = "true,false";
   static final String C_FORMAT_STRING = "c";
   private static final String NULL = "null";
   private static final String DEFAULT = "default";
   private static final String DEFAULT_2_3_0 = "default_2_3_0";
   private static final String JVM_DEFAULT = "JVM default";
   public static final String LOCALE_KEY_SNAKE_CASE = "locale";
   public static final String LOCALE_KEY_CAMEL_CASE = "locale";
   public static final String LOCALE_KEY = "locale";
   public static final String NUMBER_FORMAT_KEY_SNAKE_CASE = "number_format";
   public static final String NUMBER_FORMAT_KEY_CAMEL_CASE = "numberFormat";
   public static final String NUMBER_FORMAT_KEY = "number_format";
   public static final String CUSTOM_NUMBER_FORMATS_KEY_SNAKE_CASE = "custom_number_formats";
   public static final String CUSTOM_NUMBER_FORMATS_KEY_CAMEL_CASE = "customNumberFormats";
   public static final String CUSTOM_NUMBER_FORMATS_KEY = "custom_number_formats";
   public static final String TIME_FORMAT_KEY_SNAKE_CASE = "time_format";
   public static final String TIME_FORMAT_KEY_CAMEL_CASE = "timeFormat";
   public static final String TIME_FORMAT_KEY = "time_format";
   public static final String DATE_FORMAT_KEY_SNAKE_CASE = "date_format";
   public static final String DATE_FORMAT_KEY_CAMEL_CASE = "dateFormat";
   public static final String DATE_FORMAT_KEY = "date_format";
   public static final String CUSTOM_DATE_FORMATS_KEY_SNAKE_CASE = "custom_date_formats";
   public static final String CUSTOM_DATE_FORMATS_KEY_CAMEL_CASE = "customDateFormats";
   public static final String CUSTOM_DATE_FORMATS_KEY = "custom_date_formats";
   public static final String DATETIME_FORMAT_KEY_SNAKE_CASE = "datetime_format";
   public static final String DATETIME_FORMAT_KEY_CAMEL_CASE = "datetimeFormat";
   public static final String DATETIME_FORMAT_KEY = "datetime_format";
   public static final String TIME_ZONE_KEY_SNAKE_CASE = "time_zone";
   public static final String TIME_ZONE_KEY_CAMEL_CASE = "timeZone";
   public static final String TIME_ZONE_KEY = "time_zone";
   public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY_SNAKE_CASE = "sql_date_and_time_time_zone";
   public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY_CAMEL_CASE = "sqlDateAndTimeTimeZone";
   public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY = "sql_date_and_time_time_zone";
   public static final String CLASSIC_COMPATIBLE_KEY_SNAKE_CASE = "classic_compatible";
   public static final String CLASSIC_COMPATIBLE_KEY_CAMEL_CASE = "classicCompatible";
   public static final String CLASSIC_COMPATIBLE_KEY = "classic_compatible";
   public static final String TEMPLATE_EXCEPTION_HANDLER_KEY_SNAKE_CASE = "template_exception_handler";
   public static final String TEMPLATE_EXCEPTION_HANDLER_KEY_CAMEL_CASE = "templateExceptionHandler";
   public static final String TEMPLATE_EXCEPTION_HANDLER_KEY = "template_exception_handler";
   public static final String ATTEMPT_EXCEPTION_REPORTER_KEY_SNAKE_CASE = "attempt_exception_reporter";
   public static final String ATTEMPT_EXCEPTION_REPORTER_KEY_CAMEL_CASE = "attemptExceptionReporter";
   public static final String ATTEMPT_EXCEPTION_REPORTER_KEY = "attempt_exception_reporter";
   public static final String ARITHMETIC_ENGINE_KEY_SNAKE_CASE = "arithmetic_engine";
   public static final String ARITHMETIC_ENGINE_KEY_CAMEL_CASE = "arithmeticEngine";
   public static final String ARITHMETIC_ENGINE_KEY = "arithmetic_engine";
   public static final String OBJECT_WRAPPER_KEY_SNAKE_CASE = "object_wrapper";
   public static final String OBJECT_WRAPPER_KEY_CAMEL_CASE = "objectWrapper";
   public static final String OBJECT_WRAPPER_KEY = "object_wrapper";
   public static final String BOOLEAN_FORMAT_KEY_SNAKE_CASE = "boolean_format";
   public static final String BOOLEAN_FORMAT_KEY_CAMEL_CASE = "booleanFormat";
   public static final String BOOLEAN_FORMAT_KEY = "boolean_format";
   public static final String OUTPUT_ENCODING_KEY_SNAKE_CASE = "output_encoding";
   public static final String OUTPUT_ENCODING_KEY_CAMEL_CASE = "outputEncoding";
   public static final String OUTPUT_ENCODING_KEY = "output_encoding";
   public static final String URL_ESCAPING_CHARSET_KEY_SNAKE_CASE = "url_escaping_charset";
   public static final String URL_ESCAPING_CHARSET_KEY_CAMEL_CASE = "urlEscapingCharset";
   public static final String URL_ESCAPING_CHARSET_KEY = "url_escaping_charset";
   public static final String STRICT_BEAN_MODELS_KEY_SNAKE_CASE = "strict_bean_models";
   public static final String STRICT_BEAN_MODELS_KEY_CAMEL_CASE = "strictBeanModels";
   public static final String STRICT_BEAN_MODELS_KEY = "strict_bean_models";
   public static final String AUTO_FLUSH_KEY_SNAKE_CASE = "auto_flush";
   public static final String AUTO_FLUSH_KEY_CAMEL_CASE = "autoFlush";
   public static final String AUTO_FLUSH_KEY = "auto_flush";
   public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY_SNAKE_CASE = "new_builtin_class_resolver";
   public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY_CAMEL_CASE = "newBuiltinClassResolver";
   public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY = "new_builtin_class_resolver";
   public static final String SHOW_ERROR_TIPS_KEY_SNAKE_CASE = "show_error_tips";
   public static final String SHOW_ERROR_TIPS_KEY_CAMEL_CASE = "showErrorTips";
   public static final String SHOW_ERROR_TIPS_KEY = "show_error_tips";
   public static final String API_BUILTIN_ENABLED_KEY_SNAKE_CASE = "api_builtin_enabled";
   public static final String API_BUILTIN_ENABLED_KEY_CAMEL_CASE = "apiBuiltinEnabled";
   public static final String API_BUILTIN_ENABLED_KEY = "api_builtin_enabled";
   public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY_SNAKE_CASE = "truncate_builtin_algorithm";
   public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY_CAMEL_CASE = "truncateBuiltinAlgorithm";
   public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY = "truncate_builtin_algorithm";
   public static final String LOG_TEMPLATE_EXCEPTIONS_KEY_SNAKE_CASE = "log_template_exceptions";
   public static final String LOG_TEMPLATE_EXCEPTIONS_KEY_CAMEL_CASE = "logTemplateExceptions";
   public static final String LOG_TEMPLATE_EXCEPTIONS_KEY = "log_template_exceptions";
   public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY_SNAKE_CASE = "wrap_unchecked_exceptions";
   public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY_CAMEL_CASE = "wrapUncheckedExceptions";
   public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY = "wrap_unchecked_exceptions";
   public static final String LAZY_IMPORTS_KEY_SNAKE_CASE = "lazy_imports";
   public static final String LAZY_IMPORTS_KEY_CAMEL_CASE = "lazyImports";
   public static final String LAZY_IMPORTS_KEY = "lazy_imports";
   public static final String LAZY_AUTO_IMPORTS_KEY_SNAKE_CASE = "lazy_auto_imports";
   public static final String LAZY_AUTO_IMPORTS_KEY_CAMEL_CASE = "lazyAutoImports";
   public static final String LAZY_AUTO_IMPORTS_KEY = "lazy_auto_imports";
   public static final String AUTO_IMPORT_KEY_SNAKE_CASE = "auto_import";
   public static final String AUTO_IMPORT_KEY_CAMEL_CASE = "autoImport";
   public static final String AUTO_IMPORT_KEY = "auto_import";
   public static final String AUTO_INCLUDE_KEY_SNAKE_CASE = "auto_include";
   public static final String AUTO_INCLUDE_KEY_CAMEL_CASE = "autoInclude";
   public static final String AUTO_INCLUDE_KEY = "auto_include";
   /** @deprecated */
   @Deprecated
   public static final String STRICT_BEAN_MODELS = "strict_bean_models";
   private static final String[] SETTING_NAMES_SNAKE_CASE = new String[]{"api_builtin_enabled", "arithmetic_engine", "attempt_exception_reporter", "auto_flush", "auto_import", "auto_include", "boolean_format", "classic_compatible", "custom_date_formats", "custom_number_formats", "date_format", "datetime_format", "lazy_auto_imports", "lazy_imports", "locale", "log_template_exceptions", "new_builtin_class_resolver", "number_format", "object_wrapper", "output_encoding", "show_error_tips", "sql_date_and_time_time_zone", "strict_bean_models", "template_exception_handler", "time_format", "time_zone", "truncate_builtin_algorithm", "url_escaping_charset", "wrap_unchecked_exceptions"};
   private static final String[] SETTING_NAMES_CAMEL_CASE = new String[]{"apiBuiltinEnabled", "arithmeticEngine", "attemptExceptionReporter", "autoFlush", "autoImport", "autoInclude", "booleanFormat", "classicCompatible", "customDateFormats", "customNumberFormats", "dateFormat", "datetimeFormat", "lazyAutoImports", "lazyImports", "locale", "logTemplateExceptions", "newBuiltinClassResolver", "numberFormat", "objectWrapper", "outputEncoding", "showErrorTips", "sqlDateAndTimeTimeZone", "strictBeanModels", "templateExceptionHandler", "timeFormat", "timeZone", "truncateBuiltinAlgorithm", "urlEscapingCharset", "wrapUncheckedExceptions"};
   private Configurable parent;
   private Properties properties;
   private HashMap<Object, Object> customAttributes;
   private Locale locale;
   private String numberFormat;
   private String timeFormat;
   private String dateFormat;
   private String dateTimeFormat;
   private TimeZone timeZone;
   private TimeZone sqlDataAndTimeTimeZone;
   private boolean sqlDataAndTimeTimeZoneSet;
   private String booleanFormat;
   private String trueStringValue;
   private String falseStringValue;
   private Integer classicCompatible;
   private TemplateExceptionHandler templateExceptionHandler;
   private AttemptExceptionReporter attemptExceptionReporter;
   private ArithmeticEngine arithmeticEngine;
   private ObjectWrapper objectWrapper;
   private String outputEncoding;
   private boolean outputEncodingSet;
   private String urlEscapingCharset;
   private boolean urlEscapingCharsetSet;
   private Boolean autoFlush;
   private Boolean showErrorTips;
   private TemplateClassResolver newBuiltinClassResolver;
   private Boolean apiBuiltinEnabled;
   private TruncateBuiltinAlgorithm truncateBuiltinAlgorithm;
   private Boolean logTemplateExceptions;
   private Boolean wrapUncheckedExceptions;
   private Map<String, ? extends TemplateDateFormatFactory> customDateFormats;
   private Map<String, ? extends TemplateNumberFormatFactory> customNumberFormats;
   private LinkedHashMap<String, String> autoImports;
   private ArrayList<String> autoIncludes;
   private Boolean lazyImports;
   private Boolean lazyAutoImports;
   private boolean lazyAutoImportsSet;
   private static final String ALLOWED_CLASSES_SNAKE_CASE = "allowed_classes";
   private static final String TRUSTED_TEMPLATES_SNAKE_CASE = "trusted_templates";
   private static final String ALLOWED_CLASSES_CAMEL_CASE = "allowedClasses";
   private static final String TRUSTED_TEMPLATES_CAMEL_CASE = "trustedTemplates";

   /** @deprecated */
   @Deprecated
   public Configurable() {
      this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
   }

   protected Configurable(Version incompatibleImprovements) {
      _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
      this.parent = null;
      this.properties = new Properties();
      this.locale = _TemplateAPI.getDefaultLocale();
      this.properties.setProperty("locale", this.locale.toString());
      this.timeZone = _TemplateAPI.getDefaultTimeZone();
      this.properties.setProperty("time_zone", this.timeZone.getID());
      this.sqlDataAndTimeTimeZone = null;
      this.properties.setProperty("sql_date_and_time_time_zone", String.valueOf(this.sqlDataAndTimeTimeZone));
      this.numberFormat = "number";
      this.properties.setProperty("number_format", this.numberFormat);
      this.timeFormat = "";
      this.properties.setProperty("time_format", this.timeFormat);
      this.dateFormat = "";
      this.properties.setProperty("date_format", this.dateFormat);
      this.dateTimeFormat = "";
      this.properties.setProperty("datetime_format", this.dateTimeFormat);
      this.classicCompatible = 0;
      this.properties.setProperty("classic_compatible", this.classicCompatible.toString());
      this.templateExceptionHandler = _TemplateAPI.getDefaultTemplateExceptionHandler(incompatibleImprovements);
      this.properties.setProperty("template_exception_handler", this.templateExceptionHandler.getClass().getName());
      this.wrapUncheckedExceptions = _TemplateAPI.getDefaultWrapUncheckedExceptions(incompatibleImprovements);
      this.attemptExceptionReporter = _TemplateAPI.getDefaultAttemptExceptionReporter(incompatibleImprovements);
      this.arithmeticEngine = ArithmeticEngine.BIGDECIMAL_ENGINE;
      this.properties.setProperty("arithmetic_engine", this.arithmeticEngine.getClass().getName());
      this.objectWrapper = Configuration.getDefaultObjectWrapper(incompatibleImprovements);
      this.autoFlush = Boolean.TRUE;
      this.properties.setProperty("auto_flush", this.autoFlush.toString());
      this.newBuiltinClassResolver = TemplateClassResolver.UNRESTRICTED_RESOLVER;
      this.properties.setProperty("new_builtin_class_resolver", this.newBuiltinClassResolver.getClass().getName());
      this.truncateBuiltinAlgorithm = DefaultTruncateBuiltinAlgorithm.ASCII_INSTANCE;
      this.showErrorTips = Boolean.TRUE;
      this.properties.setProperty("show_error_tips", this.showErrorTips.toString());
      this.apiBuiltinEnabled = Boolean.FALSE;
      this.properties.setProperty("api_builtin_enabled", this.apiBuiltinEnabled.toString());
      this.logTemplateExceptions = _TemplateAPI.getDefaultLogTemplateExceptions(incompatibleImprovements);
      this.properties.setProperty("log_template_exceptions", this.logTemplateExceptions.toString());
      this.setBooleanFormat("true,false");
      this.customAttributes = new HashMap();
      this.customDateFormats = Collections.emptyMap();
      this.customNumberFormats = Collections.emptyMap();
      this.lazyImports = false;
      this.lazyAutoImportsSet = true;
      this.initAutoImportsMap();
      this.initAutoIncludesList();
   }

   public Configurable(Configurable parent) {
      this.parent = parent;
      this.properties = new Properties(parent.properties);
      this.customAttributes = new HashMap(0);
   }

   protected Object clone() throws CloneNotSupportedException {
      Configurable copy = (Configurable)super.clone();
      if (this.properties != null) {
         copy.properties = new Properties(this.properties);
      }

      if (this.customAttributes != null) {
         copy.customAttributes = (HashMap)this.customAttributes.clone();
      }

      if (this.autoImports != null) {
         copy.autoImports = (LinkedHashMap)this.autoImports.clone();
      }

      if (this.autoIncludes != null) {
         copy.autoIncludes = (ArrayList)this.autoIncludes.clone();
      }

      return copy;
   }

   public final Configurable getParent() {
      return this.parent;
   }

   void setParent(Configurable parent) {
      this.parent = parent;
   }

   public void setClassicCompatible(boolean classicCompatibility) {
      this.classicCompatible = classicCompatibility ? 1 : 0;
      this.properties.setProperty("classic_compatible", this.classicCompatibilityIntToString(this.classicCompatible));
   }

   public void setClassicCompatibleAsInt(int classicCompatibility) {
      if (classicCompatibility >= 0 && classicCompatibility <= 2) {
         this.classicCompatible = classicCompatibility;
      } else {
         throw new IllegalArgumentException("Unsupported \"classicCompatibility\": " + classicCompatibility);
      }
   }

   private String classicCompatibilityIntToString(Integer i) {
      if (i == null) {
         return null;
      } else if (i == 0) {
         return "false";
      } else {
         return i == 1 ? "true" : i.toString();
      }
   }

   public boolean isClassicCompatible() {
      return this.classicCompatible != null ? this.classicCompatible != 0 : this.parent.isClassicCompatible();
   }

   public int getClassicCompatibleAsInt() {
      return this.classicCompatible != null ? this.classicCompatible : this.parent.getClassicCompatibleAsInt();
   }

   public boolean isClassicCompatibleSet() {
      return this.classicCompatible != null;
   }

   public void setLocale(Locale locale) {
      NullArgumentException.check("locale", locale);
      this.locale = locale;
      this.properties.setProperty("locale", locale.toString());
   }

   public Locale getLocale() {
      return this.locale != null ? this.locale : this.parent.getLocale();
   }

   public boolean isLocaleSet() {
      return this.locale != null;
   }

   public void setTimeZone(TimeZone timeZone) {
      NullArgumentException.check("timeZone", timeZone);
      this.timeZone = timeZone;
      this.properties.setProperty("time_zone", timeZone.getID());
   }

   public TimeZone getTimeZone() {
      return this.timeZone != null ? this.timeZone : this.parent.getTimeZone();
   }

   public boolean isTimeZoneSet() {
      return this.timeZone != null;
   }

   public void setSQLDateAndTimeTimeZone(TimeZone tz) {
      this.sqlDataAndTimeTimeZone = tz;
      this.sqlDataAndTimeTimeZoneSet = true;
      this.properties.setProperty("sql_date_and_time_time_zone", tz != null ? tz.getID() : "null");
   }

   public TimeZone getSQLDateAndTimeTimeZone() {
      return this.sqlDataAndTimeTimeZoneSet ? this.sqlDataAndTimeTimeZone : (this.parent != null ? this.parent.getSQLDateAndTimeTimeZone() : null);
   }

   public boolean isSQLDateAndTimeTimeZoneSet() {
      return this.sqlDataAndTimeTimeZoneSet;
   }

   public void setNumberFormat(String numberFormat) {
      NullArgumentException.check("numberFormat", numberFormat);
      this.numberFormat = numberFormat;
      this.properties.setProperty("number_format", numberFormat);
   }

   public String getNumberFormat() {
      return this.numberFormat != null ? this.numberFormat : this.parent.getNumberFormat();
   }

   public boolean isNumberFormatSet() {
      return this.numberFormat != null;
   }

   public Map<String, ? extends TemplateNumberFormatFactory> getCustomNumberFormats() {
      return this.customNumberFormats == null ? this.parent.getCustomNumberFormats() : this.customNumberFormats;
   }

   public Map<String, ? extends TemplateNumberFormatFactory> getCustomNumberFormatsWithoutFallback() {
      return this.customNumberFormats;
   }

   public void setCustomNumberFormats(Map<String, ? extends TemplateNumberFormatFactory> customNumberFormats) {
      NullArgumentException.check("customNumberFormats", customNumberFormats);
      this.validateFormatNames(customNumberFormats.keySet());
      this.customNumberFormats = customNumberFormats;
   }

   private void validateFormatNames(Set<String> keySet) {
      Iterator var2 = keySet.iterator();

      while(var2.hasNext()) {
         String name = (String)var2.next();
         if (name.length() == 0) {
            throw new IllegalArgumentException("Format names can't be 0 length");
         }

         char firstChar = name.charAt(0);
         if (firstChar == '@') {
            throw new IllegalArgumentException("Format names can't start with '@'. '@' is only used when referring to them from format strings. In: " + name);
         }

         if (!Character.isLetter(firstChar)) {
            throw new IllegalArgumentException("Format name must start with letter: " + name);
         }

         for(int i = 1; i < name.length(); ++i) {
            if (!Character.isLetterOrDigit(name.charAt(i))) {
               throw new IllegalArgumentException("Format name can only contain letters and digits: " + name);
            }
         }
      }

   }

   public boolean isCustomNumberFormatsSet() {
      return this.customNumberFormats != null;
   }

   public TemplateNumberFormatFactory getCustomNumberFormat(String name) {
      if (this.customNumberFormats != null) {
         TemplateNumberFormatFactory r = (TemplateNumberFormatFactory)this.customNumberFormats.get(name);
         if (r != null) {
            return r;
         }
      }

      return this.parent != null ? this.parent.getCustomNumberFormat(name) : null;
   }

   public boolean hasCustomFormats() {
      return this.customNumberFormats != null && !this.customNumberFormats.isEmpty() || this.customDateFormats != null && !this.customDateFormats.isEmpty() || this.getParent() != null && this.getParent().hasCustomFormats();
   }

   public void setBooleanFormat(String booleanFormat) {
      NullArgumentException.check("booleanFormat", booleanFormat);
      if (booleanFormat.equals("true,false")) {
         this.trueStringValue = null;
         this.falseStringValue = null;
      } else if (booleanFormat.equals("c")) {
         this.trueStringValue = "true";
         this.falseStringValue = "false";
      } else {
         int commaIdx = booleanFormat.indexOf(44);
         if (commaIdx == -1) {
            throw new IllegalArgumentException("Setting value must be a string that contains two comma-separated values for true and false, or it must be \"c\", but it was " + StringUtil.jQuote(booleanFormat) + ".");
         }

         this.trueStringValue = booleanFormat.substring(0, commaIdx);
         this.falseStringValue = booleanFormat.substring(commaIdx + 1);
      }

      this.booleanFormat = booleanFormat;
      this.properties.setProperty("boolean_format", booleanFormat);
   }

   public String getBooleanFormat() {
      return this.booleanFormat != null ? this.booleanFormat : this.parent.getBooleanFormat();
   }

   public boolean isBooleanFormatSet() {
      return this.booleanFormat != null;
   }

   String formatBoolean(boolean value, boolean fallbackToTrueFalse) throws TemplateException {
      String s;
      if (value) {
         s = this.getTrueStringValue();
         if (s == null) {
            if (fallbackToTrueFalse) {
               return "true";
            } else {
               throw new _MiscTemplateException(this.getNullBooleanFormatErrorDescription());
            }
         } else {
            return s;
         }
      } else {
         s = this.getFalseStringValue();
         if (s == null) {
            if (fallbackToTrueFalse) {
               return "false";
            } else {
               throw new _MiscTemplateException(this.getNullBooleanFormatErrorDescription());
            }
         } else {
            return s;
         }
      }
   }

   private _ErrorDescriptionBuilder getNullBooleanFormatErrorDescription() {
      return (new _ErrorDescriptionBuilder(new Object[]{"Can't convert boolean to string automatically, because the \"", "boolean_format", "\" setting was ", new _DelayedJQuote(this.getBooleanFormat()), this.getBooleanFormat().equals("true,false") ? ", which is the legacy deprecated default, and we treat it as if no format was set. This is the default configuration; you should provide the format explicitly for each place where you print a boolean." : "."})).tips("Write something like myBool?string('yes', 'no') to specify boolean formatting in place.", new Object[]{"If you want \"true\"/\"false\" result as you are generating computer-language output (not for direct human consumption), then use \"?c\", like ${myBool?c}. (If you always generate computer-language output, then it's might be reasonable to set the \"", "boolean_format", "\" setting to \"c\" instead.)"}, new Object[]{"If you need the same two values on most places, the programmers can set the \"", "boolean_format", "\" setting to something like \"yes,no\". However, then it will be easy to unwillingly format booleans like that."});
   }

   String getTrueStringValue() {
      return this.booleanFormat != null ? this.trueStringValue : (this.parent != null ? this.parent.getTrueStringValue() : null);
   }

   String getFalseStringValue() {
      return this.booleanFormat != null ? this.falseStringValue : (this.parent != null ? this.parent.getFalseStringValue() : null);
   }

   public void setTimeFormat(String timeFormat) {
      NullArgumentException.check("timeFormat", timeFormat);
      this.timeFormat = timeFormat;
      this.properties.setProperty("time_format", timeFormat);
   }

   public String getTimeFormat() {
      return this.timeFormat != null ? this.timeFormat : this.parent.getTimeFormat();
   }

   public boolean isTimeFormatSet() {
      return this.timeFormat != null;
   }

   public void setDateFormat(String dateFormat) {
      NullArgumentException.check("dateFormat", dateFormat);
      this.dateFormat = dateFormat;
      this.properties.setProperty("date_format", dateFormat);
   }

   public String getDateFormat() {
      return this.dateFormat != null ? this.dateFormat : this.parent.getDateFormat();
   }

   public boolean isDateFormatSet() {
      return this.dateFormat != null;
   }

   public void setDateTimeFormat(String dateTimeFormat) {
      NullArgumentException.check("dateTimeFormat", dateTimeFormat);
      this.dateTimeFormat = dateTimeFormat;
      this.properties.setProperty("datetime_format", dateTimeFormat);
   }

   public String getDateTimeFormat() {
      return this.dateTimeFormat != null ? this.dateTimeFormat : this.parent.getDateTimeFormat();
   }

   public boolean isDateTimeFormatSet() {
      return this.dateTimeFormat != null;
   }

   public Map<String, ? extends TemplateDateFormatFactory> getCustomDateFormats() {
      return this.customDateFormats == null ? this.parent.getCustomDateFormats() : this.customDateFormats;
   }

   public Map<String, ? extends TemplateDateFormatFactory> getCustomDateFormatsWithoutFallback() {
      return this.customDateFormats;
   }

   public void setCustomDateFormats(Map<String, ? extends TemplateDateFormatFactory> customDateFormats) {
      NullArgumentException.check("customDateFormats", customDateFormats);
      this.validateFormatNames(customDateFormats.keySet());
      this.customDateFormats = customDateFormats;
   }

   public boolean isCustomDateFormatsSet() {
      return this.customDateFormats != null;
   }

   public TemplateDateFormatFactory getCustomDateFormat(String name) {
      if (this.customDateFormats != null) {
         TemplateDateFormatFactory r = (TemplateDateFormatFactory)this.customDateFormats.get(name);
         if (r != null) {
            return r;
         }
      }

      return this.parent != null ? this.parent.getCustomDateFormat(name) : null;
   }

   public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
      NullArgumentException.check("templateExceptionHandler", templateExceptionHandler);
      this.templateExceptionHandler = templateExceptionHandler;
      this.properties.setProperty("template_exception_handler", templateExceptionHandler.getClass().getName());
   }

   public TemplateExceptionHandler getTemplateExceptionHandler() {
      return this.templateExceptionHandler != null ? this.templateExceptionHandler : this.parent.getTemplateExceptionHandler();
   }

   public boolean isTemplateExceptionHandlerSet() {
      return this.templateExceptionHandler != null;
   }

   public void setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
      NullArgumentException.check("attemptExceptionReporter", attemptExceptionReporter);
      this.attemptExceptionReporter = attemptExceptionReporter;
   }

   public AttemptExceptionReporter getAttemptExceptionReporter() {
      return this.attemptExceptionReporter != null ? this.attemptExceptionReporter : this.parent.getAttemptExceptionReporter();
   }

   public boolean isAttemptExceptionReporterSet() {
      return this.attemptExceptionReporter != null;
   }

   public void setArithmeticEngine(ArithmeticEngine arithmeticEngine) {
      NullArgumentException.check("arithmeticEngine", arithmeticEngine);
      this.arithmeticEngine = arithmeticEngine;
      this.properties.setProperty("arithmetic_engine", arithmeticEngine.getClass().getName());
   }

   public ArithmeticEngine getArithmeticEngine() {
      return this.arithmeticEngine != null ? this.arithmeticEngine : this.parent.getArithmeticEngine();
   }

   public boolean isArithmeticEngineSet() {
      return this.arithmeticEngine != null;
   }

   public void setObjectWrapper(ObjectWrapper objectWrapper) {
      NullArgumentException.check("objectWrapper", objectWrapper);
      this.objectWrapper = objectWrapper;
      this.properties.setProperty("object_wrapper", objectWrapper.getClass().getName());
   }

   public ObjectWrapper getObjectWrapper() {
      return this.objectWrapper != null ? this.objectWrapper : this.parent.getObjectWrapper();
   }

   public boolean isObjectWrapperSet() {
      return this.objectWrapper != null;
   }

   public void setOutputEncoding(String outputEncoding) {
      this.outputEncoding = outputEncoding;
      if (outputEncoding != null) {
         this.properties.setProperty("output_encoding", outputEncoding);
      } else {
         this.properties.remove("output_encoding");
      }

      this.outputEncodingSet = true;
   }

   public String getOutputEncoding() {
      return this.outputEncodingSet ? this.outputEncoding : (this.parent != null ? this.parent.getOutputEncoding() : null);
   }

   public boolean isOutputEncodingSet() {
      return this.outputEncodingSet;
   }

   public void setURLEscapingCharset(String urlEscapingCharset) {
      this.urlEscapingCharset = urlEscapingCharset;
      if (urlEscapingCharset != null) {
         this.properties.setProperty("url_escaping_charset", urlEscapingCharset);
      } else {
         this.properties.remove("url_escaping_charset");
      }

      this.urlEscapingCharsetSet = true;
   }

   public String getURLEscapingCharset() {
      return this.urlEscapingCharsetSet ? this.urlEscapingCharset : (this.parent != null ? this.parent.getURLEscapingCharset() : null);
   }

   public boolean isURLEscapingCharsetSet() {
      return this.urlEscapingCharsetSet;
   }

   public void setNewBuiltinClassResolver(TemplateClassResolver newBuiltinClassResolver) {
      NullArgumentException.check("newBuiltinClassResolver", newBuiltinClassResolver);
      this.newBuiltinClassResolver = newBuiltinClassResolver;
      this.properties.setProperty("new_builtin_class_resolver", newBuiltinClassResolver.getClass().getName());
   }

   public TemplateClassResolver getNewBuiltinClassResolver() {
      return this.newBuiltinClassResolver != null ? this.newBuiltinClassResolver : this.parent.getNewBuiltinClassResolver();
   }

   public boolean isNewBuiltinClassResolverSet() {
      return this.newBuiltinClassResolver != null;
   }

   public void setAutoFlush(boolean autoFlush) {
      this.autoFlush = autoFlush;
      this.properties.setProperty("auto_flush", String.valueOf(autoFlush));
   }

   public boolean getAutoFlush() {
      return this.autoFlush != null ? this.autoFlush : (this.parent != null ? this.parent.getAutoFlush() : true);
   }

   public boolean isAutoFlushSet() {
      return this.autoFlush != null;
   }

   public void setShowErrorTips(boolean showTips) {
      this.showErrorTips = showTips;
      this.properties.setProperty("show_error_tips", String.valueOf(showTips));
   }

   public boolean getShowErrorTips() {
      return this.showErrorTips != null ? this.showErrorTips : (this.parent != null ? this.parent.getShowErrorTips() : true);
   }

   public boolean isShowErrorTipsSet() {
      return this.showErrorTips != null;
   }

   public void setAPIBuiltinEnabled(boolean value) {
      this.apiBuiltinEnabled = value;
      this.properties.setProperty("api_builtin_enabled", String.valueOf(value));
   }

   public boolean isAPIBuiltinEnabled() {
      return this.apiBuiltinEnabled != null ? this.apiBuiltinEnabled : (this.parent != null ? this.parent.isAPIBuiltinEnabled() : false);
   }

   public boolean isAPIBuiltinEnabledSet() {
      return this.apiBuiltinEnabled != null;
   }

   public void setTruncateBuiltinAlgorithm(TruncateBuiltinAlgorithm truncateBuiltinAlgorithm) {
      NullArgumentException.check("truncateBuiltinAlgorithm", truncateBuiltinAlgorithm);
      this.truncateBuiltinAlgorithm = truncateBuiltinAlgorithm;
   }

   public TruncateBuiltinAlgorithm getTruncateBuiltinAlgorithm() {
      return this.truncateBuiltinAlgorithm != null ? this.truncateBuiltinAlgorithm : this.parent.getTruncateBuiltinAlgorithm();
   }

   public boolean isTruncateBuiltinAlgorithmSet() {
      return this.truncateBuiltinAlgorithm != null;
   }

   public void setLogTemplateExceptions(boolean value) {
      this.logTemplateExceptions = value;
      this.properties.setProperty("log_template_exceptions", String.valueOf(value));
   }

   public boolean getLogTemplateExceptions() {
      return this.logTemplateExceptions != null ? this.logTemplateExceptions : (this.parent != null ? this.parent.getLogTemplateExceptions() : true);
   }

   public boolean isLogTemplateExceptionsSet() {
      return this.logTemplateExceptions != null;
   }

   public void setWrapUncheckedExceptions(boolean wrapUncheckedExceptions) {
      this.wrapUncheckedExceptions = wrapUncheckedExceptions;
   }

   public boolean getWrapUncheckedExceptions() {
      return this.wrapUncheckedExceptions != null ? this.wrapUncheckedExceptions : (this.parent != null ? this.parent.getWrapUncheckedExceptions() : false);
   }

   public boolean isWrapUncheckedExceptionsSet() {
      return this.wrapUncheckedExceptions != null;
   }

   public boolean getLazyImports() {
      return this.lazyImports != null ? this.lazyImports : this.parent.getLazyImports();
   }

   public void setLazyImports(boolean lazyImports) {
      this.lazyImports = lazyImports;
   }

   public boolean isLazyImportsSet() {
      return this.lazyImports != null;
   }

   public Boolean getLazyAutoImports() {
      return this.lazyAutoImportsSet ? this.lazyAutoImports : this.parent.getLazyAutoImports();
   }

   public void setLazyAutoImports(Boolean lazyAutoImports) {
      this.lazyAutoImports = lazyAutoImports;
      this.lazyAutoImportsSet = true;
   }

   public boolean isLazyAutoImportsSet() {
      return this.lazyAutoImportsSet;
   }

   public void addAutoImport(String namespaceVarName, String templateName) {
      synchronized(this) {
         if (this.autoImports == null) {
            this.initAutoImportsMap();
         } else {
            this.autoImports.remove(namespaceVarName);
         }

         this.autoImports.put(namespaceVarName, templateName);
      }
   }

   private void initAutoImportsMap() {
      this.autoImports = new LinkedHashMap(4);
   }

   public void removeAutoImport(String namespaceVarName) {
      synchronized(this) {
         if (this.autoImports != null) {
            this.autoImports.remove(namespaceVarName);
         }

      }
   }

   public void setAutoImports(Map map) {
      NullArgumentException.check("map", map);
      synchronized(this) {
         if (this.autoImports != null) {
            this.autoImports.clear();
         }

         Iterator var3 = map.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry)var3.next();
            Object key = entry.getKey();
            if (!(key instanceof String)) {
               throw new IllegalArgumentException("Key in Map wasn't a String, but a(n) " + key.getClass().getName() + ".");
            }

            Object value = entry.getValue();
            if (!(value instanceof String)) {
               throw new IllegalArgumentException("Value in Map wasn't a String, but a(n) " + value.getClass().getName() + ".");
            }

            this.addAutoImport((String)key, (String)value);
         }

      }
   }

   public Map<String, String> getAutoImports() {
      return (Map)(this.autoImports != null ? this.autoImports : this.parent.getAutoImports());
   }

   public boolean isAutoImportsSet() {
      return this.autoImports != null;
   }

   public Map<String, String> getAutoImportsWithoutFallback() {
      return this.autoImports;
   }

   public void addAutoInclude(String templateName) {
      this.addAutoInclude(templateName, false);
   }

   private void addAutoInclude(String templateName, boolean keepDuplicate) {
      synchronized(this) {
         if (this.autoIncludes == null) {
            this.initAutoIncludesList();
         } else if (!keepDuplicate) {
            this.autoIncludes.remove(templateName);
         }

         this.autoIncludes.add(templateName);
      }
   }

   private void initAutoIncludesList() {
      this.autoIncludes = new ArrayList(4);
   }

   public void setAutoIncludes(List templateNames) {
      NullArgumentException.check("templateNames", templateNames);
      synchronized(this) {
         if (this.autoIncludes != null) {
            this.autoIncludes.clear();
         }

         Iterator var3 = templateNames.iterator();

         while(var3.hasNext()) {
            Object templateName = var3.next();
            if (!(templateName instanceof String)) {
               throw new IllegalArgumentException("List items must be String-s.");
            }

            this.addAutoInclude((String)templateName, this instanceof Configuration && ((Configuration)this).getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_25);
         }

      }
   }

   public List<String> getAutoIncludes() {
      return (List)(this.autoIncludes != null ? this.autoIncludes : this.parent.getAutoIncludes());
   }

   public boolean isAutoIncludesSet() {
      return this.autoIncludes != null;
   }

   public List<String> getAutoIncludesWithoutFallback() {
      return this.autoIncludes;
   }

   public void removeAutoInclude(String templateName) {
      synchronized(this) {
         if (this.autoIncludes != null) {
            this.autoIncludes.remove(templateName);
         }

      }
   }

   public void setSetting(String name, String value) throws TemplateException {
      boolean unknown = false;

      try {
         if ("locale".equals(name)) {
            if ("JVM default".equalsIgnoreCase(value)) {
               this.setLocale(Locale.getDefault());
            } else {
               this.setLocale(StringUtil.deduceLocale(value));
            }
         } else if (!"number_format".equals(name) && !"numberFormat".equals(name)) {
            Map map;
            if (!"custom_number_formats".equals(name) && !"customNumberFormats".equals(name)) {
               if (!"time_format".equals(name) && !"timeFormat".equals(name)) {
                  if (!"date_format".equals(name) && !"dateFormat".equals(name)) {
                     if (!"datetime_format".equals(name) && !"datetimeFormat".equals(name)) {
                        if (!"custom_date_formats".equals(name) && !"customDateFormats".equals(name)) {
                           if (!"time_zone".equals(name) && !"timeZone".equals(name)) {
                              if (!"sql_date_and_time_time_zone".equals(name) && !"sqlDateAndTimeTimeZone".equals(name)) {
                                 if (!"classic_compatible".equals(name) && !"classicCompatible".equals(name)) {
                                    if (!"template_exception_handler".equals(name) && !"templateExceptionHandler".equals(name)) {
                                       if (!"attempt_exception_reporter".equals(name) && !"attemptExceptionReporter".equals(name)) {
                                          if (!"arithmetic_engine".equals(name) && !"arithmeticEngine".equals(name)) {
                                             if (!"object_wrapper".equals(name) && !"objectWrapper".equals(name)) {
                                                if (!"boolean_format".equals(name) && !"booleanFormat".equals(name)) {
                                                   if (!"output_encoding".equals(name) && !"outputEncoding".equals(name)) {
                                                      if (!"url_escaping_charset".equals(name) && !"urlEscapingCharset".equals(name)) {
                                                         if (!"strict_bean_models".equals(name) && !"strictBeanModels".equals(name)) {
                                                            if (!"auto_flush".equals(name) && !"autoFlush".equals(name)) {
                                                               if (!"show_error_tips".equals(name) && !"showErrorTips".equals(name)) {
                                                                  if (!"api_builtin_enabled".equals(name) && !"apiBuiltinEnabled".equals(name)) {
                                                                     if (!"truncate_builtin_algorithm".equals(name) && !"truncateBuiltinAlgorithm".equals(name)) {
                                                                        if (!"new_builtin_class_resolver".equals(name) && !"newBuiltinClassResolver".equals(name)) {
                                                                           if (!"log_template_exceptions".equals(name) && !"logTemplateExceptions".equals(name)) {
                                                                              if (!"wrap_unchecked_exceptions".equals(name) && !"wrapUncheckedExceptions".equals(name)) {
                                                                                 if (!"lazy_auto_imports".equals(name) && !"lazyAutoImports".equals(name)) {
                                                                                    if (!"lazy_imports".equals(name) && !"lazyImports".equals(name)) {
                                                                                       if (!"auto_include".equals(name) && !"autoInclude".equals(name)) {
                                                                                          if (!"auto_import".equals(name) && !"autoImport".equals(name)) {
                                                                                             unknown = true;
                                                                                          } else {
                                                                                             this.setAutoImports(this.parseAsImportList(value));
                                                                                          }
                                                                                       } else {
                                                                                          this.setAutoIncludes(this.parseAsList(value));
                                                                                       }
                                                                                    } else {
                                                                                       this.setLazyImports(StringUtil.getYesNo(value));
                                                                                    }
                                                                                 } else {
                                                                                    this.setLazyAutoImports(value.equals("null") ? null : StringUtil.getYesNo(value));
                                                                                 }
                                                                              } else {
                                                                                 this.setWrapUncheckedExceptions(StringUtil.getYesNo(value));
                                                                              }
                                                                           } else {
                                                                              this.setLogTemplateExceptions(StringUtil.getYesNo(value));
                                                                           }
                                                                        } else if ("unrestricted".equals(value)) {
                                                                           this.setNewBuiltinClassResolver(TemplateClassResolver.UNRESTRICTED_RESOLVER);
                                                                        } else if ("safer".equals(value)) {
                                                                           this.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
                                                                        } else if (!"allows_nothing".equals(value) && !"allowsNothing".equals(value)) {
                                                                           if (value.indexOf(":") != -1) {
                                                                              List segments = this.parseAsSegmentedList(value);
                                                                              Set allowedClasses = null;
                                                                              List trustedTemplates = null;

                                                                              for(int i = 0; i < segments.size(); ++i) {
                                                                                 KeyValuePair kv = (KeyValuePair)segments.get(i);
                                                                                 String segmentKey = (String)kv.getKey();
                                                                                 List segmentValue = (List)kv.getValue();
                                                                                 if (!segmentKey.equals("allowed_classes") && !segmentKey.equals("allowedClasses")) {
                                                                                    if (!segmentKey.equals("trusted_templates") && !segmentKey.equals("trustedTemplates")) {
                                                                                       throw new ParseException("Unrecognized list segment key: " + StringUtil.jQuote(segmentKey) + ". Supported keys are: \"" + "allowed_classes" + "\", \"" + "allowedClasses" + "\", \"" + "trusted_templates" + "\", \"" + "trustedTemplates" + "\". ", 0, 0);
                                                                                    }

                                                                                    trustedTemplates = segmentValue;
                                                                                 } else {
                                                                                    allowedClasses = new HashSet(segmentValue);
                                                                                 }
                                                                              }

                                                                              this.setNewBuiltinClassResolver(new OptInTemplateClassResolver(allowedClasses, trustedTemplates));
                                                                           } else {
                                                                              if ("allow_nothing".equals(value)) {
                                                                                 throw new IllegalArgumentException("The correct value would be: allows_nothing");
                                                                              }

                                                                              if ("allowNothing".equals(value)) {
                                                                                 throw new IllegalArgumentException("The correct value would be: allowsNothing");
                                                                              }

                                                                              if (value.indexOf(46) == -1) {
                                                                                 throw this.invalidSettingValueException(name, value);
                                                                              }

                                                                              this.setNewBuiltinClassResolver((TemplateClassResolver)_ObjectBuilderSettingEvaluator.eval(value, TemplateClassResolver.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                                                           }
                                                                        } else {
                                                                           this.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
                                                                        }
                                                                     } else if ("ascii".equalsIgnoreCase(value)) {
                                                                        this.setTruncateBuiltinAlgorithm(DefaultTruncateBuiltinAlgorithm.ASCII_INSTANCE);
                                                                     } else if ("unicode".equalsIgnoreCase(value)) {
                                                                        this.setTruncateBuiltinAlgorithm(DefaultTruncateBuiltinAlgorithm.UNICODE_INSTANCE);
                                                                     } else {
                                                                        this.setTruncateBuiltinAlgorithm((TruncateBuiltinAlgorithm)_ObjectBuilderSettingEvaluator.eval(value, TruncateBuiltinAlgorithm.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                                                     }
                                                                  } else {
                                                                     this.setAPIBuiltinEnabled(StringUtil.getYesNo(value));
                                                                  }
                                                               } else {
                                                                  this.setShowErrorTips(StringUtil.getYesNo(value));
                                                               }
                                                            } else {
                                                               this.setAutoFlush(StringUtil.getYesNo(value));
                                                            }
                                                         } else {
                                                            this.setStrictBeanModels(StringUtil.getYesNo(value));
                                                         }
                                                      } else {
                                                         this.setURLEscapingCharset(value);
                                                      }
                                                   } else {
                                                      this.setOutputEncoding(value);
                                                   }
                                                } else {
                                                   this.setBooleanFormat(value);
                                                }
                                             } else if ("default".equalsIgnoreCase(value)) {
                                                if (this instanceof Configuration) {
                                                   ((Configuration)this).unsetObjectWrapper();
                                                } else {
                                                   this.setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_0));
                                                }
                                             } else if ("default_2_3_0".equalsIgnoreCase(value)) {
                                                this.setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_0));
                                             } else if ("simple".equalsIgnoreCase(value)) {
                                                this.setObjectWrapper(ObjectWrapper.SIMPLE_WRAPPER);
                                             } else if ("beans".equalsIgnoreCase(value)) {
                                                this.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
                                             } else if ("jython".equalsIgnoreCase(value)) {
                                                Class clazz = Class.forName("freemarker.ext.jython.JythonWrapper");
                                                this.setObjectWrapper((ObjectWrapper)clazz.getField("INSTANCE").get((Object)null));
                                             } else {
                                                this.setObjectWrapper((ObjectWrapper)_ObjectBuilderSettingEvaluator.eval(value, ObjectWrapper.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                             }
                                          } else if (value.indexOf(46) == -1) {
                                             if ("bigdecimal".equalsIgnoreCase(value)) {
                                                this.setArithmeticEngine(ArithmeticEngine.BIGDECIMAL_ENGINE);
                                             } else {
                                                if (!"conservative".equalsIgnoreCase(value)) {
                                                   throw this.invalidSettingValueException(name, value);
                                                }

                                                this.setArithmeticEngine(ArithmeticEngine.CONSERVATIVE_ENGINE);
                                             }
                                          } else {
                                             this.setArithmeticEngine((ArithmeticEngine)_ObjectBuilderSettingEvaluator.eval(value, ArithmeticEngine.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                          }
                                       } else if (value.indexOf(46) == -1) {
                                          if (!"log_error".equalsIgnoreCase(value) && !"logError".equals(value)) {
                                             if (!"log_warn".equalsIgnoreCase(value) && !"logWarn".equals(value)) {
                                                if (!"default".equalsIgnoreCase(value) || !(this instanceof Configuration)) {
                                                   throw this.invalidSettingValueException(name, value);
                                                }

                                                ((Configuration)this).unsetAttemptExceptionReporter();
                                             } else {
                                                this.setAttemptExceptionReporter(AttemptExceptionReporter.LOG_WARN_REPORTER);
                                             }
                                          } else {
                                             this.setAttemptExceptionReporter(AttemptExceptionReporter.LOG_ERROR_REPORTER);
                                          }
                                       } else {
                                          this.setAttemptExceptionReporter((AttemptExceptionReporter)_ObjectBuilderSettingEvaluator.eval(value, AttemptExceptionReporter.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                       }
                                    } else if (value.indexOf(46) == -1) {
                                       if ("debug".equalsIgnoreCase(value)) {
                                          this.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                                       } else if (!"html_debug".equalsIgnoreCase(value) && !"htmlDebug".equals(value)) {
                                          if ("ignore".equalsIgnoreCase(value)) {
                                             this.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
                                          } else if ("rethrow".equalsIgnoreCase(value)) {
                                             this.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                                          } else {
                                             if (!"default".equalsIgnoreCase(value) || !(this instanceof Configuration)) {
                                                throw this.invalidSettingValueException(name, value);
                                             }

                                             ((Configuration)this).unsetTemplateExceptionHandler();
                                          }
                                       } else {
                                          this.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                                       }
                                    } else {
                                       this.setTemplateExceptionHandler((TemplateExceptionHandler)_ObjectBuilderSettingEvaluator.eval(value, TemplateExceptionHandler.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                    }
                                 } else {
                                    char firstChar;
                                    if (value != null && value.length() > 0) {
                                       firstChar = value.charAt(0);
                                    } else {
                                       firstChar = 0;
                                    }

                                    if (!Character.isDigit(firstChar) && firstChar != '+' && firstChar != '-') {
                                       this.setClassicCompatible(value != null ? StringUtil.getYesNo(value) : false);
                                    } else {
                                       this.setClassicCompatibleAsInt(Integer.parseInt(value));
                                    }
                                 }
                              } else {
                                 this.setSQLDateAndTimeTimeZone(value.equals("null") ? null : this.parseTimeZoneSettingValue(value));
                              }
                           } else {
                              this.setTimeZone(this.parseTimeZoneSettingValue(value));
                           }
                        } else {
                           map = (Map)_ObjectBuilderSettingEvaluator.eval(value, Map.class, false, _SettingEvaluationEnvironment.getCurrent());
                           _CoreAPI.checkSettingValueItemsType("Map keys", String.class, map.keySet());
                           _CoreAPI.checkSettingValueItemsType("Map values", TemplateDateFormatFactory.class, map.values());
                           this.setCustomDateFormats(map);
                        }
                     } else {
                        this.setDateTimeFormat(value);
                     }
                  } else {
                     this.setDateFormat(value);
                  }
               } else {
                  this.setTimeFormat(value);
               }
            } else {
               map = (Map)_ObjectBuilderSettingEvaluator.eval(value, Map.class, false, _SettingEvaluationEnvironment.getCurrent());
               _CoreAPI.checkSettingValueItemsType("Map keys", String.class, map.keySet());
               _CoreAPI.checkSettingValueItemsType("Map values", TemplateNumberFormatFactory.class, map.values());
               this.setCustomNumberFormats(map);
            }
         } else {
            this.setNumberFormat(value);
         }
      } catch (Exception var11) {
         throw this.settingValueAssignmentException(name, value, var11);
      }

      if (unknown) {
         throw this.unknownSettingException(name);
      }
   }

   public Set<String> getSettingNames(boolean camelCase) {
      return new _SortedArraySet(camelCase ? SETTING_NAMES_CAMEL_CASE : SETTING_NAMES_SNAKE_CASE);
   }

   private TimeZone parseTimeZoneSettingValue(String value) {
      TimeZone tz;
      if ("JVM default".equalsIgnoreCase(value)) {
         tz = TimeZone.getDefault();
      } else {
         tz = TimeZone.getTimeZone(value);
      }

      return tz;
   }

   /** @deprecated */
   @Deprecated
   public void setStrictBeanModels(boolean strict) {
      if (!(this.objectWrapper instanceof BeansWrapper)) {
         throw new IllegalStateException("The value of the object_wrapper setting isn't a " + BeansWrapper.class.getName() + ".");
      } else {
         ((BeansWrapper)this.objectWrapper).setStrict(strict);
      }
   }

   /** @deprecated */
   @Deprecated
   public String getSetting(String key) {
      return this.properties.getProperty(key);
   }

   /** @deprecated */
   @Deprecated
   public Map getSettings() {
      return Collections.unmodifiableMap(this.properties);
   }

   protected Environment getEnvironment() {
      return this instanceof Environment ? (Environment)this : Environment.getCurrentEnvironment();
   }

   protected TemplateException unknownSettingException(String name) {
      return new UnknownSettingException(this.getEnvironment(), name, this.getCorrectedNameForUnknownSetting(name));
   }

   protected String getCorrectedNameForUnknownSetting(String name) {
      return null;
   }

   protected TemplateException settingValueAssignmentException(String name, String value, Throwable cause) {
      return new SettingValueAssignmentException(this.getEnvironment(), name, value, cause);
   }

   protected TemplateException invalidSettingValueException(String name, String value) {
      return new _MiscTemplateException(this.getEnvironment(), new Object[]{"Invalid value for setting ", new _DelayedJQuote(name), ": ", new _DelayedJQuote(value)});
   }

   public void setSettings(Properties props) throws TemplateException {
      _SettingEvaluationEnvironment prevEnv = _SettingEvaluationEnvironment.startScope();

      try {
         Iterator it = props.keySet().iterator();

         while(it.hasNext()) {
            String key = (String)it.next();
            this.setSetting(key, props.getProperty(key).trim());
         }
      } finally {
         _SettingEvaluationEnvironment.endScope(prevEnv);
      }

   }

   public void setSettings(InputStream propsIn) throws TemplateException, IOException {
      Properties p = new Properties();
      p.load(propsIn);
      this.setSettings(p);
   }

   void setCustomAttribute(Object key, Object value) {
      synchronized(this.customAttributes) {
         this.customAttributes.put(key, value);
      }
   }

   Object getCustomAttribute(Object key, CustomAttribute attr) {
      synchronized(this.customAttributes) {
         Object o = this.customAttributes.get(key);
         if (o == null && !this.customAttributes.containsKey(key)) {
            o = attr.create();
            this.customAttributes.put(key, o);
         }

         return o;
      }
   }

   boolean isCustomAttributeSet(Object key) {
      return this.customAttributes.containsKey(key);
   }

   void copyDirectCustomAttributes(Configurable target, boolean overwriteExisting) {
      synchronized(this.customAttributes) {
         Iterator var4 = this.customAttributes.entrySet().iterator();

         while(true) {
            Map.Entry custAttrEnt;
            Object custAttrKey;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               custAttrEnt = (Map.Entry)var4.next();
               custAttrKey = custAttrEnt.getKey();
            } while(!overwriteExisting && target.isCustomAttributeSet(custAttrKey));

            if (custAttrKey instanceof String) {
               target.setCustomAttribute((String)custAttrKey, custAttrEnt.getValue());
            } else {
               target.setCustomAttribute(custAttrKey, custAttrEnt.getValue());
            }
         }
      }
   }

   public void setCustomAttribute(String name, Object value) {
      synchronized(this.customAttributes) {
         this.customAttributes.put(name, value);
      }
   }

   public String[] getCustomAttributeNames() {
      synchronized(this.customAttributes) {
         Collection names = new LinkedList(this.customAttributes.keySet());
         Iterator iter = names.iterator();

         while(iter.hasNext()) {
            if (!(iter.next() instanceof String)) {
               iter.remove();
            }
         }

         return (String[])((String[])names.toArray(new String[names.size()]));
      }
   }

   public void removeCustomAttribute(String name) {
      synchronized(this.customAttributes) {
         this.customAttributes.remove(name);
      }
   }

   public Object getCustomAttribute(String name) {
      Object retval;
      synchronized(this.customAttributes) {
         retval = this.customAttributes.get(name);
         if (retval == null && this.customAttributes.containsKey(name)) {
            return null;
         }
      }

      return retval == null && this.parent != null ? this.parent.getCustomAttribute(name) : retval;
   }

   protected void doAutoImportsAndIncludes(Environment env) throws TemplateException, IOException {
      if (this.parent != null) {
         this.parent.doAutoImportsAndIncludes(env);
      }

   }

   protected ArrayList parseAsList(String text) throws ParseException {
      return (new SettingStringParser(text)).parseAsList();
   }

   protected ArrayList parseAsSegmentedList(String text) throws ParseException {
      return (new SettingStringParser(text)).parseAsSegmentedList();
   }

   protected HashMap parseAsImportList(String text) throws ParseException {
      return (new SettingStringParser(text)).parseAsImportList();
   }

   private static class SettingStringParser {
      private String text;
      private int p;
      private int ln;

      private SettingStringParser(String text) {
         this.text = text;
         this.p = 0;
         this.ln = text.length();
      }

      ArrayList parseAsSegmentedList() throws ParseException {
         ArrayList segments = new ArrayList();
         ArrayList currentSegment = null;

         while(true) {
            char c = this.skipWS();
            if (c == ' ') {
               break;
            }

            String item = this.fetchStringValue();
            c = this.skipWS();
            if (c == ':') {
               currentSegment = new ArrayList();
               segments.add(new KeyValuePair(item, currentSegment));
            } else {
               if (currentSegment == null) {
                  throw new ParseException("The very first list item must be followed by \":\" so it will be the key for the following sub-list.", 0, 0);
               }

               currentSegment.add(item);
            }

            if (c == ' ') {
               break;
            }

            if (c != ',' && c != ':') {
               throw new ParseException("Expected \",\" or \":\" or the end of text but found \"" + c + "\"", 0, 0);
            }

            ++this.p;
         }

         return segments;
      }

      ArrayList parseAsList() throws ParseException {
         ArrayList seq = new ArrayList();

         while(true) {
            char c = this.skipWS();
            if (c == ' ') {
               break;
            }

            seq.add(this.fetchStringValue());
            c = this.skipWS();
            if (c == ' ') {
               break;
            }

            if (c != ',') {
               throw new ParseException("Expected \",\" or the end of text but found \"" + c + "\"", 0, 0);
            }

            ++this.p;
         }

         return seq;
      }

      HashMap parseAsImportList() throws ParseException {
         HashMap map = new HashMap();

         while(true) {
            char c = this.skipWS();
            if (c != ' ') {
               String lib = this.fetchStringValue();
               c = this.skipWS();
               if (c == ' ') {
                  throw new ParseException("Unexpected end of text: expected \"as\"", 0, 0);
               }

               String s = this.fetchKeyword();
               if (!s.equalsIgnoreCase("as")) {
                  throw new ParseException("Expected \"as\", but found " + StringUtil.jQuote(s), 0, 0);
               }

               c = this.skipWS();
               if (c == ' ') {
                  throw new ParseException("Unexpected end of text: expected gate hash name", 0, 0);
               }

               String ns = this.fetchStringValue();
               map.put(ns, lib);
               c = this.skipWS();
               if (c != ' ') {
                  if (c != ',') {
                     throw new ParseException("Expected \",\" or the end of text but found \"" + c + "\"", 0, 0);
                  }

                  ++this.p;
                  continue;
               }
            }

            return map;
         }
      }

      String fetchStringValue() throws ParseException {
         String w = this.fetchWord();
         if (w.startsWith("'") || w.startsWith("\"")) {
            w = w.substring(1, w.length() - 1);
         }

         return StringUtil.FTLStringLiteralDec(w);
      }

      String fetchKeyword() throws ParseException {
         String w = this.fetchWord();
         if (!w.startsWith("'") && !w.startsWith("\"")) {
            return w;
         } else {
            throw new ParseException("Keyword expected, but a string value found: " + w, 0, 0);
         }
      }

      char skipWS() {
         while(this.p < this.ln) {
            char c = this.text.charAt(this.p);
            if (!Character.isWhitespace(c)) {
               return c;
            }

            ++this.p;
         }

         return ' ';
      }

      private String fetchWord() throws ParseException {
         if (this.p == this.ln) {
            throw new ParseException("Unexpeced end of text", 0, 0);
         } else {
            char c = this.text.charAt(this.p);
            int b = this.p;
            if (c != '\'' && c != '"') {
               do {
                  c = this.text.charAt(this.p);
                  if (!Character.isLetterOrDigit(c) && c != '/' && c != '\\' && c != '_' && c != '.' && c != '-' && c != '!' && c != '*' && c != '?') {
                     break;
                  }

                  ++this.p;
               } while(this.p < this.ln);

               if (b == this.p) {
                  throw new ParseException("Unexpected character: " + c, 0, 0);
               } else {
                  return this.text.substring(b, this.p);
               }
            } else {
               boolean escaped = false;
               char q = c;
               ++this.p;

               for(; this.p < this.ln; ++this.p) {
                  c = this.text.charAt(this.p);
                  if (!escaped) {
                     if (c == '\\') {
                        escaped = true;
                     } else if (c == q) {
                        break;
                     }
                  } else {
                     escaped = false;
                  }
               }

               if (this.p == this.ln) {
                  throw new ParseException("Missing " + q, 0, 0);
               } else {
                  ++this.p;
                  return this.text.substring(b, this.p);
               }
            }
         }
      }

      // $FF: synthetic method
      SettingStringParser(String x0, Object x1) {
         this(x0);
      }
   }

   private static class KeyValuePair {
      private final Object key;
      private final Object value;

      KeyValuePair(Object key, Object value) {
         this.key = key;
         this.value = value;
      }

      Object getKey() {
         return this.key;
      }

      Object getValue() {
         return this.value;
      }
   }

   public static class SettingValueAssignmentException extends _MiscTemplateException {
      private SettingValueAssignmentException(Environment env, String name, String value, Throwable cause) {
         super(cause, env, "Failed to set FreeMarker configuration setting ", new _DelayedJQuote(name), " to value ", new _DelayedJQuote(value), "; see cause exception.");
      }

      // $FF: synthetic method
      SettingValueAssignmentException(Environment x0, String x1, String x2, Throwable x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }

   public static class UnknownSettingException extends _MiscTemplateException {
      private UnknownSettingException(Environment env, String name, String correctedName) {
         super(env, "Unknown FreeMarker configuration setting: ", new _DelayedJQuote(name), correctedName == null ? "" : new Object[]{". You may meant: ", new _DelayedJQuote(correctedName)});
      }

      // $FF: synthetic method
      UnknownSettingException(Environment x0, String x1, String x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
