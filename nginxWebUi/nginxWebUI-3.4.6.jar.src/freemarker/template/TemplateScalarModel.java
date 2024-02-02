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
/*    */ public interface TemplateScalarModel
/*    */   extends TemplateModel
/*    */ {
/* 37 */   public static final TemplateModel EMPTY_STRING = new SimpleScalar("");
/*    */   
/*    */   String getAsString() throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateScalarModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */