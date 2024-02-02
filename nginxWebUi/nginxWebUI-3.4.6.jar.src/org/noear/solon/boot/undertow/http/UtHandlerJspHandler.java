/*    */ package org.noear.solon.boot.undertow.http;
/*    */ 
/*    */ import org.noear.solon.boot.ServerProps;
/*    */ import org.noear.solon.boot.undertow.XPluginImp;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ import org.noear.solon.web.servlet.SolonServletHandler;
/*    */ 
/*    */ public class UtHandlerJspHandler
/*    */   extends SolonServletHandler
/*    */ {
/*    */   protected void preHandle(Context ctx) {
/* 12 */     if (ServerProps.output_meta)
/* 13 */       ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver()); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\http\UtHandlerJspHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */