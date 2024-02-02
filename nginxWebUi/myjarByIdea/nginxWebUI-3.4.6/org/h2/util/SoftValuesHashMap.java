package org.h2.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoftValuesHashMap<K, V> extends AbstractMap<K, V> {
   private final Map<K, SoftValue<V>> map = new HashMap();
   private final ReferenceQueue<V> queue = new ReferenceQueue();

   private void processQueue() {
      while(true) {
         Reference var1 = this.queue.poll();
         if (var1 == null) {
            return;
         }

         SoftValue var2 = (SoftValue)var1;
         Object var3 = var2.key;
         this.map.remove(var3);
      }
   }

   public V get(Object var1) {
      this.processQueue();
      SoftReference var2 = (SoftReference)this.map.get(var1);
      return var2 == null ? null : var2.get();
   }

   public V put(K var1, V var2) {
      this.processQueue();
      SoftValue var3 = (SoftValue)this.map.put(var1, new SoftValue(var2, this.queue, var1));
      return var3 == null ? null : var3.get();
   }

   public V remove(Object var1) {
      this.processQueue();
      SoftReference var2 = (SoftReference)this.map.remove(var1);
      return var2 == null ? null : var2.get();
   }

   public void clear() {
      this.processQueue();
      this.map.clear();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      throw new UnsupportedOperationException();
   }

   private static class SoftValue<T> extends SoftReference<T> {
      final Object key;

      public SoftValue(T var1, ReferenceQueue<T> var2, Object var3) {
         super(var1, var2);
         this.key = var3;
      }
   }
}
