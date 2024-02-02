/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.api.CrawlerSessionManagerConfig;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.servlet.http.HttpSession;
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
/*     */ public class CrawlerSessionManagerHandler
/*     */   implements HttpHandler
/*     */ {
/*  47 */   private static final String SESSION_ATTRIBUTE_NAME = "listener_" + CrawlerSessionManagerHandler.class.getName();
/*     */   
/*  49 */   private final Map<String, String> clientIpSessionId = new ConcurrentHashMap<>();
/*  50 */   private final Map<String, String> sessionIdClientIp = new ConcurrentHashMap<>();
/*     */   
/*     */   private final CrawlerSessionManagerConfig config;
/*     */   
/*     */   private final Pattern uaPattern;
/*     */   
/*     */   private final HttpHandler next;
/*     */   
/*     */   public CrawlerSessionManagerHandler(CrawlerSessionManagerConfig config, HttpHandler next) {
/*  59 */     this.config = config;
/*  60 */     this.next = next;
/*  61 */     this.uaPattern = Pattern.compile(config.getCrawlerUserAgents());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  68 */     boolean isBot = false;
/*  69 */     String sessionId = null;
/*  70 */     String clientIp = null;
/*  71 */     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*     */ 
/*     */     
/*  74 */     if (src.getOriginalRequest().getSession(false) == null) {
/*     */ 
/*     */       
/*  77 */       HeaderValues userAgentHeaders = exchange.getRequestHeaders().get(Headers.USER_AGENT);
/*  78 */       if (userAgentHeaders != null) {
/*  79 */         Iterator<String> uaHeaders = userAgentHeaders.iterator();
/*  80 */         String uaHeader = null;
/*  81 */         if (uaHeaders.hasNext()) {
/*  82 */           uaHeader = uaHeaders.next();
/*     */         }
/*     */ 
/*     */         
/*  86 */         if (uaHeader != null && !uaHeaders.hasNext())
/*     */         {
/*  88 */           if (this.uaPattern.matcher(uaHeader).matches()) {
/*  89 */             isBot = true;
/*     */             
/*  91 */             if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
/*  92 */               UndertowLogger.REQUEST_LOGGER.debug(exchange + ": Bot found. UserAgent=" + uaHeader);
/*     */             }
/*     */           } 
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 100 */         if (isBot) {
/* 101 */           clientIp = src.getServletRequest().getRemoteAddr();
/* 102 */           sessionId = this.clientIpSessionId.get(clientIp);
/* 103 */           if (sessionId != null) {
/* 104 */             src.setOverridenSessionId(sessionId);
/* 105 */             if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
/* 106 */               UndertowLogger.REQUEST_LOGGER.debug(exchange + ": SessionID=" + sessionId);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 114 */     if (isBot) {
/* 115 */       final String finalSessionId = sessionId;
/* 116 */       final String finalClientId = clientIp;
/* 117 */       exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener)
/*     */             {
/*     */               try {
/* 123 */                 ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 124 */                 if (finalSessionId == null) {
/*     */                   
/* 126 */                   HttpSession s = src.getOriginalRequest().getSession(false);
/* 127 */                   if (s != null) {
/* 128 */                     CrawlerSessionManagerHandler.this.clientIpSessionId.put(finalClientId, s.getId());
/* 129 */                     CrawlerSessionManagerHandler.this.sessionIdClientIp.put(s.getId(), finalClientId);
/*     */                     
/* 131 */                     s.setAttribute(CrawlerSessionManagerHandler.SESSION_ATTRIBUTE_NAME, new CrawlerBindingListener(CrawlerSessionManagerHandler.this.clientIpSessionId, CrawlerSessionManagerHandler.this.sessionIdClientIp));
/* 132 */                     s.setMaxInactiveInterval(CrawlerSessionManagerHandler.this.config.getSessionInactiveInterval());
/*     */                     
/* 134 */                     if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
/* 135 */                       UndertowLogger.REQUEST_LOGGER.debug(exchange + ": New bot session. SessionID=" + s
/* 136 */                           .getId());
/*     */                     }
/*     */                   }
/*     */                 
/* 140 */                 } else if (UndertowLogger.REQUEST_LOGGER.isDebugEnabled()) {
/* 141 */                   UndertowLogger.REQUEST_LOGGER.debug(exchange + ": Bot session accessed. SessionID=" + finalSessionId);
/*     */                 }
/*     */               
/*     */               } finally {
/*     */                 
/* 146 */                 nextListener.proceed();
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*     */     
/* 152 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\CrawlerSessionManagerHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */