/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.function.Function;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FuncMap<K, V>
/*    */   extends TransMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Function<Object, K> keyFunc;
/*    */   private final Function<Object, V> valueFunc;
/*    */   
/*    */   public FuncMap(Supplier<Map<K, V>> mapFactory, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
/* 32 */     this(mapFactory.get(), keyFunc, valueFunc);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FuncMap(Map<K, V> emptyMap, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
/* 44 */     super(emptyMap);
/* 45 */     this.keyFunc = keyFunc;
/* 46 */     this.valueFunc = valueFunc;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected K customKey(Object key) {
/* 58 */     if (null != this.keyFunc) {
/* 59 */       return this.keyFunc.apply(key);
/*    */     }
/*    */     
/* 62 */     return (K)key;
/*    */   }
/*    */ 
/*    */   
/*    */   protected V customValue(Object value) {
/* 67 */     if (null != this.valueFunc) {
/* 68 */       return this.valueFunc.apply(value);
/*    */     }
/*    */     
/* 71 */     return (V)value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\FuncMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */