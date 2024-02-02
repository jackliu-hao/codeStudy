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
/*    */ public interface ExceptionBiConsumer<T, U, E extends Exception>
/*    */ {
/*    */   default ExceptionBiConsumer<T, U, E> andThen(ExceptionBiConsumer<? super T, ? super U, ? extends E> after) {
/* 40 */     Assert.checkNotNullParam("after", after);
/* 41 */     return (t, u) -> {
/*    */         accept((T)t, (U)u);
/*    */         after.accept(t, u);
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionRunnable<E> compose(ExceptionSupplier<? extends T, ? extends E> before1, ExceptionSupplier<? extends U, ? extends E> before2) {
/* 48 */     Assert.checkNotNullParam("before1", before1);
/* 49 */     Assert.checkNotNullParam("before2", before2);
/* 50 */     return () -> accept((T)before1.get(), (U)before2.get());
/*    */   }
/*    */   
/*    */   void accept(T paramT, U paramU) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionBiConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */