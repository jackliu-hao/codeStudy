package cn.hutool.bloomfilter.bitMap;

import java.io.Serializable;

public class IntMap implements BitMap, Serializable {
   private static final long serialVersionUID = 1L;
   private final int[] ints;

   public IntMap() {
      this.ints = new int[93750000];
   }

   public IntMap(int size) {
      this.ints = new int[size];
   }

   public void add(long i) {
      int r = (int)(i / 32L);
      int c = (int)(i & 31L);
      this.ints[r] |= 1 << c;
   }

   public boolean contains(long i) {
      int r = (int)(i / 32L);
      int c = (int)(i & 31L);
      return (this.ints[r] >>> c & 1) == 1;
   }

   public void remove(long i) {
      int r = (int)(i / 32L);
      int c = (int)(i & 31L);
      int[] var10000 = this.ints;
      var10000[r] &= ~(1 << c);
   }
}
