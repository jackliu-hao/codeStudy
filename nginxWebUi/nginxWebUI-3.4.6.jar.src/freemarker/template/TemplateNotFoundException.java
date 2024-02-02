/*    */ package freemarker.template;
/*    */ 
/*    */ import java.io.FileNotFoundException;
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
/*    */ public final class TemplateNotFoundException
/*    */   extends FileNotFoundException
/*    */ {
/*    */   private final String templateName;
/*    */   private final Object customLookupCondition;
/*    */   
/*    */   public TemplateNotFoundException(String templateName, Object customLookupCondition, String message) {
/* 40 */     super(message);
/* 41 */     this.templateName = templateName;
/* 42 */     this.customLookupCondition = customLookupCondition;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getTemplateName() {
/* 49 */     return this.templateName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getCustomLookupCondition() {
/* 58 */     return this.customLookupCondition;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateNotFoundException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */