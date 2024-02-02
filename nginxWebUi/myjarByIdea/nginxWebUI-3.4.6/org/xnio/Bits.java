package org.xnio;

public final class Bits {
   private Bits() {
   }

   public static int intBitMask(int low, int high) {
      assert low >= 0;

      assert low <= high;

      assert high < 32;

      return (high == 31 ? 0 : 1 << high + 1) - (1 << low);
   }

   public static long longBitMask(int low, int high) {
      assert low >= 0;

      assert low <= high;

      assert high < 64;

      return (high == 63 ? 0L : 1L << (int)((long)high + 1L)) - (1L << (int)((long)low));
   }

   public static boolean anyAreSet(int var, int flags) {
      return (var & flags) != 0;
   }

   public static boolean allAreSet(int var, int flags) {
      return (var & flags) == flags;
   }

   public static boolean anyAreClear(int var, int flags) {
      return (var & flags) != flags;
   }

   public static boolean allAreClear(int var, int flags) {
      return (var & flags) == 0;
   }

   public static boolean anyAreSet(long var, long flags) {
      return (var & flags) != 0L;
   }

   public static boolean allAreSet(long var, long flags) {
      return (var & flags) == flags;
   }

   public static boolean anyAreClear(long var, long flags) {
      return (var & flags) != flags;
   }

   public static boolean allAreClear(long var, long flags) {
      return (var & flags) == 0L;
   }

   public static int unsigned(byte v) {
      return v & 255;
   }

   public static int unsigned(short v) {
      return v & '\uffff';
   }

   public static long unsigned(int v) {
      return (long)v & 4294967295L;
   }

   public static short shortFromBytesLE(byte[] b, int off) {
      return (short)(b[off + 1] << 8 | b[off] & 255);
   }

   public static short shortFromBytesBE(byte[] b, int off) {
      return (short)(b[off] << 8 | b[off + 1] & 255);
   }

   public static char charFromBytesLE(byte[] b, int off) {
      return (char)(b[off + 1] << 8 | b[off] & 255);
   }

   public static char charFromBytesBE(byte[] b, int off) {
      return (char)(b[off] << 8 | b[off + 1] & 255);
   }

   public static int mediumFromBytesLE(byte[] b, int off) {
      return (b[off + 2] & 255) << 16 | (b[off + 1] & 255) << 8 | b[off] & 255;
   }

   public static int mediumFromBytesBE(byte[] b, int off) {
      return (b[off] & 255) << 16 | (b[off + 1] & 255) << 8 | b[off + 2] & 255;
   }

   public static int intFromBytesLE(byte[] b, int off) {
      return b[off + 3] << 24 | (b[off + 2] & 255) << 16 | (b[off + 1] & 255) << 8 | b[off] & 255;
   }

   public static int intFromBytesBE(byte[] b, int off) {
      return b[off] << 24 | (b[off + 1] & 255) << 16 | (b[off + 2] & 255) << 8 | b[off + 3] & 255;
   }

   public static long longFromBytesLE(byte[] b, int off) {
      return ((long)b[off + 7] & 255L) << 56 | ((long)b[off + 6] & 255L) << 48 | ((long)b[off + 5] & 255L) << 40 | ((long)b[off + 4] & 255L) << 32 | ((long)b[off + 3] & 255L) << 24 | ((long)b[off + 2] & 255L) << 16 | ((long)b[off + 1] & 255L) << 8 | (long)b[off] & 255L;
   }

   public static long longFromBytesBE(byte[] b, int off) {
      return ((long)b[off] & 255L) << 56 | ((long)b[off + 1] & 255L) << 48 | ((long)b[off + 2] & 255L) << 40 | ((long)b[off + 3] & 255L) << 32 | ((long)b[off + 4] & 255L) << 24 | ((long)b[off + 5] & 255L) << 16 | ((long)b[off + 6] & 255L) << 8 | (long)b[off + 7] & 255L;
   }
}
