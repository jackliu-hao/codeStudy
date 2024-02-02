/*    */ package org.noear.solon.boot.jlhttp;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.boot.ServerProps;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ 
/*    */ 
/*    */ public class JlHttpContextHandler
/*    */   implements HTTPServer.ContextHandler
/*    */ {
/*    */   public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
/*    */     try {
/* 14 */       return handleDo(request, response);
/* 15 */     } catch (Throwable ex) {
/*    */ 
/*    */       
/* 18 */       EventBus.push(ex);
/*    */       
/* 20 */       response.sendHeaders(500);
/* 21 */       return 0;
/*    */     } 
/*    */   }
/*    */   
/*    */   private int handleDo(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
/* 26 */     JlHttpContext ctx = new JlHttpContext(request, response);
/*    */     
/* 28 */     ctx.contentType("text/plain;charset=UTF-8");
/*    */     
/* 30 */     if (ServerProps.output_meta) {
/* 31 */       ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
/*    */     }
/*    */     
/* 34 */     Solon.app().tryHandle(ctx);
/*    */     
/* 36 */     if (ctx.getHandled() && ctx.status() >= 200) {
/* 37 */       ctx.commit();
/*    */       
/* 39 */       return 0;
/*    */     } 
/* 41 */     return 404;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\jlhttp\JlHttpContextHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */