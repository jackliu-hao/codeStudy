/*    */ package org.apache.http.protocol;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*    */ public class BasicHttpContext
/*    */   implements HttpContext
/*    */ {
/*    */   private final HttpContext parentContext;
/*    */   private final Map<String, Object> map;
/*    */   
/*    */   public BasicHttpContext() {
/* 52 */     this(null);
/*    */   }
/*    */ 
/*    */   
/*    */   public BasicHttpContext(HttpContext parentContext) {
/* 57 */     this.map = new ConcurrentHashMap<String, Object>();
/* 58 */     this.parentContext = parentContext;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getAttribute(String id) {
/* 63 */     Args.notNull(id, "Id");
/* 64 */     Object obj = this.map.get(id);
/* 65 */     if (obj == null && this.parentContext != null) {
/* 66 */       obj = this.parentContext.getAttribute(id);
/*    */     }
/* 68 */     return obj;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setAttribute(String id, Object obj) {
/* 73 */     Args.notNull(id, "Id");
/* 74 */     if (obj != null) {
/* 75 */       this.map.put(id, obj);
/*    */     } else {
/* 77 */       this.map.remove(id);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Object removeAttribute(String id) {
/* 83 */     Args.notNull(id, "Id");
/* 84 */     return this.map.remove(id);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clear() {
/* 91 */     this.map.clear();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 96 */     return this.map.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\BasicHttpContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */