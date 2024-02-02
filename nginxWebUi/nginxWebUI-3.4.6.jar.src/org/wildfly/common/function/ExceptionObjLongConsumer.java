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
/*    */ public interface ExceptionObjLongConsumer<T, E extends Exception>
/*    */ {
/*    */   default ExceptionObjLongConsumer<T, E> andThen(ExceptionObjLongConsumer<? super T, ? extends E> after) {
/* 40 */     Assert.checkNotNullParam("after", after);
/* 41 */     return (t, v) -> {
/*    */         accept((T)t, v);
/*    */         after.accept(t, v);
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionObjLongConsumer<T, E> compose(ExceptionObjLongConsumer<? super T, ? extends E> before) {
/* 48 */     Assert.checkNotNullParam("before", before);
/* 49 */     return (t, v) -> {
/*    */         before.accept(t, v);
/*    */         accept((T)t, v);
/*    */       };
/*    */   }
/*    */   
/*    */   void accept(T paramT, long paramLong) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionObjLongConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */