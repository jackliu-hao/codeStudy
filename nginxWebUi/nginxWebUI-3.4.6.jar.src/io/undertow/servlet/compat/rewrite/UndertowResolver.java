/*     */ package io.undertow.servlet.compat.rewrite;
/*     */ 
/*     */ import io.undertow.server.handlers.resource.Resource;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.util.DateUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.Calendar;
/*     */ import javax.servlet.http.HttpServletRequest;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UndertowResolver
/*     */   extends Resolver
/*     */ {
/*     */   private final ServletRequestContext servletRequestContext;
/*     */   private final HttpServletRequest request;
/*     */   
/*     */   public UndertowResolver(ServletRequestContext servletRequestContext, HttpServletRequest request) {
/*  38 */     this.servletRequestContext = servletRequestContext;
/*  39 */     this.request = request;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String resolve(String key) {
/*  50 */     if (key.equals("HTTP_USER_AGENT"))
/*  51 */       return this.request.getHeader("user-agent"); 
/*  52 */     if (key.equals("HTTP_REFERER"))
/*  53 */       return this.request.getHeader("referer"); 
/*  54 */     if (key.equals("HTTP_COOKIE"))
/*  55 */       return this.request.getHeader("cookie"); 
/*  56 */     if (key.equals("HTTP_FORWARDED"))
/*  57 */       return this.request.getHeader("forwarded"); 
/*  58 */     if (key.equals("HTTP_HOST")) {
/*  59 */       String host = this.request.getHeader("host");
/*  60 */       int index = (host != null) ? host.indexOf(':') : -1;
/*  61 */       if (index != -1)
/*  62 */         host = host.substring(0, index); 
/*  63 */       return host;
/*  64 */     }  if (key.equals("HTTP_PROXY_CONNECTION"))
/*  65 */       return this.request.getHeader("proxy-connection"); 
/*  66 */     if (key.equals("HTTP_ACCEPT"))
/*  67 */       return this.request.getHeader("accept"); 
/*  68 */     if (key.equals("REMOTE_ADDR"))
/*  69 */       return this.request.getRemoteAddr(); 
/*  70 */     if (key.equals("REMOTE_HOST"))
/*  71 */       return this.request.getRemoteHost(); 
/*  72 */     if (key.equals("REMOTE_PORT"))
/*  73 */       return String.valueOf(this.request.getRemotePort()); 
/*  74 */     if (key.equals("REMOTE_USER"))
/*  75 */       return this.request.getRemoteUser(); 
/*  76 */     if (key.equals("REMOTE_IDENT"))
/*  77 */       return this.request.getRemoteUser(); 
/*  78 */     if (key.equals("REQUEST_METHOD"))
/*  79 */       return this.request.getMethod(); 
/*  80 */     if (key.equals("SCRIPT_FILENAME"))
/*  81 */       return this.request.getRealPath(this.request.getServletPath()); 
/*  82 */     if (key.equals("REQUEST_PATH"))
/*  83 */       return this.servletRequestContext.getExchange().getRelativePath(); 
/*  84 */     if (key.equals("CONTEXT_PATH"))
/*  85 */       return this.request.getContextPath(); 
/*  86 */     if (key.equals("SERVLET_PATH"))
/*  87 */       return emptyStringIfNull(this.request.getServletPath()); 
/*  88 */     if (key.equals("PATH_INFO"))
/*  89 */       return emptyStringIfNull(this.request.getPathInfo()); 
/*  90 */     if (key.equals("QUERY_STRING"))
/*  91 */       return emptyStringIfNull(this.request.getQueryString()); 
/*  92 */     if (key.equals("AUTH_TYPE"))
/*  93 */       return this.request.getAuthType(); 
/*  94 */     if (key.equals("DOCUMENT_ROOT"))
/*  95 */       return this.request.getRealPath("/"); 
/*  96 */     if (key.equals("SERVER_NAME"))
/*  97 */       return this.request.getLocalName(); 
/*  98 */     if (key.equals("SERVER_ADDR"))
/*  99 */       return this.request.getLocalAddr(); 
/* 100 */     if (key.equals("SERVER_PORT"))
/* 101 */       return String.valueOf(this.request.getLocalPort()); 
/* 102 */     if (key.equals("SERVER_PROTOCOL"))
/* 103 */       return this.request.getProtocol(); 
/* 104 */     if (key.equals("SERVER_SOFTWARE"))
/* 105 */       return "tomcat"; 
/* 106 */     if (key.equals("THE_REQUEST"))
/* 107 */       return this.request.getMethod() + " " + this.request.getRequestURI() + " " + this.request
/* 108 */         .getProtocol(); 
/* 109 */     if (key.equals("REQUEST_URI"))
/* 110 */       return this.request.getRequestURI(); 
/* 111 */     if (key.equals("REQUEST_FILENAME"))
/* 112 */       return this.request.getPathTranslated(); 
/* 113 */     if (key.equals("HTTPS"))
/* 114 */       return this.request.isSecure() ? "on" : "off"; 
/* 115 */     if (key.equals("TIME_YEAR"))
/* 116 */       return String.valueOf(Calendar.getInstance().get(1)); 
/* 117 */     if (key.equals("TIME_MON"))
/* 118 */       return String.valueOf(Calendar.getInstance().get(2)); 
/* 119 */     if (key.equals("TIME_DAY"))
/* 120 */       return String.valueOf(Calendar.getInstance().get(5)); 
/* 121 */     if (key.equals("TIME_HOUR"))
/* 122 */       return String.valueOf(Calendar.getInstance().get(11)); 
/* 123 */     if (key.equals("TIME_MIN"))
/* 124 */       return String.valueOf(Calendar.getInstance().get(12)); 
/* 125 */     if (key.equals("TIME_SEC"))
/* 126 */       return String.valueOf(Calendar.getInstance().get(13)); 
/* 127 */     if (key.equals("TIME_WDAY"))
/* 128 */       return String.valueOf(Calendar.getInstance().get(7)); 
/* 129 */     if (key.equals("TIME")) {
/* 130 */       return DateUtils.getCurrentDateTime(this.servletRequestContext.getExchange());
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */   
/*     */   public String resolveEnv(String key) {
/* 136 */     Object result = this.request.getAttribute(key);
/* 137 */     return (result != null) ? result.toString() : System.getProperty(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public String resolveSsl(String key) {
/* 142 */     return null;
/*     */   }
/*     */   
/*     */   public String resolveHttp(String key) {
/* 146 */     return this.request.getHeader(key);
/*     */   }
/*     */   
/*     */   public boolean resolveResource(int type, String name) {
/*     */     Resource resource;
/*     */     try {
/* 152 */       resource = this.servletRequestContext.getDeployment().getDeploymentInfo().getResourceManager().getResource(name);
/* 153 */     } catch (IOException e) {
/* 154 */       throw new RuntimeException(e);
/*     */     } 
/* 156 */     switch (type) {
/*     */       case 0:
/* 158 */         return (resource == null);
/*     */       case 1:
/* 160 */         return (resource != null);
/*     */       case 2:
/* 162 */         return (resource != null && resource
/* 163 */           .getContentLength().longValue() > 0L);
/*     */     } 
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String emptyStringIfNull(String value) {
/* 171 */     if (value == null) {
/* 172 */       return "";
/*     */     }
/* 174 */     return value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\UndertowResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */