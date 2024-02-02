/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Console;
/*     */ import java.util.Properties;
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
/*     */ public class SystemPropsUtil
/*     */ {
/*  22 */   public static String HUTOOL_DATE_LENIENT = "hutool.date.lenient";
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
/*     */   public static String get(String name, String defaultValue) {
/*  34 */     return StrUtil.nullToDefault(get(name, false), defaultValue);
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
/*     */   public static String get(String name, boolean quiet) {
/*  47 */     String value = null;
/*     */     try {
/*  49 */       value = System.getProperty(name);
/*  50 */     } catch (SecurityException e) {
/*  51 */       if (false == quiet) {
/*  52 */         Console.error("Caught a SecurityException reading the system property '{}'; the SystemUtil property value will default to null.", new Object[] { name });
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  57 */     if (null == value) {
/*     */       try {
/*  59 */         value = System.getenv(name);
/*  60 */       } catch (SecurityException e) {
/*  61 */         if (false == quiet) {
/*  62 */           Console.error("Caught a SecurityException reading the system env '{}'; the SystemUtil env value will default to null.", new Object[] { name });
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  68 */     return value;
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
/*     */   public static String get(String key) {
/*  80 */     return get(key, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean getBoolean(String key, boolean defaultValue) {
/*  91 */     String value = get(key);
/*  92 */     if (value == null) {
/*  93 */       return defaultValue;
/*     */     }
/*     */     
/*  96 */     value = value.trim().toLowerCase();
/*  97 */     if (value.isEmpty()) {
/*  98 */       return true;
/*     */     }
/*     */     
/* 101 */     return Convert.toBool(value, Boolean.valueOf(defaultValue)).booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getInt(String key, int defaultValue) {
/* 112 */     return Convert.toInt(get(key), Integer.valueOf(defaultValue)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getLong(String key, long defaultValue) {
/* 123 */     return Convert.toLong(get(key), Long.valueOf(defaultValue)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties getProps() {
/* 130 */     return System.getProperties();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void set(String key, String value) {
/* 140 */     if (null == value) {
/* 141 */       System.clearProperty(key);
/*     */     } else {
/* 143 */       System.setProperty(key, value);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\SystemPropsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */