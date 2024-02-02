/*     */ package cn.hutool.core.date.format;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Tuple;
/*     */ import java.text.DateFormat;
/*     */ import java.text.Format;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ abstract class FormatCache<F extends Format>
/*     */ {
/*     */   static final int NONE = -1;
/*  27 */   private final ConcurrentMap<Tuple, F> cInstanceCache = new ConcurrentHashMap<>(7);
/*     */   
/*  29 */   private static final ConcurrentMap<Tuple, String> C_DATE_TIME_INSTANCE_CACHE = new ConcurrentHashMap<>(7);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public F getInstance() {
/*  37 */     return getDateTimeInstance(Integer.valueOf(3), Integer.valueOf(3), null, null);
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
/*     */   
/*     */   public F getInstance(String pattern, TimeZone timeZone, Locale locale) {
/*  50 */     Assert.notBlank(pattern, "pattern must not be blank", new Object[0]);
/*  51 */     if (timeZone == null) {
/*  52 */       timeZone = TimeZone.getDefault();
/*     */     }
/*  54 */     if (locale == null) {
/*  55 */       locale = Locale.getDefault();
/*     */     }
/*  57 */     Tuple key = new Tuple(new Object[] { pattern, timeZone, locale });
/*  58 */     Format format = (Format)this.cInstanceCache.get(key);
/*  59 */     if (format == null) {
/*  60 */       format = (Format)createInstance(pattern, timeZone, locale);
/*  61 */       Format format1 = (Format)this.cInstanceCache.putIfAbsent(key, (F)format);
/*  62 */       if (format1 != null)
/*     */       {
/*     */         
/*  65 */         format = format1;
/*     */       }
/*     */     } 
/*  68 */     return (F)format;
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
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract F createInstance(String paramString, TimeZone paramTimeZone, Locale paramLocale);
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
/*     */   F getDateTimeInstance(Integer dateStyle, Integer timeStyle, TimeZone timeZone, Locale locale) {
/*  96 */     if (locale == null) {
/*  97 */       locale = Locale.getDefault();
/*     */     }
/*  99 */     String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
/* 100 */     return getInstance(pattern, timeZone, locale);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   F getDateInstance(int dateStyle, TimeZone timeZone, Locale locale) {
/* 116 */     return getDateTimeInstance(Integer.valueOf(dateStyle), null, timeZone, locale);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   F getTimeInstance(int timeStyle, TimeZone timeZone, Locale locale) {
/* 132 */     return getDateTimeInstance(null, Integer.valueOf(timeStyle), timeZone, locale);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
/* 148 */     Tuple key = new Tuple(new Object[] { dateStyle, timeStyle, locale });
/*     */     
/* 150 */     String pattern = C_DATE_TIME_INSTANCE_CACHE.get(key);
/* 151 */     if (pattern == null) {
/*     */       try {
/*     */         DateFormat formatter;
/* 154 */         if (dateStyle == null) {
/* 155 */           formatter = DateFormat.getTimeInstance(timeStyle.intValue(), locale);
/* 156 */         } else if (timeStyle == null) {
/* 157 */           formatter = DateFormat.getDateInstance(dateStyle.intValue(), locale);
/*     */         } else {
/* 159 */           formatter = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), locale);
/*     */         } 
/* 161 */         pattern = ((SimpleDateFormat)formatter).toPattern();
/* 162 */         String previous = C_DATE_TIME_INSTANCE_CACHE.putIfAbsent(key, pattern);
/* 163 */         if (previous != null)
/*     */         {
/*     */ 
/*     */           
/* 167 */           pattern = previous;
/*     */         }
/* 169 */       } catch (ClassCastException ex) {
/* 170 */         throw new IllegalArgumentException("No date time pattern for locale: " + locale);
/*     */       } 
/*     */     }
/* 173 */     return pattern;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\FormatCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */