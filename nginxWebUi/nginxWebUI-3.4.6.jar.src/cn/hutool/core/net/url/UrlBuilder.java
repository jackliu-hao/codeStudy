/*     */ package cn.hutool.core.net.url;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.net.RFC3986;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLStreamHandler;
/*     */ import java.nio.charset.Charset;
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
/*     */ public final class UrlBuilder
/*     */   implements Builder<String>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String DEFAULT_SCHEME = "http";
/*     */   private String scheme;
/*     */   private String host;
/*  44 */   private int port = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private UrlPath path;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private UrlQuery query;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String fragment;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Charset charset;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean needEncodePercent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlBuilder of(URI uri, Charset charset) {
/*  76 */     return of(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getRawQuery(), uri.getFragment(), charset);
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
/*     */   public static UrlBuilder ofHttpWithoutEncode(String httpUrl) {
/*  88 */     return ofHttp(httpUrl, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlBuilder ofHttp(String httpUrl) {
/*  99 */     return ofHttp(httpUrl, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlBuilder ofHttp(String httpUrl, Charset charset) {
/* 110 */     Assert.notBlank(httpUrl, "Http url must be not blank!", new Object[0]);
/*     */     
/* 112 */     int sepIndex = httpUrl.indexOf("://");
/* 113 */     if (sepIndex < 0) {
/* 114 */       httpUrl = "http://" + httpUrl.trim();
/*     */     }
/* 116 */     return of(httpUrl, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlBuilder of(String url) {
/* 126 */     return of(url, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlBuilder of(String url, Charset charset) {
/* 137 */     Assert.notBlank(url, "Url must be not blank!", new Object[0]);
/* 138 */     return of(URLUtil.url(StrUtil.trim(url)), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlBuilder of(URL url, Charset charset) {
/* 149 */     return of(url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef(), charset);
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
/*     */   public static UrlBuilder of(String scheme, String host, int port, String path, String query, String fragment, Charset charset) {
/* 165 */     return of(scheme, host, port, 
/* 166 */         UrlPath.of(path, charset), 
/* 167 */         UrlQuery.of(query, charset, false), fragment, charset);
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
/*     */   public static UrlBuilder of(String scheme, String host, int port, UrlPath path, UrlQuery query, String fragment, Charset charset) {
/* 183 */     return new UrlBuilder(scheme, host, port, path, query, fragment, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static UrlBuilder create() {
/* 192 */     return new UrlBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder() {
/* 199 */     this.charset = CharsetUtil.CHARSET_UTF_8;
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
/*     */   public UrlBuilder(String scheme, String host, int port, UrlPath path, UrlQuery query, String fragment, Charset charset) {
/* 214 */     this.charset = charset;
/* 215 */     this.scheme = scheme;
/* 216 */     this.host = host;
/* 217 */     this.port = port;
/* 218 */     this.path = path;
/* 219 */     this.query = query;
/* 220 */     setFragment(fragment);
/*     */     
/* 222 */     this.needEncodePercent = (null != charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 231 */     return this.scheme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSchemeWithDefault() {
/* 240 */     return StrUtil.emptyToDefault(this.scheme, "http");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder setScheme(String scheme) {
/* 250 */     this.scheme = scheme;
/* 251 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 260 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder setHost(String host) {
/* 270 */     this.host = host;
/* 271 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 280 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder setPort(int port) {
/* 290 */     this.port = port;
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAuthority() {
/* 300 */     return (this.port < 0) ? this.host : (this.host + ":" + this.port);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlPath getPath() {
/* 309 */     return this.path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPathStr() {
/* 318 */     return (null == this.path) ? "/" : this.path.build(this.charset, this.needEncodePercent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder setPath(UrlPath path) {
/* 328 */     this.path = path;
/* 329 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder addPath(CharSequence path) {
/* 339 */     UrlPath.of(path, this.charset).getSegments().forEach(this::addPathSegment);
/* 340 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder addPathSegment(CharSequence segment) {
/* 351 */     if (StrUtil.isEmpty(segment)) {
/* 352 */       return this;
/*     */     }
/* 354 */     if (null == this.path) {
/* 355 */       this.path = new UrlPath();
/*     */     }
/* 357 */     this.path.add(segment);
/* 358 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public UrlBuilder appendPath(CharSequence path) {
/* 370 */     return addPath(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlQuery getQuery() {
/* 380 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQueryStr() {
/* 389 */     return (null == this.query) ? null : this.query.build(this.charset, this.needEncodePercent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder setQuery(UrlQuery query) {
/* 399 */     this.query = query;
/* 400 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder addQuery(String key, Object value) {
/* 411 */     if (StrUtil.isEmpty(key)) {
/* 412 */       return this;
/*     */     }
/*     */     
/* 415 */     if (this.query == null) {
/* 416 */       this.query = new UrlQuery();
/*     */     }
/* 418 */     this.query.add(key, value);
/* 419 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFragment() {
/* 428 */     return this.fragment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFragmentEncoded() {
/* 437 */     (new char[1])[0] = '%'; char[] safeChars = this.needEncodePercent ? null : new char[1];
/* 438 */     return RFC3986.FRAGMENT.encode(this.fragment, this.charset, safeChars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder setFragment(String fragment) {
/* 448 */     if (StrUtil.isEmpty(fragment)) {
/* 449 */       this.fragment = null;
/*     */     }
/* 451 */     this.fragment = StrUtil.removePrefix(fragment, "#");
/* 452 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 461 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UrlBuilder setCharset(Charset charset) {
/* 471 */     this.charset = charset;
/* 472 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String build() {
/* 482 */     return toURL().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL toURL() {
/* 491 */     return toURL(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URL toURL(URLStreamHandler handler) {
/* 501 */     StringBuilder fileBuilder = new StringBuilder();
/*     */ 
/*     */     
/* 504 */     fileBuilder.append(StrUtil.blankToDefault(getPathStr(), "/"));
/*     */ 
/*     */     
/* 507 */     String query = getQueryStr();
/* 508 */     if (StrUtil.isNotBlank(query)) {
/* 509 */       fileBuilder.append('?').append(query);
/*     */     }
/*     */ 
/*     */     
/* 513 */     if (StrUtil.isNotBlank(this.fragment)) {
/* 514 */       fileBuilder.append('#').append(getFragmentEncoded());
/*     */     }
/*     */     
/*     */     try {
/* 518 */       return new URL(getSchemeWithDefault(), this.host, this.port, fileBuilder.toString(), handler);
/* 519 */     } catch (MalformedURLException e) {
/* 520 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI toURI() {
/*     */     try {
/* 531 */       return new URI(
/* 532 */           getSchemeWithDefault(), 
/* 533 */           getAuthority(), 
/* 534 */           getPathStr(), 
/* 535 */           getQueryStr(), 
/* 536 */           getFragmentEncoded());
/* 537 */     } catch (URISyntaxException e) {
/* 538 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 544 */     return build();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\ne\\url\UrlBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */