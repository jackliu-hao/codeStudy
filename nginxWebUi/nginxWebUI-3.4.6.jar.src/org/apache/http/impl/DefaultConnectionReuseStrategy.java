/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.TokenIterator;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.message.BasicTokenIterator;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class DefaultConnectionReuseStrategy
/*     */   implements ConnectionReuseStrategy
/*     */ {
/*  70 */   public static final DefaultConnectionReuseStrategy INSTANCE = new DefaultConnectionReuseStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean keepAlive(HttpResponse response, HttpContext context) {
/*  80 */     Args.notNull(response, "HTTP response");
/*  81 */     Args.notNull(context, "HTTP context");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  86 */     if (response.getStatusLine().getStatusCode() == 204) {
/*  87 */       Header clh = response.getFirstHeader("Content-Length");
/*  88 */       if (clh != null) {
/*     */         try {
/*  90 */           int contentLen = Integer.parseInt(clh.getValue());
/*  91 */           if (contentLen > 0) {
/*  92 */             return false;
/*     */           }
/*  94 */         } catch (NumberFormatException ex) {}
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  99 */       Header header1 = response.getFirstHeader("Transfer-Encoding");
/* 100 */       if (header1 != null) {
/* 101 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 105 */     HttpRequest request = (HttpRequest)context.getAttribute("http.request");
/* 106 */     if (request != null) {
/*     */       try {
/* 108 */         BasicTokenIterator basicTokenIterator = new BasicTokenIterator(request.headerIterator("Connection"));
/* 109 */         while (basicTokenIterator.hasNext()) {
/* 110 */           String token = basicTokenIterator.nextToken();
/* 111 */           if ("Close".equalsIgnoreCase(token)) {
/* 112 */             return false;
/*     */           }
/*     */         } 
/* 115 */       } catch (ParseException px) {
/*     */         
/* 117 */         return false;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 123 */     ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
/* 124 */     Header teh = response.getFirstHeader("Transfer-Encoding");
/* 125 */     if (teh != null) {
/* 126 */       if (!"chunked".equalsIgnoreCase(teh.getValue())) {
/* 127 */         return false;
/*     */       }
/*     */     }
/* 130 */     else if (canResponseHaveBody(request, response)) {
/* 131 */       Header[] clhs = response.getHeaders("Content-Length");
/*     */       
/* 133 */       if (clhs.length == 1) {
/* 134 */         Header clh = clhs[0];
/*     */         try {
/* 136 */           long contentLen = Long.parseLong(clh.getValue());
/* 137 */           if (contentLen < 0L) {
/* 138 */             return false;
/*     */           }
/* 140 */         } catch (NumberFormatException ex) {
/* 141 */           return false;
/*     */         } 
/*     */       } else {
/* 144 */         return false;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     HeaderIterator headerIterator = response.headerIterator("Connection");
/* 153 */     if (!headerIterator.hasNext()) {
/* 154 */       headerIterator = response.headerIterator("Proxy-Connection");
/*     */     }
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
/* 180 */     if (headerIterator.hasNext()) {
/*     */       try {
/* 182 */         BasicTokenIterator basicTokenIterator = new BasicTokenIterator(headerIterator);
/* 183 */         boolean keepalive = false;
/* 184 */         while (basicTokenIterator.hasNext()) {
/* 185 */           String token = basicTokenIterator.nextToken();
/* 186 */           if ("Close".equalsIgnoreCase(token))
/* 187 */             return false; 
/* 188 */           if ("Keep-Alive".equalsIgnoreCase(token))
/*     */           {
/* 190 */             keepalive = true;
/*     */           }
/*     */         } 
/* 193 */         if (keepalive) {
/* 194 */           return true;
/*     */         
/*     */         }
/*     */       }
/* 198 */       catch (ParseException px) {
/*     */         
/* 200 */         return false;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 205 */     return !ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0);
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
/*     */   protected TokenIterator createTokenIterator(HeaderIterator hit) {
/* 219 */     return (TokenIterator)new BasicTokenIterator(hit);
/*     */   }
/*     */   
/*     */   private boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
/* 223 */     if (request != null && request.getRequestLine().getMethod().equalsIgnoreCase("HEAD")) {
/* 224 */       return false;
/*     */     }
/* 226 */     int status = response.getStatusLine().getStatusCode();
/* 227 */     return (status >= 200 && status != 204 && status != 304 && status != 205);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultConnectionReuseStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */