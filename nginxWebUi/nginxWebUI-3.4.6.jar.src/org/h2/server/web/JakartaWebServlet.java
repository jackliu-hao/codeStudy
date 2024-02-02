/*     */ package org.h2.server.web;
/*     */ 
/*     */ import jakarta.servlet.ServletConfig;
/*     */ import jakarta.servlet.ServletOutputStream;
/*     */ import jakarta.servlet.http.HttpServlet;
/*     */ import jakarta.servlet.http.HttpServletRequest;
/*     */ import jakarta.servlet.http.HttpServletResponse;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import org.h2.util.NetworkConnectionInfo;
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
/*     */ public class JakartaWebServlet
/*     */   extends HttpServlet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private transient WebServer server;
/*     */   
/*     */   public void init() {
/*  35 */     ServletConfig servletConfig = getServletConfig();
/*  36 */     Enumeration<E> enumeration = servletConfig.getInitParameterNames();
/*  37 */     ArrayList<String> arrayList = new ArrayList();
/*  38 */     while (enumeration.hasMoreElements()) {
/*  39 */       String str1 = enumeration.nextElement().toString();
/*  40 */       String str2 = servletConfig.getInitParameter(str1);
/*  41 */       if (!str1.startsWith("-")) {
/*  42 */         str1 = "-" + str1;
/*     */       }
/*  44 */       arrayList.add(str1);
/*  45 */       if (str2.length() > 0) {
/*  46 */         arrayList.add(str2);
/*     */       }
/*     */     } 
/*  49 */     String[] arrayOfString = arrayList.<String>toArray(new String[0]);
/*  50 */     this.server = new WebServer();
/*  51 */     this.server.setAllowChunked(false);
/*  52 */     this.server.init(arrayOfString);
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/*  57 */     this.server.stop();
/*     */   }
/*     */   
/*     */   private boolean allow(HttpServletRequest paramHttpServletRequest) {
/*  61 */     if (this.server.getAllowOthers()) {
/*  62 */       return true;
/*     */     }
/*  64 */     String str = paramHttpServletRequest.getRemoteAddr();
/*     */     try {
/*  66 */       InetAddress inetAddress = InetAddress.getByName(str);
/*  67 */       return inetAddress.isLoopbackAddress();
/*  68 */     } catch (UnknownHostException|NoClassDefFoundError unknownHostException) {
/*     */       
/*  70 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private String getAllowedFile(HttpServletRequest paramHttpServletRequest, String paramString) {
/*  76 */     if (!allow(paramHttpServletRequest)) {
/*  77 */       return "notAllowed.jsp";
/*     */     }
/*  79 */     if (paramString.length() == 0) {
/*  80 */       return "index.do";
/*     */     }
/*  82 */     return paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws IOException {
/*  88 */     paramHttpServletRequest.setCharacterEncoding("utf-8");
/*  89 */     String str1 = paramHttpServletRequest.getPathInfo();
/*  90 */     if (str1 == null) {
/*  91 */       paramHttpServletResponse.sendRedirect(paramHttpServletRequest.getRequestURI() + "/"); return;
/*     */     } 
/*  93 */     if (str1.startsWith("/")) {
/*  94 */       str1 = str1.substring(1);
/*     */     }
/*  96 */     str1 = getAllowedFile(paramHttpServletRequest, str1);
/*     */ 
/*     */     
/*  99 */     Properties properties = new Properties();
/* 100 */     Enumeration<E> enumeration = paramHttpServletRequest.getAttributeNames();
/* 101 */     while (enumeration.hasMoreElements()) {
/* 102 */       String str7 = enumeration.nextElement().toString();
/* 103 */       String str8 = paramHttpServletRequest.getAttribute(str7).toString();
/* 104 */       properties.put(str7, str8);
/*     */     } 
/* 106 */     enumeration = paramHttpServletRequest.getParameterNames();
/* 107 */     while (enumeration.hasMoreElements()) {
/* 108 */       String str7 = enumeration.nextElement().toString();
/* 109 */       String str8 = paramHttpServletRequest.getParameter(str7);
/* 110 */       properties.put(str7, str8);
/*     */     } 
/*     */     
/* 113 */     WebSession webSession = null;
/* 114 */     String str2 = properties.getProperty("jsessionid");
/* 115 */     if (str2 != null) {
/* 116 */       webSession = this.server.getSession(str2);
/*     */     }
/* 118 */     WebApp webApp = new WebApp(this.server);
/* 119 */     webApp.setSession(webSession, properties);
/* 120 */     String str3 = paramHttpServletRequest.getHeader("if-modified-since");
/*     */     
/* 122 */     String str4 = paramHttpServletRequest.getScheme();
/* 123 */     StringBuilder stringBuilder = (new StringBuilder(str4)).append("://").append(paramHttpServletRequest.getServerName());
/* 124 */     int i = paramHttpServletRequest.getServerPort();
/* 125 */     if ((i != 80 || !str4.equals("http")) && (i != 443 || !str4.equals("https"))) {
/* 126 */       stringBuilder.append(':').append(i);
/*     */     }
/* 128 */     String str5 = stringBuilder.append(paramHttpServletRequest.getContextPath()).toString();
/* 129 */     str1 = webApp.processRequest(str1, new NetworkConnectionInfo(str5, paramHttpServletRequest.getRemoteAddr(), paramHttpServletRequest.getRemotePort()));
/* 130 */     webSession = webApp.getSession();
/*     */     
/* 132 */     String str6 = webApp.getMimeType();
/* 133 */     boolean bool = webApp.getCache();
/*     */     
/* 135 */     if (bool && this.server.getStartDateTime().equals(str3)) {
/* 136 */       paramHttpServletResponse.setStatus(304);
/*     */       return;
/*     */     } 
/* 139 */     byte[] arrayOfByte = this.server.getFile(str1);
/* 140 */     if (arrayOfByte == null) {
/* 141 */       paramHttpServletResponse.sendError(404);
/* 142 */       arrayOfByte = ("File not found: " + str1).getBytes(StandardCharsets.UTF_8);
/*     */     } else {
/* 144 */       if (webSession != null && str1.endsWith(".jsp")) {
/* 145 */         String str = new String(arrayOfByte, StandardCharsets.UTF_8);
/* 146 */         str = PageParser.parse(str, webSession.map);
/* 147 */         arrayOfByte = str.getBytes(StandardCharsets.UTF_8);
/*     */       } 
/* 149 */       paramHttpServletResponse.setContentType(str6);
/* 150 */       if (!bool) {
/* 151 */         paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
/*     */       } else {
/* 153 */         paramHttpServletResponse.setHeader("Cache-Control", "max-age=10");
/* 154 */         paramHttpServletResponse.setHeader("Last-Modified", this.server.getStartDateTime());
/*     */       } 
/*     */     } 
/* 157 */     if (arrayOfByte != null) {
/* 158 */       ServletOutputStream servletOutputStream = paramHttpServletResponse.getOutputStream();
/* 159 */       servletOutputStream.write(arrayOfByte);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws IOException {
/* 166 */     doGet(paramHttpServletRequest, paramHttpServletResponse);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\web\JakartaWebServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */