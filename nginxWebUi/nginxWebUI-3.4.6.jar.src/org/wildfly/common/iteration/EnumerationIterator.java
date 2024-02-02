/*    */ package org.wildfly.common.iteration;
/*    */ 
/*    */ import java.util.Enumeration;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface EnumerationIterator<E>
/*    */   extends Enumeration<E>, Iterator<E>
/*    */ {
/*    */   default boolean hasMoreElements() {
/* 37 */     return hasNext();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default E nextElement() {
/* 46 */     return next();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static <E> EnumerationIterator<E> over(final E item) {
/* 57 */     return new EnumerationIterator<E>()
/*    */       {
/*    */         public boolean hasNext() {
/* 60 */           return !this.done;
/*    */         }
/*    */         boolean done;
/*    */         public E next() {
/* 64 */           if (!hasNext()) throw new NoSuchElementException(); 
/* 65 */           this.done = true;
/* 66 */           return (E)item;
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\EnumerationIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */