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
/*    */ public interface ExceptionObjIntConsumer<T, E extends Exception>
/*    */ {
/*    */   default ExceptionObjIntConsumer<T, E> andThen(ExceptionObjIntConsumer<? super T, ? extends E> after) {
/* 40 */     Assert.checkNotNullParam("after", after);
/* 41 */     return (t, v) -> {
/*    */         accept((T)t, v);
/*    */         after.accept(t, v);
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionObjIntConsumer<T, E> compose(ExceptionObjIntConsumer<? super T, ? extends E> before) {
/* 48 */     Assert.checkNotNullParam("before", before);
/* 49 */     return (t, v) -> {
/*    */         before.accept(t, v);
/*    */         accept((T)t, v);
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionObjIntConsumer<T, E> andThen(ExceptionObjLongConsumer<? super T, ? extends E> after) {
/* 56 */     Assert.checkNotNullParam("after", after);
/* 57 */     return (t, v) -> {
/*    */         accept((T)t, v);
/*    */         after.accept(t, v);
/*    */       };
/*    */   }
/*    */   
/*    */   default ExceptionObjIntConsumer<T, E> compose(ExceptionObjLongConsumer<? super T, ? extends E> before) {
/* 64 */     Assert.checkNotNullParam("before", before);
/* 65 */     return (t, v) -> {
/*    */         before.accept(t, v);
/*    */         accept((T)t, v);
/*    */       };
/*    */   }
/*    */   
/*    */   void accept(T paramT, int paramInt) throws E;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\function\ExceptionObjIntConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */