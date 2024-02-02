/*    */ package freemarker.template;
/*    */ 
/*    */ import freemarker.core.Environment;
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
/*    */ public abstract class LocalizedString
/*    */   implements TemplateScalarModel
/*    */ {
/*    */   public String getAsString() throws TemplateModelException {
/* 49 */     Environment env = Environment.getCurrentEnvironment();
/* 50 */     Locale locale = env.getLocale();
/* 51 */     return getLocalizedString(locale);
/*    */   }
/*    */   
/*    */   public abstract String getLocalizedString(Locale paramLocale) throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\LocalizedString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */