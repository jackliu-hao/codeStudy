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
/*    */ public interface ExceptionToIntBiFunction<T, U, E extends Exception>
/*    */ {
/*    */   default <R> ExceptionBiFunction<T, U, R, E> andThen(ExceptionIntFunction<R, E> after) {
/* 41 */     Assert.checkNotNullParam("after", after);
/* 42 */     return (t, u) -> after.apply(apply((T)t, (U)u));
/*    */   }
/*    */   
/*    */   default <R> ExceptionBiFunction<T, U, R, E> andThen(ExceptionLongFunction<R, E> after) {
/* 46 */     Assert.checkNotNullParam("after", after);
/* 47 */     return (t, u) -> after.apply(apply((T)t, (U)u));
/*    */   }
/*    */   
/*    */   int apply(T paramT, U paramU) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionToIntBiFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */