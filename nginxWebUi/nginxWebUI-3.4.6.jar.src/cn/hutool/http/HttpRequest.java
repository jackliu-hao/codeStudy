/*      */ package cn.hutool.http;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.convert.Convert;
/*      */ import cn.hutool.core.io.IORuntimeException;
/*      */ import cn.hutool.core.io.resource.BytesResource;
/*      */ import cn.hutool.core.io.resource.FileResource;
/*      */ import cn.hutool.core.io.resource.MultiFileResource;
/*      */ import cn.hutool.core.io.resource.Resource;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import cn.hutool.core.map.TableMap;
/*      */ import cn.hutool.core.net.url.UrlBuilder;
/*      */ import cn.hutool.core.net.url.UrlQuery;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.http.body.BytesBody;
/*      */ import cn.hutool.http.body.FormUrlEncodedBody;
/*      */ import cn.hutool.http.body.MultipartBody;
/*      */ import cn.hutool.http.cookie.GlobalCookieManager;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.net.CookieManager;
/*      */ import java.net.HttpCookie;
/*      */ import java.net.Proxy;
/*      */ import java.net.URLStreamHandler;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.Collection;
/*      */ import java.util.Map;
/*      */ import java.util.function.Consumer;
/*      */ import javax.net.ssl.HostnameVerifier;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HttpRequest
/*      */   extends HttpBase<HttpRequest>
/*      */ {
/*      */   public static HttpRequest post(String url) {
/*   56 */     return of(url).method(Method.POST);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest get(String url) {
/*   66 */     return of(url).method(Method.GET);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest head(String url) {
/*   76 */     return of(url).method(Method.HEAD);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest options(String url) {
/*   86 */     return of(url).method(Method.OPTIONS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest put(String url) {
/*   96 */     return of(url).method(Method.PUT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest patch(String url) {
/*  107 */     return of(url).method(Method.PATCH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest delete(String url) {
/*  117 */     return of(url).method(Method.DELETE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest trace(String url) {
/*  127 */     return of(url).method(Method.TRACE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest of(String url) {
/*  141 */     return of(url, HttpGlobalConfig.isDecodeUrl() ? DEFAULT_CHARSET : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest of(String url, Charset charset) {
/*  156 */     return of(UrlBuilder.ofHttp(url, charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HttpRequest of(UrlBuilder url) {
/*  167 */     return new HttpRequest(url);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setGlobalTimeout(int customTimeout) {
/*  178 */     HttpGlobalConfig.setTimeout(customTimeout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static CookieManager getCookieManager() {
/*  189 */     return GlobalCookieManager.getCookieManager();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setCookieManager(CookieManager customCookieManager) {
/*  200 */     GlobalCookieManager.setCookieManager(customCookieManager);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void closeCookie() {
/*  210 */     GlobalCookieManager.setCookieManager(null);
/*      */   }
/*      */ 
/*      */   
/*  214 */   private HttpConfig config = HttpConfig.create();
/*      */   private UrlBuilder url;
/*      */   private URLStreamHandler urlHandler;
/*  217 */   private Method method = Method.GET;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HttpConnection httpConnection;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Map<String, Object> form;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String cookie;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isMultiPart;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isRest;
/*      */ 
/*      */ 
/*      */   
/*      */   private int redirectCount;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public HttpRequest(String url) {
/*  252 */     this(UrlBuilder.ofHttp(url));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest(UrlBuilder url) {
/*  261 */     this.url = (UrlBuilder)Assert.notNull(url, "URL must be not null!", new Object[0]);
/*      */     
/*  263 */     Charset charset = url.getCharset();
/*  264 */     if (null != charset) {
/*  265 */       charset(charset);
/*      */     }
/*      */     
/*  268 */     header(GlobalHeaders.INSTANCE.headers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getUrl() {
/*  278 */     return this.url.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setUrl(String url) {
/*  289 */     return setUrl(UrlBuilder.ofHttp(url, this.charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setUrl(UrlBuilder urlBuilder) {
/*  300 */     this.url = urlBuilder;
/*  301 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setUrlHandler(URLStreamHandler urlHandler) {
/*  318 */     this.urlHandler = urlHandler;
/*  319 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Method getMethod() {
/*  329 */     return this.method;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setMethod(Method method) {
/*  341 */     return method(method);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpConnection getConnection() {
/*  352 */     return this.httpConnection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest method(Method method) {
/*  362 */     this.method = method;
/*  363 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest contentType(String contentType) {
/*  375 */     header(Header.CONTENT_TYPE, contentType);
/*  376 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest keepAlive(boolean isKeepAlive) {
/*  386 */     header(Header.CONNECTION, isKeepAlive ? "Keep-Alive" : "Close");
/*  387 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isKeepAlive() {
/*  394 */     String connection = header(Header.CONNECTION);
/*  395 */     if (connection == null) {
/*  396 */       return (false == "HTTP/1.0".equalsIgnoreCase(this.httpVersion));
/*      */     }
/*      */     
/*  399 */     return (false == "close".equalsIgnoreCase(connection));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String contentLength() {
/*  408 */     return header(Header.CONTENT_LENGTH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest contentLength(int value) {
/*  418 */     header(Header.CONTENT_LENGTH, String.valueOf(value));
/*  419 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest cookie(Collection<HttpCookie> cookies) {
/*  431 */     return cookie(CollUtil.isEmpty(cookies) ? null : cookies.<HttpCookie>toArray(new HttpCookie[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest cookie(HttpCookie... cookies) {
/*  443 */     if (ArrayUtil.isEmpty((Object[])cookies)) {
/*  444 */       return disableCookie();
/*      */     }
/*      */ 
/*      */     
/*  448 */     return cookie(ArrayUtil.join((Object[])cookies, "; "));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest cookie(String cookie) {
/*  460 */     this.cookie = cookie;
/*  461 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest disableCookie() {
/*  473 */     return cookie("");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest enableDefaultCookie() {
/*  482 */     return cookie((String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(String name, Object value) {
/*      */     String strValue;
/*  496 */     if (StrUtil.isBlank(name) || ObjectUtil.isNull(value)) {
/*  497 */       return this;
/*      */     }
/*      */ 
/*      */     
/*  501 */     this.bodyBytes = null;
/*      */     
/*  503 */     if (value instanceof File)
/*      */     {
/*  505 */       return form(name, (File)value);
/*      */     }
/*      */     
/*  508 */     if (value instanceof Resource) {
/*  509 */       return form(name, (Resource)value);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  514 */     if (value instanceof Iterable) {
/*      */       
/*  516 */       strValue = CollUtil.join((Iterable)value, ",");
/*  517 */     } else if (ArrayUtil.isArray(value)) {
/*  518 */       if (File.class == ArrayUtil.getComponentType(value))
/*      */       {
/*  520 */         return form(name, (File[])value);
/*      */       }
/*      */       
/*  523 */       strValue = ArrayUtil.join((Object[])value, ",");
/*      */     } else {
/*      */       
/*  526 */       strValue = Convert.toStr(value, null);
/*      */     } 
/*      */     
/*  529 */     return putToForm(name, strValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(String name, Object value, Object... parameters) {
/*  541 */     form(name, value);
/*      */     
/*  543 */     for (int i = 0; i < parameters.length; i += 2) {
/*  544 */       form(parameters[i].toString(), parameters[i + 1]);
/*      */     }
/*  546 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(Map<String, Object> formMap) {
/*  556 */     if (MapUtil.isNotEmpty(formMap)) {
/*  557 */       formMap.forEach(this::form);
/*      */     }
/*  559 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest formStr(Map<String, String> formMapStr) {
/*  570 */     if (MapUtil.isNotEmpty(formMapStr)) {
/*  571 */       formMapStr.forEach(this::form);
/*      */     }
/*  573 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(String name, File... files) {
/*  585 */     if (ArrayUtil.isEmpty((Object[])files)) {
/*  586 */       return this;
/*      */     }
/*  588 */     if (1 == files.length) {
/*  589 */       File file = files[0];
/*  590 */       return form(name, file, file.getName());
/*      */     } 
/*  592 */     return form(name, (Resource)new MultiFileResource(files));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(String name, File file) {
/*  604 */     return form(name, file, file.getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(String name, File file, String fileName) {
/*  617 */     if (null != file) {
/*  618 */       form(name, (Resource)new FileResource(file, fileName));
/*      */     }
/*  620 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(String name, byte[] fileBytes, String fileName) {
/*  634 */     if (null != fileBytes) {
/*  635 */       form(name, (Resource)new BytesResource(fileBytes, fileName));
/*      */     }
/*  637 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest form(String name, Resource resource) {
/*  650 */     if (null != resource) {
/*  651 */       if (false == isKeepAlive()) {
/*  652 */         keepAlive(true);
/*      */       }
/*      */       
/*  655 */       this.isMultiPart = true;
/*  656 */       return putToForm(name, resource);
/*      */     } 
/*  658 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, Object> form() {
/*  667 */     return this.form;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, Resource> fileForm() {
/*  677 */     Map<String, Resource> result = MapUtil.newHashMap();
/*  678 */     this.form.forEach((key, value) -> {
/*      */           if (value instanceof Resource) {
/*      */             result.put(key, (Resource)value);
/*      */           }
/*      */         });
/*  683 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest body(String body) {
/*  702 */     return body(body, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest body(String body, String contentType) {
/*  719 */     byte[] bytes = StrUtil.bytes(body, this.charset);
/*  720 */     body(bytes);
/*  721 */     this.form = null;
/*      */     
/*  723 */     if (null != contentType) {
/*      */       
/*  725 */       contentType(contentType);
/*      */     } else {
/*      */       
/*  728 */       contentType = HttpUtil.getContentTypeByRequestBody(body);
/*  729 */       if (null != contentType && ContentType.isDefault(header(Header.CONTENT_TYPE))) {
/*  730 */         if (null != this.charset)
/*      */         {
/*  732 */           contentType = ContentType.build(contentType, this.charset);
/*      */         }
/*  734 */         contentType(contentType);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  739 */     if (StrUtil.containsAnyIgnoreCase(contentType, new CharSequence[] { "json", "xml" })) {
/*  740 */       this.isRest = true;
/*  741 */       contentLength(bytes.length);
/*      */     } 
/*  743 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest body(byte[] bodyBytes) {
/*  754 */     if (null != bodyBytes) {
/*  755 */       this.bodyBytes = bodyBytes;
/*      */     }
/*  757 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setConfig(HttpConfig config) {
/*  769 */     this.config = config;
/*  770 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest timeout(int milliseconds) {
/*  788 */     this.config.timeout(milliseconds);
/*  789 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setConnectionTimeout(int milliseconds) {
/*  800 */     this.config.setConnectionTimeout(milliseconds);
/*  801 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setReadTimeout(int milliseconds) {
/*  812 */     this.config.setReadTimeout(milliseconds);
/*  813 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest disableCache() {
/*  822 */     this.config.disableCache();
/*  823 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setFollowRedirects(boolean isFollowRedirects) {
/*  834 */     return setMaxRedirectCount(isFollowRedirects ? 2 : 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setMaxRedirectCount(int maxRedirectCount) {
/*  846 */     this.config.setMaxRedirectCount(maxRedirectCount);
/*  847 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setHostnameVerifier(HostnameVerifier hostnameVerifier) {
/*  858 */     this.config.setHostnameVerifier(hostnameVerifier);
/*  859 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setHttpProxy(String host, int port) {
/*  871 */     this.config.setHttpProxy(host, port);
/*  872 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setProxy(Proxy proxy) {
/*  882 */     this.config.setProxy(proxy);
/*  883 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setSSLSocketFactory(SSLSocketFactory ssf) {
/*  895 */     this.config.setSSLSocketFactory(ssf);
/*  896 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setSSLProtocol(String protocol) {
/*  916 */     this.config.setSSLProtocol(protocol);
/*  917 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setRest(boolean isRest) {
/*  929 */     this.isRest = isRest;
/*  930 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest setChunkedStreamingMode(int blockSize) {
/*  942 */     this.config.setBlockSize(blockSize);
/*  943 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest addInterceptor(HttpInterceptor<HttpRequest> interceptor) {
/*  955 */     return addRequestInterceptor(interceptor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
/*  966 */     this.config.addRequestInterceptor(interceptor);
/*  967 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
/*  978 */     this.config.addResponseInterceptor(interceptor);
/*  979 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpResponse execute() {
/*  988 */     return execute(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpResponse executeAsync() {
/* 1002 */     return execute(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpResponse execute(boolean isAsync) {
/* 1012 */     return doExecute(isAsync, this.config.requestInterceptors, this.config.responseInterceptors);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void then(Consumer<HttpResponse> consumer) {
/* 1023 */     try (HttpResponse response = execute(true)) {
/* 1024 */       consumer.accept(response);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest basicAuth(String username, String password) {
/* 1039 */     return auth(HttpUtil.buildBasicAuth(username, password, this.charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest basicProxyAuth(String username, String password) {
/* 1054 */     return proxyAuth(HttpUtil.buildBasicAuth(username, password, this.charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest bearerAuth(String token) {
/* 1065 */     return auth("Bearer " + token);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest auth(String content) {
/* 1076 */     header(Header.AUTHORIZATION, content, true);
/* 1077 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpRequest proxyAuth(String content) {
/* 1088 */     header(Header.PROXY_AUTHORIZATION, content, true);
/* 1089 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1094 */     StringBuilder sb = StrUtil.builder();
/* 1095 */     sb.append("Request Url: ").append(this.url.setCharset(this.charset)).append("\r\n");
/* 1096 */     sb.append(super.toString());
/* 1097 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HttpResponse doExecute(boolean isAsync, HttpInterceptor.Chain<HttpRequest> requestInterceptors, HttpInterceptor.Chain<HttpResponse> responseInterceptors) {
/* 1112 */     if (null != requestInterceptors) {
/* 1113 */       for (HttpInterceptor<HttpRequest> interceptor : requestInterceptors) {
/* 1114 */         interceptor.process(this);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/* 1119 */     urlWithParamIfGet();
/*      */     
/* 1121 */     initConnection();
/*      */     
/* 1123 */     send();
/*      */ 
/*      */     
/* 1126 */     HttpResponse httpResponse = sendRedirectIfPossible(isAsync);
/*      */ 
/*      */     
/* 1129 */     if (null == httpResponse) {
/* 1130 */       httpResponse = new HttpResponse(this.httpConnection, this.config, this.charset, isAsync, isIgnoreResponseBody());
/*      */     }
/*      */ 
/*      */     
/* 1134 */     if (null != responseInterceptors) {
/* 1135 */       for (HttpInterceptor<HttpResponse> interceptor : responseInterceptors) {
/* 1136 */         interceptor.process(httpResponse);
/*      */       }
/*      */     }
/*      */     
/* 1140 */     return httpResponse;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initConnection() {
/* 1147 */     if (null != this.httpConnection)
/*      */     {
/* 1149 */       this.httpConnection.disconnectQuietly();
/*      */     }
/*      */     
/* 1152 */     this
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1165 */       .httpConnection = HttpConnection.create(this.url.setCharset(this.charset).toURL(this.urlHandler), this.config.proxy).setConnectTimeout(this.config.connectionTimeout).setReadTimeout(this.config.readTimeout).setMethod(this.method).setHttpsInfo(this.config.hostnameVerifier, this.config.ssf).setInstanceFollowRedirects(false).setChunkedStreamingMode(this.config.blockSize).header(this.headers, true);
/*      */     
/* 1167 */     if (null != this.cookie) {
/*      */       
/* 1169 */       this.httpConnection.setCookie(this.cookie);
/*      */     } else {
/*      */       
/* 1172 */       GlobalCookieManager.add(this.httpConnection);
/*      */     } 
/*      */ 
/*      */     
/* 1176 */     if (this.config.isDisableCache) {
/* 1177 */       this.httpConnection.disableCache();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void urlWithParamIfGet() {
/* 1187 */     if (Method.GET.equals(this.method) && false == this.isRest && this.redirectCount <= 0) {
/* 1188 */       UrlQuery query = this.url.getQuery();
/* 1189 */       if (null == query) {
/* 1190 */         query = new UrlQuery();
/* 1191 */         this.url.setQuery(query);
/*      */       } 
/*      */ 
/*      */       
/* 1195 */       if (ArrayUtil.isNotEmpty(this.bodyBytes)) {
/* 1196 */         query.parse(StrUtil.str(this.bodyBytes, this.charset), this.charset);
/*      */       } else {
/* 1198 */         query.addAll(this.form);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HttpResponse sendRedirectIfPossible(boolean isAsync) {
/* 1211 */     if (this.config.maxRedirectCount > 0) {
/*      */       int responseCode;
/*      */       try {
/* 1214 */         responseCode = this.httpConnection.responseCode();
/* 1215 */       } catch (IOException e) {
/*      */         
/* 1217 */         this.httpConnection.disconnectQuietly();
/* 1218 */         throw new HttpException(e);
/*      */       } 
/*      */       
/* 1221 */       if (responseCode != 200 && 
/* 1222 */         HttpStatus.isRedirected(responseCode)) {
/* 1223 */         setUrl(UrlBuilder.ofHttpWithoutEncode(this.httpConnection.header(Header.LOCATION)));
/* 1224 */         if (this.redirectCount < this.config.maxRedirectCount) {
/* 1225 */           this.redirectCount++;
/*      */           
/* 1227 */           return doExecute(isAsync, this.config.interceptorOnRedirect ? this.config.requestInterceptors : null, this.config.interceptorOnRedirect ? this.config.responseInterceptors : null);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1233 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void send() throws IORuntimeException {
/*      */     try {
/* 1243 */       if (Method.POST.equals(this.method) || Method.PUT
/* 1244 */         .equals(this.method) || Method.DELETE
/* 1245 */         .equals(this.method) || this.isRest) {
/*      */         
/* 1247 */         if (isMultipart()) {
/* 1248 */           sendMultipart();
/*      */         } else {
/* 1250 */           sendFormUrlEncoded();
/*      */         } 
/*      */       } else {
/* 1253 */         this.httpConnection.connect();
/*      */       } 
/* 1255 */     } catch (IOException e) {
/*      */       
/* 1257 */       this.httpConnection.disconnectQuietly();
/* 1258 */       throw new IORuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendFormUrlEncoded() throws IOException {
/*      */     FormUrlEncodedBody formUrlEncodedBody;
/* 1269 */     if (StrUtil.isBlank(header(Header.CONTENT_TYPE)))
/*      */     {
/* 1271 */       this.httpConnection.header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.toString(this.charset), true);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1276 */     if (ArrayUtil.isNotEmpty(this.bodyBytes)) {
/* 1277 */       BytesBody bytesBody = BytesBody.create(this.bodyBytes);
/*      */     } else {
/* 1279 */       formUrlEncodedBody = FormUrlEncodedBody.create(this.form, this.charset);
/*      */     } 
/* 1281 */     formUrlEncodedBody.writeClose(this.httpConnection.getOutputStream());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendMultipart() throws IOException {
/* 1291 */     MultipartBody multipartBody = MultipartBody.create(this.form, this.charset);
/*      */     
/* 1293 */     this.httpConnection.header(Header.CONTENT_TYPE, multipartBody.getContentType(), true);
/* 1294 */     multipartBody.writeClose(this.httpConnection.getOutputStream());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isIgnoreResponseBody() {
/* 1305 */     return (Method.HEAD == this.method || Method.CONNECT == this.method || Method.OPTIONS == this.method || Method.TRACE == this.method);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isMultipart() {
/* 1323 */     if (this.isMultiPart) {
/* 1324 */       return true;
/*      */     }
/*      */     
/* 1327 */     String contentType = header(Header.CONTENT_TYPE);
/* 1328 */     return (StrUtil.isNotEmpty(contentType) && contentType
/* 1329 */       .startsWith(ContentType.MULTIPART.getValue()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HttpRequest putToForm(String name, Object value) {
/* 1340 */     if (null == name || null == value) {
/* 1341 */       return this;
/*      */     }
/* 1343 */     if (null == this.form) {
/* 1344 */       this.form = (Map<String, Object>)new TableMap(16);
/*      */     }
/* 1346 */     this.form.put(name, value);
/* 1347 */     return this;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */