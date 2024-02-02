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
/*    */ public interface ExceptionToLongFunction<T, E extends Exception>
/*    */ {
/*    */   default <R> ExceptionFunction<T, R, E> andThen(ExceptionLongFunction<R, E> after) {
/* 40 */     Assert.checkNotNullParam("after", after);
/* 41 */     return t -> after.apply(apply((T)t));
/*    */   }
/*    */   
/*    */   default <T2> ExceptionToLongFunction<T2, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
/* 45 */     Assert.checkNotNullParam("before", before);
/* 46 */     return t -> apply((T)before.apply(t));
/*    */   }
/*    */   
/*    */   long apply(T paramT) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionToLongFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */