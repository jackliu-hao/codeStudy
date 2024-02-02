package org.h2.mvstore;

import java.util.BitSet;
import org.h2.util.MathUtils;

public class FreeSpaceBitSet {
   private static final boolean DETAILED_INFO = false;
   private final int firstFreeBlock;
   private final int blockSize;
   private final BitSet set = new BitSet();
   private int failureFlags;

   public FreeSpaceBitSet(int var1, int var2) {
      this.firstFreeBlock = var1;
      this.blockSize = var2;
      this.clear();
   }

   public void clear() {
      this.set.clear();
      this.set.set(0, this.firstFreeBlock);
   }

   public boolean isUsed(long var1, int var3) {
      int var4 = this.getBlock(var1);
      int var5 = this.getBlockCount(var3);

      for(int var6 = var4; var6 < var4 + var5; ++var6) {
         if (!this.set.get(var6)) {
            return false;
         }
      }

      return true;
   }

   public boolean isFree(long var1, int var3) {
      int var4 = this.getBlock(var1);
      int var5 = this.getBlockCount(var3);

      for(int var6 = var4; var6 < var4 + var5; ++var6) {
         if (this.set.get(var6)) {
            return false;
         }
      }

      return true;
   }

   public long allocate(int var1) {
      return this.allocate(var1, 0L, 0L);
   }

   long allocate(int var1, long var2, long var4) {
      return this.getPos(this.allocate(this.getBlockCount(var1), (int)var2, (int)var4, true));
   }

   long predictAllocation(int var1, long var2, long var4) {
      return (long)this.allocate(var1, (int)var2, (int)var4, false);
   }

   boolean isFragmented() {
      return Integer.bitCount(this.failureFlags & 15) > 1;
   }

   private int allocate(int var1, int var2, int var3, boolean var4) {
      int var5 = 0;
      int var6 = 0;

      int var7;
      int var8;
      label47:
      while(true) {
         while(true) {
            var7 = this.set.nextClearBit(var6);
            var8 = this.set.nextSetBit(var7 + 1);
            int var9 = var8 - var7;
            if (var8 < 0 || var9 >= var1) {
               if (var3 >= 0 && var7 >= var3 || var7 + var1 <= var2) {
                  break label47;
               }

               if (var3 < 0) {
                  var7 = this.getAfterLastBlock();
                  var8 = -1;
                  break label47;
               }

               var6 = var3;
            } else {
               var5 += var9;
               var6 = var8;
            }
         }
      }

      assert this.set.nextSetBit(var7) == -1 || this.set.nextSetBit(var7) >= var7 + var1 : "Double alloc: " + Integer.toHexString(var7) + "/" + Integer.toHexString(var1) + " " + this;

      if (var4) {
         this.set.set(var7, var7 + var1);
      } else {
         this.failureFlags <<= 1;
         if (var8 < 0 && var5 > 4 * var1) {
            this.failureFlags |= 1;
         }
      }

      return var7;
   }

   public void markUsed(long var1, int var3) {
      int var4 = this.getBlock(var1);
      int var5 = this.getBlockCount(var3);
      if (this.set.nextSetBit(var4) != -1 && this.set.nextSetBit(var4) < var4 + var5) {
         throw DataUtils.newMVStoreException(6, "Double mark: " + Integer.toHexString(var4) + "/" + Integer.toHexString(var5) + " " + this);
      } else {
         this.set.set(var4, var4 + var5);
      }
   }

   public void free(long var1, int var3) {
      int var4 = this.getBlock(var1);
      int var5 = this.getBlockCount(var3);

      assert this.set.nextClearBit(var4) >= var4 + var5 : "Double free: " + Integer.toHexString(var4) + "/" + Integer.toHexString(var5) + " " + this;

      this.set.clear(var4, var4 + var5);
   }

   private long getPos(int var1) {
      return (long)var1 * (long)this.blockSize;
   }

   private int getBlock(long var1) {
      return (int)(var1 / (long)this.blockSize);
   }

   private int getBlockCount(int var1) {
      return MathUtils.roundUpInt(var1, this.blockSize) / this.blockSize;
   }

   int getFillRate() {
      return this.getProjectedFillRate(0);
   }

   int getProjectedFillRate(int var1) {
      int var4 = 3;

      int var2;
      int var3;
      do {
         --var4;
         if (var4 == 0) {
            return 100;
         }

         var3 = this.set.length();
         var2 = this.set.cardinality();
      } while(var3 != this.set.length() || var2 > var3);

      var2 -= this.firstFreeBlock + var1;
      var3 -= this.firstFreeBlock;
      return var2 == 0 ? 0 : (int)((100L * (long)var2 + (long)var3 - 1L) / (long)var3);
   }

   long getFirstFree() {
      return this.getPos(this.set.nextClearBit(0));
   }

   long getLastFree() {
      return this.getPos(this.getAfterLastBlock());
   }

   int getAfterLastBlock() {
      return this.set.previousSetBit(this.set.size() - 1) + 1;
   }

   int getMovePriority(int var1) {
      int var2 = this.set.previousClearBit(var1);
      int var3;
      if (var2 < 0) {
         var2 = this.firstFreeBlock;
         var3 = 0;
      } else {
         var3 = var2 - this.set.previousSetBit(var2);
      }

      int var4 = this.set.nextClearBit(var1);
      int var5 = this.set.nextSetBit(var4);
      if (var5 >= 0) {
         var3 += var5 - var4;
      }

      return (var4 - var2 - 1) * 1000 / (var3 + 1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('[');
      int var2 = 0;

      while(true) {
         if (var2 > 0) {
            var1.append(", ");
         }

         int var3 = this.set.nextClearBit(var2);
         var1.append(Integer.toHexString(var3)).append('-');
         int var4 = this.set.nextSetBit(var3 + 1);
         if (var4 < 0) {
            var1.append(']');
            return var1.toString();
         }

         var1.append(Integer.toHexString(var4 - 1));
         var2 = var4 + 1;
      }
   }
}
