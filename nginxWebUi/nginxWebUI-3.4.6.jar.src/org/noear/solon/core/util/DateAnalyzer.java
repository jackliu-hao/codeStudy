/*     */ package org.noear.solon.core.util;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import org.noear.solon.Solon;
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
/*     */ public class DateAnalyzer
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
/*  42 */   private static DateAnalyzer global = new DateAnalyzer();
/*     */   
/*     */   public static void setGlobal(DateAnalyzer instance) {
/*  45 */     if (instance != null) {
/*  46 */       global = instance;
/*     */     }
/*     */   }
/*     */   
/*     */   public static DateAnalyzer getGlobal() {
/*  51 */     return global;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date parse(String val) throws ParseException {
/*  59 */     int len = val.length();
/*  60 */     String ft = null;
/*     */ 
/*     */     
/*  63 */     if (len == 29) {
/*  64 */       if (val.charAt(26) == ':' && val.charAt(28) == '0') {
/*  65 */         ft = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
/*     */       }
/*  67 */     } else if (len == 24) {
/*  68 */       if (val.charAt(10) == 'T') {
/*  69 */         ft = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
/*     */       }
/*  71 */     } else if (len == 23) {
/*  72 */       if (val.charAt(19) == ',') {
/*  73 */         ft = "yyyy-MM-dd HH:mm:ss,SSS";
/*     */       } else {
/*  75 */         ft = "yyyy-MM-dd HH:mm:ss.SSS";
/*     */       } 
/*  77 */     } else if (len == 22) {
/*  78 */       ft = "yyyyMMddHHmmssSSSZ";
/*  79 */     } else if (len == 19) {
/*  80 */       if (val.charAt(10) == 'T') {
/*  81 */         ft = "yyyy-MM-dd'T'HH:mm:ss";
/*     */       } else {
/*  83 */         char c1 = val.charAt(4);
/*  84 */         if (c1 == '/') {
/*  85 */           ft = "yyyy/MM/dd HH:mm:ss";
/*  86 */         } else if (c1 == '.') {
/*  87 */           ft = "yyyy.MM.dd HH:mm:ss";
/*     */         } else {
/*  89 */           ft = "yyyy-MM-dd HH:mm:ss";
/*     */         } 
/*     */       } 
/*  92 */     } else if (len == 17) {
/*  93 */       ft = "yyyyMMddHHmmssSSS";
/*  94 */     } else if (len == 16) {
/*  95 */       char c1 = val.charAt(4);
/*  96 */       if (c1 == '/') {
/*  97 */         ft = "yyyy/MM/dd HH:mm";
/*  98 */       } else if (c1 == '.') {
/*  99 */         ft = "yyyy.MM.dd HH:mm";
/*     */       } else {
/* 101 */         ft = "yyyy-MM-dd HH:mm";
/*     */       } 
/* 103 */     } else if (len == 14) {
/* 104 */       ft = "yyyyMMddHHmmss";
/* 105 */     } else if (len == 10) {
/* 106 */       char c1 = val.charAt(4);
/* 107 */       if (c1 == '/') {
/* 108 */         ft = "yyyy/MM/dd";
/* 109 */       } else if (c1 == '.') {
/* 110 */         ft = "yyyy.MM.dd";
/* 111 */       } else if (c1 == '-') {
/* 112 */         ft = "yyyy-MM-dd";
/*     */       } 
/* 114 */     } else if (len == 9) {
/* 115 */       char c1 = val.charAt(4);
/* 116 */       if (c1 == '/') {
/* 117 */         ft = "yyyy/MM/dd";
/* 118 */       } else if (c1 == '.') {
/* 119 */         ft = "yyyy.MM.dd";
/* 120 */       } else if (c1 == '-') {
/* 121 */         ft = "yyyy-MM-dd";
/*     */       } else {
/* 123 */         ft = "HH时mm分ss秒";
/*     */       } 
/* 125 */     } else if (len == 8) {
/* 126 */       char c1 = val.charAt(4);
/* 127 */       if (c1 == '/') {
/* 128 */         ft = "yyyy/MM/dd";
/* 129 */       } else if (c1 == '.') {
/* 130 */         ft = "yyyy.MM.dd";
/* 131 */       } else if (c1 == '-') {
/* 132 */         ft = "yyyy-MM-dd";
/*     */       }
/* 134 */       else if (val.charAt(2) == ':') {
/* 135 */         ft = "HH:mm:ss";
/*     */       } else {
/* 137 */         ft = "yyyyMMdd";
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 142 */     if (ft != null) {
/* 143 */       DateFormat df = null;
/* 144 */       if (Solon.app() == null) {
/* 145 */         df = new SimpleDateFormat(ft);
/*     */       } else {
/* 147 */         df = new SimpleDateFormat(ft, Solon.cfg().locale());
/*     */       } 
/*     */       
/* 150 */       df.setTimeZone(TimeZone.getDefault());
/* 151 */       return df.parse(val);
/*     */     } 
/* 153 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\DateAnalyzer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */