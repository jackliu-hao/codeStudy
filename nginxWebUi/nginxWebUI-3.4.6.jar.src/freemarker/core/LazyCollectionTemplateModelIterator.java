/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
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
/*    */ class LazyCollectionTemplateModelIterator
/*    */   implements TemplateModelIterator
/*    */ {
/*    */   private final TemplateCollectionModel templateCollectionModel;
/*    */   private TemplateModelIterator iterator;
/*    */   
/*    */   public LazyCollectionTemplateModelIterator(TemplateCollectionModel templateCollectionModel) {
/* 36 */     this.templateCollectionModel = templateCollectionModel;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel next() throws TemplateModelException {
/* 41 */     ensureIteratorInitialized();
/* 42 */     return this.iterator.next();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() throws TemplateModelException {
/* 47 */     ensureIteratorInitialized();
/* 48 */     return this.iterator.hasNext();
/*    */   }
/*    */   
/*    */   private void ensureIteratorInitialized() throws TemplateModelException {
/* 52 */     if (this.iterator == null)
/* 53 */       this.iterator = this.templateCollectionModel.iterator(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LazyCollectionTemplateModelIterator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */