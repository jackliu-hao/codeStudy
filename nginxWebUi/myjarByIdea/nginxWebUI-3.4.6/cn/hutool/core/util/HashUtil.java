package cn.hutool.core.util;

import cn.hutool.core.lang.hash.CityHash;
import cn.hutool.core.lang.hash.MetroHash;
import cn.hutool.core.lang.hash.MurmurHash;
import cn.hutool.core.lang.hash.Number128;

public class HashUtil {
   public static int additiveHash(String key, int prime) {
      int hash = key.length();

      for(int i = 0; i < key.length(); ++i) {
         hash += key.charAt(i);
      }

      return hash % prime;
   }

   public static int rotatingHash(String key, int prime) {
      int hash = key.length();

      for(int i = 0; i < key.length(); ++i) {
         hash = hash << 4 ^ hash >> 28 ^ key.charAt(i);
      }

      return hash % prime;
   }

   public static int oneByOneHash(String key) {
      int hash = 0;

      for(int i = 0; i < key.length(); ++i) {
         hash += key.charAt(i);
         hash += hash << 10;
         hash ^= hash >> 6;
      }

      hash += hash << 3;
      hash ^= hash >> 11;
      hash += hash << 15;
      return hash;
   }

   public static int bernstein(String key) {
      int hash = 0;

      for(int i = 0; i < key.length(); ++i) {
         hash = 33 * hash + key.charAt(i);
      }

      return hash;
   }

   public static int universal(char[] key, int mask, int[] tab) {
      int hash = key.length;
      int len = key.length;

      for(int i = 0; i < len << 3; i += 8) {
         char k = key[i >> 3];
         if ((k & 1) == 0) {
            hash ^= tab[i];
         }

         if ((k & 2) == 0) {
            hash ^= tab[i + 1];
         }

         if ((k & 4) == 0) {
            hash ^= tab[i + 2];
         }

         if ((k & 8) == 0) {
            hash ^= tab[i + 3];
         }

         if ((k & 16) == 0) {
            hash ^= tab[i + 4];
         }

         if ((k & 32) == 0) {
            hash ^= tab[i + 5];
         }

         if ((k & 64) == 0) {
            hash ^= tab[i + 6];
         }

         if ((k & 128) == 0) {
            hash ^= tab[i + 7];
         }
      }

      return hash & mask;
   }

   public static int zobrist(char[] key, int mask, int[][] tab) {
      int hash = key.length;

      for(int i = 0; i < key.length; ++i) {
         hash ^= tab[i][key[i]];
      }

      return hash & mask;
   }

   public static int fnvHash(byte[] data) {
      int p = 16777619;
      int hash = -2128831035;
      byte[] var3 = data;
      int var4 = data.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         byte b = var3[var5];
         hash = (hash ^ b) * 16777619;
      }

      hash += hash << 13;
      hash ^= hash >> 7;
      hash += hash << 3;
      hash ^= hash >> 17;
      hash += hash << 5;
      return Math.abs(hash);
   }

   public static int fnvHash(String data) {
      int p = 16777619;
      int hash = -2128831035;

      for(int i = 0; i < data.length(); ++i) {
         hash = (hash ^ data.charAt(i)) * 16777619;
      }

      hash += hash << 13;
      hash ^= hash >> 7;
      hash += hash << 3;
      hash ^= hash >> 17;
      hash += hash << 5;
      return Math.abs(hash);
   }

   public static int intHash(int key) {
      key += ~(key << 15);
      key ^= key >>> 10;
      key += key << 3;
      key ^= key >>> 6;
      key += ~(key << 11);
      key ^= key >>> 16;
      return key;
   }

   public static int rsHash(String str) {
      int b = 378551;
      int a = 63689;
      int hash = 0;

      for(int i = 0; i < str.length(); ++i) {
         hash = hash * a + str.charAt(i);
         a *= b;
      }

      return hash & Integer.MAX_VALUE;
   }

   public static int jsHash(String str) {
      int hash = 1315423911;

      for(int i = 0; i < str.length(); ++i) {
         hash ^= (hash << 5) + str.charAt(i) + (hash >> 2);
      }

      return Math.abs(hash) & Integer.MAX_VALUE;
   }

   public static int pjwHash(String str) {
      int bitsInUnsignedInt = 32;
      int threeQuarters = bitsInUnsignedInt * 3 / 4;
      int oneEighth = bitsInUnsignedInt / 8;
      int highBits = -1 << bitsInUnsignedInt - oneEighth;
      int hash = 0;

      for(int i = 0; i < str.length(); ++i) {
         hash = (hash << oneEighth) + str.charAt(i);
         int test;
         if ((test = hash & highBits) != 0) {
            hash = (hash ^ test >> threeQuarters) & ~highBits;
         }
      }

      return hash & Integer.MAX_VALUE;
   }

   public static int elfHash(String str) {
      int hash = 0;

      for(int i = 0; i < str.length(); ++i) {
         hash = (hash << 4) + str.charAt(i);
         int x;
         if ((x = (int)((long)hash & 4026531840L)) != 0) {
            hash ^= x >> 24;
            hash &= ~x;
         }
      }

      return hash & Integer.MAX_VALUE;
   }

   public static int bkdrHash(String str) {
      int seed = 131;
      int hash = 0;

      for(int i = 0; i < str.length(); ++i) {
         hash = hash * seed + str.charAt(i);
      }

      return hash & Integer.MAX_VALUE;
   }

   public static int sdbmHash(String str) {
      int hash = 0;

      for(int i = 0; i < str.length(); ++i) {
         hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
      }

      return hash & Integer.MAX_VALUE;
   }

   public static int djbHash(String str) {
      int hash = 5381;

      for(int i = 0; i < str.length(); ++i) {
         hash = (hash << 5) + hash + str.charAt(i);
      }

      return hash & Integer.MAX_VALUE;
   }

   public static int dekHash(String str) {
      int hash = str.length();

      for(int i = 0; i < str.length(); ++i) {
         hash = hash << 5 ^ hash >> 27 ^ str.charAt(i);
      }

      return hash & Integer.MAX_VALUE;
   }

   public static int apHash(String str) {
      int hash = 0;

      for(int i = 0; i < str.length(); ++i) {
         hash ^= (i & 1) == 0 ? hash << 7 ^ str.charAt(i) ^ hash >> 3 : ~(hash << 11 ^ str.charAt(i) ^ hash >> 5);
      }

      return hash;
   }

   public static long tianlHash(String str) {
      int iLength = str.length();
      if (iLength == 0) {
         return 0L;
      } else {
         long hash;
         if (iLength <= 256) {
            hash = 16777216L * (long)(iLength - 1);
         } else {
            hash = 4278190080L;
         }

         int i;
         char ucChar;
         if (iLength <= 96) {
            for(i = 1; i <= iLength; ++i) {
               ucChar = str.charAt(i - 1);
               if (ucChar <= 'Z' && ucChar >= 'A') {
                  ucChar = (char)(ucChar + 32);
               }

               hash += (3L * (long)i * (long)ucChar * (long)ucChar + 5L * (long)i * (long)ucChar + 7L * (long)i + (long)(11 * ucChar)) % 16777216L;
            }
         } else {
            for(i = 1; i <= 96; ++i) {
               ucChar = str.charAt(i + iLength - 96 - 1);
               if (ucChar <= 'Z' && ucChar >= 'A') {
                  ucChar = (char)(ucChar + 32);
               }

               hash += (3L * (long)i * (long)ucChar * (long)ucChar + 5L * (long)i * (long)ucChar + 7L * (long)i + (long)(11 * ucChar)) % 16777216L;
            }
         }

         if (hash < 0L) {
            hash *= -1L;
         }

         return hash;
      }
   }

   public static int javaDefaultHash(String str) {
      int h = 0;
      int off = 0;
      int len = str.length();

      for(int i = 0; i < len; ++i) {
         h = 31 * h + str.charAt(off++);
      }

      return h;
   }

   public static long mixHash(String str) {
      long hash = (long)str.hashCode();
      hash <<= 32;
      hash |= (long)fnvHash(str);
      return hash;
   }

   public static int identityHashCode(Object obj) {
      return System.identityHashCode(obj);
   }

   public static int murmur32(byte[] data) {
      return MurmurHash.hash32(data);
   }

   public static long murmur64(byte[] data) {
      return MurmurHash.hash64(data);
   }

   public static long[] murmur128(byte[] data) {
      return MurmurHash.hash128(data);
   }

   public static int cityHash32(byte[] data) {
      return CityHash.hash32(data);
   }

   public static long cityHash64(byte[] data, long seed) {
      return CityHash.hash64(data, seed);
   }

   public static long cityHash64(byte[] data, long seed0, long seed1) {
      return CityHash.hash64(data, seed0, seed1);
   }

   public static long cityHash64(byte[] data) {
      return CityHash.hash64(data);
   }

   public static long[] cityHash128(byte[] data) {
      return CityHash.hash128(data).getLongArray();
   }

   public static long[] cityHash128(byte[] data, Number128 seed) {
      return CityHash.hash128(data, seed).getLongArray();
   }

   public static long metroHash64(byte[] data, long seed) {
      return MetroHash.hash64(data, seed);
   }

   public static long metroHash64(byte[] data) {
      return MetroHash.hash64(data);
   }

   public static long[] metroHash128(byte[] data, long seed) {
      return MetroHash.hash128(data, seed).getLongArray();
   }

   public static long[] metroHash128(byte[] data) {
      return MetroHash.hash128(data).getLongArray();
   }

   public static long hfHash(String data) {
      int length = data.length();
      long hash = 0L;

      for(int i = 0; i < length; ++i) {
         hash += (long)data.charAt(i) * 3L * (long)i;
      }

      if (hash < 0L) {
         hash = -hash;
      }

      return hash;
   }

   public static long hfIpHash(String data) {
      int length = data.length();
      long hash = 0L;

      for(int i = 0; i < length; ++i) {
         hash += (long)(data.charAt(i % 4) ^ data.charAt(i));
      }

      return hash;
   }
}
