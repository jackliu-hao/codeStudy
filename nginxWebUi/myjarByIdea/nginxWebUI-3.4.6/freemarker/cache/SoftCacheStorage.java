package freemarker.cache;

import freemarker.template.utility.UndeclaredThrowableException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SoftCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {
   private static final Method atomicRemove = getAtomicRemoveMethod();
   private final ReferenceQueue queue;
   private final Map map;
   private final boolean concurrent;

   public SoftCacheStorage() {
      this(new ConcurrentHashMap());
   }

   public boolean isConcurrent() {
      return this.concurrent;
   }

   public SoftCacheStorage(Map backingMap) {
      this.queue = new ReferenceQueue();
      this.map = backingMap;
      this.concurrent = this.map instanceof ConcurrentMap;
   }

   public Object get(Object key) {
      this.processQueue();
      Reference ref = (Reference)this.map.get(key);
      return ref == null ? null : ref.get();
   }

   public void put(Object key, Object value) {
      this.processQueue();
      this.map.put(key, new SoftValueReference(key, value, this.queue));
   }

   public void remove(Object key) {
      this.processQueue();
      this.map.remove(key);
   }

   public void clear() {
      this.map.clear();
      this.processQueue();
   }

   public int getSize() {
      this.processQueue();
      return this.map.size();
   }

   private void processQueue() {
      while(true) {
         SoftValueReference ref = (SoftValueReference)this.queue.poll();
         if (ref == null) {
            return;
         }

         Object key = ref.getKey();
         if (this.concurrent) {
            try {
               atomicRemove.invoke(this.map, key, ref);
            } catch (InvocationTargetException | IllegalAccessException var4) {
               throw new UndeclaredThrowableException(var4);
            }
         } else if (this.map.get(key) == ref) {
            this.map.remove(key);
         }
      }
   }

   private static Method getAtomicRemoveMethod() {
      try {
         return Class.forName("java.util.concurrent.ConcurrentMap").getMethod("remove", Object.class, Object.class);
      } catch (ClassNotFoundException var1) {
         return null;
      } catch (NoSuchMethodException var2) {
         throw new UndeclaredThrowableException(var2);
      }
   }

   private static final class SoftValueReference extends SoftReference {
      private final Object key;

      SoftValueReference(Object key, Object value, ReferenceQueue queue) {
         super(value, queue);
         this.key = key;
      }

      Object getKey() {
         return this.key;
      }
   }
}
