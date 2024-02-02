/*    */ package freemarker.core;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.NoSuchElementException;
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
/*    */ public class _ArrayEnumeration
/*    */   implements Enumeration
/*    */ {
/*    */   private final Object[] array;
/*    */   private final int size;
/*    */   private int nextIndex;
/*    */   
/*    */   public _ArrayEnumeration(Object[] array, int size) {
/* 33 */     this.array = array;
/* 34 */     this.size = size;
/* 35 */     this.nextIndex = 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasMoreElements() {
/* 40 */     return (this.nextIndex < this.size);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object nextElement() {
/* 45 */     if (this.nextIndex >= this.size) {
/* 46 */       throw new NoSuchElementException();
/*    */     }
/* 48 */     return this.array[this.nextIndex++];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_ArrayEnumeration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */