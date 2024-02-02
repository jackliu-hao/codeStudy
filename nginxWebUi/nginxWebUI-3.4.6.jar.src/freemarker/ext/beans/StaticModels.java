/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
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
/*    */ 
/*    */ 
/*    */ class StaticModels
/*    */   extends ClassBasedModelFactory
/*    */ {
/*    */   StaticModels(BeansWrapper wrapper) {
/* 35 */     super(wrapper);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected TemplateModel createModel(Class<?> clazz) throws TemplateModelException {
/* 41 */     return (TemplateModel)new StaticModel(clazz, getWrapper());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\StaticModels.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */