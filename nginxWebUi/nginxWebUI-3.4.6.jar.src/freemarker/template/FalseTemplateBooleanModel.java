/*    */ package freemarker.template;
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
/*    */ final class FalseTemplateBooleanModel
/*    */   implements SerializableTemplateBooleanModel
/*    */ {
/*    */   public boolean getAsBoolean() {
/* 29 */     return false;
/*    */   }
/*    */   
/*    */   private Object readResolve() {
/* 33 */     return FALSE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\FalseTemplateBooleanModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */