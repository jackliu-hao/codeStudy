package cn.hutool.core.lang.hash;

import cn.hutool.core.util.ByteUtil;
import java.util.Arrays;

public class CityHash {
   private static final long k0 = -4348849565147123417L;
   private static final long k1 = -5435081209227447693L;
   private static final long k2 = -7286425919675154353L;
   private static final long kMul = -7070675565921424023L;
   private static final int c1 = -862048943;
   private static final int c2 = 461845907;

   public static int hash32(byte[] data) {
      int len = data.length;
      if (len <= 24) {
         return len <= 12 ? (len <= 4 ? hash32Len0to4(data) : hash32Len5to12(data)) : hash32Len13to24(data);
      } else {
         int g = -862048943 * len;
         int f = g;
         int a0 = rotate32(fetch32(data, len - 4) * -862048943, 17) * 461845907;
         int a1 = rotate32(fetch32(data, len - 8) * -862048943, 17) * 461845907;
         int a2 = rotate32(fetch32(data, len - 16) * -862048943, 17) * 461845907;
         int a3 = rotate32(fetch32(data, len - 12) * -862048943, 17) * 461845907;
         int a4 = rotate32(fetch32(data, len - 20) * -862048943, 17) * 461845907;
         int h = len ^ a0;
         h = rotate32(h, 19);
         h = h * 5 + -430675100;
         h ^= a2;
         h = rotate32(h, 19);
         h = h * 5 + -430675100;
         g ^= a1;
         g = rotate32(g, 19);
         g = g * 5 + -430675100;
         g ^= a3;
         g = rotate32(g, 19);
         g = g * 5 + -430675100;
         f += a4;
         f = rotate32(f, 19);
         f = f * 5 + -430675100;
         int iters = (len - 1) / 20;
         int pos = 0;

         int swapValue;
         do {
            a0 = rotate32(fetch32(data, pos) * -862048943, 17) * 461845907;
            a1 = fetch32(data, pos + 4);
            a2 = rotate32(fetch32(data, pos + 8) * -862048943, 17) * 461845907;
            a3 = rotate32(fetch32(data, pos + 12) * -862048943, 17) * 461845907;
            a4 = fetch32(data, pos + 16);
            h ^= a0;
            h = rotate32(h, 18);
            h = h * 5 + -430675100;
            f += a1;
            f = rotate32(f, 19);
            f *= -862048943;
            g += a2;
            g = rotate32(g, 18);
            g = g * 5 + -430675100;
            h ^= a3 + a1;
            h = rotate32(h, 19);
            h = h * 5 + -430675100;
            g ^= a4;
            g = Integer.reverseBytes(g) * 5;
            h += a4 * 5;
            h = Integer.reverseBytes(h);
            f += a0;
            swapValue = f;
            f = g;
            g = h;
            h = swapValue;
            pos += 20;
            --iters;
         } while(iters != 0);

         g = rotate32(g, 11) * -862048943;
         g = rotate32(g, 17) * -862048943;
         f = rotate32(f, 11) * -862048943;
         f = rotate32(f, 17) * -862048943;
         h = rotate32(swapValue + g, 19);
         h = h * 5 + -430675100;
         h = rotate32(h, 17) * -862048943;
         h = rotate32(h + f, 19);
         h = h * 5 + -430675100;
         h = rotate32(h, 17) * -862048943;
         return h;
      }
   }

   public static long hash64(byte[] data) {
      int len = data.length;
      if (len <= 32) {
         return len <= 16 ? hashLen0to16(data) : hashLen17to32(data);
      } else if (len <= 64) {
         return hashLen33to64(data);
      } else {
         long x = fetch64(data, len - 40);
         long y = fetch64(data, len - 16) + fetch64(data, len - 56);
         long z = hashLen16(fetch64(data, len - 48) + (long)len, fetch64(data, len - 24));
         Number128 v = weakHashLen32WithSeeds(data, len - 64, (long)len, z);
         Number128 w = weakHashLen32WithSeeds(data, len - 32, y + -5435081209227447693L, x);
         x = x * -5435081209227447693L + fetch64(data, 0);
         len = len - 1 & -64;
         int pos = 0;

         long swapValue;
         do {
            x = rotate64(x + y + v.getLowValue() + fetch64(data, pos + 8), 37) * -5435081209227447693L;
            y = rotate64(y + v.getHighValue() + fetch64(data, pos + 48), 42) * -5435081209227447693L;
            x ^= w.getHighValue();
            y += v.getLowValue() + fetch64(data, pos + 40);
            z = rotate64(z + w.getLowValue(), 33) * -5435081209227447693L;
            v = weakHashLen32WithSeeds(data, pos, v.getHighValue() * -5435081209227447693L, x + w.getLowValue());
            w = weakHashLen32WithSeeds(data, pos + 32, z + w.getHighValue(), y + fetch64(data, pos + 16));
            swapValue = x;
            x = z;
            z = swapValue;
            pos += 64;
            len -= 64;
         } while(len != 0);

         return hashLen16(hashLen16(v.getLowValue(), w.getLowValue()) + shiftMix(y) * -5435081209227447693L + swapValue, hashLen16(v.getHighValue(), w.getHighValue()) + x);
      }
   }

   public static long hash64(byte[] data, long seed0, long seed1) {
      return hashLen16(hash64(data) - seed0, seed1);
   }

   public static long hash64(byte[] data, long seed) {
      return hash64(data, -7286425919675154353L, seed);
   }

   public static Number128 hash128(byte[] data) {
      int len = data.length;
      return len >= 16 ? hash128(data, 16, new Number128(fetch64(data, 0), fetch64(data, 8) + -4348849565147123417L)) : hash128(data, 0, new Number128(-4348849565147123417L, -5435081209227447693L));
   }

   public static Number128 hash128(byte[] data, Number128 seed) {
      return hash128(data, 0, seed);
   }

   private static Number128 hash128(byte[] byteArray, int start, Number128 seed) {
      int len = byteArray.length - start;
      if (len < 128) {
         return cityMurmur(Arrays.copyOfRange(byteArray, start, byteArray.length), seed);
      } else {
         Number128 v = new Number128(0L, 0L);
         Number128 w = new Number128(0L, 0L);
         long x = seed.getLowValue();
         long y = seed.getHighValue();
         long z = (long)len * -5435081209227447693L;
         v.setLowValue(rotate64(y ^ -5435081209227447693L, 49) * -5435081209227447693L + fetch64(byteArray, start));
         v.setHighValue(rotate64(v.getLowValue(), 42) * -5435081209227447693L + fetch64(byteArray, start + 8));
         w.setLowValue(rotate64(y + z, 35) * -5435081209227447693L + x);
         w.setHighValue(rotate64(x + fetch64(byteArray, start + 88), 53) * -5435081209227447693L);
         int pos = start;

         long swapValue;
         do {
            x = rotate64(x + y + v.getLowValue() + fetch64(byteArray, pos + 8), 37) * -5435081209227447693L;
            y = rotate64(y + v.getHighValue() + fetch64(byteArray, pos + 48), 42) * -5435081209227447693L;
            x ^= w.getHighValue();
            y += v.getLowValue() + fetch64(byteArray, pos + 40);
            z = rotate64(z + w.getLowValue(), 33) * -5435081209227447693L;
            v = weakHashLen32WithSeeds(byteArray, pos, v.getHighValue() * -5435081209227447693L, x + w.getLowValue());
            w = weakHashLen32WithSeeds(byteArray, pos + 32, z + w.getHighValue(), y + fetch64(byteArray, pos + 16));
            swapValue = x;
            pos += 64;
            x = rotate64(z + y + v.getLowValue() + fetch64(byteArray, pos + 8), 37) * -5435081209227447693L;
            y = rotate64(y + v.getHighValue() + fetch64(byteArray, pos + 48), 42) * -5435081209227447693L;
            x ^= w.getHighValue();
            y += v.getLowValue() + fetch64(byteArray, pos + 40);
            z = rotate64(swapValue + w.getLowValue(), 33) * -5435081209227447693L;
            v = weakHashLen32WithSeeds(byteArray, pos, v.getHighValue() * -5435081209227447693L, x + w.getLowValue());
            w = weakHashLen32WithSeeds(byteArray, pos + 32, z + w.getHighValue(), y + fetch64(byteArray, pos + 16));
            swapValue = x;
            x = z;
            z = swapValue;
            pos += 64;
            len -= 128;
         } while(len >= 128);

         x += rotate64(v.getLowValue() + swapValue, 49) * -4348849565147123417L;
         y = y * -4348849565147123417L + rotate64(w.getHighValue(), 37);
         z = swapValue * -4348849565147123417L + rotate64(w.getLowValue(), 27);
         w.setLowValue(w.getLowValue() * 9L);
         v.setLowValue(v.getLowValue() * -4348849565147123417L);
         int tail_done = 0;

         while(tail_done < len) {
            tail_done += 32;
            y = rotate64(x + y, 42) * -4348849565147123417L + v.getHighValue();
            w.setLowValue(w.getLowValue() + fetch64(byteArray, pos + len - tail_done + 16));
            x = x * -4348849565147123417L + w.getLowValue();
            z += w.getHighValue() + fetch64(byteArray, pos + len - tail_done);
            w.setHighValue(w.getHighValue() + v.getLowValue());
            v = weakHashLen32WithSeeds(byteArray, pos + len - tail_done, v.getLowValue() + z, v.getHighValue());
            v.setLowValue(v.getLowValue() * -4348849565147123417L);
         }

         x = hashLen16(x, v.getLowValue());
         y = hashLen16(y + z, w.getLowValue());
         return new Number128(hashLen16(x + v.getHighValue(), w.getHighValue()) + y, hashLen16(x + w.getHighValue(), y + v.getHighValue()));
      }
   }

   private static int hash32Len0to4(byte[] byteArray) {
      int b = 0;
      int c = 9;
      int len = byteArray.length;
      byte[] var4 = byteArray;
      int var5 = byteArray.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int v = var4[var6];
         b = b * -862048943 + v;
         c ^= b;
      }

      return fmix(mur(b, mur(len, c)));
   }

   private static int hash32Len5to12(byte[] byteArray) {
      int len = byteArray.length;
      int b = len * 5;
      int c = 9;
      int d = b;
      int a = len + fetch32(byteArray, 0);
      b += fetch32(byteArray, len - 4);
      c += fetch32(byteArray, len >>> 1 & 4);
      return fmix(mur(c, mur(b, mur(a, d))));
   }

   private static int hash32Len13to24(byte[] byteArray) {
      int len = byteArray.length;
      int a = fetch32(byteArray, (len >>> 1) - 4);
      int b = fetch32(byteArray, 4);
      int c = fetch32(byteArray, len - 8);
      int d = fetch32(byteArray, len >>> 1);
      int e = fetch32(byteArray, 0);
      int f = fetch32(byteArray, len - 4);
      return fmix(mur(f, mur(e, mur(d, mur(c, mur(b, mur(a, len)))))));
   }

   private static long hashLen0to16(byte[] byteArray) {
      int len = byteArray.length;
      long mul;
      long a;
      if (len >= 8) {
         mul = -7286425919675154353L + (long)len * 2L;
         a = fetch64(byteArray, 0) + -7286425919675154353L;
         long b = fetch64(byteArray, len - 8);
         long c = rotate64(b, 37) * mul + a;
         long d = (rotate64(a, 25) + b) * mul;
         return hashLen16(c, d, mul);
      } else if (len >= 4) {
         mul = -7286425919675154353L + (long)(len * 2);
         a = (long)fetch32(byteArray, 0) & 4294967295L;
         return hashLen16((long)len + (a << 3), (long)fetch32(byteArray, len - 4) & 4294967295L, mul);
      } else if (len > 0) {
         int a = byteArray[0] & 255;
         int b = byteArray[len >>> 1] & 255;
         int c = byteArray[len - 1] & 255;
         int y = a + (b << 8);
         int z = len + (c << 2);
         return shiftMix((long)y * -7286425919675154353L ^ (long)z * -4348849565147123417L) * -7286425919675154353L;
      } else {
         return -7286425919675154353L;
      }
   }

   private static long hashLen17to32(byte[] byteArray) {
      int len = byteArray.length;
      long mul = -7286425919675154353L + (long)len * 2L;
      long a = fetch64(byteArray, 0) * -5435081209227447693L;
      long b = fetch64(byteArray, 8);
      long c = fetch64(byteArray, len - 8) * mul;
      long d = fetch64(byteArray, len - 16) * -7286425919675154353L;
      return hashLen16(rotate64(a + b, 43) + rotate64(c, 30) + d, a + rotate64(b + -7286425919675154353L, 18) + c, mul);
   }

   private static long hashLen33to64(byte[] byteArray) {
      int len = byteArray.length;
      long mul = -7286425919675154353L + (long)len * 2L;
      long a = fetch64(byteArray, 0) * -7286425919675154353L;
      long b = fetch64(byteArray, 8);
      long c = fetch64(byteArray, len - 24);
      long d = fetch64(byteArray, len - 32);
      long e = fetch64(byteArray, 16) * -7286425919675154353L;
      long f = fetch64(byteArray, 24) * 9L;
      long g = fetch64(byteArray, len - 8);
      long h = fetch64(byteArray, len - 16) * mul;
      long u = rotate64(a + g, 43) + (rotate64(b, 30) + c) * 9L;
      long v = (a + g ^ d) + f + 1L;
      long w = Long.reverseBytes((u + v) * mul) + h;
      long x = rotate64(e + f, 42) + c;
      long y = (Long.reverseBytes((v + w) * mul) + g) * mul;
      long z = e + f + c;
      a = Long.reverseBytes((x + z) * mul + y) + b;
      b = shiftMix((z + a) * mul + d + h) * mul;
      return b + x;
   }

   private static long fetch64(byte[] byteArray, int start) {
      return ByteUtil.bytesToLong(byteArray, start, ByteUtil.CPU_ENDIAN);
   }

   private static int fetch32(byte[] byteArray, int start) {
      return ByteUtil.bytesToInt(byteArray, start, ByteUtil.CPU_ENDIAN);
   }

   private static long rotate64(long val, int shift) {
      return shift == 0 ? val : val >>> shift | val << 64 - shift;
   }

   private static int rotate32(int val, int shift) {
      return shift == 0 ? val : val >>> shift | val << 32 - shift;
   }

   private static long hashLen16(long u, long v, long mul) {
      long a = (u ^ v) * mul;
      a ^= a >>> 47;
      long b = (v ^ a) * mul;
      b ^= b >>> 47;
      b *= mul;
      return b;
   }

   private static long hashLen16(long u, long v) {
      return hash128to64(new Number128(u, v));
   }

   private static long hash128to64(Number128 number128) {
      long a = (number128.getLowValue() ^ number128.getHighValue()) * -7070675565921424023L;
      a ^= a >>> 47;
      long b = (number128.getHighValue() ^ a) * -7070675565921424023L;
      b ^= b >>> 47;
      b *= -7070675565921424023L;
      return b;
   }

   private static long shiftMix(long val) {
      return val ^ val >>> 47;
   }

   private static int fmix(int h) {
      h ^= h >>> 16;
      h *= -2048144789;
      h ^= h >>> 13;
      h *= -1028477387;
      h ^= h >>> 16;
      return h;
   }

   private static int mur(int a, int h) {
      a *= -862048943;
      a = rotate32(a, 17);
      a *= 461845907;
      h ^= a;
      h = rotate32(h, 19);
      return h * 5 + -430675100;
   }

   private static Number128 weakHashLen32WithSeeds(long w, long x, long y, long z, long a, long b) {
      a += w;
      b = rotate64(b + a + z, 21);
      long c = a;
      a += x;
      a += y;
      b += rotate64(a, 44);
      return new Number128(a + z, b + c);
   }

   private static Number128 weakHashLen32WithSeeds(byte[] byteArray, int start, long a, long b) {
      return weakHashLen32WithSeeds(fetch64(byteArray, start), fetch64(byteArray, start + 8), fetch64(byteArray, start + 16), fetch64(byteArray, start + 24), a, b);
   }

   private static Number128 cityMurmur(byte[] byteArray, Number128 seed) {
      int len = byteArray.length;
      long a = seed.getLowValue();
      long b = seed.getHighValue();
      int l = len - 16;
      long c;
      long d;
      if (l <= 0) {
         a = shiftMix(a * -5435081209227447693L) * -5435081209227447693L;
         c = b * -5435081209227447693L + hashLen0to16(byteArray);
         d = shiftMix(a + (len >= 8 ? fetch64(byteArray, 0) : c));
      } else {
         c = hashLen16(fetch64(byteArray, len - 8) + -5435081209227447693L, a);
         d = hashLen16(b + (long)len, c + fetch64(byteArray, len - 16));
         a += d;
         int pos = 0;

         do {
            a ^= shiftMix(fetch64(byteArray, pos) * -5435081209227447693L) * -5435081209227447693L;
            a *= -5435081209227447693L;
            b ^= a;
            c ^= shiftMix(fetch64(byteArray, pos + 8) * -5435081209227447693L) * -5435081209227447693L;
            c *= -5435081209227447693L;
            d ^= c;
            pos += 16;
            l -= 16;
         } while(l > 0);
      }

      a = hashLen16(a, c);
      b = hashLen16(d, b);
      return new Number128(a ^ b, hashLen16(b, a));
   }
}
