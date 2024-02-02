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
/*    */ 
/*    */ public class FixedLinkedHashMap<K, V>
/*    */   extends LinkedHashMap<K, V>
/*    */ {
/*    */   private static final long serialVersionUID = -629171177321416095L;
/*    */   private int capacity;
/*    */   
/*    */   public FixedLinkedHashMap(int capacity) {
/* 26 */     super(capacity + 1, 1.0F, true);
/* 27 */     this.capacity = capacity;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCapacity() {
/* 36 */     return this.capacity;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setCapacity(int capacity) {
/* 45 */     this.capacity = capacity;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
/* 51 */     return (size() > this.capacity);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\FixedLinkedHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */