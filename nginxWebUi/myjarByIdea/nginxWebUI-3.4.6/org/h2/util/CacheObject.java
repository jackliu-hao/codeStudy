package org.h2.util;

import org.h2.message.DbException;

public abstract class CacheObject implements Comparable<CacheObject> {
   public CacheObject cachePrevious;
   public CacheObject cacheNext;
   public CacheObject cacheChained;
   private int pos;
   private boolean changed;

   public abstract boolean canRemove();

   public abstract int getMemory();

   public void setPos(int var1) {
      if (this.cachePrevious == null && this.cacheNext == null && this.cacheChained == null) {
         this.pos = var1;
      } else {
         throw DbException.getInternalError("setPos too late");
      }
   }

   public int getPos() {
      return this.pos;
   }

   public boolean isChanged() {
      return this.changed;
   }

   public void setChanged(boolean var1) {
      this.changed = var1;
   }

   public int compareTo(CacheObject var1) {
      return Integer.compare(this.getPos(), var1.getPos());
   }

   public boolean isStream() {
      return false;
   }
}
