/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Iterator;
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
/*    */ public class IteratorEnumeration<E>
/*    */   implements Enumeration<E>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Iterator<E> iterator;
/*    */   
/*    */   public IteratorEnumeration(Iterator<E> iterator) {
/* 24 */     this.iterator = iterator;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasMoreElements() {
/* 29 */     return this.iterator.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public E nextElement() {
/* 34 */     return this.iterator.next();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\IteratorEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */