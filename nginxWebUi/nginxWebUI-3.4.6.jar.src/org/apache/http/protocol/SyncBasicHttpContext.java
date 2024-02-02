/*    */ package org.apache.http.protocol;
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
/*    */ public class SyncBasicHttpContext
/*    */   extends BasicHttpContext
/*    */ {
/*    */   public SyncBasicHttpContext(HttpContext parentContext) {
/* 41 */     super(parentContext);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SyncBasicHttpContext() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized Object getAttribute(String id) {
/* 53 */     return super.getAttribute(id);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized void setAttribute(String id, Object obj) {
/* 58 */     super.setAttribute(id, obj);
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized Object removeAttribute(String id) {
/* 63 */     return super.removeAttribute(id);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized void clear() {
/* 71 */     super.clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\SyncBasicHttpContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */