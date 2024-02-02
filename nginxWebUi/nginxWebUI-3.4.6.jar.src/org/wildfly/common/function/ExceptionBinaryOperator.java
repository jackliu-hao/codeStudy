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
/*    */ @FunctionalInterface
/*    */ public interface ExceptionBinaryOperator<T, E extends Exception>
/*    */   extends ExceptionBiFunction<T, T, T, E>
/*    */ {
/*    */   default ExceptionBinaryOperator<T, E> andThen(ExceptionUnaryOperator<T, ? extends E> after) {
/* 32 */     Assert.checkNotNullParam("after", after);
/* 33 */     return (t, u) -> after.apply(apply((T)t, (T)u));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionBinaryOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */