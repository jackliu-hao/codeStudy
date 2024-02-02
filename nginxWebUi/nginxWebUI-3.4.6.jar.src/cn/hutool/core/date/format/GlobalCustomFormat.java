/*     */ package cn.hutool.core.date.format;
/*     */ 
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.Function;
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
/*     */ public class GlobalCustomFormat
/*     */ {
/*     */   public static final String FORMAT_SECONDS = "#sss";
/*     */   public static final String FORMAT_MILLISECONDS = "#SSS";
/*  28 */   private static final Map<CharSequence, Function<Date, String>> formatterMap = new ConcurrentHashMap<>();
/*  29 */   private static final Map<CharSequence, Function<CharSequence, Date>> parserMap = new ConcurrentHashMap<>();
/*     */   
/*     */   static {
/*  32 */     putFormatter("#sss", date -> String.valueOf(Math.floorDiv(date.getTime(), 1000L)));
/*  33 */     putParser("#sss", dateStr -> DateUtil.date(Math.multiplyExact(Long.parseLong(dateStr.toString()), 1000L)));
/*     */     
/*  35 */     putFormatter("#SSS", date -> String.valueOf(date.getTime()));
/*  36 */     putParser("#SSS", dateStr -> DateUtil.date(Long.parseLong(dateStr.toString())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void putFormatter(String format, Function<Date, String> func) {
/*  46 */     Assert.notNull(format, "Format must be not null !", new Object[0]);
/*  47 */     Assert.notNull(func, "Function must be not null !", new Object[0]);
/*  48 */     formatterMap.put(format, func);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void putParser(String format, Function<CharSequence, Date> func) {
/*  58 */     Assert.notNull(format, "Format must be not null !", new Object[0]);
/*  59 */     Assert.notNull(func, "Function must be not null !", new Object[0]);
/*  60 */     parserMap.put(format, func);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isCustomFormat(String format) {
/*  70 */     return formatterMap.containsKey(format);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(Date date, CharSequence format) {
/*  81 */     if (null != formatterMap) {
/*  82 */       Function<Date, String> func = formatterMap.get(format);
/*  83 */       if (null != func) {
/*  84 */         return func.apply(date);
/*     */       }
/*     */     } 
/*     */     
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(TemporalAccessor temporalAccessor, CharSequence format) {
/*  99 */     return format((Date)DateUtil.date(temporalAccessor), format);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date parse(CharSequence dateStr, String format) {
/* 110 */     if (null != parserMap) {
/* 111 */       Function<CharSequence, Date> func = parserMap.get(format);
/* 112 */       if (null != func) {
/* 113 */         return func.apply(dateStr);
/*     */       }
/*     */     } 
/*     */     
/* 117 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\GlobalCustomFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */