package freemarker.cache;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.TemplateConfiguration;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class TemplateCache {
   public static final long DEFAULT_TEMPLATE_UPDATE_DELAY_MILLIS = 5000L;
   private static final String ASTERISKSTR = "*";
   private static final char ASTERISK = '*';
   private static final char SLASH = '/';
   private static final String LOCALE_PART_SEPARATOR = "_";
   private static final Logger LOG = Logger.getLogger("freemarker.cache");
   private final TemplateLoader templateLoader;
   private final CacheStorage storage;
   private final TemplateLookupStrategy templateLookupStrategy;
   private final TemplateNameFormat templateNameFormat;
   private final TemplateConfigurationFactory templateConfigurations;
   private final boolean isStorageConcurrent;
   private long updateDelay;
   private boolean localizedLookup;
   private Configuration config;
   private static final Method INIT_CAUSE = getInitCauseMethod();

   /** @deprecated */
   @Deprecated
   public TemplateCache() {
      this(_TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0));
   }

   /** @deprecated */
   @Deprecated
   public TemplateCache(TemplateLoader templateLoader) {
      this(templateLoader, (Configuration)null);
   }

   /** @deprecated */
   @Deprecated
   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage) {
      this(templateLoader, cacheStorage, (Configuration)null);
   }

   public TemplateCache(TemplateLoader templateLoader, Configuration config) {
      this(templateLoader, _TemplateAPI.createDefaultCacheStorage(Configuration.VERSION_2_3_0), config);
   }

   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, Configuration config) {
      this(templateLoader, cacheStorage, _TemplateAPI.getDefaultTemplateLookupStrategy(Configuration.VERSION_2_3_0), _TemplateAPI.getDefaultTemplateNameFormat(Configuration.VERSION_2_3_0), config);
   }

   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, Configuration config) {
      this(templateLoader, cacheStorage, templateLookupStrategy, templateNameFormat, (TemplateConfigurationFactory)null, config);
   }

   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations, Configuration config) {
      this.updateDelay = 5000L;
      this.localizedLookup = true;
      this.templateLoader = templateLoader;
      NullArgumentException.check("cacheStorage", cacheStorage);
      this.storage = cacheStorage;
      this.isStorageConcurrent = cacheStorage instanceof ConcurrentCacheStorage && ((ConcurrentCacheStorage)cacheStorage).isConcurrent();
      NullArgumentException.check("templateLookupStrategy", templateLookupStrategy);
      this.templateLookupStrategy = templateLookupStrategy;
      NullArgumentException.check("templateNameFormat", templateNameFormat);
      this.templateNameFormat = templateNameFormat;
      this.templateConfigurations = templateConfigurations;
      this.config = config;
   }

   /** @deprecated */
   @Deprecated
   public void setConfiguration(Configuration config) {
      this.config = config;
      this.clear();
   }

   public TemplateLoader getTemplateLoader() {
      return this.templateLoader;
   }

   public CacheStorage getCacheStorage() {
      return this.storage;
   }

   public TemplateLookupStrategy getTemplateLookupStrategy() {
      return this.templateLookupStrategy;
   }

   public TemplateNameFormat getTemplateNameFormat() {
      return this.templateNameFormat;
   }

   public TemplateConfigurationFactory getTemplateConfigurations() {
      return this.templateConfigurations;
   }

   public MaybeMissingTemplate getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL) throws IOException {
      NullArgumentException.check("name", name);
      NullArgumentException.check("locale", locale);
      NullArgumentException.check("encoding", encoding);

      try {
         name = this.templateNameFormat.normalizeRootBasedName(name);
      } catch (MalformedTemplateNameException var7) {
         if (this.templateNameFormat == TemplateNameFormat.DEFAULT_2_3_0 && this.config.getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_4_0) {
            return new MaybeMissingTemplate((String)null, var7);
         }

         throw var7;
      }

      if (this.templateLoader == null) {
         return new MaybeMissingTemplate(name, "The TemplateLoader was null.");
      } else {
         Template template = this.getTemplateInternal(name, locale, customLookupCondition, encoding, parseAsFTL);
         return template != null ? new MaybeMissingTemplate(template) : new MaybeMissingTemplate(name, (String)null);
      }
   }

   /** @deprecated */
   @Deprecated
   public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws IOException {
      return this.getTemplate(name, locale, (Object)null, encoding, parseAsFTL).getTemplate();
   }

   /** @deprecated */
   @Deprecated
   protected static TemplateLoader createLegacyDefaultTemplateLoader() {
      return _TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0);
   }

   private Template getTemplateInternal(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL) throws IOException {
      boolean debug = LOG.isDebugEnabled();
      String debugName = debug ? this.buildDebugName(name, locale, customLookupCondition, encoding, parseAsFTL) : null;
      TemplateKey tk = new TemplateKey(name, locale, customLookupCondition, encoding, parseAsFTL);
      CachedTemplate cachedTemplate;
      if (this.isStorageConcurrent) {
         cachedTemplate = (CachedTemplate)this.storage.get(tk);
      } else {
         synchronized(this.storage) {
            cachedTemplate = (CachedTemplate)this.storage.get(tk);
         }
      }

      long now = System.currentTimeMillis();
      long lastModified = -1L;
      boolean rethrown = false;
      TemplateLookupResult newLookupResult = null;

      Template var30;
      try {
         Object newLookupResultSource;
         Template template;
         if (cachedTemplate != null) {
            if (now - cachedTemplate.lastChecked < this.updateDelay) {
               if (debug) {
                  LOG.debug(debugName + " cached copy not yet stale; using cached.");
               }

               newLookupResultSource = cachedTemplate.templateOrException;
               if (!(newLookupResultSource instanceof Template) && newLookupResultSource != null) {
                  if (newLookupResultSource instanceof RuntimeException) {
                     this.throwLoadFailedException((RuntimeException)newLookupResultSource);
                  } else if (newLookupResultSource instanceof IOException) {
                     rethrown = true;
                     this.throwLoadFailedException((IOException)newLookupResultSource);
                  }

                  throw new BugException("t is " + newLookupResultSource.getClass().getName());
               }

               template = (Template)newLookupResultSource;
               return template;
            }

            cachedTemplate = cachedTemplate.cloneCachedTemplate();
            cachedTemplate.lastChecked = now;
            newLookupResult = this.lookupTemplate(name, locale, customLookupCondition);
            if (!newLookupResult.isPositive()) {
               if (debug) {
                  LOG.debug(debugName + " no source found.");
               }

               this.storeNegativeLookup(tk, cachedTemplate, (Exception)null);
               newLookupResultSource = null;
               return (Template)newLookupResultSource;
            }

            newLookupResultSource = newLookupResult.getTemplateSource();
            lastModified = this.templateLoader.getLastModified(newLookupResultSource);
            boolean lastModifiedNotChanged = lastModified == cachedTemplate.lastModified;
            boolean sourceEquals = newLookupResultSource.equals(cachedTemplate.source);
            if (lastModifiedNotChanged && sourceEquals) {
               if (debug) {
                  LOG.debug(debugName + ": using cached since " + newLookupResultSource + " hasn't changed.");
               }

               this.storeCached(tk, cachedTemplate);
               Template var19 = (Template)cachedTemplate.templateOrException;
               return var19;
            }

            if (debug) {
               if (!sourceEquals) {
                  LOG.debug("Updating source because: sourceEquals=" + sourceEquals + ", newlyFoundSource=" + StringUtil.jQuoteNoXSS(newLookupResultSource) + ", cached.source=" + StringUtil.jQuoteNoXSS(cachedTemplate.source));
               } else if (!lastModifiedNotChanged) {
                  LOG.debug("Updating source because: lastModifiedNotChanged=" + lastModifiedNotChanged + ", cached.lastModified=" + cachedTemplate.lastModified + " != source.lastModified=" + lastModified);
               }
            }
         } else {
            if (debug) {
               LOG.debug("Couldn't find template in cache for " + debugName + "; will try to load it.");
            }

            cachedTemplate = new CachedTemplate();
            cachedTemplate.lastChecked = now;
            newLookupResult = this.lookupTemplate(name, locale, customLookupCondition);
            if (!newLookupResult.isPositive()) {
               this.storeNegativeLookup(tk, cachedTemplate, (Exception)null);
               newLookupResultSource = null;
               return (Template)newLookupResultSource;
            }

            lastModified = Long.MIN_VALUE;
            cachedTemplate.lastModified = Long.MIN_VALUE;
         }

         newLookupResultSource = newLookupResult.getTemplateSource();
         cachedTemplate.source = newLookupResultSource;
         if (debug) {
            LOG.debug("Loading template for " + debugName + " from " + StringUtil.jQuoteNoXSS(newLookupResultSource));
         }

         lastModified = lastModified == Long.MIN_VALUE ? this.templateLoader.getLastModified(newLookupResultSource) : lastModified;
         template = this.loadTemplate(this.templateLoader, newLookupResultSource, name, newLookupResult.getTemplateSourceName(), locale, customLookupCondition, encoding, parseAsFTL);
         cachedTemplate.templateOrException = template;
         cachedTemplate.lastModified = lastModified;
         this.storeCached(tk, cachedTemplate);
         var30 = template;
      } catch (RuntimeException var26) {
         if (cachedTemplate != null) {
            this.storeNegativeLookup(tk, cachedTemplate, var26);
         }

         throw var26;
      } catch (IOException var27) {
         if (!rethrown) {
            this.storeNegativeLookup(tk, cachedTemplate, var27);
         }

         throw var27;
      } finally {
         if (newLookupResult != null && newLookupResult.isPositive()) {
            this.templateLoader.closeTemplateSource(newLookupResult.getTemplateSource());
         }

      }

      return var30;
   }

   private static final Method getInitCauseMethod() {
      try {
         return Throwable.class.getMethod("initCause", Throwable.class);
      } catch (NoSuchMethodException var1) {
         return null;
      }
   }

   private IOException newIOException(String message, Throwable cause) {
      if (cause == null) {
         return new IOException(message);
      } else {
         IOException ioe;
         if (INIT_CAUSE != null) {
            ioe = new IOException(message);

            try {
               INIT_CAUSE.invoke(ioe, cause);
            } catch (RuntimeException var5) {
               throw var5;
            } catch (Exception var6) {
               throw new UndeclaredThrowableException(var6);
            }
         } else {
            ioe = new IOException(message + "\nCaused by: " + cause.getClass().getName() + ": " + cause.getMessage());
         }

         return ioe;
      }
   }

   private void throwLoadFailedException(Throwable e) throws IOException {
      throw this.newIOException("There was an error loading the template on an earlier attempt; see cause exception.", e);
   }

   private void storeNegativeLookup(TemplateKey tk, CachedTemplate cachedTemplate, Exception e) {
      cachedTemplate.templateOrException = e;
      cachedTemplate.source = null;
      cachedTemplate.lastModified = 0L;
      this.storeCached(tk, cachedTemplate);
   }

   private void storeCached(TemplateKey tk, CachedTemplate cachedTemplate) {
      if (this.isStorageConcurrent) {
         this.storage.put(tk, cachedTemplate);
      } else {
         synchronized(this.storage) {
            this.storage.put(tk, cachedTemplate);
         }
      }

   }

   private Template loadTemplate(TemplateLoader templateLoader, Object source, String name, String sourceName, Locale locale, Object customLookupCondition, String initialEncoding, boolean parseAsFTL) throws IOException {
      TemplateConfiguration tc;
      try {
         tc = this.templateConfigurations != null ? this.templateConfigurations.get(sourceName, source) : null;
      } catch (TemplateConfigurationFactoryException var64) {
         throw this.newIOException("Error while getting TemplateConfiguration; see cause exception.", var64);
      }

      if (tc != null) {
         if (tc.isEncodingSet()) {
            initialEncoding = tc.getEncoding();
         }

         if (tc.isLocaleSet()) {
            locale = tc.getLocale();
         }
      }

      Template template;
      Reader reader;
      Throwable var14;
      if (parseAsFTL) {
         try {
            Reader reader = templateLoader.getReader(source, initialEncoding);
            Throwable var71 = null;

            try {
               template = new Template(name, sourceName, reader, this.config, tc, initialEncoding);
            } catch (Throwable var63) {
               var71 = var63;
               throw var63;
            } finally {
               if (reader != null) {
                  if (var71 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var62) {
                        var71.addSuppressed(var62);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (Template.WrongEncodingException var67) {
            String actualEncoding = var67.getTemplateSpecifiedEncoding();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Initial encoding \"" + initialEncoding + "\" was incorrect, re-reading with \"" + actualEncoding + "\". Template: " + sourceName);
            }

            reader = templateLoader.getReader(source, actualEncoding);
            var14 = null;

            try {
               template = new Template(name, sourceName, reader, this.config, tc, actualEncoding);
            } catch (Throwable var61) {
               var14 = var61;
               throw var61;
            } finally {
               if (reader != null) {
                  if (var14 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var60) {
                        var14.addSuppressed(var60);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         }
      } else {
         StringWriter sw = new StringWriter();
         char[] buf = new char[4096];
         reader = templateLoader.getReader(source, initialEncoding);
         var14 = null;

         try {
            label503:
            while(true) {
               while(true) {
                  int charsRead = reader.read(buf);
                  if (charsRead > 0) {
                     sw.write(buf, 0, charsRead);
                  } else if (charsRead < 0) {
                     break label503;
                  }
               }
            }
         } catch (Throwable var68) {
            var14 = var68;
            throw var68;
         } finally {
            if (reader != null) {
               if (var14 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var59) {
                     var14.addSuppressed(var59);
                  }
               } else {
                  reader.close();
               }
            }

         }

         template = Template.getPlainTextTemplate(name, sourceName, sw.toString(), this.config);
         template.setEncoding(initialEncoding);
      }

      if (tc != null) {
         tc.apply(template);
      }

      template.setLocale(locale);
      template.setCustomLookupCondition(customLookupCondition);
      return template;
   }

   public long getDelay() {
      synchronized(this) {
         return this.updateDelay;
      }
   }

   public void setDelay(long delay) {
      synchronized(this) {
         this.updateDelay = delay;
      }
   }

   public boolean getLocalizedLookup() {
      synchronized(this) {
         return this.localizedLookup;
      }
   }

   public void setLocalizedLookup(boolean localizedLookup) {
      synchronized(this) {
         if (this.localizedLookup != localizedLookup) {
            this.localizedLookup = localizedLookup;
            this.clear();
         }

      }
   }

   public void clear() {
      synchronized(this.storage) {
         this.storage.clear();
         if (this.templateLoader instanceof StatefulTemplateLoader) {
            ((StatefulTemplateLoader)this.templateLoader).resetState();
         }

      }
   }

   public void removeTemplate(String name, Locale locale, String encoding, boolean parse) throws IOException {
      this.removeTemplate(name, locale, (Object)null, encoding, parse);
   }

   public void removeTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
      if (name == null) {
         throw new IllegalArgumentException("Argument \"name\" can't be null");
      } else if (locale == null) {
         throw new IllegalArgumentException("Argument \"locale\" can't be null");
      } else if (encoding == null) {
         throw new IllegalArgumentException("Argument \"encoding\" can't be null");
      } else {
         name = this.templateNameFormat.normalizeRootBasedName(name);
         if (name != null && this.templateLoader != null) {
            boolean debug = LOG.isDebugEnabled();
            String debugName = debug ? this.buildDebugName(name, locale, customLookupCondition, encoding, parse) : null;
            TemplateKey tk = new TemplateKey(name, locale, customLookupCondition, encoding, parse);
            if (this.isStorageConcurrent) {
               this.storage.remove(tk);
            } else {
               synchronized(this.storage) {
                  this.storage.remove(tk);
               }
            }

            if (debug) {
               LOG.debug(debugName + " was removed from the cache, if it was there");
            }
         }

      }
   }

   private String buildDebugName(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
      return StringUtil.jQuoteNoXSS(name) + "(" + StringUtil.jQuoteNoXSS((Object)locale) + (customLookupCondition != null ? ", cond=" + StringUtil.jQuoteNoXSS(customLookupCondition) : "") + ", " + encoding + (parse ? ", parsed)" : ", unparsed]");
   }

   /** @deprecated */
   @Deprecated
   public static String getFullTemplatePath(Environment env, String baseName, String targetName) {
      try {
         return env.toFullTemplateName(baseName, targetName);
      } catch (MalformedTemplateNameException var4) {
         throw new IllegalArgumentException(var4.getMessage());
      }
   }

   private TemplateLookupResult lookupTemplate(String name, Locale locale, Object customLookupCondition) throws IOException {
      TemplateLookupResult lookupResult = this.templateLookupStrategy.lookup(new TemplateCacheTemplateLookupContext(name, locale, customLookupCondition));
      if (lookupResult == null) {
         throw new NullPointerException("Lookup result shouldn't be null");
      } else {
         return lookupResult;
      }
   }

   private TemplateLookupResult lookupTemplateWithAcquisitionStrategy(String path) throws IOException {
      int asterisk = path.indexOf(42);
      if (asterisk == -1) {
         return TemplateLookupResult.from(path, this.findTemplateSource(path));
      } else {
         StringTokenizer tok = new StringTokenizer(path, "/");
         int lastAsterisk = -1;

         ArrayList tokpath;
         String basePath;
         for(tokpath = new ArrayList(); tok.hasMoreTokens(); tokpath.add(basePath)) {
            basePath = tok.nextToken();
            if (basePath.equals("*")) {
               if (lastAsterisk != -1) {
                  tokpath.remove(lastAsterisk);
               }

               lastAsterisk = tokpath.size();
            }
         }

         if (lastAsterisk == -1) {
            return TemplateLookupResult.from(path, this.findTemplateSource(path));
         } else {
            basePath = this.concatPath(tokpath, 0, lastAsterisk);
            String resourcePath = this.concatPath(tokpath, lastAsterisk + 1, tokpath.size());
            if (resourcePath.endsWith("/")) {
               resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
            }

            StringBuilder buf = (new StringBuilder(path.length())).append(basePath);
            int l = basePath.length();

            while(true) {
               String fullPath = buf.append(resourcePath).toString();
               Object templateSource = this.findTemplateSource(fullPath);
               if (templateSource != null) {
                  return TemplateLookupResult.from(fullPath, templateSource);
               }

               if (l == 0) {
                  return TemplateLookupResult.createNegativeResult();
               }

               l = basePath.lastIndexOf(47, l - 2) + 1;
               buf.setLength(l);
            }
         }
      }
   }

   private Object findTemplateSource(String path) throws IOException {
      Object result = this.templateLoader.findTemplateSource(path);
      if (LOG.isDebugEnabled()) {
         LOG.debug("TemplateLoader.findTemplateSource(" + StringUtil.jQuote(path) + "): " + (result == null ? "Not found" : "Found"));
      }

      return this.modifyForConfIcI(result);
   }

   private Object modifyForConfIcI(Object templateSource) {
      if (templateSource == null) {
         return null;
      } else if (this.config.getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_21) {
         return templateSource;
      } else {
         if (templateSource instanceof URLTemplateSource) {
            URLTemplateSource urlTemplateSource = (URLTemplateSource)templateSource;
            if (urlTemplateSource.getUseCaches() == null) {
               urlTemplateSource.setUseCaches(false);
            }
         } else if (templateSource instanceof MultiTemplateLoader.MultiSource) {
            this.modifyForConfIcI(((MultiTemplateLoader.MultiSource)templateSource).getWrappedSource());
         }

         return templateSource;
      }
   }

   private String concatPath(List path, int from, int to) {
      StringBuilder buf = new StringBuilder((to - from) * 16);

      for(int i = from; i < to; ++i) {
         buf.append(path.get(i)).append('/');
      }

      return buf.toString();
   }

   public static final class MaybeMissingTemplate {
      private final Template template;
      private final String missingTemplateNormalizedName;
      private final String missingTemplateReason;
      private final MalformedTemplateNameException missingTemplateCauseException;

      private MaybeMissingTemplate(Template template) {
         this.template = template;
         this.missingTemplateNormalizedName = null;
         this.missingTemplateReason = null;
         this.missingTemplateCauseException = null;
      }

      private MaybeMissingTemplate(String normalizedName, MalformedTemplateNameException missingTemplateCauseException) {
         this.template = null;
         this.missingTemplateNormalizedName = normalizedName;
         this.missingTemplateReason = null;
         this.missingTemplateCauseException = missingTemplateCauseException;
      }

      private MaybeMissingTemplate(String normalizedName, String missingTemplateReason) {
         this.template = null;
         this.missingTemplateNormalizedName = normalizedName;
         this.missingTemplateReason = missingTemplateReason;
         this.missingTemplateCauseException = null;
      }

      public Template getTemplate() {
         return this.template;
      }

      public String getMissingTemplateReason() {
         return this.missingTemplateReason != null ? this.missingTemplateReason : (this.missingTemplateCauseException != null ? this.missingTemplateCauseException.getMalformednessDescription() : null);
      }

      public String getMissingTemplateNormalizedName() {
         return this.missingTemplateNormalizedName;
      }

      // $FF: synthetic method
      MaybeMissingTemplate(String x0, MalformedTemplateNameException x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      MaybeMissingTemplate(String x0, String x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      MaybeMissingTemplate(Template x0, Object x1) {
         this(x0);
      }
   }

   private class TemplateCacheTemplateLookupContext extends TemplateLookupContext {
      TemplateCacheTemplateLookupContext(String templateName, Locale templateLocale, Object customLookupCondition) {
         super(templateName, TemplateCache.this.localizedLookup ? templateLocale : null, customLookupCondition);
      }

      public TemplateLookupResult lookupWithAcquisitionStrategy(String name) throws IOException {
         if (name.startsWith("/")) {
            throw new IllegalArgumentException("Non-normalized name, starts with \"/\": " + name);
         } else {
            return TemplateCache.this.lookupTemplateWithAcquisitionStrategy(name);
         }
      }

      public TemplateLookupResult lookupWithLocalizedThenAcquisitionStrategy(String templateName, Locale templateLocale) throws IOException {
         if (templateLocale == null) {
            return this.lookupWithAcquisitionStrategy(templateName);
         } else {
            int lastDot = templateName.lastIndexOf(46);
            String prefix = lastDot == -1 ? templateName : templateName.substring(0, lastDot);
            String suffix = lastDot == -1 ? "" : templateName.substring(lastDot);
            String localeName = "_" + templateLocale.toString();
            StringBuilder buf = new StringBuilder(templateName.length() + localeName.length());
            buf.append(prefix);

            while(true) {
               buf.setLength(prefix.length());
               String path = buf.append(localeName).append(suffix).toString();
               TemplateLookupResult lookupResult = this.lookupWithAcquisitionStrategy(path);
               if (lookupResult.isPositive()) {
                  return lookupResult;
               }

               int lastUnderscore = localeName.lastIndexOf(95);
               if (lastUnderscore == -1) {
                  return this.createNegativeLookupResult();
               }

               localeName = localeName.substring(0, lastUnderscore);
            }
         }
      }
   }

   private static final class CachedTemplate implements Cloneable, Serializable {
      private static final long serialVersionUID = 1L;
      Object templateOrException;
      Object source;
      long lastChecked;
      long lastModified;

      private CachedTemplate() {
      }

      public CachedTemplate cloneCachedTemplate() {
         try {
            return (CachedTemplate)super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new UndeclaredThrowableException(var2);
         }
      }

      // $FF: synthetic method
      CachedTemplate(Object x0) {
         this();
      }
   }

   private static final class TemplateKey {
      private final String name;
      private final Locale locale;
      private final Object customLookupCondition;
      private final String encoding;
      private final boolean parse;

      TemplateKey(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
         this.name = name;
         this.locale = locale;
         this.customLookupCondition = customLookupCondition;
         this.encoding = encoding;
         this.parse = parse;
      }

      public boolean equals(Object o) {
         if (!(o instanceof TemplateKey)) {
            return false;
         } else {
            TemplateKey tk = (TemplateKey)o;
            return this.parse == tk.parse && this.name.equals(tk.name) && this.locale.equals(tk.locale) && this.nullSafeEquals(this.customLookupCondition, tk.customLookupCondition) && this.encoding.equals(tk.encoding);
         }
      }

      private boolean nullSafeEquals(Object o1, Object o2) {
         return o1 != null ? (o2 != null ? o1.equals(o2) : false) : o2 == null;
      }

      public int hashCode() {
         return this.name.hashCode() ^ this.locale.hashCode() ^ this.encoding.hashCode() ^ (this.customLookupCondition != null ? this.customLookupCondition.hashCode() : 0) ^ Boolean.valueOf(!this.parse).hashCode();
      }
   }
}
