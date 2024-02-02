/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.core.message.Listener;
/*    */ import org.noear.solon.core.message.Message;
/*    */ import org.noear.solon.core.message.Session;
/*    */ import org.noear.solon.socketd.annotation.ClientEndpoint;
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin
/*    */ {
/*    */   public void start(AopContext context) {
/* 18 */     Solon.app().listenAfter(new RouterListener());
/*    */     
/* 20 */     context.beanBuilderAdd(ClientEndpoint.class, (clz, wrap, anno) -> {
/*    */           if (Listener.class.isAssignableFrom(clz)) {
/*    */             Listener l = (Listener)wrap.raw();
/*    */ 
/*    */             
/*    */             Session s = SocketD.createSession(anno.uri(), anno.autoReconnect());
/*    */ 
/*    */             
/*    */             s.listener(l);
/*    */ 
/*    */             
/*    */             if (Utils.isNotEmpty(anno.handshakeHeader()))
/*    */               try {
/*    */                 s.sendHandshake(Message.wrapHandshake(anno.handshakeHeader()));
/* 34 */               } catch (Throwable ex) {
/*    */                 EventBus.push(ex);
/*    */               }  
/*    */             if (anno.heartbeatRate() > 0)
/*    */               s.sendHeartbeatAuto(anno.heartbeatRate()); 
/*    */           } 
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */