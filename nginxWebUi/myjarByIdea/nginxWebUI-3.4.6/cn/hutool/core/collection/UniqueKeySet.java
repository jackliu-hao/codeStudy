package cn.hutool.core.collection;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class UniqueKeySet<K, V> extends AbstractSet<V> implements Serializable {
   private static final long serialVersionUID = 1L;
   private Map<K, V> map;
   private final Function<V, K> uniqueGenerator;

   public UniqueKeySet(Function<V, K> uniqueGenerator) {
      this(false, uniqueGenerator);
   }

   public UniqueKeySet(Function<V, K> uniqueGenerator, Collection<? extends V> c) {
      this(false, uniqueGenerator, c);
   }

   public UniqueKeySet(boolean isLinked, Function<V, K> uniqueGenerator) {
      this(MapBuilder.create(isLinked), uniqueGenerator);
   }

   public UniqueKeySet(boolean isLinked, Function<V, K> uniqueGenerator, Collection<? extends V> c) {
      this(isLinked, uniqueGenerator);
      this.addAll(c);
   }

   public UniqueKeySet(int initialCapacity, float loadFactor, Function<V, K> uniqueGenerator) {
      this(MapBuilder.create(new HashMap(initialCapacity, loadFactor)), uniqueGenerator);
   }

   public UniqueKeySet(MapBuilder<K, V> builder, Function<V, K> uniqueGenerator) {
      this.map = builder.build();
      this.uniqueGenerator = uniqueGenerator;
   }

   public Iterator<V> iterator() {
      return this.map.values().iterator();
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean contains(Object o) {
      return this.map.containsKey(this.uniqueGenerator.apply(o));
   }

   public boolean add(V v) {
      return null == this.map.put(this.uniqueGenerator.apply(v), v);
   }

   public boolean addIfAbsent(V v) {
      return null == this.map.putIfAbsent(this.uniqueGenerator.apply(v), v);
   }

   public boolean addAllIfAbsent(Collection<? extends V> c) {
      boolean modified = false;
      Iterator var3 = c.iterator();

      while(var3.hasNext()) {
         V v = var3.next();
         if (this.addIfAbsent(v)) {
            modified = true;
         }
      }

      return modified;
   }

   public boolean remove(Object o) {
      return null != this.map.remove(this.uniqueGenerator.apply(o));
   }

   public void clear() {
      this.map.clear();
   }

   public UniqueKeySet<K, V> clone() {
      try {
         UniqueKeySet<K, V> newSet = (UniqueKeySet)super.clone();
         newSet.map = (Map)ObjectUtil.clone(this.map);
         return newSet;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }
}
