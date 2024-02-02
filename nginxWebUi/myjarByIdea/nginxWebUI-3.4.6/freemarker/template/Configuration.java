package freemarker.template;

import freemarker.cache.CacheStorage;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MruCacheStorage;
import freemarker.cache.SoftCacheStorage;
import freemarker.cache.TemplateCache;
import freemarker.cache.TemplateConfigurationFactory;
import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLookupStrategy;
import freemarker.cache.TemplateNameFormat;
import freemarker.core.BugException;
import freemarker.core.CSSOutputFormat;
import freemarker.core.CombinedMarkupOutputFormat;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.JSONOutputFormat;
import freemarker.core.JavaScriptOutputFormat;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.OutputFormat;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.PlainTextOutputFormat;
import freemarker.core.RTFOutputFormat;
import freemarker.core.UndefinedOutputFormat;
import freemarker.core.UnregisteredOutputFormatException;
import freemarker.core.XHTMLOutputFormat;
import freemarker.core.XMLOutputFormat;
import freemarker.core._CoreAPI;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.core._SortedArraySet;
import freemarker.core._UnmodifiableCompositeSet;
import freemarker.log.Logger;
import freemarker.template.utility.CaptureOutput;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.HtmlEscape;
import freemarker.template.utility.NormalizeNewlines;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StandardCompress;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.XmlEscape;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Configuration extends Configurable implements Cloneable, ParserConfiguration {
   private static final Logger CACHE_LOG = Logger.getLogger("freemarker.cache");
   private static final String VERSION_PROPERTIES_PATH = "/freemarker/version.properties";
   public static final String DEFAULT_ENCODING_KEY_SNAKE_CASE = "default_encoding";
   public static final String DEFAULT_ENCODING_KEY_CAMEL_CASE = "defaultEncoding";
   public static final String DEFAULT_ENCODING_KEY = "default_encoding";
   public static final String LOCALIZED_LOOKUP_KEY_SNAKE_CASE = "localized_lookup";
   public static final String LOCALIZED_LOOKUP_KEY_CAMEL_CASE = "localizedLookup";
   public static final String LOCALIZED_LOOKUP_KEY = "localized_lookup";
   public static final String STRICT_SYNTAX_KEY_SNAKE_CASE = "strict_syntax";
   public static final String STRICT_SYNTAX_KEY_CAMEL_CASE = "strictSyntax";
   public static final String STRICT_SYNTAX_KEY = "strict_syntax";
   public static final String WHITESPACE_STRIPPING_KEY_SNAKE_CASE = "whitespace_stripping";
   public static final String WHITESPACE_STRIPPING_KEY_CAMEL_CASE = "whitespaceStripping";
   public static final String WHITESPACE_STRIPPING_KEY = "whitespace_stripping";
   public static final String OUTPUT_FORMAT_KEY_SNAKE_CASE = "output_format";
   public static final String OUTPUT_FORMAT_KEY_CAMEL_CASE = "outputFormat";
   public static final String OUTPUT_FORMAT_KEY = "output_format";
   public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_SNAKE_CASE = "recognize_standard_file_extensions";
   public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE = "recognizeStandardFileExtensions";
   public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY = "recognize_standard_file_extensions";
   public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_SNAKE_CASE = "registered_custom_output_formats";
   public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE = "registeredCustomOutputFormats";
   public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY = "registered_custom_output_formats";
   public static final String AUTO_ESCAPING_POLICY_KEY_SNAKE_CASE = "auto_escaping_policy";
   public static final String AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE = "autoEscapingPolicy";
   public static final String AUTO_ESCAPING_POLICY_KEY = "auto_escaping_policy";
   public static final String CACHE_STORAGE_KEY_SNAKE_CASE = "cache_storage";
   public static final String CACHE_STORAGE_KEY_CAMEL_CASE = "cacheStorage";
   public static final String CACHE_STORAGE_KEY = "cache_storage";
   public static final String TEMPLATE_UPDATE_DELAY_KEY_SNAKE_CASE = "template_update_delay";
   public static final String TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE = "templateUpdateDelay";
   public static final String TEMPLATE_UPDATE_DELAY_KEY = "template_update_delay";
   /** @deprecated */
   public static final String AUTO_IMPORT_KEY_SNAKE_CASE = "auto_import";
   /** @deprecated */
   public static final String AUTO_IMPORT_KEY_CAMEL_CASE = "autoImport";
   /** @deprecated */
   public static final String AUTO_IMPORT_KEY = "auto_import";
   public static final String AUTO_INCLUDE_KEY_SNAKE_CASE = "auto_include";
   public static final String AUTO_INCLUDE_KEY_CAMEL_CASE = "autoInclude";
   public static final String AUTO_INCLUDE_KEY = "auto_include";
   public static final String TAG_SYNTAX_KEY_SNAKE_CASE = "tag_syntax";
   public static final String TAG_SYNTAX_KEY_CAMEL_CASE = "tagSyntax";
   public static final String TAG_SYNTAX_KEY = "tag_syntax";
   public static final String INTERPOLATION_SYNTAX_KEY_SNAKE_CASE = "interpolation_syntax";
   public static final String INTERPOLATION_SYNTAX_KEY_CAMEL_CASE = "interpolationSyntax";
   public static final String INTERPOLATION_SYNTAX_KEY = "interpolation_syntax";
   public static final String NAMING_CONVENTION_KEY_SNAKE_CASE = "naming_convention";
   public static final String NAMING_CONVENTION_KEY_CAMEL_CASE = "namingConvention";
   public static final String NAMING_CONVENTION_KEY = "naming_convention";
   public static final String TAB_SIZE_KEY_SNAKE_CASE = "tab_size";
   public static final String TAB_SIZE_KEY_CAMEL_CASE = "tabSize";
   public static final String TAB_SIZE_KEY = "tab_size";
   public static final String TEMPLATE_LOADER_KEY_SNAKE_CASE = "template_loader";
   public static final String TEMPLATE_LOADER_KEY_CAMEL_CASE = "templateLoader";
   public static final String TEMPLATE_LOADER_KEY = "template_loader";
   public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_SNAKE_CASE = "template_lookup_strategy";
   public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE = "templateLookupStrategy";
   public static final String TEMPLATE_LOOKUP_STRATEGY_KEY = "template_lookup_strategy";
   public static final String TEMPLATE_NAME_FORMAT_KEY_SNAKE_CASE = "template_name_format";
   public static final String TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE = "templateNameFormat";
   public static final String TEMPLATE_NAME_FORMAT_KEY = "template_name_format";
   public static final String TEMPLATE_CONFIGURATIONS_KEY_SNAKE_CASE = "template_configurations";
   public static final String TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE = "templateConfigurations";
   public static final String TEMPLATE_CONFIGURATIONS_KEY = "template_configurations";
   public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_SNAKE_CASE = "incompatible_improvements";
   public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE = "incompatibleImprovements";
   public static final String INCOMPATIBLE_IMPROVEMENTS_KEY = "incompatible_improvements";
   /** @deprecated */
   @Deprecated
   public static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";
   /** @deprecated */
   @Deprecated
   public static final String INCOMPATIBLE_ENHANCEMENTS = "incompatible_enhancements";
   public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_SNAKE_CASE = "fallback_on_null_loop_variable";
   public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE = "fallbackOnNullLoopVariable";
   public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY = "fallback_on_null_loop_variable";
   private static final String[] SETTING_NAMES_SNAKE_CASE = new String[]{"auto_escaping_policy", "cache_storage", "default_encoding", "fallback_on_null_loop_variable", "incompatible_improvements", "interpolation_syntax", "localized_lookup", "naming_convention", "output_format", "recognize_standard_file_extensions", "registered_custom_output_formats", "strict_syntax", "tab_size", "tag_syntax", "template_configurations", "template_loader", "template_lookup_strategy", "template_name_format", "template_update_delay", "whitespace_stripping"};
   private static final String[] SETTING_NAMES_CAMEL_CASE = new String[]{"autoEscapingPolicy", "cacheStorage", "defaultEncoding", "fallbackOnNullLoopVariable", "incompatibleImprovements", "interpolationSyntax", "localizedLookup", "namingConvention", "outputFormat", "recognizeStandardFileExtensions", "registeredCustomOutputFormats", "strictSyntax", "tabSize", "tagSyntax", "templateConfigurations", "templateLoader", "templateLookupStrategy", "templateNameFormat", "templateUpdateDelay", "whitespaceStripping"};
   private static final Map<String, OutputFormat> STANDARD_OUTPUT_FORMATS = new HashMap();
   public static final int AUTO_DETECT_TAG_SYNTAX = 0;
   public static final int ANGLE_BRACKET_TAG_SYNTAX = 1;
   public static final int SQUARE_BRACKET_TAG_SYNTAX = 2;
   public static final int LEGACY_INTERPOLATION_SYNTAX = 20;
   public static final int DOLLAR_INTERPOLATION_SYNTAX = 21;
   public static final int SQUARE_BRACKET_INTERPOLATION_SYNTAX = 22;
   public static final int AUTO_DETECT_NAMING_CONVENTION = 10;
   public static final int LEGACY_NAMING_CONVENTION = 11;
   public static final int CAMEL_CASE_NAMING_CONVENTION = 12;
   public static final int DISABLE_AUTO_ESCAPING_POLICY = 20;
   public static final int ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY = 21;
   public static final int ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY = 22;
   public static final Version VERSION_2_3_0;
   public static final Version VERSION_2_3_19;
   public static final Version VERSION_2_3_20;
   public static final Version VERSION_2_3_21;
   public static final Version VERSION_2_3_22;
   public static final Version VERSION_2_3_23;
   public static final Version VERSION_2_3_24;
   public static final Version VERSION_2_3_25;
   public static final Version VERSION_2_3_26;
   public static final Version VERSION_2_3_27;
   public static final Version VERSION_2_3_28;
   public static final Version VERSION_2_3_29;
   public static final Version VERSION_2_3_30;
   public static final Version VERSION_2_3_31;
   public static final Version DEFAULT_INCOMPATIBLE_IMPROVEMENTS;
   /** @deprecated */
   @Deprecated
   public static final String DEFAULT_INCOMPATIBLE_ENHANCEMENTS;
   /** @deprecated */
   @Deprecated
   public static final int PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS;
   private static final String NULL = "null";
   private static final String DEFAULT = "default";
   private static final String JVM_DEFAULT = "JVM default";
   private static final Version VERSION;
   private static final String FM_24_DETECTION_CLASS_NAME = "freemarker.core._2_4_OrLaterMarker";
   private static final boolean FM_24_DETECTED;
   private static final Object defaultConfigLock;
   private static volatile Configuration defaultConfig;
   private boolean strictSyntax;
   private volatile boolean localizedLookup;
   private boolean whitespaceStripping;
   private int autoEscapingPolicy;
   private OutputFormat outputFormat;
   private boolean outputFormatExplicitlySet;
   private Boolean recognizeStandardFileExtensions;
   private Map<String, ? extends OutputFormat> registeredCustomOutputFormats;
   private Version incompatibleImprovements;
   private int tagSyntax;
   private int interpolationSyntax;
   private int namingConvention;
   private int tabSize;
   private boolean fallbackOnNullLoopVariable;
   private boolean preventStrippings;
   private TemplateCache cache;
   private boolean templateLoaderExplicitlySet;
   private boolean templateLookupStrategyExplicitlySet;
   private boolean templateNameFormatExplicitlySet;
   private boolean cacheStorageExplicitlySet;
   private boolean objectWrapperExplicitlySet;
   private boolean templateExceptionHandlerExplicitlySet;
   private boolean attemptExceptionReporterExplicitlySet;
   private boolean logTemplateExceptionsExplicitlySet;
   private boolean wrapUncheckedExceptionsExplicitlySet;
   private boolean localeExplicitlySet;
   private boolean defaultEncodingExplicitlySet;
   private boolean timeZoneExplicitlySet;
   private HashMap sharedVariables;
   private HashMap rewrappableSharedVariables;
   private String defaultEncoding;
   private ConcurrentMap localeToCharsetMap;

   /** @deprecated */
   @Deprecated
   public Configuration() {
      this(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
   }

   public Configuration(Version incompatibleImprovements) {
      super(incompatibleImprovements);
      this.strictSyntax = true;
      this.localizedLookup = true;
      this.whitespaceStripping = true;
      this.autoEscapingPolicy = 21;
      this.outputFormat = UndefinedOutputFormat.INSTANCE;
      this.registeredCustomOutputFormats = Collections.emptyMap();
      this.tagSyntax = 1;
      this.interpolationSyntax = 20;
      this.namingConvention = 10;
      this.tabSize = 8;
      this.fallbackOnNullLoopVariable = true;
      this.sharedVariables = new HashMap();
      this.rewrappableSharedVariables = null;
      this.defaultEncoding = getDefaultDefaultEncoding();
      this.localeToCharsetMap = new ConcurrentHashMap();
      checkFreeMarkerVersionClash();
      NullArgumentException.check("incompatibleImprovements", incompatibleImprovements);
      checkCurrentVersionNotRecycled(incompatibleImprovements);
      this.incompatibleImprovements = incompatibleImprovements;
      this.createTemplateCache();
      this.loadBuiltInSharedVariables();
   }

   private static void checkFreeMarkerVersionClash() {
      if (FM_24_DETECTED) {
         throw new RuntimeException("Clashing FreeMarker versions (" + VERSION + " and some post-2.3.x) detected: found post-2.3.x class " + "freemarker.core._2_4_OrLaterMarker" + ". You probably have two different freemarker.jar-s in the classpath.");
      }
   }

   private void createTemplateCache() {
      this.cache = new TemplateCache(this.getDefaultTemplateLoader(), this.getDefaultCacheStorage(), this.getDefaultTemplateLookupStrategy(), this.getDefaultTemplateNameFormat(), (TemplateConfigurationFactory)null, this);
      this.cache.clear();
      this.cache.setDelay(5000L);
   }

   private void recreateTemplateCacheWith(TemplateLoader loader, CacheStorage storage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations) {
      TemplateCache oldCache = this.cache;
      this.cache = new TemplateCache(loader, storage, templateLookupStrategy, templateNameFormat, templateConfigurations, this);
      this.cache.clear();
      this.cache.setDelay(oldCache.getDelay());
      this.cache.setLocalizedLookup(this.localizedLookup);
   }

   private void recreateTemplateCache() {
      this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.getTemplateConfigurations());
   }

   private TemplateLoader getDefaultTemplateLoader() {
      return createDefaultTemplateLoader(this.getIncompatibleImprovements(), this.getTemplateLoader());
   }

   static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements) {
      return createDefaultTemplateLoader(incompatibleImprovements, (TemplateLoader)null);
   }

   private static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements, TemplateLoader existingTemplateLoader) {
      if (incompatibleImprovements.intValue() < _TemplateAPI.VERSION_INT_2_3_21) {
         if (existingTemplateLoader instanceof LegacyDefaultFileTemplateLoader) {
            return existingTemplateLoader;
         } else {
            try {
               return new LegacyDefaultFileTemplateLoader();
            } catch (Exception var3) {
               CACHE_LOG.warn("Couldn't create legacy default TemplateLoader which accesses the current directory. (Use new Configuration(Configuration.VERSION_2_3_21) or higher to avoid this.)", var3);
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private TemplateLookupStrategy getDefaultTemplateLookupStrategy() {
      return getDefaultTemplateLookupStrategy(this.getIncompatibleImprovements());
   }

   static TemplateLookupStrategy getDefaultTemplateLookupStrategy(Version incompatibleImprovements) {
      return TemplateLookupStrategy.DEFAULT_2_3_0;
   }

   private TemplateNameFormat getDefaultTemplateNameFormat() {
      return getDefaultTemplateNameFormat(this.getIncompatibleImprovements());
   }

   static TemplateNameFormat getDefaultTemplateNameFormat(Version incompatibleImprovements) {
      return TemplateNameFormat.DEFAULT_2_3_0;
   }

   private CacheStorage getDefaultCacheStorage() {
      return createDefaultCacheStorage(this.getIncompatibleImprovements(), this.getCacheStorage());
   }

   static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements, CacheStorage existingCacheStorage) {
      return (CacheStorage)(existingCacheStorage instanceof DefaultSoftCacheStorage ? existingCacheStorage : new DefaultSoftCacheStorage());
   }

   static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements) {
      return createDefaultCacheStorage(incompatibleImprovements, (CacheStorage)null);
   }

   private TemplateExceptionHandler getDefaultTemplateExceptionHandler() {
      return getDefaultTemplateExceptionHandler(this.getIncompatibleImprovements());
   }

   private AttemptExceptionReporter getDefaultAttemptExceptionReporter() {
      return getDefaultAttemptExceptionReporter(this.getIncompatibleImprovements());
   }

   private boolean getDefaultLogTemplateExceptions() {
      return getDefaultLogTemplateExceptions(this.getIncompatibleImprovements());
   }

   private boolean getDefaultWrapUncheckedExceptions() {
      return getDefaultWrapUncheckedExceptions(this.getIncompatibleImprovements());
   }

   private ObjectWrapper getDefaultObjectWrapper() {
      return getDefaultObjectWrapper(this.getIncompatibleImprovements());
   }

   static TemplateExceptionHandler getDefaultTemplateExceptionHandler(Version incompatibleImprovements) {
      return TemplateExceptionHandler.DEBUG_HANDLER;
   }

   static AttemptExceptionReporter getDefaultAttemptExceptionReporter(Version incompatibleImprovements) {
      return AttemptExceptionReporter.LOG_ERROR_REPORTER;
   }

   static boolean getDefaultLogTemplateExceptions(Version incompatibleImprovements) {
      return true;
   }

   static boolean getDefaultWrapUncheckedExceptions(Version incompatibleImprovements) {
      return false;
   }

   public Object clone() {
      try {
         Configuration copy = (Configuration)super.clone();
         copy.sharedVariables = new HashMap(this.sharedVariables);
         copy.localeToCharsetMap = new ConcurrentHashMap(this.localeToCharsetMap);
         copy.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
         return copy;
      } catch (CloneNotSupportedException var2) {
         throw new BugException("Cloning failed", var2);
      }
   }

   private void loadBuiltInSharedVariables() {
      this.sharedVariables.put("capture_output", new CaptureOutput());
      this.sharedVariables.put("compress", StandardCompress.INSTANCE);
      this.sharedVariables.put("html_escape", new HtmlEscape());
      this.sharedVariables.put("normalize_newlines", new NormalizeNewlines());
      this.sharedVariables.put("xml_escape", new XmlEscape());
   }

   public void loadBuiltInEncodingMap() {
      this.localeToCharsetMap.clear();
      this.localeToCharsetMap.put("ar", "ISO-8859-6");
      this.localeToCharsetMap.put("be", "ISO-8859-5");
      this.localeToCharsetMap.put("bg", "ISO-8859-5");
      this.localeToCharsetMap.put("ca", "ISO-8859-1");
      this.localeToCharsetMap.put("cs", "ISO-8859-2");
      this.localeToCharsetMap.put("da", "ISO-8859-1");
      this.localeToCharsetMap.put("de", "ISO-8859-1");
      this.localeToCharsetMap.put("el", "ISO-8859-7");
      this.localeToCharsetMap.put("en", "ISO-8859-1");
      this.localeToCharsetMap.put("es", "ISO-8859-1");
      this.localeToCharsetMap.put("et", "ISO-8859-1");
      this.localeToCharsetMap.put("fi", "ISO-8859-1");
      this.localeToCharsetMap.put("fr", "ISO-8859-1");
      this.localeToCharsetMap.put("hr", "ISO-8859-2");
      this.localeToCharsetMap.put("hu", "ISO-8859-2");
      this.localeToCharsetMap.put("is", "ISO-8859-1");
      this.localeToCharsetMap.put("it", "ISO-8859-1");
      this.localeToCharsetMap.put("iw", "ISO-8859-8");
      this.localeToCharsetMap.put("ja", "Shift_JIS");
      this.localeToCharsetMap.put("ko", "EUC-KR");
      this.localeToCharsetMap.put("lt", "ISO-8859-2");
      this.localeToCharsetMap.put("lv", "ISO-8859-2");
      this.localeToCharsetMap.put("mk", "ISO-8859-5");
      this.localeToCharsetMap.put("nl", "ISO-8859-1");
      this.localeToCharsetMap.put("no", "ISO-8859-1");
      this.localeToCharsetMap.put("pl", "ISO-8859-2");
      this.localeToCharsetMap.put("pt", "ISO-8859-1");
      this.localeToCharsetMap.put("ro", "ISO-8859-2");
      this.localeToCharsetMap.put("ru", "ISO-8859-5");
      this.localeToCharsetMap.put("sh", "ISO-8859-5");
      this.localeToCharsetMap.put("sk", "ISO-8859-2");
      this.localeToCharsetMap.put("sl", "ISO-8859-2");
      this.localeToCharsetMap.put("sq", "ISO-8859-2");
      this.localeToCharsetMap.put("sr", "ISO-8859-5");
      this.localeToCharsetMap.put("sv", "ISO-8859-1");
      this.localeToCharsetMap.put("tr", "ISO-8859-9");
      this.localeToCharsetMap.put("uk", "ISO-8859-5");
      this.localeToCharsetMap.put("zh", "GB2312");
      this.localeToCharsetMap.put("zh_TW", "Big5");
   }

   public void clearEncodingMap() {
      this.localeToCharsetMap.clear();
   }

   /** @deprecated */
   @Deprecated
   public static Configuration getDefaultConfiguration() {
      Configuration defaultConfig = Configuration.defaultConfig;
      if (defaultConfig == null) {
         synchronized(defaultConfigLock) {
            defaultConfig = Configuration.defaultConfig;
            if (defaultConfig == null) {
               defaultConfig = new Configuration();
               Configuration.defaultConfig = defaultConfig;
            }
         }
      }

      return defaultConfig;
   }

   /** @deprecated */
   @Deprecated
   public static void setDefaultConfiguration(Configuration config) {
      synchronized(defaultConfigLock) {
         defaultConfig = config;
      }
   }

   public void setTemplateLoader(TemplateLoader templateLoader) {
      synchronized(this) {
         if (this.cache.getTemplateLoader() != templateLoader) {
            this.recreateTemplateCacheWith(templateLoader, this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
         }

         this.templateLoaderExplicitlySet = true;
      }
   }

   public void unsetTemplateLoader() {
      if (this.templateLoaderExplicitlySet) {
         this.setTemplateLoader(this.getDefaultTemplateLoader());
         this.templateLoaderExplicitlySet = false;
      }

   }

   public boolean isTemplateLoaderExplicitlySet() {
      return this.templateLoaderExplicitlySet;
   }

   public TemplateLoader getTemplateLoader() {
      return this.cache == null ? null : this.cache.getTemplateLoader();
   }

   public void setTemplateLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
      if (this.cache.getTemplateLookupStrategy() != templateLookupStrategy) {
         this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), templateLookupStrategy, this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
      }

      this.templateLookupStrategyExplicitlySet = true;
   }

   public void unsetTemplateLookupStrategy() {
      if (this.templateLookupStrategyExplicitlySet) {
         this.setTemplateLookupStrategy(this.getDefaultTemplateLookupStrategy());
         this.templateLookupStrategyExplicitlySet = false;
      }

   }

   public boolean isTemplateLookupStrategyExplicitlySet() {
      return this.templateLookupStrategyExplicitlySet;
   }

   public TemplateLookupStrategy getTemplateLookupStrategy() {
      return this.cache == null ? null : this.cache.getTemplateLookupStrategy();
   }

   public void setTemplateNameFormat(TemplateNameFormat templateNameFormat) {
      if (this.cache.getTemplateNameFormat() != templateNameFormat) {
         this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), templateNameFormat, this.cache.getTemplateConfigurations());
      }

      this.templateNameFormatExplicitlySet = true;
   }

   public void unsetTemplateNameFormat() {
      if (this.templateNameFormatExplicitlySet) {
         this.setTemplateNameFormat(this.getDefaultTemplateNameFormat());
         this.templateNameFormatExplicitlySet = false;
      }

   }

   public boolean isTemplateNameFormatExplicitlySet() {
      return this.templateNameFormatExplicitlySet;
   }

   public TemplateNameFormat getTemplateNameFormat() {
      return this.cache == null ? null : this.cache.getTemplateNameFormat();
   }

   public void setTemplateConfigurations(TemplateConfigurationFactory templateConfigurations) {
      if (this.cache.getTemplateConfigurations() != templateConfigurations) {
         if (templateConfigurations != null) {
            templateConfigurations.setConfiguration(this);
         }

         this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), templateConfigurations);
      }

   }

   public TemplateConfigurationFactory getTemplateConfigurations() {
      return this.cache == null ? null : this.cache.getTemplateConfigurations();
   }

   public void setCacheStorage(CacheStorage cacheStorage) {
      synchronized(this) {
         if (this.getCacheStorage() != cacheStorage) {
            this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), cacheStorage, this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
         }

         this.cacheStorageExplicitlySet = true;
      }
   }

   public void unsetCacheStorage() {
      if (this.cacheStorageExplicitlySet) {
         this.setCacheStorage(this.getDefaultCacheStorage());
         this.cacheStorageExplicitlySet = false;
      }

   }

   public boolean isCacheStorageExplicitlySet() {
      return this.cacheStorageExplicitlySet;
   }

   public CacheStorage getCacheStorage() {
      synchronized(this) {
         return this.cache == null ? null : this.cache.getCacheStorage();
      }
   }

   public void setDirectoryForTemplateLoading(File dir) throws IOException {
      TemplateLoader tl = this.getTemplateLoader();
      if (tl instanceof FileTemplateLoader) {
         String path = ((FileTemplateLoader)tl).baseDir.getCanonicalPath();
         if (path.equals(dir.getCanonicalPath())) {
            return;
         }
      }

      this.setTemplateLoader(new FileTemplateLoader(dir));
   }

   public void setServletContextForTemplateLoading(Object servletContext, String path) {
      try {
         Class webappTemplateLoaderClass = ClassUtil.forName("freemarker.cache.WebappTemplateLoader");
         Class servletContextClass = ClassUtil.forName("javax.servlet.ServletContext");
         Class[] constructorParamTypes;
         Object[] constructorParams;
         if (path == null) {
            constructorParamTypes = new Class[]{servletContextClass};
            constructorParams = new Object[]{servletContext};
         } else {
            constructorParamTypes = new Class[]{servletContextClass, String.class};
            constructorParams = new Object[]{servletContext, path};
         }

         this.setTemplateLoader((TemplateLoader)webappTemplateLoaderClass.getConstructor(constructorParamTypes).newInstance(constructorParams));
      } catch (Exception var7) {
         throw new BugException(var7);
      }
   }

   public void setClassForTemplateLoading(Class resourceLoaderClass, String basePackagePath) {
      this.setTemplateLoader(new ClassTemplateLoader(resourceLoaderClass, basePackagePath));
   }

   public void setClassLoaderForTemplateLoading(ClassLoader classLoader, String basePackagePath) {
      this.setTemplateLoader(new ClassTemplateLoader(classLoader, basePackagePath));
   }

   /** @deprecated */
   @Deprecated
   public void setTemplateUpdateDelay(int seconds) {
      this.cache.setDelay(1000L * (long)seconds);
   }

   public void setTemplateUpdateDelayMilliseconds(long millis) {
      this.cache.setDelay(millis);
   }

   public long getTemplateUpdateDelayMilliseconds() {
      return this.cache.getDelay();
   }

   /** @deprecated */
   @Deprecated
   public void setStrictSyntaxMode(boolean b) {
      this.strictSyntax = b;
   }

   public void setObjectWrapper(ObjectWrapper objectWrapper) {
      ObjectWrapper prevObjectWrapper = this.getObjectWrapper();
      super.setObjectWrapper(objectWrapper);
      this.objectWrapperExplicitlySet = true;
      if (objectWrapper != prevObjectWrapper) {
         try {
            this.setSharedVariablesFromRewrappableSharedVariables();
         } catch (TemplateModelException var4) {
            throw new RuntimeException("Failed to re-wrap earliearly set shared variables with the newly set object wrapper", var4);
         }
      }

   }

   public void unsetObjectWrapper() {
      if (this.objectWrapperExplicitlySet) {
         this.setObjectWrapper(this.getDefaultObjectWrapper());
         this.objectWrapperExplicitlySet = false;
      }

   }

   public boolean isObjectWrapperExplicitlySet() {
      return this.objectWrapperExplicitlySet;
   }

   public void setLocale(Locale locale) {
      super.setLocale(locale);
      this.localeExplicitlySet = true;
   }

   public void unsetLocale() {
      if (this.localeExplicitlySet) {
         this.setLocale(getDefaultLocale());
         this.localeExplicitlySet = false;
      }

   }

   public boolean isLocaleExplicitlySet() {
      return this.localeExplicitlySet;
   }

   static Locale getDefaultLocale() {
      return Locale.getDefault();
   }

   public void setTimeZone(TimeZone timeZone) {
      super.setTimeZone(timeZone);
      this.timeZoneExplicitlySet = true;
   }

   public void unsetTimeZone() {
      if (this.timeZoneExplicitlySet) {
         this.setTimeZone(getDefaultTimeZone());
         this.timeZoneExplicitlySet = false;
      }

   }

   public boolean isTimeZoneExplicitlySet() {
      return this.timeZoneExplicitlySet;
   }

   static TimeZone getDefaultTimeZone() {
      return TimeZone.getDefault();
   }

   public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
      super.setTemplateExceptionHandler(templateExceptionHandler);
      this.templateExceptionHandlerExplicitlySet = true;
   }

   public void unsetTemplateExceptionHandler() {
      if (this.templateExceptionHandlerExplicitlySet) {
         this.setTemplateExceptionHandler(this.getDefaultTemplateExceptionHandler());
         this.templateExceptionHandlerExplicitlySet = false;
      }

   }

   public boolean isTemplateExceptionHandlerExplicitlySet() {
      return this.templateExceptionHandlerExplicitlySet;
   }

   public void setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
      super.setAttemptExceptionReporter(attemptExceptionReporter);
      this.attemptExceptionReporterExplicitlySet = true;
   }

   public void unsetAttemptExceptionReporter() {
      if (this.attemptExceptionReporterExplicitlySet) {
         this.setAttemptExceptionReporter(this.getDefaultAttemptExceptionReporter());
         this.attemptExceptionReporterExplicitlySet = false;
      }

   }

   public boolean isAttemptExceptionReporterExplicitlySet() {
      return this.attemptExceptionReporterExplicitlySet;
   }

   public void setLogTemplateExceptions(boolean value) {
      super.setLogTemplateExceptions(value);
      this.logTemplateExceptionsExplicitlySet = true;
   }

   public void unsetLogTemplateExceptions() {
      if (this.logTemplateExceptionsExplicitlySet) {
         this.setLogTemplateExceptions(this.getDefaultLogTemplateExceptions());
         this.logTemplateExceptionsExplicitlySet = false;
      }

   }

   public boolean isLogTemplateExceptionsExplicitlySet() {
      return this.logTemplateExceptionsExplicitlySet;
   }

   public void setWrapUncheckedExceptions(boolean value) {
      super.setWrapUncheckedExceptions(value);
      this.wrapUncheckedExceptionsExplicitlySet = true;
   }

   public void unsetWrapUncheckedExceptions() {
      if (this.wrapUncheckedExceptionsExplicitlySet) {
         this.setWrapUncheckedExceptions(this.getDefaultWrapUncheckedExceptions());
         this.wrapUncheckedExceptionsExplicitlySet = false;
      }

   }

   public boolean isWrapUncheckedExceptionsExplicitlySet() {
      return this.wrapUncheckedExceptionsExplicitlySet;
   }

   public boolean getStrictSyntaxMode() {
      return this.strictSyntax;
   }

   public void setIncompatibleImprovements(Version incompatibleImprovements) {
      _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
      if (!this.incompatibleImprovements.equals(incompatibleImprovements)) {
         checkCurrentVersionNotRecycled(incompatibleImprovements);
         this.incompatibleImprovements = incompatibleImprovements;
         if (!this.templateLoaderExplicitlySet) {
            this.templateLoaderExplicitlySet = true;
            this.unsetTemplateLoader();
         }

         if (!this.templateLookupStrategyExplicitlySet) {
            this.templateLookupStrategyExplicitlySet = true;
            this.unsetTemplateLookupStrategy();
         }

         if (!this.templateNameFormatExplicitlySet) {
            this.templateNameFormatExplicitlySet = true;
            this.unsetTemplateNameFormat();
         }

         if (!this.cacheStorageExplicitlySet) {
            this.cacheStorageExplicitlySet = true;
            this.unsetCacheStorage();
         }

         if (!this.templateExceptionHandlerExplicitlySet) {
            this.templateExceptionHandlerExplicitlySet = true;
            this.unsetTemplateExceptionHandler();
         }

         if (!this.attemptExceptionReporterExplicitlySet) {
            this.attemptExceptionReporterExplicitlySet = true;
            this.unsetAttemptExceptionReporter();
         }

         if (!this.logTemplateExceptionsExplicitlySet) {
            this.logTemplateExceptionsExplicitlySet = true;
            this.unsetLogTemplateExceptions();
         }

         if (!this.wrapUncheckedExceptionsExplicitlySet) {
            this.wrapUncheckedExceptionsExplicitlySet = true;
            this.unsetWrapUncheckedExceptions();
         }

         if (!this.objectWrapperExplicitlySet) {
            this.objectWrapperExplicitlySet = true;
            this.unsetObjectWrapper();
         }

         this.recreateTemplateCache();
      }

   }

   private static void checkCurrentVersionNotRecycled(Version incompatibleImprovements) {
      _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.configuration", "Configuration");
   }

   public Version getIncompatibleImprovements() {
      return this.incompatibleImprovements;
   }

   /** @deprecated */
   @Deprecated
   public void setIncompatibleEnhancements(String version) {
      this.setIncompatibleImprovements(new Version(version));
   }

   /** @deprecated */
   @Deprecated
   public String getIncompatibleEnhancements() {
      return this.incompatibleImprovements.toString();
   }

   /** @deprecated */
   @Deprecated
   public int getParsedIncompatibleEnhancements() {
      return this.getIncompatibleImprovements().intValue();
   }

   public void setWhitespaceStripping(boolean b) {
      this.whitespaceStripping = b;
   }

   public boolean getWhitespaceStripping() {
      return this.whitespaceStripping;
   }

   public void setAutoEscapingPolicy(int autoEscapingPolicy) {
      _TemplateAPI.validateAutoEscapingPolicyValue(autoEscapingPolicy);
      int prevAutoEscaping = this.getAutoEscapingPolicy();
      this.autoEscapingPolicy = autoEscapingPolicy;
      if (prevAutoEscaping != autoEscapingPolicy) {
         this.clearTemplateCache();
      }

   }

   public int getAutoEscapingPolicy() {
      return this.autoEscapingPolicy;
   }

   public void setOutputFormat(OutputFormat outputFormat) {
      if (outputFormat == null) {
         throw new NullArgumentException("outputFormat", "You may meant: " + UndefinedOutputFormat.class.getSimpleName() + ".INSTANCE");
      } else {
         OutputFormat prevOutputFormat = this.getOutputFormat();
         this.outputFormat = outputFormat;
         this.outputFormatExplicitlySet = true;
         if (prevOutputFormat != outputFormat) {
            this.clearTemplateCache();
         }

      }
   }

   public OutputFormat getOutputFormat() {
      return this.outputFormat;
   }

   public boolean isOutputFormatExplicitlySet() {
      return this.outputFormatExplicitlySet;
   }

   public void unsetOutputFormat() {
      this.outputFormat = UndefinedOutputFormat.INSTANCE;
      this.outputFormatExplicitlySet = false;
   }

   public OutputFormat getOutputFormat(String name) throws UnregisteredOutputFormatException {
      if (name.length() == 0) {
         throw new IllegalArgumentException("0-length format name");
      } else if (name.charAt(name.length() - 1) == '}') {
         int openBrcIdx = name.indexOf(123);
         if (openBrcIdx == -1) {
            throw new IllegalArgumentException("Missing opening '{' in: " + name);
         } else {
            MarkupOutputFormat outerOF = this.getMarkupOutputFormatForCombined(name.substring(0, openBrcIdx));
            MarkupOutputFormat innerOF = this.getMarkupOutputFormatForCombined(name.substring(openBrcIdx + 1, name.length() - 1));
            return new CombinedMarkupOutputFormat(name, outerOF, innerOF);
         }
      } else {
         OutputFormat custOF = (OutputFormat)this.registeredCustomOutputFormats.get(name);
         if (custOF != null) {
            return custOF;
         } else {
            OutputFormat stdOF = (OutputFormat)STANDARD_OUTPUT_FORMATS.get(name);
            if (stdOF == null) {
               StringBuilder sb = new StringBuilder();
               sb.append("Unregistered output format name, ");
               sb.append(StringUtil.jQuote(name));
               sb.append(". The output formats registered in the Configuration are: ");
               Set<String> registeredNames = new TreeSet();
               registeredNames.addAll(STANDARD_OUTPUT_FORMATS.keySet());
               registeredNames.addAll(this.registeredCustomOutputFormats.keySet());
               boolean first = true;

               String registeredName;
               for(Iterator var7 = registeredNames.iterator(); var7.hasNext(); sb.append(StringUtil.jQuote(registeredName))) {
                  registeredName = (String)var7.next();
                  if (first) {
                     first = false;
                  } else {
                     sb.append(", ");
                  }
               }

               throw new UnregisteredOutputFormatException(sb.toString());
            } else {
               return stdOF;
            }
         }
      }
   }

   private MarkupOutputFormat getMarkupOutputFormatForCombined(String outerName) throws UnregisteredOutputFormatException {
      OutputFormat of = this.getOutputFormat(outerName);
      if (!(of instanceof MarkupOutputFormat)) {
         throw new IllegalArgumentException("The \"" + outerName + "\" output format can't be used in ...{...} expression, because it's not a markup format.");
      } else {
         MarkupOutputFormat outerOF = (MarkupOutputFormat)of;
         return outerOF;
      }
   }

   public void setRegisteredCustomOutputFormats(Collection<? extends OutputFormat> registeredCustomOutputFormats) {
      NullArgumentException.check(registeredCustomOutputFormats);
      Map<String, OutputFormat> m = new LinkedHashMap(registeredCustomOutputFormats.size() * 4 / 3, 1.0F);
      Iterator var3 = registeredCustomOutputFormats.iterator();

      OutputFormat outputFormat;
      OutputFormat replaced;
      do {
         if (!var3.hasNext()) {
            this.registeredCustomOutputFormats = Collections.unmodifiableMap(m);
            this.clearTemplateCache();
            return;
         }

         outputFormat = (OutputFormat)var3.next();
         String name = outputFormat.getName();
         if (name.equals(UndefinedOutputFormat.INSTANCE.getName())) {
            throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
         }

         if (name.equals(PlainTextOutputFormat.INSTANCE.getName())) {
            throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
         }

         if (name.length() == 0) {
            throw new IllegalArgumentException("The output format name can't be 0 long");
         }

         if (!Character.isLetterOrDigit(name.charAt(0))) {
            throw new IllegalArgumentException("The output format name must start with letter or digit: " + name);
         }

         if (name.indexOf(43) != -1) {
            throw new IllegalArgumentException("The output format name can't contain \"+\" character: " + name);
         }

         if (name.indexOf(123) != -1) {
            throw new IllegalArgumentException("The output format name can't contain \"{\" character: " + name);
         }

         if (name.indexOf(125) != -1) {
            throw new IllegalArgumentException("The output format name can't contain \"}\" character: " + name);
         }

         replaced = (OutputFormat)m.put(outputFormat.getName(), outputFormat);
      } while(replaced == null);

      if (replaced == outputFormat) {
         throw new IllegalArgumentException("Duplicate output format in the collection: " + outputFormat);
      } else {
         throw new IllegalArgumentException("Clashing output format names between " + replaced + " and " + outputFormat + ".");
      }
   }

   public Collection<? extends OutputFormat> getRegisteredCustomOutputFormats() {
      return this.registeredCustomOutputFormats.values();
   }

   public void setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
      boolean prevEffectiveValue = this.getRecognizeStandardFileExtensions();
      this.recognizeStandardFileExtensions = recognizeStandardFileExtensions;
      if (prevEffectiveValue != recognizeStandardFileExtensions) {
         this.clearTemplateCache();
      }

   }

   public void unsetRecognizeStandardFileExtensions() {
      if (this.recognizeStandardFileExtensions != null) {
         this.recognizeStandardFileExtensions = null;
      }

   }

   public boolean isRecognizeStandardFileExtensionsExplicitlySet() {
      return this.recognizeStandardFileExtensions != null;
   }

   public boolean getRecognizeStandardFileExtensions() {
      return this.recognizeStandardFileExtensions == null ? this.incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_24 : this.recognizeStandardFileExtensions;
   }

   public void setTagSyntax(int tagSyntax) {
      _TemplateAPI.valideTagSyntaxValue(tagSyntax);
      this.tagSyntax = tagSyntax;
   }

   public int getTagSyntax() {
      return this.tagSyntax;
   }

   public void setInterpolationSyntax(int interpolationSyntax) {
      _TemplateAPI.valideInterpolationSyntaxValue(interpolationSyntax);
      this.interpolationSyntax = interpolationSyntax;
   }

   public int getInterpolationSyntax() {
      return this.interpolationSyntax;
   }

   public void setNamingConvention(int namingConvention) {
      _TemplateAPI.validateNamingConventionValue(namingConvention);
      this.namingConvention = namingConvention;
   }

   public int getNamingConvention() {
      return this.namingConvention;
   }

   public void setTabSize(int tabSize) {
      if (tabSize < 1) {
         throw new IllegalArgumentException("\"tabSize\" must be at least 1, but was " + tabSize);
      } else if (tabSize > 256) {
         throw new IllegalArgumentException("\"tabSize\" can't be more than 256, but was " + tabSize);
      } else {
         this.tabSize = tabSize;
      }
   }

   public int getTabSize() {
      return this.tabSize;
   }

   public boolean getFallbackOnNullLoopVariable() {
      return this.fallbackOnNullLoopVariable;
   }

   public void setFallbackOnNullLoopVariable(boolean fallbackOnNullLoopVariable) {
      this.fallbackOnNullLoopVariable = fallbackOnNullLoopVariable;
   }

   boolean getPreventStrippings() {
      return this.preventStrippings;
   }

   void setPreventStrippings(boolean preventStrippings) {
      this.preventStrippings = preventStrippings;
   }

   public Template getTemplate(String name) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
      return this.getTemplate(name, (Locale)null, (Object)null, (String)null, true, false);
   }

   public Template getTemplate(String name, Locale locale) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
      return this.getTemplate(name, locale, (Object)null, (String)null, true, false);
   }

   public Template getTemplate(String name, String encoding) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
      return this.getTemplate(name, (Locale)null, (Object)null, encoding, true, false);
   }

   public Template getTemplate(String name, Locale locale, String encoding) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
      return this.getTemplate(name, locale, (Object)null, encoding, true, false);
   }

   public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
      return this.getTemplate(name, locale, (Object)null, encoding, parseAsFTL, false);
   }

   public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
      return this.getTemplate(name, locale, (Object)null, encoding, parseAsFTL, ignoreMissing);
   }

   public Template getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
      if (locale == null) {
         locale = this.getLocale();
      }

      if (encoding == null) {
         encoding = this.getEncoding(locale);
      }

      TemplateCache.MaybeMissingTemplate maybeTemp = this.cache.getTemplate(name, locale, customLookupCondition, encoding, parseAsFTL);
      Template temp = maybeTemp.getTemplate();
      if (temp == null) {
         if (ignoreMissing) {
            return null;
         } else {
            TemplateLoader tl = this.getTemplateLoader();
            String msg;
            String normName;
            if (tl == null) {
               msg = "Don't know where to load template " + StringUtil.jQuote(name) + " from because the \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), so it's null.";
            } else {
               normName = maybeTemp.getMissingTemplateNormalizedName();
               String missingTempReason = maybeTemp.getMissingTemplateReason();
               TemplateLookupStrategy templateLookupStrategy = this.getTemplateLookupStrategy();
               msg = "Template not found for name " + StringUtil.jQuote(name) + (normName != null && name != null && !this.removeInitialSlash(name).equals(normName) ? " (normalized: " + StringUtil.jQuote(normName) + ")" : "") + (customLookupCondition != null ? " and custom lookup condition " + StringUtil.jQuote(customLookupCondition) : "") + "." + (missingTempReason != null ? "\nReason given: " + this.ensureSentenceIsClosed(missingTempReason) : "") + "\nThe name was interpreted by this TemplateLoader: " + StringUtil.tryToString(tl) + "." + (!this.isKnownNonConfusingLookupStrategy(templateLookupStrategy) ? "\n(Before that, the name was possibly changed by this lookup strategy: " + StringUtil.tryToString(templateLookupStrategy) + ".)" : "") + (!this.templateLoaderExplicitlySet ? "\nWarning: The \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), and using the default value is most certainly not intended and dangerous, and can be the cause of this error." : "") + (missingTempReason == null && name.indexOf(92) != -1 ? "\nWarning: The name contains backslash (\"\\\") instead of slash (\"/\"); template names should use slash only." : "");
            }

            normName = maybeTemp.getMissingTemplateNormalizedName();
            throw new TemplateNotFoundException(normName != null ? normName : name, customLookupCondition, msg);
         }
      } else {
         return temp;
      }
   }

   private boolean isKnownNonConfusingLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
      return templateLookupStrategy == TemplateLookupStrategy.DEFAULT_2_3_0;
   }

   private String removeInitialSlash(String name) {
      return name.startsWith("/") ? name.substring(1) : name;
   }

   private String ensureSentenceIsClosed(String s) {
      if (s != null && s.length() != 0) {
         char lastChar = s.charAt(s.length() - 1);
         return lastChar != '.' && lastChar != '!' && lastChar != '?' ? s + "." : s;
      } else {
         return s;
      }
   }

   public void setDefaultEncoding(String encoding) {
      this.defaultEncoding = encoding;
      this.defaultEncodingExplicitlySet = true;
   }

   public String getDefaultEncoding() {
      return this.defaultEncoding;
   }

   public void unsetDefaultEncoding() {
      if (this.defaultEncodingExplicitlySet) {
         this.setDefaultEncoding(getDefaultDefaultEncoding());
         this.defaultEncodingExplicitlySet = false;
      }

   }

   public boolean isDefaultEncodingExplicitlySet() {
      return this.defaultEncodingExplicitlySet;
   }

   private static String getDefaultDefaultEncoding() {
      return getJVMDefaultEncoding();
   }

   private static String getJVMDefaultEncoding() {
      return SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
   }

   public String getEncoding(Locale locale) {
      if (this.localeToCharsetMap.isEmpty()) {
         return this.defaultEncoding;
      } else {
         NullArgumentException.check("locale", locale);
         String charset = (String)this.localeToCharsetMap.get(locale.toString());
         if (charset == null) {
            if (locale.getVariant().length() > 0) {
               Locale l = new Locale(locale.getLanguage(), locale.getCountry());
               charset = (String)this.localeToCharsetMap.get(l.toString());
               if (charset != null) {
                  this.localeToCharsetMap.put(locale.toString(), charset);
               }
            }

            charset = (String)this.localeToCharsetMap.get(locale.getLanguage());
            if (charset != null) {
               this.localeToCharsetMap.put(locale.toString(), charset);
            }
         }

         return charset != null ? charset : this.defaultEncoding;
      }
   }

   public void setEncoding(Locale locale, String encoding) {
      this.localeToCharsetMap.put(locale.toString(), encoding);
   }

   public void setSharedVariable(String name, TemplateModel tm) {
      Object replaced = this.sharedVariables.put(name, tm);
      if (replaced != null && this.rewrappableSharedVariables != null) {
         this.rewrappableSharedVariables.remove(name);
      }

   }

   public Set getSharedVariableNames() {
      return new HashSet(this.sharedVariables.keySet());
   }

   public void setSharedVariable(String name, Object value) throws TemplateModelException {
      this.setSharedVariable(name, this.getObjectWrapper().wrap(value));
   }

   public void setSharedVariables(Map<String, ?> map) throws TemplateModelException {
      this.rewrappableSharedVariables = new HashMap(map);
      this.sharedVariables.clear();
      this.setSharedVariablesFromRewrappableSharedVariables();
   }

   /** @deprecated */
   public void setSharedVaribles(Map map) throws TemplateModelException {
      this.setSharedVariables(map);
   }

   private void setSharedVariablesFromRewrappableSharedVariables() throws TemplateModelException {
      if (this.rewrappableSharedVariables != null) {
         String name;
         TemplateModel valueAsTM;
         for(Iterator it = this.rewrappableSharedVariables.entrySet().iterator(); it.hasNext(); this.sharedVariables.put(name, valueAsTM)) {
            Map.Entry ent = (Map.Entry)it.next();
            name = (String)ent.getKey();
            Object value = ent.getValue();
            if (value instanceof TemplateModel) {
               valueAsTM = (TemplateModel)value;
            } else {
               valueAsTM = this.getObjectWrapper().wrap(value);
            }
         }

      }
   }

   public void setAllSharedVariables(TemplateHashModelEx hash) throws TemplateModelException {
      TemplateModelIterator keys = hash.keys().iterator();
      TemplateModelIterator values = hash.values().iterator();

      while(keys.hasNext()) {
         this.setSharedVariable(((TemplateScalarModel)keys.next()).getAsString(), values.next());
      }

   }

   public TemplateModel getSharedVariable(String name) {
      return (TemplateModel)this.sharedVariables.get(name);
   }

   public void clearSharedVariables() {
      this.sharedVariables.clear();
      this.loadBuiltInSharedVariables();
   }

   public void clearTemplateCache() {
      this.cache.clear();
   }

   public void removeTemplateFromCache(String name) throws IOException {
      Locale loc = this.getLocale();
      this.removeTemplateFromCache(name, loc, (Object)null, this.getEncoding(loc), true);
   }

   public void removeTemplateFromCache(String name, Locale locale) throws IOException {
      this.removeTemplateFromCache(name, locale, (Object)null, this.getEncoding(locale), true);
   }

   public void removeTemplateFromCache(String name, String encoding) throws IOException {
      this.removeTemplateFromCache(name, this.getLocale(), (Object)null, encoding, true);
   }

   public void removeTemplateFromCache(String name, Locale locale, String encoding) throws IOException {
      this.removeTemplateFromCache(name, locale, (Object)null, encoding, true);
   }

   public void removeTemplateFromCache(String name, Locale locale, String encoding, boolean parse) throws IOException {
      this.removeTemplateFromCache(name, locale, (Object)null, encoding, parse);
   }

   public void removeTemplateFromCache(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
      this.cache.removeTemplate(name, locale, customLookupCondition, encoding, parse);
   }

   public boolean getLocalizedLookup() {
      return this.cache.getLocalizedLookup();
   }

   public void setLocalizedLookup(boolean localizedLookup) {
      this.localizedLookup = localizedLookup;
      this.cache.setLocalizedLookup(localizedLookup);
   }

   public void setSetting(String name, String value) throws TemplateException {
      boolean unknown = false;

      try {
         if ("TemplateUpdateInterval".equalsIgnoreCase(name)) {
            name = "template_update_delay";
         } else if ("DefaultEncoding".equalsIgnoreCase(name)) {
            name = "default_encoding";
         }

         if (!"default_encoding".equals(name) && !"defaultEncoding".equals(name)) {
            if (!"localized_lookup".equals(name) && !"localizedLookup".equals(name)) {
               if (!"strict_syntax".equals(name) && !"strictSyntax".equals(name)) {
                  if (!"whitespace_stripping".equals(name) && !"whitespaceStripping".equals(name)) {
                     if (!"auto_escaping_policy".equals(name) && !"autoEscapingPolicy".equals(name)) {
                        if (!"output_format".equals(name) && !"outputFormat".equals(name)) {
                           if (!"registered_custom_output_formats".equals(name) && !"registeredCustomOutputFormats".equals(name)) {
                              if (!"recognize_standard_file_extensions".equals(name) && !"recognizeStandardFileExtensions".equals(name)) {
                                 if (!"cache_storage".equals(name) && !"cacheStorage".equals(name)) {
                                    if (!"template_update_delay".equals(name) && !"templateUpdateDelay".equals(name)) {
                                       if (!"tag_syntax".equals(name) && !"tagSyntax".equals(name)) {
                                          if (!"interpolation_syntax".equals(name) && !"interpolationSyntax".equals(name)) {
                                             if (!"naming_convention".equals(name) && !"namingConvention".equals(name)) {
                                                if (!"tab_size".equals(name) && !"tabSize".equals(name)) {
                                                   if (!"incompatible_improvements".equals(name) && !"incompatibleImprovements".equals(name)) {
                                                      if ("incompatible_enhancements".equals(name)) {
                                                         this.setIncompatibleEnhancements(value);
                                                      } else if (!"template_loader".equals(name) && !"templateLoader".equals(name)) {
                                                         if (!"template_lookup_strategy".equals(name) && !"templateLookupStrategy".equals(name)) {
                                                            if (!"template_name_format".equals(name) && !"templateNameFormat".equals(name)) {
                                                               if (!"template_configurations".equals(name) && !"templateConfigurations".equals(name)) {
                                                                  if (!"fallback_on_null_loop_variable".equals(name) && !"fallbackOnNullLoopVariable".equals(name)) {
                                                                     unknown = true;
                                                                  } else {
                                                                     this.setFallbackOnNullLoopVariable(StringUtil.getYesNo(value));
                                                                  }
                                                               } else if (value.equals("null")) {
                                                                  this.setTemplateConfigurations((TemplateConfigurationFactory)null);
                                                               } else {
                                                                  this.setTemplateConfigurations((TemplateConfigurationFactory)_ObjectBuilderSettingEvaluator.eval(value, TemplateConfigurationFactory.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                                               }
                                                            } else if (value.equalsIgnoreCase("default")) {
                                                               this.unsetTemplateNameFormat();
                                                            } else if (value.equalsIgnoreCase("default_2_3_0")) {
                                                               this.setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_3_0);
                                                            } else {
                                                               if (!value.equalsIgnoreCase("default_2_4_0")) {
                                                                  throw this.invalidSettingValueException(name, value);
                                                               }

                                                               this.setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_4_0);
                                                            }
                                                         } else if (value.equalsIgnoreCase("default")) {
                                                            this.unsetTemplateLookupStrategy();
                                                         } else {
                                                            this.setTemplateLookupStrategy((TemplateLookupStrategy)_ObjectBuilderSettingEvaluator.eval(value, TemplateLookupStrategy.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                                         }
                                                      } else if (value.equalsIgnoreCase("default")) {
                                                         this.unsetTemplateLoader();
                                                      } else {
                                                         this.setTemplateLoader((TemplateLoader)_ObjectBuilderSettingEvaluator.eval(value, TemplateLoader.class, true, _SettingEvaluationEnvironment.getCurrent()));
                                                      }
                                                   } else {
                                                      this.setIncompatibleImprovements(new Version(value));
                                                   }
                                                } else {
                                                   this.setTabSize(Integer.parseInt(value));
                                                }
                                             } else if (!"auto_detect".equals(value) && !"autoDetect".equals(value)) {
                                                if ("legacy".equals(value)) {
                                                   this.setNamingConvention(11);
                                                } else {
                                                   if (!"camel_case".equals(value) && !"camelCase".equals(value)) {
                                                      throw this.invalidSettingValueException(name, value);
                                                   }

                                                   this.setNamingConvention(12);
                                                }
                                             } else {
                                                this.setNamingConvention(10);
                                             }
                                          } else if ("legacy".equals(value)) {
                                             this.setInterpolationSyntax(20);
                                          } else if ("dollar".equals(value)) {
                                             this.setInterpolationSyntax(21);
                                          } else {
                                             if (!"square_bracket".equals(value) && !"squareBracket".equals(value)) {
                                                throw this.invalidSettingValueException(name, value);
                                             }

                                             this.setInterpolationSyntax(22);
                                          }
                                       } else if (!"auto_detect".equals(value) && !"autoDetect".equals(value)) {
                                          if (!"angle_bracket".equals(value) && !"angleBracket".equals(value)) {
                                             if (!"square_bracket".equals(value) && !"squareBracket".equals(value)) {
                                                throw this.invalidSettingValueException(name, value);
                                             }

                                             this.setTagSyntax(2);
                                          } else {
                                             this.setTagSyntax(1);
                                          }
                                       } else {
                                          this.setTagSyntax(0);
                                       }
                                    } else {
                                       long multiplier;
                                       String valueWithoutUnit;
                                       if (value.endsWith("ms")) {
                                          multiplier = 1L;
                                          valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 2));
                                       } else if (value.endsWith("s")) {
                                          multiplier = 1000L;
                                          valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 1));
                                       } else if (value.endsWith("m")) {
                                          multiplier = 60000L;
                                          valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 1));
                                       } else if (value.endsWith("h")) {
                                          multiplier = 3600000L;
                                          valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 1));
                                       } else {
                                          multiplier = 1000L;
                                          valueWithoutUnit = value;
                                       }

                                       this.setTemplateUpdateDelayMilliseconds((long)Integer.parseInt(valueWithoutUnit) * multiplier);
                                    }
                                 } else {
                                    if (value.equalsIgnoreCase("default")) {
                                       this.unsetCacheStorage();
                                    }

                                    if (value.indexOf(46) == -1) {
                                       int strongSize = 0;
                                       int softSize = 0;
                                       Map map = StringUtil.parseNameValuePairList(value, String.valueOf(Integer.MAX_VALUE));
                                       Iterator it = map.entrySet().iterator();

                                       while(it.hasNext()) {
                                          Map.Entry ent = (Map.Entry)it.next();
                                          String pname = (String)ent.getKey();

                                          int pvalue;
                                          try {
                                             pvalue = Integer.parseInt((String)ent.getValue());
                                          } catch (NumberFormatException var12) {
                                             throw this.invalidSettingValueException(name, value);
                                          }

                                          if ("soft".equalsIgnoreCase(pname)) {
                                             softSize = pvalue;
                                          } else {
                                             if (!"strong".equalsIgnoreCase(pname)) {
                                                throw this.invalidSettingValueException(name, value);
                                             }

                                             strongSize = pvalue;
                                          }
                                       }

                                       if (softSize == 0 && strongSize == 0) {
                                          throw this.invalidSettingValueException(name, value);
                                       }

                                       this.setCacheStorage(new MruCacheStorage(strongSize, softSize));
                                    } else {
                                       this.setCacheStorage((CacheStorage)_ObjectBuilderSettingEvaluator.eval(value, CacheStorage.class, false, _SettingEvaluationEnvironment.getCurrent()));
                                    }
                                 }
                              } else if (value.equalsIgnoreCase("default")) {
                                 this.unsetRecognizeStandardFileExtensions();
                              } else {
                                 this.setRecognizeStandardFileExtensions(StringUtil.getYesNo(value));
                              }
                           } else {
                              List list = (List)_ObjectBuilderSettingEvaluator.eval(value, List.class, true, _SettingEvaluationEnvironment.getCurrent());
                              Iterator var5 = list.iterator();

                              while(var5.hasNext()) {
                                 Object item = var5.next();
                                 if (!(item instanceof OutputFormat)) {
                                    throw new _MiscTemplateException(this.getEnvironment(), new Object[]{"Invalid value for setting ", new _DelayedJQuote(name), ": List items must be " + OutputFormat.class.getName() + " instances, in: ", value});
                                 }
                              }

                              this.setRegisteredCustomOutputFormats(list);
                           }
                        } else if (value.equalsIgnoreCase("default")) {
                           this.unsetOutputFormat();
                        } else {
                           OutputFormat stdOF = (OutputFormat)STANDARD_OUTPUT_FORMATS.get(value);
                           this.setOutputFormat(stdOF != null ? stdOF : (OutputFormat)_ObjectBuilderSettingEvaluator.eval(value, OutputFormat.class, true, _SettingEvaluationEnvironment.getCurrent()));
                        }
                     } else if (!"enable_if_default".equals(value) && !"enableIfDefault".equals(value)) {
                        if (!"enable_if_supported".equals(value) && !"enableIfSupported".equals(value)) {
                           if (!"disable".equals(value)) {
                              throw this.invalidSettingValueException(name, value);
                           }

                           this.setAutoEscapingPolicy(20);
                        } else {
                           this.setAutoEscapingPolicy(22);
                        }
                     } else {
                        this.setAutoEscapingPolicy(21);
                     }
                  } else {
                     this.setWhitespaceStripping(StringUtil.getYesNo(value));
                  }
               } else {
                  this.setStrictSyntaxMode(StringUtil.getYesNo(value));
               }
            } else {
               this.setLocalizedLookup(StringUtil.getYesNo(value));
            }
         } else if ("JVM default".equalsIgnoreCase(value)) {
            this.setDefaultEncoding(getJVMDefaultEncoding());
         } else {
            this.setDefaultEncoding(value);
         }
      } catch (Exception var13) {
         throw this.settingValueAssignmentException(name, value, var13);
      }

      if (unknown) {
         super.setSetting(name, value);
      }

   }

   private String rightTrim(String s) {
      int ln;
      for(ln = s.length(); ln > 0 && Character.isWhitespace(s.charAt(ln - 1)); --ln) {
      }

      return s.substring(0, ln);
   }

   public Set<String> getSettingNames(boolean camelCase) {
      return new _UnmodifiableCompositeSet(super.getSettingNames(camelCase), new _SortedArraySet(camelCase ? SETTING_NAMES_CAMEL_CASE : SETTING_NAMES_SNAKE_CASE));
   }

   protected String getCorrectedNameForUnknownSetting(String name) {
      if (!"encoding".equals(name) && !"charset".equals(name) && !"default_charset".equals(name)) {
         return "defaultCharset".equals(name) ? "defaultEncoding" : super.getCorrectedNameForUnknownSetting(name);
      } else {
         return "default_encoding";
      }
   }

   protected void doAutoImportsAndIncludes(Environment env) throws TemplateException, IOException {
      Template t = env.getMainTemplate();
      this.doAutoImports(env, t);
      this.doAutoIncludes(env, t);
   }

   private void doAutoImports(Environment env, Template t) throws IOException, TemplateException {
      Map<String, String> envAutoImports = env.getAutoImportsWithoutFallback();
      Map<String, String> tAutoImports = t.getAutoImportsWithoutFallback();
      boolean lazyAutoImports = env.getLazyAutoImports() != null ? env.getLazyAutoImports() : env.getLazyImports();
      Iterator var6 = this.getAutoImportsWithoutFallback().entrySet().iterator();

      while(true) {
         Map.Entry autoImport;
         String nsVarName;
         do {
            do {
               if (!var6.hasNext()) {
                  if (tAutoImports != null) {
                     var6 = tAutoImports.entrySet().iterator();

                     label45:
                     while(true) {
                        do {
                           if (!var6.hasNext()) {
                              break label45;
                           }

                           autoImport = (Map.Entry)var6.next();
                           nsVarName = (String)autoImport.getKey();
                        } while(envAutoImports != null && envAutoImports.containsKey(nsVarName));

                        env.importLib((String)autoImport.getValue(), nsVarName, lazyAutoImports);
                     }
                  }

                  if (envAutoImports != null) {
                     var6 = envAutoImports.entrySet().iterator();

                     while(var6.hasNext()) {
                        autoImport = (Map.Entry)var6.next();
                        nsVarName = (String)autoImport.getKey();
                        env.importLib((String)autoImport.getValue(), nsVarName, lazyAutoImports);
                     }
                  }

                  return;
               }

               autoImport = (Map.Entry)var6.next();
               nsVarName = (String)autoImport.getKey();
            } while(tAutoImports != null && tAutoImports.containsKey(nsVarName));
         } while(envAutoImports != null && envAutoImports.containsKey(nsVarName));

         env.importLib((String)autoImport.getValue(), nsVarName, lazyAutoImports);
      }
   }

   private void doAutoIncludes(Environment env, Template t) throws TemplateException, IOException, TemplateNotFoundException, MalformedTemplateNameException, ParseException {
      List<String> tAutoIncludes = t.getAutoIncludesWithoutFallback();
      List<String> envAutoIncludes = env.getAutoIncludesWithoutFallback();
      Iterator var5 = this.getAutoIncludesWithoutFallback().iterator();

      while(true) {
         String templateName;
         do {
            do {
               if (!var5.hasNext()) {
                  if (tAutoIncludes != null) {
                     var5 = tAutoIncludes.iterator();

                     label40:
                     while(true) {
                        do {
                           if (!var5.hasNext()) {
                              break label40;
                           }

                           templateName = (String)var5.next();
                        } while(envAutoIncludes != null && envAutoIncludes.contains(templateName));

                        env.include(this.getTemplate(templateName, env.getLocale()));
                     }
                  }

                  if (envAutoIncludes != null) {
                     var5 = envAutoIncludes.iterator();

                     while(var5.hasNext()) {
                        templateName = (String)var5.next();
                        env.include(this.getTemplate(templateName, env.getLocale()));
                     }
                  }

                  return;
               }

               templateName = (String)var5.next();
            } while(tAutoIncludes != null && tAutoIncludes.contains(templateName));
         } while(envAutoIncludes != null && envAutoIncludes.contains(templateName));

         env.include(this.getTemplate(templateName, env.getLocale()));
      }
   }

   /** @deprecated */
   @Deprecated
   public static String getVersionNumber() {
      return VERSION.toString();
   }

   public static Version getVersion() {
      return VERSION;
   }

   public static ObjectWrapper getDefaultObjectWrapper(Version incompatibleImprovements) {
      return (ObjectWrapper)(incompatibleImprovements.intValue() < _TemplateAPI.VERSION_INT_2_3_21 ? ObjectWrapper.DEFAULT_WRAPPER : (new DefaultObjectWrapperBuilder(incompatibleImprovements)).build());
   }

   public Set getSupportedBuiltInNames() {
      return this.getSupportedBuiltInNames(this.getNamingConvention());
   }

   public Set<String> getSupportedBuiltInNames(int namingConvention) {
      return _CoreAPI.getSupportedBuiltInNames(namingConvention);
   }

   public Set getSupportedBuiltInDirectiveNames() {
      return this.getSupportedBuiltInDirectiveNames(this.getNamingConvention());
   }

   public Set<String> getSupportedBuiltInDirectiveNames(int namingConvention) {
      if (namingConvention == 10) {
         return _CoreAPI.ALL_BUILT_IN_DIRECTIVE_NAMES;
      } else if (namingConvention == 11) {
         return _CoreAPI.LEGACY_BUILT_IN_DIRECTIVE_NAMES;
      } else if (namingConvention == 12) {
         return _CoreAPI.CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;
      } else {
         throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
      }
   }

   private static String getRequiredVersionProperty(Properties vp, String properyName) {
      String s = vp.getProperty(properyName);
      if (s == null) {
         throw new RuntimeException("Version file is corrupt: \"" + properyName + "\" property is missing.");
      } else {
         return s;
      }
   }

   static {
      STANDARD_OUTPUT_FORMATS.put(UndefinedOutputFormat.INSTANCE.getName(), UndefinedOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(HTMLOutputFormat.INSTANCE.getName(), HTMLOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(XHTMLOutputFormat.INSTANCE.getName(), XHTMLOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(XMLOutputFormat.INSTANCE.getName(), XMLOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(RTFOutputFormat.INSTANCE.getName(), RTFOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(PlainTextOutputFormat.INSTANCE.getName(), PlainTextOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(CSSOutputFormat.INSTANCE.getName(), CSSOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(JavaScriptOutputFormat.INSTANCE.getName(), JavaScriptOutputFormat.INSTANCE);
      STANDARD_OUTPUT_FORMATS.put(JSONOutputFormat.INSTANCE.getName(), JSONOutputFormat.INSTANCE);
      VERSION_2_3_0 = new Version(2, 3, 0);
      VERSION_2_3_19 = new Version(2, 3, 19);
      VERSION_2_3_20 = new Version(2, 3, 20);
      VERSION_2_3_21 = new Version(2, 3, 21);
      VERSION_2_3_22 = new Version(2, 3, 22);
      VERSION_2_3_23 = new Version(2, 3, 23);
      VERSION_2_3_24 = new Version(2, 3, 24);
      VERSION_2_3_25 = new Version(2, 3, 25);
      VERSION_2_3_26 = new Version(2, 3, 26);
      VERSION_2_3_27 = new Version(2, 3, 27);
      VERSION_2_3_28 = new Version(2, 3, 28);
      VERSION_2_3_29 = new Version(2, 3, 29);
      VERSION_2_3_30 = new Version(2, 3, 30);
      VERSION_2_3_31 = new Version(2, 3, 31);
      DEFAULT_INCOMPATIBLE_IMPROVEMENTS = VERSION_2_3_0;
      DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.toString();
      PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.intValue();

      try {
         Properties props = ClassUtil.loadProperties(Configuration.class, "/freemarker/version.properties");
         String versionString = getRequiredVersionProperty(props, "version");
         String buildDateStr = getRequiredVersionProperty(props, "buildTimestamp");
         if (buildDateStr.endsWith("Z")) {
            buildDateStr = buildDateStr.substring(0, buildDateStr.length() - 1) + "+0000";
         }

         Date buildDate;
         try {
            buildDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)).parse(buildDateStr);
         } catch (java.text.ParseException var8) {
            buildDate = null;
         }

         Boolean gaeCompliant = Boolean.valueOf(getRequiredVersionProperty(props, "isGAECompliant"));
         VERSION = new Version(versionString, gaeCompliant, buildDate);
      } catch (IOException var9) {
         throw new RuntimeException("Failed to load and parse /freemarker/version.properties", var9);
      }

      boolean fm24detected;
      try {
         Class.forName("freemarker.core._2_4_OrLaterMarker");
         fm24detected = true;
      } catch (ClassNotFoundException var5) {
         fm24detected = false;
      } catch (LinkageError var6) {
         fm24detected = true;
      } catch (Throwable var7) {
         fm24detected = false;
      }

      FM_24_DETECTED = fm24detected;
      defaultConfigLock = new Object();
   }

   private static class DefaultSoftCacheStorage extends SoftCacheStorage {
      private DefaultSoftCacheStorage() {
      }

      // $FF: synthetic method
      DefaultSoftCacheStorage(Object x0) {
         this();
      }
   }

   private static class LegacyDefaultFileTemplateLoader extends FileTemplateLoader {
      public LegacyDefaultFileTemplateLoader() throws IOException {
      }
   }
}
