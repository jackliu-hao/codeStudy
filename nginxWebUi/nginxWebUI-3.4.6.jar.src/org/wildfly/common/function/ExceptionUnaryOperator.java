/*    */ package org.wildfly.common.function;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ @FunctionalInterface
/*    */ public interface ExceptionUnaryOperator<T, E extends Exception>
/*    */   extends ExceptionFunction<T, T, E>
/*    */ {
/*    */   default ExceptionUnaryOperator<T, E> andThen(ExceptionUnaryOperator<T, ? extends E> after) {
/* 31 */     Assert.checkNotNullParam("after", after);
/* 32 */     return t -> after.apply(apply((T)t));
/*    */   }
/*    */   
/*    */   default ExceptionUnaryOperator<T, E> compose(ExceptionUnaryOperator<T, ? extends E> before) {
/* 36 */     Assert.checkNotNullParam("before", before);
/* 37 */     return t -> apply((T)before.apply(t));
/*    */   }
/*    */   
/*    */   static <T, E extends Exception> ExceptionUnaryOperator<T, E> of(ExceptionFunction<T, T, E> func) {
/* 41 */     Objects.requireNonNull(func); return (func instanceof ExceptionUnaryOperator) ? (ExceptionUnaryOperator)func : func::apply;
/*    */   }
/*    */   
/*    */   static <T, E extends Exception> ExceptionUnaryOperator<T, E> identity() {
/* 45 */     return t -> t;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionUnaryOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */