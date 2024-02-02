/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.lang.Chain;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IterChain<T>
/*    */   implements Iterator<T>, Chain<Iterator<T>, IterChain<T>>
/*    */ {
/* 19 */   protected final List<Iterator<T>> allIterators = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int currentIter;
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
/*    */   public IterChain<T> addChain(Iterator<T> iterator) {
/* 41 */     if (this.allIterators.contains(iterator)) {
/* 42 */       throw new IllegalArgumentException("Duplicate iterator");
/*    */     }
/* 44 */     this.allIterators.add(iterator);
/* 45 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public IterChain() {
/* 50 */     this.currentIter = -1; } @SafeVarargs public IterChain(Iterator<T>... iterators) { this.currentIter = -1;
/*    */     for (Iterator<T> iterator : iterators)
/*    */       addChain(iterator);  }
/*    */    public boolean hasNext() {
/* 54 */     if (this.currentIter == -1) {
/* 55 */       this.currentIter = 0;
/*    */     }
/*    */     
/* 58 */     int size = this.allIterators.size();
/* 59 */     for (int i = this.currentIter; i < size; i++) {
/* 60 */       Iterator<T> iterator = this.allIterators.get(i);
/* 61 */       if (iterator.hasNext()) {
/* 62 */         this.currentIter = i;
/* 63 */         return true;
/*    */       } 
/*    */     } 
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public T next() {
/* 71 */     if (false == hasNext()) {
/* 72 */       throw new NoSuchElementException();
/*    */     }
/*    */     
/* 75 */     return ((Iterator<T>)this.allIterators.get(this.currentIter)).next();
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 80 */     if (-1 == this.currentIter) {
/* 81 */       throw new IllegalStateException("next() has not yet been called");
/*    */     }
/*    */     
/* 84 */     ((Iterator)this.allIterators.get(this.currentIter)).remove();
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<Iterator<T>> iterator() {
/* 89 */     return this.allIterators.iterator();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\IterChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */