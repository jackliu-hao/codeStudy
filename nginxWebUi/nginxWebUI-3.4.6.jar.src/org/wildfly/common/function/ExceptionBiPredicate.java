/*    */ package org.wildfly.common.function;
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
/*    */ public interface ExceptionBiPredicate<T, U, E extends Exception>
/*    */ {
/*    */   default ExceptionBiPredicate<T, U, E> and(ExceptionBiPredicate<T, U, E> other) {
/* 39 */     return (t, u) -> (test((T)t, (U)u) && other.test(t, u));
/*    */   }
/*    */   
/*    */   default ExceptionBiPredicate<T, U, E> or(ExceptionBiPredicate<T, U, E> other) {
/* 43 */     return (t, u) -> (test((T)t, (U)u) || other.test(t, u));
/*    */   }
/*    */   
/*    */   default ExceptionBiPredicate<T, U, E> xor(ExceptionBiPredicate<T, U, E> other) {
/* 47 */     return (t, u) -> (test((T)t, (U)u) != other.test(t, u));
/*    */   }
/*    */   
/*    */   default ExceptionBiPredicate<T, U, E> not() {
/* 51 */     return (t, u) -> !test((T)t, (U)u);
/*    */   }
/*    */   
/*    */   boolean test(T paramT, U paramU) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionBiPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */