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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ObjectWrapperAndUnwrapper
/*    */   extends ObjectWrapper
/*    */ {
/* 49 */   public static final Object CANT_UNWRAP_TO_TARGET_CLASS = new Object();
/*    */   
/*    */   Object unwrap(TemplateModel paramTemplateModel) throws TemplateModelException;
/*    */   
/*    */   Object tryUnwrapTo(TemplateModel paramTemplateModel, Class<?> paramClass) throws TemplateModelException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\ObjectWrapperAndUnwrapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */