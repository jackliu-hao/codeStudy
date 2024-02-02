/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public class IteratorModel
/*     */   extends BeanModel
/*     */   implements TemplateModelIterator, TemplateCollectionModel
/*     */ {
/*     */   private boolean accessed = false;
/*     */   
/*     */   public IteratorModel(Iterator iterator, BeansWrapper wrapper) {
/*  61 */     super(iterator, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelIterator iterator() throws TemplateModelException {
/*  70 */     synchronized (this) {
/*  71 */       if (this.accessed) {
/*  72 */         throw new TemplateModelException("This collection is stateful and can not be iterated over the second time.");
/*     */       }
/*     */ 
/*     */       
/*  76 */       this.accessed = true;
/*     */     } 
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  86 */     return ((Iterator)this.object).hasNext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel next() throws TemplateModelException {
/*     */     try {
/*  97 */       return wrap(((Iterator)this.object).next());
/*  98 */     } catch (NoSuchElementException e) {
/*  99 */       throw new TemplateModelException("No more elements in the iterator.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAsBoolean() {
/* 110 */     return hasNext();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\IteratorModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */