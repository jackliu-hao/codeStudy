package io.undertow.util;

import org.xnio.conduits.Conduit;

public class ImmediateConduitFactory<T extends Conduit> implements ConduitFactory<T> {
   private final T value;

   public ImmediateConduitFactory(T value) {
      this.value = value;
   }

   public T create() {
      return this.value;
   }
}
