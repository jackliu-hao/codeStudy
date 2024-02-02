/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.utility.ClassUtil;
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
/*    */ public class _DelayedFTLTypeDescription
/*    */   extends _DelayedConversionToString
/*    */ {
/*    */   public _DelayedFTLTypeDescription(TemplateModel tm) {
/* 29 */     super(tm);
/*    */   }
/*    */ 
/*    */   
/*    */   protected String doConversion(Object obj) {
/* 34 */     return ClassUtil.getFTLTypeDescription((TemplateModel)obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_DelayedFTLTypeDescription.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */