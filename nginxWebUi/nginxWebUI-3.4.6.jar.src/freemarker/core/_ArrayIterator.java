/*    */ package freemarker.core;
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
/*    */ public class _ArrayIterator
/*    */   implements Iterator
/*    */ {
/*    */   private final Object[] array;
/*    */   private int nextIndex;
/*    */   
/*    */   public _ArrayIterator(Object[] array) {
/* 32 */     this.array = array;
/* 33 */     this.nextIndex = 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 38 */     return (this.nextIndex < this.array.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object next() {
/* 43 */     if (this.nextIndex >= this.array.length) {
/* 44 */       throw new NoSuchElementException();
/*    */     }
/* 46 */     return this.array[this.nextIndex++];
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 51 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_ArrayIterator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */