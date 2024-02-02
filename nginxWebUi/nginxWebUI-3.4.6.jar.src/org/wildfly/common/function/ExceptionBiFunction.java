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
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface ExceptionBiFunction<T, U, R, E extends Exception>
/*    */ {
/*    */   default <R2> ExceptionBiFunction<T, U, R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
/* 41 */     Assert.checkNotNullParam("after", after);
/* 42 */     return (t, u) -> after.apply(apply((T)t, (U)u));
/*    */   }
/*    */   
/*    */   default ExceptionBiConsumer<T, U, E> andThen(ExceptionConsumer<R, ? extends E> after) {
/* 46 */     Assert.checkNotNullParam("after", after);
/* 47 */     return (t, u) -> after.accept(apply((T)t, (U)u));
/*    */   }
/*    */   
/*    */   default ExceptionSupplier<R, E> compose(ExceptionSupplier<? extends T, ? extends E> before1, ExceptionSupplier<? extends U, ? extends E> before2) {
/* 51 */     Assert.checkNotNullParam("before1", before1);
/* 52 */     Assert.checkNotNullParam("before2", before2);
/* 53 */     return () -> apply((T)before1.get(), (U)before2.get());
/*    */   }
/*    */   
/*    */   R apply(T paramT, U paramU) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionBiFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */