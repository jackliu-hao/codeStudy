/*     */ package io.undertow.servlet.compat.rewrite;
/*     */ 
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.spec.HttpServletRequestImpl;
/*     */ import io.undertow.servlet.spec.HttpServletResponseImpl;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.QueryParameterUtils;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.Cookie;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RewriteHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final RewriteConfig config;
/*     */   private final HttpHandler next;
/*  48 */   protected ThreadLocal<Boolean> invoked = new ThreadLocal<>();
/*     */   
/*     */   public RewriteHandler(RewriteConfig config, HttpHandler next) {
/*  51 */     this.config = config;
/*  52 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  57 */     RewriteRule[] rules = this.config.getRules();
/*  58 */     if (rules == null || rules.length == 0) {
/*  59 */       this.next.handleRequest(exchange);
/*     */       
/*     */       return;
/*     */     } 
/*  63 */     if (Boolean.TRUE.equals(this.invoked.get())) {
/*  64 */       this.next.handleRequest(exchange);
/*  65 */       this.invoked.set(null);
/*     */       return;
/*     */     } 
/*  68 */     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  69 */     HttpServletRequestImpl request = src.getOriginalRequest();
/*  70 */     HttpServletResponseImpl response = src.getOriginalResponse();
/*  71 */     UndertowResolver resolver = new UndertowResolver(src, (HttpServletRequest)src.getOriginalRequest());
/*     */     
/*  73 */     this.invoked.set(Boolean.TRUE);
/*     */ 
/*     */ 
/*     */     
/*  77 */     CharSequence url = exchange.getRelativePath();
/*  78 */     CharSequence host = request.getServerName();
/*  79 */     boolean rewritten = false;
/*  80 */     boolean done = false;
/*  81 */     for (int i = 0; i < rules.length; i++) {
/*  82 */       CharSequence test = rules[i].isHost() ? host : url;
/*  83 */       CharSequence newtest = rules[i].evaluate(test, resolver);
/*  84 */       if (newtest != null && !test.equals(newtest.toString())) {
/*  85 */         if (UndertowServletLogger.REQUEST_LOGGER.isDebugEnabled()) {
/*  86 */           UndertowServletLogger.REQUEST_LOGGER.debug("Rewrote " + test + " as " + newtest + " with rule pattern " + rules[i]
/*  87 */               .getPatternString());
/*     */         }
/*  89 */         if (rules[i].isHost()) {
/*  90 */           host = newtest;
/*     */         } else {
/*  92 */           url = newtest;
/*     */         } 
/*  94 */         rewritten = true;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 100 */       if (rules[i].isForbidden() && newtest != null) {
/* 101 */         response.sendError(403);
/* 102 */         done = true;
/*     */         
/*     */         break;
/*     */       } 
/* 106 */       if (rules[i].isGone() && newtest != null) {
/* 107 */         response.sendError(410);
/* 108 */         done = true;
/*     */         
/*     */         break;
/*     */       } 
/* 112 */       if (rules[i].isRedirect() && newtest != null) {
/*     */         
/* 114 */         String queryString = request.getQueryString();
/* 115 */         StringBuffer urlString = new StringBuffer(url);
/* 116 */         if (queryString != null && queryString.length() > 0) {
/* 117 */           int index = urlString.indexOf("?");
/* 118 */           if (index != -1) {
/*     */             
/* 120 */             if (rules[i].isQsappend()) {
/* 121 */               urlString.append('&');
/* 122 */               urlString.append(queryString);
/*     */ 
/*     */             
/*     */             }
/* 126 */             else if (index == urlString.length() - 1) {
/* 127 */               urlString.deleteCharAt(index);
/*     */             } 
/*     */           } else {
/* 130 */             urlString.append('?');
/* 131 */             urlString.append(queryString);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 138 */         if (urlString.charAt(0) == '/' && !hasScheme(urlString)) {
/* 139 */           urlString.insert(0, request.getContextPath());
/*     */         }
/* 141 */         response.sendRedirect(urlString.toString());
/* 142 */         response.setStatus(rules[i].getRedirectCode());
/* 143 */         done = true;
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */ 
/*     */       
/* 150 */       if (rules[i].isCookie() && newtest != null) {
/*     */         
/* 152 */         Cookie cookie = new Cookie(rules[i].getCookieName(), rules[i].getCookieResult());
/* 153 */         cookie.setDomain(rules[i].getCookieDomain());
/* 154 */         cookie.setMaxAge(rules[i].getCookieLifetime());
/* 155 */         cookie.setPath(rules[i].getCookiePath());
/* 156 */         cookie.setSecure(rules[i].isCookieSecure());
/* 157 */         cookie.setHttpOnly(rules[i].isCookieHttpOnly());
/* 158 */         response.addCookie(cookie);
/*     */       } 
/*     */       
/* 161 */       if (rules[i].isEnv() && newtest != null) {
/* 162 */         Map<String, String> attrs = (Map<String, String>)exchange.getAttachment(HttpServerExchange.REQUEST_ATTRIBUTES);
/* 163 */         if (attrs == null) {
/* 164 */           attrs = new HashMap<>();
/* 165 */           exchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, attrs);
/*     */         } 
/* 167 */         for (int j = 0; j < rules[i].getEnvSize(); j++) {
/* 168 */           String envName = rules[i].getEnvName(j);
/* 169 */           String envResult = rules[i].getEnvResult(j);
/* 170 */           attrs.put(envName, envResult);
/* 171 */           request.setAttribute(envName, envResult);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 176 */       if (rules[i].isType() && newtest != null) {
/* 177 */         exchange.getRequestHeaders().put(Headers.CONTENT_TYPE, rules[i].getTypeValue());
/*     */       }
/*     */       
/* 180 */       if (rules[i].isQsappend() && newtest != null) {
/* 181 */         String queryString = request.getQueryString();
/* 182 */         String urlString = url.toString();
/* 183 */         if (urlString.indexOf('?') != -1 && queryString != null) {
/* 184 */           url = urlString + "&" + queryString;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 191 */       if (rules[i].isChain() && newtest == null) {
/* 192 */         for (int j = i; j < rules.length; j++) {
/* 193 */           if (!rules[j].isChain()) {
/* 194 */             i = j;
/*     */ 
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } else {
/* 201 */         if (rules[i].isLast() && newtest != null) {
/*     */           break;
/*     */         }
/*     */         
/* 205 */         if (rules[i].isNext() && newtest != null) {
/* 206 */           i = 0;
/*     */ 
/*     */         
/*     */         }
/* 210 */         else if (newtest != null) {
/* 211 */           i += rules[i].getSkip();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 216 */     if (rewritten) {
/* 217 */       if (!done) {
/*     */         
/* 219 */         String urlString = url.toString();
/* 220 */         String queryString = null;
/* 221 */         int queryIndex = urlString.indexOf('?');
/* 222 */         if (queryIndex != -1) {
/* 223 */           queryString = urlString.substring(queryIndex + 1);
/* 224 */           urlString = urlString.substring(0, queryIndex);
/*     */         } 
/*     */         
/* 227 */         StringBuilder chunk = new StringBuilder();
/* 228 */         chunk.append(request.getContextPath());
/* 229 */         chunk.append(urlString);
/* 230 */         String requestPath = chunk.toString();
/* 231 */         exchange.setRequestURI(requestPath);
/* 232 */         exchange.setRequestPath(requestPath);
/* 233 */         exchange.setRelativePath(urlString);
/*     */ 
/*     */         
/* 236 */         if (queryString != null) {
/* 237 */           exchange.setQueryString(queryString);
/* 238 */           exchange.getQueryParameters().clear();
/* 239 */           exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(queryString, (String)exchange.getConnection().getUndertowOptions().get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name())));
/*     */         } 
/*     */         
/* 242 */         if (!host.equals(request.getServerName())) {
/* 243 */           exchange.getRequestHeaders().put(Headers.HOST, host + ":" + exchange.getHostPort());
/*     */         }
/*     */         
/* 246 */         src.getDeployment().getHandler().handleRequest(exchange);
/*     */       } 
/*     */     } else {
/* 249 */       this.next.handleRequest(exchange);
/*     */     } 
/*     */     
/* 252 */     this.invoked.set(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean hasScheme(StringBuffer uri) {
/* 261 */     int len = uri.length();
/* 262 */     for (int i = 0; i < len; i++) {
/* 263 */       char c = uri.charAt(i);
/* 264 */       if (c == ':')
/* 265 */         return (i > 0); 
/* 266 */       if (!isSchemeChar(c)) {
/* 267 */         return false;
/*     */       }
/*     */     } 
/* 270 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isSchemeChar(char c) {
/* 278 */     return (Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '.');
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\RewriteHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */