/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
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
/*    */ 
/*    */ public class PartitionIter<T>
/*    */   implements IterableIter<List<T>>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final Iterator<T> iterator;
/*    */   protected final int partitionSize;
/*    */   
/*    */   public PartitionIter(Iterator<T> iterator, int partitionSize) {
/* 39 */     this.iterator = iterator;
/* 40 */     this.partitionSize = partitionSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 45 */     return this.iterator.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public List<T> next() {
/* 50 */     List<T> list = new ArrayList<>(this.partitionSize);
/* 51 */     for (int i = 0; i < this.partitionSize && false != 
/* 52 */       this.iterator.hasNext(); i++)
/*    */     {
/*    */       
/* 55 */       list.add(this.iterator.next());
/*    */     }
/* 57 */     return list;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\PartitionIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */