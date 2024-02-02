package org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlexibleHashMap<K, V> implements Map<K, V> {
   public static final int INITAL_CAPACITY = 16;
   public static final int INITAL_BUCKET_CAPACITY = 8;
   public static final double LOAD_FACTOR = 0.75;
   protected final AbstractEqualityComparator<? super K> comparator;
   protected LinkedList<Entry<K, V>>[] buckets;
   protected int n;
   protected int threshold;
   protected int currentPrime;
   protected int initialBucketCapacity;

   public FlexibleHashMap() {
      this((AbstractEqualityComparator)null, 16, 8);
   }

   public FlexibleHashMap(AbstractEqualityComparator<? super K> comparator) {
      this(comparator, 16, 8);
   }

   public FlexibleHashMap(AbstractEqualityComparator<? super K> comparator, int initialCapacity, int initialBucketCapacity) {
      this.n = 0;
      this.threshold = 12;
      this.currentPrime = 1;
      this.initialBucketCapacity = 8;
      if (comparator == null) {
         comparator = ObjectEqualityComparator.INSTANCE;
      }

      this.comparator = (AbstractEqualityComparator)comparator;
      this.buckets = createEntryListArray(initialBucketCapacity);
      this.initialBucketCapacity = initialBucketCapacity;
   }

   private static <K, V> LinkedList<Entry<K, V>>[] createEntryListArray(int length) {
      LinkedList<Entry<K, V>>[] result = (LinkedList[])(new LinkedList[length]);
      return result;
   }

   protected int getBucket(K key) {
      int hash = this.comparator.hashCode(key);
      int b = hash & this.buckets.length - 1;
      return b;
   }

   public V get(Object key) {
      K typedKey = key;
      if (key == null) {
         return null;
      } else {
         int b = this.getBucket(key);
         LinkedList<Entry<K, V>> bucket = this.buckets[b];
         if (bucket == null) {
            return null;
         } else {
            Iterator i$ = bucket.iterator();

            Entry e;
            do {
               if (!i$.hasNext()) {
                  return null;
               }

               e = (Entry)i$.next();
            } while(!this.comparator.equals(e.key, typedKey));

            return e.value;
         }
      }
   }

   public V put(K key, V value) {
      if (key == null) {
         return null;
      } else {
         if (this.n > this.threshold) {
            this.expand();
         }

         int b = this.getBucket(key);
         LinkedList<Entry<K, V>> bucket = this.buckets[b];
         if (bucket == null) {
            bucket = this.buckets[b] = new LinkedList();
         }

         Iterator i$ = bucket.iterator();

         Entry e;
         do {
            if (!i$.hasNext()) {
               bucket.add(new Entry(key, value));
               ++this.n;
               return null;
            }

            e = (Entry)i$.next();
         } while(!this.comparator.equals(e.key, key));

         V prev = e.value;
         e.value = value;
         ++this.n;
         return prev;
      }
   }

   public V remove(Object key) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      throw new UnsupportedOperationException();
   }

   public Set<K> keySet() {
      throw new UnsupportedOperationException();
   }

   public Collection<V> values() {
      List<V> a = new ArrayList(this.size());
      LinkedList[] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         LinkedList<Entry<K, V>> bucket = arr$[i$];
         if (bucket != null) {
            Iterator i$ = bucket.iterator();

            while(i$.hasNext()) {
               Entry<K, V> e = (Entry)i$.next();
               a.add(e.value);
            }
         }
      }

      return a;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      throw new UnsupportedOperationException();
   }

   public boolean containsKey(Object key) {
      return this.get(key) != null;
   }

   public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();
      LinkedList[] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         LinkedList<Entry<K, V>> bucket = arr$[i$];
         Entry e;
         if (bucket != null) {
            for(Iterator i$ = bucket.iterator(); i$.hasNext(); hash = MurmurHash.update(hash, this.comparator.hashCode(e.key))) {
               e = (Entry)i$.next();
               if (e == null) {
                  break;
               }
            }
         }
      }

      hash = MurmurHash.finish(hash, this.size());
      return hash;
   }

   public boolean equals(Object o) {
      throw new UnsupportedOperationException();
   }

   protected void expand() {
      LinkedList<Entry<K, V>>[] old = this.buckets;
      this.currentPrime += 4;
      int newCapacity = this.buckets.length * 2;
      LinkedList<Entry<K, V>>[] newTable = createEntryListArray(newCapacity);
      this.buckets = newTable;
      this.threshold = (int)((double)newCapacity * 0.75);
      int oldSize = this.size();
      LinkedList[] arr$ = old;
      int len$ = old.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         LinkedList<Entry<K, V>> bucket = arr$[i$];
         if (bucket != null) {
            Iterator i$ = bucket.iterator();

            while(i$.hasNext()) {
               Entry<K, V> e = (Entry)i$.next();
               if (e == null) {
                  break;
               }

               this.put(e.key, e.value);
            }
         }
      }

      this.n = oldSize;
   }

   public int size() {
      return this.n;
   }

   public boolean isEmpty() {
      return this.n == 0;
   }

   public void clear() {
      this.buckets = createEntryListArray(16);
      this.n = 0;
   }

   public String toString() {
      if (this.size() == 0) {
         return "{}";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append('{');
         boolean first = true;
         LinkedList[] arr$ = this.buckets;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            LinkedList<Entry<K, V>> bucket = arr$[i$];
            Entry e;
            if (bucket != null) {
               for(Iterator i$ = bucket.iterator(); i$.hasNext(); buf.append(e.toString())) {
                  e = (Entry)i$.next();
                  if (e == null) {
                     break;
                  }

                  if (first) {
                     first = false;
                  } else {
                     buf.append(", ");
                  }
               }
            }
         }

         buf.append('}');
         return buf.toString();
      }
   }

   public String toTableString() {
      StringBuilder buf = new StringBuilder();
      LinkedList[] arr$ = this.buckets;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         LinkedList<Entry<K, V>> bucket = arr$[i$];
         if (bucket == null) {
            buf.append("null\n");
         } else {
            buf.append('[');
            boolean first = true;
            Iterator i$ = bucket.iterator();

            while(i$.hasNext()) {
               Entry<K, V> e = (Entry)i$.next();
               if (first) {
                  first = false;
               } else {
                  buf.append(" ");
               }

               if (e == null) {
                  buf.append("_");
               } else {
                  buf.append(e.toString());
               }
            }

            buf.append("]\n");
         }
      }

      return buf.toString();
   }

   public static void main(String[] args) {
      FlexibleHashMap<String, Integer> map = new FlexibleHashMap();
      map.put("hi", 1);
      map.put("mom", 2);
      map.put("foo", 3);
      map.put("ach", 4);
      map.put("cbba", 5);
      map.put("d", 6);
      map.put("edf", 7);
      map.put("mom", 8);
      map.put("hi", 9);
      System.out.println(map);
      System.out.println(map.toTableString());
   }

   public static class Entry<K, V> {
      public final K key;
      public V value;

      public Entry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public String toString() {
         return this.key.toString() + ":" + this.value.toString();
      }
   }
}
