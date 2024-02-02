/*    */ package freemarker.core;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class _CoreLocaleUtils
/*    */ {
/*    */   public static Locale getLessSpecificLocale(Locale locale) {
/* 34 */     String country = locale.getCountry();
/* 35 */     if (locale.getVariant().length() != 0) {
/* 36 */       String language = locale.getLanguage();
/* 37 */       return (country != null) ? new Locale(language, country) : new Locale(language);
/*    */     } 
/* 39 */     if (country.length() != 0) {
/* 40 */       return new Locale(locale.getLanguage());
/*    */     }
/* 42 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_CoreLocaleUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */