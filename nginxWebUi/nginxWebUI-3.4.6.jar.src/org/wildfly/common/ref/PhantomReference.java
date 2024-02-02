/*    */ package org.wildfly.common.ref;
/*    */ 
/*    */ import java.lang.ref.PhantomReference;
/*    */ import java.lang.ref.ReferenceQueue;
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
/*    */ public class PhantomReference<T, A>
/*    */   extends PhantomReference<T>
/*    */   implements Reference<T, A>, Reapable<T, A>
/*    */ {
/*    */   private final A attachment;
/*    */   private final Reaper<T, A> reaper;
/*    */   
/*    */   public PhantomReference(T referent, A attachment, ReferenceQueue<? super T> q) {
/* 46 */     super(referent, q);
/* 47 */     this.attachment = attachment;
/* 48 */     this.reaper = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PhantomReference(T referent, A attachment, Reaper<T, A> reaper) {
/* 59 */     super(referent, (ReferenceQueue)References.ReaperThread.REAPER_QUEUE);
/* 60 */     this.reaper = reaper;
/* 61 */     this.attachment = attachment;
/*    */   }
/*    */   
/*    */   public A getAttachment() {
/* 65 */     return this.attachment;
/*    */   }
/*    */   
/*    */   public Reference.Type getType() {
/* 69 */     return Reference.Type.PHANTOM;
/*    */   }
/*    */   
/*    */   public Reaper<T, A> getReaper() {
/* 73 */     return this.reaper;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 77 */     return "phantom reference";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\PhantomReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */