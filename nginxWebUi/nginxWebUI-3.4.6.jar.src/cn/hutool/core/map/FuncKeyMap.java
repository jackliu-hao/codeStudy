/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.function.Function;
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
/*    */ public class FuncKeyMap<K, V>
/*    */   extends CustomKeyMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Function<Object, K> keyFunc;
/*    */   
/*    */   public FuncKeyMap(Map<K, V> emptyMap, Function<Object, K> keyFunc) {
/* 29 */     super(emptyMap);
/* 30 */     this.keyFunc = keyFunc;
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
/* 42 */     if (null != this.keyFunc) {
/* 43 */       return this.keyFunc.apply(key);
/*    */     }
/*    */     
/* 46 */     return (K)key;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\FuncKeyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */