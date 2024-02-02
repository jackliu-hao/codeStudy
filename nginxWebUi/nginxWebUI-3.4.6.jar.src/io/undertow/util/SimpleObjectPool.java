/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.util.concurrent.LinkedBlockingDeque;
/*    */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleObjectPool<T>
/*    */   implements ObjectPool
/*    */ {
/*    */   private final Supplier<T> supplier;
/*    */   private final Consumer<T> recycler;
/*    */   private final Consumer<T> consumer;
/*    */   private final LinkedBlockingDeque<T> pool;
/*    */   
/*    */   public SimpleObjectPool(int poolSize, Supplier<T> supplier, Consumer<T> recycler, Consumer<T> consumer) {
/* 43 */     this.supplier = supplier;
/* 44 */     this.recycler = recycler;
/* 45 */     this.consumer = consumer;
/* 46 */     this.pool = new LinkedBlockingDeque<>(poolSize);
/*    */   }
/*    */   
/*    */   public SimpleObjectPool(int poolSize, Supplier<T> supplier, Consumer<T> consumer) {
/* 50 */     this(poolSize, supplier, object -> {  }consumer);
/*    */   }
/*    */ 
/*    */   
/*    */   public PooledObject<T> allocate() {
/* 55 */     T obj = this.pool.poll();
/* 56 */     if (obj == null) {
/* 57 */       obj = this.supplier.get();
/*    */     }
/* 59 */     return new SimplePooledObject<>(obj, this);
/*    */   }
/*    */   
/*    */   private static final class SimplePooledObject<T>
/*    */     implements PooledObject<T>
/*    */   {
/* 65 */     private static final AtomicIntegerFieldUpdater<SimplePooledObject> closedUpdater = AtomicIntegerFieldUpdater.newUpdater(SimplePooledObject.class, "closed");
/*    */     private volatile int closed;
/*    */     private final T object;
/*    */     private final SimpleObjectPool<T> objectPool;
/*    */     
/*    */     SimplePooledObject(T object, SimpleObjectPool<T> objectPool) {
/* 71 */       this.object = object;
/* 72 */       this.objectPool = objectPool;
/*    */     }
/*    */ 
/*    */     
/*    */     public T getObject() {
/* 77 */       if (closedUpdater.get(this) != 0) {
/* 78 */         throw UndertowMessages.MESSAGES.objectIsClosed();
/*    */       }
/* 80 */       return this.object;
/*    */     }
/*    */ 
/*    */     
/*    */     public void close() {
/* 85 */       if (closedUpdater.compareAndSet(this, 0, 1)) {
/* 86 */         this.objectPool.recycler.accept(this.object);
/* 87 */         if (!this.objectPool.pool.offer(this.object))
/* 88 */           this.objectPool.consumer.accept(this.object); 
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\SimpleObjectPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */