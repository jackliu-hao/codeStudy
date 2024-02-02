/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TimeZone;
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
/*     */ class JavaTemplateDateFormatFactory
/*     */   extends TemplateDateFormatFactory
/*     */ {
/*  34 */   static final JavaTemplateDateFormatFactory INSTANCE = new JavaTemplateDateFormatFactory();
/*     */   
/*  36 */   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
/*     */   
/*  38 */   private static final ConcurrentHashMap<CacheKey, DateFormat> GLOBAL_FORMAT_CACHE = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int LEAK_ALERT_DATE_FORMAT_CACHE_SIZE = 1024;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
/*  53 */     return new JavaTemplateDateFormat(getJavaDateFormat(dateType, params, locale, timeZone));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DateFormat getJavaDateFormat(int dateType, String nameOrPattern, Locale locale, TimeZone timeZone) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
/*  63 */     CacheKey cacheKey = new CacheKey(dateType, nameOrPattern, locale, timeZone);
/*     */ 
/*     */     
/*  66 */     DateFormat jFormat = GLOBAL_FORMAT_CACHE.get(cacheKey);
/*  67 */     if (jFormat == null) {
/*     */       
/*  69 */       StringTokenizer tok = new StringTokenizer(nameOrPattern, "_");
/*  70 */       int tok1Style = tok.hasMoreTokens() ? parseDateStyleToken(tok.nextToken()) : 2;
/*  71 */       if (tok1Style != -1) {
/*  72 */         int tok2Style; switch (dateType) {
/*     */           case 0:
/*  74 */             throw new UnknownDateTypeFormattingUnsupportedException();
/*     */           
/*     */           case 1:
/*  77 */             jFormat = DateFormat.getTimeInstance(tok1Style, cacheKey.locale);
/*     */             break;
/*     */           
/*     */           case 2:
/*  81 */             jFormat = DateFormat.getDateInstance(tok1Style, cacheKey.locale);
/*     */             break;
/*     */           
/*     */           case 3:
/*  85 */             tok2Style = tok.hasMoreTokens() ? parseDateStyleToken(tok.nextToken()) : tok1Style;
/*  86 */             if (tok2Style != -1) {
/*  87 */               jFormat = DateFormat.getDateTimeInstance(tok1Style, tok2Style, cacheKey.locale);
/*     */             }
/*     */             break;
/*     */         } 
/*     */       
/*     */       } 
/*  93 */       if (jFormat == null) {
/*     */         try {
/*  95 */           jFormat = new SimpleDateFormat(nameOrPattern, cacheKey.locale);
/*  96 */         } catch (IllegalArgumentException e) {
/*  97 */           String msg = e.getMessage();
/*  98 */           throw new InvalidFormatParametersException((msg != null) ? msg : "Invalid SimpleDateFormat pattern", e);
/*     */         } 
/*     */       }
/*     */       
/* 102 */       jFormat.setTimeZone(cacheKey.timeZone);
/*     */       
/* 104 */       if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
/* 105 */         boolean triggered = false;
/* 106 */         synchronized (JavaTemplateDateFormatFactory.class) {
/* 107 */           if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
/* 108 */             triggered = true;
/* 109 */             GLOBAL_FORMAT_CACHE.clear();
/*     */           } 
/*     */         } 
/* 112 */         if (triggered) {
/* 113 */           LOG.warn("Global Java DateFormat cache has exceeded 1024 entries => cache flushed. Typical cause: Some template generates high variety of format pattern strings.");
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 119 */       DateFormat prevJFormat = GLOBAL_FORMAT_CACHE.putIfAbsent(cacheKey, jFormat);
/* 120 */       if (prevJFormat != null) {
/* 121 */         jFormat = prevJFormat;
/*     */       }
/*     */     } 
/*     */     
/* 125 */     return (DateFormat)jFormat.clone();
/*     */   }
/*     */   
/*     */   private static final class CacheKey {
/*     */     private final int dateType;
/*     */     private final String pattern;
/*     */     private final Locale locale;
/*     */     private final TimeZone timeZone;
/*     */     
/*     */     CacheKey(int dateType, String pattern, Locale locale, TimeZone timeZone) {
/* 135 */       this.dateType = dateType;
/* 136 */       this.pattern = pattern;
/* 137 */       this.locale = locale;
/* 138 */       this.timeZone = timeZone;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 143 */       if (o instanceof CacheKey) {
/* 144 */         CacheKey fk = (CacheKey)o;
/* 145 */         return (this.dateType == fk.dateType && fk.pattern.equals(this.pattern) && fk.locale.equals(this.locale) && fk.timeZone
/* 146 */           .equals(this.timeZone));
/*     */       } 
/* 148 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 153 */       return this.dateType ^ this.pattern.hashCode() ^ this.locale.hashCode() ^ this.timeZone.hashCode();
/*     */     }
/*     */   }
/*     */   
/*     */   private int parseDateStyleToken(String token) {
/* 158 */     if ("short".equals(token)) {
/* 159 */       return 3;
/*     */     }
/* 161 */     if ("medium".equals(token)) {
/* 162 */       return 2;
/*     */     }
/* 164 */     if ("long".equals(token)) {
/* 165 */       return 1;
/*     */     }
/* 167 */     if ("full".equals(token)) {
/* 168 */       return 0;
/*     */     }
/* 170 */     return -1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\JavaTemplateDateFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */