package io.undertow.util;

import io.undertow.UndertowMessages;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NewInstanceObjectPool<T> implements ObjectPool {
   private final Supplier<T> supplier;
   private final Consumer<T> consumer;

   public NewInstanceObjectPool(Supplier<T> supplier, Consumer<T> consumer) {
      this.supplier = supplier;
      this.consumer = consumer;
   }

   public PooledObject allocate() {
      final T obj = this.supplier.get();
      return new PooledObject() {
         private volatile boolean closed = false;

         public T getObject() {
            if (this.closed) {
               throw UndertowMessages.MESSAGES.objectIsClosed();
            } else {
               return obj;
            }
         }

         public void close() {
            this.closed = true;
            NewInstanceObjectPool.this.consumer.accept(obj);
         }
      };
   }
}
