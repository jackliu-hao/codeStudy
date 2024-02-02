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
/*    */ public class ReverseComparator<E>
/*    */   implements Comparator<E>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 8083701245147495562L;
/*    */   private final Comparator<? super E> comparator;
/*    */   
/*    */   public ReverseComparator(Comparator<? super E> comparator) {
/* 21 */     this.comparator = (null == comparator) ? ComparableComparator.INSTANCE : comparator;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int compare(E o1, E o2) {
/* 27 */     return this.comparator.compare(o2, o1);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 32 */     return "ReverseComparator".hashCode() ^ this.comparator.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object object) {
/* 37 */     if (this == object) {
/* 38 */       return true;
/*    */     }
/* 40 */     if (null == object) {
/* 41 */       return false;
/*    */     }
/* 43 */     if (object.getClass().equals(getClass())) {
/* 44 */       ReverseComparator<?> thatrc = (ReverseComparator)object;
/* 45 */       return this.comparator.equals(thatrc.comparator);
/*    */     } 
/* 47 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\ReverseComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */