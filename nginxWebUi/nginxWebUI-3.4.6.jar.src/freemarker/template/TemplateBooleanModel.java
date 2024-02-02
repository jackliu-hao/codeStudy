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
/*    */ public interface TemplateBooleanModel
/*    */   extends TemplateModel
/*    */ {
/* 40 */   public static final TemplateBooleanModel FALSE = new FalseTemplateBooleanModel();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 45 */   public static final TemplateBooleanModel TRUE = new TrueTemplateBooleanModel();
/*    */   
/*    */   boolean getAsBoolean() throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateBooleanModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */