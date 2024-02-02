package cn.hutool.core.map;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
   private static final long serialVersionUID = -629171177321416095L;
   private int capacity;

   public FixedLinkedHashMap(int capacity) {
      super(capacity + 1, 1.0F, true);
      this.capacity = capacity;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public void setCapacity(int capacity) {
      this.capacity = capacity;
   }

   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return this.size() > this.capacity;
   }
}
