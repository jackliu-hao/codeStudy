package cn.hutool.core.net.url;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.RFC3986;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;

public final class UrlBuilder implements Builder<String> {
   private static final long serialVersionUID = 1L;
   private static final String DEFAULT_SCHEME = "http";
   private String scheme;
   private String host;
   private int port = -1;
   private UrlPath path;
   private UrlQuery query;
   private String fragment;
   private Charset charset;
   private boolean needEncodePercent;

   public static UrlBuilder of(URI uri, Charset charset) {
      return of(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getRawQuery(), uri.getFragment(), charset);
   }

   public static UrlBuilder ofHttpWithoutEncode(String httpUrl) {
      return ofHttp(httpUrl, (Charset)null);
   }

   public static UrlBuilder ofHttp(String httpUrl) {
      return ofHttp(httpUrl, CharsetUtil.CHARSET_UTF_8);
   }

   public static UrlBuilder ofHttp(String httpUrl, Charset charset) {
      Assert.notBlank(httpUrl, "Http url must be not blank!");
      int sepIndex = httpUrl.indexOf("://");
      if (sepIndex < 0) {
         httpUrl = "http://" + httpUrl.trim();
      }

      return of(httpUrl, charset);
   }

   public static UrlBuilder of(String url) {
      return of(url, CharsetUtil.CHARSET_UTF_8);
   }

   public static UrlBuilder of(String url, Charset charset) {
      Assert.notBlank(url, "Url must be not blank!");
      return of(URLUtil.url(StrUtil.trim(url)), charset);
   }

   public static UrlBuilder of(URL url, Charset charset) {
      return of(url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef(), charset);
   }

   public static UrlBuilder of(String scheme, String host, int port, String path, String query, String fragment, Charset charset) {
      return of(scheme, host, port, UrlPath.of(path, charset), UrlQuery.of(query, charset, false), fragment, charset);
   }

   public static UrlBuilder of(String scheme, String host, int port, UrlPath path, UrlQuery query, String fragment, Charset charset) {
      return new UrlBuilder(scheme, host, port, path, query, fragment, charset);
   }

   public static UrlBuilder create() {
      return new UrlBuilder();
   }

   public UrlBuilder() {
      this.charset = CharsetUtil.CHARSET_UTF_8;
   }

   public UrlBuilder(String scheme, String host, int port, UrlPath path, UrlQuery query, String fragment, Charset charset) {
      this.charset = charset;
      this.scheme = scheme;
      this.host = host;
      this.port = port;
      this.path = path;
      this.query = query;
      this.setFragment(fragment);
      this.needEncodePercent = null != charset;
   }

   public String getScheme() {
      return this.scheme;
   }

   public String getSchemeWithDefault() {
      return StrUtil.emptyToDefault(this.scheme, "http");
   }

   public UrlBuilder setScheme(String scheme) {
      this.scheme = scheme;
      return this;
   }

   public String getHost() {
      return this.host;
   }

   public UrlBuilder setHost(String host) {
      this.host = host;
      return this;
   }

   public int getPort() {
      return this.port;
   }

   public UrlBuilder setPort(int port) {
      this.port = port;
      return this;
   }

   public String getAuthority() {
      return this.port < 0 ? this.host : this.host + ":" + this.port;
   }

   public UrlPath getPath() {
      return this.path;
   }

   public String getPathStr() {
      return null == this.path ? "/" : this.path.build(this.charset, this.needEncodePercent);
   }

   public UrlBuilder setPath(UrlPath path) {
      this.path = path;
      return this;
   }

   public UrlBuilder addPath(CharSequence path) {
      UrlPath.of(path, this.charset).getSegments().forEach(this::addPathSegment);
      return this;
   }

   public UrlBuilder addPathSegment(CharSequence segment) {
      if (StrUtil.isEmpty(segment)) {
         return this;
      } else {
         if (null == this.path) {
            this.path = new UrlPath();
         }

         this.path.add(segment);
         return this;
      }
   }

   /** @deprecated */
   @Deprecated
   public UrlBuilder appendPath(CharSequence path) {
      return this.addPath(path);
   }

   public UrlQuery getQuery() {
      return this.query;
   }

   public String getQueryStr() {
      return null == this.query ? null : this.query.build(this.charset, this.needEncodePercent);
   }

   public UrlBuilder setQuery(UrlQuery query) {
      this.query = query;
      return this;
   }

   public UrlBuilder addQuery(String key, Object value) {
      if (StrUtil.isEmpty(key)) {
         return this;
      } else {
         if (this.query == null) {
            this.query = new UrlQuery();
         }

         this.query.add(key, value);
         return this;
      }
   }

   public String getFragment() {
      return this.fragment;
   }

   public String getFragmentEncoded() {
      char[] safeChars = this.needEncodePercent ? null : new char[]{'%'};
      return RFC3986.FRAGMENT.encode(this.fragment, this.charset, safeChars);
   }

   public UrlBuilder setFragment(String fragment) {
      if (StrUtil.isEmpty(fragment)) {
         this.fragment = null;
      }

      this.fragment = StrUtil.removePrefix(fragment, "#");
      return this;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public UrlBuilder setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public String build() {
      return this.toURL().toString();
   }

   public URL toURL() {
      return this.toURL((URLStreamHandler)null);
   }

   public URL toURL(URLStreamHandler handler) {
      StringBuilder fileBuilder = new StringBuilder();
      fileBuilder.append(StrUtil.blankToDefault(this.getPathStr(), "/"));
      String query = this.getQueryStr();
      if (StrUtil.isNotBlank(query)) {
         fileBuilder.append('?').append(query);
      }

      if (StrUtil.isNotBlank(this.fragment)) {
         fileBuilder.append('#').append(this.getFragmentEncoded());
      }

      try {
         return new URL(this.getSchemeWithDefault(), this.host, this.port, fileBuilder.toString(), handler);
      } catch (MalformedURLException var5) {
         return null;
      }
   }

   public URI toURI() {
      try {
         return new URI(this.getSchemeWithDefault(), this.getAuthority(), this.getPathStr(), this.getQueryStr(), this.getFragmentEncoded());
      } catch (URISyntaxException var2) {
         return null;
      }
   }

   public String toString() {
      return this.build();
   }
}
