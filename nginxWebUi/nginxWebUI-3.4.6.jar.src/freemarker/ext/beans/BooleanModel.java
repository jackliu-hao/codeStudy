/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateBooleanModel;
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
/*    */ public class BooleanModel
/*    */   extends BeanModel
/*    */   implements TemplateBooleanModel
/*    */ {
/*    */   private final boolean value;
/*    */   
/*    */   public BooleanModel(Boolean bool, BeansWrapper wrapper) {
/* 32 */     super(bool, wrapper, false);
/* 33 */     this.value = bool.booleanValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getAsBoolean() {
/* 38 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\BooleanModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */