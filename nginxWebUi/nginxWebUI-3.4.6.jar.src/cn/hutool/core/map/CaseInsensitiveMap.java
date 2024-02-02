/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ public class CaseInsensitiveMap<K, V>
/*    */   extends FuncKeyMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 4043263744224569870L;
/*    */   
/*    */   public CaseInsensitiveMap() {
/* 24 */     this(16);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveMap(int initialCapacity) {
/* 33 */     this(initialCapacity, 0.75F);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveMap(Map<? extends K, ? extends V> m) {
/* 43 */     this(0.75F, m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveMap(float loadFactor, Map<? extends K, ? extends V> m) {
/* 54 */     this(m.size(), loadFactor);
/* 55 */     putAll(m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
/* 65 */     this(MapBuilder.create(new HashMap<>(initialCapacity, loadFactor)));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   CaseInsensitiveMap(MapBuilder<K, V> emptyMapBuilder) {
/* 75 */     super(emptyMapBuilder.build(), key -> {
/*    */           if (key instanceof CharSequence)
/*    */             key = key.toString().toLowerCase(); 
/*    */           return key;
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\CaseInsensitiveMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */