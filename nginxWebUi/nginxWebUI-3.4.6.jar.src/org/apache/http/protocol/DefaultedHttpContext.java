/*    */ package org.apache.http.protocol;
/*    */ 
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
/*    */ @Deprecated
/*    */ public final class DefaultedHttpContext
/*    */   implements HttpContext
/*    */ {
/*    */   private final HttpContext local;
/*    */   private final HttpContext defaults;
/*    */   
/*    */   public DefaultedHttpContext(HttpContext local, HttpContext defaults) {
/* 50 */     this.local = (HttpContext)Args.notNull(local, "HTTP context");
/* 51 */     this.defaults = defaults;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getAttribute(String id) {
/* 56 */     Object obj = this.local.getAttribute(id);
/* 57 */     if (obj == null) {
/* 58 */       return this.defaults.getAttribute(id);
/*    */     }
/* 60 */     return obj;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object removeAttribute(String id) {
/* 65 */     return this.local.removeAttribute(id);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setAttribute(String id, Object obj) {
/* 70 */     this.local.setAttribute(id, obj);
/*    */   }
/*    */   
/*    */   public HttpContext getDefaults() {
/* 74 */     return this.defaults;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     StringBuilder buf = new StringBuilder();
/* 80 */     buf.append("[local: ").append(this.local);
/* 81 */     buf.append("defaults: ").append(this.defaults);
/* 82 */     buf.append("]");
/* 83 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\DefaultedHttpContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */