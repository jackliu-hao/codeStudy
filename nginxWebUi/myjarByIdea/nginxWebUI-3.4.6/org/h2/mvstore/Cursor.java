package org.h2.mvstore;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Cursor<K, V> implements Iterator<K> {
   private final boolean reverse;
   private final K to;
   private CursorPos<K, V> cursorPos;
   private CursorPos<K, V> keeper;
   private K current;
   private K last;
   private V lastValue;
   private Page<K, V> lastPage;

   public Cursor(RootReference<K, V> var1, K var2, K var3) {
      this(var1, var2, var3, false);
   }

   public Cursor(RootReference<K, V> var1, K var2, K var3, boolean var4) {
      this.lastPage = var1.root;
      this.cursorPos = traverseDown(this.lastPage, var2, var4);
      this.to = var3;
      this.reverse = var4;
   }

   public boolean hasNext() {
      CursorPos var10000;
      if (this.cursorPos != null) {
         for(int var1 = this.reverse ? -1 : 1; this.current == null; var10000.index += var1) {
            label77: {
               Page var2;
               int var3;
               CursorPos var4;
               label78: {
                  var2 = this.cursorPos.page;
                  var3 = this.cursorPos.index;
                  if (this.reverse) {
                     if (var3 >= 0) {
                        break label78;
                     }
                  } else if (var3 < upperBound(var2)) {
                     break label78;
                  }

                  var4 = this.cursorPos;
                  this.cursorPos = this.cursorPos.parent;
                  if (this.cursorPos == null) {
                     return false;
                  }

                  var4.parent = this.keeper;
                  this.keeper = var4;
                  break label77;
               }

               while(!var2.isLeaf()) {
                  var2 = var2.getChildPage(var3);
                  var3 = this.reverse ? upperBound(var2) - 1 : 0;
                  if (this.keeper == null) {
                     this.cursorPos = new CursorPos(var2, var3, this.cursorPos);
                  } else {
                     var4 = this.keeper;
                     this.keeper = this.keeper.parent;
                     var4.parent = this.cursorPos;
                     var4.page = var2;
                     var4.index = var3;
                     this.cursorPos = var4;
                  }
               }

               if (this.reverse) {
                  if (var3 < 0) {
                     break label77;
                  }
               } else if (var3 >= var2.getKeyCount()) {
                  break label77;
               }

               Object var5 = var2.getKey(var3);
               if (this.to != null && Integer.signum(var2.map.getKeyType().compare(var5, this.to)) == var1) {
                  return false;
               }

               this.current = this.last = var5;
               this.lastValue = var2.getValue(var3);
               this.lastPage = var2;
            }

            var10000 = this.cursorPos;
         }
      }

      return this.current != null;
   }

   public K next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.current = null;
         return this.last;
      }
   }

   public K getKey() {
      return this.last;
   }

   public V getValue() {
      return this.lastValue;
   }

   Page<K, V> getPage() {
      return this.lastPage;
   }

   public void skip(long var1) {
      if (var1 < 10L) {
         while(var1-- > 0L && this.hasNext()) {
            this.next();
         }
      } else if (this.hasNext()) {
         assert this.cursorPos != null;

         CursorPos var3;
         CursorPos var4;
         for(var3 = this.cursorPos; (var4 = var3.parent) != null; var3 = var4) {
         }

         Page var5 = var3.page;
         MVMap var6 = var5.map;
         long var7 = var6.getKeyIndex(this.next());
         this.last = var6.getKey(var7 + (this.reverse ? -var1 : var1));
         this.cursorPos = traverseDown(var5, this.last, this.reverse);
      }

   }

   static <K, V> CursorPos<K, V> traverseDown(Page<K, V> var0, K var1, boolean var2) {
      CursorPos var3 = var1 != null ? CursorPos.traverseDown(var0, var1) : (var2 ? var0.getAppendCursorPos((CursorPos)null) : var0.getPrependCursorPos((CursorPos)null));
      int var4 = var3.index;
      if (var4 < 0) {
         var4 = ~var4;
         if (var2) {
            --var4;
         }

         var3.index = var4;
      }

      return var3;
   }

   private static <K, V> int upperBound(Page<K, V> var0) {
      return var0.isLeaf() ? var0.getKeyCount() : var0.map.getChildPageCount(var0);
   }
}
