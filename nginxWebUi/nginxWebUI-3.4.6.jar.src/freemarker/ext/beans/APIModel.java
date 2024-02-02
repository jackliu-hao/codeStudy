/*    */ package freemarker.ext.beans;
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
/*    */ final class APIModel
/*    */   extends BeanModel
/*    */ {
/*    */   APIModel(Object object, BeansWrapper wrapper) {
/* 38 */     super(object, wrapper, false);
/*    */   }
/*    */   
/*    */   protected boolean isMethodsShadowItems() {
/* 42 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\APIModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */