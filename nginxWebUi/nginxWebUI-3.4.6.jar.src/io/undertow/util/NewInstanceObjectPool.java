/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.util.function.Consumer;
/*    */ import java.util.function.Supplier;
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
/*    */ public class NewInstanceObjectPool<T>
/*    */   implements ObjectPool
/*    */ {
/*    */   private final Supplier<T> supplier;
/*    */   private final Consumer<T> consumer;
/*    */   
/*    */   public NewInstanceObjectPool(Supplier<T> supplier, Consumer<T> consumer) {
/* 36 */     this.supplier = supplier;
/* 37 */     this.consumer = consumer;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public PooledObject allocate() {
/* 43 */     final T obj = this.supplier.get();
/* 44 */     return new PooledObject()
/*    */       {
/*    */         private volatile boolean closed = false;
/*    */ 
/*    */         
/*    */         public T getObject() {
/* 50 */           if (this.closed) {
/* 51 */             throw UndertowMessages.MESSAGES.objectIsClosed();
/*    */           }
/* 53 */           return (T)obj;
/*    */         }
/*    */ 
/*    */         
/*    */         public void close() {
/* 58 */           this.closed = true;
/* 59 */           NewInstanceObjectPool.this.consumer.accept(obj);
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\NewInstanceObjectPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */