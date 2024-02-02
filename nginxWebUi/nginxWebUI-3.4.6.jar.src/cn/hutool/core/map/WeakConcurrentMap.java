/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import cn.hutool.core.util.ReferenceUtil;
/*    */ import java.lang.ref.Reference;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ConcurrentMap;
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
/*    */ public class WeakConcurrentMap<K, V>
/*    */   extends ReferenceConcurrentMap<K, V>
/*    */ {
/*    */   public WeakConcurrentMap() {
/* 24 */     this(new ConcurrentHashMap<>());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WeakConcurrentMap(ConcurrentMap<Reference<K>, V> raw) {
/* 33 */     super(raw, ReferenceUtil.ReferenceType.WEAK);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\WeakConcurrentMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */