/*    */ package org.wildfly.common.function;
/*    */ 
/*    */ import org.wildfly.common.Assert;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface ExceptionConsumer<T, E extends Exception>
/*    */ {
/*    */   default ExceptionConsumer<T, E> andThen(ExceptionConsumer<? super T, ? extends E> after) {
/* 39 */     Assert.checkNotNullParam("after", after);
/* 40 */     return t -> {
/*    */         accept((T)t);
/*    */         after.accept(t);
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionConsumer<T, E> compose(ExceptionConsumer<? super T, ? extends E> before) {
/* 47 */     Assert.checkNotNullParam("before", before);
/* 48 */     return t -> {
/*    */         accept((T)t);
/*    */         before.accept(t);
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionRunnable<E> compose(ExceptionSupplier<? extends T, ? extends E> before) {
/* 55 */     Assert.checkNotNullParam("before", before);
/* 56 */     return () -> accept((T)before.get());
/*    */   }
/*    */   
/*    */   void accept(T paramT) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */