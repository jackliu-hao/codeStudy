/*      */ package freemarker.core;
/*      */ 
/*      */ import freemarker.ext.beans.BeansWrapper;
/*      */ import freemarker.template.AttemptExceptionReporter;
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template.ObjectWrapper;
/*      */ import freemarker.template.TemplateException;
/*      */ import freemarker.template.TemplateExceptionHandler;
/*      */ import freemarker.template.Version;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import freemarker.template.utility.NullArgumentException;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Configurable
/*      */ {
/*      */   static final String C_TRUE_FALSE = "true,false";
/*      */   static final String C_FORMAT_STRING = "c";
/*      */   private static final String NULL = "null";
/*      */   private static final String DEFAULT = "default";
/*      */   private static final String DEFAULT_2_3_0 = "default_2_3_0";
/*      */   private static final String JVM_DEFAULT = "JVM default";
/*      */   public static final String LOCALE_KEY_SNAKE_CASE = "locale";
/*      */   public static final String LOCALE_KEY_CAMEL_CASE = "locale";
/*      */   public static final String LOCALE_KEY = "locale";
/*      */   public static final String NUMBER_FORMAT_KEY_SNAKE_CASE = "number_format";
/*      */   public static final String NUMBER_FORMAT_KEY_CAMEL_CASE = "numberFormat";
/*      */   public static final String NUMBER_FORMAT_KEY = "number_format";
/*      */   public static final String CUSTOM_NUMBER_FORMATS_KEY_SNAKE_CASE = "custom_number_formats";
/*      */   public static final String CUSTOM_NUMBER_FORMATS_KEY_CAMEL_CASE = "customNumberFormats";
/*      */   public static final String CUSTOM_NUMBER_FORMATS_KEY = "custom_number_formats";
/*      */   public static final String TIME_FORMAT_KEY_SNAKE_CASE = "time_format";
/*      */   public static final String TIME_FORMAT_KEY_CAMEL_CASE = "timeFormat";
/*      */   public static final String TIME_FORMAT_KEY = "time_format";
/*      */   public static final String DATE_FORMAT_KEY_SNAKE_CASE = "date_format";
/*      */   public static final String DATE_FORMAT_KEY_CAMEL_CASE = "dateFormat";
/*      */   public static final String DATE_FORMAT_KEY = "date_format";
/*      */   public static final String CUSTOM_DATE_FORMATS_KEY_SNAKE_CASE = "custom_date_formats";
/*      */   public static final String CUSTOM_DATE_FORMATS_KEY_CAMEL_CASE = "customDateFormats";
/*      */   public static final String CUSTOM_DATE_FORMATS_KEY = "custom_date_formats";
/*      */   public static final String DATETIME_FORMAT_KEY_SNAKE_CASE = "datetime_format";
/*      */   public static final String DATETIME_FORMAT_KEY_CAMEL_CASE = "datetimeFormat";
/*      */   public static final String DATETIME_FORMAT_KEY = "datetime_format";
/*      */   public static final String TIME_ZONE_KEY_SNAKE_CASE = "time_zone";
/*      */   public static final String TIME_ZONE_KEY_CAMEL_CASE = "timeZone";
/*      */   public static final String TIME_ZONE_KEY = "time_zone";
/*      */   public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY_SNAKE_CASE = "sql_date_and_time_time_zone";
/*      */   public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY_CAMEL_CASE = "sqlDateAndTimeTimeZone";
/*      */   public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY = "sql_date_and_time_time_zone";
/*      */   public static final String CLASSIC_COMPATIBLE_KEY_SNAKE_CASE = "classic_compatible";
/*      */   public static final String CLASSIC_COMPATIBLE_KEY_CAMEL_CASE = "classicCompatible";
/*      */   public static final String CLASSIC_COMPATIBLE_KEY = "classic_compatible";
/*      */   public static final String TEMPLATE_EXCEPTION_HANDLER_KEY_SNAKE_CASE = "template_exception_handler";
/*      */   public static final String TEMPLATE_EXCEPTION_HANDLER_KEY_CAMEL_CASE = "templateExceptionHandler";
/*      */   public static final String TEMPLATE_EXCEPTION_HANDLER_KEY = "template_exception_handler";
/*      */   public static final String ATTEMPT_EXCEPTION_REPORTER_KEY_SNAKE_CASE = "attempt_exception_reporter";
/*      */   public static final String ATTEMPT_EXCEPTION_REPORTER_KEY_CAMEL_CASE = "attemptExceptionReporter";
/*      */   public static final String ATTEMPT_EXCEPTION_REPORTER_KEY = "attempt_exception_reporter";
/*      */   public static final String ARITHMETIC_ENGINE_KEY_SNAKE_CASE = "arithmetic_engine";
/*      */   public static final String ARITHMETIC_ENGINE_KEY_CAMEL_CASE = "arithmeticEngine";
/*      */   public static final String ARITHMETIC_ENGINE_KEY = "arithmetic_engine";
/*      */   public static final String OBJECT_WRAPPER_KEY_SNAKE_CASE = "object_wrapper";
/*      */   public static final String OBJECT_WRAPPER_KEY_CAMEL_CASE = "objectWrapper";
/*      */   public static final String OBJECT_WRAPPER_KEY = "object_wrapper";
/*      */   public static final String BOOLEAN_FORMAT_KEY_SNAKE_CASE = "boolean_format";
/*      */   public static final String BOOLEAN_FORMAT_KEY_CAMEL_CASE = "booleanFormat";
/*      */   public static final String BOOLEAN_FORMAT_KEY = "boolean_format";
/*      */   public static final String OUTPUT_ENCODING_KEY_SNAKE_CASE = "output_encoding";
/*      */   public static final String OUTPUT_ENCODING_KEY_CAMEL_CASE = "outputEncoding";
/*      */   public static final String OUTPUT_ENCODING_KEY = "output_encoding";
/*      */   public static final String URL_ESCAPING_CHARSET_KEY_SNAKE_CASE = "url_escaping_charset";
/*      */   public static final String URL_ESCAPING_CHARSET_KEY_CAMEL_CASE = "urlEscapingCharset";
/*      */   public static final String URL_ESCAPING_CHARSET_KEY = "url_escaping_charset";
/*      */   public static final String STRICT_BEAN_MODELS_KEY_SNAKE_CASE = "strict_bean_models";
/*      */   public static final String STRICT_BEAN_MODELS_KEY_CAMEL_CASE = "strictBeanModels";
/*      */   public static final String STRICT_BEAN_MODELS_KEY = "strict_bean_models";
/*      */   public static final String AUTO_FLUSH_KEY_SNAKE_CASE = "auto_flush";
/*      */   public static final String AUTO_FLUSH_KEY_CAMEL_CASE = "autoFlush";
/*      */   public static final String AUTO_FLUSH_KEY = "auto_flush";
/*      */   public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY_SNAKE_CASE = "new_builtin_class_resolver";
/*      */   public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY_CAMEL_CASE = "newBuiltinClassResolver";
/*      */   public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY = "new_builtin_class_resolver";
/*      */   public static final String SHOW_ERROR_TIPS_KEY_SNAKE_CASE = "show_error_tips";
/*      */   public static final String SHOW_ERROR_TIPS_KEY_CAMEL_CASE = "showErrorTips";
/*      */   public static final String SHOW_ERROR_TIPS_KEY = "show_error_tips";
/*      */   public static final String API_BUILTIN_ENABLED_KEY_SNAKE_CASE = "api_builtin_enabled";
/*      */   public static final String API_BUILTIN_ENABLED_KEY_CAMEL_CASE = "apiBuiltinEnabled";
/*      */   public static final String API_BUILTIN_ENABLED_KEY = "api_builtin_enabled";
/*      */   public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY_SNAKE_CASE = "truncate_builtin_algorithm";
/*      */   public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY_CAMEL_CASE = "truncateBuiltinAlgorithm";
/*      */   public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY = "truncate_builtin_algorithm";
/*      */   public static final String LOG_TEMPLATE_EXCEPTIONS_KEY_SNAKE_CASE = "log_template_exceptions";
/*      */   public static final String LOG_TEMPLATE_EXCEPTIONS_KEY_CAMEL_CASE = "logTemplateExceptions";
/*      */   public static final String LOG_TEMPLATE_EXCEPTIONS_KEY = "log_template_exceptions";
/*      */   public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY_SNAKE_CASE = "wrap_unchecked_exceptions";
/*      */   public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY_CAMEL_CASE = "wrapUncheckedExceptions";
/*      */   public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY = "wrap_unchecked_exceptions";
/*      */   public static final String LAZY_IMPORTS_KEY_SNAKE_CASE = "lazy_imports";
/*      */   public static final String LAZY_IMPORTS_KEY_CAMEL_CASE = "lazyImports";
/*      */   public static final String LAZY_IMPORTS_KEY = "lazy_imports";
/*      */   public static final String LAZY_AUTO_IMPORTS_KEY_SNAKE_CASE = "lazy_auto_imports";
/*      */   public static final String LAZY_AUTO_IMPORTS_KEY_CAMEL_CASE = "lazyAutoImports";
/*      */   public static final String LAZY_AUTO_IMPORTS_KEY = "lazy_auto_imports";
/*      */   public static final String AUTO_IMPORT_KEY_SNAKE_CASE = "auto_import";
/*      */   public static final String AUTO_IMPORT_KEY_CAMEL_CASE = "autoImport";
/*      */   public static final String AUTO_IMPORT_KEY = "auto_import";
/*      */   public static final String AUTO_INCLUDE_KEY_SNAKE_CASE = "auto_include";
/*      */   public static final String AUTO_INCLUDE_KEY_CAMEL_CASE = "autoInclude";
/*      */   public static final String AUTO_INCLUDE_KEY = "auto_include";
/*      */   @Deprecated
/*      */   public static final String STRICT_BEAN_MODELS = "strict_bean_models";
/*  299 */   private static final String[] SETTING_NAMES_SNAKE_CASE = new String[] { "api_builtin_enabled", "arithmetic_engine", "attempt_exception_reporter", "auto_flush", "auto_import", "auto_include", "boolean_format", "classic_compatible", "custom_date_formats", "custom_number_formats", "date_format", "datetime_format", "lazy_auto_imports", "lazy_imports", "locale", "log_template_exceptions", "new_builtin_class_resolver", "number_format", "object_wrapper", "output_encoding", "show_error_tips", "sql_date_and_time_time_zone", "strict_bean_models", "template_exception_handler", "time_format", "time_zone", "truncate_builtin_algorithm", "url_escaping_charset", "wrap_unchecked_exceptions" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  332 */   private static final String[] SETTING_NAMES_CAMEL_CASE = new String[] { "apiBuiltinEnabled", "arithmeticEngine", "attemptExceptionReporter", "autoFlush", "autoImport", "autoInclude", "booleanFormat", "classicCompatible", "customDateFormats", "customNumberFormats", "dateFormat", "datetimeFormat", "lazyAutoImports", "lazyImports", "locale", "logTemplateExceptions", "newBuiltinClassResolver", "numberFormat", "objectWrapper", "outputEncoding", "showErrorTips", "sqlDateAndTimeTimeZone", "strictBeanModels", "templateExceptionHandler", "timeFormat", "timeZone", "truncateBuiltinAlgorithm", "urlEscapingCharset", "wrapUncheckedExceptions" };
/*      */   
/*      */   private Configurable parent;
/*      */   
/*      */   private Properties properties;
/*      */   
/*      */   private HashMap<Object, Object> customAttributes;
/*      */   
/*      */   private Locale locale;
/*      */   
/*      */   private String numberFormat;
/*      */   
/*      */   private String timeFormat;
/*      */   
/*      */   private String dateFormat;
/*      */   
/*      */   private String dateTimeFormat;
/*      */   
/*      */   private TimeZone timeZone;
/*      */   
/*      */   private TimeZone sqlDataAndTimeTimeZone;
/*      */   
/*      */   private boolean sqlDataAndTimeTimeZoneSet;
/*      */   
/*      */   private String booleanFormat;
/*      */   
/*      */   private String trueStringValue;
/*      */   
/*      */   private String falseStringValue;
/*      */   
/*      */   private Integer classicCompatible;
/*      */   
/*      */   private TemplateExceptionHandler templateExceptionHandler;
/*      */   
/*      */   private AttemptExceptionReporter attemptExceptionReporter;
/*      */   
/*      */   private ArithmeticEngine arithmeticEngine;
/*      */   
/*      */   private ObjectWrapper objectWrapper;
/*      */   
/*      */   private String outputEncoding;
/*      */   
/*      */   private boolean outputEncodingSet;
/*      */   
/*      */   private String urlEscapingCharset;
/*      */   
/*      */   private boolean urlEscapingCharsetSet;
/*      */   
/*      */   private Boolean autoFlush;
/*      */   
/*      */   private Boolean showErrorTips;
/*      */   
/*      */   private TemplateClassResolver newBuiltinClassResolver;
/*      */   
/*      */   private Boolean apiBuiltinEnabled;
/*      */   
/*      */   private TruncateBuiltinAlgorithm truncateBuiltinAlgorithm;
/*      */   
/*      */   private Boolean logTemplateExceptions;
/*      */   
/*      */   private Boolean wrapUncheckedExceptions;
/*      */   
/*      */   private Map<String, ? extends TemplateDateFormatFactory> customDateFormats;
/*      */   
/*      */   private Map<String, ? extends TemplateNumberFormatFactory> customNumberFormats;
/*      */   
/*      */   private LinkedHashMap<String, String> autoImports;
/*      */   
/*      */   private ArrayList<String> autoIncludes;
/*      */   private Boolean lazyImports;
/*      */   private Boolean lazyAutoImports;
/*      */   private boolean lazyAutoImportsSet;
/*      */   private static final String ALLOWED_CLASSES_SNAKE_CASE = "allowed_classes";
/*      */   private static final String TRUSTED_TEMPLATES_SNAKE_CASE = "trusted_templates";
/*      */   private static final String ALLOWED_CLASSES_CAMEL_CASE = "allowedClasses";
/*      */   private static final String TRUSTED_TEMPLATES_CAMEL_CASE = "trustedTemplates";
/*      */   
/*      */   @Deprecated
/*      */   public Configurable() {
/*  411 */     this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Configurable(Version incompatibleImprovements) {
/*  420 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/*      */     
/*  422 */     this.parent = null;
/*  423 */     this.properties = new Properties();
/*      */     
/*  425 */     this.locale = _TemplateAPI.getDefaultLocale();
/*  426 */     this.properties.setProperty("locale", this.locale.toString());
/*      */     
/*  428 */     this.timeZone = _TemplateAPI.getDefaultTimeZone();
/*  429 */     this.properties.setProperty("time_zone", this.timeZone.getID());
/*      */     
/*  431 */     this.sqlDataAndTimeTimeZone = null;
/*  432 */     this.properties.setProperty("sql_date_and_time_time_zone", String.valueOf(this.sqlDataAndTimeTimeZone));
/*      */     
/*  434 */     this.numberFormat = "number";
/*  435 */     this.properties.setProperty("number_format", this.numberFormat);
/*      */     
/*  437 */     this.timeFormat = "";
/*  438 */     this.properties.setProperty("time_format", this.timeFormat);
/*      */     
/*  440 */     this.dateFormat = "";
/*  441 */     this.properties.setProperty("date_format", this.dateFormat);
/*      */     
/*  443 */     this.dateTimeFormat = "";
/*  444 */     this.properties.setProperty("datetime_format", this.dateTimeFormat);
/*      */     
/*  446 */     this.classicCompatible = Integer.valueOf(0);
/*  447 */     this.properties.setProperty("classic_compatible", this.classicCompatible.toString());
/*      */     
/*  449 */     this.templateExceptionHandler = _TemplateAPI.getDefaultTemplateExceptionHandler(incompatibleImprovements);
/*  450 */     this.properties.setProperty("template_exception_handler", this.templateExceptionHandler.getClass().getName());
/*      */     
/*  452 */     this.wrapUncheckedExceptions = Boolean.valueOf(_TemplateAPI.getDefaultWrapUncheckedExceptions(incompatibleImprovements));
/*      */     
/*  454 */     this.attemptExceptionReporter = _TemplateAPI.getDefaultAttemptExceptionReporter(incompatibleImprovements);
/*      */     
/*  456 */     this.arithmeticEngine = ArithmeticEngine.BIGDECIMAL_ENGINE;
/*  457 */     this.properties.setProperty("arithmetic_engine", this.arithmeticEngine.getClass().getName());
/*      */     
/*  459 */     this.objectWrapper = Configuration.getDefaultObjectWrapper(incompatibleImprovements);
/*      */ 
/*      */     
/*  462 */     this.autoFlush = Boolean.TRUE;
/*  463 */     this.properties.setProperty("auto_flush", this.autoFlush.toString());
/*      */     
/*  465 */     this.newBuiltinClassResolver = TemplateClassResolver.UNRESTRICTED_RESOLVER;
/*  466 */     this.properties.setProperty("new_builtin_class_resolver", this.newBuiltinClassResolver.getClass().getName());
/*      */     
/*  468 */     this.truncateBuiltinAlgorithm = DefaultTruncateBuiltinAlgorithm.ASCII_INSTANCE;
/*      */     
/*  470 */     this.showErrorTips = Boolean.TRUE;
/*  471 */     this.properties.setProperty("show_error_tips", this.showErrorTips.toString());
/*      */     
/*  473 */     this.apiBuiltinEnabled = Boolean.FALSE;
/*  474 */     this.properties.setProperty("api_builtin_enabled", this.apiBuiltinEnabled.toString());
/*      */     
/*  476 */     this.logTemplateExceptions = Boolean.valueOf(
/*  477 */         _TemplateAPI.getDefaultLogTemplateExceptions(incompatibleImprovements));
/*  478 */     this.properties.setProperty("log_template_exceptions", this.logTemplateExceptions.toString());
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  483 */     setBooleanFormat("true,false");
/*      */     
/*  485 */     this.customAttributes = new HashMap<>();
/*      */     
/*  487 */     this.customDateFormats = Collections.emptyMap();
/*  488 */     this.customNumberFormats = Collections.emptyMap();
/*      */     
/*  490 */     this.lazyImports = Boolean.valueOf(false);
/*  491 */     this.lazyAutoImportsSet = true;
/*      */     
/*  493 */     initAutoImportsMap();
/*  494 */     initAutoIncludesList();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Configurable(Configurable parent) {
/*  502 */     this.parent = parent;
/*  503 */     this.properties = new Properties(parent.properties);
/*  504 */     this.customAttributes = new HashMap<>(0);
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object clone() throws CloneNotSupportedException {
/*  509 */     Configurable copy = (Configurable)super.clone();
/*  510 */     if (this.properties != null) {
/*  511 */       copy.properties = new Properties(this.properties);
/*      */     }
/*  513 */     if (this.customAttributes != null) {
/*  514 */       copy.customAttributes = (HashMap<Object, Object>)this.customAttributes.clone();
/*      */     }
/*  516 */     if (this.autoImports != null) {
/*  517 */       copy.autoImports = (LinkedHashMap<String, String>)this.autoImports.clone();
/*      */     }
/*  519 */     if (this.autoIncludes != null) {
/*  520 */       copy.autoIncludes = (ArrayList<String>)this.autoIncludes.clone();
/*      */     }
/*  522 */     return copy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Configurable getParent() {
/*  544 */     return this.parent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setParent(Configurable parent) {
/*  553 */     this.parent = parent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClassicCompatible(boolean classicCompatibility) {
/*  561 */     this.classicCompatible = Integer.valueOf(classicCompatibility ? 1 : 0);
/*  562 */     this.properties.setProperty("classic_compatible", classicCompatibilityIntToString(this.classicCompatible));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClassicCompatibleAsInt(int classicCompatibility) {
/*  576 */     if (classicCompatibility < 0 || classicCompatibility > 2) {
/*  577 */       throw new IllegalArgumentException("Unsupported \"classicCompatibility\": " + classicCompatibility);
/*      */     }
/*  579 */     this.classicCompatible = Integer.valueOf(classicCompatibility);
/*      */   }
/*      */   
/*      */   private String classicCompatibilityIntToString(Integer i) {
/*  583 */     if (i == null) return null; 
/*  584 */     if (i.intValue() == 0) return "false"; 
/*  585 */     if (i.intValue() == 1) return "true"; 
/*  586 */     return i.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClassicCompatible() {
/*  640 */     return (this.classicCompatible != null) ? ((this.classicCompatible.intValue() != 0)) : this.parent.isClassicCompatible();
/*      */   }
/*      */   
/*      */   public int getClassicCompatibleAsInt() {
/*  644 */     return (this.classicCompatible != null) ? this.classicCompatible.intValue() : this.parent.getClassicCompatibleAsInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isClassicCompatibleSet() {
/*  653 */     return (this.classicCompatible != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLocale(Locale locale) {
/*  665 */     NullArgumentException.check("locale", locale);
/*  666 */     this.locale = locale;
/*  667 */     this.properties.setProperty("locale", locale.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Locale getLocale() {
/*  674 */     return (this.locale != null) ? this.locale : this.parent.getLocale();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLocaleSet() {
/*  683 */     return (this.locale != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTimeZone(TimeZone timeZone) {
/*  698 */     NullArgumentException.check("timeZone", timeZone);
/*  699 */     this.timeZone = timeZone;
/*  700 */     this.properties.setProperty("time_zone", timeZone.getID());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimeZone getTimeZone() {
/*  707 */     return (this.timeZone != null) ? this.timeZone : this.parent.getTimeZone();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTimeZoneSet() {
/*  716 */     return (this.timeZone != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSQLDateAndTimeTimeZone(TimeZone tz) {
/*  782 */     this.sqlDataAndTimeTimeZone = tz;
/*  783 */     this.sqlDataAndTimeTimeZoneSet = true;
/*  784 */     this.properties.setProperty("sql_date_and_time_time_zone", (tz != null) ? tz.getID() : "null");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimeZone getSQLDateAndTimeTimeZone() {
/*  797 */     return this.sqlDataAndTimeTimeZoneSet ? this.sqlDataAndTimeTimeZone : ((this.parent != null) ? this.parent
/*      */       
/*  799 */       .getSQLDateAndTimeTimeZone() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSQLDateAndTimeTimeZoneSet() {
/*  808 */     return this.sqlDataAndTimeTimeZoneSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNumberFormat(String numberFormat) {
/*  837 */     NullArgumentException.check("numberFormat", numberFormat);
/*  838 */     this.numberFormat = numberFormat;
/*  839 */     this.properties.setProperty("number_format", numberFormat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNumberFormat() {
/*  846 */     return (this.numberFormat != null) ? this.numberFormat : this.parent.getNumberFormat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isNumberFormatSet() {
/*  855 */     return (this.numberFormat != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, ? extends TemplateNumberFormatFactory> getCustomNumberFormats() {
/*  876 */     return (this.customNumberFormats == null) ? this.parent.getCustomNumberFormats() : this.customNumberFormats;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, ? extends TemplateNumberFormatFactory> getCustomNumberFormatsWithoutFallback() {
/*  885 */     return this.customNumberFormats;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCustomNumberFormats(Map<String, ? extends TemplateNumberFormatFactory> customNumberFormats) {
/*  903 */     NullArgumentException.check("customNumberFormats", customNumberFormats);
/*  904 */     validateFormatNames(customNumberFormats.keySet());
/*  905 */     this.customNumberFormats = customNumberFormats;
/*      */   }
/*      */   
/*      */   private void validateFormatNames(Set<String> keySet) {
/*  909 */     for (String name : keySet) {
/*  910 */       if (name.length() == 0) {
/*  911 */         throw new IllegalArgumentException("Format names can't be 0 length");
/*      */       }
/*  913 */       char firstChar = name.charAt(0);
/*  914 */       if (firstChar == '@') {
/*  915 */         throw new IllegalArgumentException("Format names can't start with '@'. '@' is only used when referring to them from format strings. In: " + name);
/*      */       }
/*      */ 
/*      */       
/*  919 */       if (!Character.isLetter(firstChar)) {
/*  920 */         throw new IllegalArgumentException("Format name must start with letter: " + name);
/*      */       }
/*  922 */       for (int i = 1; i < name.length(); i++) {
/*      */         
/*  924 */         if (!Character.isLetterOrDigit(name.charAt(i))) {
/*  925 */           throw new IllegalArgumentException("Format name can only contain letters and digits: " + name);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCustomNumberFormatsSet() {
/*  937 */     return (this.customNumberFormats != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateNumberFormatFactory getCustomNumberFormat(String name) {
/*  947 */     if (this.customNumberFormats != null) {
/*  948 */       TemplateNumberFormatFactory r = this.customNumberFormats.get(name);
/*  949 */       if (r != null) {
/*  950 */         return r;
/*      */       }
/*      */     } 
/*  953 */     return (this.parent != null) ? this.parent.getCustomNumberFormat(name) : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasCustomFormats() {
/*  962 */     return ((this.customNumberFormats != null && !this.customNumberFormats.isEmpty()) || (this.customDateFormats != null && 
/*  963 */       !this.customDateFormats.isEmpty()) || (
/*  964 */       getParent() != null && getParent().hasCustomFormats()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBooleanFormat(String booleanFormat) {
/*  984 */     NullArgumentException.check("booleanFormat", booleanFormat);
/*      */     
/*  986 */     if (booleanFormat.equals("true,false")) {
/*      */ 
/*      */       
/*  989 */       this.trueStringValue = null;
/*  990 */       this.falseStringValue = null;
/*  991 */     } else if (booleanFormat.equals("c")) {
/*  992 */       this.trueStringValue = "true";
/*  993 */       this.falseStringValue = "false";
/*      */     } else {
/*  995 */       int commaIdx = booleanFormat.indexOf(',');
/*  996 */       if (commaIdx == -1) {
/*  997 */         throw new IllegalArgumentException("Setting value must be a string that contains two comma-separated values for true and false, or it must be \"c\", but it was " + 
/*      */ 
/*      */             
/* 1000 */             StringUtil.jQuote(booleanFormat) + ".");
/*      */       }
/* 1002 */       this.trueStringValue = booleanFormat.substring(0, commaIdx);
/* 1003 */       this.falseStringValue = booleanFormat.substring(commaIdx + 1);
/*      */     } 
/*      */     
/* 1006 */     this.booleanFormat = booleanFormat;
/* 1007 */     this.properties.setProperty("boolean_format", booleanFormat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getBooleanFormat() {
/* 1014 */     return (this.booleanFormat != null) ? this.booleanFormat : this.parent.getBooleanFormat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBooleanFormatSet() {
/* 1023 */     return (this.booleanFormat != null);
/*      */   }
/*      */   
/*      */   String formatBoolean(boolean value, boolean fallbackToTrueFalse) throws TemplateException {
/* 1027 */     if (value) {
/* 1028 */       String str = getTrueStringValue();
/* 1029 */       if (str == null) {
/* 1030 */         if (fallbackToTrueFalse) {
/* 1031 */           return "true";
/*      */         }
/* 1033 */         throw new _MiscTemplateException(getNullBooleanFormatErrorDescription());
/*      */       } 
/*      */       
/* 1036 */       return str;
/*      */     } 
/*      */     
/* 1039 */     String s = getFalseStringValue();
/* 1040 */     if (s == null) {
/* 1041 */       if (fallbackToTrueFalse) {
/* 1042 */         return "false";
/*      */       }
/* 1044 */       throw new _MiscTemplateException(getNullBooleanFormatErrorDescription());
/*      */     } 
/*      */     
/* 1047 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private _ErrorDescriptionBuilder getNullBooleanFormatErrorDescription() {
/* 1053 */     return (new _ErrorDescriptionBuilder(new Object[] { "Can't convert boolean to string automatically, because the \"", "boolean_format", "\" setting was ", new _DelayedJQuote(
/*      */             
/* 1055 */             getBooleanFormat()), 
/* 1056 */           getBooleanFormat().equals("true,false") ? ", which is the legacy deprecated default, and we treat it as if no format was set. This is the default configuration; you should provide the format explicitly for each place where you print a boolean." : "."
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1061 */         })).tips(new Object[] { "Write something like myBool?string('yes', 'no') to specify boolean formatting in place.", { "If you want \"true\"/\"false\" result as you are generating computer-language output (not for direct human consumption), then use \"?c\", like ${myBool?c}. (If you always generate computer-language output, then it's might be reasonable to set the \"", "boolean_format", "\" setting to \"c\" instead.)" }, { "If you need the same two values on most places, the programmers can set the \"", "boolean_format", "\" setting to something like \"yes,no\". However, then it will be easy to unwillingly format booleans like that." } });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String getTrueStringValue() {
/* 1091 */     return (this.booleanFormat != null) ? this.trueStringValue : ((this.parent != null) ? this.parent.getTrueStringValue() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String getFalseStringValue() {
/* 1100 */     return (this.booleanFormat != null) ? this.falseStringValue : ((this.parent != null) ? this.parent.getFalseStringValue() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTimeFormat(String timeFormat) {
/* 1112 */     NullArgumentException.check("timeFormat", timeFormat);
/* 1113 */     this.timeFormat = timeFormat;
/* 1114 */     this.properties.setProperty("time_format", timeFormat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTimeFormat() {
/* 1121 */     return (this.timeFormat != null) ? this.timeFormat : this.parent.getTimeFormat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTimeFormatSet() {
/* 1130 */     return (this.timeFormat != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDateFormat(String dateFormat) {
/* 1142 */     NullArgumentException.check("dateFormat", dateFormat);
/* 1143 */     this.dateFormat = dateFormat;
/* 1144 */     this.properties.setProperty("date_format", dateFormat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDateFormat() {
/* 1151 */     return (this.dateFormat != null) ? this.dateFormat : this.parent.getDateFormat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDateFormatSet() {
/* 1160 */     return (this.dateFormat != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDateTimeFormat(String dateTimeFormat) {
/* 1250 */     NullArgumentException.check("dateTimeFormat", dateTimeFormat);
/* 1251 */     this.dateTimeFormat = dateTimeFormat;
/* 1252 */     this.properties.setProperty("datetime_format", dateTimeFormat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDateTimeFormat() {
/* 1259 */     return (this.dateTimeFormat != null) ? this.dateTimeFormat : this.parent.getDateTimeFormat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDateTimeFormatSet() {
/* 1268 */     return (this.dateTimeFormat != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, ? extends TemplateDateFormatFactory> getCustomDateFormats() {
/* 1289 */     return (this.customDateFormats == null) ? this.parent.getCustomDateFormats() : this.customDateFormats;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, ? extends TemplateDateFormatFactory> getCustomDateFormatsWithoutFallback() {
/* 1299 */     return this.customDateFormats;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCustomDateFormats(Map<String, ? extends TemplateDateFormatFactory> customDateFormats) {
/* 1318 */     NullArgumentException.check("customDateFormats", customDateFormats);
/* 1319 */     validateFormatNames(customDateFormats.keySet());
/* 1320 */     this.customDateFormats = customDateFormats;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCustomDateFormatsSet() {
/* 1329 */     return (this.customDateFormats != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateDateFormatFactory getCustomDateFormat(String name) {
/* 1339 */     if (this.customDateFormats != null) {
/* 1340 */       TemplateDateFormatFactory r = this.customDateFormats.get(name);
/* 1341 */       if (r != null) {
/* 1342 */         return r;
/*      */       }
/*      */     } 
/* 1345 */     return (this.parent != null) ? this.parent.getCustomDateFormat(name) : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
/* 1374 */     NullArgumentException.check("templateExceptionHandler", templateExceptionHandler);
/* 1375 */     this.templateExceptionHandler = templateExceptionHandler;
/* 1376 */     this.properties.setProperty("template_exception_handler", templateExceptionHandler.getClass().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateExceptionHandler getTemplateExceptionHandler() {
/* 1383 */     return (this.templateExceptionHandler != null) ? this.templateExceptionHandler : this.parent
/* 1384 */       .getTemplateExceptionHandler();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTemplateExceptionHandlerSet() {
/* 1393 */     return (this.templateExceptionHandler != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
/* 1413 */     NullArgumentException.check("attemptExceptionReporter", attemptExceptionReporter);
/* 1414 */     this.attemptExceptionReporter = attemptExceptionReporter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AttemptExceptionReporter getAttemptExceptionReporter() {
/* 1423 */     return (this.attemptExceptionReporter != null) ? this.attemptExceptionReporter : this.parent
/* 1424 */       .getAttemptExceptionReporter();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAttemptExceptionReporterSet() {
/* 1433 */     return (this.attemptExceptionReporter != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setArithmeticEngine(ArithmeticEngine arithmeticEngine) {
/* 1441 */     NullArgumentException.check("arithmeticEngine", arithmeticEngine);
/* 1442 */     this.arithmeticEngine = arithmeticEngine;
/* 1443 */     this.properties.setProperty("arithmetic_engine", arithmeticEngine.getClass().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArithmeticEngine getArithmeticEngine() {
/* 1450 */     return (this.arithmeticEngine != null) ? this.arithmeticEngine : this.parent
/* 1451 */       .getArithmeticEngine();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isArithmeticEngineSet() {
/* 1460 */     return (this.arithmeticEngine != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setObjectWrapper(ObjectWrapper objectWrapper) {
/* 1468 */     NullArgumentException.check("objectWrapper", objectWrapper);
/* 1469 */     this.objectWrapper = objectWrapper;
/* 1470 */     this.properties.setProperty("object_wrapper", objectWrapper.getClass().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ObjectWrapper getObjectWrapper() {
/* 1477 */     return (this.objectWrapper != null) ? this.objectWrapper : this.parent
/* 1478 */       .getObjectWrapper();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isObjectWrapperSet() {
/* 1487 */     return (this.objectWrapper != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOutputEncoding(String outputEncoding) {
/* 1499 */     this.outputEncoding = outputEncoding;
/*      */     
/* 1501 */     if (outputEncoding != null) {
/* 1502 */       this.properties.setProperty("output_encoding", outputEncoding);
/*      */     } else {
/* 1504 */       this.properties.remove("output_encoding");
/*      */     } 
/* 1506 */     this.outputEncodingSet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getOutputEncoding() {
/* 1513 */     return this.outputEncodingSet ? this.outputEncoding : ((this.parent != null) ? this.parent
/*      */       
/* 1515 */       .getOutputEncoding() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOutputEncodingSet() {
/* 1524 */     return this.outputEncodingSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setURLEscapingCharset(String urlEscapingCharset) {
/* 1534 */     this.urlEscapingCharset = urlEscapingCharset;
/*      */     
/* 1536 */     if (urlEscapingCharset != null) {
/* 1537 */       this.properties.setProperty("url_escaping_charset", urlEscapingCharset);
/*      */     } else {
/* 1539 */       this.properties.remove("url_escaping_charset");
/*      */     } 
/* 1541 */     this.urlEscapingCharsetSet = true;
/*      */   }
/*      */   
/*      */   public String getURLEscapingCharset() {
/* 1545 */     return this.urlEscapingCharsetSet ? this.urlEscapingCharset : ((this.parent != null) ? this.parent
/*      */       
/* 1547 */       .getURLEscapingCharset() : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isURLEscapingCharsetSet() {
/* 1556 */     return this.urlEscapingCharsetSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNewBuiltinClassResolver(TemplateClassResolver newBuiltinClassResolver) {
/* 1577 */     NullArgumentException.check("newBuiltinClassResolver", newBuiltinClassResolver);
/* 1578 */     this.newBuiltinClassResolver = newBuiltinClassResolver;
/* 1579 */     this.properties.setProperty("new_builtin_class_resolver", newBuiltinClassResolver
/* 1580 */         .getClass().getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateClassResolver getNewBuiltinClassResolver() {
/* 1590 */     return (this.newBuiltinClassResolver != null) ? this.newBuiltinClassResolver : this.parent
/* 1591 */       .getNewBuiltinClassResolver();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isNewBuiltinClassResolverSet() {
/* 1600 */     return (this.newBuiltinClassResolver != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoFlush(boolean autoFlush) {
/* 1620 */     this.autoFlush = Boolean.valueOf(autoFlush);
/* 1621 */     this.properties.setProperty("auto_flush", String.valueOf(autoFlush));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoFlush() {
/* 1630 */     return (this.autoFlush != null) ? this.autoFlush
/* 1631 */       .booleanValue() : ((this.parent != null) ? this.parent
/* 1632 */       .getAutoFlush() : true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutoFlushSet() {
/* 1641 */     return (this.autoFlush != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setShowErrorTips(boolean showTips) {
/* 1651 */     this.showErrorTips = Boolean.valueOf(showTips);
/* 1652 */     this.properties.setProperty("show_error_tips", String.valueOf(showTips));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getShowErrorTips() {
/* 1661 */     return (this.showErrorTips != null) ? this.showErrorTips
/* 1662 */       .booleanValue() : ((this.parent != null) ? this.parent
/* 1663 */       .getShowErrorTips() : true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isShowErrorTipsSet() {
/* 1672 */     return (this.showErrorTips != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAPIBuiltinEnabled(boolean value) {
/* 1682 */     this.apiBuiltinEnabled = Boolean.valueOf(value);
/* 1683 */     this.properties.setProperty("api_builtin_enabled", String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAPIBuiltinEnabled() {
/* 1692 */     return (this.apiBuiltinEnabled != null) ? this.apiBuiltinEnabled
/* 1693 */       .booleanValue() : ((this.parent != null) ? this.parent
/* 1694 */       .isAPIBuiltinEnabled() : false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAPIBuiltinEnabledSet() {
/* 1703 */     return (this.apiBuiltinEnabled != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTruncateBuiltinAlgorithm(TruncateBuiltinAlgorithm truncateBuiltinAlgorithm) {
/* 1718 */     NullArgumentException.check("truncateBuiltinAlgorithm", truncateBuiltinAlgorithm);
/* 1719 */     this.truncateBuiltinAlgorithm = truncateBuiltinAlgorithm;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TruncateBuiltinAlgorithm getTruncateBuiltinAlgorithm() {
/* 1728 */     return (this.truncateBuiltinAlgorithm != null) ? this.truncateBuiltinAlgorithm : this.parent.getTruncateBuiltinAlgorithm();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTruncateBuiltinAlgorithmSet() {
/* 1737 */     return (this.truncateBuiltinAlgorithm != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLogTemplateExceptions(boolean value) {
/* 1753 */     this.logTemplateExceptions = Boolean.valueOf(value);
/* 1754 */     this.properties.setProperty("log_template_exceptions", String.valueOf(value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getLogTemplateExceptions() {
/* 1763 */     return (this.logTemplateExceptions != null) ? this.logTemplateExceptions
/* 1764 */       .booleanValue() : ((this.parent != null) ? this.parent
/* 1765 */       .getLogTemplateExceptions() : true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLogTemplateExceptionsSet() {
/* 1774 */     return (this.logTemplateExceptions != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWrapUncheckedExceptions(boolean wrapUncheckedExceptions) {
/* 1795 */     this.wrapUncheckedExceptions = Boolean.valueOf(wrapUncheckedExceptions);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getWrapUncheckedExceptions() {
/* 1804 */     return (this.wrapUncheckedExceptions != null) ? this.wrapUncheckedExceptions.booleanValue() : ((this.parent != null) ? this.parent
/* 1805 */       .getWrapUncheckedExceptions() : false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWrapUncheckedExceptionsSet() {
/* 1812 */     return (this.wrapUncheckedExceptions != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getLazyImports() {
/* 1821 */     return (this.lazyImports != null) ? this.lazyImports.booleanValue() : this.parent.getLazyImports();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLazyImports(boolean lazyImports) {
/* 1851 */     this.lazyImports = Boolean.valueOf(lazyImports);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLazyImportsSet() {
/* 1860 */     return (this.lazyImports != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean getLazyAutoImports() {
/* 1869 */     return this.lazyAutoImportsSet ? this.lazyAutoImports : this.parent.getLazyAutoImports();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLazyAutoImports(Boolean lazyAutoImports) {
/* 1881 */     this.lazyAutoImports = lazyAutoImports;
/* 1882 */     this.lazyAutoImportsSet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLazyAutoImportsSet() {
/* 1891 */     return this.lazyAutoImportsSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addAutoImport(String namespaceVarName, String templateName) {
/* 1925 */     synchronized (this) {
/* 1926 */       if (this.autoImports == null) {
/* 1927 */         initAutoImportsMap();
/*      */       } else {
/*      */         
/* 1930 */         this.autoImports.remove(namespaceVarName);
/*      */       } 
/* 1932 */       this.autoImports.put(namespaceVarName, templateName);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initAutoImportsMap() {
/* 1937 */     this.autoImports = new LinkedHashMap<>(4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeAutoImport(String namespaceVarName) {
/* 1946 */     synchronized (this) {
/* 1947 */       if (this.autoImports != null) {
/* 1948 */         this.autoImports.remove(namespaceVarName);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoImports(Map map) {
/* 1962 */     NullArgumentException.check("map", map);
/*      */ 
/*      */     
/* 1965 */     synchronized (this) {
/* 1966 */       if (this.autoImports != null) {
/* 1967 */         this.autoImports.clear();
/*      */       }
/* 1969 */       for (Map.Entry<?, ?> entry : (Iterable<Map.Entry<?, ?>>)map.entrySet()) {
/* 1970 */         Object key = entry.getKey();
/* 1971 */         if (!(key instanceof String)) {
/* 1972 */           throw new IllegalArgumentException("Key in Map wasn't a String, but a(n) " + key
/* 1973 */               .getClass().getName() + ".");
/*      */         }
/*      */         
/* 1976 */         Object value = entry.getValue();
/* 1977 */         if (!(value instanceof String)) {
/* 1978 */           throw new IllegalArgumentException("Value in Map wasn't a String, but a(n) " + value
/* 1979 */               .getClass().getName() + ".");
/*      */         }
/*      */         
/* 1982 */         addAutoImport((String)key, (String)value);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, String> getAutoImports() {
/* 2006 */     return (this.autoImports != null) ? this.autoImports : this.parent.getAutoImports();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutoImportsSet() {
/* 2015 */     return (this.autoImports != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, String> getAutoImportsWithoutFallback() {
/* 2025 */     return this.autoImports;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addAutoInclude(String templateName) {
/* 2054 */     addAutoInclude(templateName, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void addAutoInclude(String templateName, boolean keepDuplicate) {
/* 2064 */     synchronized (this) {
/* 2065 */       if (this.autoIncludes == null) {
/* 2066 */         initAutoIncludesList();
/*      */       }
/* 2068 */       else if (!keepDuplicate) {
/* 2069 */         this.autoIncludes.remove(templateName);
/*      */       } 
/*      */       
/* 2072 */       this.autoIncludes.add(templateName);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void initAutoIncludesList() {
/* 2077 */     this.autoIncludes = new ArrayList<>(4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAutoIncludes(List templateNames) {
/* 2087 */     NullArgumentException.check("templateNames", templateNames);
/*      */     
/* 2089 */     synchronized (this) {
/* 2090 */       if (this.autoIncludes != null) {
/* 2091 */         this.autoIncludes.clear();
/*      */       }
/* 2093 */       for (Object templateName : templateNames) {
/* 2094 */         if (!(templateName instanceof String)) {
/* 2095 */           throw new IllegalArgumentException("List items must be String-s.");
/*      */         }
/* 2097 */         addAutoInclude((String)templateName, (this instanceof Configuration && ((Configuration)this)
/* 2098 */             .getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_25));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getAutoIncludes() {
/* 2122 */     return (this.autoIncludes != null) ? this.autoIncludes : this.parent.getAutoIncludes();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAutoIncludesSet() {
/* 2131 */     return (this.autoIncludes != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<String> getAutoIncludesWithoutFallback() {
/* 2141 */     return this.autoIncludes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeAutoInclude(String templateName) {
/* 2150 */     synchronized (this) {
/* 2151 */       if (this.autoIncludes != null) {
/* 2152 */         this.autoIncludes.remove(templateName);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSetting(String name, String value) throws TemplateException {
/* 2624 */     boolean unknown = false;
/*      */     try {
/* 2626 */       if ("locale".equals(name)) {
/* 2627 */         if ("JVM default".equalsIgnoreCase(value)) {
/* 2628 */           setLocale(Locale.getDefault());
/*      */         } else {
/* 2630 */           setLocale(StringUtil.deduceLocale(value));
/*      */         } 
/* 2632 */       } else if ("number_format".equals(name) || "numberFormat".equals(name)) {
/* 2633 */         setNumberFormat(value);
/* 2634 */       } else if ("custom_number_formats".equals(name) || "customNumberFormats"
/* 2635 */         .equals(name)) {
/* 2636 */         Map<String, ? extends TemplateNumberFormatFactory> map = (Map)_ObjectBuilderSettingEvaluator.eval(value, Map.class, false, 
/* 2637 */             _SettingEvaluationEnvironment.getCurrent());
/* 2638 */         _CoreAPI.checkSettingValueItemsType("Map keys", String.class, map.keySet());
/* 2639 */         _CoreAPI.checkSettingValueItemsType("Map values", TemplateNumberFormatFactory.class, map.values());
/* 2640 */         setCustomNumberFormats(map);
/* 2641 */       } else if ("time_format".equals(name) || "timeFormat".equals(name)) {
/* 2642 */         setTimeFormat(value);
/* 2643 */       } else if ("date_format".equals(name) || "dateFormat".equals(name)) {
/* 2644 */         setDateFormat(value);
/* 2645 */       } else if ("datetime_format".equals(name) || "datetimeFormat".equals(name)) {
/* 2646 */         setDateTimeFormat(value);
/* 2647 */       } else if ("custom_date_formats".equals(name) || "customDateFormats"
/* 2648 */         .equals(name)) {
/* 2649 */         Map<String, ? extends TemplateDateFormatFactory> map = (Map)_ObjectBuilderSettingEvaluator.eval(value, Map.class, false, 
/* 2650 */             _SettingEvaluationEnvironment.getCurrent());
/* 2651 */         _CoreAPI.checkSettingValueItemsType("Map keys", String.class, map.keySet());
/* 2652 */         _CoreAPI.checkSettingValueItemsType("Map values", TemplateDateFormatFactory.class, map.values());
/* 2653 */         setCustomDateFormats(map);
/* 2654 */       } else if ("time_zone".equals(name) || "timeZone".equals(name)) {
/* 2655 */         setTimeZone(parseTimeZoneSettingValue(value));
/* 2656 */       } else if ("sql_date_and_time_time_zone".equals(name) || "sqlDateAndTimeTimeZone"
/* 2657 */         .equals(name)) {
/* 2658 */         setSQLDateAndTimeTimeZone(value.equals("null") ? null : parseTimeZoneSettingValue(value));
/* 2659 */       } else if ("classic_compatible".equals(name) || "classicCompatible"
/* 2660 */         .equals(name)) {
/*      */         char firstChar;
/* 2662 */         if (value != null && value.length() > 0) {
/* 2663 */           firstChar = value.charAt(0);
/*      */         } else {
/* 2665 */           firstChar = Character.MIN_VALUE;
/*      */         } 
/* 2667 */         if (Character.isDigit(firstChar) || firstChar == '+' || firstChar == '-') {
/* 2668 */           setClassicCompatibleAsInt(Integer.parseInt(value));
/*      */         } else {
/* 2670 */           setClassicCompatible((value != null) ? StringUtil.getYesNo(value) : false);
/*      */         } 
/* 2672 */       } else if ("template_exception_handler".equals(name) || "templateExceptionHandler"
/* 2673 */         .equals(name)) {
/* 2674 */         if (value.indexOf('.') == -1) {
/* 2675 */           if ("debug".equalsIgnoreCase(value)) {
/* 2676 */             setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
/*      */           }
/* 2678 */           else if ("html_debug".equalsIgnoreCase(value) || "htmlDebug".equals(value)) {
/* 2679 */             setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
/*      */           }
/* 2681 */           else if ("ignore".equalsIgnoreCase(value)) {
/* 2682 */             setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
/*      */           }
/* 2684 */           else if ("rethrow".equalsIgnoreCase(value)) {
/* 2685 */             setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
/*      */           }
/* 2687 */           else if ("default".equalsIgnoreCase(value) && this instanceof Configuration) {
/* 2688 */             ((Configuration)this).unsetTemplateExceptionHandler();
/*      */           } else {
/* 2690 */             throw invalidSettingValueException(name, value);
/*      */           } 
/*      */         } else {
/* 2693 */           setTemplateExceptionHandler((TemplateExceptionHandler)_ObjectBuilderSettingEvaluator.eval(value, TemplateExceptionHandler.class, false, 
/* 2694 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 2696 */       } else if ("attempt_exception_reporter".equals(name) || "attemptExceptionReporter"
/* 2697 */         .equals(name)) {
/* 2698 */         if (value.indexOf('.') == -1) {
/* 2699 */           if ("log_error".equalsIgnoreCase(value) || "logError".equals(value)) {
/* 2700 */             setAttemptExceptionReporter(AttemptExceptionReporter.LOG_ERROR_REPORTER);
/*      */           }
/* 2702 */           else if ("log_warn".equalsIgnoreCase(value) || "logWarn".equals(value)) {
/* 2703 */             setAttemptExceptionReporter(AttemptExceptionReporter.LOG_WARN_REPORTER);
/*      */           }
/* 2705 */           else if ("default".equalsIgnoreCase(value) && this instanceof Configuration) {
/* 2706 */             ((Configuration)this).unsetAttemptExceptionReporter();
/*      */           } else {
/* 2708 */             throw invalidSettingValueException(name, value);
/*      */           } 
/*      */         } else {
/* 2711 */           setAttemptExceptionReporter((AttemptExceptionReporter)_ObjectBuilderSettingEvaluator.eval(value, AttemptExceptionReporter.class, false, 
/* 2712 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 2714 */       } else if ("arithmetic_engine".equals(name) || "arithmeticEngine".equals(name)) {
/* 2715 */         if (value.indexOf('.') == -1) {
/* 2716 */           if ("bigdecimal".equalsIgnoreCase(value)) {
/* 2717 */             setArithmeticEngine(ArithmeticEngine.BIGDECIMAL_ENGINE);
/* 2718 */           } else if ("conservative".equalsIgnoreCase(value)) {
/* 2719 */             setArithmeticEngine(ArithmeticEngine.CONSERVATIVE_ENGINE);
/*      */           } else {
/* 2721 */             throw invalidSettingValueException(name, value);
/*      */           } 
/*      */         } else {
/* 2724 */           setArithmeticEngine((ArithmeticEngine)_ObjectBuilderSettingEvaluator.eval(value, ArithmeticEngine.class, false, 
/* 2725 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 2727 */       } else if ("object_wrapper".equals(name) || "objectWrapper".equals(name)) {
/* 2728 */         if ("default".equalsIgnoreCase(value)) {
/* 2729 */           if (this instanceof Configuration) {
/* 2730 */             ((Configuration)this).unsetObjectWrapper();
/*      */           } else {
/* 2732 */             setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_0));
/*      */           } 
/* 2734 */         } else if ("default_2_3_0".equalsIgnoreCase(value)) {
/* 2735 */           setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_0));
/* 2736 */         } else if ("simple".equalsIgnoreCase(value)) {
/* 2737 */           setObjectWrapper(ObjectWrapper.SIMPLE_WRAPPER);
/* 2738 */         } else if ("beans".equalsIgnoreCase(value)) {
/* 2739 */           setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
/* 2740 */         } else if ("jython".equalsIgnoreCase(value)) {
/* 2741 */           Class<?> clazz = Class.forName("freemarker.ext.jython.JythonWrapper");
/*      */           
/* 2743 */           setObjectWrapper((ObjectWrapper)clazz
/* 2744 */               .getField("INSTANCE").get(null));
/*      */         } else {
/* 2746 */           setObjectWrapper((ObjectWrapper)_ObjectBuilderSettingEvaluator.eval(value, ObjectWrapper.class, false, 
/* 2747 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 2749 */       } else if ("boolean_format".equals(name) || "booleanFormat".equals(name)) {
/* 2750 */         setBooleanFormat(value);
/* 2751 */       } else if ("output_encoding".equals(name) || "outputEncoding".equals(name)) {
/* 2752 */         setOutputEncoding(value);
/* 2753 */       } else if ("url_escaping_charset".equals(name) || "urlEscapingCharset"
/* 2754 */         .equals(name)) {
/* 2755 */         setURLEscapingCharset(value);
/* 2756 */       } else if ("strict_bean_models".equals(name) || "strictBeanModels"
/* 2757 */         .equals(name)) {
/* 2758 */         setStrictBeanModels(StringUtil.getYesNo(value));
/* 2759 */       } else if ("auto_flush".equals(name) || "autoFlush".equals(name)) {
/* 2760 */         setAutoFlush(StringUtil.getYesNo(value));
/* 2761 */       } else if ("show_error_tips".equals(name) || "showErrorTips".equals(name)) {
/* 2762 */         setShowErrorTips(StringUtil.getYesNo(value));
/* 2763 */       } else if ("api_builtin_enabled".equals(name) || "apiBuiltinEnabled"
/* 2764 */         .equals(name)) {
/* 2765 */         setAPIBuiltinEnabled(StringUtil.getYesNo(value));
/* 2766 */       } else if ("truncate_builtin_algorithm".equals(name) || "truncateBuiltinAlgorithm"
/* 2767 */         .equals(name)) {
/* 2768 */         if ("ascii".equalsIgnoreCase(value)) {
/* 2769 */           setTruncateBuiltinAlgorithm(DefaultTruncateBuiltinAlgorithm.ASCII_INSTANCE);
/* 2770 */         } else if ("unicode".equalsIgnoreCase(value)) {
/* 2771 */           setTruncateBuiltinAlgorithm(DefaultTruncateBuiltinAlgorithm.UNICODE_INSTANCE);
/*      */         } else {
/* 2773 */           setTruncateBuiltinAlgorithm((TruncateBuiltinAlgorithm)_ObjectBuilderSettingEvaluator.eval(value, TruncateBuiltinAlgorithm.class, false, 
/*      */                 
/* 2775 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 2777 */       } else if ("new_builtin_class_resolver".equals(name) || "newBuiltinClassResolver"
/* 2778 */         .equals(name)) {
/* 2779 */         if ("unrestricted".equals(value)) {
/* 2780 */           setNewBuiltinClassResolver(TemplateClassResolver.UNRESTRICTED_RESOLVER);
/* 2781 */         } else if ("safer".equals(value)) {
/* 2782 */           setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
/* 2783 */         } else if ("allows_nothing".equals(value) || "allowsNothing".equals(value)) {
/* 2784 */           setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
/* 2785 */         } else if (value.indexOf(":") != -1) {
/* 2786 */           List<KeyValuePair> segments = parseAsSegmentedList(value);
/* 2787 */           Set allowedClasses = null;
/* 2788 */           List<?> trustedTemplates = null;
/* 2789 */           for (int i = 0; i < segments.size(); i++) {
/* 2790 */             KeyValuePair kv = segments.get(i);
/* 2791 */             String segmentKey = (String)kv.getKey();
/* 2792 */             List<?> segmentValue = (List)kv.getValue();
/* 2793 */             if (segmentKey.equals("allowed_classes") || segmentKey
/* 2794 */               .equals("allowedClasses")) {
/* 2795 */               allowedClasses = new HashSet(segmentValue);
/* 2796 */             } else if (segmentKey.equals("trusted_templates") || segmentKey
/* 2797 */               .equals("trustedTemplates")) {
/* 2798 */               trustedTemplates = segmentValue;
/*      */             } else {
/* 2800 */               throw new ParseException("Unrecognized list segment key: " + 
/* 2801 */                   StringUtil.jQuote(segmentKey) + ". Supported keys are: \"" + "allowed_classes" + "\", \"" + "allowedClasses" + "\", \"" + "trusted_templates" + "\", \"" + "trustedTemplates" + "\". ", 0, 0);
/*      */             } 
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2810 */           setNewBuiltinClassResolver(new OptInTemplateClassResolver(allowedClasses, trustedTemplates));
/*      */         } else {
/* 2812 */           if ("allow_nothing".equals(value)) {
/* 2813 */             throw new IllegalArgumentException("The correct value would be: allows_nothing");
/*      */           }
/* 2815 */           if ("allowNothing".equals(value)) {
/* 2816 */             throw new IllegalArgumentException("The correct value would be: allowsNothing");
/*      */           }
/* 2818 */           if (value.indexOf('.') != -1)
/* 2819 */           { setNewBuiltinClassResolver((TemplateClassResolver)_ObjectBuilderSettingEvaluator.eval(value, TemplateClassResolver.class, false, 
/*      */                   
/* 2821 */                   _SettingEvaluationEnvironment.getCurrent())); }
/*      */           else
/* 2823 */           { throw invalidSettingValueException(name, value); } 
/*      */         } 
/* 2825 */       } else if ("log_template_exceptions".equals(name) || "logTemplateExceptions"
/* 2826 */         .equals(name)) {
/* 2827 */         setLogTemplateExceptions(StringUtil.getYesNo(value));
/* 2828 */       } else if ("wrap_unchecked_exceptions".equals(name) || "wrapUncheckedExceptions"
/* 2829 */         .equals(name)) {
/* 2830 */         setWrapUncheckedExceptions(StringUtil.getYesNo(value));
/* 2831 */       } else if ("lazy_auto_imports".equals(name) || "lazyAutoImports".equals(name)) {
/* 2832 */         setLazyAutoImports(value.equals("null") ? null : Boolean.valueOf(StringUtil.getYesNo(value)));
/* 2833 */       } else if ("lazy_imports".equals(name) || "lazyImports".equals(name)) {
/* 2834 */         setLazyImports(StringUtil.getYesNo(value));
/* 2835 */       } else if ("auto_include".equals(name) || "autoInclude"
/* 2836 */         .equals(name)) {
/* 2837 */         setAutoIncludes(parseAsList(value));
/* 2838 */       } else if ("auto_import".equals(name) || "autoImport".equals(name)) {
/* 2839 */         setAutoImports(parseAsImportList(value));
/*      */       } else {
/* 2841 */         unknown = true;
/*      */       } 
/* 2843 */     } catch (Exception e) {
/* 2844 */       throw settingValueAssignmentException(name, value, e);
/*      */     } 
/* 2846 */     if (unknown) {
/* 2847 */       throw unknownSettingException(name);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set<String> getSettingNames(boolean camelCase) {
/* 2863 */     return new _SortedArraySet<>(camelCase ? SETTING_NAMES_CAMEL_CASE : SETTING_NAMES_SNAKE_CASE);
/*      */   }
/*      */   
/*      */   private TimeZone parseTimeZoneSettingValue(String value) {
/*      */     TimeZone tz;
/* 2868 */     if ("JVM default".equalsIgnoreCase(value)) {
/* 2869 */       tz = TimeZone.getDefault();
/*      */     } else {
/* 2871 */       tz = TimeZone.getTimeZone(value);
/*      */     } 
/* 2873 */     return tz;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setStrictBeanModels(boolean strict) {
/* 2881 */     if (!(this.objectWrapper instanceof BeansWrapper)) {
/* 2882 */       throw new IllegalStateException("The value of the object_wrapper setting isn't a " + BeansWrapper.class
/* 2883 */           .getName() + ".");
/*      */     }
/* 2885 */     ((BeansWrapper)this.objectWrapper).setStrict(strict);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public String getSetting(String key) {
/* 2899 */     return this.properties.getProperty(key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Map getSettings() {
/* 2915 */     return Collections.unmodifiableMap(this.properties);
/*      */   }
/*      */   
/*      */   protected Environment getEnvironment() {
/* 2919 */     return (this instanceof Environment) ? (Environment)this : 
/*      */       
/* 2921 */       Environment.getCurrentEnvironment();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TemplateException unknownSettingException(String name) {
/* 2928 */     return new UnknownSettingException(
/* 2929 */         getEnvironment(), name, getCorrectedNameForUnknownSetting(name));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getCorrectedNameForUnknownSetting(String name) {
/* 2938 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected TemplateException settingValueAssignmentException(String name, String value, Throwable cause) {
/* 2945 */     return new SettingValueAssignmentException(getEnvironment(), name, value, cause);
/*      */   }
/*      */   
/*      */   protected TemplateException invalidSettingValueException(String name, String value) {
/* 2949 */     return new _MiscTemplateException(getEnvironment(), new Object[] { "Invalid value for setting ", new _DelayedJQuote(name), ": ", new _DelayedJQuote(value) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class UnknownSettingException
/*      */     extends _MiscTemplateException
/*      */   {
/*      */     private UnknownSettingException(Environment env, String name, String correctedName) {
/* 2959 */       super(env, new Object[] { null, null, (correctedName == null) ? "" : new Object[2] });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class SettingValueAssignmentException
/*      */     extends _MiscTemplateException
/*      */   {
/*      */     private SettingValueAssignmentException(Environment env, String name, String value, Throwable cause) {
/* 2976 */       super(cause, env, new Object[] { "Failed to set FreeMarker configuration setting ", new _DelayedJQuote(name), " to value ", new _DelayedJQuote(value), "; see cause exception." });
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSettings(Properties props) throws TemplateException {
/* 2991 */     _SettingEvaluationEnvironment prevEnv = _SettingEvaluationEnvironment.startScope();
/*      */     try {
/* 2993 */       for (Iterator<String> it = props.keySet().iterator(); it.hasNext(); ) {
/* 2994 */         String key = it.next();
/* 2995 */         setSetting(key, props.getProperty(key).trim());
/*      */       } 
/*      */     } finally {
/* 2998 */       _SettingEvaluationEnvironment.endScope(prevEnv);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSettings(InputStream propsIn) throws TemplateException, IOException {
/* 3012 */     Properties p = new Properties();
/* 3013 */     p.load(propsIn);
/* 3014 */     setSettings(p);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setCustomAttribute(Object key, Object value) {
/* 3023 */     synchronized (this.customAttributes) {
/* 3024 */       this.customAttributes.put(key, value);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Object getCustomAttribute(Object key, CustomAttribute attr) {
/* 3034 */     synchronized (this.customAttributes) {
/* 3035 */       Object o = this.customAttributes.get(key);
/* 3036 */       if (o == null && !this.customAttributes.containsKey(key)) {
/* 3037 */         o = attr.create();
/* 3038 */         this.customAttributes.put(key, o);
/*      */       } 
/* 3040 */       return o;
/*      */     } 
/*      */   }
/*      */   
/*      */   boolean isCustomAttributeSet(Object key) {
/* 3045 */     return this.customAttributes.containsKey(key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void copyDirectCustomAttributes(Configurable target, boolean overwriteExisting) {
/* 3057 */     synchronized (this.customAttributes) {
/* 3058 */       for (Map.Entry<?, ?> custAttrEnt : this.customAttributes.entrySet()) {
/* 3059 */         Object custAttrKey = custAttrEnt.getKey();
/* 3060 */         if (overwriteExisting || !target.isCustomAttributeSet(custAttrKey)) {
/* 3061 */           if (custAttrKey instanceof String) {
/* 3062 */             target.setCustomAttribute((String)custAttrKey, custAttrEnt.getValue()); continue;
/*      */           } 
/* 3064 */           target.setCustomAttribute(custAttrKey, custAttrEnt.getValue());
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCustomAttribute(String name, Object value) {
/* 3081 */     synchronized (this.customAttributes) {
/* 3082 */       this.customAttributes.put(name, value);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String[] getCustomAttributeNames() {
/* 3095 */     synchronized (this.customAttributes) {
/* 3096 */       Collection names = new LinkedList(this.customAttributes.keySet());
/* 3097 */       for (Iterator iter = names.iterator(); iter.hasNext();) {
/* 3098 */         if (!(iter.next() instanceof String)) {
/* 3099 */           iter.remove();
/*      */         }
/*      */       } 
/* 3102 */       return (String[])names.toArray((Object[])new String[names.size()]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeCustomAttribute(String name) {
/* 3117 */     synchronized (this.customAttributes) {
/* 3118 */       this.customAttributes.remove(name);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getCustomAttribute(String name) {
/*      */     Object retval;
/* 3136 */     synchronized (this.customAttributes) {
/* 3137 */       retval = this.customAttributes.get(name);
/* 3138 */       if (retval == null && this.customAttributes.containsKey(name)) {
/* 3139 */         return null;
/*      */       }
/*      */     } 
/* 3142 */     if (retval == null && this.parent != null) {
/* 3143 */       return this.parent.getCustomAttribute(name);
/*      */     }
/* 3145 */     return retval;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void doAutoImportsAndIncludes(Environment env) throws TemplateException, IOException {
/* 3154 */     if (this.parent != null) this.parent.doAutoImportsAndIncludes(env); 
/*      */   }
/*      */   
/*      */   protected ArrayList parseAsList(String text) throws ParseException {
/* 3158 */     return (new SettingStringParser(text)).parseAsList();
/*      */   }
/*      */ 
/*      */   
/*      */   protected ArrayList parseAsSegmentedList(String text) throws ParseException {
/* 3163 */     return (new SettingStringParser(text)).parseAsSegmentedList();
/*      */   }
/*      */   
/*      */   protected HashMap parseAsImportList(String text) throws ParseException {
/* 3167 */     return (new SettingStringParser(text)).parseAsImportList();
/*      */   }
/*      */   
/*      */   private static class KeyValuePair {
/*      */     private final Object key;
/*      */     private final Object value;
/*      */     
/*      */     KeyValuePair(Object key, Object value) {
/* 3175 */       this.key = key;
/* 3176 */       this.value = value;
/*      */     }
/*      */     
/*      */     Object getKey() {
/* 3180 */       return this.key;
/*      */     }
/*      */     
/*      */     Object getValue() {
/* 3184 */       return this.value;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class SettingStringParser
/*      */   {
/*      */     private String text;
/*      */     
/*      */     private int p;
/*      */     private int ln;
/*      */     
/*      */     private SettingStringParser(String text) {
/* 3197 */       this.text = text;
/* 3198 */       this.p = 0;
/* 3199 */       this.ln = text.length();
/*      */     }
/*      */     
/*      */     ArrayList parseAsSegmentedList() throws ParseException {
/* 3203 */       ArrayList<Configurable.KeyValuePair> segments = new ArrayList();
/* 3204 */       ArrayList<String> currentSegment = null;
/*      */ 
/*      */       
/*      */       while (true) {
/* 3208 */         char c = skipWS();
/* 3209 */         if (c == ' ')
/* 3210 */           break;  String item = fetchStringValue();
/* 3211 */         c = skipWS();
/*      */         
/* 3213 */         if (c == ':') {
/* 3214 */           currentSegment = new ArrayList();
/* 3215 */           segments.add(new Configurable.KeyValuePair(item, currentSegment));
/*      */         } else {
/* 3217 */           if (currentSegment == null) {
/* 3218 */             throw new ParseException("The very first list item must be followed by \":\" so it will be the key for the following sub-list.", 0, 0);
/*      */           }
/*      */ 
/*      */ 
/*      */           
/* 3223 */           currentSegment.add(item);
/*      */         } 
/*      */         
/* 3226 */         if (c == ' ')
/* 3227 */           break;  if (c != ',' && c != ':') throw new ParseException("Expected \",\" or \":\" or the end of text but found \"" + c + "\"", 0, 0);
/*      */ 
/*      */         
/* 3230 */         this.p++;
/*      */       } 
/* 3232 */       return segments;
/*      */     }
/*      */ 
/*      */     
/*      */     ArrayList parseAsList() throws ParseException {
/* 3237 */       ArrayList<String> seq = new ArrayList();
/*      */       while (true) {
/* 3239 */         char c = skipWS();
/* 3240 */         if (c == ' ')
/* 3241 */           break;  seq.add(fetchStringValue());
/* 3242 */         c = skipWS();
/* 3243 */         if (c == ' ')
/* 3244 */           break;  if (c != ',') throw new ParseException("Expected \",\" or the end of text but found \"" + c + "\"", 0, 0);
/*      */ 
/*      */         
/* 3247 */         this.p++;
/*      */       } 
/* 3249 */       return seq;
/*      */     }
/*      */ 
/*      */     
/*      */     HashMap parseAsImportList() throws ParseException {
/* 3254 */       HashMap<Object, Object> map = new HashMap<>();
/*      */       while (true) {
/* 3256 */         char c = skipWS();
/* 3257 */         if (c == ' ')
/* 3258 */           break;  String lib = fetchStringValue();
/*      */         
/* 3260 */         c = skipWS();
/* 3261 */         if (c == ' ') throw new ParseException("Unexpected end of text: expected \"as\"", 0, 0);
/*      */         
/* 3263 */         String s = fetchKeyword();
/* 3264 */         if (!s.equalsIgnoreCase("as")) throw new ParseException("Expected \"as\", but found " + 
/* 3265 */               StringUtil.jQuote(s), 0, 0);
/*      */         
/* 3267 */         c = skipWS();
/* 3268 */         if (c == ' ') throw new ParseException("Unexpected end of text: expected gate hash name", 0, 0);
/*      */         
/* 3270 */         String ns = fetchStringValue();
/*      */         
/* 3272 */         map.put(ns, lib);
/*      */         
/* 3274 */         c = skipWS();
/* 3275 */         if (c == ' ')
/* 3276 */           break;  if (c != ',') throw new ParseException("Expected \",\" or the end of text but found \"" + c + "\"", 0, 0);
/*      */ 
/*      */         
/* 3279 */         this.p++;
/*      */       } 
/* 3281 */       return map;
/*      */     }
/*      */     
/*      */     String fetchStringValue() throws ParseException {
/* 3285 */       String w = fetchWord();
/* 3286 */       if (w.startsWith("'") || w.startsWith("\"")) {
/* 3287 */         w = w.substring(1, w.length() - 1);
/*      */       }
/* 3289 */       return StringUtil.FTLStringLiteralDec(w);
/*      */     }
/*      */     
/*      */     String fetchKeyword() throws ParseException {
/* 3293 */       String w = fetchWord();
/* 3294 */       if (w.startsWith("'") || w.startsWith("\"")) {
/* 3295 */         throw new ParseException("Keyword expected, but a string value found: " + w, 0, 0);
/*      */       }
/*      */       
/* 3298 */       return w;
/*      */     }
/*      */ 
/*      */     
/*      */     char skipWS() {
/* 3303 */       while (this.p < this.ln) {
/* 3304 */         char c = this.text.charAt(this.p);
/* 3305 */         if (!Character.isWhitespace(c)) return c; 
/* 3306 */         this.p++;
/*      */       } 
/* 3308 */       return ' ';
/*      */     }
/*      */     
/*      */     private String fetchWord() throws ParseException {
/* 3312 */       if (this.p == this.ln) throw new ParseException("Unexpeced end of text", 0, 0);
/*      */ 
/*      */       
/* 3315 */       char c = this.text.charAt(this.p);
/* 3316 */       int b = this.p;
/* 3317 */       if (c == '\'' || c == '"') {
/* 3318 */         boolean escaped = false;
/* 3319 */         char q = c;
/* 3320 */         this.p++;
/* 3321 */         while (this.p < this.ln) {
/* 3322 */           c = this.text.charAt(this.p);
/* 3323 */           if (!escaped) {
/* 3324 */             if (c == '\\') {
/* 3325 */               escaped = true;
/* 3326 */             } else if (c == q) {
/*      */               break;
/*      */             } 
/*      */           } else {
/* 3330 */             escaped = false;
/*      */           } 
/* 3332 */           this.p++;
/*      */         } 
/* 3334 */         if (this.p == this.ln) {
/* 3335 */           throw new ParseException("Missing " + q, 0, 0);
/*      */         }
/* 3337 */         this.p++;
/* 3338 */         return this.text.substring(b, this.p);
/*      */       } 
/*      */       do {
/* 3341 */         c = this.text.charAt(this.p);
/* 3342 */         if (!Character.isLetterOrDigit(c) && c != '/' && c != '\\' && c != '_' && c != '.' && c != '-' && c != '!' && c != '*' && c != '?') {
/*      */           break;
/*      */         }
/*      */         
/* 3346 */         this.p++;
/* 3347 */       } while (this.p < this.ln);
/* 3348 */       if (b == this.p) {
/* 3349 */         throw new ParseException("Unexpected character: " + c, 0, 0);
/*      */       }
/* 3351 */       return this.text.substring(b, this.p);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Configurable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */