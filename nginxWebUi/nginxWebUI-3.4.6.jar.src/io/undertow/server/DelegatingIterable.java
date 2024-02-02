/*    */ package io.undertow.server;
/*    */ 
/*    */ import java.util.Iterator;
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
/*    */ final class DelegatingIterable<E>
/*    */   implements Iterable<E>
/*    */ {
/*    */   private final Iterable<E> delegate;
/*    */   
/*    */   DelegatingIterable(Iterable<E> delegate) {
/* 30 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   Iterable<E> getDelegate() {
/* 34 */     return this.delegate;
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<E> iterator() {
/* 39 */     return new ReadOnlyIterator<>(this.delegate.iterator());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\DelegatingIterable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */