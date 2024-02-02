/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.cache.CacheStorage;
/*     */ import freemarker.cache.TemplateLoader;
/*     */ import freemarker.cache.TemplateLookupStrategy;
/*     */ import freemarker.cache.TemplateNameFormat;
/*     */ import freemarker.core.Expression;
/*     */ import freemarker.core.OutputFormat;
/*     */ import freemarker.core.TemplateObject;
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class _TemplateAPI
/*     */ {
/*  44 */   public static final int VERSION_INT_2_3_0 = Configuration.VERSION_2_3_0.intValue();
/*  45 */   public static final int VERSION_INT_2_3_19 = Configuration.VERSION_2_3_19.intValue();
/*  46 */   public static final int VERSION_INT_2_3_20 = Configuration.VERSION_2_3_20.intValue();
/*  47 */   public static final int VERSION_INT_2_3_21 = Configuration.VERSION_2_3_21.intValue();
/*  48 */   public static final int VERSION_INT_2_3_22 = Configuration.VERSION_2_3_22.intValue();
/*  49 */   public static final int VERSION_INT_2_3_23 = Configuration.VERSION_2_3_23.intValue();
/*  50 */   public static final int VERSION_INT_2_3_24 = Configuration.VERSION_2_3_24.intValue();
/*  51 */   public static final int VERSION_INT_2_3_25 = Configuration.VERSION_2_3_25.intValue();
/*  52 */   public static final int VERSION_INT_2_3_26 = Configuration.VERSION_2_3_26.intValue();
/*  53 */   public static final int VERSION_INT_2_3_27 = Configuration.VERSION_2_3_27.intValue();
/*  54 */   public static final int VERSION_INT_2_3_28 = Configuration.VERSION_2_3_28.intValue();
/*  55 */   public static final int VERSION_INT_2_3_29 = Configuration.VERSION_2_3_29.intValue();
/*  56 */   public static final int VERSION_INT_2_3_30 = Configuration.VERSION_2_3_30.intValue();
/*  57 */   public static final int VERSION_INT_2_3_31 = Configuration.VERSION_2_3_31.intValue();
/*  58 */   public static final int VERSION_INT_2_4_0 = Version.intValueFor(2, 4, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   public static final SimpleObjectWrapper SAFE_OBJECT_WRAPPER = new SimpleObjectWrapper(Configuration.VERSION_2_3_0); static {
/*  84 */     SAFE_OBJECT_WRAPPER.writeProtect();
/*     */   }
/*     */   
/*     */   public static void checkVersionNotNullAndSupported(Version incompatibleImprovements) {
/*  88 */     NullArgumentException.check("incompatibleImprovements", incompatibleImprovements);
/*  89 */     int iciV = incompatibleImprovements.intValue();
/*  90 */     if (iciV > Configuration.getVersion().intValue()) {
/*  91 */       throw new IllegalArgumentException("The FreeMarker version requested by \"incompatibleImprovements\" was " + incompatibleImprovements + ", but the installed FreeMarker version is only " + 
/*     */           
/*  93 */           Configuration.getVersion() + ". You may need to upgrade FreeMarker in your project.");
/*     */     }
/*  95 */     if (iciV < VERSION_INT_2_3_0) {
/*  96 */       throw new IllegalArgumentException("\"incompatibleImprovements\" must be at least 2.3.0.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkCurrentVersionNotRecycled(Version incompatibleImprovements, String logCategory, String configuredClassShortName) {
/* 109 */     if (incompatibleImprovements == Configuration.getVersion()) {
/* 110 */       Logger.getLogger(logCategory)
/* 111 */         .error(configuredClassShortName + ".incompatibleImprovements was set to the object returned by Configuration.getVersion(). That defeats the purpose of incompatibleImprovements, and makes upgrading FreeMarker a potentially breaking change. Also, this probably won't be allowed starting from 2.4.0. Instead, set incompatibleImprovements to the highest concrete version that's known to be compatible with your application.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getTemplateLanguageVersionAsInt(TemplateObject to) {
/* 120 */     return getTemplateLanguageVersionAsInt(to.getTemplate());
/*     */   }
/*     */   
/*     */   public static int getTemplateLanguageVersionAsInt(Template t) {
/* 124 */     return t.getTemplateLanguageVersion().intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void DefaultObjectWrapperFactory_clearInstanceCache() {
/* 129 */     DefaultObjectWrapperBuilder.clearInstanceCache();
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateExceptionHandler getDefaultTemplateExceptionHandler(Version incompatibleImprovements) {
/* 134 */     return Configuration.getDefaultTemplateExceptionHandler(incompatibleImprovements);
/*     */   }
/*     */ 
/*     */   
/*     */   public static AttemptExceptionReporter getDefaultAttemptExceptionReporter(Version incompatibleImprovements) {
/* 139 */     return Configuration.getDefaultAttemptExceptionReporter(incompatibleImprovements);
/*     */   }
/*     */   
/*     */   public static boolean getDefaultLogTemplateExceptions(Version incompatibleImprovements) {
/* 143 */     return Configuration.getDefaultLogTemplateExceptions(incompatibleImprovements);
/*     */   }
/*     */   
/*     */   public static boolean getDefaultWrapUncheckedExceptions(Version incompatibleImprovements) {
/* 147 */     return Configuration.getDefaultWrapUncheckedExceptions(incompatibleImprovements);
/*     */   }
/*     */   
/*     */   public static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements) {
/* 151 */     return Configuration.createDefaultTemplateLoader(incompatibleImprovements);
/*     */   }
/*     */   
/*     */   public static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements) {
/* 155 */     return Configuration.createDefaultCacheStorage(incompatibleImprovements);
/*     */   }
/*     */   
/*     */   public static TemplateLookupStrategy getDefaultTemplateLookupStrategy(Version incompatibleImprovements) {
/* 159 */     return Configuration.getDefaultTemplateLookupStrategy(incompatibleImprovements);
/*     */   }
/*     */   
/*     */   public static TemplateNameFormat getDefaultTemplateNameFormat(Version incompatibleImprovements) {
/* 163 */     return Configuration.getDefaultTemplateNameFormat(incompatibleImprovements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set getConfigurationSettingNames(Configuration cfg, boolean camelCase) {
/* 170 */     return cfg.getSettingNames(camelCase);
/*     */   }
/*     */   
/*     */   public static void setAutoEscaping(Template t, boolean autoEscaping) {
/* 174 */     t.setAutoEscaping(autoEscaping);
/*     */   }
/*     */   
/*     */   public static void setOutputFormat(Template t, OutputFormat outputFormat) {
/* 178 */     t.setOutputFormat(outputFormat);
/*     */   }
/*     */   
/*     */   public static void validateAutoEscapingPolicyValue(int autoEscaping) {
/* 182 */     if (autoEscaping != 21 && autoEscaping != 22 && autoEscaping != 20)
/*     */     {
/*     */       
/* 185 */       throw new IllegalArgumentException("\"auto_escaping\" can only be set to one of these: Configuration.ENABLE_AUTO_ESCAPING_IF_DEFAULT, or Configuration.ENABLE_AUTO_ESCAPING_IF_SUPPORTEDor Configuration.DISABLE_AUTO_ESCAPING");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void validateNamingConventionValue(int namingConvention) {
/* 193 */     if (namingConvention != 10 && namingConvention != 11 && namingConvention != 12)
/*     */     {
/*     */       
/* 196 */       throw new IllegalArgumentException("\"naming_convention\" can only be set to one of these: Configuration.AUTO_DETECT_NAMING_CONVENTION, or Configuration.LEGACY_NAMING_CONVENTIONor Configuration.CAMEL_CASE_NAMING_CONVENTION");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void valideTagSyntaxValue(int tagSyntax) {
/* 204 */     if (tagSyntax != 0 && tagSyntax != 2 && tagSyntax != 1)
/*     */     {
/*     */       
/* 207 */       throw new IllegalArgumentException("\"tag_syntax\" can only be set to one of these: Configuration.AUTO_DETECT_TAG_SYNTAX, Configuration.ANGLE_BRACKET_TAG_SYNTAX, or Configuration.SQUARE_BRACKET_TAG_SYNTAX");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void valideInterpolationSyntaxValue(int interpolationSyntax) {
/* 214 */     if (interpolationSyntax != 20 && interpolationSyntax != 21 && interpolationSyntax != 22)
/*     */     {
/*     */       
/* 217 */       throw new IllegalArgumentException("\"interpolation_syntax\" can only be set to one of these: Configuration.LEGACY_INTERPOLATION_SYNTAX, Configuration.DOLLAR_INTERPOLATION_SYNTAX, or Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Expression getBlamedExpression(TemplateException e) {
/* 224 */     return e.getBlamedExpression();
/*     */   }
/*     */   
/*     */   public static Locale getDefaultLocale() {
/* 228 */     return Configuration.getDefaultLocale();
/*     */   }
/*     */   
/*     */   public static TimeZone getDefaultTimeZone() {
/* 232 */     return Configuration.getDefaultTimeZone();
/*     */   }
/*     */   
/*     */   public static void setPreventStrippings(Configuration conf, boolean preventStrippings) {
/* 236 */     conf.setPreventStrippings(preventStrippings);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\_TemplateAPI.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */