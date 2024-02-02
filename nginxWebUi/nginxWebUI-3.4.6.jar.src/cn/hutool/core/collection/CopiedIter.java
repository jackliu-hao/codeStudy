/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class CopiedIter<E>
/*    */   implements IterableIter<E>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Iterator<E> listIterator;
/*    */   
/*    */   public static <E> CopiedIter<E> copyOf(Iterator<E> iterator) {
/* 36 */     return new CopiedIter<>(iterator);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CopiedIter(Iterator<E> iterator) {
/* 45 */     List<E> eleList = ListUtil.toList(iterator);
/* 46 */     this.listIterator = eleList.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 51 */     return this.listIterator.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public E next() {
/* 56 */     return this.listIterator.next();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void remove() throws UnsupportedOperationException {
/* 66 */     throw new UnsupportedOperationException("This is a read-only iterator.");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\CopiedIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */