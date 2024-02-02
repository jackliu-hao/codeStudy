/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.message.AbstractHttpMessage;
/*     */ import org.apache.http.message.BasicRequestLine;
/*     */ import org.apache.http.params.HttpProtocolParams;
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
/*     */ @Deprecated
/*     */ public class RequestWrapper
/*     */   extends AbstractHttpMessage
/*     */   implements HttpUriRequest
/*     */ {
/*     */   private final HttpRequest original;
/*     */   private URI uri;
/*     */   private String method;
/*     */   private ProtocolVersion version;
/*     */   private int execCount;
/*     */   
/*     */   public RequestWrapper(HttpRequest request) throws ProtocolException {
/*  68 */     Args.notNull(request, "HTTP request");
/*  69 */     this.original = request;
/*  70 */     setParams(request.getParams());
/*  71 */     setHeaders(request.getAllHeaders());
/*     */     
/*  73 */     if (request instanceof HttpUriRequest) {
/*  74 */       this.uri = ((HttpUriRequest)request).getURI();
/*  75 */       this.method = ((HttpUriRequest)request).getMethod();
/*  76 */       this.version = null;
/*     */     } else {
/*  78 */       RequestLine requestLine = request.getRequestLine();
/*     */       try {
/*  80 */         this.uri = new URI(requestLine.getUri());
/*  81 */       } catch (URISyntaxException ex) {
/*  82 */         throw new ProtocolException("Invalid request URI: " + requestLine.getUri(), ex);
/*     */       } 
/*     */       
/*  85 */       this.method = requestLine.getMethod();
/*  86 */       this.version = request.getProtocolVersion();
/*     */     } 
/*  88 */     this.execCount = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetHeaders() {
/*  93 */     this.headergroup.clear();
/*  94 */     setHeaders(this.original.getAllHeaders());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMethod() {
/*  99 */     return this.method;
/*     */   }
/*     */   
/*     */   public void setMethod(String method) {
/* 103 */     Args.notNull(method, "Method name");
/* 104 */     this.method = method;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolVersion getProtocolVersion() {
/* 109 */     if (this.version == null) {
/* 110 */       this.version = HttpProtocolParams.getVersion(getParams());
/*     */     }
/* 112 */     return this.version;
/*     */   }
/*     */   
/*     */   public void setProtocolVersion(ProtocolVersion version) {
/* 116 */     this.version = version;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getURI() {
/* 122 */     return this.uri;
/*     */   }
/*     */   
/*     */   public void setURI(URI uri) {
/* 126 */     this.uri = uri;
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestLine getRequestLine() {
/* 131 */     ProtocolVersion ver = getProtocolVersion();
/* 132 */     String uritext = null;
/* 133 */     if (this.uri != null) {
/* 134 */       uritext = this.uri.toASCIIString();
/*     */     }
/* 136 */     if (uritext == null || uritext.isEmpty()) {
/* 137 */       uritext = "/";
/*     */     }
/* 139 */     return (RequestLine)new BasicRequestLine(getMethod(), uritext, ver);
/*     */   }
/*     */ 
/*     */   
/*     */   public void abort() throws UnsupportedOperationException {
/* 144 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAborted() {
/* 149 */     return false;
/*     */   }
/*     */   
/*     */   public HttpRequest getOriginal() {
/* 153 */     return this.original;
/*     */   }
/*     */   
/*     */   public boolean isRepeatable() {
/* 157 */     return true;
/*     */   }
/*     */   
/*     */   public int getExecCount() {
/* 161 */     return this.execCount;
/*     */   }
/*     */   
/*     */   public void incrementExecCount() {
/* 165 */     this.execCount++;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\RequestWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */