/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.protocol.RequestAddCookies;
/*     */ import org.apache.http.client.protocol.RequestAuthCache;
/*     */ import org.apache.http.client.protocol.RequestClientConnControl;
/*     */ import org.apache.http.client.protocol.RequestDefaultHeaders;
/*     */ import org.apache.http.client.protocol.RequestProxyAuthentication;
/*     */ import org.apache.http.client.protocol.RequestTargetAuthentication;
/*     */ import org.apache.http.client.protocol.ResponseProcessCookies;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.params.HttpConnectionParams;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.params.HttpProtocolParams;
/*     */ import org.apache.http.params.SyncBasicHttpParams;
/*     */ import org.apache.http.protocol.BasicHttpProcessor;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ import org.apache.http.protocol.RequestContent;
/*     */ import org.apache.http.protocol.RequestExpectContinue;
/*     */ import org.apache.http.protocol.RequestTargetHost;
/*     */ import org.apache.http.protocol.RequestUserAgent;
/*     */ import org.apache.http.util.VersionInfo;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class DefaultHttpClient
/*     */   extends AbstractHttpClient
/*     */ {
/*     */   public DefaultHttpClient(ClientConnectionManager conman, HttpParams params) {
/* 129 */     super(conman, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHttpClient(ClientConnectionManager conman) {
/* 138 */     super(conman, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultHttpClient(HttpParams params) {
/* 143 */     super(null, params);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultHttpClient() {
/* 148 */     super(null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpParams createHttpParams() {
/* 159 */     SyncBasicHttpParams syncBasicHttpParams = new SyncBasicHttpParams();
/* 160 */     setDefaultHttpParams((HttpParams)syncBasicHttpParams);
/* 161 */     return (HttpParams)syncBasicHttpParams;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDefaultHttpParams(HttpParams params) {
/* 181 */     HttpProtocolParams.setVersion(params, (ProtocolVersion)HttpVersion.HTTP_1_1);
/* 182 */     HttpProtocolParams.setContentCharset(params, HTTP.DEF_CONTENT_CHARSET.name());
/* 183 */     HttpConnectionParams.setTcpNoDelay(params, true);
/* 184 */     HttpConnectionParams.setSocketBufferSize(params, 8192);
/* 185 */     HttpProtocolParams.setUserAgent(params, VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", DefaultHttpClient.class));
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
/*     */   protected BasicHttpProcessor createHttpProcessor() {
/* 209 */     BasicHttpProcessor httpproc = new BasicHttpProcessor();
/* 210 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestDefaultHeaders());
/*     */     
/* 212 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestContent());
/* 213 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestTargetHost());
/*     */     
/* 215 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestClientConnControl());
/* 216 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestUserAgent());
/* 217 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestExpectContinue());
/*     */     
/* 219 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestAddCookies());
/* 220 */     httpproc.addInterceptor((HttpResponseInterceptor)new ResponseProcessCookies());
/*     */     
/* 222 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestAuthCache());
/* 223 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestTargetAuthentication());
/* 224 */     httpproc.addInterceptor((HttpRequestInterceptor)new RequestProxyAuthentication());
/* 225 */     return httpproc;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */