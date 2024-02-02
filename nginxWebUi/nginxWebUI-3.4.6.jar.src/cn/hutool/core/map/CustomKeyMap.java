/*    */ package cn.hutool.core.map;
/*    */ 
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
/*    */ public abstract class CustomKeyMap<K, V>
/*    */   extends TransMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = 4043263744224569870L;
/*    */   
/*    */   public CustomKeyMap(Map<K, V> emptyMap) {
/* 24 */     super(emptyMap);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected V customValue(Object value) {
/* 30 */     return (V)value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\CustomKeyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */