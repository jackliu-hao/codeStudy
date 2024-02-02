/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.http.PushBuilder;
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
/*     */ public class PushBuilderImpl
/*     */   implements PushBuilder
/*     */ {
/*     */   private static final Set<HttpString> IGNORE;
/*     */   private static final Set<HttpString> CONDITIONAL;
/*     */   private static final Set<String> INVALID_METHOD;
/*     */   private final HttpServletRequestImpl servletRequest;
/*     */   private String method;
/*     */   private String queryString;
/*     */   private String sessionId;
/*     */   
/*     */   static {
/*  47 */     Set<HttpString> ignore = new HashSet<>();
/*  48 */     ignore.add(Headers.IF_MATCH);
/*  49 */     ignore.add(Headers.IF_NONE_MATCH);
/*  50 */     ignore.add(Headers.IF_MODIFIED_SINCE);
/*  51 */     ignore.add(Headers.IF_UNMODIFIED_SINCE);
/*  52 */     ignore.add(Headers.IF_RANGE);
/*  53 */     ignore.add(Headers.RANGE);
/*  54 */     ignore.add(Headers.ACCEPT_RANGES);
/*  55 */     ignore.add(Headers.EXPECT);
/*  56 */     ignore.add(Headers.REFERER);
/*  57 */     IGNORE = Collections.unmodifiableSet(ignore);
/*     */     
/*  59 */     Set<HttpString> conditional = new HashSet<>();
/*  60 */     conditional.add(Headers.IF_MATCH);
/*  61 */     conditional.add(Headers.IF_NONE_MATCH);
/*  62 */     conditional.add(Headers.IF_MODIFIED_SINCE);
/*  63 */     conditional.add(Headers.IF_UNMODIFIED_SINCE);
/*  64 */     conditional.add(Headers.IF_RANGE);
/*  65 */     CONDITIONAL = Collections.unmodifiableSet(conditional);
/*  66 */     Set<String> invalid = new HashSet<>();
/*  67 */     invalid.add("OPTIONS");
/*  68 */     invalid.add("PUT");
/*  69 */     invalid.add("POST");
/*  70 */     invalid.add("DELETE");
/*  71 */     invalid.add("CONNECT");
/*  72 */     invalid.add("TRACE");
/*  73 */     invalid.add("");
/*  74 */     INVALID_METHOD = Collections.unmodifiableSet(invalid);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   private final HeaderMap headers = new HeaderMap();
/*     */   
/*     */   private String path;
/*     */   
/*     */   public PushBuilderImpl(HttpServletRequestImpl servletRequest) {
/*  86 */     this.servletRequest = servletRequest;
/*  87 */     this.method = "GET";
/*  88 */     this.queryString = servletRequest.getQueryString();
/*  89 */     HttpSession session = servletRequest.getSession(false);
/*  90 */     if (session != null) {
/*  91 */       this.sessionId = session.getId();
/*     */     } else {
/*  93 */       this.sessionId = servletRequest.getRequestedSessionId();
/*     */     } 
/*     */     
/*  96 */     for (HeaderValues header : servletRequest.getExchange().getRequestHeaders()) {
/*  97 */       if (!IGNORE.contains(header.getHeaderName())) {
/*  98 */         this.headers.addAll(header.getHeaderName(), (Collection)header);
/*     */       }
/*     */     } 
/* 101 */     if (servletRequest.getQueryString() == null) {
/* 102 */       this.headers.add(Headers.REFERER, servletRequest.getRequestURL().toString());
/*     */     } else {
/* 104 */       this.headers.add(Headers.REFERER, servletRequest.getRequestURL() + "?" + servletRequest.getQueryString());
/*     */     } 
/* 106 */     this.path = null;
/* 107 */     for (Cookie cookie : servletRequest.getExchange().responseCookies()) {
/* 108 */       if (cookie.getMaxAge() != null && cookie.getMaxAge().intValue() <= 0) {
/*     */         
/* 110 */         HeaderValues existing = this.headers.get(Headers.COOKIE);
/* 111 */         if (existing != null) {
/* 112 */           Iterator<String> it = existing.iterator();
/* 113 */           while (it.hasNext()) {
/* 114 */             String val = it.next();
/* 115 */             if (val.startsWith(cookie.getName() + "="))
/* 116 */               it.remove(); 
/*     */           } 
/*     */         }  continue;
/*     */       } 
/* 120 */       if (!cookie.getName().equals(servletRequest.getServletContext().getSessionCookieConfig().getName())) {
/* 121 */         this.headers.add(Headers.COOKIE, cookie.getName() + "=" + cookie.getValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PushBuilder method(String method) {
/* 130 */     if (method == null) {
/* 131 */       throw UndertowServletMessages.MESSAGES.paramCannotBeNullNPE("method");
/*     */     }
/* 133 */     if (INVALID_METHOD.contains(method)) {
/* 134 */       throw UndertowServletMessages.MESSAGES.invalidMethodForPushRequest(method);
/*     */     }
/* 136 */     this.method = method;
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PushBuilder queryString(String queryString) {
/* 142 */     this.queryString = queryString;
/* 143 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PushBuilder sessionId(String sessionId) {
/* 148 */     this.sessionId = sessionId;
/* 149 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PushBuilder setHeader(String name, String value) {
/* 154 */     this.headers.put(new HttpString(name), value);
/* 155 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PushBuilder addHeader(String name, String value) {
/* 160 */     this.headers.add(new HttpString(name), value);
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PushBuilder removeHeader(String name) {
/* 166 */     this.headers.remove(name);
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public PushBuilder path(String path) {
/* 172 */     this.path = path;
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void push() {
/* 178 */     if (this.path == null) {
/* 179 */       throw UndertowServletMessages.MESSAGES.pathWasNotSet();
/*     */     }
/* 181 */     ServerConnection con = this.servletRequest.getExchange().getConnection();
/* 182 */     if (con.isPushSupported()) {
/* 183 */       HeaderMap newHeaders = new HeaderMap();
/* 184 */       for (HeaderValues entry : this.headers) {
/* 185 */         newHeaders.addAll(entry.getHeaderName(), (Collection)entry);
/*     */       }
/* 187 */       if (this.sessionId != null) {
/* 188 */         newHeaders.put(Headers.COOKIE, "JSESSIONID=" + this.sessionId);
/*     */       }
/* 190 */       String path = this.path;
/* 191 */       if (!path.startsWith("/")) {
/* 192 */         path = this.servletRequest.getContextPath() + "/" + path;
/*     */       }
/* 194 */       if (this.queryString != null && !this.queryString.isEmpty()) {
/* 195 */         if (path.contains("?")) {
/* 196 */           path = path + "&" + this.queryString;
/*     */         } else {
/* 198 */           path = path + "?" + this.queryString;
/*     */         } 
/*     */       }
/* 201 */       con.pushResource(path, new HttpString(this.method), newHeaders);
/*     */     } 
/* 203 */     this.path = null;
/* 204 */     for (HttpString h : CONDITIONAL) {
/* 205 */       this.headers.remove(h);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMethod() {
/* 211 */     return this.method;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getQueryString() {
/* 216 */     return this.queryString;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSessionId() {
/* 221 */     return this.sessionId;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getHeaderNames() {
/* 226 */     Set<String> names = new HashSet<>();
/* 227 */     for (HeaderValues name : this.headers) {
/* 228 */       names.add(name.getHeaderName().toString());
/*     */     }
/* 230 */     return names;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHeader(String name) {
/* 235 */     return this.headers.getFirst(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 240 */     return this.path;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\PushBuilderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */