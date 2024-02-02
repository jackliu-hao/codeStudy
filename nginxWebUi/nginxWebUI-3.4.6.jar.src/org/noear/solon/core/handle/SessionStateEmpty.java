/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SessionStateEmpty
/*    */   implements SessionState
/*    */ {
/* 13 */   private Map<String, Object> sessionMap = null;
/*    */   public Map<String, Object> sessionMap() {
/* 15 */     if (this.sessionMap == null) {
/* 16 */       this.sessionMap = new HashMap<>();
/*    */     }
/*    */     
/* 19 */     return this.sessionMap;
/*    */   }
/*    */ 
/*    */   
/*    */   public String sessionId() {
/* 24 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String sessionChangeId() {
/* 29 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<String> sessionKeys() {
/* 34 */     return sessionMap().keySet();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object sessionGet(String key) {
/* 39 */     return sessionMap().get(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionSet(String key, Object val) {
/* 44 */     if (val == null) {
/* 45 */       sessionRemove(key);
/*    */     } else {
/* 47 */       sessionMap().put(key, val);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionRemove(String key) {
/* 53 */     sessionMap().remove(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionClear() {
/* 58 */     sessionMap().clear();
/*    */   }
/*    */ 
/*    */   
/*    */   public void sessionReset() {
/* 63 */     sessionMap().clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\SessionStateEmpty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */