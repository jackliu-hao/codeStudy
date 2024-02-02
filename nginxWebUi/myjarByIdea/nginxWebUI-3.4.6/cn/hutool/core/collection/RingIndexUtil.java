package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RingIndexUtil {
   public static int ringNextIntByObj(Object object, AtomicInteger atomicInteger) {
      Assert.notNull(object);
      int modulo = CollUtil.size(object);
      return ringNextInt(modulo, atomicInteger);
   }

   public static int ringNextInt(int modulo, AtomicInteger atomicInteger) {
      Assert.notNull(atomicInteger);
      Assert.isTrue(modulo > 0);
      if (modulo <= 1) {
         return 0;
      } else {
         int current;
         int next;
         do {
            current = atomicInteger.get();
            next = (current + 1) % modulo;
         } while(!atomicInteger.compareAndSet(current, next));

         return next;
      }
   }

   public static long ringNextLong(long modulo, AtomicLong atomicLong) {
      Assert.notNull(atomicLong);
      Assert.isTrue(modulo > 0L);
      if (modulo <= 1L) {
         return 0L;
      } else {
         long current;
         long next;
         do {
            current = atomicLong.get();
            next = (current + 1L) % modulo;
         } while(!atomicLong.compareAndSet(current, next));

         return next;
      }
   }
}
