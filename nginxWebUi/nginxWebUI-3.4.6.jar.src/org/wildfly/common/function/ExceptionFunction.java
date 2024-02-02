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
/*    */ @FunctionalInterface
/*    */ public interface ExceptionFunction<T, R, E extends Exception>
/*    */ {
/*    */   default <R2> ExceptionFunction<T, R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
/* 40 */     Assert.checkNotNullParam("after", after);
/* 41 */     return t -> after.apply(apply((T)t));
/*    */   }
/*    */   
/*    */   default <R2> ExceptionFunction<T, R2, E> andThen(ExceptionBiFunction<? super T, ? super R, ? extends R2, ? extends E> after) {
/* 45 */     Assert.checkNotNullParam("after", after);
/* 46 */     return t -> after.apply(t, apply((T)t));
/*    */   }
/*    */   
/*    */   default <T2> ExceptionFunction<T2, R, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
/* 50 */     Assert.checkNotNullParam("before", before);
/* 51 */     return t -> apply((T)before.apply(t));
/*    */   }
/*    */   
/*    */   default ExceptionConsumer<T, E> andThen(ExceptionConsumer<? super R, ? extends E> after) {
/* 55 */     Assert.checkNotNullParam("after", after);
/* 56 */     return t -> after.accept(apply((T)t));
/*    */   }
/*    */   
/*    */   default ExceptionConsumer<T, E> andThen(ExceptionBiConsumer<? super T, ? super R, ? extends E> after) {
/* 60 */     Assert.checkNotNullParam("after", after);
/* 61 */     return t -> after.accept(t, apply((T)t));
/*    */   }
/*    */   
/*    */   default ExceptionPredicate<T, E> andThen(ExceptionPredicate<? super R, ? extends E> after) {
/* 65 */     Assert.checkNotNullParam("after", after);
/* 66 */     return t -> after.test(apply((T)t));
/*    */   }
/*    */   
/*    */   default ExceptionPredicate<T, E> andThen(ExceptionBiPredicate<? super T, ? super R, ? extends E> after) {
/* 70 */     Assert.checkNotNullParam("after", after);
/* 71 */     return t -> after.test(t, apply((T)t));
/*    */   }
/*    */   
/*    */   default ExceptionSupplier<R, E> compose(ExceptionSupplier<? extends T, ? extends E> before) {
/* 75 */     Assert.checkNotNullParam("before", before);
/* 76 */     return () -> apply((T)before.get());
/*    */   }
/*    */   
/*    */   R apply(T paramT) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */