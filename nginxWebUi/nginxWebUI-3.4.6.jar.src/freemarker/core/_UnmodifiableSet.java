/*    */ package freemarker.core;
/*    */ 
/*    */ import java.util.AbstractSet;
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
/*    */ public abstract class _UnmodifiableSet<E>
/*    */   extends AbstractSet<E>
/*    */ {
/*    */   public boolean add(E o) {
/* 29 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean remove(Object o) {
/* 34 */     if (contains(o)) {
/* 35 */       throw new UnsupportedOperationException();
/*    */     }
/* 37 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 42 */     if (!isEmpty())
/* 43 */       throw new UnsupportedOperationException(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_UnmodifiableSet.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */