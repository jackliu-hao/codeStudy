/*    */ package cn.hutool.core.map;
/*    */ 
/*    */ import cn.hutool.core.util.ObjectUtil;
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
/*    */ public abstract class AbsEntry<K, V>
/*    */   implements Map.Entry<K, V>
/*    */ {
/*    */   public V setValue(V value) {
/* 21 */     throw new UnsupportedOperationException("Entry is read only.");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object object) {
/* 26 */     if (object instanceof Map.Entry) {
/* 27 */       Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
/* 28 */       return (ObjectUtil.equals(getKey(), that.getKey()) && 
/* 29 */         ObjectUtil.equals(getValue(), that.getValue()));
/*    */     } 
/* 31 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 37 */     K k = getKey();
/* 38 */     V v = getValue();
/* 39 */     return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return (new StringBuilder()).append(getKey()).append("=").append(getValue()).toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\AbsEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */