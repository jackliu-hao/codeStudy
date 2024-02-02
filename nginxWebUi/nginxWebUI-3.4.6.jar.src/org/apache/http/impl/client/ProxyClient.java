/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.auth.AuthSchemeFactory;
/*     */ import org.apache.http.auth.AuthSchemeRegistry;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.params.HttpClientParamConfig;
/*     */ import org.apache.http.client.protocol.RequestClientConnControl;
/*     */ import org.apache.http.config.ConnectionConfig;
/*     */ import org.apache.http.conn.HttpConnectionFactory;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.RouteInfo;
/*     */ import org.apache.http.entity.BufferedHttpEntity;
/*     */ import org.apache.http.impl.DefaultConnectionReuseStrategy;
/*     */ import org.apache.http.impl.auth.BasicSchemeFactory;
/*     */ import org.apache.http.impl.auth.DigestSchemeFactory;
/*     */ import org.apache.http.impl.auth.HttpAuthenticator;
/*     */ import org.apache.http.impl.auth.KerberosSchemeFactory;
/*     */ import org.apache.http.impl.auth.NTLMSchemeFactory;
/*     */ import org.apache.http.impl.auth.SPNegoSchemeFactory;
/*     */ import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
/*     */ import org.apache.http.impl.execchain.TunnelRefusedException;
/*     */ import org.apache.http.message.BasicHttpRequest;
/*     */ import org.apache.http.params.BasicHttpParams;
/*     */ import org.apache.http.params.HttpParamConfig;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.BasicHttpContext;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.protocol.HttpProcessor;
/*     */ import org.apache.http.protocol.HttpRequestExecutor;
/*     */ import org.apache.http.protocol.ImmutableHttpProcessor;
/*     */ import org.apache.http.protocol.RequestTargetHost;
/*     */ import org.apache.http.protocol.RequestUserAgent;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.EntityUtils;
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
/*     */ public class ProxyClient
/*     */ {
/*     */   private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;
/*     */   private final ConnectionConfig connectionConfig;
/*     */   private final RequestConfig requestConfig;
/*     */   private final HttpProcessor httpProcessor;
/*     */   private final HttpRequestExecutor requestExec;
/*     */   private final ProxyAuthenticationStrategy proxyAuthStrategy;
/*     */   private final HttpAuthenticator authenticator;
/*     */   private final AuthState proxyAuthState;
/*     */   private final AuthSchemeRegistry authSchemeRegistry;
/*     */   private final ConnectionReuseStrategy reuseStrategy;
/*     */   
/*     */   public ProxyClient(HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, ConnectionConfig connectionConfig, RequestConfig requestConfig) {
/* 106 */     this.connFactory = (connFactory != null) ? connFactory : (HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection>)ManagedHttpClientConnectionFactory.INSTANCE;
/* 107 */     this.connectionConfig = (connectionConfig != null) ? connectionConfig : ConnectionConfig.DEFAULT;
/* 108 */     this.requestConfig = (requestConfig != null) ? requestConfig : RequestConfig.DEFAULT;
/* 109 */     this.httpProcessor = (HttpProcessor)new ImmutableHttpProcessor(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestTargetHost(), (HttpRequestInterceptor)new RequestClientConnControl(), (HttpRequestInterceptor)new RequestUserAgent() });
/*     */     
/* 111 */     this.requestExec = new HttpRequestExecutor();
/* 112 */     this.proxyAuthStrategy = new ProxyAuthenticationStrategy();
/* 113 */     this.authenticator = new HttpAuthenticator();
/* 114 */     this.proxyAuthState = new AuthState();
/* 115 */     this.authSchemeRegistry = new AuthSchemeRegistry();
/* 116 */     this.authSchemeRegistry.register("Basic", (AuthSchemeFactory)new BasicSchemeFactory());
/* 117 */     this.authSchemeRegistry.register("Digest", (AuthSchemeFactory)new DigestSchemeFactory());
/* 118 */     this.authSchemeRegistry.register("NTLM", (AuthSchemeFactory)new NTLMSchemeFactory());
/* 119 */     this.authSchemeRegistry.register("Negotiate", (AuthSchemeFactory)new SPNegoSchemeFactory());
/* 120 */     this.authSchemeRegistry.register("Kerberos", (AuthSchemeFactory)new KerberosSchemeFactory());
/* 121 */     this.reuseStrategy = (ConnectionReuseStrategy)new DefaultConnectionReuseStrategy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ProxyClient(HttpParams params) {
/* 129 */     this(null, HttpParamConfig.getConnectionConfig(params), HttpClientParamConfig.getRequestConfig(params));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProxyClient(RequestConfig requestConfig) {
/* 138 */     this(null, null, requestConfig);
/*     */   }
/*     */   
/*     */   public ProxyClient() {
/* 142 */     this(null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpParams getParams() {
/* 150 */     return (HttpParams)new BasicHttpParams();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public AuthSchemeRegistry getAuthSchemeRegistry() {
/* 158 */     return this.authSchemeRegistry;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Socket tunnel(HttpHost proxy, HttpHost target, Credentials credentials) throws IOException, HttpException {
/*     */     HttpResponse response;
/* 165 */     Args.notNull(proxy, "Proxy host");
/* 166 */     Args.notNull(target, "Target host");
/* 167 */     Args.notNull(credentials, "Credentials");
/* 168 */     HttpHost host = target;
/* 169 */     if (host.getPort() <= 0) {
/* 170 */       host = new HttpHost(host.getHostName(), 80, host.getSchemeName());
/*     */     }
/* 172 */     HttpRoute route = new HttpRoute(host, this.requestConfig.getLocalAddress(), proxy, false, RouteInfo.TunnelType.TUNNELLED, RouteInfo.LayerType.PLAIN);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 177 */     ManagedHttpClientConnection conn = (ManagedHttpClientConnection)this.connFactory.create(route, this.connectionConfig);
/*     */     
/* 179 */     BasicHttpContext basicHttpContext = new BasicHttpContext();
/*     */ 
/*     */     
/* 182 */     BasicHttpRequest basicHttpRequest = new BasicHttpRequest("CONNECT", host.toHostString(), (ProtocolVersion)HttpVersion.HTTP_1_1);
/*     */ 
/*     */     
/* 185 */     BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
/* 186 */     credsProvider.setCredentials(new AuthScope(proxy), credentials);
/*     */ 
/*     */     
/* 189 */     basicHttpContext.setAttribute("http.target_host", target);
/* 190 */     basicHttpContext.setAttribute("http.connection", conn);
/* 191 */     basicHttpContext.setAttribute("http.request", basicHttpRequest);
/* 192 */     basicHttpContext.setAttribute("http.route", route);
/* 193 */     basicHttpContext.setAttribute("http.auth.proxy-scope", this.proxyAuthState);
/* 194 */     basicHttpContext.setAttribute("http.auth.credentials-provider", credsProvider);
/* 195 */     basicHttpContext.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
/* 196 */     basicHttpContext.setAttribute("http.request-config", this.requestConfig);
/*     */     
/* 198 */     this.requestExec.preProcess((HttpRequest)basicHttpRequest, this.httpProcessor, (HttpContext)basicHttpContext);
/*     */     
/*     */     while (true) {
/* 201 */       if (!conn.isOpen()) {
/* 202 */         Socket socket = new Socket(proxy.getHostName(), proxy.getPort());
/* 203 */         conn.bind(socket);
/*     */       } 
/*     */       
/* 206 */       this.authenticator.generateAuthResponse((HttpRequest)basicHttpRequest, this.proxyAuthState, (HttpContext)basicHttpContext);
/*     */       
/* 208 */       response = this.requestExec.execute((HttpRequest)basicHttpRequest, (HttpClientConnection)conn, (HttpContext)basicHttpContext);
/*     */       
/* 210 */       int i = response.getStatusLine().getStatusCode();
/* 211 */       if (i < 200) {
/* 212 */         throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
/*     */       }
/*     */       
/* 215 */       if (this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, (HttpContext)basicHttpContext))
/*     */       {
/* 217 */         if (this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, (HttpContext)basicHttpContext)) {
/*     */ 
/*     */           
/* 220 */           if (this.reuseStrategy.keepAlive(response, (HttpContext)basicHttpContext)) {
/*     */             
/* 222 */             HttpEntity entity = response.getEntity();
/* 223 */             EntityUtils.consume(entity);
/*     */           } else {
/* 225 */             conn.close();
/*     */           } 
/*     */           
/* 228 */           basicHttpRequest.removeHeaders("Proxy-Authorization");
/*     */           
/*     */           continue;
/*     */         } 
/*     */       }
/*     */       
/*     */       break;
/*     */     } 
/*     */     
/* 237 */     int status = response.getStatusLine().getStatusCode();
/*     */     
/* 239 */     if (status > 299) {
/*     */ 
/*     */       
/* 242 */       HttpEntity entity = response.getEntity();
/* 243 */       if (entity != null) {
/* 244 */         response.setEntity((HttpEntity)new BufferedHttpEntity(entity));
/*     */       }
/*     */       
/* 247 */       conn.close();
/* 248 */       throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
/*     */     } 
/*     */     
/* 251 */     return conn.getSocket();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\ProxyClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */