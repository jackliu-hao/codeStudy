package org.h2.mvstore.tx;

import java.util.BitSet;
import org.h2.mvstore.RootReference;

final class Snapshot<K, V> {
   final RootReference<K, V> root;
   final BitSet committingTransactions;

   Snapshot(RootReference<K, V> var1, BitSet var2) {
      this.root = var1;
      this.committingTransactions = var2;
   }

   public int hashCode() {
      int var2 = 1;
      var2 = 31 * var2 + this.committingTransactions.hashCode();
      var2 = 31 * var2 + this.root.hashCode();
      return var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Snapshot)) {
         return false;
      } else {
         Snapshot var2 = (Snapshot)var1;
         return this.committingTransactions == var2.committingTransactions && this.root == var2.root;
      }
   }
}
