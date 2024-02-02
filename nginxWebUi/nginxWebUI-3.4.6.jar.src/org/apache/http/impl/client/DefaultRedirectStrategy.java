/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Arrays;
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
/*     */ import org.apache.http.client.RedirectStrategy;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.HttpGet;
/*     */ import org.apache.http.client.methods.HttpHead;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.methods.RequestBuilder;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.client.utils.URIUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class DefaultRedirectStrategy
/*     */   implements RedirectStrategy
/*     */ {
/*  75 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   public static final int SC_PERMANENT_REDIRECT = 308;
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
/*     */ 
/*     */   
/*  85 */   public static final DefaultRedirectStrategy INSTANCE = new DefaultRedirectStrategy();
/*     */   
/*     */   private final String[] redirectMethods;
/*     */   
/*     */   public DefaultRedirectStrategy() {
/*  90 */     this(new String[] { "GET", "HEAD" });
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
/*     */   public DefaultRedirectStrategy(String[] redirectMethods) {
/* 104 */     String[] tmp = (String[])redirectMethods.clone();
/* 105 */     Arrays.sort((Object[])tmp);
/* 106 */     this.redirectMethods = tmp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 114 */     Args.notNull(request, "HTTP request");
/* 115 */     Args.notNull(response, "HTTP response");
/*     */     
/* 117 */     int statusCode = response.getStatusLine().getStatusCode();
/* 118 */     String method = request.getRequestLine().getMethod();
/* 119 */     Header locationHeader = response.getFirstHeader("location");
/* 120 */     switch (statusCode) {
/*     */       case 302:
/* 122 */         return (isRedirectable(method) && locationHeader != null);
/*     */       case 301:
/*     */       case 307:
/*     */       case 308:
/* 126 */         return isRedirectable(method);
/*     */       case 303:
/* 128 */         return true;
/*     */     } 
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 138 */     Args.notNull(request, "HTTP request");
/* 139 */     Args.notNull(response, "HTTP response");
/* 140 */     Args.notNull(context, "HTTP context");
/*     */     
/* 142 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */ 
/*     */     
/* 145 */     Header locationHeader = response.getFirstHeader("location");
/* 146 */     if (locationHeader == null)
/*     */     {
/* 148 */       throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
/*     */     }
/*     */ 
/*     */     
/* 152 */     String location = locationHeader.getValue();
/* 153 */     if (this.log.isDebugEnabled()) {
/* 154 */       this.log.debug("Redirect requested to location '" + location + "'");
/*     */     }
/*     */     
/* 157 */     RequestConfig config = clientContext.getRequestConfig();
/*     */     
/* 159 */     URI uri = createLocationURI(location);
/*     */     
/*     */     try {
/* 162 */       if (config.isNormalizeUri()) {
/* 163 */         uri = URIUtils.normalizeSyntax(uri);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 168 */       if (!uri.isAbsolute()) {
/* 169 */         if (!config.isRelativeRedirectsAllowed()) {
/* 170 */           throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
/*     */         }
/*     */ 
/*     */         
/* 174 */         HttpHost target = clientContext.getTargetHost();
/* 175 */         Asserts.notNull(target, "Target host");
/* 176 */         URI requestURI = new URI(request.getRequestLine().getUri());
/* 177 */         URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, config.isNormalizeUri() ? URIUtils.NORMALIZE : URIUtils.NO_FLAGS);
/*     */         
/* 179 */         uri = URIUtils.resolve(absoluteRequestURI, uri);
/*     */       } 
/* 181 */     } catch (URISyntaxException ex) {
/* 182 */       throw new ProtocolException(ex.getMessage(), ex);
/*     */     } 
/*     */     
/* 185 */     RedirectLocations redirectLocations = (RedirectLocations)clientContext.getAttribute("http.protocol.redirect-locations");
/*     */     
/* 187 */     if (redirectLocations == null) {
/* 188 */       redirectLocations = new RedirectLocations();
/* 189 */       context.setAttribute("http.protocol.redirect-locations", redirectLocations);
/*     */     } 
/* 191 */     if (!config.isCircularRedirectsAllowed() && 
/* 192 */       redirectLocations.contains(uri)) {
/* 193 */       throw new CircularRedirectException("Circular redirect to '" + uri + "'");
/*     */     }
/*     */     
/* 196 */     redirectLocations.add(uri);
/* 197 */     return uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected URI createLocationURI(String location) throws ProtocolException {
/*     */     try {
/* 205 */       return new URI(location);
/* 206 */     } catch (URISyntaxException ex) {
/* 207 */       throw new ProtocolException("Invalid redirect URI: " + location, ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isRedirectable(String method) {
/* 215 */     return (Arrays.binarySearch((Object[])this.redirectMethods, method) >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 223 */     URI uri = getLocationURI(request, response, context);
/* 224 */     String method = request.getRequestLine().getMethod();
/* 225 */     if (method.equalsIgnoreCase("HEAD"))
/* 226 */       return (HttpUriRequest)new HttpHead(uri); 
/* 227 */     if (method.equalsIgnoreCase("GET")) {
/* 228 */       return (HttpUriRequest)new HttpGet(uri);
/*     */     }
/* 230 */     int status = response.getStatusLine().getStatusCode();
/* 231 */     return (status == 307 || status == 308) ? RequestBuilder.copy(request).setUri(uri).build() : (HttpUriRequest)new HttpGet(uri);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultRedirectStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */