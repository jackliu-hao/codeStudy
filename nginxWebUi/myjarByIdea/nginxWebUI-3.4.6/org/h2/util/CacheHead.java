package org.h2.util;

public class CacheHead extends CacheObject {
   public boolean canRemove() {
      return false;
   }

   public int getMemory() {
      return 0;
   }
}
