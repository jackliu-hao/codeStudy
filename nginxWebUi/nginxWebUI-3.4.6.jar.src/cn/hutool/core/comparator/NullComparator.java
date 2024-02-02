/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ import java.util.Objects;
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
/*    */ public class NullComparator<T>
/*    */   implements Comparator<T>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final boolean nullGreater;
/*    */   protected final Comparator<T> comparator;
/*    */   
/*    */   public NullComparator(boolean nullGreater, Comparator<? super T> comparator) {
/* 31 */     this.nullGreater = nullGreater;
/* 32 */     this.comparator = (Comparator)comparator;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(T a, T b) {
/* 37 */     if (a == b)
/* 38 */       return 0; 
/* 39 */     if (a == null)
/* 40 */       return this.nullGreater ? 1 : -1; 
/* 41 */     if (b == null) {
/* 42 */       return this.nullGreater ? -1 : 1;
/*    */     }
/* 44 */     return doCompare(a, b);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Comparator<T> thenComparing(Comparator<? super T> other) {
/* 50 */     Objects.requireNonNull(other);
/* 51 */     return new NullComparator(this.nullGreater, (this.comparator == null) ? other : this.comparator.thenComparing(other));
/*    */   }
/*    */ 
/*    */   
/*    */   public Comparator<T> reversed() {
/* 56 */     return new NullComparator((false == this.nullGreater), (this.comparator == null) ? null : this.comparator.reversed());
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
/*    */   
/*    */   protected int doCompare(T a, T b) {
/* 69 */     if (null == this.comparator) {
/* 70 */       if (a instanceof Comparable && b instanceof Comparable) {
/* 71 */         return ((Comparable<T>)a).compareTo(b);
/*    */       }
/* 73 */       return 0;
/*    */     } 
/*    */     
/* 76 */     return this.comparator.compare(a, b);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\NullComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */