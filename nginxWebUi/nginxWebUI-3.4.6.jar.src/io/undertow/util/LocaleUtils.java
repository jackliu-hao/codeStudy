/*    */ package io.undertow.util;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocaleUtils
/*    */ {
/*    */   public static Locale getLocaleFromString(String localeString) {
/* 34 */     if (localeString == null) {
/* 35 */       return null;
/*    */     }
/* 37 */     return Locale.forLanguageTag(localeString);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Locale> getLocalesFromHeader(String acceptLanguage) {
/* 49 */     if (acceptLanguage == null) {
/* 50 */       return Collections.emptyList();
/*    */     }
/* 52 */     return getLocalesFromHeader(Collections.singletonList(acceptLanguage));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<Locale> getLocalesFromHeader(List<String> acceptLanguage) {
/* 64 */     if (acceptLanguage == null || acceptLanguage.isEmpty()) {
/* 65 */       return Collections.emptyList();
/*    */     }
/* 67 */     List<Locale> ret = new ArrayList<>();
/* 68 */     List<List<QValueParser.QValueResult>> parsedResults = QValueParser.parse(acceptLanguage);
/* 69 */     for (List<QValueParser.QValueResult> qvalueResult : parsedResults) {
/* 70 */       for (QValueParser.QValueResult res : qvalueResult) {
/* 71 */         if (!res.isQValueZero()) {
/* 72 */           Locale e = getLocaleFromString(res.getValue());
/* 73 */           ret.add(e);
/*    */         } 
/*    */       } 
/*    */     } 
/* 77 */     return ret;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\LocaleUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */