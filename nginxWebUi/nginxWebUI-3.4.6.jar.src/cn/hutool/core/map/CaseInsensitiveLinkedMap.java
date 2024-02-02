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
/*    */ public class CaseInsensitiveLinkedMap<K, V>
/*    */   extends CaseInsensitiveMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 4043263744224569870L;
/*    */   
/*    */   public CaseInsensitiveLinkedMap() {
/* 24 */     this(16);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveLinkedMap(int initialCapacity) {
/* 33 */     this(initialCapacity, 0.75F);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveLinkedMap(Map<? extends K, ? extends V> m) {
/* 42 */     this(0.75F, m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveLinkedMap(float loadFactor, Map<? extends K, ? extends V> m) {
/* 53 */     this(m.size(), loadFactor);
/* 54 */     putAll(m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveLinkedMap(int initialCapacity, float loadFactor) {
/* 64 */     super(new LinkedHashMap<>(initialCapacity, loadFactor));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\CaseInsensitiveLinkedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */