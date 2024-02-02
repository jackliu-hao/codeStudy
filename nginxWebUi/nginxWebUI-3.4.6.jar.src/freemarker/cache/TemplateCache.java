/*      */ package freemarker.cache;
/*      */ 
/*      */ import freemarker.core.BugException;
/*      */ import freemarker.core.Environment;
/*      */ import freemarker.core.ParserConfiguration;
/*      */ import freemarker.core.TemplateConfiguration;
/*      */ import freemarker.log.Logger;
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template.MalformedTemplateNameException;
/*      */ import freemarker.template.Template;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import freemarker.template.utility.NullArgumentException;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import freemarker.template.utility.UndeclaredThrowableException;
/*      */ import java.io.IOException;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.StringWriter;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TemplateCache
/*      */ {
/*      */   public static final long DEFAULT_TEMPLATE_UPDATE_DELAY_MILLIS = 5000L;
/*      */   private static final String ASTERISKSTR = "*";
/*      */   private static final char ASTERISK = '*';
/*      */   private static final char SLASH = '/';
/*      */   private static final String LOCALE_PART_SEPARATOR = "_";
/*   71 */   private static final Logger LOG = Logger.getLogger("freemarker.cache");
/*      */   
/*      */   private final TemplateLoader templateLoader;
/*      */   
/*      */   private final CacheStorage storage;
/*      */   
/*      */   private final TemplateLookupStrategy templateLookupStrategy;
/*      */   
/*      */   private final TemplateNameFormat templateNameFormat;
/*      */   
/*      */   private final TemplateConfigurationFactory templateConfigurations;
/*      */   
/*      */   private final boolean isStorageConcurrent;
/*   84 */   private long updateDelay = 5000L;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean localizedLookup = true;
/*      */ 
/*      */ 
/*      */   
/*      */   private Configuration config;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public TemplateCache() {
/*  100 */     this(_TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public TemplateCache(TemplateLoader templateLoader) {
/*  108 */     this(templateLoader, (Configuration)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage) {
/*  116 */     this(templateLoader, cacheStorage, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateCache(TemplateLoader templateLoader, Configuration config) {
/*  126 */     this(templateLoader, _TemplateAPI.createDefaultCacheStorage(Configuration.VERSION_2_3_0), config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, Configuration config) {
/*  137 */     this(templateLoader, cacheStorage, 
/*  138 */         _TemplateAPI.getDefaultTemplateLookupStrategy(Configuration.VERSION_2_3_0), 
/*  139 */         _TemplateAPI.getDefaultTemplateNameFormat(Configuration.VERSION_2_3_0), config);
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
/*      */   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, Configuration config) {
/*  153 */     this(templateLoader, cacheStorage, templateLookupStrategy, templateNameFormat, null, config);
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
/*      */   public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations, Configuration config) {
/*  179 */     this.templateLoader = templateLoader;
/*      */     
/*  181 */     NullArgumentException.check("cacheStorage", cacheStorage);
/*  182 */     this.storage = cacheStorage;
/*  183 */     this
/*  184 */       .isStorageConcurrent = (cacheStorage instanceof ConcurrentCacheStorage && ((ConcurrentCacheStorage)cacheStorage).isConcurrent());
/*      */     
/*  186 */     NullArgumentException.check("templateLookupStrategy", templateLookupStrategy);
/*  187 */     this.templateLookupStrategy = templateLookupStrategy;
/*      */     
/*  189 */     NullArgumentException.check("templateNameFormat", templateNameFormat);
/*  190 */     this.templateNameFormat = templateNameFormat;
/*      */ 
/*      */     
/*  193 */     this.templateConfigurations = templateConfigurations;
/*      */     
/*  195 */     this.config = config;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setConfiguration(Configuration config) {
/*  207 */     this.config = config;
/*  208 */     clear();
/*      */   }
/*      */   
/*      */   public TemplateLoader getTemplateLoader() {
/*  212 */     return this.templateLoader;
/*      */   }
/*      */   
/*      */   public CacheStorage getCacheStorage() {
/*  216 */     return this.storage;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateLookupStrategy getTemplateLookupStrategy() {
/*  223 */     return this.templateLookupStrategy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateNameFormat getTemplateNameFormat() {
/*  230 */     return this.templateNameFormat;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TemplateConfigurationFactory getTemplateConfigurations() {
/*  237 */     return this.templateConfigurations;
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
/*      */   public MaybeMissingTemplate getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL) throws IOException {
/*  273 */     NullArgumentException.check("name", name);
/*  274 */     NullArgumentException.check("locale", locale);
/*  275 */     NullArgumentException.check("encoding", encoding);
/*      */     
/*      */     try {
/*  278 */       name = this.templateNameFormat.normalizeRootBasedName(name);
/*  279 */     } catch (MalformedTemplateNameException e) {
/*      */       
/*  281 */       if (this.templateNameFormat != TemplateNameFormat.DEFAULT_2_3_0 || this.config
/*  282 */         .getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_4_0) {
/*  283 */         throw e;
/*      */       }
/*  285 */       return new MaybeMissingTemplate(null, e);
/*      */     } 
/*      */     
/*  288 */     if (this.templateLoader == null) {
/*  289 */       return new MaybeMissingTemplate(name, "The TemplateLoader was null.");
/*      */     }
/*      */     
/*  292 */     Template template = getTemplateInternal(name, locale, customLookupCondition, encoding, parseAsFTL);
/*  293 */     return (template != null) ? new MaybeMissingTemplate(template) : new MaybeMissingTemplate(name, (String)null);
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
/*      */   public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws IOException {
/*  309 */     return getTemplate(name, locale, null, encoding, parseAsFTL).getTemplate();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   protected static TemplateLoader createLegacyDefaultTemplateLoader() {
/*  319 */     return _TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Template getTemplateInternal(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL) throws IOException {
/*      */     CachedTemplate cachedTemplate;
/*  326 */     boolean debug = LOG.isDebugEnabled();
/*      */     
/*  328 */     String debugName = debug ? buildDebugName(name, locale, customLookupCondition, encoding, parseAsFTL) : null;
/*      */     
/*  330 */     TemplateKey tk = new TemplateKey(name, locale, customLookupCondition, encoding, parseAsFTL);
/*      */ 
/*      */     
/*  333 */     if (this.isStorageConcurrent) {
/*  334 */       cachedTemplate = (CachedTemplate)this.storage.get(tk);
/*      */     } else {
/*  336 */       synchronized (this.storage) {
/*  337 */         cachedTemplate = (CachedTemplate)this.storage.get(tk);
/*      */       } 
/*      */     } 
/*      */     
/*  341 */     long now = System.currentTimeMillis();
/*      */     
/*  343 */     long lastModified = -1L;
/*  344 */     boolean rethrown = false;
/*  345 */     TemplateLookupResult newLookupResult = null;
/*      */     try {
/*  347 */       if (cachedTemplate != null) {
/*      */         
/*  349 */         if (now - cachedTemplate.lastChecked < this.updateDelay) {
/*  350 */           if (debug) {
/*  351 */             LOG.debug(debugName + " cached copy not yet stale; using cached.");
/*      */           }
/*      */           
/*  354 */           Object t = cachedTemplate.templateOrException;
/*  355 */           if (t instanceof Template || t == null)
/*  356 */             return (Template)t; 
/*  357 */           if (t instanceof RuntimeException) {
/*  358 */             throwLoadFailedException((RuntimeException)t);
/*  359 */           } else if (t instanceof IOException) {
/*  360 */             rethrown = true;
/*  361 */             throwLoadFailedException((IOException)t);
/*      */           } 
/*  363 */           throw new BugException("t is " + t.getClass().getName());
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  368 */         cachedTemplate = cachedTemplate.cloneCachedTemplate();
/*      */         
/*  370 */         cachedTemplate.lastChecked = now;
/*      */ 
/*      */         
/*  373 */         newLookupResult = lookupTemplate(name, locale, customLookupCondition);
/*      */ 
/*      */         
/*  376 */         if (!newLookupResult.isPositive()) {
/*  377 */           if (debug) {
/*  378 */             LOG.debug(debugName + " no source found.");
/*      */           }
/*  380 */           storeNegativeLookup(tk, cachedTemplate, null);
/*  381 */           return null;
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  386 */         Object newLookupResultSource = newLookupResult.getTemplateSource();
/*  387 */         lastModified = this.templateLoader.getLastModified(newLookupResultSource);
/*  388 */         boolean lastModifiedNotChanged = (lastModified == cachedTemplate.lastModified);
/*  389 */         boolean sourceEquals = newLookupResultSource.equals(cachedTemplate.source);
/*  390 */         if (lastModifiedNotChanged && sourceEquals) {
/*  391 */           if (debug) {
/*  392 */             LOG.debug(debugName + ": using cached since " + newLookupResultSource + " hasn't changed.");
/*      */           }
/*  394 */           storeCached(tk, cachedTemplate);
/*  395 */           return (Template)cachedTemplate.templateOrException;
/*  396 */         }  if (debug) {
/*  397 */           if (!sourceEquals) {
/*  398 */             LOG.debug("Updating source because: sourceEquals=" + sourceEquals + ", newlyFoundSource=" + 
/*      */                 
/*  400 */                 StringUtil.jQuoteNoXSS(newLookupResultSource) + ", cached.source=" + 
/*  401 */                 StringUtil.jQuoteNoXSS(cachedTemplate.source));
/*  402 */           } else if (!lastModifiedNotChanged) {
/*  403 */             LOG.debug("Updating source because: lastModifiedNotChanged=" + lastModifiedNotChanged + ", cached.lastModified=" + cachedTemplate.lastModified + " != source.lastModified=" + lastModified);
/*      */           }
/*      */         
/*      */         }
/*      */       }
/*      */       else {
/*      */         
/*  410 */         if (debug) {
/*  411 */           LOG.debug("Couldn't find template in cache for " + debugName + "; will try to load it.");
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  417 */         cachedTemplate = new CachedTemplate();
/*  418 */         cachedTemplate.lastChecked = now;
/*      */         
/*  420 */         newLookupResult = lookupTemplate(name, locale, customLookupCondition);
/*      */         
/*  422 */         if (!newLookupResult.isPositive()) {
/*  423 */           storeNegativeLookup(tk, cachedTemplate, null);
/*  424 */           return null;
/*      */         } 
/*      */         
/*  427 */         cachedTemplate.lastModified = lastModified = Long.MIN_VALUE;
/*      */       } 
/*      */       
/*  430 */       Object source = newLookupResult.getTemplateSource();
/*  431 */       cachedTemplate.source = source;
/*      */ 
/*      */       
/*  434 */       if (debug) {
/*  435 */         LOG.debug("Loading template for " + debugName + " from " + StringUtil.jQuoteNoXSS(source));
/*      */       }
/*      */       
/*  438 */       lastModified = (lastModified == Long.MIN_VALUE) ? this.templateLoader.getLastModified(source) : lastModified;
/*  439 */       Template template = loadTemplate(this.templateLoader, source, name, newLookupResult
/*      */           
/*  441 */           .getTemplateSourceName(), locale, customLookupCondition, encoding, parseAsFTL);
/*      */       
/*  443 */       cachedTemplate.templateOrException = template;
/*  444 */       cachedTemplate.lastModified = lastModified;
/*  445 */       storeCached(tk, cachedTemplate);
/*  446 */       return template;
/*  447 */     } catch (RuntimeException e) {
/*  448 */       if (cachedTemplate != null) {
/*  449 */         storeNegativeLookup(tk, cachedTemplate, e);
/*      */       }
/*  451 */       throw e;
/*  452 */     } catch (IOException e) {
/*  453 */       if (!rethrown) {
/*  454 */         storeNegativeLookup(tk, cachedTemplate, e);
/*      */       }
/*  456 */       throw e;
/*      */     } finally {
/*  458 */       if (newLookupResult != null && newLookupResult.isPositive()) {
/*  459 */         this.templateLoader.closeTemplateSource(newLookupResult.getTemplateSource());
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*  464 */   private static final Method INIT_CAUSE = getInitCauseMethod();
/*      */   
/*      */   private static final Method getInitCauseMethod() {
/*      */     try {
/*  468 */       return Throwable.class.getMethod("initCause", new Class[] { Throwable.class });
/*  469 */     } catch (NoSuchMethodException e) {
/*  470 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IOException newIOException(String message, Throwable cause) {
/*      */     IOException ioe;
/*  479 */     if (cause == null) {
/*  480 */       return new IOException(message);
/*      */     }
/*      */ 
/*      */     
/*  484 */     if (INIT_CAUSE != null) {
/*  485 */       ioe = new IOException(message);
/*      */       try {
/*  487 */         INIT_CAUSE.invoke(ioe, new Object[] { cause });
/*  488 */       } catch (RuntimeException ex) {
/*  489 */         throw ex;
/*  490 */       } catch (Exception ex) {
/*  491 */         throw new UndeclaredThrowableException(ex);
/*      */       } 
/*      */     } else {
/*      */       
/*  495 */       ioe = new IOException(message + "\nCaused by: " + cause.getClass().getName() + ": " + cause.getMessage());
/*      */     } 
/*  497 */     return ioe;
/*      */   }
/*      */   
/*      */   private void throwLoadFailedException(Throwable e) throws IOException {
/*  501 */     throw newIOException("There was an error loading the template on an earlier attempt; see cause exception.", e);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void storeNegativeLookup(TemplateKey tk, CachedTemplate cachedTemplate, Exception e) {
/*  507 */     cachedTemplate.templateOrException = e;
/*  508 */     cachedTemplate.source = null;
/*  509 */     cachedTemplate.lastModified = 0L;
/*  510 */     storeCached(tk, cachedTemplate);
/*      */   }
/*      */   
/*      */   private void storeCached(TemplateKey tk, CachedTemplate cachedTemplate) {
/*  514 */     if (this.isStorageConcurrent) {
/*  515 */       this.storage.put(tk, cachedTemplate);
/*      */     } else {
/*  517 */       synchronized (this.storage) {
/*  518 */         this.storage.put(tk, cachedTemplate);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Template loadTemplate(TemplateLoader templateLoader, Object source, String name, String sourceName, Locale locale, Object customLookupCondition, String initialEncoding, boolean parseAsFTL) throws IOException {
/*      */     TemplateConfiguration tc;
/*      */     Template template;
/*      */     try {
/*  529 */       tc = (this.templateConfigurations != null) ? this.templateConfigurations.get(sourceName, source) : null;
/*  530 */     } catch (TemplateConfigurationFactoryException e) {
/*  531 */       throw newIOException("Error while getting TemplateConfiguration; see cause exception.", e);
/*      */     } 
/*  533 */     if (tc != null) {
/*      */       
/*  535 */       if (tc.isEncodingSet()) {
/*  536 */         initialEncoding = tc.getEncoding();
/*      */       }
/*  538 */       if (tc.isLocaleSet()) {
/*  539 */         locale = tc.getLocale();
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  545 */     if (parseAsFTL) {
/*      */       
/*  547 */       try (Reader reader = templateLoader.getReader(source, initialEncoding)) {
/*  548 */         template = new Template(name, sourceName, reader, this.config, (ParserConfiguration)tc, initialEncoding);
/*      */       }
/*  550 */       catch (freemarker.template.Template.WrongEncodingException wee) {
/*  551 */         String actualEncoding = wee.getTemplateSpecifiedEncoding();
/*  552 */         if (LOG.isDebugEnabled()) {
/*  553 */           LOG.debug("Initial encoding \"" + initialEncoding + "\" was incorrect, re-reading with \"" + actualEncoding + "\". Template: " + sourceName);
/*      */         }
/*      */ 
/*      */         
/*  557 */         try (Reader reader = templateLoader.getReader(source, actualEncoding)) {
/*  558 */           template = new Template(name, sourceName, reader, this.config, (ParserConfiguration)tc, actualEncoding);
/*      */         } 
/*      */       } 
/*      */     } else {
/*      */       
/*  563 */       StringWriter sw = new StringWriter();
/*  564 */       char[] buf = new char[4096];
/*  565 */       try (Reader reader = templateLoader.getReader(source, initialEncoding)) {
/*      */         
/*      */         while (true) {
/*  568 */           int charsRead = reader.read(buf);
/*  569 */           if (charsRead > 0) {
/*  570 */             sw.write(buf, 0, charsRead); continue;
/*  571 */           }  if (charsRead < 0) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */       } 
/*  576 */       template = Template.getPlainTextTemplate(name, sourceName, sw.toString(), this.config);
/*  577 */       template.setEncoding(initialEncoding);
/*      */     } 
/*      */ 
/*      */     
/*  581 */     if (tc != null) {
/*  582 */       tc.apply(template);
/*      */     }
/*      */     
/*  585 */     template.setLocale(locale);
/*  586 */     template.setCustomLookupCondition(customLookupCondition);
/*  587 */     return template;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getDelay() {
/*  597 */     synchronized (this) {
/*  598 */       return this.updateDelay;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDelay(long delay) {
/*  609 */     synchronized (this) {
/*  610 */       this.updateDelay = delay;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getLocalizedLookup() {
/*  619 */     synchronized (this) {
/*  620 */       return this.localizedLookup;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLocalizedLookup(boolean localizedLookup) {
/*  629 */     synchronized (this) {
/*  630 */       if (this.localizedLookup != localizedLookup) {
/*  631 */         this.localizedLookup = localizedLookup;
/*  632 */         clear();
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
/*      */   public void clear() {
/*  645 */     synchronized (this.storage) {
/*  646 */       this.storage.clear();
/*  647 */       if (this.templateLoader instanceof StatefulTemplateLoader) {
/*  648 */         ((StatefulTemplateLoader)this.templateLoader).resetState();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeTemplate(String name, Locale locale, String encoding, boolean parse) throws IOException {
/*  659 */     removeTemplate(name, locale, null, encoding, parse);
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
/*      */   public void removeTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
/*  673 */     if (name == null) {
/*  674 */       throw new IllegalArgumentException("Argument \"name\" can't be null");
/*      */     }
/*  676 */     if (locale == null) {
/*  677 */       throw new IllegalArgumentException("Argument \"locale\" can't be null");
/*      */     }
/*  679 */     if (encoding == null) {
/*  680 */       throw new IllegalArgumentException("Argument \"encoding\" can't be null");
/*      */     }
/*  682 */     name = this.templateNameFormat.normalizeRootBasedName(name);
/*  683 */     if (name != null && this.templateLoader != null) {
/*  684 */       boolean debug = LOG.isDebugEnabled();
/*      */       
/*  686 */       String debugName = debug ? buildDebugName(name, locale, customLookupCondition, encoding, parse) : null;
/*      */       
/*  688 */       TemplateKey tk = new TemplateKey(name, locale, customLookupCondition, encoding, parse);
/*      */       
/*  690 */       if (this.isStorageConcurrent) {
/*  691 */         this.storage.remove(tk);
/*      */       } else {
/*  693 */         synchronized (this.storage) {
/*  694 */           this.storage.remove(tk);
/*      */         } 
/*      */       } 
/*  697 */       if (debug) {
/*  698 */         LOG.debug(debugName + " was removed from the cache, if it was there");
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private String buildDebugName(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
/*  705 */     return StringUtil.jQuoteNoXSS(name) + "(" + 
/*  706 */       StringUtil.jQuoteNoXSS(locale) + ((customLookupCondition != null) ? (", cond=" + 
/*  707 */       StringUtil.jQuoteNoXSS(customLookupCondition)) : "") + ", " + encoding + (parse ? ", parsed)" : ", unparsed]");
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
/*      */   @Deprecated
/*      */   public static String getFullTemplatePath(Environment env, String baseName, String targetName) {
/*      */     try {
/*  723 */       return env.toFullTemplateName(baseName, targetName);
/*  724 */     } catch (MalformedTemplateNameException e) {
/*  725 */       throw new IllegalArgumentException(e.getMessage());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private TemplateLookupResult lookupTemplate(String name, Locale locale, Object customLookupCondition) throws IOException {
/*  731 */     TemplateLookupResult lookupResult = this.templateLookupStrategy.lookup(new TemplateCacheTemplateLookupContext(name, locale, customLookupCondition));
/*      */     
/*  733 */     if (lookupResult == null) {
/*  734 */       throw new NullPointerException("Lookup result shouldn't be null");
/*      */     }
/*  736 */     return lookupResult;
/*      */   }
/*      */   
/*      */   private TemplateLookupResult lookupTemplateWithAcquisitionStrategy(String path) throws IOException {
/*  740 */     int asterisk = path.indexOf('*');
/*      */     
/*  742 */     if (asterisk == -1) {
/*  743 */       return TemplateLookupResult.from(path, findTemplateSource(path));
/*      */     }
/*  745 */     StringTokenizer tok = new StringTokenizer(path, "/");
/*  746 */     int lastAsterisk = -1;
/*  747 */     List<String> tokpath = new ArrayList();
/*  748 */     while (tok.hasMoreTokens()) {
/*  749 */       String pathToken = tok.nextToken();
/*  750 */       if (pathToken.equals("*")) {
/*  751 */         if (lastAsterisk != -1) {
/*  752 */           tokpath.remove(lastAsterisk);
/*      */         }
/*  754 */         lastAsterisk = tokpath.size();
/*      */       } 
/*  756 */       tokpath.add(pathToken);
/*      */     } 
/*  758 */     if (lastAsterisk == -1) {
/*  759 */       return TemplateLookupResult.from(path, findTemplateSource(path));
/*      */     }
/*  761 */     String basePath = concatPath(tokpath, 0, lastAsterisk);
/*  762 */     String resourcePath = concatPath(tokpath, lastAsterisk + 1, tokpath.size());
/*  763 */     if (resourcePath.endsWith("/")) {
/*  764 */       resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
/*      */     }
/*  766 */     StringBuilder buf = (new StringBuilder(path.length())).append(basePath);
/*  767 */     int l = basePath.length();
/*      */     while (true) {
/*  769 */       String fullPath = buf.append(resourcePath).toString();
/*  770 */       Object templateSource = findTemplateSource(fullPath);
/*  771 */       if (templateSource != null) {
/*  772 */         return TemplateLookupResult.from(fullPath, templateSource);
/*      */       }
/*  774 */       if (l == 0) {
/*  775 */         return TemplateLookupResult.createNegativeResult();
/*      */       }
/*  777 */       l = basePath.lastIndexOf('/', l - 2) + 1;
/*  778 */       buf.setLength(l);
/*      */     } 
/*      */   }
/*      */   
/*      */   private Object findTemplateSource(String path) throws IOException {
/*  783 */     Object result = this.templateLoader.findTemplateSource(path);
/*  784 */     if (LOG.isDebugEnabled()) {
/*  785 */       LOG.debug("TemplateLoader.findTemplateSource(" + StringUtil.jQuote(path) + "): " + ((result == null) ? "Not found" : "Found"));
/*      */     }
/*      */     
/*  788 */     return modifyForConfIcI(result);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Object modifyForConfIcI(Object templateSource) {
/*  796 */     if (templateSource == null) return null;
/*      */     
/*  798 */     if (this.config.getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_21) {
/*  799 */       return templateSource;
/*      */     }
/*      */     
/*  802 */     if (templateSource instanceof URLTemplateSource) {
/*  803 */       URLTemplateSource urlTemplateSource = (URLTemplateSource)templateSource;
/*  804 */       if (urlTemplateSource.getUseCaches() == null) {
/*  805 */         urlTemplateSource.setUseCaches(false);
/*      */       }
/*  807 */     } else if (templateSource instanceof MultiTemplateLoader.MultiSource) {
/*  808 */       modifyForConfIcI(((MultiTemplateLoader.MultiSource)templateSource).getWrappedSource());
/*      */     } 
/*  810 */     return templateSource;
/*      */   }
/*      */   
/*      */   private String concatPath(List path, int from, int to) {
/*  814 */     StringBuilder buf = new StringBuilder((to - from) * 16);
/*  815 */     for (int i = from; i < to; i++) {
/*  816 */       buf.append(path.get(i)).append('/');
/*      */     }
/*  818 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class TemplateKey
/*      */   {
/*      */     private final String name;
/*      */     
/*      */     private final Locale locale;
/*      */     
/*      */     private final Object customLookupCondition;
/*      */     private final String encoding;
/*      */     private final boolean parse;
/*      */     
/*      */     TemplateKey(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
/*  833 */       this.name = name;
/*  834 */       this.locale = locale;
/*  835 */       this.customLookupCondition = customLookupCondition;
/*  836 */       this.encoding = encoding;
/*  837 */       this.parse = parse;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object o) {
/*  842 */       if (o instanceof TemplateKey) {
/*  843 */         TemplateKey tk = (TemplateKey)o;
/*  844 */         return (this.parse == tk.parse && this.name
/*      */           
/*  846 */           .equals(tk.name) && this.locale
/*  847 */           .equals(tk.locale) && 
/*  848 */           nullSafeEquals(this.customLookupCondition, tk.customLookupCondition) && this.encoding
/*  849 */           .equals(tk.encoding));
/*      */       } 
/*  851 */       return false;
/*      */     }
/*      */     
/*      */     private boolean nullSafeEquals(Object o1, Object o2) {
/*  855 */       return (o1 != null) ? ((o2 != null) ? o1
/*  856 */         .equals(o2) : false) : ((o2 == null));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  862 */       return this.name
/*  863 */         .hashCode() ^ this.locale
/*  864 */         .hashCode() ^ this.encoding
/*  865 */         .hashCode() ^ ((this.customLookupCondition != null) ? this.customLookupCondition
/*  866 */         .hashCode() : 0) ^ 
/*  867 */         Boolean.valueOf(!this.parse).hashCode();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static final class CachedTemplate
/*      */     implements Cloneable, Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     
/*      */     Object templateOrException;
/*      */     
/*      */     Object source;
/*      */     
/*      */     long lastChecked;
/*      */     
/*      */     long lastModified;
/*      */     
/*      */     private CachedTemplate() {}
/*      */     
/*      */     public CachedTemplate cloneCachedTemplate() {
/*      */       try {
/*  889 */         return (CachedTemplate)clone();
/*  890 */       } catch (CloneNotSupportedException e) {
/*  891 */         throw new UndeclaredThrowableException(e);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private class TemplateCacheTemplateLookupContext
/*      */     extends TemplateLookupContext {
/*      */     TemplateCacheTemplateLookupContext(String templateName, Locale templateLocale, Object customLookupCondition) {
/*  899 */       super(templateName, TemplateCache.this.localizedLookup ? templateLocale : null, customLookupCondition);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public TemplateLookupResult lookupWithAcquisitionStrategy(String name) throws IOException {
/*  905 */       if (name.startsWith("/")) {
/*  906 */         throw new IllegalArgumentException("Non-normalized name, starts with \"/\": " + name);
/*      */       }
/*      */       
/*  909 */       return TemplateCache.this.lookupTemplateWithAcquisitionStrategy(name);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public TemplateLookupResult lookupWithLocalizedThenAcquisitionStrategy(String templateName, Locale templateLocale) throws IOException {
/*  916 */       if (templateLocale == null) {
/*  917 */         return lookupWithAcquisitionStrategy(templateName);
/*      */       }
/*      */       
/*  920 */       int lastDot = templateName.lastIndexOf('.');
/*  921 */       String prefix = (lastDot == -1) ? templateName : templateName.substring(0, lastDot);
/*  922 */       String suffix = (lastDot == -1) ? "" : templateName.substring(lastDot);
/*  923 */       String localeName = "_" + templateLocale.toString();
/*  924 */       StringBuilder buf = new StringBuilder(templateName.length() + localeName.length());
/*  925 */       buf.append(prefix);
/*      */       while (true) {
/*  927 */         buf.setLength(prefix.length());
/*  928 */         String path = buf.append(localeName).append(suffix).toString();
/*  929 */         TemplateLookupResult lookupResult = lookupWithAcquisitionStrategy(path);
/*  930 */         if (lookupResult.isPositive()) {
/*  931 */           return lookupResult;
/*      */         }
/*      */         
/*  934 */         int lastUnderscore = localeName.lastIndexOf('_');
/*  935 */         if (lastUnderscore == -1) {
/*      */           break;
/*      */         }
/*  938 */         localeName = localeName.substring(0, lastUnderscore);
/*      */       } 
/*  940 */       return createNegativeLookupResult();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static final class MaybeMissingTemplate
/*      */   {
/*      */     private final Template template;
/*      */     
/*      */     private final String missingTemplateNormalizedName;
/*      */     
/*      */     private final String missingTemplateReason;
/*      */     
/*      */     private final MalformedTemplateNameException missingTemplateCauseException;
/*      */ 
/*      */     
/*      */     private MaybeMissingTemplate(Template template) {
/*  958 */       this.template = template;
/*  959 */       this.missingTemplateNormalizedName = null;
/*  960 */       this.missingTemplateReason = null;
/*  961 */       this.missingTemplateCauseException = null;
/*      */     }
/*      */     
/*      */     private MaybeMissingTemplate(String normalizedName, MalformedTemplateNameException missingTemplateCauseException) {
/*  965 */       this.template = null;
/*  966 */       this.missingTemplateNormalizedName = normalizedName;
/*  967 */       this.missingTemplateReason = null;
/*  968 */       this.missingTemplateCauseException = missingTemplateCauseException;
/*      */     }
/*      */     
/*      */     private MaybeMissingTemplate(String normalizedName, String missingTemplateReason) {
/*  972 */       this.template = null;
/*  973 */       this.missingTemplateNormalizedName = normalizedName;
/*  974 */       this.missingTemplateReason = missingTemplateReason;
/*  975 */       this.missingTemplateCauseException = null;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Template getTemplate() {
/*  982 */       return this.template;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getMissingTemplateReason() {
/*  991 */       return (this.missingTemplateReason != null) ? this.missingTemplateReason : ((this.missingTemplateCauseException != null) ? this.missingTemplateCauseException
/*      */ 
/*      */         
/*  994 */         .getMalformednessDescription() : null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getMissingTemplateNormalizedName() {
/* 1004 */       return this.missingTemplateNormalizedName;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\TemplateCache.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */