package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Serializable {
   private static final long serialVersionUID = 7997886765361607470L;
   private static final Boolean PRESENT = true;
   private final ConcurrentHashMap<E, Boolean> map;

   public ConcurrentHashSet() {
      this.map = new ConcurrentHashMap();
   }

   public ConcurrentHashSet(int initialCapacity) {
      this.map = new ConcurrentHashMap(initialCapacity);
   }

   public ConcurrentHashSet(int initialCapacity, float loadFactor) {
      this.map = new ConcurrentHashMap(initialCapacity, loadFactor);
   }

   public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
      this.map = new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
   }

   public ConcurrentHashSet(Iterable<E> iter) {
      if (iter instanceof Collection) {
         Collection<E> collection = (Collection)iter;
         this.map = new ConcurrentHashMap((int)((float)collection.size() / 0.75F));
         this.addAll(collection);
      } else {
         this.map = new ConcurrentHashMap();
         Iterator var4 = iter.iterator();

         while(var4.hasNext()) {
            E e = var4.next();
            this.add(e);
         }
      }

   }

   public Iterator<E> iterator() {
      return this.map.keySet().iterator();
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean contains(Object o) {
      return this.map.containsKey(o);
   }

   public boolean add(E e) {
      return this.map.put(e, PRESENT) == null;
   }

   public boolean remove(Object o) {
      return PRESENT.equals(this.map.remove(o));
   }

   public void clear() {
      this.map.clear();
   }
}
