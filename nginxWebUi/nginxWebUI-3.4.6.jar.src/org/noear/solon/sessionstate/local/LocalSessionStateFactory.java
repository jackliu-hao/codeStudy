/*    */ package org.noear.solon.sessionstate.local;
/*    */ 
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.SessionState;
/*    */ import org.noear.solon.core.handle.SessionStateFactory;
/*    */ 
/*    */ public class LocalSessionStateFactory
/*    */   implements SessionStateFactory {
/*    */   private static LocalSessionStateFactory instance;
/*    */   public static final int SESSION_STATE_PRIORITY = 1;
/*    */   
/*    */   public static LocalSessionStateFactory getInstance() {
/* 13 */     if (instance == null) {
/* 14 */       instance = new LocalSessionStateFactory();
/*    */     }
/*    */     
/* 17 */     return instance;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int priority() {
/* 26 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public SessionState create(Context ctx) {
/* 31 */     return (SessionState)new LocalSessionState(ctx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\sessionstate\local\LocalSessionStateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */