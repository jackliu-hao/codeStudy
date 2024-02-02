/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestFactory;
/*     */ import org.apache.http.MethodNotSupportedException;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.message.BasicHttpEntityEnclosingRequest;
/*     */ import org.apache.http.message.BasicHttpRequest;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class DefaultHttpRequestFactory
/*     */   implements HttpRequestFactory
/*     */ {
/*  48 */   public static final DefaultHttpRequestFactory INSTANCE = new DefaultHttpRequestFactory();
/*     */   
/*  50 */   private static final String[] RFC2616_COMMON_METHODS = new String[] { "GET" };
/*     */ 
/*     */ 
/*     */   
/*  54 */   private static final String[] RFC2616_ENTITY_ENC_METHODS = new String[] { "POST", "PUT" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   private static final String[] RFC2616_SPECIAL_METHODS = new String[] { "HEAD", "OPTIONS", "DELETE", "TRACE", "CONNECT" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   private static final String[] RFC5789_ENTITY_ENC_METHODS = new String[] { "PATCH" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isOneOf(String[] methods, String method) {
/*  76 */     for (String method2 : methods) {
/*  77 */       if (method2.equalsIgnoreCase(method)) {
/*  78 */         return true;
/*     */       }
/*     */     } 
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequest newHttpRequest(RequestLine requestline) throws MethodNotSupportedException {
/*  87 */     Args.notNull(requestline, "Request line");
/*  88 */     String method = requestline.getMethod();
/*  89 */     if (isOneOf(RFC2616_COMMON_METHODS, method))
/*  90 */       return (HttpRequest)new BasicHttpRequest(requestline); 
/*  91 */     if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method))
/*  92 */       return (HttpRequest)new BasicHttpEntityEnclosingRequest(requestline); 
/*  93 */     if (isOneOf(RFC2616_SPECIAL_METHODS, method))
/*  94 */       return (HttpRequest)new BasicHttpRequest(requestline); 
/*  95 */     if (isOneOf(RFC5789_ENTITY_ENC_METHODS, method)) {
/*  96 */       return (HttpRequest)new BasicHttpEntityEnclosingRequest(requestline);
/*     */     }
/*  98 */     throw new MethodNotSupportedException(method + " method not supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequest newHttpRequest(String method, String uri) throws MethodNotSupportedException {
/* 105 */     if (isOneOf(RFC2616_COMMON_METHODS, method))
/* 106 */       return (HttpRequest)new BasicHttpRequest(method, uri); 
/* 107 */     if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method))
/* 108 */       return (HttpRequest)new BasicHttpEntityEnclosingRequest(method, uri); 
/* 109 */     if (isOneOf(RFC2616_SPECIAL_METHODS, method))
/* 110 */       return (HttpRequest)new BasicHttpRequest(method, uri); 
/* 111 */     if (isOneOf(RFC5789_ENTITY_ENC_METHODS, method)) {
/* 112 */       return (HttpRequest)new BasicHttpEntityEnclosingRequest(method, uri);
/*     */     }
/* 114 */     throw new MethodNotSupportedException(method + " method not supported");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultHttpRequestFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */