/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.attribute.StoredResponse;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.server.handlers.form.FormData;
/*     */ import io.undertow.server.handlers.form.FormDataParser;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.LocaleUtils;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class RequestDumpingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   
/*     */   public RequestDumpingHandler(HttpHandler next) {
/*  52 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  57 */     final StringBuilder sb = new StringBuilder();
/*     */     
/*  59 */     final SecurityContext sc = exchange.getSecurityContext();
/*  60 */     sb.append("\n----------------------------REQUEST---------------------------\n");
/*  61 */     sb.append("               URI=" + exchange.getRequestURI() + "\n");
/*  62 */     sb.append(" characterEncoding=" + exchange.getRequestHeaders().get(Headers.CONTENT_ENCODING) + "\n");
/*  63 */     sb.append("     contentLength=" + exchange.getRequestContentLength() + "\n");
/*  64 */     sb.append("       contentType=" + exchange.getRequestHeaders().get(Headers.CONTENT_TYPE) + "\n");
/*  65 */     if (sc != null) {
/*  66 */       if (sc.isAuthenticated()) {
/*  67 */         sb.append("          authType=" + sc.getMechanismName() + "\n");
/*  68 */         sb.append("         principle=" + sc.getAuthenticatedAccount().getPrincipal() + "\n");
/*     */       } else {
/*  70 */         sb.append("          authType=none\n");
/*     */       } 
/*     */     }
/*     */     
/*  74 */     for (Cookie cookie : exchange.requestCookies()) {
/*  75 */       sb.append("            cookie=" + cookie.getName() + "=" + cookie
/*  76 */           .getValue() + "\n");
/*     */     }
/*  78 */     for (HeaderValues header : exchange.getRequestHeaders()) {
/*  79 */       for (String value : header) {
/*  80 */         sb.append("            header=" + header.getHeaderName() + "=" + value + "\n");
/*     */       }
/*     */     } 
/*  83 */     sb.append("            locale=" + LocaleUtils.getLocalesFromHeader((List)exchange.getRequestHeaders().get(Headers.ACCEPT_LANGUAGE)) + "\n");
/*  84 */     sb.append("            method=" + exchange.getRequestMethod() + "\n");
/*  85 */     Map<String, Deque<String>> pnames = exchange.getQueryParameters();
/*  86 */     for (Map.Entry<String, Deque<String>> entry : pnames.entrySet()) {
/*  87 */       String pname = entry.getKey();
/*  88 */       Iterator<String> pvalues = ((Deque<String>)entry.getValue()).iterator();
/*  89 */       sb.append("         parameter=");
/*  90 */       sb.append(pname);
/*  91 */       sb.append('=');
/*  92 */       while (pvalues.hasNext()) {
/*  93 */         sb.append(pvalues.next());
/*  94 */         if (pvalues.hasNext()) {
/*  95 */           sb.append(", ");
/*     */         }
/*     */       } 
/*  98 */       sb.append("\n");
/*     */     } 
/* 100 */     sb.append("          protocol=" + exchange.getProtocol() + "\n");
/* 101 */     sb.append("       queryString=" + exchange.getQueryString() + "\n");
/* 102 */     sb.append("        remoteAddr=" + exchange.getSourceAddress() + "\n");
/* 103 */     sb.append("        remoteHost=" + exchange.getSourceAddress().getHostName() + "\n");
/* 104 */     sb.append("            scheme=" + exchange.getRequestScheme() + "\n");
/* 105 */     sb.append("              host=" + exchange.getRequestHeaders().getFirst(Headers.HOST) + "\n");
/* 106 */     sb.append("        serverPort=" + exchange.getDestinationAddress().getPort() + "\n");
/* 107 */     sb.append("          isSecure=" + exchange.isSecure() + "\n");
/*     */     
/* 109 */     exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */         {
/*     */           public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener)
/*     */           {
/* 113 */             RequestDumpingHandler.this.dumpRequestBody(exchange, sb);
/*     */ 
/*     */             
/* 116 */             sb.append("--------------------------RESPONSE--------------------------\n");
/* 117 */             if (sc != null) {
/* 118 */               if (sc.isAuthenticated()) {
/* 119 */                 sb.append("          authType=" + sc.getMechanismName() + "\n");
/* 120 */                 sb.append("         principle=" + sc.getAuthenticatedAccount().getPrincipal() + "\n");
/*     */               } else {
/* 122 */                 sb.append("          authType=none\n");
/*     */               } 
/*     */             }
/* 125 */             sb.append("     contentLength=" + exchange.getResponseContentLength() + "\n");
/* 126 */             sb.append("       contentType=" + exchange.getResponseHeaders().getFirst(Headers.CONTENT_TYPE) + "\n");
/* 127 */             for (Cookie cookie : exchange.responseCookies()) {
/* 128 */               sb.append("            cookie=" + cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain() + "; path=" + cookie.getPath() + "\n");
/*     */             }
/* 130 */             for (HeaderValues header : exchange.getResponseHeaders()) {
/* 131 */               for (String value : header) {
/* 132 */                 sb.append("            header=" + header.getHeaderName() + "=" + value + "\n");
/*     */               }
/*     */             } 
/* 135 */             sb.append("            status=" + exchange.getStatusCode() + "\n");
/* 136 */             String storedResponse = StoredResponse.INSTANCE.readAttribute(exchange);
/* 137 */             if (storedResponse != null) {
/* 138 */               sb.append("body=\n");
/* 139 */               sb.append(storedResponse);
/*     */             } 
/*     */             
/* 142 */             sb.append("\n==============================================================");
/*     */ 
/*     */             
/* 145 */             nextListener.proceed();
/* 146 */             UndertowLogger.REQUEST_DUMPER_LOGGER.info(sb.toString());
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */     
/* 152 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private void dumpRequestBody(HttpServerExchange exchange, StringBuilder sb) {
/*     */     try {
/* 157 */       FormData formData = (FormData)exchange.getAttachment(FormDataParser.FORM_DATA);
/* 158 */       if (formData != null) {
/* 159 */         sb.append("body=\n");
/*     */         
/* 161 */         for (String formField : formData) {
/* 162 */           Deque<FormData.FormValue> formValues = formData.get(formField);
/*     */           
/* 164 */           sb.append(formField)
/* 165 */             .append("=");
/* 166 */           for (FormData.FormValue formValue : formValues) {
/* 167 */             sb.append(formValue.isFileItem() ? "[file-content]" : formValue.getValue());
/* 168 */             sb.append("\n");
/*     */             
/* 170 */             if (formValue.getHeaders() != null) {
/* 171 */               sb.append("headers=\n");
/* 172 */               for (HeaderValues header : formValue.getHeaders()) {
/* 173 */                 sb.append("\t")
/* 174 */                   .append(header.getHeaderName()).append("=").append(header.getFirst()).append("\n");
/*     */               }
/*     */             }
/*     */           
/*     */           } 
/*     */         } 
/*     */       } 
/* 181 */     } catch (Exception e) {
/* 182 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 188 */     return "dump-request()";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 196 */       return "dump-request";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 201 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 206 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 211 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 217 */       return new RequestDumpingHandler.Wrapper();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper implements HandlerWrapper {
/*     */     private Wrapper() {}
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 225 */       return new RequestDumpingHandler(handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\RequestDumpingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */