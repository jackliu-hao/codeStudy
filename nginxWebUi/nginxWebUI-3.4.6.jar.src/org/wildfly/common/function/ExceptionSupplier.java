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
/*    */ public interface ExceptionSupplier<T, E extends Exception>
/*    */ {
/*    */   default ExceptionRunnable<E> andThen(ExceptionConsumer<? super T, ? extends E> after) {
/* 39 */     Assert.checkNotNullParam("after", after);
/* 40 */     return () -> after.accept(get());
/*    */   }
/*    */   
/*    */   default <R> ExceptionSupplier<R, E> andThen(ExceptionFunction<? super T, ? extends R, ? extends E> after) {
/* 44 */     Assert.checkNotNullParam("after", after);
/* 45 */     return () -> after.apply(get());
/*    */   }
/*    */   
/*    */   T get() throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionSupplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */