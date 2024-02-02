/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Enumeration;
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
/*    */ public class EnumerationIter<E>
/*    */   implements IterableIter<E>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Enumeration<E> e;
/*    */   
/*    */   public EnumerationIter(Enumeration<E> enumeration) {
/* 24 */     this.e = enumeration;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 29 */     return this.e.hasMoreElements();
/*    */   }
/*    */ 
/*    */   
/*    */   public E next() {
/* 34 */     return this.e.nextElement();
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 39 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\EnumerationIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */