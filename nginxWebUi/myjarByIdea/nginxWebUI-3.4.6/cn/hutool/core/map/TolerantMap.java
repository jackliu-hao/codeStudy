package cn.hutool.core.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TolerantMap<K, V> extends MapWrapper<K, V> {
   private static final long serialVersionUID = -4158133823263496197L;
   private final V defaultValue;

   public TolerantMap(V defaultValue) {
      this(new HashMap(), defaultValue);
   }

   public TolerantMap(int initialCapacity, float loadFactor, V defaultValue) {
      this(new HashMap(initialCapacity, loadFactor), defaultValue);
   }

   public TolerantMap(int initialCapacity, V defaultValue) {
      this(new HashMap(initialCapacity), defaultValue);
   }

   public TolerantMap(Map<K, V> map, V defaultValue) {
      super(map);
      this.defaultValue = defaultValue;
   }

   public static <K, V> TolerantMap<K, V> of(Map<K, V> map, V defaultValue) {
      return new TolerantMap(map, defaultValue);
   }

   public V get(Object key) {
      return this.getOrDefault(key, this.defaultValue);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         if (!super.equals(o)) {
            return false;
         } else {
            TolerantMap<?, ?> that = (TolerantMap)o;
            return this.getRaw().equals(that.getRaw()) && Objects.equals(this.defaultValue, that.defaultValue);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getRaw(), this.defaultValue});
   }

   public String toString() {
      return "TolerantMap{map=" + this.getRaw() + ", defaultValue=" + this.defaultValue + '}';
   }
}
