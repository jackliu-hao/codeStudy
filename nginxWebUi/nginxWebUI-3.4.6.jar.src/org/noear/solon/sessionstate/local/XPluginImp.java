/*    */ package org.noear.solon.sessionstate.local;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.Bridge;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.util.PrintUtil;
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin {
/*    */   public void start(AopContext context) {
/* 12 */     if (!Solon.app().enableSessionState()) {
/*    */       return;
/*    */     }
/*    */     
/* 16 */     if (Bridge.sessionStateFactory().priority() >= 1) {
/*    */       return;
/*    */     }
/*    */     
/* 20 */     SessionProp.init();
/*    */     
/* 22 */     Bridge.sessionStateFactorySet(LocalSessionStateFactory.getInstance());
/*    */     
/* 24 */     PrintUtil.info("Session", "solon: Local session state plugin is loaded");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\sessionstate\local\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */