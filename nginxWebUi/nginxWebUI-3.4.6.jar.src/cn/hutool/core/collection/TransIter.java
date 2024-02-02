/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.Iterator;
/*    */ import java.util.function.Function;
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
/*    */ public class TransIter<F, T>
/*    */   implements Iterator<T>
/*    */ {
/*    */   private final Iterator<? extends F> backingIterator;
/*    */   private final Function<? super F, ? extends T> func;
/*    */   
/*    */   public TransIter(Iterator<? extends F> backingIterator, Function<? super F, ? extends T> func) {
/* 28 */     this.backingIterator = (Iterator<? extends F>)Assert.notNull(backingIterator);
/* 29 */     this.func = (Function<? super F, ? extends T>)Assert.notNull(func);
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean hasNext() {
/* 34 */     return this.backingIterator.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public final T next() {
/* 39 */     return this.func.apply(this.backingIterator.next());
/*    */   }
/*    */ 
/*    */   
/*    */   public final void remove() {
/* 44 */     this.backingIterator.remove();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\TransIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */