package org.wildfly.common.math;

import org.wildfly.common.Assert;

public final class HashMath {
   private static final int PRESELECTED_PRIME = 1299827;

   private HashMath() {
   }

   public static int roundToPowerOfTwo(int value) {
      Assert.checkMinimumParameter("value", 0, value);
      Assert.checkMaximumParameter("value", 1073741824, value);
      return value <= 1 ? value : Integer.highestOneBit(value - 1) << 1;
   }

   public static int multiHashOrdered(int accumulatedHash, int prime, int nextHash) {
      return multiplyWrap(accumulatedHash, prime) + nextHash;
   }

   public static int multiHashUnordered(int accumulatedHash, int prime, int nextHash) {
      return multiplyWrap(nextHash, prime) + accumulatedHash;
   }

   public static int multiHashOrdered(int accumulatedHash, int nextHash) {
      return multiHashOrdered(accumulatedHash, 1299827, nextHash);
   }

   public static int multiHashUnordered(int accumulatedHash, int nextHash) {
      return multiHashUnordered(accumulatedHash, 1299827, nextHash);
   }

   public static int multiplyWrap(int a, int b) {
      long r1 = (long)a * (long)b;
      return (int)r1 ^ (int)(r1 >>> 32);
   }
}
