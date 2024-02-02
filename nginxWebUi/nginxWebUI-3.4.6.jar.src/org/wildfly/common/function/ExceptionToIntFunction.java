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
/*    */ public interface ExceptionToIntFunction<T, E extends Exception>
/*    */ {
/*    */   default <R> ExceptionFunction<T, R, E> andThen(ExceptionIntFunction<? extends R, ? extends E> after) {
/* 40 */     Assert.checkNotNullParam("after", after);
/* 41 */     return t -> after.apply(apply((T)t));
/*    */   }
/*    */   
/*    */   default <R> ExceptionFunction<T, R, E> andThen(ExceptionLongFunction<? extends R, ? extends E> after) {
/* 45 */     Assert.checkNotNullParam("after", after);
/* 46 */     return t -> after.apply(apply((T)t));
/*    */   }
/*    */   
/*    */   default <T2> ExceptionToIntFunction<T2, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
/* 50 */     Assert.checkNotNullParam("before", before);
/* 51 */     return t -> apply((T)before.apply(t));
/*    */   }
/*    */   
/*    */   int apply(T paramT) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionToIntFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */