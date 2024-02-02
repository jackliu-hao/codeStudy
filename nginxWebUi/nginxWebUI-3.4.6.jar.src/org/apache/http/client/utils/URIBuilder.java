/*     */ package org.apache.http.client.utils;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.conn.util.InetAddressUtils;
/*     */ import org.apache.http.message.BasicNameValuePair;
/*     */ import org.apache.http.util.TextUtils;
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
/*     */ public class URIBuilder
/*     */ {
/*     */   private String scheme;
/*     */   private String encodedSchemeSpecificPart;
/*     */   private String encodedAuthority;
/*     */   private String userInfo;
/*     */   private String encodedUserInfo;
/*     */   private String host;
/*     */   private int port;
/*     */   private String encodedPath;
/*     */   private List<String> pathSegments;
/*     */   private String encodedQuery;
/*     */   private List<NameValuePair> queryParams;
/*     */   private String query;
/*     */   private Charset charset;
/*     */   private String fragment;
/*     */   private String encodedFragment;
/*     */   
/*     */   public URIBuilder() {
/*  72 */     this.port = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder(String string) throws URISyntaxException {
/*  82 */     this(new URI(string), (Charset)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder(URI uri) {
/*  90 */     this(uri, (Charset)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder(String string, Charset charset) throws URISyntaxException {
/* 100 */     this(new URI(string), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder(URI uri, Charset charset) {
/* 109 */     setCharset(charset);
/* 110 */     digestURI(uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setCharset(Charset charset) {
/* 117 */     this.charset = charset;
/* 118 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 125 */     return this.charset;
/*     */   }
/*     */   
/*     */   private List<NameValuePair> parseQuery(String query, Charset charset) {
/* 129 */     if (query != null && !query.isEmpty()) {
/* 130 */       return URLEncodedUtils.parse(query, charset);
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */   
/*     */   private List<String> parsePath(String path, Charset charset) {
/* 136 */     if (path != null && !path.isEmpty()) {
/* 137 */       return URLEncodedUtils.parsePathSegments(path, charset);
/*     */     }
/* 139 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI build() throws URISyntaxException {
/* 146 */     return new URI(buildString());
/*     */   }
/*     */   
/*     */   private String buildString() {
/* 150 */     StringBuilder sb = new StringBuilder();
/* 151 */     if (this.scheme != null) {
/* 152 */       sb.append(this.scheme).append(':');
/*     */     }
/* 154 */     if (this.encodedSchemeSpecificPart != null) {
/* 155 */       sb.append(this.encodedSchemeSpecificPart);
/*     */     } else {
/* 157 */       if (this.encodedAuthority != null) {
/* 158 */         sb.append("//").append(this.encodedAuthority);
/* 159 */       } else if (this.host != null) {
/* 160 */         sb.append("//");
/* 161 */         if (this.encodedUserInfo != null) {
/* 162 */           sb.append(this.encodedUserInfo).append("@");
/* 163 */         } else if (this.userInfo != null) {
/* 164 */           sb.append(encodeUserInfo(this.userInfo)).append("@");
/*     */         } 
/* 166 */         if (InetAddressUtils.isIPv6Address(this.host)) {
/* 167 */           sb.append("[").append(this.host).append("]");
/*     */         } else {
/* 169 */           sb.append(this.host);
/*     */         } 
/* 171 */         if (this.port >= 0) {
/* 172 */           sb.append(":").append(this.port);
/*     */         }
/*     */       } 
/* 175 */       if (this.encodedPath != null) {
/* 176 */         sb.append(normalizePath(this.encodedPath, (sb.length() == 0)));
/* 177 */       } else if (this.pathSegments != null) {
/* 178 */         sb.append(encodePath(this.pathSegments));
/*     */       } 
/* 180 */       if (this.encodedQuery != null) {
/* 181 */         sb.append("?").append(this.encodedQuery);
/* 182 */       } else if (this.queryParams != null && !this.queryParams.isEmpty()) {
/* 183 */         sb.append("?").append(encodeUrlForm(this.queryParams));
/* 184 */       } else if (this.query != null) {
/* 185 */         sb.append("?").append(encodeUric(this.query));
/*     */       } 
/*     */     } 
/* 188 */     if (this.encodedFragment != null) {
/* 189 */       sb.append("#").append(this.encodedFragment);
/* 190 */     } else if (this.fragment != null) {
/* 191 */       sb.append("#").append(encodeUric(this.fragment));
/*     */     } 
/* 193 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private static String normalizePath(String path, boolean relative) {
/* 197 */     String s = path;
/* 198 */     if (TextUtils.isBlank(s)) {
/* 199 */       return "";
/*     */     }
/* 201 */     if (!relative && !s.startsWith("/")) {
/* 202 */       s = "/" + s;
/*     */     }
/* 204 */     return s;
/*     */   }
/*     */   
/*     */   private void digestURI(URI uri) {
/* 208 */     this.scheme = uri.getScheme();
/* 209 */     this.encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
/* 210 */     this.encodedAuthority = uri.getRawAuthority();
/* 211 */     this.host = uri.getHost();
/* 212 */     this.port = uri.getPort();
/* 213 */     this.encodedUserInfo = uri.getRawUserInfo();
/* 214 */     this.userInfo = uri.getUserInfo();
/* 215 */     this.encodedPath = uri.getRawPath();
/* 216 */     this.pathSegments = parsePath(uri.getRawPath(), (this.charset != null) ? this.charset : Consts.UTF_8);
/* 217 */     this.encodedQuery = uri.getRawQuery();
/* 218 */     this.queryParams = parseQuery(uri.getRawQuery(), (this.charset != null) ? this.charset : Consts.UTF_8);
/* 219 */     this.encodedFragment = uri.getRawFragment();
/* 220 */     this.fragment = uri.getFragment();
/*     */   }
/*     */   
/*     */   private String encodeUserInfo(String userInfo) {
/* 224 */     return URLEncodedUtils.encUserInfo(userInfo, (this.charset != null) ? this.charset : Consts.UTF_8);
/*     */   }
/*     */   
/*     */   private String encodePath(List<String> pathSegments) {
/* 228 */     return URLEncodedUtils.formatSegments(pathSegments, (this.charset != null) ? this.charset : Consts.UTF_8);
/*     */   }
/*     */   
/*     */   private String encodeUrlForm(List<NameValuePair> params) {
/* 232 */     return URLEncodedUtils.format(params, (this.charset != null) ? this.charset : Consts.UTF_8);
/*     */   }
/*     */   
/*     */   private String encodeUric(String fragment) {
/* 236 */     return URLEncodedUtils.encUric(fragment, (this.charset != null) ? this.charset : Consts.UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setScheme(String scheme) {
/* 243 */     this.scheme = scheme;
/* 244 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setUserInfo(String userInfo) {
/* 252 */     this.userInfo = userInfo;
/* 253 */     this.encodedSchemeSpecificPart = null;
/* 254 */     this.encodedAuthority = null;
/* 255 */     this.encodedUserInfo = null;
/* 256 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setUserInfo(String username, String password) {
/* 264 */     return setUserInfo(username + ':' + password);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setHost(String host) {
/* 271 */     this.host = host;
/* 272 */     this.encodedSchemeSpecificPart = null;
/* 273 */     this.encodedAuthority = null;
/* 274 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setPort(int port) {
/* 281 */     this.port = (port < 0) ? -1 : port;
/* 282 */     this.encodedSchemeSpecificPart = null;
/* 283 */     this.encodedAuthority = null;
/* 284 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setPath(String path) {
/* 293 */     return setPathSegments((path != null) ? URLEncodedUtils.splitPathSegments(path) : null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setPathSegments(String... pathSegments) {
/* 304 */     this.pathSegments = (pathSegments.length > 0) ? Arrays.<String>asList(pathSegments) : null;
/* 305 */     this.encodedSchemeSpecificPart = null;
/* 306 */     this.encodedPath = null;
/* 307 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setPathSegments(List<String> pathSegments) {
/* 318 */     this.pathSegments = (pathSegments != null && pathSegments.size() > 0) ? new ArrayList<String>(pathSegments) : null;
/* 319 */     this.encodedSchemeSpecificPart = null;
/* 320 */     this.encodedPath = null;
/* 321 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder removeQuery() {
/* 328 */     this.queryParams = null;
/* 329 */     this.query = null;
/* 330 */     this.encodedQuery = null;
/* 331 */     this.encodedSchemeSpecificPart = null;
/* 332 */     return this;
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
/*     */   @Deprecated
/*     */   public URIBuilder setQuery(String query) {
/* 346 */     this.queryParams = parseQuery(query, (this.charset != null) ? this.charset : Consts.UTF_8);
/* 347 */     this.query = null;
/* 348 */     this.encodedQuery = null;
/* 349 */     this.encodedSchemeSpecificPart = null;
/* 350 */     return this;
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
/*     */   public URIBuilder setParameters(List<NameValuePair> nvps) {
/* 364 */     if (this.queryParams == null) {
/* 365 */       this.queryParams = new ArrayList<NameValuePair>();
/*     */     } else {
/* 367 */       this.queryParams.clear();
/*     */     } 
/* 369 */     this.queryParams.addAll(nvps);
/* 370 */     this.encodedQuery = null;
/* 371 */     this.encodedSchemeSpecificPart = null;
/* 372 */     this.query = null;
/* 373 */     return this;
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
/*     */   public URIBuilder addParameters(List<NameValuePair> nvps) {
/* 387 */     if (this.queryParams == null) {
/* 388 */       this.queryParams = new ArrayList<NameValuePair>();
/*     */     }
/* 390 */     this.queryParams.addAll(nvps);
/* 391 */     this.encodedQuery = null;
/* 392 */     this.encodedSchemeSpecificPart = null;
/* 393 */     this.query = null;
/* 394 */     return this;
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
/*     */   public URIBuilder setParameters(NameValuePair... nvps) {
/* 408 */     if (this.queryParams == null) {
/* 409 */       this.queryParams = new ArrayList<NameValuePair>();
/*     */     } else {
/* 411 */       this.queryParams.clear();
/*     */     } 
/* 413 */     for (NameValuePair nvp : nvps) {
/* 414 */       this.queryParams.add(nvp);
/*     */     }
/* 416 */     this.encodedQuery = null;
/* 417 */     this.encodedSchemeSpecificPart = null;
/* 418 */     this.query = null;
/* 419 */     return this;
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
/*     */   public URIBuilder addParameter(String param, String value) {
/* 431 */     if (this.queryParams == null) {
/* 432 */       this.queryParams = new ArrayList<NameValuePair>();
/*     */     }
/* 434 */     this.queryParams.add(new BasicNameValuePair(param, value));
/* 435 */     this.encodedQuery = null;
/* 436 */     this.encodedSchemeSpecificPart = null;
/* 437 */     this.query = null;
/* 438 */     return this;
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
/*     */   public URIBuilder setParameter(String param, String value) {
/* 450 */     if (this.queryParams == null) {
/* 451 */       this.queryParams = new ArrayList<NameValuePair>();
/*     */     }
/* 453 */     if (!this.queryParams.isEmpty()) {
/* 454 */       for (Iterator<NameValuePair> it = this.queryParams.iterator(); it.hasNext(); ) {
/* 455 */         NameValuePair nvp = it.next();
/* 456 */         if (nvp.getName().equals(param)) {
/* 457 */           it.remove();
/*     */         }
/*     */       } 
/*     */     }
/* 461 */     this.queryParams.add(new BasicNameValuePair(param, value));
/* 462 */     this.encodedQuery = null;
/* 463 */     this.encodedSchemeSpecificPart = null;
/* 464 */     this.query = null;
/* 465 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder clearParameters() {
/* 474 */     this.queryParams = null;
/* 475 */     this.encodedQuery = null;
/* 476 */     this.encodedSchemeSpecificPart = null;
/* 477 */     return this;
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
/*     */   public URIBuilder setCustomQuery(String query) {
/* 491 */     this.query = query;
/* 492 */     this.encodedQuery = null;
/* 493 */     this.encodedSchemeSpecificPart = null;
/* 494 */     this.queryParams = null;
/* 495 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URIBuilder setFragment(String fragment) {
/* 503 */     this.fragment = fragment;
/* 504 */     this.encodedFragment = null;
/* 505 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAbsolute() {
/* 512 */     return (this.scheme != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOpaque() {
/* 519 */     return (this.pathSegments == null && this.encodedPath == null);
/*     */   }
/*     */   
/*     */   public String getScheme() {
/* 523 */     return this.scheme;
/*     */   }
/*     */   
/*     */   public String getUserInfo() {
/* 527 */     return this.userInfo;
/*     */   }
/*     */   
/*     */   public String getHost() {
/* 531 */     return this.host;
/*     */   }
/*     */   
/*     */   public int getPort() {
/* 535 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPathEmpty() {
/* 542 */     return ((this.pathSegments == null || this.pathSegments.isEmpty()) && (this.encodedPath == null || this.encodedPath.isEmpty()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getPathSegments() {
/* 550 */     return (this.pathSegments != null) ? new ArrayList<String>(this.pathSegments) : Collections.<String>emptyList();
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 554 */     if (this.pathSegments == null) {
/* 555 */       return null;
/*     */     }
/* 557 */     StringBuilder result = new StringBuilder();
/* 558 */     for (String segment : this.pathSegments) {
/* 559 */       result.append('/').append(segment);
/*     */     }
/* 561 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isQueryEmpty() {
/* 568 */     return ((this.queryParams == null || this.queryParams.isEmpty()) && this.encodedQuery == null);
/*     */   }
/*     */   
/*     */   public List<NameValuePair> getQueryParams() {
/* 572 */     return (this.queryParams != null) ? new ArrayList<NameValuePair>(this.queryParams) : Collections.<NameValuePair>emptyList();
/*     */   }
/*     */   
/*     */   public String getFragment() {
/* 576 */     return this.fragment;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 581 */     return buildString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\URIBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */