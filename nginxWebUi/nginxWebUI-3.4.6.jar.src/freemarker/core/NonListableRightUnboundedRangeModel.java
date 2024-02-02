/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateModelException;
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
/*    */ final class NonListableRightUnboundedRangeModel
/*    */   extends RightUnboundedRangeModel
/*    */ {
/*    */   NonListableRightUnboundedRangeModel(int begin) {
/* 32 */     super(begin);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() throws TemplateModelException {
/* 37 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\NonListableRightUnboundedRangeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */