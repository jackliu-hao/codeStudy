/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.Comparator;
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
/*    */ public class InstanceComparator<T>
/*    */   implements Comparator<T>
/*    */ {
/*    */   private final boolean atEndIfMiss;
/*    */   private final Class<?>[] instanceOrder;
/*    */   
/*    */   public InstanceComparator(Class<?>... instanceOrder) {
/* 44 */     this(false, instanceOrder);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InstanceComparator(boolean atEndIfMiss, Class<?>... instanceOrder) {
/* 54 */     Assert.notNull(instanceOrder, "'instanceOrder' array must not be null", new Object[0]);
/* 55 */     this.atEndIfMiss = atEndIfMiss;
/* 56 */     this.instanceOrder = instanceOrder;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int compare(T o1, T o2) {
/* 62 */     int i1 = getOrder(o1);
/* 63 */     int i2 = getOrder(o2);
/* 64 */     return Integer.compare(i1, i2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private int getOrder(T object) {
/* 74 */     if (object != null) {
/* 75 */       for (int i = 0; i < this.instanceOrder.length; i++) {
/* 76 */         if (this.instanceOrder[i].isInstance(object)) {
/* 77 */           return i;
/*    */         }
/*    */       } 
/*    */     }
/* 81 */     return this.atEndIfMiss ? this.instanceOrder.length : -1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\InstanceComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */