/*    */ package org.noear.solon.logging.integration;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.Props;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.core.handle.FilterChain;
/*    */ import org.noear.solon.logging.AppenderManager;
/*    */ import org.noear.solon.logging.LogOptions;
/*    */ import org.noear.solon.logging.event.Appender;
/*    */ import org.slf4j.MDC;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin
/*    */ {
/*    */   public void start(AopContext context) {
/* 22 */     loadAppenderConfig();
/*    */   }
/*    */   
/*    */   private void loadAppenderConfig() {
/* 26 */     Props props = Solon.cfg().getProp("solon.logging.appender");
/*    */ 
/*    */     
/* 29 */     AppenderManager.getInstance();
/*    */ 
/*    */     
/* 32 */     if (props.size() > 0) {
/* 33 */       props.forEach((k, v) -> {
/*    */             String key = (String)k;
/*    */             
/*    */             String val = (String)v;
/*    */             
/*    */             if (key.endsWith(".class")) {
/*    */               Appender appender = (Appender)Utils.newInstance(val);
/*    */               
/*    */               if (appender != null) {
/*    */                 String name = key.substring(0, key.length() - 6);
/*    */                 AppenderManager.getInstance().register(name, appender);
/*    */               } 
/*    */             } 
/*    */           });
/*    */     }
/* 48 */     LogOptions.getLoggerLevelInit();
/*    */     
/* 50 */     Solon.app().filter(-9, (ctx, chain) -> {
/*    */           MDC.clear();
/*    */           chain.doFilter(ctx);
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   public void stop() throws Throwable {
/* 58 */     AppenderManager.getInstance().stop();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\integration\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */