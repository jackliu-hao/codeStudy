/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.util.AbstractList;
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
/*    */ public class Partition<T>
/*    */   extends AbstractList<List<T>>
/*    */ {
/*    */   protected final List<T> list;
/*    */   protected final int size;
/*    */   
/*    */   public Partition(List<T> list, int size) {
/* 28 */     this.list = list;
/* 29 */     this.size = Math.min(size, list.size());
/*    */   }
/*    */ 
/*    */   
/*    */   public List<T> get(int index) {
/* 34 */     int start = index * this.size;
/* 35 */     int end = Math.min(start + this.size, this.list.size());
/* 36 */     return this.list.subList(start, end);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int size() {
/* 42 */     int size = this.size;
/* 43 */     int total = this.list.size();
/* 44 */     int length = total / size;
/* 45 */     if (total % size > 0) {
/* 46 */       length++;
/*    */     }
/* 48 */     return length;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 53 */     return this.list.isEmpty();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\Partition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */