package com.google.protobuf;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class SmallSortedMap<K extends Comparable<K>, V> extends AbstractMap<K, V> {
   private final int maxArraySize;
   private List<SmallSortedMap<K, V>.Entry> entryList;
   private Map<K, V> overflowEntries;
   private boolean isImmutable;
   private volatile SmallSortedMap<K, V>.EntrySet lazyEntrySet;
   private Map<K, V> overflowEntriesDescending;
   private volatile SmallSortedMap<K, V>.DescendingEntrySet lazyDescendingEntrySet;

   static <FieldDescriptorType extends FieldSet.FieldDescriptorLite<FieldDescriptorType>> SmallSortedMap<FieldDescriptorType, Object> newFieldMap(int arraySize) {
      return new SmallSortedMap<FieldDescriptorType, Object>(arraySize) {
         public void makeImmutable() {
            if (!this.isImmutable()) {
               Map.Entry entry;
               List value;
               for(int i = 0; i < this.getNumArrayEntries(); ++i) {
                  entry = this.getArrayEntryAt(i);
                  if (((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) {
                     value = (List)entry.getValue();
                     entry.setValue(Collections.unmodifiableList(value));
                  }
               }

               Iterator var4 = this.getOverflowEntries().iterator();

               while(var4.hasNext()) {
                  entry = (Map.Entry)var4.next();
                  if (((FieldSet.FieldDescriptorLite)entry.getKey()).isRepeated()) {
                     value = (List)entry.getValue();
                     entry.setValue(Collections.unmodifiableList(value));
                  }
               }
            }

            super.makeImmutable();
         }
      };
   }

   static <K extends Comparable<K>, V> SmallSortedMap<K, V> newInstanceForTest(int arraySize) {
      return new SmallSortedMap(arraySize);
   }

   private SmallSortedMap(int arraySize) {
      this.maxArraySize = arraySize;
      this.entryList = Collections.emptyList();
      this.overflowEntries = Collections.emptyMap();
      this.overflowEntriesDescending = Collections.emptyMap();
   }

   public void makeImmutable() {
      if (!this.isImmutable) {
         this.overflowEntries = this.overflowEntries.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.overflowEntries);
         this.overflowEntriesDescending = this.overflowEntriesDescending.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.overflowEntriesDescending);
         this.isImmutable = true;
      }

   }

   public boolean isImmutable() {
      return this.isImmutable;
   }

   public int getNumArrayEntries() {
      return this.entryList.size();
   }

   public Map.Entry<K, V> getArrayEntryAt(int index) {
      return (Map.Entry)this.entryList.get(index);
   }

   public int getNumOverflowEntries() {
      return this.overflowEntries.size();
   }

   public Iterable<Map.Entry<K, V>> getOverflowEntries() {
      return (Iterable)(this.overflowEntries.isEmpty() ? SmallSortedMap.EmptySet.iterable() : this.overflowEntries.entrySet());
   }

   Iterable<Map.Entry<K, V>> getOverflowEntriesDescending() {
      return (Iterable)(this.overflowEntriesDescending.isEmpty() ? SmallSortedMap.EmptySet.iterable() : this.overflowEntriesDescending.entrySet());
   }

   public int size() {
      return this.entryList.size() + this.overflowEntries.size();
   }

   public boolean containsKey(Object o) {
      K key = (Comparable)o;
      return this.binarySearchInArray(key) >= 0 || this.overflowEntries.containsKey(key);
   }

   public V get(Object o) {
      K key = (Comparable)o;
      int index = this.binarySearchInArray(key);
      return index >= 0 ? ((Entry)this.entryList.get(index)).getValue() : this.overflowEntries.get(key);
   }

   public V put(K key, V value) {
      this.checkMutable();
      int index = this.binarySearchInArray(key);
      if (index >= 0) {
         return ((Entry)this.entryList.get(index)).setValue(value);
      } else {
         this.ensureEntryArrayMutable();
         int insertionPoint = -(index + 1);
         if (insertionPoint >= this.maxArraySize) {
            return this.getOverflowEntriesMutable().put(key, value);
         } else {
            if (this.entryList.size() == this.maxArraySize) {
               SmallSortedMap<K, V>.Entry lastEntryInArray = (Entry)this.entryList.remove(this.maxArraySize - 1);
               this.getOverflowEntriesMutable().put(lastEntryInArray.getKey(), lastEntryInArray.getValue());
            }

            this.entryList.add(insertionPoint, new Entry(key, value));
            return null;
         }
      }
   }

   public void clear() {
      this.checkMutable();
      if (!this.entryList.isEmpty()) {
         this.entryList.clear();
      }

      if (!this.overflowEntries.isEmpty()) {
         this.overflowEntries.clear();
      }

   }

   public V remove(Object o) {
      this.checkMutable();
      K key = (Comparable)o;
      int index = this.binarySearchInArray(key);
      if (index >= 0) {
         return this.removeArrayEntryAt(index);
      } else {
         return this.overflowEntries.isEmpty() ? null : this.overflowEntries.remove(key);
      }
   }

   private V removeArrayEntryAt(int index) {
      this.checkMutable();
      V removed = ((Entry)this.entryList.remove(index)).getValue();
      if (!this.overflowEntries.isEmpty()) {
         Iterator<Map.Entry<K, V>> iterator = this.getOverflowEntriesMutable().entrySet().iterator();
         this.entryList.add(new Entry((Map.Entry)iterator.next()));
         iterator.remove();
      }

      return removed;
   }

   private int binarySearchInArray(K key) {
      int left = 0;
      int right = this.entryList.size() - 1;
      int mid;
      if (right >= 0) {
         mid = key.compareTo(((Entry)this.entryList.get(right)).getKey());
         if (mid > 0) {
            return -(right + 2);
         }

         if (mid == 0) {
            return right;
         }
      }

      while(left <= right) {
         mid = (left + right) / 2;
         int cmp = key.compareTo(((Entry)this.entryList.get(mid)).getKey());
         if (cmp < 0) {
            right = mid - 1;
         } else {
            if (cmp <= 0) {
               return mid;
            }

            left = mid + 1;
         }
      }

      return -(left + 1);
   }

   public Set<Map.Entry<K, V>> entrySet() {
      if (this.lazyEntrySet == null) {
         this.lazyEntrySet = new EntrySet();
      }

      return this.lazyEntrySet;
   }

   Set<Map.Entry<K, V>> descendingEntrySet() {
      if (this.lazyDescendingEntrySet == null) {
         this.lazyDescendingEntrySet = new DescendingEntrySet();
      }

      return this.lazyDescendingEntrySet;
   }

   private void checkMutable() {
      if (this.isImmutable) {
         throw new UnsupportedOperationException();
      }
   }

   private SortedMap<K, V> getOverflowEntriesMutable() {
      this.checkMutable();
      if (this.overflowEntries.isEmpty() && !(this.overflowEntries instanceof TreeMap)) {
         this.overflowEntries = new TreeMap();
         this.overflowEntriesDescending = ((TreeMap)this.overflowEntries).descendingMap();
      }

      return (SortedMap)this.overflowEntries;
   }

   private void ensureEntryArrayMutable() {
      this.checkMutable();
      if (this.entryList.isEmpty() && !(this.entryList instanceof ArrayList)) {
         this.entryList = new ArrayList(this.maxArraySize);
      }

   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof SmallSortedMap)) {
         return super.equals(o);
      } else {
         SmallSortedMap<?, ?> other = (SmallSortedMap)o;
         int size = this.size();
         if (size != other.size()) {
            return false;
         } else {
            int numArrayEntries = this.getNumArrayEntries();
            if (numArrayEntries != other.getNumArrayEntries()) {
               return this.entrySet().equals(other.entrySet());
            } else {
               for(int i = 0; i < numArrayEntries; ++i) {
                  if (!this.getArrayEntryAt(i).equals(other.getArrayEntryAt(i))) {
                     return false;
                  }
               }

               if (numArrayEntries != size) {
                  return this.overflowEntries.equals(other.overflowEntries);
               } else {
                  return true;
               }
            }
         }
      }
   }

   public int hashCode() {
      int h = 0;
      int listSize = this.getNumArrayEntries();

      for(int i = 0; i < listSize; ++i) {
         h += ((Entry)this.entryList.get(i)).hashCode();
      }

      if (this.getNumOverflowEntries() > 0) {
         h += this.overflowEntries.hashCode();
      }

      return h;
   }

   // $FF: synthetic method
   SmallSortedMap(int x0, Object x1) {
      this(x0);
   }

   private static class EmptySet {
      private static final Iterator<Object> ITERATOR = new Iterator<Object>() {
         public boolean hasNext() {
            return false;
         }

         public Object next() {
            throw new NoSuchElementException();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
      private static final Iterable<Object> ITERABLE = new Iterable<Object>() {
         public Iterator<Object> iterator() {
            return SmallSortedMap.EmptySet.ITERATOR;
         }
      };

      static <T> Iterable<T> iterable() {
         return ITERABLE;
      }
   }

   private class DescendingEntryIterator implements Iterator<Map.Entry<K, V>> {
      private int pos;
      private Iterator<Map.Entry<K, V>> lazyOverflowIterator;

      private DescendingEntryIterator() {
         this.pos = SmallSortedMap.this.entryList.size();
      }

      public boolean hasNext() {
         return this.pos > 0 && this.pos <= SmallSortedMap.this.entryList.size() || this.getOverflowIterator().hasNext();
      }

      public Map.Entry<K, V> next() {
         return this.getOverflowIterator().hasNext() ? (Map.Entry)this.getOverflowIterator().next() : (Map.Entry)SmallSortedMap.this.entryList.get(--this.pos);
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      private Iterator<Map.Entry<K, V>> getOverflowIterator() {
         if (this.lazyOverflowIterator == null) {
            this.lazyOverflowIterator = SmallSortedMap.this.overflowEntriesDescending.entrySet().iterator();
         }

         return this.lazyOverflowIterator;
      }

      // $FF: synthetic method
      DescendingEntryIterator(Object x1) {
         this();
      }
   }

   private class EntryIterator implements Iterator<Map.Entry<K, V>> {
      private int pos;
      private boolean nextCalledBeforeRemove;
      private Iterator<Map.Entry<K, V>> lazyOverflowIterator;

      private EntryIterator() {
         this.pos = -1;
      }

      public boolean hasNext() {
         return this.pos + 1 < SmallSortedMap.this.entryList.size() || !SmallSortedMap.this.overflowEntries.isEmpty() && this.getOverflowIterator().hasNext();
      }

      public Map.Entry<K, V> next() {
         this.nextCalledBeforeRemove = true;
         return ++this.pos < SmallSortedMap.this.entryList.size() ? (Map.Entry)SmallSortedMap.this.entryList.get(this.pos) : (Map.Entry)this.getOverflowIterator().next();
      }

      public void remove() {
         if (!this.nextCalledBeforeRemove) {
            throw new IllegalStateException("remove() was called before next()");
         } else {
            this.nextCalledBeforeRemove = false;
            SmallSortedMap.this.checkMutable();
            if (this.pos < SmallSortedMap.this.entryList.size()) {
               SmallSortedMap.this.removeArrayEntryAt(this.pos--);
            } else {
               this.getOverflowIterator().remove();
            }

         }
      }

      private Iterator<Map.Entry<K, V>> getOverflowIterator() {
         if (this.lazyOverflowIterator == null) {
            this.lazyOverflowIterator = SmallSortedMap.this.overflowEntries.entrySet().iterator();
         }

         return this.lazyOverflowIterator;
      }

      // $FF: synthetic method
      EntryIterator(Object x1) {
         this();
      }
   }

   private class DescendingEntrySet extends SmallSortedMap<K, V>.EntrySet {
      private DescendingEntrySet() {
         super(null);
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return SmallSortedMap.this.new DescendingEntryIterator();
      }

      // $FF: synthetic method
      DescendingEntrySet(Object x1) {
         this();
      }
   }

   private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      private EntrySet() {
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return SmallSortedMap.this.new EntryIterator();
      }

      public int size() {
         return SmallSortedMap.this.size();
      }

      public boolean contains(Object o) {
         Map.Entry<K, V> entry = (Map.Entry)o;
         V existing = SmallSortedMap.this.get(entry.getKey());
         V value = entry.getValue();
         return existing == value || existing != null && existing.equals(value);
      }

      public boolean add(Map.Entry<K, V> entry) {
         if (!this.contains(entry)) {
            SmallSortedMap.this.put((Comparable)entry.getKey(), entry.getValue());
            return true;
         } else {
            return false;
         }
      }

      public boolean remove(Object o) {
         Map.Entry<K, V> entry = (Map.Entry)o;
         if (this.contains(entry)) {
            SmallSortedMap.this.remove(entry.getKey());
            return true;
         } else {
            return false;
         }
      }

      public void clear() {
         SmallSortedMap.this.clear();
      }

      // $FF: synthetic method
      EntrySet(Object x1) {
         this();
      }
   }

   private class Entry implements Map.Entry<K, V>, Comparable<SmallSortedMap<K, V>.Entry> {
      private final K key;
      private V value;

      Entry(Map.Entry<K, V> copy) {
         this((Comparable)copy.getKey(), copy.getValue());
      }

      Entry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public int compareTo(SmallSortedMap<K, V>.Entry other) {
         return this.getKey().compareTo(other.getKey());
      }

      public V setValue(V newValue) {
         SmallSortedMap.this.checkMutable();
         V oldValue = this.value;
         this.value = newValue;
         return oldValue;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> other = (Map.Entry)o;
            return this.equals(this.key, other.getKey()) && this.equals(this.value, other.getValue());
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "=" + this.value;
      }

      private boolean equals(Object o1, Object o2) {
         return o1 == null ? o2 == null : o1.equals(o2);
      }
   }
}
