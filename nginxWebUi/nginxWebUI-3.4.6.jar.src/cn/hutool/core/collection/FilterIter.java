/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.lang.Filter;
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
/*    */ public class FilterIter<E>
/*    */   implements Iterator<E>
/*    */ {
/*    */   private final Iterator<? extends E> iterator;
/*    */   private final Filter<? super E> filter;
/*    */   private E nextObject;
/*    */   private boolean nextObjectSet = false;
/*    */   
/*    */   public FilterIter(Iterator<? extends E> iterator, Filter<? super E> filter) {
/* 37 */     this.iterator = (Iterator<? extends E>)Assert.notNull(iterator);
/* 38 */     this.filter = filter;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 43 */     return (this.nextObjectSet || setNextObject());
/*    */   }
/*    */ 
/*    */   
/*    */   public E next() {
/* 48 */     if (false == this.nextObjectSet && false == setNextObject()) {
/* 49 */       throw new NoSuchElementException();
/*    */     }
/* 51 */     this.nextObjectSet = false;
/* 52 */     return this.nextObject;
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 57 */     if (this.nextObjectSet) {
/* 58 */       throw new IllegalStateException("remove() cannot be called");
/*    */     }
/* 60 */     this.iterator.remove();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Iterator<? extends E> getIterator() {
/* 69 */     return this.iterator;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Filter<? super E> getFilter() {
/* 78 */     return this.filter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean setNextObject() {
/* 85 */     while (this.iterator.hasNext()) {
/* 86 */       E object = this.iterator.next();
/* 87 */       if (null != this.filter && this.filter.accept(object)) {
/* 88 */         this.nextObject = object;
/* 89 */         this.nextObjectSet = true;
/* 90 */         return true;
/*    */       } 
/*    */     } 
/* 93 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\FilterIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */