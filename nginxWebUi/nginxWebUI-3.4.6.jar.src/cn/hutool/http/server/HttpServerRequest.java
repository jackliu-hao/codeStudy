/*     */ package cn.hutool.http.server;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.map.CaseInsensitiveMap;
/*     */ import cn.hutool.core.map.multi.ListValueMap;
/*     */ import cn.hutool.core.net.NetUtil;
/*     */ import cn.hutool.core.net.multipart.MultipartFormData;
/*     */ import cn.hutool.core.net.multipart.UploadSetting;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.http.Header;
/*     */ import cn.hutool.http.HttpUtil;
/*     */ import cn.hutool.http.Method;
/*     */ import cn.hutool.http.useragent.UserAgent;
/*     */ import cn.hutool.http.useragent.UserAgentUtil;
/*     */ import com.sun.net.httpserver.Headers;
/*     */ import com.sun.net.httpserver.HttpExchange;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.net.HttpCookie;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpServerRequest
/*     */   extends HttpServerBase
/*     */ {
/*     */   private Map<String, HttpCookie> cookieCache;
/*     */   private ListValueMap<String, String> paramsCache;
/*     */   private MultipartFormData multipartFormDataCache;
/*     */   private Charset charsetCache;
/*     */   private byte[] bodyCache;
/*     */   
/*     */   public HttpServerRequest(HttpExchange httpExchange) {
/*  52 */     super(httpExchange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethod() {
/*  61 */     return this.httpExchange.getRequestMethod();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isGetMethod() {
/*  70 */     return Method.GET.name().equalsIgnoreCase(getMethod());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPostMethod() {
/*  79 */     return Method.POST.name().equalsIgnoreCase(getMethod());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  88 */     return this.httpExchange.getRequestURI();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  97 */     return getURI().getPath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQuery() {
/* 106 */     return getURI().getQuery();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Headers getHeaders() {
/* 115 */     return this.httpExchange.getRequestHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHeader(Header headerKey) {
/* 125 */     return getHeader(headerKey.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHeader(String headerKey) {
/* 135 */     return getHeaders().getFirst(headerKey);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHeader(String headerKey, Charset charset) {
/* 146 */     String header = getHeader(headerKey);
/* 147 */     if (null != header) {
/* 148 */       return CharsetUtil.convert(header, CharsetUtil.CHARSET_ISO_8859_1, charset);
/*     */     }
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 159 */     return getHeader(Header.CONTENT_TYPE);
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
/*     */   public Charset getCharset() {
/* 172 */     if (null == this.charsetCache) {
/* 173 */       String contentType = getContentType();
/* 174 */       String charsetStr = HttpUtil.getCharset(contentType);
/* 175 */       this.charsetCache = CharsetUtil.parse(charsetStr, DEFAULT_CHARSET);
/*     */     } 
/*     */     
/* 178 */     return this.charsetCache;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUserAgentStr() {
/* 187 */     return getHeader(Header.USER_AGENT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UserAgent getUserAgent() {
/* 196 */     return UserAgentUtil.parse(getUserAgentStr());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCookiesStr() {
/* 205 */     return getHeader(Header.COOKIE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<HttpCookie> getCookies() {
/* 214 */     return getCookieMap().values();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, HttpCookie> getCookieMap() {
/* 223 */     if (null == this.cookieCache) {
/* 224 */       this.cookieCache = Collections.unmodifiableMap(CollUtil.toMap(
/* 225 */             NetUtil.parseCookies(getCookiesStr()), (Map)new CaseInsensitiveMap(), HttpCookie::getName));
/*     */     }
/*     */ 
/*     */     
/* 229 */     return this.cookieCache;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpCookie getCookie(String cookieName) {
/* 239 */     return getCookieMap().get(cookieName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMultipart() {
/* 248 */     if (false == isPostMethod()) {
/* 249 */       return false;
/*     */     }
/*     */     
/* 252 */     String contentType = getContentType();
/* 253 */     if (StrUtil.isBlank(contentType)) {
/* 254 */       return false;
/*     */     }
/* 256 */     return contentType.toLowerCase().startsWith("multipart/");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBody() {
/* 266 */     return getBody(getCharset());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBody(Charset charset) {
/* 276 */     return StrUtil.str(getBodyBytes(), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBodyBytes() {
/* 285 */     if (null == this.bodyCache) {
/* 286 */       this.bodyCache = IoUtil.readBytes(getBodyStream(), true);
/*     */     }
/* 288 */     return this.bodyCache;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getBodyStream() {
/* 297 */     return this.httpExchange.getRequestBody();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getParam(String name) {
/* 307 */     return (String)getParams().get(name, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getParams(String name) {
/* 318 */     return (List<String>)getParams().get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListValueMap<String, String> getParams() {
/* 327 */     if (null == this.paramsCache) {
/* 328 */       this.paramsCache = new ListValueMap();
/* 329 */       Charset charset = getCharset();
/*     */ 
/*     */       
/* 332 */       String query = getQuery();
/* 333 */       if (StrUtil.isNotBlank(query)) {
/* 334 */         this.paramsCache.putAll(HttpUtil.decodeParams(query, charset));
/*     */       }
/*     */ 
/*     */       
/* 338 */       if (isMultipart()) {
/* 339 */         this.paramsCache.putAll((Map)getMultipart().getParamListMap());
/*     */       } else {
/*     */         
/* 342 */         String body = getBody();
/* 343 */         if (StrUtil.isNotBlank(body)) {
/* 344 */           this.paramsCache.putAll(HttpUtil.decodeParams(body, charset));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 349 */     return this.paramsCache;
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
/*     */   
/*     */   public String getClientIP(String... otherHeaderNames) {
/* 374 */     String[] headers = { "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
/* 375 */     if (ArrayUtil.isNotEmpty((Object[])otherHeaderNames)) {
/* 376 */       headers = (String[])ArrayUtil.addAll((Object[][])new String[][] { headers, otherHeaderNames });
/*     */     }
/*     */     
/* 379 */     return getClientIPByHeader(headers);
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
/*     */   public String getClientIPByHeader(String... headerNames) {
/* 396 */     for (String header : headerNames) {
/* 397 */       String str1 = getHeader(header);
/* 398 */       if (false == NetUtil.isUnknown(str1)) {
/* 399 */         return NetUtil.getMultistageReverseProxyIp(str1);
/*     */       }
/*     */     } 
/*     */     
/* 403 */     String ip = this.httpExchange.getRemoteAddress().getHostName();
/* 404 */     return NetUtil.getMultistageReverseProxyIp(ip);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartFormData getMultipart() throws IORuntimeException {
/* 415 */     if (null == this.multipartFormDataCache) {
/* 416 */       this.multipartFormDataCache = parseMultipart(new UploadSetting());
/*     */     }
/* 418 */     return this.multipartFormDataCache;
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
/*     */   public MultipartFormData parseMultipart(UploadSetting uploadSetting) throws IORuntimeException {
/* 432 */     MultipartFormData formData = new MultipartFormData(uploadSetting);
/*     */     try {
/* 434 */       formData.parseRequestStream(getBodyStream(), getCharset());
/* 435 */     } catch (IOException e) {
/* 436 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/* 439 */     return formData;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\HttpServerRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */