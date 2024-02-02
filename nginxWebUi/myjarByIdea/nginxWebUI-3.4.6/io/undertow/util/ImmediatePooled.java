package io.undertow.util;

import org.xnio.Pooled;

public class ImmediatePooled<T> implements Pooled<T> {
   private final T value;

   public ImmediatePooled(T value) {
      this.value = value;
   }

   public void discard() {
   }

   public void free() {
   }

   public T getResource() throws IllegalStateException {
      return this.value;
   }

   public void close() {
   }
}
