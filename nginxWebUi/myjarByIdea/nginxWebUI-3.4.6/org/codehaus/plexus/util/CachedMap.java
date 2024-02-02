package org.codehaus.plexus.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class CachedMap implements Map {
   private final FastMap _backingFastMap;
   private final Map _backingMap;
   private final FastMap _keysMap;
   private final int _mask;
   private final Object[] _keys;
   private final Object[] _values;

   public CachedMap() {
      this(256, new FastMap());
   }

   public CachedMap(int cacheSize) {
      this(cacheSize, new FastMap(cacheSize));
   }

   public CachedMap(int cacheSize, Map backingMap) {
      int actualCacheSize;
      for(actualCacheSize = 1; actualCacheSize < cacheSize; actualCacheSize <<= 1) {
      }

      this._keys = new Object[actualCacheSize];
      this._values = new Object[actualCacheSize];
      this._mask = actualCacheSize - 1;
      if (backingMap instanceof FastMap) {
         this._backingFastMap = (FastMap)backingMap;
         this._backingMap = this._backingFastMap;
         this._keysMap = null;
      } else {
         this._backingFastMap = null;
         this._backingMap = backingMap;
         this._keysMap = new FastMap(backingMap.size());
         Iterator i = backingMap.keySet().iterator();

         while(i.hasNext()) {
            Object key = i.next();
            this._keysMap.put(key, key);
         }
      }

   }

   public int getCacheSize() {
      return this._keys.length;
   }

   public Map getBackingMap() {
      return (Map)(this._backingFastMap != null ? this._backingFastMap : this._backingMap);
   }

   public void flush() {
      for(int i = 0; i < this._keys.length; ++i) {
         this._keys[i] = null;
         this._values[i] = null;
      }

      if (this._keysMap != null) {
         Iterator i = this._backingMap.keySet().iterator();

         while(i.hasNext()) {
            Object key = i.next();
            this._keysMap.put(key, key);
         }
      }

   }

   public Object get(Object key) {
      int index = key.hashCode() & this._mask;
      return key.equals(this._keys[index]) ? this._values[index] : this.getCacheMissed(key, index);
   }

   private Object getCacheMissed(Object key, int index) {
      Object value;
      if (this._backingFastMap != null) {
         Map.Entry entry = this._backingFastMap.getEntry(key);
         if (entry != null) {
            this._keys[index] = entry.getKey();
            value = entry.getValue();
            this._values[index] = value;
            return value;
         } else {
            return null;
         }
      } else {
         Object mapKey = this._keysMap.get(key);
         if (mapKey != null) {
            this._keys[index] = mapKey;
            value = this._backingMap.get(key);
            this._values[index] = value;
            return value;
         } else {
            return null;
         }
      }
   }

   public Object put(Object key, Object value) {
      int index = key.hashCode() & this._mask;
      if (key.equals(this._keys[index])) {
         this._values[index] = value;
      } else if (this._keysMap != null) {
         this._keysMap.put(key, key);
      }

      return this._backingMap.put(key, value);
   }

   public Object remove(Object key) {
      int index = key.hashCode() & this._mask;
      if (key.equals(this._keys[index])) {
         this._keys[index] = null;
      }

      if (this._keysMap != null) {
         this._keysMap.remove(key);
      }

      return this._backingMap.remove(key);
   }

   public boolean containsKey(Object key) {
      int index = key.hashCode() & this._mask;
      return key.equals(this._keys[index]) ? true : this._backingMap.containsKey(key);
   }

   public int size() {
      return this._backingMap.size();
   }

   public boolean isEmpty() {
      return this._backingMap.isEmpty();
   }

   public boolean containsValue(Object value) {
      return this._backingMap.containsValue(value);
   }

   public void putAll(Map map) {
      this._backingMap.putAll(map);
      this.flush();
   }

   public void clear() {
      this._backingMap.clear();
      this.flush();
   }

   public Set keySet() {
      return Collections.unmodifiableSet(this._backingMap.keySet());
   }

   public Collection values() {
      return Collections.unmodifiableCollection(this._backingMap.values());
   }

   public Set entrySet() {
      return Collections.unmodifiableSet(this._backingMap.entrySet());
   }

   public boolean equals(Object o) {
      return this._backingMap.equals(o);
   }

   public int hashCode() {
      return this._backingMap.hashCode();
   }
}
