/*    */ package org.wildfly.common.ref;
/*    */ 
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.lang.ref.WeakReference;
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
/*    */ public class WeakReference<T, A>
/*    */   extends WeakReference<T>
/*    */   implements Reference<T, A>, Reapable<T, A>
/*    */ {
/*    */   private final A attachment;
/*    */   private final Reaper<T, A> reaper;
/*    */   
/*    */   public WeakReference(T referent) {
/* 44 */     this(referent, (A)null, (Reaper<T, A>)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WeakReference(T referent, A attachment) {
/* 54 */     this(referent, attachment, (Reaper<T, A>)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WeakReference(T referent, A attachment, ReferenceQueue<? super T> q) {
/* 65 */     super(referent, q);
/* 66 */     this.attachment = attachment;
/* 67 */     this.reaper = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WeakReference(T referent, A attachment, Reaper<T, A> reaper) {
/* 78 */     super(referent, (ReferenceQueue)References.ReaperThread.REAPER_QUEUE);
/* 79 */     this.attachment = attachment;
/* 80 */     this.reaper = reaper;
/*    */   }
/*    */   
/*    */   public A getAttachment() {
/* 84 */     return this.attachment;
/*    */   }
/*    */   
/*    */   public Reference.Type getType() {
/* 88 */     return Reference.Type.WEAK;
/*    */   }
/*    */   
/*    */   public Reaper<T, A> getReaper() {
/* 92 */     return this.reaper;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 96 */     return "weak reference to " + String.valueOf(get());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\WeakReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */