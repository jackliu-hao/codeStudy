package io.undertow.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class CopyOnWriteMap<K, V> implements ConcurrentMap<K, V> {
   private volatile Map<K, V> delegate = Collections.emptyMap();

   public CopyOnWriteMap() {
   }

   public CopyOnWriteMap(Map<K, V> existing) {
      this.delegate = new HashMap(existing);
   }

   public synchronized V putIfAbsent(K key, V value) {
      Map<K, V> delegate = this.delegate;
      V existing = delegate.get(key);
      if (existing != null) {
         return existing;
      } else {
         this.putInternal(key, value);
         return null;
      }
   }

   public synchronized boolean remove(Object key, Object value) {
      Map<K, V> delegate = this.delegate;
      V existing = delegate.get(key);
      if (existing.equals(value)) {
         this.removeInternal(key);
         return true;
      } else {
         return false;
      }
   }

   public synchronized boolean replace(K key, V oldValue, V newValue) {
      Map<K, V> delegate = this.delegate;
      V existing = delegate.get(key);
      if (existing.equals(oldValue)) {
         this.putInternal(key, newValue);
         return true;
      } else {
         return false;
      }
   }

   public synchronized V replace(K key, V value) {
      Map<K, V> delegate = this.delegate;
      V existing = delegate.get(key);
      if (existing != null) {
         this.putInternal(key, value);
         return existing;
      } else {
         return null;
      }
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
   }

   public V get(Object key) {
      return this.delegate.get(key);
   }

   public synchronized V put(K key, V value) {
      return this.putInternal(key, value);
   }

   public synchronized V remove(Object key) {
      return this.removeInternal(key);
   }

   public synchronized void putAll(Map<? extends K, ? extends V> m) {
      Map<K, V> delegate = new HashMap(this.delegate);
      Iterator var3 = m.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<? extends K, ? extends V> e = (Map.Entry)var3.next();
         delegate.put(e.getKey(), e.getValue());
      }

      this.delegate = delegate;
   }

   public synchronized void clear() {
      this.delegate = Collections.emptyMap();
   }

   public Set<K> keySet() {
      return this.delegate.keySet();
   }

   public Collection<V> values() {
      return this.delegate.values();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return this.delegate.entrySet();
   }

   private V putInternal(K key, V value) {
      Map<K, V> delegate = new HashMap(this.delegate);
      V existing = delegate.put(key, value);
      this.delegate = delegate;
      return existing;
   }

   public V removeInternal(Object key) {
      Map<K, V> delegate = new HashMap(this.delegate);
      V existing = delegate.remove(key);
      this.delegate = delegate;
      return existing;
   }
}
