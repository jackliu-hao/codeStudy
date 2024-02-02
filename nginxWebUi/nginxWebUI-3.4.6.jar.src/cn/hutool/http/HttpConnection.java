/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import cn.hutool.http.ssl.DefaultSSLInfo;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.ProtocolException;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpConnection
/*     */ {
/*     */   private final URL url;
/*     */   private final Proxy proxy;
/*     */   private HttpURLConnection conn;
/*     */   
/*     */   public static HttpConnection create(String urlStr, Proxy proxy) {
/*  46 */     return create(URLUtil.toUrlForHttp(urlStr), proxy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpConnection create(URL url, Proxy proxy) {
/*  57 */     return new HttpConnection(url, proxy);
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
/*     */   public HttpConnection(URL url, Proxy proxy) {
/*  69 */     this.url = url;
/*  70 */     this.proxy = proxy;
/*     */ 
/*     */     
/*  73 */     initConn();
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
/*     */   public HttpConnection initConn() {
/*     */     try {
/*  86 */       this.conn = openHttp();
/*  87 */     } catch (IOException e) {
/*  88 */       throw new HttpException(e);
/*     */     } 
/*     */ 
/*     */     
/*  92 */     this.conn.setDoInput(true);
/*     */     
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getMethod() {
/* 105 */     return Method.valueOf(this.conn.getRequestMethod());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection setMethod(Method method) {
/* 115 */     if (Method.POST.equals(method) || Method.PUT
/* 116 */       .equals(method) || Method.PATCH
/* 117 */       .equals(method) || Method.DELETE
/* 118 */       .equals(method)) {
/* 119 */       this.conn.setUseCaches(false);
/*     */ 
/*     */       
/* 122 */       if (Method.PATCH.equals(method)) {
/* 123 */         HttpGlobalConfig.allowPatch();
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 129 */       this.conn.setRequestMethod(method.toString());
/* 130 */     } catch (ProtocolException e) {
/* 131 */       throw new HttpException(e);
/*     */     } 
/*     */     
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/* 143 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Proxy getProxy() {
/* 152 */     return this.proxy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpURLConnection getHttpURLConnection() {
/* 161 */     return this.conn;
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
/*     */   public HttpConnection header(String header, String value, boolean isOverride) {
/* 178 */     if (null != this.conn) {
/* 179 */       if (isOverride) {
/* 180 */         this.conn.setRequestProperty(header, value);
/*     */       } else {
/* 182 */         this.conn.addRequestProperty(header, value);
/*     */       } 
/*     */     }
/*     */     
/* 186 */     return this;
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
/*     */   public HttpConnection header(Header header, String value, boolean isOverride) {
/* 199 */     return header(header.toString(), value, isOverride);
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
/*     */   public HttpConnection header(Map<String, List<String>> headerMap, boolean isOverride) {
/* 211 */     if (MapUtil.isNotEmpty(headerMap))
/*     */     {
/* 213 */       for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
/* 214 */         String name = entry.getKey();
/* 215 */         for (String value : entry.getValue()) {
/* 216 */           header(name, StrUtil.nullToEmpty(value), isOverride);
/*     */         }
/*     */       } 
/*     */     }
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String header(String name) {
/* 230 */     return this.conn.getHeaderField(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String header(Header name) {
/* 240 */     return header(name.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, List<String>> headers() {
/* 249 */     return this.conn.getHeaderFields();
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
/*     */   public HttpConnection setHttpsInfo(HostnameVerifier hostnameVerifier, SSLSocketFactory ssf) throws HttpException {
/* 264 */     HttpURLConnection conn = this.conn;
/*     */     
/* 266 */     if (conn instanceof HttpsURLConnection) {
/*     */       
/* 268 */       HttpsURLConnection httpsConn = (HttpsURLConnection)conn;
/*     */       
/* 270 */       httpsConn.setHostnameVerifier((HostnameVerifier)ObjectUtil.defaultIfNull(hostnameVerifier, DefaultSSLInfo.TRUST_ANY_HOSTNAME_VERIFIER));
/* 271 */       httpsConn.setSSLSocketFactory((SSLSocketFactory)ObjectUtil.defaultIfNull(ssf, DefaultSSLInfo.DEFAULT_SSF));
/*     */     } 
/*     */     
/* 274 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection disableCache() {
/* 284 */     this.conn.setUseCaches(false);
/* 285 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection setConnectTimeout(int timeout) {
/* 295 */     if (timeout > 0 && null != this.conn) {
/* 296 */       this.conn.setConnectTimeout(timeout);
/*     */     }
/*     */     
/* 299 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection setReadTimeout(int timeout) {
/* 309 */     if (timeout > 0 && null != this.conn) {
/* 310 */       this.conn.setReadTimeout(timeout);
/*     */     }
/*     */     
/* 313 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection setConnectionAndReadTimeout(int timeout) {
/* 323 */     setConnectTimeout(timeout);
/* 324 */     setReadTimeout(timeout);
/*     */     
/* 326 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection setCookie(String cookie) {
/* 336 */     if (cookie != null) {
/* 337 */       header(Header.COOKIE, cookie, true);
/*     */     }
/* 339 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection setChunkedStreamingMode(int blockSize) {
/* 350 */     if (blockSize > 0) {
/* 351 */       this.conn.setChunkedStreamingMode(blockSize);
/*     */     }
/* 353 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection setInstanceFollowRedirects(boolean isInstanceFollowRedirects) {
/* 363 */     this.conn.setInstanceFollowRedirects(isInstanceFollowRedirects);
/* 364 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection connect() throws IOException {
/* 374 */     if (null != this.conn) {
/* 375 */       this.conn.connect();
/*     */     }
/* 377 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection disconnectQuietly() {
/*     */     try {
/* 388 */       disconnect();
/* 389 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */     
/* 393 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConnection disconnect() {
/* 402 */     if (null != this.conn) {
/* 403 */       this.conn.disconnect();
/*     */     }
/* 405 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() throws IOException {
/* 416 */     if (null != this.conn) {
/* 417 */       return this.conn.getInputStream();
/*     */     }
/* 419 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getErrorStream() {
/* 428 */     if (null != this.conn) {
/* 429 */       return this.conn.getErrorStream();
/*     */     }
/* 431 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOutputStream() throws IOException {
/* 441 */     if (null == this.conn) {
/* 442 */       throw new IOException("HttpURLConnection has not been initialized.");
/*     */     }
/*     */     
/* 445 */     Method method = getMethod();
/*     */ 
/*     */     
/* 448 */     this.conn.setDoOutput(true);
/* 449 */     OutputStream out = this.conn.getOutputStream();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 455 */     if (method == Method.GET && method != getMethod()) {
/* 456 */       ReflectUtil.setFieldValue(this.conn, "method", Method.GET.name());
/*     */     }
/*     */     
/* 459 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int responseCode() throws IOException {
/* 469 */     if (null != this.conn) {
/* 470 */       return this.conn.getResponseCode();
/*     */     }
/* 472 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCharsetName() {
/* 483 */     return HttpUtil.getCharset(this.conn);
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
/*     */   public Charset getCharset() {
/* 495 */     Charset charset = null;
/* 496 */     String charsetName = getCharsetName();
/* 497 */     if (StrUtil.isNotBlank(charsetName)) {
/*     */       try {
/* 499 */         charset = Charset.forName(charsetName);
/* 500 */       } catch (UnsupportedCharsetException unsupportedCharsetException) {}
/*     */     }
/*     */ 
/*     */     
/* 504 */     return charset;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 509 */     StringBuilder sb = StrUtil.builder();
/* 510 */     sb.append("Request URL: ").append(this.url).append("\r\n");
/* 511 */     sb.append("Request Method: ").append(getMethod()).append("\r\n");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 517 */     return sb.toString();
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
/*     */   private HttpURLConnection openHttp() throws IOException {
/* 529 */     URLConnection conn = openConnection();
/* 530 */     if (false == conn instanceof HttpURLConnection)
/*     */     {
/* 532 */       throw new HttpException("'{}' of URL [{}] is not a http connection, make sure URL is format for http.", new Object[] { conn.getClass().getName(), this.url });
/*     */     }
/*     */     
/* 535 */     return (HttpURLConnection)conn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private URLConnection openConnection() throws IOException {
/* 545 */     return (null == this.proxy) ? this.url.openConnection() : this.url.openConnection(this.proxy);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */