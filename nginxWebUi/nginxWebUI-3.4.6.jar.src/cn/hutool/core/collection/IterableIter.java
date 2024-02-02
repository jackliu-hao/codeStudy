/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface IterableIter<T>
/*    */   extends Iterable<T>, Iterator<T>
/*    */ {
/*    */   default Iterator<T> iterator() {
/* 16 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\IterableIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */