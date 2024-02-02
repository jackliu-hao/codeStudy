package cn.hutool.bloomfilter;

public class BloomFilterUtil {
   public static BitSetBloomFilter createBitSet(int c, int n, int k) {
      return new BitSetBloomFilter(c, n, k);
   }

   public static BitMapBloomFilter createBitMap(int m) {
      return new BitMapBloomFilter(m);
   }
}
