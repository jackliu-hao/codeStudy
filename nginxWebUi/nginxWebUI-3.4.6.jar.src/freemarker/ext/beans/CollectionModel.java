/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.ext.util.ModelFactory;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
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
/*     */ public class CollectionModel
/*     */   extends StringModel
/*     */   implements TemplateCollectionModel, TemplateSequenceModel
/*     */ {
/*  44 */   static final ModelFactory FACTORY = new ModelFactory()
/*     */     {
/*     */       
/*     */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*     */       {
/*  49 */         return (TemplateModel)new CollectionModel((Collection)object, (BeansWrapper)wrapper);
/*     */       }
/*     */     };
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
/*     */   public CollectionModel(Collection collection, BeansWrapper wrapper) {
/*  63 */     super(collection, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(int index) throws TemplateModelException {
/*  75 */     if (this.object instanceof List) {
/*     */       try {
/*  77 */         return wrap(((List)this.object).get(index));
/*  78 */       } catch (IndexOutOfBoundsException e) {
/*  79 */         return null;
/*     */       } 
/*     */     }
/*     */     
/*  83 */     throw new TemplateModelException("Underlying collection is not a list, it's " + this.object.getClass().getName());
/*     */   }
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
/*     */   public boolean getSupportsIndexedAccess() {
/*  97 */     return this.object instanceof List;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModelIterator iterator() {
/* 102 */     return new IteratorModel(((Collection)this.object).iterator(), this.wrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 107 */     return ((Collection)this.object).size();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\CollectionModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */