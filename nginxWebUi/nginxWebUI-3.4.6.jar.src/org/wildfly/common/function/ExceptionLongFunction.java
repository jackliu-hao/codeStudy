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
/*    */ public interface ExceptionLongFunction<R, E extends Exception>
/*    */ {
/*    */   default <R2> ExceptionLongFunction<R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
/* 40 */     Assert.checkNotNullParam("after", after);
/* 41 */     return t -> after.apply(apply(t));
/*    */   }
/*    */   
/*    */   default <T> ExceptionFunction<T, R, E> compose(ExceptionToLongFunction<? super T, ? extends E> before) {
/* 45 */     Assert.checkNotNullParam("before", before);
/* 46 */     return t -> apply(before.apply(t));
/*    */   }
/*    */   
/*    */   R apply(long paramLong) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionLongFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */