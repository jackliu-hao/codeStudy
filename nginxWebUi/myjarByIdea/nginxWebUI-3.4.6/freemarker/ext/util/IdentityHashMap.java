package freemarker.ext.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/** @deprecated */
@Deprecated
public class IdentityHashMap extends AbstractMap implements Map, Cloneable, Serializable {
   public static final long serialVersionUID = 362498820763181265L;
   private transient Entry[] table;
   private transient int count;
   private int threshold;
   private float loadFactor;
   private transient int modCount;
   private transient Set keySet;
   private transient Set entrySet;
   private transient Collection values;
   private static final int KEYS = 0;
   private static final int VALUES = 1;
   private static final int ENTRIES = 2;
   private static EmptyHashIterator emptyHashIterator = new EmptyHashIterator();

   public IdentityHashMap(int initialCapacity, float loadFactor) {
      this.modCount = 0;
      this.keySet = null;
      this.entrySet = null;
      this.values = null;
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Initial Capacity: " + initialCapacity);
      } else if (!(loadFactor <= 0.0F) && !Float.isNaN(loadFactor)) {
         if (initialCapacity == 0) {
            initialCapacity = 1;
         }

         this.loadFactor = loadFactor;
         this.table = new Entry[initialCapacity];
         this.threshold = (int)((float)initialCapacity * loadFactor);
      } else {
         throw new IllegalArgumentException("Illegal Load factor: " + loadFactor);
      }
   }

   public IdentityHashMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public IdentityHashMap() {
      this(11, 0.75F);
   }

   public IdentityHashMap(Map t) {
      this(Math.max(2 * t.size(), 11), 0.75F);
      this.putAll(t);
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public boolean containsValue(Object value) {
      Entry[] tab = this.table;
      int i;
      Entry e;
      if (value == null) {
         i = tab.length;

         while(i-- > 0) {
            for(e = tab[i]; e != null; e = e.next) {
               if (e.value == null) {
                  return true;
               }
            }
         }
      } else {
         i = tab.length;

         while(i-- > 0) {
            for(e = tab[i]; e != null; e = e.next) {
               if (value.equals(e.value)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean containsKey(Object key) {
      Entry[] tab = this.table;
      if (key != null) {
         int hash = System.identityHashCode(key);
         int index = (hash & Integer.MAX_VALUE) % tab.length;

         for(Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && key == e.key) {
               return true;
            }
         }
      } else {
         for(Entry e = tab[0]; e != null; e = e.next) {
            if (e.key == null) {
               return true;
            }
         }
      }

      return false;
   }

   public Object get(Object key) {
      Entry[] tab = this.table;
      if (key != null) {
         int hash = System.identityHashCode(key);
         int index = (hash & Integer.MAX_VALUE) % tab.length;

         for(Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && key == e.key) {
               return e.value;
            }
         }
      } else {
         for(Entry e = tab[0]; e != null; e = e.next) {
            if (e.key == null) {
               return e.value;
            }
         }
      }

      return null;
   }

   private void rehash() {
      int oldCapacity = this.table.length;
      Entry[] oldMap = this.table;
      int newCapacity = oldCapacity * 2 + 1;
      Entry[] newMap = new Entry[newCapacity];
      ++this.modCount;
      this.threshold = (int)((float)newCapacity * this.loadFactor);
      this.table = newMap;
      int i = oldCapacity;

      Entry e;
      int index;
      while(i-- > 0) {
         for(Entry old = oldMap[i]; old != null; newMap[index] = e) {
            e = old;
            old = old.next;
            index = (e.hash & Integer.MAX_VALUE) % newCapacity;
            e.next = newMap[index];
         }
      }

   }

   public Object put(Object key, Object value) {
      Entry[] tab = this.table;
      int hash = 0;
      int index = 0;
      Entry e;
      Object old;
      if (key != null) {
         hash = System.identityHashCode(key);
         index = (hash & Integer.MAX_VALUE) % tab.length;

         for(e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && key == e.key) {
               old = e.value;
               e.value = value;
               return old;
            }
         }
      } else {
         for(e = tab[0]; e != null; e = e.next) {
            if (e.key == null) {
               old = e.value;
               e.value = value;
               return old;
            }
         }
      }

      ++this.modCount;
      if (this.count >= this.threshold) {
         this.rehash();
         tab = this.table;
         index = (hash & Integer.MAX_VALUE) % tab.length;
      }

      e = new Entry(hash, key, value, tab[index]);
      tab[index] = e;
      ++this.count;
      return null;
   }

   public Object remove(Object key) {
      Entry[] tab = this.table;
      if (key != null) {
         int hash = System.identityHashCode(key);
         int index = (hash & Integer.MAX_VALUE) % tab.length;
         Entry e = tab[index];

         for(Entry prev = null; e != null; e = e.next) {
            if (e.hash == hash && key == e.key) {
               ++this.modCount;
               if (prev != null) {
                  prev.next = e.next;
               } else {
                  tab[index] = e.next;
               }

               --this.count;
               Object oldValue = e.value;
               e.value = null;
               return oldValue;
            }

            prev = e;
         }
      } else {
         Entry e = tab[0];

         for(Entry prev = null; e != null; e = e.next) {
            if (e.key == null) {
               ++this.modCount;
               if (prev != null) {
                  prev.next = e.next;
               } else {
                  tab[0] = e.next;
               }

               --this.count;
               Object oldValue = e.value;
               e.value = null;
               return oldValue;
            }

            prev = e;
         }
      }

      return null;
   }

   public void putAll(Map t) {
      Iterator i = t.entrySet().iterator();

      while(i.hasNext()) {
         Map.Entry e = (Map.Entry)i.next();
         this.put(e.getKey(), e.getValue());
      }

   }

   public void clear() {
      Entry[] tab = this.table;
      ++this.modCount;
      int index = tab.length;

      while(true) {
         --index;
         if (index < 0) {
            this.count = 0;
            return;
         }

         tab[index] = null;
      }
   }

   public Object clone() {
      try {
         IdentityHashMap t = (IdentityHashMap)super.clone();
         t.table = new Entry[this.table.length];

         for(int i = this.table.length; i-- > 0; t.table[i] = this.table[i] != null ? (Entry)this.table[i].clone() : null) {
         }

         t.keySet = null;
         t.entrySet = null;
         t.values = null;
         t.modCount = 0;
         return t;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }
   }

   public Set keySet() {
      if (this.keySet == null) {
         this.keySet = new AbstractSet() {
            public Iterator iterator() {
               return IdentityHashMap.this.getHashIterator(0);
            }

            public int size() {
               return IdentityHashMap.this.count;
            }

            public boolean contains(Object o) {
               return IdentityHashMap.this.containsKey(o);
            }

            public boolean remove(Object o) {
               int oldSize = IdentityHashMap.this.count;
               IdentityHashMap.this.remove(o);
               return IdentityHashMap.this.count != oldSize;
            }

            public void clear() {
               IdentityHashMap.this.clear();
            }
         };
      }

      return this.keySet;
   }

   public Collection values() {
      if (this.values == null) {
         this.values = new AbstractCollection() {
            public Iterator iterator() {
               return IdentityHashMap.this.getHashIterator(1);
            }

            public int size() {
               return IdentityHashMap.this.count;
            }

            public boolean contains(Object o) {
               return IdentityHashMap.this.containsValue(o);
            }

            public void clear() {
               IdentityHashMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public Set entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new AbstractSet() {
            public Iterator iterator() {
               return IdentityHashMap.this.getHashIterator(2);
            }

            public boolean contains(Object o) {
               if (!(o instanceof Map.Entry)) {
                  return false;
               } else {
                  Map.Entry entry = (Map.Entry)o;
                  Object key = entry.getKey();
                  Entry[] tab = IdentityHashMap.this.table;
                  int hash = key == null ? 0 : System.identityHashCode(key);
                  int index = (hash & Integer.MAX_VALUE) % tab.length;

                  for(Entry e = tab[index]; e != null; e = e.next) {
                     if (e.hash == hash && e.equals(entry)) {
                        return true;
                     }
                  }

                  return false;
               }
            }

            public boolean remove(Object o) {
               if (!(o instanceof Map.Entry)) {
                  return false;
               } else {
                  Map.Entry entry = (Map.Entry)o;
                  Object key = entry.getKey();
                  Entry[] tab = IdentityHashMap.this.table;
                  int hash = key == null ? 0 : System.identityHashCode(key);
                  int index = (hash & Integer.MAX_VALUE) % tab.length;
                  Entry e = tab[index];

                  for(Entry prev = null; e != null; e = e.next) {
                     if (e.hash == hash && e.equals(entry)) {
                        IdentityHashMap.this.modCount++;
                        if (prev != null) {
                           prev.next = e.next;
                        } else {
                           tab[index] = e.next;
                        }

                        IdentityHashMap.this.count--;
                        e.value = null;
                        return true;
                     }

                     prev = e;
                  }

                  return false;
               }
            }

            public int size() {
               return IdentityHashMap.this.count;
            }

            public void clear() {
               IdentityHashMap.this.clear();
            }
         };
      }

      return this.entrySet;
   }

   private Iterator getHashIterator(int type) {
      return (Iterator)(this.count == 0 ? emptyHashIterator : new HashIterator(type));
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      s.writeInt(this.table.length);
      s.writeInt(this.count);

      for(int index = this.table.length - 1; index >= 0; --index) {
         for(Entry entry = this.table[index]; entry != null; entry = entry.next) {
            s.writeObject(entry.key);
            s.writeObject(entry.value);
         }
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      int numBuckets = s.readInt();
      this.table = new Entry[numBuckets];
      int size = s.readInt();

      for(int i = 0; i < size; ++i) {
         Object key = s.readObject();
         Object value = s.readObject();
         this.put(key, value);
      }

   }

   int capacity() {
      return this.table.length;
   }

   float loadFactor() {
      return this.loadFactor;
   }

   private class HashIterator implements Iterator {
      Entry[] table;
      int index;
      Entry entry;
      Entry lastReturned;
      int type;
      private int expectedModCount;

      HashIterator(int type) {
         this.table = IdentityHashMap.this.table;
         this.index = this.table.length;
         this.entry = null;
         this.lastReturned = null;
         this.expectedModCount = IdentityHashMap.this.modCount;
         this.type = type;
      }

      public boolean hasNext() {
         Entry e = this.entry;
         int i = this.index;

         for(Entry[] t = this.table; e == null && i > 0; e = t[i]) {
            --i;
         }

         this.entry = e;
         this.index = i;
         return e != null;
      }

      public Object next() {
         if (IdentityHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            Entry et = this.entry;
            int i = this.index;

            for(Entry[] t = this.table; et == null && i > 0; et = t[i]) {
               --i;
            }

            this.entry = et;
            this.index = i;
            if (et != null) {
               Entry e = this.lastReturned = this.entry;
               this.entry = e.next;
               return this.type == 0 ? e.key : (this.type == 1 ? e.value : e);
            } else {
               throw new NoSuchElementException();
            }
         }
      }

      public void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else if (IdentityHashMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            Entry[] tab = IdentityHashMap.this.table;
            int index = (this.lastReturned.hash & Integer.MAX_VALUE) % tab.length;
            Entry e = tab[index];

            for(Entry prev = null; e != null; e = e.next) {
               if (e == this.lastReturned) {
                  IdentityHashMap.this.modCount++;
                  ++this.expectedModCount;
                  if (prev == null) {
                     tab[index] = e.next;
                  } else {
                     prev.next = e.next;
                  }

                  IdentityHashMap.this.count--;
                  this.lastReturned = null;
                  return;
               }

               prev = e;
            }

            throw new ConcurrentModificationException();
         }
      }
   }

   private static class EmptyHashIterator implements Iterator {
      EmptyHashIterator() {
      }

      public boolean hasNext() {
         return false;
      }

      public Object next() {
         throw new NoSuchElementException();
      }

      public void remove() {
         throw new IllegalStateException();
      }
   }

   private static class Entry implements Map.Entry {
      int hash;
      Object key;
      Object value;
      Entry next;

      Entry(int hash, Object key, Object value, Entry next) {
         this.hash = hash;
         this.key = key;
         this.value = value;
         this.next = next;
      }

      protected Object clone() {
         return new Entry(this.hash, this.key, this.value, this.next == null ? null : (Entry)this.next.clone());
      }

      public Object getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }

      public Object setValue(Object value) {
         Object oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public boolean equals(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            boolean var10000;
            label31: {
               Map.Entry e = (Map.Entry)o;
               if (this.key == e.getKey()) {
                  if (this.value == null) {
                     if (e.getValue() == null) {
                        break label31;
                     }
                  } else if (this.value.equals(e.getValue())) {
                     break label31;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public int hashCode() {
         return this.hash ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "=" + this.value;
      }
   }
}
