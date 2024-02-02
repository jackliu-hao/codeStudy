/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import java.util.LinkedHashMap;
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
/*    */ public class CamelCaseLinkedMap<K, V>
/*    */   extends CamelCaseMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 4043263744224569870L;
/*    */   
/*    */   public CamelCaseLinkedMap() {
/* 24 */     this(16);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CamelCaseLinkedMap(int initialCapacity) {
/* 33 */     this(initialCapacity, 0.75F);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CamelCaseLinkedMap(Map<? extends K, ? extends V> m) {
/* 42 */     this(0.75F, m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CamelCaseLinkedMap(float loadFactor, Map<? extends K, ? extends V> m) {
/* 52 */     this(m.size(), loadFactor);
/* 53 */     putAll(m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CamelCaseLinkedMap(int initialCapacity, float loadFactor) {
/* 63 */     super(new LinkedHashMap<>(initialCapacity, loadFactor));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\CamelCaseLinkedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */