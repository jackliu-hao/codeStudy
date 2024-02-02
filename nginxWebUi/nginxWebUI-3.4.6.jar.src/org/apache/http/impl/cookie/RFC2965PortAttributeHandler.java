/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.ClientCookie;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookieRestrictionViolationException;
/*     */ import org.apache.http.cookie.MalformedCookieException;
/*     */ import org.apache.http.cookie.SetCookie;
/*     */ import org.apache.http.cookie.SetCookie2;
/*     */ import org.apache.http.util.Args;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class RFC2965PortAttributeHandler
/*     */   implements CommonCookieAttributeHandler
/*     */ {
/*     */   private static int[] parsePortAttribute(String portValue) throws MalformedCookieException {
/*  67 */     StringTokenizer st = new StringTokenizer(portValue, ",");
/*  68 */     int[] ports = new int[st.countTokens()];
/*     */     try {
/*  70 */       int i = 0;
/*  71 */       while (st.hasMoreTokens()) {
/*  72 */         ports[i] = Integer.parseInt(st.nextToken().trim());
/*  73 */         if (ports[i] < 0) {
/*  74 */           throw new MalformedCookieException("Invalid Port attribute.");
/*     */         }
/*  76 */         i++;
/*     */       } 
/*  78 */     } catch (NumberFormatException e) {
/*  79 */       throw new MalformedCookieException("Invalid Port attribute: " + e.getMessage());
/*     */     } 
/*     */     
/*  82 */     return ports;
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
/*     */   private static boolean portMatch(int port, int[] ports) {
/*  95 */     boolean portInList = false;
/*  96 */     for (int port2 : ports) {
/*  97 */       if (port == port2) {
/*  98 */         portInList = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 102 */     return portInList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void parse(SetCookie cookie, String portValue) throws MalformedCookieException {
/* 111 */     Args.notNull(cookie, "Cookie");
/* 112 */     if (cookie instanceof SetCookie2) {
/* 113 */       SetCookie2 cookie2 = (SetCookie2)cookie;
/* 114 */       if (portValue != null && !portValue.trim().isEmpty()) {
/* 115 */         int[] ports = parsePortAttribute(portValue);
/* 116 */         cookie2.setPorts(ports);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
/* 128 */     Args.notNull(cookie, "Cookie");
/* 129 */     Args.notNull(origin, "Cookie origin");
/* 130 */     int port = origin.getPort();
/* 131 */     if (cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("port"))
/*     */     {
/* 133 */       if (!portMatch(port, cookie.getPorts())) {
/* 134 */         throw new CookieRestrictionViolationException("Port attribute violates RFC 2965: Request port not found in cookie's port list.");
/*     */       }
/*     */     }
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
/*     */   public boolean match(Cookie cookie, CookieOrigin origin) {
/* 148 */     Args.notNull(cookie, "Cookie");
/* 149 */     Args.notNull(origin, "Cookie origin");
/* 150 */     int port = origin.getPort();
/* 151 */     if (cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("port")) {
/*     */       
/* 153 */       if (cookie.getPorts() == null)
/*     */       {
/* 155 */         return false;
/*     */       }
/* 157 */       if (!portMatch(port, cookie.getPorts())) {
/* 158 */         return false;
/*     */       }
/*     */     } 
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttributeName() {
/* 166 */     return "port";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\RFC2965PortAttributeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */