/*    */ package org.noear.solon.boot.web;
/*    */ 
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.boot.ServerProps;
/*    */ import org.noear.solon.core.handle.SessionState;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SessionStateBase
/*    */   implements SessionState
/*    */ {
/*    */   protected abstract String cookieGet(String paramString);
/*    */   
/*    */   protected abstract void cookieSet(String paramString1, String paramString2);
/*    */   
/*    */   protected String sessionIdGet(boolean reset) {
/* 18 */     String sid = cookieGet(ServerProps.session_cookieName);
/*    */     
/* 20 */     if (!reset && 
/* 21 */       !Utils.isEmpty(sid) && sid.length() > 30) {
/* 22 */       return sid;
/*    */     }
/*    */ 
/*    */     
/* 26 */     sid = Utils.guid();
/* 27 */     cookieSet(ServerProps.session_cookieName, sid);
/*    */     
/* 29 */     cookieSet(ServerProps.session_cookieName2, Utils.md5(sid + "&L8e!@T0"));
/*    */     
/* 31 */     return sid;
/*    */   }
/*    */   
/*    */   protected String sessionIdPush() {
/* 35 */     String skey = cookieGet(ServerProps.session_cookieName);
/*    */     
/* 37 */     if (Utils.isNotEmpty(skey)) {
/* 38 */       cookieSet(ServerProps.session_cookieName, skey);
/*    */     }
/*    */     
/* 41 */     return skey;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\web\SessionStateBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */