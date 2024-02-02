package org.h2.mvstore.tx;

import org.h2.value.VersionedValue;

class VersionedValueUncommitted<T> extends VersionedValueCommitted<T> {
   private final long operationId;
   private final T committedValue;

   private VersionedValueUncommitted(long var1, T var3, T var4) {
      super(var3);

      assert var1 != 0L;

      this.operationId = var1;
      this.committedValue = var4;
   }

   static <X> VersionedValue<X> getInstance(long var0, X var2, X var3) {
      return new VersionedValueUncommitted(var0, var2, var3);
   }

   public boolean isCommitted() {
      return false;
   }

   public long getOperationId() {
      return this.operationId;
   }

   public T getCommittedValue() {
      return this.committedValue;
   }

   public String toString() {
      return super.toString() + " " + TransactionStore.getTransactionId(this.operationId) + "/" + TransactionStore.getLogId(this.operationId) + " " + this.committedValue;
   }
}
