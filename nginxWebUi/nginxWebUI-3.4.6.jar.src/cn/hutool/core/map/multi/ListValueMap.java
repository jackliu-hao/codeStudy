/*    */ package cn.hutool.core.map.multi;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
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
/*    */ public class ListValueMap<K, V>
/*    */   extends AbsCollValueMap<K, V, List<V>>
/*    */ {
/*    */   private static final long serialVersionUID = 6044017508487827899L;
/*    */   
/*    */   public ListValueMap() {
/* 26 */     this(16);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ListValueMap(int initialCapacity) {
/* 35 */     this(initialCapacity, 0.75F);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ListValueMap(Map<? extends K, ? extends Collection<V>> m) {
/* 44 */     this(0.75F, m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ListValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
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
/*    */   public ListValueMap(int initialCapacity, float loadFactor) {
/* 65 */     super(new HashMap<>(initialCapacity, loadFactor));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected List<V> createCollection() {
/* 71 */     return new ArrayList<>(3);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\multi\ListValueMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */