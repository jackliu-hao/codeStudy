/*    */ package org.noear.solon.web.servlet;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.noear.solon.Solon;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonServletHandler
/*    */   extends HttpServlet
/*    */ {
/*    */   protected void preHandle(Context ctx) {}
/*    */   
/*    */   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 24 */     SolonServletContext ctx = new SolonServletContext(request, response);
/* 25 */     ctx.contentType("text/plain;charset=UTF-8");
/*    */     
/* 27 */     preHandle(ctx);
/*    */     
/* 29 */     Solon.app().tryHandle(ctx);
/*    */     
/* 31 */     if (!ctx.getHandled() || ctx.status() == 404) {
/* 32 */       response.setStatus(404);
/*    */     } else {
/* 34 */       ctx.commit();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\SolonServletHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */