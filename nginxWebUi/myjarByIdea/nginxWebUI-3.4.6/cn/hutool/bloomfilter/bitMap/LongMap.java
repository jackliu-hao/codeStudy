package cn.hutool.bloomfilter.bitMap;

import java.io.Serializable;

public class LongMap implements BitMap, Serializable {
   private static final long serialVersionUID = 1L;
   private final long[] longs;

   public LongMap() {
      this.longs = new long[93750000];
   }

   public LongMap(int size) {
      this.longs = new long[size];
   }

   public void add(long i) {
      int r = (int)(i / 64L);
      long c = i & 63L;
      this.longs[r] |= 1L << (int)c;
   }

   public boolean contains(long i) {
      int r = (int)(i / 64L);
      long c = i & 63L;
      return (this.longs[r] >>> (int)c & 1L) == 1L;
   }

   public void remove(long i) {
      int r = (int)(i / 64L);
      long c = i & 63L;
      long[] var10000 = this.longs;
      var10000[r] &= ~(1L << (int)c);
   }
}
