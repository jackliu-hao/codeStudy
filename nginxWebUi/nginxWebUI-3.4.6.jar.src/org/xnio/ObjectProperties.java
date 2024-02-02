/*     */ package org.xnio;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
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
/*     */ final class ObjectProperties
/*     */   extends Hashtable<String, String>
/*     */ {
/*     */   private static final long serialVersionUID = -4691081844415343670L;
/*     */   private final Map<String, String> realMap;
/*     */   
/*     */   public static Property property(String key, String value) {
/*  41 */     return new Property(key, value);
/*     */   }
/*     */   
/*     */   public static ObjectProperties properties(Property... properties) {
/*  45 */     return new ObjectProperties(properties);
/*     */   }
/*     */   
/*     */   public ObjectProperties(int initialCapacity, float loadFactor) {
/*  49 */     this.realMap = new LinkedHashMap<>(initialCapacity, loadFactor);
/*     */   }
/*     */   
/*     */   public ObjectProperties(int initialCapacity) {
/*  53 */     this.realMap = new LinkedHashMap<>(initialCapacity);
/*     */   }
/*     */   
/*     */   public ObjectProperties() {
/*  57 */     this.realMap = new LinkedHashMap<>();
/*     */   }
/*     */   
/*     */   public ObjectProperties(Map<? extends String, ? extends String> t) {
/*  61 */     this.realMap = new LinkedHashMap<>(t);
/*     */   }
/*     */   
/*     */   public ObjectProperties(Property... properties) {
/*  65 */     this.realMap = new LinkedHashMap<>(properties.length);
/*  66 */     for (Property property : properties) {
/*  67 */       this.realMap.put(property.getKey(), property.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public int size() {
/*  72 */     return this.realMap.size();
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  76 */     return this.realMap.isEmpty();
/*     */   }
/*     */   
/*     */   public Enumeration<String> keys() {
/*  80 */     return Collections.enumeration(this.realMap.keySet());
/*     */   }
/*     */   
/*     */   public Enumeration<String> elements() {
/*  84 */     return Collections.enumeration(this.realMap.values());
/*     */   }
/*     */   
/*     */   public boolean contains(Object value) {
/*  88 */     return this.realMap.containsValue(value);
/*     */   }
/*     */   
/*     */   public boolean containsValue(Object value) {
/*  92 */     return this.realMap.containsValue(value);
/*     */   }
/*     */   
/*     */   public boolean containsKey(Object key) {
/*  96 */     return this.realMap.containsKey(key);
/*     */   }
/*     */   
/*     */   public String get(Object key) {
/* 100 */     return this.realMap.get(key);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void rehash() {}
/*     */   
/*     */   public String put(String key, String value) {
/* 107 */     return this.realMap.put(key, value);
/*     */   }
/*     */   
/*     */   public String remove(Object key) {
/* 111 */     return this.realMap.remove(key);
/*     */   }
/*     */   
/*     */   public void putAll(Map<? extends String, ? extends String> t) {
/* 115 */     this.realMap.putAll(t);
/*     */   }
/*     */   
/*     */   public void clear() {
/* 119 */     this.realMap.clear();
/*     */   }
/*     */   
/*     */   public Object clone() {
/* 123 */     return super.clone();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 127 */     return this.realMap.toString();
/*     */   }
/*     */   
/*     */   public Set<String> keySet() {
/* 131 */     return this.realMap.keySet();
/*     */   }
/*     */   
/*     */   public Set<Map.Entry<String, String>> entrySet() {
/* 135 */     return this.realMap.entrySet();
/*     */   }
/*     */   
/*     */   public Collection<String> values() {
/* 139 */     return this.realMap.values();
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Property
/*     */   {
/*     */     private final String key;
/*     */     
/*     */     private final String value;
/*     */     
/*     */     public Property(String key, String value) {
/* 150 */       if (key == null) {
/* 151 */         throw new IllegalArgumentException("key is null");
/*     */       }
/* 153 */       if (value == null) {
/* 154 */         throw new IllegalArgumentException("value is null");
/*     */       }
/* 156 */       this.key = key;
/* 157 */       this.value = value;
/*     */     }
/*     */     
/*     */     public String getKey() {
/* 161 */       return this.key;
/*     */     }
/*     */     
/*     */     public String getValue() {
/* 165 */       return this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ObjectProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */