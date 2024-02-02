/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class ComparableComparator<E extends Comparable<? super E>>
/*    */   implements Comparator<E>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 3020871676147289162L;
/* 18 */   public static final ComparableComparator INSTANCE = new ComparableComparator();
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
/*    */   public int compare(E obj1, E obj2) {
/* 40 */     return obj1.compareTo(obj2);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 45 */     return "ComparableComparator".hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object object) {
/* 50 */     return (this == object || (null != object && object.getClass().equals(getClass())));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\ComparableComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */