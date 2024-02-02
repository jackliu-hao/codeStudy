/*    */ package org.antlr.v4.runtime.misc;
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
/*    */ public final class ObjectEqualityComparator
/*    */   extends AbstractEqualityComparator<Object>
/*    */ {
/* 39 */   public static final ObjectEqualityComparator INSTANCE = new ObjectEqualityComparator();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode(Object obj) {
/* 49 */     if (obj == null) {
/* 50 */       return 0;
/*    */     }
/*    */     
/* 53 */     return obj.hashCode();
/*    */   }
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
/*    */   public boolean equals(Object a, Object b) {
/* 67 */     if (a == null) {
/* 68 */       return (b == null);
/*    */     }
/*    */     
/* 71 */     return a.equals(b);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\ObjectEqualityComparator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */