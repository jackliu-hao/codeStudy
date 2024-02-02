/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public final class MapFieldLite<K, V>
/*     */   extends LinkedHashMap<K, V>
/*     */ {
/*     */   private boolean isMutable;
/*     */   
/*     */   private MapFieldLite() {
/*  52 */     this.isMutable = true;
/*     */   }
/*     */   
/*     */   private MapFieldLite(Map<K, V> mapData) {
/*  56 */     super(mapData);
/*  57 */     this.isMutable = true;
/*     */   }
/*     */ 
/*     */   
/*  61 */   private static final MapFieldLite EMPTY_MAP_FIELD = new MapFieldLite();
/*     */   
/*     */   static {
/*  64 */     EMPTY_MAP_FIELD.makeImmutable();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, V> MapFieldLite<K, V> emptyMapField() {
/*  70 */     return EMPTY_MAP_FIELD;
/*     */   }
/*     */   
/*     */   public void mergeFrom(MapFieldLite<K, V> other) {
/*  74 */     ensureMutable();
/*  75 */     if (!other.isEmpty()) {
/*  76 */       putAll(other);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/*  83 */     return isEmpty() ? Collections.<Map.Entry<K, V>>emptySet() : super.entrySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  88 */     ensureMutable();
/*  89 */     super.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public V put(K key, V value) {
/*  94 */     ensureMutable();
/*  95 */     Internal.checkNotNull(key);
/*     */     
/*  97 */     Internal.checkNotNull(value);
/*  98 */     return super.put(key, value);
/*     */   }
/*     */   
/*     */   public V put(Map.Entry<K, V> entry) {
/* 102 */     return put(entry.getKey(), entry.getValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends K, ? extends V> m) {
/* 107 */     ensureMutable();
/* 108 */     checkForNullKeysAndValues(m);
/* 109 */     super.putAll(m);
/*     */   }
/*     */ 
/*     */   
/*     */   public V remove(Object key) {
/* 114 */     ensureMutable();
/* 115 */     return super.remove(key);
/*     */   }
/*     */   
/*     */   private static void checkForNullKeysAndValues(Map<?, ?> m) {
/* 119 */     for (Object key : m.keySet()) {
/* 120 */       Internal.checkNotNull(key);
/* 121 */       Internal.checkNotNull(m.get(key));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean equals(Object a, Object b) {
/* 126 */     if (a instanceof byte[] && b instanceof byte[]) {
/* 127 */       return Arrays.equals((byte[])a, (byte[])b);
/*     */     }
/* 129 */     return a.equals(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <K, V> boolean equals(Map<K, V> a, Map<K, V> b) {
/* 137 */     if (a == b) {
/* 138 */       return true;
/*     */     }
/* 140 */     if (a.size() != b.size()) {
/* 141 */       return false;
/*     */     }
/* 143 */     for (Map.Entry<K, V> entry : a.entrySet()) {
/* 144 */       if (!b.containsKey(entry.getKey())) {
/* 145 */         return false;
/*     */       }
/* 147 */       if (!equals(entry.getValue(), b.get(entry.getKey()))) {
/* 148 */         return false;
/*     */       }
/*     */     } 
/* 151 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 158 */     return (object instanceof Map && equals(this, (Map<K, V>)object));
/*     */   }
/*     */   
/*     */   private static int calculateHashCodeForObject(Object a) {
/* 162 */     if (a instanceof byte[]) {
/* 163 */       return Internal.hashCode((byte[])a);
/*     */     }
/*     */     
/* 166 */     if (a instanceof Internal.EnumLite) {
/* 167 */       throw new UnsupportedOperationException();
/*     */     }
/* 169 */     return a.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <K, V> int calculateHashCodeForMap(Map<K, V> a) {
/* 177 */     int result = 0;
/* 178 */     for (Map.Entry<K, V> entry : a.entrySet()) {
/* 179 */       result += 
/* 180 */         calculateHashCodeForObject(entry.getKey()) ^ calculateHashCodeForObject(entry.getValue());
/*     */     }
/* 182 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 187 */     return calculateHashCodeForMap(this);
/*     */   }
/*     */   
/*     */   private static Object copy(Object object) {
/* 191 */     if (object instanceof byte[]) {
/* 192 */       byte[] data = (byte[])object;
/* 193 */       return Arrays.copyOf(data, data.length);
/*     */     } 
/* 195 */     return object;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <K, V> Map<K, V> copy(Map<K, V> map) {
/* 205 */     Map<K, V> result = new LinkedHashMap<>();
/* 206 */     for (Map.Entry<K, V> entry : map.entrySet()) {
/* 207 */       result.put(entry.getKey(), (V)copy(entry.getValue()));
/*     */     }
/* 209 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public MapFieldLite<K, V> mutableCopy() {
/* 214 */     return isEmpty() ? new MapFieldLite() : new MapFieldLite(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void makeImmutable() {
/* 222 */     this.isMutable = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMutable() {
/* 227 */     return this.isMutable;
/*     */   }
/*     */   
/*     */   private void ensureMutable() {
/* 231 */     if (!isMutable())
/* 232 */       throw new UnsupportedOperationException(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapFieldLite.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */