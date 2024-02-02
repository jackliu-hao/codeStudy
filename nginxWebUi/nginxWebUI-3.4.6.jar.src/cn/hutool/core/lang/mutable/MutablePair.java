/*    */ package cn.hutool.core.lang.mutable;
/*    */ 
/*    */ import cn.hutool.core.lang.Pair;
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
/*    */ public class MutablePair<K, V>
/*    */   extends Pair<K, V>
/*    */   implements Mutable<Pair<K, V>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public MutablePair(K key, V value) {
/* 22 */     super(key, value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MutablePair<K, V> setKey(K key) {
/* 32 */     this.key = key;
/* 33 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MutablePair<K, V> setValue(V value) {
/* 43 */     this.value = value;
/* 44 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public Pair<K, V> get() {
/* 49 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(Pair<K, V> pair) {
/* 54 */     this.key = pair.getKey();
/* 55 */     this.value = pair.getValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\mutable\MutablePair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */