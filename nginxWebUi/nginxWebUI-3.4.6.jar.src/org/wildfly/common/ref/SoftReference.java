/*    */ package org.wildfly.common.ref;
/*    */ 
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.lang.ref.SoftReference;
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
/*    */ public class SoftReference<T, A>
/*    */   extends SoftReference<T>
/*    */   implements Reference<T, A>, Reapable<T, A>
/*    */ {
/*    */   private final A attachment;
/*    */   private final Reaper<T, A> reaper;
/*    */   
/*    */   public SoftReference(T referent) {
/* 44 */     this(referent, (A)null, (ReferenceQueue<? super T>)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SoftReference(T referent, A attachment) {
/* 54 */     this(referent, attachment, (ReferenceQueue<? super T>)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SoftReference(T referent, A attachment, ReferenceQueue<? super T> q) {
/* 65 */     super(referent, q);
/* 66 */     this.reaper = null;
/* 67 */     this.attachment = attachment;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SoftReference(T referent, A attachment, Reaper<T, A> reaper) {
/* 78 */     super(referent, (ReferenceQueue)References.ReaperThread.REAPER_QUEUE);
/* 79 */     this.reaper = reaper;
/* 80 */     this.attachment = attachment;
/*    */   }
/*    */   
/*    */   public Reaper<T, A> getReaper() {
/* 84 */     return this.reaper;
/*    */   }
/*    */   
/*    */   public A getAttachment() {
/* 88 */     return this.attachment;
/*    */   }
/*    */   
/*    */   public Reference.Type getType() {
/* 92 */     return Reference.Type.SOFT;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 96 */     return "soft reference to " + String.valueOf(get());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\SoftReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */