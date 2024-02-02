/*     */ package oshi.util;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import oshi.annotation.concurrent.NotThreadSafe;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NotThreadSafe
/*     */ public final class GlobalConfig
/*     */ {
/*     */   private static final String OSHI_PROPERTIES = "oshi.properties";
/*  44 */   private static final Properties CONFIG = FileUtil.readPropertiesFromFilename("oshi.properties");
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
/*     */   public static String get(String key, String def) {
/*  59 */     return CONFIG.getProperty(key, def);
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
/*     */   public static int get(String key, int def) {
/*  72 */     String value = CONFIG.getProperty(key);
/*  73 */     return (value == null) ? def : ParseUtil.parseIntOrDefault(value, def);
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
/*     */   public static double get(String key, double def) {
/*  86 */     String value = CONFIG.getProperty(key);
/*  87 */     return (value == null) ? def : ParseUtil.parseDoubleOrDefault(value, def);
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
/*     */   public static boolean get(String key, boolean def) {
/* 100 */     String value = CONFIG.getProperty(key);
/* 101 */     return (value == null) ? def : Boolean.parseBoolean(value);
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
/*     */   public static void set(String key, Object val) {
/* 114 */     if (val == null) {
/* 115 */       CONFIG.remove(key);
/*     */     } else {
/* 117 */       CONFIG.setProperty(key, val.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void remove(String key) {
/* 128 */     CONFIG.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clear() {
/* 135 */     CONFIG.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void load(Properties properties) {
/* 145 */     CONFIG.putAll(properties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PropertyException
/*     */     extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = -7482581936621748005L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PropertyException(String property) {
/* 160 */       super("Invalid property: \"" + property + "\" = " + GlobalConfig.get(property, (String)null));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PropertyException(String property, String message) {
/* 170 */       super("Invalid property \"" + property + "\": " + message);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\GlobalConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */