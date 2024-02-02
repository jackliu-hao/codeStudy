package org.h2.mvstore;

public final class CursorPos<K, V> {
   public Page<K, V> page;
   public int index;
   public CursorPos<K, V> parent;

   public CursorPos(Page<K, V> var1, int var2, CursorPos<K, V> var3) {
      this.page = var1;
      this.index = var2;
      this.parent = var3;
   }

   static <K, V> CursorPos<K, V> traverseDown(Page<K, V> var0, K var1) {
      CursorPos var2;
      int var3;
      for(var2 = null; !var0.isLeaf(); var0 = var0.getChildPage(var3)) {
         var3 = var0.binarySearch(var1) + 1;
         if (var3 < 0) {
            var3 = -var3;
         }

         var2 = new CursorPos(var0, var3, var2);
      }

      return new CursorPos(var0, var0.binarySearch(var1), var2);
   }

   int processRemovalInfo(long var1) {
      int var3 = 0;

      for(CursorPos var4 = this; var4 != null; var4 = var4.parent) {
         var3 += var4.page.removePage(var1);
      }

      return var3;
   }

   public String toString() {
      return "CursorPos{page=" + this.page + ", index=" + this.index + ", parent=" + this.parent + '}';
   }
}
