/*     */ package org.noear.snack.core.utils;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import org.noear.snack.core.DEFAULTS;
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
/*     */ public class DateUtil
/*     */ {
/*     */   public static final String FORMAT_29 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
/*     */   public static final String FORMAT_24_ISO08601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
/*     */   public static final String FORMAT_23_a = "yyyy-MM-dd HH:mm:ss,SSS";
/*     */   public static final String FORMAT_23_b = "yyyy-MM-dd HH:mm:ss.SSS";
/*     */   public static final String FORMAT_22 = "yyyyMMddHHmmssSSSZ";
/*     */   public static final String FORMAT_19_ISO = "yyyy-MM-dd'T'HH:mm:ss";
/*     */   public static final String FORMAT_19_a = "yyyy-MM-dd HH:mm:ss";
/*     */   public static final String FORMAT_19_b = "yyyy/MM/dd HH:mm:ss";
/*     */   public static final String FORMAT_19_c = "yyyy.MM.dd HH:mm:ss";
/*     */   public static final String FORMAT_17 = "yyyyMMddHHmmssSSS";
/*     */   public static final String FORMAT_16_a = "yyyy-MM-dd HH:mm";
/*     */   public static final String FORMAT_16_b = "yyyy/MM/dd HH:mm";
/*     */   public static final String FORMAT_16_c = "yyyy.MM.dd HH:mm";
/*     */   public static final String FORMAT_14 = "yyyyMMddHHmmss";
/*     */   public static final String FORMAT_10_a = "yyyy-MM-dd";
/*     */   public static final String FORMAT_10_b = "yyyy/MM/dd";
/*     */   public static final String FORMAT_10_c = "yyyy.MM.dd";
/*     */   public static final String FORMAT_9 = "HH时mm分ss秒";
/*     */   public static final String FORMAT_8_a = "HH:mm:ss";
/*     */   public static final String FORMAT_8_b = "yyyyMMdd";
/*     */   
/*     */   public static Date parse(String val) throws ParseException {
/*  43 */     int len = val.length();
/*  44 */     String ft = null;
/*     */ 
/*     */     
/*  47 */     if (len == 29) {
/*  48 */       if (val.charAt(26) == ':' && val.charAt(28) == '0') {
/*  49 */         ft = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
/*     */       }
/*  51 */     } else if (len == 24) {
/*  52 */       if (val.charAt(10) == 'T') {
/*  53 */         ft = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
/*     */       }
/*  55 */     } else if (len == 23) {
/*  56 */       if (val.charAt(19) == ',') {
/*  57 */         ft = "yyyy-MM-dd HH:mm:ss,SSS";
/*     */       } else {
/*  59 */         ft = "yyyy-MM-dd HH:mm:ss.SSS";
/*     */       } 
/*  61 */     } else if (len == 22) {
/*  62 */       ft = "yyyyMMddHHmmssSSSZ";
/*  63 */     } else if (len == 19) {
/*  64 */       if (val.charAt(10) == 'T') {
/*  65 */         ft = "yyyy-MM-dd'T'HH:mm:ss";
/*     */       } else {
/*  67 */         char c1 = val.charAt(4);
/*  68 */         if (c1 == '/') {
/*  69 */           ft = "yyyy/MM/dd HH:mm:ss";
/*  70 */         } else if (c1 == '.') {
/*  71 */           ft = "yyyy.MM.dd HH:mm:ss";
/*     */         } else {
/*  73 */           ft = "yyyy-MM-dd HH:mm:ss";
/*     */         } 
/*     */       } 
/*  76 */     } else if (len == 17) {
/*  77 */       ft = "yyyyMMddHHmmssSSS";
/*  78 */     } else if (len == 16) {
/*  79 */       char c1 = val.charAt(4);
/*  80 */       if (c1 == '/') {
/*  81 */         ft = "yyyy/MM/dd HH:mm";
/*  82 */       } else if (c1 == '.') {
/*  83 */         ft = "yyyy.MM.dd HH:mm";
/*     */       } else {
/*  85 */         ft = "yyyy-MM-dd HH:mm";
/*     */       } 
/*  87 */     } else if (len == 14) {
/*  88 */       ft = "yyyyMMddHHmmss";
/*  89 */     } else if (len == 10) {
/*  90 */       char c1 = val.charAt(4);
/*  91 */       if (c1 == '/') {
/*  92 */         ft = "yyyy/MM/dd";
/*  93 */       } else if (c1 == '.') {
/*  94 */         ft = "yyyy.MM.dd";
/*  95 */       } else if (c1 == '-') {
/*  96 */         ft = "yyyy-MM-dd";
/*     */       } 
/*  98 */     } else if (len == 9) {
/*  99 */       char c1 = val.charAt(4);
/* 100 */       if (c1 == '/') {
/* 101 */         ft = "yyyy/MM/dd";
/* 102 */       } else if (c1 == '.') {
/* 103 */         ft = "yyyy.MM.dd";
/* 104 */       } else if (c1 == '-') {
/* 105 */         ft = "yyyy-MM-dd";
/*     */       } else {
/* 107 */         ft = "HH时mm分ss秒";
/*     */       } 
/* 109 */     } else if (len == 8) {
/* 110 */       char c1 = val.charAt(4);
/* 111 */       if (c1 == '/') {
/* 112 */         ft = "yyyy/MM/dd";
/* 113 */       } else if (c1 == '.') {
/* 114 */         ft = "yyyy.MM.dd";
/* 115 */       } else if (c1 == '-') {
/* 116 */         ft = "yyyy-MM-dd";
/*     */       }
/* 118 */       else if (val.charAt(2) == ':') {
/* 119 */         ft = "HH:mm:ss";
/*     */       } else {
/* 121 */         ft = "yyyyMMdd";
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 126 */     if (ft != null) {
/* 127 */       DateFormat df = new SimpleDateFormat(ft, DEFAULTS.DEF_LOCALE);
/* 128 */       df.setTimeZone(DEFAULTS.DEF_TIME_ZONE);
/* 129 */       return df.parse(val);
/*     */     } 
/* 131 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(Date date, String dateFormat) {
/* 140 */     return format(date, dateFormat, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(Date date, String dateFormat, TimeZone timeZone) {
/* 147 */     DateFormat df = new SimpleDateFormat(dateFormat, DEFAULTS.DEF_LOCALE);
/* 148 */     if (timeZone != null) {
/* 149 */       df.setTimeZone(timeZone);
/*     */     }
/*     */     
/* 152 */     return df.format(date);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\cor\\utils\DateUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */