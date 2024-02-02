/*    */ package io.undertow.server;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.function.Consumer;
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
/*    */ final class ReadOnlyIterator<E>
/*    */   implements Iterator<E>
/*    */ {
/*    */   final Iterator<E> delegate;
/*    */   
/*    */   ReadOnlyIterator(Iterator<E> delegate) {
/* 31 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 36 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public void forEachRemaining(Consumer<? super E> action) {
/* 41 */     this.delegate.forEachRemaining(action);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 46 */     return this.delegate.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public E next() {
/* 51 */     return this.delegate.next();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ReadOnlyIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */