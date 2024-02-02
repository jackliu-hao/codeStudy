package cn.hutool.core.lang.mutable;

import cn.hutool.core.lang.Pair;

public class MutablePair<K, V> extends Pair<K, V> implements Mutable<Pair<K, V>> {
   private static final long serialVersionUID = 1L;

   public MutablePair(K key, V value) {
      super(key, value);
   }

   public MutablePair<K, V> setKey(K key) {
      this.key = key;
      return this;
   }

   public MutablePair<K, V> setValue(V value) {
      this.value = value;
      return this;
   }

   public Pair<K, V> get() {
      return this;
   }

   public void set(Pair<K, V> pair) {
      this.key = pair.getKey();
      this.value = pair.getValue();
   }
}
