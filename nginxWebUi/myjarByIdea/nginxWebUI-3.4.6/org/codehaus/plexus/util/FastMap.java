package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FastMap implements Map, Cloneable, Serializable {
   private transient EntryImpl[] _entries;
   private transient int _capacity;
   private transient int _mask;
   private transient EntryImpl _poolFirst;
   private transient EntryImpl _mapFirst;
   private transient EntryImpl _mapLast;
   private transient int _size;
   private transient Values _values;
   private transient EntrySet _entrySet;
   private transient KeySet _keySet;

   public FastMap() {
      this.initialize(256);
   }

   public FastMap(Map map) {
      int capacity = map instanceof FastMap ? ((FastMap)map).capacity() : map.size();
      this.initialize(capacity);
      this.putAll(map);
   }

   public FastMap(int capacity) {
      this.initialize(capacity);
   }

   public int size() {
      return this._size;
   }

   public int capacity() {
      return this._capacity;
   }

   public boolean isEmpty() {
      return this._size == 0;
   }

   public boolean containsKey(Object key) {
      for(EntryImpl entry = this._entries[keyHash(key) & this._mask]; entry != null; entry = entry._next) {
         if (key.equals(entry._key)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsValue(Object value) {
      for(EntryImpl entry = this._mapFirst; entry != null; entry = entry._after) {
         if (value.equals(entry._value)) {
            return true;
         }
      }

      return false;
   }

   public Object get(Object key) {
      for(EntryImpl entry = this._entries[keyHash(key) & this._mask]; entry != null; entry = entry._next) {
         if (key.equals(entry._key)) {
            return entry._value;
         }
      }

      return null;
   }

   public Map.Entry getEntry(Object key) {
      for(EntryImpl entry = this._entries[keyHash(key) & this._mask]; entry != null; entry = entry._next) {
         if (key.equals(entry._key)) {
            return entry;
         }
      }

      return null;
   }

   public Object put(Object key, Object value) {
      for(EntryImpl entry = this._entries[keyHash(key) & this._mask]; entry != null; entry = entry._next) {
         if (key.equals(entry._key)) {
            Object prevValue = entry._value;
            entry._value = value;
            return prevValue;
         }
      }

      this.addEntry(key, value);
      return null;
   }

   public void putAll(Map map) {
      Iterator i = map.entrySet().iterator();

      while(i.hasNext()) {
         Map.Entry e = (Map.Entry)i.next();
         this.addEntry(e.getKey(), e.getValue());
      }

   }

   public Object remove(Object key) {
      for(EntryImpl entry = this._entries[keyHash(key) & this._mask]; entry != null; entry = entry._next) {
         if (key.equals(entry._key)) {
            Object prevValue = entry._value;
            this.removeEntry(entry);
            return prevValue;
         }
      }

      return null;
   }

   public void clear() {
      for(EntryImpl entry = this._mapFirst; entry != null; entry = entry._after) {
         entry._key = null;
         entry._value = null;
         entry._before = null;
         entry._next = null;
         if (entry._previous == null) {
            this._entries[entry._index] = null;
         } else {
            entry._previous = null;
         }
      }

      if (this._mapLast != null) {
         this._mapLast._after = this._poolFirst;
         this._poolFirst = this._mapFirst;
         this._mapFirst = null;
         this._mapLast = null;
         this._size = 0;
         this.sizeChanged();
      }

   }

   public void setCapacity(int newCapacity) {
      int tableLength;
      EntryImpl entry;
      if (newCapacity > this._capacity) {
         for(tableLength = this._capacity; tableLength < newCapacity; ++tableLength) {
            entry = new EntryImpl();
            entry._after = this._poolFirst;
            this._poolFirst = entry;
         }
      } else if (newCapacity < this._capacity) {
         for(tableLength = newCapacity; tableLength < this._capacity && this._poolFirst != null; ++tableLength) {
            entry = this._poolFirst;
            this._poolFirst = entry._after;
            entry._after = null;
         }
      }

      for(tableLength = 16; tableLength < newCapacity; tableLength <<= 1) {
      }

      if (this._entries.length != tableLength) {
         this._entries = new EntryImpl[tableLength];
         this._mask = tableLength - 1;

         for(entry = this._mapFirst; entry != null; entry = entry._after) {
            int index = keyHash(entry._key) & this._mask;
            entry._index = index;
            entry._previous = null;
            EntryImpl next = this._entries[index];
            entry._next = next;
            if (next != null) {
               next._previous = entry;
            }

            this._entries[index] = entry;
         }
      }

      this._capacity = newCapacity;
   }

   public Object clone() {
      try {
         FastMap clone = (FastMap)super.clone();
         clone.initialize(this._capacity);
         clone.putAll(this);
         return clone;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj instanceof Map) {
         Map that = (Map)obj;
         if (this.size() == that.size()) {
            for(EntryImpl entry = this._mapFirst; entry != null; entry = entry._after) {
               if (!that.entrySet().contains(entry)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int code = 0;

      for(EntryImpl entry = this._mapFirst; entry != null; entry = entry._after) {
         code += entry.hashCode();
      }

      return code;
   }

   public String toString() {
      return this.entrySet().toString();
   }

   public Collection values() {
      return this._values;
   }

   public Set entrySet() {
      return this._entrySet;
   }

   public Set keySet() {
      return this._keySet;
   }

   protected void sizeChanged() {
      if (this.size() > this.capacity()) {
         this.setCapacity(this.capacity() * 2);
      }

   }

   private static int keyHash(Object key) {
      int hashCode = key.hashCode();
      hashCode += ~(hashCode << 9);
      hashCode ^= hashCode >>> 14;
      hashCode += hashCode << 4;
      hashCode ^= hashCode >>> 10;
      return hashCode;
   }

   private void addEntry(Object key, Object value) {
      EntryImpl entry = this._poolFirst;
      if (entry != null) {
         this._poolFirst = entry._after;
         entry._after = null;
      } else {
         entry = new EntryImpl();
      }

      entry._key = key;
      entry._value = value;
      int index = keyHash(key) & this._mask;
      entry._index = index;
      EntryImpl next = this._entries[index];
      entry._next = next;
      if (next != null) {
         next._previous = entry;
      }

      this._entries[index] = entry;
      if (this._mapLast != null) {
         entry._before = this._mapLast;
         this._mapLast._after = entry;
      } else {
         this._mapFirst = entry;
      }

      this._mapLast = entry;
      ++this._size;
      this.sizeChanged();
   }

   private void removeEntry(EntryImpl entry) {
      EntryImpl previous = entry._previous;
      EntryImpl next = entry._next;
      if (previous != null) {
         previous._next = next;
         entry._previous = null;
      } else {
         this._entries[entry._index] = next;
      }

      if (next != null) {
         next._previous = previous;
         entry._next = null;
      }

      EntryImpl before = entry._before;
      EntryImpl after = entry._after;
      if (before != null) {
         before._after = after;
         entry._before = null;
      } else {
         this._mapFirst = after;
      }

      if (after != null) {
         after._before = before;
      } else {
         this._mapLast = before;
      }

      entry._key = null;
      entry._value = null;
      entry._after = this._poolFirst;
      this._poolFirst = entry;
      --this._size;
      this.sizeChanged();
   }

   private void initialize(int capacity) {
      int tableLength;
      for(tableLength = 16; tableLength < capacity; tableLength <<= 1) {
      }

      this._entries = new EntryImpl[tableLength];
      this._mask = tableLength - 1;
      this._capacity = capacity;
      this._size = 0;
      this._values = new Values();
      this._entrySet = new EntrySet();
      this._keySet = new KeySet();
      this._poolFirst = null;
      this._mapFirst = null;
      this._mapLast = null;

      for(int i = 0; i < capacity; ++i) {
         EntryImpl entry = new EntryImpl();
         entry._after = this._poolFirst;
         this._poolFirst = entry;
      }

   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      int capacity = stream.readInt();
      this.initialize(capacity);
      int size = stream.readInt();

      for(int i = 0; i < size; ++i) {
         Object key = stream.readObject();
         Object value = stream.readObject();
         this.addEntry(key, value);
      }

   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.writeInt(this._capacity);
      stream.writeInt(this._size);
      int count = 0;

      for(EntryImpl entry = this._mapFirst; entry != null; entry = entry._after) {
         stream.writeObject(entry._key);
         stream.writeObject(entry._value);
         ++count;
      }

      if (count != this._size) {
         throw new IOException("FastMap Corrupted");
      }
   }

   private static final class EntryImpl implements Map.Entry {
      private Object _key;
      private Object _value;
      private int _index;
      private EntryImpl _previous;
      private EntryImpl _next;
      private EntryImpl _before;
      private EntryImpl _after;

      private EntryImpl() {
      }

      public Object getKey() {
         return this._key;
      }

      public Object getValue() {
         return this._value;
      }

      public Object setValue(Object value) {
         Object old = this._value;
         this._value = value;
         return old;
      }

      public boolean equals(Object that) {
         if (!(that instanceof Map.Entry)) {
            return false;
         } else {
            boolean var10000;
            label31: {
               Map.Entry entry = (Map.Entry)that;
               if (this._key.equals(entry.getKey())) {
                  if (this._value != null) {
                     if (this._value.equals(entry.getValue())) {
                        break label31;
                     }
                  } else if (entry.getValue() == null) {
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
         return this._key.hashCode() ^ (this._value != null ? this._value.hashCode() : 0);
      }

      public String toString() {
         return this._key + "=" + this._value;
      }

      // $FF: synthetic method
      EntryImpl(Object x0) {
         this();
      }
   }

   private class KeySet extends AbstractSet {
      private KeySet() {
      }

      public Iterator iterator() {
         return new Iterator() {
            EntryImpl after;
            EntryImpl before;

            {
               this.after = FastMap.this._mapFirst;
            }

            public void remove() {
               FastMap.this.removeEntry(this.before);
            }

            public boolean hasNext() {
               return this.after != null;
            }

            public Object next() {
               this.before = this.after;
               this.after = this.after._after;
               return this.before._key;
            }
         };
      }

      public int size() {
         return FastMap.this._size;
      }

      public boolean contains(Object obj) {
         return FastMap.this.containsKey(obj);
      }

      public boolean remove(Object obj) {
         return FastMap.this.remove(obj) != null;
      }

      public void clear() {
         FastMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object x1) {
         this();
      }
   }

   private class EntrySet extends AbstractSet {
      private EntrySet() {
      }

      public Iterator iterator() {
         return new Iterator() {
            EntryImpl after;
            EntryImpl before;

            {
               this.after = FastMap.this._mapFirst;
            }

            public void remove() {
               FastMap.this.removeEntry(this.before);
            }

            public boolean hasNext() {
               return this.after != null;
            }

            public Object next() {
               this.before = this.after;
               this.after = this.after._after;
               return this.before;
            }
         };
      }

      public int size() {
         return FastMap.this._size;
      }

      public boolean contains(Object obj) {
         if (obj instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry)obj;
            Map.Entry mapEntry = FastMap.this.getEntry(entry.getKey());
            return entry.equals(mapEntry);
         } else {
            return false;
         }
      }

      public boolean remove(Object obj) {
         if (obj instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry)obj;
            EntryImpl mapEntry = (EntryImpl)FastMap.this.getEntry(entry.getKey());
            if (mapEntry != null && entry.getValue().equals(mapEntry._value)) {
               FastMap.this.removeEntry(mapEntry);
               return true;
            }
         }

         return false;
      }

      // $FF: synthetic method
      EntrySet(Object x1) {
         this();
      }
   }

   private class Values extends AbstractCollection {
      private Values() {
      }

      public Iterator iterator() {
         return new Iterator() {
            EntryImpl after;
            EntryImpl before;

            {
               this.after = FastMap.this._mapFirst;
            }

            public void remove() {
               FastMap.this.removeEntry(this.before);
            }

            public boolean hasNext() {
               return this.after != null;
            }

            public Object next() {
               this.before = this.after;
               this.after = this.after._after;
               return this.before._value;
            }
         };
      }

      public int size() {
         return FastMap.this._size;
      }

      public boolean contains(Object o) {
         return FastMap.this.containsValue(o);
      }

      public void clear() {
         FastMap.this.clear();
      }

      // $FF: synthetic method
      Values(Object x1) {
         this();
      }
   }
}
