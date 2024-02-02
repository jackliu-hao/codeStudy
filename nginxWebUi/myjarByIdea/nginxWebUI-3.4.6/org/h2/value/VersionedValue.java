package org.h2.value;

public class VersionedValue<T> {
   protected VersionedValue() {
   }

   public boolean isCommitted() {
      return true;
   }

   public long getOperationId() {
      return 0L;
   }

   public T getCurrentValue() {
      return this;
   }

   public T getCommittedValue() {
      return this;
   }
}
