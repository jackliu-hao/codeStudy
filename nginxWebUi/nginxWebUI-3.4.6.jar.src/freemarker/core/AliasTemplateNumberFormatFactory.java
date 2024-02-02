/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.StringUtil;
/*    */ import java.util.Locale;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ public final class AliasTemplateNumberFormatFactory
/*    */   extends TemplateNumberFormatFactory
/*    */ {
/*    */   private final String defaultTargetFormatString;
/*    */   private final Map<Locale, String> localizedTargetFormatStrings;
/*    */   
/*    */   public AliasTemplateNumberFormatFactory(String targetFormatString) {
/* 42 */     this.defaultTargetFormatString = targetFormatString;
/* 43 */     this.localizedTargetFormatStrings = null;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AliasTemplateNumberFormatFactory(String defaultTargetFormatString, Map<Locale, String> localizedTargetFormatStrings) {
/* 60 */     this.defaultTargetFormatString = defaultTargetFormatString;
/* 61 */     this.localizedTargetFormatStrings = localizedTargetFormatStrings;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateNumberFormat get(String params, Locale locale, Environment env) throws TemplateValueFormatException {
/* 67 */     TemplateFormatUtil.checkHasNoParameters(params);
/*    */     try {
/*    */       String targetFormatString;
/* 70 */       if (this.localizedTargetFormatStrings != null) {
/* 71 */         Locale lookupLocale = locale;
/* 72 */         targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
/* 73 */         while (targetFormatString == null && (
/* 74 */           lookupLocale = _CoreLocaleUtils.getLessSpecificLocale(lookupLocale)) != null) {
/* 75 */           targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
/*    */         }
/*    */       } else {
/* 78 */         targetFormatString = null;
/*    */       } 
/* 80 */       if (targetFormatString == null) {
/* 81 */         targetFormatString = this.defaultTargetFormatString;
/*    */       }
/* 83 */       return env.getTemplateNumberFormat(targetFormatString, locale);
/* 84 */     } catch (TemplateValueFormatException e) {
/* 85 */       throw new AliasTargetTemplateValueFormatException("Failed to create format based on target format string,  " + 
/* 86 */           StringUtil.jQuote(params) + ". Reason given: " + e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\AliasTemplateNumberFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */