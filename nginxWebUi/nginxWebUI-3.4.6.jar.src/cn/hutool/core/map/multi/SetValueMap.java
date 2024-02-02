/*    */ package cn.hutool.core.map.multi;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.LinkedHashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ public class SetValueMap<K, V>
/*    */   extends AbsCollValueMap<K, V, Set<V>>
/*    */ {
/*    */   private static final long serialVersionUID = 6044017508487827899L;
/*    */   
/*    */   public SetValueMap() {
/* 26 */     this(16);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SetValueMap(int initialCapacity) {
/* 35 */     this(initialCapacity, 0.75F);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SetValueMap(Map<? extends K, ? extends Collection<V>> m) {
/* 44 */     this(0.75F, m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SetValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
/* 54 */     this(m.size(), loadFactor);
/* 55 */     putAllValues(m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SetValueMap(int initialCapacity, float loadFactor) {
/* 65 */     super(new HashMap<>(initialCapacity, loadFactor));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected Set<V> createCollection() {
/* 71 */     return new LinkedHashSet<>(3);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\multi\SetValueMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */