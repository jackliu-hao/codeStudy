package org.noear.solon.ext;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {
   private final LinkedHashMap<String, V> _m;
   private final HashMap<String, String> _k;
   private final Locale locale;

   public LinkedCaseInsensitiveMap() {
      this(16, (Locale)null);
   }

   public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
      this._m = new LinkedHashMap<String, V>(initialCapacity) {
         public boolean containsKey(Object key) {
            return LinkedCaseInsensitiveMap.this.containsKey(key);
         }

         protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
            boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
            if (doRemove) {
               LinkedCaseInsensitiveMap.this._k.remove(LinkedCaseInsensitiveMap.this.convertKey((String)eldest.getKey()));
            }

            return doRemove;
         }
      };
      this._k = new HashMap(initialCapacity);
      this.locale = locale != null ? locale : Locale.getDefault();
   }

   private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
      this._m = (LinkedHashMap)other._m.clone();
      this._k = (HashMap)other._k.clone();
      this.locale = other.locale;
   }

   public int size() {
      return this._m.size();
   }

   public boolean isEmpty() {
      return this._m.isEmpty();
   }

   public boolean containsKey(Object key) {
      return key instanceof String && this._k.containsKey(this.convertKey((String)key));
   }

   public boolean containsValue(Object value) {
      return this._m.containsValue(value);
   }

   public V get(Object key) {
      if (key instanceof String) {
         String key2 = (String)this._k.get(this.convertKey((String)key));
         if (key2 != null) {
            return this._m.get(key2);
         }
      }

      return null;
   }

   public V getOrDefault(Object key, V defaultValue) {
      if (key instanceof String) {
         String key2 = (String)this._k.get(this.convertKey((String)key));
         if (key2 != null) {
            return this._m.get(key2);
         }
      }

      return defaultValue;
   }

   public V put(String key, V value) {
      String oldKey = (String)this._k.put(this.convertKey(key), key);
      if (oldKey != null && !oldKey.equals(key)) {
         this._m.remove(oldKey);
      }

      return this._m.put(key, value);
   }

   public void putAll(Map<? extends String, ? extends V> map) {
      if (!map.isEmpty()) {
         map.forEach(this::put);
      }
   }

   public V remove(Object key) {
      if (key instanceof String) {
         String key2 = (String)this._k.remove(this.convertKey((String)key));
         if (key2 != null) {
            return this._m.remove(key2);
         }
      }

      return null;
   }

   public void clear() {
      this._k.clear();
      this._m.clear();
   }

   public Set<String> keySet() {
      return this._m.keySet();
   }

   public Collection<V> values() {
      return this._m.values();
   }

   public Set<Map.Entry<String, V>> entrySet() {
      return this._m.entrySet();
   }

   public LinkedCaseInsensitiveMap<V> clone() {
      return new LinkedCaseInsensitiveMap(this);
   }

   public boolean equals(Object obj) {
      return this._m.equals(obj);
   }

   public int hashCode() {
      return this._m.hashCode();
   }

   public String toString() {
      return this._m.toString();
   }

   public Locale getLocale() {
      return this.locale;
   }

   protected String convertKey(String key) {
      return key.toLowerCase(this.getLocale());
   }

   protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
      return false;
   }
}
