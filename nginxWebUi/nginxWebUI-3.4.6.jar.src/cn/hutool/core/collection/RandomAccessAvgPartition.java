/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.RandomAccess;
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
/*    */ public class RandomAccessAvgPartition<T>
/*    */   extends AvgPartition<T>
/*    */   implements RandomAccess
/*    */ {
/*    */   public RandomAccessAvgPartition(List<T> list, int limit) {
/* 30 */     super(list, limit);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\RandomAccessAvgPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */