/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.StringUtil;
/*    */ import java.util.Locale;
/*    */ import java.util.Map;
/*    */ import java.util.TimeZone;
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
/*    */ public final class AliasTemplateDateFormatFactory
/*    */   extends TemplateDateFormatFactory
/*    */ {
/*    */   private final String defaultTargetFormatString;
/*    */   private final Map<Locale, String> localizedTargetFormatStrings;
/*    */   
/*    */   public AliasTemplateDateFormatFactory(String targetFormatString) {
/* 43 */     this.defaultTargetFormatString = targetFormatString;
/* 44 */     this.localizedTargetFormatStrings = null;
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
/*    */   public AliasTemplateDateFormatFactory(String defaultTargetFormatString, Map<Locale, String> localizedTargetFormatStrings) {
/* 61 */     this.defaultTargetFormatString = defaultTargetFormatString;
/* 62 */     this.localizedTargetFormatStrings = localizedTargetFormatStrings;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws TemplateValueFormatException {
/* 68 */     TemplateFormatUtil.checkHasNoParameters(params);
/*    */     try {
/*    */       String targetFormatString;
/* 71 */       if (this.localizedTargetFormatStrings != null) {
/* 72 */         Locale lookupLocale = locale;
/* 73 */         targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
/* 74 */         while (targetFormatString == null && (
/* 75 */           lookupLocale = _CoreLocaleUtils.getLessSpecificLocale(lookupLocale)) != null) {
/* 76 */           targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
/*    */         }
/*    */       } else {
/* 79 */         targetFormatString = null;
/*    */       } 
/* 81 */       if (targetFormatString == null) {
/* 82 */         targetFormatString = this.defaultTargetFormatString;
/*    */       }
/* 84 */       return env.getTemplateDateFormat(targetFormatString, dateType, locale, timeZone, zonelessInput);
/* 85 */     } catch (TemplateValueFormatException e) {
/* 86 */       throw new AliasTargetTemplateValueFormatException("Failed to create format based on target format string,  " + 
/* 87 */           StringUtil.jQuote(params) + ". Reason given: " + e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\AliasTemplateDateFormatFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */