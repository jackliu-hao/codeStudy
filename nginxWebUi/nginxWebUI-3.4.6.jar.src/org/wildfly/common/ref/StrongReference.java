/*    */ package org.wildfly.common.ref;
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
/*    */ public class StrongReference<T, A>
/*    */   implements Reference<T, A>
/*    */ {
/*    */   private volatile T referent;
/*    */   private final A attachment;
/*    */   
/*    */   public StrongReference(T referent, A attachment) {
/* 41 */     this.referent = referent;
/* 42 */     this.attachment = attachment;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StrongReference(T referent) {
/* 51 */     this(referent, null);
/*    */   }
/*    */   
/*    */   public T get() {
/* 55 */     return this.referent;
/*    */   }
/*    */   
/*    */   public void clear() {
/* 59 */     this.referent = null;
/*    */   }
/*    */   
/*    */   public A getAttachment() {
/* 63 */     return this.attachment;
/*    */   }
/*    */   
/*    */   public Reference.Type getType() {
/* 67 */     return Reference.Type.STRONG;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 71 */     return "strong reference to " + String.valueOf(get());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\StrongReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */