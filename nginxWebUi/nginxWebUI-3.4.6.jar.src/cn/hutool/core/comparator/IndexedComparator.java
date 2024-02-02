/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.util.ArrayUtil;
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
/*    */ public class IndexedComparator<T>
/*    */   implements Comparator<T>
/*    */ {
/*    */   private final boolean atEndIfMiss;
/*    */   private final T[] array;
/*    */   
/*    */   public IndexedComparator(T... objs) {
/* 28 */     this(false, objs);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IndexedComparator(boolean atEndIfMiss, T... objs) {
/* 39 */     Assert.notNull(objs, "'objs' array must not be null", new Object[0]);
/* 40 */     this.atEndIfMiss = atEndIfMiss;
/* 41 */     this.array = objs;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(T o1, T o2) {
/* 46 */     int index1 = getOrder(o1);
/* 47 */     int index2 = getOrder(o2);
/*    */     
/* 49 */     return Integer.compare(index1, index2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private int getOrder(T object) {
/* 59 */     int order = ArrayUtil.indexOf((Object[])this.array, object);
/* 60 */     if (order < 0) {
/* 61 */       order = this.atEndIfMiss ? this.array.length : -1;
/*    */     }
/* 63 */     return order;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\IndexedComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */