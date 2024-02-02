/*     */ package cn.hutool.setting;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
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
/*     */ public class GroupedMap
/*     */   extends LinkedHashMap<String, LinkedHashMap<String, String>>
/*     */ {
/*     */   private static final long serialVersionUID = -7777365130776081931L;
/*  26 */   private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
/*  27 */   private final ReentrantReadWriteLock.ReadLock readLock = this.cacheLock.readLock();
/*  28 */   private final ReentrantReadWriteLock.WriteLock writeLock = this.cacheLock.writeLock();
/*  29 */   private int size = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String group, String key) {
/*  39 */     this.readLock.lock();
/*     */     try {
/*  41 */       LinkedHashMap<String, String> map = get(StrUtil.nullToEmpty(group));
/*  42 */       if (MapUtil.isNotEmpty(map)) {
/*  43 */         return map.get(key);
/*     */       }
/*     */     } finally {
/*  46 */       this.readLock.unlock();
/*     */     } 
/*  48 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public LinkedHashMap<String, String> get(Object key) {
/*  53 */     this.readLock.lock();
/*     */     try {
/*  55 */       return super.get(key);
/*     */     } finally {
/*  57 */       this.readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  68 */     this.writeLock.lock();
/*     */     try {
/*  70 */       if (this.size < 0) {
/*  71 */         this.size = 0;
/*  72 */         for (LinkedHashMap<String, String> value : values()) {
/*  73 */           this.size += value.size();
/*     */         }
/*     */       } 
/*     */     } finally {
/*  77 */       this.writeLock.unlock();
/*     */     } 
/*  79 */     return this.size;
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
/*     */   public String put(String group, String key, String value) {
/*  91 */     group = StrUtil.nullToEmpty(group).trim();
/*  92 */     this.writeLock.lock();
/*     */     try {
/*  94 */       LinkedHashMap<String, String> valueMap = computeIfAbsent(group, k -> new LinkedHashMap<>());
/*  95 */       this.size = -1;
/*  96 */       return valueMap.put(key, value);
/*     */     } finally {
/*  98 */       this.writeLock.unlock();
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
/*     */   public GroupedMap putAll(String group, Map<? extends String, ? extends String> m) {
/* 110 */     for (Map.Entry<? extends String, ? extends String> entry : m.entrySet()) {
/* 111 */       put(group, entry.getKey(), entry.getValue());
/*     */     }
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String remove(String group, String key) {
/* 124 */     group = StrUtil.nullToEmpty(group).trim();
/* 125 */     this.writeLock.lock();
/*     */     try {
/* 127 */       LinkedHashMap<String, String> valueMap = get(group);
/* 128 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 129 */         return valueMap.remove(key);
/*     */       }
/*     */     } finally {
/* 132 */       this.writeLock.unlock();
/*     */     } 
/* 134 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty(String group) {
/* 144 */     group = StrUtil.nullToEmpty(group).trim();
/* 145 */     this.readLock.lock();
/*     */     try {
/* 147 */       LinkedHashMap<String, String> valueMap = get(group);
/* 148 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 149 */         return valueMap.isEmpty();
/*     */       }
/*     */     } finally {
/* 152 */       this.readLock.unlock();
/*     */     } 
/* 154 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 164 */     return (size() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsKey(String group, String key) {
/* 175 */     group = StrUtil.nullToEmpty(group).trim();
/* 176 */     this.readLock.lock();
/*     */     try {
/* 178 */       LinkedHashMap<String, String> valueMap = get(group);
/* 179 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 180 */         return valueMap.containsKey(key);
/*     */       }
/*     */     } finally {
/* 183 */       this.readLock.unlock();
/*     */     } 
/* 185 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsValue(String group, String value) {
/* 196 */     group = StrUtil.nullToEmpty(group).trim();
/* 197 */     this.readLock.lock();
/*     */     try {
/* 199 */       LinkedHashMap<String, String> valueMap = get(group);
/* 200 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 201 */         return valueMap.containsValue(value);
/*     */       }
/*     */     } finally {
/* 204 */       this.readLock.unlock();
/*     */     } 
/* 206 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedMap clear(String group) {
/* 216 */     group = StrUtil.nullToEmpty(group).trim();
/* 217 */     this.writeLock.lock();
/*     */     try {
/* 219 */       LinkedHashMap<String, String> valueMap = get(group);
/* 220 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 221 */         valueMap.clear();
/*     */       }
/*     */     } finally {
/* 224 */       this.writeLock.unlock();
/*     */     } 
/* 226 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 231 */     this.readLock.lock();
/*     */     try {
/* 233 */       return super.keySet();
/*     */     } finally {
/* 235 */       this.readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> keySet(String group) {
/* 246 */     group = StrUtil.nullToEmpty(group).trim();
/* 247 */     this.readLock.lock();
/*     */     try {
/* 249 */       LinkedHashMap<String, String> valueMap = get(group);
/* 250 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 251 */         return valueMap.keySet();
/*     */       }
/*     */     } finally {
/* 254 */       this.readLock.unlock();
/*     */     } 
/* 256 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> values(String group) {
/* 266 */     group = StrUtil.nullToEmpty(group).trim();
/* 267 */     this.readLock.lock();
/*     */     try {
/* 269 */       LinkedHashMap<String, String> valueMap = get(group);
/* 270 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 271 */         return valueMap.values();
/*     */       }
/*     */     } finally {
/* 274 */       this.readLock.unlock();
/*     */     } 
/* 276 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, LinkedHashMap<String, String>>> entrySet() {
/* 281 */     this.readLock.lock();
/*     */     try {
/* 283 */       return super.entrySet();
/*     */     } finally {
/* 285 */       this.readLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, String>> entrySet(String group) {
/* 296 */     group = StrUtil.nullToEmpty(group).trim();
/* 297 */     this.readLock.lock();
/*     */     try {
/* 299 */       LinkedHashMap<String, String> valueMap = get(group);
/* 300 */       if (MapUtil.isNotEmpty(valueMap)) {
/* 301 */         return valueMap.entrySet();
/*     */       }
/*     */     } finally {
/* 304 */       this.readLock.unlock();
/*     */     } 
/* 306 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 311 */     this.readLock.lock();
/*     */     try {
/* 313 */       return super.toString();
/*     */     } finally {
/* 315 */       this.readLock.unlock();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\GroupedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */