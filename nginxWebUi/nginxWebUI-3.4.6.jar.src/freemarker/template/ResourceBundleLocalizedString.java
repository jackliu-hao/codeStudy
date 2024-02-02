/*    */ package freemarker.template;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.MissingResourceException;
/*    */ import java.util.ResourceBundle;
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
/*    */ public class ResourceBundleLocalizedString
/*    */   extends LocalizedString
/*    */ {
/*    */   private String resourceKey;
/*    */   private String resourceBundleLookupKey;
/*    */   
/*    */   public ResourceBundleLocalizedString(String resourceBundleLookupKey, String resourceKey) {
/* 41 */     this.resourceBundleLookupKey = resourceBundleLookupKey;
/* 42 */     this.resourceKey = resourceKey;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getLocalizedString(Locale locale) throws TemplateModelException {
/*    */     try {
/* 48 */       ResourceBundle rb = ResourceBundle.getBundle(this.resourceBundleLookupKey, locale);
/* 49 */       return rb.getString(this.resourceKey);
/* 50 */     } catch (MissingResourceException mre) {
/* 51 */       throw new TemplateModelException("missing resource", mre);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\ResourceBundleLocalizedString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */