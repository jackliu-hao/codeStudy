/*     */ package org.jboss.logging;
/*     */ 
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
/*     */ class LoggingLocale
/*     */ {
/*  31 */   private static final Locale LOCALE = getDefaultLocale();
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
/*     */   static Locale getLocale() {
/*  48 */     return LOCALE;
/*     */   }
/*     */   
/*     */   private static Locale getDefaultLocale() {
/*  52 */     String bcp47Tag = SecurityActions.getSystemProperty("org.jboss.logging.locale", "");
/*  53 */     if (bcp47Tag.trim().isEmpty()) {
/*  54 */       return Locale.getDefault();
/*     */     }
/*     */ 
/*     */     
/*  58 */     return forLanguageTag(bcp47Tag);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Locale forLanguageTag(String locale) {
/*  63 */     if ("en-CA".equalsIgnoreCase(locale))
/*  64 */       return Locale.CANADA; 
/*  65 */     if ("fr-CA".equalsIgnoreCase(locale))
/*  66 */       return Locale.CANADA_FRENCH; 
/*  67 */     if ("zh".equalsIgnoreCase(locale))
/*  68 */       return Locale.CHINESE; 
/*  69 */     if ("en".equalsIgnoreCase(locale))
/*  70 */       return Locale.ENGLISH; 
/*  71 */     if ("fr-FR".equalsIgnoreCase(locale))
/*  72 */       return Locale.FRANCE; 
/*  73 */     if ("fr".equalsIgnoreCase(locale))
/*  74 */       return Locale.FRENCH; 
/*  75 */     if ("de".equalsIgnoreCase(locale))
/*  76 */       return Locale.GERMAN; 
/*  77 */     if ("de-DE".equalsIgnoreCase(locale))
/*  78 */       return Locale.GERMANY; 
/*  79 */     if ("it".equalsIgnoreCase(locale))
/*  80 */       return Locale.ITALIAN; 
/*  81 */     if ("it-IT".equalsIgnoreCase(locale))
/*  82 */       return Locale.ITALY; 
/*  83 */     if ("ja-JP".equalsIgnoreCase(locale))
/*  84 */       return Locale.JAPAN; 
/*  85 */     if ("ja".equalsIgnoreCase(locale))
/*  86 */       return Locale.JAPANESE; 
/*  87 */     if ("ko-KR".equalsIgnoreCase(locale))
/*  88 */       return Locale.KOREA; 
/*  89 */     if ("ko".equalsIgnoreCase(locale))
/*  90 */       return Locale.KOREAN; 
/*  91 */     if ("zh-CN".equalsIgnoreCase(locale))
/*  92 */       return Locale.SIMPLIFIED_CHINESE; 
/*  93 */     if ("zh-TW".equalsIgnoreCase(locale))
/*  94 */       return Locale.TRADITIONAL_CHINESE; 
/*  95 */     if ("en-UK".equalsIgnoreCase(locale))
/*  96 */       return Locale.UK; 
/*  97 */     if ("en-US".equalsIgnoreCase(locale)) {
/*  98 */       return Locale.US;
/*     */     }
/*     */ 
/*     */     
/* 102 */     String[] parts = locale.split("-");
/* 103 */     int len = parts.length;
/* 104 */     int index = 0;
/* 105 */     int count = 0;
/* 106 */     String language = parts[index++];
/* 107 */     String region = "";
/* 108 */     String variant = "";
/*     */     
/* 110 */     while (index < len && 
/* 111 */       count++ != 2 && isAlpha(parts[index], 3, 3))
/*     */     {
/*     */       
/* 114 */       index++;
/*     */     }
/*     */     
/* 117 */     if (index != len && isAlpha(parts[index], 4, 4)) {
/* 118 */       index++;
/*     */     }
/*     */     
/* 121 */     if (index != len && (isAlpha(parts[index], 2, 2) || isNumeric(parts[index], 3, 3))) {
/* 122 */       region = parts[index++];
/*     */     }
/*     */     
/* 125 */     if (index != len && isAlphaOrNumeric(parts[index], 5, 8)) {
/* 126 */       variant = parts[index];
/*     */     }
/* 128 */     return new Locale(language, region, variant);
/*     */   }
/*     */   
/*     */   private static boolean isAlpha(String value, int minLen, int maxLen) {
/* 132 */     int len = value.length();
/* 133 */     if (len < minLen || len > maxLen) {
/* 134 */       return false;
/*     */     }
/* 136 */     for (int i = 0; i < len; i++) {
/* 137 */       if (!Character.isLetter(value.charAt(i))) {
/* 138 */         return false;
/*     */       }
/*     */     } 
/* 141 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean isNumeric(String value, int minLen, int maxLen) {
/* 145 */     int len = value.length();
/* 146 */     if (len < minLen || len > maxLen) {
/* 147 */       return false;
/*     */     }
/* 149 */     for (int i = 0; i < len; i++) {
/* 150 */       if (!Character.isDigit(value.charAt(i))) {
/* 151 */         return false;
/*     */       }
/*     */     } 
/* 154 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isAlphaOrNumeric(String value, int minLen, int maxLen) {
/* 159 */     int len = value.length();
/* 160 */     if (len < minLen || len > maxLen) {
/* 161 */       return false;
/*     */     }
/* 163 */     for (int i = 0; i < len; i++) {
/* 164 */       if (!Character.isLetterOrDigit(value.charAt(i))) {
/* 165 */         return false;
/*     */       }
/*     */     } 
/* 168 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\LoggingLocale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */