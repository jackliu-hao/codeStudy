/*    */ package org.noear.solon.boot.undertow.jsp;
/*    */ 
/*    */ import io.undertow.servlet.api.ServletInfo;
/*    */ import java.io.IOException;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.ServletRequest;
/*    */ import javax.servlet.ServletResponse;
/*    */ import org.apache.jasper.servlet.JspServlet;
/*    */ import org.noear.solon.core.handle.Context;
/*    */ 
/*    */ public class JspServletEx
/*    */   extends JspServlet
/*    */ {
/*    */   public static ServletInfo createServlet(String name, String path) {
/* 15 */     ServletInfo servlet = new ServletInfo(name, JspServletEx.class);
/* 16 */     servlet.addMapping(path);
/* 17 */     servlet.setRequireWelcomeFileMapping(true);
/* 18 */     return servlet;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
/* 24 */     if (Context.current() == null) {
/*    */       return;
/*    */     }
/*    */     
/* 28 */     super.service(req, res);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boo\\undertow\jsp\JspServletEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */