package cn.hutool.core.map;

import cn.hutool.core.util.ObjectUtil;
import java.util.Map;

public abstract class AbsEntry<K, V> implements Map.Entry<K, V> {
   public V setValue(V value) {
      throw new UnsupportedOperationException("Entry is read only.");
   }

   public boolean equals(Object object) {
      if (!(object instanceof Map.Entry)) {
         return false;
      } else {
         Map.Entry<?, ?> that = (Map.Entry)object;
         return ObjectUtil.equals(this.getKey(), that.getKey()) && ObjectUtil.equals(this.getValue(), that.getValue());
      }
   }

   public int hashCode() {
      K k = this.getKey();
      V v = this.getValue();
      return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
   }

   public String toString() {
      return this.getKey() + "=" + this.getValue();
   }
}
