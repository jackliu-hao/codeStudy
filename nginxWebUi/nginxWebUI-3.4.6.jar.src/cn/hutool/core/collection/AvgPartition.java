/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.List;
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
/*    */ public class AvgPartition<T>
/*    */   extends Partition<T>
/*    */ {
/*    */   final int limit;
/*    */   final int remainder;
/*    */   
/*    */   public AvgPartition(List<T> list, int limit) {
/* 35 */     super(list, list.size() / ((limit <= 0) ? 1 : limit));
/* 36 */     Assert.isTrue((limit > 0), "Partition limit must be > 0", new Object[0]);
/* 37 */     this.limit = limit;
/* 38 */     this.remainder = list.size() % limit;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<T> get(int index) {
/* 43 */     int size = this.size;
/* 44 */     int remainder = this.remainder;
/*    */     
/* 46 */     int start = index * size + Math.min(index, remainder);
/* 47 */     int end = start + size;
/* 48 */     if (index + 1 <= remainder)
/*    */     {
/* 50 */       end++;
/*    */     }
/* 52 */     return this.list.subList(start, end);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 57 */     return this.limit;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\AvgPartition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */