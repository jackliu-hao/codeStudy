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
/*    */ public class SimpleObjectWrapper
/*    */   extends DefaultObjectWrapper
/*    */ {
/* 31 */   static final SimpleObjectWrapper instance = new SimpleObjectWrapper();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public SimpleObjectWrapper() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SimpleObjectWrapper(Version incompatibleImprovements) {
/* 47 */     super(incompatibleImprovements);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
/* 56 */     throw new TemplateModelException(getClass().getName() + " deliberately won't wrap this type: " + obj
/* 57 */         .getClass().getName());
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateHashModel wrapAsAPI(Object obj) throws TemplateModelException {
/* 62 */     throw new TemplateModelException(getClass().getName() + " deliberately doesn't allow ?api.");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\SimpleObjectWrapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */