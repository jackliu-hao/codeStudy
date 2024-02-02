/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
/*    */ import freemarker.template.utility.NullArgumentException;
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
/*    */ class SingleIterationCollectionModel
/*    */   implements TemplateCollectionModel
/*    */ {
/*    */   private TemplateModelIterator iterator;
/*    */   
/*    */   SingleIterationCollectionModel(TemplateModelIterator iterator) {
/* 38 */     NullArgumentException.check(iterator);
/* 39 */     this.iterator = iterator;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModelIterator iterator() throws TemplateModelException {
/* 44 */     if (this.iterator == null) {
/* 45 */       throw new IllegalStateException("Can't return the iterator again, as this TemplateCollectionModel can only be iterated once.");
/*    */     }
/*    */     
/* 48 */     TemplateModelIterator result = this.iterator;
/* 49 */     this.iterator = null;
/* 50 */     return result;
/*    */   }
/*    */   
/*    */   protected TemplateModelIterator getIterator() {
/* 54 */     return this.iterator;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\SingleIterationCollectionModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */