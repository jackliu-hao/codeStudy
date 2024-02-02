/*     */ package org.jboss.logging;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ public final class Messages
/*     */ {
/*     */   public static <T> T getBundle(Class<T> type) {
/*  46 */     return getBundle(type, LoggingLocale.getLocale());
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
/*     */   public static <T> T getBundle(final Class<T> type, final Locale locale) {
/*  58 */     if (System.getSecurityManager() == null) {
/*  59 */       return doGetBundle(type, locale);
/*     */     }
/*  61 */     return AccessController.doPrivileged(new PrivilegedAction<T>() {
/*     */           public T run() {
/*  63 */             return Messages.doGetBundle(type, locale);
/*     */           }
/*     */         });
/*     */   }
/*     */   private static <T> T doGetBundle(Class<T> type, Locale locale) {
/*     */     Field field;
/*  69 */     String language = locale.getLanguage();
/*  70 */     String country = locale.getCountry();
/*  71 */     String variant = locale.getVariant();
/*     */     
/*  73 */     Class<? extends T> bundleClass = null;
/*  74 */     if (variant != null && variant.length() > 0) {
/*  75 */       try { bundleClass = Class.forName(join(type.getName(), "$bundle", language, country, variant), true, type.getClassLoader()).asSubclass(type); }
/*  76 */       catch (ClassNotFoundException classNotFoundException) {}
/*     */     }
/*     */     
/*  79 */     if (bundleClass == null && country != null && country.length() > 0) {
/*  80 */       try { bundleClass = Class.forName(join(type.getName(), "$bundle", language, country, null), true, type.getClassLoader()).asSubclass(type); }
/*  81 */       catch (ClassNotFoundException classNotFoundException) {}
/*     */     }
/*     */     
/*  84 */     if (bundleClass == null && language != null && language.length() > 0) {
/*  85 */       try { bundleClass = Class.forName(join(type.getName(), "$bundle", language, null, null), true, type.getClassLoader()).asSubclass(type); }
/*  86 */       catch (ClassNotFoundException classNotFoundException) {}
/*     */     }
/*     */     
/*  89 */     if (bundleClass == null) {
/*  90 */       try { bundleClass = Class.forName(join(type.getName(), "$bundle", null, null, null), true, type.getClassLoader()).asSubclass(type); }
/*  91 */       catch (ClassNotFoundException e)
/*  92 */       { throw new IllegalArgumentException("Invalid bundle " + type + " (implementation not found)"); }
/*     */     
/*     */     }
/*     */     try {
/*  96 */       field = bundleClass.getField("INSTANCE");
/*  97 */     } catch (NoSuchFieldException e) {
/*  98 */       throw new IllegalArgumentException("Bundle implementation " + bundleClass + " has no instance field");
/*     */     } 
/*     */     try {
/* 101 */       return type.cast(field.get(null));
/* 102 */     } catch (IllegalAccessException e) {
/* 103 */       throw new IllegalArgumentException("Bundle implementation " + bundleClass + " could not be instantiated", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String join(String interfaceName, String a, String b, String c, String d) {
/* 108 */     StringBuilder build = new StringBuilder();
/* 109 */     build.append(interfaceName).append('_').append(a);
/* 110 */     if (b != null && b.length() > 0) {
/* 111 */       build.append('_');
/* 112 */       build.append(b);
/*     */     } 
/* 114 */     if (c != null && c.length() > 0) {
/* 115 */       build.append('_');
/* 116 */       build.append(c);
/*     */     } 
/* 118 */     if (d != null && d.length() > 0) {
/* 119 */       build.append('_');
/* 120 */       build.append(d);
/*     */     } 
/* 122 */     return build.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */