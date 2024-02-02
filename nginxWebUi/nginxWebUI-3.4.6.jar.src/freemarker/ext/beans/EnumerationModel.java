/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import java.util.Enumeration;
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
/*     */ public class EnumerationModel
/*     */   extends BeanModel
/*     */   implements TemplateModelIterator, TemplateCollectionModel
/*     */ {
/*     */   private boolean accessed = false;
/*     */   
/*     */   public EnumerationModel(Enumeration enumeration, BeansWrapper wrapper) {
/*  57 */     super(enumeration, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelIterator iterator() throws TemplateModelException {
/*  66 */     synchronized (this) {
/*  67 */       if (this.accessed) {
/*  68 */         throw new TemplateModelException("This collection is stateful and can not be iterated over the second time.");
/*     */       }
/*     */ 
/*     */       
/*  72 */       this.accessed = true;
/*     */     } 
/*  74 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  82 */     return ((Enumeration)this.object).hasMoreElements();
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
/*  93 */       return wrap(((Enumeration)this.object).nextElement());
/*  94 */     } catch (NoSuchElementException e) {
/*  95 */       throw new TemplateModelException("No more elements in the enumeration.");
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
/* 106 */     return hasNext();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\EnumerationModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */