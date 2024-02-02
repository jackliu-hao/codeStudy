package cn.hutool.core.lang.hash;

import cn.hutool.core.util.ByteUtil;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MetroHash {
   private static final long k0_64 = -691005195L;
   private static final long k1_64 = -1565916357L;
   private static final long k2_64 = 1654206401L;
   private static final long k3_64 = 817650473L;
   private static final long k0_128 = -935685663L;
   private static final long k1_128 = -2042045477L;
   private static final long k2_128 = 2078195771L;
   private static final long k3_128 = 794325157L;

   public static long hash64(byte[] data) {
      return hash64(data, 1337L);
   }

   public static Number128 hash128(byte[] data) {
      return hash128(data, 1337L);
   }

   public static long hash64(byte[] data, long seed) {
      byte[] buffer = data;
      long hash = (seed + 1654206401L) * -691005195L;
      long v0 = hash;
      long v1 = hash;
      long v2 = hash;
      long v3 = hash;
      if (data.length >= 32) {
         while(true) {
            if (buffer.length < 32) {
               v2 ^= rotateLeft64((v0 + v3) * -691005195L + v1, -37) * -1565916357L;
               v3 ^= rotateLeft64((v1 + v2) * -1565916357L + v0, -37) * -691005195L;
               v0 ^= rotateLeft64((v0 + v2) * -691005195L + v3, -37) * -1565916357L;
               v1 ^= rotateLeft64((v1 + v3) * -1565916357L + v2, -37) * -691005195L;
               hash += v0 ^ v1;
               break;
            }

            v0 += littleEndian64(buffer, 0) * -691005195L;
            v0 = rotateLeft64(v0, -29) + v2;
            v1 += littleEndian64(buffer, 8) * -1565916357L;
            v1 = rotateLeft64(v1, -29) + v3;
            v2 += littleEndian64(buffer, 24) * 1654206401L;
            v2 = rotateLeft64(v2, -29) + v0;
            v3 += littleEndian64(buffer, 32) * 817650473L;
            v3 = rotateLeft64(v3, -29) + v1;
            buffer = Arrays.copyOfRange(buffer, 32, buffer.length);
         }
      }

      if (buffer.length >= 16) {
         v0 = hash + littleEndian64(buffer, 0) * 1654206401L;
         v0 = rotateLeft64(v0, -29) * 817650473L;
         v1 = hash + littleEndian64(buffer, 8) * 1654206401L;
         v1 = rotateLeft64(v1, -29) * 817650473L;
         v0 ^= rotateLeft64(v0 * -691005195L, -21) + v1;
         v1 ^= rotateLeft64(v1 * 817650473L, -21) + v0;
         hash += v1;
         buffer = Arrays.copyOfRange(buffer, 16, buffer.length);
      }

      if (buffer.length >= 8) {
         hash += littleEndian64(buffer, 0) * 817650473L;
         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
         hash ^= rotateLeft64(hash, -55) * -1565916357L;
      }

      if (buffer.length >= 4) {
         hash += (long)littleEndian32(Arrays.copyOfRange(buffer, 0, 4)) * 817650473L;
         hash ^= rotateLeft64(hash, -26) * -1565916357L;
         buffer = Arrays.copyOfRange(buffer, 4, buffer.length);
      }

      if (buffer.length >= 2) {
         hash += (long)littleEndian16(Arrays.copyOfRange(buffer, 0, 2)) * 817650473L;
         buffer = Arrays.copyOfRange(buffer, 2, buffer.length);
         hash ^= rotateLeft64(hash, -48) * -1565916357L;
      }

      if (buffer.length >= 1) {
         hash += (long)buffer[0] * 817650473L;
         hash ^= rotateLeft64(hash, -38) * -1565916357L;
      }

      hash ^= rotateLeft64(hash, -28);
      hash *= -691005195L;
      hash ^= rotateLeft64(hash, -29);
      return hash;
   }

   public static Number128 hash128(byte[] data, long seed) {
      byte[] buffer = data;
      long v0 = (seed - -935685663L) * 794325157L;
      long v1 = (seed + -2042045477L) * 2078195771L;
      if (data.length >= 32) {
         long v2 = (seed + -935685663L) * 2078195771L;

         long v3;
         for(v3 = (seed - -2042045477L) * 794325157L; buffer.length >= 32; v3 = rotateRight(v3, 29) + v1) {
            v0 += littleEndian64(buffer, 0) * -935685663L;
            buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
            v0 = rotateRight(v0, 29) + v2;
            v1 += littleEndian64(buffer, 0) * -2042045477L;
            buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
            v1 = rotateRight(v1, 29) + v3;
            v2 += littleEndian64(buffer, 0) * 2078195771L;
            buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
            v2 = rotateRight(v2, 29) + v0;
            v3 = littleEndian64(buffer, 0) * 794325157L;
            buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
         }

         v2 ^= rotateRight((v0 + v3) * -935685663L + v1, 21) * -2042045477L;
         v3 ^= rotateRight((v1 + v2) * -2042045477L + v0, 21) * -935685663L;
         v0 ^= rotateRight((v0 + v2) * -935685663L + v3, 21) * -2042045477L;
         v1 ^= rotateRight((v1 + v3) * -2042045477L + v2, 21) * -935685663L;
      }

      if (buffer.length >= 16) {
         v0 += littleEndian64(buffer, 0) * 2078195771L;
         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
         v0 = rotateRight(v0, 33) * 794325157L;
         v1 += littleEndian64(buffer, 0) * 2078195771L;
         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
         v1 = rotateRight(v1, 33) * 794325157L;
         v0 ^= rotateRight(v0 * 2078195771L + v1, 45) + -2042045477L;
         v1 ^= rotateRight(v1 * 794325157L + v0, 45) + -935685663L;
      }

      if (buffer.length >= 8) {
         v0 += littleEndian64(buffer, 0) * 2078195771L;
         buffer = Arrays.copyOfRange(buffer, 8, buffer.length);
         v0 = rotateRight(v0, 33) * 794325157L;
         v0 ^= rotateRight(v0 * 2078195771L + v1, 27) * -2042045477L;
      }

      if (buffer.length >= 4) {
         v1 += (long)littleEndian32(buffer) * 2078195771L;
         buffer = Arrays.copyOfRange(buffer, 4, buffer.length);
         v1 = rotateRight(v1, 33) * 794325157L;
         v1 ^= rotateRight(v1 * 794325157L + v0, 46) * -935685663L;
      }

      if (buffer.length >= 2) {
         v0 += (long)littleEndian16(buffer) * 2078195771L;
         buffer = Arrays.copyOfRange(buffer, 2, buffer.length);
         v0 = rotateRight(v0, 33) * 794325157L;
         v0 ^= rotateRight(v0 * 2078195771L * v1, 22) * -2042045477L;
      }

      if (buffer.length >= 1) {
         v1 += (long)buffer[0] * 2078195771L;
         v1 = rotateRight(v1, 33) * 794325157L;
         v1 ^= rotateRight(v1 * 794325157L + v0, 58) * -935685663L;
      }

      v0 += rotateRight(v0 * -935685663L + v1, 13);
      v1 += rotateRight(v1 * -2042045477L + v0, 37);
      v0 += rotateRight(v0 * 2078195771L + v1, 13);
      v1 += rotateRight(v1 * 794325157L + v0, 37);
      return new Number128(v0, v1);
   }

   private static long littleEndian64(byte[] b, int start) {
      return ByteUtil.bytesToLong(b, start, ByteOrder.LITTLE_ENDIAN);
   }

   private static int littleEndian32(byte[] b) {
      return b[0] | b[1] << 8 | b[2] << 16 | b[3] << 24;
   }

   private static int littleEndian16(byte[] b) {
      return ByteUtil.bytesToShort(b, ByteOrder.LITTLE_ENDIAN);
   }

   private static long rotateLeft64(long x, int k) {
      int n = 64;
      int s = k & n - 1;
      return x << s | x >> n - s;
   }

   private static long rotateRight(long val, int shift) {
      return val >> shift | val << 64 - shift;
   }
}
