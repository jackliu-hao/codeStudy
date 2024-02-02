/*     */ package ch.qos.logback.core.rolling.helper;
/*     */ 
/*     */ import ch.qos.logback.core.pattern.DynamicConverter;
/*     */ import ch.qos.logback.core.util.CachingDateFormatter;
/*     */ import ch.qos.logback.core.util.DatePatternToRegexUtil;
/*     */ import java.util.Date;
/*     */ import java.util.List;
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
/*     */ public class DateTokenConverter<E>
/*     */   extends DynamicConverter<E>
/*     */   implements MonoTypedConverter
/*     */ {
/*     */   public static final String CONVERTER_KEY = "d";
/*     */   public static final String AUXILIARY_TOKEN = "AUX";
/*     */   public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
/*     */   private String datePattern;
/*     */   private TimeZone timeZone;
/*     */   private CachingDateFormatter cdf;
/*     */   private boolean primary = true;
/*     */   
/*     */   public void start() {
/*  47 */     this.datePattern = getFirstOption();
/*  48 */     if (this.datePattern == null) {
/*  49 */       this.datePattern = "yyyy-MM-dd";
/*     */     }
/*     */     
/*  52 */     List<String> optionList = getOptionList();
/*  53 */     if (optionList != null) {
/*  54 */       for (int optionIndex = 1; optionIndex < optionList.size(); optionIndex++) {
/*  55 */         String option = optionList.get(optionIndex);
/*  56 */         if ("AUX".equalsIgnoreCase(option)) {
/*  57 */           this.primary = false;
/*     */         } else {
/*  59 */           this.timeZone = TimeZone.getTimeZone(option);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*  64 */     this.cdf = new CachingDateFormatter(this.datePattern);
/*  65 */     if (this.timeZone != null) {
/*  66 */       this.cdf.setTimeZone(this.timeZone);
/*     */     }
/*     */   }
/*     */   
/*     */   public String convert(Date date) {
/*  71 */     return this.cdf.format(date.getTime());
/*     */   }
/*     */   
/*     */   public String convert(Object o) {
/*  75 */     if (o == null) {
/*  76 */       throw new IllegalArgumentException("Null argument forbidden");
/*     */     }
/*  78 */     if (o instanceof Date) {
/*  79 */       return convert((Date)o);
/*     */     }
/*  81 */     throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatePattern() {
/*  88 */     return this.datePattern;
/*     */   }
/*     */   
/*     */   public TimeZone getTimeZone() {
/*  92 */     return this.timeZone;
/*     */   }
/*     */   
/*     */   public boolean isApplicable(Object o) {
/*  96 */     return o instanceof Date;
/*     */   }
/*     */   
/*     */   public String toRegex() {
/* 100 */     DatePatternToRegexUtil datePatternToRegexUtil = new DatePatternToRegexUtil(this.datePattern);
/* 101 */     return datePatternToRegexUtil.toRegex();
/*     */   }
/*     */   
/*     */   public boolean isPrimary() {
/* 105 */     return this.primary;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\DateTokenConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */