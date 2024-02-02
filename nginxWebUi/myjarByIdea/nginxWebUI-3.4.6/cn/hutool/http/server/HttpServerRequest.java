package cn.hutool.http.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.multipart.MultipartFormData;
import cn.hutool.core.net.multipart.UploadSetting;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.SerializedLambda;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpServerRequest extends HttpServerBase {
   private Map<String, HttpCookie> cookieCache;
   private ListValueMap<String, String> paramsCache;
   private MultipartFormData multipartFormDataCache;
   private Charset charsetCache;
   private byte[] bodyCache;

   public HttpServerRequest(HttpExchange httpExchange) {
      super(httpExchange);
   }

   public String getMethod() {
      return this.httpExchange.getRequestMethod();
   }

   public boolean isGetMethod() {
      return Method.GET.name().equalsIgnoreCase(this.getMethod());
   }

   public boolean isPostMethod() {
      return Method.POST.name().equalsIgnoreCase(this.getMethod());
   }

   public URI getURI() {
      return this.httpExchange.getRequestURI();
   }

   public String getPath() {
      return this.getURI().getPath();
   }

   public String getQuery() {
      return this.getURI().getQuery();
   }

   public Headers getHeaders() {
      return this.httpExchange.getRequestHeaders();
   }

   public String getHeader(Header headerKey) {
      return this.getHeader(headerKey.toString());
   }

   public String getHeader(String headerKey) {
      return this.getHeaders().getFirst(headerKey);
   }

   public String getHeader(String headerKey, Charset charset) {
      String header = this.getHeader(headerKey);
      return null != header ? CharsetUtil.convert(header, CharsetUtil.CHARSET_ISO_8859_1, charset) : null;
   }

   public String getContentType() {
      return this.getHeader(Header.CONTENT_TYPE);
   }

   public Charset getCharset() {
      if (null == this.charsetCache) {
         String contentType = this.getContentType();
         String charsetStr = HttpUtil.getCharset(contentType);
         this.charsetCache = CharsetUtil.parse(charsetStr, DEFAULT_CHARSET);
      }

      return this.charsetCache;
   }

   public String getUserAgentStr() {
      return this.getHeader(Header.USER_AGENT);
   }

   public UserAgent getUserAgent() {
      return UserAgentUtil.parse(this.getUserAgentStr());
   }

   public String getCookiesStr() {
      return this.getHeader(Header.COOKIE);
   }

   public Collection<HttpCookie> getCookies() {
      return this.getCookieMap().values();
   }

   public Map<String, HttpCookie> getCookieMap() {
      if (null == this.cookieCache) {
         this.cookieCache = Collections.unmodifiableMap(CollUtil.toMap(NetUtil.parseCookies(this.getCookiesStr()), new CaseInsensitiveMap(), HttpCookie::getName));
      }

      return this.cookieCache;
   }

   public HttpCookie getCookie(String cookieName) {
      return (HttpCookie)this.getCookieMap().get(cookieName);
   }

   public boolean isMultipart() {
      if (!this.isPostMethod()) {
         return false;
      } else {
         String contentType = this.getContentType();
         return StrUtil.isBlank(contentType) ? false : contentType.toLowerCase().startsWith("multipart/");
      }
   }

   public String getBody() {
      return this.getBody(this.getCharset());
   }

   public String getBody(Charset charset) {
      return StrUtil.str(this.getBodyBytes(), charset);
   }

   public byte[] getBodyBytes() {
      if (null == this.bodyCache) {
         this.bodyCache = IoUtil.readBytes(this.getBodyStream(), true);
      }

      return this.bodyCache;
   }

   public InputStream getBodyStream() {
      return this.httpExchange.getRequestBody();
   }

   public String getParam(String name) {
      return (String)this.getParams().get(name, 0);
   }

   public List<String> getParams(String name) {
      return (List)this.getParams().get(name);
   }

   public ListValueMap<String, String> getParams() {
      if (null == this.paramsCache) {
         this.paramsCache = new ListValueMap();
         Charset charset = this.getCharset();
         String query = this.getQuery();
         if (StrUtil.isNotBlank(query)) {
            this.paramsCache.putAll(HttpUtil.decodeParams(query, charset));
         }

         if (this.isMultipart()) {
            this.paramsCache.putAll(this.getMultipart().getParamListMap());
         } else {
            String body = this.getBody();
            if (StrUtil.isNotBlank(body)) {
               this.paramsCache.putAll(HttpUtil.decodeParams(body, charset));
            }
         }
      }

      return this.paramsCache;
   }

   public String getClientIP(String... otherHeaderNames) {
      String[] headers = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
      if (ArrayUtil.isNotEmpty((Object[])otherHeaderNames)) {
         headers = (String[])ArrayUtil.addAll(headers, otherHeaderNames);
      }

      return this.getClientIPByHeader(headers);
   }

   public String getClientIPByHeader(String... headerNames) {
      String[] var3 = headerNames;
      int var4 = headerNames.length;

      String ip;
      for(int var5 = 0; var5 < var4; ++var5) {
         String header = var3[var5];
         ip = this.getHeader(header);
         if (!NetUtil.isUnknown(ip)) {
            return NetUtil.getMultistageReverseProxyIp(ip);
         }
      }

      ip = this.httpExchange.getRemoteAddress().getHostName();
      return NetUtil.getMultistageReverseProxyIp(ip);
   }

   public MultipartFormData getMultipart() throws IORuntimeException {
      if (null == this.multipartFormDataCache) {
         this.multipartFormDataCache = this.parseMultipart(new UploadSetting());
      }

      return this.multipartFormDataCache;
   }

   public MultipartFormData parseMultipart(UploadSetting uploadSetting) throws IORuntimeException {
      MultipartFormData formData = new MultipartFormData(uploadSetting);

      try {
         formData.parseRequestStream(this.getBodyStream(), this.getCharset());
         return formData;
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "getName":
            if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("java/net/HttpCookie") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
               return HttpCookie::getName;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
