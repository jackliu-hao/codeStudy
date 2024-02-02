package cn.hutool.core.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class ReferenceUtil {
   public static <T> Reference<T> create(ReferenceType type, T referent) {
      return create(type, referent, (ReferenceQueue)null);
   }

   public static <T> Reference<T> create(ReferenceType type, T referent, ReferenceQueue<T> queue) {
      switch (type) {
         case SOFT:
            return new SoftReference(referent, queue);
         case WEAK:
            return new WeakReference(referent, queue);
         case PHANTOM:
            return new PhantomReference(referent, queue);
         default:
            return null;
      }
   }

   public static enum ReferenceType {
      SOFT,
      WEAK,
      PHANTOM;
   }
}
