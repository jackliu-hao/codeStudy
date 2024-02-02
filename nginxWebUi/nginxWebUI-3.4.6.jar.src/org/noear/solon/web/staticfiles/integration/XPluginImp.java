/*    */ package org.noear.solon.web.staticfiles.integration;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.AopContext;
/*    */ import org.noear.solon.core.NvMap;
/*    */ import org.noear.solon.core.Plugin;
/*    */ import org.noear.solon.core.handle.Handler;
/*    */ import org.noear.solon.core.handle.HandlerPipeline;
/*    */ import org.noear.solon.web.staticfiles.StaticMappings;
/*    */ import org.noear.solon.web.staticfiles.StaticMimes;
/*    */ import org.noear.solon.web.staticfiles.StaticRepository;
/*    */ import org.noear.solon.web.staticfiles.StaticResourceHandler;
/*    */ import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;
/*    */ 
/*    */ public class XPluginImp
/*    */   implements Plugin
/*    */ {
/*    */   public void start(AopContext context) {
/* 20 */     if (!Solon.app().enableStaticfiles()) {
/*    */       return;
/*    */     }
/*    */     
/* 24 */     if (!XPluginProp.enable()) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 29 */     XPluginProp.maxAge();
/*    */ 
/*    */     
/* 32 */     if (Utils.getResource("static/") != null) {
/* 33 */       StaticMappings.add("/", (StaticRepository)new ClassPathStaticRepository("static/"));
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 39 */     NvMap mimeTypes = Solon.cfg().getXmap("solon.mime");
/* 40 */     mimeTypes.forEach((k, v) -> StaticMimes.add("." + k, v));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 46 */     HandlerPipeline pipeline = new HandlerPipeline();
/* 47 */     pipeline.next((Handler)new StaticResourceHandler()).next(Solon.app().handlerGet());
/* 48 */     Solon.app().handlerSet((Handler)pipeline);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\integration\XPluginImp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */