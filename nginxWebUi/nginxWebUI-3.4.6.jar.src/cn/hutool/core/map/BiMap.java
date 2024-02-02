/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import java.util.Map;
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
/*    */ public class BiMap<K, V>
/*    */   extends MapWrapper<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private Map<V, K> inverse;
/*    */   
/*    */   public BiMap(Map<K, V> raw) {
/* 26 */     super(raw);
/*    */   }
/*    */ 
/*    */   
/*    */   public V put(K key, V value) {
/* 31 */     if (null != this.inverse) {
/* 32 */       this.inverse.put(value, key);
/*    */     }
/* 34 */     return super.put(key, value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void putAll(Map<? extends K, ? extends V> m) {
/* 39 */     super.putAll(m);
/* 40 */     if (null != this.inverse) {
/* 41 */       m.forEach((key, value) -> this.inverse.put((V)value, (K)key));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public V remove(Object key) {
/* 47 */     V v = super.remove(key);
/* 48 */     if (null != this.inverse && null != v) {
/* 49 */       this.inverse.remove(v);
/*    */     }
/* 51 */     return v;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean remove(Object key, Object value) {
/* 56 */     return (super.remove(key, value) && null != this.inverse && this.inverse.remove(value, key));
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 61 */     super.clear();
/* 62 */     this.inverse = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<V, K> getInverse() {
/* 71 */     if (null == this.inverse) {
/* 72 */       this.inverse = MapUtil.inverse(getRaw());
/*    */     }
/* 74 */     return this.inverse;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public K getKey(V value) {
/* 84 */     return getInverse().get(value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\BiMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */