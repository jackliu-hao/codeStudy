/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.UsernamePasswordCredentials;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.RouteInfo;
/*     */ import org.apache.http.impl.client.BasicCredentialsProvider;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.protocol.HttpProcessor;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class ProtocolExec
/*     */   implements ClientExecChain
/*     */ {
/*  77 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final ClientExecChain requestExecutor;
/*     */   private final HttpProcessor httpProcessor;
/*     */   
/*     */   public ProtocolExec(ClientExecChain requestExecutor, HttpProcessor httpProcessor) {
/*  83 */     Args.notNull(requestExecutor, "HTTP client request executor");
/*  84 */     Args.notNull(httpProcessor, "HTTP protocol processor");
/*  85 */     this.requestExecutor = requestExecutor;
/*  86 */     this.httpProcessor = httpProcessor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void rewriteRequestURI(HttpRequestWrapper request, HttpRoute route, boolean normalizeUri) throws ProtocolException {
/*  93 */     URI uri = request.getURI();
/*  94 */     if (uri != null) {
/*     */       try {
/*  96 */         request.setURI(URIUtils.rewriteURIForRoute(uri, (RouteInfo)route, normalizeUri));
/*  97 */       } catch (URISyntaxException ex) {
/*  98 */         throw new ProtocolException("Invalid URI: " + uri, ex);
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
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/* 110 */     Args.notNull(route, "HTTP route");
/* 111 */     Args.notNull(request, "HTTP request");
/* 112 */     Args.notNull(context, "HTTP context");
/*     */     
/* 114 */     HttpRequest original = request.getOriginal();
/* 115 */     URI uri = null;
/* 116 */     if (original instanceof HttpUriRequest) {
/* 117 */       uri = ((HttpUriRequest)original).getURI();
/*     */     } else {
/* 119 */       String uriString = original.getRequestLine().getUri();
/*     */       try {
/* 121 */         uri = URI.create(uriString);
/* 122 */       } catch (IllegalArgumentException ex) {
/* 123 */         if (this.log.isDebugEnabled()) {
/* 124 */           this.log.debug("Unable to parse '" + uriString + "' as a valid URI; " + "request URI and Host header may be inconsistent", ex);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 130 */     request.setURI(uri);
/*     */ 
/*     */     
/* 133 */     rewriteRequestURI(request, route, context.getRequestConfig().isNormalizeUri());
/*     */     
/* 135 */     HttpParams params = request.getParams();
/* 136 */     HttpHost virtualHost = (HttpHost)params.getParameter("http.virtual-host");
/*     */     
/* 138 */     if (virtualHost != null && virtualHost.getPort() == -1) {
/* 139 */       int port = route.getTargetHost().getPort();
/* 140 */       if (port != -1) {
/* 141 */         virtualHost = new HttpHost(virtualHost.getHostName(), port, virtualHost.getSchemeName());
/*     */       }
/*     */       
/* 144 */       if (this.log.isDebugEnabled()) {
/* 145 */         this.log.debug("Using virtual host" + virtualHost);
/*     */       }
/*     */     } 
/*     */     
/* 149 */     HttpHost target = null;
/* 150 */     if (virtualHost != null) {
/* 151 */       target = virtualHost;
/*     */     }
/* 153 */     else if (uri != null && uri.isAbsolute() && uri.getHost() != null) {
/* 154 */       target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
/*     */     } 
/*     */     
/* 157 */     if (target == null) {
/* 158 */       target = request.getTarget();
/*     */     }
/* 160 */     if (target == null) {
/* 161 */       target = route.getTargetHost();
/*     */     }
/*     */ 
/*     */     
/* 165 */     if (uri != null) {
/* 166 */       String userinfo = uri.getUserInfo();
/* 167 */       if (userinfo != null) {
/* 168 */         BasicCredentialsProvider basicCredentialsProvider; CredentialsProvider credsProvider = context.getCredentialsProvider();
/* 169 */         if (credsProvider == null) {
/* 170 */           basicCredentialsProvider = new BasicCredentialsProvider();
/* 171 */           context.setCredentialsProvider((CredentialsProvider)basicCredentialsProvider);
/*     */         } 
/* 173 */         basicCredentialsProvider.setCredentials(new AuthScope(target), (Credentials)new UsernamePasswordCredentials(userinfo));
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 180 */     context.setAttribute("http.target_host", target);
/* 181 */     context.setAttribute("http.route", route);
/* 182 */     context.setAttribute("http.request", request);
/*     */     
/* 184 */     this.httpProcessor.process((HttpRequest)request, (HttpContext)context);
/*     */     
/* 186 */     CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
/*     */ 
/*     */     
/*     */     try {
/* 190 */       context.setAttribute("http.response", response);
/* 191 */       this.httpProcessor.process((HttpResponse)response, (HttpContext)context);
/* 192 */       return response;
/* 193 */     } catch (RuntimeException ex) {
/* 194 */       response.close();
/* 195 */       throw ex;
/* 196 */     } catch (IOException ex) {
/* 197 */       response.close();
/* 198 */       throw ex;
/* 199 */     } catch (HttpException ex) {
/* 200 */       response.close();
/* 201 */       throw ex;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\ProtocolExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */