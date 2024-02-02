package org.h2.mvstore.db;

import org.h2.mvstore.MVStore;
import org.h2.mvstore.tx.Transaction;
import org.h2.store.InDoubtTransaction;

final class MVInDoubtTransaction implements InDoubtTransaction {
   private final MVStore store;
   private final Transaction transaction;
   private int state = 0;

   MVInDoubtTransaction(MVStore var1, Transaction var2) {
      this.store = var1;
      this.transaction = var2;
   }

   public void setState(int var1) {
      if (var1 == 1) {
         this.transaction.commit();
      } else {
         this.transaction.rollback();
      }

      this.store.commit();
      this.state = var1;
   }

   public int getState() {
      return this.state;
   }

   public String getTransactionName() {
      return this.transaction.getName();
   }
}
