package org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MultiMap<K, V> extends LinkedHashMap<K, List<V>> {
   public void map(K key, V value) {
      List<V> elementsForKey = (List)this.get(key);
      if (elementsForKey == null) {
         elementsForKey = new ArrayList();
         super.put(key, elementsForKey);
      }

      ((List)elementsForKey).add(value);
   }

   public List<Pair<K, V>> getPairs() {
      List<Pair<K, V>> pairs = new ArrayList();
      Iterator i$ = this.keySet().iterator();

      while(i$.hasNext()) {
         K key = i$.next();
         Iterator i$ = ((List)this.get(key)).iterator();

         while(i$.hasNext()) {
            V value = i$.next();
            pairs.add(new Pair(key, value));
         }
      }

      return pairs;
   }
}
