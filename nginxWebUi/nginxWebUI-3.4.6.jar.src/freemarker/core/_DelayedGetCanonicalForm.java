/*    */ package freemarker.core;
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
/*    */ public class _DelayedGetCanonicalForm
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedGetCanonicalForm(TemplateObject obj) {
/* 27 */     super(obj);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/*    */     try {
/* 33 */       return ((TemplateObject)obj).getCanonicalForm();
/* 34 */     } catch (Exception e) {
/* 35 */       return "{Error getting canonical form}";
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedGetCanonicalForm.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */