package org.xnio;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class ObjectProperties extends Hashtable<String, String> {
   private static final long serialVersionUID = -4691081844415343670L;
   private final Map<String, String> realMap;

   public static Property property(String key, String value) {
      return new Property(key, value);
   }

   public static ObjectProperties properties(Property... properties) {
      return new ObjectProperties(properties);
   }

   public ObjectProperties(int initialCapacity, float loadFactor) {
      this.realMap = new LinkedHashMap(initialCapacity, loadFactor);
   }

   public ObjectProperties(int initialCapacity) {
      this.realMap = new LinkedHashMap(initialCapacity);
   }

   public ObjectProperties() {
      this.realMap = new LinkedHashMap();
   }

   public ObjectProperties(Map<? extends String, ? extends String> t) {
      this.realMap = new LinkedHashMap(t);
   }

   public ObjectProperties(Property... properties) {
      this.realMap = new LinkedHashMap(properties.length);
      Property[] var2 = properties;
      int var3 = properties.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Property property = var2[var4];
         this.realMap.put(property.getKey(), property.getValue());
      }

   }

   public int size() {
      return this.realMap.size();
   }

   public boolean isEmpty() {
      return this.realMap.isEmpty();
   }

   public Enumeration<String> keys() {
      return Collections.enumeration(this.realMap.keySet());
   }

   public Enumeration<String> elements() {
      return Collections.enumeration(this.realMap.values());
   }

   public boolean contains(Object value) {
      return this.realMap.containsValue(value);
   }

   public boolean containsValue(Object value) {
      return this.realMap.containsValue(value);
   }

   public boolean containsKey(Object key) {
      return this.realMap.containsKey(key);
   }

   public String get(Object key) {
      return (String)this.realMap.get(key);
   }

   protected void rehash() {
   }

   public String put(String key, String value) {
      return (String)this.realMap.put(key, value);
   }

   public String remove(Object key) {
      return (String)this.realMap.remove(key);
   }

   public void putAll(Map<? extends String, ? extends String> t) {
      this.realMap.putAll(t);
   }

   public void clear() {
      this.realMap.clear();
   }

   public Object clone() {
      return super.clone();
   }

   public String toString() {
      return this.realMap.toString();
   }

   public Set<String> keySet() {
      return this.realMap.keySet();
   }

   public Set<Map.Entry<String, String>> entrySet() {
      return this.realMap.entrySet();
   }

   public Collection<String> values() {
      return this.realMap.values();
   }

   public static final class Property {
      private final String key;
      private final String value;

      public Property(String key, String value) {
         if (key == null) {
            throw new IllegalArgumentException("key is null");
         } else if (value == null) {
            throw new IllegalArgumentException("value is null");
         } else {
            this.key = key;
            this.value = value;
         }
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
