package org.h2.table;

import java.util.LinkedHashSet;
import java.util.Set;
import org.h2.index.Index;

public final class IndexHints {
   private final LinkedHashSet<String> allowedIndexes;

   private IndexHints(LinkedHashSet<String> var1) {
      this.allowedIndexes = var1;
   }

   public static IndexHints createUseIndexHints(LinkedHashSet<String> var0) {
      return new IndexHints(var0);
   }

   public Set<String> getAllowedIndexes() {
      return this.allowedIndexes;
   }

   public String toString() {
      return "IndexHints{allowedIndexes=" + this.allowedIndexes + '}';
   }

   public boolean allowIndex(Index var1) {
      return this.allowedIndexes.contains(var1.getName());
   }
}
