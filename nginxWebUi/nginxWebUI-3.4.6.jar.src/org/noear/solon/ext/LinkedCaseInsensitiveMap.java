/*     */ package org.noear.solon.ext;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LinkedCaseInsensitiveMap<V>
/*     */   implements Map<String, V>, Serializable, Cloneable
/*     */ {
/*     */   private final LinkedHashMap<String, V> _m;
/*     */   private final HashMap<String, String> _k;
/*     */   private final Locale locale;
/*     */   
/*     */   public LinkedCaseInsensitiveMap() {
/*  23 */     this(16, null);
/*     */   }
/*     */   
/*     */   public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
/*  27 */     this._m = new LinkedHashMap<String, V>(initialCapacity)
/*     */       {
/*     */         public boolean containsKey(Object key) {
/*  30 */           return LinkedCaseInsensitiveMap.this.containsKey(key);
/*     */         }
/*     */ 
/*     */         
/*     */         protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
/*  35 */           boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
/*  36 */           if (doRemove) {
/*  37 */             LinkedCaseInsensitiveMap.this._k.remove(LinkedCaseInsensitiveMap.this.convertKey(eldest.getKey()));
/*     */           }
/*  39 */           return doRemove;
/*     */         }
/*     */       };
/*  42 */     this._k = new HashMap<>(initialCapacity);
/*  43 */     this.locale = (locale != null) ? locale : Locale.getDefault();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
/*  51 */     this._m = (LinkedHashMap<String, V>)other._m.clone();
/*  52 */     this._k = (HashMap<String, String>)other._k.clone();
/*  53 */     this.locale = other.locale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  61 */     return this._m.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  66 */     return this._m.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  71 */     return (key instanceof String && this._k.containsKey(convertKey((String)key)));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/*  76 */     return this._m.containsValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public V get(Object key) {
/*  81 */     if (key instanceof String) {
/*  82 */       String key2 = this._k.get(convertKey((String)key));
/*  83 */       if (key2 != null) {
/*  84 */         return this._m.get(key2);
/*     */       }
/*     */     } 
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public V getOrDefault(Object key, V defaultValue) {
/*  92 */     if (key instanceof String) {
/*  93 */       String key2 = this._k.get(convertKey((String)key));
/*  94 */       if (key2 != null) {
/*  95 */         return this._m.get(key2);
/*     */       }
/*     */     } 
/*  98 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(String key, V value) {
/* 103 */     String oldKey = this._k.put(convertKey(key), key);
/* 104 */     if (oldKey != null && !oldKey.equals(key)) {
/* 105 */       this._m.remove(oldKey);
/*     */     }
/* 107 */     return this._m.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends V> map) {
/* 112 */     if (map.isEmpty()) {
/*     */       return;
/*     */     }
/* 115 */     map.forEach(this::put);
/*     */   }
/*     */ 
/*     */   
/*     */   public V remove(Object key) {
/* 120 */     if (key instanceof String) {
/* 121 */       String key2 = this._k.remove(convertKey((String)key));
/* 122 */       if (key2 != null) {
/* 123 */         return this._m.remove(key2);
/*     */       }
/*     */     } 
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 131 */     this._k.clear();
/* 132 */     this._m.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 137 */     return this._m.keySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<V> values() {
/* 142 */     return this._m.values();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, V>> entrySet() {
/* 147 */     return this._m.entrySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public LinkedCaseInsensitiveMap<V> clone() {
/* 152 */     return new LinkedCaseInsensitiveMap(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 157 */     return this._m.equals(obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 162 */     return this._m.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 167 */     return this._m.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 175 */     return this.locale;
/*     */   }
/*     */   
/*     */   protected String convertKey(String key) {
/* 179 */     return key.toLowerCase(getLocale());
/*     */   }
/*     */   
/*     */   protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
/* 183 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\ext\LinkedCaseInsensitiveMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */