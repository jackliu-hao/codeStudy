package io.undertow.util;

import io.undertow.UndertowMessages;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleObjectPool<T> implements ObjectPool {
   private final Supplier<T> supplier;
   private final Consumer<T> recycler;
   private final Consumer<T> consumer;
   private final LinkedBlockingDeque<T> pool;

   public SimpleObjectPool(int poolSize, Supplier<T> supplier, Consumer<T> recycler, Consumer<T> consumer) {
      this.supplier = supplier;
      this.recycler = recycler;
      this.consumer = consumer;
      this.pool = new LinkedBlockingDeque(poolSize);
   }

   public SimpleObjectPool(int poolSize, Supplier<T> supplier, Consumer<T> consumer) {
      this(poolSize, supplier, (object) -> {
      }, consumer);
   }

   public PooledObject<T> allocate() {
      T obj = this.pool.poll();
      if (obj == null) {
         obj = this.supplier.get();
      }

      return new SimplePooledObject(obj, this);
   }

   private static final class SimplePooledObject<T> implements PooledObject<T> {
      private static final AtomicIntegerFieldUpdater<SimplePooledObject> closedUpdater = AtomicIntegerFieldUpdater.newUpdater(SimplePooledObject.class, "closed");
      private volatile int closed;
      private final T object;
      private final SimpleObjectPool<T> objectPool;

      SimplePooledObject(T object, SimpleObjectPool<T> objectPool) {
         this.object = object;
         this.objectPool = objectPool;
      }

      public T getObject() {
         if (closedUpdater.get(this) != 0) {
            throw UndertowMessages.MESSAGES.objectIsClosed();
         } else {
            return this.object;
         }
      }

      public void close() {
         if (closedUpdater.compareAndSet(this, 0, 1)) {
            this.objectPool.recycler.accept(this.object);
            if (!this.objectPool.pool.offer(this.object)) {
               this.objectPool.consumer.accept(this.object);
            }
         }

      }
   }
}
