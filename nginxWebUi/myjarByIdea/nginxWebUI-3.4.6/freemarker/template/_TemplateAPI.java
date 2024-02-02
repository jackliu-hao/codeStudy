package freemarker.template;

import freemarker.cache.CacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLookupStrategy;
import freemarker.cache.TemplateNameFormat;
import freemarker.core.Expression;
import freemarker.core.OutputFormat;
import freemarker.core.TemplateObject;
import freemarker.log.Logger;
import freemarker.template.utility.NullArgumentException;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class _TemplateAPI {
   public static final int VERSION_INT_2_3_0;
   public static final int VERSION_INT_2_3_19;
   public static final int VERSION_INT_2_3_20;
   public static final int VERSION_INT_2_3_21;
   public static final int VERSION_INT_2_3_22;
   public static final int VERSION_INT_2_3_23;
   public static final int VERSION_INT_2_3_24;
   public static final int VERSION_INT_2_3_25;
   public static final int VERSION_INT_2_3_26;
   public static final int VERSION_INT_2_3_27;
   public static final int VERSION_INT_2_3_28;
   public static final int VERSION_INT_2_3_29;
   public static final int VERSION_INT_2_3_30;
   public static final int VERSION_INT_2_3_31;
   public static final int VERSION_INT_2_4_0;
   public static final SimpleObjectWrapper SAFE_OBJECT_WRAPPER;

   public static void checkVersionNotNullAndSupported(Version incompatibleImprovements) {
      NullArgumentException.check("incompatibleImprovements", incompatibleImprovements);
      int iciV = incompatibleImprovements.intValue();
      if (iciV > Configuration.getVersion().intValue()) {
         throw new IllegalArgumentException("The FreeMarker version requested by \"incompatibleImprovements\" was " + incompatibleImprovements + ", but the installed FreeMarker version is only " + Configuration.getVersion() + ". You may need to upgrade FreeMarker in your project.");
      } else if (iciV < VERSION_INT_2_3_0) {
         throw new IllegalArgumentException("\"incompatibleImprovements\" must be at least 2.3.0.");
      }
   }

   public static void checkCurrentVersionNotRecycled(Version incompatibleImprovements, String logCategory, String configuredClassShortName) {
      if (incompatibleImprovements == Configuration.getVersion()) {
         Logger.getLogger(logCategory).error(configuredClassShortName + ".incompatibleImprovements was set to the object returned by Configuration.getVersion(). That defeats the purpose of incompatibleImprovements, and makes upgrading FreeMarker a potentially breaking change. Also, this probably won't be allowed starting from 2.4.0. Instead, set incompatibleImprovements to the highest concrete version that's known to be compatible with your application.");
      }

   }

   public static int getTemplateLanguageVersionAsInt(TemplateObject to) {
      return getTemplateLanguageVersionAsInt(to.getTemplate());
   }

   public static int getTemplateLanguageVersionAsInt(Template t) {
      return t.getTemplateLanguageVersion().intValue();
   }

   public static void DefaultObjectWrapperFactory_clearInstanceCache() {
      DefaultObjectWrapperBuilder.clearInstanceCache();
   }

   public static TemplateExceptionHandler getDefaultTemplateExceptionHandler(Version incompatibleImprovements) {
      return Configuration.getDefaultTemplateExceptionHandler(incompatibleImprovements);
   }

   public static AttemptExceptionReporter getDefaultAttemptExceptionReporter(Version incompatibleImprovements) {
      return Configuration.getDefaultAttemptExceptionReporter(incompatibleImprovements);
   }

   public static boolean getDefaultLogTemplateExceptions(Version incompatibleImprovements) {
      return Configuration.getDefaultLogTemplateExceptions(incompatibleImprovements);
   }

   public static boolean getDefaultWrapUncheckedExceptions(Version incompatibleImprovements) {
      return Configuration.getDefaultWrapUncheckedExceptions(incompatibleImprovements);
   }

   public static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements) {
      return Configuration.createDefaultTemplateLoader(incompatibleImprovements);
   }

   public static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements) {
      return Configuration.createDefaultCacheStorage(incompatibleImprovements);
   }

   public static TemplateLookupStrategy getDefaultTemplateLookupStrategy(Version incompatibleImprovements) {
      return Configuration.getDefaultTemplateLookupStrategy(incompatibleImprovements);
   }

   public static TemplateNameFormat getDefaultTemplateNameFormat(Version incompatibleImprovements) {
      return Configuration.getDefaultTemplateNameFormat(incompatibleImprovements);
   }

   public static Set getConfigurationSettingNames(Configuration cfg, boolean camelCase) {
      return cfg.getSettingNames(camelCase);
   }

   public static void setAutoEscaping(Template t, boolean autoEscaping) {
      t.setAutoEscaping(autoEscaping);
   }

   public static void setOutputFormat(Template t, OutputFormat outputFormat) {
      t.setOutputFormat(outputFormat);
   }

   public static void validateAutoEscapingPolicyValue(int autoEscaping) {
      if (autoEscaping != 21 && autoEscaping != 22 && autoEscaping != 20) {
         throw new IllegalArgumentException("\"auto_escaping\" can only be set to one of these: Configuration.ENABLE_AUTO_ESCAPING_IF_DEFAULT, or Configuration.ENABLE_AUTO_ESCAPING_IF_SUPPORTEDor Configuration.DISABLE_AUTO_ESCAPING");
      }
   }

   public static void validateNamingConventionValue(int namingConvention) {
      if (namingConvention != 10 && namingConvention != 11 && namingConvention != 12) {
         throw new IllegalArgumentException("\"naming_convention\" can only be set to one of these: Configuration.AUTO_DETECT_NAMING_CONVENTION, or Configuration.LEGACY_NAMING_CONVENTIONor Configuration.CAMEL_CASE_NAMING_CONVENTION");
      }
   }

   public static void valideTagSyntaxValue(int tagSyntax) {
      if (tagSyntax != 0 && tagSyntax != 2 && tagSyntax != 1) {
         throw new IllegalArgumentException("\"tag_syntax\" can only be set to one of these: Configuration.AUTO_DETECT_TAG_SYNTAX, Configuration.ANGLE_BRACKET_TAG_SYNTAX, or Configuration.SQUARE_BRACKET_TAG_SYNTAX");
      }
   }

   public static void valideInterpolationSyntaxValue(int interpolationSyntax) {
      if (interpolationSyntax != 20 && interpolationSyntax != 21 && interpolationSyntax != 22) {
         throw new IllegalArgumentException("\"interpolation_syntax\" can only be set to one of these: Configuration.LEGACY_INTERPOLATION_SYNTAX, Configuration.DOLLAR_INTERPOLATION_SYNTAX, or Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX");
      }
   }

   public static Expression getBlamedExpression(TemplateException e) {
      return e.getBlamedExpression();
   }

   public static Locale getDefaultLocale() {
      return Configuration.getDefaultLocale();
   }

   public static TimeZone getDefaultTimeZone() {
      return Configuration.getDefaultTimeZone();
   }

   public static void setPreventStrippings(Configuration conf, boolean preventStrippings) {
      conf.setPreventStrippings(preventStrippings);
   }

   static {
      VERSION_INT_2_3_0 = Configuration.VERSION_2_3_0.intValue();
      VERSION_INT_2_3_19 = Configuration.VERSION_2_3_19.intValue();
      VERSION_INT_2_3_20 = Configuration.VERSION_2_3_20.intValue();
      VERSION_INT_2_3_21 = Configuration.VERSION_2_3_21.intValue();
      VERSION_INT_2_3_22 = Configuration.VERSION_2_3_22.intValue();
      VERSION_INT_2_3_23 = Configuration.VERSION_2_3_23.intValue();
      VERSION_INT_2_3_24 = Configuration.VERSION_2_3_24.intValue();
      VERSION_INT_2_3_25 = Configuration.VERSION_2_3_25.intValue();
      VERSION_INT_2_3_26 = Configuration.VERSION_2_3_26.intValue();
      VERSION_INT_2_3_27 = Configuration.VERSION_2_3_27.intValue();
      VERSION_INT_2_3_28 = Configuration.VERSION_2_3_28.intValue();
      VERSION_INT_2_3_29 = Configuration.VERSION_2_3_29.intValue();
      VERSION_INT_2_3_30 = Configuration.VERSION_2_3_30.intValue();
      VERSION_INT_2_3_31 = Configuration.VERSION_2_3_31.intValue();
      VERSION_INT_2_4_0 = Version.intValueFor(2, 4, 0);
      SAFE_OBJECT_WRAPPER = new SimpleObjectWrapper(Configuration.VERSION_2_3_0);
      SAFE_OBJECT_WRAPPER.writeProtect();
   }
}
