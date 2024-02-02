package org.h2.mvstore.tx;

import org.h2.value.VersionedValue;

class VersionedValueCommitted<T> extends VersionedValue<T> {
   public final T value;

   VersionedValueCommitted(T var1) {
      this.value = var1;
   }

   static <X> VersionedValue<X> getInstance(X var0) {
      assert var0 != null;

      return (VersionedValue)(var0 instanceof VersionedValue ? (VersionedValue)var0 : new VersionedValueCommitted(var0));
   }

   public T getCurrentValue() {
      return this.value;
   }

   public T getCommittedValue() {
      return this.value;
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
