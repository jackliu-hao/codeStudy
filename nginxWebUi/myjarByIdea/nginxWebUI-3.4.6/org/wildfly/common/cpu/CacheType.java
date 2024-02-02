package org.wildfly.common.cpu;

import java.util.EnumSet;

public enum CacheType {
   UNKNOWN(false, false),
   DATA(false, true),
   INSTRUCTION(true, false),
   UNIFIED(true, true);

   private static final int fullSize = values().length;
   private final boolean instruction;
   private final boolean data;

   private CacheType(boolean instruction, boolean data) {
      this.instruction = instruction;
      this.data = data;
   }

   public boolean isInstruction() {
      return this.instruction;
   }

   public boolean isData() {
      return this.data;
   }

   public static boolean isFull(EnumSet<CacheType> set) {
      return set != null && set.size() == fullSize;
   }

   public boolean in(CacheType v1) {
      return this == v1;
   }

   public boolean in(CacheType v1, CacheType v2) {
      return this == v1 || this == v2;
   }

   public boolean in(CacheType v1, CacheType v2, CacheType v3) {
      return this == v1 || this == v2 || this == v3;
   }

   public boolean in(CacheType... values) {
      if (values != null) {
         CacheType[] var2 = values;
         int var3 = values.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CacheType value = var2[var4];
            if (this == value) {
               return true;
            }
         }
      }

      return false;
   }
}
