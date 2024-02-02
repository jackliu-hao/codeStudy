/*    */ package org.apache.http.impl.conn.tsccm;
/*    */ 
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.lang.ref.WeakReference;
/*    */ import org.apache.http.conn.routing.HttpRoute;
/*    */ import org.apache.http.util.Args;
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
/*    */ @Deprecated
/*    */ public class BasicPoolEntryRef
/*    */   extends WeakReference<BasicPoolEntry>
/*    */ {
/*    */   private final HttpRoute route;
/*    */   
/*    */   public BasicPoolEntryRef(BasicPoolEntry entry, ReferenceQueue<Object> queue) {
/* 59 */     super(entry, queue);
/* 60 */     Args.notNull(entry, "Pool entry");
/* 61 */     this.route = entry.getPlannedRoute();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final HttpRoute getRoute() {
/* 72 */     return this.route;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\tsccm\BasicPoolEntryRef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */