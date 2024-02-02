/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ResponseCommitListener;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.SameSiteNoneIncompatibleClientChecker;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class SameSiteCookieHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final String mode;
/*     */   private final Pattern cookiePattern;
/*     */   private final boolean enableClientChecker;
/*     */   private final boolean addSecureForNone;
/*     */   
/*     */   public SameSiteCookieHandler(HttpHandler next, String mode) {
/*  48 */     this(next, mode, null, true, true, true);
/*     */   }
/*     */   
/*     */   public SameSiteCookieHandler(HttpHandler next, String mode, String cookiePattern) {
/*  52 */     this(next, mode, cookiePattern, true, true, true);
/*     */   }
/*     */   
/*     */   public SameSiteCookieHandler(HttpHandler next, String mode, String cookiePattern, boolean caseSensitive) {
/*  56 */     this(next, mode, cookiePattern, caseSensitive, true, true);
/*     */   }
/*     */   
/*     */   public SameSiteCookieHandler(HttpHandler next, String mode, String cookiePattern, boolean caseSensitive, boolean enableClientChecker, boolean addSecureForNone) {
/*  60 */     this.next = next;
/*  61 */     this.mode = mode;
/*  62 */     if (cookiePattern != null && !cookiePattern.isEmpty()) {
/*  63 */       this.cookiePattern = Pattern.compile(cookiePattern, caseSensitive ? 0 : 2);
/*     */     } else {
/*  65 */       this.cookiePattern = null;
/*     */     } 
/*  67 */     boolean modeIsNone = CookieSameSiteMode.NONE.toString().equalsIgnoreCase(mode);
/*  68 */     this.enableClientChecker = (enableClientChecker && modeIsNone);
/*  69 */     this.addSecureForNone = (addSecureForNone && modeIsNone);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  74 */     if (this.mode != null) {
/*  75 */       exchange.addResponseCommitListener(new ResponseCommitListener()
/*     */           {
/*     */             public void beforeCommit(HttpServerExchange exchange)
/*     */             {
/*  79 */               String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
/*  80 */               if (SameSiteCookieHandler.this.enableClientChecker && userAgent != null && !SameSiteNoneIncompatibleClientChecker.shouldSendSameSiteNone(userAgent)) {
/*     */                 return;
/*     */               }
/*  83 */               for (Cookie cookie : exchange.responseCookies()) {
/*  84 */                 if (SameSiteCookieHandler.this.cookiePattern == null || SameSiteCookieHandler.this.cookiePattern.matcher(cookie.getName()).matches()) {
/*     */ 
/*     */                   
/*  87 */                   cookie.setSameSiteMode(SameSiteCookieHandler.this.mode);
/*  88 */                   if (SameSiteCookieHandler.this.addSecureForNone)
/*     */                   {
/*  90 */                     cookie.setSecure(true);
/*     */                   }
/*     */                 } 
/*     */               } 
/*     */             }
/*     */           });
/*     */     }
/*  97 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 104 */       return "samesite-cookie";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 109 */       Map<String, Class<?>> parameters = new HashMap<>();
/* 110 */       parameters.put("mode", String.class);
/* 111 */       parameters.put("cookie-pattern", String.class);
/* 112 */       parameters.put("case-sensitive", Boolean.class);
/* 113 */       parameters.put("enable-client-checker", Boolean.class);
/* 114 */       parameters.put("add-secure-for-none", Boolean.class);
/* 115 */       return parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 120 */       return Collections.singleton("mode");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 125 */       return "mode";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 130 */       final String mode = (String)config.get("mode");
/* 131 */       final String pattern = (String)config.get("cookie-pattern");
/* 132 */       final Boolean caseSensitive = (Boolean)config.get("case-sensitive");
/* 133 */       final Boolean enableClientChecker = (Boolean)config.get("enable-client-checker");
/* 134 */       final Boolean addSecureForNone = (Boolean)config.get("add-secure-for-none");
/* 135 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 138 */             return new SameSiteCookieHandler(handler, mode, pattern, (caseSensitive == null) ? true : caseSensitive
/* 139 */                 .booleanValue(), (enableClientChecker == null) ? true : enableClientChecker
/* 140 */                 .booleanValue(), (addSecureForNone == null) ? true : addSecureForNone
/* 141 */                 .booleanValue());
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\SameSiteCookieHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */