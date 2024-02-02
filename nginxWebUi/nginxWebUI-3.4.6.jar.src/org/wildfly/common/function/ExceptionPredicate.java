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
/*    */ @FunctionalInterface
/*    */ public interface ExceptionPredicate<T, E extends Exception>
/*    */ {
/*    */   default ExceptionPredicate<T, E> and(ExceptionPredicate<T, E> other) {
/* 38 */     return t -> (test((T)t) && other.test(t));
/*    */   }
/*    */   
/*    */   default ExceptionPredicate<T, E> or(ExceptionPredicate<T, E> other) {
/* 42 */     return t -> (test((T)t) || other.test(t));
/*    */   }
/*    */   
/*    */   default ExceptionPredicate<T, E> xor(ExceptionPredicate<T, E> other) {
/* 46 */     return t -> (test((T)t) != other.test(t));
/*    */   }
/*    */   
/*    */   default ExceptionPredicate<T, E> not() {
/* 50 */     return t -> !test((T)t);
/*    */   }
/*    */   
/*    */   default <U> ExceptionBiPredicate<T, U, E> with(ExceptionPredicate<? super U, ? extends E> other) {
/* 54 */     return (t, u) -> (test((T)t) && other.test(u));
/*    */   }
/*    */   
/*    */   boolean test(T paramT) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */