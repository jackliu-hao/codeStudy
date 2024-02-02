/*    */ package cn.hutool.core.comparator;
/*    */ 
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import java.util.function.Function;
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
/*    */ public class FuncComparator<T>
/*    */   extends NullComparator<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Function<T, Comparable<?>> func;
/*    */   
/*    */   public FuncComparator(boolean nullGreater, Function<T, Comparable<?>> func) {
/* 25 */     super(nullGreater, null);
/* 26 */     this.func = func;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int doCompare(T a, T b) {
/*    */     Comparable<?> v1;
/*    */     Comparable<?> v2;
/*    */     try {
/* 34 */       v1 = this.func.apply(a);
/* 35 */       v2 = this.func.apply(b);
/* 36 */     } catch (Exception e) {
/* 37 */       throw new ComparatorException(e);
/*    */     } 
/*    */     
/* 40 */     return compare(a, b, v1, v2);
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
/*    */ 
/*    */ 
/*    */   
/*    */   private int compare(T o1, T o2, Comparable v1, Comparable v2) {
/* 56 */     int result = ObjectUtil.compare(v1, v2);
/* 57 */     if (0 == result)
/*    */     {
/* 59 */       result = CompareUtil.compare(o1, o2, this.nullGreater);
/*    */     }
/* 61 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\FuncComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */