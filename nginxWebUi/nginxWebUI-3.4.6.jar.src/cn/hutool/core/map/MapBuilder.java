/*     */ package cn.hutool.core.map;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
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
/*     */ public class MapBuilder<K, V>
/*     */   implements Builder<Map<K, V>>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Map<K, V> map;
/*     */   
/*     */   public static <K, V> MapBuilder<K, V> create() {
/*  30 */     return create(false);
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
/*     */   public static <K, V> MapBuilder<K, V> create(boolean isLinked) {
/*  43 */     return create(MapUtil.newHashMap(isLinked));
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
/*     */   public static <K, V> MapBuilder<K, V> create(Map<K, V> map) {
/*  56 */     return new MapBuilder<>(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapBuilder(Map<K, V> map) {
/*  65 */     this.map = map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapBuilder<K, V> put(K k, V v) {
/*  76 */     this.map.put(k, v);
/*  77 */     return this;
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
/*     */   public MapBuilder<K, V> put(boolean condition, K k, V v) {
/*  90 */     if (condition) {
/*  91 */       put(k, v);
/*     */     }
/*  93 */     return this;
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
/*     */   public MapBuilder<K, V> put(boolean condition, K k, Supplier<V> supplier) {
/* 106 */     if (condition) {
/* 107 */       put(k, supplier.get());
/*     */     }
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapBuilder<K, V> putAll(Map<K, V> map) {
/* 119 */     this.map.putAll(map);
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MapBuilder<K, V> clear() {
/* 130 */     this.map.clear();
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<K, V> map() {
/* 140 */     return this.map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<K, V> build() {
/* 151 */     return map();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String join(String separator, String keyValueSeparator) {
/* 162 */     return MapUtil.join(this.map, separator, keyValueSeparator, new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String joinIgnoreNull(String separator, String keyValueSeparator) {
/* 173 */     return MapUtil.joinIgnoreNull(this.map, separator, keyValueSeparator, new String[0]);
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
/*     */   public String join(String separator, String keyValueSeparator, boolean isIgnoreNull) {
/* 185 */     return MapUtil.join(this.map, separator, keyValueSeparator, isIgnoreNull, new String[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\MapBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */