/*     */ package org.apache.http.client.methods;
/*     */ 
/*     */ import java.net.URI;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.message.AbstractHttpMessage;
/*     */ import org.apache.http.message.BasicRequestLine;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ public class HttpRequestWrapper
/*     */   extends AbstractHttpMessage
/*     */   implements HttpUriRequest
/*     */ {
/*     */   private final HttpRequest original;
/*     */   private final HttpHost target;
/*     */   private final String method;
/*     */   private RequestLine requestLine;
/*     */   private ProtocolVersion version;
/*     */   private URI uri;
/*     */   
/*     */   private HttpRequestWrapper(HttpRequest request, HttpHost target) {
/*  63 */     this.original = (HttpRequest)Args.notNull(request, "HTTP request");
/*  64 */     this.target = target;
/*  65 */     this.version = this.original.getRequestLine().getProtocolVersion();
/*  66 */     this.method = this.original.getRequestLine().getMethod();
/*  67 */     if (request instanceof HttpUriRequest) {
/*  68 */       this.uri = ((HttpUriRequest)request).getURI();
/*     */     } else {
/*  70 */       this.uri = null;
/*     */     } 
/*  72 */     setHeaders(request.getAllHeaders());
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolVersion getProtocolVersion() {
/*  77 */     return (this.version != null) ? this.version : this.original.getProtocolVersion();
/*     */   }
/*     */   
/*     */   public void setProtocolVersion(ProtocolVersion version) {
/*  81 */     this.version = version;
/*  82 */     this.requestLine = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  87 */     return this.uri;
/*     */   }
/*     */   
/*     */   public void setURI(URI uri) {
/*  91 */     this.uri = uri;
/*  92 */     this.requestLine = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMethod() {
/*  97 */     return this.method;
/*     */   }
/*     */ 
/*     */   
/*     */   public void abort() throws UnsupportedOperationException {
/* 102 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAborted() {
/* 107 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestLine getRequestLine() {
/* 112 */     if (this.requestLine == null) {
/*     */       String requestUri;
/* 114 */       if (this.uri != null) {
/* 115 */         requestUri = this.uri.toASCIIString();
/*     */       } else {
/* 117 */         requestUri = this.original.getRequestLine().getUri();
/*     */       } 
/* 119 */       if (requestUri == null || requestUri.isEmpty()) {
/* 120 */         requestUri = "/";
/*     */       }
/* 122 */       this.requestLine = (RequestLine)new BasicRequestLine(this.method, requestUri, getProtocolVersion());
/*     */     } 
/* 124 */     return this.requestLine;
/*     */   }
/*     */   
/*     */   public HttpRequest getOriginal() {
/* 128 */     return this.original;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHost getTarget() {
/* 135 */     return this.target;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 140 */     return getRequestLine() + " " + this.headergroup;
/*     */   }
/*     */   
/*     */   static class HttpEntityEnclosingRequestWrapper
/*     */     extends HttpRequestWrapper
/*     */     implements HttpEntityEnclosingRequest {
/*     */     private HttpEntity entity;
/*     */     
/*     */     HttpEntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request, HttpHost target) {
/* 149 */       super((HttpRequest)request, target);
/* 150 */       this.entity = request.getEntity();
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpEntity getEntity() {
/* 155 */       return this.entity;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setEntity(HttpEntity entity) {
/* 160 */       this.entity = entity;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean expectContinue() {
/* 165 */       Header expect = getFirstHeader("Expect");
/* 166 */       return (expect != null && "100-continue".equalsIgnoreCase(expect.getValue()));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpRequestWrapper wrap(HttpRequest request) {
/* 178 */     return wrap(request, (HttpHost)null);
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
/*     */   public static HttpRequestWrapper wrap(HttpRequest request, HttpHost target) {
/* 191 */     Args.notNull(request, "HTTP request");
/* 192 */     return (request instanceof HttpEntityEnclosingRequest) ? new HttpEntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request, target) : new HttpRequestWrapper(request, target);
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
/*     */   @Deprecated
/*     */   public HttpParams getParams() {
/* 205 */     if (this.params == null) {
/* 206 */       this.params = this.original.getParams().copy();
/*     */     }
/* 208 */     return this.params;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpRequestWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */