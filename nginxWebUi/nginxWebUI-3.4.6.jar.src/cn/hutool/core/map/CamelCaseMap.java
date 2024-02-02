/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
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
/*    */ 
/*    */ public class CamelCaseMap<K, V>
/*    */   extends FuncKeyMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 4043263744224569870L;
/*    */   
/*    */   public CamelCaseMap() {
/* 26 */     this(16);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CamelCaseMap(int initialCapacity) {
/* 35 */     this(initialCapacity, 0.75F);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CamelCaseMap(Map<? extends K, ? extends V> m) {
/* 44 */     this(0.75F, m);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CamelCaseMap(float loadFactor, Map<? extends K, ? extends V> m) {
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
/*    */   public CamelCaseMap(int initialCapacity, float loadFactor) {
/* 65 */     this(MapBuilder.create(new HashMap<>(initialCapacity, loadFactor)));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   CamelCaseMap(MapBuilder<K, V> emptyMapBuilder) {
/* 75 */     super(emptyMapBuilder.build(), key -> {
/*    */           if (key instanceof CharSequence)
/*    */             key = StrUtil.toCamelCase(key.toString()); 
/*    */           return key;
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\CamelCaseMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */