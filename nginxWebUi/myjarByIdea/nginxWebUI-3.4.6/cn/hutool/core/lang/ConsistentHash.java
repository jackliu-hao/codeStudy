package cn.hutool.core.lang;

import cn.hutool.core.lang.hash.Hash32;
import cn.hutool.core.util.HashUtil;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash<T> implements Serializable {
   private static final long serialVersionUID = 1L;
   Hash32<Object> hashFunc;
   private final int numberOfReplicas;
   private final SortedMap<Integer, T> circle = new TreeMap();

   public ConsistentHash(int numberOfReplicas, Collection<T> nodes) {
      this.numberOfReplicas = numberOfReplicas;
      this.hashFunc = (key) -> {
         return HashUtil.fnvHash(key.toString());
      };
      Iterator var3 = nodes.iterator();

      while(var3.hasNext()) {
         T node = var3.next();
         this.add(node);
      }

   }

   public ConsistentHash(Hash32<Object> hashFunc, int numberOfReplicas, Collection<T> nodes) {
      this.numberOfReplicas = numberOfReplicas;
      this.hashFunc = hashFunc;
      Iterator var4 = nodes.iterator();

      while(var4.hasNext()) {
         T node = var4.next();
         this.add(node);
      }

   }

   public void add(T node) {
      for(int i = 0; i < this.numberOfReplicas; ++i) {
         this.circle.put(this.hashFunc.hash32(node.toString() + i), node);
      }

   }

   public void remove(T node) {
      for(int i = 0; i < this.numberOfReplicas; ++i) {
         this.circle.remove(this.hashFunc.hash32(node.toString() + i));
      }

   }

   public T get(Object key) {
      if (this.circle.isEmpty()) {
         return null;
      } else {
         int hash = this.hashFunc.hash32(key);
         if (!this.circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = this.circle.tailMap(hash);
            hash = tailMap.isEmpty() ? (Integer)this.circle.firstKey() : (Integer)tailMap.firstKey();
         }

         return this.circle.get(hash);
      }
   }
}
