/*      */ package freemarker.template;
/*      */ 
/*      */ import freemarker.cache.CacheStorage;
/*      */ import freemarker.cache.ClassTemplateLoader;
/*      */ import freemarker.cache.FileTemplateLoader;
/*      */ import freemarker.cache.MruCacheStorage;
/*      */ import freemarker.cache.SoftCacheStorage;
/*      */ import freemarker.cache.TemplateCache;
/*      */ import freemarker.cache.TemplateConfigurationFactory;
/*      */ import freemarker.cache.TemplateLoader;
/*      */ import freemarker.cache.TemplateLookupStrategy;
/*      */ import freemarker.cache.TemplateNameFormat;
/*      */ import freemarker.core.BugException;
/*      */ import freemarker.core.CSSOutputFormat;
/*      */ import freemarker.core.CombinedMarkupOutputFormat;
/*      */ import freemarker.core.Configurable;
/*      */ import freemarker.core.Environment;
/*      */ import freemarker.core.HTMLOutputFormat;
/*      */ import freemarker.core.JSONOutputFormat;
/*      */ import freemarker.core.JavaScriptOutputFormat;
/*      */ import freemarker.core.MarkupOutputFormat;
/*      */ import freemarker.core.OutputFormat;
/*      */ import freemarker.core.ParseException;
/*      */ import freemarker.core.ParserConfiguration;
/*      */ import freemarker.core.PlainTextOutputFormat;
/*      */ import freemarker.core.RTFOutputFormat;
/*      */ import freemarker.core.UndefinedOutputFormat;
/*      */ import freemarker.core.UnregisteredOutputFormatException;
/*      */ import freemarker.core.XHTMLOutputFormat;
/*      */ import freemarker.core.XMLOutputFormat;
/*      */ import freemarker.core._CoreAPI;
/*      */ import freemarker.core._DelayedJQuote;
/*      */ import freemarker.core._MiscTemplateException;
/*      */ import freemarker.core._ObjectBuilderSettingEvaluator;
/*      */ import freemarker.core._SettingEvaluationEnvironment;
/*      */ import freemarker.core._SortedArraySet;
/*      */ import freemarker.core._UnmodifiableCompositeSet;
/*      */ import freemarker.log.Logger;
/*      */ import freemarker.template.utility.CaptureOutput;
/*      */ import freemarker.template.utility.ClassUtil;
/*      */ import freemarker.template.utility.HtmlEscape;
/*      */ import freemarker.template.utility.NormalizeNewlines;
/*      */ import freemarker.template.utility.NullArgumentException;
/*      */ import freemarker.template.utility.SecurityUtilities;
/*      */ import freemarker.template.utility.StandardCompress;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import freemarker.template.utility.XmlEscape;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Configuration
/*      */   extends Configurable
/*      */   implements Cloneable, ParserConfiguration
/*      */ {
/*  151 */   private static final Logger CACHE_LOG = Logger.getLogger("freemarker.cache");
/*      */ 
/*      */   
/*      */   private static final String VERSION_PROPERTIES_PATH = "/freemarker/version.properties";
/*      */ 
/*      */   
/*      */   public static final String DEFAULT_ENCODING_KEY_SNAKE_CASE = "default_encoding";
/*      */ 
/*      */   
/*      */   public static final String DEFAULT_ENCODING_KEY_CAMEL_CASE = "defaultEncoding";
/*      */ 
/*      */   
/*      */   public static final String DEFAULT_ENCODING_KEY = "default_encoding";
/*      */ 
/*      */   
/*      */   public static final String LOCALIZED_LOOKUP_KEY_SNAKE_CASE = "localized_lookup";
/*      */ 
/*      */   
/*      */   public static final String LOCALIZED_LOOKUP_KEY_CAMEL_CASE = "localizedLookup";
/*      */ 
/*      */   
/*      */   public static final String LOCALIZED_LOOKUP_KEY = "localized_lookup";
/*      */ 
/*      */   
/*      */   public static final String STRICT_SYNTAX_KEY_SNAKE_CASE = "strict_syntax";
/*      */ 
/*      */   
/*      */   public static final String STRICT_SYNTAX_KEY_CAMEL_CASE = "strictSyntax";
/*      */ 
/*      */   
/*      */   public static final String STRICT_SYNTAX_KEY = "strict_syntax";
/*      */ 
/*      */   
/*      */   public static final String WHITESPACE_STRIPPING_KEY_SNAKE_CASE = "whitespace_stripping";
/*      */ 
/*      */   
/*      */   public static final String WHITESPACE_STRIPPING_KEY_CAMEL_CASE = "whitespaceStripping";
/*      */ 
/*      */   
/*      */   public static final String WHITESPACE_STRIPPING_KEY = "whitespace_stripping";
/*      */ 
/*      */   
/*      */   public static final String OUTPUT_FORMAT_KEY_SNAKE_CASE = "output_format";
/*      */ 
/*      */   
/*      */   public static final String OUTPUT_FORMAT_KEY_CAMEL_CASE = "outputFormat";
/*      */ 
/*      */   
/*      */   public static final String OUTPUT_FORMAT_KEY = "output_format";
/*      */ 
/*      */   
/*      */   public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_SNAKE_CASE = "recognize_standard_file_extensions";
/*      */ 
/*      */   
/*      */   public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE = "recognizeStandardFileExtensions";
/*      */ 
/*      */   
/*      */   public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY = "recognize_standard_file_extensions";
/*      */ 
/*      */   
/*      */   public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_SNAKE_CASE = "registered_custom_output_formats";
/*      */ 
/*      */   
/*      */   public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE = "registeredCustomOutputFormats";
/*      */ 
/*      */   
/*      */   public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY = "registered_custom_output_formats";
/*      */ 
/*      */   
/*      */   public static final String AUTO_ESCAPING_POLICY_KEY_SNAKE_CASE = "auto_escaping_policy";
/*      */ 
/*      */   
/*      */   public static final String AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE = "autoEscapingPolicy";
/*      */ 
/*      */   
/*      */   public static final String AUTO_ESCAPING_POLICY_KEY = "auto_escaping_policy";
/*      */ 
/*      */   
/*      */   public static final String CACHE_STORAGE_KEY_SNAKE_CASE = "cache_storage";
/*      */ 
/*      */   
/*      */   public static final String CACHE_STORAGE_KEY_CAMEL_CASE = "cacheStorage";
/*      */ 
/*      */   
/*      */   public static final String CACHE_STORAGE_KEY = "cache_storage";
/*      */ 
/*      */   
/*      */   public static final String TEMPLATE_UPDATE_DELAY_KEY_SNAKE_CASE = "template_update_delay";
/*      */ 
/*      */   
/*      */   public static final String TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE = "templateUpdateDelay";
/*      */ 
/*      */   
/*      */   public static final String TEMPLATE_UPDATE_DELAY_KEY = "template_update_delay";
/*      */ 
/*      */   
/*      */   public static final String AUTO_IMPORT_KEY_SNAKE_CASE = "auto_import";
/*      */ 
/*      */   
/*      */   public static final String AUTO_IMPORT_KEY_CAMEL_CASE = "autoImport";
/*      */   
/*      */   public static final String AUTO_IMPORT_KEY = "auto_import";
/*      */   
/*      */   public static final String AUTO_INCLUDE_KEY_SNAKE_CASE = "auto_include";
/*      */   
/*      */   public static final String AUTO_INCLUDE_KEY_CAMEL_CASE = "autoInclude";
/*      */   
/*      */   public static final String AUTO_INCLUDE_KEY = "auto_include";
/*      */   
/*      */   public static final String TAG_SYNTAX_KEY_SNAKE_CASE = "tag_syntax";
/*      */   
/*      */   public static final String TAG_SYNTAX_KEY_CAMEL_CASE = "tagSyntax";
/*      */   
/*      */   public static final String TAG_SYNTAX_KEY = "tag_syntax";
/*      */   
/*      */   public static final String INTERPOLATION_SYNTAX_KEY_SNAKE_CASE = "interpolation_syntax";
/*      */   
/*      */   public static final String INTERPOLATION_SYNTAX_KEY_CAMEL_CASE = "interpolationSyntax";
/*      */   
/*      */   public static final String INTERPOLATION_SYNTAX_KEY = "interpolation_syntax";
/*      */   
/*      */   public static final String NAMING_CONVENTION_KEY_SNAKE_CASE = "naming_convention";
/*      */   
/*      */   public static final String NAMING_CONVENTION_KEY_CAMEL_CASE = "namingConvention";
/*      */   
/*      */   public static final String NAMING_CONVENTION_KEY = "naming_convention";
/*      */   
/*      */   public static final String TAB_SIZE_KEY_SNAKE_CASE = "tab_size";
/*      */   
/*      */   public static final String TAB_SIZE_KEY_CAMEL_CASE = "tabSize";
/*      */   
/*      */   public static final String TAB_SIZE_KEY = "tab_size";
/*      */   
/*      */   public static final String TEMPLATE_LOADER_KEY_SNAKE_CASE = "template_loader";
/*      */   
/*      */   public static final String TEMPLATE_LOADER_KEY_CAMEL_CASE = "templateLoader";
/*      */   
/*      */   public static final String TEMPLATE_LOADER_KEY = "template_loader";
/*      */   
/*      */   public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_SNAKE_CASE = "template_lookup_strategy";
/*      */   
/*      */   public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE = "templateLookupStrategy";
/*      */   
/*      */   public static final String TEMPLATE_LOOKUP_STRATEGY_KEY = "template_lookup_strategy";
/*      */   
/*      */   public static final String TEMPLATE_NAME_FORMAT_KEY_SNAKE_CASE = "template_name_format";
/*      */   
/*      */   public static final String TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE = "templateNameFormat";
/*      */   
/*      */   public static final String TEMPLATE_NAME_FORMAT_KEY = "template_name_format";
/*      */   
/*      */   public static final String TEMPLATE_CONFIGURATIONS_KEY_SNAKE_CASE = "template_configurations";
/*      */   
/*      */   public static final String TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE = "templateConfigurations";
/*      */   
/*      */   public static final String TEMPLATE_CONFIGURATIONS_KEY = "template_configurations";
/*      */   
/*      */   public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_SNAKE_CASE = "incompatible_improvements";
/*      */   
/*      */   public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE = "incompatibleImprovements";
/*      */   
/*      */   public static final String INCOMPATIBLE_IMPROVEMENTS_KEY = "incompatible_improvements";
/*      */   
/*      */   @Deprecated
/*      */   public static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";
/*      */   
/*      */   @Deprecated
/*      */   public static final String INCOMPATIBLE_ENHANCEMENTS = "incompatible_enhancements";
/*      */   
/*      */   public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_SNAKE_CASE = "fallback_on_null_loop_variable";
/*      */   
/*      */   public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE = "fallbackOnNullLoopVariable";
/*      */   
/*      */   public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY = "fallback_on_null_loop_variable";
/*      */   
/*  326 */   private static final String[] SETTING_NAMES_SNAKE_CASE = new String[] { "auto_escaping_policy", "cache_storage", "default_encoding", "fallback_on_null_loop_variable", "incompatible_improvements", "interpolation_syntax", "localized_lookup", "naming_convention", "output_format", "recognize_standard_file_extensions", "registered_custom_output_formats", "strict_syntax", "tab_size", "tag_syntax", "template_configurations", "template_loader", "template_lookup_strategy", "template_name_format", "template_update_delay", "whitespace_stripping" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  350 */   private static final String[] SETTING_NAMES_CAMEL_CASE = new String[] { "autoEscapingPolicy", "cacheStorage", "defaultEncoding", "fallbackOnNullLoopVariable", "incompatibleImprovements", "interpolationSyntax", "localizedLookup", "namingConvention", "outputFormat", "recognizeStandardFileExtensions", "registeredCustomOutputFormats", "strictSyntax", "tabSize", "tagSyntax", "templateConfigurations", "templateLoader", "templateLookupStrategy", "templateNameFormat", "templateUpdateDelay", "whitespaceStripping" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  376 */   private static final Map<String, OutputFormat> STANDARD_OUTPUT_FORMATS = new HashMap<>(); static {
/*  377 */     STANDARD_OUTPUT_FORMATS.put(UndefinedOutputFormat.INSTANCE.getName(), UndefinedOutputFormat.INSTANCE);
/*  378 */     STANDARD_OUTPUT_FORMATS.put(HTMLOutputFormat.INSTANCE.getName(), HTMLOutputFormat.INSTANCE);
/*  379 */     STANDARD_OUTPUT_FORMATS.put(XHTMLOutputFormat.INSTANCE.getName(), XHTMLOutputFormat.INSTANCE);
/*  380 */     STANDARD_OUTPUT_FORMATS.put(XMLOutputFormat.INSTANCE.getName(), XMLOutputFormat.INSTANCE);
/*  381 */     STANDARD_OUTPUT_FORMATS.put(RTFOutputFormat.INSTANCE.getName(), RTFOutputFormat.INSTANCE);
/*  382 */     STANDARD_OUTPUT_FORMATS.put(PlainTextOutputFormat.INSTANCE.getName(), PlainTextOutputFormat.INSTANCE);
/*  383 */     STANDARD_OUTPUT_FORMATS.put(CSSOutputFormat.INSTANCE.getName(), CSSOutputFormat.INSTANCE);
/*  384 */     STANDARD_OUTPUT_FORMATS.put(JavaScriptOutputFormat.INSTANCE.getName(), JavaScriptOutputFormat.INSTANCE);
/*  385 */     STANDARD_OUTPUT_FORMATS.put(JSONOutputFormat.INSTANCE.getName(), JSONOutputFormat.INSTANCE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int AUTO_DETECT_TAG_SYNTAX = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ANGLE_BRACKET_TAG_SYNTAX = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SQUARE_BRACKET_TAG_SYNTAX = 2;
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int LEGACY_INTERPOLATION_SYNTAX = 20;
/*      */ 
/*      */   
/*      */   public static final int DOLLAR_INTERPOLATION_SYNTAX = 21;
/*      */ 
/*      */   
/*      */   public static final int SQUARE_BRACKET_INTERPOLATION_SYNTAX = 22;
/*      */ 
/*      */   
/*      */   public static final int AUTO_DETECT_NAMING_CONVENTION = 10;
/*      */ 
/*      */   
/*      */   public static final int LEGACY_NAMING_CONVENTION = 11;
/*      */ 
/*      */   
/*      */   public static final int CAMEL_CASE_NAMING_CONVENTION = 12;
/*      */ 
/*      */   
/*      */   public static final int DISABLE_AUTO_ESCAPING_POLICY = 20;
/*      */ 
/*      */   
/*      */   public static final int ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY = 21;
/*      */ 
/*      */   
/*      */   public static final int ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY = 22;
/*      */ 
/*      */ 
/*      */   
/*      */   static {
/*      */     boolean fm24detected;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*  437 */   public static final Version VERSION_2_3_0 = new Version(2, 3, 0);
/*      */ 
/*      */   
/*  440 */   public static final Version VERSION_2_3_19 = new Version(2, 3, 19);
/*      */ 
/*      */   
/*  443 */   public static final Version VERSION_2_3_20 = new Version(2, 3, 20);
/*      */ 
/*      */   
/*  446 */   public static final Version VERSION_2_3_21 = new Version(2, 3, 21);
/*      */ 
/*      */   
/*  449 */   public static final Version VERSION_2_3_22 = new Version(2, 3, 22);
/*      */ 
/*      */   
/*  452 */   public static final Version VERSION_2_3_23 = new Version(2, 3, 23);
/*      */ 
/*      */   
/*  455 */   public static final Version VERSION_2_3_24 = new Version(2, 3, 24);
/*      */ 
/*      */   
/*  458 */   public static final Version VERSION_2_3_25 = new Version(2, 3, 25);
/*      */ 
/*      */   
/*  461 */   public static final Version VERSION_2_3_26 = new Version(2, 3, 26);
/*      */ 
/*      */   
/*  464 */   public static final Version VERSION_2_3_27 = new Version(2, 3, 27);
/*      */ 
/*      */   
/*  467 */   public static final Version VERSION_2_3_28 = new Version(2, 3, 28);
/*      */ 
/*      */   
/*  470 */   public static final Version VERSION_2_3_29 = new Version(2, 3, 29);
/*      */ 
/*      */   
/*  473 */   public static final Version VERSION_2_3_30 = new Version(2, 3, 30);
/*      */ 
/*      */   
/*  476 */   public static final Version VERSION_2_3_31 = new Version(2, 3, 31);
/*      */ 
/*      */   
/*  479 */   public static final Version DEFAULT_INCOMPATIBLE_IMPROVEMENTS = VERSION_2_3_0;
/*      */   
/*      */   @Deprecated
/*  482 */   public static final String DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.toString();
/*      */   
/*      */   @Deprecated
/*  485 */   public static final int PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.intValue(); private static final String NULL = "null"; private static final String DEFAULT = "default";
/*      */   private static final String JVM_DEFAULT = "JVM default";
/*      */   private static final Version VERSION;
/*      */   private static final String FM_24_DETECTION_CLASS_NAME = "freemarker.core._2_4_OrLaterMarker";
/*      */   private static final boolean FM_24_DETECTED;
/*      */   
/*      */   static {
/*      */     try {
/*      */       Date buildDate;
/*  494 */       Properties props = ClassUtil.loadProperties(Configuration.class, "/freemarker/version.properties");
/*      */       
/*  496 */       String versionString = getRequiredVersionProperty(props, "version");
/*      */ 
/*      */ 
/*      */       
/*  500 */       String buildDateStr = getRequiredVersionProperty(props, "buildTimestamp");
/*  501 */       if (buildDateStr.endsWith("Z")) {
/*  502 */         buildDateStr = buildDateStr.substring(0, buildDateStr.length() - 1) + "+0000";
/*      */       }
/*      */       try {
/*  505 */         buildDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)).parse(buildDateStr);
/*  506 */       } catch (ParseException e) {
/*  507 */         buildDate = null;
/*      */       } 
/*      */ 
/*      */       
/*  511 */       Boolean gaeCompliant = Boolean.valueOf(getRequiredVersionProperty(props, "isGAECompliant"));
/*      */       
/*  513 */       VERSION = new Version(versionString, gaeCompliant, buildDate);
/*  514 */     } catch (IOException e) {
/*  515 */       throw new RuntimeException("Failed to load and parse /freemarker/version.properties", e);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  524 */       Class.forName("freemarker.core._2_4_OrLaterMarker");
/*  525 */       fm24detected = true;
/*  526 */     } catch (ClassNotFoundException e) {
/*  527 */       fm24detected = false;
/*  528 */     } catch (LinkageError e) {
/*  529 */       fm24detected = true;
/*  530 */     } catch (Throwable e) {
/*      */       
/*  532 */       fm24detected = false;
/*      */     } 
/*  534 */     FM_24_DETECTED = fm24detected;
/*      */   }
/*      */   
/*  537 */   private static final Object defaultConfigLock = new Object();
/*      */   
/*      */   private static volatile Configuration defaultConfig;
/*      */   
/*      */   private boolean strictSyntax = true;
/*      */   private volatile boolean localizedLookup = true;
/*      */   private boolean whitespaceStripping = true;
/*  544 */   private int autoEscapingPolicy = 21;
/*  545 */   private OutputFormat outputFormat = (OutputFormat)UndefinedOutputFormat.INSTANCE;
/*      */   private boolean outputFormatExplicitlySet;
/*      */   private Boolean recognizeStandardFileExtensions;
/*  548 */   private Map<String, ? extends OutputFormat> registeredCustomOutputFormats = Collections.emptyMap();
/*      */   private Version incompatibleImprovements;
/*  550 */   private int tagSyntax = 1;
/*  551 */   private int interpolationSyntax = 20;
/*  552 */   private int namingConvention = 10;
/*  553 */   private int tabSize = 8;
/*      */   
/*      */   private boolean fallbackOnNullLoopVariable = true;
/*      */   
/*      */   private boolean preventStrippings;
/*      */   
/*      */   private TemplateCache cache;
/*      */   
/*      */   private boolean templateLoaderExplicitlySet;
/*      */   
/*      */   private boolean templateLookupStrategyExplicitlySet;
/*      */   private boolean templateNameFormatExplicitlySet;
/*      */   private boolean cacheStorageExplicitlySet;
/*      */   private boolean objectWrapperExplicitlySet;
/*      */   private boolean templateExceptionHandlerExplicitlySet;
/*      */   private boolean attemptExceptionReporterExplicitlySet;
/*      */   private boolean logTemplateExceptionsExplicitlySet;
/*      */   private boolean wrapUncheckedExceptionsExplicitlySet;
/*      */   private boolean localeExplicitlySet;
/*      */   private boolean defaultEncodingExplicitlySet;
/*      */   private boolean timeZoneExplicitlySet;
/*  574 */   private HashMap sharedVariables = new HashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  581 */   private HashMap rewrappableSharedVariables = null;
/*      */   
/*  583 */   private String defaultEncoding = getDefaultDefaultEncoding();
/*  584 */   private ConcurrentMap localeToCharsetMap = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Configuration() {
/*  595 */     this(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
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
/*      */   public Configuration(Version incompatibleImprovements) {
/*  954 */     super(incompatibleImprovements);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  959 */     checkFreeMarkerVersionClash();
/*      */     
/*  961 */     NullArgumentException.check("incompatibleImprovements", incompatibleImprovements);
/*  962 */     checkCurrentVersionNotRecycled(incompatibleImprovements);
/*  963 */     this.incompatibleImprovements = incompatibleImprovements;
/*      */     
/*  965 */     createTemplateCache();
/*  966 */     loadBuiltInSharedVariables();
/*      */   }
/*      */   
/*      */   private static void checkFreeMarkerVersionClash() {
/*  970 */     if (FM_24_DETECTED) {
/*  971 */       throw new RuntimeException("Clashing FreeMarker versions (" + VERSION + " and some post-2.3.x) detected: found post-2.3.x class " + "freemarker.core._2_4_OrLaterMarker" + ". You probably have two different freemarker.jar-s in the classpath.");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void createTemplateCache() {
/*  978 */     this
/*      */ 
/*      */ 
/*      */       
/*  982 */       .cache = new TemplateCache(getDefaultTemplateLoader(), getDefaultCacheStorage(), getDefaultTemplateLookupStrategy(), getDefaultTemplateNameFormat(), null, this);
/*      */ 
/*      */     
/*  985 */     this.cache.clear();
/*  986 */     this.cache.setDelay(5000L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void recreateTemplateCacheWith(TemplateLoader loader, CacheStorage storage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations) {
/*  993 */     TemplateCache oldCache = this.cache;
/*  994 */     this.cache = new TemplateCache(loader, storage, templateLookupStrategy, templateNameFormat, templateConfigurations, this);
/*      */     
/*  996 */     this.cache.clear();
/*  997 */     this.cache.setDelay(oldCache.getDelay());
/*  998 */     this.cache.setLocalizedLookup(this.localizedLookup);
/*      */   }
/*      */   
/*      */   private void recreateTemplateCache() {
/* 1002 */     recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache
/* 1003 */         .getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), 
/* 1004 */         getTemplateConfigurations());
/*      */   }
/*      */   
/*      */   private TemplateLoader getDefaultTemplateLoader() {
/* 1008 */     return createDefaultTemplateLoader(getIncompatibleImprovements(), getTemplateLoader());
/*      */   }
/*      */   
/*      */   static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements) {
/* 1012 */     return createDefaultTemplateLoader(incompatibleImprovements, (TemplateLoader)null);
/*      */   }
/*      */ 
/*      */   
/*      */   private static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements, TemplateLoader existingTemplateLoader) {
/* 1017 */     if (incompatibleImprovements.intValue() < _TemplateAPI.VERSION_INT_2_3_21) {
/* 1018 */       if (existingTemplateLoader instanceof LegacyDefaultFileTemplateLoader) {
/* 1019 */         return existingTemplateLoader;
/*      */       }
/*      */       try {
/* 1022 */         return (TemplateLoader)new LegacyDefaultFileTemplateLoader();
/* 1023 */       } catch (Exception e) {
/* 1024 */         CACHE_LOG.warn("Couldn't create legacy default TemplateLoader which accesses the current directory. (Use new Configuration(Configuration.VERSION_2_3_21) or higher to avoid this.)", e);
/*      */         
/* 1026 */         return null;
/*      */       } 
/*      */     } 
/* 1029 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static class LegacyDefaultFileTemplateLoader
/*      */     extends FileTemplateLoader {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TemplateLookupStrategy getDefaultTemplateLookupStrategy() {
/* 1042 */     return getDefaultTemplateLookupStrategy(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   static TemplateLookupStrategy getDefaultTemplateLookupStrategy(Version incompatibleImprovements) {
/* 1046 */     return TemplateLookupStrategy.DEFAULT_2_3_0;
/*      */   }
/*      */   
/*      */   private TemplateNameFormat getDefaultTemplateNameFormat() {
/* 1050 */     return getDefaultTemplateNameFormat(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   static TemplateNameFormat getDefaultTemplateNameFormat(Version incompatibleImprovements) {
/* 1054 */     return TemplateNameFormat.DEFAULT_2_3_0;
/*      */   }
/*      */   
/*      */   private CacheStorage getDefaultCacheStorage() {
/* 1058 */     return createDefaultCacheStorage(getIncompatibleImprovements(), getCacheStorage());
/*      */   }
/*      */   
/*      */   static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements, CacheStorage existingCacheStorage) {
/* 1062 */     if (existingCacheStorage instanceof DefaultSoftCacheStorage) {
/* 1063 */       return existingCacheStorage;
/*      */     }
/* 1065 */     return (CacheStorage)new DefaultSoftCacheStorage();
/*      */   }
/*      */   
/*      */   static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements) {
/* 1069 */     return createDefaultCacheStorage(incompatibleImprovements, (CacheStorage)null);
/*      */   }
/*      */   
/*      */   private static class DefaultSoftCacheStorage extends SoftCacheStorage {
/*      */     private DefaultSoftCacheStorage() {}
/*      */   }
/*      */   
/*      */   private TemplateExceptionHandler getDefaultTemplateExceptionHandler() {
/* 1077 */     return getDefaultTemplateExceptionHandler(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   private AttemptExceptionReporter getDefaultAttemptExceptionReporter() {
/* 1081 */     return getDefaultAttemptExceptionReporter(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   private boolean getDefaultLogTemplateExceptions() {
/* 1085 */     return getDefaultLogTemplateExceptions(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   private boolean getDefaultWrapUncheckedExceptions() {
/* 1089 */     return getDefaultWrapUncheckedExceptions(getIncompatibleImprovements());
/*      */   }
/*      */   
/*      */   private ObjectWrapper getDefaultObjectWrapper() {
/* 1093 */     return getDefaultObjectWrapper(getIncompatibleImprovements());
/*      */   }
/*      */ 
/*      */   
/*      */   static TemplateExceptionHandler getDefaultTemplateExceptionHandler(Version incompatibleImprovements) {
/* 1098 */     return TemplateExceptionHandler.DEBUG_HANDLER;
/*      */   }
/*      */ 
/*      */   
/*      */   static AttemptExceptionReporter getDefaultAttemptExceptionReporter(Version incompatibleImprovements) {
/* 1103 */     return AttemptExceptionReporter.LOG_ERROR_REPORTER;
/*      */   }
/*      */ 
/*      */   
/*      */   static boolean getDefaultLogTemplateExceptions(Version incompatibleImprovements) {
/* 1108 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   static boolean getDefaultWrapUncheckedExceptions(Version incompatibleImprovements) {
/* 1113 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public Object clone() {
/*      */     try {
/* 1119 */       Configuration copy = (Configuration)super.clone();
/* 1120 */       copy.sharedVariables = new HashMap<>(this.sharedVariables);
/* 1121 */       copy.localeToCharsetMap = new ConcurrentHashMap<>(this.localeToCharsetMap);
/* 1122 */       copy.recreateTemplateCacheWith(this.cache
/* 1123 */           .getTemplateLoader(), this.cache.getCacheStorage(), this.cache
/* 1124 */           .getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache
/* 1125 */           .getTemplateConfigurations());
/* 1126 */       return copy;
/* 1127 */     } catch (CloneNotSupportedException e) {
/* 1128 */       throw new BugException("Cloning failed", e);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void loadBuiltInSharedVariables() {
/* 1133 */     this.sharedVariables.put("capture_output", new CaptureOutput());
/* 1134 */     this.sharedVariables.put("compress", StandardCompress.INSTANCE);
/* 1135 */     this.sharedVariables.put("html_escape", new HtmlEscape());
/* 1136 */     this.sharedVariables.put("normalize_newlines", new NormalizeNewlines());
/* 1137 */     this.sharedVariables.put("xml_escape", new XmlEscape());
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
/*      */   public void loadBuiltInEncodingMap() {
/* 1194 */     this.localeToCharsetMap.clear();
/* 1195 */     this.localeToCharsetMap.put("ar", "ISO-8859-6");
/* 1196 */     this.localeToCharsetMap.put("be", "ISO-8859-5");
/* 1197 */     this.localeToCharsetMap.put("bg", "ISO-8859-5");
/* 1198 */     this.localeToCharsetMap.put("ca", "ISO-8859-1");
/* 1199 */     this.localeToCharsetMap.put("cs", "ISO-8859-2");
/* 1200 */     this.localeToCharsetMap.put("da", "ISO-8859-1");
/* 1201 */     this.localeToCharsetMap.put("de", "ISO-8859-1");
/* 1202 */     this.localeToCharsetMap.put("el", "ISO-8859-7");
/* 1203 */     this.localeToCharsetMap.put("en", "ISO-8859-1");
/* 1204 */     this.localeToCharsetMap.put("es", "ISO-8859-1");
/* 1205 */     this.localeToCharsetMap.put("et", "ISO-8859-1");
/* 1206 */     this.localeToCharsetMap.put("fi", "ISO-8859-1");
/* 1207 */     this.localeToCharsetMap.put("fr", "ISO-8859-1");
/* 1208 */     this.localeToCharsetMap.put("hr", "ISO-8859-2");
/* 1209 */     this.localeToCharsetMap.put("hu", "ISO-8859-2");
/* 1210 */     this.localeToCharsetMap.put("is", "ISO-8859-1");
/* 1211 */     this.localeToCharsetMap.put("it", "ISO-8859-1");
/* 1212 */     this.localeToCharsetMap.put("iw", "ISO-8859-8");
/* 1213 */     this.localeToCharsetMap.put("ja", "Shift_JIS");
/* 1214 */     this.localeToCharsetMap.put("ko", "EUC-KR");
/* 1215 */     this.localeToCharsetMap.put("lt", "ISO-8859-2");
/* 1216 */     this.localeToCharsetMap.put("lv", "ISO-8859-2");
/* 1217 */     this.localeToCharsetMap.put("mk", "ISO-8859-5");
/* 1218 */     this.localeToCharsetMap.put("nl", "ISO-8859-1");
/* 1219 */     this.localeToCharsetMap.put("no", "ISO-8859-1");
/* 1220 */     this.localeToCharsetMap.put("pl", "ISO-8859-2");
/* 1221 */     this.localeToCharsetMap.put("pt", "ISO-8859-1");
/* 1222 */     this.localeToCharsetMap.put("ro", "ISO-8859-2");
/* 1223 */     this.localeToCharsetMap.put("ru", "ISO-8859-5");
/* 1224 */     this.localeToCharsetMap.put("sh", "ISO-8859-5");
/* 1225 */     this.localeToCharsetMap.put("sk", "ISO-8859-2");
/* 1226 */     this.localeToCharsetMap.put("sl", "ISO-8859-2");
/* 1227 */     this.localeToCharsetMap.put("sq", "ISO-8859-2");
/* 1228 */     this.localeToCharsetMap.put("sr", "ISO-8859-5");
/* 1229 */     this.localeToCharsetMap.put("sv", "ISO-8859-1");
/* 1230 */     this.localeToCharsetMap.put("tr", "ISO-8859-9");
/* 1231 */     this.localeToCharsetMap.put("uk", "ISO-8859-5");
/* 1232 */     this.localeToCharsetMap.put("zh", "GB2312");
/* 1233 */     this.localeToCharsetMap.put("zh_TW", "Big5");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearEncodingMap() {
/* 1242 */     this.localeToCharsetMap.clear();
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
/*      */   @Deprecated
/*      */   public static Configuration getDefaultConfiguration() {
/* 1262 */     Configuration defaultConfig = Configuration.defaultConfig;
/* 1263 */     if (defaultConfig == null) {
/* 1264 */       synchronized (defaultConfigLock) {
/* 1265 */         defaultConfig = Configuration.defaultConfig;
/* 1266 */         if (defaultConfig == null) {
/* 1267 */           defaultConfig = new Configuration();
/* 1268 */           Configuration.defaultConfig = defaultConfig;
/*      */         } 
/*      */       } 
/*      */     }
/* 1272 */     return defaultConfig;
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
/*      */   @Deprecated
/*      */   public static void setDefaultConfiguration(Configuration config) {
/* 1285 */     synchronized (defaultConfigLock) {
/* 1286 */       defaultConfig = config;
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
/*      */   public void setTemplateLoader(TemplateLoader templateLoader) {
/* 1311 */     synchronized (this) {
/* 1312 */       if (this.cache.getTemplateLoader() != templateLoader) {
/* 1313 */         recreateTemplateCacheWith(templateLoader, this.cache.getCacheStorage(), this.cache
/* 1314 */             .getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache
/* 1315 */             .getTemplateConfigurations());
/*      */       }
/* 1317 */       this.templateLoaderExplicitlySet = true;
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
/*      */   public void unsetTemplateLoader() {
/* 1329 */     if (this.templateLoaderExplicitlySet) {
/* 1330 */       setTemplateLoader(getDefaultTemplateLoader());
/* 1331 */       this.templateLoaderExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTemplateLoaderExplicitlySet() {
/* 1341 */     return this.templateLoaderExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateLoader getTemplateLoader() {
/* 1348 */     if (this.cache == null) {
/* 1349 */       return null;
/*      */     }
/* 1351 */     return this.cache.getTemplateLoader();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTemplateLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
/* 1361 */     if (this.cache.getTemplateLookupStrategy() != templateLookupStrategy) {
/* 1362 */       recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), templateLookupStrategy, this.cache
/* 1363 */           .getTemplateNameFormat(), this.cache
/* 1364 */           .getTemplateConfigurations());
/*      */     }
/* 1366 */     this.templateLookupStrategyExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetTemplateLookupStrategy() {
/* 1377 */     if (this.templateLookupStrategyExplicitlySet) {
/* 1378 */       setTemplateLookupStrategy(getDefaultTemplateLookupStrategy());
/* 1379 */       this.templateLookupStrategyExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTemplateLookupStrategyExplicitlySet() {
/* 1390 */     return this.templateLookupStrategyExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateLookupStrategy getTemplateLookupStrategy() {
/* 1397 */     if (this.cache == null) {
/* 1398 */       return null;
/*      */     }
/* 1400 */     return this.cache.getTemplateLookupStrategy();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTemplateNameFormat(TemplateNameFormat templateNameFormat) {
/* 1410 */     if (this.cache.getTemplateNameFormat() != templateNameFormat) {
/* 1411 */       recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache
/* 1412 */           .getTemplateLookupStrategy(), templateNameFormat, this.cache
/* 1413 */           .getTemplateConfigurations());
/*      */     }
/* 1415 */     this.templateNameFormatExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetTemplateNameFormat() {
/* 1426 */     if (this.templateNameFormatExplicitlySet) {
/* 1427 */       setTemplateNameFormat(getDefaultTemplateNameFormat());
/* 1428 */       this.templateNameFormatExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTemplateNameFormatExplicitlySet() {
/* 1438 */     return this.templateNameFormatExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateNameFormat getTemplateNameFormat() {
/* 1445 */     if (this.cache == null) {
/* 1446 */       return null;
/*      */     }
/* 1448 */     return this.cache.getTemplateNameFormat();
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
/*      */   public void setTemplateConfigurations(TemplateConfigurationFactory templateConfigurations) {
/* 1466 */     if (this.cache.getTemplateConfigurations() != templateConfigurations) {
/* 1467 */       if (templateConfigurations != null) {
/* 1468 */         templateConfigurations.setConfiguration(this);
/*      */       }
/* 1470 */       recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache
/* 1471 */           .getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), templateConfigurations);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateConfigurationFactory getTemplateConfigurations() {
/* 1480 */     if (this.cache == null) {
/* 1481 */       return null;
/*      */     }
/* 1483 */     return this.cache.getTemplateConfigurations();
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
/*      */   public void setCacheStorage(CacheStorage cacheStorage) {
/* 1500 */     synchronized (this) {
/* 1501 */       if (getCacheStorage() != cacheStorage) {
/* 1502 */         recreateTemplateCacheWith(this.cache.getTemplateLoader(), cacheStorage, this.cache
/* 1503 */             .getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache
/* 1504 */             .getTemplateConfigurations());
/*      */       }
/* 1506 */       this.cacheStorageExplicitlySet = true;
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
/*      */   public void unsetCacheStorage() {
/* 1518 */     if (this.cacheStorageExplicitlySet) {
/* 1519 */       setCacheStorage(getDefaultCacheStorage());
/* 1520 */       this.cacheStorageExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCacheStorageExplicitlySet() {
/* 1530 */     return this.cacheStorageExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CacheStorage getCacheStorage() {
/* 1540 */     synchronized (this) {
/* 1541 */       if (this.cache == null) {
/* 1542 */         return null;
/*      */       }
/* 1544 */       return this.cache.getCacheStorage();
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
/*      */   public void setDirectoryForTemplateLoading(File dir) throws IOException {
/* 1565 */     TemplateLoader tl = getTemplateLoader();
/* 1566 */     if (tl instanceof FileTemplateLoader) {
/* 1567 */       String path = ((FileTemplateLoader)tl).baseDir.getCanonicalPath();
/* 1568 */       if (path.equals(dir.getCanonicalPath()))
/*      */         return; 
/*      */     } 
/* 1571 */     setTemplateLoader((TemplateLoader)new FileTemplateLoader(dir));
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
/*      */   public void setServletContextForTemplateLoading(Object servletContext, String path) {
/*      */     try {
/*      */       Class[] constructorParamTypes;
/*      */       Object[] constructorParams;
/* 1590 */       Class<TemplateLoader> webappTemplateLoaderClass = ClassUtil.forName("freemarker.cache.WebappTemplateLoader");
/*      */ 
/*      */       
/* 1593 */       Class servletContextClass = ClassUtil.forName("javax.servlet.ServletContext");
/*      */ 
/*      */ 
/*      */       
/* 1597 */       if (path == null) {
/* 1598 */         constructorParamTypes = new Class[] { servletContextClass };
/* 1599 */         constructorParams = new Object[] { servletContext };
/*      */       } else {
/* 1601 */         constructorParamTypes = new Class[] { servletContextClass, String.class };
/* 1602 */         constructorParams = new Object[] { servletContext, path };
/*      */       } 
/*      */       
/* 1605 */       setTemplateLoader(webappTemplateLoaderClass
/*      */           
/* 1607 */           .getConstructor(constructorParamTypes)
/* 1608 */           .newInstance(constructorParams));
/* 1609 */     } catch (Exception e) {
/* 1610 */       throw new BugException(e);
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
/*      */   public void setClassForTemplateLoading(Class resourceLoaderClass, String basePackagePath) {
/* 1626 */     setTemplateLoader((TemplateLoader)new ClassTemplateLoader(resourceLoaderClass, basePackagePath));
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
/*      */   public void setClassLoaderForTemplateLoading(ClassLoader classLoader, String basePackagePath) {
/* 1644 */     setTemplateLoader((TemplateLoader)new ClassTemplateLoader(classLoader, basePackagePath));
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
/*      */   @Deprecated
/*      */   public void setTemplateUpdateDelay(int seconds) {
/* 1661 */     this.cache.setDelay(1000L * seconds);
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
/*      */   public void setTemplateUpdateDelayMilliseconds(long millis) {
/* 1679 */     this.cache.setDelay(millis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getTemplateUpdateDelayMilliseconds() {
/* 1688 */     return this.cache.getDelay();
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
/*      */   @Deprecated
/*      */   public void setStrictSyntaxMode(boolean b) {
/* 1705 */     this.strictSyntax = b;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObjectWrapper(ObjectWrapper objectWrapper) {
/* 1710 */     ObjectWrapper prevObjectWrapper = getObjectWrapper();
/* 1711 */     super.setObjectWrapper(objectWrapper);
/* 1712 */     this.objectWrapperExplicitlySet = true;
/* 1713 */     if (objectWrapper != prevObjectWrapper) {
/*      */       try {
/* 1715 */         setSharedVariablesFromRewrappableSharedVariables();
/* 1716 */       } catch (TemplateModelException e) {
/* 1717 */         throw new RuntimeException("Failed to re-wrap earliearly set shared variables with the newly set object wrapper", e);
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
/*      */   public void unsetObjectWrapper() {
/* 1732 */     if (this.objectWrapperExplicitlySet) {
/* 1733 */       setObjectWrapper(getDefaultObjectWrapper());
/* 1734 */       this.objectWrapperExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isObjectWrapperExplicitlySet() {
/* 1744 */     return this.objectWrapperExplicitlySet;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setLocale(Locale locale) {
/* 1749 */     super.setLocale(locale);
/* 1750 */     this.localeExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetLocale() {
/* 1759 */     if (this.localeExplicitlySet) {
/* 1760 */       setLocale(getDefaultLocale());
/* 1761 */       this.localeExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLocaleExplicitlySet() {
/* 1772 */     return this.localeExplicitlySet;
/*      */   }
/*      */   
/*      */   static Locale getDefaultLocale() {
/* 1776 */     return Locale.getDefault();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimeZone(TimeZone timeZone) {
/* 1781 */     super.setTimeZone(timeZone);
/* 1782 */     this.timeZoneExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetTimeZone() {
/* 1791 */     if (this.timeZoneExplicitlySet) {
/* 1792 */       setTimeZone(getDefaultTimeZone());
/* 1793 */       this.timeZoneExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTimeZoneExplicitlySet() {
/* 1804 */     return this.timeZoneExplicitlySet;
/*      */   }
/*      */   
/*      */   static TimeZone getDefaultTimeZone() {
/* 1808 */     return TimeZone.getDefault();
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
/* 1813 */     super.setTemplateExceptionHandler(templateExceptionHandler);
/* 1814 */     this.templateExceptionHandlerExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetTemplateExceptionHandler() {
/* 1825 */     if (this.templateExceptionHandlerExplicitlySet) {
/* 1826 */       setTemplateExceptionHandler(getDefaultTemplateExceptionHandler());
/* 1827 */       this.templateExceptionHandlerExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isTemplateExceptionHandlerExplicitlySet() {
/* 1838 */     return this.templateExceptionHandlerExplicitlySet;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
/* 1843 */     super.setAttemptExceptionReporter(attemptExceptionReporter);
/* 1844 */     this.attemptExceptionReporterExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetAttemptExceptionReporter() {
/* 1855 */     if (this.attemptExceptionReporterExplicitlySet) {
/* 1856 */       setAttemptExceptionReporter(getDefaultAttemptExceptionReporter());
/* 1857 */       this.attemptExceptionReporterExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAttemptExceptionReporterExplicitlySet() {
/* 1868 */     return this.attemptExceptionReporterExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLogTemplateExceptions(boolean value) {
/* 1878 */     super.setLogTemplateExceptions(value);
/* 1879 */     this.logTemplateExceptionsExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetLogTemplateExceptions() {
/* 1890 */     if (this.logTemplateExceptionsExplicitlySet) {
/* 1891 */       setLogTemplateExceptions(getDefaultLogTemplateExceptions());
/* 1892 */       this.logTemplateExceptionsExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLogTemplateExceptionsExplicitlySet() {
/* 1902 */     return this.logTemplateExceptionsExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWrapUncheckedExceptions(boolean value) {
/* 1912 */     super.setWrapUncheckedExceptions(value);
/* 1913 */     this.wrapUncheckedExceptionsExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetWrapUncheckedExceptions() {
/* 1920 */     if (this.wrapUncheckedExceptionsExplicitlySet) {
/* 1921 */       setWrapUncheckedExceptions(getDefaultWrapUncheckedExceptions());
/* 1922 */       this.wrapUncheckedExceptionsExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWrapUncheckedExceptionsExplicitlySet() {
/* 1932 */     return this.wrapUncheckedExceptionsExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getStrictSyntaxMode() {
/* 1940 */     return this.strictSyntax;
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
/*      */   public void setIncompatibleImprovements(Version incompatibleImprovements) {
/* 1964 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/*      */     
/* 1966 */     if (!this.incompatibleImprovements.equals(incompatibleImprovements)) {
/* 1967 */       checkCurrentVersionNotRecycled(incompatibleImprovements);
/*      */       
/* 1969 */       this.incompatibleImprovements = incompatibleImprovements;
/*      */       
/* 1971 */       if (!this.templateLoaderExplicitlySet) {
/* 1972 */         this.templateLoaderExplicitlySet = true;
/* 1973 */         unsetTemplateLoader();
/*      */       } 
/*      */       
/* 1976 */       if (!this.templateLookupStrategyExplicitlySet) {
/* 1977 */         this.templateLookupStrategyExplicitlySet = true;
/* 1978 */         unsetTemplateLookupStrategy();
/*      */       } 
/*      */       
/* 1981 */       if (!this.templateNameFormatExplicitlySet) {
/* 1982 */         this.templateNameFormatExplicitlySet = true;
/* 1983 */         unsetTemplateNameFormat();
/*      */       } 
/*      */       
/* 1986 */       if (!this.cacheStorageExplicitlySet) {
/* 1987 */         this.cacheStorageExplicitlySet = true;
/* 1988 */         unsetCacheStorage();
/*      */       } 
/*      */       
/* 1991 */       if (!this.templateExceptionHandlerExplicitlySet) {
/* 1992 */         this.templateExceptionHandlerExplicitlySet = true;
/* 1993 */         unsetTemplateExceptionHandler();
/*      */       } 
/*      */       
/* 1996 */       if (!this.attemptExceptionReporterExplicitlySet) {
/* 1997 */         this.attemptExceptionReporterExplicitlySet = true;
/* 1998 */         unsetAttemptExceptionReporter();
/*      */       } 
/*      */       
/* 2001 */       if (!this.logTemplateExceptionsExplicitlySet) {
/* 2002 */         this.logTemplateExceptionsExplicitlySet = true;
/* 2003 */         unsetLogTemplateExceptions();
/*      */       } 
/*      */       
/* 2006 */       if (!this.wrapUncheckedExceptionsExplicitlySet) {
/* 2007 */         this.wrapUncheckedExceptionsExplicitlySet = true;
/* 2008 */         unsetWrapUncheckedExceptions();
/*      */       } 
/*      */       
/* 2011 */       if (!this.objectWrapperExplicitlySet) {
/* 2012 */         this.objectWrapperExplicitlySet = true;
/* 2013 */         unsetObjectWrapper();
/*      */       } 
/*      */       
/* 2016 */       recreateTemplateCache();
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void checkCurrentVersionNotRecycled(Version incompatibleImprovements) {
/* 2021 */     _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.configuration", "Configuration");
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
/*      */   public Version getIncompatibleImprovements() {
/* 2033 */     return this.incompatibleImprovements;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setIncompatibleEnhancements(String version) {
/* 2042 */     setIncompatibleImprovements(new Version(version));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public String getIncompatibleEnhancements() {
/* 2050 */     return this.incompatibleImprovements.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int getParsedIncompatibleEnhancements() {
/* 2058 */     return getIncompatibleImprovements().intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWhitespaceStripping(boolean b) {
/* 2066 */     this.whitespaceStripping = b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getWhitespaceStripping() {
/* 2077 */     return this.whitespaceStripping;
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
/*      */   public void setAutoEscapingPolicy(int autoEscapingPolicy) {
/* 2130 */     _TemplateAPI.validateAutoEscapingPolicyValue(autoEscapingPolicy);
/*      */     
/* 2132 */     int prevAutoEscaping = getAutoEscapingPolicy();
/* 2133 */     this.autoEscapingPolicy = autoEscapingPolicy;
/* 2134 */     if (prevAutoEscaping != autoEscapingPolicy) {
/* 2135 */       clearTemplateCache();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getAutoEscapingPolicy() {
/* 2146 */     return this.autoEscapingPolicy;
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
/*      */   public void setOutputFormat(OutputFormat outputFormat) {
/* 2175 */     if (outputFormat == null) {
/* 2176 */       throw new NullArgumentException("outputFormat", "You may meant: " + UndefinedOutputFormat.class
/*      */           
/* 2178 */           .getSimpleName() + ".INSTANCE");
/*      */     }
/* 2180 */     OutputFormat prevOutputFormat = getOutputFormat();
/* 2181 */     this.outputFormat = outputFormat;
/* 2182 */     this.outputFormatExplicitlySet = true;
/* 2183 */     if (prevOutputFormat != outputFormat) {
/* 2184 */       clearTemplateCache();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OutputFormat getOutputFormat() {
/* 2195 */     return this.outputFormat;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOutputFormatExplicitlySet() {
/* 2204 */     return this.outputFormatExplicitlySet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetOutputFormat() {
/* 2215 */     this.outputFormat = (OutputFormat)UndefinedOutputFormat.INSTANCE;
/* 2216 */     this.outputFormatExplicitlySet = false;
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
/*      */   public OutputFormat getOutputFormat(String name) throws UnregisteredOutputFormatException {
/* 2241 */     if (name.length() == 0) {
/* 2242 */       throw new IllegalArgumentException("0-length format name");
/*      */     }
/* 2244 */     if (name.charAt(name.length() - 1) == '}') {
/*      */       
/* 2246 */       int openBrcIdx = name.indexOf('{');
/* 2247 */       if (openBrcIdx == -1) {
/* 2248 */         throw new IllegalArgumentException("Missing opening '{' in: " + name);
/*      */       }
/*      */       
/* 2251 */       MarkupOutputFormat outerOF = getMarkupOutputFormatForCombined(name.substring(0, openBrcIdx));
/* 2252 */       MarkupOutputFormat innerOF = getMarkupOutputFormatForCombined(name
/* 2253 */           .substring(openBrcIdx + 1, name.length() - 1));
/*      */       
/* 2255 */       return (OutputFormat)new CombinedMarkupOutputFormat(name, outerOF, innerOF);
/*      */     } 
/* 2257 */     OutputFormat custOF = this.registeredCustomOutputFormats.get(name);
/* 2258 */     if (custOF != null) {
/* 2259 */       return custOF;
/*      */     }
/*      */     
/* 2262 */     OutputFormat stdOF = STANDARD_OUTPUT_FORMATS.get(name);
/* 2263 */     if (stdOF == null) {
/* 2264 */       StringBuilder sb = new StringBuilder();
/* 2265 */       sb.append("Unregistered output format name, ");
/* 2266 */       sb.append(StringUtil.jQuote(name));
/* 2267 */       sb.append(". The output formats registered in the Configuration are: ");
/*      */       
/* 2269 */       Set<String> registeredNames = new TreeSet<>();
/* 2270 */       registeredNames.addAll(STANDARD_OUTPUT_FORMATS.keySet());
/* 2271 */       registeredNames.addAll(this.registeredCustomOutputFormats.keySet());
/*      */       
/* 2273 */       boolean first = true;
/* 2274 */       for (String registeredName : registeredNames) {
/* 2275 */         if (first) {
/* 2276 */           first = false;
/*      */         } else {
/* 2278 */           sb.append(", ");
/*      */         } 
/* 2280 */         sb.append(StringUtil.jQuote(registeredName));
/*      */       } 
/*      */       
/* 2283 */       throw new UnregisteredOutputFormatException(sb.toString());
/*      */     } 
/* 2285 */     return stdOF;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private MarkupOutputFormat getMarkupOutputFormatForCombined(String outerName) throws UnregisteredOutputFormatException {
/* 2291 */     OutputFormat of = getOutputFormat(outerName);
/* 2292 */     if (!(of instanceof MarkupOutputFormat)) {
/* 2293 */       throw new IllegalArgumentException("The \"" + outerName + "\" output format can't be used in ...{...} expression, because it's not a markup format.");
/*      */     }
/*      */     
/* 2296 */     MarkupOutputFormat outerOF = (MarkupOutputFormat)of;
/* 2297 */     return outerOF;
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
/*      */   public void setRegisteredCustomOutputFormats(Collection<? extends OutputFormat> registeredCustomOutputFormats) {
/* 2328 */     NullArgumentException.check(registeredCustomOutputFormats);
/*      */     
/* 2330 */     Map<String, OutputFormat> m = new LinkedHashMap<>(registeredCustomOutputFormats.size() * 4 / 3, 1.0F);
/* 2331 */     for (OutputFormat outputFormat : registeredCustomOutputFormats) {
/* 2332 */       String name = outputFormat.getName();
/* 2333 */       if (name.equals(UndefinedOutputFormat.INSTANCE.getName())) {
/* 2334 */         throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
/*      */       }
/*      */       
/* 2337 */       if (name.equals(PlainTextOutputFormat.INSTANCE.getName())) {
/* 2338 */         throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
/*      */       }
/*      */       
/* 2341 */       if (name.length() == 0) {
/* 2342 */         throw new IllegalArgumentException("The output format name can't be 0 long");
/*      */       }
/* 2344 */       if (!Character.isLetterOrDigit(name.charAt(0))) {
/* 2345 */         throw new IllegalArgumentException("The output format name must start with letter or digit: " + name);
/*      */       }
/*      */       
/* 2348 */       if (name.indexOf('+') != -1) {
/* 2349 */         throw new IllegalArgumentException("The output format name can't contain \"+\" character: " + name);
/*      */       }
/*      */       
/* 2352 */       if (name.indexOf('{') != -1) {
/* 2353 */         throw new IllegalArgumentException("The output format name can't contain \"{\" character: " + name);
/*      */       }
/*      */       
/* 2356 */       if (name.indexOf('}') != -1) {
/* 2357 */         throw new IllegalArgumentException("The output format name can't contain \"}\" character: " + name);
/*      */       }
/*      */ 
/*      */       
/* 2361 */       OutputFormat replaced = m.put(outputFormat.getName(), outputFormat);
/* 2362 */       if (replaced != null) {
/* 2363 */         if (replaced == outputFormat) {
/* 2364 */           throw new IllegalArgumentException("Duplicate output format in the collection: " + outputFormat);
/*      */         }
/*      */         
/* 2367 */         throw new IllegalArgumentException("Clashing output format names between " + replaced + " and " + outputFormat + ".");
/*      */       } 
/*      */     } 
/*      */     
/* 2371 */     this.registeredCustomOutputFormats = Collections.unmodifiableMap(m);
/*      */     
/* 2373 */     clearTemplateCache();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Collection<? extends OutputFormat> getRegisteredCustomOutputFormats() {
/* 2382 */     return this.registeredCustomOutputFormats.values();
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
/*      */   public void setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
/* 2411 */     boolean prevEffectiveValue = getRecognizeStandardFileExtensions();
/* 2412 */     this.recognizeStandardFileExtensions = Boolean.valueOf(recognizeStandardFileExtensions);
/* 2413 */     if (prevEffectiveValue != recognizeStandardFileExtensions) {
/* 2414 */       clearTemplateCache();
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
/*      */   public void unsetRecognizeStandardFileExtensions() {
/* 2426 */     if (this.recognizeStandardFileExtensions != null) {
/* 2427 */       this.recognizeStandardFileExtensions = null;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isRecognizeStandardFileExtensionsExplicitlySet() {
/* 2438 */     return (this.recognizeStandardFileExtensions != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getRecognizeStandardFileExtensions() {
/* 2448 */     return (this.recognizeStandardFileExtensions == null) ? (
/* 2449 */       (this.incompatibleImprovements.intValue() >= _TemplateAPI.VERSION_INT_2_3_24)) : this.recognizeStandardFileExtensions
/* 2450 */       .booleanValue();
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
/*      */   public void setTagSyntax(int tagSyntax) {
/* 2483 */     _TemplateAPI.valideTagSyntaxValue(tagSyntax);
/* 2484 */     this.tagSyntax = tagSyntax;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTagSyntax() {
/* 2492 */     return this.tagSyntax;
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
/*      */   public void setInterpolationSyntax(int interpolationSyntax) {
/* 2510 */     _TemplateAPI.valideInterpolationSyntaxValue(interpolationSyntax);
/* 2511 */     this.interpolationSyntax = interpolationSyntax;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInterpolationSyntax() {
/* 2521 */     return this.interpolationSyntax;
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
/*      */   public void setNamingConvention(int namingConvention) {
/* 2576 */     _TemplateAPI.validateNamingConventionValue(namingConvention);
/* 2577 */     this.namingConvention = namingConvention;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getNamingConvention() {
/* 2587 */     return this.namingConvention;
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
/*      */   public void setTabSize(int tabSize) {
/* 2605 */     if (tabSize < 1) {
/* 2606 */       throw new IllegalArgumentException("\"tabSize\" must be at least 1, but was " + tabSize);
/*      */     }
/*      */     
/* 2609 */     if (tabSize > 256) {
/* 2610 */       throw new IllegalArgumentException("\"tabSize\" can't be more than 256, but was " + tabSize);
/*      */     }
/* 2612 */     this.tabSize = tabSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTabSize() {
/* 2622 */     return this.tabSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getFallbackOnNullLoopVariable() {
/* 2631 */     return this.fallbackOnNullLoopVariable;
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
/*      */   public void setFallbackOnNullLoopVariable(boolean fallbackOnNullLoopVariable) {
/* 2650 */     this.fallbackOnNullLoopVariable = fallbackOnNullLoopVariable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean getPreventStrippings() {
/* 2659 */     return this.preventStrippings;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setPreventStrippings(boolean preventStrippings) {
/* 2669 */     this.preventStrippings = preventStrippings;
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
/*      */   public Template getTemplate(String name) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
/* 2685 */     return getTemplate(name, (Locale)null, (Object)null, (String)null, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template getTemplate(String name, Locale locale) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
/* 2694 */     return getTemplate(name, locale, (Object)null, (String)null, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template getTemplate(String name, String encoding) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
/* 2703 */     return getTemplate(name, (Locale)null, (Object)null, encoding, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template getTemplate(String name, Locale locale, String encoding) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
/* 2712 */     return getTemplate(name, locale, (Object)null, encoding, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
/* 2721 */     return getTemplate(name, locale, (Object)null, encoding, parseAsFTL, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
/* 2732 */     return getTemplate(name, locale, (Object)null, encoding, parseAsFTL, ignoreMissing);
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
/*      */   public Template getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
/* 2829 */     if (locale == null) {
/* 2830 */       locale = getLocale();
/*      */     }
/* 2832 */     if (encoding == null) {
/* 2833 */       encoding = getEncoding(locale);
/*      */     }
/*      */     
/* 2836 */     TemplateCache.MaybeMissingTemplate maybeTemp = this.cache.getTemplate(name, locale, customLookupCondition, encoding, parseAsFTL);
/* 2837 */     Template temp = maybeTemp.getTemplate();
/* 2838 */     if (temp == null) {
/* 2839 */       String msg; if (ignoreMissing) {
/* 2840 */         return null;
/*      */       }
/*      */       
/* 2843 */       TemplateLoader tl = getTemplateLoader();
/*      */       
/* 2845 */       if (tl == null) {
/* 2846 */         msg = "Don't know where to load template " + StringUtil.jQuote(name) + " from because the \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), so it's null.";
/*      */       }
/*      */       else {
/*      */         
/* 2850 */         String missingTempNormName = maybeTemp.getMissingTemplateNormalizedName();
/* 2851 */         String missingTempReason = maybeTemp.getMissingTemplateReason();
/* 2852 */         TemplateLookupStrategy templateLookupStrategy = getTemplateLookupStrategy();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2876 */         msg = "Template not found for name " + StringUtil.jQuote(name) + ((missingTempNormName != null && name != null && !removeInitialSlash(name).equals(missingTempNormName)) ? (" (normalized: " + StringUtil.jQuote(missingTempNormName) + ")") : "") + ((customLookupCondition != null) ? (" and custom lookup condition " + StringUtil.jQuote(customLookupCondition)) : "") + "." + ((missingTempReason != null) ? ("\nReason given: " + ensureSentenceIsClosed(missingTempReason)) : "") + "\nThe name was interpreted by this TemplateLoader: " + StringUtil.tryToString(tl) + "." + (!isKnownNonConfusingLookupStrategy(templateLookupStrategy) ? ("\n(Before that, the name was possibly changed by this lookup strategy: " + StringUtil.tryToString(templateLookupStrategy) + ".)") : "") + (!this.templateLoaderExplicitlySet ? "\nWarning: The \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), and using the default value is most certainly not intended and dangerous, and can be the cause of this error." : "") + ((missingTempReason == null && name.indexOf('\\') != -1) ? "\nWarning: The name contains backslash (\"\\\") instead of slash (\"/\"); template names should use slash only." : "");
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2882 */       String normName = maybeTemp.getMissingTemplateNormalizedName();
/* 2883 */       throw new TemplateNotFoundException((normName != null) ? normName : name, customLookupCondition, msg);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2888 */     return temp;
/*      */   }
/*      */   
/*      */   private boolean isKnownNonConfusingLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
/* 2892 */     return (templateLookupStrategy == TemplateLookupStrategy.DEFAULT_2_3_0);
/*      */   }
/*      */   
/*      */   private String removeInitialSlash(String name) {
/* 2896 */     return name.startsWith("/") ? name.substring(1) : name;
/*      */   }
/*      */   
/*      */   private String ensureSentenceIsClosed(String s) {
/* 2900 */     if (s == null || s.length() == 0) {
/* 2901 */       return s;
/*      */     }
/*      */     
/* 2904 */     char lastChar = s.charAt(s.length() - 1);
/* 2905 */     return (lastChar == '.' || lastChar == '!' || lastChar == '?') ? s : (s + ".");
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
/*      */   public void setDefaultEncoding(String encoding) {
/* 2924 */     this.defaultEncoding = encoding;
/* 2925 */     this.defaultEncodingExplicitlySet = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDefaultEncoding() {
/* 2934 */     return this.defaultEncoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void unsetDefaultEncoding() {
/* 2943 */     if (this.defaultEncodingExplicitlySet) {
/* 2944 */       setDefaultEncoding(getDefaultDefaultEncoding());
/* 2945 */       this.defaultEncodingExplicitlySet = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDefaultEncodingExplicitlySet() {
/* 2956 */     return this.defaultEncodingExplicitlySet;
/*      */   }
/*      */   
/*      */   private static String getDefaultDefaultEncoding() {
/* 2960 */     return getJVMDefaultEncoding();
/*      */   }
/*      */   
/*      */   private static String getJVMDefaultEncoding() {
/* 2964 */     return SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
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
/*      */   public String getEncoding(Locale locale) {
/* 2977 */     if (this.localeToCharsetMap.isEmpty()) {
/* 2978 */       return this.defaultEncoding;
/*      */     }
/*      */     
/* 2981 */     NullArgumentException.check("locale", locale);
/* 2982 */     String charset = (String)this.localeToCharsetMap.get(locale.toString());
/* 2983 */     if (charset == null) {
/* 2984 */       if (locale.getVariant().length() > 0) {
/* 2985 */         Locale l = new Locale(locale.getLanguage(), locale.getCountry());
/* 2986 */         charset = (String)this.localeToCharsetMap.get(l.toString());
/* 2987 */         if (charset != null) {
/* 2988 */           this.localeToCharsetMap.put(locale.toString(), charset);
/*      */         }
/*      */       } 
/* 2991 */       charset = (String)this.localeToCharsetMap.get(locale.getLanguage());
/* 2992 */       if (charset != null) {
/* 2993 */         this.localeToCharsetMap.put(locale.toString(), charset);
/*      */       }
/*      */     } 
/* 2996 */     return (charset != null) ? charset : this.defaultEncoding;
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
/*      */   public void setEncoding(Locale locale, String encoding) {
/* 3010 */     this.localeToCharsetMap.put(locale.toString(), encoding);
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
/*      */   public void setSharedVariable(String name, TemplateModel tm) {
/* 3033 */     Object replaced = this.sharedVariables.put(name, tm);
/* 3034 */     if (replaced != null && this.rewrappableSharedVariables != null) {
/* 3035 */       this.rewrappableSharedVariables.remove(name);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set getSharedVariableNames() {
/* 3046 */     return new HashSet(this.sharedVariables.keySet());
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
/*      */   public void setSharedVariable(String name, Object value) throws TemplateModelException {
/* 3065 */     setSharedVariable(name, getObjectWrapper().wrap(value));
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
/*      */   public void setSharedVariables(Map<String, ?> map) throws TemplateModelException {
/* 3091 */     this.rewrappableSharedVariables = new HashMap<>(map);
/* 3092 */     this.sharedVariables.clear();
/* 3093 */     setSharedVariablesFromRewrappableSharedVariables();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSharedVaribles(Map<String, ?> map) throws TemplateModelException {
/* 3102 */     setSharedVariables(map);
/*      */   }
/*      */   
/*      */   private void setSharedVariablesFromRewrappableSharedVariables() throws TemplateModelException {
/* 3106 */     if (this.rewrappableSharedVariables == null)
/* 3107 */       return;  for (Iterator<Map.Entry> it = this.rewrappableSharedVariables.entrySet().iterator(); it.hasNext(); ) {
/* 3108 */       TemplateModel valueAsTM; Map.Entry ent = it.next();
/* 3109 */       String name = (String)ent.getKey();
/* 3110 */       Object value = ent.getValue();
/*      */ 
/*      */       
/* 3113 */       if (value instanceof TemplateModel) {
/* 3114 */         valueAsTM = (TemplateModel)value;
/*      */       } else {
/* 3116 */         valueAsTM = getObjectWrapper().wrap(value);
/*      */       } 
/* 3118 */       this.sharedVariables.put(name, valueAsTM);
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
/*      */   public void setAllSharedVariables(TemplateHashModelEx hash) throws TemplateModelException {
/* 3142 */     TemplateModelIterator keys = hash.keys().iterator();
/* 3143 */     TemplateModelIterator values = hash.values().iterator();
/* 3144 */     while (keys.hasNext()) {
/* 3145 */       setSharedVariable(((TemplateScalarModel)keys.next()).getAsString(), values.next());
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
/*      */   public TemplateModel getSharedVariable(String name) {
/* 3162 */     return (TemplateModel)this.sharedVariables.get(name);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearSharedVariables() {
/* 3169 */     this.sharedVariables.clear();
/* 3170 */     loadBuiltInSharedVariables();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearTemplateCache() {
/* 3180 */     this.cache.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeTemplateFromCache(String name) throws IOException {
/* 3190 */     Locale loc = getLocale();
/* 3191 */     removeTemplateFromCache(name, loc, (Object)null, getEncoding(loc), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeTemplateFromCache(String name, Locale locale) throws IOException {
/* 3201 */     removeTemplateFromCache(name, locale, (Object)null, getEncoding(locale), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeTemplateFromCache(String name, String encoding) throws IOException {
/* 3211 */     removeTemplateFromCache(name, getLocale(), (Object)null, encoding, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeTemplateFromCache(String name, Locale locale, String encoding) throws IOException {
/* 3221 */     removeTemplateFromCache(name, locale, (Object)null, encoding, true);
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
/*      */   public void removeTemplateFromCache(String name, Locale locale, String encoding, boolean parse) throws IOException {
/* 3233 */     removeTemplateFromCache(name, locale, (Object)null, encoding, parse);
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
/*      */   public void removeTemplateFromCache(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
/* 3252 */     this.cache.removeTemplate(name, locale, customLookupCondition, encoding, parse);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getLocalizedLookup() {
/* 3261 */     return this.cache.getLocalizedLookup();
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
/*      */   public void setLocalizedLookup(boolean localizedLookup) {
/* 3285 */     this.localizedLookup = localizedLookup;
/* 3286 */     this.cache.setLocalizedLookup(localizedLookup);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setSetting(String name, String value) throws TemplateException {
/* 3291 */     boolean unknown = false;
/*      */     try {
/* 3293 */       if ("TemplateUpdateInterval".equalsIgnoreCase(name)) {
/* 3294 */         name = "template_update_delay";
/* 3295 */       } else if ("DefaultEncoding".equalsIgnoreCase(name)) {
/* 3296 */         name = "default_encoding";
/*      */       } 
/*      */       
/* 3299 */       if ("default_encoding".equals(name) || "defaultEncoding".equals(name)) {
/* 3300 */         if ("JVM default".equalsIgnoreCase(value)) {
/* 3301 */           setDefaultEncoding(getJVMDefaultEncoding());
/*      */         } else {
/* 3303 */           setDefaultEncoding(value);
/*      */         } 
/* 3305 */       } else if ("localized_lookup".equals(name) || "localizedLookup".equals(name)) {
/* 3306 */         setLocalizedLookup(StringUtil.getYesNo(value));
/* 3307 */       } else if ("strict_syntax".equals(name) || "strictSyntax".equals(name)) {
/* 3308 */         setStrictSyntaxMode(StringUtil.getYesNo(value));
/* 3309 */       } else if ("whitespace_stripping".equals(name) || "whitespaceStripping"
/* 3310 */         .equals(name)) {
/* 3311 */         setWhitespaceStripping(StringUtil.getYesNo(value));
/* 3312 */       } else if ("auto_escaping_policy".equals(name) || "autoEscapingPolicy".equals(name)) {
/* 3313 */         if ("enable_if_default".equals(value) || "enableIfDefault".equals(value)) {
/* 3314 */           setAutoEscapingPolicy(21);
/* 3315 */         } else if ("enable_if_supported".equals(value) || "enableIfSupported".equals(value)) {
/* 3316 */           setAutoEscapingPolicy(22);
/* 3317 */         } else if ("disable".equals(value)) {
/* 3318 */           setAutoEscapingPolicy(20);
/*      */         } else {
/* 3320 */           throw invalidSettingValueException(name, value);
/*      */         } 
/* 3322 */       } else if ("output_format".equals(name) || "outputFormat".equals(name)) {
/* 3323 */         if (value.equalsIgnoreCase("default")) {
/* 3324 */           unsetOutputFormat();
/*      */         } else {
/* 3326 */           OutputFormat stdOF = STANDARD_OUTPUT_FORMATS.get(value);
/* 3327 */           setOutputFormat((stdOF != null) ? stdOF : 
/*      */               
/* 3329 */               (OutputFormat)_ObjectBuilderSettingEvaluator.eval(value, OutputFormat.class, true, 
/* 3330 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 3332 */       } else if ("registered_custom_output_formats".equals(name) || "registeredCustomOutputFormats"
/* 3333 */         .equals(name)) {
/* 3334 */         List<? extends OutputFormat> list = (List)_ObjectBuilderSettingEvaluator.eval(value, List.class, true, 
/* 3335 */             _SettingEvaluationEnvironment.getCurrent());
/* 3336 */         for (Object item : list) {
/* 3337 */           if (!(item instanceof OutputFormat)) {
/* 3338 */             throw new _MiscTemplateException(getEnvironment(), new Object[] { "Invalid value for setting ", new _DelayedJQuote(name), ": List items must be " + OutputFormat.class
/*      */                   
/* 3340 */                   .getName() + " instances, in: ", value });
/*      */           }
/*      */         } 
/* 3343 */         setRegisteredCustomOutputFormats(list);
/* 3344 */       } else if ("recognize_standard_file_extensions".equals(name) || "recognizeStandardFileExtensions"
/* 3345 */         .equals(name)) {
/* 3346 */         if (value.equalsIgnoreCase("default")) {
/* 3347 */           unsetRecognizeStandardFileExtensions();
/*      */         } else {
/* 3349 */           setRecognizeStandardFileExtensions(StringUtil.getYesNo(value));
/*      */         } 
/* 3351 */       } else if ("cache_storage".equals(name) || "cacheStorage".equals(name)) {
/* 3352 */         if (value.equalsIgnoreCase("default"))
/* 3353 */           unsetCacheStorage(); 
/* 3354 */         if (value.indexOf('.') == -1) {
/* 3355 */           int strongSize = 0;
/* 3356 */           int softSize = 0;
/* 3357 */           Map map = StringUtil.parseNameValuePairList(value, 
/* 3358 */               String.valueOf(2147483647));
/* 3359 */           Iterator<Map.Entry> it = map.entrySet().iterator();
/* 3360 */           while (it.hasNext()) {
/* 3361 */             int pvalue; Map.Entry ent = it.next();
/* 3362 */             String pname = (String)ent.getKey();
/*      */             
/*      */             try {
/* 3365 */               pvalue = Integer.parseInt((String)ent.getValue());
/* 3366 */             } catch (NumberFormatException e) {
/* 3367 */               throw invalidSettingValueException(name, value);
/*      */             } 
/* 3369 */             if ("soft".equalsIgnoreCase(pname)) {
/* 3370 */               softSize = pvalue; continue;
/* 3371 */             }  if ("strong".equalsIgnoreCase(pname)) {
/* 3372 */               strongSize = pvalue; continue;
/*      */             } 
/* 3374 */             throw invalidSettingValueException(name, value);
/*      */           } 
/*      */           
/* 3377 */           if (softSize == 0 && strongSize == 0) {
/* 3378 */             throw invalidSettingValueException(name, value);
/*      */           }
/* 3380 */           setCacheStorage((CacheStorage)new MruCacheStorage(strongSize, softSize));
/*      */         } else {
/* 3382 */           setCacheStorage((CacheStorage)_ObjectBuilderSettingEvaluator.eval(value, CacheStorage.class, false, 
/* 3383 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 3385 */       } else if ("template_update_delay".equals(name) || "templateUpdateDelay"
/* 3386 */         .equals(name)) {
/*      */         long multiplier;
/*      */         String valueWithoutUnit;
/* 3389 */         if (value.endsWith("ms")) {
/* 3390 */           multiplier = 1L;
/* 3391 */           valueWithoutUnit = rightTrim(value.substring(0, value.length() - 2));
/* 3392 */         } else if (value.endsWith("s")) {
/* 3393 */           multiplier = 1000L;
/* 3394 */           valueWithoutUnit = rightTrim(value.substring(0, value.length() - 1));
/* 3395 */         } else if (value.endsWith("m")) {
/* 3396 */           multiplier = 60000L;
/* 3397 */           valueWithoutUnit = rightTrim(value.substring(0, value.length() - 1));
/* 3398 */         } else if (value.endsWith("h")) {
/* 3399 */           multiplier = 3600000L;
/* 3400 */           valueWithoutUnit = rightTrim(value.substring(0, value.length() - 1));
/*      */         } else {
/* 3402 */           multiplier = 1000L;
/* 3403 */           valueWithoutUnit = value;
/*      */         } 
/* 3405 */         setTemplateUpdateDelayMilliseconds(Integer.parseInt(valueWithoutUnit) * multiplier);
/* 3406 */       } else if ("tag_syntax".equals(name) || "tagSyntax".equals(name)) {
/* 3407 */         if ("auto_detect".equals(value) || "autoDetect".equals(value)) {
/* 3408 */           setTagSyntax(0);
/* 3409 */         } else if ("angle_bracket".equals(value) || "angleBracket".equals(value)) {
/* 3410 */           setTagSyntax(1);
/* 3411 */         } else if ("square_bracket".equals(value) || "squareBracket".equals(value)) {
/* 3412 */           setTagSyntax(2);
/*      */         } else {
/* 3414 */           throw invalidSettingValueException(name, value);
/*      */         } 
/* 3416 */       } else if ("interpolation_syntax".equals(name) || "interpolationSyntax"
/* 3417 */         .equals(name)) {
/* 3418 */         if ("legacy".equals(value)) {
/* 3419 */           setInterpolationSyntax(20);
/* 3420 */         } else if ("dollar".equals(value)) {
/* 3421 */           setInterpolationSyntax(21);
/* 3422 */         } else if ("square_bracket".equals(value) || "squareBracket".equals(value)) {
/* 3423 */           setInterpolationSyntax(22);
/*      */         } else {
/* 3425 */           throw invalidSettingValueException(name, value);
/*      */         } 
/* 3427 */       } else if ("naming_convention".equals(name) || "namingConvention".equals(name)) {
/* 3428 */         if ("auto_detect".equals(value) || "autoDetect".equals(value)) {
/* 3429 */           setNamingConvention(10);
/* 3430 */         } else if ("legacy".equals(value)) {
/* 3431 */           setNamingConvention(11);
/* 3432 */         } else if ("camel_case".equals(value) || "camelCase".equals(value)) {
/* 3433 */           setNamingConvention(12);
/*      */         } else {
/* 3435 */           throw invalidSettingValueException(name, value);
/*      */         } 
/* 3437 */       } else if ("tab_size".equals(name) || "tabSize".equals(name)) {
/* 3438 */         setTabSize(Integer.parseInt(value));
/* 3439 */       } else if ("incompatible_improvements".equals(name) || "incompatibleImprovements"
/* 3440 */         .equals(name)) {
/* 3441 */         setIncompatibleImprovements(new Version(value));
/* 3442 */       } else if ("incompatible_enhancements".equals(name)) {
/* 3443 */         setIncompatibleEnhancements(value);
/* 3444 */       } else if ("template_loader".equals(name) || "templateLoader".equals(name)) {
/* 3445 */         if (value.equalsIgnoreCase("default")) {
/* 3446 */           unsetTemplateLoader();
/*      */         } else {
/* 3448 */           setTemplateLoader((TemplateLoader)_ObjectBuilderSettingEvaluator.eval(value, TemplateLoader.class, true, 
/* 3449 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 3451 */       } else if ("template_lookup_strategy".equals(name) || "templateLookupStrategy"
/* 3452 */         .equals(name)) {
/* 3453 */         if (value.equalsIgnoreCase("default")) {
/* 3454 */           unsetTemplateLookupStrategy();
/*      */         } else {
/* 3456 */           setTemplateLookupStrategy((TemplateLookupStrategy)_ObjectBuilderSettingEvaluator.eval(value, TemplateLookupStrategy.class, false, 
/* 3457 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 3459 */       } else if ("template_name_format".equals(name) || "templateNameFormat"
/* 3460 */         .equals(name)) {
/* 3461 */         if (value.equalsIgnoreCase("default")) {
/* 3462 */           unsetTemplateNameFormat();
/* 3463 */         } else if (value.equalsIgnoreCase("default_2_3_0")) {
/* 3464 */           setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_3_0);
/* 3465 */         } else if (value.equalsIgnoreCase("default_2_4_0")) {
/* 3466 */           setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_4_0);
/*      */         } else {
/* 3468 */           throw invalidSettingValueException(name, value);
/*      */         } 
/* 3470 */       } else if ("template_configurations".equals(name) || "templateConfigurations"
/* 3471 */         .equals(name)) {
/* 3472 */         if (value.equals("null")) {
/* 3473 */           setTemplateConfigurations((TemplateConfigurationFactory)null);
/*      */         } else {
/* 3475 */           setTemplateConfigurations((TemplateConfigurationFactory)_ObjectBuilderSettingEvaluator.eval(value, TemplateConfigurationFactory.class, false, 
/* 3476 */                 _SettingEvaluationEnvironment.getCurrent()));
/*      */         } 
/* 3478 */       } else if ("fallback_on_null_loop_variable".equals(name) || "fallbackOnNullLoopVariable"
/* 3479 */         .equals(name)) {
/* 3480 */         setFallbackOnNullLoopVariable(StringUtil.getYesNo(value));
/*      */       } else {
/* 3482 */         unknown = true;
/*      */       } 
/* 3484 */     } catch (Exception e) {
/* 3485 */       throw settingValueAssignmentException(name, value, e);
/*      */     } 
/* 3487 */     if (unknown) {
/* 3488 */       super.setSetting(name, value);
/*      */     }
/*      */   }
/*      */   
/*      */   private String rightTrim(String s) {
/* 3493 */     int ln = s.length();
/* 3494 */     while (ln > 0 && Character.isWhitespace(s.charAt(ln - 1))) {
/* 3495 */       ln--;
/*      */     }
/* 3497 */     return s.substring(0, ln);
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
/*      */   public Set<String> getSettingNames(boolean camelCase) {
/* 3514 */     return (Set<String>)new _UnmodifiableCompositeSet(super
/* 3515 */         .getSettingNames(camelCase), (Set)new _SortedArraySet(camelCase ? (Object[])SETTING_NAMES_CAMEL_CASE : (Object[])SETTING_NAMES_SNAKE_CASE));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected String getCorrectedNameForUnknownSetting(String name) {
/* 3521 */     if ("encoding".equals(name) || "charset".equals(name) || "default_charset".equals(name))
/*      */     {
/* 3523 */       return "default_encoding";
/*      */     }
/* 3525 */     if ("defaultCharset".equals(name)) {
/* 3526 */       return "defaultEncoding";
/*      */     }
/* 3528 */     return super.getCorrectedNameForUnknownSetting(name);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void doAutoImportsAndIncludes(Environment env) throws TemplateException, IOException {
/* 3533 */     Template t = env.getMainTemplate();
/* 3534 */     doAutoImports(env, t);
/* 3535 */     doAutoIncludes(env, t);
/*      */   }
/*      */   
/*      */   private void doAutoImports(Environment env, Template t) throws IOException, TemplateException {
/* 3539 */     Map<String, String> envAutoImports = env.getAutoImportsWithoutFallback();
/* 3540 */     Map<String, String> tAutoImports = t.getAutoImportsWithoutFallback();
/*      */ 
/*      */     
/* 3543 */     boolean lazyAutoImports = (env.getLazyAutoImports() != null) ? env.getLazyAutoImports().booleanValue() : env.getLazyImports();
/*      */     
/* 3545 */     for (Map.Entry<String, String> autoImport : (Iterable<Map.Entry<String, String>>)getAutoImportsWithoutFallback().entrySet()) {
/* 3546 */       String nsVarName = autoImport.getKey();
/* 3547 */       if ((tAutoImports == null || !tAutoImports.containsKey(nsVarName)) && (envAutoImports == null || 
/* 3548 */         !envAutoImports.containsKey(nsVarName))) {
/* 3549 */         env.importLib(autoImport.getValue(), nsVarName, lazyAutoImports);
/*      */       }
/*      */     } 
/* 3552 */     if (tAutoImports != null) {
/* 3553 */       for (Map.Entry<String, String> autoImport : tAutoImports.entrySet()) {
/* 3554 */         String nsVarName = autoImport.getKey();
/* 3555 */         if (envAutoImports == null || !envAutoImports.containsKey(nsVarName)) {
/* 3556 */           env.importLib(autoImport.getValue(), nsVarName, lazyAutoImports);
/*      */         }
/*      */       } 
/*      */     }
/* 3560 */     if (envAutoImports != null) {
/* 3561 */       for (Map.Entry<String, String> autoImport : envAutoImports.entrySet()) {
/* 3562 */         String nsVarName = autoImport.getKey();
/* 3563 */         env.importLib(autoImport.getValue(), nsVarName, lazyAutoImports);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void doAutoIncludes(Environment env, Template t) throws TemplateException, IOException, TemplateNotFoundException, MalformedTemplateNameException, ParseException {
/* 3574 */     List<String> tAutoIncludes = t.getAutoIncludesWithoutFallback();
/* 3575 */     List<String> envAutoIncludes = env.getAutoIncludesWithoutFallback();
/*      */     
/* 3577 */     for (String templateName : getAutoIncludesWithoutFallback()) {
/* 3578 */       if ((tAutoIncludes == null || !tAutoIncludes.contains(templateName)) && (envAutoIncludes == null || 
/* 3579 */         !envAutoIncludes.contains(templateName))) {
/* 3580 */         env.include(getTemplate(templateName, env.getLocale()));
/*      */       }
/*      */     } 
/*      */     
/* 3584 */     if (tAutoIncludes != null) {
/* 3585 */       for (String templateName : tAutoIncludes) {
/* 3586 */         if (envAutoIncludes == null || !envAutoIncludes.contains(templateName)) {
/* 3587 */           env.include(getTemplate(templateName, env.getLocale()));
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/* 3592 */     if (envAutoIncludes != null) {
/* 3593 */       for (String templateName : envAutoIncludes) {
/* 3594 */         env.include(getTemplate(templateName, env.getLocale()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String getVersionNumber() {
/* 3606 */     return VERSION.toString();
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
/*      */   public static Version getVersion() {
/* 3642 */     return VERSION;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ObjectWrapper getDefaultObjectWrapper(Version incompatibleImprovements) {
/* 3653 */     if (incompatibleImprovements.intValue() < _TemplateAPI.VERSION_INT_2_3_21) {
/* 3654 */       return ObjectWrapper.DEFAULT_WRAPPER;
/*      */     }
/* 3656 */     return (ObjectWrapper)(new DefaultObjectWrapperBuilder(incompatibleImprovements)).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set getSupportedBuiltInNames() {
/* 3666 */     return getSupportedBuiltInNames(getNamingConvention());
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
/*      */   public Set<String> getSupportedBuiltInNames(int namingConvention) {
/* 3682 */     return _CoreAPI.getSupportedBuiltInNames(namingConvention);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Set getSupportedBuiltInDirectiveNames() {
/* 3691 */     return getSupportedBuiltInDirectiveNames(getNamingConvention());
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
/*      */   public Set<String> getSupportedBuiltInDirectiveNames(int namingConvention) {
/* 3706 */     if (namingConvention == 10)
/* 3707 */       return _CoreAPI.ALL_BUILT_IN_DIRECTIVE_NAMES; 
/* 3708 */     if (namingConvention == 11)
/* 3709 */       return _CoreAPI.LEGACY_BUILT_IN_DIRECTIVE_NAMES; 
/* 3710 */     if (namingConvention == 12) {
/* 3711 */       return _CoreAPI.CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;
/*      */     }
/* 3713 */     throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
/*      */   }
/*      */ 
/*      */   
/*      */   private static String getRequiredVersionProperty(Properties vp, String properyName) {
/* 3718 */     String s = vp.getProperty(properyName);
/* 3719 */     if (s == null) {
/* 3720 */       throw new RuntimeException("Version file is corrupt: \"" + properyName + "\" property is missing.");
/*      */     }
/*      */     
/* 3723 */     return s;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\Configuration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */