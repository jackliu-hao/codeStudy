/*     */ package freemarker.template;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
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
/*     */ public class SimpleCollection
/*     */   extends WrappingTemplateModel
/*     */   implements TemplateCollectionModel, Serializable
/*     */ {
/*     */   private boolean iteratorOwned;
/*     */   private final Iterator iterator;
/*     */   private final Iterable iterable;
/*     */   
/*     */   @Deprecated
/*     */   public SimpleCollection(Iterator iterator) {
/*  53 */     this.iterator = iterator;
/*  54 */     this.iterable = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public SimpleCollection(Iterable iterable) {
/*  62 */     this.iterable = iterable;
/*  63 */     this.iterator = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public SimpleCollection(Collection collection) {
/*  73 */     this(collection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCollection(Collection collection, ObjectWrapper wrapper) {
/*  80 */     this(collection, wrapper);
/*     */   }
/*     */   
/*     */   public SimpleCollection(Iterator iterator, ObjectWrapper wrapper) {
/*  84 */     super(wrapper);
/*  85 */     this.iterator = iterator;
/*  86 */     this.iterable = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleCollection(Iterable iterable, ObjectWrapper wrapper) {
/*  93 */     super(wrapper);
/*  94 */     this.iterable = iterable;
/*  95 */     this.iterator = null;
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
/*     */   public TemplateModelIterator iterator() {
/* 109 */     return (this.iterator != null) ? new SimpleTemplateModelIterator(this.iterator, false) : new SimpleTemplateModelIterator(this.iterable
/*     */         
/* 111 */         .iterator(), true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class SimpleTemplateModelIterator
/*     */     implements TemplateModelIterator
/*     */   {
/*     */     private final Iterator iterator;
/*     */ 
/*     */     
/*     */     private boolean iteratorOwnedByMe;
/*     */ 
/*     */     
/*     */     SimpleTemplateModelIterator(Iterator iterator, boolean iteratorOwnedByMe) {
/* 126 */       this.iterator = iterator;
/* 127 */       this.iteratorOwnedByMe = iteratorOwnedByMe;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel next() throws TemplateModelException {
/* 132 */       if (!this.iteratorOwnedByMe) {
/* 133 */         synchronized (SimpleCollection.this) {
/* 134 */           checkIteratorOwned();
/* 135 */           SimpleCollection.this.iteratorOwned = true;
/* 136 */           this.iteratorOwnedByMe = true;
/*     */         } 
/*     */       }
/*     */       
/* 140 */       if (!this.iterator.hasNext()) {
/* 141 */         throw new TemplateModelException("The collection has no more items.");
/*     */       }
/*     */       
/* 144 */       Object value = this.iterator.next();
/* 145 */       return (value instanceof TemplateModel) ? (TemplateModel)value : SimpleCollection.this.wrap(value);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasNext() throws TemplateModelException {
/* 151 */       if (!this.iteratorOwnedByMe) {
/* 152 */         synchronized (SimpleCollection.this) {
/* 153 */           checkIteratorOwned();
/*     */         } 
/*     */       }
/*     */       
/* 157 */       return this.iterator.hasNext();
/*     */     }
/*     */     
/*     */     private void checkIteratorOwned() throws TemplateModelException {
/* 161 */       if (SimpleCollection.this.iteratorOwned)
/* 162 */         throw new TemplateModelException("This collection value wraps a java.util.Iterator, thus it can be listed only once."); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\SimpleCollection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */