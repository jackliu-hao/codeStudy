package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReferenceUtil;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReferenceConcurrentMap<K, V> implements ConcurrentMap<K, V>, Iterable<Map.Entry<K, V>>, Serializable {
   final ConcurrentMap<Reference<K>, V> raw;
   private final ReferenceQueue<K> lastQueue;
   private final ReferenceUtil.ReferenceType keyType;
   private BiConsumer<Reference<? extends K>, V> purgeListener;

   public ReferenceConcurrentMap(ConcurrentMap<Reference<K>, V> raw, ReferenceUtil.ReferenceType referenceType) {
      this.raw = raw;
      this.keyType = referenceType;
      this.lastQueue = new ReferenceQueue();
   }

   public void setPurgeListener(BiConsumer<Reference<? extends K>, V> purgeListener) {
      this.purgeListener = purgeListener;
   }

   public int size() {
      this.purgeStaleKeys();
      return this.raw.size();
   }

   public boolean isEmpty() {
      return 0 == this.size();
   }

   public V get(Object key) {
      this.purgeStaleKeys();
      return this.raw.get(this.ofKey(key, (ReferenceQueue)null));
   }

   public boolean containsKey(Object key) {
      this.purgeStaleKeys();
      return this.raw.containsKey(this.ofKey(key, (ReferenceQueue)null));
   }

   public boolean containsValue(Object value) {
      this.purgeStaleKeys();
      return this.raw.containsValue(value);
   }

   public V put(K key, V value) {
      this.purgeStaleKeys();
      return this.raw.put(this.ofKey(key, this.lastQueue), value);
   }

   public V putIfAbsent(K key, V value) {
      this.purgeStaleKeys();
      return this.raw.putIfAbsent(this.ofKey(key, this.lastQueue), value);
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      m.forEach(this::put);
   }

   public V replace(K key, V value) {
      this.purgeStaleKeys();
      return this.raw.replace(this.ofKey(key, this.lastQueue), value);
   }

   public boolean replace(K key, V oldValue, V newValue) {
      this.purgeStaleKeys();
      return this.raw.replace(this.ofKey(key, this.lastQueue), oldValue, newValue);
   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      this.purgeStaleKeys();
      this.raw.replaceAll((kWeakKey, value) -> {
         return function.apply(kWeakKey.get(), value);
      });
   }

   public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
      this.purgeStaleKeys();
      return this.raw.computeIfAbsent(this.ofKey(key, this.lastQueue), (kWeakKey) -> {
         return mappingFunction.apply(key);
      });
   }

   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      this.purgeStaleKeys();
      return this.raw.computeIfPresent(this.ofKey(key, this.lastQueue), (kWeakKey, value) -> {
         return remappingFunction.apply(key, value);
      });
   }

   public V computeIfAbsent(K key, Func0<? extends V> supplier) {
      return this.computeIfAbsent(key, (keyParam) -> {
         return supplier.callWithRuntimeException();
      });
   }

   public V remove(Object key) {
      this.purgeStaleKeys();
      return this.raw.remove(this.ofKey(key, (ReferenceQueue)null));
   }

   public boolean remove(Object key, Object value) {
      this.purgeStaleKeys();
      return this.raw.remove(this.ofKey(key, (ReferenceQueue)null), value);
   }

   public void clear() {
      this.raw.clear();

      while(this.lastQueue.poll() != null) {
      }

   }

   public Set<K> keySet() {
      Collection<K> trans = CollUtil.trans(this.raw.keySet(), (reference) -> {
         return null == reference ? null : reference.get();
      });
      return new HashSet(trans);
   }

   public Collection<V> values() {
      this.purgeStaleKeys();
      return this.raw.values();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      this.purgeStaleKeys();
      return (Set)this.raw.entrySet().stream().map((entry) -> {
         return new AbstractMap.SimpleImmutableEntry(((Reference)entry.getKey()).get(), entry.getValue());
      }).collect(Collectors.toSet());
   }

   public void forEach(BiConsumer<? super K, ? super V> action) {
      this.purgeStaleKeys();
      this.raw.forEach((key, value) -> {
         action.accept(key.get(), value);
      });
   }

   public Iterator<Map.Entry<K, V>> iterator() {
      return this.entrySet().iterator();
   }

   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      this.purgeStaleKeys();
      return this.raw.compute(this.ofKey(key, this.lastQueue), (kWeakKey, value) -> {
         return remappingFunction.apply(key, value);
      });
   }

   public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      this.purgeStaleKeys();
      return this.raw.merge(this.ofKey(key, this.lastQueue), value, remappingFunction);
   }

   private void purgeStaleKeys() {
      Reference reference;
      while((reference = this.lastQueue.poll()) != null) {
         V value = this.raw.remove(reference);
         if (null != this.purgeListener) {
            this.purgeListener.accept(reference, value);
         }
      }

   }

   private Reference<K> ofKey(K key, ReferenceQueue<? super K> queue) {
      switch (this.keyType) {
         case WEAK:
            return new WeakKey(key, queue);
         case SOFT:
            return new SoftKey(key, queue);
         default:
            throw new IllegalArgumentException("Unsupported key type: " + this.keyType);
      }
   }

   private static class SoftKey<K> extends SoftReference<K> {
      private final int hashCode;

      SoftKey(K key, ReferenceQueue<? super K> queue) {
         super(key, queue);
         this.hashCode = key.hashCode();
      }

      public int hashCode() {
         return this.hashCode;
      }

      public boolean equals(Object other) {
         if (other == this) {
            return true;
         } else {
            return other instanceof SoftKey ? ObjectUtil.equals(((SoftKey)other).get(), this.get()) : false;
         }
      }
   }

   private static class WeakKey<K> extends WeakReference<K> {
      private final int hashCode;

      WeakKey(K key, ReferenceQueue<? super K> queue) {
         super(key, queue);
         this.hashCode = key.hashCode();
      }

      public int hashCode() {
         return this.hashCode;
      }

      public boolean equals(Object other) {
         if (other == this) {
            return true;
         } else {
            return other instanceof WeakKey ? ObjectUtil.equals(((WeakKey)other).get(), this.get()) : false;
         }
      }
   }
}
