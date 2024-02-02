/*    */ package org.noear.solon.sessionstate.local;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ 
/*    */ class SessionProp {
/*  6 */   public static int session_timeout = 0;
/*    */   public static String session_state_domain;
/*    */   public static boolean session_state_domain_auto;
/*    */   
/*    */   public static void init() {
/* 11 */     session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
/* 12 */     session_state_domain = Solon.cfg().get("server.session.state.domain");
/* 13 */     session_state_domain_auto = Solon.cfg().getBool("server.session.state.domain.auto", true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\sessionstate\local\SessionProp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */