/*    */ package io.undertow.servlet.util;
/*    */ 
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
/*    */ public class IteratorEnumeration<T>
/*    */   implements Enumeration<T>
/*    */ {
/*    */   private final Iterator<T> iterator;
/*    */   
/*    */   public IteratorEnumeration(Iterator<T> iterator) {
/* 34 */     this.iterator = iterator;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasMoreElements() {
/* 39 */     return this.iterator.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public T nextElement() {
/* 44 */     return this.iterator.next();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\IteratorEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */