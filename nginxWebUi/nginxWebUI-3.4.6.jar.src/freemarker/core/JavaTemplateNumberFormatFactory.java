/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ class JavaTemplateNumberFormatFactory
/*     */   extends TemplateNumberFormatFactory
/*     */ {
/*  33 */   static final JavaTemplateNumberFormatFactory INSTANCE = new JavaTemplateNumberFormatFactory();
/*     */   
/*     */   static final String COMPUTER = "computer";
/*     */   
/*  37 */   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
/*     */   
/*  39 */   private static final ConcurrentHashMap<CacheKey, NumberFormat> GLOBAL_FORMAT_CACHE = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE = 1024;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateNumberFormat get(String params, Locale locale, Environment env) throws InvalidFormatParametersException {
/*  51 */     CacheKey cacheKey = new CacheKey((env != null) ? env.transformNumberFormatGlobalCacheKey(params) : params, locale);
/*     */     
/*  53 */     NumberFormat jFormat = GLOBAL_FORMAT_CACHE.get(cacheKey);
/*  54 */     if (jFormat == null) {
/*  55 */       if ("number".equals(params)) {
/*  56 */         jFormat = NumberFormat.getNumberInstance(locale);
/*  57 */       } else if ("currency".equals(params)) {
/*  58 */         jFormat = NumberFormat.getCurrencyInstance(locale);
/*  59 */       } else if ("percent".equals(params)) {
/*  60 */         jFormat = NumberFormat.getPercentInstance(locale);
/*  61 */       } else if ("computer".equals(params)) {
/*  62 */         jFormat = env.getCNumberFormat();
/*     */       } else {
/*     */         try {
/*  65 */           jFormat = ExtendedDecimalFormatParser.parse(params, locale);
/*  66 */         } catch (ParseException e) {
/*  67 */           String msg = e.getMessage();
/*  68 */           throw new InvalidFormatParametersException((msg != null) ? msg : "Invalid DecimalFormat pattern", e);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/*  73 */       if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
/*  74 */         boolean triggered = false;
/*  75 */         synchronized (JavaTemplateNumberFormatFactory.class) {
/*  76 */           if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
/*  77 */             triggered = true;
/*  78 */             GLOBAL_FORMAT_CACHE.clear();
/*     */           } 
/*     */         } 
/*  81 */         if (triggered) {
/*  82 */           LOG.warn("Global Java NumberFormat cache has exceeded 1024 entries => cache flushed. Typical cause: Some template generates high variety of format pattern strings.");
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*  88 */       NumberFormat prevJFormat = GLOBAL_FORMAT_CACHE.putIfAbsent(cacheKey, jFormat);
/*  89 */       if (prevJFormat != null) {
/*  90 */         jFormat = prevJFormat;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  95 */     jFormat = (NumberFormat)jFormat.clone();
/*     */     
/*  97 */     return new JavaTemplateNumberFormat(jFormat, params);
/*     */   }
/*     */   
/*     */   private static final class CacheKey {
/*     */     private final String pattern;
/*     */     private final Locale locale;
/*     */     
/*     */     CacheKey(String pattern, Locale locale) {
/* 105 */       this.pattern = pattern;
/* 106 */       this.locale = locale;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 111 */       if (o instanceof CacheKey) {
/* 112 */         CacheKey fk = (CacheKey)o;
/* 113 */         return (fk.pattern.equals(this.pattern) && fk.locale.equals(this.locale));
/*     */       } 
/* 115 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 120 */       return this.pattern.hashCode() ^ this.locale.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\JavaTemplateNumberFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */