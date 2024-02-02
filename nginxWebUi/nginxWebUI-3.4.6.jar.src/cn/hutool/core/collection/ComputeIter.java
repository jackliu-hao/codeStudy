/*    */ package cn.hutool.core.collection;
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
/*    */ public abstract class ComputeIter<T>
/*    */   implements Iterator<T>
/*    */ {
/*    */   private T next;
/*    */   private boolean finished;
/*    */   
/*    */   protected abstract T computeNext();
/*    */   
/*    */   public boolean hasNext() {
/* 34 */     if (null != this.next)
/*    */     {
/* 36 */       return true; } 
/* 37 */     if (this.finished)
/*    */     {
/* 39 */       return false;
/*    */     }
/*    */     
/* 42 */     T result = computeNext();
/* 43 */     if (null == result) {
/*    */       
/* 45 */       this.finished = true;
/* 46 */       return false;
/*    */     } 
/* 48 */     this.next = result;
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T next() {
/* 56 */     if (false == hasNext()) {
/* 57 */       throw new NoSuchElementException("No more lines");
/*    */     }
/*    */     
/* 60 */     T result = this.next;
/*    */     
/* 62 */     this.next = null;
/* 63 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void finish() {
/* 70 */     this.finished = true;
/* 71 */     this.next = null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\ComputeIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */