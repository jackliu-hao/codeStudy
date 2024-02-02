package cn.hutool.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.BytesResource;
import cn.hutool.core.io.resource.FileResource;
import cn.hutool.core.io.resource.MultiFileResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.body.BytesBody;
import cn.hutool.http.body.FormUrlEncodedBody;
import cn.hutool.http.body.MultipartBody;
import cn.hutool.http.body.RequestBody;
import cn.hutool.http.cookie.GlobalCookieManager;
import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.Proxy;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class HttpRequest extends HttpBase<HttpRequest> {
   private HttpConfig config;
   private UrlBuilder url;
   private URLStreamHandler urlHandler;
   private Method method;
   private HttpConnection httpConnection;
   private Map<String, Object> form;
   private String cookie;
   private boolean isMultiPart;
   private boolean isRest;
   private int redirectCount;

   public static HttpRequest post(String url) {
      return of(url).method(Method.POST);
   }

   public static HttpRequest get(String url) {
      return of(url).method(Method.GET);
   }

   public static HttpRequest head(String url) {
      return of(url).method(Method.HEAD);
   }

   public static HttpRequest options(String url) {
      return of(url).method(Method.OPTIONS);
   }

   public static HttpRequest put(String url) {
      return of(url).method(Method.PUT);
   }

   public static HttpRequest patch(String url) {
      return of(url).method(Method.PATCH);
   }

   public static HttpRequest delete(String url) {
      return of(url).method(Method.DELETE);
   }

   public static HttpRequest trace(String url) {
      return of(url).method(Method.TRACE);
   }

   public static HttpRequest of(String url) {
      return of(url, HttpGlobalConfig.isDecodeUrl() ? DEFAULT_CHARSET : null);
   }

   public static HttpRequest of(String url, Charset charset) {
      return of(UrlBuilder.ofHttp(url, charset));
   }

   public static HttpRequest of(UrlBuilder url) {
      return new HttpRequest(url);
   }

   public static void setGlobalTimeout(int customTimeout) {
      HttpGlobalConfig.setTimeout(customTimeout);
   }

   public static CookieManager getCookieManager() {
      return GlobalCookieManager.getCookieManager();
   }

   public static void setCookieManager(CookieManager customCookieManager) {
      GlobalCookieManager.setCookieManager(customCookieManager);
   }

   public static void closeCookie() {
      GlobalCookieManager.setCookieManager((CookieManager)null);
   }

   /** @deprecated */
   @Deprecated
   public HttpRequest(String url) {
      this(UrlBuilder.ofHttp(url));
   }

   public HttpRequest(UrlBuilder url) {
      this.config = HttpConfig.create();
      this.method = Method.GET;
      this.url = (UrlBuilder)Assert.notNull(url, "URL must be not null!");
      Charset charset = url.getCharset();
      if (null != charset) {
         this.charset(charset);
      }

      this.header(GlobalHeaders.INSTANCE.headers);
   }

   public String getUrl() {
      return this.url.toString();
   }

   public HttpRequest setUrl(String url) {
      return this.setUrl(UrlBuilder.ofHttp(url, this.charset));
   }

   public HttpRequest setUrl(UrlBuilder urlBuilder) {
      this.url = urlBuilder;
      return this;
   }

   public HttpRequest setUrlHandler(URLStreamHandler urlHandler) {
      this.urlHandler = urlHandler;
      return this;
   }

   public Method getMethod() {
      return this.method;
   }

   public HttpRequest setMethod(Method method) {
      return this.method(method);
   }

   public HttpConnection getConnection() {
      return this.httpConnection;
   }

   public HttpRequest method(Method method) {
      this.method = method;
      return this;
   }

   public HttpRequest contentType(String contentType) {
      this.header(Header.CONTENT_TYPE, contentType);
      return this;
   }

   public HttpRequest keepAlive(boolean isKeepAlive) {
      this.header(Header.CONNECTION, isKeepAlive ? "Keep-Alive" : "Close");
      return this;
   }

   public boolean isKeepAlive() {
      String connection = this.header(Header.CONNECTION);
      if (connection == null) {
         return !"HTTP/1.0".equalsIgnoreCase(this.httpVersion);
      } else {
         return !"close".equalsIgnoreCase(connection);
      }
   }

   public String contentLength() {
      return this.header(Header.CONTENT_LENGTH);
   }

   public HttpRequest contentLength(int value) {
      this.header(Header.CONTENT_LENGTH, String.valueOf(value));
      return this;
   }

   public HttpRequest cookie(Collection<HttpCookie> cookies) {
      return this.cookie(CollUtil.isEmpty(cookies) ? null : (HttpCookie[])cookies.toArray(new HttpCookie[0]));
   }

   public HttpRequest cookie(HttpCookie... cookies) {
      return ArrayUtil.isEmpty((Object[])cookies) ? this.disableCookie() : this.cookie(ArrayUtil.join((Object[])cookies, "; "));
   }

   public HttpRequest cookie(String cookie) {
      this.cookie = cookie;
      return this;
   }

   public HttpRequest disableCookie() {
      return this.cookie("");
   }

   public HttpRequest enableDefaultCookie() {
      return this.cookie((String)null);
   }

   public HttpRequest form(String name, Object value) {
      if (!StrUtil.isBlank(name) && !ObjectUtil.isNull(value)) {
         this.bodyBytes = null;
         if (value instanceof File) {
            return this.form(name, (File)value);
         } else if (value instanceof Resource) {
            return this.form(name, (Resource)value);
         } else {
            String strValue;
            if (value instanceof Iterable) {
               strValue = CollUtil.join((Iterable)((Iterable)value), ",");
            } else if (ArrayUtil.isArray(value)) {
               if (File.class == ArrayUtil.getComponentType(value)) {
                  return this.form(name, (File[])((File[])value));
               }

               strValue = ArrayUtil.join((Object[])((Object[])((Object[])value)), ",");
            } else {
               strValue = Convert.toStr(value, (String)null);
            }

            return this.putToForm(name, strValue);
         }
      } else {
         return this;
      }
   }

   public HttpRequest form(String name, Object value, Object... parameters) {
      this.form(name, value);

      for(int i = 0; i < parameters.length; i += 2) {
         this.form(parameters[i].toString(), parameters[i + 1]);
      }

      return this;
   }

   public HttpRequest form(Map<String, Object> formMap) {
      if (MapUtil.isNotEmpty(formMap)) {
         formMap.forEach(this::form);
      }

      return this;
   }

   public HttpRequest formStr(Map<String, String> formMapStr) {
      if (MapUtil.isNotEmpty(formMapStr)) {
         formMapStr.forEach(this::form);
      }

      return this;
   }

   public HttpRequest form(String name, File... files) {
      if (ArrayUtil.isEmpty((Object[])files)) {
         return this;
      } else if (1 == files.length) {
         File file = files[0];
         return this.form(name, file, file.getName());
      } else {
         return this.form(name, (Resource)(new MultiFileResource(files)));
      }
   }

   public HttpRequest form(String name, File file) {
      return this.form(name, file, file.getName());
   }

   public HttpRequest form(String name, File file, String fileName) {
      if (null != file) {
         this.form(name, (Resource)(new FileResource(file, fileName)));
      }

      return this;
   }

   public HttpRequest form(String name, byte[] fileBytes, String fileName) {
      if (null != fileBytes) {
         this.form(name, (Resource)(new BytesResource(fileBytes, fileName)));
      }

      return this;
   }

   public HttpRequest form(String name, Resource resource) {
      if (null != resource) {
         if (!this.isKeepAlive()) {
            this.keepAlive(true);
         }

         this.isMultiPart = true;
         return this.putToForm(name, resource);
      } else {
         return this;
      }
   }

   public Map<String, Object> form() {
      return this.form;
   }

   public Map<String, Resource> fileForm() {
      Map<String, Resource> result = MapUtil.newHashMap();
      this.form.forEach((key, value) -> {
         if (value instanceof Resource) {
            result.put(key, (Resource)value);
         }

      });
      return result;
   }

   public HttpRequest body(String body) {
      return this.body(body, (String)null);
   }

   public HttpRequest body(String body, String contentType) {
      byte[] bytes = StrUtil.bytes(body, this.charset);
      this.body(bytes);
      this.form = null;
      if (null != contentType) {
         this.contentType(contentType);
      } else {
         contentType = HttpUtil.getContentTypeByRequestBody(body);
         if (null != contentType && ContentType.isDefault(this.header(Header.CONTENT_TYPE))) {
            if (null != this.charset) {
               contentType = ContentType.build(contentType, this.charset);
            }

            this.contentType(contentType);
         }
      }

      if (StrUtil.containsAnyIgnoreCase(contentType, new CharSequence[]{"json", "xml"})) {
         this.isRest = true;
         this.contentLength(bytes.length);
      }

      return this;
   }

   public HttpRequest body(byte[] bodyBytes) {
      if (null != bodyBytes) {
         this.bodyBytes = bodyBytes;
      }

      return this;
   }

   public HttpRequest setConfig(HttpConfig config) {
      this.config = config;
      return this;
   }

   public HttpRequest timeout(int milliseconds) {
      this.config.timeout(milliseconds);
      return this;
   }

   public HttpRequest setConnectionTimeout(int milliseconds) {
      this.config.setConnectionTimeout(milliseconds);
      return this;
   }

   public HttpRequest setReadTimeout(int milliseconds) {
      this.config.setReadTimeout(milliseconds);
      return this;
   }

   public HttpRequest disableCache() {
      this.config.disableCache();
      return this;
   }

   public HttpRequest setFollowRedirects(boolean isFollowRedirects) {
      return this.setMaxRedirectCount(isFollowRedirects ? 2 : 0);
   }

   public HttpRequest setMaxRedirectCount(int maxRedirectCount) {
      this.config.setMaxRedirectCount(maxRedirectCount);
      return this;
   }

   public HttpRequest setHostnameVerifier(HostnameVerifier hostnameVerifier) {
      this.config.setHostnameVerifier(hostnameVerifier);
      return this;
   }

   public HttpRequest setHttpProxy(String host, int port) {
      this.config.setHttpProxy(host, port);
      return this;
   }

   public HttpRequest setProxy(Proxy proxy) {
      this.config.setProxy(proxy);
      return this;
   }

   public HttpRequest setSSLSocketFactory(SSLSocketFactory ssf) {
      this.config.setSSLSocketFactory(ssf);
      return this;
   }

   public HttpRequest setSSLProtocol(String protocol) {
      this.config.setSSLProtocol(protocol);
      return this;
   }

   public HttpRequest setRest(boolean isRest) {
      this.isRest = isRest;
      return this;
   }

   public HttpRequest setChunkedStreamingMode(int blockSize) {
      this.config.setBlockSize(blockSize);
      return this;
   }

   public HttpRequest addInterceptor(HttpInterceptor<HttpRequest> interceptor) {
      return this.addRequestInterceptor(interceptor);
   }

   public HttpRequest addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
      this.config.addRequestInterceptor(interceptor);
      return this;
   }

   public HttpRequest addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
      this.config.addResponseInterceptor(interceptor);
      return this;
   }

   public HttpResponse execute() {
      return this.execute(false);
   }

   public HttpResponse executeAsync() {
      return this.execute(true);
   }

   public HttpResponse execute(boolean isAsync) {
      return this.doExecute(isAsync, this.config.requestInterceptors, this.config.responseInterceptors);
   }

   public void then(Consumer<HttpResponse> consumer) {
      HttpResponse response = this.execute(true);
      Throwable var3 = null;

      try {
         consumer.accept(response);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (response != null) {
            if (var3 != null) {
               try {
                  response.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               response.close();
            }
         }

      }

   }

   public HttpRequest basicAuth(String username, String password) {
      return this.auth(HttpUtil.buildBasicAuth(username, password, this.charset));
   }

   public HttpRequest basicProxyAuth(String username, String password) {
      return this.proxyAuth(HttpUtil.buildBasicAuth(username, password, this.charset));
   }

   public HttpRequest bearerAuth(String token) {
      return this.auth("Bearer " + token);
   }

   public HttpRequest auth(String content) {
      this.header(Header.AUTHORIZATION, content, true);
      return this;
   }

   public HttpRequest proxyAuth(String content) {
      this.header(Header.PROXY_AUTHORIZATION, content, true);
      return this;
   }

   public String toString() {
      StringBuilder sb = StrUtil.builder();
      sb.append("Request Url: ").append(this.url.setCharset(this.charset)).append("\r\n");
      sb.append(super.toString());
      return sb.toString();
   }

   private HttpResponse doExecute(boolean isAsync, HttpInterceptor.Chain<HttpRequest> requestInterceptors, HttpInterceptor.Chain<HttpResponse> responseInterceptors) {
      if (null != requestInterceptors) {
         Iterator var4 = requestInterceptors.iterator();

         while(var4.hasNext()) {
            HttpInterceptor<HttpRequest> interceptor = (HttpInterceptor)var4.next();
            interceptor.process(this);
         }
      }

      this.urlWithParamIfGet();
      this.initConnection();
      this.send();
      HttpResponse httpResponse = this.sendRedirectIfPossible(isAsync);
      if (null == httpResponse) {
         httpResponse = new HttpResponse(this.httpConnection, this.config, this.charset, isAsync, this.isIgnoreResponseBody());
      }

      if (null != responseInterceptors) {
         Iterator var7 = responseInterceptors.iterator();

         while(var7.hasNext()) {
            HttpInterceptor<HttpResponse> interceptor = (HttpInterceptor)var7.next();
            interceptor.process(httpResponse);
         }
      }

      return httpResponse;
   }

   private void initConnection() {
      if (null != this.httpConnection) {
         this.httpConnection.disconnectQuietly();
      }

      this.httpConnection = HttpConnection.create(this.url.setCharset(this.charset).toURL(this.urlHandler), this.config.proxy).setConnectTimeout(this.config.connectionTimeout).setReadTimeout(this.config.readTimeout).setMethod(this.method).setHttpsInfo(this.config.hostnameVerifier, this.config.ssf).setInstanceFollowRedirects(false).setChunkedStreamingMode(this.config.blockSize).header(this.headers, true);
      if (null != this.cookie) {
         this.httpConnection.setCookie(this.cookie);
      } else {
         GlobalCookieManager.add(this.httpConnection);
      }

      if (this.config.isDisableCache) {
         this.httpConnection.disableCache();
      }

   }

   private void urlWithParamIfGet() {
      if (Method.GET.equals(this.method) && !this.isRest && this.redirectCount <= 0) {
         UrlQuery query = this.url.getQuery();
         if (null == query) {
            query = new UrlQuery();
            this.url.setQuery(query);
         }

         if (ArrayUtil.isNotEmpty((byte[])this.bodyBytes)) {
            query.parse(StrUtil.str(this.bodyBytes, this.charset), this.charset);
         } else {
            query.addAll(this.form);
         }
      }

   }

   private HttpResponse sendRedirectIfPossible(boolean isAsync) {
      if (this.config.maxRedirectCount > 0) {
         int responseCode;
         try {
            responseCode = this.httpConnection.responseCode();
         } catch (IOException var4) {
            this.httpConnection.disconnectQuietly();
            throw new HttpException(var4);
         }

         if (responseCode != 200 && HttpStatus.isRedirected(responseCode)) {
            this.setUrl(UrlBuilder.ofHttpWithoutEncode(this.httpConnection.header(Header.LOCATION)));
            if (this.redirectCount < this.config.maxRedirectCount) {
               ++this.redirectCount;
               return this.doExecute(isAsync, this.config.interceptorOnRedirect ? this.config.requestInterceptors : null, this.config.interceptorOnRedirect ? this.config.responseInterceptors : null);
            }
         }
      }

      return null;
   }

   private void send() throws IORuntimeException {
      try {
         if (!Method.POST.equals(this.method) && !Method.PUT.equals(this.method) && !Method.DELETE.equals(this.method) && !this.isRest) {
            this.httpConnection.connect();
         } else if (this.isMultipart()) {
            this.sendMultipart();
         } else {
            this.sendFormUrlEncoded();
         }

      } catch (IOException var2) {
         this.httpConnection.disconnectQuietly();
         throw new IORuntimeException(var2);
      }
   }

   private void sendFormUrlEncoded() throws IOException {
      if (StrUtil.isBlank(this.header(Header.CONTENT_TYPE))) {
         this.httpConnection.header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.toString(this.charset), true);
      }

      Object body;
      if (ArrayUtil.isNotEmpty((byte[])this.bodyBytes)) {
         body = BytesBody.create(this.bodyBytes);
      } else {
         body = FormUrlEncodedBody.create(this.form, this.charset);
      }

      ((RequestBody)body).writeClose(this.httpConnection.getOutputStream());
   }

   private void sendMultipart() throws IOException {
      MultipartBody multipartBody = MultipartBody.create(this.form, this.charset);
      this.httpConnection.header(Header.CONTENT_TYPE, multipartBody.getContentType(), true);
      multipartBody.writeClose(this.httpConnection.getOutputStream());
   }

   private boolean isIgnoreResponseBody() {
      return Method.HEAD == this.method || Method.CONNECT == this.method || Method.OPTIONS == this.method || Method.TRACE == this.method;
   }

   private boolean isMultipart() {
      if (this.isMultiPart) {
         return true;
      } else {
         String contentType = this.header(Header.CONTENT_TYPE);
         return StrUtil.isNotEmpty(contentType) && contentType.startsWith(ContentType.MULTIPART.getValue());
      }
   }

   private HttpRequest putToForm(String name, Object value) {
      if (null != name && null != value) {
         if (null == this.form) {
            this.form = new TableMap(16);
         }

         this.form.put(name, value);
         return this;
      } else {
         return this;
      }
   }
}
