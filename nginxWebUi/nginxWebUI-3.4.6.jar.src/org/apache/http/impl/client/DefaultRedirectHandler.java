/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.CircularRedirectException;
/*     */ import org.apache.http.client.RedirectHandler;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class DefaultRedirectHandler
/*     */   implements RedirectHandler
/*     */ {
/*  66 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
/*     */     HttpRequest request;
/*     */     String method;
/*  78 */     Args.notNull(response, "HTTP response");
/*     */     
/*  80 */     int statusCode = response.getStatusLine().getStatusCode();
/*  81 */     switch (statusCode) {
/*     */       case 301:
/*     */       case 302:
/*     */       case 307:
/*  85 */         request = (HttpRequest)context.getAttribute("http.request");
/*     */         
/*  87 */         method = request.getRequestLine().getMethod();
/*  88 */         return (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("HEAD"));
/*     */       
/*     */       case 303:
/*  91 */         return true;
/*     */     } 
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
/*     */     URI uri;
/* 101 */     Args.notNull(response, "HTTP response");
/*     */     
/* 103 */     Header locationHeader = response.getFirstHeader("location");
/* 104 */     if (locationHeader == null)
/*     */     {
/* 106 */       throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
/*     */     }
/*     */ 
/*     */     
/* 110 */     String location = locationHeader.getValue();
/* 111 */     if (this.log.isDebugEnabled()) {
/* 112 */       this.log.debug("Redirect requested to location '" + location + "'");
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 117 */       uri = new URI(location);
/* 118 */     } catch (URISyntaxException ex) {
/* 119 */       throw new ProtocolException("Invalid redirect URI: " + location, ex);
/*     */     } 
/*     */     
/* 122 */     HttpParams params = response.getParams();
/*     */ 
/*     */     
/* 125 */     if (!uri.isAbsolute()) {
/* 126 */       if (params.isParameterTrue("http.protocol.reject-relative-redirect")) {
/* 127 */         throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
/*     */       }
/*     */ 
/*     */       
/* 131 */       HttpHost target = (HttpHost)context.getAttribute("http.target_host");
/*     */       
/* 133 */       Asserts.notNull(target, "Target host");
/*     */       
/* 135 */       HttpRequest request = (HttpRequest)context.getAttribute("http.request");
/*     */ 
/*     */       
/*     */       try {
/* 139 */         URI requestURI = new URI(request.getRequestLine().getUri());
/* 140 */         URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, URIUtils.DROP_FRAGMENT_AND_NORMALIZE);
/* 141 */         uri = URIUtils.resolve(absoluteRequestURI, uri);
/* 142 */       } catch (URISyntaxException ex) {
/* 143 */         throw new ProtocolException(ex.getMessage(), ex);
/*     */       } 
/*     */     } 
/*     */     
/* 147 */     if (params.isParameterFalse("http.protocol.allow-circular-redirects")) {
/*     */       URI uRI;
/* 149 */       RedirectLocations redirectLocations = (RedirectLocations)context.getAttribute("http.protocol.redirect-locations");
/*     */ 
/*     */       
/* 152 */       if (redirectLocations == null) {
/* 153 */         redirectLocations = new RedirectLocations();
/* 154 */         context.setAttribute("http.protocol.redirect-locations", redirectLocations);
/*     */       } 
/*     */ 
/*     */       
/* 158 */       if (uri.getFragment() != null) {
/*     */         try {
/* 160 */           HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
/*     */ 
/*     */ 
/*     */           
/* 164 */           uRI = URIUtils.rewriteURI(uri, target, URIUtils.DROP_FRAGMENT_AND_NORMALIZE);
/* 165 */         } catch (URISyntaxException ex) {
/* 166 */           throw new ProtocolException(ex.getMessage(), ex);
/*     */         } 
/*     */       } else {
/* 169 */         uRI = uri;
/*     */       } 
/*     */       
/* 172 */       if (redirectLocations.contains(uRI)) {
/* 173 */         throw new CircularRedirectException("Circular redirect to '" + uRI + "'");
/*     */       }
/*     */       
/* 176 */       redirectLocations.add(uRI);
/*     */     } 
/*     */ 
/*     */     
/* 180 */     return uri;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultRedirectHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */