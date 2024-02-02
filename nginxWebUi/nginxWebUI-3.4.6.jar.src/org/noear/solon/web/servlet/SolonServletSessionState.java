/*    */ package org.noear.solon.web.servlet;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.Enumeration;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.noear.solon.core.handle.SessionState;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonServletSessionState
/*    */   implements SessionState
/*    */ {
/*    */   private HttpServletRequest _request;
/*    */   
/*    */   public SolonServletSessionState(HttpServletRequest request) {
/* 17 */     this._request = request;
/*    */   }
/*    */ 
/*    */   
/*    */   public String sessionId() {
/* 22 */     return this._request.getRequestedSessionId();
/*    */   }
/*    */ 
/*    */   
/*    */   public String sessionChangeId() {
/* 27 */     return this._request.changeSessionId();
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<String> sessionKeys() {
/* 32 */     return Collections.list(this._request.getSession().getAttributeNames());
/*    */   }
/*    */ 
/*    */   
/*    */   public Object sessionGet(String key) {
/* 37 */     return this._request.getSession().getAttribute(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionSet(String key, Object val) {
/* 42 */     if (val == null) {
/* 43 */       sessionRemove(key);
/*    */     } else {
/* 45 */       this._request.getSession().setAttribute(key, val);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionRemove(String key) {
/* 51 */     this._request.getSession().removeAttribute(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionClear() {
/* 56 */     Enumeration<String> names = this._request.getSession().getAttributeNames();
/* 57 */     while (names.hasMoreElements()) {
/* 58 */       this._request.getSession().removeAttribute(names.nextElement());
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionReset() {
/* 64 */     this._request.getSession().invalidate();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\SolonServletSessionState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */