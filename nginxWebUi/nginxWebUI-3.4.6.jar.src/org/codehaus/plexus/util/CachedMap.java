/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CachedMap
/*     */   implements Map
/*     */ {
/*     */   private final FastMap _backingFastMap;
/*     */   private final Map _backingMap;
/*     */   private final FastMap _keysMap;
/*     */   private final int _mask;
/*     */   private final Object[] _keys;
/*     */   private final Object[] _values;
/*     */   
/*     */   public CachedMap() {
/*  82 */     this(256, new FastMap());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CachedMap(int cacheSize) {
/*  94 */     this(cacheSize, new FastMap(cacheSize));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CachedMap(int cacheSize, Map backingMap) {
/* 110 */     int actualCacheSize = 1;
/* 111 */     while (actualCacheSize < cacheSize) {
/* 112 */       actualCacheSize <<= 1;
/*     */     }
/*     */ 
/*     */     
/* 116 */     this._keys = new Object[actualCacheSize];
/* 117 */     this._values = new Object[actualCacheSize];
/* 118 */     this._mask = actualCacheSize - 1;
/*     */ 
/*     */     
/* 121 */     if (backingMap instanceof FastMap) {
/* 122 */       this._backingFastMap = (FastMap)backingMap;
/* 123 */       this._backingMap = this._backingFastMap;
/* 124 */       this._keysMap = null;
/*     */     } else {
/* 126 */       this._backingFastMap = null;
/* 127 */       this._backingMap = backingMap;
/* 128 */       this._keysMap = new FastMap(backingMap.size());
/* 129 */       for (Iterator i = backingMap.keySet().iterator(); i.hasNext(); ) {
/* 130 */         Object key = i.next();
/* 131 */         this._keysMap.put(key, key);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCacheSize() {
/* 142 */     return this._keys.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map getBackingMap() {
/* 153 */     return (this._backingFastMap != null) ? this._backingFastMap : this._backingMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() {
/* 161 */     for (int i = 0; i < this._keys.length; i++) {
/* 162 */       this._keys[i] = null;
/* 163 */       this._values[i] = null;
/*     */     } 
/*     */     
/* 166 */     if (this._keysMap != null)
/*     */     {
/* 168 */       for (Iterator iterator = this._backingMap.keySet().iterator(); iterator.hasNext(); ) {
/* 169 */         Object key = iterator.next();
/* 170 */         this._keysMap.put(key, key);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(Object key) {
/* 189 */     int index = key.hashCode() & this._mask;
/* 190 */     return key.equals(this._keys[index]) ? this._values[index] : getCacheMissed(key, index);
/*     */   }
/*     */   
/*     */   private Object getCacheMissed(Object key, int index) {
/* 194 */     if (this._backingFastMap != null) {
/* 195 */       Map.Entry entry = this._backingFastMap.getEntry(key);
/* 196 */       if (entry != null) {
/* 197 */         this._keys[index] = entry.getKey();
/* 198 */         Object value = entry.getValue();
/* 199 */         this._values[index] = value;
/* 200 */         return value;
/*     */       } 
/* 202 */       return null;
/*     */     } 
/*     */     
/* 205 */     Object mapKey = this._keysMap.get(key);
/* 206 */     if (mapKey != null) {
/* 207 */       this._keys[index] = mapKey;
/* 208 */       Object value = this._backingMap.get(key);
/* 209 */       this._values[index] = value;
/* 210 */       return value;
/*     */     } 
/* 212 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object put(Object key, Object value) {
/* 234 */     int index = key.hashCode() & this._mask;
/* 235 */     if (key.equals(this._keys[index])) {
/* 236 */       this._values[index] = value;
/* 237 */     } else if (this._keysMap != null) {
/* 238 */       this._keysMap.put(key, key);
/*     */     } 
/*     */ 
/*     */     
/* 242 */     return this._backingMap.put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object remove(Object key) {
/* 259 */     int index = key.hashCode() & this._mask;
/* 260 */     if (key.equals(this._keys[index])) {
/* 261 */       this._keys[index] = null;
/*     */     }
/*     */     
/* 264 */     if (this._keysMap != null) {
/* 265 */       this._keysMap.remove(key);
/*     */     }
/*     */     
/* 268 */     return this._backingMap.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 280 */     int index = key.hashCode() & this._mask;
/* 281 */     if (key.equals(this._keys[index])) {
/* 282 */       return true;
/*     */     }
/* 284 */     return this._backingMap.containsKey(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 296 */     return this._backingMap.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 305 */     return this._backingMap.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 321 */     return this._backingMap.containsValue(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putAll(Map map) {
/* 341 */     this._backingMap.putAll(map);
/* 342 */     flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 353 */     this._backingMap.clear();
/* 354 */     flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set keySet() {
/* 364 */     return Collections.unmodifiableSet(this._backingMap.keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection values() {
/* 373 */     return Collections.unmodifiableCollection(this._backingMap.values());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set entrySet() {
/* 383 */     return Collections.unmodifiableSet(this._backingMap.entrySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 396 */     return this._backingMap.equals(o);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 405 */     return this._backingMap.hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\CachedMap.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */