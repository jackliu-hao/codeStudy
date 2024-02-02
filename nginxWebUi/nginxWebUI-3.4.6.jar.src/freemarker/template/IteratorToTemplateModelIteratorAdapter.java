/*    */ package freemarker.template;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
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
/*    */ class IteratorToTemplateModelIteratorAdapter
/*    */   implements TemplateModelIterator
/*    */ {
/*    */   private final Iterator<?> it;
/*    */   private final ObjectWrapper wrapper;
/*    */   
/*    */   IteratorToTemplateModelIteratorAdapter(Iterator<?> it, ObjectWrapper wrapper) {
/* 34 */     this.it = it;
/* 35 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel next() throws TemplateModelException {
/*    */     try {
/* 41 */       return this.wrapper.wrap(this.it.next());
/* 42 */     } catch (NoSuchElementException e) {
/* 43 */       throw new TemplateModelException("The collection has no more items.", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() throws TemplateModelException {
/* 49 */     return this.it.hasNext();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\IteratorToTemplateModelIteratorAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */