/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.function.BiFunction;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class TransMap<K, V>
/*    */   extends MapWrapper<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public TransMap(Supplier<Map<K, V>> mapFactory) {
/* 27 */     super(mapFactory);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TransMap(Map<K, V> emptyMap) {
/* 38 */     super(emptyMap);
/*    */   }
/*    */ 
/*    */   
/*    */   public V get(Object key) {
/* 43 */     return super.get(customKey(key));
/*    */   }
/*    */ 
/*    */   
/*    */   public V put(K key, V value) {
/* 48 */     return super.put(customKey(key), customValue(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void putAll(Map<? extends K, ? extends V> m) {
/* 53 */     m.forEach(this::put);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean containsKey(Object key) {
/* 58 */     return super.containsKey(customKey(key));
/*    */   }
/*    */ 
/*    */   
/*    */   public V remove(Object key) {
/* 63 */     return super.remove(customKey(key));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean remove(Object key, Object value) {
/* 68 */     return super.remove(customKey(key), customValue(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean replace(K key, V oldValue, V newValue) {
/* 73 */     return super.replace(customKey(key), customValue(oldValue), customValue(values()));
/*    */   }
/*    */ 
/*    */   
/*    */   public V replace(K key, V value) {
/* 78 */     return super.replace(customKey(key), customValue(value));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public V getOrDefault(Object key, V defaultValue) {
/* 84 */     return super.getOrDefault(customKey(key), customValue(defaultValue));
/*    */   }
/*    */ 
/*    */   
/*    */   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
/* 89 */     return super.computeIfPresent(customKey(key), (k, v) -> remappingFunction.apply(customKey(k), customValue(v)));
/*    */   }
/*    */ 
/*    */   
/*    */   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
/* 94 */     return super.compute(customKey(key), (k, v) -> remappingFunction.apply(customKey(k), customValue(v)));
/*    */   }
/*    */ 
/*    */   
/*    */   public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
/* 99 */     return super.merge(customKey(key), customValue(value), (v1, v2) -> remappingFunction.apply(customValue(v1), customValue(v2)));
/*    */   }
/*    */   
/*    */   protected abstract K customKey(Object paramObject);
/*    */   
/*    */   protected abstract V customValue(Object paramObject);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\TransMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */