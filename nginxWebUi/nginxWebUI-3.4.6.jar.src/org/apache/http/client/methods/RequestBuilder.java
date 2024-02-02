/*     */ package org.apache.http.client.methods;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.NameValuePair;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.entity.UrlEncodedFormEntity;
/*     */ import org.apache.http.client.utils.URIBuilder;
/*     */ import org.apache.http.client.utils.URLEncodedUtils;
/*     */ import org.apache.http.entity.ContentType;
/*     */ import org.apache.http.message.BasicHeader;
/*     */ import org.apache.http.message.BasicNameValuePair;
/*     */ import org.apache.http.message.HeaderGroup;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ import org.apache.http.util.Args;
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
/*     */ public class RequestBuilder
/*     */ {
/*     */   private String method;
/*     */   private Charset charset;
/*     */   private ProtocolVersion version;
/*     */   private URI uri;
/*     */   private HeaderGroup headerGroup;
/*     */   private HttpEntity entity;
/*     */   private List<NameValuePair> parameters;
/*     */   private RequestConfig config;
/*     */   
/*     */   RequestBuilder(String method) {
/*  82 */     this.charset = Consts.UTF_8;
/*  83 */     this.method = method;
/*     */   }
/*     */ 
/*     */   
/*     */   RequestBuilder(String method, URI uri) {
/*  88 */     this.method = method;
/*  89 */     this.uri = uri;
/*     */   }
/*     */ 
/*     */   
/*     */   RequestBuilder(String method, String uri) {
/*  94 */     this.method = method;
/*  95 */     this.uri = (uri != null) ? URI.create(uri) : null;
/*     */   }
/*     */   
/*     */   RequestBuilder() {
/*  99 */     this(null);
/*     */   }
/*     */   
/*     */   public static RequestBuilder create(String method) {
/* 103 */     Args.notBlank(method, "HTTP method");
/* 104 */     return new RequestBuilder(method);
/*     */   }
/*     */   
/*     */   public static RequestBuilder get() {
/* 108 */     return new RequestBuilder("GET");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder get(URI uri) {
/* 115 */     return new RequestBuilder("GET", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder get(String uri) {
/* 122 */     return new RequestBuilder("GET", uri);
/*     */   }
/*     */   
/*     */   public static RequestBuilder head() {
/* 126 */     return new RequestBuilder("HEAD");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder head(URI uri) {
/* 133 */     return new RequestBuilder("HEAD", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder head(String uri) {
/* 140 */     return new RequestBuilder("HEAD", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder patch() {
/* 147 */     return new RequestBuilder("PATCH");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder patch(URI uri) {
/* 154 */     return new RequestBuilder("PATCH", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder patch(String uri) {
/* 161 */     return new RequestBuilder("PATCH", uri);
/*     */   }
/*     */   
/*     */   public static RequestBuilder post() {
/* 165 */     return new RequestBuilder("POST");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder post(URI uri) {
/* 172 */     return new RequestBuilder("POST", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder post(String uri) {
/* 179 */     return new RequestBuilder("POST", uri);
/*     */   }
/*     */   
/*     */   public static RequestBuilder put() {
/* 183 */     return new RequestBuilder("PUT");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder put(URI uri) {
/* 190 */     return new RequestBuilder("PUT", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder put(String uri) {
/* 197 */     return new RequestBuilder("PUT", uri);
/*     */   }
/*     */   
/*     */   public static RequestBuilder delete() {
/* 201 */     return new RequestBuilder("DELETE");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder delete(URI uri) {
/* 208 */     return new RequestBuilder("DELETE", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder delete(String uri) {
/* 215 */     return new RequestBuilder("DELETE", uri);
/*     */   }
/*     */   
/*     */   public static RequestBuilder trace() {
/* 219 */     return new RequestBuilder("TRACE");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder trace(URI uri) {
/* 226 */     return new RequestBuilder("TRACE", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder trace(String uri) {
/* 233 */     return new RequestBuilder("TRACE", uri);
/*     */   }
/*     */   
/*     */   public static RequestBuilder options() {
/* 237 */     return new RequestBuilder("OPTIONS");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder options(URI uri) {
/* 244 */     return new RequestBuilder("OPTIONS", uri);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestBuilder options(String uri) {
/* 251 */     return new RequestBuilder("OPTIONS", uri);
/*     */   }
/*     */   
/*     */   public static RequestBuilder copy(HttpRequest request) {
/* 255 */     Args.notNull(request, "HTTP request");
/* 256 */     return (new RequestBuilder()).doCopy(request);
/*     */   }
/*     */   
/*     */   private RequestBuilder doCopy(HttpRequest request) {
/* 260 */     if (request == null) {
/* 261 */       return this;
/*     */     }
/* 263 */     this.method = request.getRequestLine().getMethod();
/* 264 */     this.version = request.getRequestLine().getProtocolVersion();
/*     */     
/* 266 */     if (this.headerGroup == null) {
/* 267 */       this.headerGroup = new HeaderGroup();
/*     */     }
/* 269 */     this.headerGroup.clear();
/* 270 */     this.headerGroup.setHeaders(request.getAllHeaders());
/*     */     
/* 272 */     this.parameters = null;
/* 273 */     this.entity = null;
/*     */     
/* 275 */     if (request instanceof HttpEntityEnclosingRequest) {
/* 276 */       HttpEntity originalEntity = ((HttpEntityEnclosingRequest)request).getEntity();
/* 277 */       ContentType contentType = ContentType.get(originalEntity);
/* 278 */       if (contentType != null && contentType.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
/*     */         
/*     */         try {
/* 281 */           List<NameValuePair> formParams = URLEncodedUtils.parse(originalEntity);
/* 282 */           if (!formParams.isEmpty()) {
/* 283 */             this.parameters = formParams;
/*     */           }
/* 285 */         } catch (IOException ignore) {}
/*     */       } else {
/*     */         
/* 288 */         this.entity = originalEntity;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 293 */     if (request instanceof HttpUriRequest) {
/* 294 */       this.uri = ((HttpUriRequest)request).getURI();
/*     */     } else {
/* 296 */       this.uri = URI.create(request.getRequestLine().getUri());
/*     */     } 
/*     */     
/* 299 */     if (request instanceof Configurable) {
/* 300 */       this.config = ((Configurable)request).getConfig();
/*     */     } else {
/* 302 */       this.config = null;
/*     */     } 
/* 304 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestBuilder setCharset(Charset charset) {
/* 311 */     this.charset = charset;
/* 312 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 319 */     return this.charset;
/*     */   }
/*     */   
/*     */   public String getMethod() {
/* 323 */     return this.method;
/*     */   }
/*     */   
/*     */   public ProtocolVersion getVersion() {
/* 327 */     return this.version;
/*     */   }
/*     */   
/*     */   public RequestBuilder setVersion(ProtocolVersion version) {
/* 331 */     this.version = version;
/* 332 */     return this;
/*     */   }
/*     */   
/*     */   public URI getUri() {
/* 336 */     return this.uri;
/*     */   }
/*     */   
/*     */   public RequestBuilder setUri(URI uri) {
/* 340 */     this.uri = uri;
/* 341 */     return this;
/*     */   }
/*     */   
/*     */   public RequestBuilder setUri(String uri) {
/* 345 */     this.uri = (uri != null) ? URI.create(uri) : null;
/* 346 */     return this;
/*     */   }
/*     */   
/*     */   public Header getFirstHeader(String name) {
/* 350 */     return (this.headerGroup != null) ? this.headerGroup.getFirstHeader(name) : null;
/*     */   }
/*     */   
/*     */   public Header getLastHeader(String name) {
/* 354 */     return (this.headerGroup != null) ? this.headerGroup.getLastHeader(name) : null;
/*     */   }
/*     */   
/*     */   public Header[] getHeaders(String name) {
/* 358 */     return (this.headerGroup != null) ? this.headerGroup.getHeaders(name) : null;
/*     */   }
/*     */   
/*     */   public RequestBuilder addHeader(Header header) {
/* 362 */     if (this.headerGroup == null) {
/* 363 */       this.headerGroup = new HeaderGroup();
/*     */     }
/* 365 */     this.headerGroup.addHeader(header);
/* 366 */     return this;
/*     */   }
/*     */   
/*     */   public RequestBuilder addHeader(String name, String value) {
/* 370 */     if (this.headerGroup == null) {
/* 371 */       this.headerGroup = new HeaderGroup();
/*     */     }
/* 373 */     this.headerGroup.addHeader((Header)new BasicHeader(name, value));
/* 374 */     return this;
/*     */   }
/*     */   
/*     */   public RequestBuilder removeHeader(Header header) {
/* 378 */     if (this.headerGroup == null) {
/* 379 */       this.headerGroup = new HeaderGroup();
/*     */     }
/* 381 */     this.headerGroup.removeHeader(header);
/* 382 */     return this;
/*     */   }
/*     */   
/*     */   public RequestBuilder removeHeaders(String name) {
/* 386 */     if (name == null || this.headerGroup == null) {
/* 387 */       return this;
/*     */     }
/* 389 */     for (HeaderIterator i = this.headerGroup.iterator(); i.hasNext(); ) {
/* 390 */       Header header = i.nextHeader();
/* 391 */       if (name.equalsIgnoreCase(header.getName())) {
/* 392 */         i.remove();
/*     */       }
/*     */     } 
/* 395 */     return this;
/*     */   }
/*     */   
/*     */   public RequestBuilder setHeader(Header header) {
/* 399 */     if (this.headerGroup == null) {
/* 400 */       this.headerGroup = new HeaderGroup();
/*     */     }
/* 402 */     this.headerGroup.updateHeader(header);
/* 403 */     return this;
/*     */   }
/*     */   
/*     */   public RequestBuilder setHeader(String name, String value) {
/* 407 */     if (this.headerGroup == null) {
/* 408 */       this.headerGroup = new HeaderGroup();
/*     */     }
/* 410 */     this.headerGroup.updateHeader((Header)new BasicHeader(name, value));
/* 411 */     return this;
/*     */   }
/*     */   
/*     */   public HttpEntity getEntity() {
/* 415 */     return this.entity;
/*     */   }
/*     */   
/*     */   public RequestBuilder setEntity(HttpEntity entity) {
/* 419 */     this.entity = entity;
/* 420 */     return this;
/*     */   }
/*     */   
/*     */   public List<NameValuePair> getParameters() {
/* 424 */     return (this.parameters != null) ? new ArrayList<NameValuePair>(this.parameters) : new ArrayList<NameValuePair>();
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestBuilder addParameter(NameValuePair nvp) {
/* 429 */     Args.notNull(nvp, "Name value pair");
/* 430 */     if (this.parameters == null) {
/* 431 */       this.parameters = new LinkedList<NameValuePair>();
/*     */     }
/* 433 */     this.parameters.add(nvp);
/* 434 */     return this;
/*     */   }
/*     */   
/*     */   public RequestBuilder addParameter(String name, String value) {
/* 438 */     return addParameter((NameValuePair)new BasicNameValuePair(name, value));
/*     */   }
/*     */   
/*     */   public RequestBuilder addParameters(NameValuePair... nvps) {
/* 442 */     for (NameValuePair nvp : nvps) {
/* 443 */       addParameter(nvp);
/*     */     }
/* 445 */     return this;
/*     */   }
/*     */   
/*     */   public RequestConfig getConfig() {
/* 449 */     return this.config;
/*     */   }
/*     */   
/*     */   public RequestBuilder setConfig(RequestConfig config) {
/* 453 */     this.config = config;
/* 454 */     return this;
/*     */   }
/*     */   public HttpUriRequest build() {
/*     */     HttpRequestBase result;
/*     */     UrlEncodedFormEntity urlEncodedFormEntity;
/* 459 */     URI uriNotNull = (this.uri != null) ? this.uri : URI.create("/");
/* 460 */     HttpEntity entityCopy = this.entity;
/* 461 */     if (this.parameters != null && !this.parameters.isEmpty()) {
/* 462 */       if (entityCopy == null && ("POST".equalsIgnoreCase(this.method) || "PUT".equalsIgnoreCase(this.method))) {
/*     */         
/* 464 */         urlEncodedFormEntity = new UrlEncodedFormEntity(this.parameters, (this.charset != null) ? this.charset : HTTP.DEF_CONTENT_CHARSET);
/*     */       } else {
/*     */         try {
/* 467 */           uriNotNull = (new URIBuilder(uriNotNull)).setCharset(this.charset).addParameters(this.parameters).build();
/*     */ 
/*     */         
/*     */         }
/* 471 */         catch (URISyntaxException ex) {}
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 476 */     if (urlEncodedFormEntity == null) {
/* 477 */       result = new InternalRequest(this.method);
/*     */     } else {
/* 479 */       InternalEntityEclosingRequest request = new InternalEntityEclosingRequest(this.method);
/* 480 */       request.setEntity((HttpEntity)urlEncodedFormEntity);
/* 481 */       result = request;
/*     */     } 
/* 483 */     result.setProtocolVersion(this.version);
/* 484 */     result.setURI(uriNotNull);
/* 485 */     if (this.headerGroup != null) {
/* 486 */       result.setHeaders(this.headerGroup.getAllHeaders());
/*     */     }
/* 488 */     result.setConfig(this.config);
/* 489 */     return result;
/*     */   }
/*     */   
/*     */   static class InternalRequest
/*     */     extends HttpRequestBase
/*     */   {
/*     */     private final String method;
/*     */     
/*     */     InternalRequest(String method) {
/* 498 */       this.method = method;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMethod() {
/* 503 */       return this.method;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class InternalEntityEclosingRequest
/*     */     extends HttpEntityEnclosingRequestBase
/*     */   {
/*     */     private final String method;
/*     */     
/*     */     InternalEntityEclosingRequest(String method) {
/* 514 */       this.method = method;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMethod() {
/* 519 */       return this.method;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 526 */     StringBuilder builder = new StringBuilder();
/* 527 */     builder.append("RequestBuilder [method=");
/* 528 */     builder.append(this.method);
/* 529 */     builder.append(", charset=");
/* 530 */     builder.append(this.charset);
/* 531 */     builder.append(", version=");
/* 532 */     builder.append(this.version);
/* 533 */     builder.append(", uri=");
/* 534 */     builder.append(this.uri);
/* 535 */     builder.append(", headerGroup=");
/* 536 */     builder.append(this.headerGroup);
/* 537 */     builder.append(", entity=");
/* 538 */     builder.append(this.entity);
/* 539 */     builder.append(", parameters=");
/* 540 */     builder.append(this.parameters);
/* 541 */     builder.append(", config=");
/* 542 */     builder.append(this.config);
/* 543 */     builder.append("]");
/* 544 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\RequestBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */