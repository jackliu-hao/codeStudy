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
/*    */ public class RandomAccessPartition<T>
/*    */   extends Partition<T>
/*    */   implements RandomAccess
/*    */ {
/*    */   public RandomAccessPartition(List<T> list, int size) {
/* 25 */     super(list, size);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\RandomAccessPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */