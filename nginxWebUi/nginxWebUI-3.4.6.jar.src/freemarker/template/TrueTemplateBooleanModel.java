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
/*    */ final class TrueTemplateBooleanModel
/*    */   implements SerializableTemplateBooleanModel
/*    */ {
/*    */   public boolean getAsBoolean() {
/* 29 */     return true;
/*    */   }
/*    */   
/*    */   private Object readResolve() {
/* 33 */     return TRUE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TrueTemplateBooleanModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */