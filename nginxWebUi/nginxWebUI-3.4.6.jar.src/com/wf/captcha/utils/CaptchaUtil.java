/*     */ package com.wf.captcha.utils;
/*     */ 
/*     */ import com.wf.captcha.SpecCaptcha;
/*     */ import com.wf.captcha.base.Captcha;
/*     */ import java.awt.Font;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CaptchaUtil
/*     */ {
/*     */   private static final String SESSION_KEY = "captcha";
/*     */   private static final int DEFAULT_LEN = 4;
/*     */   private static final int DEFAULT_WIDTH = 130;
/*     */   private static final int DEFAULT_HEIGHT = 48;
/*     */   
/*     */   public static void out(HttpServletRequest request, HttpServletResponse response) throws IOException {
/*  31 */     out(4, request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void out(int len, HttpServletRequest request, HttpServletResponse response) throws IOException {
/*  44 */     out(130, 48, len, request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void out(int width, int height, int len, HttpServletRequest request, HttpServletResponse response) throws IOException {
/*  59 */     out(width, height, len, null, request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void out(Font font, HttpServletRequest request, HttpServletResponse response) throws IOException {
/*  72 */     out(130, 48, 4, font, request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void out(int len, Font font, HttpServletRequest request, HttpServletResponse response) throws IOException {
/*  86 */     out(130, 48, len, font, request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void out(int width, int height, int len, Font font, HttpServletRequest request, HttpServletResponse response) throws IOException {
/* 102 */     SpecCaptcha specCaptcha = new SpecCaptcha(width, height, len);
/* 103 */     if (font != null) {
/* 104 */       specCaptcha.setFont(font);
/*     */     }
/* 106 */     out((Captcha)specCaptcha, request, response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void out(Captcha captcha, HttpServletRequest request, HttpServletResponse response) throws IOException {
/* 120 */     setHeader(response);
/* 121 */     request.getSession().setAttribute("captcha", captcha.text().toLowerCase());
/* 122 */     captcha.out((OutputStream)response.getOutputStream());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean ver(String code, HttpServletRequest request) {
/* 133 */     if (code != null) {
/* 134 */       String captcha = (String)request.getSession().getAttribute("captcha");
/* 135 */       return code.trim().toLowerCase().equals(captcha);
/*     */     } 
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clear(HttpServletRequest request) {
/* 146 */     request.getSession().removeAttribute("captcha");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setHeader(HttpServletResponse response) {
/* 155 */     response.setContentType("image/gif");
/* 156 */     response.setHeader("Pragma", "No-cache");
/* 157 */     response.setHeader("Cache-Control", "no-cache");
/* 158 */     response.setDateHeader("Expires", 0L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captch\\utils\CaptchaUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */