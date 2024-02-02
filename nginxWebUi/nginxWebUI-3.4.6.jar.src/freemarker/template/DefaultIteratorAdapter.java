/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.ext.util.WrapperTemplateModel;
/*     */ import freemarker.template.utility.ObjectWrapperWithAPISupport;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultIteratorAdapter
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateCollectionModel, AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, Serializable
/*     */ {
/*     */   private final Iterator iterator;
/*     */   private boolean iteratorOwnedBySomeone;
/*     */   
/*     */   public static DefaultIteratorAdapter adapt(Iterator iterator, ObjectWrapper wrapper) {
/*  60 */     return new DefaultIteratorAdapter(iterator, wrapper);
/*     */   }
/*     */   
/*     */   private DefaultIteratorAdapter(Iterator iterator, ObjectWrapper wrapper) {
/*  64 */     super(wrapper);
/*  65 */     this.iterator = iterator;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getWrappedObject() {
/*  70 */     return this.iterator;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAdaptedObject(Class hint) {
/*  75 */     return getWrappedObject();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModelIterator iterator() throws TemplateModelException {
/*  80 */     return new SimpleTemplateModelIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel getAPI() throws TemplateModelException {
/*  85 */     return ((ObjectWrapperWithAPISupport)getObjectWrapper()).wrapAsAPI(this.iterator);
/*     */   }
/*     */ 
/*     */   
/*     */   private class SimpleTemplateModelIterator
/*     */     implements TemplateModelIterator
/*     */   {
/*     */     private boolean iteratorOwnedByMe;
/*     */     
/*     */     private SimpleTemplateModelIterator() {}
/*     */     
/*     */     public TemplateModel next() throws TemplateModelException {
/*  97 */       if (!this.iteratorOwnedByMe) {
/*  98 */         checkNotOwner();
/*  99 */         DefaultIteratorAdapter.this.iteratorOwnedBySomeone = true;
/* 100 */         this.iteratorOwnedByMe = true;
/*     */       } 
/*     */       
/* 103 */       if (!DefaultIteratorAdapter.this.iterator.hasNext()) {
/* 104 */         throw new TemplateModelException("The collection has no more items.");
/*     */       }
/*     */       
/* 107 */       Object value = DefaultIteratorAdapter.this.iterator.next();
/* 108 */       return (value instanceof TemplateModel) ? (TemplateModel)value : DefaultIteratorAdapter.this.wrap(value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasNext() throws TemplateModelException {
/* 114 */       if (!this.iteratorOwnedByMe) {
/* 115 */         checkNotOwner();
/*     */       }
/*     */       
/* 118 */       return DefaultIteratorAdapter.this.iterator.hasNext();
/*     */     }
/*     */     
/*     */     private void checkNotOwner() throws TemplateModelException {
/* 122 */       if (DefaultIteratorAdapter.this.iteratorOwnedBySomeone)
/* 123 */         throw new TemplateModelException("This collection value wraps a java.util.Iterator, thus it can be listed only once."); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\DefaultIteratorAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */