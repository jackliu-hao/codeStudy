/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
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
/*    */ abstract class Interpolation
/*    */   extends TemplateElement
/*    */ {
/*    */   protected abstract String dump(boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/*    */   protected final String dump(boolean canonical) {
/* 29 */     return dump(canonical, false);
/*    */   }
/*    */   
/*    */   final String getCanonicalFormInStringLiteral() {
/* 33 */     return dump(true, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract Object calculateInterpolatedStringOrMarkup(Environment paramEnvironment) throws TemplateException;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean isShownInStackTrace() {
/* 45 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Interpolation.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */