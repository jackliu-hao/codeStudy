/*    */ package com.wf.captcha.servlet;
/*    */ 
/*    */ import com.wf.captcha.utils.CaptchaUtil;
/*    */ import java.io.IOException;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.http.HttpServlet;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CaptchaServlet
/*    */   extends HttpServlet
/*    */ {
/*    */   private static final long serialVersionUID = -90304944339413093L;
/*    */   
/*    */   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 21 */     CaptchaUtil.out(request, response);
/*    */   }
/*    */ 
/*    */   
/*    */   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
/* 26 */     doGet(request, response);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\servlet\CaptchaServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */