/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.TemplateCollectionModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelAdapter;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateModelIterator;
/*    */ import freemarker.template.utility.UndeclaredThrowableException;
/*    */ import java.util.AbstractCollection;
/*    */ import java.util.Iterator;
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
/*    */ class CollectionAdapter
/*    */   extends AbstractCollection
/*    */   implements TemplateModelAdapter
/*    */ {
/*    */   private final BeansWrapper wrapper;
/*    */   private final TemplateCollectionModel model;
/*    */   
/*    */   CollectionAdapter(TemplateCollectionModel model, BeansWrapper wrapper) {
/* 41 */     this.model = model;
/* 42 */     this.wrapper = wrapper;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel getTemplateModel() {
/* 47 */     return (TemplateModel)this.model;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 52 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator iterator() {
/*    */     try {
/* 58 */       return new Iterator() {
/* 59 */           final TemplateModelIterator i = CollectionAdapter.this.model.iterator();
/*    */ 
/*    */           
/*    */           public boolean hasNext() {
/*    */             try {
/* 64 */               return this.i.hasNext();
/* 65 */             } catch (TemplateModelException e) {
/* 66 */               throw new UndeclaredThrowableException(e);
/*    */             } 
/*    */           }
/*    */ 
/*    */           
/*    */           public Object next() {
/*    */             try {
/* 73 */               return CollectionAdapter.this.wrapper.unwrap(this.i.next());
/* 74 */             } catch (TemplateModelException e) {
/* 75 */               throw new UndeclaredThrowableException(e);
/*    */             } 
/*    */           }
/*    */ 
/*    */           
/*    */           public void remove() {
/* 81 */             throw new UnsupportedOperationException();
/*    */           }
/*    */         };
/* 84 */     } catch (TemplateModelException e) {
/* 85 */       throw new UndeclaredThrowableException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\CollectionAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */